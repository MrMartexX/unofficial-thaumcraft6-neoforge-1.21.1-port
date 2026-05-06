# Wispy FX behavior verification

Generated from local legacy source and current port source.

## Legacy FXDispatcher.drawWispyMotes

~~~java
public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float grav) {
        drawWispyMotes(d, e, f, vx, vy, vz, age, 0.25f + getWorld().rand.nextFloat() * 0.75f, 0.25f + getWorld().rand.nextFloat() * 0.75f, 0.25f + getWorld().rand.nextFloat() * 0.75f, grav);
    }
~~~

~~~java
public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) {
        FXGeneric fb = new FXGeneric(getWorld(), d, e, f, vx, vy, vz);
        fb.setMaxAge((int)(age + age / 2 * getWorld().rand.nextFloat()));
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(0.0f, 0.6f, 0.6f, 0.0f);
        fb.setGridSize(64);
        fb.setParticles(512, 16, 1);
        fb.setScale(1.0f, 0.5f);
        fb.setLoop(true);
        fb.setWind(0.001);
        fb.setGravity(grav);
        fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);
        ParticleEngine.addEffect(getWorld(), fb);
    }
~~~

## Legacy plant methods containing drawWispyMotes

### src/main/java/thaumcraft/common/blocks/world/plants/BlockPlantShimmerleaf.java

~~~java
public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextInt(3) == 0) {
            float xr = (float)(pos.getX() + 0.5f + rand.nextGaussian() * 0.1);
            float yr = (float)(pos.getY() + 0.4f + rand.nextGaussian() * 0.1);
            float zr = (float)(pos.getZ() + 0.5f + rand.nextGaussian() * 0.1);
            FXDispatcher.INSTANCE.drawWispyMotes(xr, yr, zr, rand.nextGaussian() * 0.01, rand.nextGaussian() * 0.01, rand.nextGaussian() * 0.01, 10, 0.3f + world.rand.nextFloat() * 0.3f, 0.7f + world.rand.nextFloat() * 0.3f, 0.7f + world.rand.nextFloat() * 0.3f, 0.0f);
        }
    }
~~~

### src/main/java/thaumcraft/common/blocks/world/plants/BlockPlantVishroom.java

~~~java
public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextInt(3) == 0) {
            float xr = pos.getX() + 0.5f + (rand.nextFloat() - rand.nextFloat()) * 0.4f;
            float yr = pos.getY() + 0.3f;
            float zr = pos.getZ() + 0.5f + (rand.nextFloat() - rand.nextFloat()) * 0.4f;
            FXDispatcher.INSTANCE.drawWispyMotes(xr, yr, zr, 0.0, 0.0, 0.0, 10, 0.5f, 0.3f, 0.8f, 0.001f);
        }
    }
~~~

## Current TCFXDispatcher

