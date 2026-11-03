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
import thaumcraft.common.world.features.TCCultistPortalFeature;

/**
 * Worldgen structure wrapper for the Thaumcraft 6 Lesser Crimson Portal ({@code EntityCultistPortalLesser}),
 * enabling {@code /locate structure thaumcraft:cultist_portal_lesser} in 1.21.1.
 */
public final class TCCultistPortalStructure extends Structure {
    public static final MapCodec<TCCultistPortalStructure> CODEC = simpleCodec(TCCultistPortalStructure::new);

    public TCCultistPortalStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        ChunkGenerator generator = context.chunkGenerator();
        int seaLevel = generator.getSeaLevel();

        int x = chunkPos.getMinBlockX() + 4 + context.random().nextInt(8);
        int z = chunkPos.getMinBlockZ() + 4 + context.random().nextInt(8);

        int oceanFloorY = generator.getBaseHeight(
                x, z, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        int worldSurfaceY = generator.getBaseHeight(
                x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        int groundY = oceanFloorY - 1;

        if (oceanFloorY != worldSurfaceY || oceanFloorY < seaLevel) {
            return Optional.empty();
        }

        NoiseColumn column = generator.getBaseColumn(x, z, context.heightAccessor(), context.randomState());
        BlockState groundState = column.getBlock(groundY);
        if (!TCCultistPortalFeature.isValidPortalGround(groundState)) {
            return Optional.empty();
        }

        BlockPos portalPos = new BlockPos(x, oceanFloorY, z);
        return Optional.of(new GenerationStub(portalPos, builder ->
                builder.addPiece(new Piece(x, oceanFloorY, z))));
    }

    @Override
    public StructureType<?> type() {
        return TCStructures.CULTIST_PORTAL_LESSER.get();
    }

    public static final class Piece extends StructurePiece {
        private final int portalX;
        private final int portalY;
        private final int portalZ;

        public Piece(int portalX, int portalY, int portalZ) {
            super(
                    TCStructures.CULTIST_PORTAL_PIECE.get(),
                    0,
                    new BoundingBox(
                            portalX,
                            portalY,
                            portalZ,
                            portalX,
                            portalY + 2,
                            portalZ
                    )
            );
            this.portalX = portalX;
            this.portalY = portalY;
            this.portalZ = portalZ;
        }

        public Piece(CompoundTag tag) {
            super(TCStructures.CULTIST_PORTAL_PIECE.get(), tag);
            this.portalX = tag.getInt("PortalX");
            this.portalY = tag.contains("PortalY") ? tag.getInt("PortalY") : this.boundingBox.minY();
            this.portalZ = tag.getInt("PortalZ");
            this.boundingBox = new BoundingBox(portalX, portalY, portalZ, portalX, portalY + 2, portalZ);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            tag.putInt("PortalX", this.portalX);
            tag.putInt("PortalY", this.portalY);
            tag.putInt("PortalZ", this.portalZ);
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
            BlockPos origin = new BlockPos(this.portalX, this.portalY, this.portalZ);
            if (box.isInside(origin)) {
                TCCultistPortalFeature.spawnPlannedPortal(level, random, origin);
            }
        }
    }
}
