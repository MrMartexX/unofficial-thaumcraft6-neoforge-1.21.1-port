package thaumcraft.common.research;

public record TCThaumonomiconCategoryView(
        String key,
        String requiredResearch,
        String icon,
        String background,
        String overlay,
        int completionPercent
) {
    public TCThaumonomiconCategoryView(
            String key,
            String requiredResearch,
            String icon,
            String background,
            String overlay
    ) {
        this(key, requiredResearch, icon, background, overlay, 0);
    }

    public TCThaumonomiconCategoryView {
        key = safe(key);
        requiredResearch = safe(requiredResearch);
        icon = safe(icon);
        background = safe(background);
        overlay = safe(overlay);
        completionPercent = Math.max(0, Math.min(100, completionPercent));
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
