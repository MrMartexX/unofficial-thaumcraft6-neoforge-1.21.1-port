package thaumcraft.common.lib.fx;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

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

    public static void drawWispParticles(
            Level level,
            double x,
            double y,
            double z,
            double motionX,
            double motionY,
            double motionZ,
            int color,
            int delay
    ) {
        if (!level.isClientSide()) {
            return;
        }

        RandomSource random = level.random;
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float scale = 1.0F + random.nextFloat() * 0.25F;
        TCLegacyFXData data = TCLegacyFXData.generic(
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
                0.5F,
                scale
        ).withScale(scale, 0.05F)
                .withMotion(
                        TCLegacyFXData.LEGACY_DEFAULT_SLOWDOWN,
                        0.0F,
                        0.0025D,
                        0.0D,
                        0.0025D,
                        2.5E-4D,
                        0.0D
                );

        drawLegacyFXWithDelay(level, data, x, y, z, motionX, motionY, motionZ, delay);
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

    public static void drawVentParticles(Level level, double x, double y, double z, double motionX, double motionY, double motionZ, int color) {
        if (!level.isClientSide()) {
            return;
        }

        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        TCLegacyFXData data = TCLegacyFXData.generic(
                        16,
                        1,
                        5,
                        1,
                        64,
                        false,
                        1,
                        red,
                        green,
                        blue,
                        0.4F,
                        0.05F
                )
                .withScale(0.05F, 1.0F)
                .withAlpha(0.4F, 0.0F)
                .withMotion(0.85D, 0.0025F, 0.0075D * 5.0D, 0.0075D * 5.0D, 0.0075D * 5.0D, 0.0D, 0.0D);

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

    public static void crucibleBubble(Level level, double x, double y, double z, float red, float green, float blue) {
        if (!level.isClientSide()) {
            return;
        }
        RandomSource random = level.random;
        float scale = random.nextFloat() * 0.3F + 0.3F;
        TCLegacyFXData data = TCLegacyFXData.generic(
                15 + random.nextInt(10), 64, 1, 1, 64, false, 0,
                red, green, blue, 1.0F, scale
        ).withMotion(
                TCLegacyFXData.LEGACY_DEFAULT_SLOWDOWN,
                -0.001F,
                0.002D,
                0.002D,
                0.002D,
                0.0D,
                0.0D
        ).withFinalFrames(65, 66, 66);
        drawLegacyFX(level, data, x, y, z, 0.0D, 0.0D, 0.0D);
    }

    public static void crucibleBoil(
            Level level,
            BlockPos position,
            float fluidHeight,
            AspectList aspects,
            int intensity
    ) {
        if (!level.isClientSide()) {
            return;
        }
        RandomSource random = level.random;
        Aspect[] present = aspects == null ? new Aspect[0] : aspects.getAspects();
        for (int index = 0; index < 2; index++) {
            float red = 1.0F;
            float green = 1.0F;
            float blue = 1.0F;
            if (present.length > 0) {
                int color = present[random.nextInt(present.length)].getColor();
                red = (color >> 16 & 0xFF) / 255.0F;
                green = (color >> 8 & 0xFF) / 255.0F;
                blue = (color & 0xFF) / 255.0F;
            }
            int age = (int) (7.0D + 8.0D / (random.nextDouble() * 0.8D + 0.2D));
            float scale = random.nextFloat() * 0.3F + 0.2F;
            TCLegacyFXData data = TCLegacyFXData.generic(
                    age, 64, 1, 1, 64, false, 0,
                    red, green, blue, 1.0F, scale
            ).withMotion(
                    TCLegacyFXData.LEGACY_DEFAULT_SLOWDOWN,
                    -0.025F * intensity,
                    0.001D,
                    0.001D,
                    0.001D,
                    0.0D,
                    0.0D
            ).withFinalFrames(65, 66);
            drawLegacyFX(
                    level,
                    data,
                    position.getX() + 0.2D + random.nextFloat() * 0.6D,
                    position.getY() + 0.1D + fluidHeight,
                    position.getZ() + 0.2D + random.nextFloat() * 0.6D,
                    0.0D,
                    0.002D,
                    0.0D
            );
        }
    }

    public static void crucibleFroth(Level level, double x, double y, double z) {
        if (!level.isClientSide()) {
            return;
        }
        RandomSource random = level.random;
        float scale = random.nextFloat() * 0.2F + 0.2F;
        TCLegacyFXData data = TCLegacyFXData.generic(
                4 + random.nextInt(3), 64, 1, 1, 64, false, 0,
                0.5F, 0.5F, 0.7F, 1.0F, scale
        ).withMotion(
                TCLegacyFXData.LEGACY_DEFAULT_SLOWDOWN,
                0.1F,
                0.001D,
                0.001D,
                0.001D,
                0.0D,
                0.0D
        ).withFinalFrames(65, 66);
        drawLegacyFX(level, data, x, y, z, 0.0D, 0.0D, 0.0D);
    }

    public static void crucibleFrothDown(Level level, double x, double y, double z) {
        if (!level.isClientSide()) {
            return;
        }
        RandomSource random = level.random;
        float scale = random.nextFloat() * 0.2F + 0.4F;
        TCLegacyFXData data = TCLegacyFXData.generic(
                12 + random.nextInt(12), 73, 1, 1, 64, false, 1,
                0.25F, 0.0F, 0.75F, 0.8F, scale
        ).withMotion(
                TCLegacyFXData.LEGACY_DEFAULT_SLOWDOWN,
                0.05F,
                0.001D,
                0.001D,
                0.001D,
                0.0D,
                0.0D
        ).withFinalFrames(65, 66);
        drawLegacyFX(level, data, x, y, z, 0.0D, 0.0D, 0.0D);
    }

    public static void drawCrucibleBamf(Level level, double x, double y, double z) {
        if (!level.isClientSide()) {
            return;
        }
        RandomSource random = level.random;
        int puffs = 8 + random.nextInt(3);
        for (int index = 0; index < puffs; index++) {
            double velocityX = signed(random, 0.05F, 0.05F);
            double velocityY = signed(random, 0.05F, 0.05F) + 0.1D;
            double velocityZ = signed(random, 0.05F, 0.05F);
            float red = Mth.clamp(0.5F * (1.0F + (float) random.nextGaussian() * 0.1F), 0.0F, 1.0F);
            float green = Mth.clamp(0.1F * (1.0F + (float) random.nextGaussian() * 0.1F), 0.0F, 1.0F);
            float blue = Mth.clamp(0.6F * (1.0F + (float) random.nextGaussian() * 0.1F), 0.0F, 1.0F);
            TCLegacyFXData data = TCLegacyFXData.generic(
                    20 + random.nextInt(15), 123, 5, 1, 16, true, 1,
                    red, green, blue, 1.0F, 3.0F
            ).withAlpha(1.0F, 0.1F)
                    .withScale(3.0F, 4.0F + random.nextFloat() * 3.0F)
                    .withMotion(0.7D, 0.0F, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D)
                    .withRotation(random.nextBoolean() ? -1.0F : 1.0F);
            drawLegacyFX(
                    level,
                    data,
                    x + velocityX * 2.0D,
                    y + velocityY * 2.0D,
                    z + velocityZ * 2.0D,
                    velocityX / 2.0D,
                    velocityY / 2.0D,
                    velocityZ / 2.0D
            );
        }
        for (int index = 0; index < 2 + random.nextInt(3); index++) {
            double velocityX = signed(random, 0.025F, 0.025F);
            double velocityY = signed(random, 0.025F, 0.025F);
            double velocityZ = signed(random, 0.025F, 0.025F);
            drawWispyMotes(
                    level,
                    x + velocityX * 2.0D,
                    y + velocityY * 2.0D,
                    z + velocityZ * 2.0D,
                    velocityX,
                    velocityY,
                    velocityZ,
                    15 + random.nextInt(10),
                    -0.01F
            );
        }
        float flareScale = 10.0F + random.nextFloat() * 2.0F;
        TCLegacyFXData flare = TCLegacyFXData.generic(
                10 + random.nextInt(5), 77, 1, 1, 16, false, 0,
                1.0F, 0.9F, 1.0F, 1.0F, flareScale
        ).withAlpha(1.0F, 0.0F)
                .withScale(flareScale, 0.0F)
                .withRotation((float) random.nextGaussian());
        drawLegacyFX(level, flare, x, y, z, 0.0D, 0.0D, 0.0D);
    }

    private static double signed(RandomSource random, float base, float spread) {
        return (base + random.nextFloat() * spread) * (random.nextBoolean() ? -1.0D : 1.0D);
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

    public static void essentiaTrailFx(
            Level level,
            BlockPos source,
            BlockPos destination,
            int count,
            int color,
            float scale,
            int extension
    ) {
        if (!level.isClientSide()) {
            return;
        }
        Vec3 start = Vec3.atCenterOf(source);
        Vec3 end = Vec3.atCenterOf(destination);
        Vec3 delta = end.subtract(start);
        int samples = Math.max(8, Math.min(24, (int) Math.ceil(delta.length() * 2.0D)));
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        for (int index = 0; index <= samples; index++) {
            double progress = index / (double) samples;
            double taper = Math.sin(progress * Math.PI);
            double wave = Math.sin(count * 0.25D + progress * Math.PI * 4.0D) * 0.04D * taper;
            Vec3 point = start.add(delta.scale(progress)).add(wave, wave * 0.5D, -wave);
            TCLegacyFXData data = TCLegacyFXData.generic(
                    Math.max(4, extension + 4),
                    512,
                    16,
                    1,
                    64,
                    true,
                    1,
                    red,
                    green,
                    blue,
                    0.55F,
                    Math.max(0.15F, scale * (float) taper * 4.0F)
            ).withAlpha(0.55F, 0.0F);
            drawLegacyFX(level, data, point.x(), point.y(), point.z(), 0.0D, 0.0D, 0.0D);
        }
    }

    public static void drawInfusionPedestalParticles(
            Level level,
            BlockPos pedestalPos,
            BlockPos matrixPos,
            ItemStack stack
    ) {
        if (!level.isClientSide() || stack == null || stack.isEmpty()) {
            return;
        }
        RandomSource random = level.random;
        double x = pedestalPos.getX() + 0.4D + random.nextFloat() * 0.2D;
        double y = pedestalPos.getY() + 1.23D + random.nextFloat() * 0.2D;
        double z = pedestalPos.getZ() + 0.4D + random.nextFloat() * 0.2D;
        Vec3 delta = new Vec3(
                matrixPos.getX() + 0.5D - x,
                matrixPos.getY() - 0.5D - y,
                matrixPos.getZ() + 0.5D - z
        );
        double distance = Math.max(0.001D, delta.length());
        Vec3 motion = delta.scale(Math.min(0.25D, distance / 15.0D) / distance);
        float red = 0.4F + random.nextFloat() * 0.2F;
        float green = 0.2F;
        float blue = 0.6F + random.nextFloat() * 0.3F;
        TCLegacyFXData data = TCLegacyFXData.generic(
                Math.max(4, (int) (distance * 10.0D)),
                24,
                4,
                1,
                64,
                true,
                0,
                red,
                green,
                blue,
                0.3F,
                0.5F + random.nextFloat() * 0.5F
        ).withAlpha(0.3F, 0.3F, 0.0F)
                .withMotion(0.985D, 0.0F, 0.005D, 0.005D, 0.005D, 0.0D, 0.0D);
        drawLegacyFX(level, data, x, y, z, motion.x(), motion.y(), motion.z());
    }
}
