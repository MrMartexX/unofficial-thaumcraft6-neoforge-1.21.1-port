package thaumcraft.common.lib.fx;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public final class TCFXDispatcher {
    @FunctionalInterface
    public interface ClientSink {
        void drawWispyMotes(
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
        );
    }

    private static ClientSink clientSink = (level, x, y, z, motionX, motionY, motionZ, age, red, green, blue, gravity) -> {
    };

    private TCFXDispatcher() {
    }

    public static void setClientSink(ClientSink sink) {
        clientSink = sink == null
                ? (level, x, y, z, motionX, motionY, motionZ, age, red, green, blue, gravity) -> {
                }
                : sink;
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

        clientSink.drawWispyMotes(level, x, y, z, motionX, motionY, motionZ, age, red, green, blue, gravity);
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