package thaumcraft.common.tiles.essentia;

import java.util.EnumMap;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.essentia.TCBellowsBlock;
import thaumcraft.common.blocks.essentia.TCSmelterAuxBlock;
import thaumcraft.common.blocks.essentia.TCSmelterBlock;
import thaumcraft.common.blocks.essentia.TCSmelterVentBlock;
import thaumcraft.common.menu.TCSmelterMenu;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;

/**
 * Server-authoritative TC6 essentia smelter machine.
 *
 * <p>The inventory, smelting cadence, efficiency loss, vent mitigation and Alembic routing follow
 * legacy {@code TileSmelter}. Modern menu and sided capability access are adapters around that state.</p>
 */
public class TCSmelterBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_COUNT = 2;
    public static final int MAX_VIS = 256;
    public static final int BASE_SMELT_TIME = 100;

    private static final int[] SLOTS_BOTTOM = {SLOT_FUEL};
    private static final int[] SLOTS_TOP = {};
    private static final int[] SLOTS_SIDES = {SLOT_INPUT};

    private final SmelterType smelterType;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private final IItemHandler unsidedItemHandler = new SidedInvWrapper(this, null);
    private final EnumMap<Direction, IItemHandler> sidedItemHandlers = new EnumMap<>(Direction.class);

    private AspectList aspects = new AspectList();
    private int vis;
    private int smeltTime = BASE_SMELT_TIME;
    private boolean speedBoost;
    private int furnaceBurnTime;
    private int currentItemBurnTime;
    private int furnaceCookTime;
    private int bellows = -1;
    private int transferTicks;
    private int pendingFlux;

    public TCSmelterBlockEntity(BlockPos pos, BlockState state) {
        this(TCBlockEntities.SMELTER_BASIC.get(), pos, state, SmelterType.BASIC);
    }

    protected TCSmelterBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            SmelterType smelterType
    ) {
        super(type, pos, state);
        this.smelterType = smelterType == null ? SmelterType.BASIC : smelterType;
        for (Direction direction : Direction.values()) {
            sidedItemHandlers.put(direction, new SidedInvWrapper(this, direction));
        }
    }

    public IItemHandler itemHandler(@Nullable Direction side) {
        return side == null ? unsidedItemHandler : sidedItemHandlers.get(side);
    }

    public ItemStack getStoredItem(int slot) {
        return getItem(slot);
    }

    public void setStoredItemForValidation(int slot, ItemStack stack) {
        setItem(slot, stack == null ? ItemStack.EMPTY : stack);
    }

    public AspectList storedAspects() {
        return aspects.copy();
    }

    public int storedVis() {
        return vis;
    }

    public int maxVis() {
        return MAX_VIS;
    }

    public int smeltTime() {
        return smeltTime;
    }

    public int furnaceBurnTime() {
        return furnaceBurnTime;
    }

    public int currentItemBurnTime() {
        return currentItemBurnTime;
    }

    public int furnaceCookTime() {
        return furnaceCookTime;
    }

    public int bellows() {
        return bellows;
    }

    public SmelterType smelterType() {
        return smelterType;
    }

    public boolean speedBoost() {
        return speedBoost;
    }

    public int pendingFlux() {
        return pendingFlux;
    }

    public boolean isBurning() {
        return furnaceBurnTime > 0;
    }

    public int cookProgressScaled(int height) {
        return smeltTime <= 0 ? 0 : furnaceCookTime * height / smeltTime;
    }

    public int visScaled(int height) {
        return vis * height / MAX_VIS;
    }

    public int burnTimeRemainingScaled(int height) {
        int total = currentItemBurnTime <= 0 ? 200 : currentItemBurnTime;
        return furnaceBurnTime * height / total;
    }

    public void setBurnStateForValidation(int burnTime, int currentBurnTime, int cookTime, int targetSmeltTime) {
        furnaceBurnTime = Math.max(0, burnTime);
        currentItemBurnTime = Math.max(0, currentBurnTime);
        furnaceCookTime = Math.max(0, cookTime);
        smeltTime = Math.max(1, targetSmeltTime);
        markChangedAndSync();
        syncEnabledBlockState();
    }

    public void setMachineStateForValidation(
            int newBurnTime,
            int newCurrentItemBurnTime,
            int newCookTime,
            int newSmeltTime,
            boolean newSpeedBoost,
            int newBellows
    ) {
        furnaceBurnTime = Math.max(0, newBurnTime);
        currentItemBurnTime = Math.max(0, newCurrentItemBurnTime);
        furnaceCookTime = Math.max(0, newCookTime);
        smeltTime = Math.max(1, newSmeltTime);
        speedBoost = newSpeedBoost;
        bellows = newBellows;
        markChangedAndSync();
        syncEnabledBlockState();
    }

    public void setStoredAspectsForValidation(AspectList newAspects) {
        aspects = newAspects == null ? new AspectList() : newAspects.copy();
        vis = Math.max(0, Math.min(MAX_VIS, aspects.visSize()));
        markChangedAndSync();
    }

    public void setPendingFluxForValidation(int amount) {
        pendingFlux = Math.max(0, amount);
    }

    public boolean emitPendingFluxForValidation() {
        return pollutePendingFlux();
    }

    public boolean outputBufferedEssentiaForValidation() {
        return outputBufferedEssentia();
    }

    public void setTransferTicksForValidation(int ticks) {
        transferTicks = Math.max(0, ticks);
    }

    public int validVentCountForValidation() {
        return validVentDirections().length;
    }

    public static double ventMitigationChance(int ventCount) {
        return ventCount <= 0 ? 0.0D : 1.0D - Math.pow(1.0D - 0.333D, ventCount);
    }

    public boolean canAcceptAspects(AspectList incoming) {
        return incoming != null && incoming.size() > 0 && incoming.visSize() <= MAX_VIS - vis;
    }

    public boolean canSmeltStoredInputForValidation() {
        return canProcessInput();
    }

    public boolean takeFromContainer(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0 || aspects.getAmount(aspect) < amount) {
            return false;
        }
        aspects.remove(aspect, amount);
        vis = aspects.visSize();
        markChangedAndSync();
        return true;
    }

    public static float efficiencyForType(SmelterType type) {
        return switch (type) {
            case BASIC -> 0.8F;
            case THAUMIUM -> 0.9F;
            case VOID -> 0.95F;
        };
    }

    public static int speedForType(SmelterType type) {
        return type == SmelterType.THAUMIUM ? 10 : 15;
    }

    public static int outputInterval(SmelterType type, boolean alumentumBoost) {
        int speed = speedForType(type);
        return alumentumBoost ? Math.max(1, (int) (speed * 0.8F)) : speed;
    }

    public static int smeltTimeForVis(int visSize, int bellowsCount) {
        return Math.max(1, (int) (visSize * 2 * (1.0F - 0.125F * Math.max(0, bellowsCount))));
    }

    public enum SmelterType {
        BASIC,
        THAUMIUM,
        VOID
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCSmelterBlockEntity smelter) {
        if (!level.isClientSide) {
            smelter.tickServer();
        }
    }

    private void tickServer() {
        boolean wasBurning = isBurning();
        boolean dirty = refreshBellows();
        transferTicks++;

        if (furnaceBurnTime > 0) {
            furnaceBurnTime--;
            dirty = true;
        }

        // Legacy TileSmelter distills buffered slurry before starting fuel or completing a new item.
        dirty |= outputBufferedEssentia();

        if (!isBurning() && canProcessInput()) {
            dirty |= tryConsumeFuel();
        }

        if (isBurning() && canProcessInput()) {
            AspectList inputAspects = aspectsFromInput();
            smeltTime = smeltTimeForVis(inputAspects.visSize(), bellows);
            furnaceCookTime++;
            if (furnaceCookTime >= smeltTime) {
                furnaceCookTime = 0;
                dirty |= smeltInputAspects(inputAspects);
            }
            dirty = true;
        } else if (furnaceCookTime != 0) {
            furnaceCookTime = 0;
            dirty = true;
        }

        dirty |= pollutePendingFlux();

        if (wasBurning != isBurning()) {
            syncEnabledBlockState();
        }
        if (dirty) {
            markChangedAndSync();
        }
    }

    private boolean refreshBellows() {
        if (level == null) {
            return false;
        }
        int oldBellows = bellows;
        int foundBellows = 0;
        Direction smelterFacing = smelterFacing();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (direction == smelterFacing) {
                continue;
            }
            BlockState neighbour = level.getBlockState(worldPosition.relative(direction));
            if (neighbour.is(TCBlocks.BELLOWS.get())
                    && neighbour.hasProperty(TCBellowsBlock.FACING)
                    && neighbour.hasProperty(TCBellowsBlock.ENABLED)
                    && neighbour.getValue(TCBellowsBlock.ENABLED)
                    && neighbour.getValue(TCBellowsBlock.FACING) == direction.getOpposite()) {
                foundBellows++;
            }
        }
        bellows = foundBellows;
        return oldBellows != bellows;
    }

    private Direction smelterFacing() {
        return getBlockState().hasProperty(TCSmelterBlock.FACING)
                ? getBlockState().getValue(TCSmelterBlock.FACING)
                : Direction.NORTH;
    }

    private boolean canProcessInput() {
        return canAcceptAspects(aspectsFromInput());
    }

    private AspectList aspectsFromInput() {
        ItemStack input = items.get(SLOT_INPUT);
        return input.isEmpty() ? new AspectList() : new AspectList(input);
    }

    private boolean tryConsumeFuel() {
        ItemStack fuel = items.get(SLOT_FUEL);
        int burnTime = getBurnTime(fuel);
        if (burnTime <= 0) {
            return false;
        }

        furnaceBurnTime = burnTime;
        currentItemBurnTime = burnTime;
        speedBoost = fuel.is(TCItems.ALUMENTUM.get());
        ItemStack remainder = fuel.getCraftingRemainingItem().copy();
        fuel.shrink(1);
        if (fuel.isEmpty()) {
            items.set(SLOT_FUEL, remainder);
        }
        return true;
    }

    public static int getBurnTime(ItemStack stack) {
        return stack.isEmpty() ? 0 : stack.getBurnTime(null);
    }

    private ConversionResult applyEfficiencyLoss(AspectList inputAspects) {
        AspectList converted = new AspectList();
        if (inputAspects == null || inputAspects.size() == 0 || level == null) {
            return new ConversionResult(converted, 0);
        }

        float efficiency = efficiencyForType(smelterType);
        int fluxLoss = 0;
        for (Aspect aspect : inputAspects.getAspects()) {
            if (aspect == null) {
                continue;
            }
            int kept = 0;
            int amount = inputAspects.getAmount(aspect);
            float threshold = aspect == Aspect.FLUX ? efficiency * 0.66F : efficiency;
            for (int index = 0; index < amount; index++) {
                if (level.random.nextFloat() > threshold) {
                    fluxLoss++;
                } else {
                    kept++;
                }
            }
            if (kept > 0) {
                converted.add(aspect, kept);
            }
        }
        return new ConversionResult(converted, fluxLoss);
    }

    private boolean smeltInputAspects(AspectList inputAspects) {
        if (!canAcceptAspects(inputAspects)) {
            return false;
        }

        ConversionResult conversion = applyEfficiencyLoss(inputAspects);
        if (conversion.aspects().size() > 0) {
            aspects.add(conversion.aspects());
            vis = aspects.visSize();
        }
        pendingFlux += conversion.fluxLoss();

        ItemStack input = items.get(SLOT_INPUT);
        input.shrink(1);
        if (input.isEmpty()) {
            items.set(SLOT_INPUT, ItemStack.EMPTY);
        }
        return true;
    }

    private boolean outputBufferedEssentia() {
        if (level == null || level.isClientSide || aspects.size() == 0
                || transferTicks % outputInterval(smelterType, speedBoost) != 0) {
            return false;
        }

        boolean moved = tryOutputOneAt(worldPosition);
        Direction smelterFacing = smelterFacing();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (direction == smelterFacing) {
                continue;
            }
            BlockPos auxPos = worldPosition.relative(direction);
            BlockState auxState = level.getBlockState(auxPos);
            if (auxState.is(TCBlocks.SMELTER_AUX.get())
                    && auxState.hasProperty(TCSmelterAuxBlock.FACING)
                    && auxState.getValue(TCSmelterAuxBlock.FACING) == direction.getOpposite()) {
                moved |= tryOutputOneAt(auxPos);
            }
        }
        return moved;
    }

    private boolean tryOutputOneAt(BlockPos outputPos) {
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect != null && aspects.getAmount(aspect) > 0
                    && TCAlembicBlockEntity.processAlembics(level, outputPos, aspect)) {
                return takeFromContainer(aspect, 1);
            }
        }
        return false;
    }

    private boolean pollutePendingFlux() {
        if (level == null || level.isClientSide || pendingFlux <= 0) {
            return false;
        }

        int pollution = 0;
        Direction[] vents = validVentDirections();
        for (int point = 0; point < pendingFlux; point++) {
            boolean vented = false;
            for (Direction ventDirection : vents) {
                if (level.random.nextFloat() < 0.333F) {
                    level.blockEvent(
                            worldPosition,
                            getBlockState().getBlock(),
                            1,
                            ventDirection.getOpposite().ordinal()
                    );
                    vented = true;
                    break;
                }
            }
            if (!vented) {
                pollution++;
            }
        }
        if (pollution > 0) {
            AuraHelper.polluteAura(level, worldPosition, pollution, true);
        }
        pendingFlux = 0;
        return true;
    }

    private Direction[] validVentDirections() {
        if (level == null) {
            return new Direction[0];
        }
        java.util.ArrayList<Direction> vents = new java.util.ArrayList<>(3);
        Direction smelterFacing = smelterFacing();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (direction == smelterFacing) {
                continue;
            }
            BlockState neighbour = level.getBlockState(worldPosition.relative(direction));
            if (neighbour.is(TCBlocks.SMELTER_VENT.get())
                    && neighbour.hasProperty(TCSmelterVentBlock.FACING)
                    && neighbour.getValue(TCSmelterVentBlock.FACING) == direction.getOpposite()) {
                vents.add(direction);
            }
        }
        return vents.toArray(Direction[]::new);
    }

    @Override
    public boolean triggerEvent(int id, int data) {
        if (id == 1 && level != null && level.isClientSide) {
            Direction direction = Direction.from3DDataValue(data);
            level.playLocalSound(
                    worldPosition.getX() + 0.5D + direction.getOpposite().getStepX(),
                    worldPosition.getY() + 0.5D,
                    worldPosition.getZ() + 0.5D + direction.getOpposite().getStepZ(),
                    SoundEvents.FIRE_EXTINGUISH,
                    SoundSource.BLOCKS,
                    0.25F,
                    2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F,
                    false
            );
            for (int index = 0; index < 4; index++) {
                double x = worldPosition.getX() + 0.5D + direction.getOpposite().getStepX()
                        + 0.1D - level.random.nextDouble() * 0.2D;
                double y = worldPosition.getY() + 0.5D + 0.1D - level.random.nextDouble() * 0.2D;
                double z = worldPosition.getZ() + 0.5D + direction.getOpposite().getStepZ()
                        + 0.1D - level.random.nextDouble() * 0.2D;
                level.addParticle(
                        net.minecraft.core.particles.ParticleTypes.CLOUD,
                        x,
                        y,
                        z,
                        direction.getOpposite().getStepX() / 4.0D + 0.1D - level.random.nextDouble() * 0.2D,
                        0.1D - level.random.nextDouble() * 0.2D,
                        direction.getOpposite().getStepZ() / 4.0D + 0.1D - level.random.nextDouble() * 0.2D
                );
            }
            return true;
        }
        return super.triggerEvent(id, data);
    }

    private void syncEnabledBlockState() {
        if (level == null || level.isClientSide) {
            return;
        }
        BlockState state = getBlockState();
        if (state.hasProperty(TCSmelterBlock.ENABLED)
                && state.getValue(TCSmelterBlock.ENABLED) != isBurning()) {
            level.setBlock(
                    worldPosition,
                    state.setValue(TCSmelterBlock.ENABLED, isBurning()),
                    Block.UPDATE_ALL
            );
        }
    }

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
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
        ItemStack stored = stack.copy();
        stored.limitSize(getMaxStackSize(stored));
        items.set(slot, stored);
        markChangedAndSync();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == SLOT_INPUT) {
            AspectList list = stack.isEmpty() ? new AspectList() : new AspectList(stack);
            return list.size() > 0;
        }
        return slot == SLOT_FUEL && getBurnTime(stack) > 0;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_BOTTOM;
        }
        if (side == Direction.UP) {
            return SLOTS_TOP;
        }
        return SLOTS_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return side != Direction.UP && canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return side != Direction.UP || slot != SLOT_FUEL
                || stack.is(net.minecraft.world.item.Items.BUCKET);
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

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.thaumcraft.smelter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new TCSmelterMenu(containerId, inventory, this);
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(worldPosition);
    }

    public void dropContents(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            Containers.dropContents(level, pos, this);
            clearContent();
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
        ContainerHelper.saveAllItems(tag, items, registries);
        aspects.writeToNBT(tag);
        tag.putInt("Vis", vis);
        tag.putInt("SmeltTime", smeltTime);
        tag.putBoolean("SpeedBoost", speedBoost);
        tag.putInt("BurnTime", furnaceBurnTime);
        tag.putInt("CurrentItemBurnTime", currentItemBurnTime);
        tag.putInt("CookTime", furnaceCookTime);
        tag.putInt("Bellows", bellows);
        tag.putInt("TransferTicks", transferTicks);
        tag.putInt("PendingFlux", pendingFlux);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int slot = 0; slot < items.size(); slot++) {
            items.set(slot, ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(tag, items, registries);
        aspects = new AspectList();
        aspects.readFromNBT(tag);
        vis = Math.max(0, Math.min(MAX_VIS, aspects.visSize()));
        smeltTime = Math.max(1, tag.contains("SmeltTime") ? tag.getInt("SmeltTime") : BASE_SMELT_TIME);
        speedBoost = tag.getBoolean("SpeedBoost");
        furnaceBurnTime = Math.max(0, tag.getInt("BurnTime"));
        currentItemBurnTime = Math.max(0, tag.getInt("CurrentItemBurnTime"));
        furnaceCookTime = Math.max(0, tag.getInt("CookTime"));
        bellows = tag.contains("Bellows") ? tag.getInt("Bellows") : -1;
        transferTicks = Math.max(0, tag.getInt("TransferTicks"));
        pendingFlux = Math.max(0, tag.getInt("PendingFlux"));
    }

    private record ConversionResult(AspectList aspects, int fluxLoss) {
    }
}
