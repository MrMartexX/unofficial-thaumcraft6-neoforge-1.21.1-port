package thaumcraft.client.lib;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

/**
 * Compatibility layer for TC6 GUI quad rendering.
 *
 * Legacy UtilsFX.renderQuadCentered(..., brightness, blend, opacity) used a GL fixed-function-ish
 * path with alphaFunc(1/255), additive blending and a lightmap-derived brightness path.
 * Minecraft 1.21.1's vanilla POSITION_TEX_COLOR / POSITION_COLOR_TEX_LIGHTMAP shaders discard
 * texture fragments below alpha 0.1 and the textured lightmap shader does not sample Sampler2.
 *
 * This class keeps the public legacy call shape intact and only switches to the custom shader for
 * the exact TC6 knowledge-particle flare atlas frames.
 */
public final class TCGuiUtils {
    private static final ResourceLocation TC_PARTICLES =
            ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/particles.png");

    private TCGuiUtils() {
    }

    public static void renderQuadCentered(
            GuiGraphics guiGraphics,
            ResourceLocation texture,
            float scale,
            float red,
            float green,
            float blue,
            int brightness,
            int blend,
            float opacity
    ) {
        renderQuadCentered(guiGraphics, texture, 1, 1, 0, scale, red, green, blue, brightness, blend, opacity);
    }

    public static void renderQuadCentered(
            GuiGraphics guiGraphics,
            ResourceLocation texture,
            int gridX,
            int gridY,
            int frame,
            float scale,
            float red,
            float green,
            float blue,
            int brightness,
            int blend,
            float opacity
    ) {
        int safeGridX = Math.max(1, gridX);
        int safeGridY = Math.max(1, gridY);
        int xm = frame % safeGridX;
        int ym = frame / safeGridY;

        float f1 = xm / (float) safeGridX;
        float f2 = f1 + 1.0F / safeGridX;
        float f3 = ym / (float) safeGridY;
        float f4 = f3 + 1.0F / safeGridY;

        float x0 = -0.5F * scale;
        float y0 = 0.5F * scale;
        float x1 = 0.5F * scale;
        float y1 = -0.5F * scale;

        if (isLegacyKnowledgeParticleFlare(texture, safeGridX, safeGridY, frame, brightness, blend)) {
            renderLegacyKnowledgeParticleFlare(guiGraphics.pose(), texture, x0, y0, x1, y1, f1, f2, f3, f4, red, green, blue, opacity, brightness);
            return;
        }

        renderLegacyQuad(
                guiGraphics.pose(),
                texture,
                x0,
                y0,
                x1,
                y1,
                f1,
                f2,
                f3,
                f4,
                red,
                green,
                blue,
                opacity,
                brightness,
                blend
        );
    }

