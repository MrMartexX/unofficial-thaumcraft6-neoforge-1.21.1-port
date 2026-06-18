package thaumcraft.common.crafting.infusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
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
import thaumcraft.common.crafting.crucible.TCCrucibleAspectCost;
import thaumcraft.common.registry.TCRecipes;

public final class TCInfusionRecipe implements Recipe<CraftingInput> {
    private static final int MAX_ASPECTS = 64;
    private static final int MAX_COMPONENTS = 64;

    private final String group;
    private final String research;
    private final int instability;
    private final Ingredient catalyst;
    private final List<Ingredient> components;
    private final List<TCCrucibleAspectCost> aspectCosts;
    private final ItemStack result;

    public TCInfusionRecipe(
            String group,
            String research,
            int instability,
            Ingredient catalyst,
            List<Ingredient> components,
            List<TCCrucibleAspectCost> aspectCosts,
            ItemStack result
    ) {
        this.group = group == null ? "" : group.trim();
        this.research = research == null ? "" : research.trim();
        if (this.research.isBlank()) {
            throw new IllegalArgumentException("Infusion recipe research key cannot be blank");
        }
        if (instability < 0) {
            throw new IllegalArgumentException("Infusion recipe instability cannot be negative");
        }
        this.instability = instability;
        this.catalyst = catalyst == null ? Ingredient.EMPTY : catalyst;
        if (this.catalyst.isEmpty()) {
            throw new IllegalArgumentException("Infusion recipe catalyst cannot be empty");
        }
        this.components = List.copyOf(components == null ? List.of() : components);
        if (this.components.size() > MAX_COMPONENTS) {
            throw new IllegalArgumentException("Too many infusion components: " + this.components.size());
        }
        for (Ingredient component : this.components) {
            if (component == null || component.isEmpty()) {
                throw new IllegalArgumentException("Infusion recipe components cannot be empty");
            }
        }
        this.aspectCosts = List.copyOf(aspectCosts == null ? List.of() : aspectCosts);
        this.result = result == null ? ItemStack.EMPTY : result.copy();
        if (this.result.isEmpty()) {
            throw new IllegalArgumentException("Infusion recipe result cannot be empty");
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
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(catalyst);
        ingredients.addAll(components);
        return ingredients;
    }

    public String getGroup() {
        return group;
    }

    public String getResearch() {
        return research;
    }

    public int instability() {
        return instability;
    }

    public Ingredient catalyst() {
        return catalyst;
    }

    public List<Ingredient> components() {
        return components;
    }

    public List<TCCrucibleAspectCost> aspectCosts() {
        return aspectCosts;
    }

    public ItemStack result() {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipes.INFUSION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TCRecipes.INFUSION_TYPE.get();
    }

    public static final class Serializer implements RecipeSerializer<TCInfusionRecipe> {
        private static final MapCodec<TCInfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(TCInfusionRecipe::getGroup),
                Codec.STRING.fieldOf("research").forGetter(TCInfusionRecipe::getResearch),
                Codec.INT.optionalFieldOf("instability", 0).forGetter(TCInfusionRecipe::instability),
                Ingredient.CODEC_NONEMPTY.fieldOf("catalyst").forGetter(TCInfusionRecipe::catalyst),
                Ingredient.CODEC_NONEMPTY.listOf().optionalFieldOf("components", List.of()).forGetter(TCInfusionRecipe::components),
                TCCrucibleAspectCost.LIST_CODEC.optionalFieldOf("aspects", List.of())
                        .forGetter(TCInfusionRecipe::aspectCosts),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TCInfusionRecipe::result)
        ).apply(instance, TCInfusionRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TCInfusionRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<TCInfusionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TCInfusionRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TCInfusionRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeUtf(recipe.getResearch());
            buffer.writeVarInt(recipe.instability);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.catalyst);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            if (recipe.components.size() > MAX_COMPONENTS) {
                throw new IllegalArgumentException("Too many infusion components: " + recipe.components.size());
            }
            buffer.writeVarInt(recipe.components.size());
            for (Ingredient component : recipe.components) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, component);
            }
            if (recipe.aspectCosts.size() > MAX_ASPECTS) {
                throw new IllegalArgumentException("Too many infusion aspect costs: " + recipe.aspectCosts.size());
            }
            buffer.writeVarInt(recipe.aspectCosts.size());
            for (TCCrucibleAspectCost cost : recipe.aspectCosts) {
                buffer.writeUtf(cost.aspect());
                buffer.writeVarInt(cost.amount());
            }
        }

        private static TCInfusionRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            String research = buffer.readUtf();
            int instability = buffer.readVarInt();
            Ingredient catalyst = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            int componentCount = buffer.readVarInt();
            if (componentCount < 0 || componentCount > MAX_COMPONENTS) {
                throw new IllegalArgumentException("Invalid infusion component count: " + componentCount);
            }
            ArrayList<Ingredient> components = new ArrayList<>(componentCount);
            for (int index = 0; index < componentCount; index++) {
                components.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }
            int aspectCount = buffer.readVarInt();
            if (aspectCount < 0 || aspectCount > MAX_ASPECTS) {
                throw new IllegalArgumentException("Invalid infusion aspect count: " + aspectCount);
            }
            ArrayList<TCCrucibleAspectCost> aspects = new ArrayList<>(aspectCount);
            for (int index = 0; index < aspectCount; index++) {
                aspects.add(new TCCrucibleAspectCost(buffer.readUtf(), buffer.readVarInt()));
            }
            return new TCInfusionRecipe(group, research, instability, catalyst, List.copyOf(components), List.copyOf(aspects), result);
        }
    }
}