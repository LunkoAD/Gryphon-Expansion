package net.lunkoashtail.gryphonexpansion.client;

import net.lunkoashtail.gryphonexpansion.GryphonExpansion;
import net.lunkoashtail.gryphonexpansion.client.model.GryphonModel;
import net.lunkoashtail.gryphonexpansion.client.renderer.GryphonRenderer;
import net.lunkoashtail.gryphonexpansion.client.screen.GryphonScreen;
import net.lunkoashtail.gryphonexpansion.entity.ModEntities;
import net.lunkoashtail.gryphonexpansion.menu.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * Client-only event subscribers for registering entity renderers, model layers,
 * and menu→screen mappings.
 *
 * <p>{@code Bus.MOD} events fire during the mod loading phase (before a world is
 * loaded), which is the only time these registrations are valid.
 */
@EventBusSubscriber(modid = GryphonExpansion.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    /** Register the GryphonModel's LayerDefinition so NeoForge bakes it at startup. */
    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(GryphonModel.LAYER_LOCATION, GryphonModel::createBodyLayer);
    }

    /** Bind the GryphonEntity type to its renderer. */
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.GRYPHON.get(), GryphonRenderer::new);
    }

    /** Bind the GryphonMenu type to its screen so Minecraft opens the right GUI. */
    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.GRYPHON_MENU.get(), GryphonScreen::new);
    }
}
