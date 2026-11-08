package thaumcraft.common.crafting.arcane;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCRecipes;

public class TCVoidJarArcaneRecipe extends TCShapedArcaneRecipe {
    public TCVoidJarArcaneRecipe(
            String group,
            String research,
            int vis,
            List<TCArcaneCrystalCost> crystalCosts,
            ShapedRecipePattern pattern,
            ItemStack result
    ) {
        super(group, research, vis, crystalCosts, pattern, result);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack out = super.assemble(input, registries);
        for (int i = 0; i < input.size(); i++) {
            ItemStack in = input.getItem(i);
            if (!in.isEmpty() && (in.is(TCBlocks.JAR_NORMAL.get().asItem())
                    || in.has(TCDataComponents.ASPECT_STACK.get())
                    || in.has(TCDataComponents.ASPECT_FILTER.get()))) {
                if (in.has(TCDataComponents.ASPECT_STACK.get())) {
                    out.set(TCDataComponents.ASPECT_STACK.get(), in.get(TCDataComponents.ASPECT_STACK.get()));
                }
                if (in.has(TCDataComponents.ASPECT_FILTER.get())) {
                    out.set(TCDataComponents.ASPECT_FILTER.get(), in.get(TCDataComponents.ASPECT_FILTER.get()));
                }
                break;
            }
        }
        return out;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipes.ARCANE_SHAPED_VOID_JAR_SERIALIZER.get();
    }

    public static final class Serializer implements RecipeSerializer<TCVoidJarArcaneRecipe> {
        private static final MapCodec<TCVoidJarArcaneRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(TCVoidJarArcaneRecipe::getGroup),
                Codec.STRING.fieldOf("research").forGetter(TCVoidJarArcaneRecipe::getResearch),
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("vis").forGetter(TCVoidJarArcaneRecipe::getVis),
                TCArcaneCrystalCost.LIST_CODEC.optionalFieldOf("crystals", List.of())
                        .forGetter(TCVoidJarArcaneRecipe::crystalCosts),
                ShapedRecipePattern.MAP_CODEC.forGetter(TCVoidJarArcaneRecipe::pattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TCVoidJarArcaneRecipe::result)
        ).apply(instance, TCVoidJarArcaneRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TCVoidJarArcaneRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<TCVoidJarArcaneRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TCVoidJarArcaneRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TCVoidJarArcaneRecipe recipe) {
            TCArcaneRecipe.writeCommon(buffer, recipe);
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern());
        }

        private static TCVoidJarArcaneRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            TCArcaneRecipe.CommonFields fields = TCArcaneRecipe.readCommon(buffer);
            ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            return new TCVoidJarArcaneRecipe(
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
