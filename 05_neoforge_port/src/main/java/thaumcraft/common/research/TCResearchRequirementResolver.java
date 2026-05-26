package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.components.TCAspectStackComponent;
import thaumcraft.common.items.components.TCStoredEnchantComponent;

public final class TCResearchRequirementResolver {
    private static final Pattern ASPECT_KEY_PATTERN = Pattern.compile("key:\\s*\\\"([^\\\"]+)\\\"");
    private static final Pattern ASPECT_AMOUNT_PATTERN = Pattern.compile("amount:\\s*(\\d+)s?");
    private static final Pattern ENCHANTMENT_PATTERN = Pattern.compile("id:\\s*(\\d+)s?\\s*,\\s*lvl:\\s*(\\d+)s?");

    private TCResearchRequirementResolver() {
    }

    public static ItemRequirementResolution resolveItemRequirement(String raw) {
        if (raw == null || raw.isBlank()) {
            return ItemRequirementResolution.unresolved("blank requirement", "blank requirement");
        }

        String value = raw.trim().replace('\'', '"');

        if (value.startsWith("oredict:")) {
            String oreName = value.substring("oredict:".length());
            if (oreName.isBlank()) {
                return ItemRequirementResolution.unresolved("blank legacy oredict key", "blank legacy oredict key");
            }
            return ItemRequirementResolution.ok(ItemRequirement.tags(raw, oreDictionaryItemTags(oreName), 1));
        }

        String[] split = value.split(";");
        String rawId = split[0];
        String damageText = split.length > 2 ? split[2] : "0";

        String mappedId = legacyFlattenedItemId(rawId, damageText, value);
        ResourceLocation id;

        try {
            id = ResourceLocation.parse(mappedId);
        } catch (Exception ignored) {
            return ItemRequirementResolution.unresolved(
                    "invalid resource location after mapping: " + mappedId,
                    "invalid resource location: " + mappedId
            );
        }

        Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        if (item == null) {
            ItemRequirementResolution legacyFamilyResolution = classifyUnmappedLegacyFamily(rawId, damageText, value);
            if (legacyFamilyResolution != null) {
                return legacyFamilyResolution;
            }
            return ItemRequirementResolution.unresolved(
                    "missing modern item id: " + id,
                    "missing modern item id: " + id
            );
        }

        int count = parsePositiveInt(split.length > 1 ? split[1] : "", 1);
        int damage = parsePositiveInt(damageText, 0);
        boolean hasNbt = value.contains("{");

        if (hasNbt && mappedId.equals(rawId)) {
            return ItemRequirementResolution.unresolved(
                    "legacy NBT-sensitive ItemStack requirement not mapped yet",
                    "legacy NBT-sensitive ItemStack requirement: " + id
            );
        }

        if (damage > 0 && mappedId.equals(rawId)) {
            return ItemRequirementResolution.unresolved(
                    "legacy metadata value has no explicit flattening mapping: damage=" + damage,
                    "legacy metadata unmapped: " + rawId + ";" + damage
            );
        }

        return ItemRequirementResolution.ok(ItemRequirement.item(
                raw,
                item,
                count,
                legacyAspectStackRequirement(rawId, damage, value),
                legacyStoredMagicRequirement(rawId, value)
        ));
    }

    public static KnowledgeRequirementResolution resolveKnowledgeRequirement(String raw) {
        if (raw == null || raw.isBlank()) {
            return KnowledgeRequirementResolution.unresolved("blank requirement", "blank requirement");
        }

        String[] split = raw.split(";");
        if (split.length != 3) {
            return KnowledgeRequirementResolution.unresolved("expected type;category;points", "malformed knowledge requirement");
        }

        TCKnowledgeType type = TCKnowledgeType.parse(split[0]);
        String category = TCPlayerKnowledge.normalizeCategory(split[1]);
        int points = parsePositiveInt(split[2], 0);
        if (type == null) {
            return KnowledgeRequirementResolution.unresolved("unknown knowledge type: " + split[0], "unknown knowledge type: " + split[0]);
        }
        if (category.isBlank()) {
            return KnowledgeRequirementResolution.unresolved("blank category", "blank knowledge category");
        }
        if (points <= 0) {
            return KnowledgeRequirementResolution.unresolved("non-positive point cost", "non-positive knowledge point cost");
        }

        return KnowledgeRequirementResolution.ok(new KnowledgeRequirement(raw, type, category, points));
    }

