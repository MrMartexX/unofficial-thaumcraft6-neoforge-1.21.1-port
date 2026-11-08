package thaumcraft.common.crafting.infusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import thaumcraft.common.crafting.crucible.TCCrucibleAspectCost;
import thaumcraft.common.items.tools.TCEnumInfusionEnchantment;
import thaumcraft.common.registry.TCRecipes;

public class TCInfusionEnchantmentRecipe extends TCInfusionRecipe {
    private final TCEnumInfusionEnchantment enchantment;

    public TCInfusionEnchantmentRecipe(
            String group,
            String research,
            int instability,
            TCEnumInfusionEnchantment enchantment,
            List<Ingredient> components,
            List<TCCrucibleAspectCost> aspectCosts
    ) {
        super(group, research, instability, Ingredient.EMPTY, components, aspectCosts, ItemStack.EMPTY, true);
        this.enchantment = enchantment;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public TCEnumInfusionEnchantment enchantment() {
        return enchantment;
    }

    @Override
    public boolean matchesCatalyst(ItemStack centralItem) {
        if (centralItem == null || centralItem.isEmpty() || enchantment == null) {
            return false;
        }
        if (!TCEnumInfusionEnchantment.matchesToolClass(centralItem, enchantment)) {
            return false;
        }
        int currentLevel = TCEnumInfusionEnchantment.getInfusionEnchantmentLevel(centralItem, enchantment);
        return currentLevel < enchantment.maxLevel;
    }

    @Override
    public int resolveInstability(ItemStack centralItem) {
        int currentLevel = centralItem == null ? 0 : TCEnumInfusionEnchantment.getInfusionEnchantmentLevel(centralItem, enchantment);
        return instability() + currentLevel * 2;
    }

    @Override
    public List<TCCrucibleAspectCost> resolveAspectCosts(ItemStack centralItem) {
        int currentLevel = centralItem == null ? 0 : TCEnumInfusionEnchantment.getInfusionEnchantmentLevel(centralItem, enchantment);
        int multiplier = currentLevel + 1;
        List<TCCrucibleAspectCost> scaled = new ArrayList<>(aspectCosts.size());
        for (TCCrucibleAspectCost cost : aspectCosts) {
            scaled.add(new TCCrucibleAspectCost(cost.aspect(), cost.amount() * multiplier));
        }
        return scaled;
    }

    @Override
    public ItemStack resolveResult(ItemStack centralItem) {
        if (centralItem == null || centralItem.isEmpty() || enchantment == null) {
            return ItemStack.EMPTY;
        }
        ItemStack out = centralItem.copy();
        int currentLevel = TCEnumInfusionEnchantment.getInfusionEnchantmentLevel(centralItem, enchantment);
        TCEnumInfusionEnchantment.addInfusionEnchantment(out, enchantment, currentLevel + 1);
        return out;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TCRecipes.INFUSION_ENCHANTMENT_SERIALIZER.get();
    }

    public static final class Serializer implements RecipeSerializer<TCInfusionEnchantmentRecipe> {
        private static final MapCodec<TCInfusionEnchantmentRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(TCInfusionRecipe::getGroup),
                Codec.STRING.fieldOf("research").forGetter(TCInfusionRecipe::getResearch),
                Codec.INT.optionalFieldOf("instability", 0).forGetter(TCInfusionRecipe::instability),
                Codec.STRING.xmap(
                        name -> {
                            for (TCEnumInfusionEnchantment ie : TCEnumInfusionEnchantment.values()) {
                                if (ie.id.equalsIgnoreCase(name) || ie.name().equalsIgnoreCase(name)) {
                                    return ie;
                                }
                            }
                            return TCEnumInfusionEnchantment.valueOf(name);
                        },
                        TCEnumInfusionEnchantment::name
                ).fieldOf("enchantment").forGetter(TCInfusionEnchantmentRecipe::enchantment),
                Ingredient.CODEC_NONEMPTY.listOf().optionalFieldOf("components", List.of()).forGetter(TCInfusionRecipe::components),
                TCCrucibleAspectCost.LIST_CODEC.optionalFieldOf("aspects", List.of())
                        .forGetter(TCInfusionRecipe::aspectCosts)
        ).apply(instance, TCInfusionEnchantmentRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TCInfusionEnchantmentRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        @Override
        public MapCodec<TCInfusionEnchantmentRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TCInfusionEnchantmentRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, TCInfusionEnchantmentRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeUtf(recipe.getResearch());
            buffer.writeVarInt(recipe.instability());
            buffer.writeUtf(recipe.enchantment().name());
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

        private static TCInfusionEnchantmentRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            String research = buffer.readUtf();
            int instability = buffer.readVarInt();
            String enchName = buffer.readUtf();
            TCEnumInfusionEnchantment ench = null;
            for (TCEnumInfusionEnchantment ie : TCEnumInfusionEnchantment.values()) {
                if (ie.id.equalsIgnoreCase(enchName) || ie.name().equalsIgnoreCase(enchName)) {
                    ench = ie;
                    break;
                }
            }
            if (ench == null) {
                ench = TCEnumInfusionEnchantment.valueOf(enchName);
            }
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
            return new TCInfusionEnchantmentRecipe(group, research, instability, ench, List.copyOf(components), List.copyOf(aspects));
        }
    }
}
