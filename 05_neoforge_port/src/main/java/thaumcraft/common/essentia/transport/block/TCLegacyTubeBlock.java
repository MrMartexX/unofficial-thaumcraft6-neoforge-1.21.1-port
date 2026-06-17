package thaumcraft.common.essentia.transport.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlockEntities;

import javax.annotation.Nullable;

/**
 * Common legacy-style tube block shell.
 */
public class TCLegacyTubeBlock extends Block implements EntityBlock {
    private final TCLegacyTubeVariant variant;

    public TCLegacyTubeBlock(TCLegacyTubeVariant variant, BlockBehaviour.Properties properties) {
        super(properties);
        this.variant = variant;
    }

    public TCLegacyTubeVariant variant() {
        return variant;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TCBlockEntities.createTubeBlockEntity(variant, pos, state);
    }
}