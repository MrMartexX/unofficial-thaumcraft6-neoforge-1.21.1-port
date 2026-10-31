package thaumcraft.client.integration.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.integration.jei.ThaumcraftJeiPlugin;
import thaumcraft.common.crafting.crucible.TCCrucibleAspectCost;
import thaumcraft.common.crafting.crucible.TCCrucibleRecipe;
import thaumcraft.common.items.TCAspectVariantStacks;
import thaumcraft.common.registry.TCBlocks;

import java.util.List;

public final class CrucibleRecipeCategory implements IRecipeCategory<TCCrucibleRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public CrucibleRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(150, 78);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(TCBlocks.CRUCIBLE.get()));
        this.arrow = guiHelper.createAnimatedRecipeArrow(20);
    }

    @Override
    public RecipeType<TCCrucibleRecipe> getRecipeType() {
        return ThaumcraftJeiPlugin.CRUCIBLE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.thaumcraft.crucible");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public int getWidth() {
        return 150;
    }

    @Override
    public int getHeight() {
        return 78;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TCCrucibleRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(16, 24)
                .addIngredients(recipe.catalyst())
                .setStandardSlotBackground();

        builder.addOutputSlot(116, 24)
                .addItemStack(recipe.result())
                .setOutputSlotBackground();

        for (TCCrucibleAspectCost cost : recipe.aspectCosts()) {
            Aspect aspect = Aspect.getAspect(cost.aspect());
            if (aspect != null) {
                ItemStack crystal = TCAspectVariantStacks.crystal(aspect);
                if (!crystal.isEmpty()) {
                    crystal.setCount(cost.amount());
                    builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(crystal);
                }
            }
        }
    }

    @Override
    public void draw(TCCrucibleRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, 62, 24);
        Minecraft mc = Minecraft.getInstance();
        graphics.drawString(mc.font, recipe.getResearch(), 10, 4, 0x665522, false);

        List<TCCrucibleAspectCost> costs = recipe.aspectCosts();
        for (int i = 0; i < costs.size() && i < 7; i++) {
            TCCrucibleAspectCost cost = costs.get(i);
            Aspect aspect = Aspect.getAspect(cost.aspect());
            if (aspect != null) {
                int x = 10 + i * 20;
                int y = 52;
                graphics.blit(aspect.getImage(), x, y, 0, 0, 16, 16, 16, 16);
                graphics.drawString(mc.font, Integer.toString(cost.amount()), x + 11, y + 9, 0xFFFFFF, true);
            }
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, TCCrucibleRecipe recipe, IRecipeSlotsView slotsView, double mouseX, double mouseY) {
        List<TCCrucibleAspectCost> costs = recipe.aspectCosts();
        for (int i = 0; i < costs.size() && i < 7; i++) {
            int x = 10 + i * 20;
            int y = 52;
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                Aspect aspect = Aspect.getAspect(costs.get(i).aspect());
                if (aspect != null) {
                    tooltip.add(Component.literal(aspect.getLocalizedDescription() + " × " + costs.get(i).amount()));
                }
            }
        }
    }
}
