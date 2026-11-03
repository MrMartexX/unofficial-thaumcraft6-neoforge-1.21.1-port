package thaumcraft.common.golems.seals;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import thaumcraft.api.golems.seals.SealPos;

@EventBusSubscriber(modid = "thaumcraft")
public final class TCSealNetwork {
    public static final int UPSERT = 0, REMOVE = 1, CLEAR_CHUNK = 2;
    private TCSealNetwork() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("2").playToClient(TCSealSyncPayload.TYPE, TCSealSyncPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    var level = context.player().level();
                    if (!level.isClientSide || !level.dimension().equals(payload.dimension()) || payload.data() == null) return;
                    SealHandler.applyClientSync(level, payload);
                }));
    }

    public static void send(ServerLevel level, SealEntity seal, int operation) {
        var pos = seal.getSealPos().pos;
        if (!level.hasChunkAt(pos)) return;
        PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(pos),
                new TCSealSyncPayload(level.dimension(), operation, seal.writeNBT()));
    }

    @SubscribeEvent
    public static void watch(ChunkWatchEvent.Sent event) {
        var level = event.getLevel();
        var chunk = event.getPos();
        PacketDistributor.sendToPlayer(event.getPlayer(), clear(level, chunk));
        for (var seal : TCSealSavedData.get(level).seals.values()) {
            if (new ChunkPos(seal.getSealPos().pos).equals(chunk)) {
                PacketDistributor.sendToPlayer(event.getPlayer(), new TCSealSyncPayload(level.dimension(), UPSERT, seal.writeNBT()));
            }
        }
    }

    @SubscribeEvent
    public static void unwatch(ChunkWatchEvent.UnWatch event) {
        PacketDistributor.sendToPlayer(event.getPlayer(), clear(event.getLevel(), event.getPos()));
    }

    private static TCSealSyncPayload clear(ServerLevel level, ChunkPos chunk) {
        CompoundTag data = new CompoundTag();
        data.putLong("chunk", chunk.toLong());
        return new TCSealSyncPayload(level.dimension(), CLEAR_CHUNK, data);
    }
}
