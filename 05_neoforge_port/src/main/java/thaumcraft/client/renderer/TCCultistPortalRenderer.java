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
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCCultistPortalGreaterEntity;
import thaumcraft.common.entities.TCCultistPortalLesserEntity;

abstract class TCCultistPortalRenderer<T extends Entity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/cultist_portal.png");

    TCCultistPortalRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.0F;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (!isVisible(entity)) {
            return;
        }
        float pulse = pulse(entity);
        float size = baseSize(entity) + pulse * 0.04F;
        int alpha = Math.min(255, Math.round((0.72F + pulse * 0.02F) * 255.0F));

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.pushPose();
        poseStack.translate(0.0F, entity.getBbHeight() * 0.5F, 0.0F);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        addQuad(buffer, poseStack.last().pose(), size, alpha);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        poseStack.popPose();

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    protected float baseSize(T entity) {
        return Math.max(entity.getBbWidth(), entity.getBbHeight()) * 0.75F;
    }

    protected abstract boolean isVisible(T entity);

    protected abstract float pulse(T entity);

    private static void addQuad(BufferBuilder buffer, Matrix4f matrix, float size, int alpha) {
        buffer.addVertex(matrix, -size, -size, 0.0F).setUv(0.0F, 1.0F).setColor(255, 255, 255, alpha);
        buffer.addVertex(matrix, -size, size, 0.0F).setUv(0.0F, 0.0F).setColor(255, 255, 255, alpha);
        buffer.addVertex(matrix, size, size, 0.0F).setUv(1.0F, 0.0F).setColor(255, 255, 255, alpha);
        buffer.addVertex(matrix, size, -size, 0.0F).setUv(1.0F, 1.0F).setColor(255, 255, 255, alpha);
    }
}

final class TCCultistPortalLesserRenderer extends TCCultistPortalRenderer<TCCultistPortalLesserEntity> {
    TCCultistPortalLesserRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected boolean isVisible(TCCultistPortalLesserEntity entity) {
        return entity.isActive() || entity.pulseForValidation() > 0;
    }

    @Override
    protected float pulse(TCCultistPortalLesserEntity entity) {
        return entity.pulseForValidation();
    }
}

final class TCCultistPortalGreaterRenderer extends TCCultistPortalRenderer<TCCultistPortalGreaterEntity> {
    TCCultistPortalGreaterRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected boolean isVisible(TCCultistPortalGreaterEntity entity) {
        return true;
    }

    @Override
    protected float pulse(TCCultistPortalGreaterEntity entity) {
        return entity.pulse;
    }
}