    public static String legacyFlattenedItemId(String id, String damageText) {
        return legacyFlattenedItemId(id, damageText, "");
    }

    private static String legacyFlattenedItemId(String id, String damageText, String normalizedRaw) {
        int damage = parsePositiveInt(damageText, 0);
        String materialFamilyId = TCLegacyMaterialFamilyMappings.modernItemId(id, damageText);
        if (materialFamilyId != null) {
            return materialFamilyId;
        }
        if (id.equals("minecraft:web")) {
            return "minecraft:cobweb";
        }
        if (id.equals("minecraft:noteblock")) {
            return "minecraft:note_block";
        }
        if (id.equals("minecraft:dye")) {
            return switch (damage) {
                case 15 -> "minecraft:bone_meal";
                case 4 -> "minecraft:lapis_lazuli";
                case 3 -> "minecraft:cocoa_beans";
                default -> "minecraft:ink_sac";
            };
        }
        if (id.equals("thaumcraft:arcane_stone")) {
            return "thaumcraft:stone_arcane";
        }
        if (id.equals("thaumcraft:leather")) {
            return "minecraft:leather";
        }
        if (id.equals("thaumcraft:nitor") && damage == 4) {
            return "thaumcraft:nitor_yellow";
        }
        if (id.equals("thaumcraft:curio") && damage == 6) {
            return "thaumcraft:curio_rites";
        }
        if (id.equals("thaumcraft:crystal_essence")) {
            String aspect = extractAspectKey(normalizedRaw);
            if (!aspect.isBlank()) {
                return "thaumcraft:crystal_essence_" + aspect;
            }
        }
        if (id.equals("thaumcraft:phial") && damage == 1) {
            String aspect = extractAspectKey(normalizedRaw);
            if (!aspect.isBlank()) {
                return "thaumcraft:phial_" + aspect;
            }
        }
        if (id.endsWith("thaumcraft:enchanted_placeholder")) {
            String target = flattenedEnchantmentPlaceholderId(normalizedRaw);
            if (!target.isBlank()) {
                return target;
            }
        }
        return id;
    }

