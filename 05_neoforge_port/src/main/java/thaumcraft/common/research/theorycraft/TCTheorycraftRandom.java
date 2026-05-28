package thaumcraft.common.research.theorycraft;

import java.util.List;
import java.util.Random;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import thaumcraft.common.research.TCResearchManager;

final class TCTheorycraftRandom {
    private TCTheorycraftRandom() {
    }

    static Random seeded(long seed) {
        return new Random(seed);
    }

    static int between(ServerPlayer player, Random fallback, int min, int max) {
        if (player != null) {
            return Mth.nextInt(player.getRandom(), min, max);
        }
        return min + fallback.nextInt(max - min + 1);
    }

    static boolean nextBoolean(ServerPlayer player, Random fallback) {
        return player == null ? fallback.nextBoolean() : player.getRandom().nextBoolean();
    }

    static String category(ServerPlayer player, Random fallback) {
        List<String> categories = TCResearchManager.categoryKeys();
        if (categories.isEmpty()) {
            return "BASICS";
        }
        int index = player == null ? fallback.nextInt(categories.size()) : player.getRandom().nextInt(categories.size());
        return categories.get(index);
    }
}
