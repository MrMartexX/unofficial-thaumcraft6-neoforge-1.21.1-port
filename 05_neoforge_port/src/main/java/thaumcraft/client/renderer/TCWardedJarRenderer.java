package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;

/**
 * 1:1 port of legacy TileJarRenderer + ModelJar for Warded Jars and Void Jars:
 * renders the colored essentia cuboid, Jar Brace lid, top tube lid extension,
 * and crooked paper label with dark-ink aspect symbol.
 */
final class TCWardedJarRenderer implements BlockEntityRenderer<TCWardedJarBlockEntity> {
    private static final ResourceLocation ESSENTIA =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "block/animatedglow");
    private static final ResourceLocation TEX_LABEL =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/models/label.png");
    private static final ResourceLocation TEX_BRINE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/models/jarbrine.png");

    private final ModelPart lidBrace;
    private final ModelPart lidExtension;

    TCWardedJarRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = createJarAccessoriesModel();
        this.lidBrace = root.getChild("lid_brace");
        this.lidExtension = root.getChild("lid_extension");
    }

    private static ModelPart createJarAccessoriesModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "lid_brace",
                CubeListBuilder.create()
                        .texOffs(32, 24)
                        .addBox(-3.0F, 0.0F, -3.0F, 6.0F, 2.0F, 6.0F),
                PartPose.offset(0.0F, -14.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "lid_extension",
                CubeListBuilder.create()
                        .texOffs(0, 23)
                        .addBox(-2.0F, -16.0F, -2.0F, 4.0F, 2.0F, 4.0F),
                PartPose.ZERO
        );
        return LayerDefinition.create(mesh, 64, 32).bakeRoot();
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
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.01F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

        if (jar.isBlocked()) {
            poseStack.pushPose();
            poseStack.scale(1.001F, 1.001F, 1.001F);
            VertexConsumer brineConsumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TEX_BRINE));
            lidBrace.render(poseStack, brineConsumer, packedLight, packedOverlay);
            poseStack.popPose();
        }

        if (jar.getLevel() != null
                && jar.getLevel().getBlockEntity(jar.getBlockPos().above()) instanceof TCEssentiaTransport transport
                && transport.isConnectable(Direction.DOWN)) {
            poseStack.pushPose();
            poseStack.scale(0.9F, 1.0F, 0.9F);
            VertexConsumer brineConsumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TEX_BRINE));
            lidExtension.render(poseStack, brineConsumer, packedLight, packedOverlay);
            poseStack.popPose();
        }

        Aspect filter = jar.aspectFilter();
        if (filter != null) {
            Direction facing = jar.labelFacing();
            poseStack.pushPose();
            switch (facing) {
                case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
                case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                default -> {
                }
            }

            float rot = (filter.getTag().hashCode() + jar.getBlockPos().getX() + facing.get3DDataValue()) % 4 - 2;

            poseStack.pushPose();
            poseStack.translate(0.0F, -0.4F, 0.315F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(rot));
            renderCenteredQuad(
                    buffers.getBuffer(RenderType.entityTranslucent(TEX_LABEL)),
                    poseStack.last(),
                    0.5F,
                    1.0F,
                    1.0F,
                    1.0F,
                    1.0F,
                    packedLight
            );
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0.0F, -0.4F, 0.316F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(rot));
            poseStack.scale(0.021F, 0.021F, 0.021F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            renderAspectInkQuad(
                    buffers.getBuffer(RenderType.entityTranslucent(filter.getImage())),
                    poseStack.last(),
                    -8.0F,
                    -8.0F,
                    packedLight
            );
            poseStack.popPose();

            poseStack.popPose();
        }

        poseStack.popPose();

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

    static void renderCenteredQuad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float scale,
            float red,
            float green,
            float blue,
            float alpha,
            int packedLight
    ) {
        float half = 0.5F * scale;
        // Front and back faces to match legacy GL_CULL_FACE disabled
        vertex(consumer, pose, -half, half, 0.0F, 1.0F, 1.0F, red, green, blue, alpha, packedLight, 0, 0, 1);
        vertex(consumer, pose, half, half, 0.0F, 1.0F, 0.0F, red, green, blue, alpha, packedLight, 0, 0, 1);
        vertex(consumer, pose, half, -half, 0.0F, 0.0F, 0.0F, red, green, blue, alpha, packedLight, 0, 0, 1);
        vertex(consumer, pose, -half, -half, 0.0F, 0.0F, 1.0F, red, green, blue, alpha, packedLight, 0, 0, 1);

        vertex(consumer, pose, -half, -half, 0.0F, 0.0F, 1.0F, red, green, blue, alpha, packedLight, 0, 0, -1);
        vertex(consumer, pose, half, -half, 0.0F, 0.0F, 0.0F, red, green, blue, alpha, packedLight, 0, 0, -1);
        vertex(consumer, pose, half, half, 0.0F, 1.0F, 0.0F, red, green, blue, alpha, packedLight, 0, 0, -1);
        vertex(consumer, pose, -half, half, 0.0F, 1.0F, 1.0F, red, green, blue, alpha, packedLight, 0, 0, -1);
    }

    static void renderAspectInkQuad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,
            float y,
            int packedLight
    ) {
        float x2 = x + 16.0F;
        float y2 = y + 16.0F;
        // Legacy UtilsFX.drawTag(x, y, aspect) uses bw=true -> (0.1F, 0.1F, 0.1F, 0.8F)
        vertex(consumer, pose, x, y2, 0.0F, 0.0F, 1.0F, 0.1F, 0.1F, 0.1F, 0.8F, packedLight, 0, 0, 1);
        vertex(consumer, pose, x2, y2, 0.0F, 1.0F, 1.0F, 0.1F, 0.1F, 0.1F, 0.8F, packedLight, 0, 0, 1);
        vertex(consumer, pose, x2, y, 0.0F, 1.0F, 0.0F, 0.1F, 0.1F, 0.1F, 0.8F, packedLight, 0, 0, 1);
        vertex(consumer, pose, x, y, 0.0F, 0.0F, 0.0F, 0.1F, 0.1F, 0.1F, 0.8F, packedLight, 0, 0, 1);

        vertex(consumer, pose, x, y, 0.0F, 0.0F, 0.0F, 0.1F, 0.1F, 0.1F, 0.8F, packedLight, 0, 0, -1);
        vertex(consumer, pose, x2, y, 0.0F, 1.0F, 0.0F, 0.1F, 0.1F, 0.1F, 0.8F, packedLight, 0, 0, -1);
        vertex(consumer, pose, x2, y2, 0.0F, 1.0F, 1.0F, 0.1F, 0.1F, 0.1F, 0.8F, packedLight, 0, 0, -1);
        vertex(consumer, pose, x, y2, 0.0F, 0.0F, 1.0F, 0.1F, 0.1F, 0.1F, 0.8F, packedLight, 0, 0, -1);
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
        vertex(consumer, pose, x1, y1, z1, sprite.getU0(), sprite.getV1(), red, green, blue, 1.0F, packedLight, normalX, normalY, normalZ);
        vertex(consumer, pose, x2, y2, z2, sprite.getU1(), sprite.getV1(), red, green, blue, 1.0F, packedLight, normalX, normalY, normalZ);
        vertex(consumer, pose, x3, y3, z3, sprite.getU1(), sprite.getV0(), red, green, blue, 1.0F, packedLight, normalX, normalY, normalZ);
        vertex(consumer, pose, x4, y4, z4, sprite.getU0(), sprite.getV0(), red, green, blue, 1.0F, packedLight, normalX, normalY, normalZ);
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x, float y, float z,
            float u, float v,
            float red, float green, float blue, float alpha,
            int packedLight,
            float normalX, float normalY, float normalZ
    ) {
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(red, green, blue, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, normalX, normalY, normalZ);
    }
}
