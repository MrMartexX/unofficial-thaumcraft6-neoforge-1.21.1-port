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
import thaumcraft.common.tiles.crafting.TCGolemBuilderBlockEntity;

final class TCGolemBuilderRenderer implements BlockEntityRenderer<TCGolemBuilderBlockEntity> {
    private final ItemRenderer itemRenderer;

    TCGolemBuilderRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
            TCGolemBuilderBlockEntity entity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay) {
        ItemStack output = entity.getItem(0);
        if (output.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.65D, 0.5D);
        long gameTime = entity.getLevel() == null ? 0L : entity.getLevel().getGameTime();
        poseStack.mulPose(Axis.YP.rotationDegrees((gameTime + partialTick) * 2.5F));
        poseStack.scale(0.6F, 0.6F, 0.6F);
        itemRenderer.renderStatic(
                output,
                ItemDisplayContext.GROUND,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                entity.getLevel(),
                0
        );
        poseStack.popPose();
    }
}
