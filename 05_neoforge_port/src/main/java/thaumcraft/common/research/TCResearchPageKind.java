package thaumcraft.common.research;

import java.util.Locale;

public enum TCResearchPageKind {
    ARCANE,
    BLUEPRINT,
    CRAFTING,
    CRUCIBLE,
    GROUP,
    INFUSION,
    MISSING;

    static TCResearchPageKind parse(String value) {
        return valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
