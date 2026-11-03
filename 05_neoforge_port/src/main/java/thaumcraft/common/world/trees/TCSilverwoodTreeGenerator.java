package thaumcraft.common.world.trees;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.blocks.world.plants.TCLeavesBlock;

public class TCSilverwoodTreeGenerator {
    private final int minTreeHeight;
    private final int randomTreeHeight;
    private final boolean worldgen;

    public TCSilverwoodTreeGenerator(boolean doBlockNotify, int minTreeHeight, int randomTreeHeight) {
        this.worldgen = !doBlockNotify;
        this.minTreeHeight = minTreeHeight;
        this.randomTreeHeight = randomTreeHeight;
    }

    public boolean generate(WorldGenLevel level, RandomSource random, BlockPos pos) {
        int height = random.nextInt(randomTreeHeight) + minTreeHeight;

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (y < level.getMinBuildHeight() + 1 || y + height + 1 > level.getMaxBuildHeight()) {
            return false;
        }

        boolean clear = true;

        for (int yy = y; yy <= y + 1 + height; ++yy) {
            int spread = 1;

            if (yy == y) {
                spread = 0;
            }

            if (yy >= y + 1 + height - 2) {
                spread = 3;
            }

            for (int xx = x - spread; xx <= x + spread && clear; ++xx) {
                for (int zz = z - spread; zz <= z + spread && clear; ++zz) {
                    BlockPos checkPos = new BlockPos(xx, yy, zz);

                    if (yy < level.getMinBuildHeight() || yy >= level.getMaxBuildHeight()) {
                        clear = false;
                        continue;
                    }

                    if (!level.hasChunkAt(checkPos)) {
                        clear = false;
                        continue;
                    }
                    BlockState checkState = level.getBlockState(checkPos);

                    if (!isTreeReplaceable(checkState) && yy > y) {
                        clear = false;
                    }
                }
            }
        }

        if (!clear) {
            return false;
        }

        BlockPos soilPos = pos.below();

        if (!isValidSoil(level.getBlockState(soilPos))) {
            return false;
        }

        if (y >= level.getMaxBuildHeight() - height - 1) {
            return false;
        }

        int start = y + height - 5;
        int end = y + height + 3 + random.nextInt(3);
        BlockState leaves = silverwoodLeaves();

        for (int yy = start; yy <= end; ++yy) {
            int crownY = Mth.clamp(yy, y + height - 3, y + height);

            for (int xx = x - 5; xx <= x + 5; ++xx) {
                for (int zz = z - 5; zz <= z + 5; ++zz) {
                    double dx = xx - x;
                    double dy = yy - crownY;
                    double dz = zz - z;
                    double dist = dx * dx + dy * dy + dz * dz;

                    BlockPos leafPos = new BlockPos(xx, yy, zz);

                    if (dist < 10 + random.nextInt(8) && canWriteBlock(level, leafPos) && canReplaceWithLeaves(level, leafPos)) {
                        level.setBlock(leafPos, leaves, 3);
                    }
                }
            }
        }

        BlockState verticalLog = silverwoodLog(Direction.Axis.Y);
        BlockState xLog = silverwoodLog(Direction.Axis.X);
        BlockState zLog = silverwoodLog(Direction.Axis.Z);

        int k;

        for (k = 0; k < height; ++k) {
            BlockPos trunkPos = new BlockPos(x, y + k, z);
            BlockState trunkState = level.getBlockState(trunkPos);

            if (isTreeReplaceable(trunkState)) {
                setIfReplaceable(level, trunkPos, verticalLog);
                setIfReplaceable(level, new BlockPos(x - 1, y + k, z), verticalLog);
                setIfReplaceable(level, new BlockPos(x + 1, y + k, z), verticalLog);
                setIfReplaceable(level, new BlockPos(x, y + k, z - 1), verticalLog);
                setIfReplaceable(level, new BlockPos(x, y + k, z + 1), verticalLog);
            }
        }

        setIfReplaceable(level, new BlockPos(x, y + k, z), verticalLog);

        setIfReplaceable(level, new BlockPos(x - 1, y, z - 1), verticalLog);
        setIfReplaceable(level, new BlockPos(x + 1, y, z + 1), verticalLog);
        setIfReplaceable(level, new BlockPos(x - 1, y, z + 1), verticalLog);
        setIfReplaceable(level, new BlockPos(x + 1, y, z - 1), verticalLog);

        if (random.nextInt(3) != 0) {
            setIfReplaceable(level, new BlockPos(x - 1, y + 1, z - 1), verticalLog);
        }
        if (random.nextInt(3) != 0) {
            setIfReplaceable(level, new BlockPos(x + 1, y + 1, z + 1), verticalLog);
        }
        if (random.nextInt(3) != 0) {
            setIfReplaceable(level, new BlockPos(x - 1, y + 1, z + 1), verticalLog);
        }
        if (random.nextInt(3) != 0) {
            setIfReplaceable(level, new BlockPos(x + 1, y + 1, z - 1), verticalLog);
        }

        setIfReplaceable(level, new BlockPos(x - 2, y, z), xLog);
        setIfReplaceable(level, new BlockPos(x + 2, y, z), xLog);
        setIfReplaceable(level, new BlockPos(x, y, z - 2), zLog);
        setIfReplaceable(level, new BlockPos(x, y, z + 2), zLog);

        setIfReplaceable(level, new BlockPos(x - 2, y - 1, z), verticalLog);
        setIfReplaceable(level, new BlockPos(x + 2, y - 1, z), verticalLog);
        setIfReplaceable(level, new BlockPos(x, y - 1, z - 2), verticalLog);
        setIfReplaceable(level, new BlockPos(x, y - 1, z + 2), verticalLog);

        setIfReplaceable(level, new BlockPos(x - 1, y + height - 4, z - 1), verticalLog);
        setIfReplaceable(level, new BlockPos(x + 1, y + height - 4, z + 1), verticalLog);
        setIfReplaceable(level, new BlockPos(x - 1, y + height - 4, z + 1), verticalLog);
        setIfReplaceable(level, new BlockPos(x + 1, y + height - 4, z - 1), verticalLog);

        if (random.nextInt(3) == 0) {
            setIfReplaceable(level, new BlockPos(x - 1, y + height - 5, z - 1), verticalLog);
        }
        if (random.nextInt(3) == 0) {
            setIfReplaceable(level, new BlockPos(x + 1, y + height - 5, z + 1), verticalLog);
        }
        if (random.nextInt(3) == 0) {
            setIfReplaceable(level, new BlockPos(x - 1, y + height - 5, z + 1), verticalLog);
        }
        if (random.nextInt(3) == 0) {
            setIfReplaceable(level, new BlockPos(x + 1, y + height - 5, z - 1), verticalLog);
        }

        setIfReplaceable(level, new BlockPos(x - 2, y + height - 4, z), xLog);
        setIfReplaceable(level, new BlockPos(x + 2, y + height - 4, z), xLog);
        setIfReplaceable(level, new BlockPos(x, y + height - 4, z - 2), zLog);
        setIfReplaceable(level, new BlockPos(x, y + height - 4, z + 2), zLog);

        if (worldgen) {
            placeShimmerleafPatch(level, random, pos);
        }

        return true;
    }