~~~java
package thaumcraft.common.lib.fx;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public final class TCFXDispatcher {
    @FunctionalInterface
    public interface ClientSink {
        void addLegacyFX(
                Level level,
                TCLegacyFXData data,
                double x,
                double y,
                double z,
                double motionX,
                double motionY,
                double motionZ
        );
    }

    private static ClientSink clientSink = (level, data, x, y, z, motionX, motionY, motionZ) -> {
    };

    private TCFXDispatcher() {
    }

    public static void setClientSink(ClientSink sink) {
        clientSink = sink == null
                ? (level, data, x, y, z, motionX, motionY, motionZ) -> {
                }
                : sink;
    }

    public static void drawLegacyFX(
            Level level,
            TCLegacyFXData data,
            double x,
            double y,
            double z,
            double motionX,
            double motionY,
            double motionZ
    ) {
        if (!level.isClientSide()) {
            return;
        }

        clientSink.addLegacyFX(level, data, x, y, z, motionX, motionY, motionZ);
    }

    public static void drawWispyMotesOnBlock(Level level, BlockPos pos, int age, float gravity) {
        if (!level.isClientSide()) {
            return;
        }

        RandomSource random = level.random;

        drawWispyMotes(
                level,
                pos.getX() + random.nextFloat(),
                pos.getY(),
                pos.getZ() + random.nextFloat(),
                0.0D,
                0.0D,
                0.0D,
                age,
                0.4F + random.nextFloat() * 0.6F,
                0.6F + random.nextFloat() * 0.4F,
                0.6F + random.nextFloat() * 0.4F,
                gravity
        );
    }

    public static void drawWispyMotes(
            Level level,
            double x,
            double y,
            double z,
            double motionX,
            double motionY,
            double motionZ,
            int age,
            float gravity
    ) {
        if (!level.isClientSide()) {
            return;
        }

        RandomSource random = level.random;

        drawWispyMotes(
                level,
                x,
                y,
                z,
                motionX,
                motionY,
                motionZ,
                age,
                0.25F + random.nextFloat() * 0.75F,
                0.25F + random.nextFloat() * 0.75F,
                0.25F + random.nextFloat() * 0.75F,
                gravity
        );
    }

    public static void drawWispyMotes(
            Level level,
            double x,
            double y,
            double z,
            double motionX,
            double motionY,
            double motionZ,
            int age,
            float red,
            float green,
            float blue,
            float gravity
    ) {
        if (!level.isClientSide()) {
            return;
        }

        TCLegacyFXData data = TCLegacyFXData.wispyMote(age, red, green, blue, gravity, level.random.nextFloat());

        drawLegacyFX(level, data, x, y, z, motionX, motionY, motionZ);
    }

    public static void drawGenericParticles(
            Level level,
            double x,
            double y,
            double z,
            double motionX,
            double motionY,
            double motionZ,
            int age,
            int startParticle,
            int numParticles,
            int particleInc,
            int gridSize,
            boolean loop,
            int layer,
            float red,
            float green,
            float blue,
            float alpha,
            float scale
    ) {
        if (!level.isClientSide()) {
            return;
        }

        TCLegacyFXData data = TCLegacyFXData.generic(
                age,
                startParticle,
                numParticles,
                particleInc,
                gridSize,
                loop,
                layer,
                red,
                green,
                blue,
                alpha,
                scale
        );

        drawLegacyFX(level, data, x, y, z, motionX, motionY, motionZ);
    }

    public static void drawShimmerleafMote(Level level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide() || random.nextInt(3) != 0) {
            return;
        }

        double x = pos.getX() + 0.5D + random.nextGaussian() * 0.1D;
        double y = pos.getY() + 0.4D + random.nextGaussian() * 0.1D;
        double z = pos.getZ() + 0.5D + random.nextGaussian() * 0.1D;

        drawWispyMotes(
                level,
                x,
                y,
                z,
                random.nextGaussian() * 0.01D,
                random.nextGaussian() * 0.01D,
                random.nextGaussian() * 0.01D,
                10,
                0.3F + level.random.nextFloat() * 0.3F,
                0.7F + level.random.nextFloat() * 0.3F,
                0.7F + level.random.nextFloat() * 0.3F,
                0.0F
        );
    }

    public static void drawVishroomMote(Level level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide() || random.nextInt(3) != 0) {
            return;
        }

        double x = pos.getX() + 0.5D + (random.nextFloat() - random.nextFloat()) * 0.4D;
        double y = pos.getY() + 0.3D;
        double z = pos.getZ() + 0.5D + (random.nextFloat() - random.nextFloat()) * 0.4D;

        drawWispyMotes(
                level,
                x,
                y,
                z,
                0.0D,
                0.0D,
                0.0D,
                10,
                0.5F,
                0.3F,
                0.8F,
                0.001F
        );
    }
}
~~~

## Current TCLegacyFXData

~~~java
package thaumcraft.common.lib.fx;

import java.util.Arrays;

public final class TCLegacyFXData {
    public final int maxAge;
    public final int startParticle;
    public final int numParticles;
    public final int particleInc;
    public final int gridSize;
    public final boolean loop;
    public final int layer;

    public final float redStart;
    public final float greenStart;
    public final float blueStart;
    public final float redEnd;
    public final float greenEnd;
    public final float blueEnd;

    public final float[] alphaKeys;
    public final float[] scaleKeys;

    public final double slowDown;
    public final float gravity;
    public final double randomX;
    public final double randomY;
    public final double randomZ;
    public final double windX;
    public final double windZ;
    public final float rotationSpeed;
    public final boolean fullBright;

