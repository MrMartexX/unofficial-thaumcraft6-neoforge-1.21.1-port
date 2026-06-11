package thaumcraft.client.fx.legacy;

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
import net.minecraft.util.Mth;
import thaumcraft.client.lib.TCLegacyShaders;
import thaumcraft.common.lib.fx.TCLegacyFXData;

/**
 * GUI-coordinate equivalent of legacy TC6 FXGenericGui.
 */
public final class TCLegacyFXGenericGui {
    private final TCLegacyFXData data;

    private double x;
    private double y;
    private double z;
    private double xo;
    private double yo;
    private double zo;

    private double xd;
    private double yd;
    private double zd;

    private int age;
    private boolean removed;

    public TCLegacyFXGenericGui(
            TCLegacyFXData data,
            double x,
            double y,
            double z,
            double xd,
            double yd,
            double zd
    ) {
        this.data = data;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
    }

    public int getLayer() {
        return this.data.layer;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public boolean canRender() {
        if (this.removed) {
            return false;
        }

        float alpha = TCLegacyFXData.sampleKeys(this.data.alphaKeys, this.age, this.data.maxAge);
        float particleScale = TCLegacyFXData.sampleKeys(this.data.scaleKeys, this.age, this.data.maxAge);

        return alpha > 0.0F && particleScale > 0.0F;
    }

    public void tick(java.util.Random random) {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.data.maxAge) {
            this.removed = true;
            return;
        }

        this.yd -= 0.04D * this.data.gravity;

        this.x += this.xd;
        this.y += this.yd;
        this.z += this.zd;

        this.xd *= this.data.slowDown;
        this.yd *= this.data.slowDown;
        this.zd *= this.data.slowDown;

        this.xd += random.nextGaussian() * this.data.randomX;
        this.yd += random.nextGaussian() * this.data.randomY;
        this.zd += random.nextGaussian() * this.data.randomZ;

        this.xd += this.data.windX;
        this.zd += this.data.windZ;
    }

    public void render(GuiGraphics guiGraphics, float partialTicks) {
        float alpha = TCLegacyFXData.sampleKeys(this.data.alphaKeys, this.age, this.data.maxAge);
        float particleScale = TCLegacyFXData.sampleKeys(this.data.scaleKeys, this.age, this.data.maxAge);

        if (alpha <= 0.0F || particleScale <= 0.0F) {
            return;
        }

        float progress = Mth.clamp((this.age + partialTicks) / (float) this.data.maxAge, 0.0F, 1.0F);
        float red = this.data.redStart + (this.data.redEnd - this.data.redStart) * progress;
        float green = this.data.greenStart + (this.data.greenEnd - this.data.greenStart) * progress;
        float blue = this.data.blueStart + (this.data.blueEnd - this.data.blueStart) * progress;

        int frame = getCurrentFrame();
        int gridSize = Math.max(1, this.data.gridSize);
        float u0 = Math.floorMod(frame, gridSize) / (float) gridSize;
        float u1 = u0 + 1.0F / gridSize;
        float v0 = Math.floorDiv(frame, gridSize) / (float) gridSize;
        float v1 = v0 + 1.0F / gridSize;

        float renderX = (float) Mth.lerp(partialTicks, this.xo, this.x);
        float renderY = (float) Mth.lerp(partialTicks, this.yo, this.y);
        float renderZ = (float) Mth.lerp(partialTicks, this.zo, this.z);

        // Legacy FXGenericGui: ts = 0.1F * scale, so full quad size = 0.2F * scale.
        float halfSize = 0.1F * particleScale;

        ShaderInstance shader = TCLegacyShaders.legacyParticleShader();
        if (shader != null) {
            RenderSystem.setShader(() -> shader);
        } else {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        }

        RenderSystem.setShaderTexture(0, TCLegacyParticleEngine.PARTICLE_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(renderX, renderY, 240.0F + renderZ);

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        PoseStack.Pose pose = guiGraphics.pose().last();

        buffer.addVertex(pose, -halfSize, halfSize, 0.0F)
                .setUv(u1, v1)
                .setColor(red, green, blue, alpha);

        buffer.addVertex(pose, halfSize, halfSize, 0.0F)
                .setUv(u1, v0)
                .setColor(red, green, blue, alpha);

        buffer.addVertex(pose, halfSize, -halfSize, 0.0F)
                .setUv(u0, v0)
                .setColor(red, green, blue, alpha);

        buffer.addVertex(pose, -halfSize, -halfSize, 0.0F)
                .setUv(u0, v1)
                .setColor(red, green, blue, alpha);

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        guiGraphics.pose().popPose();
    }

    private int getCurrentFrame() {
        if (this.data.loop) {
            return this.data.startParticle + (this.age / this.data.particleInc) % this.data.numParticles;
        }

        float progress = this.age / (float) Math.max(1, this.data.maxAge);
        int offset = Math.min(this.data.numParticles - 1, (int) (this.data.numParticles * progress));

        return this.data.startParticle + offset;
    }
}
