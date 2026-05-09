package thaumcraft.common.research;

import java.util.Locale;

public enum TCKnowledgeType {
    OBSERVATION("observation", 16),
    THEORY("theory", 32);

    private final String id;
    private final int rawUnitsPerPoint;

    TCKnowledgeType(String id, int rawUnitsPerPoint) {
        this.id = id;
        this.rawUnitsPerPoint = rawUnitsPerPoint;
    }

    public String id() {
        return id;
    }

    public int rawUnitsPerPoint() {
        return rawUnitsPerPoint;
    }

    public int pointsToRaw(int points) {
        return Math.max(0, points) * rawUnitsPerPoint;
    }

    public int rawToPoints(int raw) {
        return Math.max(0, raw) / rawUnitsPerPoint;
    }

    public static TCKnowledgeType parse(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);

        for (TCKnowledgeType type : values()) {
            if (type.id.equals(normalized) || type.name().toLowerCase(Locale.ROOT).equals(normalized)) {
                return type;
            }
        }

        return null;
    }
}