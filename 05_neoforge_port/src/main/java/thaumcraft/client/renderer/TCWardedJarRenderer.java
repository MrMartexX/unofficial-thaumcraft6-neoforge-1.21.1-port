package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;

/** Dynamic equivalent of legacy TileJarRenderer's colored essentia cuboid. */
final class TCWardedJarRenderer implements BlockEntityRenderer<TCWardedJarBlockEntity> {
    private static final ResourceLocation ESSENTIA =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "block/animatedglow");

    TCWardedJarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            TCWardedJarBlockEntity jar,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        Aspect aspect = jar.storedAspect();
        if (aspect == null || jar.storedAmount() <= 0) {
            return;
        }

        float top = 0.0625F + jar.storedAmount() / (float) TCWardedJarBlockEntity.CAPACITY * 0.625F;
        int color = aspect.getColor();
        float red = ((color >> 16) & 255) / 255.0F;
        float green = ((color >> 8) & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(ESSENTIA);
        VertexConsumer consumer = buffers.getBuffer(Sheets.translucentCullBlockSheet());
        PoseStack.Pose pose = poseStack.last();
        float min = 0.25F;
        float max = 0.75F;
        float bottom = 0.0625F;

        quad(consumer, pose, min, top, min, max, top, min, max, top, max, min, top, max,
                sprite, red, green, blue, packedLight, 0, 1, 0);
        quad(consumer, pose, min, bottom, max, max, bottom, max, max, bottom, min, min, bottom, min,
                sprite, red, green, blue, packedLight, 0, -1, 0);
        quad(consumer, pose, min, bottom, min, max, bottom, min, max, top, min, min, top, min,
                sprite, red, green, blue, packedLight, 0, 0, -1);
        quad(consumer, pose, max, bottom, max, min, bottom, max, min, top, max, max, top, max,
                sprite, red, green, blue, packedLight, 0, 0, 1);
        quad(consumer, pose, min, bottom, max, min, bottom, min, min, top, min, min, top, max,
                sprite, red, green, blue, packedLight, -1, 0, 0);
        quad(consumer, pose, max, bottom, min, max, bottom, max, max, top, max, max, top, min,
                sprite, red, green, blue, packedLight, 1, 0, 0);
    }

    private static void quad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            TextureAtlasSprite sprite,
            float red, float green, float blue,
            int packedLight,
            float normalX, float normalY, float normalZ
    ) {
        vertex(consumer, pose, x1, y1, z1, sprite.getU0(), sprite.getV1(), red, green, blue, packedLight, normalX, normalY, normalZ);
        vertex(consumer, pose, x2, y2, z2, sprite.getU1(), sprite.getV1(), red, green, blue, packedLight, normalX, normalY, normalZ);
        vertex(consumer, pose, x3, y3, z3, sprite.getU1(), sprite.getV0(), red, green, blue, packedLight, normalX, normalY, normalZ);
        vertex(consumer, pose, x4, y4, z4, sprite.getU0(), sprite.getV0(), red, green, blue, packedLight, normalX, normalY, normalZ);
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x, float y, float z,
            float u, float v,
            float red, float green, float blue,
            int packedLight,
            float normalX, float normalY, float normalZ
    ) {
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(red, green, blue, 1.0F)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, normalX, normalY, normalZ);
    }
}
