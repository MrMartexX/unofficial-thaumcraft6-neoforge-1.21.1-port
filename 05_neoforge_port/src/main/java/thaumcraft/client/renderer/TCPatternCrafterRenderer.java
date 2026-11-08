package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.crafting.TCPatternCrafterBlock;
import thaumcraft.common.tiles.crafting.TCPatternCrafterBlockEntity;

/**
 * 1:1 port of legacy TilePatternCrafterRenderer:
 * renders the active crafting pattern mode icon on the front face and two
 * counter-rotating 3D extruded brass gears inside the housing.
 */
final class TCPatternCrafterRenderer implements BlockEntityRenderer<TCPatternCrafterBlockEntity> {
    private static final ResourceLocation TEX_MODE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/block/pattern_crafter_modes.png");
    private static final ResourceLocation TEX_GEAR =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/gear_brass.png");

    TCPatternCrafterRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            TCPatternCrafterBlockEntity crafter,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        Direction facing = crafter.getBlockState().hasProperty(TCPatternCrafterBlock.FACING)
                ? crafter.getBlockState().getValue(TCPatternCrafterBlock.FACING)
                : Direction.NORTH;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.75F, 0.5F);
        switch (facing) {
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            default -> {
            }
        }

        // Pattern mode icon on the front indicator panel
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        poseStack.translate(0.0F, 0.0F, -0.501F);
        renderModeStripQuad(
                buffers.getBuffer(RenderType.entityTranslucent(TEX_MODE)),
                poseStack.last(),
                10,
                1,
                Math.max(0, Math.min(9, crafter.getPatternType())),
                0.5F,
                packedLight
        );
        poseStack.popPose();

        float rot = crafter.getClientRot();
        VertexConsumer gearConsumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TEX_GEAR));

        // Left brass gear
        poseStack.pushPose();
        poseStack.translate(-0.2F, -0.40625F, 0.05F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-rot % 360.0F));
        poseStack.scale(0.5F, 0.5F, 0.2F);
        poseStack.translate(-0.5F, -0.5F, 0.0F);
        renderTextureIn3D(gearConsumer, poseStack.last(), 1.0F, 1.0F, 0.0F, 0.0F, 16, 16, 0.1F, packedLight);
        poseStack.popPose();

        // Right brass gear
        poseStack.pushPose();
        poseStack.translate(0.2F, -0.40625F, 0.05F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(rot % 360.0F));
        poseStack.scale(0.5F, 0.5F, 0.2F);
        poseStack.translate(-0.5F, -0.5F, 0.0F);
        renderTextureIn3D(gearConsumer, poseStack.last(), 1.0F, 1.0F, 0.0F, 0.0F, 16, 16, 0.1F, packedLight);
        poseStack.popPose();

        poseStack.popPose();
    }

    private static void renderModeStripQuad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            int gridX,
            int gridY,
            int frame,
            float scale,
            int packedLight
    ) {
        int xm = frame % gridX;
        int ym = frame / gridY;
        float u0 = (float) xm / gridX;
        float u1 = u0 + 1.0F / gridX;
        float v0 = (float) ym / gridY;
        float v1 = v0 + 1.0F / gridY;
        float half = 0.5F * scale;

        vertex(consumer, pose, -half, half, 0.0F, u1, v1, packedLight, 0, 0, 1);
        vertex(consumer, pose, half, half, 0.0F, u1, v0, packedLight, 0, 0, 1);
        vertex(consumer, pose, half, -half, 0.0F, u0, v0, packedLight, 0, 0, 1);
        vertex(consumer, pose, -half, -half, 0.0F, u0, v1, packedLight, 0, 0, 1);

        vertex(consumer, pose, -half, -half, 0.0F, u0, v1, packedLight, 0, 0, -1);
        vertex(consumer, pose, half, -half, 0.0F, u0, v0, packedLight, 0, 0, -1);
        vertex(consumer, pose, half, half, 0.0F, u1, v0, packedLight, 0, 0, -1);
        vertex(consumer, pose, -half, half, 0.0F, u1, v1, packedLight, 0, 0, -1);
    }

    static void renderTextureIn3D(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float maxU,
            float maxV,
            float minU,
            float minV,
            int width,
            int height,
            float thickness,
            int packedLight
    ) {
        // Front face (z = 0)
        vertex(consumer, pose, 0.0F, 0.0F, 0.0F, maxU, minV, packedLight, 0.0F, 0.0F, 1.0F);
        vertex(consumer, pose, 1.0F, 0.0F, 0.0F, minU, minV, packedLight, 0.0F, 0.0F, 1.0F);
        vertex(consumer, pose, 1.0F, 1.0F, 0.0F, minU, maxV, packedLight, 0.0F, 0.0F, 1.0F);
        vertex(consumer, pose, 0.0F, 1.0F, 0.0F, maxU, maxV, packedLight, 0.0F, 0.0F, 1.0F);

        // Back face (z = -thickness)
        vertex(consumer, pose, 0.0F, 1.0F, -thickness, maxU, maxV, packedLight, 0.0F, 0.0F, -1.0F);
        vertex(consumer, pose, 1.0F, 1.0F, -thickness, minU, maxV, packedLight, 0.0F, 0.0F, -1.0F);
        vertex(consumer, pose, 1.0F, 0.0F, -thickness, minU, minV, packedLight, 0.0F, 0.0F, -1.0F);
        vertex(consumer, pose, 0.0F, 0.0F, -thickness, maxU, minV, packedLight, 0.0F, 0.0F, -1.0F);

        float halfTexelU = 0.5F * (maxU - minU) / width;
        float halfTexelV = 0.5F * (minV - maxV) / height;

        // -X and +X pixel slices
        for (int k = 0; k < width; k++) {
            float x0 = (float) k / width;
            float x1 = x0 + 1.0F / width;
            float u = maxU + (minU - maxU) * x0 - halfTexelU;

            vertex(consumer, pose, x0, 0.0F, -thickness, u, minV, packedLight, -1.0F, 0.0F, 0.0F);
            vertex(consumer, pose, x0, 0.0F, 0.0F, u, minV, packedLight, -1.0F, 0.0F, 0.0F);
            vertex(consumer, pose, x0, 1.0F, 0.0F, u, maxV, packedLight, -1.0F, 0.0F, 0.0F);
            vertex(consumer, pose, x0, 1.0F, -thickness, u, maxV, packedLight, -1.0F, 0.0F, 0.0F);

            vertex(consumer, pose, x1, 1.0F, -thickness, u, maxV, packedLight, 1.0F, 0.0F, 0.0F);
            vertex(consumer, pose, x1, 1.0F, 0.0F, u, maxV, packedLight, 1.0F, 0.0F, 0.0F);
            vertex(consumer, pose, x1, 0.0F, 0.0F, u, minV, packedLight, 1.0F, 0.0F, 0.0F);
            vertex(consumer, pose, x1, 0.0F, -thickness, u, minV, packedLight, 1.0F, 0.0F, 0.0F);
        }

        // +Y and -Y pixel slices
        for (int k = 0; k < height; k++) {
            float y0 = (float) k / height;
            float y1 = y0 + 1.0F / height;
            float v = minV + (maxV - minV) * y0 - halfTexelV;

            vertex(consumer, pose, 0.0F, y1, 0.0F, maxU, v, packedLight, 0.0F, 1.0F, 0.0F);
            vertex(consumer, pose, 1.0F, y1, 0.0F, minU, v, packedLight, 0.0F, 1.0F, 0.0F);
            vertex(consumer, pose, 1.0F, y1, -thickness, minU, v, packedLight, 0.0F, 1.0F, 0.0F);
            vertex(consumer, pose, 0.0F, y1, -thickness, maxU, v, packedLight, 0.0F, 1.0F, 0.0F);

            vertex(consumer, pose, 1.0F, y0, 0.0F, minU, v, packedLight, 0.0F, -1.0F, 0.0F);
            vertex(consumer, pose, 0.0F, y0, 0.0F, maxU, v, packedLight, 0.0F, -1.0F, 0.0F);
            vertex(consumer, pose, 0.0F, y0, -thickness, maxU, v, packedLight, 0.0F, -1.0F, 0.0F);
            vertex(consumer, pose, 1.0F, y0, -thickness, minU, v, packedLight, 0.0F, -1.0F, 0.0F);
        }
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float u,
            float v,
            int packedLight,
            float nx,
            float ny,
            float nz
    ) {
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(1.0F, 1.0F, 1.0F, 1.0F)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, nx, ny, nz);
    }
}
