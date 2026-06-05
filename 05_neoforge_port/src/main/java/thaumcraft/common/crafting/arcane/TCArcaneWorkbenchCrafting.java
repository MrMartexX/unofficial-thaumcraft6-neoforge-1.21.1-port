package thaumcraft.common.crafting.arcane;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import thaumcraft.common.items.ItemAspectVariant;
import thaumcraft.common.items.components.TCAspectStackComponent;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCRecipes;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.tiles.crafting.TCArcaneWorkbenchBlockEntity;

public final class TCArcaneWorkbenchCrafting {
    public static final List<String> PRIMAL_ASPECT_ORDER = List.of(
            "aer",
            "ignis",
            "aqua",
            "terra",
            "ordo",
            "perditio"
    );

    private TCArcaneWorkbenchCrafting() {
    }

    public static ResolvedCraft resolve(ServerPlayer player, TCArcaneWorkbenchBlockEntity workbench) {
        if (player == null || workbench == null || workbench.getLevel() == null) {
            return ResolvedCraft.empty();
        }

        Level level = workbench.getLevel();
        CraftingInput input = workbench.craftingInput();
        Optional<RecipeHolder<TCArcaneRecipe>> arcaneRecipe = matchingArcaneRecipe(level.getRecipeManager(), level, input);
        if (arcaneRecipe.isPresent()) {
            TCArcaneRecipe recipe = arcaneRecipe.get().value();
            int vis = recipe.getVis();
            boolean hasVis = workbench.canSpendVis(vis);
            boolean hasCrystals = hasCrystalCosts(workbench, recipe.crystalCosts());
            boolean hasResearch = TCResearchManager.knowsResearchStrict(
                    TCPlayerKnowledgeStore.get(player),
                    recipe.getResearch()
            );

            if (!hasVis || !hasCrystals) {
                return new ResolvedCraft(
                        Kind.ARCANE_BLOCKED,
                        arcaneRecipe.get().id(),
                        ItemStack.EMPTY,
                        vis,
                        recipe.crystalCosts(),
                        hasVis,
                        hasCrystals,
                        hasResearch
                );
            }
            if (hasResearch) {
                return new ResolvedCraft(
                        Kind.ARCANE,
                        arcaneRecipe.get().id(),
                        recipe.assemble(input, player.server.registryAccess()),
                        vis,
                        recipe.crystalCosts(),
                        true,
                        true,
                        true
                );
            }
        }

        return resolveVanilla(player, workbench, input);
    }

    public static boolean craft(ServerPlayer player, TCArcaneWorkbenchBlockEntity workbench, ResolvedCraft expected) {
        if (player == null || workbench == null || expected == null || expected.output().isEmpty()) {
            return false;
        }

        ResolvedCraft actual = resolve(player, workbench);
        if (!actual.matchesExpected(expected)) {
            return false;
        }

        CraftingInput input = workbench.craftingInput();
        NonNullList<ItemStack> remainingItems = remainingItems(player, workbench, actual, input);
        if (actual.kind() == Kind.ARCANE && !workbench.spendVis(actual.vis())) {
            return false;
        }

        consumeMatrix(player, workbench, remainingItems);
        if (actual.kind() == Kind.ARCANE) {
            consumeCrystals(workbench, actual.crystalCosts());
        }
        workbench.setChanged();
        TCResearchManager.markCraftedResearchReferences(player, actual.output());
        return true;
    }

