package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.crafting.TCSalisMundusRecipe;
import thaumcraft.common.crafting.TCTripleMeatTreatRecipe;
import thaumcraft.common.crafting.arcane.TCArcaneRecipe;
import thaumcraft.common.crafting.arcane.TCShapedArcaneRecipe;
import thaumcraft.common.crafting.arcane.TCShapelessArcaneRecipe;
import thaumcraft.common.crafting.arcane.TCVoidJarArcaneRecipe;
import thaumcraft.common.crafting.crucible.TCCrucibleRecipe;
import thaumcraft.common.crafting.infusion.TCCatalystUpgradeInfusionRecipe;
import thaumcraft.common.crafting.infusion.TCInfusionEnchantmentRecipe;
import thaumcraft.common.crafting.infusion.TCInfusionRecipe;
import thaumcraft.common.crafting.infusion.TCRunicAugmentInfusionRecipe;

public final class TCRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Thaumcraft.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Thaumcraft.MODID);

    public static final Supplier<RecipeType<TCArcaneRecipe>> ARCANE_TYPE = RECIPE_TYPES.register(
            "arcane",
            () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "arcane"))
    );
    public static final Supplier<RecipeType<TCCrucibleRecipe>> CRUCIBLE_TYPE = RECIPE_TYPES.register(
            "crucible",
            () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "crucible"))
    );
    public static final Supplier<RecipeType<TCInfusionRecipe>> INFUSION_TYPE = RECIPE_TYPES.register(
            "infusion",
            () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "infusion"))
    );

    public static final Supplier<RecipeSerializer<TCShapedArcaneRecipe>> ARCANE_SHAPED_SERIALIZER =
            RECIPE_SERIALIZERS.register("arcane_shaped", TCShapedArcaneRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<TCVoidJarArcaneRecipe>> ARCANE_SHAPED_VOID_JAR_SERIALIZER =
            RECIPE_SERIALIZERS.register("arcane_shaped_void_jar", TCVoidJarArcaneRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<TCShapelessArcaneRecipe>> ARCANE_SHAPELESS_SERIALIZER =
            RECIPE_SERIALIZERS.register("arcane_shapeless", TCShapelessArcaneRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<TCCrucibleRecipe>> CRUCIBLE_SERIALIZER =
            RECIPE_SERIALIZERS.register("crucible", TCCrucibleRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<TCInfusionRecipe>> INFUSION_SERIALIZER =
            RECIPE_SERIALIZERS.register("infusion", TCInfusionRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<TCCatalystUpgradeInfusionRecipe>> CATALYST_UPGRADE_INFUSION_SERIALIZER =
            RECIPE_SERIALIZERS.register("catalyst_upgrade_infusion", TCCatalystUpgradeInfusionRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<TCRunicAugmentInfusionRecipe>> RUNIC_AUGMENT_INFUSION_SERIALIZER =
            RECIPE_SERIALIZERS.register("runic_augment_infusion", TCRunicAugmentInfusionRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<TCInfusionEnchantmentRecipe>> INFUSION_ENCHANTMENT_SERIALIZER =
            RECIPE_SERIALIZERS.register("infusion_enchantment", TCInfusionEnchantmentRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<TCSalisMundusRecipe>> SALIS_MUNDUS_SERIALIZER =
            RECIPE_SERIALIZERS.register("salis_mundus", TCSalisMundusRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<TCTripleMeatTreatRecipe>> TRIPLE_MEAT_TREAT_SERIALIZER =
            RECIPE_SERIALIZERS.register("triple_meat_treat", () -> new SimpleCraftingRecipeSerializer<>(TCTripleMeatTreatRecipe::new));

    private TCRecipes() {
    }
}
