package thaumcraft.common.warp;

import java.util.Locale;

public enum TCWarpType {
    PERMANENT,
    NORMAL,
    TEMPORARY;

    public static TCWarpType parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }
}
