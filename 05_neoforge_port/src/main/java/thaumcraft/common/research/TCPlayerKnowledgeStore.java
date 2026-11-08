package thaumcraft.common.research;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class TCPlayerKnowledgeStore {
    private static final String ROOT_KEY = "ThaumcraftKnowledge";

    private TCPlayerKnowledgeStore() {
    }

    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer cloned) || !(event.getOriginal() instanceof ServerPlayer original)) {
            return;
        }
        CompoundTag oldData = original.getPersistentData();
        if (oldData.contains(ROOT_KEY)) {
            cloned.getPersistentData().put(ROOT_KEY, oldData.getCompound(ROOT_KEY).copy());
        }
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
