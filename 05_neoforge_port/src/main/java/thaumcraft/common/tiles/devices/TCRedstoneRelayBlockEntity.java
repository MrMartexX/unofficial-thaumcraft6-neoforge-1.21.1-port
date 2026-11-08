package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlockEntities;

public class TCRedstoneRelayBlockEntity extends BlockEntity {
    private int in = 1;
    private int out = 15;

    public TCRedstoneRelayBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.REDSTONE_RELAY.get(), pos, state);
    }

    public int getIn() {
        return in;
    }

    public void setIn(int in) {
        this.in = Math.max(1, Math.min(15, in));
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public int getOut() {
        return out;
    }

    public void setOut(int out) {
        this.out = Math.max(1, Math.min(15, out));
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void increaseIn() {
        int next = in + 1;
        if (next > 15) {
            next = 1;
        }
        setIn(next);
    }

    public void increaseOut() {
        int next = out + 1;
        if (next > 15) {
            next = 1;
        }
        setOut(next);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("in", in);
        tag.putInt("out", out);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("in")) {
            in = tag.getInt("in");
        }
        if (tag.contains("out")) {
            out = tag.getInt("out");
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("in", in);
        tag.putInt("out", out);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
