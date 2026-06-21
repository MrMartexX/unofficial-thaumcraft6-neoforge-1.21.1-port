package thaumcraft.common.lib.fx;

import java.util.Arrays;

public final class TCLegacyFXData {
    public static final double LEGACY_DEFAULT_SLOWDOWN = 0.9800000190734863D;

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
    public final int[] finalFrames;

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
        this(
                maxAge,
                startParticle,
                numParticles,
                particleInc,
                gridSize,
                loop,
                layer,
                redStart,
                greenStart,
                blueStart,
                redEnd,
                greenEnd,
                blueEnd,
                alphaKeys,
                scaleKeys,
                slowDown,
                gravity,
                randomX,
                randomY,
                randomZ,
                windX,
                windZ,
                rotationSpeed,
                fullBright,
                new int[0]
        );
    }

    private TCLegacyFXData(
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
            boolean fullBright,
            int[] finalFrames
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
        this.finalFrames = finalFrames == null ? new int[0] : Arrays.copyOf(finalFrames, finalFrames.length);
    }

    public static TCLegacyFXData wispyMote(int age, float red, float green, float blue, float gravity, float randomAgeFactor) {
        int randomizedAge = Math.max(1, (int) (age + age / 2.0F * randomAgeFactor));

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
                LEGACY_DEFAULT_SLOWDOWN,
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
                LEGACY_DEFAULT_SLOWDOWN,
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

    public static TCLegacyFXData simpleSparkle(
            int age,
            int startParticle,
            float red,
            float green,
            float blue,
            float scale,
            float gravity
    ) {
        return generic(
                age,
                startParticle,
                16,
                1,
                64,
                true,
                0,
                red,
                green,
                blue,
                1.0F,
                scale
        ).withAlpha(0.0F, 1.0F, 1.0F, 0.0F)
                .withScale(scale, scale * 2.0F)
                .withMotion(
                        1.0D,
                        gravity,
                        5.0E-4D,
                        0.001D,
                        5.0E-4D,
                        5.0E-4D,
                        0.0D
                );
    }

    public static TCLegacyFXData blockRune(
            int age,
            int runeParticle,
            float red,
            float green,
            float blue,
            float scale,
            float gravity
    ) {
        return generic(
                age * 3,
                runeParticle,
                1,
                1,
                64,
                false,
                0,
                red,
                green,
                blue,
                0.5F,
                scale
        ).withAlpha(0.0F, 0.5F, 0.4F, 0.0F)
                .withMotion(
                        LEGACY_DEFAULT_SLOWDOWN,
                        gravity,
                        0.0D,
                        0.0D,
                        0.0D,
                        0.0D,
                        0.0D
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
                this.fullBright,
                this.finalFrames
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
                this.fullBright,
                this.finalFrames
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
                this.fullBright,
                this.finalFrames
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
                this.fullBright,
                this.finalFrames
        );
    }

    public TCLegacyFXData withFinalFrames(int... finalFrames) {
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
                this.rotationSpeed,
                this.fullBright,
                finalFrames
        );
    }

    public static float sampleKeys(float[] keys, int age, int maxAge) {
        if (keys == null || keys.length == 0) {
            return 0.0F;
        }

        if (keys.length == 1 || maxAge <= 1) {
            return keys[0];
        }

        float progress = Math.max(0.0F, Math.min(1.0F, age / (float) Math.max(1, maxAge)));
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
