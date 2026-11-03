package thaumcraft.common.golems;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import thaumcraft.common.golems.seals.SealHandler;

@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft")
public final class TCGolemEvents {
    private TCGolemEvents() {
    }

    @net.neoforged.bus.api.SubscribeEvent
    public static void onServerStopped(net.neoforged.neoforge.event.server.ServerStoppedEvent event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            thaumcraft.common.golems.tasks.TaskHandler.clear(level);
        }
        SealHandler.sealEntities.clear();
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            SealHandler.tickSeals(level);
        }
    }
}