    public static boolean isCrystal(ItemStack stack, String aspectTag) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemAspectVariant variant)) {
            return false;
        }
        if (variant.kind() != ItemAspectVariant.Kind.CRYSTAL_ESSENCE) {
            return false;
        }
        TCAspectStackComponent aspectStack = stack.get(TCDataComponents.ASPECT_STACK.get());
        String actual = aspectStack == null || aspectStack.isEmpty() ? variant.aspectTag() : aspectStack.aspect();
        return aspectTag.equals(actual);
    }

    public static int crystalSlotForAspect(String aspectTag) {
        int index = PRIMAL_ASPECT_ORDER.indexOf(aspectTag);
        return index < 0 ? -1 : TCArcaneWorkbenchBlockEntity.CRYSTAL_SLOT_START + index;
    }

    private static Optional<RecipeHolder<TCArcaneRecipe>> matchingArcaneRecipe(
            RecipeManager recipeManager,
            Level level,
            CraftingInput input
    ) {
        for (RecipeHolder<TCArcaneRecipe> holder : recipeManager.getAllRecipesFor(TCRecipes.ARCANE_TYPE.get())) {
            if (holder.value().matches(input, level)) {
                return Optional.of(holder);
            }
        }
        return Optional.empty();
    }

    private static ResolvedCraft resolveVanilla(ServerPlayer player, TCArcaneWorkbenchBlockEntity workbench, CraftingInput input) {
        Level level = workbench.getLevel();
        if (level == null) {
            return ResolvedCraft.empty();
        }
        Optional<RecipeHolder<CraftingRecipe>> recipe = level.getRecipeManager().getRecipeFor(
                RecipeType.CRAFTING,
                input,
                level
        );
        if (recipe.isEmpty()) {
            return ResolvedCraft.empty();
        }
        ItemStack output = recipe.get().value().assemble(input, player.server.registryAccess());
        return new ResolvedCraft(
                Kind.VANILLA,
                recipe.get().id(),
                output,
                0,
                List.of(),
                true,
                true,
                true
        );
    }

    private static boolean hasCrystalCosts(TCArcaneWorkbenchBlockEntity workbench, List<TCArcaneCrystalCost> costs) {
        for (TCArcaneCrystalCost cost : costs) {
            int slot = crystalSlotForAspect(cost.aspect());
            if (slot < 0 || workbench.getItem(slot).getCount() < cost.amount()) {
                return false;
            }
            if (!isCrystal(workbench.getItem(slot), cost.aspect())) {
                return false;
            }
        }
        return true;
    }

    private static NonNullList<ItemStack> remainingItems(
            ServerPlayer player,
            TCArcaneWorkbenchBlockEntity workbench,
            ResolvedCraft craft,
            CraftingInput input
    ) {
        if (craft.kind() == Kind.VANILLA) {
            Optional<RecipeHolder<CraftingRecipe>> recipe = workbench.getLevel().getRecipeManager().getRecipeFor(
                    RecipeType.CRAFTING,
                    input,
                    workbench.getLevel()
            );
            if (recipe.isPresent()) {
                return recipe.get().value().getRemainingItems(input);
            }
        }
        NonNullList<ItemStack> empty = NonNullList.withSize(TCArcaneWorkbenchBlockEntity.MATRIX_SLOT_COUNT, ItemStack.EMPTY);
        return empty;
    }

    private static void consumeMatrix(ServerPlayer player, TCArcaneWorkbenchBlockEntity workbench, NonNullList<ItemStack> remainingItems) {
        for (int slot = 0; slot < TCArcaneWorkbenchBlockEntity.MATRIX_SLOT_COUNT; slot++) {
            ItemStack current = workbench.getItem(slot);
            if (current.isEmpty()) {
                continue;
            }
            workbench.removeItem(slot, 1);
            ItemStack remainder = slot < remainingItems.size() ? remainingItems.get(slot) : ItemStack.EMPTY;
            if (remainder.isEmpty()) {
                continue;
            }
            ItemStack updated = workbench.getItem(slot);
            if (updated.isEmpty()) {
                workbench.setItem(slot, remainder.copy());
            } else if (!player.getInventory().add(remainder.copy())) {
                player.drop(remainder.copy(), false);
            }
        }
    }

    private static void consumeCrystals(TCArcaneWorkbenchBlockEntity workbench, List<TCArcaneCrystalCost> costs) {
        for (TCArcaneCrystalCost cost : costs) {
            int slot = crystalSlotForAspect(cost.aspect());
            if (slot >= 0) {
                workbench.removeItem(slot, cost.amount());
            }
        }
    }

    public enum Kind {
        EMPTY,
        ARCANE,
        ARCANE_BLOCKED,
        VANILLA
    }

    public record ResolvedCraft(
            Kind kind,
            ResourceLocation recipeId,
            ItemStack output,
            int vis,
            List<TCArcaneCrystalCost> crystalCosts,
            boolean hasVis,
            boolean hasCrystals,
            boolean hasResearch
    ) {
        public ResolvedCraft {
            output = output.copy();
            crystalCosts = List.copyOf(crystalCosts);
        }

        public static ResolvedCraft empty() {
            return new ResolvedCraft(Kind.EMPTY, null, ItemStack.EMPTY, 0, List.of(), true, true, true);
        }

        boolean matchesExpected(ResolvedCraft expected) {
            return kind == expected.kind
                    && recipeId != null
                    && recipeId.equals(expected.recipeId)
                    && ItemStack.isSameItemSameComponents(output, expected.output);
        }
    }
}
