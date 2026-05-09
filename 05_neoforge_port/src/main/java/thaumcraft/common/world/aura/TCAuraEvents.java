package thaumcraft.common.world.aura;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import thaumcraft.common.config.TCConfig;

public final class TCAuraEvents {
    private TCAuraEvents() {
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            AuraHandler.tickLevel(level);
        }
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!TCConfig.GENERATE_AURA.get() || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        AuraHandler.ensureAuraChunk(level, event.getChunk().getPos());
    }

    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            AuraHandler.unloadAuraChunk(level, event.getChunk().getPos());
        }
    }
}
