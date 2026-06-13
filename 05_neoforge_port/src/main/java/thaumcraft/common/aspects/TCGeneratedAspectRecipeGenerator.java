package thaumcraft.common.aspects;

import java.util.ArrayList;
import java.util.Comparator;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.crafting.arcane.TCArcaneRecipe;
import thaumcraft.common.crafting.crucible.TCCrucibleRecipe;
import thaumcraft.common.crafting.infusion.TCInfusionRecipe;

public final class TCGeneratedAspectRecipeGenerator {
    private static final List<ResourceLocation> LEGACY_PREFERRED_INGREDIENT_ITEMS = List.of(
            ResourceLocation.withDefaultNamespace("stick"),
            ResourceLocation.withDefaultNamespace("oak_planks"),
            ResourceLocation.withDefaultNamespace("cobblestone"),
            ResourceLocation.withDefaultNamespace("stone"),
            ResourceLocation.withDefaultNamespace("string"),
            ResourceLocation.withDefaultNamespace("leather"),
            ResourceLocation.withDefaultNamespace("white_wool"),
            ResourceLocation.withDefaultNamespace("iron_ingot"),
            ResourceLocation.withDefaultNamespace("gold_ingot"),
            ResourceLocation.withDefaultNamespace("diamond"),
            ResourceLocation.withDefaultNamespace("emerald"),
            ResourceLocation.withDefaultNamespace("quartz"),
            ResourceLocation.withDefaultNamespace("coal"),
            ResourceLocation.withDefaultNamespace("redstone"),
            ResourceLocation.withDefaultNamespace("lapis_lazuli"),
            ResourceLocation.withDefaultNamespace("glass"),
            ResourceLocation.withDefaultNamespace("paper"),
            ResourceLocation.withDefaultNamespace("slime_ball")
    );

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
        Thaumcraft.LOGGER.info("Thaumcraft generated aspect cache rebuilt from crafting, arcane, crucible, and infusion recipes: {} generated object assignments.", count);
    }

    static int rebuildCraftingRecipeCache(RecipeManager recipeManager, HolderLookup.Provider registries) {
        List<RecipeAspectSource> recipeSources = recipeAspectSources(recipeManager, registries);
        LinkedHashMap<TCAspectStackKey, AspectList> generated = new LinkedHashMap<>();

        for (RecipeAspectSource source : recipeSources) {
            ItemStack result = source.result();
            if (!result.isEmpty() && canGenerateCraftingOutput(result)) {
                resolveGeneratedAspects(result, recipeSources, registries, generated, new LinkedHashSet<>());
            }
        }

        for (ResourceLocation itemId : TCAspectAssignments.complexDirectObjectTags().keySet()) {
            BuiltInRegistries.ITEM.getOptional(itemId).ifPresent(item -> resolveGeneratedAspects(new ItemStack(item), recipeSources, registries, generated, new LinkedHashSet<>()));
        }

        TCGeneratedAspectCache.replaceGeneratedObjectTags(generated);
        return generated.size();
    }

    private static AspectList resolveGeneratedAspects(
            ItemStack stack,
            List<RecipeAspectSource> recipeSources,
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

        AspectList best = firstGeneratedFromCrucibleRecipe(key, recipeSources, registries, generated, history);
        if (best == null) {
            best = firstGeneratedFromInfusionRecipe(key, recipeSources, registries, generated, history);
        }

        if (best == null) {
            int bestValue = Integer.MAX_VALUE;
            for (RecipeAspectSource source : recipeSources) {
                if (!matchesResultKey(source, key) || source.crucibleRecipe() != null || source.infusionRecipe() != null) {
                    continue;
                }

                Set<TCAspectStackKey> candidateHistory = new LinkedHashSet<>(history);
                AspectList candidate = getAspectsFromIngredients(source, recipeSources, registries, generated, candidateHistory);
                if (candidate == null) {
                    continue;
                }
                addArcaneVisBonus(candidate, source.arcaneRecipe(), source.result());
                removeNonPositive(candidate);
                int value = candidate.visSize();
                if (value > 0 && value < bestValue) {
                    best = candidate;
                    bestValue = value;
                }
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
        RecipeAspectSource source = new RecipeAspectSource(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "validation"), recipe, recipeOut.copy(), null);
        return getAspectsFromIngredients(source, List.of(), registries, new LinkedHashMap<>(), new LinkedHashSet<>());
    }

    static AspectList calculateArcaneRecipeAspectsForValidation(TCArcaneRecipe recipe, ItemStack recipeOut, HolderLookup.Provider registries) {
        RecipeAspectSource source = new RecipeAspectSource(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "validation"), recipe, recipeOut.copy(), recipe);
        AspectList aspects = getAspectsFromIngredients(source, List.of(), registries, new LinkedHashMap<>(), new LinkedHashSet<>());
        addArcaneVisBonus(aspects, recipe, recipeOut);
        removeNonPositive(aspects);
        return aspects;
    }

    static AspectList calculateCrucibleRecipeAspectsForValidation(TCCrucibleRecipe recipe, ItemStack recipeOut, HolderLookup.Provider registries) {
        AspectList aspects = getAspectsFromCrucibleRecipe(recipe, List.of(), registries, new LinkedHashMap<>(), new LinkedHashSet<>());
        removeNonPositive(aspects);
        return aspects;
    }

    static AspectList calculateInfusionRecipeAspectsForValidation(TCInfusionRecipe recipe, ItemStack recipeOut, HolderLookup.Provider registries) {
        AspectList aspects = getAspectsFromInfusionRecipe(recipe, List.of(), registries, new LinkedHashMap<>(), new LinkedHashSet<>());
        removeNonPositive(aspects);
        return aspects;
    }

    private static AspectList firstGeneratedFromCrucibleRecipe(
            TCAspectStackKey key,
            List<RecipeAspectSource> recipeSources,
            HolderLookup.Provider registries,
            Map<TCAspectStackKey, AspectList> generated,
            Set<TCAspectStackKey> history) {
        for (RecipeAspectSource source : recipeSources) {
            if (!matchesResultKey(source, key) || source.crucibleRecipe() == null) {
                continue;
            }
            Set<TCAspectStackKey> candidateHistory = new LinkedHashSet<>(history);
            AspectList candidate = getAspectsFromCrucibleRecipe(source.crucibleRecipe(), recipeSources, registries, generated, candidateHistory);
            if (candidate != null) {
                removeNonPositive(candidate);
                if (candidate.visSize() > 0) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private static AspectList firstGeneratedFromInfusionRecipe(
            TCAspectStackKey key,
            List<RecipeAspectSource> recipeSources,
            HolderLookup.Provider registries,
            Map<TCAspectStackKey, AspectList> generated,
            Set<TCAspectStackKey> history) {
        for (RecipeAspectSource source : recipeSources) {
            if (!matchesResultKey(source, key) || source.infusionRecipe() == null) {
                continue;
            }
            Set<TCAspectStackKey> candidateHistory = new LinkedHashSet<>(history);
            AspectList candidate = getAspectsFromInfusionRecipe(source.infusionRecipe(), recipeSources, registries, generated, candidateHistory);
            if (candidate != null) {
                removeNonPositive(candidate);
                if (candidate.visSize() > 0) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private static AspectList getAspectsFromIngredients(
            RecipeAspectSource source,
            List<RecipeAspectSource> recipeSources,
            HolderLookup.Provider registries,
            Map<TCAspectStackKey, AspectList> generated,
            Set<TCAspectStackKey> history) {
        AspectList mid = new AspectList();
        Recipe<CraftingInput> recipe = source.craftingInputRecipe();
        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        for (Ingredient ingredient : ingredients) {
            addIngredientAspects(mid, ingredient, recipeSources, registries, generated, history);
        }

        NonNullList<ItemStack> remainingItems = getRemainingItems(recipe, ingredients);
        for (ItemStack remaining : remainingItems) {
            if (remaining.isEmpty()) {
                continue;
            }

            AspectList aspects = resolveGeneratedAspects(remaining, recipeSources, registries, generated, history);
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
                float value = mid.getAmount(aspect) * 0.75F / source.result().getCount();
                if (value < 1.0F && value > 0.75F) {
                    value = 1.0F;
                }
                out.add(aspect, (int)value);
            }
        }
        removeNonPositive(out);
        return out;
    }

    private static AspectList getAspectsFromInfusionRecipe(
            TCInfusionRecipe recipe,
            List<RecipeAspectSource> recipeSources,
            HolderLookup.Provider registries,
            Map<TCAspectStackKey, AspectList> generated,
            Set<TCAspectStackKey> history) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(recipe.central());
        ingredients.addAll(recipe.components());

        AspectList out = getAspectsFromIngredientList(ingredients, recipe.result(), recipeSources, registries, generated, history);
        AspectList recipeAspects = recipe.aspects();
        int outputCount = recipe.result().getCount();
        for (Aspect aspect : recipeAspects.getAspects()) {
            if (aspect != null) {
                int amount = (int)(Math.sqrt(recipeAspects.getAmount(aspect)) / (float)outputCount);
                out.add(aspect, amount);
            }
        }
        removeNonPositive(out);
        return out;
    }

    private static AspectList getAspectsFromIngredientList(
            NonNullList<Ingredient> ingredients,
            ItemStack result,
            List<RecipeAspectSource> recipeSources,
            HolderLookup.Provider registries,
            Map<TCAspectStackKey, AspectList> generated,
            Set<TCAspectStackKey> history) {
        AspectList mid = new AspectList();
        for (Ingredient ingredient : ingredients) {
            addIngredientAspects(mid, ingredient, recipeSources, registries, generated, history);
        }

        AspectList out = new AspectList();
        for (Aspect aspect : mid.getAspects()) {
            if (aspect != null) {
                float value = mid.getAmount(aspect) * 0.75F / result.getCount();
                if (value < 1.0F && value > 0.75F) {
                    value = 1.0F;
                }
                out.add(aspect, (int)value);
            }
        }
        removeNonPositive(out);
        return out;
    }

    private static void addIngredientAspects(
            AspectList target,
            Ingredient ingredient,
            List<RecipeAspectSource> recipeSources,
            HolderLookup.Provider registries,
            Map<TCAspectStackKey, AspectList> generated,
            Set<TCAspectStackKey> history) {
        ItemStack first = firstMatchingStack(ingredient);
        if (first.isEmpty()) {
            return;
        }

        AspectList aspects = resolveGeneratedAspects(first, recipeSources, registries, generated, history);
        if (aspects != null) {
            addAll(target, aspects);
        }
    }

    private static AspectList getAspectsFromCrucibleRecipe(
            TCCrucibleRecipe recipe,
            List<RecipeAspectSource> recipeSources,
            HolderLookup.Provider registries,
            Map<TCAspectStackKey, AspectList> generated,
            Set<TCAspectStackKey> history) {
        ItemStack catalyst = firstMatchingStack(recipe.catalyst());
        if (catalyst.isEmpty()) {
            return null;
        }

        AspectList out = new AspectList();
        AspectList catalystAspects = resolveGeneratedAspects(catalyst, recipeSources, registries, generated, history);
        if (catalystAspects != null && catalystAspects.size() > 0) {
            addAll(out, catalystAspects);
        }

        AspectList recipeAspects = recipe.aspects();
        int outputCount = recipe.result().getCount();
        for (Aspect aspect : recipeAspects.getAspects()) {
            if (aspect != null) {
                int amount = (int)(Math.sqrt(recipeAspects.getAmount(aspect)) / (float)outputCount);
                out.add(aspect, amount);
            }
        }
        removeNonPositive(out);
        return out;
    }

    private static NonNullList<ItemStack> getRemainingItems(Recipe<CraftingInput> recipe, NonNullList<Ingredient> ingredients) {
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
        for (ResourceLocation preferred : LEGACY_PREFERRED_INGREDIENT_ITEMS) {
            for (ItemStack stack : stacks) {
                if (preferred.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()))) {
                    return stack.copy();
                }
            }
        }
        return stacks[0].copy();
    }

    private static List<RecipeAspectSource> recipeAspectSources(RecipeManager recipeManager, HolderLookup.Provider registries) {
        List<RecipeAspectSource> recipes = new ArrayList<>();
        for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
            if (holder.value() instanceof CraftingRecipe craftingRecipe) {
                recipes.add(new RecipeAspectSource(holder.id(), craftingRecipe, craftingRecipe.getResultItem(registries), null));
            } else if (holder.value() instanceof TCArcaneRecipe arcaneRecipe) {
                recipes.add(new RecipeAspectSource(holder.id(), arcaneRecipe, arcaneRecipe.getResultItem(registries), arcaneRecipe));
            } else if (holder.value() instanceof TCCrucibleRecipe crucibleRecipe) {
                recipes.add(new RecipeAspectSource(holder.id(), crucibleRecipe, crucibleRecipe.getResultItem(registries), null, crucibleRecipe, null));
            } else if (holder.value() instanceof TCInfusionRecipe infusionRecipe) {
                recipes.add(new RecipeAspectSource(holder.id(), infusionRecipe, infusionRecipe.getResultItem(registries), null, null, infusionRecipe));
            }
        }
        recipes.sort(Comparator.comparing(source -> source.id().toString()));
        return recipes;
    }

    private static boolean matchesResultKey(RecipeAspectSource source, TCAspectStackKey key) {
        ItemStack result = source.result();
        return !result.isEmpty() && TCAspectStackKey.from(result).equals(key);
    }

    private static void addArcaneVisBonus(AspectList aspects, TCArcaneRecipe recipe, ItemStack result) {
        if (recipe == null || recipe.getVis() <= 0 || result.isEmpty()) {
            return;
        }

        int amount = (int)(Math.sqrt(1 + recipe.getVis() / 2) / (float)result.getCount());
        if (amount > 0) {
            aspects.add(Aspect.MAGIC, amount);
        }
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

    private record RecipeAspectSource(ResourceLocation id, Recipe<?> recipe, ItemStack result, TCArcaneRecipe arcaneRecipe, TCCrucibleRecipe crucibleRecipe, TCInfusionRecipe infusionRecipe) {
        private RecipeAspectSource(ResourceLocation id, Recipe<CraftingInput> recipe, ItemStack result, TCArcaneRecipe arcaneRecipe) {
            this(id, recipe, result, arcaneRecipe, null, null);
        }

        @SuppressWarnings("unchecked")
        private Recipe<CraftingInput> craftingInputRecipe() {
            return (Recipe<CraftingInput>) recipe;
        }
    }

    private TCGeneratedAspectRecipeGenerator() {
    }
}
