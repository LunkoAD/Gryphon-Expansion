package net.lunkoashtail.gryphonexpansion.entity;

import net.lunkoashtail.gryphonexpansion.GryphonExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, GryphonExpansion.MOD_ID);

    /**
     * The Gryphon entity type.
     * <p>
     * The string passed to {@code build()} is only used internally for error messages
     * and logging — the actual registry key comes from the first argument to
     * {@code register()}.
     * <p>
     * Size: 1.4 × 1.6 blocks (horse is 1.4 × 1.6, so this matches that footprint).
     * Tracking range 10 chunks ensures the entity stays synced at reasonable distances.
     */
    public static final DeferredHolder<EntityType<?>, EntityType<GryphonEntity>> GRYPHON =
            ENTITY_TYPES.register("gryphon",
                    () -> EntityType.Builder.<GryphonEntity>of(GryphonEntity::new, MobCategory.CREATURE)
                            .sized(1.4f, 1.6f)
                            .clientTrackingRange(10)
                            .updateInterval(3)
                            .build("gryphon"));

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}
