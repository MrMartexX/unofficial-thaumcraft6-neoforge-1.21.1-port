package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.blocks.devices.TCArcaneEarBlock;
import thaumcraft.common.blocks.devices.TCArcaneEarToggleBlock;
import thaumcraft.common.registry.TCBlockEntities;

public class TCArcaneEarBlockEntity extends BlockEntity {
    private byte note = 0;
    private byte tone = 0;
    private int redstoneSignalTimer = 0;

    public TCArcaneEarBlockEntity(BlockPos pos, BlockState state) {
        super(state.getBlock() instanceof TCArcaneEarToggleBlock
                ? TCBlockEntities.ARCANE_EAR_TOGGLE.get()
                : TCBlockEntities.ARCANE_EAR.get(), pos, state);
    }

    public byte getNote() {
        return this.note;
    }

    public byte getTone() {
        return this.tone;
    }

    public int changePitch() {
        this.note = (byte) ((this.note + 1) % 25);
        setChanged();
        return this.note;
    }

    public void trigger() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        BlockState state = getBlockState();
        if (state.getBlock() instanceof TCArcaneEarToggleBlock) {
            boolean next = !state.getValue(TCArcaneEarBlock.ENABLED);
            this.level.setBlock(this.worldPosition, state.setValue(TCArcaneEarBlock.ENABLED, next), 3);
        } else {
            this.redstoneSignalTimer = 10;
            this.level.setBlock(this.worldPosition, state.setValue(TCArcaneEarBlock.ENABLED, true), 3);
        }
        this.level.updateNeighborsAt(this.worldPosition, state.getBlock());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TCArcaneEarBlockEntity entity) {
        if (!level.isClientSide && !(state.getBlock() instanceof TCArcaneEarToggleBlock)) {
            if (entity.redstoneSignalTimer > 0) {
                entity.redstoneSignalTimer--;
                if (entity.redstoneSignalTimer == 0 && state.getValue(TCArcaneEarBlock.ENABLED)) {
                    level.setBlock(pos, state.setValue(TCArcaneEarBlock.ENABLED, false), 3);
                    level.updateNeighborsAt(pos, state.getBlock());
                }
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.note = tag.getByte("note");
        this.tone = tag.getByte("tone");
        this.redstoneSignalTimer = tag.getInt("timer");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putByte("note", this.note);
        tag.putByte("tone", this.tone);
        tag.putInt("timer", this.redstoneSignalTimer);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putByte("note", this.note);
        tag.putByte("tone", this.tone);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