    public TCLegacyFXData(
            int maxAge,
            int startParticle,
            int numParticles,
            int particleInc,
            int gridSize,
            boolean loop,
            int layer,
            float redStart,
            float greenStart,
            float blueStart,
            float redEnd,
            float greenEnd,
            float blueEnd,
            float[] alphaKeys,
            float[] scaleKeys,
            double slowDown,
            float gravity,
            double randomX,
            double randomY,
            double randomZ,
            double windX,
            double windZ,
            float rotationSpeed,
            boolean fullBright
    ) {
        this.maxAge = Math.max(1, maxAge);
        this.startParticle = startParticle;
        this.numParticles = Math.max(1, numParticles);
        this.particleInc = Math.max(1, particleInc);
        this.gridSize = Math.max(1, gridSize);
        this.loop = loop;
        this.layer = Math.max(0, layer);

        this.redStart = redStart;
        this.greenStart = greenStart;
        this.blueStart = blueStart;
        this.redEnd = redEnd;
        this.greenEnd = greenEnd;
        this.blueEnd = blueEnd;

        this.alphaKeys = sanitizeKeys(alphaKeys, 1.0F);
        this.scaleKeys = sanitizeKeys(scaleKeys, 1.0F);

        this.slowDown = slowDown;
        this.gravity = gravity;
        this.randomX = randomX;
        this.randomY = randomY;
        this.randomZ = randomZ;
        this.windX = windX;
        this.windZ = windZ;
        this.rotationSpeed = rotationSpeed;
        this.fullBright = fullBright;
    }

    public static TCLegacyFXData wispyMote(int age, float red, float green, float blue, float gravity, float randomAgeFactor) {
        int randomizedAge = Math.max(1, (int) (age + (age / 2.0F) * randomAgeFactor));

        return new TCLegacyFXData(
                randomizedAge,
                512,
                16,
                1,
                64,
                true,
                0,
                red,
                green,
                blue,
                red,
                green,
                blue,
                new float[] { 0.0F, 0.6F, 0.6F, 0.0F },
                new float[] { 1.0F, 0.5F },
                1.0D,
                gravity,
                0.0025D,
                0.0D,
                0.0025D,
                0.001D,
                0.0D,
                0.0F,
                true
        );
    }

    public static TCLegacyFXData generic(
            int age,
            int startParticle,
            int numParticles,
            int particleInc,
            int gridSize,
            boolean loop,
            int layer,
            float red,
            float green,
            float blue,
            float alpha,
            float scale
    ) {
        return new TCLegacyFXData(
                age,
                startParticle,
                numParticles,
                particleInc,
                gridSize,
                loop,
                layer,
                red,
                green,
                blue,
                red,
                green,
                blue,
                new float[] { alpha },
                new float[] { scale },
                1.0D,
                0.0F,
                0.0D,
                0.0D,
                0.0D,
                0.0D,
                0.0D,
                0.0F,
                true
        );
    }

    public TCLegacyFXData withMotion(
            double slowDown,
            float gravity,
            double randomX,
            double randomY,
            double randomZ,
            double windX,
            double windZ
    ) {
        return new TCLegacyFXData(
                this.maxAge,
                this.startParticle,
                this.numParticles,
                this.particleInc,
                this.gridSize,
                this.loop,
                this.layer,
                this.redStart,
                this.greenStart,
                this.blueStart,
                this.redEnd,
                this.greenEnd,
                this.blueEnd,
                this.alphaKeys,
                this.scaleKeys,
                slowDown,
                gravity,
                randomX,
                randomY,
                randomZ,
                windX,
                windZ,
                this.rotationSpeed,
                this.fullBright
        );
    }

    public TCLegacyFXData withAlpha(float... alphaKeys) {
        return new TCLegacyFXData(
                this.maxAge,
                this.startParticle,
                this.numParticles,
                this.particleInc,
                this.gridSize,
                this.loop,
                this.layer,
                this.redStart,
                this.greenStart,
                this.blueStart,
                this.redEnd,
                this.greenEnd,
                this.blueEnd,
                alphaKeys,
                this.scaleKeys,
                this.slowDown,
                this.gravity,
                this.randomX,
                this.randomY,
                this.randomZ,
                this.windX,
                this.windZ,
                this.rotationSpeed,
                this.fullBright
        );
    }

    public TCLegacyFXData withScale(float... scaleKeys) {
        return new TCLegacyFXData(
                this.maxAge,
                this.startParticle,
                this.numParticles,
                this.particleInc,
                this.gridSize,
                this.loop,
                this.layer,
                this.redStart,
                this.greenStart,
                this.blueStart,
                this.redEnd,
                this.greenEnd,
                this.blueEnd,
                this.alphaKeys,
                scaleKeys,
                this.slowDown,
                this.gravity,
                this.randomX,
                this.randomY,
                this.randomZ,
                this.windX,
                this.windZ,
                this.rotationSpeed,
                this.fullBright
        );
    }

    public TCLegacyFXData withRotation(float rotationSpeed) {
        return new TCLegacyFXData(
                this.maxAge,
                this.startParticle,
                this.numParticles,
                this.particleInc,
                this.gridSize,
                this.loop,
                this.layer,
                this.redStart,
                this.greenStart,
                this.blueStart,
                this.redEnd,
                this.greenEnd,
                this.blueEnd,
                this.alphaKeys,
                this.scaleKeys,
                this.slowDown,
                this.gravity,
                this.randomX,
                this.randomY,
                this.randomZ,
                this.windX,
                this.windZ,
                rotationSpeed,
                this.fullBright
        );
    }

