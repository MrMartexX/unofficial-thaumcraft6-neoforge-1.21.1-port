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
        if (table == null) {
            return;
        }

        ActionResult result;
        if (!menu.stillValid(player)) {
            result = ActionResult.rejected("invalid_menu");
        } else {
            result = switch (payload.actionId()) {
            case TCResearchTableActionPayload.ACTION_START_THEORY -> startTheory(player, table, payload.aidKeys());
            case TCResearchTableActionPayload.ACTION_DRAW_CARDS -> drawCards(player, table);
            case TCResearchTableActionPayload.ACTION_SELECT_CARD -> selectCard(player, table, payload.choiceIndex());
            case TCResearchTableActionPayload.ACTION_COMMIT_SELECTED -> commitSelected(table);
            case TCResearchTableActionPayload.ACTION_COMPLETE_THEORY -> completeTheory(player, table);
            case TCResearchTableActionPayload.ACTION_SCRAP_THEORY -> scrapTheory(table);
            case TCResearchTableActionPayload.ACTION_SELECT_AND_COMMIT -> selectAndCommit(player, table, payload.choiceIndex());
            default -> ActionResult.rejected("unknown_action");
            };
        }

        if (result.changed()) {
            table.setChanged();
        }
        menu.broadcastChanges();
        TCResearchTableNetwork.sendActionResult(player, table, payload.actionId(), result.accepted(), result.key());
    }

    private static ActionResult startTheory(ServerPlayer player, TCResearchTableBlockEntity table, List<String> selectedAidKeys) {
        if (table.getTheoryData() != null) {
            return ActionResult.rejected("theory_exists");
        }
        if (!table.hasUsableScribingTools()) {
            return ActionResult.rejected("missing_scribing_tools");
        }
        if (table.getPaperCount() <= 0) {
            return ActionResult.rejected("missing_paper");
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
        return ActionResult.changed("started");
    }

    private static ActionResult drawCards(ServerPlayer player, TCResearchTableBlockEntity table) {
        TCResearchTableData data = table.getTheoryData();
        if (data == null) {
            return ActionResult.rejected("missing_theory");
        }
        if (data.isComplete()) {
            return ActionResult.rejected("theory_complete");
        }
        if (!table.consumePaperFromTable()) {
            return ActionResult.rejected("missing_paper");
        }
        data.drawCards(data.bonusDraws > 0 ? 3 : 2, player);
        return ActionResult.changed("cards_drawn");
    }

    private static ActionResult selectCard(ServerPlayer player, TCResearchTableBlockEntity table, int choiceIndex) {
        long now = System.currentTimeMillis();
        long previous = ANTI_SPAM.getOrDefault(player.getUUID(), 0L);
        if (now - previous < 333L) {
            return ActionResult.rejected("cooldown");
        }
        ANTI_SPAM.put(player.getUUID(), now);

        TCResearchTableData data = table.getTheoryData();
        if (data == null) {
            return ActionResult.rejected("missing_theory");
        }
        if (choiceIndex < 0 || choiceIndex >= data.cardChoices.size()) {
            return ActionResult.rejected("invalid_choice");
        }
        if (!table.hasUsableScribingTools()) {
            return ActionResult.rejected("missing_scribing_tools");
        }

        TCResearchTableData.CardChoice choice = data.cardChoices.get(choiceIndex);
        if (choice.selected) {
            return ActionResult.rejected("card_already_selected");
        }
        List<ItemStack> requiredItems = choice.card.getRequiredItems();
        if (!hasRequiredItems(player, requiredItems)) {
            return ActionResult.rejected("missing_required_items");
        }
        ArrayList<ConsumeEntry> consumePlan = buildRequiredItemConsumePlan(player, requiredItems, choice.card.getRequiredItemsConsumed());
        if (consumePlan == null) {
            return ActionResult.rejected("missing_required_items");
        }
        if (!choice.card.activate(player, data)) {
            return ActionResult.rejected("activation_failed");
        }

        applyConsumePlan(player, consumePlan);
        table.consumeInkFromTable();
        choice.selected = true;
        data.addInspiration(-choice.card.getInspirationCost());
        return ActionResult.changed("card_selected");
    }

    private static ActionResult commitSelected(TCResearchTableBlockEntity table) {
        TCResearchTableData data = table.getTheoryData();
        if (data == null) {
            return ActionResult.rejected("missing_theory");
        }
        if (data.lastDraw != null) {
            data.savedCards.add(data.lastDraw.card.getSeed());
        }
        for (TCResearchTableData.CardChoice choice : data.cardChoices) {
            if (choice.selected) {
                data.lastDraw = choice;
                data.cardChoices.clear();
                return ActionResult.changed("selected_card_committed");
            }
        }
        return ActionResult.rejected("no_selected_card");
    }

    private static ActionResult selectAndCommit(ServerPlayer player, TCResearchTableBlockEntity table, int choiceIndex) {
        ActionResult selected = selectCard(player, table, choiceIndex);
        if (!selected.accepted()) {
            return selected;
        }

        ActionResult committed = commitSelected(table);
        if (!committed.accepted()) {
            return committed;
        }
        return ActionResult.changed("selected_card_committed");
    }

    private static ActionResult completeTheory(ServerPlayer player, TCResearchTableBlockEntity table) {
        TCResearchTableData data = table.getTheoryData();
        if (data == null) {
            return ActionResult.rejected("missing_theory");
        }
        if (!data.isComplete()) {
            return ActionResult.rejected("theory_incomplete");
        }
        table.finishTheory(player);
        return ActionResult.changed("theory_completed");
    }

    private static ActionResult scrapTheory(TCResearchTableBlockEntity table) {
        TCResearchTableData data = table.getTheoryData();
        if (data == null) {
            return ActionResult.rejected("missing_theory");
        }
        if (data.isComplete()) {
            return ActionResult.rejected("theory_complete");
        }
        table.setTheoryData(null);
        return ActionResult.changed("theory_scrapped");
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
        ArrayList<ConsumeEntry> plan = buildRequiredItemConsumePlan(player, requiredItems, consumed);
        if (plan == null) {
            return false;
        }
        applyConsumePlan(player, plan);
        return true;
    }

    private static ArrayList<ConsumeEntry> buildRequiredItemConsumePlan(
            ServerPlayer player,
            List<ItemStack> requiredItems,
            List<Boolean> consumed
    ) {
        ArrayList<ConsumeEntry> plan = new ArrayList<>();
        if (requiredItems == null || requiredItems.isEmpty()) {
            return plan;
        }
        if (consumed == null || consumed.size() != requiredItems.size()) {
            return plan;
        }

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
                return null;
            }
        }

        return plan;
    }

    private static void applyConsumePlan(ServerPlayer player, ArrayList<ConsumeEntry> plan) {
        if (plan.isEmpty()) {
            return;
        }

        Inventory inventory = player.getInventory();
        for (ConsumeEntry entry : plan) {
            if (entry.slot() >= 0 && entry.slot() < inventory.items.size()) {
                inventory.items.get(entry.slot()).shrink(entry.amount());
            }
        }
        inventory.setChanged();
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

    private record ActionResult(boolean accepted, String key, boolean changed) {
        private static ActionResult changed(String key) {
            return new ActionResult(true, key, true);
        }

        private static ActionResult rejected(String key) {
            return new ActionResult(false, key, false);
        }
    }
}
