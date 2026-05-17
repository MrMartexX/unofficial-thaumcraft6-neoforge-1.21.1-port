package thaumcraft.common.items.tools;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import thaumcraft.client.TCThaumometerClientEffects;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCScanResult;
import thaumcraft.common.research.TCScanningManager;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;
import thaumcraft.common.world.aura.TCAuraNetwork;

public class ItemThaumometer extends Item {
    public ItemThaumometer() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            TCThaumometerClientEffects.onUse(level, player, hand);
            return InteractionResultHolder.consume(stack);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            TCScanResult result = TCScanningManager.scanLookingAndMutate(serverPlayer);
            level.playSound(null, serverPlayer.blockPosition(), TCSounds.SCAN.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
            sendScanFeedback(serverPlayer, result);
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        boolean ready = isSelected || slotId == 0;
        if (!ready || level.isClientSide() || entity.tickCount % 20 != 0 || !(entity instanceof ServerPlayer player)) {
            return;
        }

        TCAuraNetwork.sendAuraToPlayer(player, player.blockPosition());
        AuraHandler.getAuraChunk(level, player.blockPosition()).ifPresent(chunk -> warnAboutFlux(player, chunk));
    }

    private static void sendScanFeedback(ServerPlayer player, TCScanResult result) {
        if (result.suppressMessage()) {
            return;
        }

        if (!result.success()) {
            player.displayClientMessage(Component.translatable("tc.unknownobject").withStyle(net.minecraft.ChatFormatting.DARK_PURPLE, net.minecraft.ChatFormatting.ITALIC), true);
            return;
        }

        player.displayClientMessage(Component.translatable("tc.knownobject").withStyle(net.minecraft.ChatFormatting.GREEN, net.minecraft.ChatFormatting.ITALIC), true);
    }

    private static void warnAboutFlux(ServerPlayer player, AuraChunk chunk) {
        if ((chunk.getFlux() <= chunk.getVis() && chunk.getFlux() <= chunk.getBase() / 3.0F)
                || TCResearchManager.isResearchComplete(TCPlayerKnowledgeStore.get(player), "FLUX")) {
            return;
        }

        TCResearchManager.startResearchWithPopup(player, "FLUX");
        player.displayClientMessage(
                Component.translatable("research.FLUX.warn").withStyle(net.minecraft.ChatFormatting.DARK_PURPLE),
                true
        );
    }
}
