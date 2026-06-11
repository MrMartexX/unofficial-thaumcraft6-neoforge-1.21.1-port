package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record TCArcaneRecipePageView(
        ResourceLocation recipeId,
        boolean shaped,
        int width,
        int height,
        ItemStack result,
        List<List<ItemStack>> ingredients,
        String research,
        int vis,
        List<ItemStack> crystalStacks
) {
    public TCArcaneRecipePageView {
        width = Math.max(1, Math.min(3, width));
        height = Math.max(1, Math.min(3, height));
        result = result == null ? ItemStack.EMPTY : result.copy();
        research = research == null ? "" : research;
        vis = Math.max(0, vis);

        ingredients = copyStackVariants(ingredients);
        crystalStacks = copyStacks(crystalStacks);
    }

    private static List<List<ItemStack>> copyStackVariants(List<List<ItemStack>> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return List.of();
        }
        ArrayList<List<ItemStack>> copiedIngredients = new ArrayList<>(ingredients.size());
        for (List<ItemStack> variants : ingredients) {
            if (variants == null || variants.isEmpty()) {
                copiedIngredients.add(List.of());
                continue;
            }
            copiedIngredients.add(variants.stream().map(ItemStack::copy).toList());
        }
        return List.copyOf(copiedIngredients);
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks) {
        if (stacks == null || stacks.isEmpty()) {
            return List.of();
        }
        return stacks.stream().map(ItemStack::copy).toList();
    }
}
