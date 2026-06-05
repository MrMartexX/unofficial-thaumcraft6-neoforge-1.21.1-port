package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menu.TCArcaneWorkbenchMenu;
import thaumcraft.common.crafting.arcane.TCArcaneWorkbenchCrafting;

public class TCArcaneWorkbenchScreen extends AbstractContainerScreen<TCArcaneWorkbenchMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/arcaneworkbench.png");
    private static final int LEGACY_AVAILABLE_OK_COLOR = 0x6E6EEE;
    private static final int LEGACY_AVAILABLE_MISSING_COLOR = 0xEE6E6E;
    private static final int LEGACY_COST_COLOR = 0xC0FFFF;

    public TCArcaneWorkbenchScreen(TCArcaneWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 190;
        imageHeight = 234;
        titleLabelX = 0;
        titleLabelY = -1000;
        inventoryLabelX = 0;
        inventoryLabelY = -1000;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        renderRequiredCrystalGlows(guiGraphics, partialTick);
        renderVisFeedback(guiGraphics);
    }

    private void renderRequiredCrystalGlows(GuiGraphics guiGraphics, float partialTick) {
        if (menu.requiredCrystalMask() == 0) {
            return;
        }

        RenderSystem.enableBlend();
        for (int index = 0; index < TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.size(); index++) {
            if (!menu.isCrystalRequired(index)) {
                continue;
            }
            Aspect aspect = Aspect.getAspect(TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.get(index));
            if (aspect == null) {
                continue;
            }
            int color = aspect.getColor();
            float red = ((color >> 16) & 0xFF) / 255.0F;
            float green = ((color >> 8) & 0xFF) / 255.0F;
            float blue = (color & 0xFF) / 255.0F;
            float alpha = menu.hasCrystals() ? 0.33F : 0.50F;
            int tick = minecraft == null || minecraft.player == null ? 0 : minecraft.player.tickCount;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(
                    leftPos + TCArcaneWorkbenchMenu.CRYSTAL_X[index] + 7.5F,
                    topPos + TCArcaneWorkbenchMenu.CRYSTAL_Y[index] + 8.0F,
                    50.0F
            );
            guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(index * 60.0F + tick + partialTick));
            guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
            guiGraphics.setColor(red, green, blue, alpha);
            guiGraphics.blit(BACKGROUND, -32, -32, 192, 0, 64, 64, 256, 256);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.pose().popPose();
        }
    }

    private void renderVisFeedback(GuiGraphics guiGraphics) {
        Component available = Component.translatable(
                "gui.thaumcraft.arcane_workbench.available",
                menu.availableVis(),
                Component.translatable("workbench.available")
        );
        drawCenteredHalfScale(
                guiGraphics,
                available.getString(),
                leftPos + 168,
                topPos + 46,
                menu.availableVis() < menu.visCost() ? LEGACY_AVAILABLE_MISSING_COLOR : LEGACY_AVAILABLE_OK_COLOR
        );

        if (menu.visCost() > 0) {
            Component cost = Component.translatable(
                    "gui.thaumcraft.arcane_workbench.cost",
                    menu.visCost(),
                    Component.translatable("workbench.cost")
            );
            drawCenteredHalfScale(guiGraphics, cost.getString(), leftPos + 168, topPos + 38, LEGACY_COST_COLOR);
        }
    }

    private void drawCenteredHalfScale(GuiGraphics guiGraphics, String text, int x, int y, int color) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 100.0F);
        guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
        guiGraphics.drawString(font, text, -font.width(text) / 2, 0, color, false);
        guiGraphics.pose().popPose();
    }
}
