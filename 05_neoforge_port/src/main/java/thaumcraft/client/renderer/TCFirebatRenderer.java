package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCFirebatEntity;

final class TCFirebatRenderer extends EntityRenderer<TCFirebatEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/firebat.png");
    private static final float LEGACY_RENDER_SCALE = 0.35F;

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart outerRightWing;
    private final ModelPart outerLeftWing;

    TCFirebatRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.25F;
        ModelPart root = createLegacyModel().bakeRoot();
        head = root.getChild("head");
        body = root.getChild("body");
        rightWing = body.getChild("right_wing");
        leftWing = body.getChild("left_wing");
        outerRightWing = rightWing.getChild("outer_right_wing");
        outerLeftWing = leftWing.getChild("outer_left_wing");
    }

    @Override
    public void render(TCFirebatEntity firebat, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        setupLegacyAnim(firebat, partialTick);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        poseStack.pushPose();
        if (firebat.isResting()) {
            poseStack.translate(0.0F, -0.1F, 0.0F);
        } else {
            poseStack.translate(0.0F, Mth.cos((firebat.tickCount + partialTick) * 0.3F) * 0.1F, 0.0F);
        }
        poseStack.translate(0.0F, 1.5F, 0.0F);
        poseStack.scale(-LEGACY_RENDER_SCALE, -LEGACY_RENDER_SCALE, LEGACY_RENDER_SCALE);
        head.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        body.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(firebat, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TCFirebatEntity entity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLightLevel(TCFirebatEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected int getSkyLightLevel(TCFirebatEntity entity, BlockPos pos) {
        return 15;
    }

    private void setupLegacyAnim(TCFirebatEntity firebat, float partialTick) {
        head.resetPose();
        body.resetPose();
        rightWing.resetPose();
        leftWing.resetPose();
        outerRightWing.resetPose();
        outerLeftWing.resetPose();

        if (firebat.isResting()) {
            head.xRot = firebat.getXRot() * ((float) Math.PI / 180.0F);
            head.yRot = (float) Math.PI - firebat.getYHeadRot() * ((float) Math.PI / 180.0F);
            head.zRot = (float) Math.PI;
            head.setPos(0.0F, -2.0F, 0.0F);
            rightWing.setPos(-3.0F, 0.0F, 3.0F);
            leftWing.setPos(3.0F, 0.0F, 3.0F);
            body.xRot = (float) Math.PI;
            rightWing.xRot = -0.15707964F;
            rightWing.yRot = -1.2566371F;
            outerRightWing.yRot = -1.7278761F;
            leftWing.xRot = rightWing.xRot;
            leftWing.yRot = -rightWing.yRot;
            outerLeftWing.yRot = -outerRightWing.yRot;
        } else {
            head.xRot = firebat.getXRot() * ((float) Math.PI / 180.0F);
            head.yRot = firebat.getYHeadRot() * ((float) Math.PI / 180.0F);
            head.zRot = 0.0F;
            head.setPos(0.0F, 0.0F, 0.0F);
            rightWing.setPos(0.0F, 0.0F, 0.0F);
            leftWing.setPos(0.0F, 0.0F, 0.0F);
            float age = firebat.tickCount + partialTick;
            body.xRot = 0.7853982F + Mth.cos(age * 0.1F) * 0.15F;
            body.yRot = 0.0F;
            rightWing.yRot = Mth.cos(age * 1.3F) * (float) Math.PI * 0.25F;
            leftWing.yRot = -rightWing.yRot;
            outerRightWing.yRot = rightWing.yRot * 0.5F;
            outerLeftWing.yRot = -rightWing.yRot * 0.5F;
        }
    }

    private static LayerDefinition createLegacyModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.ZERO
        );
        head.addOrReplaceChild(
                "right_ear",
                CubeListBuilder.create().texOffs(24, 0).addBox(-4.0F, -6.0F, -2.0F, 3.0F, 4.0F, 1.0F),
                PartPose.ZERO
        );
        head.addOrReplaceChild(
                "left_ear",
                CubeListBuilder.create().mirror().texOffs(24, 0).addBox(1.0F, -6.0F, -2.0F, 3.0F, 4.0F, 1.0F),
                PartPose.ZERO
        );
        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-3.0F, 4.0F, -3.0F, 6.0F, 12.0F, 6.0F)
                        .texOffs(0, 34).addBox(-5.0F, 16.0F, 0.0F, 10.0F, 6.0F, 1.0F),
                PartPose.ZERO
        );
        PartDefinition rightWing = body.addOrReplaceChild(
                "right_wing",
                CubeListBuilder.create().texOffs(42, 0).addBox(-12.0F, 1.0F, 1.5F, 10.0F, 16.0F, 1.0F),
                PartPose.ZERO
        );
        rightWing.addOrReplaceChild(
                "outer_right_wing",
                CubeListBuilder.create().texOffs(24, 16).addBox(-8.0F, 1.0F, 0.0F, 8.0F, 12.0F, 1.0F),
                PartPose.offset(-12.0F, 1.0F, 1.5F)
        );
        PartDefinition leftWing = body.addOrReplaceChild(
                "left_wing",
                CubeListBuilder.create().mirror().texOffs(42, 0).addBox(2.0F, 1.0F, 1.5F, 10.0F, 16.0F, 1.0F),
                PartPose.ZERO
        );
        leftWing.addOrReplaceChild(
                "outer_left_wing",
                CubeListBuilder.create().mirror().texOffs(24, 16).addBox(0.0F, 1.0F, 0.0F, 8.0F, 12.0F, 1.0F),
                PartPose.offset(12.0F, 1.0F, 1.5F)
        );
        return LayerDefinition.create(mesh, 64, 64);
    }
}
