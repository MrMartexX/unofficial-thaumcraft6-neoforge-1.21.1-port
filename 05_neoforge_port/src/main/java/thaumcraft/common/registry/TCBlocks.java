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
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.25F, 0.25F)
                    .noOcclusion())
    );

    public static final Supplier<Block> CRYSTAL_IGNIS = BLOCKS.register("crystal_ignis", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.25F, 0.25F)
                    .noOcclusion())
    );

    public static final Supplier<Block> CRYSTAL_AQUA = BLOCKS.register("crystal_aqua", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.25F, 0.25F)
                    .noOcclusion())
    );

    public static final Supplier<Block> CRYSTAL_TERRA = BLOCKS.register("crystal_terra", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.25F, 0.25F)
                    .noOcclusion())
    );

    public static final Supplier<Block> CRYSTAL_ORDO = BLOCKS.register("crystal_ordo", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.25F, 0.25F)
                    .noOcclusion())
    );

    public static final Supplier<Block> CRYSTAL_PERDITIO = BLOCKS.register("crystal_perditio", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.25F, 0.25F)
                    .noOcclusion())
    );

    public static final Supplier<Block> CRYSTAL_VITIUM = BLOCKS.register("crystal_vitium", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                    .strength(0.25F, 0.25F)
                    .noOcclusion())
    );

    public static final Supplier<Block> STONE_ARCANE = BLOCKS.register("stone_arcane", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(2.0F, 10.0F)
                    .requiresCorrectToolForDrops())
    );

    public static final Supplier<Block> STONE_ARCANE_BRICK = BLOCKS.register("stone_arcane_brick", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)
                    .strength(2.0F, 10.0F)
                    .requiresCorrectToolForDrops())
    );

    public static final Supplier<Block> STONE_ANCIENT = BLOCKS.register("stone_ancient", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(2.0F, 10.0F)
                    .requiresCorrectToolForDrops())
    );

    private TCBlocks() {
    }
}