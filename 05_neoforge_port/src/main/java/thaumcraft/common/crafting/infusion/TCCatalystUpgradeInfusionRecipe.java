package thaumcraft.common.crafting.infusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import thaumcraft.common.crafting.crucible.TCCrucibleAspectCost;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCRecipes;

public class TCCatalystUpgradeInfusionRecipe extends TCInfusionRecipe {
    public TCCatalystUpgradeInfusionRecipe(
            String group,
            String research,
            int instability,
            Ingredient catalyst,
            List<Ingredient> components,
            List<TCCrucibleAspectCost> aspectCosts,
            ItemStack result
    ) {
        super(group, research, instability, catalyst, components, aspectCosts, result);
    }

    @Override
    public ItemStack resolveResult(ItemStack centralItem) {
        ItemStack out = result().copy();
        if (centralItem != null && !centralItem.isEmpty()) {
            if (centralItem.isDamageableItem() && out.isDamageableItem()) {
                out.setDamageValue(centralItem.getDamageValue());
            }
            if (centralItem.has(DataComponents.CUSTOM_NAME)) {
                out.set(DataComponents.CUSTOM_NAME, centralItem.get(DataComponents.CUSTOM_NAME));
            }
            if (centralItem.has(DataComponents.REPAIR_COST)) {
                out.set(DataComponents.REPAIR_COST, centralItem.get(DataComponents.REPAIR_COST));
            }
            if (centralItem.has(DataComponents.ENCHANTMENTS)) {
                out.set(DataComponents.ENCHANTMENTS, centralItem.get(DataComponents.ENCHANTMENTS));
            }
            if (centralItem.has(TCDataComponents.INFUSION_ENCHANTS.get())) {
                out.set(TCDataComponents.INFUSION_ENCHANTS.get(), centralItem.get(TCDataComponents.INFUSION_ENCHANTS.get()));
            }
            if (centralItem.has(TCDataComponents.RUNIC_CHARGE.get())) {
                out.set(TCDataComponents.RUNIC_CHARGE.get(), centralItem.get(TCDataComponents.RUNIC_CHARGE.get()));
            }
            if (centralItem.has(TCDataComponents.FORTRESS_MASK.get()) && !out.has(TCDataComponents.FORTRESS_MASK.get())) {
                out.set(TCDataComponents.FORTRESS_MASK.get(), centralItem.get(TCDataComponents.FORTRESS_MASK.get()));
            }
            if (centralItem.has(TCDataComponents.FORTRESS_GOGGLES.get()) && !out.has(TCDataComponents.FORTRESS_GOGGLES.get())) {
                out.set(TCDataComponents.FORTRESS_GOGGLES.get(), centralItem.get(TCDataComponents.FORTRESS_GOGGLES.get()));
            }
            if (centralItem.has(DataComponents.CUSTOM_DATA)) {
                out.set(DataComponents.CUSTOM_DATA, centralItem.get(DataComponents.CUSTOM_DATA));
            }
        }
        return out;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipes.CATALYST_UPGRADE_INFUSION_SERIALIZER.get();
    }

    public static final class Serializer implements RecipeSerializer<TCCatalystUpgradeInfusionRecipe> {
        private static final MapCodec<TCCatalystUpgradeInfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(TCInfusionRecipe::getGroup),
                Codec.STRING.fieldOf("research").forGetter(TCInfusionRecipe::getResearch),
                Codec.INT.optionalFieldOf("instability", 0).forGetter(TCInfusionRecipe::instability),
                Ingredient.CODEC_NONEMPTY.fieldOf("catalyst").forGetter(TCInfusionRecipe::catalyst),
                Ingredient.CODEC_NONEMPTY.listOf().optionalFieldOf("components", List.of()).forGetter(TCInfusionRecipe::components),
                TCCrucibleAspectCost.LIST_CODEC.optionalFieldOf("aspects", List.of())
                        .forGetter(TCInfusionRecipe::aspectCosts),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TCInfusionRecipe::result)
        ).apply(instance, TCCatalystUpgradeInfusionRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TCCatalystUpgradeInfusionRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<TCCatalystUpgradeInfusionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TCCatalystUpgradeInfusionRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TCCatalystUpgradeInfusionRecipe recipe) {
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

        private static TCCatalystUpgradeInfusionRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
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
            return new TCCatalystUpgradeInfusionRecipe(group, research, instability, catalyst, List.copyOf(components), List.copyOf(aspects), result);
        }
    }
}
