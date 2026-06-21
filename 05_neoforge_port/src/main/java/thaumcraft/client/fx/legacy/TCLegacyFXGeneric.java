package thaumcraft.client.fx.legacy;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import thaumcraft.common.lib.fx.TCLegacyFXData;

public final class TCLegacyFXGeneric {
    private final Level level;
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

    private float roll;
    private float oRoll;

    private boolean removed;

    public TCLegacyFXGeneric(
            Level level,
            TCLegacyFXData data,
            double x,
            double y,
            double z,
            double xd,
            double yd,
            double zd
    ) {
        this.level = level;
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
        this.roll = 0.0F;
        this.oRoll = 0.0F;
        this.removed = false;
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
        this.oRoll = this.roll;

        if (this.age++ >= this.data.maxAge) {
            this.removed = true;
            return;
        }

        this.roll += 3.1415927F * this.data.rotationSpeed * 2.0F;

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

    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        float alpha = TCLegacyFXData.sampleKeys(this.data.alphaKeys, this.age, this.data.maxAge);
        float particleScale = TCLegacyFXData.sampleKeys(this.data.scaleKeys, this.age, this.data.maxAge);

        if (alpha <= 0.0F || particleScale <= 0.0F) {
            return;
        }

        Vec3 cameraPos = camera.getPosition();

        float rx = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float ry = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float rz = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        Quaternionf rotation;

        if (this.roll == 0.0F) {
            rotation = camera.rotation();
        } else {
            rotation = new Quaternionf(camera.rotation());
            rotation.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
        }

        float size = 0.1F * particleScale;

        Vector3f[] corners = new Vector3f[] {
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };

        for (Vector3f corner : corners) {
            corner.rotate(rotation);
            corner.mul(size);
            corner.add(rx, ry, rz);
        }

        int frame = getCurrentFrame();
        float u0 = getFrameU(frame, 0.0F);
        float u1 = getFrameU(frame, 1.0F);
        float v0 = getFrameV(frame, 0.0F);
        float v1 = getFrameV(frame, 1.0F);

        float progress = Mth.clamp((this.age + partialTicks) / (float) this.data.maxAge, 0.0F, 1.0F);
        float light = getLegacyBrightness(partialTicks);
        float red = (this.data.redStart + (this.data.redEnd - this.data.redStart) * progress) * light;
        float green = (this.data.greenStart + (this.data.greenEnd - this.data.greenStart) * progress) * light;
        float blue = (this.data.blueStart + (this.data.blueEnd - this.data.blueStart) * progress) * light;

        buffer.addVertex(corners[0].x(), corners[0].y(), corners[0].z())
                .setUv(u1, v1)
                .setColor(red, green, blue, alpha);

        buffer.addVertex(corners[1].x(), corners[1].y(), corners[1].z())
                .setUv(u1, v0)
                .setColor(red, green, blue, alpha);

        buffer.addVertex(corners[2].x(), corners[2].y(), corners[2].z())
                .setUv(u0, v0)
                .setColor(red, green, blue, alpha);

        buffer.addVertex(corners[3].x(), corners[3].y(), corners[3].z())
                .setUv(u0, v1)
                .setColor(red, green, blue, alpha);
    }

    private float getLegacyBrightness(float partialTicks) {
        if (this.data.fullBright || this.level == null) {
            return 1.0F;
        }

        /*
         * Legacy FXGeneric sent Particle#getBrightnessForRender(partialTicks) to the lightmap.
         * The modern compatibility shader is intentionally texture/color only to keep the legacy
         * 1/255 alpha cutoff. Approximate the old lightmap contribution on CPU so non-fullbright
         * particles are no longer always rendered as fullbright.
         */
        double lx = Mth.lerp(partialTicks, this.xo, this.x);
        double ly = Mth.lerp(partialTicks, this.yo, this.y);
        double lz = Mth.lerp(partialTicks, this.zo, this.z);

        int packedLight = LevelRenderer.getLightColor(this.level, BlockPos.containing(lx, ly, lz));
        int block = (packedLight >> 4) & 0xF;
        int sky = (packedLight >> 20) & 0xF;

        return Mth.clamp(Math.max(block, sky) / 15.0F, 0.2F, 1.0F);
    }

    private int getCurrentFrame() {
        if (this.data.finalFrames.length > 0 && this.age > this.data.maxAge - this.data.finalFrames.length) {
            int index = Mth.clamp(this.data.maxAge - this.age, 0, this.data.finalFrames.length - 1);
            return this.data.finalFrames[index];
        }
        if (this.data.loop) {
            return this.data.startParticle + (this.age / this.data.particleInc) % this.data.numParticles;
        }

        float progress = this.age / (float) Math.max(1, this.data.maxAge);
        int offset = Math.min(this.data.numParticles - 1, (int) (this.data.numParticles * progress));

        return this.data.startParticle + offset;
    }

    private float getFrameU(int frame, float edge) {
        int particleTextureIndexX = Math.floorMod(frame, this.data.gridSize);
        return (particleTextureIndexX + edge) / (float) this.data.gridSize;
    }

    private float getFrameV(int frame, float edge) {
        int particleTextureIndexY = Math.floorDiv(frame, this.data.gridSize);
        return (particleTextureIndexY + edge) / (float) this.data.gridSize;
    }
}
