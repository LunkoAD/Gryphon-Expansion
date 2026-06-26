package net.lunkoashtail.gryphonexpansion.menu;

import net.lunkoashtail.gryphonexpansion.GryphonExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, GryphonExpansion.MOD_ID);

    /**
     * GryphonMenu type registered via NeoForge's {@code IMenuTypeExtension.create()}.
     * <p>
     * The factory lambda is the client-side constructor: it receives the extra
     * {@code FriendlyByteBuf} that the server wrote when calling
     * {@code ServerPlayer.openMenu(..., buf -> buf.writeInt(entityId))}.
     * The client reads that buffer to look up the Gryphon entity in the client level.
     */
    public static final DeferredHolder<MenuType<?>, MenuType<GryphonMenu>> GRYPHON_MENU =
            MENUS.register("gryphon_menu",
                    () -> IMenuTypeExtension.create(GryphonMenu::new));

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
