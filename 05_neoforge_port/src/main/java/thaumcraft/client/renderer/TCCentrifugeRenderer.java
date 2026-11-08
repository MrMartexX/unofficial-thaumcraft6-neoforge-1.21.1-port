package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.tiles.essentia.TCCentrifugeBlockEntity;

final class TCCentrifugeRenderer implements BlockEntityRenderer<TCCentrifugeBlockEntity> {
    private static final ResourceLocation TEX =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/models/centrifuge.png");
    private final TCModelCentrifuge model = new TCModelCentrifuge();

    TCCentrifugeRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            TCCentrifugeBlockEntity centrifuge,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay) {
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEX));
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        model.renderBoxes(poseStack, vc, packedLight, packedOverlay);
        float rotation = centrifuge.rotation + centrifuge.rotationSpeed * partialTick;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        model.renderSpinnyBit(poseStack, vc, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
