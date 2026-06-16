package thaumcraft.common.essentia.transport.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.essentia.transport.block.TCLegacySmelterEndpoint;

public class TCLegacySmelterEndpointBlockEntity extends TCAbstractEssentiaTransportBlockEntity {
    private final TCLegacySmelterEndpoint endpoint;

    public TCLegacySmelterEndpointBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, TCLegacySmelterEndpoint endpoint) {
        super(type, pos, state, endpoint.mode(), endpoint.storageCapacity());
        this.endpoint = endpoint;
    }

    public TCLegacySmelterEndpoint endpoint() {
        return endpoint;
    }
}