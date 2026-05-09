package thaumcraft.client.gui;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

final class ClientAspectTooltipComponent implements ClientTooltipComponent {
    private static final int ICON_SIZE = 16;
    private static final int ICON_STEP = 18;
    private static final int TEXTURE_SIZE = 32;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int OUTLINE_COLOR = 0xFF000000;

    private final List<AspectTooltipComponent.Entry> entries;

    ClientAspectTooltipComponent(AspectTooltipComponent component) {
        this.entries = component.entries();
    }

    @Override
    public int getHeight() {
        return ICON_STEP;
    }

    @Override
    public int getWidth(Font font) {
        return entries.size() * ICON_STEP;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        int iconY = y + 2;
        for (int index = 0; index < entries.size(); index++) {
            AspectTooltipComponent.Entry entry = entries.get(index);
            int iconX = x + index * ICON_STEP;
            renderAspectIcon(graphics, entry, iconX, iconY);
            renderAmount(font, graphics, entry.amount(), iconX, iconY);
        }
    }

    private static void renderAspectIcon(GuiGraphics graphics, AspectTooltipComponent.Entry entry, int x, int y) {
        int color = entry.aspect().getColor();
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        graphics.setColor(red, green, blue, 1.0F);
        graphics.blit(
                entry.aspect().getImage(),
                x,
                y,
                ICON_SIZE,
                ICON_SIZE,
                0.0F,
                0.0F,
                TEXTURE_SIZE,
                TEXTURE_SIZE,
                TEXTURE_SIZE,
                TEXTURE_SIZE);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void renderAmount(Font font, GuiGraphics graphics, int amount, int x, int y) {
        String text = Integer.toString(amount);
        int textX = x * 2 + ICON_SIZE * 2 - font.width(text);
        int textY = y * 2 + ICON_SIZE * 2 - font.lineHeight;

        graphics.pose().pushPose();
        graphics.pose().scale(0.5F, 0.5F, 1.0F);
        graphics.drawString(font, text, textX - 1, textY, OUTLINE_COLOR, false);
        graphics.drawString(font, text, textX + 1, textY, OUTLINE_COLOR, false);
        graphics.drawString(font, text, textX, textY - 1, OUTLINE_COLOR, false);
        graphics.drawString(font, text, textX, textY + 1, OUTLINE_COLOR, false);
        graphics.drawString(font, text, textX, textY, TEXT_COLOR, false);
        graphics.pose().popPose();
    }
}
