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
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.essentia.TCBellowsBlock;
import thaumcraft.common.tiles.essentia.TCBellowsBlockEntity;

/** Modern BER reconstruction of legacy {@code TileBellowsRenderer}. */
final class TCBellowsRenderer implements BlockEntityRenderer<TCBellowsBlockEntity> {
    private static final ResourceLocation BELLOWS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/blocks/bellows.png");
    private static final ResourceLocation BORE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/models/bore.png");

    private final ModelPart bottomPlank;
    private final ModelPart topPlank;
    private final ModelPart middlePlank;
    private final ModelPart bag;
    private final ModelPart nozzle;
    private final ModelPart boreNozzle1;
    private final ModelPart boreNozzle2;

    TCBellowsRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart bellowsRoot = createBellowsModel().bakeRoot();
        bottomPlank = bellowsRoot.getChild("bottom_plank");
        topPlank = bellowsRoot.getChild("top_plank");
        middlePlank = bellowsRoot.getChild("middle_plank");
        bag = bellowsRoot.getChild("bag");
        nozzle = bellowsRoot.getChild("nozzle");

        ModelPart boreRoot = createBoreNozzleModel().bakeRoot();
        boreNozzle1 = boreRoot.getChild("nozzle_1");
        boreNozzle2 = boreRoot.getChild("nozzle_2");
    }

    @Override
    public void render(
            TCBellowsBlockEntity bellows,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        Direction facing = bellows.getBlockState().hasProperty(TCBellowsBlock.FACING)
                ? bellows.getBlockState().getValue(TCBellowsBlock.FACING)
                : Direction.NORTH;

        if (bellows.hasTubeBufferExtension()) {
            renderTubeBufferExtension(facing, poseStack, bufferSource, packedLight);
        }

        float scale = bellows.inflation(partialTick);
        float tscale = 0.125F + scale * 0.875F;
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(BELLOWS_TEXTURE));

        poseStack.pushPose();
        translateFromOrientation(poseStack, facing);
        poseStack.translate(0.0F, 1.0F, 0.0F);

        poseStack.pushPose();
        poseStack.scale(0.5F, (scale + 0.1F) / 2.0F, 0.5F);
        bag.setPos(0.0F, 0.5F, 0.0F);
        bag.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.translate(0.0F, -1.0F, 0.0F);
        poseStack.pushPose();
        poseStack.translate(0.0F, -tscale / 2.0F + 0.5F, 0.0F);
        topPlank.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.0F, tscale / 2.0F - 0.5F, 0.0F);
        bottomPlank.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        middlePlank.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        nozzle.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private void renderTubeBufferExtension(
            Direction facing,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(BORE_TEXTURE));
        poseStack.pushPose();
        poseStack.translate(
                0.5F + facing.getStepX(),
                facing.getStepY(),
                0.5F + facing.getStepZ()
        );
        orientBoreNozzle(poseStack, facing.getOpposite());
        boreNozzle1.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        boreNozzle2.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private static void translateFromOrientation(PoseStack poseStack, Direction facing) {
        poseStack.translate(0.5F, -0.5F, 0.5F);
        switch (facing) {
            case DOWN -> {
                poseStack.translate(0.0F, 1.0F, -1.0F);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            }
            case UP -> {
                poseStack.translate(0.0F, 1.0F, 1.0F);
                poseStack.mulPose(Axis.XP.rotationDegrees(270.0F));
            }
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            case SOUTH -> {
            }
        }
    }

    private static void orientBoreNozzle(PoseStack poseStack, Direction side) {
        switch (side) {
            case DOWN -> {
                poseStack.translate(-0.5F, 0.5F, 0.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            }
            case UP -> {
                poseStack.translate(0.5F, 0.5F, 0.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            case EAST -> {
            }
        }
    }

    private static LayerDefinition createBellowsModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "bottom_plank",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                        .addBox(-6.0F, 0.0F, -6.0F, 12.0F, 2.0F, 12.0F),
                PartPose.offset(0.0F, 22.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "middle_plank",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                        .addBox(-6.0F, -1.0F, -6.0F, 12.0F, 2.0F, 12.0F),
                PartPose.offset(0.0F, 16.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "top_plank",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                        .addBox(-6.0F, 0.0F, -6.0F, 12.0F, 2.0F, 12.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "bag",
                CubeListBuilder.create().texOffs(48, 0).mirror()
                        .addBox(-10.0F, -12.033333F, -10.0F, 20.0F, 24.0F, 20.0F),
                PartPose.offset(0.0F, 16.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "nozzle",
                CubeListBuilder.create().texOffs(0, 36).mirror()
                        .addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 2.0F),
                PartPose.offset(0.0F, 16.0F, 6.0F)
        );
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static LayerDefinition createBoreNozzleModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "nozzle_1",
                CubeListBuilder.create().texOffs(106, 42).mirror()
                        .addBox(2.5F, -2.0F, -2.0F, 5.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "nozzle_2",
                CubeListBuilder.create().texOffs(106, 51).mirror()
                        .addBox(7.0F, -2.5F, -2.5F, 1.0F, 5.0F, 5.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F)
        );
        return LayerDefinition.create(mesh, 128, 64);
    }
}
