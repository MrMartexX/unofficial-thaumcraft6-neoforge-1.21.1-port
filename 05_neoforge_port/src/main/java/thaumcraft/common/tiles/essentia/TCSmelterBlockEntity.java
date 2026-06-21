package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.essentia.TCSmelterBlock;
import thaumcraft.common.registry.TCBlockEntities;

/**
 * First server-owned smelter machine model.
 *
 * This class now owns basic fuel/cook progression and input item aspect conversion. It still
 * intentionally leaves bellows discovery, efficiency/flux loss, vents and Alembic output for
 * later focused slices.
 */
public final class TCSmelterBlockEntity extends BlockEntity {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_COUNT = 2;
    public static final int MAX_VIS = 256;
    public static final int BASE_SMELT_TIME = 100;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
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
        super(TCBlockEntities.SMELTER_BASIC.get(), pos, state);
    }

    public ItemStack getStoredItem(int slot) {
        return slot >= 0 && slot < SLOT_COUNT ? items.get(slot) : ItemStack.EMPTY;
    }

    public void setStoredItemForValidation(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SLOT_COUNT) {
            return;
        }
        items.set(slot, stack == null ? ItemStack.EMPTY : stack.copy());
        markChangedAndSync();
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

    public boolean speedBoost() {
        return speedBoost;
    }
    public int pendingFlux() {
        return pendingFlux;
    }

    public boolean isBurning() {
        return furnaceBurnTime > 0;
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

    public static int smeltTimeForVis(int visSize, int bellowsCount) {
        return Math.max(1, (int) (visSize * 2 * (1.0F - 0.125F * Math.max(0, bellowsCount))));
    }

    public enum SmelterType {
        BASIC,
        THAUMIUM,
        VOID
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCSmelterBlockEntity smelter) {
        if (level == null || level.isClientSide) {
            return;
        }
        smelter.tickServer();
    }

    private void tickServer() {
        boolean wasBurning = isBurning();
        boolean dirty = false;

        if (furnaceBurnTime > 0) {
            furnaceBurnTime--;
            dirty = true;
        }

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

        dirty |= outputBufferedEssentia();

        if (wasBurning != isBurning()) {
            syncEnabledBlockState();
        }
        if (dirty) {
            markChangedAndSync();
        }
    }

    private boolean canProcessInput() {
        AspectList inputAspects = aspectsFromInput();
        return canAcceptAspects(inputAspects);
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
        speedBoost = false;
        fuel.shrink(1);
        if (fuel.isEmpty()) {
            items.set(SLOT_FUEL, ItemStack.EMPTY);
        }
        return true;
    }

    private static int getBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        return AbstractFurnaceBlockEntity.getFuel().getOrDefault(stack.getItem(), 0);
    }


    private AspectList applyEfficiencyLoss(AspectList inputAspects) {
        AspectList converted = new AspectList();
        if (inputAspects == null || inputAspects.size() == 0) {
            return converted;
        }
        RandomSource random = RandomSource.create();
        float efficiency = efficiencyForType(SmelterType.BASIC);
        int fluxLoss = 0;
        for (Aspect aspect : inputAspects.getAspects()) {
            if (aspect == null) {
                continue;
            }
            int kept = 0;
            int amount = inputAspects.getAmount(aspect);
            float threshold = isFluxAspect(aspect) ? efficiency * 0.66F : efficiency;
            for (int i = 0; i < amount; i++) {
                if (random.nextFloat() > threshold) {
                    fluxLoss++;
                } else {
                    kept++;
                }
            }
            if (kept > 0) {
                converted.add(aspect, kept);
            }
        }
        pendingFlux += fluxLoss;
        return converted;
    }

    private static boolean isFluxAspect(Aspect aspect) {
        return aspect != null && "flux".equals(aspect.getTag());
    }
    private boolean smeltInputAspects(AspectList inputAspects) {
        if (!canAcceptAspects(inputAspects)) {
            return false;
        }
        AspectList convertedAspects = applyEfficiencyLoss(inputAspects);
        if (convertedAspects.size() > 0) {
            aspects.add(convertedAspects);
            vis = aspects.visSize();
        }
        ItemStack input = items.get(SLOT_INPUT);
        input.shrink(1);
        if (input.isEmpty()) {
            items.set(SLOT_INPUT, ItemStack.EMPTY);
        }
        return true;
    }


    private boolean outputBufferedEssentia() {
        if (level == null || level.isClientSide || aspects == null || aspects.size() == 0) {
            return false;
        }
        transferTicks++;
        int speed = speedForType(SmelterType.BASIC);
        if (transferTicks % speed != 0) {
            return false;
        }
        if (!(level.getBlockEntity(worldPosition.above()) instanceof TCAlembicBlockEntity alembic)) {
            return false;
        }
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect == null || aspects.getAmount(aspect) <= 0) {
                continue;
            }
            int remainder = alembic.addToContainer(aspect, 1);
            if (remainder == 0) {
                return takeFromContainer(aspect, 1);
            }
        }
        return false;
    }
    private void syncEnabledBlockState() {
        if (level == null || level.isClientSide) {
            return;
        }
        BlockState state = getBlockState();
        if (!state.hasProperty(TCSmelterBlock.ENABLED)) {
            return;
        }
        boolean burning = isBurning();
        if (state.getValue(TCSmelterBlock.ENABLED) != burning) {
            level.setBlock(worldPosition, state.setValue(TCSmelterBlock.ENABLED, burning), Block.UPDATE_ALL);
        }
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
        ContainerHelper.loadAllItems(tag, items, registries);
        aspects = new AspectList();
        aspects.readFromNBT(tag);
        vis = Math.max(0, Math.min(MAX_VIS, tag.contains("Vis") ? tag.getInt("Vis") : aspects.visSize()));
        smeltTime = Math.max(1, tag.contains("SmeltTime") ? tag.getInt("SmeltTime") : BASE_SMELT_TIME);
        speedBoost = tag.getBoolean("SpeedBoost");
        furnaceBurnTime = Math.max(0, tag.getInt("BurnTime"));
        currentItemBurnTime = Math.max(0, tag.getInt("CurrentItemBurnTime"));
        furnaceCookTime = Math.max(0, tag.getInt("CookTime"));
        bellows = tag.contains("Bellows") ? tag.getInt("Bellows") : -1;
        transferTicks = Math.max(0, tag.getInt("TransferTicks"));
        pendingFlux = Math.max(0, tag.getInt("PendingFlux"));
    }
}


