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
        set(player, knowledge, true);
    }

    public static void set(ServerPlayer player, TCPlayerKnowledge knowledge, boolean sync) {
        player.getPersistentData().put(ROOT_KEY, knowledge.save());
        if (sync) {
            sync(player);
        }
    }

    public static void mutate(ServerPlayer player, KnowledgeMutation mutation) {
        mutate(player, mutation, true);
    }

    public static void mutate(ServerPlayer player, KnowledgeMutation mutation, boolean sync) {
        TCPlayerKnowledge knowledge = get(player);
        mutation.apply(knowledge);
        set(player, knowledge, sync);
    }

    public static void sync(ServerPlayer player) {
        TCKnowledgeNetwork.syncToPlayer(player);
    }

    @FunctionalInterface
    public interface KnowledgeMutation {
        void apply(TCPlayerKnowledge knowledge);
    }
}
