package thaumcraft.common.research.theorycraft;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import thaumcraft.common.menu.TCResearchTableMenu;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;

final class TCResearchTableActions {
    private static final Map<UUID, Long> ANTI_SPAM = new HashMap<>();

    private TCResearchTableActions() {
    }

    static void handle(ServerPlayer player, TCResearchTableMenu menu, TCResearchTableActionPayload payload) {
        TCResearchTableBlockEntity table = menu.blockEntity();
        if (table == null || !menu.stillValid(player)) {
            return;
        }

        boolean changed = switch (payload.actionId()) {
            case TCResearchTableActionPayload.ACTION_START_THEORY -> startTheory(player, table);
            case TCResearchTableActionPayload.ACTION_DRAW_CARDS -> drawCards(player, table);
            case TCResearchTableActionPayload.ACTION_SELECT_CARD -> selectCard(player, table, payload.choiceIndex());
            case TCResearchTableActionPayload.ACTION_COMMIT_SELECTED -> commitSelected(table);
            case TCResearchTableActionPayload.ACTION_COMPLETE_THEORY -> completeTheory(player, table);
            case TCResearchTableActionPayload.ACTION_SCRAP_THEORY -> scrapTheory(table);
            default -> false;
        };

        if (changed) {
            table.setChanged();
            TCResearchTableNetwork.syncToPlayer(player, table);
            menu.broadcastChanges();
        }
    }

    private static boolean startTheory(ServerPlayer player, TCResearchTableBlockEntity table) {
        if (table.getTheoryData() != null || !table.hasUsableScribingTools() || table.getPaperCount() <= 0) {
            return false;
        }
        TCResearchTableData data = new TCResearchTableData(player);
        data.initialize(player, java.util.List.of());
        table.setTheoryData(data);
        return true;
    }

    private static boolean drawCards(ServerPlayer player, TCResearchTableBlockEntity table) {
        TCResearchTableData data = table.getTheoryData();
        if (data == null || data.isComplete() || !table.consumePaperFromTable()) {
            return false;
        }
        data.drawCards(data.bonusDraws > 0 ? 3 : 2, player);
        return true;
    }

    private static boolean selectCard(ServerPlayer player, TCResearchTableBlockEntity table, int choiceIndex) {
        long now = System.currentTimeMillis();
        long previous = ANTI_SPAM.getOrDefault(player.getUUID(), 0L);
        if (now - previous < 333L) {
            return false;
        }
        ANTI_SPAM.put(player.getUUID(), now);

        TCResearchTableData data = table.getTheoryData();
        if (data == null || choiceIndex < 0 || choiceIndex >= data.cardChoices.size() || !table.hasUsableScribingTools()) {
            return false;
        }

        TCResearchTableData.CardChoice choice = data.cardChoices.get(choiceIndex);
        if (!choice.card.getRequiredItems().isEmpty()) {
            return false;
        }
        if (!choice.card.activate(player, data)) {
            return false;
        }

        table.consumeInkFromTable();
        choice.selected = true;
        data.addInspiration(-choice.card.getInspirationCost());
        return true;
    }

    private static boolean commitSelected(TCResearchTableBlockEntity table) {
        TCResearchTableData data = table.getTheoryData();
        if (data == null) {
            return false;
        }
        if (data.lastDraw != null) {
            data.savedCards.add(data.lastDraw.card.getSeed());
        }
        for (TCResearchTableData.CardChoice choice : data.cardChoices) {
            if (choice.selected) {
                data.lastDraw = choice;
                data.cardChoices.clear();
                return true;
            }
        }
        return false;
    }

    private static boolean completeTheory(ServerPlayer player, TCResearchTableBlockEntity table) {
        TCResearchTableData data = table.getTheoryData();
        if (data == null || !data.isComplete()) {
            return false;
        }
        table.finishTheory(player);
        return true;
    }

    private static boolean scrapTheory(TCResearchTableBlockEntity table) {
        TCResearchTableData data = table.getTheoryData();
        if (data == null || data.isComplete()) {
            return false;
        }
        table.setTheoryData(null);
        return true;
    }
}
