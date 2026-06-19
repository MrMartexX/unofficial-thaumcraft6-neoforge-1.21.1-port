package thaumcraft.common.crafting.infusion;

import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.crafting.crucible.TCCrucibleAspectCost;
import thaumcraft.common.registry.TCRecipes;
import thaumcraft.common.research.TCPlayerKnowledge;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.research.TCResearchManager;

public final class TCInfusionRecipeMatcher {
    private TCInfusionRecipeMatcher() {
    }

    public static Optional<RecipeHolder<TCInfusionRecipe>> findMatchingRecipe(
            RecipeManager recipeManager,
            ServerPlayer player,
            AspectList aspects,
            ItemStack catalyst,
            List<ItemStack> components
    ) {
        if (recipeManager == null || player == null || aspects == null || catalyst == null || catalyst.isEmpty()) {
            return Optional.empty();
        }

        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        RecipeHolder<TCInfusionRecipe> best = null;
        int highestScore = -1;

        for (RecipeHolder<TCInfusionRecipe> holder : recipeManager.getAllRecipesFor(TCRecipes.INFUSION_TYPE.get())) {
            TCInfusionRecipe recipe = holder.value();
            if (!TCResearchManager.knowsResearchStrict(knowledge, recipe.getResearch())) {
                continue;
            }
            if (!matches(recipe, aspects, catalyst, components)) {
                continue;
            }

            int score = recipeScore(recipe);
            if (score > highestScore) {
                highestScore = score;
                best = holder;
            }
        }

        return Optional.ofNullable(best);
    }

    public static boolean matches(TCInfusionRecipe recipe, AspectList aspects, ItemStack catalyst, List<ItemStack> components) {
        if (recipe == null || aspects == null || catalyst == null || catalyst.isEmpty()) {
            return false;
        }
        ItemStack singleCatalyst = catalyst.copy();
        singleCatalyst.setCount(1);
        if (!recipe.catalyst().test(singleCatalyst)) {
            return false;
        }
        return hasRequiredAspects(recipe, aspects) && hasRequiredComponents(recipe, components);
    }

    public static boolean hasRequiredAspects(TCInfusionRecipe recipe, AspectList aspects) {
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

    public static AspectList removeRequiredAspects(TCInfusionRecipe recipe, AspectList aspects) {
        AspectList remaining = aspects == null ? new AspectList() : aspects.copy();
        if (recipe == null) {
            return remaining;
        }
        for (TCCrucibleAspectCost cost : recipe.aspectCosts()) {
            remaining.remove(cost.resolvedAspect(), cost.amount());
        }
        return remaining;
    }

    public static boolean hasRequiredComponents(TCInfusionRecipe recipe, List<ItemStack> components) {
        if (recipe == null) {
            return false;
        }
        List<Ingredient> required = recipe.components();
        if (required.isEmpty()) {
            return true;
        }
        if (components == null || components.size() != required.size()) {
            return false;
        }

        List<ItemStack> singleComponents = components.stream()
                .map(stack -> stack == null ? ItemStack.EMPTY : stack.copyWithCount(1))
                .toList();
        for (ItemStack stack : singleComponents) {
            if (stack.isEmpty()) {
                return false;
            }
        }
        return RecipeMatcher.findMatches(singleComponents, required) != null;
    }

    public static int recipeAspectCost(TCInfusionRecipe recipe) {
        int total = 0;
        if (recipe != null) {
            for (TCCrucibleAspectCost cost : recipe.aspectCosts()) {
                total += cost.amount();
            }
        }
        return total;
    }

    public static int recipeScore(TCInfusionRecipe recipe) {
        if (recipe == null) {
            return -1;
        }
        return recipeAspectCost(recipe) * 100 + recipe.components().size();
    }
}
