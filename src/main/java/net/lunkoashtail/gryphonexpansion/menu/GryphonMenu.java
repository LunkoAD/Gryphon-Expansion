package net.lunkoashtail.gryphonexpansion.menu;

import net.lunkoashtail.gryphonexpansion.entity.GryphonEntity;
import net.lunkoashtail.gryphonexpansion.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nullable;

/**
 * GryphonMenu — the server-authoritative container for the Gryphon inventory GUI.
 *
 * <p>Slot layout (indices used in {@link #quickMoveStack}):
 * <ul>
 *   <li>0 – 3   : gear slots (saddle, armor, harness, chest) — {@code gryphon.gearInventory}
 *   <li>4 – 67  : 64-slot storage inventory — {@code gryphon.inventory}
 *                 (inactive / uninteractable when no Chest is in gear slot 3)
 *   <li>68 – 94 : player main inventory (rows 1–3, slots 9–35)
 *   <li>95 – 103: player hotbar (slots 0–8)
 * </ul>
 *
 * <p><b>Chest gate:</b> The 64 storage slots use {@link LockedStorageSlot}, which
 * overrides {@code isActive()} / {@code mayPlace()} / {@code mayPickup()} to block
 * interaction whenever gear slot 3 holds no {@code minecraft:chest}. The screen
 * draws a locked overlay over the grid in the same condition.
 *
 * <p><b>Client reconstruction:</b> entity ID is written by the server lambda in
 * {@code GryphonEntity.mobInteract()} and read here from the {@code FriendlyByteBuf}.
 */
public class GryphonMenu extends AbstractContainerMenu {

    // ─── Slot index ranges ────────────────────────────────────────────────────────

    private static final int GEAR_START   = 0;
    private static final int GEAR_END     = 3;   // now 4 gear slots (0–3)
    private static final int INV_START    = 4;   // storage starts after gear
    private static final int INV_END      = 67;
    private static final int PLAYER_START = 68;
    private static final int PLAYER_END   = 94;
    private static final int HOTBAR_START = 95;
    private static final int HOTBAR_END   = 103;

    // ─── Screen layout pixel constants ────────────────────────────────────────────
    // Layout overview (320 × 280 px):
    //   Left panel  (x 0–157): 3D preview | gear column (4 slots) | stat bars
    //   Right panel (x 162+) : 8×8 storage grid  (locked overlay when no chest)
    //   Bottom                : player inventory + hotbar (centred)

    public static final int GEAR_SLOT_X    = 104;
    public static final int GEAR_SLOT_0_Y  = 38;   // saddle  — label at y=30
    public static final int GEAR_SLOT_1_Y  = 62;   // armor   — label at y=54
    public static final int GEAR_SLOT_2_Y  = 86;   // harness — label at y=78
    public static final int GEAR_SLOT_3_Y  = 110;  // chest   — label at y=102

    public static final int INV_START_X    = 162;
    public static final int INV_START_Y    = 28;

    public static final int PLAYER_INV_X   = 79;   // (320 − 9×18) / 2
    public static final int PLAYER_INV_Y   = 196;
    public static final int HOTBAR_Y       = 256;

    @Nullable
    public final GryphonEntity gryphon;

    // ─── Server-side constructor ──────────────────────────────────────────────────

