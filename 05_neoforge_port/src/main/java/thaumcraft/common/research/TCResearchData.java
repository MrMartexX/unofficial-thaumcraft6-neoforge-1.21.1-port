package thaumcraft.common.research;

import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.Thaumcraft;

record TCResearchData(
        Map<String, TCResearchCategoryDefinition> categories,
        Map<String, TCResearchEntryDefinition> entries
) {
    TCResearchData {
        categories = Collections.unmodifiableMap(new LinkedHashMap<>(categories));
        entries = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
    }

    static TCResearchData empty() {
        return new TCResearchData(legacyCategories(), Map.of());
    }

    int stageCount() {
        return entries.values().stream().mapToInt(entry -> entry.stages().size()).sum();
    }

    int addendumCount() {
        return entries.values().stream().mapToInt(entry -> entry.addenda().size()).sum();
    }

    TCResearchValidationReport validateResearchReferences() {
        ArrayList<TCResearchReference> unresolved = new ArrayList<>();
        int entryReferences = 0;
        int externalReferences = 0;

        for (TCResearchEntryDefinition entry : entries.values()) {
            ReferenceCounts counts = validateReferences(entry, "parents", entry.parents(), unresolved);
            entryReferences += counts.entryReferences();
            externalReferences += counts.externalReferences();

            counts = validateReferences(entry, "siblings", entry.siblings(), unresolved);
            entryReferences += counts.entryReferences();
            externalReferences += counts.externalReferences();

            for (int i = 0; i < entry.stages().size(); i++) {
                counts = validateReferences(entry, "stages[" + i + "].required_research", entry.stages().get(i).requiredResearch(), unresolved);
                entryReferences += counts.entryReferences();
                externalReferences += counts.externalReferences();
            }

            for (int i = 0; i < entry.addenda().size(); i++) {
                counts = validateReferences(entry, "addenda[" + i + "].required_research", entry.addenda().get(i).requiredResearch(), unresolved);
                entryReferences += counts.entryReferences();
                externalReferences += counts.externalReferences();
            }
        }

        return new TCResearchValidationReport(entryReferences, externalReferences, unresolved);
    }

    private ReferenceCounts validateReferences(TCResearchEntryDefinition owner, String location, List<String> references, List<TCResearchReference> unresolved) {
        int entryReferences = 0;
        int externalReferences = 0;

        for (String rawReference : references) {
            String normalized = normalizeReference(rawReference);
            if (normalized.isEmpty()) {
                continue;
            }

            if (entries.containsKey(normalized)) {
                entryReferences++;
            } else if (isExternalResearchTrigger(normalized)) {
                externalReferences++;
            } else {
                unresolved.add(new TCResearchReference(owner.key(), location, rawReference, normalized));
            }
        }

        return new ReferenceCounts(entryReferences, externalReferences);
    }

    private static String normalizeReference(String rawReference) {
        if (rawReference == null || rawReference.isBlank()) {
            return "";
        }

        String key = rawReference.trim();
        while (key.startsWith("~")) {
            key = key.substring(1);
        }

        int stageSeparator = key.indexOf('@');
        if (stageSeparator >= 0) {
            key = key.substring(0, stageSeparator);
        }

        return key.trim();
    }

    private static boolean isExternalResearchTrigger(String key) {
        return key.startsWith("!") || key.startsWith("f_") || key.startsWith("m_");
    }

    TCResearchData withEntries(List<TCResearchEntryDefinition> parsedEntries) {
        LinkedHashMap<String, TCResearchCategoryDefinition> categoryMap = new LinkedHashMap<>(categories);
        LinkedHashMap<String, TCResearchEntryDefinition> entryMap = new LinkedHashMap<>();

        for (TCResearchEntryDefinition entry : parsedEntries) {
            categoryMap.computeIfAbsent(entry.category(), TCResearchData::placeholderCategory);
            TCResearchEntryDefinition previous = entryMap.put(entry.key(), entry);
            if (previous != null) {
                throw new IllegalArgumentException("Duplicate research entry key " + entry.key());
            }
        }

        return new TCResearchData(categoryMap, entryMap);
    }

    private static LinkedHashMap<String, TCResearchCategoryDefinition> legacyCategories() {
        LinkedHashMap<String, TCResearchCategoryDefinition> categories = new LinkedHashMap<>();
        put(categories, "BASICS", "", tags(Aspect.PLANT, 5, Aspect.ORDER, 5, Aspect.ENTROPY, 5, Aspect.AIR, 5, Aspect.FIRE, 5, Aspect.EARTH, 3, Aspect.WATER, 5),
                "textures/items/thaumonomicon_cheat.png", "textures/gui/gui_research_back_1.png");
        put(categories, "AUROMANCY", "UNLOCKAUROMANCY", tags(Aspect.AURA, 20, Aspect.MAGIC, 20, Aspect.FLUX, 15, Aspect.CRYSTAL, 5, Aspect.COLD, 5, Aspect.AIR, 5),
                "textures/research/cat_auromancy.png", "textures/gui/gui_research_back_2.png");
        put(categories, "ALCHEMY", "UNLOCKALCHEMY", tags(Aspect.ALCHEMY, 30, Aspect.FLUX, 10, Aspect.MAGIC, 10, Aspect.LIFE, 5, Aspect.AVERSION, 5, Aspect.DESIRE, 5, Aspect.WATER, 5),
                "textures/research/cat_alchemy.png", "textures/gui/gui_research_back_3.png");
        put(categories, "ARTIFICE", "UNLOCKARTIFICE", tags(Aspect.MECHANISM, 10, Aspect.CRAFT, 10, Aspect.METAL, 10, Aspect.TOOL, 10, Aspect.ENERGY, 10, Aspect.LIGHT, 5, Aspect.FLIGHT, 5, Aspect.TRAP, 5, Aspect.FIRE, 5),
                "textures/research/cat_artifice.png", "textures/gui/gui_research_back_4.png");
        put(categories, "INFUSION", "UNLOCKINFUSION", tags(Aspect.MAGIC, 30, Aspect.PROTECT, 10, Aspect.TOOL, 10, Aspect.FLUX, 5, Aspect.CRAFT, 5, Aspect.SOUL, 5, Aspect.EARTH, 3),
                "textures/research/cat_infusion.png", "textures/gui/gui_research_back_7.png");
        put(categories, "GOLEMANCY", "UNLOCKGOLEMANCY", tags(Aspect.MAN, 20, Aspect.MOTION, 10, Aspect.MIND, 10, Aspect.MECHANISM, 10, Aspect.EXCHANGE, 5, Aspect.SENSES, 5, Aspect.BEAST, 5, Aspect.ORDER, 5),
                "textures/research/cat_golemancy.png", "textures/gui/gui_research_back_5.png");
        put(categories, "ELDRITCH", "UNLOCKELDRITCH", tags(Aspect.ELDRITCH, 20, Aspect.DARKNESS, 10, Aspect.MAGIC, 5, Aspect.MIND, 5, Aspect.VOID, 5, Aspect.DEATH, 5, Aspect.UNDEAD, 5, Aspect.ENTROPY, 5),
                "textures/research/cat_eldritch.png", "textures/gui/gui_research_back_6.png");
        return categories;
    }

    private static void put(LinkedHashMap<String, TCResearchCategoryDefinition> categories, String key, String requiredResearch, AspectList formula, String icon, String background) {
        categories.put(key, new TCResearchCategoryDefinition(
                key,
                requiredResearch,
                formula,
                ThaumcraftLocation.assets(icon),
                ThaumcraftLocation.assets(background),
                ThaumcraftLocation.assets("textures/gui/gui_research_back_over.png")
        ));
    }

    private static TCResearchCategoryDefinition placeholderCategory(String key) {
        return new TCResearchCategoryDefinition(key, "", new AspectList(), ThaumcraftLocation.assets("textures/items/thaumonomicon.png"), null, null);
    }

    private static AspectList tags(Object... pairs) {
        AspectList list = new AspectList();
        for (int i = 0; i < pairs.length; i += 2) {
            list.add((Aspect) pairs[i], (Integer) pairs[i + 1]);
        }
        return list;
    }

    private static final class ThaumcraftLocation {
        static net.minecraft.resources.ResourceLocation assets(String path) {
            return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path);
        }
    }

    private record ReferenceCounts(int entryReferences, int externalReferences) {
    }
}
