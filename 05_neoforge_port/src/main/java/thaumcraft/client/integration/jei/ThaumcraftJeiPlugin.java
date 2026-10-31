package thaumcraft.client.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import thaumcraft.Thaumcraft;
import thaumcraft.client.gui.TCArcaneWorkbenchScreen;
import thaumcraft.client.integration.jei.category.ArcaneWorkbenchRecipeCategory;
import thaumcraft.client.integration.jei.category.CrucibleRecipeCategory;
import thaumcraft.client.integration.jei.category.InfusionRecipeCategory;
import thaumcraft.common.crafting.arcane.TCArcaneRecipe;
import thaumcraft.common.crafting.crucible.TCCrucibleRecipe;
import thaumcraft.common.crafting.infusion.TCInfusionRecipe;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCRecipes;

import java.util.List;

@JeiPlugin
public class ThaumcraftJeiPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_UID =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "jei_plugin");

    public static final RecipeType<TCArcaneRecipe> ARCANE =
            RecipeType.create(Thaumcraft.MODID, "arcane", TCArcaneRecipe.class);
    public static final RecipeType<TCCrucibleRecipe> CRUCIBLE =
            RecipeType.create(Thaumcraft.MODID, "crucible", TCCrucibleRecipe.class);
    public static final RecipeType<TCInfusionRecipe> INFUSION =
            RecipeType.create(Thaumcraft.MODID, "infusion", TCInfusionRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new ArcaneWorkbenchRecipeCategory(guiHelper),
                new CrucibleRecipeCategory(guiHelper),
                new InfusionRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        List<TCArcaneRecipe> arcaneRecipes = minecraft.level.getRecipeManager()
                .getAllRecipesFor(TCRecipes.ARCANE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(ARCANE, arcaneRecipes);

        List<TCCrucibleRecipe> crucibleRecipes = minecraft.level.getRecipeManager()
                .getAllRecipesFor(TCRecipes.CRUCIBLE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(CRUCIBLE, crucibleRecipes);

        List<TCInfusionRecipe> infusionRecipes = minecraft.level.getRecipeManager()
                .getAllRecipesFor(TCRecipes.INFUSION_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(INFUSION, infusionRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(TCBlocks.ARCANE_WORKBENCH.get()), ARCANE);
        registration.addRecipeCatalyst(new ItemStack(TCBlocks.CRUCIBLE.get()), CRUCIBLE);
        registration.addRecipeCatalyst(new ItemStack(TCBlocks.INFUSION_MATRIX.get()), INFUSION);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(TCArcaneWorkbenchScreen.class, 115, 60, 28, 20, ARCANE);
    }
}
