package thaumcraft.common.items.curios;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.research.TCResearchDiscoveryEvents;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.research.TCThaumonomiconNetwork;

public final class ItemThaumonomicon extends Item {
    public ItemThaumonomicon() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            boolean discovered = TCResearchManager.addResearchMarker(
                    serverPlayer,
                    TCResearchDiscoveryEvents.GOT_THAUMONOMICON,
                    false
            );
            TCResearchManager.completeKnownResearchSiblings(serverPlayer, false);
            if (discovered) {
                TCPlayerKnowledgeStore.sync(serverPlayer);
            }
            TCThaumonomiconNetwork.openFor(serverPlayer);
            level.playSound(
                    null,
                    player.blockPosition(),
                    TCSounds.PAGE.get(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