    public static List<TagKey<Item>> oreDictionaryItemTags(String oreName) {
        ArrayList<TagKey<Item>> tags = new ArrayList<>();
        String commonPath = commonTagPath(oreName);
        if (commonPath != null) {
            tags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", commonPath)));
        }
        tags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "legacy_ore_dictionary/" + camelToSnake(oreName))));
        return tags;
    }

    public static int parsePositiveInt(String value, int fallback) {
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed < 0 ? fallback : parsed;
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static ItemRequirementResolution classifyUnmappedLegacyFamily(String rawId, String damageText, String normalizedRaw) {
        int damage = parsePositiveInt(damageText, 0);
        if (rawId.equals("thaumcraft:crystal_essence")) {
            String aspect = extractAspectKey(normalizedRaw);
            return ItemRequirementResolution.unresolved(
                    "legacy aspect crystal essence requirement not mapped yet: aspect=" + (aspect.isBlank() ? "unknown" : aspect),
                    "legacy aspect crystal essence requirement"
            );
        }
        if (rawId.equals("thaumcraft:phial")) {
            String aspect = extractAspectKey(normalizedRaw);
            return ItemRequirementResolution.unresolved(
                    "legacy essentia phial requirement not mapped yet: aspect=" + (aspect.isBlank() ? "unknown" : aspect),
                    "legacy essentia phial requirement"
            );
        }
        if (rawId.endsWith("thaumcraft:enchanted_placeholder")) {
            return ItemRequirementResolution.unresolved(
                    "legacy enchanted placeholder requirement not mapped yet: " + normalizedRaw,
                    "legacy enchanted placeholder requirement"
            );
        }
        TCLegacyMaterialFamilyMappings.Classification materialFamily = TCLegacyMaterialFamilyMappings.classify(rawId, damageText);
        if (materialFamily != null) {
            return ItemRequirementResolution.unresolved(materialFamily.reason(), materialFamily.summaryKey());
        }
        if (rawId.equals("thaumcraft:curio")) {
            return ItemRequirementResolution.unresolved(
                    "legacy curio-family requirement not mapped yet: " + rawId + ";damage=" + damage,
                    "legacy curio-family requirement: " + rawId
            );
        }
        String subsystem = legacySubsystemRequirement(rawId);
        if (subsystem != null) {
            return ItemRequirementResolution.unresolved(
                    subsystem + " requirement not mapped yet: " + rawId + ";damage=" + damage,
                    subsystem + " requirement: " + rawId
            );
        }
        return null;
    }

    private static TCAspectStackComponent legacyAspectStackRequirement(String rawId, int damage, String normalizedRaw) {
        String aspect = extractAspectKey(normalizedRaw);
        if (aspect.isBlank()) {
            return null;
        }
        if (rawId.equals("thaumcraft:crystal_essence")) {
            return new TCAspectStackComponent(aspect, extractAspectAmount(normalizedRaw, 1));
        }
        if (rawId.equals("thaumcraft:phial") && damage == 1) {
            return new TCAspectStackComponent(aspect, extractAspectAmount(normalizedRaw, 10));
        }
        return null;
    }

    private static TCStoredEnchantComponent legacyStoredMagicRequirement(String rawId, String normalizedRaw) {
        if (!rawId.endsWith("thaumcraft:enchanted_placeholder")) {
            return null;
        }
        Matcher matcher = ENCHANTMENT_PATTERN.matcher(normalizedRaw);
        if (!matcher.find()) {
            return null;
        }
        int legacyId = parsePositiveInt(matcher.group(1), -1);
        int level = parsePositiveInt(matcher.group(2), 1);
        String id = switch (legacyId) {
            case 0 -> "protection";
            case 16 -> "sharpness";
            case 33 -> "silk_touch";
            case 35 -> "fortune";
            default -> "";
        };
        return id.isBlank() ? null : new TCStoredEnchantComponent(id, level);
    }

    private static String extractAspectKey(String normalizedRaw) {
        Matcher matcher = ASPECT_KEY_PATTERN.matcher(normalizedRaw);
        if (!matcher.find()) {
            return "";
        }
        return sanitizePathSegment(matcher.group(1));
    }

    private static int extractAspectAmount(String normalizedRaw, int fallback) {
        Matcher matcher = ASPECT_AMOUNT_PATTERN.matcher(normalizedRaw);
        if (!matcher.find()) {
            return fallback;
        }
        return parsePositiveInt(matcher.group(1), fallback);
    }

    private static String flattenedEnchantmentPlaceholderId(String normalizedRaw) {
        Matcher matcher = ENCHANTMENT_PATTERN.matcher(normalizedRaw);
        if (!matcher.find()) {
            return "";
        }
        int legacyId = parsePositiveInt(matcher.group(1), -1);
        int level = parsePositiveInt(matcher.group(2), 1);
        String enchantment = switch (legacyId) {
            case 0 -> "protection";
            case 16 -> "sharpness";
            case 33 -> "silk_touch";
            case 35 -> "fortune";
            default -> "";
        };
        if (enchantment.isBlank()) {
            return "";
        }
        return "thaumcraft:enchanted_placeholder_" + enchantment + "_" + level;
    }

    private static String sanitizePathSegment(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_./-]", "_");
    }

    private static String legacySubsystemRequirement(String rawId) {
        return switch (rawId) {
            case "thaumcraft:arcane_workbench",
                 "thaumcraft:wand_workbench",
                 "thaumcraft:research_table",
                 "thaumcraft:scribing_tools" -> "legacy research/crafting station";
            case "thaumcraft:caster_basic",
                 "thaumcraft:focus_1",
                 "thaumcraft:focus_2",
                 "thaumcraft:focus_3",
                 "thaumcraft:vis_resonator" -> "legacy auromancy";
            case "thaumcraft:crucible",
                 "thaumcraft:nitor",
                 "thaumcraft:smelter_basic",
                 "thaumcraft:tallow",
                 "thaumcraft:leather" -> "legacy alchemy";
            case "thaumcraft:infusion_matrix" -> "legacy infusion";
            case "thaumcraft:thaumium_axe",
                 "thaumcraft:thaumium_hoe",
                 "thaumcraft:thaumium_pick",
                 "thaumcraft:thaumium_shovel",
                 "thaumcraft:thaumium_sword" -> "legacy thaumium tool";
            case "thaumcraft:mirrored_glass" -> "legacy artifice/mirror";
            case "thaumcraft:brain" -> "legacy biological component";
            default -> null;
        };
    }

    private static String commonTagPath(String entry) {
        String lower = entry.toLowerCase(java.util.Locale.ROOT);
        if (lower.startsWith("ore") && entry.length() > 3) {
            return "ores/" + camelToSnake(entry.substring(3));
        }
        if (lower.startsWith("ingot") && entry.length() > 5) {
            return "ingots/" + camelToSnake(entry.substring(5));
        }
        if (lower.startsWith("block") && entry.length() > 5) {
            return "storage_blocks/" + camelToSnake(entry.substring(5));
        }
        if (lower.startsWith("plate") && entry.length() > 5) {
            return "plates/" + camelToSnake(entry.substring(5));
        }
        if (lower.startsWith("gem") && entry.length() > 3) {
            return "gems/" + camelToSnake(entry.substring(3));
        }
        if (lower.startsWith("dust") && entry.length() > 4) {
            return "dusts/" + camelToSnake(entry.substring(4));
        }
        if (lower.startsWith("nugget") && entry.length() > 6) {
            return "nuggets/" + camelToSnake(entry.substring(6));
        }
        return null;
    }

    private static String camelToSnake(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (Character.isUpperCase(ch) && builder.length() > 0) {
                builder.append('_');
            }
            builder.append(Character.toLowerCase(ch));
        }
        return builder.toString();
    }

    public record ItemRequirement(String raw, Item item, List<TagKey<Item>> tags, int count, TCAspectStackComponent aspectStack, TCStoredEnchantComponent storedMagic) {
        public ItemRequirement {
            tags = List.copyOf(tags);
        }

        static ItemRequirement item(String raw, Item item, int count) {
            return item(raw, item, count, null, null);
        }

        static ItemRequirement item(String raw, Item item, int count, TCAspectStackComponent aspectStack) {
            return item(raw, item, count, aspectStack, null);
        }

        static ItemRequirement item(String raw, Item item, int count, TCAspectStackComponent aspectStack, TCStoredEnchantComponent storedMagic) {
            return new ItemRequirement(raw, item, List.of(), count, aspectStack, storedMagic);
        }

        static ItemRequirement tags(String raw, List<TagKey<Item>> tags, int count) {
            return new ItemRequirement(raw, null, tags, count, null, null);
        }

        boolean hasAspectStackRequirement() {
            return aspectStack != null && !aspectStack.isEmpty();
        }

        boolean hasStoredMagicRequirement() {
            return storedMagic != null && !storedMagic.isEmpty();
        }
    }

    public record KnowledgeRequirement(String raw, TCKnowledgeType type, String category, int points) {
    }

    public record ItemRequirementResolution(boolean resolved, String reason, String summaryKey, ItemRequirement requirement) {
        static ItemRequirementResolution ok(ItemRequirement requirement) {
            return new ItemRequirementResolution(true, "resolved", "resolved", requirement);
        }

        static ItemRequirementResolution unresolved(String reason, String summaryKey) {
            return new ItemRequirementResolution(false, reason, summaryKey, null);
        }
    }

    public record KnowledgeRequirementResolution(boolean resolved, String reason, String summaryKey, KnowledgeRequirement requirement) {
        static KnowledgeRequirementResolution ok(KnowledgeRequirement requirement) {
            return new KnowledgeRequirementResolution(true, "resolved", "resolved", requirement);
        }

        static KnowledgeRequirementResolution unresolved(String reason, String summaryKey) {
            return new KnowledgeRequirementResolution(false, reason, summaryKey, null);
        }
    }
}
