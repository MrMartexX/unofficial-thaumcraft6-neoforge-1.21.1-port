package thaumcraft.common.world.trees;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 1:1 port of Legacy Thaumcraft 6 {@code WorldGenBigMagicTree} (BiomeGenMagicalForest:43, 63).
 * Generates tall (11..21 block) wide-crowned multi-branch oak trees for Magical Forest,
 * avoiding the tiny 4-block dwarf bushes produced by vanilla 1.21.1 {@code fancy_oak}.
 */
public class TCBigMagicTreeGenerator {
    private static final byte[] OTHER_COORD_PAIRS = new byte[] { 2, 0, 0, 1, 2, 1 };

    private Random rand;
    private WorldGenLevel level;
    private final int[] basePos = new int[] { 0, 0, 0 };
    private int heightLimit;
    private int height;
    private final double heightAttenuation = 0.6618D;
    private final double branchSlope = 0.381D;
    private final double scaleWidth = 1.25D;
    private final double leafDensity = 0.9D;
    private final int heightLimitLimit = 11;
    private final int leafDistanceLimit = 4;
    private int[][] leafNodes;

    public boolean generate(WorldGenLevel level, RandomSource random, BlockPos pos) {
        if (pos.getY() < level.getLevel().getSeaLevel() || !level.getFluidState(pos).isEmpty()) {
            return false;
        }

        this.level = level;
        this.rand = new Random(random.nextLong());
        this.basePos[0] = pos.getX();
        this.basePos[1] = pos.getY();
        this.basePos[2] = pos.getZ();

        if (this.heightLimit == 0) {
            this.heightLimit = 11 + this.rand.nextInt(this.heightLimitLimit);
        }

        if (!validTreeLocation()) {
            clearRuntimeState();
            return false;
        }

        generateLeafNodeList();
        generateLeaves();
        generateTrunk();
        generateLeafNodeBases();
        clearRuntimeState();
        return true;
    }

    private void clearRuntimeState() {
        this.level = null;
        this.rand = null;
        this.leafNodes = null;
    }

    private void generateLeafNodeList() {
        this.height = (int)(this.heightLimit * this.heightAttenuation);
        if (this.height >= this.heightLimit) {
            this.height = this.heightLimit - 1;
        }

        int nodeCount = (int)(1.382D + Math.pow(this.leafDensity * this.heightLimit / 13.0D, 2.0D));
        if (nodeCount < 1) {
            nodeCount = 1;
        }

        int[][] nodes = new int[nodeCount * this.heightLimit][4];
        int y = this.basePos[1] + this.heightLimit - this.leafDistanceLimit;
        int used = 1;
        int trunkTopY = this.basePos[1] + this.height;
        int layer = this.heightLimit - this.leafDistanceLimit;

        nodes[0][0] = this.basePos[0];
        nodes[0][1] = y;
        nodes[0][2] = this.basePos[2];
        nodes[0][3] = trunkTopY;

        while (layer >= 0) {
            float f = layerSize(layer);
            if (f >= 0.0F) {
                for (int l = 0; l < nodeCount; ++l) {
                    double radius = this.scaleWidth * f * (this.rand.nextFloat() + 0.328D);
                    double angle = this.rand.nextFloat() * 2.0D * Math.PI;
                    int nodeX = Mth.floor(this.basePos[0] + radius * Math.sin(angle) + 0.5D);
                    int nodeZ = Mth.floor(this.basePos[2] + radius * Math.cos(angle) + 0.5D);
                    int nodeY = this.basePos[1] + layer - 1;

                    int[] start = new int[] { nodeX, nodeY, nodeZ };
                    int[] end = new int[] { nodeX, nodeY + this.leafDistanceLimit, nodeZ };

                    if (checkBlockLine(start, end) == -1) {
                        int dx = this.basePos[0] - nodeX;
                        int dz = this.basePos[2] - nodeZ;
                        double branchBaseY = nodeY - Math.sqrt(dx * dx + dz * dz) * this.branchSlope;
                        int minBranchBaseY = this.basePos[1] + Mth.ceil(this.heightLimit * 0.2D);
                        int baseY = branchBaseY > trunkTopY ? trunkTopY : Math.max(minBranchBaseY, (int)branchBaseY);
                        int[] branchBase = new int[] { this.basePos[0], baseY, this.basePos[2] };

                        if (checkBlockLine(branchBase, start) == -1) {
                            nodes[used][0] = nodeX;
                            nodes[used][1] = nodeY;
                            nodes[used][2] = nodeZ;
                            nodes[used][3] = baseY;
                            ++used;
                        }
                    }
                }
            }
            --layer;
        }

        this.leafNodes = new int[used][4];
        System.arraycopy(nodes, 0, this.leafNodes, 0, used);
    }

