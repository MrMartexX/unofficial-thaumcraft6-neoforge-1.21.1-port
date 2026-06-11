package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.crafting.arcane.TCArcaneRecipe;
import thaumcraft.common.crafting.arcane.TCShapedArcaneRecipe;
import thaumcraft.common.crafting.arcane.TCShapelessArcaneRecipe;

public final class TCRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Thaumcraft.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Thaumcraft.MODID);

    public static final Supplier<RecipeType<TCArcaneRecipe>> ARCANE_TYPE = RECIPE_TYPES.register(
            "arcane",
            () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "arcane"))
    );

    public static final Supplier<RecipeSerializer<TCShapedArcaneRecipe>> ARCANE_SHAPED_SERIALIZER =
            RECIPE_SERIALIZERS.register("arcane_shaped", TCShapedArcaneRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<TCShapelessArcaneRecipe>> ARCANE_SHAPELESS_SERIALIZER =
            RECIPE_SERIALIZERS.register("arcane_shapeless", TCShapelessArcaneRecipe.Serializer::new);

    private TCRecipes() {
    }
}
