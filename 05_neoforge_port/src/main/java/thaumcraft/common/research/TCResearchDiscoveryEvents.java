package thaumcraft.common.research;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import thaumcraft.common.items.ItemAspectVariant;
import thaumcraft.common.registry.TCItems;

/** Restores the hidden TC6 discovery markers that bootstrap early thaumaturgy. */
public final class TCResearchDiscoveryEvents {
    public static final String GOT_THAUMONOMICON = "!gotthaumonomicon";
    public static final String GOT_CRYSTALS = "!gotcrystals";
    public static final String GOT_DREAM = "!gotdream";

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
            boolean firstCrystal = TCResearchManager.addResearchMarker(player, GOT_CRYSTALS, false);
            changed |= firstCrystal;
            if (firstCrystal) {
                player.displayClientMessage(
                        Component.translatable("got.crystals").withStyle(ChatFormatting.DARK_PURPLE),
                        false
                );
            }
        }

        if (changed) {
            TCPlayerKnowledgeStore.sync(player);
        }
    }

    /**
     * Legacy TC6 progression: after the first crystal discovery, sleeping grants !gotdream.
     * The original mod also handed the player a written "Strange Dreams" journal. For the
     * modern port we provide a named writable book so the survival flow is visible while the
     * full legacy written-book pages are restored separately.
     */
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        if (!knowledge.isResearchKnown(GOT_CRYSTALS) || knowledge.isResearchKnown(GOT_DREAM)) {
            return;
        }

        if (!TCResearchManager.addResearchMarker(player, GOT_DREAM, false)) {
            return;
        }

        ItemStack dreamJournal = new ItemStack(Items.WRITTEN_BOOK);
        List<Filterable<Component>> pages = List.of(
                Filterable.passThrough(Component.translatable("book.start.1")),
                Filterable.passThrough(Component.translatable("book.start.2")),
                Filterable.passThrough(Component.translatable("book.start.3"))
        );
        WrittenBookContent content = new WrittenBookContent(
                Filterable.passThrough(Component.translatable("book.start.title").getString()),
                player.getScoreboardName(),
                3,
                pages,
                true
        );
        dreamJournal.set(DataComponents.WRITTEN_BOOK_CONTENT, content);
        dreamJournal.set(DataComponents.CUSTOM_NAME,
                Component.translatable("book.start.title").withStyle(ChatFormatting.DARK_PURPLE));
        if (!player.getInventory().add(dreamJournal)) {
            player.drop(dreamJournal, false);
        }

        player.displayClientMessage(
                Component.translatable("got.dream").withStyle(ChatFormatting.DARK_PURPLE),
                false
        );
        TCPlayerKnowledgeStore.sync(player);
    }
}