    private float layerSize(int layer) {
        if (layer < this.heightLimit * 0.3F) {
            return -1.0F;
        }
        float halfHeight = this.heightLimit / 2.0F;
        float delta = halfHeight - layer;
        float size = Mth.sqrt(halfHeight * halfHeight - delta * delta);
        if (delta == 0.0F) {
            size = halfHeight;
        } else if (Math.abs(delta) >= halfHeight) {
            return 0.0F;
        }
        return size * 0.5F;
    }

    private float leafSize(int layer) {
        return (layer >= 0 && layer < this.leafDistanceLimit)
                ? (layer != 0 && layer != this.leafDistanceLimit - 1 ? 3.0F : 2.0F)
                : -1.0F;
    }

    private void generateLeafNode(int x, int y, int z) {
        BlockState leafState = Blocks.OAK_LEAVES.defaultBlockState()
                .setValue(LeavesBlock.DISTANCE, 1)
                .setValue(LeavesBlock.PERSISTENT, false);
        for (int i = 0; i < this.leafDistanceLimit; ++i) {
            crossSection(x, y + i, z, leafSize(i), leafState);
        }
    }

    private void crossSection(int cx, int cy, int cz, float radius, BlockState leafState) {
        int r = (int)(radius + 0.618D);
        for (int dx = -r; dx <= r; ++dx) {
            for (int dz = -r; dz <= r; ++dz) {
                if (Math.pow(Math.abs(dx) + 0.5D, 2.0D) + Math.pow(Math.abs(dz) + 0.5D, 2.0D) <= radius * radius) {
                    BlockPos pos = new BlockPos(cx + dx, cy, cz + dz);
                    if (canWriteBlock(this.level, pos)) {
                        BlockState state = this.level.getBlockState(pos);
                        if (state.isAir() || state.getBlock() instanceof LeavesBlock || state.canBeReplaced()) {
                            this.level.setBlock(pos, leafState, 19);
                        }
                    }
                }
            }
        }
    }

    private void generateLeaves() {
        for (int[] node : this.leafNodes) {
            if (leafNodeNeedsBase(node[3] - this.basePos[1])) {
                generateLeafNode(node[0], node[1], node[2]);
            }
        }
    }

    private boolean leafNodeNeedsBase(int heightFromBase) {
        return heightFromBase >= this.heightLimit * 0.2D;
    }

    private void generateTrunk() {
        int[] start = new int[] { this.basePos[0], this.basePos[1], this.basePos[2] };
        int[] end = new int[] { this.basePos[0], this.basePos[1] + this.height, this.basePos[2] };
        placeBlockLine(start, end, Blocks.OAK_LOG.defaultBlockState());
    }

    private void generateLeafNodeBases() {
        int[] base = new int[] { this.basePos[0], this.basePos[1], this.basePos[2] };
        for (int[] node : this.leafNodes) {
            int[] target = new int[] { node[0], node[1], node[2] };
            base[1] = node[3];
            if (leafNodeNeedsBase(base[1] - this.basePos[1])) {
                placeBlockLine(base, target, Blocks.OAK_LOG.defaultBlockState());
            }
        }
    }

