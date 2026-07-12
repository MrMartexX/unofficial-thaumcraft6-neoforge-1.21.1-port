package thaumcraft.common.tiles.devices;

import java.util.EnumMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCFluids;
import thaumcraft.common.registry.TCItems;

/** Server-owned Arcane Spa behavior based on legacy {@code TileSpa}. */
public final class TCArcaneSpaBlockEntity extends BlockEntity implements WorldlyContainer {
    public static final int SLOT_BATH_SALTS = 0;
    public static final int SLOT_COUNT = 1;
    public static final int TANK_CAPACITY_MB = 5000;
    public static final int LEGACY_BATCH_MB = 1000;
    public static final int LEGACY_TICK_INTERVAL = 40;
    private static final int[] SIDE_SLOTS = {SLOT_BATH_SALTS};
    private static final int[] TOP_SLOTS = {};

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private final SpaFluidTank tank = new SpaFluidTank();
    private final IItemHandler unsidedItemHandler = new SidedInvWrapper(this, null);
    private final EnumMap<Direction, IItemHandler> sidedItemHandlers = new EnumMap<>(Direction.class);
    private boolean mix = true;
    private int counter;

    public TCArcaneSpaBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.SPA.get(), pos, state);
        for (Direction direction : Direction.values()) {
            sidedItemHandlers.put(direction, new SidedInvWrapper(this, direction));
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCArcaneSpaBlockEntity spa) {
        if (!level.isClientSide) {
            spa.tickServer();
        }
    }

    public IFluidHandler fluidHandler() {
        return tank;
    }

    public IItemHandler itemHandler(@Nullable Direction side) {
        return side == null ? unsidedItemHandler : sidedItemHandlers.get(side);
    }

    public boolean mix() {
        return mix;
    }

    public int fluidAmount() {
        return tank.getFluidAmount();
    }

    public FluidStack storedFluidForValidation() {
        return tank.getFluid().copy();
    }

    public void setMixForValidation(boolean value) {
        mix = value;
        markChangedAndSync();
    }

    public void setFluidForValidation(Fluid fluid, int amount) {
        tank.setFluid(fluid == null || fluid == Fluids.EMPTY || amount <= 0
                ? FluidStack.EMPTY
                : new FluidStack(fluid, Math.min(TANK_CAPACITY_MB, amount)));
        markChangedAndSync();
    }

    public void tickServerForValidation(int ticks) {
        for (int i = 0; i < ticks; i++) {
            tickServer();
        }
    }

    public boolean insertBathSaltsFromPlayer(ItemStack held, boolean creative) {
        if (!canPlaceItem(SLOT_BATH_SALTS, held)) {
            return false;
        }
        ItemStack stored = items.get(SLOT_BATH_SALTS);
        if (!stored.isEmpty() && (!ItemStack.isSameItemSameComponents(stored, held)
                || stored.getCount() >= Math.min(stored.getMaxStackSize(), getMaxStackSize(stored)))) {
            return false;
        }
        if (stored.isEmpty()) {
            items.set(SLOT_BATH_SALTS, new ItemStack(held.getItem(), 1));
        } else {
            stored.grow(1);
        }
        if (!creative) {
            held.shrink(1);
        }
        markChangedAndSync();
        return true;
    }

    public void toggleMix() {
        mix = !mix;
        markChangedAndSync();
    }

    private void tickServer() {
        if (level == null || level.isClientSide) {
            return;
        }
        if (counter++ % LEGACY_TICK_INTERVAL != 0 || level.hasNeighborSignal(worldPosition) || !hasIngredients()) {
            return;
        }

        BlockState targetState = targetFluidBlockState();
        Block targetBlock = targetState.getBlock();
        BlockPos above = worldPosition.above();
        BlockState aboveState = level.getBlockState(above);
        if (aboveState.is(targetBlock) && aboveState.getFluidState().isSource()) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos candidate = worldPosition.offset(x, 1, z);
                    if (isValidLocation(candidate, true, targetBlock)) {
                        consumeIngredients();
                        level.setBlock(candidate, targetState, Block.UPDATE_ALL);
                        markChangedAndSync();
                        return;
                    }
                }
            }
        } else if (isValidLocation(above, false, targetBlock)) {
            consumeIngredients();
            level.setBlock(above, targetState, Block.UPDATE_ALL);
            markChangedAndSync();
        }
    }

    private boolean hasIngredients() {
        if (mix) {
            return tank.getFluidAmount() >= LEGACY_BATCH_MB
                    && tank.getFluid().is(Fluids.WATER)
                    && !items.get(SLOT_BATH_SALTS).isEmpty()
                    && items.get(SLOT_BATH_SALTS).is(TCItems.BATH_SALTS.get());
        }
        return tank.getFluidAmount() >= LEGACY_BATCH_MB
                && !tank.getFluid().isEmpty()
                && tank.getFluid().getFluid() != Fluids.EMPTY
                && targetFluidBlockState().getBlock() != Blocks.AIR;
    }

    private BlockState targetFluidBlockState() {
        if (mix) {
            return TCFluids.PURIFYING_FLUID.get().defaultFluidState().createLegacyBlock();
        }
        return tank.getFluid().getFluid().defaultFluidState().createLegacyBlock();
    }

    private void consumeIngredients() {
        if (mix) {
            removeItem(SLOT_BATH_SALTS, 1);
        }
        tank.drain(LEGACY_BATCH_MB, IFluidHandler.FluidAction.EXECUTE);
    }

    private boolean isValidLocation(BlockPos pos, boolean mustBeAdjacent, Block target) {
        if (level == null) {
            return false;
        }
        if ((target == Blocks.WATER || target == Blocks.BUBBLE_COLUMN) && level.dimensionType().ultraWarm()) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        BlockState below = level.getBlockState(pos.below());
        boolean supported = below.isFaceSturdy(level, pos.below(), Direction.UP);
        boolean replaceable = state.canBeReplaced();
        boolean alreadySource = state.is(target) && state.getFluidState().isSource();
        return supported
                && replaceable
                && !alreadySource
                && (!mustBeAdjacent || touchesTargetSource(pos, target));
    }

    private boolean touchesTargetSource(BlockPos pos, Block target) {
        if (level == null) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            BlockState neighbour = level.getBlockState(pos.relative(direction));
            if (neighbour.is(target) && neighbour.getFluidState().isSource()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return items.get(SLOT_BATH_SALTS).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == SLOT_BATH_SALTS ? items.get(SLOT_BATH_SALTS) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = slot == SLOT_BATH_SALTS ? ContainerHelper.removeItem(items, slot, amount) : ItemStack.EMPTY;
        if (!removed.isEmpty()) {
            markChangedAndSync();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return slot == SLOT_BATH_SALTS ? ContainerHelper.takeItem(items, slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot != SLOT_BATH_SALTS) {
            return;
        }
        ItemStack stored = stack == null ? ItemStack.EMPTY : stack.copy();
        stored.limitSize(getMaxStackSize(stored));
        items.set(SLOT_BATH_SALTS, stored);
        markChangedAndSync();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == SLOT_BATH_SALTS && stack != null && stack.is(TCItems.BATH_SALTS.get());
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.UP ? TOP_SLOTS : SIDE_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return side != Direction.UP && canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return side != Direction.UP && slot == SLOT_BATH_SALTS;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.set(SLOT_BATH_SALTS, ItemStack.EMPTY);
        markChangedAndSync();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tank.writeToNBT(registries, tag);
        tag.putBoolean("mix", mix);
        tag.putInt("Counter", counter);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items.set(SLOT_BATH_SALTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        tank.readFromNBT(registries, tag);
        mix = !tag.contains("mix") || tag.getBoolean("mix");
        counter = Math.max(0, tag.getInt("Counter"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private final class SpaFluidTank extends FluidTank {
        private SpaFluidTank() {
            super(TANK_CAPACITY_MB);
        }

        @Override
        protected void onContentsChanged() {
            markChangedAndSync();
        }
    }
}
