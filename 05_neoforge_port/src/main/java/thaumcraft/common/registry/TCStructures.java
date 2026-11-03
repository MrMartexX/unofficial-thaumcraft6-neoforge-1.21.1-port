package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.world.structures.TCCultistPortalStructure;
import thaumcraft.common.world.structures.TCMoundStructure;

/**
 * Structure and StructurePiece registrations enabling {@code /locate structure thaumcraft:mound}
 * and {@code /locate structure thaumcraft:cultist_portal_lesser}.
 */
public final class TCStructures {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, Thaumcraft.MODID);

    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, Thaumcraft.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<TCMoundStructure>> MOUND =
            STRUCTURE_TYPES.register("mound", () -> () -> TCMoundStructure.CODEC);

    public static final DeferredHolder<StructureType<?>, StructureType<TCCultistPortalStructure>> CULTIST_PORTAL_LESSER =
            STRUCTURE_TYPES.register("cultist_portal_lesser", () -> () -> TCCultistPortalStructure.CODEC);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> MOUND_PIECE =
            STRUCTURE_PIECES.register("mound_piece", () -> (StructurePieceType.ContextlessType) TCMoundStructure.Piece::new);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> CULTIST_PORTAL_PIECE =
            STRUCTURE_PIECES.register("cultist_portal_piece", () -> (StructurePieceType.ContextlessType) TCCultistPortalStructure.Piece::new);

    private TCStructures() {
    }
}
