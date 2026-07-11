package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.blocks.crafting.TCThaumatoriumBlock;
import thaumcraft.common.tiles.crafting.TCThaumatoriumBlockEntity;

final class TCThaumatoriumRenderer implements BlockEntityRenderer<TCThaumatoriumBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    TCThaumatoriumRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(TCThaumatoriumBlockEntity thaumatorium, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack display = thaumatorium.displayRecipeOutput();
        if (display.isEmpty()) {
            return;
        }
        Direction facing = thaumatorium.getBlockState().hasProperty(TCThaumatoriumBlock.FACING)
                ? thaumatorium.getBlockState().getValue(TCThaumatoriumBlock.FACING)
                : Direction.NORTH;
        poseStack.pushPose();
        poseStack.translate(0.5D + facing.getStepX() / 1.99D, 1.125D, 0.5D + facing.getStepZ() / 1.99D);
        poseStack.mulPose(Axis.YP.rotationDegrees(yawFor(facing)));
        poseStack.scale(0.75F, 0.75F, 0.75F);
        context.getItemRenderer().renderStatic(display, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, thaumatorium.getLevel(), 0);
        poseStack.popPose();
    }

    private static float yawFor(Direction facing) {
        return switch (facing) {
            case EAST -> 90.0F;
            case WEST -> 270.0F;
            case NORTH -> 180.0F;
            default -> 0.0F;
        };
    }
}
