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
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.common.blocks.crafting.TCResearchTableBlock;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;

final class TCResearchTableRenderer implements BlockEntityRenderer<TCResearchTableBlockEntity> {
    private static final ResourceLocation TABLE_DETAIL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/blocks/research_table_model.png");
    private static final ResourceLocation QUILL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/research/quill.png");
    private static final int TEXTURE_WIDTH = 64;
    private static final int TEXTURE_HEIGHT = 32;

    TCResearchTableRenderer(BlockEntityRendererProvider.Context context) {
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

        if (table.getTheoryData() != null) {
            renderScroll(poseStack, bufferSource, packedLight);
        }

        ItemStack tools = table.getItem(TCResearchTableBlockEntity.SLOT_SCRIBING_TOOLS);
        if (!tools.isEmpty() && tools.getItem() instanceof IScribeTools) {
            renderInkwell(poseStack, bufferSource, packedLight);
            renderQuill(poseStack, bufferSource, packedLight);
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

    private static void renderScroll(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TABLE_DETAIL_TEXTURE));
        renderBox(
                consumer,
                poseStack,
                packedLight,
                -0.62F,
                0.015F,
                -0.02F,
                -0.12F,
                0.14F,
                0.105F,
                0,
                0,
                10,
                4,
                255,
                255,
                255,
                255);

        int color = Aspect.ALCHEMY.getColor();
        renderBox(
                consumer,
                poseStack,
                packedLight,
                -0.40F,
                0.005F,
                -0.035F,
                -0.32F,
                0.15F,
                0.12F,
                0,
                4,
                4,
                8,
                (color >> 16) & 255,
                (color >> 8) & 255,
                color & 255,
                255);
    }

    private static void renderInkwell(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(TABLE_DETAIL_TEXTURE));
        renderBox(
                consumer,
                poseStack,
                packedLight,
                -0.38F,
                0.01F,
                0.17F,
                -0.19F,
                0.135F,
                0.36F,
                0,
                16,
                8,
                24,
                255,
                255,
                255,
                255);
    }

    private static void renderQuill(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(-0.34D, 0.145D, 0.26D);
        poseStack.mulPose(Axis.YP.rotationDegrees(60.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-20.0F));
        poseStack.scale(0.34F, 0.34F, 0.34F);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(QUILL_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        addVertex(consumer, pose, packedLight, -0.5F, -0.5F, 0.0F, 0.0F, 1.0F, 255, 255, 255, 255, 0.0F, 1.0F, 0.0F);
        addVertex(consumer, pose, packedLight, 0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 255, 255, 255, 255, 0.0F, 1.0F, 0.0F);
        addVertex(consumer, pose, packedLight, 0.5F, 0.5F, 0.0F, 1.0F, 0.0F, 255, 255, 255, 255, 0.0F, 1.0F, 0.0F);
        addVertex(consumer, pose, packedLight, -0.5F, 0.5F, 0.0F, 0.0F, 0.0F, 255, 255, 255, 255, 0.0F, 1.0F, 0.0F);
        poseStack.popPose();
    }

    private static void renderBox(
            VertexConsumer consumer,
            PoseStack poseStack,
            int packedLight,
            float minX,
            float minY,
            float minZ,
            float maxX,
            float maxY,
            float maxZ,
            int textureU,
            int textureV,
            int textureWidth,
            int textureHeight,
            int red,
            int green,
            int blue,
            int alpha) {
        float u0 = textureU / (float) TEXTURE_WIDTH;
        float v0 = textureV / (float) TEXTURE_HEIGHT;
        float u1 = (textureU + textureWidth) / (float) TEXTURE_WIDTH;
        float v1 = (textureV + textureHeight) / (float) TEXTURE_HEIGHT;
        PoseStack.Pose pose = poseStack.last();

        addQuad(consumer, pose, packedLight, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ, u0, v1, u1, v1, u1, v0, u0, v0, red, green, blue, alpha, 0.0F, 0.0F, 1.0F);
        addQuad(consumer, pose, packedLight, maxX, minY, minZ, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, u0, v1, u1, v1, u1, v0, u0, v0, red, green, blue, alpha, 0.0F, 0.0F, -1.0F);
        addQuad(consumer, pose, packedLight, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ, u0, v1, u1, v1, u1, v0, u0, v0, red, green, blue, alpha, -1.0F, 0.0F, 0.0F);
        addQuad(consumer, pose, packedLight, maxX, minY, maxZ, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, u0, v1, u1, v1, u1, v0, u0, v0, red, green, blue, alpha, 1.0F, 0.0F, 0.0F);
        addQuad(consumer, pose, packedLight, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, minX, maxY, minZ, u0, v1, u1, v1, u1, v0, u0, v0, red, green, blue, alpha, 0.0F, 1.0F, 0.0F);
        addQuad(consumer, pose, packedLight, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ, u0, v1, u1, v1, u1, v0, u0, v0, red, green, blue, alpha, 0.0F, -1.0F, 0.0F);
    }

    private static void addQuad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            int packedLight,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2,
            float x3,
            float y3,
            float z3,
            float x4,
            float y4,
            float z4,
            float u1,
            float v1,
            float u2,
            float v2,
            float u3,
            float v3,
            float u4,
            float v4,
            int red,
            int green,
            int blue,
            int alpha,
            float normalX,
            float normalY,
            float normalZ) {
        addVertex(consumer, pose, packedLight, x1, y1, z1, u1, v1, red, green, blue, alpha, normalX, normalY, normalZ);
        addVertex(consumer, pose, packedLight, x2, y2, z2, u2, v2, red, green, blue, alpha, normalX, normalY, normalZ);
        addVertex(consumer, pose, packedLight, x3, y3, z3, u3, v3, red, green, blue, alpha, normalX, normalY, normalZ);
        addVertex(consumer, pose, packedLight, x4, y4, z4, u4, v4, red, green, blue, alpha, normalX, normalY, normalZ);
    }

    private static void addVertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            int packedLight,
            float x,
            float y,
            float z,
            float u,
            float v,
            int red,
            int green,
            int blue,
            int alpha,
            float normalX,
            float normalY,
            float normalZ) {
        consumer.addVertex(pose, x, y, z)
                .setColor(red, green, blue, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, normalX, normalY, normalZ);
    }
}
