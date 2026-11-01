package thaumcraft.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.construct.TCTurretCrossbowAdvancedEntity;
import thaumcraft.common.menu.TCTurretAdvancedMenu;

public class TCTurretAdvancedScreen extends AbstractContainerScreen<TCTurretAdvancedMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_turret_advanced.png");

    public TCTurretAdvancedScreen(TCTurretAdvancedMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 175;
        this.imageHeight = 232;
        this.inventoryLabelY = 72;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        TCTurretCrossbowAdvancedEntity turret = this.menu.getAdvancedTurret();
        if (turret != null && turret.getMaxHealth() > 0) {
            int h = (int) (39.0F * (turret.getHealth() / turret.getMaxHealth()));
            guiGraphics.blit(TEXTURE, x + 30, y + 59, 192, 48, h, 6, 256, 256);

            // Draw 4 toggle checkboxes
            drawToggle(guiGraphics, x + 90, y + 13, turret.getTargetAnimal(), Component.translatable("button.turretfocus.1"));
            drawToggle(guiGraphics, x + 90, y + 27, turret.getTargetMob(), Component.translatable("button.turretfocus.2"));
            drawToggle(guiGraphics, x + 90, y + 41, turret.getTargetPlayer(), Component.translatable("button.turretfocus.3"));
            drawToggle(guiGraphics, x + 90, y + 55, turret.getTargetFriendly(), Component.translatable("button.turretfocus.4"));
        }
    }

    private void drawToggle(GuiGraphics guiGraphics, int bx, int by, boolean checked, Component label) {
        guiGraphics.blit(TEXTURE, bx, by, 192, checked ? 24 : 16, 8, 8, 256, 256);
        guiGraphics.drawString(this.font, label, bx + 12, by, 0xFFFFFF, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.minecraft != null && this.minecraft.gameMode != null) {
            int x = (this.width - this.imageWidth) / 2;
            int y = (this.height - this.imageHeight) / 2;

            if (isInside(mouseX, mouseY, x + 90, y + 13, 80, 10)) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
                return true;
            }
            if (isInside(mouseX, mouseY, x + 90, y + 27, 80, 10)) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 2);
                return true;
            }
            if (isInside(mouseX, mouseY, x + 90, y + 41, 80, 10)) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 3);
                return true;
            }
            if (isInside(mouseX, mouseY, x + 90, y + 55, 80, 10)) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 4);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static boolean isInside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, (this.imageWidth - this.font.width(this.title)) / 2, 6, 0x404040, false);
    }
}
