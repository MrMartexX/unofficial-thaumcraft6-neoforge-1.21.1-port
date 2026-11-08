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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.tiles.essentia.TCAlembicBlockEntity;

/**
 * 1:1 port of legacy TileAlembicRenderer + ModelBoreBase.renderNozzle:
 * renders the paper label with dark-ink aspect symbol and horizontal output nozzles.
 */
final class TCAlembicRenderer implements BlockEntityRenderer<TCAlembicBlockEntity> {
    private static final ResourceLocation TEX_LABEL =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/models/label.png");
    private static final ResourceLocation TEX_BORE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/models/bore.png");

    private final ModelPart nozzle1;
    private final ModelPart nozzle2;

    TCAlembicRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = createNozzleModel();
        this.nozzle1 = root.getChild("nozzle1");
        this.nozzle2 = root.getChild("nozzle2");
    }

    private static ModelPart createNozzleModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "nozzle1",
                CubeListBuilder.create()
                        .texOffs(106, 42)
                        .addBox(2.5F, -2.0F, -2.0F, 5.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "nozzle2",
                CubeListBuilder.create()
                        .texOffs(106, 51)
                        .addBox(7.0F, -2.5F, -2.5F, 1.0F, 5.0F, 5.0F),
                PartPose.offset(0.0F, 8.0F, 0.0F)
        );
        return LayerDefinition.create(mesh, 128, 64).bakeRoot();
    }

    @Override
    public void render(
            TCAlembicBlockEntity alembic,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        Aspect filter = alembic.aspectFilter();
        if (filter != null) {
            Direction facing = alembic.labelFacing();
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.0F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            switch (facing) {
                case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
                default -> {
                }
            }

            poseStack.pushPose();
            poseStack.translate(0.0F, 0.5F, -0.376F);
            TCWardedJarRenderer.renderCenteredQuad(
                    buffers.getBuffer(RenderType.entityTranslucent(TEX_LABEL)),
                    poseStack.last(),
                    0.44F,
                    1.0F,
                    1.0F,
                    1.0F,
                    1.0F,
                    packedLight
            );
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0.0F, 0.5F, -0.377F);
            poseStack.scale(0.02F, -0.02F, 0.02F);
            TCWardedJarRenderer.renderAspectInkQuad(
                    buffers.getBuffer(RenderType.entityTranslucent(filter.getImage())),
                    poseStack.last(),
                    -8.0F,
                    -8.0F,
                    packedLight
            );
            poseStack.popPose();

            poseStack.popPose();
        }

        if (alembic.getLevel() != null) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                if (alembic.canOutputTo(dir)
                        && alembic.getLevel().getBlockEntity(alembic.getBlockPos().relative(dir)) instanceof TCEssentiaTransport transport
                        && transport.isConnectable(dir.getOpposite())
                        && transport.canInputFrom(dir.getOpposite())) {
                    poseStack.pushPose();
                    poseStack.translate(0.5F, 0.0F, 0.5F);
                    switch (dir) {
                        case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                        case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
                        case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                        default -> {
                        }
                    }
                    VertexConsumer boreConsumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TEX_BORE));
                    nozzle1.render(poseStack, boreConsumer, packedLight, packedOverlay);
                    nozzle2.render(poseStack, boreConsumer, packedLight, packedOverlay);
                    poseStack.popPose();
                }
            }
        }
    }
}
