package thaumcraft.common.menu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.crafting.crucible.TCCrucibleRecipe;
import thaumcraft.common.registry.TCMenus;
import thaumcraft.common.registry.TCRecipes;
import thaumcraft.common.tiles.crafting.TCThaumatoriumBlockEntity;
import thaumcraft.common.research.TCKnowledgeClientCache;
import thaumcraft.common.registry.TCBlocks;

public final class TCThaumatoriumMenu extends AbstractContainerMenu {
    public static final int SLOT_CATALYST = 0;
    public static final int PLAYER_INVENTORY_START = 1;
    public static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    public static final int PLAYER_HOTBAR_START = PLAYER_INVENTORY_END;
    public static final int PLAYER_HOTBAR_END = PLAYER_HOTBAR_START + 9;

    private final Container thaumatorium;
    private final TCThaumatoriumBlockEntity blockEntity;
    private final BlockPos blockPos;
    private int syncedMaxRecipes = TCThaumatoriumBlockEntity.BASE_MAX_RECIPES;
    private int syncedCurrentCraft = -1;
    private int syncedHeated;

    public TCThaumatoriumMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, clientSource(inventory, extraData));
    }

    public TCThaumatoriumMenu(int containerId, Inventory inventory, TCThaumatoriumBlockEntity thaumatorium) {
        this(containerId, inventory, thaumatorium, thaumatorium, thaumatorium.getBlockPos());
    }

    private TCThaumatoriumMenu(int containerId, Inventory inventory, MenuSource source) {
        this(containerId, inventory, source.container(), source.blockEntity(), source.pos());
    }

    private TCThaumatoriumMenu(
            int containerId,
            Inventory inventory,
            Container thaumatorium,
            TCThaumatoriumBlockEntity blockEntity,
            BlockPos blockPos
    ) {
        super(TCMenus.THAUMATORIUM.get(), containerId);
        checkContainerSize(thaumatorium, TCThaumatoriumBlockEntity.SLOT_COUNT);
        this.thaumatorium = thaumatorium;
        this.blockEntity = blockEntity;
        this.blockPos = blockPos;

        addSlot(new Slot(thaumatorium, TCThaumatoriumBlockEntity.SLOT_CATALYST, 55, 24));
        bindPlayerInventory(inventory);
        addDataSlot(data(() -> blockEntity == null ? syncedMaxRecipes : blockEntity.maxRecipes(),
                value -> syncedMaxRecipes = Math.max(TCThaumatoriumBlockEntity.BASE_MAX_RECIPES, value)));
        addDataSlot(data(() -> blockEntity == null ? syncedCurrentCraft : blockEntity.currentCraft(),
                value -> syncedCurrentCraft = value));
        addDataSlot(data(() -> blockEntity != null && blockEntity.heated() ? 1 : syncedHeated,
                value -> syncedHeated = value == 0 ? 0 : 1));
    }

    private static MenuSource clientSource(Inventory inventory, RegistryFriendlyByteBuf extraData) {
        if (extraData == null) {
            return new MenuSource(new SimpleContainer(TCThaumatoriumBlockEntity.SLOT_COUNT), null, BlockPos.ZERO);
        }
        BlockPos pos = extraData.readBlockPos();
        Level level = inventory.player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TCThaumatoriumBlockEntity thaumatorium) {
            return new MenuSource(thaumatorium, thaumatorium, pos);
        }
        return new MenuSource(new SimpleContainer(TCThaumatoriumBlockEntity.SLOT_COUNT), null, pos);
    }

    private void bindPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 135 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 193));
        }
    }

    public BlockPos blockPos() {
        return blockPos;
    }

    public TCThaumatoriumBlockEntity blockEntity() {
        return blockEntity;
    }

    public int maxRecipes() {
        return blockEntity == null ? syncedMaxRecipes : blockEntity.maxRecipes();
    }

    public int currentCraft() {
        return blockEntity == null ? syncedCurrentCraft : blockEntity.currentCraft();
    }

    public boolean heated() {
        return blockEntity == null ? syncedHeated != 0 : blockEntity.heated();
    }

    @Nullable
    public Aspect currentSuctionAspect() {
        return blockEntity == null ? null : blockEntity.currentSuctionAspect();
    }

    public AspectList storedEssentia() {
        return blockEntity == null ? new AspectList() : blockEntity.storedEssentia();
    }

    public List<ResourceLocation> selectedRecipes() {
        return blockEntity == null ? List.of() : blockEntity.selectedRecipes();
    }

    public boolean isSelected(ResourceLocation recipeId) {
        return selectedRecipes().contains(recipeId);
    }

    public List<RecipeHolder<TCCrucibleRecipe>> availableRecipesForClient() {
        if (blockEntity == null || blockEntity.getLevel() == null || getSlot(SLOT_CATALYST).getItem().isEmpty()) {
            return List.of();
        }
        ItemStack catalyst = getSlot(SLOT_CATALYST).getItem().copy();
        catalyst.setCount(1);
        ArrayList<RecipeHolder<TCCrucibleRecipe>> recipes = new ArrayList<>();
        for (RecipeHolder<TCCrucibleRecipe> holder : blockEntity.getLevel().getRecipeManager().getAllRecipesFor(TCRecipes.CRUCIBLE_TYPE.get())) {
            TCCrucibleRecipe recipe = holder.value();
            if (!recipe.catalyst().test(catalyst)) {
                continue;
            }
            if (TCKnowledgeClientCache.hasResearch(recipe.getResearch()) || isSelected(holder.id())) {
                recipes.add(holder);
            }
        }
        recipes.sort(Comparator.comparing(holder -> holder.value().result().getHoverName().getString()));
        return List.copyOf(recipes);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index == SLOT_CATALYST) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, SLOT_CATALYST, SLOT_CATALYST + 1, false)) {
            if (index < PLAYER_INVENTORY_END) {
                if (!moveItemStackTo(stack, PLAYER_HOTBAR_START, PLAYER_HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null || blockEntity.getLevel() == null) {
            return player.level().isClientSide && player.distanceToSqr(
                    blockPos.getX() + 0.5D,
                    blockPos.getY() + 0.5D,
                    blockPos.getZ() + 0.5D
            ) <= 64.0D;
        }
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player,
                TCBlocks.THAUMATORIUM.get()
        );
    }

    @Override
    public void broadcastChanges() {
        if (blockEntity != null) {
            syncedMaxRecipes = blockEntity.maxRecipes();
            syncedCurrentCraft = blockEntity.currentCraft();
            syncedHeated = blockEntity.heated() ? 1 : 0;
        }
        super.broadcastChanges();
    }

    private static DataSlot data(IntGetter getter, IntSetter setter) {
        return new DataSlot() {
            @Override
            public int get() {
                return getter.get();
            }

            @Override
            public void set(int value) {
                setter.set(value);
            }
        };
    }

    private interface IntGetter {
        int get();
    }

    private interface IntSetter {
        void set(int value);
    }

    private record MenuSource(Container container, @Nullable TCThaumatoriumBlockEntity blockEntity, BlockPos pos) {
    }
}
