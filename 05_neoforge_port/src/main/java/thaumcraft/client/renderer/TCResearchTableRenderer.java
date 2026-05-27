package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.common.blocks.crafting.TCResearchTableBlock;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;

final class TCResearchTableRenderer implements BlockEntityRenderer<TCResearchTableBlockEntity> {
    private final ItemRenderer itemRenderer;

    TCResearchTableRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
            TCResearchTableBlockEntity table,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay) {
        poseStack.pushPose();
        orientToTable(table, poseStack);

        ItemStack paper = table.getItem(TCResearchTableBlockEntity.SLOT_PAPER);
        if (paper.is(Items.PAPER) && !paper.isEmpty()) {
            renderPaper(table, poseStack, bufferSource, packedLight);
        }

        ItemStack tools = table.getItem(TCResearchTableBlockEntity.SLOT_SCRIBING_TOOLS);
        if (!tools.isEmpty() && tools.getItem() instanceof IScribeTools) {
            renderScribingTools(table, tools, poseStack, bufferSource, packedLight);
        }

        poseStack.popPose();
    }

    private static void orientToTable(TCResearchTableBlockEntity table, PoseStack poseStack) {
        Direction facing = table.getBlockState().hasProperty(TCResearchTableBlock.FACING)
                ? table.getBlockState().getValue(TCResearchTableBlock.FACING)
                : Direction.NORTH;
        poseStack.translate(0.5D, 1.005D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationFor(facing)));
    }

    private static float rotationFor(Direction facing) {
        return switch (facing) {
            case EAST -> 90.0F;
            case SOUTH -> 180.0F;
            case WEST -> 270.0F;
            default -> 0.0F;
        };
    }

    private void renderPaper(TCResearchTableBlockEntity table, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.12D, 0.012D, -0.16D);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-8.0F));
        poseStack.scale(0.48F, 0.48F, 0.48F);
        itemRenderer.renderStatic(
                new ItemStack(Items.PAPER),
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                table.getLevel(),
                0);
        poseStack.popPose();
    }

    private void renderScribingTools(
            TCResearchTableBlockEntity table,
            ItemStack tools,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight) {
        poseStack.pushPose();
        poseStack.translate(-0.30D, 0.028D, 0.26D);
        poseStack.mulPose(Axis.XP.rotationDegrees(72.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(28.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-35.0F));
        poseStack.scale(0.42F, 0.42F, 0.42F);
        itemRenderer.renderStatic(
                tools,
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                table.getLevel(),
                1);
        poseStack.popPose();
    }
}
