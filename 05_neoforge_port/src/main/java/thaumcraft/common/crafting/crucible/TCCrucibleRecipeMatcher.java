package thaumcraft.common.crafting.crucible;

import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.registry.TCRecipes;
import thaumcraft.common.research.TCPlayerKnowledge;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.research.TCResearchManager;

public final class TCCrucibleRecipeMatcher {
    private TCCrucibleRecipeMatcher() {
    }

    public static Optional<RecipeHolder<TCCrucibleRecipe>> findMatchingRecipe(
            RecipeManager recipeManager,
            ServerPlayer player,
            AspectList aspects,
            ItemStack catalyst
    ) {
        if (recipeManager == null || player == null || aspects == null || catalyst == null || catalyst.isEmpty()) {
            return Optional.empty();
        }

        ItemStack singleCatalyst = catalyst.copy();
        singleCatalyst.setCount(1);
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        RecipeHolder<TCCrucibleRecipe> best = null;
        int highestCost = 0;

        for (RecipeHolder<TCCrucibleRecipe> holder : recipeManager.getAllRecipesFor(TCRecipes.CRUCIBLE_TYPE.get())) {
            TCCrucibleRecipe recipe = holder.value();
            if (!TCResearchManager.knowsResearchStrict(knowledge, recipe.getResearch())) {
                continue;
            }
            if (!matches(recipe, aspects, singleCatalyst)) {
                continue;
            }

            int cost = recipeAspectCost(recipe);
            if (cost > highestCost) {
                highestCost = cost;
                best = holder;
            }
        }

        return Optional.ofNullable(best);
    }

    public static boolean matches(TCCrucibleRecipe recipe, AspectList aspects, ItemStack catalyst) {
        if (recipe == null || aspects == null || catalyst == null || catalyst.isEmpty()) {
            return false;
        }
        if (!recipe.catalyst().test(catalyst)) {
            return false;
        }
        return hasRequiredAspects(recipe, aspects);
    }

    public static boolean hasRequiredAspects(TCCrucibleRecipe recipe, AspectList aspects) {
        if (recipe == null || aspects == null) {
            return false;
        }
        for (TCCrucibleAspectCost cost : recipe.aspectCosts()) {
            if (aspects.getAmount(cost.resolvedAspect()) < cost.amount()) {
                return false;
            }
        }
        return true;
    }

    public static AspectList removeRequiredAspects(TCCrucibleRecipe recipe, AspectList aspects) {
        AspectList remaining = aspects.copy();
        for (TCCrucibleAspectCost cost : recipe.aspectCosts()) {
            remaining.remove(cost.resolvedAspect(), cost.amount());
        }
        return remaining;
    }

    public static int recipeAspectCost(TCCrucibleRecipe recipe) {
        int total = 0;
        if (recipe != null) {
            for (TCCrucibleAspectCost cost : recipe.aspectCosts()) {
                total += cost.amount();
            }
        }
        return total;
    }
}
