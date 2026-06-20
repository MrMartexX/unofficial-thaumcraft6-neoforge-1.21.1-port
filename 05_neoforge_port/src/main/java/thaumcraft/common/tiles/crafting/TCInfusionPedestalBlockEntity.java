package thaumcraft.common.tiles.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.registry.TCBlockEntities;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import thaumcraft.common.blocks.devices.TCInlayNetwork;
import thaumcraft.common.crafting.infusion.TCInfusionSurroundingsInvalidator;
import thaumcraft.common.tiles.devices.TCStabilizerBlockEntity;

public class TCInfusionPedestalBlockEntity extends BlockEntity {
    private static final int SLOT = 0;
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    public TCInfusionPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.INFUSION_PEDESTAL.get(), pos, state);
    }

    public ItemStack getStoredStack() {
        return items.get(SLOT).copy();
    }

    public boolean insertOne(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !items.get(SLOT).isEmpty()) {
            return false;
        }
        items.set(SLOT, stack.copyWithCount(1));
        markChangedAndSync();
        return true;
    }

    public void setStoredForValidation(ItemStack stack) {
        items.set(SLOT, stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
        markChangedAndSync();
    }

    public void setStoredForCrafting(ItemStack stack) {
        items.set(SLOT, stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        markChangedAndSync();
    }

    public ItemStack extractStored() {
        ItemStack stored = items.get(SLOT);
        if (stored.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack extracted = stored.copy();
        items.set(SLOT, ItemStack.EMPTY);
        markChangedAndSync();
        return extracted;
    }

    public void dropStored(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        ItemStack stored = extractStored();
        if (!stored.isEmpty()) {
            Containers.dropItemStack(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, stored);
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        dropStored(level, pos);
    }

    public BlockPos findInstabilityMitigator() {
        if (!(level instanceof ServerLevel serverLevel) || TCInlayNetwork.charge(getBlockState()) <= 0) {
            return null;
        }
        return seekSource(serverLevel, worldPosition, TCInlayNetwork.charge(getBlockState()), new HashSet<>());
    }

    private static BlockPos seekSource(ServerLevel level, BlockPos pos, int lastCharge, Set<BlockPos> visited) {
        if (!visited.add(pos.immutable())) {
            return null;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos adjacent = pos.relative(direction);
            if (TCInlayNetwork.sourceStrengthAt(level, adjacent) >= 5
                    && level.getBlockEntity(adjacent) instanceof TCStabilizerBlockEntity) {
                return adjacent;
            }
            BlockState state = level.getBlockState(adjacent);
            int charge = TCInlayNetwork.charge(state);
            if (TCInlayNetwork.isNetworkNode(state) && charge > lastCharge) {
                BlockPos source = seekSource(level, adjacent, charge, visited);
                if (source != null) {
                    return source;
                }
            }
        }
        return null;
    }

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            TCInfusionSurroundingsInvalidator.requestNearby(level, worldPosition);
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
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items.set(SLOT, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        ItemStack stored = items.get(SLOT);
        if (!stored.isEmpty() && stored.getCount() > 1) {
            stored.setCount(1);
        }
    }
}
