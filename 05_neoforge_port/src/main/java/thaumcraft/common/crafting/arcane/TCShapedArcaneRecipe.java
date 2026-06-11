package thaumcraft.common.crafting.arcane;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import thaumcraft.common.registry.TCRecipes;

public final class TCShapedArcaneRecipe extends TCArcaneRecipe {
    private final ShapedRecipePattern pattern;

    public TCShapedArcaneRecipe(
            String group,
            String research,
            int vis,
            List<TCArcaneCrystalCost> crystalCosts,
            ShapedRecipePattern pattern,
            ItemStack result
    ) {
        super(group, research, vis, crystalCosts, result);
        this.pattern = pattern;
    }

    public ShapedRecipePattern pattern() {
        return pattern;
    }

    @Override
    public boolean shaped() {
        return true;
    }

    @Override
    public int width() {
        return pattern.width();
    }

    @Override
    public int height() {
        return pattern.height();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return pattern.matches(input);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= pattern.width() && height >= pattern.height();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return pattern.ingredients();
    }

    @Override
    public boolean isIncomplete() {
        return getIngredients().isEmpty()
                || getIngredients().stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(Ingredient::hasNoItems);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipes.ARCANE_SHAPED_SERIALIZER.get();
    }

    public static final class Serializer implements RecipeSerializer<TCShapedArcaneRecipe> {
        private static final MapCodec<TCShapedArcaneRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(TCShapedArcaneRecipe::getGroup),
                Codec.STRING.fieldOf("research").forGetter(TCShapedArcaneRecipe::getResearch),
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("vis").forGetter(TCShapedArcaneRecipe::getVis),
                TCArcaneCrystalCost.LIST_CODEC.optionalFieldOf("crystals", List.of())
                        .forGetter(TCShapedArcaneRecipe::crystalCosts),
                ShapedRecipePattern.MAP_CODEC.forGetter(TCShapedArcaneRecipe::pattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TCShapedArcaneRecipe::result)
        ).apply(instance, TCShapedArcaneRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TCShapedArcaneRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<TCShapedArcaneRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TCShapedArcaneRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TCShapedArcaneRecipe recipe) {
            TCArcaneRecipe.writeCommon(buffer, recipe);
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
        }

        private static TCShapedArcaneRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            CommonFields fields = TCArcaneRecipe.readCommon(buffer);
            ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            return new TCShapedArcaneRecipe(
                    fields.group(),
                    fields.research(),
                    fields.vis(),
                    fields.crystalCosts(),
                    pattern,
                    fields.result()
            );
        }
    }
}
