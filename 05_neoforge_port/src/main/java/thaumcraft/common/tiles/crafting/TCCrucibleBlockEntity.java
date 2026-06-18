package thaumcraft.common.tiles.crafting;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.misc.TCNitorBlock;
import thaumcraft.common.crafting.crucible.TCCrucibleRecipe;
import thaumcraft.common.crafting.crucible.TCCrucibleRecipeMatcher;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCSounds;

public class TCCrucibleBlockEntity extends BlockEntity {
    public static final int WATER_CAPACITY = 1000;
    public static final int WATER_PER_CRAFT = 50;
    public static final int BOILING_HEAT = 151;
    public static final int MAX_HEAT = 200;
    public static final int LEGACY_ASPECT_CAP = 500;

    private int waterAmount;
    private short heat;
    private AspectList aspects = new AspectList();

    public TCCrucibleBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.CRUCIBLE.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCCrucibleBlockEntity crucible) {
        if (!(level instanceof ServerLevel)) {
            return;
        }

        short previousHeat = crucible.heat;
        if (crucible.waterAmount > 0 && isHeatSource(level.getBlockState(pos.below()))) {
            if (crucible.heat < MAX_HEAT) {
                crucible.heat++;
            }
        } else if (crucible.heat > 0) {
            crucible.heat--;
        }

        if (previousHeat != crucible.heat) {
            crucible.setChanged();
            if ((previousHeat < BOILING_HEAT) != (crucible.heat < BOILING_HEAT)) {
                crucible.markChangedAndSync();
            }
        }
    }

    public CrucibleUseResult useCatalystOrDissolve(ServerPlayer player, ItemStack stack) {
        if (level == null || level.isClientSide || stack == null || stack.isEmpty()) {
            return CrucibleUseResult.IGNORED;
        }
        if (!isBoiling()) {
            return CrucibleUseResult.NOT_BOILING;
        }

        ItemStack singleItem = stack.copy();
        singleItem.setCount(1);
        Optional<RecipeHolder<TCCrucibleRecipe>> matchingRecipe = TCCrucibleRecipeMatcher.findMatchingRecipe(
                level.getRecipeManager(),
                player,
                aspects,
                singleItem
        );
        if (matchingRecipe.isPresent()) {
            craft(matchingRecipe.get().value());
            return CrucibleUseResult.CRAFTED;
        }

        AspectList objectAspects = AspectHelper.getObjectAspects(singleItem);
        if (objectAspects == null || objectAspects.size() == 0) {
            return CrucibleUseResult.NO_ASPECTS;
        }

        aspects.add(objectAspects);
        playBubbleSound();
        markChangedAndSync();
        return CrucibleUseResult.DISSOLVED_ASPECTS;
    }

    public void addAspectForValidation(Aspect aspect, int amount) {
        if (aspect != null && amount > 0) {
            aspects.add(aspect, amount);
            markChangedAndSync();
        }
    }

    public void addAspectsForValidation(AspectList addedAspects) {
        if (addedAspects != null && addedAspects.size() > 0) {
            aspects.add(addedAspects);
            markChangedAndSync();
        }
    }

    public boolean canCraftForValidation(ServerPlayer player, ItemStack catalyst) {
        return level != null
                && isBoiling()
                && TCCrucibleRecipeMatcher.findMatchingRecipe(level.getRecipeManager(), player, aspects, catalyst).isPresent();
    }

    public AspectList getAspects() {
        return aspects.copy();
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public short getHeat() {
        return heat;
    }

    public boolean isBoiling() {
        return waterAmount > 0 && heat >= BOILING_HEAT;
    }

    public boolean fillWaterFromBucket() {
        if (waterAmount >= WATER_CAPACITY) {
            return false;
        }
        waterAmount = WATER_CAPACITY;
        markChangedAndSync();
        return true;
    }

    public void setWaterForValidation(int amount) {
        waterAmount = Math.max(0, Math.min(WATER_CAPACITY, amount));
        markChangedAndSync();
    }

    public void setHeatForValidation(int amount) {
        heat = (short) Math.max(0, Math.min(MAX_HEAT, amount));
        markChangedAndSync();
    }

    public void spillRemnants() {
        if (waterAmount <= 0 && aspects.visSize() <= 0) {
            return;
        }
        waterAmount = 0;
        heat = 0;
        aspects = new AspectList();
        if (level != null) {
            level.playSound(null, worldPosition, TCSounds.SPILL.get(), SoundSource.BLOCKS, 0.2F, 1.0F);
        }
        markChangedAndSync();
    }

    public int comparatorSignal() {
        int amount = aspects.visSize();
        if (amount <= 0) {
            return 0;
        }
        return Math.min(15, (int) Math.floor(amount / (float) LEGACY_ASPECT_CAP * 14.0F) + 1);
    }

    private void craft(TCCrucibleRecipe recipe) {
        aspects = TCCrucibleRecipeMatcher.removeRequiredAspects(recipe, aspects);
        waterAmount = Math.max(0, waterAmount - WATER_PER_CRAFT);
        ejectItem(recipe.result());
        playBubbleSound();
        markChangedAndSync();
    }

    private void ejectItem(ItemStack stack) {
        if (level == null || level.isClientSide || stack.isEmpty()) {
            return;
        }
        ItemEntity entity = new ItemEntity(
                level,
                worldPosition.getX() + 0.5D,
                worldPosition.getY() + 0.71D,
                worldPosition.getZ() + 0.5D,
                stack.copy()
        );
        entity.setDeltaMovement(0.0D, 0.075D, 0.0D);
        level.addFreshEntity(entity);
    }

    private void playBubbleSound() {
        if (level != null) {
            level.playSound(null, worldPosition, TCSounds.BUBBLE.get(), SoundSource.BLOCKS, 0.2F, 1.0F + level.random.nextFloat() * 0.4F);
        }
    }

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }

    private static boolean isHeatSource(BlockState state) {
        return state.is(Blocks.LAVA)
                || state.getBlock() instanceof FireBlock
                || state.getBlock() instanceof MagmaBlock
                || state.getBlock() instanceof TCNitorBlock;
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
        tag.putInt("Water", waterAmount);
        tag.putShort("Heat", heat);
        aspects.writeToNBT(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        waterAmount = Math.max(0, Math.min(WATER_CAPACITY, tag.getInt("Water")));
        heat = (short) Math.max(0, Math.min(MAX_HEAT, tag.getShort("Heat")));
        AspectList loadedAspects = new AspectList();
        loadedAspects.readFromNBT(tag);
        aspects = loadedAspects;
    }

    public enum CrucibleUseResult {
        IGNORED(false),
        NOT_BOILING(false),
        NO_ASPECTS(false),
        DISSOLVED_ASPECTS(true),
        CRAFTED(true);

        private final boolean consumesCatalyst;

        CrucibleUseResult(boolean consumesCatalyst) {
            this.consumesCatalyst = consumesCatalyst;
        }

        public boolean consumesCatalyst() {
            return consumesCatalyst;
        }
    }
}
