package thaumcraft.common.tiles.crafting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.crafting.TCThaumatoriumBlock;
import thaumcraft.common.blocks.misc.TCNitorBlock;
import thaumcraft.common.crafting.crucible.TCCrucibleAspectCost;
import thaumcraft.common.crafting.crucible.TCCrucibleRecipe;
import thaumcraft.common.crafting.crucible.TCCrucibleRecipeMatcher;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.TCEssentiaStack;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.menu.TCThaumatoriumMenu;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCRecipes;
import thaumcraft.common.research.TCPlayerKnowledge;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.research.TCResearchManager;

public class TCThaumatoriumBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, TCEssentiaTransport {
    public static final int SLOT_CATALYST = 0;
    public static final int SLOT_COUNT = 1;
    public static final int BASE_MAX_RECIPES = 1;
    public static final int MNEMONIC_MATRIX_BONUS = 2;
    public static final int SUCTION_AMOUNT = 128;
    private static final int[] SLOTS = {SLOT_CATALYST};

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private final IItemHandler unsidedItemHandler = new SidedInvWrapper(this, null);
    private final EnumMap<Direction, IItemHandler> sidedItemHandlers = new EnumMap<>(Direction.class);
    private final ArrayList<ResourceLocation> selectedRecipes = new ArrayList<>();
    private final ArrayList<String> selectedPlayers = new ArrayList<>();

    private AspectList essentia = new AspectList();
    private int currentCraft = -1;
    private int maxRecipes = BASE_MAX_RECIPES;
    @Nullable
    private Aspect currentSuction;
    private int venting;
    private int counter;
    private boolean heated;

