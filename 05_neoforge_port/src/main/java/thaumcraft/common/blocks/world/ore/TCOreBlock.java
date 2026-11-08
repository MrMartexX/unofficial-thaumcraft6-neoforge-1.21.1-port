package thaumcraft.common.blocks.world.ore;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TCOreBlock extends DropExperienceBlock {
    public TCOreBlock(UniformInt xpRange, BlockBehaviour.Properties properties) {
        super(xpRange, properties);
    }
}
