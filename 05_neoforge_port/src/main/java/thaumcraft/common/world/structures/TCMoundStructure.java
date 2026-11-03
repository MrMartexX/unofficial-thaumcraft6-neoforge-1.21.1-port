package thaumcraft.common.world.structures;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import thaumcraft.common.registry.TCStructures;
import thaumcraft.common.world.features.TCMoundFeature;

/**
 * Worldgen structure wrapper for the Thaumcraft 6 Ancient Mound ({@code WorldGenMound}),
 * allowing players to find mounds via {@code /locate structure thaumcraft:mound} while
 * validating the 19x19 footprint flatness and dry-land requirements up front.
 */
public final class TCMoundStructure extends Structure {
    public static final MapCodec<TCMoundStructure> CODEC = simpleCodec(TCMoundStructure::new);

    private static final int[][] CHECK_OFFSETS = {
            {0, 0},
            {9, 9},
            {18, 0},
            {18, 18},
            {0, 18}
    };

    public TCMoundStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int chunkMinX = chunkPos.getMinBlockX();
        int chunkMinZ = chunkPos.getMinBlockZ();

        // One candidate per selected chunk, as in ThaumcraftWorldGenerator.generateSurface.
        int i = chunkMinX + 4 + context.random().nextInt(9);
        int k = chunkMinZ + 4 + context.random().nextInt(9);
        int groundY = validateCandidateFootprint(context, i, k);
        if (groundY != Integer.MIN_VALUE
                && groundY - 9 >= context.heightAccessor().getMinBuildHeight()
                && groundY + 7 < context.heightAccessor().getMaxBuildHeight()) {
            BlockPos locatePos = new BlockPos(i + 9, groundY, k + 9);
            long seed = context.random().nextLong();
            return Optional.of(new GenerationStub(locatePos, builder ->
                    builder.addPiece(new Piece(i, groundY, k, seed))));
        }
        return Optional.empty();
    }

    private static int validateCandidateFootprint(GenerationContext context, int i, int k) {
        ChunkGenerator generator = context.chunkGenerator();
        int seaLevel = generator.getSeaLevel();

        int baseOceanFloor = generator.getBaseHeight(
                i, k, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        int baseWorldSurface = generator.getBaseHeight(
                i, k, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        int baseGroundY = baseOceanFloor - 1;
        if (baseOceanFloor != baseWorldSurface || baseGroundY < seaLevel) {
            return Integer.MIN_VALUE;
        }

        for (int[] offset : CHECK_OFFSETS) {
            int x = i + offset[0];
            int z = k + offset[1];
            int oceanFloor = generator.getBaseHeight(
                    x, z, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
            int worldSurface = generator.getBaseHeight(
                    x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
            int groundY = oceanFloor - 1;
            if (oceanFloor != worldSurface || groundY < seaLevel || Math.abs(groundY - baseGroundY) > 1) {
                return Integer.MIN_VALUE;
            }
            NoiseColumn column = generator.getBaseColumn(x, z, context.heightAccessor(), context.randomState());
            BlockState groundState = column.getBlock(groundY);
            if (!TCMoundFeature.isValidSpawnBlock(groundState)) {
                return Integer.MIN_VALUE;
            }
        }

        return baseGroundY;
    }

    @Override
    public StructureType<?> type() {
        return TCStructures.MOUND.get();
    }

    public static final class Piece extends StructurePiece {
        private final int moundX;
        private final int groundY;
        private final int moundZ;
        private final long seed;

        public Piece(int moundX, int groundY, int moundZ, long seed) {
            super(
                    TCStructures.MOUND_PIECE.get(),
                    0,
                    TCMoundFeature.bounds(moundX, groundY, moundZ)
            );
            this.moundX = moundX;
            this.groundY = groundY;
            this.moundZ = moundZ;
            this.seed = seed;
        }

        public Piece(CompoundTag tag) {
            super(TCStructures.MOUND_PIECE.get(), tag);
            this.moundX = tag.getInt("MoundX");
            this.groundY = tag.contains("MoundY") ? tag.getInt("MoundY") : this.boundingBox.minY();
            this.moundZ = tag.getInt("MoundZ");
            this.seed = tag.contains("Seed") ? tag.getLong("Seed") : new BlockPos(moundX, groundY, moundZ).asLong();
            this.boundingBox = TCMoundFeature.bounds(moundX, groundY, moundZ);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            tag.putInt("MoundX", this.moundX);
            tag.putInt("MoundY", this.groundY);
            tag.putInt("MoundZ", this.moundZ);
            tag.putLong("Seed", this.seed);
        }

        @Override
        public void postProcess(
                WorldGenLevel level,
                StructureManager structureManager,
                ChunkGenerator generator,
                RandomSource random,
                BoundingBox box,
                ChunkPos chunkPos,
                BlockPos pos
        ) {
            TCMoundFeature.placeSlice(level, new BlockPos(this.moundX, this.groundY - 8, this.moundZ), box, this.seed);
        }
    }
}
