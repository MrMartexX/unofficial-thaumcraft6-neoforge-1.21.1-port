package thaumcraft.common.aspects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public final class TCGeneratedAspectRecipeGenerator {
    private static volatile RecipeManager lastRecipeManager;

    public static void captureReloadContext(AddReloadListenerEvent event) {
        lastRecipeManager = event.getServerResources().getRecipeManager();
    }

    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (event.getUpdateCause() != TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD || !event.shouldUpdateStaticData()) {
            return;
        }

        RecipeManager recipeManager = lastRecipeManager;
        if (recipeManager == null) {
            TCGeneratedAspectCache.clear();
            return;
        }

        int count = rebuildCraftingRecipeCache(recipeManager, event.getRegistryAccess());
        Thaumcraft.LOGGER.info("Thaumcraft generated aspect cache rebuilt from crafting recipes: {} generated object assignments.", count);
    }

    static int rebuildCraftingRecipeCache(RecipeManager recipeManager, HolderLookup.Provider registries) {
        List<RecipeHolder<CraftingRecipe>> craftingRecipes = craftingRecipes(recipeManager);
        LinkedHashMap<TCAspectStackKey, AspectList> generated = new LinkedHashMap<>();

        for (RecipeHolder<CraftingRecipe> holder : craftingRecipes) {
            ItemStack result = holder.value().getResultItem(registries);
            if (!result.isEmpty() && canGenerateCraftingOutput(result)) {
                resolveGeneratedAspects(result, craftingRecipes, registries, generated, new LinkedHashSet<>());
            }
        }

        for (ResourceLocation itemId : TCAspectAssignments.complexDirectObjectTags().keySet()) {
            BuiltInRegistries.ITEM.getOptional(itemId).ifPresent(item -> resolveGeneratedAspects(new ItemStack(item), craftingRecipes, registries, generated, new LinkedHashSet<>()));
        }

        TCGeneratedAspectCache.replaceGeneratedObjectTags(generated);
        return generated.size();
    }

    private static AspectList resolveGeneratedAspects(
            ItemStack stack,
            List<RecipeHolder<CraftingRecipe>> craftingRecipes,
            HolderLookup.Provider registries,
            Map<TCAspectStackKey, AspectList> generated,
            Set<TCAspectStackKey> history) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        if (TCAspectStackRules.isLegacyNoAspectStack(stack)) {
            return null;
        }

        AspectList explicit = TCAspectAssignments.getExplicitObjectAspects(stack);
        if (explicit != null) {
            return TCAspectStackRules.applyStackBonuses(stack, explicit);
        }

        TCAspectStackKey key = TCAspectStackKey.from(stack);
        AspectList existing = generated.get(key);
        if (existing != null) {
            return TCAspectStackRules.applyStackBonuses(stack, existing);
        }

        if (!canGenerateCraftingOutput(stack)) {
            return null;
        }

        if (history.contains(key) || history.size() >= 100) {
            return null;
        }
        history.add(key);

        AspectList best = null;
        int bestValue = Integer.MAX_VALUE;
        for (RecipeHolder<CraftingRecipe> holder : craftingRecipes) {
            CraftingRecipe recipe = holder.value();
            ItemStack result = recipe.getResultItem(registries);
            if (result.isEmpty() || !TCAspectStackKey.from(result).equals(key)) {
                continue;
            }

            AspectList candidate = getAspectsFromIngredients(recipe, result, craftingRecipes, registries, generated, history);
            removeNonPositive(candidate);
            int value = candidate.visSize();
            if (value > 0 && value < bestValue) {
                best = candidate;
                bestValue = value;
            }
        }

        AspectList complex = TCAspectAssignments.getComplexObjectAspects(stack);
        if (best != null || complex != null) {
            AspectList combined = complex != null ? complex.copy() : new AspectList();
            if (best != null) {
                addAll(combined, best);
            }
            removeNonPositive(combined);
            if (combined.visSize() <= 0) {
                return null;
            }
            AspectList capped = capAspects(combined, 500);
            generated.put(key, capped.copy());
            return TCAspectStackRules.applyStackBonuses(stack, capped);
        }
        return null;
    }

    static AspectList calculateCraftingRecipeAspectsForValidation(CraftingRecipe recipe, ItemStack recipeOut, HolderLookup.Provider registries) {
        return getAspectsFromIngredients(recipe, recipeOut, List.of(), registries, new LinkedHashMap<>(), new LinkedHashSet<>());
    }

    private static AspectList getAspectsFromIngredients(
            CraftingRecipe recipe,
            ItemStack recipeOut,
            List<RecipeHolder<CraftingRecipe>> craftingRecipes,
            HolderLookup.Provider registries,
            Map<TCAspectStackKey, AspectList> generated,
            Set<TCAspectStackKey> history) {
        AspectList mid = new AspectList();
        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        for (Ingredient ingredient : ingredients) {
            ItemStack first = firstMatchingStack(ingredient);
            if (first.isEmpty()) {
                continue;
            }

            AspectList aspects = resolveGeneratedAspects(first, craftingRecipes, registries, generated, history);
            if (aspects != null) {
                for (Aspect aspect : aspects.getAspects()) {
                    if (aspect != null) {
                        mid.add(aspect, aspects.getAmount(aspect));
                    }
                }
            }
        }

        NonNullList<ItemStack> remainingItems = getRemainingItems(recipe, ingredients);
        for (ItemStack remaining : remainingItems) {
            if (remaining.isEmpty()) {
                continue;
            }

            AspectList aspects = resolveGeneratedAspects(remaining, craftingRecipes, registries, generated, history);
            if (aspects != null) {
                for (Aspect aspect : aspects.getAspects()) {
                    if (aspect != null) {
                        mid.reduce(aspect, aspects.getAmount(aspect));
                    }
                }
            }
        }

        AspectList out = new AspectList();
        for (Aspect aspect : mid.getAspects()) {
            if (aspect != null) {
                float value = mid.getAmount(aspect) * 0.75F / recipeOut.getCount();
                if (value < 1.0F && value > 0.75F) {
                    value = 1.0F;
                }
                out.add(aspect, (int)value);
            }
        }
        removeNonPositive(out);
        return out;
    }

    private static NonNullList<ItemStack> getRemainingItems(CraftingRecipe recipe, NonNullList<Ingredient> ingredients) {
        if (ingredients.size() > 9) {
            return NonNullList.create();
        }

        List<ItemStack> grid = new ArrayList<>(9);
        for (Ingredient ingredient : ingredients) {
            grid.add(firstMatchingStack(ingredient));
        }
        while (grid.size() < 9) {
            grid.add(ItemStack.EMPTY);
        }

        try {
            return recipe.getRemainingItems(CraftingInput.of(3, 3, grid));
        } catch (RuntimeException ignored) {
            return NonNullList.create();
        }
    }

    private static ItemStack firstMatchingStack(Ingredient ingredient) {
        ItemStack[] stacks = ingredient.getItems();
        if (stacks.length == 0) {
            return ItemStack.EMPTY;
        }
        return stacks[0].copy();
    }

    private static List<RecipeHolder<CraftingRecipe>> craftingRecipes(RecipeManager recipeManager) {
        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
        for (RecipeHolder<?> holder : recipeManager.getOrderedRecipes()) {
            if (holder.value() instanceof CraftingRecipe craftingRecipe) {
                recipes.add(new RecipeHolder<>(holder.id(), craftingRecipe));
            }
        }
        return recipes;
    }

    private static boolean canGenerateCraftingOutput(ItemStack stack) {
        String namespace = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
        return Thaumcraft.MODID.equals(namespace) || "minecraft".equals(namespace);
    }

    private static void addAll(AspectList target, AspectList source) {
        for (Aspect aspect : source.getAspects()) {
            if (aspect != null) {
                target.add(aspect, source.getAmount(aspect));
            }
        }
    }

    private static AspectList capAspects(AspectList source, int amount) {
        if (source == null) {
            return null;
        }

        AspectList out = new AspectList();
        for (Aspect aspect : source.getAspects()) {
            if (aspect != null) {
                out.merge(aspect, Math.min(amount, source.getAmount(aspect)));
            }
        }
        return out;
    }

    private static void removeNonPositive(AspectList aspects) {
        for (Aspect aspect : aspects.copy().getAspects()) {
            if (aspects.getAmount(aspect) <= 0) {
                aspects.remove(aspect);
            }
        }
    }

    private TCGeneratedAspectRecipeGenerator() {
    }
}
