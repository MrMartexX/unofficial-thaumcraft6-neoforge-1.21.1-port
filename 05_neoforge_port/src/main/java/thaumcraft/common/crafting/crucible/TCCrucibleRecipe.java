package thaumcraft.common.crafting.crucible;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import thaumcraft.common.registry.TCRecipes;

public final class TCCrucibleRecipe implements Recipe<CraftingInput> {
    private static final int MAX_ASPECTS = 64;

    private final String group;
    private final String research;
    private final Ingredient catalyst;
    private final List<TCCrucibleAspectCost> aspectCosts;
    private final ItemStack result;

    public TCCrucibleRecipe(
            String group,
            String research,
            Ingredient catalyst,
            List<TCCrucibleAspectCost> aspectCosts,
            ItemStack result
    ) {
        this.group = group == null ? "" : group.trim();
        this.research = research == null ? "" : research.trim();
        if (this.research.isBlank()) {
            throw new IllegalArgumentException("Crucible recipe research key cannot be blank");
        }
        this.catalyst = catalyst == null ? Ingredient.EMPTY : catalyst;
        if (this.catalyst.isEmpty()) {
            throw new IllegalArgumentException("Crucible recipe catalyst cannot be empty");
        }
        this.aspectCosts = List.copyOf(aspectCosts == null ? List.of() : aspectCosts);
        this.result = result == null ? ItemStack.EMPTY : result.copy();
        if (this.result.isEmpty()) {
            throw new IllegalArgumentException("Crucible recipe result cannot be empty");
        }
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, catalyst);
    }

    public String getGroup() {
        return group;
    }

    public String getResearch() {
        return research;
    }

    public Ingredient catalyst() {
        return catalyst;
    }

    public List<TCCrucibleAspectCost> aspectCosts() {
        return aspectCosts;
    }

    public ItemStack result() {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipes.CRUCIBLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TCRecipes.CRUCIBLE_TYPE.get();
    }

    public static final class Serializer implements RecipeSerializer<TCCrucibleRecipe> {
        private static final MapCodec<TCCrucibleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(TCCrucibleRecipe::getGroup),
                Codec.STRING.fieldOf("research").forGetter(TCCrucibleRecipe::getResearch),
                Ingredient.CODEC_NONEMPTY.fieldOf("catalyst").forGetter(TCCrucibleRecipe::catalyst),
                TCCrucibleAspectCost.LIST_CODEC.optionalFieldOf("aspects", List.of())
                        .forGetter(TCCrucibleRecipe::aspectCosts),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TCCrucibleRecipe::result)
        ).apply(instance, TCCrucibleRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TCCrucibleRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<TCCrucibleRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TCCrucibleRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TCCrucibleRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeUtf(recipe.getResearch());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.catalyst);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            if (recipe.aspectCosts.size() > MAX_ASPECTS) {
                throw new IllegalArgumentException("Too many crucible aspect costs: " + recipe.aspectCosts.size());
            }
            buffer.writeVarInt(recipe.aspectCosts.size());
            for (TCCrucibleAspectCost cost : recipe.aspectCosts) {
                buffer.writeUtf(cost.aspect());
                buffer.writeVarInt(cost.amount());
            }
        }

        private static TCCrucibleRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            String research = buffer.readUtf();
            Ingredient catalyst = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            int aspectCount = buffer.readVarInt();
            if (aspectCount < 0 || aspectCount > MAX_ASPECTS) {
                throw new IllegalArgumentException("Invalid crucible aspect count: " + aspectCount);
            }
            java.util.ArrayList<TCCrucibleAspectCost> aspects = new java.util.ArrayList<>(aspectCount);
            for (int index = 0; index < aspectCount; index++) {
                aspects.add(new TCCrucibleAspectCost(buffer.readUtf(), buffer.readVarInt()));
            }
            return new TCCrucibleRecipe(group, research, catalyst, List.copyOf(aspects), result);
        }
    }
}
