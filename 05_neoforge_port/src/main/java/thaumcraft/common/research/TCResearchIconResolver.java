package thaumcraft.common.research;

import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.casters.TCFocusElements;

public final class TCResearchIconResolver {
    private TCResearchIconResolver() {
    }

    public static ResolvedIcon resolve(String raw) {
        String value = raw == null ? "" : raw.trim();
        if (value.isBlank()) {
            return ResolvedIcon.unknown(value);
        }
        if (value.regionMatches(true, 0, "focus:", 0, "focus:".length())) {
            String focusKey = TCFocusElements.normalizeKey(value.substring("focus:".length()));
            return TCFocusElements.get(focusKey).isPresent()
                    ? new ResolvedIcon(Kind.FOCUS, null, focusKey, value)
                    : ResolvedIcon.unknown(value);
        }
        if (value.contains("textures/")) {
            ResourceLocation texture = parseLocation(value);
            return texture == null
                    ? ResolvedIcon.unknown(value)
                    : new ResolvedIcon(Kind.TEXTURE, texture, "", value);
        }

        String[] parts = value.split(";", -1);
        String legacyId = parts[0].trim().toLowerCase(Locale.ROOT);
        String damage = parts.length > 2 ? parts[2].trim() : "0";
        String flattened = TCResearchRequirementResolver.legacyFlattenedItemId(legacyId, damage);
        String modernId = researchIconAlias(flattened, TCResearchRequirementResolver.parsePositiveInt(damage, 0));
        ResourceLocation item = parseLocation(modernId);
        return item == null
                ? ResolvedIcon.unknown(value)
                : new ResolvedIcon(Kind.ITEM, item, "", value);
    }

    private static String researchIconAlias(String id, int damage) {
        return switch (id) {
            case "minecraft:hardened_clay" -> "minecraft:terracotta";
            case "thaumcraft:amulet_vis" -> "thaumcraft:vis_amulet";
            case "thaumcraft:arcane_ear" -> "thaumcraft:arcaneear";
            case "thaumcraft:essentia_input" -> "thaumcraft:essentiatransportin";
            case "thaumcraft:fortress_helm" -> "thaumcraft:thaumium_fortress_helm";
            case "thaumcraft:grapple_gun" -> "thaumcraft:grapplegun";
            case "thaumcraft:hungry_chest" -> "thaumcraft:hungrychest";
            case "thaumcraft:mind" -> damage == 1
                    ? "thaumcraft:mindclockwork_advanced"
                    : "thaumcraft:mindclockwork";
            case "thaumcraft:module" -> damage == 1
                    ? "thaumcraft:modaggression"
                    : "thaumcraft:modvision";
            case "thaumcraft:pattern_crafter" -> "thaumcraft:patterncrafter";
            case "thaumcraft:paving_stone_barrier" -> "thaumcraft:pavebarrier";
            case "thaumcraft:paving_stone_travel" -> "thaumcraft:pavetravel";
            case "thaumcraft:plate" -> "thaumcraft:brass_plate";
            case "thaumcraft:potion_sprayer" -> "thaumcraft:potionsprayer";
            case "thaumcraft:recharge_pedestal" -> "thaumcraft:rechargepedestal";
            case "thaumcraft:redstone_relay" -> "thaumcraft:redstonerelay";
            case "thaumcraft:sanity_soap" -> "thaumcraft:sane_soap";
            case "thaumcraft:seal" -> "thaumcraft:sealblank";
            case "thaumcraft:turret" -> switch (damage) {
                case 1 -> "thaumcraft:advancedcrossbow";
                case 2 -> "thaumcraft:arcane_bore";
                default -> "thaumcraft:automatedcrossbow";
            };
            case "thaumcraft:vis_battery" -> "thaumcraft:visbattery";
            case "thaumcraft:vis_generator" -> "thaumcraft:visgenerator";
            case "thaumcraft:voidseer_charm" -> "thaumcraft:voidseer_pearl";
            case "thaumcraft:verdant_charm" -> "thaumcraft:verdant_heart";
            default -> id;
        };
    }

    private static ResourceLocation parseLocation(String value) {
        try {
            return value.contains(":")
                    ? ResourceLocation.parse(value)
                    : ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, value);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public enum Kind {
        TEXTURE,
        FOCUS,
        ITEM,
        UNKNOWN
    }

    public record ResolvedIcon(Kind kind, ResourceLocation resource, String focusKey, String raw) {
        private static ResolvedIcon unknown(String raw) {
            return new ResolvedIcon(Kind.UNKNOWN, null, "", raw);
        }
    }
}
