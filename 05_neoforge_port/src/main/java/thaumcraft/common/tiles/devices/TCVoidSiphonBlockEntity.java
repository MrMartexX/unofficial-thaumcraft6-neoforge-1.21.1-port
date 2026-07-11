package thaumcraft.common.tiles.devices;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.blocks.devices.TCVoidSiphonBlock;
import thaumcraft.common.menu.TCVoidSiphonMenu;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCItems;

/** Server-owned TC6 Void Siphon state: one extract-only void-seed output slot and rift drain progress. */
public final class TCVoidSiphonBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int SLOT_OUTPUT = 0;
    public static final int SLOT_COUNT = 1;
    public static final int PROGRESS_REQUIRED = 2000;
    public static final double RIFT_RANGE = 8.0D;
    private static final int[] SLOTS = {SLOT_OUTPUT};

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private final IItemHandler unsidedItemHandler = new SidedInvWrapper(this, null);
    private final EnumMap<Direction, IItemHandler> sidedItemHandlers = new EnumMap<>(Direction.class);

    private int counter;
    private int progress;

    public TCVoidSiphonBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.VOID_SIPHON.get(), pos, state);
        for (Direction direction : Direction.values()) {
            sidedItemHandlers.put(direction, new SidedInvWrapper(this, direction));
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCVoidSiphonBlockEntity siphon) {
        if (!level.isClientSide) {
            siphon.tickServer(state);
        }
    }

    public IItemHandler itemHandler(@Nullable Direction side) {
        return side == null ? unsidedItemHandler : sidedItemHandlers.get(side);
    }

    public int counter() {
        return counter;
    }

    public int progress() {
        return progress;
    }

    public void setProgressForValidation(int value) {
        progress = Math.max(0, value);
        markChangedAndSync();
    }

    public void setOutputForValidation(ItemStack stack) {
        setItem(SLOT_OUTPUT, stack == null ? ItemStack.EMPTY : stack);
    }

    public int drainRiftsForValidation(List<? extends TCVoidSiphonRiftAccess> rifts, @Nullable Boolean forceShrink) {
        int drained = drainRifts(rifts == null ? List.of() : rifts, forceShrink);
        processCompletedSeeds();
        markChangedAndSync();
        return drained;
    }

    private void tickServer(BlockState state) {
        counter++;
        if (!state.hasProperty(TCVoidSiphonBlock.ENABLED)
                || !state.getValue(TCVoidSiphonBlock.ENABLED)
                || counter % 20 != 0
                || progress >= PROGRESS_REQUIRED
                || !canOutputAcceptSeed()) {
            return;
        }

        int drained = drainRifts(findValidRifts(), null);
        if (drained > 0) {
            if (counter % 40 == 0 && level != null) {
                level.blockEvent(worldPosition, getBlockState().getBlock(), 5, counter);
            }
            processCompletedSeeds();
            markChangedAndSync();
        }
    }

    private int drainRifts(List<? extends TCVoidSiphonRiftAccess> rifts, @Nullable Boolean forceShrink) {
        if (level == null || rifts.isEmpty()) {
            return 0;
        }

        int drained = 0;
        for (TCVoidSiphonRiftAccess rift : rifts) {
            if (!isValidRift(rift)) {
                continue;
            }

            double drain = Math.sqrt(rift.voidSiphonRiftSize());
            int wholeDrain = (int) drain;
            progress += wholeDrain;
            drained += wholeDrain;
            rift.voidSiphonSetStability(rift.voidSiphonStability() - drain / 15.0D);
            boolean shrink = forceShrink != null ? forceShrink : level.random.nextInt(33) == 0;
            if (shrink) {
                rift.voidSiphonSetRiftSize(Math.max(0, rift.voidSiphonRiftSize() - 1));
            }
        }
        return drained;
    }

    private List<TCVoidSiphonRiftAccess> findValidRifts() {
        if (level == null) {
            return List.of();
        }
        AABB area = new AABB(worldPosition).inflate(RIFT_RANGE);
        ArrayList<TCVoidSiphonRiftAccess> result = new ArrayList<>();
        for (Entity entity : level.getEntities((Entity) null, area, entity -> entity instanceof TCVoidSiphonRiftAccess)) {
            TCVoidSiphonRiftAccess access = (TCVoidSiphonRiftAccess) entity;
            if (isValidRift(access)) {
                result.add(access);
            }
        }
        return result;
    }

    private boolean isValidRift(TCVoidSiphonRiftAccess rift) {
        if (rift == null || level == null || !rift.voidSiphonAlive() || rift.voidSiphonRiftSize() < 2) {
            return false;
        }
        Vec3 source = Vec3.atLowerCornerWithOffset(worldPosition, 0.5D, 1.0D, 0.5D);
        Vec3 target = rift.voidSiphonPosition();
        Vec3 delta = target.subtract(source);
        Vec3 sightOrigin = delta.lengthSqr() < 1.0E-6D ? source : source.add(delta.normalize());
        return rift.voidSiphonCanBeSeenFrom(level, sightOrigin);
    }

    private void processCompletedSeeds() {
        boolean changed = false;
        while (progress >= PROGRESS_REQUIRED && canOutputAcceptSeed()) {
            progress -= PROGRESS_REQUIRED;
            ItemStack stored = getItem(SLOT_OUTPUT);
            if (stored.isEmpty()) {
                items.set(SLOT_OUTPUT, new ItemStack(TCItems.VOID_SEED.get()));
            } else {
                stored.grow(1);
            }
            changed = true;
        }
        if (changed) {
            markChangedAndSync();
        }
    }

    private boolean canOutputAcceptSeed() {
        ItemStack stored = getItem(SLOT_OUTPUT);
        return stored.isEmpty()
                || stored.is(TCItems.VOID_SEED.get()) && stored.getCount() < Math.min(stored.getMaxStackSize(), getMaxStackSize(stored));
    }

    @Override
    public boolean triggerEvent(int id, int data) {
        if (id == 5 && level != null && level.isClientSide) {
            RandomSource random = level.random;
            for (int index = 0; index < 12; index++) {
                level.addParticle(
                        net.minecraft.core.particles.ParticleTypes.PORTAL,
                        worldPosition.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.8D,
                        worldPosition.getY() + 0.9D + random.nextDouble() * 0.4D,
                        worldPosition.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.8D,
                        (random.nextDouble() - 0.5D) * 0.02D,
                        random.nextDouble() * 0.03D,
                        (random.nextDouble() - 0.5D) * 0.02D
                );
            }
            return true;
        }
        return super.triggerEvent(id, data);
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return getItem(SLOT_OUTPUT).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == SLOT_OUTPUT ? items.get(SLOT_OUTPUT) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = slot == SLOT_OUTPUT ? ContainerHelper.removeItem(items, slot, amount) : ItemStack.EMPTY;
        if (!removed.isEmpty()) {
            markChangedAndSync();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return slot == SLOT_OUTPUT ? ContainerHelper.takeItem(items, slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot != SLOT_OUTPUT) {
            return;
        }
        ItemStack stored = stack == null ? ItemStack.EMPTY : stack.copy();
        stored.limitSize(getMaxStackSize(stored));
        items.set(SLOT_OUTPUT, stored);
        markChangedAndSync();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == SLOT_OUTPUT && stack != null && stack.is(TCItems.VOID_SEED.get());
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == SLOT_OUTPUT;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.set(SLOT_OUTPUT, ItemStack.EMPTY);
        markChangedAndSync();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.thaumcraft.void_siphon");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new TCVoidSiphonMenu(containerId, inventory, this);
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(worldPosition);
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
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putShort("Progress", (short) progress);
        tag.putInt("Counter", counter);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items.set(SLOT_OUTPUT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        progress = Math.max(0, tag.getShort("Progress"));
        counter = Math.max(0, tag.getInt("Counter"));
    }

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }
}