    private static void placeShimmerleafPatch(WorldGenLevel level, RandomSource random, BlockPos center) {
        for (int i = 0; i < 18; i++) {
            int x = center.getX() + random.nextInt(8) - random.nextInt(8);
            int y = center.getY() + random.nextInt(4) - random.nextInt(4);
            int z = center.getZ() + random.nextInt(8) - random.nextInt(8);
            BlockPos target = new BlockPos(x, y, z);
            BlockPos below = target.below();

            if (canWriteBlock(level, target) && canWriteBlock(level, below)) {
                BlockState targetState = level.getBlockState(target);
                BlockState belowState = level.getBlockState(below);
                if ((targetState.isAir() || targetState.canBeReplaced())
                        && level.getFluidState(target).isEmpty()
                        && level.getFluidState(below).isEmpty()
                        && (belowState.is(Blocks.GRASS_BLOCK) || belowState.is(Blocks.DIRT) || belowState.is(Blocks.PODZOL) || belowState.is(Blocks.MOSS_BLOCK))) {
                    level.setBlock(target, TCBlocks.SHIMMERLEAF.get().defaultBlockState(), 3);
                }
            }
        }
    }

    private static void setIfReplaceable(WorldGenLevel level, BlockPos pos, BlockState state) {
        if (canWriteBlock(level, pos) && isTreeReplaceable(level.getBlockState(pos))) {
            level.setBlock(pos, state, 3);
        }
    }

    private static boolean canReplaceWithLeaves(WorldGenLevel level, BlockPos pos) {
        return level.hasChunkAt(pos) && isTreeReplaceable(level.getBlockState(pos));
    }

    private static boolean isTreeReplaceable(BlockState state) {
        return state.isAir()
                || state.getBlock() instanceof LeavesBlock
                || state.canBeReplaced();
    }

    private static boolean isValidSoil(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.ROOTED_DIRT)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MOSS_BLOCK)
                || state.is(Blocks.FARMLAND);
    }

    private static BlockState silverwoodLeaves() {
        return TCLeavesBlock.legacyGeneratedState(TCBlocks.LEAVES_SILVERWOOD.get().defaultBlockState());
    }

    private static BlockState silverwoodLog(Direction.Axis axis) {
        return TCBlocks.LOG_SILVERWOOD.get()
                .defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, axis);
    }

    private static boolean canWriteBlock(WorldGenLevel level, BlockPos pos) {
        if (level instanceof net.minecraft.server.level.WorldGenRegion region) {
            net.minecraft.world.level.ChunkPos center = region.getCenter();
            int chunkX = net.minecraft.core.SectionPos.blockToSectionCoord(pos.getX());
            int chunkZ = net.minecraft.core.SectionPos.blockToSectionCoord(pos.getZ());
            if (Math.abs(chunkX - center.x) > 1 || Math.abs(chunkZ - center.z) > 1) {
                return false;
            }
            if (pos.getY() < region.getMinBuildHeight() || pos.getY() >= region.getMaxBuildHeight()) {
                return false;
            }
        }
        return level.hasChunkAt(pos);
    }
}
