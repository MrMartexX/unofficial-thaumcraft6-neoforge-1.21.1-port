package thaumcraft.common.research;

import java.util.Collection;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.AspectList;

public final class TCResearchManager {
    private static TCResearchData activeData = TCResearchData.empty();

    private TCResearchManager() {
    }

    public static void bootstrap() {
        activeData = TCResearchData.empty();
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

    public static Optional<TCResearchEntryDefinition> getEntry(String key) {
        return Optional.ofNullable(activeData.entries().get(canonicalResearchKey(key)));
    }

    public static Collection<TCResearchCategoryDefinition> categories() {
        return activeData.categories().values();
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

        final boolean[] changed = {false};
        TCPlayerKnowledgeStore.mutate(player, knowledge -> changed[0] = knowledge.addRaw(type, category, rawAmount));
        return changed[0];
    }

    public static boolean addObservationFromScan(ServerPlayer player, AspectList aspects) {
        if (player == null || aspects == null || aspects.size() == 0 || aspects.visSize() == 0) {
            return false;
        }

        final boolean[] changed = {false};
        TCPlayerKnowledgeStore.mutate(player, knowledge -> {
            for (TCResearchCategoryDefinition category : categories()) {
                int rawAmount = category.applyFormula(aspects);
                if (rawAmount > 0) {
                    changed[0] |= knowledge.addRaw(TCKnowledgeType.OBSERVATION, category.key(), rawAmount);
                }
            }
        });
        return changed[0];
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
        return progressResearch(player, key, sync, false);
    }

    private static boolean progressResearch(ServerPlayer player, String key, boolean sync, boolean noResearchFlag) {
        String researchKey = canonicalResearchKey(key);
        if (researchKey.isBlank()) {
            return false;
        }

        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        if (isResearchComplete(knowledge, researchKey) || !doesPlayerHaveRequisites(player, researchKey)) {
            return false;
        }

        TCResearchEntryDefinition entry = activeData.entries().get(researchKey);
        TCPlayerKnowledgeStore.mutate(player, storedKnowledge -> {
            if (!storedKnowledge.isResearchKnown(researchKey)) {
                storedKnowledge.addResearch(researchKey);
            }

            if (entry != null && !entry.stages().isEmpty()) {
                int stage = storedKnowledge.getResearchStage(researchKey);
                if (stage < 0) {
                    stage = 0;
                }

                if (entry.stages().size() == 1 && stage == 0 && isEmptyGateStage(entry.stages().getFirst())) {
                    stage++;
                } else if (entry.stages().size() > 1
                        && entry.stages().size() <= stage + 1
                        && stage < entry.stages().size()
                        && isEmptyGateStage(entry.stages().get(stage))) {
                    stage++;
                }

                storedKnowledge.setResearchStage(researchKey, Math.min(entry.stages().size() + 1, stage + 1));
            }

            if (sync && entry != null && getResearchStatus(storedKnowledge, researchKey) == TCResearchStatus.COMPLETE) {
                storedKnowledge.setResearchFlag(researchKey, TCResearchFlag.POPUP);
                if (!noResearchFlag) {
                    storedKnowledge.setResearchFlag(researchKey, TCResearchFlag.RESEARCH);
                }
            }
        });

        if (entry != null) {
            completeAvailableSiblings(player, entry, sync);
            if (sync) {
                player.giveExperiencePoints(5);
            }
        }

        return true;
    }

    public static TCResearchStageRequirementResult checkCurrentStageRequirements(ServerPlayer player, String key) {
        return checkCurrentStageRequirementsInternal(player, key);
    }

    public static boolean completeCurrentStageWithChecks(ServerPlayer player, String key, boolean sync, boolean noResearchFlag) {
        TCResearchStageRequirementResult result = checkCurrentStageRequirementsInternal(player, key);
        if (!result.hasStage() || !result.passed()) {
            return false;
        }

        if (!consumeCurrentStageRequirements(player, result.researchKey())) {
            return false;
        }

        return progressResearch(player, key, sync, noResearchFlag);
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

    private static boolean isEmptyGateStage(TCResearchStageDefinition stage) {
        return stage.requiredCraft().isEmpty()
                && stage.requiredItem().isEmpty()
                && stage.requiredKnowledge().isEmpty()
                && stage.requiredResearch().isEmpty();
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
            KnowledgeRequirement requirement = KnowledgeRequirement.parse(required);
            if (requirement == null) {
                blocked.add("required_knowledge_unresolved:" + required);
                continue;
            }

            int points = knowledge.getPoints(requirement.type(), requirement.category());
            if (points < requirement.points()) {
                missing.add("required_knowledge:" + required + " has=" + points);
            } else {
                satisfied.add("required_knowledge:" + required + " has=" + points);
            }
        }

        return new TCResearchStageRequirementResult(researchKey, stageIndex, entry.stages().size(), satisfied, missing, blocked);
    }

    private static boolean consumeCurrentStageRequirements(ServerPlayer player, String key) {
        String researchKey = canonicalResearchKey(key);
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        TCResearchEntryDefinition entry = activeData.entries().get(researchKey);
        if (entry == null || entry.stages().isEmpty()) {
            return false;
        }

        int stageIndex = knowledge.getResearchStage(researchKey) - 1;
        if (stageIndex < 0 || stageIndex >= entry.stages().size()) {
            return false;
        }

        TCResearchStageDefinition stage = entry.stages().get(stageIndex);
        ArrayList<ItemRequirement> itemRequirements = new ArrayList<>();
        ArrayList<KnowledgeRequirement> knowledgeRequirements = new ArrayList<>();

        for (String required : stage.requiredItem()) {
            ItemRequirement item = parseItemRequirement(required);
            if (item == null) {
                return false;
            }
            itemRequirements.add(item);
        }

        for (String required : stage.requiredKnowledge()) {
            KnowledgeRequirement requirement = KnowledgeRequirement.parse(required);
            if (requirement == null) {
                return false;
            }
            int rawCost = requirement.type().pointsToRaw(requirement.points());
            if (knowledge.getRaw(requirement.type(), requirement.category()) < rawCost) {
                return false;
            }
            knowledgeRequirements.add(requirement);
        }

        ArrayList<ItemStack> simulatedInventory = new ArrayList<>();
        for (ItemStack stack : player.getInventory().items) {
            simulatedInventory.add(stack.copy());
        }

        for (ItemRequirement requirement : itemRequirements) {
            if (!consumeRequiredItemFromStacks(simulatedInventory, requirement)) {
                return false;
            }
        }

        for (ItemRequirement requirement : itemRequirements) {
            if (!consumeRequiredItem(player, requirement)) {
                return false;
            }
        }

        if (!knowledgeRequirements.isEmpty()) {
            TCPlayerKnowledgeStore.mutate(player, storedKnowledge -> {
                for (KnowledgeRequirement requirement : knowledgeRequirements) {
                    storedKnowledge.addRaw(
                            requirement.type(),
                            requirement.category(),
                            -requirement.type().pointsToRaw(requirement.points())
                    );
                }
            });
        }

        return true;
    }

    private static void markCraftedResearchReferences(ServerPlayer player, ItemStack crafted) {
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
        if (raw == null || raw.isBlank()) {
            return null;
        }

        String value = raw.trim().replace('\'', '"');
        if (value.startsWith("oredict:")) {
            String oreName = value.substring("oredict:".length());
            if (oreName.isBlank()) {
                return null;
            }
            return ItemRequirement.tags(raw, oreDictionaryItemTags(oreName), 1);
        }

        String[] split = value.split(";");
        ResourceLocation id;
        try {
            id = ResourceLocation.parse(legacyFlattenedItemId(split[0], split.length > 2 ? split[2] : "0"));
        } catch (Exception ignored) {
            return null;
        }

        Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        if (item == null) {
            return null;
        }

        int count = parsePositiveInt(split.length > 1 ? split[1] : "", 1);
        int damage = parsePositiveInt(split.length > 2 ? split[2] : "", 0);
        boolean hasNbt = value.contains("{");

        if (hasNbt || damage > 0 && legacyFlattenedItemId(split[0], split[2]).equals(split[0])) {
            return null;
        }

        return ItemRequirement.item(raw, item, count);
    }

    private static String legacyFlattenedItemId(String id, String damageText) {
        int damage = parsePositiveInt(damageText, 0);
        if (id.equals("minecraft:web")) {
            return "minecraft:cobweb";
        }
        if (id.equals("minecraft:noteblock")) {
            return "minecraft:note_block";
        }
        if (id.equals("minecraft:dye")) {
            return switch (damage) {
                case 15 -> "minecraft:bone_meal";
                case 4 -> "minecraft:lapis_lazuli";
                case 3 -> "minecraft:cocoa_beans";
                default -> "minecraft:ink_sac";
            };
        }
        return id;
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

    private static boolean consumeRequiredItem(ServerPlayer player, ItemRequirement requirement) {
        Inventory inventory = player.getInventory();
        boolean consumed = consumeRequiredItemFromStacks(inventory.items, requirement);
        if (consumed) {
            inventory.setChanged();
        }
        return consumed;
    }

    private static boolean consumeRequiredItemFromStacks(List<ItemStack> stacks, ItemRequirement requirement) {
        int remaining = requirement.count();

        for (int slot = 0; slot < stacks.size() && remaining > 0; slot++) {
            ItemStack stack = stacks.get(slot);
            if (!matchesRequirement(stack, requirement)) {
                continue;
            }

            int consumed = Math.min(remaining, stack.getCount());
            stack.shrink(consumed);
            remaining -= consumed;
            if (stack.isEmpty()) {
                stacks.set(slot, ItemStack.EMPTY);
            }
        }

        return remaining <= 0;
    }

    private static boolean matchesRequirement(ItemStack stack, ItemRequirement requirement) {
        if (stack.isEmpty()) {
            return false;
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

    private static String craftRequirementMarker(String raw) {
        if (raw != null && raw.startsWith("oredict:")) {
            return "[#]" + javaStringHash("oredict:" + raw.substring("oredict:".length()));
        }
        return "[#]craft:" + (raw == null ? "" : raw.trim().replace('\'', '"'));
    }

    private static List<TagKey<Item>> oreDictionaryItemTags(String oreName) {
        ArrayList<TagKey<Item>> tags = new ArrayList<>();
        String commonPath = commonTagPath(oreName);
        if (commonPath != null) {
            tags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", commonPath)));
        }
        tags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "legacy_ore_dictionary/" + camelToSnake(oreName))));
        return tags;
    }

    private static String commonTagPath(String entry) {
        String lower = entry.toLowerCase(java.util.Locale.ROOT);
        if (lower.startsWith("ore") && entry.length() > 3) {
            return "ores/" + camelToSnake(entry.substring(3));
        }
        if (lower.startsWith("ingot") && entry.length() > 5) {
            return "ingots/" + camelToSnake(entry.substring(5));
        }
        if (lower.startsWith("block") && entry.length() > 5) {
            return "storage_blocks/" + camelToSnake(entry.substring(5));
        }
        if (lower.startsWith("plate") && entry.length() > 5) {
            return "plates/" + camelToSnake(entry.substring(5));
        }
        if (lower.startsWith("gem") && entry.length() > 3) {
            return "gems/" + camelToSnake(entry.substring(3));
        }
        if (lower.startsWith("dust") && entry.length() > 4) {
            return "dusts/" + camelToSnake(entry.substring(4));
        }
        if (lower.startsWith("nugget") && entry.length() > 6) {
            return "nuggets/" + camelToSnake(entry.substring(6));
        }
        return null;
    }

    private static String camelToSnake(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (Character.isUpperCase(ch) && builder.length() > 0) {
                builder.append('_');
            }
            builder.append(Character.toLowerCase(ch));
        }
        return builder.toString();
    }

