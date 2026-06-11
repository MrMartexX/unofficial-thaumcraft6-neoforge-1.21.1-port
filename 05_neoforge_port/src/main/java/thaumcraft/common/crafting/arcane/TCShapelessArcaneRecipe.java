package thaumcraft.common.crafting.arcane;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import thaumcraft.common.registry.TCRecipes;

public final class TCShapelessArcaneRecipe extends TCArcaneRecipe {
    private static final Codec<NonNullList<Ingredient>> INGREDIENTS_CODEC = Ingredient.CODEC_NONEMPTY.listOf()
            .flatXmap(TCShapelessArcaneRecipe::validateIngredients, ingredients -> DataResult.success(List.copyOf(ingredients)));

    private final NonNullList<Ingredient> ingredients;
    private final boolean simple;

    public TCShapelessArcaneRecipe(
            String group,
            String research,
            int vis,
            List<TCArcaneCrystalCost> crystalCosts,
            NonNullList<Ingredient> ingredients,
            ItemStack result
    ) {
        super(group, research, vis, crystalCosts, result);
        if (ingredients.isEmpty() || ingredients.size() > 9) {
            throw new IllegalArgumentException("Shapeless arcane recipes require between 1 and 9 ingredients");
        }
        this.ingredients = NonNullList.create();
        this.ingredients.addAll(ingredients);
        this.simple = ingredients.stream().allMatch(Ingredient::isSimple);
    }

    @Override
    public boolean shaped() {
        return false;
    }

    @Override
    public int width() {
        return 3;
    }

    @Override
    public int height() {
        return 3;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != ingredients.size()) {
            return false;
        }
        if (!simple) {
            ArrayList<ItemStack> nonEmptyItems = new ArrayList<>(input.ingredientCount());
            for (ItemStack item : input.items()) {
                if (!item.isEmpty()) {
                    nonEmptyItems.add(item);
                }
            }
            return RecipeMatcher.findMatches(nonEmptyItems, ingredients) != null;
        }
        return input.size() == 1 && ingredients.size() == 1
                ? ingredients.getFirst().test(input.getItem(0))
                : input.stackedContents().canCraft(this, null);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= ingredients.size();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipes.ARCANE_SHAPELESS_SERIALIZER.get();
    }

    public static final class Serializer implements RecipeSerializer<TCShapelessArcaneRecipe> {
        private static final MapCodec<TCShapelessArcaneRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(TCShapelessArcaneRecipe::getGroup),
                Codec.STRING.fieldOf("research").forGetter(TCShapelessArcaneRecipe::getResearch),
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("vis").forGetter(TCShapelessArcaneRecipe::getVis),
                TCArcaneCrystalCost.LIST_CODEC.optionalFieldOf("crystals", List.of())
                        .forGetter(TCShapelessArcaneRecipe::crystalCosts),
                INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(TCShapelessArcaneRecipe::getIngredients),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TCShapelessArcaneRecipe::result)
        ).apply(instance, TCShapelessArcaneRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TCShapelessArcaneRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<TCShapelessArcaneRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TCShapelessArcaneRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TCShapelessArcaneRecipe recipe) {
            TCArcaneRecipe.writeCommon(buffer, recipe);
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
        }

        private static TCShapelessArcaneRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            CommonFields fields = TCArcaneRecipe.readCommon(buffer);
            int size = buffer.readVarInt();
            if (size < 1 || size > 9) {
                throw new IllegalArgumentException("Invalid shapeless arcane ingredient count: " + size);
            }
            NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
            ingredients.replaceAll(ignored -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            return new TCShapelessArcaneRecipe(
                    fields.group(),
                    fields.research(),
                    fields.vis(),
                    fields.crystalCosts(),
                    ingredients,
                    fields.result()
            );
        }
    }

    private static DataResult<NonNullList<Ingredient>> validateIngredients(List<Ingredient> ingredients) {
        if (ingredients.isEmpty()) {
            return DataResult.error(() -> "No ingredients for shapeless arcane recipe");
        }
        if (ingredients.size() > 9) {
            return DataResult.error(() -> "Too many ingredients for shapeless arcane recipe: " + ingredients.size());
        }
        return DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new)));
    }
}
