package thaumcraft.common.essentia.transport.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.essentia.transport.TCEssentiaTubeMode;
import thaumcraft.common.essentia.transport.TCLegacyEssentiaTransportNode;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Base BlockEntity skeleton for the modern transport pass.
 *
 * Legacy concepts are delegated to TCLegacyEssentiaTransportNode, keeping the block entity focused
 * on persistence, sync and ticking.
 */
public abstract class TCAbstractEssentiaTransportBlockEntity extends BlockEntity implements TCEssentiaTransport {
    protected final TCLegacyEssentiaTransportNode transportNode;

    protected TCAbstractEssentiaTransportBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            TCEssentiaTubeMode mode,
            int capacity
    ) {
        super(type, pos, state);
        this.transportNode = new TCLegacyEssentiaTransportNode(mode, capacity);
    }

    public TCLegacyEssentiaTransportNode transportNode() {
        return transportNode;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCAbstractEssentiaTransportBlockEntity blockEntity) {
        if (level == null || level.isClientSide) return;
        blockEntity.tickTransportServer();
    }

    protected void tickTransportServer() {
        setChanged();
    }

    protected void markTransportDirty() {
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
        tag.putString("Mode", transportNode.mode().name());
        tag.putBoolean("ValveOpen", transportNode.isValveOpen());

        CompoundTag essentiaTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : transportNode.storage().snapshot().entrySet()) {
            essentiaTag.putInt(entry.getKey(), entry.getValue());
        }
        tag.put("Essentia", essentiaTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Mode")) {
            try {
                transportNode.setMode(TCEssentiaTubeMode.valueOf(tag.getString("Mode")));
            } catch (IllegalArgumentException ignored) {
                // Keep constructor mode if saved data is invalid.
            }
        }
        if (tag.contains("ValveOpen")) {
            transportNode.setValveOpen(tag.getBoolean("ValveOpen"));
        }
        transportNode.mutableStorage().clear();
        if (tag.contains("Essentia")) {
            CompoundTag essentiaTag = tag.getCompound("Essentia");
            for (String key : essentiaTag.getAllKeys()) {
                transportNode.mutableStorage().set(key, essentiaTag.getInt(key));
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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
        if (!simulate && accepted > 0) markTransportDirty();
        return accepted;
    }

    @Override
    public int takeEssentia(String aspect, int amount, Direction face, boolean simulate) {
        int taken = transportNode.takeEssentia(aspect, amount, face, simulate);
        if (!simulate && taken > 0) markTransportDirty();
        return taken;
    }
}
