package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.devices.TCInlayNetwork;
import thaumcraft.common.registry.TCBlockEntities;

public final class TCStabilizerBlockEntity extends BlockEntity {
    public static final int CAPACITY = 15;
    private int ticks;
    private int energy;

    public TCStabilizerBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.STABILIZER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCStabilizerBlockEntity stabilizer) {
        stabilizer.ticks++;
        if (stabilizer.energy < CAPACITY && stabilizer.ticks % 20 == 0) {
            stabilizer.energy++;
            AuraHelper.polluteAura(level, pos, 0.25F, true);
            stabilizer.markChangedAndSync();
            if (level instanceof ServerLevel serverLevel) {
                TCInlayNetwork.recalculateAround(serverLevel, pos);
            }
        }
        // Flux-rift stabilization is activated when the rift entity subsystem is registered.
    }

    public int getEnergy() {
        return energy;
    }

    public boolean mitigate(int amount) {
        if (amount < 0 || energy < amount) {
            return false;
        }
        energy -= amount;
        markChangedAndSync();
        if (level instanceof ServerLevel serverLevel) {
            TCInlayNetwork.recalculateAround(serverLevel, worldPosition);
        }
        return true;
    }

    public void setEnergyForValidation(int energy) {
        this.energy = Math.max(0, Math.min(CAPACITY, energy));
        markChangedAndSync();
    }

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("energy", energy);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy = Math.max(0, Math.min(CAPACITY, tag.getInt("energy")));
    }
}
