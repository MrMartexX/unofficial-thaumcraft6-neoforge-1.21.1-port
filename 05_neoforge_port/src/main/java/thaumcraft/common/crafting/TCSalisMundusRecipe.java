package thaumcraft.common.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import thaumcraft.common.registry.TCRecipes;

/**
 * Salis Mundus crafting recipe preserving tool containers (bowl and flint)
 * in accordance with Legacy Thaumcraft 6 (RecipeMagicDust.java:85).
 */
public class TCSalisMundusRecipe extends ShapelessRecipe {

    public TCSalisMundusRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
    }

    public TCSalisMundusRecipe(ShapelessRecipe base) {
        super(base.getGroup(), base.category(), base.getResultItem(null), base.getIngredients());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(Items.BOWL) || stack.is(Items.FLINT)) {
                    remaining.set(i, stack.copyWithCount(1));
                } else if (stack.hasCraftingRemainingItem()) {
                    remaining.set(i, stack.getCraftingRemainingItem());
                }
            }
        }
        return remaining;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipes.SALIS_MUNDUS_SERIALIZER.get();
    }

    public static final class Serializer implements RecipeSerializer<TCSalisMundusRecipe> {
        private static final MapCodec<TCSalisMundusRecipe> CODEC = RecipeSerializer.SHAPELESS_RECIPE.codec()
                .xmap(TCSalisMundusRecipe::new, recipe -> recipe);

        private static final StreamCodec<RegistryFriendlyByteBuf, TCSalisMundusRecipe> STREAM_CODEC =
                RecipeSerializer.SHAPELESS_RECIPE.streamCodec().map(TCSalisMundusRecipe::new, recipe -> recipe);

        @Override
        public MapCodec<TCSalisMundusRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TCSalisMundusRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
