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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.essentia.container.TCAspectSourceContainer;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.registry.TCBlockEntities;

/** First legacy-shaped warded jar storage slice. */
public final class TCWardedJarBlockEntity extends BlockEntity implements TCAspectSourceContainer, TCEssentiaTransport {
    public static final int CAPACITY = 250;

    private Aspect aspect;
    private Aspect aspectFilter;
    private int amount;
    private Direction labelFacing = Direction.NORTH;
    private boolean blocked;
    private int transportTick;
    private final Kind kind;

    public TCWardedJarBlockEntity(BlockPos pos, BlockState state) {
        this(TCBlockEntities.WARDED_JAR.get(), pos, state, Kind.NORMAL);
    }

    public TCWardedJarBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Kind kind) {
        super(type, pos, state);
        this.kind = kind == null ? Kind.NORMAL : kind;
    }

    public Kind kind() {
        return kind;
    }

    public boolean isVoidJar() {
        return kind == Kind.VOID;
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

    public Direction labelFacing() {
        return labelFacing;
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
        setFilter(filter);
    }

    public void setFilter(Aspect filter) {
        setFilter(filter, labelFacing);
    }

    public void setFilter(Aspect filter, Direction facing) {
        aspectFilter = filter;
        if (facing != null && facing.getAxis().isHorizontal()) {
            labelFacing = facing;
        }
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

    public int addToContainer(Aspect requestedAspect, int requestedAmount) {
        if (requestedAspect == null || requestedAmount <= 0 || blocked) {
            return requestedAmount;
        }
        int accepted = addEssentia(requestedAspect.getTag(), requestedAmount, Direction.UP, false);
        return requestedAmount - accepted;
    }

    public boolean takeFromContainer(Aspect requestedAspect, int requestedAmount) {
        if (requestedAspect == null || requestedAmount <= 0 || blocked) {
            return false;
        }
        return takeEssentia(requestedAspect.getTag(), requestedAmount, Direction.UP, false) == requestedAmount;
    }

    public boolean doesContainerAccept(Aspect requestedAspect) {
        return requestedAspect != null && (aspectFilter == null || aspectFilter == requestedAspect);
    }

    public boolean canAcceptManual(Aspect requestedAspect, int requestedAmount) {
        if (requestedAspect == null || requestedAmount <= 0 || blocked || !doesContainerAccept(requestedAspect)) {
            return false;
        }
        if (aspect != null && aspect != requestedAspect) {
            return false;
        }
        return isVoidJar() || amount + requestedAmount <= CAPACITY;
    }

    public int remainingCapacity() {
        return CAPACITY - amount;
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
        if (!isConnectable(face) || !isVoidJar() && amount >= CAPACITY) {
            return TCEssentiaSuction.NONE;
        }
        Aspect suctionAspect = aspectFilter != null ? aspectFilter : aspect;
        int suction = isVoidJar() ? voidJarSuction() : (aspectFilter != null ? 64 : 32);
        return new TCEssentiaSuction(suctionAspect == null ? "" : suctionAspect.getTag(), suction);
    }

    @Override
    public int getMinimumSuction() {
        if (isVoidJar()) {
            return aspectFilter != null ? 48 : 32;
        }
        return aspectFilter != null ? 64 : 32;
    }

    @Override
    public TCEssentiaStack getEssentia(Direction face) {
        return (face == null || isConnectable(face)) && aspect != null && amount > 0
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
        int accepted = isVoidJar() ? requestedAmount : Math.min(requestedAmount, CAPACITY - amount);
        if (!simulate && accepted > 0) {
            aspect = requestedAspect;
            amount += accepted;
            if (amount > CAPACITY) {
                if (isVoidJar() && level != null && level.random.nextInt(250) == 0) {
                    AuraHelper.polluteAura(level, worldPosition, 1.0F, true);
                }
                amount = CAPACITY;
            }
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
        if (amount < requestedAmount) {
            return 0;
        }
        int drained = requestedAmount;
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
        if (level == null || level.isClientSide || ++jar.transportTick % 5 != 0
                || !jar.isVoidJar() && jar.amount >= CAPACITY) {
            return;
        }
        jar.fillFromAbove();
    }

    private int voidJarSuction() {
        return aspectFilter != null && amount < CAPACITY ? 48 : 32;
    }

    private void fillFromAbove() {
        if (level == null) {
            return;
        }
        Direction sourceFace = Direction.DOWN;
        TCEssentiaTransport source = level.getCapability(
                TCEssentiaCapabilities.BLOCK,
                worldPosition.above(),
                sourceFace
        );
        if (source == null) {
            return;
        }
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
        tag.putByte("facing", (byte) labelFacing.get3DDataValue());
        tag.putInt("Amount", amount);
        tag.putBoolean("Blocked", blocked);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        aspect = Aspect.getAspect(tag.getString("Aspect"));
        aspectFilter = Aspect.getAspect(tag.getString("AspectFilter"));
        labelFacing = tag.contains("facing") ? Direction.from3DDataValue(tag.getByte("facing")) : Direction.NORTH;
        if (labelFacing.getAxis().isVertical()) {
            labelFacing = Direction.NORTH;
        }
        amount = aspect == null ? 0 : Math.max(0, Math.min(CAPACITY, tag.getInt("Amount")));
        blocked = tag.getBoolean("Blocked");
    }

    public enum Kind {
        NORMAL,
        VOID
    }
}
