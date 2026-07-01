package thaumcraft.common.items.casters;

import java.util.List;
import java.util.Map;
import thaumcraft.api.aspects.Aspect;

public record TCFocusElementDefinition(
        String key,
        Kind kind,
        String research,
        String aspect,
        int color,
        List<String> settingOrder
) {
    public TCFocusElementDefinition {
        key = TCFocusElements.normalizeKey(key);
        research = research == null ? "" : research.trim();
        aspect = aspect == null ? "" : aspect.trim().toLowerCase(java.util.Locale.ROOT);
        color &= 0xFFFFFF;
        settingOrder = List.copyOf(settingOrder);
    }

    public boolean hasAspect() {
        return !aspect.isBlank();
    }

    public Aspect aspectObject() {
        return hasAspect() ? Aspect.getAspect(aspect) : null;
    }

    public boolean contributesFocusColor() {
        return kind == Kind.EFFECT;
    }

    public int complexity(Map<String, Integer> settings) {
        return switch (key) {
            case "thaumcraft.air" -> value(settings, "power", 1, 1, 5) * 2;
            case "thaumcraft.break" -> value(settings, "power", 1, 1, 5) * 3
                    + value(settings, "silk", 0, 0, 1) * 4
                    + (value(settings, "fortune", 0, 0, 4) == 0 ? 0 : (value(settings, "fortune", 0, 0, 4) + 1) * 3);
            case "thaumcraft.curse" -> value(settings, "duration", 1, 1, 10)
                    + value(settings, "power", 1, 1, 5) * 3;
            case "thaumcraft.earth" -> value(settings, "power", 1, 1, 5) * 3;
            case "thaumcraft.exchange" -> (value(settings, "fortune", 0, 0, 4) + 1) * 3;
            case "thaumcraft.fire" -> value(settings, "duration", 0, 0, 5)
                    + value(settings, "power", 1, 1, 5) * 2;
            case "thaumcraft.flux" -> value(settings, "power", 1, 1, 5) * 3;
            case "thaumcraft.frost" -> value(settings, "duration", 2, 2, 10)
                    + value(settings, "power", 1, 1, 5) * 2;
            case "thaumcraft.heal" -> value(settings, "power", 1, 1, 5) * 4;
            case "thaumcraft.rift" -> 3
                    + value(settings, "duration", 2, 2, 10) / 2
                    + value(settings, "depth", 8, 8, 32) / 4;
            case "thaumcraft.cloud" -> 4
                    + value(settings, "radius", 1, 1, 3) * 2
                    + value(settings, "duration", 5, 5, 30) / 5;
            case "thaumcraft.projectile" -> projectileComplexity(settings);
            case "thaumcraft.scatter" -> (int)Math.max(
                    2.0F,
                    2.0F * (value(settings, "forks", 2, 2, 10) - value(settings, "cone", 10, 10, 360) / 45.0F)
            );
            case "root" -> 0;
            case "thaumcraft.touch" -> 2;
            case "thaumcraft.bolt" -> 5;
            case "thaumcraft.mine" -> 4;
            case "thaumcraft.plan" -> 4;
            case "thaumcraft.spellbat" -> 8;
            case "thaumcraft.splittarget" -> 4;
            case "thaumcraft.splittrajectory" -> 5;
            default -> 0;
        };
    }

    private static int projectileComplexity(Map<String, Integer> settings) {
        int complexity = 4 + (value(settings, "speed", 1, 1, 5) - 1) / 2;
        return switch (value(settings, "option", 0, 0, 3)) {
            case 1 -> complexity + 3;
            case 2, 3 -> complexity + 5;
            default -> complexity;
        };
    }

    private static int value(Map<String, Integer> settings, String key, int fallback, int min, int max) {
        int value = settings == null ? fallback : settings.getOrDefault(key, fallback);
        return Math.max(min, Math.min(max, value));
    }

    public enum Kind {
        ROOT,
        MEDIUM,
        EFFECT,
        MOD
    }
}
