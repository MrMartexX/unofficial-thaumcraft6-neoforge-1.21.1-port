package thaumcraft.common.crafting.infusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import thaumcraft.common.crafting.crucible.TCCrucibleAspectCost;
import thaumcraft.common.items.curios.TCBaubleItem;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCRecipes;

public class TCRunicAugmentInfusionRecipe extends TCInfusionRecipe {
    public TCRunicAugmentInfusionRecipe(
            String group,
            String research,
            int instability,
            Ingredient catalyst,
            List<Ingredient> components,
            List<TCCrucibleAspectCost> aspectCosts,
            ItemStack result
    ) {
        super(group, research, instability, catalyst, components, aspectCosts, result, true);
    }

    @Override
    public boolean matchesCatalyst(ItemStack centralItem) {
        if (centralItem == null || centralItem.isEmpty()) {
            return false;
        }
        var item = centralItem.getItem();
        return item instanceof ArmorItem
                || item instanceof TCBaubleItem
                || centralItem.is(ItemTags.HEAD_ARMOR)
                || centralItem.is(ItemTags.CHEST_ARMOR)
                || centralItem.is(ItemTags.LEG_ARMOR)
                || centralItem.is(ItemTags.FOOT_ARMOR);
    }

    @Override
    public int resolveInstability(ItemStack centralItem) {
        int currentLevel = centralItem == null ? 0 : centralItem.getOrDefault(TCDataComponents.RUNIC_CHARGE.get(), 0);
        return Math.max(instability(), (currentLevel + 1) * 2);
    }

    @Override
    public List<TCCrucibleAspectCost> resolveAspectCosts(ItemStack centralItem) {
        int currentLevel = centralItem == null ? 0 : centralItem.getOrDefault(TCDataComponents.RUNIC_CHARGE.get(), 0);
        int multiplier = currentLevel + 1;
        List<TCCrucibleAspectCost> scaled = new ArrayList<>(aspectCosts.size());
        for (TCCrucibleAspectCost cost : aspectCosts) {
            scaled.add(new TCCrucibleAspectCost(cost.aspect(), cost.amount() * multiplier));
        }
        return scaled;
    }

    @Override
    public ItemStack resolveResult(ItemStack centralItem) {
        if (centralItem == null || centralItem.isEmpty()) {
            return result().copy();
        }
        ItemStack out = centralItem.copy();
        int currentLevel = centralItem.getOrDefault(TCDataComponents.RUNIC_CHARGE.get(), 0);
        out.set(TCDataComponents.RUNIC_CHARGE.get(), currentLevel + 1);
        return out;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipes.RUNIC_AUGMENT_INFUSION_SERIALIZER.get();
    }

    public static final class Serializer implements RecipeSerializer<TCRunicAugmentInfusionRecipe> {
        private static final MapCodec<TCRunicAugmentInfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(TCInfusionRecipe::getGroup),
                Codec.STRING.fieldOf("research").forGetter(TCInfusionRecipe::getResearch),
                Codec.INT.optionalFieldOf("instability", 0).forGetter(TCInfusionRecipe::instability),
                Ingredient.CODEC_NONEMPTY.fieldOf("catalyst").forGetter(TCInfusionRecipe::catalyst),
                Ingredient.CODEC_NONEMPTY.listOf().optionalFieldOf("components", List.of()).forGetter(TCInfusionRecipe::components),
                TCCrucibleAspectCost.LIST_CODEC.optionalFieldOf("aspects", List.of())
                        .forGetter(TCInfusionRecipe::aspectCosts),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TCInfusionRecipe::result)
        ).apply(instance, TCRunicAugmentInfusionRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TCRunicAugmentInfusionRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<TCRunicAugmentInfusionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TCRunicAugmentInfusionRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TCRunicAugmentInfusionRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeUtf(recipe.getResearch());
            buffer.writeVarInt(recipe.instability());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.catalyst());
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result());
            buffer.writeVarInt(recipe.components().size());
            for (Ingredient component : recipe.components()) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, component);
            }
            buffer.writeVarInt(recipe.aspectCosts().size());
            for (TCCrucibleAspectCost cost : recipe.aspectCosts()) {
                buffer.writeUtf(cost.aspect());
                buffer.writeVarInt(cost.amount());
            }
        }

        private static TCRunicAugmentInfusionRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            String research = buffer.readUtf();
            int instability = buffer.readVarInt();
            Ingredient catalyst = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            int componentCount = buffer.readVarInt();
            ArrayList<Ingredient> components = new ArrayList<>(componentCount);
            for (int i = 0; i < componentCount; i++) {
                components.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }
            int aspectCount = buffer.readVarInt();
            ArrayList<TCCrucibleAspectCost> aspects = new ArrayList<>(aspectCount);
            for (int i = 0; i < aspectCount; i++) {
                aspects.add(new TCCrucibleAspectCost(buffer.readUtf(), buffer.readVarInt()));
            }
            return new TCRunicAugmentInfusionRecipe(group, research, instability, catalyst, List.copyOf(components), List.copyOf(aspects), result);
        }
    }
}
