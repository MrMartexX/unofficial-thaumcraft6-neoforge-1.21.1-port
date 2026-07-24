package thaumcraft.client.gui;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

final class TCLegacyTooltipRenderer {
    private static final int MAX_WIDTH = 240;
    private static final float TOOLTIP_Z = 1000.0F;

    private TCLegacyTooltipRenderer() {
    }

    static void render(
            GuiGraphics graphics,
            Font font,
            List<Line> lines,
            int x,
            int y,
            int screenWidth,
            int screenHeight
    ) {
        if (lines.isEmpty()) {
            return;
        }

        int widest = 0;
        int totalHeight = -2;
        for (Line line : lines) {
            int width = line.small()
                    ? (int) Math.ceil(font.width(line.text()) / 2.0D)
                    : font.width(line.text());
            widest = Math.max(widest, width);
            totalHeight += line.small() ? 7 : 10;
        }
        if (lines.size() > 1) {
            totalHeight += 2;
        }
        widest = Math.min(widest, MAX_WIDTH);

        int drawX = x + 12;
        int drawY = y - 12;
        if (drawY + totalHeight > screenHeight) {
            drawY = screenHeight - totalHeight - 5;
        }
        if (drawX + widest + 4 > screenWidth) {
            drawX = Math.max(4, x - widest - 12);
        }

        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, TOOLTIP_Z);
        graphics.fill(drawX - 3, drawY - 4, drawX + widest + 3, drawY - 3, 0xF0100010);
        graphics.fill(drawX - 3, drawY + totalHeight + 3, drawX + widest + 3, drawY + totalHeight + 4, 0xF0100010);
        graphics.fill(drawX - 3, drawY - 3, drawX + widest + 3, drawY + totalHeight + 3, 0xF0100010);
        graphics.fill(drawX - 4, drawY - 3, drawX - 3, drawY + totalHeight + 3, 0xF0100010);
        graphics.fill(drawX + widest + 3, drawY - 3, drawX + widest + 4, drawY + totalHeight + 3, 0xF0100010);
        graphics.fill(drawX - 3, drawY - 2, drawX - 2, drawY + totalHeight + 2, 0x505000FF);
        graphics.fill(drawX + widest + 2, drawY - 2, drawX + widest + 3, drawY + totalHeight + 2, 0x5028007F);
        graphics.fill(drawX - 3, drawY - 3, drawX + widest + 3, drawY - 2, 0x505000FF);
        graphics.fill(drawX - 3, drawY + totalHeight + 2, drawX + widest + 3, drawY + totalHeight + 3, 0x5028007F);

        int lineY = drawY;
        for (int index = 0; index < lines.size(); index++) {
            Line line = lines.get(index);
            if (line.small()) {
                graphics.pose().pushPose();
                graphics.pose().translate(drawX, lineY, 1.0F);
                graphics.pose().scale(0.5F, 0.5F, 1.0F);
                graphics.drawString(font, line.text(), 0, 3, line.color(), true);
                graphics.pose().popPose();
                lineY += 7;
            } else {
                graphics.drawString(font, line.text(), drawX, lineY, line.color(), true);
                lineY += 10;
            }
            if (index == 0) {
                lineY += 2;
            }
        }
        graphics.pose().popPose();
    }

    record Line(String text, int color, boolean small) {
        static Line normal(String text, int color) {
            return new Line(text, color, false);
        }

        static Line small(String text, int color) {
            return new Line(text, color, true);
        }
    }
}
