package net.lunkoashtail.gryphonexpansion.client.renderer;

import net.lunkoashtail.gryphonexpansion.GryphonExpansion;
import net.lunkoashtail.gryphonexpansion.client.model.GryphonModel;
import net.lunkoashtail.gryphonexpansion.entity.GryphonEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Renderer for {@link GryphonEntity}.
 *
 * <p>Extends {@link MobRenderer} which handles the render loop, shadow, and
 * overlay (hurt flash, glowing outline). It expects:
 * <ol>
 *   <li>A model baked from {@link GryphonModel#LAYER_LOCATION}
 *   <li>A shadow radius (0.7f ≈ horse radius)
 *   <li>{@link #getTextureLocation} returning the PNG path
 * </ol>
 *
 * <p>TODO: Add render layers (e.g. a SaddleLayer, tame owner name-tag layer,
 * or a glow effect when trust is maxed). Layers are added in the constructor
 * via {@code this.addLayer(new MyLayer(this))}.
 *
 * <p>TODO: For GeckoLib: replace {@code MobRenderer} with
 * {@code GeoEntityRenderer} and swap the model type.
 */
public class GryphonRenderer extends MobRenderer<GryphonEntity, GryphonModel<GryphonEntity>> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            GryphonExpansion.MOD_ID, "textures/entity/gryphon/gryphon.png");

    public GryphonRenderer(EntityRendererProvider.Context context) {
        super(context,
              new GryphonModel<>(context.bakeLayer(GryphonModel.LAYER_LOCATION)),
              0.7f);  // shadow radius in blocks
        // TODO: add layers here, e.g.:
        //   this.addLayer(new GryphonSaddleLayer(this, context.getModelSet()));
        //   this.addLayer(new GryphonArmorLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GryphonEntity entity) {
        return TEXTURE;
    }
}
