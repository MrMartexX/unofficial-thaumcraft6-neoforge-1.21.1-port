package thaumcraft.common.tiles.devices;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.devices.TCInfernalFurnaceBlock;
import thaumcraft.common.blocks.essentia.TCBellowsBlock;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;

/** Server-owned TC6 Infernal Furnace machine state and smelting loop. */
public final class TCInfernalFurnaceBlockEntity extends BlockEntity implements WorldlyContainer {
    public static final int SLOT_COUNT = 32;

    private static final int[] TOP_SLOTS = createTopSlots();
    private static final int[] NO_SLOTS = {};

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private final IItemHandler unsidedItemHandler = new SidedInvWrapper(this, Direction.UP);
    private final EnumMap<Direction, IItemHandler> sidedItemHandlers = new EnumMap<>(Direction.class);

    private int furnaceCookTime;
    private int furnaceMaxCookTime;
    private int speedyTime;
    private int destroyedItems;
    private int completedSmelts;
    private int emittedBonusItems;

    public TCInfernalFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.INFERNAL_FURNACE.get(), pos, state);
        for (Direction direction : Direction.values()) {
            sidedItemHandlers.put(direction, new SidedInvWrapper(this, direction));
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCInfernalFurnaceBlockEntity furnace) {
        if (!level.isClientSide) {
            furnace.tickServer();
        }
    }

    public IItemHandler itemHandler(@Nullable Direction side) {
        return side == null ? unsidedItemHandler : sidedItemHandlers.get(side);
    }

    public int furnaceCookTime() {
        return furnaceCookTime;
    }

    public int furnaceMaxCookTime() {
        return furnaceMaxCookTime;
    }

    public int speedyTime() {
        return speedyTime;
    }

    public int destroyedItems() {
        return destroyedItems;
    }

    public int completedSmelts() {
        return completedSmelts;
    }

    public int emittedBonusItems() {
        return emittedBonusItems;
    }

    public ItemStack getStoredItemForValidation(int slot) {
        return getItem(slot);
    }

    public void setStoredItemForValidation(int slot, ItemStack stack) {
        setItem(slot, stack == null ? ItemStack.EMPTY : stack);
    }

    public void setMachineStateForValidation(int cookTime, int maxCookTime, int speedy) {
        furnaceCookTime = Math.max(0, cookTime);
        furnaceMaxCookTime = Math.max(0, maxCookTime);
        speedyTime = Math.max(0, speedy);
        markChangedAndSync();
    }

    public void clearCountersForValidation() {
        destroyedItems = 0;
        completedSmelts = 0;
        emittedBonusItems = 0;
    }

    public int bellowsForValidation() {
        return getBellows();
    }

    public int calcCookTimeForValidation() {
        return calcCookTime();
    }

    public static int calcCookTimeForValidation(boolean speedy, int bellows) {
        int b = Math.max(0, Math.min(4, bellows));
        if (b > 0) {
            b *= 20 - (b - 1);
        }
        return Math.max(10, (speedy ? 80 : 140) - b);
    }

    public ItemStack smeltingResultForValidation(ItemStack stack) {
        return smeltingResult(stack).map(SmeltingResult::result).orElse(ItemStack.EMPTY);
    }

    public boolean hasBonusCandidateForValidation(ItemStack stack) {
        return TCInfernalFurnaceBonus.hasKnownBonusCandidate(stack);
    }

    public ItemStack addItemsToInventory(ItemStack incoming) {
        if (incoming == null || incoming.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (!canSmelt(incoming)) {
            destroyItemFx();
            destroyedItems += incoming.getCount();
            markChangedAndSync();
            return ItemStack.EMPTY;
        }
        return insertIntoBuffer(incoming);
    }

    private void tickServer() {
        boolean dirty = false;
        boolean cookedFlag = false;
        if (furnaceCookTime > 0) {
            furnaceCookTime--;
            cookedFlag = true;
            dirty = true;
        }

        if (furnaceMaxCookTime <= 0) {
            furnaceMaxCookTime = calcCookTime();
            dirty = true;
        }
        if (furnaceCookTime > furnaceMaxCookTime) {
            furnaceCookTime = furnaceMaxCookTime;
            dirty = true;
        }

        if (furnaceCookTime <= 0 && cookedFlag) {
            dirty |= completeFirstSmeltableSlot();
        }

        if (speedyTime <= 0 && level != null) {
            int drained = (int) AuraHelper.drainVis(level, worldPosition, 20.0F, false);
            if (drained != speedyTime) {
                speedyTime = drained;
                dirty = true;
            }
        }

        if (furnaceCookTime == 0 && !cookedFlag) {
            for (int slot = 0; slot < getContainerSize(); slot++) {
                if (canSmelt(getItem(slot))) {
                    furnaceMaxCookTime = calcCookTime();
                    furnaceCookTime = furnaceMaxCookTime;
                    dirty = true;
                    break;
                }
            }
        }

        if (dirty) {
            markChangedAndSync();
        }
    }

    private boolean completeFirstSmeltableSlot() {
        if (level == null) {
            return false;
        }
        for (int slot = 0; slot < getContainerSize(); slot++) {
            ItemStack input = getItem(slot);
            if (!input.isEmpty()) {
                Optional<SmeltingResult> result = smeltingResult(input);
                if (result.isPresent()) {
                    if (speedyTime > 0) {
                        speedyTime--;
                    }
                    ejectItem(result.get(), input);
                    level.blockEvent(worldPosition, getBlockState().getBlock(), 3, 0);
                    if (level.random.nextInt(20) == 0) {
                        AuraHelper.polluteAura(level, worldPosition.relative(outputDirection()), 1.0F, true);
                    }
                    input.shrink(1);
                    if (input.isEmpty()) {
                        items.set(slot, ItemStack.EMPTY);
                    }
                    completedSmelts++;
                    return true;
                }
                items.set(slot, ItemStack.EMPTY);
                return true;
            }
        }
        return false;
    }

    private void ejectItem(SmeltingResult result, ItemStack furnaceInput) {
        if (level == null || result.result().isEmpty()) {
            return;
        }
        Direction output = outputDirection();
        ejectStack(result.result().copy(), output);

        int rolls = getBellows() + 1;
        for (int roll = 0; roll < rolls; roll++) {
            List<ItemStack> bonusItems = TCInfernalFurnaceBonus.roll(level, furnaceInput);
            for (ItemStack bonus : bonusItems) {
                if (!bonus.isEmpty()) {
                    emittedBonusItems += bonus.getCount();
                    ejectStack(bonus, output);
                }
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            int experience = experienceFor(result);
            if (experience > 0) {
                Vec3 xpPos = Vec3.atCenterOf(worldPosition).add(
                        output.getStepX() * 1.2D,
                        -0.1D,
                        output.getStepZ() * 1.2D
                );
                ExperienceOrb.award(serverLevel, xpPos, experience);
            }
        }
    }

    private void ejectStack(ItemStack stack, Direction output) {
        if (level == null || stack.isEmpty()) {
            return;
        }
        BlockPos targetPos = worldPosition.relative(output);
        IItemHandler target = level.getCapability(Capabilities.ItemHandler.BLOCK, targetPos, output.getOpposite());
        ItemStack remaining = stack.copy();
        if (target != null) {
            for (int slot = 0; slot < target.getSlots() && !remaining.isEmpty(); slot++) {
                remaining = target.insertItem(slot, remaining, false);
            }
        }
        if (!remaining.isEmpty()) {
            double x = worldPosition.getX() + 0.5D + output.getStepX() * 1.2D;
            double y = worldPosition.getY() + 0.4D;
            double z = worldPosition.getZ() + 0.5D + output.getStepZ() * 1.2D;
            ItemEntity entity = new ItemEntity(level, x, y, z, remaining);
            entity.setDeltaMovement(output.getStepX() * 0.13D, 0.0D, output.getStepZ() * 0.13D);
            level.addFreshEntity(entity);
        }
    }

    private int experienceFor(SmeltingResult result) {
        if (level == null || result.experience() <= 0.0F || result.result().getCount() <= 0) {
            return 0;
        }
        float total = result.result().getCount() * result.experience();
        int experience = (int) Math.floor(total);
        if (experience < (int) Math.ceil(total) && level.random.nextFloat() < total - experience) {
            experience++;
        }
        return experience;
    }

    private Optional<SmeltingResult> smeltingResult(ItemStack stack) {
        if (level == null || stack == null || stack.isEmpty()) {
            return Optional.empty();
        }
        SingleRecipeInput input = new SingleRecipeInput(stack);
        Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, level);
        if (recipe.isEmpty()) {
            return Optional.empty();
        }
        ItemStack output = recipe.get().value().assemble(input, level.registryAccess());
        if (output.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new SmeltingResult(output, recipe.get().value().getExperience()));
    }

    private boolean canSmelt(ItemStack stack) {
        return smeltingResult(stack).isPresent();
    }

    private ItemStack insertIntoBuffer(ItemStack incoming) {
        ItemStack remaining = incoming.copy();
        for (int slot = 0; slot < getContainerSize() && !remaining.isEmpty(); slot++) {
            ItemStack stored = getItem(slot);
            if (stored.isEmpty()) {
                int moved = Math.min(remaining.getCount(), remaining.getMaxStackSize());
                items.set(slot, remaining.copyWithCount(moved));
                remaining.shrink(moved);
            } else if (ItemStack.isSameItemSameComponents(stored, remaining)) {
                int capacity = Math.min(stored.getMaxStackSize(), getMaxStackSize(stored)) - stored.getCount();
                if (capacity > 0) {
                    int moved = Math.min(capacity, remaining.getCount());
                    stored.grow(moved);
                    remaining.shrink(moved);
                }
            }
        }
        markChangedAndSync();
        return remaining;
    }

    private int getBellows() {
        if (level == null) {
            return 0;
        }
        int bellows = 0;
        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP) {
                continue;
            }
            BlockPos bellowsPos = worldPosition.relative(direction, 2);
            BlockState state = level.getBlockState(bellowsPos);
            if (state.is(TCBlocks.BELLOWS.get())
                    && state.hasProperty(TCBellowsBlock.FACING)
                    && state.hasProperty(TCBellowsBlock.ENABLED)
                    && state.getValue(TCBellowsBlock.FACING) == direction.getOpposite()
                    && state.getValue(TCBellowsBlock.ENABLED)
                    && !level.hasNeighborSignal(bellowsPos)) {
                bellows++;
            }
        }
        return Math.min(4, bellows);
    }

    private int calcCookTime() {
        return calcCookTimeForValidation(speedyTime > 0, getBellows());
    }

    private Direction outputDirection() {
        BlockState state = getBlockState();
        return state.hasProperty(TCInfernalFurnaceBlock.FACING)
                ? state.getValue(TCInfernalFurnaceBlock.FACING).getOpposite()
                : Direction.SOUTH;
    }

    private void destroyItemFx() {
        if (level == null) {
            return;
        }
        level.playSound(
                null,
                worldPosition,
                SoundEvents.LAVA_EXTINGUISH,
                SoundSource.BLOCKS,
                0.3F,
                2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F
        );
    }

    @Override
    public boolean triggerEvent(int id, int data) {
        if (id == 3 && level != null && level.isClientSide) {
            Direction output = outputDirection();
            for (int index = 0; index < 5; index++) {
                level.playLocalSound(
                        worldPosition.getX() + 0.5D,
                        worldPosition.getY() + 0.5D,
                        worldPosition.getZ() + 0.5D,
                        SoundEvents.LAVA_POP,
                        SoundSource.BLOCKS,
                        0.1F + level.random.nextFloat() * 0.1F,
                        0.9F + level.random.nextFloat() * 0.15F,
                        false
                );
                level.addParticle(
                        net.minecraft.core.particles.ParticleTypes.LAVA,
                        worldPosition.getX() + 0.5D + output.getStepX() * 0.45D,
                        worldPosition.getY() + 0.65D,
                        worldPosition.getZ() + 0.5D + output.getStepZ() * 0.45D,
                        output.getStepX() * 0.03D,
                        0.02D,
                        output.getStepZ() * 0.03D
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
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) {
            markChangedAndSync();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= items.size()) {
            return;
        }
        ItemStack stored = stack == null ? ItemStack.EMPTY : stack.copy();
        stored.limitSize(getMaxStackSize(stored));
        items.set(slot, stored);
        markChangedAndSync();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return canSmelt(stack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.UP ? TOP_SLOTS : NO_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return side == Direction.UP && canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        markChangedAndSync();
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
        tag.putShort("CookTime", (short) furnaceCookTime);
        tag.putShort("MaxCookTime", (short) furnaceMaxCookTime);
        tag.putShort("SpeedyTime", (short) speedyTime);
        tag.putInt("DestroyedItems", destroyedItems);
        tag.putInt("CompletedSmelts", completedSmelts);
        tag.putInt("EmittedBonusItems", emittedBonusItems);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int slot = 0; slot < items.size(); slot++) {
            items.set(slot, ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(tag, items, registries);
        furnaceCookTime = Math.max(0, tag.getShort("CookTime"));
        furnaceMaxCookTime = Math.max(0, tag.getShort("MaxCookTime"));
        speedyTime = Math.max(0, tag.getShort("SpeedyTime"));
        destroyedItems = Math.max(0, tag.getInt("DestroyedItems"));
        completedSmelts = Math.max(0, tag.getInt("CompletedSmelts"));
        emittedBonusItems = Math.max(0, tag.getInt("EmittedBonusItems"));
    }

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private static int[] createTopSlots() {
        int[] slots = new int[SLOT_COUNT];
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            slots[slot] = slot;
        }
        return slots;
    }

    private record SmeltingResult(ItemStack result, float experience) {
    }
}
