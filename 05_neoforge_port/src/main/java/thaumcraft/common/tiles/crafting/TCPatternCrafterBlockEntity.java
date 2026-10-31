package thaumcraft.common.tiles.crafting;

import java.util.ArrayList;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.crafting.TCPatternCrafterBlock;
import thaumcraft.common.registry.TCBlockEntities;

/**
 * TC6 Pattern Crafter tile entity.
 * Pulls ingredients from above, matches them against current crafting pattern,
 * expends vis from the aura, and outputs crafted items and remainders below.
 */
public class TCPatternCrafterBlockEntity extends BlockEntity {
    private byte type = 0;
    private float power = 0.0F;
    private int count = 0;

    public TCPatternCrafterBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.PATTERN_CRAFTER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCPatternCrafterBlockEntity crafter) {
        if (!level.isClientSide) {
            crafter.tickServer(state);
        }
    }

    private void tickServer(BlockState state) {
        if (level == null || level.isClientSide) return;

        boolean enabled = !state.hasProperty(TCPatternCrafterBlock.ENABLED) || state.getValue(TCPatternCrafterBlock.ENABLED);
        if (!enabled) return;

        if (count++ % 20 != 0) return;

        if (power <= 0.0F) {
            power += AuraHelper.drainVis(level, worldPosition, 5.0F, false);
        }

        int needed = getRequiredCount(type);
        IItemHandler above = level.getCapability(Capabilities.ItemHandler.BLOCK, worldPosition.above(), Direction.DOWN);
        IItemHandler below = level.getCapability(Capabilities.ItemHandler.BLOCK, worldPosition.below(), Direction.UP);

        if (above == null || below == null) return;

        for (int slot = 0; slot < above.getSlots(); slot++) {
            ItemStack stackInSlot = above.getStackInSlot(slot);
            if (stackInSlot.isEmpty() || stackInSlot.getCount() < needed) continue;

            // Attempt test craft
            ItemStack sample = stackInSlot.copyWithCount(1);
            CraftingInput input = buildCraftingInput(type, sample);
            Optional<RecipeHolder<CraftingRecipe>> matched = level.getRecipeManager()
                    .getRecipeFor(RecipeType.CRAFTING, input, level);

            if (matched.isEmpty()) continue;

            CraftingRecipe recipe = matched.get().value();
            ItemStack result = recipe.assemble(input, level.registryAccess());
            if (result.isEmpty()) continue;

            NonNullList<ItemStack> remaining = recipe.getRemainingItems(input);

            // Verify if result and remaining can fit into below
            if (!canFitAll(below, result, remaining)) continue;

            if (power >= 1.0F) {
                // Extract consumed items from above
                ItemStack extracted = above.extractItem(slot, needed, false);
                if (extracted.getCount() == needed) {
                    // Insert result
                    ItemHandlerHelper.insertItem(below, result.copy(), false);
                    for (ItemStack rem : remaining) {
                        if (!rem.isEmpty()) {
                            ItemHandlerHelper.insertItem(below, rem.copy(), false);
                        }
                    }
                    power -= 1.0F;
                    setChanged();
                    level.playSound(null, worldPosition, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 0.25F, 1.2F);
                    break;
                }
            }
        }
    }

    private static boolean canFitAll(IItemHandler handler, ItemStack result, NonNullList<ItemStack> remaining) {
        if (!ItemHandlerHelper.insertItem(handler, result.copy(), true).isEmpty()) {
            return false;
        }
        for (ItemStack rem : remaining) {
            if (!rem.isEmpty() && !ItemHandlerHelper.insertItem(handler, rem.copy(), true).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static int getRequiredCount(byte patternType) {
        return switch (patternType) {
            case 0 -> 9;
            case 1 -> 1;
            case 2, 3 -> 2;
            case 4 -> 4;
            case 5, 6 -> 3;
            case 7, 8 -> 6;
            case 9 -> 8;
            default -> 9;
        };
    }

    public static CraftingInput buildCraftingInput(byte patternType, ItemStack item) {
        NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
        switch (patternType) {
            case 0 -> { // 3x3
                for (int i = 0; i < 9; i++) items.set(i, item.copy());
            }
            case 1 -> { // 1x1
                items.set(0, item.copy());
            }
            case 2 -> { // 2x1 horizontal
                items.set(0, item.copy());
                items.set(1, item.copy());
            }
            case 3 -> { // 1x2 vertical
                items.set(0, item.copy());
                items.set(3, item.copy());
            }
            case 4 -> { // 2x2
                items.set(0, item.copy());
                items.set(1, item.copy());
                items.set(3, item.copy());
                items.set(4, item.copy());
            }
            case 5 -> { // 3x1 horizontal
                items.set(0, item.copy());
                items.set(1, item.copy());
                items.set(2, item.copy());
            }
            case 6 -> { // 1x3 vertical
                items.set(0, item.copy());
                items.set(3, item.copy());
                items.set(6, item.copy());
            }
            case 7 -> { // 3x2 horizontal
                for (int i = 0; i < 6; i++) items.set(i, item.copy());
            }
            case 8 -> { // 2x3 vertical
                items.set(0, item.copy()); items.set(1, item.copy());
                items.set(3, item.copy()); items.set(4, item.copy());
                items.set(6, item.copy()); items.set(7, item.copy());
            }
            case 9 -> { // 3x3 hollow / ring
                for (int i = 0; i < 9; i++) {
                    if (i != 4) items.set(i, item.copy());
                }
            }
        }
        return CraftingInput.of(3, 3, items);
    }

    public void cycle() {
        type++;
        if (type > 9) type = 0;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public byte getPatternType() {
        return type;
    }

    public void setPatternType(byte type) {
        this.type = (byte) Math.max(0, Math.min(9, type));
        setChanged();
    }

    public float getPower() {
        return power;
    }

    public void setPowerForTesting(float power) {
        this.power = power;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putByte("type", type);
        tag.putFloat("power", power);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        type = tag.getByte("type");
        power = tag.getFloat("power");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putByte("type", type);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
