package thaumcraft.common.research;

final class TCLegacyMaterialFamilyMappings {
    private static final LegacyMaterialMapping[] CONFIRMED_MAPPINGS = {
            new LegacyMaterialMapping("thaumcraft:ingot", 0, "thaumium ingot", "thaumcraft:thaumium_ingot"),
            new LegacyMaterialMapping("thaumcraft:ingot", 2, "brass ingot", "thaumcraft:brass_ingot"),
            new LegacyMaterialMapping("thaumcraft:plate", 2, "thaumium plate", "thaumcraft:thaumium_plate"),
            new LegacyMaterialMapping("thaumcraft:plate", 3, "void plate", "thaumcraft:void_plate"),
            new LegacyMaterialMapping("thaumcraft:metal", 2, "thaumium metal", "thaumcraft:thaumium_metal"),
            new LegacyMaterialMapping("thaumcraft:metal", 3, "void metal", "thaumcraft:void_metal")
    };

    private TCLegacyMaterialFamilyMappings() {
    }

    static Classification classify(String rawId, String damageText) {
        if (!isMaterialFamily(rawId)) {
            return null;
        }
        int damage = parsePositiveInt(damageText, 0);
        LegacyMaterialMapping mapping = confirmedMapping(rawId, damage);
        if (mapping != null) {
            return new Classification(
                    "legacy material-family target not implemented yet: " + rawId + ";damage=" + damage
                            + " -> " + mapping.modernItemId() + " (" + mapping.legacyMaterialName() + ")",
                    "legacy material-family target not implemented: " + mapping.legacyMaterialName()
            );
        }
        return new Classification(
                "legacy material-family requirement not mapped yet: " + rawId + ";damage=" + damage,
                "legacy material-family requirement: " + rawId
        );
    }

    static boolean isMaterialFamily(String rawId) {
        return rawId.equals("thaumcraft:ingot")
                || rawId.equals("thaumcraft:metal")
                || rawId.equals("thaumcraft:plate")
                || rawId.equals("thaumcraft:nugget");
    }

    private static LegacyMaterialMapping confirmedMapping(String rawId, int damage) {
        for (LegacyMaterialMapping mapping : CONFIRMED_MAPPINGS) {
            if (mapping.rawId().equals(rawId) && mapping.damage() == damage) {
                return mapping;
            }
        }
        return null;
    }

    private static int parsePositiveInt(String value, int fallback) {
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed < 0 ? fallback : parsed;
        } catch (Exception ignored) {
            return fallback;
        }
    }

    record Classification(String reason, String summaryKey) {
    }

    private record LegacyMaterialMapping(String rawId, int damage, String legacyMaterialName, String modernItemId) {
    }
}
