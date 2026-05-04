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

    private TCBlocks() {
    }
}