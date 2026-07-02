package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menu.TCHandMirrorMenu;

/** Legacy 176x166 hand mirror GUI with selected-hotbar-slot overlay. */
public final class TCHandMirrorScreen extends AbstractContainerScreen<TCHandMirrorMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_handmirror.png");

    public TCHandMirrorScreen(TCHandMirrorMenu menu, Inventory inventory, Component title) {
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
        int selected = menu.selectedSlot();
        if (selected >= 0 && selected < 9) {
            graphics.blit(BACKGROUND, leftPos + 8 + selected * 18, topPos + 142, 240, 0, 16, 16, 256, 256);
        }
    }
}
