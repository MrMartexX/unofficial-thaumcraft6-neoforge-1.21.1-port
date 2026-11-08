package thaumcraft.client.gui;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCResearchManager;

/**
 * Handles firing ResearchToast notifications and sound cues when research entries are completed.
 */
public final class ResearchToastHandler {
    private ResearchToastHandler() {
    }

    public static void onResearchCompleted(Set<String> newlyCompleted) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null || newlyCompleted == null || newlyCompleted.isEmpty()) {
            return;
        }

        for (String key : newlyCompleted) {
            if (key == null || key.startsWith("!")) {
                continue;
            }
            TCResearchManager.getEntry(key).ifPresent(entry -> {
                mc.getToasts().addToast(new TCResearchToast(entry));
                mc.getSoundManager().play(SimpleSoundInstance.forUI(TCSounds.LEARN.get(), 1.0F));
            });
        }
    }
}
