package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.tiles.misc.TCHoleBlockEntity;

/**
 * 1:1 port of legacy TileHoleRenderer:
 * renders an end-portal starry void quad on each face of a Portable Hole block
 * that borders a non-air, non-hole block.
 */
final class TCHoleRenderer implements BlockEntityRenderer<TCHoleBlockEntity> {
    TCHoleRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            TCHoleBlockEntity hole,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        if (hole.getLevel() == null) {
            return;
        }

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer consumer = null;

        for (Direction face : Direction.values()) {
            BlockPos neighborPos = hole.getBlockPos().relative(face);
            BlockState neighbor = hole.getLevel().getBlockState(neighborPos);
            if (!neighbor.isAir() && !neighbor.is(TCBlocks.HOLE.get())) {
                if (consumer == null) {
                    consumer = buffers.getBuffer(RenderType.endPortal());
                }
                emitTwoSidedFaceQuad(consumer, matrix, face);
            }
        }
    }

    private static void emitTwoSidedFaceQuad(VertexConsumer consumer, Matrix4f matrix, Direction face) {
        float min = 0.001F;
        float max = 0.999F;
        switch (face) {
            case DOWN -> {
                quad(consumer, matrix, 0.0F, min, 0.0F, 1.0F, min, 0.0F, 1.0F, min, 1.0F, 0.0F, min, 1.0F);
                quad(consumer, matrix, 0.0F, min, 1.0F, 1.0F, min, 1.0F, 1.0F, min, 0.0F, 0.0F, min, 0.0F);
            }
            case UP -> {
                quad(consumer, matrix, 0.0F, max, 1.0F, 1.0F, max, 1.0F, 1.0F, max, 0.0F, 0.0F, max, 0.0F);
                quad(consumer, matrix, 0.0F, max, 0.0F, 1.0F, max, 0.0F, 1.0F, max, 1.0F, 0.0F, max, 1.0F);
            }
            case NORTH -> {
                quad(consumer, matrix, 0.0F, 0.0F, min, 0.0F, 1.0F, min, 1.0F, 1.0F, min, 1.0F, 0.0F, min);
                quad(consumer, matrix, 1.0F, 0.0F, min, 1.0F, 1.0F, min, 0.0F, 1.0F, min, 0.0F, 0.0F, min);
            }
            case SOUTH -> {
                quad(consumer, matrix, 1.0F, 0.0F, max, 1.0F, 1.0F, max, 0.0F, 1.0F, max, 0.0F, 0.0F, max);
                quad(consumer, matrix, 0.0F, 0.0F, max, 0.0F, 1.0F, max, 1.0F, 1.0F, max, 1.0F, 0.0F, max);
            }
            case WEST -> {
                quad(consumer, matrix, min, 0.0F, 1.0F, min, 1.0F, 1.0F, min, 1.0F, 0.0F, min, 0.0F, 0.0F);
                quad(consumer, matrix, min, 0.0F, 0.0F, min, 1.0F, 0.0F, min, 1.0F, 1.0F, min, 0.0F, 1.0F);
            }
            case EAST -> {
                quad(consumer, matrix, max, 0.0F, 0.0F, max, 1.0F, 0.0F, max, 1.0F, 1.0F, max, 0.0F, 1.0F);
                quad(consumer, matrix, max, 0.0F, 1.0F, max, 1.0F, 1.0F, max, 1.0F, 0.0F, max, 0.0F, 0.0F);
            }
        }
    }

    private static void quad(
            VertexConsumer consumer,
            Matrix4f matrix,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4
    ) {
        consumer.addVertex(matrix, x1, y1, z1);
        consumer.addVertex(matrix, x2, y2, z2);
        consumer.addVertex(matrix, x3, y3, z3);
        consumer.addVertex(matrix, x4, y4, z4);
    }
}
