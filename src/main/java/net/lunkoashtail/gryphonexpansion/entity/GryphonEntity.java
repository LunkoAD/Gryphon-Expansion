package net.lunkoashtail.gryphonexpansion.entity;

import net.lunkoashtail.gryphonexpansion.item.ModItems;
import net.lunkoashtail.gryphonexpansion.menu.GryphonMenu;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;

public class GryphonEntity extends TamableAnimal {

    // ─── Synced Data ──────────────────────────────────────────────────────────────
    // EntityDataAccessors defined here are automatically synced from the server to
    // every client tracking this entity whenever their values change. No manual
    // packet code needed. Each entity class gets its own SynchedEntityData namespace
    // — the ID is assigned in order of defineId calls across the class hierarchy.

    private static final EntityDataAccessor<Float> DATA_HUNGER =
            SynchedEntityData.defineId(GryphonEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> DATA_TRUST =
            SynchedEntityData.defineId(GryphonEntity.class, EntityDataSerializers.FLOAT);

    // true = male (♂), false = female (♀) — randomized in finalizeSpawn
    private static final EntityDataAccessor<Boolean> DATA_GENDER =
            SynchedEntityData.defineId(GryphonEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> DATA_LEVEL =
            SynchedEntityData.defineId(GryphonEntity.class, EntityDataSerializers.INT);

    // Raw age in ticks. Displayed as days (ticks / 24000). Grows every server tick.
    private static final EntityDataAccessor<Integer> DATA_AGE_TICKS =
            SynchedEntityData.defineId(GryphonEntity.class, EntityDataSerializers.INT);

    // Mirrors gearInventory slot 0 to the client so hasSaddleEquipped() works both sides.
    private static final EntityDataAccessor<Boolean> DATA_SADDLED =
            SynchedEntityData.defineId(GryphonEntity.class, EntityDataSerializers.BOOLEAN);

    // ─── Inventories ──────────────────────────────────────────────────────────────
    // Stored as plain fields (not IItemHandler capabilities) so both the server-side
    // GryphonMenu and the client-side GryphonScreen can get to them via the entity
    // reference. The ItemStackHandler notifies us of changes via onContentsChanged.
    //
    // To later expose these through the capability system (e.g. for pipes/hoppers),
    // attach them in a AttachCapabilitiesEvent handler with IItemHandlerModifiable.

    public final ItemStackHandler inventory = new ItemStackHandler(64) {
        @Override
        protected void onContentsChanged(int slot) {
            if (!GryphonEntity.this.level().isClientSide()) {
                // Triggers chunk save on next autosave cycle
                GryphonEntity.this.markInventoryDirty();
            }
        }
    };

    // 4 gear slots: 0 = Saddle, 1 = Armor, 2 = Harness/Accessory, 3 = Chest
    // Slot 3 (Chest) gates the 64-slot storage inventory — if empty, inventory is inaccessible.
    public final ItemStackHandler gearInventory = new ItemStackHandler(4) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.is(ModItems.GRYPHON_SADDLE.get());
                case 1 -> isGryphonArmor(stack);
                case 2 -> stack.is(ModItems.GRYPHON_HARNESS.get());
                case 3 -> stack.is(ModItems.GRYPHON_SADDLEBAG.get());
                default -> false;
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (!GryphonEntity.this.level().isClientSide()) {
                GryphonEntity.this.markInventoryDirty();
                if (slot == 0) GryphonEntity.this.syncSaddleData();
                if (slot == 1) GryphonEntity.this.syncArmorModifier();
            }
        }
    };

    // Unique ID for the transient armor bonus applied by equipped gryphon armor
    private static final ResourceLocation ARMOR_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("gryphonexpansion", "gear_armor");

    // ─── Constants ────────────────────────────────────────────────────────────────

    public static final float HUNGER_MAX = 100.0f;
    public static final float TRUST_MAX  = 100.0f;

    // TODO: replace both with a proper GryphonTreat custom FoodItem
    private static final Item TAMING_FOOD    = Items.BEEF;
    private static final Item TRUST_FOOD     = Items.COOKED_BEEF;

