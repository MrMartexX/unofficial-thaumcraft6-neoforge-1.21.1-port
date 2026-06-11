package thaumcraft.common.lib.fx;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.AABB;

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
                double motionZ,
                int delay
        );
    }

    private static ClientSink clientSink = (level, data, x, y, z, motionX, motionY, motionZ, delay) -> {
    };

    private TCFXDispatcher() {
    }

    public static void setClientSink(ClientSink sink) {
        clientSink = sink == null
                ? (level, data, x, y, z, motionX, motionY, motionZ, delay) -> {
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
        drawLegacyFXWithDelay(level, data, x, y, z, motionX, motionY, motionZ, 0);
    }

    public static void drawLegacyFXWithDelay(
            Level level,
            TCLegacyFXData data,
            double x,
            double y,
            double z,
            double motionX,
            double motionY,
            double motionZ,
            int delay
    ) {
        if (!level.isClientSide()) {
            return;
        }

        clientSink.addLegacyFX(level, data, x, y, z, motionX, motionY, motionZ, Math.max(0, delay));
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

    public static void scanHighlight(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            return;
        }

        VoxelShape shape = level.getBlockState(pos).getShape(level, pos);
        AABB box = shape.isEmpty() ? new AABB(pos) : shape.bounds().move(pos);
        scanHighlight(level, box);
    }

    public static void scanHighlight(Level level, AABB box) {
        if (!level.isClientSide()) {
            return;
        }

        RandomSource random = level.random;
        int count = Mth.ceil(((box.getXsize() + box.getYsize() + box.getZsize()) / 3.0D) * 2.0D);
        double centerX = (box.minX + box.maxX) / 2.0D;
        double centerY = (box.minY + box.maxY) / 2.0D;
        double centerZ = (box.minZ + box.maxZ) / 2.0D;

        for (Direction face : Direction.values()) {
            double baseX = 0.5D + face.getStepX() * 0.51D;
            double baseY = 0.5D + face.getStepY() * 0.51D;
            double baseZ = 0.5D + face.getStepZ() * 0.51D;

            for (int index = 0; index < count * 2; index++) {
                double x = baseX + random.nextGaussian() * box.getXsize();
                double y = baseY + random.nextGaussian() * box.getYsize();
                double z = baseZ + random.nextGaussian() * box.getZsize();

                x = Mth.clamp(x, box.minX - centerX, box.maxX - centerX);
                y = Mth.clamp(y, box.minY - centerY, box.maxY - centerY);
                z = Mth.clamp(z, box.minZ - centerZ, box.maxZ - centerZ);

                float red = Mth.nextInt(random, 16, 32) / 255.0F;
                float green = Mth.nextInt(random, 132, 165) / 255.0F;
                float blue = Mth.nextInt(random, 223, 239) / 255.0F;

                drawSimpleSparkle(
                        level,
                        centerX + x,
                        centerY + y,
                        centerZ + z,
                        0.0D,
                        0.0D,
                        0.0D,
                        0.4F + (float) random.nextGaussian() * 0.1F,
                        red,
                        green,
                        blue,
                        random.nextInt(10),
                        1.0F,
                        0.0F,
                        4
                );
            }
        }
    }

    public static void blockRunes(Level level, double x, double y, double z, float red, float green, float blue, int age, float gravity) {
        if (!level.isClientSide()) {
            return;
        }

        RandomSource random = level.random;
        TCLegacyFXData data = TCLegacyFXData.blockRune(
                age,
                224 + random.nextInt(16),
                red == 0.0F ? 1.0F : red,
                green,
                blue,
                3.0F + (float) random.nextGaussian() * 0.3F,
                gravity
        );

        drawLegacyFX(
                level,
                data,
                x + 0.5D + random.nextFloat() * 0.2D,
                y + 0.2D + random.nextFloat() * 0.6D,
                z + 0.5D - 0.3D + random.nextFloat() * 0.6D,
                0.0D,
                0.0D,
                0.0D
        );
    }

    public static void drawSimpleSparkle(
            Level level,
            double x,
            double y,
            double z,
            double motionX,
            double motionY,
            double motionZ,
            float scale,
            float red,
            float green,
            float blue,
            int delay,
            float decay,
            float gravity,
            int baseAge
    ) {
        if (!level.isClientSide()) {
            return;
        }

        RandomSource random = level.random;
        boolean sparkle = random.nextFloat() < 0.2F;
        int age = baseAge * 4 + random.nextInt(Math.max(1, baseAge));
        TCLegacyFXData data = TCLegacyFXData.simpleSparkle(
                age,
                sparkle ? 320 : 512,
                red,
                green,
                blue,
                scale,
                gravity
        ).withMotion(
                decay,
                gravity,
                5.0E-4D,
                0.001D,
                5.0E-4D,
                5.0E-4D,
                0.0D
        );

        drawLegacyFXWithDelay(level, data, x, y, z, motionX, motionY, motionZ, delay);
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
