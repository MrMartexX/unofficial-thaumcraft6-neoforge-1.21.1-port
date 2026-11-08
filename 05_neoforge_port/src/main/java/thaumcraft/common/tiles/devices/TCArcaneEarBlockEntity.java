package thaumcraft.common.tiles.devices;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import thaumcraft.common.blocks.devices.TCArcaneEarBlock;
import thaumcraft.common.blocks.devices.TCArcaneEarToggleBlock;
import thaumcraft.common.registry.TCBlockEntities;

public class TCArcaneEarBlockEntity extends BlockEntity {
    public static final Set<TCArcaneEarBlockEntity> LOADED_EARS =
            Collections.newSetFromMap(new WeakHashMap<>());

    private byte note = 0;
    private byte tone = 0;
    private int redstoneSignalTimer = 0;

    public TCArcaneEarBlockEntity(BlockPos pos, BlockState state) {
        super(state.getBlock() instanceof TCArcaneEarToggleBlock
                ? TCBlockEntities.ARCANE_EAR_TOGGLE.get()
                : TCBlockEntities.ARCANE_EAR.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        LOADED_EARS.add(this);
        if (this.level != null && !this.level.isClientSide) {
            updateTone();
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        LOADED_EARS.remove(this);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        LOADED_EARS.remove(this);
    }

    public byte getNote() {
        return this.note;
    }

    public byte getTone() {
        return this.tone;
    }

    public void updateTone() {
        if (this.level == null) return;
        BlockState state = getBlockState();
        if (!state.hasProperty(TCArcaneEarBlock.FACING)) return;
        Direction attachedTo = state.getValue(TCArcaneEarBlock.FACING).getOpposite();
        BlockPos supportPos = this.worldPosition.relative(attachedTo);
        BlockState supportState = this.level.getBlockState(supportPos);
        NoteBlockInstrument inst = supportState.instrument();
        this.tone = (byte) (inst.worksAboveNoteBlock() ? NoteBlockInstrument.HARP.ordinal() : inst.ordinal());
        setChanged();
        if (!this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    public int changePitch() {
        this.note = (byte) ((this.note + 1) % 25);
        setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
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
        Direction facing = state.getValue(TCArcaneEarBlock.FACING).getOpposite();
        this.level.updateNeighborsAt(this.worldPosition, state.getBlock());
        this.level.updateNeighborsAt(this.worldPosition.relative(facing), state.getBlock());

        // Note sound feedback
        float pitch = (float) Math.pow(2.0D, (double) (this.note - 12) / 12.0D);
        NoteBlockInstrument[] instruments = NoteBlockInstrument.values();
        NoteBlockInstrument inst = (this.tone >= 0 && this.tone < instruments.length)
                ? instruments[this.tone] : NoteBlockInstrument.HARP;
        this.level.playSound(null, this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D,
                inst.getSoundEvent().value(), SoundSource.BLOCKS, 3.0F, pitch);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TCArcaneEarBlockEntity entity) {
        if (!level.isClientSide && !(state.getBlock() instanceof TCArcaneEarToggleBlock)) {
            if (entity.redstoneSignalTimer > 0) {
                entity.redstoneSignalTimer--;
                if (entity.redstoneSignalTimer == 0 && state.getValue(TCArcaneEarBlock.ENABLED)) {
                    level.setBlock(pos, state.setValue(TCArcaneEarBlock.ENABLED, false), 3);
                    Direction facing = state.getValue(TCArcaneEarBlock.FACING).getOpposite();
                    level.updateNeighborsAt(pos, state.getBlock());
                    level.updateNeighborsAt(pos.relative(facing), state.getBlock());
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
