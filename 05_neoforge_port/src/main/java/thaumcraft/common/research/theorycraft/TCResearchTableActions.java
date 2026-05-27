package thaumcraft.common.research.theorycraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.menu.TCResearchTableMenu;
import thaumcraft.common.research.TCResearchManager;
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
            case TCResearchTableActionPayload.ACTION_START_THEORY -> startTheory(player, table, payload.aidKeys());
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

    private static boolean startTheory(ServerPlayer player, TCResearchTableBlockEntity table, List<String> selectedAidKeys) {
        if (table.getTheoryData() != null || !table.hasUsableScribingTools() || table.getPaperCount() <= 0) {
            return false;
        }
        LinkedHashSet<String> nearbyAidKeys = TCTheorycraftManager.collectNearbyAidKeys(table.getLevel(), table.getBlockPos());
        ArrayList<String> acceptedAidKeys = new ArrayList<>();
        int inspirationStart = TCResearchManager.availableTheoryInspiration(player);
        if (selectedAidKeys != null) {
            for (String aidKey : selectedAidKeys) {
                if (nearbyAidKeys.contains(aidKey)
                        && !acceptedAidKeys.contains(aidKey)
                        && acceptedAidKeys.size() + 1 < inspirationStart) {
                    acceptedAidKeys.add(aidKey);
                }
            }
        }
        TCResearchTableData data = new TCResearchTableData(player);
        data.initialize(player, acceptedAidKeys);
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
        List<ItemStack> requiredItems = choice.card.getRequiredItems();
        if (!hasRequiredItems(player, requiredItems)) {
            return false;
        }
        if (!consumeRequiredItems(player, requiredItems, choice.card.getRequiredItemsConsumed())) {
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

    static boolean hasRequiredItems(ServerPlayer player, List<ItemStack> requiredItems) {
        if (requiredItems == null || requiredItems.isEmpty()) {
            return true;
        }
        for (ItemStack required : requiredItems) {
            if (!isRequirementSatisfied(player, required)) {
                return false;
            }
        }
        return true;
    }

    static boolean consumeRequiredItems(ServerPlayer player, List<ItemStack> requiredItems, List<Boolean> consumed) {
        if (requiredItems == null || requiredItems.isEmpty()) {
            return true;
        }
        if (consumed == null || consumed.size() != requiredItems.size()) {
            return true;
        }

        ArrayList<ConsumeEntry> plan = new ArrayList<>();
        ArrayList<ItemStack> simulated = new ArrayList<>();
        for (ItemStack stack : player.getInventory().items) {
            simulated.add(stack.copy());
        }

        for (int requirementIndex = 0; requirementIndex < requiredItems.size(); requirementIndex++) {
            if (!Boolean.TRUE.equals(consumed.get(requirementIndex))) {
                continue;
            }
            ItemStack required = requiredItems.get(requirementIndex);
            if (!planRequirement(simulated, required, plan)) {
                return false;
            }
        }

        if (plan.isEmpty()) {
            return true;
        }

        Inventory inventory = player.getInventory();
        for (ConsumeEntry entry : plan) {
            if (entry.slot() >= 0 && entry.slot() < inventory.items.size()) {
                inventory.items.get(entry.slot()).shrink(entry.amount());
            }
        }
        inventory.setChanged();
        return true;
    }

    private static boolean isRequirementSatisfied(ServerPlayer player, ItemStack required) {
        if (required == null || required.isEmpty()) {
            return true;
        }

        int remaining = required.getCount();
        for (ItemStack stack : player.getInventory().items) {
            if (matchesRequiredStack(stack, required)) {
                remaining -= stack.getCount();
                if (remaining <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean planRequirement(List<ItemStack> simulated, ItemStack required, List<ConsumeEntry> plan) {
        if (required == null || required.isEmpty()) {
            return true;
        }

        int remaining = required.getCount();
        for (int slot = 0; slot < simulated.size() && remaining > 0; slot++) {
            ItemStack stack = simulated.get(slot);
            if (!matchesRequiredStack(stack, required)) {
                continue;
            }

            int amount = Math.min(remaining, stack.getCount());
            stack.shrink(amount);
            plan.add(new ConsumeEntry(slot, amount));
            remaining -= amount;
        }
        return remaining <= 0;
    }

    private static boolean matchesRequiredStack(ItemStack stack, ItemStack required) {
        return !stack.isEmpty()
                && !required.isEmpty()
                && ItemStack.isSameItemSameComponents(stack, required);
    }

    private record ConsumeEntry(int slot, int amount) {
    }
}
