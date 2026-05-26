package thaumcraft.common.research;

final class TCLegacyMaterialFamilyMappings {
    private TCLegacyMaterialFamilyMappings() {
    }

    static Classification classify(String rawId, String damageText) {
        if (!isMaterialFamily(rawId)) {
            return null;
        }
        int damage = parsePositiveInt(damageText, 0);
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
}
