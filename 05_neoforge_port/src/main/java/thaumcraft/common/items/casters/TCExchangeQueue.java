package thaumcraft.common.items.casters;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "thaumcraft")
public final class TCExchangeQueue {
    private record Request(ServerPlayer player, BlockPos pos, Direction side, boolean silk, int fortune, BlockState source, ItemStack selected) {}
    private static final Map<ServerLevel, ArrayDeque<Request>> PENDING = new IdentityHashMap<>();
    private TCExchangeQueue() {}
    public static boolean enqueue(ServerPlayer player, BlockPos pos, Direction side, boolean silk, int fortune) {
        var level = player.serverLevel();
        if (!level.getServer().isSameThread() || player.isRemoved() || !level.hasChunkAt(pos)) return false;
        var selected = TCExchangeHelper.selected(player);
        if (!(selected.getItem() instanceof BlockItem)) return false;
        PENDING.computeIfAbsent(level, key -> new ArrayDeque<>()).addLast(new Request(player, pos.immutable(), side,
                silk, Math.clamp(fortune, 0, 4), level.getBlockState(pos), selected));
        return true;
    }
    @SubscribeEvent public static void tick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        var work = PENDING.remove(level);
        if (work == null) return;
        while (!work.isEmpty()) {
            var request = work.removeFirst();
            if (request.player.isRemoved() || request.player.serverLevel() != level || !level.hasChunkAt(request.pos)) continue;
            TCExchangeHelper.exchangeSnapshot(request.player, request.pos, request.side, request.silk, request.fortune, request.source, request.selected);
        }
    }
    @SubscribeEvent public static void unload(net.neoforged.neoforge.event.level.LevelEvent.Unload event) { PENDING.remove(event.getLevel()); }
    @SubscribeEvent public static void stop(net.neoforged.neoforge.event.server.ServerStoppedEvent event) { PENDING.clear(); }
    static void sendEffects(ServerLevel level, BlockPos pos) {
        var payload = new TCExchangeFXPayload(level.dimension(), pos);
        for (var player : level.players()) if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 1024)
            PacketDistributor.sendToPlayer(player, payload);
    }
}
