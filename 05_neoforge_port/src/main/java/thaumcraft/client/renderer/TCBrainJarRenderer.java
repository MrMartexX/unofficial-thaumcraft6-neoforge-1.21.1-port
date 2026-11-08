package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thaumcraft.Thaumcraft;
import thaumcraft.common.tiles.devices.TCBrainJarBlockEntity;

final class TCBrainJarRenderer implements BlockEntityRenderer<TCBrainJarBlockEntity> {
    private static final ResourceLocation TEX_BRAIN =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/models/brain2.png");
    private static final ResourceLocation TEX_BRINE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/models/jarbrine.png");

    private final ModelPart brain;
    private final ModelPart brine;

    TCBrainJarRenderer(BlockEntityRendererProvider.Context context) {
        MeshDefinition brainMesh = new MeshDefinition();
        PartDefinition root = brainMesh.getRoot();
        root.addOrReplaceChild(
                "shape1",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0f, 0.0f, 0.0f, 12, 10, 16),
                PartPose.offset(-6.0f, 8.0f, -8.0f)
        );
        root.addOrReplaceChild(
                "shape2",
                CubeListBuilder.create().texOffs(64, 0).mirror().addBox(0.0f, 0.0f, 0.0f, 8, 3, 7),
                PartPose.offset(-4.0f, 18.0f, 0.0f)
        );
        root.addOrReplaceChild(
                "shape3",
                CubeListBuilder.create().texOffs(0, 32).mirror().addBox(0.0f, 0.0f, 0.0f, 2, 6, 2),
                PartPose.offsetAndRotation(-1.0f, 18.0f, -2.0f, 0.4089647f, 0.0f, 0.0f)
        );
        brain = LayerDefinition.create(brainMesh, 128, 64).bakeRoot();

        MeshDefinition brineMesh = new MeshDefinition();
        brineMesh.getRoot().addOrReplaceChild(
                "brine",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-4.0f, -11.0f, -4.0f, 8, 10, 8),
                PartPose.ZERO
        );
        brine = LayerDefinition.create(brineMesh, 64, 32).bakeRoot().getChild("brine");
    }

    @Override
    public void render(
            TCBrainJarBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.01f, 0.5f);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));

        int ticks = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.tickCount : 0;
        float bob = Mth.sin((ticks + partialTick) / 14.0f) * 0.03f + 0.03f;

        poseStack.pushPose();
        poseStack.translate(0.0f, -0.8f + bob, 0.0f);

        float diff;
        for (diff = blockEntity.rota - blockEntity.rotb; diff >= (float) Math.PI; diff -= (float) (Math.PI * 2)) {}
        while (diff < -(float) Math.PI) {
            diff += (float) (Math.PI * 2);
        }
        float rotation = blockEntity.rotb + diff * partialTick;
        poseStack.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(rotation)));
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0f));
        poseStack.scale(0.4f, 0.4f, 0.4f);
        brain.render(poseStack, bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEX_BRAIN)), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        brine.render(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(TEX_BRINE)), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
