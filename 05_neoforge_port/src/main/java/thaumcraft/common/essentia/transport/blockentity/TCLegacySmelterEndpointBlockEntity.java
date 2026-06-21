package thaumcraft.common.essentia.transport.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.essentia.transport.TCLegacyEssentiaTransportNode;
import thaumcraft.common.essentia.transport.block.TCLegacySmelterEndpoint;
import thaumcraft.common.tiles.essentia.TCSmelterBlockEntity;

import java.util.Map;

/**
 * Bridge block entity for the legacy thaumium/void smelter ids.
 *
 * These blocks still expose their transport endpoint node for the current tube audit boundary, while also
 * owning real smelter machine state through {@link TCSmelterBlockEntity}. This keeps transport compatibility
 * while making upgraded smelters eligible for the same fuel/cook/aspect/Alembic path as the basic smelter.
 */
public class TCLegacySmelterEndpointBlockEntity extends TCSmelterBlockEntity implements TCEssentiaTransport {
    private final TCLegacySmelterEndpoint endpoint;
    private final TCLegacyEssentiaTransportNode transportNode;

    public TCLegacySmelterEndpointBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, TCLegacySmelterEndpoint endpoint) {
        super(type, pos, state, smelterType(endpoint));
        this.endpoint = endpoint;
        this.transportNode = new TCLegacyEssentiaTransportNode(endpoint.mode(), endpoint.storageCapacity());
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

    public TCLegacyEssentiaTransportNode transportNode() {
        return transportNode;
    }

    private void markTransportDirty() {
        setChanged();
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
            level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Endpoint", endpoint.name());
        tag.putString("Mode", transportNode.mode().name());
        tag.putBoolean("ValveOpen", transportNode.isValveOpen());

        CompoundTag essentiaTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : transportNode.storage().snapshot().entrySet()) {
            essentiaTag.putInt(entry.getKey(), entry.getValue());
        }
        tag.put("EndpointEssentia", essentiaTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Mode")) {
            try {
                transportNode.setMode(thaumcraft.common.essentia.transport.TCEssentiaTubeMode.valueOf(tag.getString("Mode")));
            } catch (IllegalArgumentException ignored) {
                // Keep constructor mode if saved data is invalid.
            }
        }
        if (tag.contains("ValveOpen")) {
            transportNode.setValveOpen(tag.getBoolean("ValveOpen"));
        }
        transportNode.mutableStorage().clear();
        CompoundTag essentiaTag = tag.contains("EndpointEssentia") ? tag.getCompound("EndpointEssentia") : tag.getCompound("Essentia");
        for (String key : essentiaTag.getAllKeys()) {
            transportNode.mutableStorage().set(key, essentiaTag.getInt(key));
        }
    }

    @Override
    public boolean isConnectable(Direction face) {
        return transportNode.isConnectable(face);
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return transportNode.canInputFrom(face);
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return transportNode.canOutputTo(face);
    }

    @Override
    public TCEssentiaSuction getSuction(Direction face) {
        return transportNode.getSuction(face);
    }

    @Override
    public int getMinimumSuction() {
        return transportNode.getMinimumSuction();
    }

    @Override
    public TCEssentiaStack getEssentia(Direction face) {
        return transportNode.getEssentia(face);
    }

    @Override
    public int addEssentia(String aspect, int amount, Direction face, boolean simulate) {
        int accepted = transportNode.addEssentia(aspect, amount, face, simulate);
        if (!simulate && accepted > 0) {
            markTransportDirty();
        }
        return accepted;
    }

    @Override
    public int takeEssentia(String aspect, int amount, Direction face, boolean simulate) {
        int taken = transportNode.takeEssentia(aspect, amount, face, simulate);
        if (!simulate && taken > 0) {
            markTransportDirty();
        }
        return taken;
    }
}
