package thaumcraft.common.research;

public record TCThaumonomiconCategoryView(
        String key,
        String requiredResearch,
        String icon,
        String background,
        String overlay
) {
    public TCThaumonomiconCategoryView {
        key = safe(key);
        requiredResearch = safe(requiredResearch);
        icon = safe(icon);
        background = safe(background);
        overlay = safe(overlay);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
