package thaumcraft.common.warp;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import thaumcraft.common.config.TCConfig;

public final class TCWarpCraftingEvents {
    private TCWarpCraftingEvents() {
    }

    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack crafted = event.getCrafting();
        if (crafted.isEmpty()) {
            return;
        }

        TCWarpManager.applyCraftingWarp(player, crafted, TCConfig.WUSS_MODE.get());
    }
}
