package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import thaumcraft.Thaumcraft;

public final class TCResearchRequirementResolver {
    private TCResearchRequirementResolver() {
    }

    public static ItemRequirementResolution resolveItemRequirement(String raw) {
        if (raw == null || raw.isBlank()) {
            return ItemRequirementResolution.unresolved("blank requirement", "blank requirement");
        }

        String value = raw.trim().replace('\'', '"');
        if (value.contains("enchanted_placeholder")) {
            return ItemRequirementResolution.unresolved(
                    "legacy enchanted placeholder requirement not mapped yet",
                    "legacy enchanted placeholder requirement"
            );
        }

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

        ItemRequirementResolution legacyFamilyResolution = classifyUnmappedLegacyFamily(rawId, damageText);
        if (legacyFamilyResolution != null) {
            return legacyFamilyResolution;
        }

        String mappedId = legacyFlattenedItemId(rawId, damageText);
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
            return ItemRequirementResolution.unresolved(
                    "missing modern item id: " + id,
                    "missing modern item id: " + id
            );
        }

        int count = parsePositiveInt(split.length > 1 ? split[1] : "", 1);
        int damage = parsePositiveInt(damageText, 0);
        boolean hasNbt = value.contains("{");

        if (hasNbt) {
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

        return ItemRequirementResolution.ok(ItemRequirement.item(raw, item, count));
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
        int damage = parsePositiveInt(damageText, 0);
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

    private static ItemRequirementResolution classifyUnmappedLegacyFamily(String rawId, String damageText) {
        int damage = parsePositiveInt(damageText, 0);
        if (rawId.equals("thaumcraft:crystal_essence")) {
            return ItemRequirementResolution.unresolved(
                    "legacy aspect crystal essence requirement not mapped yet",
                    "legacy aspect crystal essence requirement"
            );
        }
        if (rawId.equals("thaumcraft:phial")) {
            return ItemRequirementResolution.unresolved(
                    "legacy essentia phial requirement not mapped yet",
                    "legacy essentia phial requirement"
            );
        }
        if (rawId.equals("thaumcraft:ingot")
                || rawId.equals("thaumcraft:metal")
                || rawId.equals("thaumcraft:plate")
                || rawId.equals("thaumcraft:nugget")) {
            return ItemRequirementResolution.unresolved(
                    "legacy material-family requirement not mapped yet: " + rawId + ";damage=" + damage,
                    "legacy material-family requirement: " + rawId
            );
        }
        return null;
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

    public record ItemRequirement(String raw, Item item, List<TagKey<Item>> tags, int count) {
        public ItemRequirement {
            tags = List.copyOf(tags);
        }

        static ItemRequirement item(String raw, Item item, int count) {
            return new ItemRequirement(raw, item, List.of(), count);
        }

        static ItemRequirement tags(String raw, List<TagKey<Item>> tags, int count) {
            return new ItemRequirement(raw, null, tags, count);
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
