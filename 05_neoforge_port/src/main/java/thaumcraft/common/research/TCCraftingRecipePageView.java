package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record TCCraftingRecipePageView(
        ResourceLocation recipeId,
        boolean shaped,
        int width,
        int height,
        ItemStack result,
        List<List<ItemStack>> ingredients
) {
    public TCCraftingRecipePageView {
        width = Math.max(1, Math.min(3, width));
        height = Math.max(1, Math.min(3, height));
        result = result == null ? ItemStack.EMPTY : result.copy();

        ArrayList<List<ItemStack>> copiedIngredients = new ArrayList<>(ingredients.size());
        for (List<ItemStack> variants : ingredients) {
            copiedIngredients.add(variants.stream().map(ItemStack::copy).toList());
        }
        ingredients = List.copyOf(copiedIngredients);
    }
}
