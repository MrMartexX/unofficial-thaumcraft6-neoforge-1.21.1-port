package thaumcraft.common.essentia.transport.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blocks.essentia.TCSmelterBlock;
import thaumcraft.common.registry.TCBlockEntities;

import javax.annotation.Nullable;

public class TCLegacySmelterEndpointBlock extends TCSmelterBlock implements EntityBlock {
    private final TCLegacySmelterEndpoint endpoint;

    public TCLegacySmelterEndpointBlock(TCLegacySmelterEndpoint endpoint, BlockBehaviour.Properties properties) {
        super(properties);
        this.endpoint = endpoint;
    }

    public TCLegacySmelterEndpoint endpoint() {
        return endpoint;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TCBlockEntities.createSmelterEndpointBlockEntity(endpoint, pos, state);
    }
}
