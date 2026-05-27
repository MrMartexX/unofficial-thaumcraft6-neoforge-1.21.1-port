package thaumcraft.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menu.TCResearchTableMenu;

public class TCResearchTableScreen extends AbstractContainerScreen<TCResearchTableMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_research_table.png");

    public TCResearchTableScreen(TCResearchTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 255;
        imageHeight = 255;
        titleLabelX = 0;
        titleLabelY = -1000;
        inventoryLabelX = 0;
        inventoryLabelY = -1000;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
