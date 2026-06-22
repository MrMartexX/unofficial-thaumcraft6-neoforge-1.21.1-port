package thaumcraft.common.essentia.transport.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.essentia.transport.block.TCLegacySmelterEndpoint;
import thaumcraft.common.tiles.essentia.TCSmelterBlockEntity;

/**
 * Tiered smelter block entity retaining the existing registry class name.
 *
 * <p>TC6 smelters are internal slurry stores, not essentia transport endpoints. Essentia leaves
 * through Alembics above the smelter or an attached auxiliary pump.</p>
 */
public final class TCLegacySmelterEndpointBlockEntity extends TCSmelterBlockEntity {
    private final TCLegacySmelterEndpoint endpoint;

    public TCLegacySmelterEndpointBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            TCLegacySmelterEndpoint endpoint
    ) {
        super(type, pos, state, smelterType(endpoint));
        this.endpoint = endpoint;
    }

    private static SmelterType smelterType(TCLegacySmelterEndpoint endpoint) {
        return switch (endpoint) {
            case THAUMIUM -> SmelterType.THAUMIUM;
            case VOID -> SmelterType.VOID;
        };
    }

    public TCLegacySmelterEndpoint endpoint() {
        return endpoint;
    }
}
