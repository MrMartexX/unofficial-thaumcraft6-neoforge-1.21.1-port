package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.essentia.TCAlembicBlock;
import thaumcraft.common.essentia.container.TCAspectSourceContainer;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.registry.TCBlockEntities;

/** Legacy-shaped Alembic storage/output endpoint. Full smelter production remains separate. */
public final class TCAlembicBlockEntity extends BlockEntity implements TCAspectSourceContainer, TCEssentiaTransport {
    public static final int CAPACITY = 128;

    private Aspect aspect;
    private Aspect aspectFilter;
    private int amount;
    private boolean blocked;

    public TCAlembicBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.ALEMBIC.get(), pos, state);
    }

    public Aspect storedAspect() {
        return aspect;
    }

    public Aspect aspectFilter() {
        return aspectFilter;
    }

    public int storedAmount() {
        return amount;
    }

    public void setStoredForValidation(Aspect newAspect, int newAmount) {
        aspect = newAspect;
        amount = newAspect == null ? 0 : Math.max(0, Math.min(CAPACITY, newAmount));
        if (amount == 0) {
            aspect = null;
        }
        markChangedAndSync();
    }

    public void setFilterForValidation(Aspect filter) {
        aspectFilter = filter;
        markChangedAndSync();
    }

    public void setBlockedForValidation(boolean value) {
        blocked = value;
        markChangedAndSync();
    }

    public int addToContainer(Aspect requestedAspect, int requestedAmount) {
        if (requestedAspect == null || requestedAmount <= 0 || blocked) {
            return requestedAmount;
        }
        if (aspectFilter != null && requestedAspect != aspectFilter) {
            return requestedAmount;
        }
        if (aspect != null && aspect != requestedAspect) {
            return requestedAmount;
        }
        int added = Math.min(requestedAmount, CAPACITY - amount);
        if (added > 0) {
            aspect = requestedAspect;
            amount += added;
            markChangedAndSync();
        }
        return requestedAmount - added;
    }

    public boolean takeFromContainer(Aspect requestedAspect, int requestedAmount) {
        if (requestedAspect == null || requestedAmount <= 0 || blocked || aspect != requestedAspect || amount < requestedAmount) {
            return false;
        }
        amount -= requestedAmount;
        if (amount <= 0) {
            amount = 0;
            aspect = null;
        }
        markChangedAndSync();
        return true;
    }

    @Override
    public boolean isSourceBlocked() {
        return blocked;
    }

    @Override
    public AspectList storedAspects() {
        return aspect == null || amount <= 0 ? new AspectList() : new AspectList().add(aspect, amount);
    }

    @Override
    public int drainAspect(Aspect requestedAspect, int requestedAmount, boolean simulate) {
        if (blocked || requestedAspect == null || requestedAmount <= 0 || requestedAspect != aspect) {
            return 0;
        }
        int drained = Math.min(requestedAmount, amount);
        if (!simulate && drained > 0) {
            amount -= drained;
            if (amount <= 0) {
                amount = 0;
                aspect = null;
            }
            markChangedAndSync();
        }
        return drained;
    }

    private Direction blockedOutputFace() {
        BlockState state = getBlockState();
        return state.hasProperty(TCAlembicBlock.FACING) ? state.getValue(TCAlembicBlock.FACING) : Direction.NORTH;
    }

    @Override
    public boolean isConnectable(Direction face) {
        return face != null && face != Direction.DOWN && face != blockedOutputFace();
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return false;
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return isConnectable(face);
    }

    @Override
    public TCEssentiaSuction getSuction(Direction face) {
        return TCEssentiaSuction.NONE;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public TCEssentiaStack getEssentia(Direction face) {
        return isConnectable(face) && aspect != null && amount > 0
                ? TCEssentiaStack.of(aspect.getTag(), amount)
                : TCEssentiaStack.EMPTY;
    }

    @Override
    public int addEssentia(String aspectTag, int requestedAmount, Direction face, boolean simulate) {
        return 0;
    }

    @Override
    public int takeEssentia(String aspectTag, int requestedAmount, Direction face, boolean simulate) {
        if (!canOutputTo(face) || requestedAmount <= 0) {
            return 0;
        }
        Aspect requestedAspect = Aspect.getAspect(aspectTag);
        if (requestedAspect == null || requestedAspect != aspect || amount < requestedAmount) {
            return 0;
        }
        if (!simulate) {
            takeFromContainer(requestedAspect, requestedAmount);
        }
        return requestedAmount;
    }

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
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
        if (aspect != null) {
            tag.putString("Aspect", aspect.getTag());
        }
        if (aspectFilter != null) {
            tag.putString("AspectFilter", aspectFilter.getTag());
        }
        tag.putInt("Amount", amount);
        tag.putBoolean("Blocked", blocked);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        aspect = Aspect.getAspect(tag.getString("Aspect"));
        aspectFilter = Aspect.getAspect(tag.getString("AspectFilter"));
        amount = aspect == null ? 0 : Math.max(0, Math.min(CAPACITY, tag.getInt("Amount")));
        blocked = tag.getBoolean("Blocked");
    }
}
