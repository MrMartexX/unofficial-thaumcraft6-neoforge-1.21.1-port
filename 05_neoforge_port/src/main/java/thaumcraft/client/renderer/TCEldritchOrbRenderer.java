package thaumcraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import thaumcraft.common.entities.TCEldritchOrbEntity;
import thaumcraft.common.entities.TCEldritchOrbRenderContract;

final class TCEldritchOrbRenderer extends EntityRenderer<TCEldritchOrbEntity> {
    private final Random random = new Random();

    TCEldritchOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.0F;
    }

    @Override
    public void render(TCEldritchOrbEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        renderLegacyTendrils(entity, poseStack);
        renderLegacyBillboard(entity, poseStack);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TCEldritchOrbEntity entity) {
        return TCEldritchOrbRenderContract.PARTICLE_TEXTURE;
    }

    private void renderLegacyTendrils(TCEldritchOrbEntity entity, PoseStack poseStack) {
        random.setSeed(TCEldritchOrbRenderContract.LEGACY_RANDOM_SEED);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.enableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        float ageRotation = entity.tickCount / 80.0F * 360.0F;
        float ramp = Math.min(entity.tickCount, 10) / 10.0F;
        poseStack.pushPose();
        for (int i = 0; i < TCEldritchOrbRenderContract.LEGACY_TENDRIL_COUNT; i++) {
            poseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F + ageRotation));

            float length = random.nextFloat() * 20.0F + 5.0F;
            float width = random.nextFloat() * 2.0F + 1.0F;
            if (ramp <= 0.0F) {
                length = 0.0F;
                width = 0.0F;
            } else {
                length /= 30.0F / ramp;
                width /= 30.0F / ramp;
            }

            Matrix4f matrix = poseStack.last().pose();
            BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            buffer.addVertex(matrix, 0.0F, 0.0F, 0.0F).setColor(255, 255, 255, 255);
            buffer.addVertex(matrix, -0.866F * width, length, -0.5F * width).setColor(64, 64, 64, 255);
            buffer.addVertex(matrix, 0.866F * width, length, -0.5F * width).setColor(64, 64, 64, 255);
            buffer.addVertex(matrix, 0.0F, length, width).setColor(64, 64, 64, 255);
            buffer.addVertex(matrix, -0.866F * width, length, -0.5F * width).setColor(64, 64, 64, 255);
            BufferUploader.drawWithShader(buffer.buildOrThrow());
        }
        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private void renderLegacyBillboard(TCEldritchOrbEntity entity, PoseStack poseStack) {
        poseStack.pushPose();
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.scale(
                TCEldritchOrbRenderContract.LEGACY_BILLBOARD_SCALE,
                TCEldritchOrbRenderContract.LEGACY_BILLBOARD_SCALE,
                TCEldritchOrbRenderContract.LEGACY_BILLBOARD_SCALE
        );

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TCEldritchOrbRenderContract.PARTICLE_TEXTURE);

        float u1 = TCEldritchOrbRenderContract.frameU1(entity.tickCount);
        float u2 = TCEldritchOrbRenderContract.frameU2(entity.tickCount);
        float v1 = TCEldritchOrbRenderContract.frameV1();
        float v2 = TCEldritchOrbRenderContract.frameV2();
        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.addVertex(matrix, -0.5F, -0.5F, 0.0F).setUv(u1, v2).setColor(255, 255, 255, 255);
        buffer.addVertex(matrix, 0.5F, -0.5F, 0.0F).setUv(u2, v2).setColor(255, 255, 255, 255);
        buffer.addVertex(matrix, 0.5F, 0.5F, 0.0F).setUv(u2, v1).setColor(255, 255, 255, 255);
        buffer.addVertex(matrix, -0.5F, 0.5F, 0.0F).setUv(u1, v1).setColor(255, 255, 255, 255);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }
}
