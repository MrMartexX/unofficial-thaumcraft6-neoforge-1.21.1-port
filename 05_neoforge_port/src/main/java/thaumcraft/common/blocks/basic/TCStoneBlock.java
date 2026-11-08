package thaumcraft.common.blocks.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TCStoneBlock extends Block {
    private final boolean beaconBase;

    public TCStoneBlock(BlockBehaviour.Properties properties) {
        this(properties, true);
    }

    public TCStoneBlock(BlockBehaviour.Properties properties, boolean beaconBase) {
        super(properties);
        this.beaconBase = beaconBase;
    }

    public boolean isBeaconBase() {
        return beaconBase;
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return state.getDestroySpeed(level, pos) >= 0.0F;
    }
}
