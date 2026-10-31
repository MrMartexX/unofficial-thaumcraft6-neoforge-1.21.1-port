package thaumcraft.client.integration.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.integration.jei.ThaumcraftJeiPlugin;
import thaumcraft.common.crafting.arcane.TCArcaneCrystalCost;
import thaumcraft.common.crafting.arcane.TCArcaneRecipe;
import thaumcraft.common.items.TCAspectVariantStacks;
import thaumcraft.common.registry.TCBlocks;

import java.util.List;

public final class ArcaneWorkbenchRecipeCategory implements IRecipeCategory<TCArcaneRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public ArcaneWorkbenchRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 100);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(TCBlocks.ARCANE_WORKBENCH.get()));
        this.arrow = guiHelper.createAnimatedRecipeArrow(20);
    }

    @Override
    public RecipeType<TCArcaneRecipe> getRecipeType() {
        return ThaumcraftJeiPlugin.ARCANE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.thaumcraft.arcane_workbench");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public int getWidth() {
        return 160;
    }

    @Override
    public int getHeight() {
        return 100;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TCArcaneRecipe recipe, IFocusGroup focuses) {
        int columns = Math.max(1, Math.min(3, recipe.width()));
        List<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            int col = i % columns;
            int row = i / columns;
            builder.addInputSlot(10 + col * 18, 18 + row * 18)
                    .addIngredients(ingredients.get(i))
                    .setStandardSlotBackground();
        }

        builder.addOutputSlot(128, 36)
                .addItemStack(recipe.result())
                .setOutputSlotBackground();

        List<TCArcaneCrystalCost> crystals = recipe.crystalCosts();
        for (int i = 0; i < crystals.size() && i < 8; i++) {
            TCArcaneCrystalCost cost = crystals.get(i);
            Aspect aspect = Aspect.getAspect(cost.aspect());
            if (aspect != null) {
                ItemStack crystalStack = TCAspectVariantStacks.crystal(aspect);
                if (!crystalStack.isEmpty()) {
                    crystalStack.setCount(cost.amount());
                    builder.addInputSlot(10 + i * 18, 76)
                            .addItemStack(crystalStack)
                            .setStandardSlotBackground();
                }
            }
        }
    }

    @Override
    public void draw(TCArcaneRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, 92, 36);
        Minecraft mc = Minecraft.getInstance();
        graphics.drawString(mc.font, "Vis: " + recipe.getVis() + "  •  " + recipe.getResearch(), 10, 4, 0x665522, false);
    }
}
