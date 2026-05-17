package thaumcraft.common.research;

import java.util.List;

record TCResearchEntryDefinition(
        String key,
        String name,
        List<String> icons,
        String category,
        int locationX,
        int locationY,
        List<String> parents,
        List<String> siblings,
        List<String> meta,
        List<TCResearchStageDefinition> stages,
        List<TCResearchStageDefinition> addenda
) {
    TCResearchEntryDefinition {
        key = TCPlayerKnowledge.normalizeResearchKey(key);
        name = name == null ? "" : name.trim();
        icons = List.copyOf(icons);
        category = TCPlayerKnowledge.normalizeCategory(category);
        parents = List.copyOf(parents);
        siblings = List.copyOf(siblings);
        meta = List.copyOf(meta);
        stages = List.copyOf(stages);
        addenda = List.copyOf(addenda);
    }
}
