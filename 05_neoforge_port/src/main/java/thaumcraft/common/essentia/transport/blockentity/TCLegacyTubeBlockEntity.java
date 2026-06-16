package thaumcraft.common.essentia.transport.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.essentia.transport.block.TCLegacyTubeVariant;

public class TCLegacyTubeBlockEntity extends TCAbstractEssentiaTransportBlockEntity {
    private final TCLegacyTubeVariant variant;

    public TCLegacyTubeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, TCLegacyTubeVariant variant) {
        super(type, pos, state, variant.mode(), variant.storageCapacity());
        this.variant = variant;
    }

    public TCLegacyTubeVariant variant() {
        return variant;
    }
}