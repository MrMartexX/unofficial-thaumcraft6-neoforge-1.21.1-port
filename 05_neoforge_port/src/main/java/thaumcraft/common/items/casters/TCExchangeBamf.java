package thaumcraft.common.items.casters;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import thaumcraft.common.lib.fx.TCLegacyFXData;

/** PacketFXBlockBamf: Exchange's sound=true, flair=false, null-face branch. */
public final class TCExchangeBamf {
    public static final int COLOR = 8038177;
    private static java.util.function.Consumer<TCExchangeFXPayload> clientSink = payload -> {};
    private TCExchangeBamf() {}
    public static void setClientSink(java.util.function.Consumer<TCExchangeFXPayload> sink) { clientSink = sink; }
    public static void accept(TCExchangeFXPayload payload) { clientSink.accept(payload); }
    public record Particle(TCLegacyFXData data, double x, double y, double z, double vx, double vy, double vz,
                           float rotationStart, float rotationDegrees, float yaw, float pitch) {}
    public record Burst(float soundPitch, List<Particle> particles) {}

    public static Burst generate(RandomSource random) {
        float soundPitch = 1 + (float)random.nextGaussian() * .05F;
        float r = ((COLOR >> 16) & 255) / 255F, g = ((COLOR >> 8) & 255) / 255F, b = (COLOR & 255) / 255F;
        var particles = new ArrayList<Particle>();
        // The original loop re-samples its bound at every condition evaluation.
        for (int i = 0; i < 8 + random.nextInt(3); i++) {
            double vx = signed(random, .05F, .05F), vy = signed(random, .05F, .05F), vz = signed(random, .05F, .05F);
            int age = 20 + random.nextInt(15);
            float red = tint(random, r), green = tint(random, g), blue = tint(random, b);
            float scaleEnd = 4 + random.nextFloat() * 3;
            float start = random.nextFloat(), rotation = random.nextBoolean() ? -1 : 1;
            var data = data(age, 123, 5, 1, red, green, blue, red, green, blue,
                    new float[]{1, .1F}, new float[]{3, scaleEnd}, .7);
            particles.add(new Particle(data, vx * 2, vy * 2, vz * 2, vx / 2, vy / 2, vz / 2, start, rotation, Float.NaN, Float.NaN));
        }
        for (int seed = 0; seed < random.nextInt(3); seed++) {
            float red = (.9F + random.nextFloat() * .1F + r) / 2, green = (.1F + g) / 2;
            float blue = (.5F + random.nextFloat() * .1F + b) / 2;
            double vx = signed(random, .0025F, .005F), vy = signed(random, .0025F, .005F), vz = signed(random, .0025F, .005F);
            float yaw = Float.NaN, pitch = Float.NaN;
            if (seed > 0 && random.nextBoolean()) { yaw = 90 * (float)random.nextGaussian(); pitch = 90 * (float)random.nextGaussian(); }
            int age = 25 + random.nextInt(20 + 20 * seed);
            int cell = 60 + random.nextInt(4);
            float scaleEnd = 10 + random.nextFloat() * 4;
            float start = random.nextFloat();
            float rotation = random.nextBoolean() ? -2 - random.nextFloat() * 2 : 2 + random.nextFloat() * 2;
            var data = data(age, cell, 1, 0, red, green, blue, .1F, 0, .1F,
                    new float[]{.75F, 0}, new float[]{5, scaleEnd}, TCLegacyFXData.LEGACY_DEFAULT_SLOWDOWN);
            particles.add(new Particle(data, vx * 5, vy * 5, vz * 5, vx, vy, vz, start, rotation, yaw, pitch));
        }
        return new Burst(soundPitch, List.copyOf(particles));
    }
    private static double signed(RandomSource random, float base, float spread) { return (base + random.nextFloat() * spread) * (random.nextBoolean() ? -1 : 1); }
    private static float tint(RandomSource random, float value) { return Mth.clamp(value * (1 + (float)random.nextGaussian() * .1F), 0, 1); }
    private static TCLegacyFXData data(int age, int cell, int frames, int layer, float r, float g, float b, float r2, float g2, float b2,
                                       float[] alpha, float[] scale, double slowdown) {
        return new TCLegacyFXData(age, cell, frames, 1, 16, false, layer, r, g, b, r2, g2, b2, alpha, scale,
                slowdown, 0, 0, 0, 0, 0, 0, 0, false);
    }
}
