package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record TCDisplayRecipePageView(
        ResourceLocation recipeId,
        TCDisplayRecipePageType type,
        ItemStack result,
        List<ItemStack> catalystStacks,
        List<ItemStack> componentStacks,
        List<ItemStack> aspectStacks,
        String titleKey,
        int instability
) {
    public TCDisplayRecipePageView {
        result = result == null ? ItemStack.EMPTY : result.copy();
        catalystStacks = copyStacks(catalystStacks);
        componentStacks = copyStacks(componentStacks);
        aspectStacks = copyStacks(aspectStacks);
        titleKey = titleKey == null ? "" : titleKey;
        instability = Math.max(0, instability);
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks) {
        if (stacks == null || stacks.isEmpty()) {
            return List.of();
        }
        ArrayList<ItemStack> copied = new ArrayList<>(stacks.size());
        for (ItemStack stack : stacks) {
            copied.add(stack == null ? ItemStack.EMPTY : stack.copy());
        }
        return List.copyOf(copied);
    }
}
