package thaumcraft.common.items.casters;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import thaumcraft.api.aspects.Aspect;

public final class TCFocusElements {
    private static final LinkedHashMap<String, TCFocusElementDefinition> DEFINITIONS = new LinkedHashMap<>();

    static {
        register("ROOT", TCFocusElementDefinition.Kind.ROOT, "BASEAUROMANCY", null, 0x999999);
        register("thaumcraft.TOUCH", TCFocusElementDefinition.Kind.MEDIUM, "BASEAUROMANCY", Aspect.AVERSION, 0xAD8085);
        register("thaumcraft.BOLT", TCFocusElementDefinition.Kind.MEDIUM, "FOCUSBOLT", Aspect.ENERGY, 0xADC495);
        register("thaumcraft.PROJECTILE", TCFocusElementDefinition.Kind.MEDIUM, "FOCUSPROJECTILE@2", Aspect.MOTION, 0xADD895, "option", "speed");
        register("thaumcraft.CLOUD", TCFocusElementDefinition.Kind.MEDIUM, "FOCUSCLOUD", Aspect.ALCHEMY, 0x99B7C5, "radius", "duration");
        register("thaumcraft.MINE", TCFocusElementDefinition.Kind.MEDIUM, "FOCUSMINE", Aspect.TRAP, 0x85B8C5, "target");
        register("thaumcraft.PLAN", TCFocusElementDefinition.Kind.MEDIUM, "FOCUSPLAN", Aspect.CRAFT, 0x85B8D8, "method");
        register("thaumcraft.SPELLBAT", TCFocusElementDefinition.Kind.MEDIUM, "FOCUSSPELLBAT", Aspect.BEAST, 0x85B8EC, "target");
        register("thaumcraft.FIRE", TCFocusElementDefinition.Kind.EFFECT, "BASEAUROMANCY", Aspect.FIRE, 0xFF5A01, "power", "duration");
        register("thaumcraft.FROST", TCFocusElementDefinition.Kind.EFFECT, "FOCUSELEMENTAL", Aspect.COLD, 0xE1FFFF, "power", "duration");
        register("thaumcraft.AIR", TCFocusElementDefinition.Kind.EFFECT, "FOCUSELEMENTAL", Aspect.AIR, 0xFFFF7E, "power");
        register("thaumcraft.EARTH", TCFocusElementDefinition.Kind.EFFECT, "FOCUSELEMENTAL", Aspect.EARTH, 0x56C000, "power");
        register("thaumcraft.FLUX", TCFocusElementDefinition.Kind.EFFECT, "FOCUSFLUX", Aspect.FLUX, 0x800080, "power");
        register("thaumcraft.BREAK", TCFocusElementDefinition.Kind.EFFECT, "FOCUSBREAK", Aspect.ENTROPY, 0x8A4968, "power", "fortune", "silk");
        register("thaumcraft.RIFT", TCFocusElementDefinition.Kind.EFFECT, "FOCUSRIFT", Aspect.ELDRITCH, 0x2F1115, "depth", "duration");
        register("thaumcraft.EXCHANGE", TCFocusElementDefinition.Kind.EFFECT, "FOCUSEXCHANGE", Aspect.EXCHANGE, 0x578357, "fortune", "silk");
        register("thaumcraft.CURSE", TCFocusElementDefinition.Kind.EFFECT, "FOCUSCURSE", Aspect.DEATH, 0x6A0005, "power", "duration");
        register("thaumcraft.HEAL", TCFocusElementDefinition.Kind.EFFECT, "FOCUSHEAL", Aspect.LIFE, 0xDE0005, "power");
        register("thaumcraft.SCATTER", TCFocusElementDefinition.Kind.MOD, "FOCUSSCATTER", null, 0x999999, "forks", "cone");
        register("thaumcraft.SPLITTARGET", TCFocusElementDefinition.Kind.MOD, "FOCUSSPLIT", null, 0x999999);
        register("thaumcraft.SPLITTRAJECTORY", TCFocusElementDefinition.Kind.MOD, "FOCUSSPLIT", null, 0x999999);
    }

    private TCFocusElements() {
    }

    public static Optional<TCFocusElementDefinition> get(String key) {
        return Optional.ofNullable(DEFINITIONS.get(normalizeKey(key)));
    }

    public static Map<String, TCFocusElementDefinition> definitions() {
        return Collections.unmodifiableMap(DEFINITIONS);
    }

    public static String normalizeKey(String key) {
        if (key == null || key.isBlank()) {
            return "";
        }
        String normalized = key.trim().toLowerCase(Locale.ROOT);
        if (!normalized.contains(".") && !"root".equals(normalized)) {
            normalized = "thaumcraft." + normalized;
        }
        return normalized;
    }

    private static void register(String key, TCFocusElementDefinition.Kind kind, String research, Aspect aspect, int color, String... settingOrder) {
        String aspectTag = aspect == null ? "" : aspect.getTag();
        TCFocusElementDefinition definition = new TCFocusElementDefinition(
                key,
                kind,
                research,
                aspectTag,
                color,
                List.of(settingOrder)
        );
        DEFINITIONS.put(definition.key(), definition);
    }
}
