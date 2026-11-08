package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.tiles.crafting.TCInfusionPedestalBlockEntity;

/**
 * 1:1 port of legacy TilePedestalRenderer (scale 1.25) and TileRechargePedestalRenderer (scale 1.5).
 */
final class TCInfusionPedestalRenderer implements BlockEntityRenderer<TCInfusionPedestalBlockEntity> {
    private final ItemRenderer itemRenderer;

    TCInfusionPedestalRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
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
        ItemStack single = stack.copyWithCount(1);
        Minecraft mc = Minecraft.getInstance();
        float ticks = (mc.getCameraEntity() != null
                ? mc.getCameraEntity().tickCount
                : (pedestal.getLevel() != null ? pedestal.getLevel().getGameTime() : 0L)) + partialTick;
        boolean recharge = pedestal.getBlockState().is(TCBlocks.RECHARGE_PEDESTAL.get());
        float scale = recharge ? 1.5F : 1.25F;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.75F, 0.5F);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0.0F, 0.125F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(ticks % 360.0F));
        itemRenderer.renderStatic(
                single,
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

    @Override
    public AABB getRenderBoundingBox(TCInfusionPedestalBlockEntity pedestal) {
        return new AABB(pedestal.getBlockPos()).expandTowards(0.0D, 1.0D, 0.0D);
    }
}
