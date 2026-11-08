package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.menu.TCPotionSprayerMenu;
import thaumcraft.common.tiles.devices.TCPotionSprayerBlockEntity;

/**
 * 1:1 legacy Potion Sprayer GUI screen.
 * Displays potion slot, animated color-tinted charges fluid tube, and essentia aspect requirements.
 */
public class TCPotionSprayerScreen extends AbstractContainerScreen<TCPotionSprayerMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/gui_potion_sprayer.png");

    public TCPotionSprayerScreen(TCPotionSprayerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 192;
        imageHeight = 233;
        titleLabelX = 16;
        titleLabelY = 16;
        inventoryLabelX = 16;
        inventoryLabelY = 139;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        renderCustomTooltips(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Base frame (192 x 233)
        graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int charges = menu.getCharges();
        if (charges > 0) {
            int tint = menu.getColor();
            float r = ((tint >> 16) & 0xFF) / 255.0F;
            float g = ((tint >> 8) & 0xFF) / 255.0F;
            float b = (tint & 0xFF) / 255.0F;
            graphics.setColor(r, g, b, 1.0F);

            int scroll = (int) ((minecraft != null && minecraft.player != null ? minecraft.player.tickCount : 0) % 256);
            int barHeight = charges * 9;
            int startY = topPos + 36 + (8 - charges) * 9;
            graphics.blit(BACKGROUND, leftPos + 128, startY, 232, scroll, 8, barHeight);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        // Render required aspect meters
        TCPotionSprayerBlockEntity sprayer = menu.getBlockEntity();
        if (sprayer != null) {
            drawAspectProgress(graphics, sprayer);
        }

        // Overlay glass frame for the charges meter
        graphics.blit(BACKGROUND, leftPos + 125, topPos + 28, 205, 28, 14, 88);

        RenderSystem.disableBlend();
    }

    private void drawAspectProgress(GuiGraphics graphics, TCPotionSprayerBlockEntity sprayer) {
        Map<String, Integer> required = sprayer.getRequiredAspects();
        Map<String, Integer> progress = sprayer.getProgressAspects();
        if (required.isEmpty()) {
            return;
        }

        int pos = 0;
        for (Map.Entry<String, Integer> entry : required.entrySet()) {
            String aspectTag = entry.getKey();
            int reqAmount = entry.getValue();
            int progAmount = progress.getOrDefault(aspectTag, 0);
            Aspect aspect = Aspect.getAspect(aspectTag);

            int barX = leftPos + 96 + 22 * (pos % 2);
            int barBottomY = topPos + 46 + 16 * (pos / 2);

            // Dark bar background (2 x 14)
            graphics.setColor(0.2F, 0.2F, 0.2F, 1.0F);
            graphics.blit(BACKGROUND, barX, barBottomY - 14, 192, 56, 2, 14);

            // Filled bar tinted by aspect color
            int fillHeight = reqAmount > 0 ? Math.min(14, Math.round((float) progAmount / reqAmount * 14.0F)) : 0;
            if (fillHeight > 0 && aspect != null) {
                int c = aspect.getColor();
                graphics.setColor(((c >> 16) & 0xFF) / 255.0F, ((c >> 8) & 0xFF) / 255.0F, (c & 0xFF) / 255.0F, 1.0F);
                graphics.blit(BACKGROUND, barX, barBottomY - fillHeight, 192, 56, 2, fillHeight);
            }
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

            // Aspect tag icon
            if (aspect != null) {
                int iconX = leftPos + 79 + 22 * (pos % 2);
                int iconY = topPos + 31 + 16 * (pos / 2);
                drawAspectIcon(graphics, aspect, iconX, iconY);
            }

            pos++;
        }
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

    private void renderCustomTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        // Charges meter tooltip (125..139, 28..116)
        if (mouseX >= leftPos + 125 && mouseX <= leftPos + 139 && mouseY >= topPos + 28 && mouseY <= topPos + 116) {
            int charges = menu.getCharges();
            List<Component> tooltip = List.of(
                    Component.translatable("tile.potion_sprayer.name"),
                    Component.literal("Charges: " + charges + " / 8")
            );
            graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }

        // Aspect icons tooltips
        TCPotionSprayerBlockEntity sprayer = menu.getBlockEntity();
        if (sprayer != null) {
            int pos = 0;
            for (Map.Entry<String, Integer> entry : sprayer.getRequiredAspects().entrySet()) {
                int iconX = leftPos + 79 + 22 * (pos % 2);
                int iconY = topPos + 31 + 16 * (pos / 2);
                if (mouseX >= iconX && mouseX <= iconX + 16 && mouseY >= iconY && mouseY <= iconY + 16) {
                    Aspect aspect = Aspect.getAspect(entry.getKey());
                    int prog = sprayer.getProgressAspects().getOrDefault(entry.getKey(), 0);
                    int req = entry.getValue();
                    String name = aspect != null ? aspect.getName() : entry.getKey();
                    List<Component> tip = List.of(
                            Component.literal(name),
                            Component.literal(prog + " / " + req)
                    );
                    graphics.renderComponentTooltip(font, tip, mouseX, mouseY);
                    break;
                }
                pos++;
            }
        }
    }
}
