package thaumcraft.common.research;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record TCInfusionRecipePageView(
        ResourceLocation recipeId,
        ItemStack result,
        List<ItemStack> catalystVariants,
        List<List<ItemStack>> componentVariants,
        List<ItemStack> aspectStacks,
        String research,
        int instability
) {
    public TCInfusionRecipePageView {
        catalystVariants = catalystVariants == null ? List.of() : List.copyOf(catalystVariants);
        componentVariants = componentVariants == null ? List.of() : List.copyOf(componentVariants);
        aspectStacks = aspectStacks == null ? List.of() : List.copyOf(aspectStacks);
        research = research == null ? "" : research;
        result = result == null ? ItemStack.EMPTY : result.copy();
    }
}