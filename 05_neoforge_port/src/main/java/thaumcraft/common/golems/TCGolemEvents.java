package thaumcraft.common.golems;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import thaumcraft.common.golems.seals.SealHandler;

public final class TCGolemEvents {
    private TCGolemEvents() {
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            SealHandler.tickSeals(level);
        }
    }
}
