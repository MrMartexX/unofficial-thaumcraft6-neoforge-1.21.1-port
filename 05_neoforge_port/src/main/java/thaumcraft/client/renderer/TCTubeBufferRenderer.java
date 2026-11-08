package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

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
import thaumcraft.common.essentia.transport.blockentity.TCLegacyTubeBlockEntity;

final class TCTubeBufferRenderer implements BlockEntityRenderer<TCLegacyTubeBlockEntity> {
    private static final ResourceLocation VALVE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/models/valve.png");
    private static final int RED = 0xFFFF4C4C;
    private static final int BLUE = 0xFF4C4CFF;

    private final ModelPart rod;

    TCTubeBufferRenderer(BlockEntityRendererProvider.Context context) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "rod",
                CubeListBuilder.create().texOffs(0, 10).mirror()
                        .addBox(-1.0F, 2.0F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.ZERO
        );
        rod = LayerDefinition.create(mesh, 64, 32).bakeRoot().getChild("rod");
    }

    @Override
    public void render(
            TCLegacyTubeBlockEntity buffer,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay) {
        if (buffer.getLevel() == null) {
            return;
        }

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(VALVE_TEXTURE));
        for (Direction dir : Direction.values()) {
            byte choke = buffer.chokedSide(dir);
            if (choke == 0 || !buffer.isSideOpen(dir)) {
                continue;
            }

            int color = (choke == 2) ? RED : BLUE;
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(dir.getOpposite().getRotation());
            poseStack.scale(2.0f, 1.0f, 2.0f);
            poseStack.translate(0.0, -0.5, 0.0);
            rod.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                    ((int) (255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255));
            poseStack.popPose();
        }
    }
}
