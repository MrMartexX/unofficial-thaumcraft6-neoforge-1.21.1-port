package thaumcraft.common.crafting;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCRecipes;

public class TCTripleMeatTreatRecipe extends CustomRecipe {
    public TCTripleMeatTreatRecipe(CraftingBookCategory category) {
        super(category);
    }

    private static boolean isMeatChunk(Item item) {
        return item == TCItems.CHUNK_BEEF.get()
                || item == TCItems.CHUNK_CHICKEN.get()
                || item == TCItems.CHUNK_PORK.get()
                || item == TCItems.CHUNK_FISH.get()
                || item == TCItems.CHUNK_RABBIT.get()
                || item == TCItems.CHUNK_MUTTON.get();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int sugarCount = 0;
        Set<Item> uniqueChunks = new HashSet<>();
        int otherItems = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(Items.SUGAR)) {
                    sugarCount++;
                } else if (isMeatChunk(stack.getItem())) {
                    uniqueChunks.add(stack.getItem());
                } else {
                    otherItems++;
                }
            }
        }

        return sugarCount == 1 && uniqueChunks.size() == 3 && otherItems == 0 && input.ingredientCount() == 4;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return new ItemStack(TCItems.TRIPLE_MEAT_TREAT.get());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 4;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipes.TRIPLE_MEAT_TREAT_SERIALIZER.get();
    }
}
