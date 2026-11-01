package thaumcraft.common.blocks.misc;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;

public class TCFleshBlock extends Block {
    public TCFleshBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.SLIME_BLOCK)
                .strength(0.25F, 2.0F));
    }
}