    public GryphonMenu(int containerId, Inventory playerInventory, GryphonEntity gryphon) {
        super(ModMenus.GRYPHON_MENU.get(), containerId);
        this.gryphon = gryphon;

        // ── Gear slots 0–3 ────────────────────────────────────────────────────────
        this.addSlot(new GearSlot(gryphon.gearInventory, 0, GEAR_SLOT_X, GEAR_SLOT_0_Y) {
            @Override public boolean mayPlace(ItemStack s) { return s.is(ModItems.GRYPHON_SADDLE.get()); }
        });
        this.addSlot(new GearSlot(gryphon.gearInventory, 1, GEAR_SLOT_X, GEAR_SLOT_1_Y) {
            @Override public boolean mayPlace(ItemStack s) { return GryphonEntity.isGryphonArmor(s); }
        });
        this.addSlot(new GearSlot(gryphon.gearInventory, 2, GEAR_SLOT_X, GEAR_SLOT_2_Y) {
            @Override public boolean mayPlace(ItemStack s) { return s.is(ModItems.GRYPHON_HARNESS.get()); }
        });
        // Saddlebag slot — gates the 64-slot storage inventory
        this.addSlot(new GearSlot(gryphon.gearInventory, 3, GEAR_SLOT_X, GEAR_SLOT_3_Y) {
            @Override public boolean mayPlace(ItemStack s) { return s.is(ModItems.GRYPHON_SADDLEBAG.get()); }
        });

        // ── 64-slot storage (4–67), locked when no chest ──────────────────────────
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int index = row * 8 + col;
                int x = INV_START_X + col * 18;
                int y = INV_START_Y + row * 18;
                this.addSlot(new LockedStorageSlot(gryphon, index, x, y));
            }
        }

        // ── Player inventory rows 1–3 (68–94) ────────────────────────────────────
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory,
                        col + row * 9 + 9,
                        PLAYER_INV_X + col * 18,
                        PLAYER_INV_Y + row * 18));
            }
        }

        // ── Player hotbar (95–103) ────────────────────────────────────────────────
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, PLAYER_INV_X + col * 18, HOTBAR_Y));
        }
    }

    // ─── Client-side constructor ──────────────────────────────────────────────────

    public GryphonMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, findGryphon(playerInventory, buf.readInt()));
    }

    @Nullable
    private static GryphonEntity findGryphon(Inventory playerInventory, int entityId) {
        Entity e = playerInventory.player.level().getEntity(entityId);
        return e instanceof GryphonEntity g ? g : null;
    }

    // ─── Shift-click ──────────────────────────────────────────────────────────────

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack remaining = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) return remaining;

        ItemStack stack = slot.getItem();
        remaining = stack.copy();

        if (index >= INV_START && index <= INV_END) {
            // From storage → gear, then player
            if (!this.moveItemStackTo(stack, GEAR_START, GEAR_END + 1, false)
                    && !this.moveItemStackTo(stack, PLAYER_START, HOTBAR_END + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= PLAYER_START && index <= HOTBAR_END) {
            // From player → gear first, then storage (if chest equipped)
            if (!this.moveItemStackTo(stack, GEAR_START, GEAR_END + 1, false)
                    && !this.moveItemStackTo(stack, INV_START, INV_END + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= GEAR_START && index <= GEAR_END) {
            // From gear → player
            if (!this.moveItemStackTo(stack, PLAYER_START, HOTBAR_END + 1, true)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        if (stack.getCount() == remaining.getCount()) return ItemStack.EMPTY;

        slot.onTake(player, stack);
        return remaining;
    }

    @Override
    public boolean stillValid(Player player) {
        if (gryphon == null) return false;
        return gryphon.isAlive() && gryphon.distanceTo(player) < 8.0f;
    }

    // ─── Inner slot types ─────────────────────────────────────────────────────────

    /** Base for all gear slots — single item, accepts nothing by default. */
    private static class GearSlot extends SlotItemHandler {
        GearSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }
        @Override public boolean mayPlace(ItemStack s) { return true; }
        @Override public int getMaxStackSize() { return 1; }
    }

    /**
     * Storage slot that is fully locked when no Chest occupies gear slot 3.
     *
     * <p>{@code isActive()} returning {@code false} tells
     * {@code AbstractContainerScreen} to skip rendering and hit-testing this slot.
     * {@code mayPlace}/{@code mayPickup} returning {@code false} protects against
     * any server-side interaction that bypasses the screen (e.g. hoppers or commands
     * should instead check {@code hasChestEquipped()} directly).
     */
    private class LockedStorageSlot extends SlotItemHandler {
        LockedStorageSlot(GryphonEntity gryphon, int index, int x, int y) {
            super(gryphon.inventory, index, x, y);
        }

        private boolean chestPresent() {
            return gryphon != null && gryphon.hasChestEquipped();
        }

        @Override public boolean isActive()              { return chestPresent(); }
        @Override public boolean mayPlace(ItemStack s)   { return chestPresent(); }
        @Override public boolean mayPickup(Player p)     { return chestPresent(); }
    }
}
