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
        inkwell.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
    }

    private static void renderQuill(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.translate(-0.5D, 0.1D, 0.125D);
        poseStack.mulPose(Axis.YP.rotationDegrees(60.0F));
        poseStack.scale(0.5F, 0.5F, 0.5F);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(QUILL_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        addVertex(consumer, pose, packedLight, -0.5F, -0.5F, 0.0F, 0.0F, 1.0F, 255, 255, 255, 255, 0.0F, 1.0F, 0.0F);
        addVertex(consumer, pose, packedLight, 0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 255, 255, 255, 255, 0.0F, 1.0F, 0.0F);
        addVertex(consumer, pose, packedLight, 0.5F, 0.5F, 0.0F, 1.0F, 0.0F, 255, 255, 255, 255, 0.0F, 1.0F, 0.0F);
        addVertex(consumer, pose, packedLight, -0.5F, 0.5F, 0.0F, 0.0F, 0.0F, 255, 255, 255, 255, 0.0F, 1.0F, 0.0F);
        poseStack.popPose();
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
