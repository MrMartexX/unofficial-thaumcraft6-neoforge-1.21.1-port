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
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.essentia.transport.blockentity.TCLegacyTubeBlockEntity;

/** Legacy {@code TileTubeValveRenderer}/{@code ModelTubeValve} reconstruction. */
final class TCTubeValveRenderer implements BlockEntityRenderer<TCLegacyTubeBlockEntity> {
    private static final ResourceLocation VALVE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/models/valve.png");

    private final ModelPart rod;
    private final ModelPart ring;

    TCTubeValveRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = createModel().bakeRoot();
        rod = root.getChild("rod");
        ring = root.getChild("ring");
    }

    @Override
    public void render(
            TCLegacyTubeBlockEntity valve,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        Direction facing = valve.facing();
        float rotation = valve.valveRotation(partialTick);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(VALVE_TEXTURE));

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        orientToLegacyFacing(poseStack, facing);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotation * 1.5F));
        poseStack.translate(0.0F, -0.03F - rotation / 360.0F * 0.09F, 0.0F);
        ring.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.pushPose();
        poseStack.scale(0.75F, 1.0F, 0.75F);
        rod.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        poseStack.popPose();
    }

    private static void orientToLegacyFacing(PoseStack poseStack, Direction facing) {
        if (facing.getAxis().isHorizontal()) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        } else {
            poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F * facing.getStepY()));
        }
        switch (facing) {
            case EAST -> poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            case WEST -> poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
            case UP -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            case DOWN -> poseStack.mulPose(Axis.YN.rotationDegrees(90.0F));
            case SOUTH -> poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            case NORTH -> poseStack.mulPose(Axis.ZN.rotationDegrees(90.0F));
        }
    }

    private static LayerDefinition createModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "rod",
                CubeListBuilder.create().texOffs(0, 10).mirror()
                        .addBox(-1.0F, 2.0F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.ZERO
        );
        root.addOrReplaceChild(
                "ring",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                        .addBox(-2.0F, 4.0F, -2.0F, 4.0F, 1.0F, 4.0F),
                PartPose.ZERO
        );
        return LayerDefinition.create(mesh, 64, 32);
    }
}
