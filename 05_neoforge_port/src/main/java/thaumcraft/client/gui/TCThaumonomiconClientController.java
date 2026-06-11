package thaumcraft.client.gui;

import net.minecraft.client.Minecraft;
import thaumcraft.common.research.TCThaumonomiconClientCache;

public final class TCThaumonomiconClientController {
    private TCThaumonomiconClientController() {
    }

    public static void tick(Minecraft minecraft) {
        if (!TCThaumonomiconClientCache.pollOpenRequested()) {
            return;
        }
        minecraft.setScreen(new TCThaumonomiconBrowserScreen());
    }
}
