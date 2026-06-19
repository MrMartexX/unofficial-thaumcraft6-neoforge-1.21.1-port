package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.tiles.crafting.TCInfusionPedestalBlockEntity;

final class TCInfusionPedestalRenderer implements BlockEntityRenderer<TCInfusionPedestalBlockEntity> {
    private final ItemRenderer itemRenderer;

    TCInfusionPedestalRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
            TCInfusionPedestalBlockEntity pedestal,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay) {
        ItemStack stack = pedestal.getStoredStack();
        if (stack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.08D, 0.5D);
        long gameTime = pedestal.getLevel() == null ? 0L : pedestal.getLevel().getGameTime();
        poseStack.mulPose(Axis.YP.rotationDegrees((gameTime + partialTick) * 2.0F));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.GROUND,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                pedestal.getLevel(),
                0
        );
        poseStack.popPose();
    }
}
