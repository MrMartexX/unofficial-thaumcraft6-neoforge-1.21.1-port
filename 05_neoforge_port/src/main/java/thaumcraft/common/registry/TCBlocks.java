package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class TCBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Thaumcraft.MODID);

    public static final Supplier<Block> ORE_AMBER = BLOCKS.register("ore_amber", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(1.5F, 3.0F)
                    .requiresCorrectToolForDrops())
    );

    public static final Supplier<Block> ORE_CINNABAR = BLOCKS.register("ore_cinnabar", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(2.0F, 3.0F)
                    .requiresCorrectToolForDrops())
    );

    public static final Supplier<Block> ORE_QUARTZ = BLOCKS.register("ore_quartz", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(3.0F, 3.0F)
                    .requiresCorrectToolForDrops())
    );

    // Temporary inert visual placeholders. Real BlockCrystal behavior will be rebuilt later.
    public static final Supplier<Block> CRYSTAL_AER = BLOCKS.register("crystal_aer", () ->
            crystalPlaceholder()
    );

    public static final Supplier<Block> CRYSTAL_IGNIS = BLOCKS.register("crystal_ignis", () ->
            crystalPlaceholder()
    );

    public static final Supplier<Block> CRYSTAL_AQUA = BLOCKS.register("crystal_aqua", () ->
            crystalPlaceholder()
    );

    public static final Supplier<Block> CRYSTAL_TERRA = BLOCKS.register("crystal_terra", () ->
            crystalPlaceholder()
    );

    public static final Supplier<Block> CRYSTAL_ORDO = BLOCKS.register("crystal_ordo", () ->
            crystalPlaceholder()
    );

    public static final Supplier<Block> CRYSTAL_PERDITIO = BLOCKS.register("crystal_perditio", () ->
            crystalPlaceholder()
    );

    public static final Supplier<Block> CRYSTAL_VITIUM = BLOCKS.register("crystal_vitium", () ->
            crystalPlaceholder()
    );

    public static final Supplier<Block> STONE_ARCANE = BLOCKS.register("stone_arcane", () ->
            stoneBlock(2.0F, 10.0F)
    );

    public static final Supplier<Block> STONE_ARCANE_BRICK = BLOCKS.register("stone_arcane_brick", () ->
            stoneBlock(2.0F, 10.0F)
    );

    public static final Supplier<Block> STONE_ANCIENT = BLOCKS.register("stone_ancient", () ->
            stoneBlock(2.0F, 10.0F)
    );

    public static final Supplier<Block> STONE_ANCIENT_TILE = BLOCKS.register("stone_ancient_tile", () ->
            stoneBlock(2.0F, 10.0F)
    );

    public static final Supplier<Block> STONE_ANCIENT_ROCK = BLOCKS.register("stone_ancient_rock", () ->
            stoneBlock(50.0F, 1200.0F)
    );

    public static final Supplier<Block> STONE_ANCIENT_GLYPHED = BLOCKS.register("stone_ancient_glyphed", () ->
            stoneBlock(2.0F, 10.0F)
    );

    public static final Supplier<Block> STONE_ANCIENT_DOORWAY = BLOCKS.register("stone_ancient_doorway", () ->
            stoneBlock(50.0F, 1200.0F)
    );

    public static final Supplier<Block> STONE_ELDRITCH_TILE = BLOCKS.register("stone_eldritch_tile", () ->
            stoneBlock(15.0F, 1000.0F)
    );

    public static final Supplier<Block> STONE_POROUS = BLOCKS.register("stone_porous", () ->
            stoneBlock(1.5F, 6.0F)
    );

    private static Block stoneBlock(float strength, float resistance) {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                .strength(strength, resistance)
                .requiresCorrectToolForDrops());
    }

    private static Block crystalPlaceholder() {
        return new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .strength(0.25F, 0.25F)
                .noOcclusion()
                .noCollission()
                .lightLevel(state -> 1));
    }

    private TCBlocks() {
    }
}