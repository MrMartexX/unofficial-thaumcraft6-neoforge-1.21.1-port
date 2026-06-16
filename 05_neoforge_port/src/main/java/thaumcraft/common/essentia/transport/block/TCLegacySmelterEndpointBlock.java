package thaumcraft.common.essentia.transport.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TCLegacySmelterEndpointBlock extends Block {
    private final TCLegacySmelterEndpoint endpoint;

    public TCLegacySmelterEndpointBlock(TCLegacySmelterEndpoint endpoint, BlockBehaviour.Properties properties) {
        super(properties);
        this.endpoint = endpoint;
    }

    public TCLegacySmelterEndpoint endpoint() {
        return endpoint;
    }
}