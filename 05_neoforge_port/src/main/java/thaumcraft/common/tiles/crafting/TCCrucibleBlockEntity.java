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
import net.minecraft.world.entity.Entity;
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
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.misc.TCNitorBlock;
import thaumcraft.common.crafting.crucible.TCCrucibleRecipe;
import thaumcraft.common.crafting.crucible.TCCrucibleRecipeMatcher;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.lib.fx.TCFXDispatcher;

public class TCCrucibleBlockEntity extends BlockEntity {
    public static final int WATER_CAPACITY = 1000;
    public static final int WATER_PER_CRAFT = 50;
    public static final int BOILING_HEAT = 151;
    public static final int MAX_HEAT = 200;
    public static final int LEGACY_ASPECT_CAP = 500;
    public static final String SPECIAL_ITEM_MARKER = "thaumcraft:crucible_special_item";
    private int waterAmount;
    private short heat;
    private int livingContactDelay;
    private long spillCounter = -100L;
    private AspectList aspects = new AspectList();

    public TCCrucibleBlockEntity(BlockPos pos, BlockState blockState) {
        super(TCBlockEntities.CRUCIBLE.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCCrucibleBlockEntity crucible) {
        if (!(level instanceof ServerLevel)) {
            return;
        }

        crucible.spillCounter++;
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

        if (crucible.aspects.visSize() > LEGACY_ASPECT_CAP) {
            crucible.spillRandom();
        }
        if (crucible.spillCounter >= 100L) {
            crucible.spillRandom();
            crucible.spillCounter = 0L;
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, TCCrucibleBlockEntity crucible) {
        if (level == null || !level.isClientSide || crucible.waterAmount <= 0) {
            return;
        }
        float fluidHeight = crucible.getFluidHeight();
        if (crucible.heat > 150) {
            TCFXDispatcher.crucibleFroth(
                    level,
                    pos.getX() + 0.2D + level.random.nextFloat() * 0.6D,
                    pos.getY() + fluidHeight,
                    pos.getZ() + 0.2D + level.random.nextFloat() * 0.6D
            );
            if (crucible.aspects.visSize() > LEGACY_ASPECT_CAP) {
                for (int index = 0; index < 2; index++) {
                    TCFXDispatcher.crucibleFrothDown(level, pos.getX(), pos.getY() + 1.0D, pos.getZ() + level.random.nextFloat());
                    TCFXDispatcher.crucibleFrothDown(level, pos.getX() + 1.0D, pos.getY() + 1.0D, pos.getZ() + level.random.nextFloat());
                    TCFXDispatcher.crucibleFrothDown(level, pos.getX() + level.random.nextFloat(), pos.getY() + 1.0D, pos.getZ());
                    TCFXDispatcher.crucibleFrothDown(level, pos.getX() + level.random.nextFloat(), pos.getY() + 1.0D, pos.getZ() + 1.0D);
                }
            }
        }
        if (level.random.nextInt(6) == 0 && crucible.aspects.size() > 0) {
            Aspect[] present = crucible.aspects.getAspects();
            int color = present[level.random.nextInt(present.length)].getColor();
            int gridX = 5 + level.random.nextInt(22);
            int gridZ = 5 + level.random.nextInt(22);
            TCFXDispatcher.crucibleBubble(
                    level,
                    pos.getX() + gridX / 32.0D + 0.015625D,
                    pos.getY() + 0.05D + fluidHeight,
                    pos.getZ() + gridZ / 32.0D + 0.015625D,
                    (color >> 16 & 0xFF) / 255.0F,
                    (color >> 8 & 0xFF) / 255.0F,
                    (color & 0xFF) / 255.0F
            );
        }
    }

    public CrucibleUseResult useCatalystOrDissolve(@Nullable ServerPlayer player, ItemStack stack) {
        if (level == null || level.isClientSide || stack == null || stack.isEmpty()) {
            return CrucibleUseResult.IGNORED;
        }
        if (!isBoiling()) {
            return CrucibleUseResult.NOT_BOILING;
        }

        ItemStack singleItem = singleItem(stack);
        SmeltStackResult result = attemptSmeltStack(player, singleItem);
        applySmeltResultEffects(result);
        return result.useResult();
    }

    public CrucibleUseResult absorbItemEntity(ItemEntity entity) {
        if (level == null || level.isClientSide || entity == null || entity.isRemoved() || isSpecialCrucibleItem(entity)) {
            return CrucibleUseResult.IGNORED;
        }
        ItemStack stack = entity.getItem();
        if (stack.isEmpty()) {
            return CrucibleUseResult.IGNORED;
        }
        if (!isBoiling()) {
            return CrucibleUseResult.NOT_BOILING;
        }

        SmeltStackResult result = attemptSmeltStack(resolveThrower(entity), stack.copy());
        applySmeltResultEffects(result);
        int remaining = result.remainingCount();
        if (remaining <= 0) {
            entity.discard();
        } else if (remaining < stack.getCount()) {
            stack.setCount(remaining);
            entity.setItem(stack);
        }
        return result.useResult();
    }

    public static boolean isSpecialCrucibleItem(ItemEntity entity) {
        return entity != null && entity.getPersistentData().getBoolean(SPECIAL_ITEM_MARKER);
    }

    public boolean shouldDamageLivingContact() {
        livingContactDelay++;
        if (livingContactDelay < 10) {
            return false;
        }
        livingContactDelay = 0;
        return isBoiling();
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

    public float getFluidHeight() {
        float base = 0.3F + 0.5F * (waterAmount / (float) WATER_CAPACITY);
        float height = base + aspects.visSize() / (float) LEGACY_ASPECT_CAP * (1.0F - base);
        if (height > 1.0F) {
            return 1.001F;
        }
        return height == 1.0F ? 0.9999F : height;
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
        int totalAspects = aspects.visSize();
        if (waterAmount <= 0 && totalAspects <= 0) {
            return;
        }
        waterAmount = 0;
        polluteSpillRemnants(totalAspects, aspects.getAmount(Aspect.FLUX));
        aspects = new AspectList();
        emitClientEvent(2, 5);
        markChangedAndSync();
    }

    public int comparatorSignal() {
        int amount = aspects.visSize();
        if (amount <= 0) {
            return 0;
        }
        return Math.min(15, (int) Math.floor(amount / (float) LEGACY_ASPECT_CAP * 14.0F) + 1);
    }

    private SmeltStackResult attemptSmeltStack(@Nullable ServerPlayer player, ItemStack stack) {
        int remaining = stack.getCount();
        boolean crafted = false;
        boolean dissolved = false;
        boolean sawNoAspects = false;

        for (int index = 0; index < remaining; index++) {
            CrucibleUseResult result = attemptSmeltSingle(player, stack);
            if (result == CrucibleUseResult.CRAFTED) {
                crafted = true;
                remaining--;
            } else if (result == CrucibleUseResult.DISSOLVED_ASPECTS) {
                dissolved = true;
                remaining--;
            } else if (result == CrucibleUseResult.NO_ASPECTS) {
                sawNoAspects = true;
                break;
            } else {
                break;
            }
        }

        return new SmeltStackResult(remaining, crafted, dissolved, sawNoAspects);
    }

    private CrucibleUseResult attemptSmeltSingle(@Nullable ServerPlayer player, ItemStack stack) {
        ItemStack singleItem = singleItem(stack);
        Optional<RecipeHolder<TCCrucibleRecipe>> matchingRecipe = TCCrucibleRecipeMatcher.findMatchingRecipe(
                level.getRecipeManager(),
                player,
                aspects,
                singleItem
        );
        if (matchingRecipe.isPresent() && waterAmount > 0) {
            craftWithoutPostEffects(matchingRecipe.get().value());
            spillCounter = -250L;
            return CrucibleUseResult.CRAFTED;
        }

        AspectList objectAspects = AspectHelper.getObjectAspects(singleItem);
        if (objectAspects == null || objectAspects.size() == 0) {
            return CrucibleUseResult.NO_ASPECTS;
        }

        aspects.add(objectAspects);
        spillCounter = -150L;
        return CrucibleUseResult.DISSOLVED_ASPECTS;
    }

    private void spillRandom() {
        Aspect[] presentAspects = aspects.getAspects();
        if (presentAspects.length > 0) {
            Aspect aspect = presentAspects[level.random.nextInt(presentAspects.length)];
            aspects.remove(aspect, 1);
            polluteAura(aspect == Aspect.FLUX ? 1.0F : 0.25F, true);
        }
        markChangedAndSync();
    }

    private void polluteSpillRemnants(int totalAspects, int fluxAspects) {
        if (totalAspects > 0) {
            polluteAura(totalAspects * 0.25F, true);
        }
        if (fluxAspects > 0) {
            polluteAura(fluxAspects * 0.75F, false);
        }
    }

    private void craftWithoutPostEffects(TCCrucibleRecipe recipe) {
        aspects = TCCrucibleRecipeMatcher.removeRequiredAspects(recipe, aspects);
        waterAmount = Math.max(0, waterAmount - WATER_PER_CRAFT);
        ejectItem(recipe.result());
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
        entity.getPersistentData().putBoolean(SPECIAL_ITEM_MARKER, true);
        entity.setDeltaMovement(0.0D, 0.075D, 0.0D);
        level.addFreshEntity(entity);
    }

    private void applySmeltResultEffects(SmeltStackResult result) {
        if (!result.mutated()) {
            setChanged();
            return;
        }
        if (result.dissolved()) {
            playBubbleSound();
            emitClientEvent(2, 1);
        }
        if (result.crafted()) {
            emitClientEvent(99, 0);
        }
        markChangedAndSync();
    }

    private void playBubbleSound() {
        if (level != null) {
            level.playSound(null, worldPosition, TCSounds.BUBBLE.get(), SoundSource.BLOCKS, 0.2F, 1.0F + level.random.nextFloat() * 0.4F);
        }
    }

    private void polluteAura(float amount, boolean showEffect) {
        if (level != null && !level.isClientSide) {
            AuraHelper.polluteAura(level, worldPosition, amount, showEffect);
        }
    }

    private static ItemStack singleItem(ItemStack stack) {
        ItemStack singleItem = stack.copy();
        singleItem.setCount(1);
        return singleItem;
    }

    @Nullable
    private static ServerPlayer resolveThrower(ItemEntity entity) {
        Entity owner = entity.getOwner();
        return owner instanceof ServerPlayer player ? player : null;
    }

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }

    private void emitClientEvent(int eventId, int parameter) {
        if (level != null && !level.isClientSide) {
            level.blockEvent(worldPosition, getBlockState().getBlock(), eventId, parameter);
        }
    }

    @Override
    public boolean triggerEvent(int eventId, int parameter) {
        if (level == null || !level.isClientSide) {
            return super.triggerEvent(eventId, parameter);
        }
        if (eventId == 99 || eventId == 1) {
            TCFXDispatcher.drawCrucibleBamf(
                    level,
                    worldPosition.getX() + 0.5D,
                    eventId == 99 ? worldPosition.getY() + 1.25D : worldPosition.getY() + 1.5D,
                    worldPosition.getZ() + 0.5D
            );
            level.playLocalSound(worldPosition, TCSounds.POOF.get(), SoundSource.BLOCKS, 0.4F,
                    1.0F + (float) level.random.nextGaussian() * 0.05F, false);
            if (eventId == 99) {
                level.playLocalSound(worldPosition, TCSounds.SPILL.get(), SoundSource.BLOCKS, 0.2F, 1.0F, false);
            }
            return true;
        }
        if (eventId == 2) {
            level.playLocalSound(worldPosition, TCSounds.SPILL.get(), SoundSource.BLOCKS, 0.2F, 1.0F, false);
            for (int index = 0; index < 10; index++) {
                TCFXDispatcher.crucibleBoil(level, worldPosition, getFluidHeight(), aspects, parameter);
            }
            return true;
        }
        return super.triggerEvent(eventId, parameter);
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

    private record SmeltStackResult(int remainingCount, boolean crafted, boolean dissolved, boolean sawNoAspects) {
        boolean mutated() {
            return crafted || dissolved;
        }

        CrucibleUseResult useResult() {
            if (crafted) {
                return CrucibleUseResult.CRAFTED;
            }
            if (dissolved) {
                return CrucibleUseResult.DISSOLVED_ASPECTS;
            }
            if (sawNoAspects) {
                return CrucibleUseResult.NO_ASPECTS;
            }
            return CrucibleUseResult.IGNORED;
        }
    }
}
