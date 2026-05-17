package thaumcraft.common.research;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public final class TCPlayerKnowledgeStore {
    private static final String ROOT_KEY = "ThaumcraftKnowledge";

    private TCPlayerKnowledgeStore() {
    }

    public static TCPlayerKnowledge get(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag knowledgeTag = persistentData.getCompound(ROOT_KEY);
        return TCPlayerKnowledge.load(knowledgeTag);
    }

    public static void set(ServerPlayer player, TCPlayerKnowledge knowledge) {
        player.getPersistentData().put(ROOT_KEY, knowledge.save());
        TCKnowledgeNetwork.syncToPlayer(player);
    }

    public static void mutate(ServerPlayer player, KnowledgeMutation mutation) {
        TCPlayerKnowledge knowledge = get(player);
        mutation.apply(knowledge);
        set(player, knowledge);
    }

    @FunctionalInterface
    public interface KnowledgeMutation {
        void apply(TCPlayerKnowledge knowledge);
    }
}