    /** Ticks between each 2-point hunger drain (200 ticks ≈ 10 seconds). */
    private static final int HUNGER_DRAIN_INTERVAL = 200;

    /** Ticks between trust decay when starving (6000 ticks ≈ 5 minutes). */
    private static final int TRUST_DECAY_INTERVAL  = 6000;

    // Server-side timers (not synced — no client UI needs them directly)
    private int hungerTimer    = 0;
    private int trustDecayTimer = 0;
    // Dirty flag so we don't mark the chunk every tick even when nothing changed
    private boolean inventoryDirty = false;

    // Riding state — not synced, tracked independently per side
    private int  rideSneakTicks = 0;   // consecutive ticks Shift has been held while ridden
    private boolean forceDismount = false; // set true around ejectPassengers() to bypass block

    // ─── Constructor ──────────────────────────────────────────────────────────────

    public GryphonEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    // ─── Attributes ───────────────────────────────────────────────────────────────
    // createAttributes() is called once at registration time to build the default
    // attribute map. finalizeSpawn() then randomizes per-entity values from it.

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                // Base speed; overridden per-entity in finalizeSpawn (horse-style variance)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5);
    }

    // ─── SynchedEntityData ────────────────────────────────────────────────────────

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // Always call super first — TamableAnimal registers its own keys (isTamed, ownerUUID, ...)
        super.defineSynchedData(builder);
        builder.define(DATA_HUNGER, HUNGER_MAX);
        builder.define(DATA_TRUST, 0.0f);
        builder.define(DATA_GENDER, true);  // male by default; randomized in finalizeSpawn
        builder.define(DATA_LEVEL, 1);
        builder.define(DATA_AGE_TICKS, 0);
        builder.define(DATA_SADDLED, false);
    }

    // ─── AI Goals ─────────────────────────────────────────────────────────────────

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.2, 10.0f, 2.0f));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    // ─── Spawn Randomization ──────────────────────────────────────────────────────

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, SpawnGroupData groupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, groupData);

        // Randomize gender on first spawn (not on chunk load — super handles that via NBT)
        this.setGender(this.random.nextBoolean());

        // Horse-style speed variance: 0.20 – 0.35 blocks/tick (= 4 – 7 blocks/second)
        double speed = 0.20 + this.random.nextDouble() * 0.15;
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);

        // Attack damage variance: 5 – 9
        double damage = 5.0 + this.random.nextDouble() * 4.0;
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(damage);

        return data;
    }

    // ─── Interaction ──────────────────────────────────────────────────────────────

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        Level level = this.level();

        if (!this.isTame()) {
            // ── Taming attempt ──────────────────────────────────────────────────
            if (held.is(TAMING_FOOD)) {
                if (!level.isClientSide()) {
                    if (!player.getAbilities().instabuild) held.shrink(1);
                    this.addTrust(15.0f);

                    // ~33% chance per feeding, or guaranteed at 50+ trust
                    if (this.random.nextInt(3) == 0 || this.getTrust() >= 50.0f) {
                        this.tame(player);
                        this.setOrderedToSit(false);
                        level.broadcastEntityEvent(this, (byte) 7); // heart particles
                    } else {
                        level.broadcastEntityEvent(this, (byte) 6); // smoke particles
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }

        } else {
            // ── Tamed ────────────────────────────────────────────────────────────

            // Owner-only actions: gear equip, sit toggle, feeding, GUI
            if (this.isOwnedBy(player)) {

                // Shift+right-click: equip gear item
                if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
                    int gearSlot = getGearSlotFor(held);
                    if (gearSlot >= 0) {
                        if (!level.isClientSide()) {
                            ItemStack existing = gearInventory.getStackInSlot(gearSlot);
                            gearInventory.setStackInSlot(gearSlot, held.copyWithCount(1));
                            if (!player.getAbilities().instabuild) held.shrink(1);
                            if (!existing.isEmpty()) player.getInventory().placeItemBackInInventory(existing);
                        }
                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }
                }

                // Cinnabar → toggle sit/stand
                if (hand == InteractionHand.MAIN_HAND && held.is(ModItems.CINNABAR.get())) {
                    if (!level.isClientSide()) {
                        this.setOrderedToSit(!this.isOrderedToSit());
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }

                // Feeding: restore hunger + small trust bump
                if (held.is(TRUST_FOOD)) {
                    if (!level.isClientSide()) {
                        if (!player.getAbilities().instabuild) held.shrink(1);
                        this.setHunger(Math.min(HUNGER_MAX, this.getHunger() + 25.0f));
                        this.addTrust(5.0f);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }

                // Stick → open GUI
                if (hand == InteractionHand.MAIN_HAND && held.is(Items.STICK)) {
                    if (!level.isClientSide() && player instanceof ServerPlayer sp) {
                        int entityId = this.getId();
                        sp.openMenu(
                            new SimpleMenuProvider(
                                (id, inv, p) -> new GryphonMenu(id, inv, this),
                                Component.translatable("menu.gryphonexpansion.gryphon")
                            ),
                            buf -> buf.writeInt(entityId)
                        );
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }

            // Mount: any player may ride a tamed gryphon with saddle equipped (matches vanilla horse behaviour)
            // Shift is excluded so shift+right-click on gear/cinnabar doesn't accidentally mount.
            if (hand == InteractionHand.MAIN_HAND && held.isEmpty() && !player.isShiftKeyDown()
                    && hasSaddleEquipped() && !this.isOrderedToSit() && !this.isVehicle()) {
                if (!level.isClientSide()) {
                    player.startRiding(this, true);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return super.mobInteract(player, hand);
    }

    // ─── Riding ───────────────────────────────────────────────────────────────────

    @Override
    public LivingEntity getControllingPassenger() {
        if (hasSaddleEquipped()) {
            Entity passenger = this.getFirstPassenger();
            if (passenger instanceof Player player) return player;
        }
        return null;
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.getControllingPassenger() instanceof Player p && p.isLocalPlayer();
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        float strafe = player.xxa * 0.5f;
        float forward = player.zza;
        if (forward <= 0.0f) forward *= 0.25f; // slower reversing
        return new Vec3(strafe, 0.0, forward);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), player.getXRot() * 0.5f);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

        // ── Creative-style flight ─────────────────────────────────────────────────
        // Gravity is suppressed while ridden; horizontal momentum is zeroed each tick
        // so movement is purely input-driven (no glide), matching creative-mode feel.
        this.setNoGravity(true);

        float speed = this.getRiddenSpeed(player);
        double vy = 0.0; // hover by default

        if (player.jumping) {
            vy = speed;
            rideSneakTicks = 0;
        } else if (player.isShiftKeyDown()) {
            vy = -speed;
            rideSneakTicks++;
            // Hold Shift for 30 ticks (1.5 s) → force-dismount
            if (!this.level().isClientSide() && rideSneakTicks >= 30) {
                rideSneakTicks = 0;
                forceDismount = true;
                this.ejectPassengers();
                forceDismount = false;
                return; // skip setDeltaMovement — entity is no longer ridden
            }
        } else {
            rideSneakTicks = 0;
        }

        // Zero out X/Z so that horizontal movement comes entirely from getRiddenInput
        // fed through super.travel() → moveRelative each tick (no momentum carry-over).
        this.setDeltaMovement(0.0, vy, 0.0);
    }

    @Override
    public void removePassenger(Entity passenger) {
        // Block the vanilla Shift-dismount packet entirely.
        // The only way to dismount is via the 30-tick hold above (forceDismount = true),
        // or forced removal (death, ejectPassengers from external code).
        if (passenger instanceof Player && passenger.isAlive() && this.isAlive() && !forceDismount) {
            return;
        }
        super.removePassenger(passenger);
        if (!this.isVehicle()) {
            this.setNoGravity(false);
        }
    }

    // ─── Tick ─────────────────────────────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();

        // Gravity is suppressed in tickRidden; restore it whenever no one is riding
        // so a world-save/reload while airborne doesn't leave the gryphon floating forever.
        if (!this.isVehicle() && this.isNoGravity()) {
            this.setNoGravity(false);
        }

        if (!this.level().isClientSide()) {
            // Age grows every tick server-side
            int newAge = this.getAgeTicks() + 1;
            this.entityData.set(DATA_AGE_TICKS, newAge); // bypass the setter range check

            // Hunger drain
            hungerTimer++;
            if (hungerTimer >= HUNGER_DRAIN_INTERVAL) {
                hungerTimer = 0;
                this.setHunger(Math.max(0.0f, this.getHunger() - 2.0f));
            }

            // Trust decays slowly when starving
            if (this.getHunger() < 20.0f) {
                trustDecayTimer++;
                if (trustDecayTimer >= TRUST_DECAY_INTERVAL) {
                    trustDecayTimer = 0;
                    this.addTrust(-3.0f);
                }
            } else {
                trustDecayTimer = 0;
            }

            // Level gate: each 25 trust points = one level (max 5)
            int targetLevel = Math.min(5, (int)(this.getTrust() / 25.0f) + 1);
            if (targetLevel > this.getLevel()) {
                this.setLevel(targetLevel);
                this.level().broadcastEntityEvent(this, (byte) 7);
            }
        }
    }

    // ─── NBT ──────────────────────────────────────────────────────────────────────
    // SynchedEntityData values are NOT automatically persisted — we must save/load
    // them manually here. The ItemStackHandlers also need explicit serialization.

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        // ItemStackHandler.serializeNBT() needs a HolderLookup.Provider because
        // ItemStack serialization became registry-aware in 1.21.
        tag.put("GryphonInventory", inventory.serializeNBT(this.registryAccess()));
        tag.put("GryphonGear",      gearInventory.serializeNBT(this.registryAccess()));
        tag.putFloat("Hunger",    this.getHunger());
        tag.putFloat("Trust",     this.getTrust());
        tag.putBoolean("Gender",  this.isGenderMale());
        tag.putInt("GryphonLevel", this.getLevel());
        tag.putInt("AgeTicks",    this.getAgeTicks());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("GryphonInventory")) {
            inventory.deserializeNBT(this.registryAccess(), tag.getCompound("GryphonInventory"));
        }
        if (tag.contains("GryphonGear")) {
            gearInventory.deserializeNBT(this.registryAccess(), tag.getCompound("GryphonGear"));
            syncArmorModifier(); // deserializeNBT bypasses onContentsChanged, so apply manually
            syncSaddleData();
        }
        this.setHunger(tag.getFloat("Hunger"));
        this.setTrust(tag.getFloat("Trust"));
        this.setGender(tag.getBoolean("Gender"));
        this.setLevel(tag.getInt("GryphonLevel"));
        this.entityData.set(DATA_AGE_TICKS, Math.max(0, tag.getInt("AgeTicks")));
    }

    // Called by ItemStackHandler.onContentsChanged; entity's chunk is saved next autosave.
    private void markInventoryDirty() {
        inventoryDirty = true;
    }

    // ─── Particle events (called from handleEntityEvent on client) ─────────────────

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 7) {
            spawnParticles(ParticleTypes.HEART, 7);
        } else if (id == 6) {
            spawnParticles(ParticleTypes.SMOKE, 7);
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void spawnParticles(net.minecraft.core.particles.SimpleParticleType type, int count) {
        for (int i = 0; i < count; i++) {
            double dx = this.random.nextGaussian() * 0.02;
            double dy = this.random.nextGaussian() * 0.02;
            double dz = this.random.nextGaussian() * 0.02;
            this.level().addParticle(type,
                    this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0),
                    dx, dy, dz);
        }
    }

    // ─── Animal abstract requirements ─────────────────────────────────────────────

    /** Items the gryphon will be attracted to (used by BreedGoal / TemptGoal). */
    @Override
    public boolean isFood(ItemStack stack) {
        // TODO: replace with a custom GryphonTreat item
        return stack.is(TAMING_FOOD) || stack.is(TRUST_FOOD);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        // TODO: spawn a baby GryphonEntity when breeding is implemented
        return null;
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────────

    public float getHunger() { return this.entityData.get(DATA_HUNGER); }
    public void  setHunger(float v) { this.entityData.set(DATA_HUNGER, Math.max(0, Math.min(HUNGER_MAX, v))); }

    public float getTrust() { return this.entityData.get(DATA_TRUST); }
    public void  setTrust(float v) { this.entityData.set(DATA_TRUST, Math.max(0, Math.min(TRUST_MAX, v))); }
    public void  addTrust(float delta) { setTrust(getTrust() + delta); }

    public boolean isGenderMale() { return this.entityData.get(DATA_GENDER); }
    public void    setGender(boolean male) { this.entityData.set(DATA_GENDER, male); }

    public int  getLevel() { return this.entityData.get(DATA_LEVEL); }
    public void setLevel(int v) { this.entityData.set(DATA_LEVEL, Math.max(1, Math.min(10, v))); }

    public int  getAgeTicks() { return this.entityData.get(DATA_AGE_TICKS); }
    /** Age expressed as whole Minecraft days (1 day = 24 000 ticks). */
    public int  getAgeInDays() { return getAgeTicks() / 24000; }

    /** True when the Gryphon Saddle occupies gear slot 0, enabling riding. Reads synced data so it works on both sides. */
    public boolean hasSaddleEquipped() {
        return this.entityData.get(DATA_SADDLED);
    }

    private void syncSaddleData() {
        this.entityData.set(DATA_SADDLED,
                gearInventory.getStackInSlot(0).is(ModItems.GRYPHON_SADDLE.get()));
    }

    /** True when a Gryphon Saddlebag occupies gear slot 3, unlocking the 64-slot storage. */
    public boolean hasChestEquipped() {
        return gearInventory.getStackInSlot(3).is(ModItems.GRYPHON_SADDLEBAG.get());
    }

    /** True for any of the four custom gryphon armor items. */
    public static boolean isGryphonArmor(ItemStack stack) {
        return stack.is(ModItems.IRON_GRYPHON_ARMOR.get())
            || stack.is(ModItems.GOLD_GRYPHON_ARMOR.get())
            || stack.is(ModItems.DIAMOND_GRYPHON_ARMOR.get())
            || stack.is(ModItems.NETHERITE_GRYPHON_ARMOR.get());
    }

    /** Armor bonus granted by the given gryphon armor item (0 if none). */
    private static double getArmorBonus(ItemStack stack) {
        if (stack.is(ModItems.NETHERITE_GRYPHON_ARMOR.get())) return 12.0;
        if (stack.is(ModItems.DIAMOND_GRYPHON_ARMOR.get()))   return 8.0;
        if (stack.is(ModItems.GOLD_GRYPHON_ARMOR.get()))      return 3.0;
        if (stack.is(ModItems.IRON_GRYPHON_ARMOR.get()))      return 4.0;
        return 0.0;
    }

    /** Applies or removes the transient armor modifier based on what is in gear slot 1. */
    private void syncArmorModifier() {
        AttributeInstance attr = this.getAttribute(Attributes.ARMOR);
        if (attr == null) return;
        attr.removeModifier(ARMOR_MODIFIER_ID);
        double bonus = getArmorBonus(gearInventory.getStackInSlot(1));
        if (bonus > 0) {
            attr.addTransientModifier(
                new AttributeModifier(ARMOR_MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    /**
     * Returns the gear slot index for the given item, or -1 if it is not a gryphon gear item.
     * Only the four custom mod items are accepted — no vanilla items.
     */
    private int getGearSlotFor(ItemStack stack) {
        if (stack.is(ModItems.GRYPHON_SADDLE.get()))    return 0;
        if (isGryphonArmor(stack))                       return 1;
        if (stack.is(ModItems.GRYPHON_HARNESS.get()))   return 2;
        if (stack.is(ModItems.GRYPHON_SADDLEBAG.get())) return 3;
        return -1;
    }

    /** Movement speed in blocks/second (attribute value × 20). */
    public double getSpeedForDisplay() {
        return getAttribute(Attributes.MOVEMENT_SPEED).getValue() * 20.0;
    }

    /** Total effective attack damage (base + any gear modifiers). */
    public double getDamageForDisplay() {
        return getAttribute(Attributes.ATTACK_DAMAGE).getValue();
    }

    /** Total effective armor (base 2 + gryphon armor bonus). */
    public double getArmorForDisplay() {
        return getAttribute(Attributes.ARMOR).getValue();
    }
}