    public static float sampleKeys(float[] keys, int age, int maxAge) {
        if (keys == null || keys.length == 0) {
            return 0.0F;
        }

        if (keys.length == 1 || maxAge <= 1) {
            return keys[0];
        }

        float progress = Math.max(0.0F, Math.min(1.0F, age / (float) Math.max(1, maxAge - 1)));
        float scaled = progress * (keys.length - 1);
        int index = Math.min(keys.length - 2, (int) scaled);
        float local = scaled - index;

        return keys[index] + (keys[index + 1] - keys[index]) * local;
    }

    private static float[] sanitizeKeys(float[] keys, float fallback) {
        if (keys == null || keys.length == 0) {
            return new float[] { fallback };
        }

        return Arrays.copyOf(keys, keys.length);
    }
}
~~~

## Current TCLegacyFXGeneric

~~~java
package thaumcraft.client.fx.legacy;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import thaumcraft.common.lib.fx.TCLegacyFXData;

public final class TCLegacyFXGeneric {
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
        float red = this.data.redStart + (this.data.redEnd - this.data.redStart) * progress;
        float green = this.data.greenStart + (this.data.greenEnd - this.data.greenStart) * progress;
        float blue = this.data.blueStart + (this.data.blueEnd - this.data.blueStart) * progress;

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

