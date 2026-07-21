package thaumcraft.common.research;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import thaumcraft.common.items.ItemAspectVariant;
import thaumcraft.common.registry.TCItems;

public final class TCResearchDiscoveryEvents {
    private static final String GOT_THAUMONOMICON = "!gotthaumonomicon";
    private static final String GOT_CRYSTALS = "!gotcrystals";

    private TCResearchDiscoveryEvents() {
    }

    public static void onItemPickup(ItemEntityPickupEvent.Post event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack stack = event.getOriginalStack();
        if (stack.isEmpty()) {
            return;
        }

        boolean changed = false;
        if (stack.is(TCItems.THAUMONOMICON.get())) {
            changed |= TCResearchManager.addResearchMarker(player, GOT_THAUMONOMICON, false);
        }
        if (stack.getItem() instanceof ItemAspectVariant variant
                && variant.kind() == ItemAspectVariant.Kind.CRYSTAL_ESSENCE) {
            changed |= TCResearchManager.addResearchMarker(player, GOT_CRYSTALS, false);
        }

        if (changed) {
            TCPlayerKnowledgeStore.sync(player);
        }
    }
}
