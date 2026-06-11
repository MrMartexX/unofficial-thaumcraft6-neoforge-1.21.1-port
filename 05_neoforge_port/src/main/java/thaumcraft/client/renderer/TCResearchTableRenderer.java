package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
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
    private static final int QUILL_WIDTH = 16;
    private static final int QUILL_HEIGHT = 16;
    private static final float QUILL_THICKNESS = 0.0625F;
    private static final double INKWELL_SURFACE_LIFT = 0.002D;

    /*
     * These are table-local compensation offsets over the exact legacy transform.
     * They are needed because the modern quill uses a standalone texture and a centered
     * 180-degree texture-orientation compensation, while legacy pulled the sprite from the
     * block atlas with renderTextureIn3D. The target is visual parity: the lower quill tip
     * exits from the top-center of the inkwell instead of being left/low/inside the table.
     */
    private static final double QUILL_COMPENSATE_X = 0.20D;
    private static final double QUILL_COMPENSATE_Y = -0.17D;
    private static final double QUILL_COMPENSATE_Z = 0.00D;

    private final ModelPart inkwell;
    private final ModelPart scrollTube;
    private final ModelPart scrollRibbon;

    TCResearchTableRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = createLegacyModel().bakeRoot();
        inkwell = root.getChild("inkwell");
        scrollTube = root.getChild("scroll_tube");
        scrollRibbon = root.getChild("scroll_ribbon");
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
        poseStack.translate(0.5D, 1.0D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
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

    private void renderScroll(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TABLE_DETAIL_TEXTURE));
        scrollTube.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.pushPose();
        poseStack.scale(1.2F, 1.2F, 1.2F);
        scrollRibbon.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 0xFF000000 | Aspect.ALCHEMY.getColor());
        poseStack.popPose();
    }

    private void renderInkwell(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(TABLE_DETAIL_TEXTURE));
        poseStack.pushPose();
        poseStack.translate(0.0D, INKWELL_SURFACE_LIFT, 0.0D);
        inkwell.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private static void renderQuill(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.translate(-0.5D, 0.1D, 0.125D);
poseStack.mulPose(Axis.YP.rotationDegrees(60.0F));
        poseStack.scale(0.5F, 0.5F, 0.5F);
VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(QUILL_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        renderTextureIn3D(consumer, pose, packedLight, 1.0F, 0.0F, 0.0F, 1.0F, QUILL_WIDTH, QUILL_HEIGHT, QUILL_THICKNESS);
        poseStack.popPose();
    }

    /**
     * Port of legacy UtilsFX.renderTextureIn3D(maxu, maxv, minu, minv, width, height, thickness).
     * Coordinates, UV direction and segmented edge construction intentionally follow the legacy
     * method instead of using centered GUI quads.
     */
    private static void renderTextureIn3D(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            int packedLight,
            float maxU,
            float maxV,
            float minU,
            float minV,
            int width,
            int height,
            float thickness) {
        addVertex(consumer, pose, packedLight, 0.0F, 0.0F, 0.0F, maxU, minV, 255, 255, 255, 255, 0.0F, 0.0F, 1.0F);
        addVertex(consumer, pose, packedLight, 1.0F, 0.0F, 0.0F, minU, minV, 255, 255, 255, 255, 0.0F, 0.0F, 1.0F);
        addVertex(consumer, pose, packedLight, 1.0F, 1.0F, 0.0F, minU, maxV, 255, 255, 255, 255, 0.0F, 0.0F, 1.0F);
        addVertex(consumer, pose, packedLight, 0.0F, 1.0F, 0.0F, maxU, maxV, 255, 255, 255, 255, 0.0F, 0.0F, 1.0F);

        addVertex(consumer, pose, packedLight, 0.0F, 1.0F, -thickness, maxU, maxV, 255, 255, 255, 255, 0.0F, 0.0F, -1.0F);
        addVertex(consumer, pose, packedLight, 1.0F, 1.0F, -thickness, minU, maxV, 255, 255, 255, 255, 0.0F, 0.0F, -1.0F);
        addVertex(consumer, pose, packedLight, 1.0F, 0.0F, -thickness, minU, minV, 255, 255, 255, 255, 0.0F, 0.0F, -1.0F);
        addVertex(consumer, pose, packedLight, 0.0F, 0.0F, -thickness, maxU, minV, 255, 255, 255, 255, 0.0F, 0.0F, -1.0F);

        float uHalfStep = 0.5F * (maxU - minU) / width;
        float vHalfStep = 0.5F * (minV - maxV) / height;

        for (int k = 0; k < width; k++) {
            float f7 = (float) k / (float) width;
            float f8 = maxU + (minU - maxU) * f7 - uHalfStep;
            float f9 = f7 + 1.0F / (float) width;

            addVertex(consumer, pose, packedLight, f7, 0.0F, -thickness, f8, minV, 255, 255, 255, 255, -1.0F, 0.0F, 0.0F);
            addVertex(consumer, pose, packedLight, f7, 0.0F, 0.0F, f8, minV, 255, 255, 255, 255, -1.0F, 0.0F, 0.0F);
            addVertex(consumer, pose, packedLight, f7, 1.0F, 0.0F, f8, maxV, 255, 255, 255, 255, -1.0F, 0.0F, 0.0F);
            addVertex(consumer, pose, packedLight, f7, 1.0F, -thickness, f8, maxV, 255, 255, 255, 255, -1.0F, 0.0F, 0.0F);

            addVertex(consumer, pose, packedLight, f9, 1.0F, -thickness, f8, maxV, 255, 255, 255, 255, 1.0F, 0.0F, 0.0F);
            addVertex(consumer, pose, packedLight, f9, 1.0F, 0.0F, f8, maxV, 255, 255, 255, 255, 1.0F, 0.0F, 0.0F);
            addVertex(consumer, pose, packedLight, f9, 0.0F, 0.0F, f8, minV, 255, 255, 255, 255, 1.0F, 0.0F, 0.0F);
            addVertex(consumer, pose, packedLight, f9, 0.0F, -thickness, f8, minV, 255, 255, 255, 255, 1.0F, 0.0F, 0.0F);
        }

        for (int k = 0; k < height; k++) {
            float f7 = (float) k / (float) height;
            float f8 = minV + (maxV - minV) * f7 - vHalfStep;
            float f9 = f7 + 1.0F / (float) height;

            addVertex(consumer, pose, packedLight, 0.0F, f9, 0.0F, maxU, f8, 255, 255, 255, 255, 0.0F, 1.0F, 0.0F);
            addVertex(consumer, pose, packedLight, 1.0F, f9, 0.0F, minU, f8, 255, 255, 255, 255, 0.0F, 1.0F, 0.0F);
            addVertex(consumer, pose, packedLight, 1.0F, f9, -thickness, minU, f8, 255, 255, 255, 255, 0.0F, 1.0F, 0.0F);
            addVertex(consumer, pose, packedLight, 0.0F, f9, -thickness, maxU, f8, 255, 255, 255, 255, 0.0F, 1.0F, 0.0F);

            addVertex(consumer, pose, packedLight, 1.0F, f7, 0.0F, minU, f8, 255, 255, 255, 255, 0.0F, -1.0F, 0.0F);
            addVertex(consumer, pose, packedLight, 0.0F, f7, 0.0F, maxU, f8, 255, 255, 255, 255, 0.0F, -1.0F, 0.0F);
            addVertex(consumer, pose, packedLight, 0.0F, f7, -thickness, maxU, f8, 255, 255, 255, 255, 0.0F, -1.0F, 0.0F);
            addVertex(consumer, pose, packedLight, 1.0F, f7, -thickness, minU, f8, 255, 255, 255, 255, 0.0F, -1.0F, 0.0F);
        }
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

    private static LayerDefinition createLegacyModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "inkwell",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .mirror()
                        .addBox(0.0F, 0.0F, 0.0F, 3.0F, 2.0F, 3.0F),
                PartPose.offset(-6.0F, -2.0F, 3.0F));
        root.addOrReplaceChild(
                "scroll_tube",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror()
                        .addBox(-8.0F, -0.5F, 0.0F, 8.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(-2.0F, -2.0F, 2.0F, 0.0F, 10.0F, 0.0F));
        root.addOrReplaceChild(
                "scroll_ribbon",
                CubeListBuilder.create()
                        .texOffs(0, 4)
                        .mirror()
                        .addBox(-4.25F, -0.275F, 0.0F, 1.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(-2.0F, -2.0F, 2.0F, 0.0F, 10.0F, 0.0F));
        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }
}
