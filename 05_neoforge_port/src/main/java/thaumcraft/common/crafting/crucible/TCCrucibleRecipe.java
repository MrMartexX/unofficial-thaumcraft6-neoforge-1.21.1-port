package thaumcraft.common.crafting.crucible;

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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.registry.TCRecipes;

public final class TCCrucibleRecipe implements Recipe<SingleRecipeInput> {
    private static final int MAX_ASPECT_COSTS = 64;

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
        this.catalyst = catalyst;
        this.aspectCosts = List.copyOf(aspectCosts);
        this.result = result.copy();
        if (this.catalyst == null || this.catalyst.isEmpty()) {
            throw new IllegalArgumentException("Crucible recipe catalyst cannot be empty");
        }
        if (this.aspectCosts.isEmpty()) {
            throw new IllegalArgumentException("Crucible recipe aspect costs cannot be empty");
        }
        if (this.result.isEmpty()) {
            throw new IllegalArgumentException("Crucible recipe result cannot be empty");
        }
    }

    public Ingredient catalyst() {
        return catalyst;
    }

    public List<TCCrucibleAspectCost> aspectCosts() {
        return aspectCosts;
    }

    public AspectList aspects() {
        return TCCrucibleAspectCost.toAspectList(aspectCosts);
    }

    public ItemStack result() {
        return result.copy();
    }

    public String research() {
        return research;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return catalyst.test(input.item());
    }

    public boolean matchesCrucible(AspectList crucibleAspects, ItemStack catalystStack) {
        if (!catalyst.test(catalystStack) || crucibleAspects == null) {
            return false;
        }
        for (TCCrucibleAspectCost cost : aspectCosts) {
            if (crucibleAspects.getAmount(cost.resolvedAspect()) < cost.amount()) {
                return false;
            }
        }
        return true;
    }

    public AspectList removeMatchingAspects(AspectList crucibleAspects) {
        AspectList remaining = crucibleAspects == null ? new AspectList() : crucibleAspects.copy();
        for (TCCrucibleAspectCost cost : aspectCosts) {
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
        return NonNullList.of(Ingredient.EMPTY, catalyst);
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipes.CRUCIBLE_SERIALIZER.get();
    }

    @Override
    public net.minecraft.world.item.crafting.RecipeType<?> getType() {
        return TCRecipes.CRUCIBLE_TYPE.get();
    }

    static void writeCommon(RegistryFriendlyByteBuf buffer, TCCrucibleRecipe recipe) {
        buffer.writeUtf(recipe.getGroup());
        buffer.writeUtf(recipe.research());
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.catalyst());
        writeAspectCosts(buffer, recipe.aspectCosts());
        ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
    }

    static TCCrucibleRecipe readCommon(RegistryFriendlyByteBuf buffer) {
        return new TCCrucibleRecipe(
                buffer.readUtf(),
                buffer.readUtf(),
                Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                readAspectCosts(buffer),
                ItemStack.STREAM_CODEC.decode(buffer)
        );
    }

    private static void writeAspectCosts(RegistryFriendlyByteBuf buffer, List<TCCrucibleAspectCost> costs) {
        if (costs.size() > MAX_ASPECT_COSTS) {
            throw new IllegalArgumentException("Too many crucible aspect costs: " + costs.size());
        }
        buffer.writeVarInt(costs.size());
        for (TCCrucibleAspectCost cost : costs) {
            buffer.writeUtf(cost.aspect());
            buffer.writeVarInt(cost.amount());
        }
    }

    private static List<TCCrucibleAspectCost> readAspectCosts(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        if (size < 1 || size > MAX_ASPECT_COSTS) {
            throw new IllegalArgumentException("Invalid crucible aspect cost count: " + size);
        }
        ArrayList<TCCrucibleAspectCost> costs = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            costs.add(new TCCrucibleAspectCost(buffer.readUtf(), buffer.readVarInt()));
        }
        return List.copyOf(costs);
    }

    public static final class Serializer implements RecipeSerializer<TCCrucibleRecipe> {
        private static final MapCodec<TCCrucibleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(TCCrucibleRecipe::getGroup),
                Codec.STRING.optionalFieldOf("research", "").forGetter(TCCrucibleRecipe::research),
                Ingredient.CODEC_NONEMPTY.fieldOf("catalyst").forGetter(TCCrucibleRecipe::catalyst),
                TCCrucibleAspectCost.MAP_CODEC.fieldOf("aspects").forGetter(TCCrucibleRecipe::aspectCosts),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TCCrucibleRecipe::result)
        ).apply(instance, TCCrucibleRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TCCrucibleRecipe> STREAM_CODEC = StreamCodec.of(
                TCCrucibleRecipe::writeCommon,
                TCCrucibleRecipe::readCommon
        );

        @Override
        public MapCodec<TCCrucibleRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TCCrucibleRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
