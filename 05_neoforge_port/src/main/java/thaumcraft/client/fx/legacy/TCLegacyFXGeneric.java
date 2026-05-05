package thaumcraft.client.fx.legacy;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class TCLegacyFXGeneric {
    private static final int LEGACY_PARTICLE_COLUMNS = 16;

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
    private int maxAge;

    private int startParticle;
    private int numParticles;
    private int particleInc;
    private int gridSize;
    private boolean loop;

    private float redStart;
    private float greenStart;
    private float blueStart;
    private float redEnd;
    private float greenEnd;
    private float blueEnd;

    private float[] alphaKeys;
    private float[] scaleKeys;

    private double slowDown;
    private float gravity;
    private double randomX;
    private double randomY;
    private double randomZ;
    private double windX;
    private double windZ;

    private float rotationSpeed;
    private float roll;
    private float oRoll;

    private boolean removed;

    private TCLegacyFXGeneric(double x, double y, double z, double xd, double yd, double zd) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        this.maxAge = 20;
        this.startParticle = 0;
        this.numParticles = 1;
        this.particleInc = 1;
        this.gridSize = 64;
        this.loop = false;

        this.redStart = 1.0F;
        this.greenStart = 1.0F;
        this.blueStart = 1.0F;
        this.redEnd = 1.0F;
        this.greenEnd = 1.0F;
        this.blueEnd = 1.0F;

        this.alphaKeys = new float[] { 1.0F };
        this.scaleKeys = new float[] { 1.0F };

        this.slowDown = 1.0D;
        this.gravity = 0.0F;
        this.randomX = 0.0D;
        this.randomY = 0.0D;
        this.randomZ = 0.0D;
        this.windX = 0.0D;
        this.windZ = 0.0D;

        this.rotationSpeed = 0.0F;
        this.roll = 0.0F;
        this.oRoll = 0.0F;
    }

    public static TCLegacyFXGeneric wispyMote(
            double x,
            double y,
            double z,
            double xd,
            double yd,
            double zd,
            int age,
            float red,
            float green,
            float blue,
            float gravity,
            float randomAgeFactor
    ) {
        TCLegacyFXGeneric fx = new TCLegacyFXGeneric(x, y, z, xd, yd, zd);

        int randomizedAge = Math.max(1, (int) (age + (age / 2.0F) * randomAgeFactor));

        fx.setMaxAge(randomizedAge);
        fx.setRBGColorF(red, green, blue);
        fx.setAlphaF(0.0F, 0.6F, 0.6F, 0.0F);
        fx.setGridSize(64);
        fx.setParticles(512, 16, 1);
        fx.setScale(1.0F, 0.5F);
        fx.setLoop(true);
        fx.setWind(0.001D);
        fx.setGravity(gravity);
        fx.setRandomMovementScale(0.0025F, 0.0F, 0.0025F);

        return fx;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = Math.max(1, maxAge);
    }

    public void setRBGColorF(float red, float green, float blue) {
        this.redStart = red;
        this.greenStart = green;
        this.blueStart = blue;
        this.redEnd = red;
        this.greenEnd = green;
        this.blueEnd = blue;
    }

    public void setRBGColorF(float redStart, float greenStart, float blueStart, float redEnd, float greenEnd, float blueEnd) {
        this.redStart = redStart;
        this.greenStart = greenStart;
        this.blueStart = blueStart;
        this.redEnd = redEnd;
        this.greenEnd = greenEnd;
        this.blueEnd = blueEnd;
    }

    public void setAlphaF(float... alphaKeys) {
        this.alphaKeys = alphaKeys == null || alphaKeys.length == 0 ? new float[] { 1.0F } : alphaKeys;
    }

    public void setScale(float... scaleKeys) {
        this.scaleKeys = scaleKeys == null || scaleKeys.length == 0 ? new float[] { 1.0F } : scaleKeys;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = Math.max(1, gridSize);
    }

    public void setParticles(int startParticle, int numParticles, int particleInc) {
        this.startParticle = startParticle;
        this.numParticles = Math.max(1, numParticles);
        this.particleInc = Math.max(1, particleInc);
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setSlowDown(double slowDown) {
        this.slowDown = slowDown;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public void setWind(double wind) {
        this.windX = wind;
        this.windZ = 0.0D;
    }

    public void setRandomMovementScale(float randomX, float randomY, float randomZ) {
        this.randomX = randomX;
        this.randomY = randomY;
        this.randomZ = randomZ;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public void tick(java.util.Random random) {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.oRoll = this.roll;

        if (this.age++ >= this.maxAge) {
            this.removed = true;
            return;
        }

        this.roll += 3.1415927F * this.rotationSpeed * 2.0F;

        this.yd -= 0.04D * this.gravity;

        this.x += this.xd;
        this.y += this.yd;
        this.z += this.zd;

        this.xd *= this.slowDown;
        this.yd *= this.slowDown;
        this.zd *= this.slowDown;

        this.xd += random.nextGaussian() * this.randomX;
        this.yd += random.nextGaussian() * this.randomY;
        this.zd += random.nextGaussian() * this.randomZ;

        this.xd += this.windX;
        this.zd += this.windZ;
    }

    public boolean canRender() {
        if (this.removed) {
            return false;
        }

        float alpha = sampleKeys(this.alphaKeys, this.age, this.maxAge);
        float particleScale = sampleKeys(this.scaleKeys, this.age, this.maxAge);

        return alpha > 0.0F && particleScale > 0.0F;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        float alpha = sampleKeys(this.alphaKeys, this.age, this.maxAge);
        float particleScale = sampleKeys(this.scaleKeys, this.age, this.maxAge);

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

        float progress = Mth.clamp((this.age + partialTicks) / (float) this.maxAge, 0.0F, 1.0F);
        float red = this.redStart + (this.redEnd - this.redStart) * progress;
        float green = this.greenStart + (this.greenEnd - this.greenStart) * progress;
        float blue = this.blueStart + (this.blueEnd - this.blueStart) * progress;

        int light = 0x00F000F0;

        buffer.addVertex(corners[0].x(), corners[0].y(), corners[0].z())
                .setUv(u1, v1)
                .setColor(red, green, blue, alpha)
                .setLight(light);

        buffer.addVertex(corners[1].x(), corners[1].y(), corners[1].z())
                .setUv(u1, v0)
                .setColor(red, green, blue, alpha)
                .setLight(light);

        buffer.addVertex(corners[2].x(), corners[2].y(), corners[2].z())
                .setUv(u0, v0)
                .setColor(red, green, blue, alpha)
                .setLight(light);

        buffer.addVertex(corners[3].x(), corners[3].y(), corners[3].z())
                .setUv(u0, v1)
                .setColor(red, green, blue, alpha)
                .setLight(light);
    }

    private int getCurrentFrame() {
        if (this.loop) {
            return this.startParticle + (this.age / this.particleInc) % this.numParticles;
        }

        float progress = this.age / (float) Math.max(1, this.maxAge);
        int offset = Math.min(this.numParticles - 1, (int) (this.numParticles * progress));

        return this.startParticle + offset;
    }

    private float getFrameU(int frame, float edge) {
        int particleTextureIndexX = Math.floorMod(frame, LEGACY_PARTICLE_COLUMNS);
        return (particleTextureIndexX + edge) / (float) this.gridSize;
    }

    private float getFrameV(int frame, float edge) {
        int particleTextureIndexY = Math.floorDiv(frame, LEGACY_PARTICLE_COLUMNS);
        return (particleTextureIndexY + edge) / (float) this.gridSize;
    }

    private static float sampleKeys(float[] keys, int age, int maxAge) {
        if (keys == null || keys.length == 0) {
            return 0.0F;
        }

        if (keys.length == 1 || maxAge <= 1) {
            return keys[0];
        }

        float progress = Mth.clamp(age / (float) Math.max(1, maxAge - 1), 0.0F, 1.0F);
        float scaled = progress * (keys.length - 1);
        int index = Math.min(keys.length - 2, (int) scaled);
        float local = scaled - index;

        return keys[index] + (keys[index + 1] - keys[index]) * local;
    }
}