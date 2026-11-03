package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCBlocks;

/**
 * Natural world generation feature restoring the iconic Thaumcraft Ancient Mound structure (WorldGenMound).
 * Generates an earthen barrow containing subterranean chambers, spawners, urns/crates, and a central sarcophagus.
 */
public final class TCMoundFeature extends Feature<NoneFeatureConfiguration> {
    private static final String MOUND_DATA_PATH = "/data/thaumcraft/structures/ancient_mound.bin";
    private static byte[] cachedData;

    public TCMoundFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    private static synchronized byte[] getMoundData() {
        if (cachedData == null) {
            try (InputStream in = TCMoundFeature.class.getResourceAsStream(MOUND_DATA_PATH)) {
                if (in == null) {
                    Thaumcraft.LOGGER.error("Failed to find ancient mound structure data at {}", MOUND_DATA_PATH);
                    return new byte[0];
                }
                cachedData = in.readAllBytes();
            } catch (IOException e) {
                Thaumcraft.LOGGER.error("Failed to load ancient mound structure data", e);
                return new byte[0];
            }
        }
        return cachedData;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // Match Legacy ThaumcraftWorldGenerator.java:74-75 (chunkX * 16 + 8 + [-4..4] -> offset 4..12)
        // so that the 19x19 mound (dx=0..18, dz=0..18) reaches at most offset 30 (<= 31) and never
        // crosses into chunk center+2 where canWriteBlock would reject 23.4% of attempts.
        int chunkMinX = SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(origin.getX()));
        int chunkMinZ = SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(origin.getZ()));
        int i = chunkMinX + 4 + random.nextInt(9);
        int k = chunkMinZ + 4 + random.nextInt(9);

        return placeMoundAt(level, random, i, k);
    }

    public static boolean placeMoundAt(WorldGenLevel level, RandomSource random, int i, int k) {
        byte[] data = getMoundData();
        if (data.length == 0 || data.length % 4 != 0) {
            return false;
        }

        // Legacy passes getPrecipitationHeight(...).down(9), i.e. first-air Y minus 9.
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, i, k);
        int groundY = surfaceY - 1;
        if (groundY < level.getLevel().getSeaLevel()
                || !level.getFluidState(new BlockPos(i, surfaceY, k)).isEmpty()
                || !level.getFluidState(new BlockPos(i, groundY, k)).isEmpty()) {
            return false;
        }
        int j = surfaceY - 9;

        // Ensure the entire 19x19 bounds are fully writable in the current WorldGenRegion
        // to prevent partial chunk carving or sharp rectangular slice artifacts along chunk borders.
        if (!canWriteBlock(level, new BlockPos(i, j - 1, k))
                || !canWriteBlock(level, new BlockPos(i + 18, j, k))
                || !canWriteBlock(level, new BlockPos(i, j, k + 18))
                || !canWriteBlock(level, new BlockPos(i + 18, j, k + 18))
                || !canWriteBlock(level, new BlockPos(i, j + 15, k))
                || !canWriteBlock(level, new BlockPos(i + 18, j + 15, k + 18))) {
            return false;
        }

        if (!locationIsValidSpawn(level, new BlockPos(i + 9, j + 9, k + 9))
                || !locationIsValidSpawn(level, new BlockPos(i, j + 9, k))
                || !locationIsValidSpawn(level, new BlockPos(i + 18, j + 9, k))
                || !locationIsValidSpawn(level, new BlockPos(i + 18, j + 9, k + 18))
                || !locationIsValidSpawn(level, new BlockPos(i, j + 9, k + 18))) {
            return false;
        }

        placeSlice(level, new BlockPos(i, j, k), bounds(i, groundY, k), random.nextLong());
        return true;
    }

    public static BoundingBox bounds(int x, int groundY, int z) {
        // Binary dy is stored as dy + 1; actual records cover -1..15 relative to groundY - 8.
        return new BoundingBox(x, groundY - 9, z, x + 18, groundY + 7, z + 18);
    }

    /** A committed structure plan must never reroll terrain, position or loot between chunk slices. */
    public static void placeSlice(WorldGenLevel level, BlockPos base, BoundingBox clip, long seed) {
        byte[] data = getMoundData();
        if (data.length == 0 || data.length % 4 != 0) {
            throw new IllegalStateException("Missing or invalid ancient_mound.bin");
        }
        RandomSource choices = RandomSource.create(seed);
        Block loot1 = pickLootContainer(choices);
        Block loot2 = pickLootContainer(choices);
        boolean trapped = choices.nextInt(3) == 0;
        List<BlockPos> shapePositions = new ArrayList<>();

        int recordCount = data.length / 4;
        for (int idx = 0; idx < recordCount; idx++) {
            int offset = idx * 4;
            int dx = data[offset] & 0xFF;
            int dy = (data[offset + 1] & 0xFF) - 1;
            int dz = data[offset + 2] & 0xFF;
            int palette = data[offset + 3] & 0xFF;

            BlockPos target = base.offset(dx, dy, dz);
            if (!clip.isInside(target)) {
                continue;
            }
            if (!canWriteBlock(level, target)) {
                throw new IllegalStateException("Mound slice outside writable generation region: " + target);
            }
            RandomSource blockRandom = (palette == 17 || palette == 20 || palette == 21)
                    ? RandomSource.create(seed ^ target.asLong()) : choices;
            placeMoundBlock(level, blockRandom, target, palette, loot1, loot2, trapped, dx, dz);
            if (palette == 13 || palette == 14 || (palette >= 6 && palette <= 12)) {
                shapePositions.add(target);
            }
        }
        for (BlockPos target : shapePositions) {
            if (level instanceof WorldGenRegion) {
                level.getChunk(target).markPosForPostprocessing(target);
            } else {
                BlockState state = level.getBlockState(target);
                level.setBlock(target, Block.updateFromNeighbourShapes(state, level, target), 20);
            }
        }
    }

    private static void placeMoundBlock(
            WorldGenLevel level,
            RandomSource random,
            BlockPos target,
            int palette,
            Block loot1,
            Block loot2,
            boolean trapped,
            int dx,
            int dz
    ) {
        switch (palette) {
            case 0 -> level.setBlock(target, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            case 1 -> level.setBlock(target, Blocks.DIRT.defaultBlockState(), Block.UPDATE_CLIENTS);
            case 2 -> level.setBlock(target, Blocks.COBBLESTONE.defaultBlockState(), Block.UPDATE_CLIENTS);
            case 3 -> level.setBlock(target, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), Block.UPDATE_CLIENTS);
            case 4 -> level.setBlock(target, Blocks.GRASS_BLOCK.defaultBlockState(), Block.UPDATE_CLIENTS);
            case 5 -> level.setBlock(target, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), Block.UPDATE_CLIENTS);
            case 6 -> level.setBlock(target, Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM), Block.UPDATE_CLIENTS);
            case 7 -> level.setBlock(target, Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM), Block.UPDATE_CLIENTS);
            case 8 -> level.setBlock(target, Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.BOTTOM), Block.UPDATE_CLIENTS);
            case 9 -> level.setBlock(target, Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP), Block.UPDATE_CLIENTS);
            case 10 -> level.setBlock(target, Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP), Block.UPDATE_CLIENTS);
            case 11 -> level.setBlock(target, Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.TOP), Block.UPDATE_CLIENTS);
            case 12 -> level.setBlock(target, Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.TOP), Block.UPDATE_CLIENTS);
            case 13 -> level.setBlock(target, Blocks.IRON_BARS.defaultBlockState(), Block.UPDATE_CLIENTS);
            case 14 -> level.setBlock(target, Blocks.LADDER.defaultBlockState()
                    .setValue(LadderBlock.FACING, Direction.EAST), Block.UPDATE_CLIENTS);
            case 15 -> level.setBlock(target, Blocks.SHORT_GRASS.defaultBlockState(), Block.UPDATE_CLIENTS);
            case 16 -> {
                if (trapped) {
                    level.setBlock(target, Blocks.TNT.defaultBlockState(), Block.UPDATE_CLIENTS);
                } else {
                    level.setBlock(target, Blocks.COBBLESTONE.defaultBlockState(), Block.UPDATE_CLIENTS);
                }
            }
            case 17 -> {
                level.setBlock(target, Blocks.SPAWNER.defaultBlockState(), Block.UPDATE_CLIENTS);
                BlockEntity be = level.getBlockEntity(target);
                if (be instanceof SpawnerBlockEntity spawner) {
                    EntityType<?> mobType = (dx == 4 && dz == 4) ? EntityType.SKELETON : EntityType.ZOMBIE;
                    spawner.setEntityId(mobType, random);
                }
            }
            case 18 -> level.setBlock(target, loot1.defaultBlockState(), Block.UPDATE_CLIENTS);
            case 19 -> level.setBlock(target, loot2.defaultBlockState(), Block.UPDATE_CLIENTS);
            case 20, 21 -> {
                BlockState chestState = (trapped ? Blocks.TRAPPED_CHEST : Blocks.CHEST).defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.WEST);
                level.setBlock(target, chestState, Block.UPDATE_CLIENTS);
                BlockEntity be = level.getBlockEntity(target);
                if (be instanceof RandomizableContainerBlockEntity chest) {
                    chest.setLootTable(BuiltInLootTables.SIMPLE_DUNGEON, random.nextLong());
                }
            }
            default -> {
            }
        }
    }

    private static Block pickLootContainer(RandomSource random) {
        float rr = random.nextFloat();
        int rarity = (rr < 0.1F) ? 2 : ((rr < 0.33F) ? 1 : 0);
        boolean isCrate = random.nextFloat() < 0.3F;
        if (rarity == 2) {
            return isCrate ? TCBlocks.LOOT_CRATE_RARE.get() : TCBlocks.LOOT_URN_RARE.get();
        } else if (rarity == 1) {
            return isCrate ? TCBlocks.LOOT_CRATE_UNCOMMON.get() : TCBlocks.LOOT_URN_UNCOMMON.get();
        } else {
            return isCrate ? TCBlocks.LOOT_CRATE_COMMON.get() : TCBlocks.LOOT_URN_COMMON.get();
        }
    }

    private static boolean locationIsValidSpawn(WorldGenLevel level, BlockPos pos) {
        if (!level.hasChunkAt(pos)) {
            return false;
        }

        int distanceToAir = 0;
        while (!isAirOrReplaceablePlant(level.getBlockState(pos.above(distanceToAir))) && distanceToAir < 3) {
            distanceToAir++;
        }
        if (distanceToAir > 2) {
            return false;
        }

        BlockPos pos2 = pos.above(distanceToAir - 1);
        BlockState blockState = level.getBlockState(pos2);
        BlockState blockBelow = level.getBlockState(pos2.below());

        if (!isAirOrReplaceablePlant(level.getBlockState(pos2.above()))) {
            return false;
        }

        if (isValidSpawnBlock(blockState)) {
            return true;
        }

        return (blockState.is(Blocks.SNOW) || isAirOrReplaceablePlant(blockState))
                && isValidSpawnBlock(blockBelow);
    }

    private static boolean isAirOrReplaceablePlant(BlockState state) {
        return (state.isAir()
                || state.canBeReplaced()
                || state.is(BlockTags.FLOWERS)
                || state.is(BlockTags.REPLACEABLE_BY_TREES))
                && state.getFluidState().isEmpty();
    }

    public static boolean isValidSpawnBlock(BlockState state) {
        return state.is(Blocks.STONE)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.ROOTED_DIRT)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MOSS_BLOCK);
    }

    private static boolean canWriteBlock(WorldGenLevel level, BlockPos pos) {
        if (level.isOutsideBuildHeight(pos)) return false;
        if (level instanceof WorldGenRegion region) {
            if (!region.ensureCanWrite(pos)) {
                return false;
            }
            ChunkPos center = region.getCenter();
            int chunkX = SectionPos.blockToSectionCoord(pos.getX());
            int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
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
