package thaumcraft.common.crafting.infusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.registry.TCRecipes;

public final class TCInfusionRecipe implements Recipe<SingleRecipeInput> {
    private static final int MAX_ASPECT_COSTS = 64;
    private static final int MAX_COMPONENTS = 64;
    private static final Codec<NonNullList<Ingredient>> COMPONENTS_CODEC = Ingredient.CODEC_NONEMPTY.listOf()
            .flatXmap(TCInfusionRecipe::validateComponents, components -> DataResult.success(List.copyOf(components)));

    private final String group;
    private final String research;
    private final int instability;
    private final Ingredient central;
    private final NonNullList<Ingredient> components;
    private final List<TCInfusionAspectCost> aspectCosts;
    private final ItemStack result;

    public TCInfusionRecipe(
            String group,
            String research,
            int instability,
            Ingredient central,
            NonNullList<Ingredient> components,
            List<TCInfusionAspectCost> aspectCosts,
            ItemStack result
    ) {
        this.group = group == null ? "" : group.trim();
        this.research = research == null ? "" : research.trim();
        if (instability < 0) {
            throw new IllegalArgumentException("Infusion recipe instability cannot be negative");
        }
        this.instability = instability;
        this.central = central;
        if (this.central == null || this.central.isEmpty()) {
            throw new IllegalArgumentException("Infusion recipe central ingredient cannot be empty");
        }
        if (components.size() > MAX_COMPONENTS) {
            throw new IllegalArgumentException("Too many infusion recipe components: " + components.size());
        }
        this.components = NonNullList.create();
        this.components.addAll(components);
        this.aspectCosts = List.copyOf(aspectCosts);
        if (this.aspectCosts.isEmpty()) {
            throw new IllegalArgumentException("Infusion recipe aspect costs cannot be empty");
        }
        this.result = result.copy();
        if (this.result.isEmpty()) {
            throw new IllegalArgumentException("Infusion recipe result cannot be empty");
        }
    }

    public Ingredient central() {
        return central;
    }

    public NonNullList<Ingredient> components() {
        return components;
    }

    public List<TCInfusionAspectCost> aspectCosts() {
        return aspectCosts;
    }

    public AspectList aspects() {
        return TCInfusionAspectCost.toAspectList(aspectCosts);
    }

    public ItemStack result() {
        return result.copy();
    }

    public String research() {
        return research;
    }

    public int instability() {
        return instability;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return central.test(input.item());
    }

    public boolean matchesInfusion(ItemStack centralStack, List<ItemStack> pedestalStacks) {
        if (!central.test(centralStack)) {
            return false;
        }

        ArrayList<ItemStack> nonEmptyItems = new ArrayList<>();
        for (ItemStack stack : pedestalStacks) {
            if (!stack.isEmpty()) {
                nonEmptyItems.add(stack);
            }
        }
        if (nonEmptyItems.size() != components.size()) {
            return false;
        }
        return RecipeMatcher.findMatches(nonEmptyItems, components) != null;
    }

    public AspectList removeMatchingAspects(AspectList infusionAspects) {
        AspectList remaining = infusionAspects == null ? new AspectList() : infusionAspects.copy();
        for (TCInfusionAspectCost cost : aspectCosts) {
            remaining.remove(cost.resolvedAspect(), cost.amount());
        }
        return remaining;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(central);
        ingredients.addAll(components);
        return ingredients;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipes.INFUSION_SERIALIZER.get();
    }

    @Override
    public net.minecraft.world.item.crafting.RecipeType<?> getType() {
        return TCRecipes.INFUSION_TYPE.get();
    }

    static void writeCommon(RegistryFriendlyByteBuf buffer, TCInfusionRecipe recipe) {
        buffer.writeUtf(recipe.getGroup());
        buffer.writeUtf(recipe.research());
        buffer.writeVarInt(recipe.instability());
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.central());
        writeComponents(buffer, recipe.components());
        writeAspectCosts(buffer, recipe.aspectCosts());
        ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
    }

    static TCInfusionRecipe readCommon(RegistryFriendlyByteBuf buffer) {
        return new TCInfusionRecipe(
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readVarInt(),
                Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                readComponents(buffer),
                readAspectCosts(buffer),
                ItemStack.STREAM_CODEC.decode(buffer)
        );
    }

    private static void writeComponents(RegistryFriendlyByteBuf buffer, NonNullList<Ingredient> components) {
        if (components.size() > MAX_COMPONENTS) {
            throw new IllegalArgumentException("Too many infusion recipe components: " + components.size());
        }
        buffer.writeVarInt(components.size());
        for (Ingredient component : components) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, component);
        }
    }

    private static NonNullList<Ingredient> readComponents(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        if (size < 0 || size > MAX_COMPONENTS) {
            throw new IllegalArgumentException("Invalid infusion recipe component count: " + size);
        }
        NonNullList<Ingredient> components = NonNullList.withSize(size, Ingredient.EMPTY);
        components.replaceAll(ignored -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
        return components;
    }

    private static void writeAspectCosts(RegistryFriendlyByteBuf buffer, List<TCInfusionAspectCost> costs) {
        if (costs.size() > MAX_ASPECT_COSTS) {
            throw new IllegalArgumentException("Too many infusion aspect costs: " + costs.size());
        }
        buffer.writeVarInt(costs.size());
        for (TCInfusionAspectCost cost : costs) {
            buffer.writeUtf(cost.aspect());
            buffer.writeVarInt(cost.amount());
        }
    }

    private static List<TCInfusionAspectCost> readAspectCosts(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        if (size < 1 || size > MAX_ASPECT_COSTS) {
            throw new IllegalArgumentException("Invalid infusion aspect cost count: " + size);
        }
        ArrayList<TCInfusionAspectCost> costs = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            costs.add(new TCInfusionAspectCost(buffer.readUtf(), buffer.readVarInt()));
        }
        return List.copyOf(costs);
    }

    private static DataResult<NonNullList<Ingredient>> validateComponents(List<Ingredient> components) {
        if (components.size() > MAX_COMPONENTS) {
            return DataResult.error(() -> "Too many infusion recipe components: " + components.size());
        }
        return DataResult.success(NonNullList.of(Ingredient.EMPTY, components.toArray(Ingredient[]::new)));
    }

    public static final class Serializer implements RecipeSerializer<TCInfusionRecipe> {
        private static final MapCodec<TCInfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(TCInfusionRecipe::getGroup),
                Codec.STRING.optionalFieldOf("research", "").forGetter(TCInfusionRecipe::research),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("instability", 0).forGetter(TCInfusionRecipe::instability),
                Ingredient.CODEC_NONEMPTY.fieldOf("central").forGetter(TCInfusionRecipe::central),
                COMPONENTS_CODEC.optionalFieldOf("components", NonNullList.create()).forGetter(TCInfusionRecipe::components),
                TCInfusionAspectCost.MAP_CODEC.fieldOf("aspects").forGetter(TCInfusionRecipe::aspectCosts),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TCInfusionRecipe::result)
        ).apply(instance, TCInfusionRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TCInfusionRecipe> STREAM_CODEC = StreamCodec.of(
                TCInfusionRecipe::writeCommon,
                TCInfusionRecipe::readCommon
        );

        @Override
        public MapCodec<TCInfusionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TCInfusionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
