package thaumcraft.api.research;

import net.minecraft.server.level.ServerPlayer;

public interface IScanThing {
    boolean checkThing(ServerPlayer player, Object object);

    String getResearchKey(ServerPlayer player, Object object);

    default void onSuccess(ServerPlayer player, Object object) {
    }
}
