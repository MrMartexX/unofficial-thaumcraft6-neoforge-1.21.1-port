package thaumcraft.common.world.trees;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.blocks.world.plants.TCLeavesBlock;

public class TCGreatwoodTreeGenerator {
    private static final byte[] OTHER_COORD_PAIRS = new byte[] { 2, 0, 0, 1, 2, 1 };

    private final boolean spiders;

    private Random rand;
    private ServerLevel level;
    private final int[] basePos = new int[] { 0, 0, 0 };
    private int heightLimit;
    private int height;
    private double heightAttenuation = 0.618D;
    private double branchSlope = 0.38D;
    private double scaleWidth = 1.2D;
    private double leafDensity = 0.9D;
    private int trunkSize = 2;
    private int heightLimitLimit = 11;
    private int leafDistanceLimit = 4;
    private int[][] leafNodes;

    public TCGreatwoodTreeGenerator(boolean spiders) {
        this.spiders = spiders;
    }

    public boolean generate(ServerLevel level, RandomSource random, BlockPos pos) {
        this.level = level;
        this.rand = new Random(random.nextLong());
        this.basePos[0] = pos.getX();
        this.basePos[1] = pos.getY();
        this.basePos[2] = pos.getZ();

        if (this.heightLimit == 0) {
            this.heightLimit = this.heightLimitLimit + this.rand.nextInt(this.heightLimitLimit);
        }

        for (int x = 0; x < this.trunkSize; ++x) {
            for (int z = 0; z < this.trunkSize; ++z) {
                if (!validTreeLocation(x, z)) {
                    clearRuntimeState();
                    return false;
                }
            }
        }

        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);

        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        generateTrunk();

        this.scaleWidth = 1.66D;
        this.basePos[0] = pos.getX();
        this.basePos[1] = pos.getY() + this.height;
        this.basePos[2] = pos.getZ();

        generateLeafNodeList();
        generateLeaves();
        generateLeafNodeBases();
        generateTrunk();

        if (this.spiders) {
            // Legacy worldgen could create a cave spider dungeon under some Greatwood trees.
            // Sapling growth always passes spiders=false, so dungeon generation is intentionally not ported here yet.
        }

        stabilizeGeneratedLeaves(this.level, pos, 26, pos.getY() - 2, pos.getY() + 60);


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
        int layer = y - this.basePos[1];

        nodes[0][0] = this.basePos[0];
        nodes[0][1] = y;
        nodes[0][2] = this.basePos[2];
        nodes[0][3] = trunkTopY;

        --y;

        while (layer >= 0) {
            float layerSize = layerSize(layer);

            if (layerSize < 0.0F) {
                --y;
                --layer;
                continue;
            }

            for (int node = 0; node < nodeCount; ++node) {
                double radius = this.scaleWidth * layerSize * (this.rand.nextFloat() + 0.328D);
                double angle = this.rand.nextFloat() * 2.0D * Math.PI;

                int nodeX = Mth.floor(radius * Math.sin(angle) + this.basePos[0] + 0.5D);
                int nodeZ = Mth.floor(radius * Math.cos(angle) + this.basePos[2] + 0.5D);

                int[] start = new int[] { nodeX, y, nodeZ };
                int[] end = new int[] { nodeX, y + this.leafDistanceLimit, nodeZ };

                if (checkBlockLine(start, end) == -1) {
                    int[] branchBase = new int[] { this.basePos[0], this.basePos[1], this.basePos[2] };
                    double horizontalDistance = Math.sqrt(
                            Math.pow(Math.abs(this.basePos[0] - start[0]), 2.0D)
                                    + Math.pow(Math.abs(this.basePos[2] - start[2]), 2.0D)
                    );
                    double branchDrop = horizontalDistance * this.branchSlope;

                    if (start[1] - branchDrop > trunkTopY) {
                        branchBase[1] = trunkTopY;
                    } else {
                        branchBase[1] = (int)(start[1] - branchDrop);
                    }

                    if (checkBlockLine(branchBase, start) == -1) {
                        nodes[used][0] = nodeX;
                        nodes[used][1] = y;
                        nodes[used][2] = nodeZ;
                        nodes[used][3] = branchBase[1];
                        ++used;
                    }
                }
            }

            --y;
            --layer;
        }

