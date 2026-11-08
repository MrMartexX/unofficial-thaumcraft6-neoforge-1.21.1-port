package thaumcraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.research.TCResearchEntryDefinition;

/**
 * 1:1 legacy Thaumcraft ResearchToast.
 * Displays a parchment toast popup in the top-right corner when research is completed.
 */
public class TCResearchToast implements Toast {
    private static final ResourceLocation HUD_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/gui/hud.png");
    private static final long DISPLAY_DURATION_MS = 5000L;

    private final TCResearchEntryDefinition entry;
    private final String titleKey;

    public TCResearchToast(TCResearchEntryDefinition entry) {
        this(entry, "research.complete");
    }

    public TCResearchToast(TCResearchEntryDefinition entry, String titleKey) {
        this.entry = entry;
        this.titleKey = titleKey == null ? "research.complete" : titleKey;
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        Font font = toastComponent.getMinecraft().font;

        // Render parchment toast frame (160 x 32)
        graphics.blit(HUD_TEXTURE, 0, 0, 0, 224, 160, 32);

        // Render research icon at (6, 8)
        if (entry != null && !entry.icons().isEmpty()) {
            TCResearchIconRenderer.render(graphics, entry.icons().get(0), 6, 8, 220);
        }

        // Subtitle: "Research Complete" (0xA239F1)
        graphics.drawString(font, Component.translatable(titleKey), 30, 7, 0xA239F1, false);

        // Main title: research entry name (0xFFAB09)
        if (entry != null) {
            Component nameComponent = Component.translatable(entry.name());
            int textWidth = font.width(nameComponent);
            if (textWidth > 124) {
                float scale = 124.0F / (float) textWidth;
                graphics.pose().pushPose();
                graphics.pose().translate(30.0F, 18.0F, 0.0F);
                graphics.pose().scale(scale, scale, 1.0F);
                graphics.drawString(font, nameComponent, 0, 0, 0xFFAB09, false);
                graphics.pose().popPose();
            } else {
                graphics.drawString(font, nameComponent, 30, 18, 0xFFAB09, false);
            }
        }

        return timeSinceLastVisible < DISPLAY_DURATION_MS ? Visibility.SHOW : Visibility.HIDE;
    }
}
