package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.crafting.crucible.TCCrucibleAspectCost;
import thaumcraft.common.crafting.crucible.TCCrucibleRecipe;
import thaumcraft.common.crafting.crucible.TCThaumatoriumSelectRecipePayload;
import thaumcraft.common.menu.TCThaumatoriumMenu;

public final class TCThaumatoriumScreen extends AbstractContainerScreen<TCThaumatoriumMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_thaumatorium.png");
    private static final int LIST_X = 76;
    private static final int LIST_Y = 18;
    private static final int LIST_WIDTH = 92;
    private static final int ROW_HEIGHT = 20;
    private static final int VISIBLE_ROWS = 5;
    private static final int STATUS_X = 8;
    private static final int STATUS_Y = 108;

    private int recipeScroll;

    public TCThaumatoriumScreen(TCThaumatoriumMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 217;
        titleLabelY = -1000;
        inventoryLabelY = -1000;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderRecipePanel(graphics, mouseX, mouseY);
        renderHoveredTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            RecipeHolder<TCCrucibleRecipe> hovered = hoveredRecipe(mouseX, mouseY);
            if (hovered != null) {
                boolean selected = menu.isSelected(hovered.id());
                if (selected || menu.selectedRecipes().size() < menu.maxRecipes()) {
                    PacketDistributor.sendToServer(new TCThaumatoriumSelectRecipePayload(menu.blockPos(), hovered.id()));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        List<RecipeHolder<TCCrucibleRecipe>> recipes = menu.availableRecipesForClient();
        int maxScroll = Math.max(0, recipes.size() - VISIBLE_ROWS);
        if (maxScroll > 0 && inRecipeList(mouseX, mouseY)) {
            recipeScroll = Math.max(0, Math.min(maxScroll, recipeScroll - (int)Math.signum(scrollY)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void renderRecipePanel(GuiGraphics graphics, int mouseX, int mouseY) {
        List<RecipeHolder<TCCrucibleRecipe>> recipes = menu.availableRecipesForClient();
        clampScroll(recipes);
        int x = leftPos + LIST_X;
        int y = topPos + LIST_Y;

        graphics.fill(x - 2, y - 2, x + LIST_WIDTH + 2, y + ROW_HEIGHT * VISIBLE_ROWS + 2, 0x66000000);
        if (recipes.isEmpty()) {
            Component text = menu.getSlot(TCThaumatoriumMenu.SLOT_CATALYST).getItem().isEmpty()
                    ? Component.translatable("gui.thaumcraft.thaumatorium.insert_catalyst")
                    : Component.translatable("gui.thaumcraft.thaumatorium.no_recipes");
            graphics.drawString(font, trim(text.getString(), LIST_WIDTH - 6), x + 3, y + 9, 0xFFCCBB88, false);
        }

        int visible = Math.min(VISIBLE_ROWS, Math.max(0, recipes.size() - recipeScroll));
        for (int row = 0; row < visible; row++) {
            int index = recipeScroll + row;
            RecipeHolder<TCCrucibleRecipe> holder = recipes.get(index);
            int rowY = y + row * ROW_HEIGHT;
            boolean selected = menu.isSelected(holder.id());
            boolean current = selected && indexOfSelected(holder.id()) == menu.currentCraft();
            boolean hovered = mouseX >= x && mouseX < x + LIST_WIDTH && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
            int color = current ? 0xAA805018 : selected ? 0x80406018 : hovered ? 0x50302010 : 0x22000000;
            graphics.fill(x, rowY, x + LIST_WIDTH, rowY + ROW_HEIGHT - 1, color);
            ItemStack result = holder.value().result();
            graphics.renderItem(result, x + 2, rowY + 2);
            String name = trim(result.getHoverName().getString(), LIST_WIDTH - 28);
            graphics.drawString(font, name, x + 22, rowY + 3, selected ? 0xFFFFDD88 : 0xFFE6D9B8, false);
            String marker = selected ? Integer.toString(indexOfSelected(holder.id()) + 1) : "+";
            graphics.drawString(font, marker, x + LIST_WIDTH - font.width(marker) - 4, rowY + 11, 0xFFFFFFFF, true);
        }

        renderStatus(graphics);
    }

    private void renderStatus(GuiGraphics graphics) {
        int x = leftPos + STATUS_X;
        int y = topPos + STATUS_Y;
        graphics.drawString(font,
                Component.translatable("gui.thaumcraft.thaumatorium.capacity", menu.selectedRecipes().size(), menu.maxRecipes()),
                x,
                y,
                0xFFE6D9B8,
                false);
        graphics.drawString(font,
                menu.heated()
                        ? Component.translatable("gui.thaumcraft.thaumatorium.heated")
                        : Component.translatable("gui.thaumcraft.thaumatorium.cold"),
                x,
                y + 10,
                menu.heated() ? 0xFFFFAA44 : 0xFF8899AA,
                false);

        Aspect suction = menu.currentSuctionAspect();
        if (suction != null) {
            drawAspectIcon(graphics, suction, x + 102, y);
            int stored = menu.storedEssentia().getAmount(suction);
            graphics.drawString(font, Integer.toString(stored), x + 120, y + 4, 0xFFFFFFFF, true);
        }
    }

    private void renderHoveredTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        RecipeHolder<TCCrucibleRecipe> holder = hoveredRecipe(mouseX, mouseY);
        if (holder == null) {
            renderTooltip(graphics, mouseX, mouseY);
            return;
        }
        TCCrucibleRecipe recipe = holder.value();
        ArrayList<Component> lines = new ArrayList<>();
        lines.add(recipe.result().getHoverName());
        lines.add(Component.literal(holder.id().toString()).withStyle(ChatFormatting.DARK_GRAY));
        lines.add(Component.translatable("gui.thaumcraft.thaumatorium.research", recipe.getResearch())
                .withStyle(ChatFormatting.GRAY));
        if (menu.isSelected(holder.id())) {
            lines.add(Component.translatable("gui.thaumcraft.thaumatorium.selected").withStyle(ChatFormatting.GOLD));
        } else if (menu.selectedRecipes().size() >= menu.maxRecipes()) {
            lines.add(Component.translatable("gui.thaumcraft.thaumatorium.full").withStyle(ChatFormatting.RED));
        } else {
            lines.add(Component.translatable("gui.thaumcraft.thaumatorium.click_add").withStyle(ChatFormatting.YELLOW));
        }
        AspectList stored = menu.storedEssentia();
        for (TCCrucibleAspectCost cost : recipe.aspectCosts()) {
            Aspect aspect = cost.resolvedAspect();
            int current = stored.getAmount(aspect);
            lines.add(Component.translatable(
                    "gui.thaumcraft.thaumatorium.aspect_cost",
                    Component.translatable("tc.aspect." + aspect.getTag()).getString(),
                    current,
                    cost.amount()
            ).withStyle(current >= cost.amount() ? ChatFormatting.GREEN : ChatFormatting.GRAY));
        }
        graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
    }

    private RecipeHolder<TCCrucibleRecipe> hoveredRecipe(double mouseX, double mouseY) {
        if (!inRecipeList(mouseX, mouseY)) {
            return null;
        }
        List<RecipeHolder<TCCrucibleRecipe>> recipes = menu.availableRecipesForClient();
        clampScroll(recipes);
        int row = ((int)mouseY - (topPos + LIST_Y)) / ROW_HEIGHT;
        int index = recipeScroll + row;
        return row >= 0 && row < VISIBLE_ROWS && index >= 0 && index < recipes.size() ? recipes.get(index) : null;
    }

    private boolean inRecipeList(double mouseX, double mouseY) {
        int x = leftPos + LIST_X;
        int y = topPos + LIST_Y;
        return mouseX >= x && mouseX < x + LIST_WIDTH
                && mouseY >= y && mouseY < y + ROW_HEIGHT * VISIBLE_ROWS;
    }

    private void clampScroll(List<RecipeHolder<TCCrucibleRecipe>> recipes) {
        recipeScroll = Math.max(0, Math.min(recipeScroll, Math.max(0, recipes.size() - VISIBLE_ROWS)));
    }

    private int indexOfSelected(ResourceLocation id) {
        List<ResourceLocation> selected = menu.selectedRecipes();
        for (int index = 0; index < selected.size(); index++) {
            if (selected.get(index).equals(id)) {
                return index;
            }
        }
        return -1;
    }

    private String trim(String text, int width) {
        return font.plainSubstrByWidth(text == null ? "" : text, Math.max(1, width));
    }

    private static void drawAspectIcon(GuiGraphics graphics, Aspect aspect, int x, int y) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(0.5F, 0.5F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.blit(aspect.getImage(), 0, 0, 0.0F, 0.0F, 32, 32, 32, 32);
        graphics.pose().popPose();
    }
}
