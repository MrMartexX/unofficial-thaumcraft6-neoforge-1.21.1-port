package thaumcraft.common.research;

final class TCLegacyMaterialFamilyMappings {
    private static final LegacyMaterialTarget[] INFERRED_TARGETS = {
            new LegacyMaterialTarget("thaumcraft:ingot", 0, "thaumium ingot", "thaumcraft:thaumium_ingot"),
            new LegacyMaterialTarget("thaumcraft:ingot", 2, "brass ingot", "thaumcraft:brass_ingot"),
            new LegacyMaterialTarget("thaumcraft:plate", 2, "thaumium plate", "thaumcraft:thaumium_plate"),
            new LegacyMaterialTarget("thaumcraft:plate", 3, "void plate", "thaumcraft:void_plate"),
            new LegacyMaterialTarget("thaumcraft:metal", 2, "thaumium metal", "thaumcraft:thaumium_metal"),
            new LegacyMaterialTarget("thaumcraft:metal", 3, "void metal", "thaumcraft:void_metal")
    };

    private TCLegacyMaterialFamilyMappings() {
    }

    static Classification classify(String rawId, String damageText) {
        if (!isMaterialFamily(rawId)) {
            return null;
        }
        int damage = parsePositiveInt(damageText, 0);
        LegacyMaterialTarget target = inferredTarget(rawId, damage);
        if (target != null) {
            return new Classification(
                    "legacy material-family target not implemented yet: " + rawId + ";damage=" + damage
                            + " -> " + target.modernItemId() + " (" + target.legacyMaterialName() + ")",
                    "legacy material-family target not implemented: " + target.legacyMaterialName()
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

    private static LegacyMaterialTarget inferredTarget(String rawId, int damage) {
        for (LegacyMaterialTarget target : INFERRED_TARGETS) {
            if (target.rawId().equals(rawId) && target.damage() == damage) {
                return target;
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

    private record LegacyMaterialTarget(String rawId, int damage, String legacyMaterialName, String modernItemId) {
    }
}