    private void placeBlockLine(int[] start, int[] end, BlockState logBase) {
        int[] delta = new int[] { 0, 0, 0 };
        byte majorAxis = 0;
        for (byte axis = 0; axis < 3; ++axis) {
            delta[axis] = end[axis] - start[axis];
            if (Math.abs(delta[axis]) > Math.abs(delta[majorAxis])) {
                majorAxis = axis;
            }
        }
        int steps = Math.abs(delta[majorAxis]);
        if (steps == 0) {
            BlockPos pos = new BlockPos(start[0], start[1], start[2]);
            if (canWriteBlock(this.level, pos) && isTreeReplaceable(this.level.getBlockState(pos))) {
                this.level.setBlock(pos, logBase.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y), 19);
            }
            return;
        }
        float fx = delta[0] / (float)steps;
        float fy = delta[1] / (float)steps;
        float fz = delta[2] / (float)steps;
        for (int j = 0; j <= steps; ++j) {
            int x = Mth.floor(start[0] + 0.5F + j * fx);
            int y = Mth.floor(start[1] + 0.5F + j * fy);
            int z = Mth.floor(start[2] + 0.5F + j * fz);
            int dx = Math.abs(x - start[0]);
            int dz = Math.abs(z - start[2]);
            int maxHoriz = Math.max(dx, dz);
            Direction.Axis axis = Direction.Axis.Y;
            if (maxHoriz > 0) {
                axis = (dx == maxHoriz) ? Direction.Axis.X : Direction.Axis.Z;
            }
            BlockPos pos = new BlockPos(x, y, z);
            if (canWriteBlock(this.level, pos) && isTreeReplaceable(this.level.getBlockState(pos))) {
                this.level.setBlock(pos, logBase.setValue(RotatedPillarBlock.AXIS, axis), 19);
            }
        }
    }

    private int checkBlockLine(int[] start, int[] end) {
        int[] delta = new int[] { 0, 0, 0 };
        byte majorAxis = 0;
        for (byte axis = 0; axis < 3; ++axis) {
            delta[axis] = end[axis] - start[axis];
            if (Math.abs(delta[axis]) > Math.abs(delta[majorAxis])) {
                majorAxis = axis;
            }
        }
        int steps = Math.abs(delta[majorAxis]);
        if (steps == 0) {
            return -1;
        }
        float fx = delta[0] / (float)steps;
        float fy = delta[1] / (float)steps;
        float fz = delta[2] / (float)steps;
        for (int j = 0; j <= steps; ++j) {
            int x = Mth.floor(start[0] + 0.5F + j * fx);
            int y = Mth.floor(start[1] + 0.5F + j * fy);
            int z = Mth.floor(start[2] + 0.5F + j * fz);
            BlockPos pos = new BlockPos(x, y, z);
            if (!this.level.hasChunkAt(pos)) {
                return j;
            }
            if (!isTreeReplaceable(this.level.getBlockState(pos))) {
                return j;
            }
        }
        return -1;
    }

    private boolean validTreeLocation() {
        BlockPos down = new BlockPos(this.basePos[0], this.basePos[1] - 1, this.basePos[2]);
        BlockState soil = this.level.getBlockState(down);
        if (!soil.is(BlockTags.DIRT) && !soil.is(Blocks.GRASS_BLOCK) && !soil.is(Blocks.FARMLAND)) {
            return false;
        }
        int[] start = new int[] { this.basePos[0], this.basePos[1], this.basePos[2] };
        int[] end = new int[] { this.basePos[0], this.basePos[1] + this.heightLimit - 1, this.basePos[2] };
        int i = checkBlockLine(start, end);
        if (i == -1) {
            return true;
        }
        if (i < 6) {
            return false;
        }
        this.heightLimit = i;
        return true;
    }

    private static boolean isTreeReplaceable(BlockState state) {
        return (state.isAir()
                || state.getBlock() instanceof LeavesBlock
                || state.is(BlockTags.LOGS)
                || state.is(BlockTags.SAPLINGS)
                || state.canBeReplaced())
                && state.getFluidState().isEmpty();
    }

    private static boolean canWriteBlock(WorldGenLevel level, BlockPos pos) {
        if (pos.getY() < level.getMinBuildHeight() || pos.getY() >= level.getMaxBuildHeight()) {
            return false;
        }
        if (!level.hasChunkAt(pos)) {
            return false;
        }
        try {
            return level.ensureCanWrite(pos);
        } catch (Exception ignored) {
            return false;
        }
    }
}