    private int getCurrentFrame() {
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
~~~

## Current TCLegacyParticleEngine

~~~java
package thaumcraft.client.fx.legacy;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.fx.TCLegacyFXData;

public final class TCLegacyParticleEngine {
    private static final int MAX_LAYERS = 4;

    private static final ResourceLocation PARTICLE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/particles.png");

    private static final List<TCLegacyFXGeneric>[] EFFECTS = createLayerLists();
    private static final List<TCLegacyFXGeneric> PENDING = new ArrayList<>();
    private static final Random RANDOM = new Random();

    private TCLegacyParticleEngine() {
    }

    @SuppressWarnings("unchecked")
    private static List<TCLegacyFXGeneric>[] createLayerLists() {
        List<TCLegacyFXGeneric>[] lists = new List[MAX_LAYERS];

        for (int i = 0; i < lists.length; i++) {
            lists[i] = new ArrayList<>();
        }

        return lists;
    }

    public static void clear() {
        for (List<TCLegacyFXGeneric> layer : EFFECTS) {
            layer.clear();
        }

        PENDING.clear();
    }

    public static void addEffect(
            Level level,
            TCLegacyFXData data,
            double x,
            double y,
            double z,
            double motionX,
            double motionY,
            double motionZ
    ) {
        if (!level.isClientSide()) {
            return;
        }

        PENDING.add(new TCLegacyFXGeneric(data, x, y, z, motionX, motionY, motionZ));
    }

    public static void tick() {
        flushPending();

        for (List<TCLegacyFXGeneric> layer : EFFECTS) {
            Iterator<TCLegacyFXGeneric> iterator = layer.iterator();

            while (iterator.hasNext()) {
                TCLegacyFXGeneric effect = iterator.next();
                effect.tick(RANDOM);

                if (effect.isRemoved()) {
                    iterator.remove();
                }
            }
        }
    }

    public static void render(Camera camera, float partialTicks) {
        flushPending();

        if (getActiveEffectCount() == 0) {
            return;
        }

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        // Use the original Thaumcraft particles.png unchanged.
        // POSITION_TEX_COLOR is closer to old FXGeneric: position + UV + vertex color.
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, PARTICLE_TEXTURE);

        for (List<TCLegacyFXGeneric> layer : EFFECTS) {
            renderLayer(layer, camera, partialTicks);
        }

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }

    private static void renderLayer(List<TCLegacyFXGeneric> layer, Camera camera, float partialTicks) {
        boolean hasRenderableEffect = false;

        for (TCLegacyFXGeneric effect : layer) {
            if (effect.canRender()) {
                hasRenderableEffect = true;
                break;
            }
        }

        if (!hasRenderableEffect) {
            return;
        }

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (TCLegacyFXGeneric effect : layer) {
            effect.render(buffer, camera, partialTicks);
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void flushPending() {
        if (PENDING.isEmpty()) {
            return;
        }

        for (TCLegacyFXGeneric effect : PENDING) {
            int layer = clampLayer(effect.getLayer());
            EFFECTS[layer].add(effect);
        }

        PENDING.clear();
    }

    private static int clampLayer(int layer) {
        if (layer < 0) {
            return 0;
        }

        return Math.min(layer, MAX_LAYERS - 1);
    }

    public static int getPendingEffectCount() {
        return PENDING.size();
    }

    public static int getActiveEffectCount() {
        int count = 0;

        for (List<TCLegacyFXGeneric> layer : EFFECTS) {
            count += layer.size();
        }

        return count;
    }

    public static int getRenderableEffectCount() {
        int count = 0;

        for (List<TCLegacyFXGeneric> layer : EFFECTS) {
            for (TCLegacyFXGeneric effect : layer) {
                if (effect.canRender()) {
                    count++;
                }
            }
        }

        return count;
    }

    public static String getDebugStats() {
        return "pending=" + getPendingEffectCount()
                + ", active=" + getActiveEffectCount()
                + ", renderable=" + getRenderableEffectCount();
    }
}
~~~

## Current TCPlantBlock

~~~java
package thaumcraft.common.blocks.world.plants;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.lib.fx.TCFXDispatcher;

public class TCPlantBlock extends BushBlock {
    public static final MapCodec<TCPlantBlock> CODEC = simpleCodec(properties ->
            new TCPlantBlock(Kind.SAPLING, properties)
    );

    private static final VoxelShape SAPLING_SHAPE = Block.box(1.6D, 0.0D, 1.6D, 14.4D, 12.8D, 14.4D);
    private static final VoxelShape SHIMMERLEAF_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
    private static final VoxelShape CINDERPEARL_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);
    private static final VoxelShape VISHROOM_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 10.0D, 13.0D);

    public enum Kind {
        SAPLING,
        SHIMMERLEAF,
        CINDERPEARL,
        VISHROOM
    }

    private final Kind kind;

    public TCPlantBlock(Kind kind, BlockBehaviour.Properties properties) {
        super(properties);
        this.kind = kind;
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (kind) {
            case SAPLING -> SAPLING_SHAPE;
            case SHIMMERLEAF -> SHIMMERLEAF_SHAPE;
            case CINDERPEARL -> CINDERPEARL_SHAPE;
            case VISHROOM -> VISHROOM_SHAPE;
        };
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return switch (kind) {
            case SHIMMERLEAF -> isLegacyGrassOrDirt(state);

            case CINDERPEARL -> state.is(Blocks.SAND)
                    || state.is(Blocks.RED_SAND)
                    || isLegacyDirt(state)
                    || state.is(Blocks.TERRACOTTA)
                    || state.is(BlockTags.TERRACOTTA);

            case VISHROOM -> isLegacyGrassOrDirt(state)
                    || state.is(Blocks.PODZOL)
                    || state.is(Blocks.MOSS_BLOCK)
                    || state.is(Blocks.MYCELIUM)
                    || state.is(Blocks.STONE)
                    || state.is(Blocks.DEEPSLATE)
                    || state.is(Blocks.TUFF)
                    || state.is(BlockTags.BASE_STONE_OVERWORLD);

            case SAPLING -> isLegacyGrassOrDirt(state)
                    || state.is(Blocks.PODZOL)
                    || state.is(Blocks.MOSS_BLOCK);
        };
    }

    private static boolean isLegacyGrassOrDirt(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || isLegacyDirt(state);
    }

    private static boolean isLegacyDirt(BlockState state) {
        return state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.ROOTED_DIRT);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (kind == Kind.CINDERPEARL) {
            spawnCinderpearlParticles(level, pos, random);
            return;
        }

        if (kind == Kind.SHIMMERLEAF) {
            TCFXDispatcher.drawShimmerleafMote(level, pos, random);
            return;
        }

        if (kind == Kind.VISHROOM) {
            TCFXDispatcher.drawVishroomMote(level, pos, random);
        }
    }

    private static void spawnCinderpearlParticles(Level level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide() || !random.nextBoolean()) {
            return;
        }

        double x = pos.getX() + 0.5D + (random.nextFloat() - random.nextFloat()) * 0.1D;
        double y = pos.getY() + 0.6D + (random.nextFloat() - random.nextFloat()) * 0.1D;
        double z = pos.getZ() + 0.5D + (random.nextFloat() - random.nextFloat()) * 0.1D;

        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        if (kind == Kind.VISHROOM
                && !level.isClientSide()
                && entity instanceof LivingEntity living
                && level.random.nextInt(5) == 0) {
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        }
    }
}
~~~
