package thaumcraft.client.renderer;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import thaumcraft.common.blocks.devices.TCVoidSiphonBlock;
import thaumcraft.common.tiles.devices.TCVoidSiphonBlockEntity;

final class TCVoidSiphonRenderer implements BlockEntityRenderer<TCVoidSiphonBlockEntity> {
    TCVoidSiphonRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            TCVoidSiphonBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay) {
        if (!blockEntity.getBlockState().hasProperty(TCVoidSiphonBlock.ENABLED)
                || !blockEntity.getBlockState().getValue(TCVoidSiphonBlock.ENABLED)) {
            return;
        }

        VertexConsumer vc = bufferSource.getBuffer(RenderType.endPortal());
        Matrix4f m = poseStack.last().pose();
        // The orb: a 0.2 cube on every face, 0.126 from its center.
        box(vc, m, 0.5f, 0.875f, 0.5f, 0.126f, 0.1f, 0.1f);
        // The core: 0.25 x 0.5 windows 0.26 from its center.
        box(vc, m, 0.5f, 0.3125f, 0.5f, 0.26f, 0.125f, 0.25f);
    }

    private static void box(VertexConsumer vc, Matrix4f m, float cx, float cy, float cz, float d, float w, float h) {
        // Up and down.
        quad(vc, m, cx - w, cy + d, cz - w, cx - w, cy + d, cz + w, cx + w, cy + d, cz + w, cx + w, cy + d, cz - w);
        quad(vc, m, cx - w, cy - d, cz - w, cx + w, cy - d, cz - w, cx + w, cy - d, cz + w, cx - w, cy - d, cz + w);
        // North and south.
        quad(vc, m, cx - w, cy - h, cz - d, cx - w, cy + h, cz - d, cx + w, cy + h, cz - d, cx + w, cy - h, cz - d);
        quad(vc, m, cx - w, cy - h, cz + d, cx + w, cy - h, cz + d, cx + w, cy + h, cz + d, cx - w, cy + h, cz + d);
        // West and east.
        quad(vc, m, cx - d, cy - h, cz - w, cx - d, cy - h, cz + w, cx - d, cy + h, cz + w, cx - d, cy + h, cz - w);
        quad(vc, m, cx + d, cy - h, cz - w, cx + d, cy + h, cz - w, cx + d, cy + h, cz + w, cx + d, cy - h, cz + w);
    }

    private static void quad(VertexConsumer vc, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4) {
        vc.addVertex(m, x1, y1, z1);
        vc.addVertex(m, x2, y2, z2);
        vc.addVertex(m, x3, y3, z3);
        vc.addVertex(m, x4, y4, z4);
        vc.addVertex(m, x4, y4, z4);
        vc.addVertex(m, x3, y3, z3);
        vc.addVertex(m, x2, y2, z2);
        vc.addVertex(m, x1, y1, z1);
    }
}
