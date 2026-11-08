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
import net.minecraft.util.FastColor;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import thaumcraft.Thaumcraft;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.essentia.transport.blockentity.TCLegacyTubeBlockEntity;

/**
 * 1:1 port of legacy TileTubeOnewayRenderer:
 * renders the blue directional plunger rod when connected on facing.getOpposite().
 */
final class TCTubeOnewayRenderer implements BlockEntityRenderer<TCLegacyTubeBlockEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/models/valve.png");
    private static final int PLUNGER_COLOR = FastColor.ARGB32.color(
            255,
            Math.round(0.45F * 255.0F),
            Math.round(0.5F * 255.0F),
            255
    );

    private final ModelPart rod;

    TCTubeOnewayRenderer(BlockEntityRendererProvider.Context context) {
        this.rod = createRodModel().getChild("rod");
    }

    private static ModelPart createRodModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "rod",
                CubeListBuilder.create()
                        .texOffs(0, 10)
                        .addBox(-1.0F, 2.0F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.ZERO
        );
        return LayerDefinition.create(mesh, 64, 32).bakeRoot();
    }

    @Override
    public void render(
            TCLegacyTubeBlockEntity tube,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        Direction facing = tube.facing();
        if (tube.getLevel() != null) {
            if (!(tube.getLevel().getBlockEntity(tube.getBlockPos().relative(facing.getOpposite())) instanceof TCEssentiaTransport transport)
                    || !transport.isConnectable(facing)) {
                return;
            }
        }

        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        orientToLegacyFacing(poseStack, facing);
        poseStack.scale(2.0F, 2.0F, 2.0F);
        poseStack.translate(0.0F, -0.32F, 0.0F);
        rod.render(poseStack, consumer, packedLight, packedOverlay, PLUNGER_COLOR);
        poseStack.popPose();
    }

    private static void orientToLegacyFacing(PoseStack poseStack, Direction facing) {
        if (facing.getStepY() == 0) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        } else {
            poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F * facing.getStepY()));
        }
        poseStack.mulPose(new Quaternionf().fromAxisAngleDeg(
                new Vector3f(facing.getStepX(), facing.getStepY(), facing.getStepZ()),
                90.0F
        ));
    }
}
