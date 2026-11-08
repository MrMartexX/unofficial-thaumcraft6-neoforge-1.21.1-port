package thaumcraft.common.tiles.devices;

import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.items.TCRechargeHelper;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.tiles.crafting.TCInfusionPedestalBlockEntity;

public class TCRechargePedestalBlockEntity extends TCInfusionPedestalBlockEntity implements WorldlyContainer {
    private static final int[] SLOTS = new int[] { 0 };
    private int counter = 0;

    public TCRechargePedestalBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.RECHARGE_PEDESTAL.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCRechargePedestalBlockEntity pedestal) {
        if (pedestal.counter++ % 10 == 0) {
            ItemStack stack = pedestal.items.get(0);
            if (!stack.isEmpty() && TCRechargeHelper.canRecharge(stack)) {
                int currentCharge = TCRechargeHelper.getCharge(stack);
                int maxCharge = TCRechargeHelper.getMaxCharge(stack);
                if (currentCharge < maxCharge) {
                    int needed = Math.min(5, maxCharge - currentCharge);
                    float drained = AuraHelper.drainVis(level, pos, (float) needed, false);
                    if (drained > 0.0F) {
                        int toAdd = Math.min(needed, Math.round(drained));
                        if (toAdd > 0) {
                            TCRechargeHelper.recharge(stack, toAdd);
                            pedestal.markChangedAndSync();
                            ArrayList<Aspect> al = Aspect.getPrimalAspects();
                            int color = al.get(level.random.nextInt(al.size())).getColor();
                            level.blockEvent(pos, state.getBlock(), 5, color);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean insertOne(ItemStack stack) {
        if (!TCRechargeHelper.canRecharge(stack)) {
            return false;
        }
        return super.insertOne(stack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return slot == 0 && TCRechargeHelper.canRecharge(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == 0;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? items.get(0) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot == 0 && !items.get(0).isEmpty()) {
            ItemStack removed = items.get(0).split(amount);
            markChangedAndSync();
            return removed;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0) {
            ItemStack res = items.get(0);
            items.set(0, ItemStack.EMPTY);
            return res;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            items.set(0, stack);
            markChangedAndSync();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.set(0, ItemStack.EMPTY);
        markChangedAndSync();
    }
}
