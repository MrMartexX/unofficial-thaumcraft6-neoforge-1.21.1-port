package thaumcraft.common.crafting.infusion;

import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import thaumcraft.api.aspects.AspectList;

/**
 * Server-owned snapshot of the legacy infusion altar inputs before any item or essentia mutation.
 */
public final class TCInfusionAssembly {
    private final ItemStack catalyst;
    private final List<ItemStack> components;
    private final AspectList aspects;

    private TCInfusionAssembly(ItemStack catalyst, List<ItemStack> components, AspectList aspects) {
        this.catalyst = catalyst == null ? ItemStack.EMPTY : catalyst.copy();
        this.components = components == null
                ? List.of()
                : components.stream()
                        .map(stack -> stack == null ? ItemStack.EMPTY : stack.copy())
                        .toList();
        this.aspects = aspects == null ? new AspectList() : aspects.copy();
    }

    public static TCInfusionAssembly of(ItemStack catalyst, List<ItemStack> components, AspectList aspects) {
        return new TCInfusionAssembly(catalyst, components, aspects);
    }

    public ItemStack catalyst() {
        return catalyst.copy();
    }

    public List<ItemStack> components() {
        return components.stream().map(ItemStack::copy).toList();
    }

    public AspectList aspects() {
        return aspects.copy();
    }

    public Optional<RecipeHolder<TCInfusionRecipe>> findMatchingRecipe(RecipeManager recipeManager, ServerPlayer player) {
        return TCInfusionRecipeMatcher.findMatchingRecipe(recipeManager, player, aspects.copy(), catalyst.copy(), components());
    }

    public TCInfusionValidationResult validateBest(RecipeManager recipeManager, ServerPlayer player) {
        if (recipeManager == null) {
            return TCInfusionValidationResult.failed("missing_recipe_manager");
        }
        Optional<RecipeHolder<TCInfusionRecipe>> match = findMatchingRecipe(recipeManager, player);
        if (match.isEmpty()) {
            return TCInfusionValidationResult.failed("no_matching_researched_recipe");
        }
        return validateAgainst(match.get());
    }

    public TCInfusionValidationResult validateAgainst(RecipeHolder<TCInfusionRecipe> holder) {
        if (holder == null) {
            return TCInfusionValidationResult.failed("missing_recipe");
        }
        TCInfusionValidationResult result = validateAgainst(holder.value());
        return result.withRecipeId(holder.id().toString());
    }

    public TCInfusionValidationResult validateAgainst(TCInfusionRecipe recipe) {
        if (recipe == null) {
            return TCInfusionValidationResult.failed("missing_recipe");
        }
        if (catalyst.isEmpty()) {
            return TCInfusionValidationResult.failed("missing_catalyst");
        }
        ItemStack singleCatalyst = catalyst.copyWithCount(1);
        if (!recipe.catalyst().test(singleCatalyst)) {
            return TCInfusionValidationResult.failed("catalyst_mismatch");
        }
        if (!TCInfusionRecipeMatcher.hasRequiredAspects(recipe, aspects)) {
            return TCInfusionValidationResult.failed("missing_aspects");
        }
        if (!TCInfusionRecipeMatcher.hasRequiredComponents(recipe, components)) {
            return TCInfusionValidationResult.failed("component_mismatch");
        }
        return TCInfusionValidationResult.valid(
                TCInfusionRecipeMatcher.removeRequiredAspects(recipe, aspects),
                recipe.components().size(),
                components.size()
        );
    }
}
