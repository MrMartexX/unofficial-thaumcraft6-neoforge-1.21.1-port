package thaumcraft.common.golems;

import net.minecraft.util.RandomSource;
import thaumcraft.common.lib.fx.TCLegacyFXData;

/** Original entity status 5..9 atlas cells, lifetimes and motion; shared for audits. */
public final class TCGolemEmotes {
    private TCGolemEmotes() {}
    public record Emote(TCLegacyFXData data, double x, double y, double z) {}
    public static Emote create(byte event, RandomSource random) {
        int cell, frames = 1, age;
        float r = 1, g = 1, b = 1, scale = 2;
        double x = 0, y = 0, z = 0;
        switch (event) {
            case 5 -> { cell = 704 + (random.nextBoolean() ? 0 : 3); frames = 3; age = 6; }
            case 6 -> { cell = 15; age = 10; r = .1F; y = .025; }
            case 7 -> { cell = 640; frames = 10; age = 10; y = .05; }
            case 8 -> { cell = 14; age = 20; b = .1F; y = .01; }
            case 9 -> {
                cell = 13;
                x = random.nextGaussian() * .009999999776482582;
                y = random.nextFloat() * .02; z = random.nextGaussian() * .009999999776482582;
                age = 20 + random.nextInt(20); scale = .3F + random.nextFloat() * .4F;
            }
            default -> throw new IllegalArgumentException("Unknown golem emote " + event);
        }
        return new Emote(new TCLegacyFXData(age, cell, frames, 1, 64, false, 1,
                r, g, b, r, g, b, new float[]{.5F}, new float[]{scale},
                TCLegacyFXData.LEGACY_DEFAULT_SLOWDOWN, 0, 0, 0, 0, 0, 0, 0, false), x, y, z);
    }
}
