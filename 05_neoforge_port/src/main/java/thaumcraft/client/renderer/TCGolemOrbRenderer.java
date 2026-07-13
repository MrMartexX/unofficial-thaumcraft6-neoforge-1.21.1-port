package thaumcraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import thaumcraft.common.entities.TCGolemOrbEntity;
import thaumcraft.common.entities.TCGolemOrbRenderContract;

final class TCGolemOrbRenderer extends EntityRenderer<TCGolemOrbEntity> {
    TCGolemOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.0F;
    }

    @Override
    public void render(TCGolemOrbEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        float bob = Mth.sin(entity.tickCount / 5.0F) * TCGolemOrbRenderContract.LEGACY_BOB_AMPLITUDE
                + TCGolemOrbRenderContract.LEGACY_BOB_OFFSET;
        poseStack.scale(1.0F + bob, 1.0F + bob, 1.0F + bob);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TCGolemOrbRenderContract.PARTICLE_TEXTURE);

        float u1 = TCGolemOrbRenderContract.frameU1(entity.tickCount);
        float u2 = TCGolemOrbRenderContract.frameU2(entity.tickCount);
        float v1 = TCGolemOrbRenderContract.frameV1(entity.isRed());
        float v2 = TCGolemOrbRenderContract.frameV2(entity.isRed());
        int alpha = Math.round(255.0F * TCGolemOrbRenderContract.LEGACY_ALPHA);
        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.addVertex(matrix, -0.5F, -0.5F, 0.0F).setUv(u1, v2).setColor(255, 255, 255, alpha);
        buffer.addVertex(matrix, 0.5F, -0.5F, 0.0F).setUv(u2, v2).setColor(255, 255, 255, alpha);
        buffer.addVertex(matrix, 0.5F, 0.5F, 0.0F).setUv(u2, v1).setColor(255, 255, 255, alpha);
        buffer.addVertex(matrix, -0.5F, 0.5F, 0.0F).setUv(u1, v1).setColor(255, 255, 255, alpha);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TCGolemOrbEntity entity) {
        return TCGolemOrbRenderContract.PARTICLE_TEXTURE;
    }
}
