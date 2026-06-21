package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.Blocks;
import thaumcraft.common.tiles.crafting.TCCrucibleBlockEntity;

/** Modern BER equivalent of legacy {@code TileCrucibleRenderer}. */
final class TCCrucibleRenderer implements BlockEntityRenderer<TCCrucibleBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    TCCrucibleRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(
            TCCrucibleBlockEntity crucible,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        if (crucible.getWaterAmount() <= 0) {
            return;
        }
        float recolor = crucible.getAspects().visSize() / (float) TCCrucibleBlockEntity.LEGACY_ASPECT_CAP;
        if (recolor > 0.0F) {
            recolor = 0.5F + recolor / 2.0F;
        }
        recolor = Math.min(1.0F, recolor);
        float red = 1.0F - recolor / 3.0F;
        float green = 1.0F - recolor;
        float blue = 1.0F - recolor / 2.0F;
        float height = crucible.getFluidHeight();
        TextureAtlasSprite water = context.getBlockRenderDispatcher()
                .getBlockModel(Blocks.WATER.defaultBlockState())
                .getParticleIcon();
        VertexConsumer consumer = bufferSource.getBuffer(Sheets.translucentCullBlockSheet());
        PoseStack.Pose pose = poseStack.last();
        addVertex(consumer, pose, 0.0F, height, 1.0F, water.getU0(), water.getV1(), red, green, blue, packedLight);
        addVertex(consumer, pose, 1.0F, height, 1.0F, water.getU1(), water.getV1(), red, green, blue, packedLight);
        addVertex(consumer, pose, 1.0F, height, 0.0F, water.getU1(), water.getV0(), red, green, blue, packedLight);
        addVertex(consumer, pose, 0.0F, height, 0.0F, water.getU0(), water.getV0(), red, green, blue, packedLight);
    }

    private static void addVertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float u,
            float v,
            float red,
            float green,
            float blue,
            int packedLight
    ) {
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(red, green, blue, 1.0F)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
