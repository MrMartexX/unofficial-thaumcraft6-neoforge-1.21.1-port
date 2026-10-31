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
import net.minecraft.world.item.crafting.Ingredient;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.integration.jei.ThaumcraftJeiPlugin;
import thaumcraft.common.crafting.crucible.TCCrucibleAspectCost;
import thaumcraft.common.crafting.infusion.TCInfusionRecipe;
import thaumcraft.common.items.TCAspectVariantStacks;
import thaumcraft.common.registry.TCBlocks;

import java.util.List;

public final class InfusionRecipeCategory implements IRecipeCategory<TCInfusionRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public InfusionRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(190, 140);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(TCBlocks.INFUSION_MATRIX.get()));
        this.arrow = guiHelper.createAnimatedRecipeArrow(20);
    }

    @Override
    public RecipeType<TCInfusionRecipe> getRecipeType() {
        return ThaumcraftJeiPlugin.INFUSION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.thaumcraft.infusion_matrix");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public int getWidth() {
        return 190;
    }

    @Override
    public int getHeight() {
        return 140;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TCInfusionRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(75, 52)
                .addIngredients(recipe.catalyst())
                .setStandardSlotBackground();

        List<Ingredient> components = recipe.components();
        int visible = Math.min(12, components.size());
        for (int i = 0; i < visible; i++) {
            double angle = -Math.PI / 2.0D + i * (Math.PI * 2.0D / Math.max(1, visible));
            int x = 75 + (int) Math.round(Math.cos(angle) * 44.0D);
            int y = 52 + (int) Math.round(Math.sin(angle) * 36.0D);
            builder.addInputSlot(x, y)
                    .addIngredients(components.get(i))
                    .setStandardSlotBackground();
        }

        builder.addOutputSlot(155, 52)
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
    public void draw(TCInfusionRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, 124, 52);
        Minecraft mc = Minecraft.getInstance();
        graphics.drawString(mc.font, "Instability: " + recipe.instability() + "  •  " + recipe.getResearch(), 6, 4, 0x665522, false);

        List<TCCrucibleAspectCost> costs = recipe.aspectCosts();
        for (int i = 0; i < costs.size() && i < 9; i++) {
            TCCrucibleAspectCost cost = costs.get(i);
            Aspect aspect = Aspect.getAspect(cost.aspect());
            if (aspect != null) {
                int x = 6 + i * 20;
                int y = 118;
                graphics.blit(aspect.getImage(), x, y, 0, 0, 16, 16, 16, 16);
                graphics.drawString(mc.font, Integer.toString(cost.amount()), x + 11, y + 9, 0xFFFFFF, true);
            }
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, TCInfusionRecipe recipe, IRecipeSlotsView slotsView, double mouseX, double mouseY) {
        List<TCCrucibleAspectCost> costs = recipe.aspectCosts();
        for (int i = 0; i < costs.size() && i < 9; i++) {
            int x = 6 + i * 20;
            int y = 118;
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                Aspect aspect = Aspect.getAspect(costs.get(i).aspect());
                if (aspect != null) {
                    tooltip.add(Component.literal(aspect.getLocalizedDescription() + " × " + costs.get(i).amount()));
                }
            }
        }
    }
}
