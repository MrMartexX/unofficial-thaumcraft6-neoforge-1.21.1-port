package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCTaintSeedEntity;

final class TCTaintSeedRenderer extends EntityRenderer<TCTaintSeedEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/taintseed.png");
    private final ModelPart root;

    TCTaintSeedRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.4F;
        root = createLegacyFoundationModel().bakeRoot();
    }

    @Override
    public void render(TCTaintSeedEntity seed, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        root.resetPose();
        float age = seed.tickCount + partialTick;
        float sway = Mth.sin(age * 0.08F) * 0.12F;
        root.yRot = sway;
        root.xRot = seed.attackAnim() * 0.35F;

        poseStack.pushPose();
        poseStack.translate(0.0F, 1.48F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
        float scale = seed.getArea() > 1 ? 0.115F : 0.085F;
        poseStack.scale(-scale, -scale, scale);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
        root.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(seed, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TCTaintSeedEntity entity) {
        return TEXTURE;
    }

    private static LayerDefinition createLegacyFoundationModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "base",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F)
                        .texOffs(0, 20).addBox(-3.0F, -18.0F, -3.0F, 6.0F, 8.0F, 6.0F),
                PartPose.ZERO
        );
        root.addOrReplaceChild(
                "tendril_north",
                CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, -18.0F, -18.0F, 4.0F, 4.0F, 18.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.35F, 0.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "tendril_south",
                CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, -18.0F, 0.0F, 4.0F, 4.0F, 18.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.35F, 0.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "tendril_west",
                CubeListBuilder.create().texOffs(32, 22).addBox(-18.0F, -16.0F, -2.0F, 18.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.35F)
        );
        root.addOrReplaceChild(
                "tendril_east",
                CubeListBuilder.create().texOffs(32, 22).addBox(0.0F, -16.0F, -2.0F, 18.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.35F)
        );
        return LayerDefinition.create(mesh, 64, 64);
    }
}