        this.leafNodes = new int[used][4];
        System.arraycopy(nodes, 0, this.leafNodes, 0, used);
    }

    private void genTreeLayer(int x, int y, int z, float radius, byte axis, BlockState leaves) {
        int roundedRadius = (int)(radius + 0.618D);
        byte coordA = OTHER_COORD_PAIRS[axis];
        byte coordB = OTHER_COORD_PAIRS[axis + 3];

        int[] center = new int[] { x, y, z };
        int[] cursor = new int[] { 0, 0, 0 };
        cursor[axis] = center[axis];

        for (int da = -roundedRadius; da <= roundedRadius; ++da) {
            cursor[coordA] = center[coordA] + da;

            for (int db = -roundedRadius; db <= roundedRadius; ++db) {
                double distance = Math.pow(Math.abs(da) + 0.5D, 2.0D)
                        + Math.pow(Math.abs(db) + 0.5D, 2.0D);

                if (distance <= radius * radius) {
                    cursor[coordB] = center[coordB] + db;
                    BlockPos pos = new BlockPos(cursor[0], cursor[1], cursor[2]);

                    if (canReplaceWithGreatwoodLeaves(pos)) {
                        this.level.setBlock(pos, leaves, 3);
                    }
                }
            }
        }
    }

    private float layerSize(int layer) {
        if (layer < (float)this.heightLimit * 0.3F) {
            return -1.618F;
        }

        float halfHeight = this.heightLimit / 2.0F;
        float centeredLayer = halfHeight - layer;
        float radius;

        if (centeredLayer == 0.0F) {
            radius = halfHeight;
        } else if (Math.abs(centeredLayer) >= halfHeight) {
            radius = 0.0F;
        } else {
            radius = (float)Math.sqrt(Math.pow(Math.abs(halfHeight), 2.0D) - Math.pow(Math.abs(centeredLayer), 2.0D));
        }

        radius *= 0.5F;
        return radius;
    }

    private float leafSize(int layer) {
        if (layer < 0 || layer >= this.leafDistanceLimit) {
            return -1.0F;
        }

        return layer != 0 && layer != this.leafDistanceLimit - 1 ? 3.0F : 2.0F;
    }

    private void generateLeafNode(int x, int y, int z) {
        for (int yy = y; yy < y + this.leafDistanceLimit; ++yy) {
            float radius = leafSize(yy - y);
            genTreeLayer(x, yy, z, radius, (byte)1, greatwoodLeaves());
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

        if (delta[majorAxis] == 0) {
            return;
        }

        byte coordA = OTHER_COORD_PAIRS[majorAxis];
        byte coordB = OTHER_COORD_PAIRS[majorAxis + 3];
        byte step = delta[majorAxis] > 0 ? (byte)1 : (byte)-1;

        double slopeA = delta[coordA] / (double)delta[majorAxis];
        double slopeB = delta[coordB] / (double)delta[majorAxis];

        int[] cursor = new int[] { 0, 0, 0 };

        for (int n = 0, endN = delta[majorAxis] + step; n != endN; n += step) {
            cursor[majorAxis] = Mth.floor(start[majorAxis] + n + 0.5D);
            cursor[coordA] = Mth.floor(start[coordA] + n * slopeA + 0.5D);
            cursor[coordB] = Mth.floor(start[coordB] + n * slopeB + 0.5D);

            int dx = Math.abs(cursor[0] - start[0]);
            int dz = Math.abs(cursor[2] - start[2]);
            int max = Math.max(dx, dz);

            Direction.Axis logAxis = Direction.Axis.Y;

            if (max > 0) {
                if (dx == max) {
                    logAxis = Direction.Axis.X;
                } else if (dz == max) {
                    logAxis = Direction.Axis.Z;
                }
            }

            BlockPos pos = new BlockPos(cursor[0], cursor[1], cursor[2]);

            if (isTreeReplaceable(this.level.getBlockState(pos))) {
                this.level.setBlock(pos, logBase.setValue(RotatedPillarBlock.AXIS, logAxis), 3);
            }
        }
    }

    private void generateLeaves() {
        for (int[] node : this.leafNodes) {
            generateLeafNode(node[0], node[1], node[2]);
        }
    }

    private boolean leafNodeNeedsBase(int heightFromBase) {
        return heightFromBase >= this.heightLimit * 0.2D;
    }

    private void generateTrunk() {
        int x = this.basePos[0];
        int y0 = this.basePos[1];
        int y1 = this.basePos[1] + this.height;
        int z = this.basePos[2];

        int[] start = new int[] { x, y0, z };
        int[] end = new int[] { x, y1, z };

        BlockState log = greatwoodLog(Direction.Axis.Y);

        placeBlockLine(start, end, log);

        if (this.trunkSize == 2) {
            ++start[0];
            ++end[0];
            placeBlockLine(start, end, log);

            ++start[2];
            ++end[2];
            placeBlockLine(start, end, log);

            --start[0];
            --end[0];
            placeBlockLine(start, end, log);
        }
    }

    private void generateLeafNodeBases() {
        int[] trunkBase = new int[] { this.basePos[0], this.basePos[1], this.basePos[2] };

        for (int[] node : this.leafNodes) {
            int[] nodePos = new int[] { node[0], node[1], node[2] };
            trunkBase[1] = node[3];
            int branchHeight = trunkBase[1] - this.basePos[1];

            if (leafNodeNeedsBase(branchHeight)) {
                placeBlockLine(trunkBase, nodePos, greatwoodLog(Direction.Axis.Y));
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

        if (delta[majorAxis] == 0) {
            return -1;
        }

        byte coordA = OTHER_COORD_PAIRS[majorAxis];
        byte coordB = OTHER_COORD_PAIRS[majorAxis + 3];
        byte step = delta[majorAxis] > 0 ? (byte)1 : (byte)-1;

        double slopeA = delta[coordA] / (double)delta[majorAxis];
        double slopeB = delta[coordB] / (double)delta[majorAxis];

        int[] cursor = new int[] { 0, 0, 0 };

        int n;
        int endN;

        for (n = 0, endN = delta[majorAxis] + step; n != endN; n += step) {
            cursor[majorAxis] = start[majorAxis] + n;
            cursor[coordA] = Mth.floor(start[coordA] + n * slopeA);
            cursor[coordB] = Mth.floor(start[coordB] + n * slopeB);

            BlockPos pos = new BlockPos(cursor[0], cursor[1], cursor[2]);
            BlockState state = this.level.getBlockState(pos);

            if (!state.isAir() && !state.is(TCBlocks.LEAVES_GREATWOOD.get())) {
                break;
            }
        }

        return n == endN ? -1 : Math.abs(n);
    }

    private boolean validTreeLocation(int offsetX, int offsetZ) {
        int[] start = new int[] { this.basePos[0] + offsetX, this.basePos[1], this.basePos[2] + offsetZ };
        int[] end = new int[] { this.basePos[0] + offsetX, this.basePos[1] + this.heightLimit - 1, this.basePos[2] + offsetZ };

        BlockPos soilPos = new BlockPos(this.basePos[0] + offsetX, this.basePos[1] - 1, this.basePos[2] + offsetZ);
        BlockState soil = this.level.getBlockState(soilPos);

        if (!isValidSoil(soil)) {
            return false;
        }

        int obstructionDistance = checkBlockLine(start, end);

        if (obstructionDistance == -1) {
            return true;
        }

        if (obstructionDistance < 6) {
            return false;
        }

        this.heightLimit = obstructionDistance;
        return true;
    }

    private boolean canReplaceWithGreatwoodLeaves(BlockPos pos) {
        BlockState state = this.level.getBlockState(pos);
        return state.isAir()
                || state.is(TCBlocks.LEAVES_GREATWOOD.get())
                || state.getBlock() instanceof LeavesBlock
                || state.canBeReplaced();
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

    private static BlockState greatwoodLeaves() {
        BlockState state = TCBlocks.LEAVES_GREATWOOD.get().defaultBlockState();

        if (state.hasProperty(LeavesBlock.DISTANCE)) {
            state = state.setValue(LeavesBlock.DISTANCE, 7);
        }

        if (state.hasProperty(LeavesBlock.PERSISTENT)) {
            state = state.setValue(LeavesBlock.PERSISTENT, false);
        }

        return state;
    }

    private static BlockState greatwoodLog(Direction.Axis axis) {
        return TCBlocks.LOG_GREATWOOD.get()
                .defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, axis);
    }

    private static void stabilizeGeneratedLeaves(ServerLevel level, BlockPos center, int horizontalRadius, int minY, int maxY) {
        int clampedMinY = Math.max(level.getMinBuildHeight(), minY);
        int clampedMaxY = Math.min(level.getMaxBuildHeight() - 1, maxY);

        for (int pass = 0; pass < 7; pass++) {
            boolean changed = false;

            for (int y = clampedMinY; y <= clampedMaxY; y++) {
                for (int x = center.getX() - horizontalRadius; x <= center.getX() + horizontalRadius; x++) {
                    for (int z = center.getZ() - horizontalRadius; z <= center.getZ() + horizontalRadius; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = level.getBlockState(pos);

                        if (!state.hasProperty(LeavesBlock.DISTANCE)) {
                            continue;
                        }

                        int distance = getUpdatedLeafDistance(level, pos);

                        if (distance != state.getValue(LeavesBlock.DISTANCE)) {
                            level.setBlock(pos, state.setValue(LeavesBlock.DISTANCE, distance), 3);
                            changed = true;
                        }
                    }
                }
            }

            if (!changed) {
                return;
            }
        }
    }

    private static int getUpdatedLeafDistance(ServerLevel level, BlockPos pos) {
        int distance = 7;

        for (Direction direction : Direction.values()) {
            BlockState neighbor = level.getBlockState(pos.relative(direction));

            if (neighbor.is(net.minecraft.tags.BlockTags.LOGS)) {
                return 1;
            }

            if (neighbor.hasProperty(LeavesBlock.DISTANCE)) {
                distance = Math.min(distance, neighbor.getValue(LeavesBlock.DISTANCE) + 1);
            }
        }

        return Math.min(distance, 7);
    }
}