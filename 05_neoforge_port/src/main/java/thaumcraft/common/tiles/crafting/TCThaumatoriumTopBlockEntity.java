package thaumcraft.common.tiles.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.registry.TCBlockEntities;

public final class TCThaumatoriumTopBlockEntity extends BlockEntity implements WorldlyContainer, TCEssentiaTransport {
    private static final int[] SLOTS = {TCThaumatoriumBlockEntity.SLOT_CATALYST};
    private final IItemHandler unsidedItemHandler = new SidedInvWrapper(this, null);

    public TCThaumatoriumTopBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.THAUMATORIUM_TOP.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCThaumatoriumTopBlockEntity top) {
        if (!level.isClientSide && !(level.getBlockEntity(pos.below()) instanceof TCThaumatoriumBlockEntity)) {
            top.setChanged();
        }
    }

    @Nullable
    public TCThaumatoriumBlockEntity bottom() {
        if (level == null) {
            return null;
        }
        BlockEntity blockEntity = level.getBlockEntity(worldPosition.below());
        return blockEntity instanceof TCThaumatoriumBlockEntity thaumatorium ? thaumatorium : null;
    }

    public IItemHandler itemHandler(@Nullable Direction side) {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium == null ? unsidedItemHandler : thaumatorium.itemHandler(side);
    }

    @Override
    public boolean isConnectable(Direction face) {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium != null && thaumatorium.isConnectable(face);
    }

    @Override
    public boolean canInputFrom(Direction face) {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium != null && thaumatorium.canInputFrom(face);
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return false;
    }

    @Override
    public TCEssentiaSuction getSuction(Direction face) {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium == null ? TCEssentiaSuction.NONE : thaumatorium.getSuction(face);
    }

    @Override
    public int getMinimumSuction() {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium == null ? 0 : thaumatorium.getMinimumSuction();
    }

    @Override
    public TCEssentiaStack getEssentia(Direction face) {
        return TCEssentiaStack.EMPTY;
    }

    @Override
    public int addEssentia(String aspect, int amount, Direction face, boolean simulate) {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium == null ? 0 : thaumatorium.addEssentia(aspect, amount, face, simulate);
    }

    @Override
    public int takeEssentia(String aspect, int amount, Direction face, boolean simulate) {
        return 0;
    }

    @Override
    public int getContainerSize() {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium == null ? TCThaumatoriumBlockEntity.SLOT_COUNT : thaumatorium.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium == null || thaumatorium.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium == null ? ItemStack.EMPTY : thaumatorium.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium == null ? ItemStack.EMPTY : thaumatorium.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium == null ? ItemStack.EMPTY : thaumatorium.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        if (thaumatorium != null) {
            thaumatorium.setItem(slot, stack);
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium != null && thaumatorium.canPlaceItem(slot, stack);
    }

    @Override
    public void clearContent() {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        if (thaumatorium != null) {
            thaumatorium.clearContent();
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium == null ? SLOTS : thaumatorium.getSlotsForFace(side);
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium != null && thaumatorium.canPlaceItemThroughFace(slot, stack, side);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium != null && thaumatorium.canTakeItemThroughFace(slot, stack, side);
    }

    @Override
    public boolean stillValid(Player player) {
        TCThaumatoriumBlockEntity thaumatorium = bottom();
        return thaumatorium != null && Container.stillValidBlockEntity(thaumatorium, player);
    }
}
