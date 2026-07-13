package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menu.TCPechMenu;

public final class TCPechScreen extends AbstractContainerScreen<TCPechMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_pech.png");
    private static final int BUTTON_X = 67;
    private static final int BUTTON_Y = 24;
    private static final int BUTTON_W = 25;
    private static final int BUTTON_H = 25;

    public TCPechScreen(TCPechMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 175;
        imageHeight = 232;
        titleLabelY = -1000;
        inventoryLabelY = -1000;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        if (menu.canGenerateTrade()) {
            graphics.blit(BACKGROUND, leftPos + BUTTON_X, topPos + BUTTON_Y, 176, 0, BUTTON_W, BUTTON_H, 256, 256);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = leftPos + BUTTON_X;
        int y = topPos + BUTTON_Y;
        if (button == 0
                && menu.canGenerateTrade()
                && mouseX >= x
                && mouseY >= y
                && mouseX < x + BUTTON_W
                && mouseY < y + BUTTON_H) {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
