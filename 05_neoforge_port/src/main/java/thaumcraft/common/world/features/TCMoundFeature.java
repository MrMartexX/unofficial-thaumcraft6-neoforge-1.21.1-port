package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import java.io.InputStream;
import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
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

        byte[] data = getMoundData();
        if (data.length == 0 || data.length % 4 != 0) {
            return false;
        }

        // Validate spawn suitability across 4 corners and center at surface height (origin.getY() + 9)
        // Offset down by 9 so origin is at surface and chambers are subterranean (matching legacy p = surface.down(9))
        int i = origin.getX();
        int j = origin.getY() - 9;
        int k = origin.getZ();

        if (!locationIsValidSpawn(level, new BlockPos(i + 9, j + 9, k + 9))
                || !locationIsValidSpawn(level, new BlockPos(i, j + 9, k))
                || !locationIsValidSpawn(level, new BlockPos(i + 18, j + 9, k))
                || !locationIsValidSpawn(level, new BlockPos(i + 18, j + 9, k + 18))
                || !locationIsValidSpawn(level, new BlockPos(i, j + 9, k + 18))) {
            return false;
        }

        Block loot1 = pickLootContainer(random);
        Block loot2 = pickLootContainer(random);
        boolean trapped = random.nextInt(3) == 0;

        int recordCount = data.length / 4;
        for (int idx = 0; idx < recordCount; idx++) {
            int offset = idx * 4;
            int dx = data[offset] & 0xFF;
            int dy = (data[offset + 1] & 0xFF) - 1;
            int dz = data[offset + 2] & 0xFF;
            int palette = data[offset + 3] & 0xFF;

            BlockPos target = new BlockPos(i + dx, j + dy, k + dz);
            if (!canWriteBlock(level, target)) {
                continue;
            }

            placeMoundBlock(level, random, target, palette, loot1, loot2, trapped, dx, dz);
        }

        return true;
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
            case 0 -> level.setBlock(target, Blocks.AIR.defaultBlockState(), 3);
            case 1 -> level.setBlock(target, Blocks.DIRT.defaultBlockState(), 3);
            case 2 -> level.setBlock(target, Blocks.COBBLESTONE.defaultBlockState(), 3);
            case 3 -> level.setBlock(target, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3);
            case 4 -> level.setBlock(target, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
            case 5 -> level.setBlock(target, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 3);
            case 6 -> level.setBlock(target, Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM), 3);
            case 7 -> level.setBlock(target, Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM), 3);
            case 8 -> level.setBlock(target, Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.BOTTOM), 3);
            case 9 -> level.setBlock(target, Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP), 3);
            case 10 -> level.setBlock(target, Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP), 3);
            case 11 -> level.setBlock(target, Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.TOP), 3);
            case 12 -> level.setBlock(target, Blocks.COBBLESTONE_STAIRS.defaultBlockState()
                    .setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.TOP), 3);
            case 13 -> level.setBlock(target, Blocks.IRON_BARS.defaultBlockState(), 3);
            case 14 -> level.setBlock(target, Blocks.LADDER.defaultBlockState()
                    .setValue(LadderBlock.FACING, Direction.EAST), 3);
            case 15 -> level.setBlock(target, Blocks.SHORT_GRASS.defaultBlockState(), 3);
            case 16 -> {
                if (trapped) {
                    level.setBlock(target, Blocks.TNT.defaultBlockState(), 3);
                } else {
                    level.setBlock(target, Blocks.COBBLESTONE.defaultBlockState(), 3);
                }
            }
            case 17 -> {
                level.setBlock(target, Blocks.SPAWNER.defaultBlockState(), 3);
                BlockEntity be = level.getBlockEntity(target);
                if (be instanceof SpawnerBlockEntity spawner) {
                    EntityType<?> mobType = (dx == 4 && dz == 4) ? EntityType.SKELETON : EntityType.ZOMBIE;
                    spawner.setEntityId(mobType, level.getRandom());
                }
            }
            case 18 -> level.setBlock(target, loot1.defaultBlockState(), 3);
            case 19 -> level.setBlock(target, loot2.defaultBlockState(), 3);
            case 20, 21 -> {
                BlockState chestState = (trapped ? Blocks.TRAPPED_CHEST : Blocks.CHEST).defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.WEST);
                level.setBlock(target, chestState, 3);
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
        while (!level.getBlockState(pos.above(distanceToAir)).isAir() && distanceToAir < 3) {
            distanceToAir++;
        }
        if (distanceToAir > 2) {
            return false;
        }

        BlockPos pos2 = pos.above(distanceToAir - 1);
        BlockState blockState = level.getBlockState(pos2);
        BlockState blockBelow = level.getBlockState(pos2.below());

        if (!level.getBlockState(pos2.above()).isAir()) {
            return false;
        }

        if (isValidSpawnBlock(blockState)) {
            return true;
        }

        return (blockState.is(Blocks.SNOW) || blockState.is(Blocks.SHORT_GRASS))
                && isValidSpawnBlock(blockBelow);
    }

    private static boolean isValidSpawnBlock(BlockState state) {
        return state.is(Blocks.STONE)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.DIRT);
    }

    private static boolean canWriteBlock(WorldGenLevel level, BlockPos pos) {
        if (level instanceof WorldGenRegion region) {
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
