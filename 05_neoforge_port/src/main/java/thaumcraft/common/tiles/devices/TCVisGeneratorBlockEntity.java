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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.blocks.devices.TCVisGeneratorBlock;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.world.aura.AuraHandler;

/**
 * TC6 Vis Generator tile entity.
 * Drains 1.0F vis from aura when empty to generate 1000 FE, pushing up to 20 FE/t
 * into the adjacent energy-receiving capability in the facing direction.
 */
public class TCVisGeneratorBlockEntity extends BlockEntity implements IEnergyStorage {
    public static final int CAPACITY = 1000;
    public static final int MAX_EXTRACT = 20;

    private int energy = 0;

    public TCVisGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.VIS_GENERATOR.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCVisGeneratorBlockEntity gen) {
        if (!level.isClientSide) {
            gen.tickServer(state);
        }
    }

    private void tickServer(BlockState state) {
        if (level == null || level.isClientSide) {
            return;
        }

        // Recharge from aura when empty
        if (energy == 0) {
            float vis = AuraHandler.drainVis(level, worldPosition, 1.0F, false);
            energy = (int) (vis * 1000.0F);
            if (energy > 0) {
                setChanged();
                level.sendBlockUpdated(worldPosition, state, state, 3);
            }
        }

        // Push energy to facing block
        if (energy > 0 && state.hasProperty(TCVisGeneratorBlock.FACING)) {
            Direction facing = state.getValue(TCVisGeneratorBlock.FACING);
            BlockPos targetPos = worldPosition.relative(facing);
            IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, facing.getOpposite());
            if (target != null && target.canReceive()) {
                int toPush = Math.min(energy, MAX_EXTRACT);
                int accepted = target.receiveEnergy(toPush, false);
                if (accepted > 0) {
                    energy -= accepted;
                    setChanged();
                    if (energy == 0) {
                        level.sendBlockUpdated(worldPosition, state, state, 3);
                    }
                }
            }
        }
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergyForTesting(int amount) {
        this.energy = Math.max(0, Math.min(CAPACITY, amount));
        setChanged();
    }

    public IEnergyStorage energyStorage(@Nullable Direction side) {
        if (side == null || !getBlockState().hasProperty(TCVisGeneratorBlock.FACING)
                || side == getBlockState().getValue(TCVisGeneratorBlock.FACING)) {
            return this;
        }
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("energy", energy);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy = tag.getInt("energy");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("energy", energy);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0; // Generation only, does not accept incoming external FE
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int toExtract = Math.min(energy, Math.min(maxExtract, MAX_EXTRACT));
        if (!simulate && toExtract > 0) {
            energy -= toExtract;
            setChanged();
        }
        return toExtract;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return CAPACITY;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