    private static int javaStringHash(String value) {
        int hash = 0;
        for (int i = 0; i < value.length(); i++) {
            hash = 31 * hash + value.charAt(i);
        }
        return hash;
    }

    private static int parsePositiveInt(String value, int fallback) {
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed < 0 ? fallback : parsed;
        } catch (Exception ignored) {
            return fallback;
        }
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

    private record ItemRequirement(String raw, Item item, List<TagKey<Item>> tags, int count) {
        private ItemRequirement {
            tags = List.copyOf(tags);
        }

        static ItemRequirement item(String raw, Item item, int count) {
            return new ItemRequirement(raw, item, List.of(), count);
        }

        static ItemRequirement tags(String raw, List<TagKey<Item>> tags, int count) {
            return new ItemRequirement(raw, null, tags, count);
        }
    }

    private record KnowledgeRequirement(String raw, TCKnowledgeType type, String category, int points) {
        static KnowledgeRequirement parse(String raw) {
            if (raw == null || raw.isBlank()) {
                return null;
            }

            String[] split = raw.split(";");
            if (split.length != 3) {
                return null;
            }

            TCKnowledgeType type = TCKnowledgeType.parse(split[0]);
            String category = TCPlayerKnowledge.normalizeCategory(split[1]);
            int points = parsePositiveInt(split[2], 0);
            if (type == null || points <= 0) {
                return null;
            }

            return new KnowledgeRequirement(raw, type, category, points);
        }
    }
}
