package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import thaumcraft.common.tiles.crafting.TCFocalManipulatorBlockEntity;

final class TCFocalManipulatorRenderer implements BlockEntityRenderer<TCFocalManipulatorBlockEntity> {
    private final ItemRenderer itemRenderer;

    TCFocalManipulatorRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
            TCFocalManipulatorBlockEntity table,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay) {
        if (table.getLevel() == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        float ticks = (mc.player != null ? mc.player.tickCount : 0) + partialTick;
        ItemStack focus = table.getItem(TCFocalManipulatorBlockEntity.SLOT_FOCUS);
        if (!focus.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.8f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(ticks % 360.0f));
            float hoverStart = Mth.sin(ticks / 14.0f) * 0.2f + 0.2f;
            renderFloatingItem(table, focus, hoverStart, poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }

    private void renderFloatingItem(
            TCFocalManipulatorBlockEntity table,
            ItemStack stack,
            float hoverStart,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay) {
        poseStack.pushPose();
        float bob = Mth.sin(hoverStart) * 0.1f + 0.1f;
        poseStack.translate(0.0f, bob + 0.125f, 0.0f);
        poseStack.mulPose(Axis.YP.rotation(hoverStart));
        itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, table.getLevel(), 0);
        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(TCFocalManipulatorBlockEntity table) {
        return new AABB(table.getBlockPos()).expandTowards(0, 1, 0).inflate(0.5);
    }
}
