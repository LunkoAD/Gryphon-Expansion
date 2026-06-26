package net.lunkoashtail.gryphonexpansion.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lunkoashtail.gryphonexpansion.GryphonExpansion;
import net.lunkoashtail.gryphonexpansion.entity.GryphonEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Basic Gryphon model — quadruped body with eagle head, 4 pawed legs, and folded wings.
 *
 * <p>Texture sheet: 128 × 64 px (adjust if you add more detail).
 * The placeholder PNG at {@code textures/entity/gryphon/gryphon.png} maps to:
 * <ul>
 *   <li>UV (0,  0) – body
 *   <li>UV (0, 24) – head
 *   <li>UV (32, 0) – wings, legs, tail
 * </ul>
 *
 * <p>TODO: Replace this with a proper Blockbench model exported to Java, or
 * integrate GeckoLib (https://github.com/bernie-g/geckolib) for animations.
 * GeckoLib requires: a .geo.json model, a .animation.json file, and replacing
 * {@code EntityModel} with {@code GeoEntityModel}.
 */
public class GryphonModel<T extends GryphonEntity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(GryphonExpansion.MOD_ID, "gryphon"), "main");

    // Model parts — kept as fields so setupAnim can animate them each frame
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart beak;
    private final ModelPart tail;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightBackLeg;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    /** Receives the baked {@link ModelPart} root from {@link net.minecraft.client.model.geom.EntityModelSet}. */
    public GryphonModel(ModelPart root) {
        // All children are children of the root part, matching the hierarchy in createBodyLayer()
        this.body          = root.getChild("body");
        this.neck          = body.getChild("neck");
        this.head          = neck.getChild("head");
        this.beak          = head.getChild("beak");
        this.tail          = body.getChild("tail");
        this.leftFrontLeg  = body.getChild("left_front_leg");
        this.rightFrontLeg = body.getChild("right_front_leg");
        this.leftBackLeg   = body.getChild("left_back_leg");
        this.rightBackLeg  = body.getChild("right_back_leg");
        this.leftWing      = body.getChild("left_wing");
        this.rightWing     = body.getChild("right_wing");
    }

    /**
     * Defines the mesh geometry (cube placements, UV, offsets).
     * Called once at registration time to produce a {@link LayerDefinition}.
     *
     * <p>Texture resolution: 128 × 64.
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // ── Body ──────────────────────────────────────────────────────────────────
        // Pivot at foot level (y=14) so the entity sits on the ground naturally.
        // Box: 10w × 10h × 14d, centered on X and Z.
        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-5.0f, -10.0f, -7.0f, 10, 10, 14),
                PartPose.offset(0.0f, 14.0f, 0.0f));

        // ── Neck (child of body) ──────────────────────────────────────────────────
        PartDefinition neck = body.addOrReplaceChild("neck",
                CubeListBuilder.create()
                        .texOffs(48, 0)
                        .addBox(-2.0f, -5.0f, -2.0f, 4, 6, 4),
                PartPose.offsetAndRotation(0.0f, -9.5f, -5.0f, -0.4f, 0.0f, 0.0f));

        // ── Head (child of neck) ──────────────────────────────────────────────────
        PartDefinition head = neck.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 24)
                        .addBox(-3.0f, -5.0f, -4.0f, 6, 5, 5),
                PartPose.offset(0.0f, -5.0f, 0.0f));

        // Eagle-style beak
        head.addOrReplaceChild("beak",
                CubeListBuilder.create()
                        .texOffs(22, 24)
                        .addBox(-1.5f, -2.5f, -6.0f, 3, 2, 3),
                PartPose.offset(0.0f, 0.0f, -3.0f));

        // ── Tail (child of body) ──────────────────────────────────────────────────
        body.addOrReplaceChild("tail",
                CubeListBuilder.create()
                        .texOffs(64, 0)
                        .addBox(-1.5f, -1.5f, 0.0f, 3, 3, 7),
                PartPose.offsetAndRotation(0.0f, -5.0f, 7.0f, 0.3f, 0.0f, 0.0f));

        // ── Front legs ───────────────────────────────────────────────────────────
        body.addOrReplaceChild("left_front_leg",
                CubeListBuilder.create()
                        .texOffs(32, 0)
                        .addBox(-1.5f, 0.0f, -1.5f, 3, 10, 3),
                PartPose.offset(3.0f, 0.0f, -4.0f));

        body.addOrReplaceChild("right_front_leg",
                CubeListBuilder.create()
                        .texOffs(44, 0)
                        .addBox(-1.5f, 0.0f, -1.5f, 3, 10, 3),
                PartPose.offset(-3.0f, 0.0f, -4.0f));

        // ── Back legs ────────────────────────────────────────────────────────────
        body.addOrReplaceChild("left_back_leg",
                CubeListBuilder.create()
                        .texOffs(32, 13)
                        .addBox(-1.5f, 0.0f, -1.5f, 3, 10, 3),
                PartPose.offset(3.5f, 0.0f, 4.5f));

        body.addOrReplaceChild("right_back_leg",
                CubeListBuilder.create()
                        .texOffs(44, 13)
                        .addBox(-1.5f, 0.0f, -1.5f, 3, 10, 3),
                PartPose.offset(-3.5f, 0.0f, 4.5f));

        // ── Wings (folded at rest) ─────────────────────────────────────────────────
        // TODO: animate wings with GeckoLib — add flap cycle when airborne.
        body.addOrReplaceChild("left_wing",
                CubeListBuilder.create()
                        .texOffs(80, 0)
                        .addBox(0.0f, -4.0f, -4.0f, 1, 8, 12),
                PartPose.offsetAndRotation(5.5f, -7.0f, 0.0f, 0.0f, 0.0f, 0.35f));

        body.addOrReplaceChild("right_wing",
                CubeListBuilder.create()
                        .texOffs(80, 20)
                        .addBox(-1.0f, -4.0f, -4.0f, 1, 8, 12),
                PartPose.offsetAndRotation(-5.5f, -7.0f, 0.0f, 0.0f, 0.0f, -0.35f));

        return LayerDefinition.create(mesh, 128, 64);
    }

    // ─── Animation ────────────────────────────────────────────────────────────────

    /**
     * Called each frame before rendering. {@code limbSwing} advances every tick the
     * entity is moving; {@code limbSwingAmount} is the 0–1 magnitude of movement.
     */
    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {

        // Head tracking follows the look direction
        head.yRot = netHeadYaw  * Mth.DEG_TO_RAD;
        head.xRot = headPitch   * Mth.DEG_TO_RAD;
        // Neck mirrors head slightly for a natural look
        neck.xRot = headPitch * Mth.DEG_TO_RAD * 0.3f - 0.4f;

        // Classic quadruped leg swing (same pattern as horses / wolves)
        final float speed = 0.6662f;
        leftFrontLeg.xRot  =  Mth.cos(limbSwing * speed)           * 1.4f * limbSwingAmount;
        rightFrontLeg.xRot =  Mth.cos(limbSwing * speed + Mth.PI)  * 1.4f * limbSwingAmount;
        leftBackLeg.xRot   =  Mth.cos(limbSwing * speed + Mth.PI)  * 1.4f * limbSwingAmount;
        rightBackLeg.xRot  =  Mth.cos(limbSwing * speed)           * 1.4f * limbSwingAmount;

        // Tail wag: gentle idle bob
        tail.xRot = 0.3f + Mth.sin(ageInTicks * 0.05f) * 0.1f;

        // Wing idle flutter (very subtle)
        // TODO: replace with proper flap animation when flying behavior is added
        float wingBob = Mth.sin(ageInTicks * 0.05f) * 0.04f;
        leftWing.zRot  =  0.35f + wingBob;
        rightWing.zRot = -0.35f - wingBob;
    }

    // ─── Render ───────────────────────────────────────────────────────────────────

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buffer,
                               int packedLight, int packedOverlay, int color) {
        // Render the entire part tree starting from body.
        // All other parts (neck, head, legs, wings, tail) are children of body.
        body.render(pose, buffer, packedLight, packedOverlay, color);
    }
}
