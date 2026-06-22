package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menu.TCSmelterMenu;

/** TC6 smelter screen using the original 176x166 texture and gauge coordinates. */
public final class TCSmelterScreen extends AbstractContainerScreen<TCSmelterMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_smelter.png");

    public TCSmelterScreen(TCSmelterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
        titleLabelY = -1000;
        inventoryLabelY = -1000;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        int burn = menu.burnTimeRemainingScaled(20);
        if (burn > 0) {
            graphics.blit(BACKGROUND, leftPos + 80, topPos + 46 - burn, 176, 20 - burn, 16, burn, 256, 256);
        }

        int cook = menu.cookProgressScaled(46);
        if (cook > 0) {
            graphics.blit(BACKGROUND, leftPos + 106, topPos + 59 - cook, 216, 46 - cook, 9, cook, 256, 256);
        }

        int vis = menu.visScaled(48);
        if (vis > 0) {
            graphics.blit(BACKGROUND, leftPos + 61, topPos + 60 - vis, 200, 48 - vis, 8, vis, 256, 256);
        }
        graphics.blit(BACKGROUND, leftPos + 60, topPos + 8, 232, 0, 10, 55, 256, 256);
    }
}
