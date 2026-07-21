package thaumcraft.common.research;

import java.util.Collection;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.components.TCAspectStackComponent;
import thaumcraft.common.items.components.TCLegacyItemComponent;
import thaumcraft.common.items.components.TCStoredEnchantComponent;
import thaumcraft.common.config.TCConfig;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.research.TCResearchRequirementResolver.ItemRequirement;
import thaumcraft.common.research.TCResearchRequirementResolver.ItemRequirementResolution;
import thaumcraft.common.research.TCResearchRequirementResolver.KnowledgeRequirement;
import thaumcraft.common.research.TCResearchRequirementResolver.KnowledgeRequirementResolution;

import thaumcraft.common.warp.TCWarpManager;
import thaumcraft.common.warp.TCWarpType;
public final class TCResearchManager {
    private static TCResearchData activeData = TCResearchData.empty();
    private static int dataRevision;

    private TCResearchManager() {
    }

    public static void bootstrap() {
        activeData = TCResearchData.empty();
        dataRevision = 0;
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new TCResearchReloadListener());
    }

    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack crafted = event.getCrafting();
        if (crafted.isEmpty()) {
            return;
        }

        markCraftedResearchReferences(player, crafted);
    }

    static void reload(TCResearchData data) {
        activeData = data == null ? TCResearchData.empty() : data;
        dataRevision++;
        Thaumcraft.LOGGER.info(
                "Thaumcraft research data reloaded: {} categories, {} entries, {} stages, {} addenda.",
                activeData.categories().size(),
                activeData.entries().size(),
                activeData.stageCount(),
                activeData.addendumCount()
        );

        TCResearchValidationReport validation = activeData.validateResearchReferences();
        if (validation.hasUnresolvedReferences()) {
            TCResearchReference first = validation.unresolvedReferences().getFirst();
            Thaumcraft.LOGGER.warn(
                    "Thaumcraft research reference validation found {} unresolved research reference(s). First: {} {} raw={} normalized={}",
                    validation.unresolvedReferenceCount(),
                    first.ownerKey(),
                    first.location(),
                    first.rawReference(),
                    first.normalizedReference()
            );
        } else {
            Thaumcraft.LOGGER.info(
                    "Thaumcraft research reference validation passed: {} entry references, {} external scan/flag trigger references, 0 unresolved.",
                    validation.entryReferenceCount(),
                    validation.externalTriggerReferenceCount()
            );
        }
    }

    public static TCResearchData data() {
        return activeData;
    }

    public static int dataRevision() {
        return dataRevision;
    }

    public static Optional<TCResearchEntryDefinition> getEntry(String key) {
        return Optional.ofNullable(activeData.entries().get(canonicalResearchKey(key)));
    }

    public static Collection<TCResearchCategoryDefinition> categories() {
        return activeData.categories().values();
    }

    public static List<String> categoryKeys() {
        return new ArrayList<>(activeData.categories().keySet());
    }

    public static List<String> availableTheoryCategories(ServerPlayer player, Set<String> blockedCategories) {
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        HashSet<String> blocked = new HashSet<>();
        if (blockedCategories != null) {
            for (String category : blockedCategories) {
                blocked.add(TCPlayerKnowledge.normalizeCategory(category));
            }
        }

        ArrayList<String> categories = new ArrayList<>();
        for (TCResearchCategoryDefinition category : activeData.categories().values()) {
            if (blocked.contains(category.key())) {
                continue;
            }
            if (category.requiredResearch().isBlank() || knowsResearchStrict(knowledge, category.requiredResearch())) {
                categories.add(category.key());
            }
        }
        return categories;
    }

    public static int availableTheoryInspiration(ServerPlayer player) {
        float total = 5.0F;
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        for (String researchKey : knowledge.completedResearch()) {
            if (!knowsResearchStrict(knowledge, researchKey)) {
                continue;
            }

            TCResearchEntryDefinition entry = activeData.entries().get(canonicalResearchKey(researchKey));
            if (entry == null) {
                continue;
            }

            if (hasMeta(entry, "SPIKY")) {
                total += 0.5F;
            }
            if (hasMeta(entry, "HIDDEN")) {
                total += 0.1F;
            }
        }
        return Math.min(15, Math.round(total));
    }

    public static Collection<TCResearchEntryDefinition> entries() {
        return activeData.entries().values();
    }

    public static TCResearchValidationReport validateReferences() {
        return activeData.validateResearchReferences();
    }

    public static List<TCResearchEntryDefinition> entriesByCategory(String category) {
        String normalizedCategory = TCPlayerKnowledge.normalizeCategory(category);
        return activeData.entries().values().stream()
                .filter(entry -> entry.category().equals(normalizedCategory))
                .sorted(Comparator.comparing(TCResearchEntryDefinition::key))
                .toList();
    }

    public static boolean addKnowledgeRaw(ServerPlayer player, TCKnowledgeType type, String category, int rawAmount) {
        if (player == null || type == null || rawAmount == 0) {
            return false;
        }

        String normalizedCategory = TCPlayerKnowledge.normalizeCategory(category);
        final boolean[] changed = {false};
        final int[] gainedPoints = {0};
        TCPlayerKnowledgeStore.mutate(player, knowledge -> {
            int before = knowledge.getPoints(type, normalizedCategory);
            changed[0] = knowledge.addRaw(type, normalizedCategory, rawAmount);
            int after = knowledge.getPoints(type, normalizedCategory);
            gainedPoints[0] = Math.max(0, after - before);
        });

        if (changed[0] && rawAmount > 0) {
            // Legacy sends one PacketKnowledgeGain per gained point. While this port still has
            // several fractional raw awards, send at least one visual event for a positive
            // changed award so the HUD path is not silently invisible.
            sendKnowledgeGainPayloads(player, type, normalizedCategory, gainedPoints[0]);
        }
        return changed[0];
    }

    public static int getKnowledgePoints(ServerPlayer player, TCKnowledgeType type, String category) {
        if (player == null || type == null) {
            return 0;
        }
        return TCPlayerKnowledgeStore.get(player).getPoints(type, category);
    }

    public static boolean consumeKnowledgeRaw(ServerPlayer player, TCKnowledgeType type, String category, int rawAmount) {
        if (player == null || type == null || rawAmount <= 0) {
            return false;
        }

        final boolean[] changed = {false};
        TCPlayerKnowledgeStore.mutate(player, knowledge -> changed[0] = knowledge.addRaw(type, category, -rawAmount));
        return changed[0];
    }

    public static boolean addObservationFromScan(ServerPlayer player, AspectList aspects) {
        if (player == null || aspects == null || aspects.size() == 0 || aspects.visSize() == 0) {
            return false;
        }

        LinkedHashMap<String, Integer> gainedByCategory = new LinkedHashMap<>();
        final boolean[] changed = {false};
        TCPlayerKnowledgeStore.mutate(player, knowledge -> {
            for (TCResearchCategoryDefinition category : categories()) {
                int rawAmount = category.applyFormula(aspects);
                if (rawAmount > 0) {
                    int before = knowledge.getPoints(TCKnowledgeType.OBSERVATION, category.key());
                    boolean categoryChanged = knowledge.addRaw(TCKnowledgeType.OBSERVATION, category.key(), rawAmount);
                    changed[0] |= categoryChanged;
                    if (categoryChanged) {
                        int after = knowledge.getPoints(TCKnowledgeType.OBSERVATION, category.key());
                        int gained = Math.max(0, after - before);
                        if (gained > 0) {
                            gainedByCategory.merge(category.key(), gained, Integer::sum);
                        }
                    }
                }
            }
        });

        if (changed[0]) {
            for (Map.Entry<String, Integer> entry : gainedByCategory.entrySet()) {
                sendKnowledgeGainPayloads(player, TCKnowledgeType.OBSERVATION, entry.getKey(), entry.getValue());
            }
        }
        return changed[0];
    }

    private static void sendKnowledgeGainPayloads(ServerPlayer player, TCKnowledgeType type, String category, int gainedPoints) {
        if (player == null || type == null || gainedPoints <= 0) {
            return;
        }

        for (int index = 0; index < gainedPoints; index++) {
            TCKnowledgeNetwork.sendKnowledgeGain(player, type, category);
        }
    }

    public static TCResearchStatus getResearchStatus(TCPlayerKnowledge knowledge, String key) {
        String researchKey = canonicalResearchKey(key);
        if (!knowledge.isResearchKnown(researchKey)) {
            return TCResearchStatus.UNKNOWN;
        }

        TCResearchEntryDefinition entry = activeData.entries().get(researchKey);
        if (entry == null || entry.stages().isEmpty() || knowledge.getResearchStage(researchKey) > entry.stages().size()) {
            return TCResearchStatus.COMPLETE;
        }

        return TCResearchStatus.IN_PROGRESS;
    }

    public static boolean isResearchKnown(TCPlayerKnowledge knowledge, String key) {
        return knowledge.isResearchKnown(key);
    }

    public static boolean isResearchComplete(TCPlayerKnowledge knowledge, String key) {
        return getResearchStatus(knowledge, key) == TCResearchStatus.COMPLETE;
    }

    public static boolean doesPlayerHaveRequisites(ServerPlayer player, String key) {
        TCResearchEntryDefinition entry = activeData.entries().get(canonicalResearchKey(key));
        if (entry == null) {
            return true;
        }

        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        for (String parent : entry.parents()) {
            if (!knowsResearchStrict(knowledge, stripHiddenPrefix(parent))) {
                return false;
            }
        }

        return true;
    }

    public static boolean canUnlockResearch(ServerPlayer player, String key) {
        return getEntry(key).isPresent() && doesPlayerHaveRequisites(player, key);
    }

    public static boolean isCategoryVisible(TCPlayerKnowledge knowledge, String categoryKey) {
        TCResearchCategoryDefinition category = activeData.categories().get(TCPlayerKnowledge.normalizeCategory(categoryKey));
        if (category == null) {
            return false;
        }
        return category.requiredResearch().isBlank() || knowsResearchStrict(knowledge, category.requiredResearch());
    }

    public static boolean isResearchVisible(ServerPlayer player, String key) {
        TCResearchEntryDefinition entry = activeData.entries().get(canonicalResearchKey(key));
        if (entry == null) {
            return false;
        }
        return isResearchVisible(player, entry, new HashSet<>());
    }

    public static List<TCResearchEntryDefinition> visibleEntriesByCategory(ServerPlayer player, String category) {
        return entriesByCategory(category).stream()
                .filter(entry -> isResearchVisible(player, entry, new HashSet<>()))
                .toList();
    }

    public static boolean progressResearch(ServerPlayer player, String key) {
        return progressResearch(player, key, true);
    }

    public static boolean progressResearch(ServerPlayer player, String key, boolean sync) {
        return advanceResearch(player, key, sync, false, false);
    }

    private static boolean progressResearch(ServerPlayer player, String key, boolean sync, boolean noResearchFlag) {
        return advanceResearch(player, key, sync, noResearchFlag, false);
    }

    private static boolean advanceResearch(
            ServerPlayer player,
            String key,
            boolean sync,
            boolean noResearchFlag,
            boolean consumeCurrentStage
    ) {
        String researchKey = canonicalResearchKey(key);
        if (researchKey.isBlank()) {
            return false;
        }

        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        if (isResearchComplete(knowledge, researchKey) || !doesPlayerHaveRequisites(player, researchKey)) {
            return false;
        }

        TCResearchEntryDefinition entry = activeData.entries().get(researchKey);
        StageConsumePlan consumePlan = StageConsumePlan.empty();
        if (consumeCurrentStage) {
            TCResearchStageRequirementResult result = checkCurrentStageRequirementsInternal(player, researchKey);
            if (!result.hasStage() || !result.passed()) {
                return false;
            }
            consumePlan = buildCurrentStageConsumePlan(player, entry, knowledge);
            if (consumePlan == null) {
                return false;
            }
        }

        TCResearchProgressionSemantics.Advance advance = entry == null
                ? null
                : TCResearchProgressionSemantics.calculate(entry.stages(), knowledge.getResearchStage(researchKey));

        for (KnowledgeRequirement requirement : consumePlan.knowledgeRequirements()) {
            if (!knowledge.addRaw(
                    requirement.type(),
                    requirement.category(),
                    -requirement.type().pointsToRaw(requirement.points())
            )) {
                return false;
            }
        }

        if (!knowledge.isResearchKnown(researchKey)) {
            knowledge.addResearch(researchKey);
        }
        if (advance != null && !entry.stages().isEmpty()) {
            knowledge.setResearchStage(researchKey, advance.updatedStage());
        }

        boolean completedEntry = entry != null && advance.completed();
        List<KnowledgeRewardGrant> knowledgeRewardGrants = List.of();
        if (completedEntry && sync) {
            knowledge.setResearchFlag(researchKey, TCResearchFlag.POPUP);
            if (!noResearchFlag) {
                knowledge.setResearchFlag(researchKey, TCResearchFlag.RESEARCH);
            }
            knowledgeRewardGrants = applyRewardKnowledge(knowledge, entry);
        }
        List<TCResearchEntryDefinition> unlockedAddenda = completedEntry
                ? unlockTriggeredAddenda(knowledge, researchKey)
                : List.of();

        applyItemConsumePlan(player, consumePlan.itemConsumePlan());
        TCPlayerKnowledgeStore.set(player, knowledge, false);

        if (advance != null) {
            applyLegacyResearchWarp(player, advance.warp());
        }
        if (completedEntry && sync) {
            giveRewardItems(player, entry);
            for (KnowledgeRewardGrant grant : knowledgeRewardGrants) {
                sendKnowledgeGainPayloads(player, grant.type(), grant.category(), grant.gainedPoints());
            }
        }
        for (TCResearchEntryDefinition addendumEntry : unlockedAddenda) {
            player.sendSystemMessage(Component.translatable(
                    "tc.addaddendum",
                    Component.translatable(addendumEntry.name())
            ));
        }

        if (entry != null) {
            completeAvailableSiblings(player, entry, sync);
            if (sync) {
                player.giveExperiencePoints(5);
            }
        }
        if (sync) {
            TCPlayerKnowledgeStore.sync(player);
        }

        return true;
    }

    private static void applyLegacyResearchWarp(ServerPlayer player, int warp) {
        if (player == null || warp <= 0 || TCConfig.WUSS_MODE.get()) {
            return;
        }
        TCResearchProgressionSemantics.WarpAward award = TCResearchProgressionSemantics.splitWarp(warp);
        if (award.permanent() > 0) {
            TCWarpManager.add(player, TCWarpType.PERMANENT, award.permanent());
        }
        if (award.normal() > 0) {
            TCWarpManager.add(player, TCWarpType.NORMAL, award.normal());
        }
    }

    public static TCResearchStageRequirementResult checkCurrentStageRequirements(ServerPlayer player, String key) {
        return checkCurrentStageRequirementsInternal(player, key);
    }

    public static boolean completeCurrentStageWithChecks(ServerPlayer player, String key, boolean sync, boolean noResearchFlag) {
        TCResearchStageRequirementResult result = checkCurrentStageRequirementsInternal(player, key);
        if (!result.hasStage() || !result.passed()) {
            return false;
        }
        return advanceResearch(player, result.researchKey(), sync, noResearchFlag, true);
    }

    public static boolean completeResearch(ServerPlayer player, String key) {
        return completeResearch(player, key, true);
    }

    public static boolean completeResearch(ServerPlayer player, String key, boolean sync) {
        boolean progressed = false;
        while (progressResearch(player, key, sync)) {
            progressed = true;
        }
        return progressed;
    }

    public static boolean completeKnownResearchSiblings(ServerPlayer player, boolean sync) {
        if (player == null) {
            return false;
        }

        boolean changed = false;
        boolean progressed;
        do {
            progressed = false;
            TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
            for (TCResearchEntryDefinition entry : entries()) {
                if (!knowledge.isResearchKnown(entry.key())) {
                    continue;
                }
                for (String sibling : entry.siblings()) {
                    String siblingKey = canonicalResearchKey(sibling);
                    if (!isResearchComplete(knowledge, siblingKey) && doesPlayerHaveRequisites(player, siblingKey)) {
                        boolean siblingProgressed = completeResearch(player, siblingKey, sync);
                        progressed |= siblingProgressed;
                        changed |= siblingProgressed;
                    }
                }
            }
        } while (progressed);

        if (changed && sync) {
            TCPlayerKnowledgeStore.sync(player);
        }
        return changed;
    }

    public static boolean startResearchWithPopup(ServerPlayer player, String key) {
        String researchKey = canonicalResearchKey(key);
        boolean progressed = progressResearch(player, researchKey, true);
        if (progressed) {
            TCPlayerKnowledgeStore.mutate(player, knowledge -> {
                knowledge.setResearchFlag(researchKey, TCResearchFlag.POPUP);
                knowledge.setResearchFlag(researchKey, TCResearchFlag.RESEARCH);
            });
        }
        return progressed;
    }

    public static boolean startResearchFromBrowser(ServerPlayer player, String key) {
        String researchKey = canonicalResearchKey(key);
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        if (knowledge.isResearchKnown(researchKey) || !canUnlockResearch(player, researchKey)) {
            return false;
        }

        // Legacy GuiResearchBrowser sends first=true, checks=false, noFlags=true.
        return progressResearch(player, researchKey, true, true);
    }

    public static boolean acknowledgeResearchEntry(ServerPlayer player, String key) {
        String researchKey = canonicalResearchKey(key);
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        if (!knowledge.isResearchKnown(researchKey)) {
            return false;
        }

        TCResearchEntryDefinition entry = activeData.entries().get(researchKey);
        int stage = knowledge.getResearchStage(researchKey);
        knowledge.clearResearchFlag(researchKey, TCResearchFlag.RESEARCH);
        knowledge.clearResearchFlag(researchKey, TCResearchFlag.PAGE);
        TCPlayerKnowledgeStore.set(player, knowledge, false);

        // Legacy GuiResearchBrowser clears flags first, then attempts checked final-stage progression.
        boolean progressed = entry != null
                && !entry.stages().isEmpty()
                && stage > 1
                && stage >= entry.stages().size()
                && completeCurrentStageWithChecks(player, researchKey, true, false);
        if (!progressed) {
            TCPlayerKnowledgeStore.sync(player);
        }
        return true;
    }

    public static boolean knowsResearchStrict(TCPlayerKnowledge knowledge, String... references) {
        for (String reference : references) {
            if (reference == null || reference.isBlank()) {
                continue;
            }

            String key = stripHiddenPrefix(reference.trim());
            if (key.contains("&&")) {
                if (!knowsResearchStrict(knowledge, key.split("&&"))) {
                    return false;
                }
            } else if (key.contains("||")) {
                boolean any = false;
                for (String part : key.split("\\|\\|")) {
                    if (knowsResearchStrict(knowledge, part)) {
                        any = true;
                        break;
                    }
                }
                if (!any) {
                    return false;
                }
            } else if (key.contains("@")) {
                if (!knowledge.isResearchKnown(key)) {
                    return false;
                }
            } else if (!isResearchComplete(knowledge, key)) {
                return false;
            }
        }

        return true;
    }

    private static void completeAvailableSiblings(ServerPlayer player, TCResearchEntryDefinition entry, boolean sync) {
        for (String sibling : entry.siblings()) {
            TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
            String siblingKey = canonicalResearchKey(sibling);
            if (!isResearchComplete(knowledge, siblingKey) && doesPlayerHaveRequisites(player, siblingKey)) {
                completeResearch(player, siblingKey, sync);
            }
        }
    }

    private static boolean isResearchVisible(ServerPlayer player, TCResearchEntryDefinition entry, Set<String> visiting) {
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        if (isResearchKnown(knowledge, entry.key())) {
            return true;
        }

        if (!visiting.add(entry.key())) {
            return false;
        }

        if (hasMeta(entry, "HIDDEN") && !doesPlayerHaveRequisites(player, entry.key())) {
            visiting.remove(entry.key());
            return false;
        }

        if (entry.parents().isEmpty()) {
            boolean visible = !hasMeta(entry, "HIDDEN");
            visiting.remove(entry.key());
            return visible;
        }

        for (String parent : entry.parents()) {
            TCResearchEntryDefinition parentEntry = activeData.entries().get(canonicalResearchKey(parent));
            if (parentEntry != null && !isResearchVisible(player, parentEntry, visiting)) {
                visiting.remove(entry.key());
                return false;
            }
        }

        visiting.remove(entry.key());
        return true;
    }

    private static boolean hasMeta(TCResearchEntryDefinition entry, String meta) {
        for (String value : entry.meta()) {
            if (value.equalsIgnoreCase(meta)) {
                return true;
            }
        }
        return false;
    }

    private static TCResearchStageRequirementResult checkCurrentStageRequirementsInternal(ServerPlayer player, String key) {
        String researchKey = canonicalResearchKey(key);
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        TCResearchEntryDefinition entry = activeData.entries().get(researchKey);
        ArrayList<String> satisfied = new ArrayList<>();
        ArrayList<String> missing = new ArrayList<>();
        ArrayList<String> blocked = new ArrayList<>();

        if (entry == null || entry.stages().isEmpty()) {
            blocked.add("unknown_or_stage_less_research:" + researchKey);
            return new TCResearchStageRequirementResult(researchKey, -1, 0, satisfied, missing, blocked);
        }

        int stageIndex = knowledge.getResearchStage(researchKey) - 1;
        if (stageIndex < 0) {
            missing.add("research_not_started:" + researchKey);
            return new TCResearchStageRequirementResult(researchKey, stageIndex, entry.stages().size(), satisfied, missing, blocked);
        }

        if (stageIndex >= entry.stages().size()) {
            satisfied.add("research_stage_already_complete:" + researchKey);
            return new TCResearchStageRequirementResult(researchKey, stageIndex, entry.stages().size(), satisfied, missing, blocked);
        }

        TCResearchStageDefinition stage = entry.stages().get(stageIndex);

        for (String required : stage.requiredItem()) {
            ItemRequirement item = parseItemRequirement(required);
            if (item == null) {
                blocked.add("required_item_unresolved:" + required);
                continue;
            }
            if (!hasRequiredItem(player, item)) {
                missing.add("required_item:" + required);
            } else {
                satisfied.add("required_item:" + required);
            }
        }

        for (String required : stage.requiredCraft()) {
            String marker = craftRequirementMarker(required);
            if (!knowledge.isResearchKnown(marker)) {
                missing.add("required_craft:" + required + " marker=" + marker);
            } else {
                satisfied.add("required_craft:" + required + " marker=" + marker);
            }
        }

        for (String required : stage.requiredResearch()) {
            if (!knowsResearchStrict(knowledge, required)) {
                missing.add("required_research:" + required);
            } else {
                satisfied.add("required_research:" + required);
            }
        }

        for (String required : stage.requiredKnowledge()) {
            KnowledgeRequirementResolution resolution = TCResearchRequirementResolver.resolveKnowledgeRequirement(required);
            if (!resolution.resolved()) {
                blocked.add("required_knowledge_unresolved:" + required);
                continue;
            }

            KnowledgeRequirement requirement = resolution.requirement();
            int points = knowledge.getPoints(requirement.type(), requirement.category());
            if (points < requirement.points()) {
                missing.add("required_knowledge:" + required + " has=" + points);
            } else {
                satisfied.add("required_knowledge:" + required + " has=" + points);
            }
        }

        return new TCResearchStageRequirementResult(researchKey, stageIndex, entry.stages().size(), satisfied, missing, blocked);
    }

    private static StageConsumePlan buildCurrentStageConsumePlan(
            ServerPlayer player,
            TCResearchEntryDefinition entry,
            TCPlayerKnowledge knowledge
    ) {
        if (entry == null || entry.stages().isEmpty()) {
            return null;
        }

        int stageIndex = knowledge.getResearchStage(entry.key()) - 1;
        if (stageIndex < 0 || stageIndex >= entry.stages().size()) {
            return null;
        }

        TCResearchStageDefinition stage = entry.stages().get(stageIndex);
        ArrayList<ItemRequirement> itemRequirements = new ArrayList<>();
        ArrayList<KnowledgeRequirement> knowledgeRequirements = new ArrayList<>();

        for (String required : stage.requiredItem()) {
            ItemRequirement item = parseItemRequirement(required);
            if (item == null) {
                return null;
            }
            itemRequirements.add(item);
        }

        for (String required : stage.requiredKnowledge()) {
            KnowledgeRequirementResolution resolution = TCResearchRequirementResolver.resolveKnowledgeRequirement(required);
            if (!resolution.resolved()) {
                return null;
            }
            KnowledgeRequirement requirement = resolution.requirement();
            int rawCost = requirement.type().pointsToRaw(requirement.points());
            if (knowledge.getRaw(requirement.type(), requirement.category()) < rawCost) {
                return null;
            }
            knowledgeRequirements.add(requirement);
        }

        ItemConsumePlan itemConsumePlan = buildItemConsumePlan(player, itemRequirements);
        if (itemConsumePlan == null) {
            return null;
        }
        return new StageConsumePlan(itemConsumePlan, knowledgeRequirements);
    }

    private static List<KnowledgeRewardGrant> applyRewardKnowledge(
            TCPlayerKnowledge knowledge,
            TCResearchEntryDefinition entry
    ) {
        ArrayList<KnowledgeRewardGrant> grants = new ArrayList<>();
        for (String rawReward : entry.rewardKnowledge()) {
            KnowledgeRequirementResolution resolution = TCResearchRequirementResolver.resolveKnowledgeRequirement(rawReward);
            if (!resolution.resolved()) {
                Thaumcraft.LOGGER.warn(
                        "Skipping unresolved research knowledge reward for {}: raw={} reason={}",
                        entry.key(),
                        rawReward,
                        resolution.reason()
                );
                continue;
            }

            KnowledgeRequirement reward = resolution.requirement();
            int before = knowledge.getPoints(reward.type(), reward.category());
            knowledge.addRaw(reward.type(), reward.category(), reward.type().pointsToRaw(reward.points()));
            int gained = Math.max(0, knowledge.getPoints(reward.type(), reward.category()) - before);
            grants.add(new KnowledgeRewardGrant(reward.type(), reward.category(), gained));
        }
        return List.copyOf(grants);
    }

    private static void giveRewardItems(ServerPlayer player, TCResearchEntryDefinition entry) {
        for (String rawReward : entry.rewardItems()) {
            ItemStack reward = createRewardStack(rawReward);
            if (reward.isEmpty()) {
                Thaumcraft.LOGGER.warn(
                        "Skipping unresolved research item reward for {}: raw={}",
                        entry.key(),
                        rawReward
                );
                continue;
            }
            if (!player.getInventory().add(reward)) {
                player.drop(reward, true);
            }
        }
    }

    private static ItemStack createRewardStack(String rawReward) {
        ItemRequirementResolution resolution = TCResearchRequirementResolver.resolveItemRequirement(rawReward);
        if (!resolution.resolved() || resolution.requirement() == null || resolution.requirement().item() == null) {
            return ItemStack.EMPTY;
        }

        ItemRequirement reward = resolution.requirement();
        ItemStack stack = new ItemStack(reward.item(), reward.count());
        if (reward.hasAspectStackRequirement()) {
            stack.set(TCDataComponents.ASPECT_STACK.get(), reward.aspectStack());
        }
        if (reward.hasStoredMagicRequirement()) {
            stack.set(TCDataComponents.STORED_MAGIC.get(), reward.storedMagic());
        }
        if (reward.hasLegacyItemRequirement()) {
            stack.set(TCDataComponents.LEGACY_ITEM.get(), reward.legacyItem());
        }
        return stack;
    }

    private static List<TCResearchEntryDefinition> unlockTriggeredAddenda(
            TCPlayerKnowledge knowledge,
            String completedResearchKey
    ) {
        ArrayList<TCResearchEntryDefinition> unlocked = new ArrayList<>();
        for (TCResearchEntryDefinition candidate : activeData.entries().values()) {
            if (candidate.addenda().isEmpty() || !isResearchComplete(knowledge, candidate.key())) {
                continue;
            }
            for (TCResearchStageDefinition addendum : candidate.addenda()) {
                if (addendum.requiredResearch().contains(completedResearchKey)) {
                    knowledge.setResearchFlag(candidate.key(), TCResearchFlag.PAGE);
                    unlocked.add(candidate);
                    break;
                }
            }
        }
        return List.copyOf(unlocked);
    }

    public static void markCraftedResearchReferences(ServerPlayer player, ItemStack crafted) {
        LinkedHashSet<String> markers = new LinkedHashSet<>();

        for (TCResearchEntryDefinition entry : activeData.entries().values()) {
            for (TCResearchStageDefinition stage : entry.stages()) {
                for (String required : stage.requiredCraft()) {
                    if (craftedStackMatchesCraftRequirement(crafted, required)) {
                        markers.add(craftRequirementMarker(required));
                    }
                }
            }
        }

        for (String marker : markers) {
            progressResearch(player, marker, true);
        }
    }

    private static boolean craftedStackMatchesCraftRequirement(ItemStack crafted, String rawRequirement) {
        ItemRequirement requirement = parseItemRequirement(rawRequirement);
        return requirement != null && matchesRequirement(crafted, requirement);
    }

    private static ItemRequirement parseItemRequirement(String raw) {
        ItemRequirementResolution resolution = TCResearchRequirementResolver.resolveItemRequirement(raw);
        return resolution.resolved() ? resolution.requirement() : null;
    }

    private static boolean hasRequiredItem(ServerPlayer player, ItemRequirement requirement) {
        return countRequiredItem(player, requirement) >= requirement.count();
    }

    private static int countRequiredItem(ServerPlayer player, ItemRequirement requirement) {
        int count = 0;
        Inventory inventory = player.getInventory();
        for (ItemStack stack : inventory.items) {
            if (matchesRequirement(stack, requirement)) {
                count += stack.getCount();
                if (count >= requirement.count()) {
                    return count;
                }
            }
        }
        return count;
    }

    private static ItemConsumePlan buildItemConsumePlan(ServerPlayer player, List<ItemRequirement> requirements) {
        ArrayList<ItemStack> simulatedInventory = new ArrayList<>();
        for (ItemStack stack : player.getInventory().items) {
            simulatedInventory.add(stack.copy());
        }

        HashMap<Integer, Integer> slotCounts = new HashMap<>();
        for (ItemRequirement requirement : requirements) {
            if (!planRequiredItem(simulatedInventory, requirement, slotCounts)) {
                return null;
            }
        }
        return new ItemConsumePlan(slotCounts);
    }

    private static boolean planRequiredItem(List<ItemStack> stacks, ItemRequirement requirement, Map<Integer, Integer> slotCounts) {
        int remaining = requirement.count();

        for (int slot = 0; slot < stacks.size() && remaining > 0; slot++) {
            ItemStack stack = stacks.get(slot);
            if (!matchesRequirement(stack, requirement)) {
                continue;
            }

            int consumed = Math.min(remaining, stack.getCount());
            stack.shrink(consumed);
            slotCounts.merge(slot, consumed, Integer::sum);
            remaining -= consumed;
            if (stack.isEmpty()) {
                stacks.set(slot, ItemStack.EMPTY);
            }
        }

        return remaining <= 0;
    }

    private static void applyItemConsumePlan(ServerPlayer player, ItemConsumePlan plan) {
        if (plan.slotCounts().isEmpty()) {
            return;
        }

        Inventory inventory = player.getInventory();
        for (Map.Entry<Integer, Integer> entry : plan.slotCounts().entrySet()) {
            int slot = entry.getKey();
            if (slot < 0 || slot >= inventory.items.size()) {
                continue;
            }
            inventory.items.get(slot).shrink(entry.getValue());
        }
        inventory.setChanged();
    }

    private static boolean matchesRequirement(ItemStack stack, ItemRequirement requirement) {
        if (stack.isEmpty()) {
            return false;
        }
        if (!matchesRequirementIdentity(stack, requirement)) {
            return false;
        }
        return matchesAspectStackRequirement(stack, requirement)
                && matchesStoredMagicRequirement(stack, requirement)
                && matchesLegacyItemRequirement(stack, requirement);
    }

    private static boolean matchesRequirementIdentity(ItemStack stack, ItemRequirement requirement) {
        if (requirement.anyItem()) {
            return true;
        }
        if (requirement.item() != null && stack.is(requirement.item())) {
            return true;
        }
        for (TagKey<Item> tag : requirement.tags()) {
            if (stack.is(tag)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesAspectStackRequirement(ItemStack stack, ItemRequirement requirement) {
        if (!requirement.hasAspectStackRequirement()) {
            return true;
        }
        TCAspectStackComponent required = requirement.aspectStack();
        TCAspectStackComponent actual = stack.get(TCDataComponents.ASPECT_STACK.get());
        if (actual == null || actual.isEmpty()) {
            return false;
        }
        return actual.aspect().equals(required.aspect()) && actual.amount() >= required.amount();
    }

    private static boolean matchesStoredMagicRequirement(ItemStack stack, ItemRequirement requirement) {
        if (!requirement.hasStoredMagicRequirement()) {
            return true;
        }
        TCStoredEnchantComponent required = requirement.storedMagic();
        TCStoredEnchantComponent actual = stack.get(TCDataComponents.STORED_MAGIC.get());
        if (actual != null && !actual.isEmpty()) {
            return actual.id().equals(required.id()) && actual.level() >= required.level();
        }
        return matchesVanillaEnchantmentRequirement(stack, required);
    }

    private static boolean matchesVanillaEnchantmentRequirement(ItemStack stack, TCStoredEnchantComponent required) {
        ItemEnchantments enchantments = stack.is(Items.ENCHANTED_BOOK)
                ? stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                : stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            String id = entry.getKey().unwrapKey()
                    .map(key -> key.location().getPath())
                    .orElse("");
            if (id.equals(required.id()) && entry.getIntValue() >= required.level()) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesLegacyItemRequirement(ItemStack stack, ItemRequirement requirement) {
        if (!requirement.hasLegacyItemRequirement()) {
            return true;
        }
        TCLegacyItemComponent required = requirement.legacyItem();
        TCLegacyItemComponent actual = stack.get(TCDataComponents.LEGACY_ITEM.get());
        if (actual == null || actual.isEmpty()) {
            return false;
        }
        return actual.family().equals(required.family())
                && actual.variant().equals(required.variant())
                && actual.metadata() == required.metadata();
    }

    private static String craftRequirementMarker(String raw) {
        if (raw != null && raw.startsWith("oredict:")) {
            return "[#]" + javaStringHash("oredict:" + raw.substring("oredict:".length()));
        }
        return "[#]craft:" + (raw == null ? "" : raw.trim().replace('\'', '"'));
    }

    private static int javaStringHash(String value) {
        int hash = 0;
        for (int i = 0; i < value.length(); i++) {
            hash = 31 * hash + value.charAt(i);
        }
        return hash;
    }

    private static String canonicalResearchKey(String key) {
        return TCPlayerKnowledge.baseResearchKey(stripHiddenPrefix(key));
    }

    private static String stripHiddenPrefix(String key) {
        String stripped = key == null ? "" : key.trim();
        while (stripped.startsWith("~")) {
            stripped = stripped.substring(1);
        }
        return stripped;
    }

    private record ItemConsumePlan(Map<Integer, Integer> slotCounts) {
        private ItemConsumePlan {
            slotCounts = Map.copyOf(slotCounts);
        }
    }

    private record StageConsumePlan(
            ItemConsumePlan itemConsumePlan,
            List<KnowledgeRequirement> knowledgeRequirements
    ) {
        private StageConsumePlan {
            knowledgeRequirements = List.copyOf(knowledgeRequirements);
        }

        static StageConsumePlan empty() {
            return new StageConsumePlan(new ItemConsumePlan(Map.of()), List.of());
        }
    }

    private record KnowledgeRewardGrant(
            TCKnowledgeType type,
            String category,
            int gainedPoints
    ) {
    }
}
