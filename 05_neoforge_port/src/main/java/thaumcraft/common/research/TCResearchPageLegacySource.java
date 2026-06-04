package thaumcraft.common.research;

import java.util.Locale;

public enum TCResearchPageLegacySource {
    CRAFTING_REGISTRY,
    FAKE_CATALOG,
    MISSING,
    RECIPE_GROUP,
    THAUMCRAFT_CATALOG;

    static TCResearchPageLegacySource parse(String value) {
        return valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
