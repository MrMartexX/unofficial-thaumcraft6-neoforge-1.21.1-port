package thaumcraft.common.research;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record TCThaumonomiconRecipeSearchView(
        ResourceLocation bookmarkId,
        int pageIndex,
        ItemStack result
) {
    public TCThaumonomiconRecipeSearchView {
        pageIndex = Math.max(0, pageIndex);
        result = result == null ? ItemStack.EMPTY : result.copy();
    }
}
