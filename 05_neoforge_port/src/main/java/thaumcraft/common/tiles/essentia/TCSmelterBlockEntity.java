package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.NonNullList;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.registry.TCBlockEntities;

/**
 * First server-owned smelter machine model boundary.
 *
 * This intentionally stores the legacy two-slot/aspect/fuel state without yet implementing
 * recipe ticking, fuel consumption, bellows discovery or Alembic production.
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
    }

    public void setStoredAspectsForValidation(AspectList newAspects) {
        aspects = newAspects == null ? new AspectList() : newAspects.copy();
        vis = Math.max(0, Math.min(MAX_VIS, aspects.visSize()));
        markChangedAndSync();
    }

    public boolean canAcceptAspects(AspectList incoming) {
        return incoming != null && incoming.size() > 0 && incoming.visSize() <= MAX_VIS - vis;
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
        return (int) (visSize * 2 * (1.0F - 0.125F * Math.max(0, bellowsCount)));
    }

    public enum SmelterType {
        BASIC,
        THAUMIUM,
        VOID
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
    }
}
