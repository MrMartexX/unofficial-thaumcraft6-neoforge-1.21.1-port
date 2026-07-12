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
import org.joml.Matrix4f;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.TCWispEntity;
import thaumcraft.common.entities.TCWispRenderContract;

final class TCWispRenderer extends EntityRenderer<TCWispEntity> {
    TCWispRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.0F;
    }

    @Override
    public void render(TCWispEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.getHealth() <= 0.0F) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int tickFrame = entity.tickCount % 16;
        renderLayer(poseStack, TCWispRenderContract.CORE, tickFrame, TCWispRenderContract.CORE.color());
        renderLayer(poseStack, TCWispRenderContract.HALO, tickFrame, TCWispRenderContract.HALO.color());

        Aspect aspect = Aspect.getAspect(entity.getWispType());
        int color = aspect == null ? 0 : aspect.getColor();
        renderLayer(poseStack, TCWispRenderContract.ASPECT_NODE, tickFrame, color);

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TCWispEntity entity) {
        return TCWispRenderContract.PARTICLE_TEXTURE;
    }

    private void renderLayer(PoseStack poseStack, TCWispRenderContract.Layer layer, int tickFrame, int color) {
        poseStack.pushPose();
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        RenderSystem.setShaderTexture(0, layer.texture());
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        addFacingQuad(buffer, poseStack.last().pose(), layer, tickFrame, color);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        poseStack.popPose();
    }

    private static void addFacingQuad(BufferBuilder buffer, Matrix4f matrix, TCWispRenderContract.Layer layer, int tickFrame, int color) {
        int frame = layer.frameBase() + tickFrame;
        int xm = frame % layer.gridX();
        int ym = frame / layer.gridY();
        float u1 = xm / (float) layer.gridX();
        float u2 = u1 + 1.0F / layer.gridX();
        float v1 = ym / (float) layer.gridY();
        float v2 = v1 + 1.0F / layer.gridY();
        float scale = layer.scale();
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        int alpha = Math.round(layer.alpha() * 255.0F);

        buffer.addVertex(matrix, -scale, -scale, 0.0F).setUv(u2, v2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, -scale, scale, 0.0F).setUv(u2, v1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, scale, scale, 0.0F).setUv(u1, v1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, scale, -scale, 0.0F).setUv(u1, v2).setColor(red, green, blue, alpha);
    }
}
