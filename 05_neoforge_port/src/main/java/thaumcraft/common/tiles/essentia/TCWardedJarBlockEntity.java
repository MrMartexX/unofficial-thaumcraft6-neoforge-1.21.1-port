package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.essentia.container.TCAspectSourceContainer;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.registry.TCBlockEntities;

/** First legacy-shaped warded jar storage slice. */
public final class TCWardedJarBlockEntity extends BlockEntity implements TCAspectSourceContainer, TCEssentiaTransport {
    public static final int CAPACITY = 250;

    private Aspect aspect;
    private Aspect aspectFilter;
    private int amount;
    private boolean blocked;
    private int transportTick;

    public TCWardedJarBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.WARDED_JAR.get(), pos, state);
    }

    public Aspect storedAspect() {
        return aspect;
    }

    public int storedAmount() {
        return amount;
    }

    public Aspect aspectFilter() {
        return aspectFilter;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setStoredForValidation(Aspect newAspect, int newAmount) {
        aspect = newAspect;
        amount = newAspect == null ? 0 : Math.max(0, Math.min(CAPACITY, newAmount));
        if (amount == 0) {
            aspect = null;
        }
        markChangedAndSync();
    }

    public void setBlockedForValidation(boolean value) {
        blocked = value;
        markChangedAndSync();
    }

    public void setFilterForValidation(Aspect filter) {
        aspectFilter = filter;
        markChangedAndSync();
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

    @Override
    public boolean isConnectable(Direction face) {
        return face == Direction.UP;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return face == Direction.UP;
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return face == Direction.UP;
    }

    @Override
    public TCEssentiaSuction getSuction(Direction face) {
        if (!isConnectable(face) || amount >= CAPACITY) {
            return TCEssentiaSuction.NONE;
        }
        Aspect suctionAspect = aspectFilter != null ? aspectFilter : aspect;
        return new TCEssentiaSuction(suctionAspect == null ? "" : suctionAspect.getTag(), aspectFilter != null ? 64 : 32);
    }

    @Override
    public int getMinimumSuction() {
        return aspectFilter != null ? 64 : 32;
    }

    @Override
    public TCEssentiaStack getEssentia(Direction face) {
        return isConnectable(face) && aspect != null && amount > 0
                ? TCEssentiaStack.of(aspect.getTag(), amount)
                : TCEssentiaStack.EMPTY;
    }

    @Override
    public int addEssentia(String aspectTag, int requestedAmount, Direction face, boolean simulate) {
        Aspect requestedAspect = Aspect.getAspect(aspectTag);
        if (!canInputFrom(face) || requestedAspect == null || requestedAmount <= 0) {
            return 0;
        }
        if (aspectFilter != null && aspectFilter != requestedAspect) {
            return 0;
        }
        if (aspect != null && aspect != requestedAspect) {
            return 0;
        }
        int accepted = Math.min(requestedAmount, CAPACITY - amount);
        if (!simulate && accepted > 0) {
            aspect = requestedAspect;
            amount += accepted;
            markChangedAndSync();
        }
        return accepted;
    }

    @Override
    public int takeEssentia(String aspectTag, int requestedAmount, Direction face, boolean simulate) {
        if (!canOutputTo(face)) {
            return 0;
        }
        Aspect requestedAspect = Aspect.getAspect(aspectTag);
        if (requestedAspect == null || requestedAspect != aspect || requestedAmount <= 0) {
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

    public int comparatorSignal() {
        return amount <= 0 ? 0 : Math.min(15, 1 + (int) Math.floor((amount / (double) CAPACITY) * 14.0D));
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCWardedJarBlockEntity jar) {
        if (level == null || level.isClientSide || ++jar.transportTick % 5 != 0 || jar.amount >= CAPACITY) {
            return;
        }
        jar.fillFromAbove();
    }

    private void fillFromAbove() {
        if (level == null || !(level.getBlockEntity(worldPosition.above()) instanceof TCEssentiaTransport source)) {
            return;
        }
        Direction sourceFace = Direction.DOWN;
        if (!source.canOutputTo(sourceFace)) {
            return;
        }

        TCEssentiaSuction ownSuction = getSuction(Direction.UP);
        TCEssentiaSuction sourceSuction = source.getSuction(sourceFace);
        Aspect target = aspectFilter;
        if (target == null && aspect != null && amount > 0) {
            target = aspect;
        }
        if (target == null) {
            TCEssentiaStack visible = source.getEssentia(sourceFace);
            if (!visible.isEmpty()
                    && sourceSuction.amount() < ownSuction.amount()
                    && ownSuction.amount() >= source.getMinimumSuction()) {
                target = Aspect.getAspect(visible.aspect());
            }
        }
        if (target == null || sourceSuction.amount() >= ownSuction.amount()) {
            return;
        }

        int taken = source.takeEssentia(target.getTag(), 1, sourceFace, false);
        if (taken > 0) {
            addEssentia(target.getTag(), taken, Direction.UP, false);
        }
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
