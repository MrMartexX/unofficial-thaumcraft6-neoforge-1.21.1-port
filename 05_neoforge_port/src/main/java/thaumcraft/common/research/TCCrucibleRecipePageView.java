package thaumcraft.common.research;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record TCCrucibleRecipePageView(
        ResourceLocation recipeId,
        ItemStack result,
        List<ItemStack> catalystVariants,
        List<ItemStack> aspectStacks,
        String research
) {
    public TCCrucibleRecipePageView {
        result = result == null ? ItemStack.EMPTY : result.copy();
        catalystVariants = copyStacks(catalystVariants);
        aspectStacks = copyStacks(aspectStacks);
        research = research == null ? "" : research;
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks) {
        if (stacks == null || stacks.isEmpty()) {
            return List.of();
        }
        return stacks.stream().map(ItemStack::copy).toList();
    }
}
