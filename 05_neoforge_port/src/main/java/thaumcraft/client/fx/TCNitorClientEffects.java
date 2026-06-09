package thaumcraft.client.fx;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.client.fx.legacy.TCLegacyParticleEngine;
import thaumcraft.common.lib.fx.TCLegacyFXData;
import thaumcraft.common.registry.TCBlocks;

/**
 * TC6-inspired Nitor client FX. The legacy 1.12 implementation spawned
 * drawNitorFlames every client tick and drawNitorCore every 10 ticks.
 */
public final class TCNitorClientEffects {
    private static final int YELLOW_NITOR_COLOR = 0xFFFF55;

    private TCNitorClientEffects() {
    }

    public static void tick(Level level, BlockPos pos, BlockState state, int count) {
        RandomSource random = level.random;

        double x = pos.getX() + 0.5D + random.nextGaussian() * 0.025D;
        double y = pos.getY() + 0.45D + random.nextGaussian() * 0.025D;
        double z = pos.getZ() + 0.5D + random.nextGaussian() * 0.025D;

        double motionX = random.nextGaussian() * 0.0025D;
        double motionY = random.nextFloat() * 0.06D;
        double motionZ = random.nextGaussian() * 0.0025D;

        spawnFlame(level, x, y, z, motionX, motionY, motionZ, nitorColor(state));

        if (count % 10 == 0) {
            spawnCore(level, pos.getX() + 0.5D, pos.getY() + 0.49D, pos.getZ() + 0.5D);
        }
    }
    private static int nitorColor(BlockState state) {
        var block = state.getBlock();
        if (block == TCBlocks.NITOR_BLACK.get()) return 0x1D1D21;
        if (block == TCBlocks.NITOR_BLUE.get()) return 0x3C44AA;
        if (block == TCBlocks.NITOR_BROWN.get()) return 0x835432;
        if (block == TCBlocks.NITOR_CYAN.get()) return 0x169C9C;
        if (block == TCBlocks.NITOR_GRAY.get()) return 0x474F52;
        if (block == TCBlocks.NITOR_GREEN.get()) return 0x5E7C16;
        if (block == TCBlocks.NITOR_LIGHTBLUE.get()) return 0x3AB3DA;
        if (block == TCBlocks.NITOR_LIME.get()) return 0x80C71F;
        if (block == TCBlocks.NITOR_MAGENTA.get()) return 0xC74EBD;
        if (block == TCBlocks.NITOR_ORANGE.get()) return 0xF9801D;
        if (block == TCBlocks.NITOR_PINK.get()) return 0xF38BAA;
        if (block == TCBlocks.NITOR_PURPLE.get()) return 0x8932B8;
        if (block == TCBlocks.NITOR_RED.get()) return 0xB02E26;
        if (block == TCBlocks.NITOR_SILVER.get()) return 0x9D9D97;
        if (block == TCBlocks.NITOR_WHITE.get()) return 0xF9FFFE;
        return YELLOW_NITOR_COLOR;
    }
    private static void spawnFlame(
            Level level,
            double x,
            double y,
            double z,
            double motionX,
            double motionY,
            double motionZ,
            int color
    ) {
        RandomSource random = level.random;
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        TCLegacyFXData data = new TCLegacyFXData(
                10 + random.nextInt(5),
                264,
                8,
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
                new float[]{0.0F, 0.66F, 0.55F, 0.0F},
                new float[]{3.0F + random.nextFloat(), 0.05F},
                TCLegacyFXData.LEGACY_DEFAULT_SLOWDOWN,
                0.0F,
                0.0025D,
                0.0D,
                0.0025D,
                0.0D,
                0.0D,
                0.0F,
                true
        );

        TCLegacyParticleEngine.addEffect(level, data, x, y, z, motionX, motionY, motionZ, 0);
    }

    private static void spawnCore(Level level, double x, double y, double z) {
        RandomSource random = level.random;

        TCLegacyFXData data = new TCLegacyFXData(
                10,
                457,
                1,
                1,
                64,
                false,
                1,
                1.0F,
                1.0F,
                1.0F,
                1.0F,
                1.0F,
                1.0F,
                new float[]{1.0F, 0.0F},
                new float[]{1.0F, 1.0F + (float) random.nextGaussian() * 0.1F, 1.0F},
                TCLegacyFXData.LEGACY_DEFAULT_SLOWDOWN,
                0.0F,
                2.0E-4D,
                2.0E-4D,
                2.0E-4D,
                0.0D,
                0.0D,
                0.0F,
                true
        );

        TCLegacyParticleEngine.addEffect(level, data, x, y, z, 0.0D, 0.0D, 0.0D, 0);
    }
}
