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
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import thaumcraft.Thaumcraft;
import thaumcraft.client.fx.TCInfusionClientEffects;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;

/** Modern BER reconstruction of legacy {@code TileInfusionMatrixRenderer}. */
final class TCInfusionMatrixRenderer implements BlockEntityRenderer<TCInfusionMatrixBlockEntity> {
    private static final ResourceLocation NORMAL = texture("infuser_normal");
    private static final ResourceLocation ANCIENT = texture("infuser_ancient");
    private static final ResourceLocation ELDRITCH = texture("infuser_eldritch");
    private final ModelPart normalCube;
    private final ModelPart overlayCube;

    TCInfusionMatrixRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = createLegacyModel().bakeRoot();
        normalCube = root.getChild("normal");
        overlayCube = root.getChild("overlay");
    }

    @Override
    public void render(
            TCInfusionMatrixBlockEntity matrix,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        float startup = matrix.clientStartUp();
        float ticks = (matrix.getLevel() == null ? 0.0F : matrix.getLevel().getGameTime()) + partialTick;
        float instability = Math.min(
                6.0F,
                1.0F + (matrix.stability() < 0.0F ? -matrix.stability() * 0.66F : 1.0F)
                        * (Math.min(matrix.clientCraftCount(), 50) / 50.0F)
        );
        ResourceLocation texture = textureForPillars(matrix);

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees((ticks % 360.0F) * startup));
        poseStack.mulPose(Axis.XP.rotationDegrees(35.0F * startup));
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F * startup));

        VertexConsumer normal = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
        renderCubes(matrix, poseStack, normalCube, normal, packedLight, ticks, startup, instability, 0xFFFFFFFF);

        if (matrix.isActive()) {
            VertexConsumer glow = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(texture));
            renderCubes(matrix, poseStack, overlayCube, glow, LightTexture.FULL_BRIGHT, ticks, startup, instability, 0);
        }
        poseStack.popPose();

        if (matrix.isCrafting()) {
            TCInfusionClientEffects.noteCraftingMatrix(matrix.getBlockPos(), matrix.clientCraftCount());
        }
    }

    private static void renderCubes(
            TCInfusionMatrixBlockEntity matrix,
            PoseStack poseStack,
            ModelPart model,
            VertexConsumer consumer,
            int packedLight,
            float ticks,
            float startup,
            float instability,
            int fixedColor
    ) {
        for (int a = 0; a < 2; a++) {
            for (int b = 0; b < 2; b++) {
                for (int c = 0; c < 2; c++) {
                    float offsetX = matrix.isActive() ? Mth.sin((ticks + a * 10.0F) / 15.0F) * 0.01F * startup * instability : 0.0F;
                    float offsetY = matrix.isActive() ? Mth.sin((ticks + b * 10.0F) / 14.0F) * 0.01F * startup * instability : 0.0F;
                    float offsetZ = matrix.isActive() ? Mth.sin((ticks + c * 10.0F) / 13.0F) * 0.01F * startup * instability : 0.0F;
                    poseStack.pushPose();
                    poseStack.translate(
                            offsetX + (a == 0 ? -0.25F : 0.25F),
                            offsetY + (b == 0 ? -0.25F : 0.25F),
                            offsetZ + (c == 0 ? -0.25F : 0.25F)
                    );
                    if (a > 0) {
                        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                    }
                    if (b > 0) {
                        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                    }
                    if (c > 0) {
                        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
                    }
                    poseStack.scale(0.45F, 0.45F, 0.45F);
                    float glowAlpha = Mth.clamp(
                            (Mth.sin((ticks + a * 2.0F + b * 3.0F + c * 4.0F) / 4.0F) * 0.1F + 0.2F) * startup,
                            0.0F,
                            1.0F
                    );
                    int color = fixedColor != 0 ? fixedColor : ((int) (glowAlpha * 255.0F) << 24) | 0xCC1AFF;
                    model.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, color);
                    poseStack.popPose();
                }
            }
        }
    }

    private static ResourceLocation textureForPillars(TCInfusionMatrixBlockEntity matrix) {
        if (matrix.getLevel() == null) {
            return NORMAL;
        }
        Block pillar = matrix.getLevel().getBlockState(matrix.getBlockPos().offset(-1, -2, -1)).getBlock();
        if (pillar == TCBlocks.PILLAR_ANCIENT.get()) {
            return ANCIENT;
        }
        return pillar == TCBlocks.PILLAR_ELDRITCH.get() ? ELDRITCH : NORMAL;
    }

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/blocks/" + name + ".png");
    }

    private static LayerDefinition createLegacyModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "normal",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F),
                PartPose.ZERO
        );
        root.addOrReplaceChild(
                "overlay",
                CubeListBuilder.create().texOffs(0, 32).mirror().addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F),
                PartPose.ZERO
        );
        return LayerDefinition.create(mesh, 64, 64);
    }
}