    public static void drawTexturedQuadFull(
            GuiGraphics guiGraphics,
            ResourceLocation texture,
            float x,
            float y,
            float z,
            float width,
            float height,
            float red,
            float green,
            float blue,
            float opacity,
            int blend
    ) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, legacyDestBlend(blend));
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        PoseStack.Pose pose = guiGraphics.pose().last();

        addVertex(buffer, pose, x, y + height, z, 0.0F, 1.0F, red, green, blue, opacity);
        addVertex(buffer, pose, x + width, y + height, z, 1.0F, 1.0F, red, green, blue, opacity);
        addVertex(buffer, pose, x + width, y, z, 1.0F, 0.0F, red, green, blue, opacity);
        addVertex(buffer, pose, x, y, z, 0.0F, 0.0F, red, green, blue, opacity);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.defaultBlendFunc();
    }

    private static boolean isLegacyKnowledgeParticleFlare(ResourceLocation texture, int gridX, int gridY, int frame, int brightness, int blend) {
        return blend == 1
                && brightness == 200
                && gridX == 64
                && gridY == 64
                && frame >= 320
                && frame < 336
                && TC_PARTICLES.equals(texture);
    }

    private static void renderLegacyKnowledgeParticleFlare(
            PoseStack poseStack,
            ResourceLocation texture,
            float x0,
            float y0,
            float x1,
            float y1,
            float uMin,
            float uMax,
            float vMin,
            float vMax,
            float red,
            float green,
            float blue,
            float opacity,
            int brightness
    ) {
        ShaderInstance shader = TCLegacyShaders.legacyParticleGuiShader();
        if (shader == null) {
            /*
             * Should only happen if something renders before shader registration; keep the game usable
             * and fall back to vanilla rather than crashing.
             */
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.setShaderTexture(0, texture);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            renderTexturedColorQuad(poseStack, x0, y0, x1, y1, uMin, uMax, vMin, vMax, red, green, blue, opacity);
            RenderSystem.defaultBlendFunc();
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShader(() -> shader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        PoseStack.Pose pose = poseStack.last();

        addVertexLight(buffer, pose, x0, y0, 0.0F, uMax, vMax, red, green, blue, opacity, brightness);
        addVertexLight(buffer, pose, x1, y0, 0.0F, uMax, vMin, red, green, blue, opacity, brightness);
        addVertexLight(buffer, pose, x1, y1, 0.0F, uMin, vMin, red, green, blue, opacity, brightness);
        addVertexLight(buffer, pose, x0, y1, 0.0F, uMin, vMax, red, green, blue, opacity, brightness);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.defaultBlendFunc();
    }

    private static void renderLegacyQuad(
            PoseStack poseStack,
            ResourceLocation texture,
            float x0,
            float y0,
            float x1,
            float y1,
            float uMin,
            float uMax,
            float vMin,
            float vMax,
            float red,
            float green,
            float blue,
            float opacity,
            int brightness,
            int blend
    ) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, legacyDestBlend(blend));
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (brightness != -99) {
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
            PoseStack.Pose pose = poseStack.last();

            addVertexLight(buffer, pose, x0, y0, 0.0F, uMax, vMax, red, green, blue, opacity, brightness);
            addVertexLight(buffer, pose, x1, y0, 0.0F, uMax, vMin, red, green, blue, opacity, brightness);
            addVertexLight(buffer, pose, x1, y1, 0.0F, uMin, vMin, red, green, blue, opacity, brightness);
            addVertexLight(buffer, pose, x0, y1, 0.0F, uMin, vMax, red, green, blue, opacity, brightness);

            BufferUploader.drawWithShader(buffer.buildOrThrow());
        } else {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            renderTexturedColorQuad(poseStack, x0, y0, x1, y1, uMin, uMax, vMin, vMax, red, green, blue, opacity);
        }

        RenderSystem.defaultBlendFunc();
    }

    private static void renderTexturedColorQuad(
            PoseStack poseStack,
            float x0,
            float y0,
            float x1,
            float y1,
            float uMin,
            float uMax,
            float vMin,
            float vMax,
            float red,
            float green,
            float blue,
            float opacity
    ) {
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        PoseStack.Pose pose = poseStack.last();

        addVertex(buffer, pose, x0, y0, 0.0F, uMax, vMax, red, green, blue, opacity);
        addVertex(buffer, pose, x1, y0, 0.0F, uMax, vMin, red, green, blue, opacity);
        addVertex(buffer, pose, x1, y1, 0.0F, uMin, vMin, red, green, blue, opacity);
        addVertex(buffer, pose, x0, y1, 0.0F, uMin, vMax, red, green, blue, opacity);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void addVertex(
            BufferBuilder buffer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float u,
            float v,
            float red,
            float green,
            float blue,
            float opacity
    ) {
        buffer.addVertex(pose, x, y, z)
                .setUv(u, v)
                .setColor(
                        toColorChannel(red),
                        toColorChannel(green),
                        toColorChannel(blue),
                        toColorChannel(opacity)
                );
    }

    private static void addVertexLight(
            BufferBuilder buffer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float u,
            float v,
            float red,
            float green,
            float blue,
            float opacity,
            int brightness
    ) {
        buffer.addVertex(pose, x, y, z)
                .setColor(
                        toColorChannel(red),
                        toColorChannel(green),
                        toColorChannel(blue),
                        toColorChannel(opacity)
                )
                .setUv(u, v)
                .setLight(brightness);
    }

    private static int toColorChannel(float value) {
        return Math.max(0, Math.min(255, Math.round(value * 255.0F)));
    }

    private static GlStateManager.DestFactor legacyDestBlend(int blend) {
        return switch (blend) {
            case 0 -> GlStateManager.DestFactor.ZERO;
            case 1 -> GlStateManager.DestFactor.ONE;
            case 768 -> GlStateManager.DestFactor.SRC_COLOR;
            case 769 -> GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR;
            case 770 -> GlStateManager.DestFactor.SRC_ALPHA;
            case 771 -> GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA;
            case 772 -> GlStateManager.DestFactor.DST_ALPHA;
            case 773 -> GlStateManager.DestFactor.ONE_MINUS_DST_ALPHA;
            default -> GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA;
        };
    }
}