    public TCThaumatoriumBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntities.THAUMATORIUM.get(), pos, state);
        for (Direction direction : Direction.values()) {
            sidedItemHandlers.put(direction, new SidedInvWrapper(this, direction));
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TCThaumatoriumBlockEntity thaumatorium) {
        if (level.isClientSide) {
            return;
        }

        thaumatorium.counter++;
        if (thaumatorium.counter % 40 == 0) {
            thaumatorium.heated = thaumatorium.checkHeat();
            thaumatorium.updateRecipeCapacity();
        }
        if (thaumatorium.counter % 5 != 0) {
            return;
        }
        thaumatorium.tickCrafting();
    }

    public IItemHandler itemHandler(@Nullable Direction side) {
        return side == null ? unsidedItemHandler : sidedItemHandlers.get(side);
    }

    public AspectList storedEssentia() {
        return essentia.copy();
    }

    public void setStoredEssentiaForValidation(AspectList list) {
        essentia = list == null ? new AspectList() : list.copy();
        markChangedAndSync();
    }

    public void setCatalystForValidation(ItemStack stack) {
        setItem(SLOT_CATALYST, stack == null ? ItemStack.EMPTY : stack);
    }

    public void setHeatedForValidation(boolean heated) {
        this.heated = heated;
        markChangedAndSync();
    }

    public void setCurrentCraftForValidation(int currentCraft) {
        this.currentCraft = currentCraft;
        markChangedAndSync();
    }

    public void selectRecipeForValidation(ResourceLocation recipeId, String playerName) {
        selectedRecipes.clear();
        selectedPlayers.clear();
        if (recipeId != null) {
            selectedRecipes.add(recipeId);
            selectedPlayers.add(playerName == null ? "" : playerName);
            currentCraft = 0;
        } else {
            currentCraft = -1;
        }
        markChangedAndSync();
    }

    public int selectedRecipeCount() {
        return selectedRecipes.size();
    }

    public int maxRecipes() {
        return maxRecipes;
    }

    public int currentCraft() {
        return currentCraft;
    }

    @Nullable
    public Aspect currentSuctionAspect() {
        return currentSuction;
    }

    public boolean heated() {
        return heated;
    }

    public int storedAmount(Aspect aspect) {
        return essentia.getAmount(aspect);
    }

    public List<ResourceLocation> selectedRecipes() {
        return List.copyOf(selectedRecipes);
    }

    public ItemStack displayRecipeOutput() {
        if (level == null || selectedRecipes.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int index = currentCraft >= 0 && currentCraft < selectedRecipes.size()
                ? currentCraft
                : (int) ((level.getGameTime() / 20L) % selectedRecipes.size());
        return recipeById(selectedRecipes.get(index))
                .map(holder -> holder.value().result())
                .orElse(ItemStack.EMPTY);
    }

    public List<RecipeHolder<TCCrucibleRecipe>> availableRecipes(ServerPlayer player) {
        if (level == null || player == null || getItem(SLOT_CATALYST).isEmpty()) {
            return List.of();
        }

        ItemStack catalyst = singleCatalyst();
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        ArrayList<RecipeHolder<TCCrucibleRecipe>> recipes = new ArrayList<>();
        for (RecipeHolder<TCCrucibleRecipe> holder : level.getRecipeManager().getAllRecipesFor(TCRecipes.CRUCIBLE_TYPE.get())) {
            TCCrucibleRecipe recipe = holder.value();
            if (!recipe.catalyst().test(catalyst)) {
                continue;
            }
            if (TCResearchManager.knowsResearchStrict(knowledge, recipe.getResearch()) || selectedRecipes.contains(holder.id())) {
                recipes.add(holder);
            }
        }
        recipes.sort(Comparator.comparing(holder -> holder.value().result().getHoverName().getString()));
        return List.copyOf(recipes);
    }

    public boolean toggleRecipe(ServerPlayer player, ResourceLocation recipeId) {
        if (player == null || recipeId == null) {
            return false;
        }
        int existing = selectedRecipes.indexOf(recipeId);
        if (existing >= 0) {
            selectedRecipes.remove(existing);
            if (existing < selectedPlayers.size()) {
                selectedPlayers.remove(existing);
            }
            currentCraft = -1;
            markChangedAndSync();
            return true;
        }
        if (selectedRecipes.size() >= maxRecipes) {
            return false;
        }
        boolean available = availableRecipes(player).stream().anyMatch(holder -> holder.id().equals(recipeId));
        if (!available) {
            return false;
        }
        selectedRecipes.add(recipeId);
        selectedPlayers.add(player.getScoreboardName());
        markChangedAndSync();
        return true;
    }

    private void tickCrafting() {
        if (level == null || !heated || isPowered() || selectedRecipes.isEmpty()) {
            currentSuction = null;
            return;
        }
        if (getItem(SLOT_CATALYST).isEmpty()) {
            currentSuction = null;
            return;
        }
        Optional<RecipeHolder<TCCrucibleRecipe>> selected = resolveCurrentRecipe();
        if (selected.isEmpty()) {
            currentSuction = null;
            return;
        }

        TCCrucibleRecipe recipe = selected.get().value();
        Aspect missing = firstMissingAspect(recipe);
        currentSuction = missing;
        if (missing == null) {
            completeRecipe(selected.get());
        } else {
            fillFromNeighbors();
        }
    }

    private Optional<RecipeHolder<TCCrucibleRecipe>> resolveCurrentRecipe() {
        if (level == null) {
            return Optional.empty();
        }
        if (currentCraft >= 0 && currentCraft < selectedRecipes.size()) {
            Optional<RecipeHolder<TCCrucibleRecipe>> recipe = recipeById(selectedRecipes.get(currentCraft));
            if (recipe.isPresent() && recipe.get().value().catalyst().test(singleCatalyst())) {
                return recipe;
            }
        }

        currentCraft = -1;
        for (int index = 0; index < selectedRecipes.size(); index++) {
            Optional<RecipeHolder<TCCrucibleRecipe>> recipe = recipeById(selectedRecipes.get(index));
            if (recipe.isPresent() && recipe.get().value().catalyst().test(singleCatalyst())) {
                currentCraft = index;
                return recipe;
            }
        }
        return Optional.empty();
    }

    private Optional<RecipeHolder<TCCrucibleRecipe>> recipeById(ResourceLocation id) {
        if (level == null || id == null) {
            return Optional.empty();
        }
        for (RecipeHolder<TCCrucibleRecipe> holder : level.getRecipeManager().getAllRecipesFor(TCRecipes.CRUCIBLE_TYPE.get())) {
            if (holder.id().equals(id)) {
                return Optional.of(holder);
            }
        }
        return Optional.empty();
    }

    @Nullable
    private Aspect firstMissingAspect(TCCrucibleRecipe recipe) {
        AspectList required = requiredAspects(recipe);
        for (Aspect aspect : required.getAspectsSortedByName()) {
            if (essentia.getAmount(aspect) < required.getAmount(aspect)) {
                return aspect;
            }
        }
        return null;
    }

    private void completeRecipe(RecipeHolder<TCCrucibleRecipe> holder) {
        if (level == null || currentCraft < 0 || currentCraft >= selectedRecipes.size()) {
            return;
        }
        TCCrucibleRecipe recipe = holder.value();
        if (!TCCrucibleRecipeMatcher.matches(recipe, essentia, singleCatalyst())) {
            return;
        }
        ItemStack removed = removeItem(SLOT_CATALYST, 1);
        if (removed.isEmpty()) {
            return;
        }

        essentia = new AspectList();
        currentSuction = null;
        ItemStack result = recipe.result();
        Direction facing = facing();
        double x = worldPosition.getX() + 0.5D + facing.getStepX() * 0.7D;
        double y = worldPosition.getY() + 0.5D;
        double z = worldPosition.getZ() + 0.5D + facing.getStepZ() * 0.7D;
        ItemEntity entity = new ItemEntity(level, x, y, z, result);
        entity.setDeltaMovement(facing.getStepX() * 0.08D, 0.1D, facing.getStepZ() * 0.08D);
        level.addFreshEntity(entity);
        level.playSound(null, worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.2F, 1.0F);
        currentCraft = -1;
        markChangedAndSync();
    }

    private void fillFromNeighbors() {
        if (level == null || currentSuction == null) {
            return;
        }
        Direction facing = facing();
        String aspect = currentSuction.getTag();
        TCEssentiaSuction ownSuction = new TCEssentiaSuction(aspect, SUCTION_AMOUNT);
        for (int y = 0; y <= 1; y++) {
            BlockPos sourceOrigin = worldPosition.above(y);
            for (Direction direction : Direction.values()) {
                if (direction == facing || direction == Direction.DOWN || y == 0 && direction == Direction.UP) {
                    continue;
                }
                BlockPos sourcePos = sourceOrigin.relative(direction);
                TCEssentiaTransport transport = level.getCapability(
                        TCEssentiaCapabilities.BLOCK,
                        sourcePos,
                        direction.getOpposite()
                );
                if (transport == null || !transport.canOutputTo(direction.getOpposite())) {
                    continue;
                }
                TCEssentiaStack stack = transport.getEssentia(direction.getOpposite());
                if (stack.isEmpty() || !aspect.equals(stack.aspect())) {
                    continue;
                }
                if (transport.getSuction(direction.getOpposite()).amount() >= ownSuction.amount()
                        || ownSuction.amount() < transport.getMinimumSuction()) {
                    continue;
                }
                int taken = transport.takeEssentia(aspect, 1, direction.getOpposite(), false);
                if (taken > 0) {
                    addToContainer(Aspect.getAspect(aspect), taken);
                    return;
                }
            }
        }
    }

    private int addToContainer(Aspect aspect, int amount) {
        if (aspect == null || amount <= 0) {
            return 0;
        }
        Optional<RecipeHolder<TCCrucibleRecipe>> recipe = resolveCurrentRecipe();
        if (recipe.isEmpty()) {
            return 0;
        }
        int required = requiredAspects(recipe.get().value()).getAmount(aspect);
        int missing = required - essentia.getAmount(aspect);
        if (missing <= 0) {
            return 0;
        }
        int accepted = Math.min(amount, missing);
        essentia.add(aspect, accepted);
        markChangedAndSync();
        return accepted;
    }

    private AspectList requiredAspects(TCCrucibleRecipe recipe) {
        AspectList list = new AspectList();
        if (recipe != null) {
            for (TCCrucibleAspectCost cost : recipe.aspectCosts()) {
                list.add(cost.resolvedAspect(), cost.amount());
            }
        }
        return list;
    }

    private ItemStack singleCatalyst() {
        ItemStack catalyst = getItem(SLOT_CATALYST).copy();
        if (!catalyst.isEmpty()) {
            catalyst.setCount(1);
        }
        return catalyst;
    }

    public boolean checkHeat() {
        return level != null && isThaumatoriumHeatSource(level.getBlockState(worldPosition.below(2)));
    }

    public static boolean isThaumatoriumHeatSource(BlockState state) {
        return state.is(Blocks.LAVA)
                || state.getBlock() instanceof FireBlock
                || state.getBlock() instanceof MagmaBlock
                || state.getBlock() instanceof TCNitorBlock;
    }

    private boolean isPowered() {
        return level != null
                && (level.hasNeighborSignal(worldPosition)
                || level.hasNeighborSignal(worldPosition.below())
                || level.hasNeighborSignal(worldPosition.above()));
    }

    private void updateRecipeCapacity() {
        int next = BASE_MAX_RECIPES;
        Direction front = facing();
        for (int y = 0; y <= 1; y++) {
            BlockPos origin = worldPosition.above(y);
            for (Direction direction : Direction.values()) {
                if (direction == Direction.DOWN || direction == front) {
                    continue;
                }
                BlockState state = level.getBlockState(origin.relative(direction));
                if (state.is(TCBlocks.GOLEM_BUILDER.get())) {
                    next += MNEMONIC_MATRIX_BONUS;
                }
            }
        }
        maxRecipes = Math.max(BASE_MAX_RECIPES, next);
        while (selectedRecipes.size() > maxRecipes) {
            selectedRecipes.remove(selectedRecipes.size() - 1);
            if (!selectedPlayers.isEmpty()) {
                selectedPlayers.remove(selectedPlayers.size() - 1);
            }
        }
        if (currentCraft >= selectedRecipes.size()) {
            currentCraft = -1;
        }
    }

    private Direction facing() {
        BlockState state = getBlockState();
        return state.hasProperty(TCThaumatoriumBlock.FACING) ? state.getValue(TCThaumatoriumBlock.FACING) : Direction.NORTH;
    }

    public int comparatorSignal() {
        ItemStack stack = getItem(SLOT_CATALYST);
        if (stack.isEmpty()) {
            return 0;
        }
        return Math.max(1, Math.min(15, (int) Math.floor(stack.getCount() / (float) stack.getMaxStackSize() * 15.0F)));
    }

    @Override
    public boolean isConnectable(Direction face) {
        return face != null && face != facing();
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return isConnectable(face);
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return false;
    }

    @Override
    public TCEssentiaSuction getSuction(Direction face) {
        return currentSuction == null || !canInputFrom(face)
                ? TCEssentiaSuction.NONE
                : new TCEssentiaSuction(currentSuction.getTag(), SUCTION_AMOUNT);
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public TCEssentiaStack getEssentia(Direction face) {
        return TCEssentiaStack.EMPTY;
    }

    @Override
    public int addEssentia(String aspect, int amount, Direction face, boolean simulate) {
        if (!canInputFrom(face) || aspect == null || amount <= 0) {
            return 0;
        }
        Aspect resolved = Aspect.getAspect(aspect);
        if (resolved == null || currentSuction == null || !currentSuction.equals(resolved)) {
            return 0;
        }
        int accepted = acceptedAmount(resolved, amount);
        if (!simulate && accepted > 0) {
            essentia.add(resolved, accepted);
            markChangedAndSync();
        }
        return accepted;
    }

    private int acceptedAmount(Aspect aspect, int amount) {
        Optional<RecipeHolder<TCCrucibleRecipe>> recipe = resolveCurrentRecipe();
        if (recipe.isEmpty()) {
            return 0;
        }
        int required = requiredAspects(recipe.get().value()).getAmount(aspect);
        return Math.max(0, Math.min(amount, required - essentia.getAmount(aspect)));
    }

    @Override
    public int takeEssentia(String aspect, int amount, Direction face, boolean simulate) {
        return 0;
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return getItem(SLOT_CATALYST).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == SLOT_CATALYST ? items.get(SLOT_CATALYST) : ItemStack.EMPTY;
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
        if (slot != SLOT_CATALYST) {
            return;
        }
        ItemStack stored = stack.copy();
        stored.limitSize(getMaxStackSize(stored));
        items.set(SLOT_CATALYST, stored);
        if (stored.isEmpty()) {
            currentSuction = null;
        }
        currentCraft = -1;
        markChangedAndSync();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == SLOT_CATALYST && stack != null && !stack.isEmpty();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == SLOT_CATALYST;
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
        return Component.translatable("container.thaumcraft.thaumatorium");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new TCThaumatoriumMenu(containerId, inventory, this);
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(worldPosition);
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
        essentia.writeToNBT(tag);
        tag.putInt("CurrentCraft", currentCraft);
        tag.putInt("MaxRecipes", maxRecipes);
        tag.putInt("Venting", venting);
        tag.putInt("Counter", counter);
        tag.putBoolean("Heated", heated);
        tag.putString("CurrentSuction", currentSuction == null ? "" : currentSuction.getTag());
        ListTag recipes = new ListTag();
        for (ResourceLocation id : selectedRecipes) {
            recipes.add(StringTag.valueOf(id.toString()));
        }
        tag.put("SelectedRecipes", recipes);
        ListTag players = new ListTag();
        for (String player : selectedPlayers) {
            players.add(StringTag.valueOf(player));
        }
        tag.put("SelectedPlayers", players);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int slot = 0; slot < items.size(); slot++) {
            items.set(slot, ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(tag, items, registries);
        essentia = new AspectList();
        essentia.readFromNBT(tag);
        currentCraft = tag.contains("CurrentCraft") ? tag.getInt("CurrentCraft") : -1;
        maxRecipes = Math.max(BASE_MAX_RECIPES, tag.contains("MaxRecipes") ? tag.getInt("MaxRecipes") : BASE_MAX_RECIPES);
        venting = Math.max(0, tag.getInt("Venting"));
        counter = Math.max(0, tag.getInt("Counter"));
        heated = tag.getBoolean("Heated");
        currentSuction = Aspect.getAspect(tag.getString("CurrentSuction"));
        selectedRecipes.clear();
        ListTag recipes = tag.getList("SelectedRecipes", Tag.TAG_STRING);
        for (int index = 0; index < recipes.size(); index++) {
            ResourceLocation id = ResourceLocation.tryParse(recipes.getString(index));
            if (id != null) {
                selectedRecipes.add(id);
            }
        }
        selectedPlayers.clear();
        ListTag players = tag.getList("SelectedPlayers", Tag.TAG_STRING);
        for (int index = 0; index < players.size(); index++) {
            selectedPlayers.add(players.getString(index));
        }
        while (selectedPlayers.size() < selectedRecipes.size()) {
            selectedPlayers.add("");
        }
        if (currentCraft >= selectedRecipes.size()) {
            currentCraft = -1;
        }
    }

    private void markChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }
}
