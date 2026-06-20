package thaumcraft.common.crafting.infusion;

import net.minecraft.util.RandomSource;

/** Injectable view of the legacy matrix RNG stream for deterministic parity audits. */
public interface TCInfusionRandomSource {
    float nextFloat();

    int nextInt(int bound);

    static TCInfusionRandomSource wrap(RandomSource random) {
        return new TCInfusionRandomSource() {
            @Override
            public float nextFloat() {
                return random.nextFloat();
            }

            @Override
            public int nextInt(int bound) {
                return random.nextInt(bound);
            }
        };
    }
}
