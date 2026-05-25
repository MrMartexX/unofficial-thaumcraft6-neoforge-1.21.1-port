package thaumcraft.common.research;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.config.TCConfig;

public final class TCResearchRequirementAuditCommands {
    private static final String[] ROOT_ALIASES = {"thaumcraft", "thaum", "tc"};
    private static final int DEFAULT_DETAIL_LIMIT = 20;
    private static final int MAX_DETAIL_LIMIT = 200;

    private TCResearchRequirementAuditCommands() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (!TCConfig.ENABLE_KNOWLEDGE_DEBUG_COMMANDS.get()) {
            return;
        }

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        for (String root : ROOT_ALIASES) {
            dispatcher.register(Commands.literal(root)
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.literal("research")
                            .then(Commands.literal("requirements")
                                    .executes(context -> auditRequirements(context, DEFAULT_DETAIL_LIMIT))
                                    .then(Commands.argument("limit", IntegerArgumentType.integer(0, MAX_DETAIL_LIMIT))
                                            .executes(context -> auditRequirements(
                                                    context,
                                                    IntegerArgumentType.getInteger(context, "limit")
                                            ))))));
        }
    }

    private static int auditRequirements(CommandContext<CommandSourceStack> context, int detailLimit) {
        RequirementAuditReport report = buildReport(detailLimit);

        context.getSource().sendSuccess(() -> Component.literal(
                "Research stage requirement audit: required_item=" + report.itemResolved() + "/" + report.itemTotal()
                        + " resolved, required_craft=" + report.craftResolved() + "/" + report.craftTotal()
                        + " modern-matchable, required_knowledge=" + report.knowledgeResolved() + "/" + report.knowledgeTotal()
                        + " resolved. unresolved=" + report.unresolvedTotal()
        ), false);

        context.getSource().sendSuccess(() -> Component.literal(
                "Note: required_craft modern-matchable means the current ItemCraftedEvent marker path can identify the crafted stack. Exact legacy ItemStack.toString().hashCode() parity is still a separate exporter task."
        ), false);

        for (String detail : report.unresolvedDetails()) {
            context.getSource().sendFailure(Component.literal("- " + detail));
        }

        return report.unresolvedTotal() == 0 ? 1 : report.unresolvedTotal();
    }

    private static RequirementAuditReport buildReport(int detailLimit) {
        int itemTotal = 0;
        int itemUnresolved = 0;
        int craftTotal = 0;
        int craftUnresolved = 0;
        int knowledgeTotal = 0;
        int knowledgeUnresolved = 0;
        ArrayList<String> details = new ArrayList<>();

        for (TCResearchEntryDefinition entry : TCResearchManager.entries()) {
            for (int stageIndex = 0; stageIndex < entry.stages().size(); stageIndex++) {
                TCResearchStageDefinition stage = entry.stages().get(stageIndex);
                String stageLabel = entry.key() + " stage " + (stageIndex + 1);

                for (String required : stage.requiredItem()) {
                    itemTotal++;
                    RequirementResolution resolution = resolveItemRequirement(required);
                    if (!resolution.resolved()) {
                        itemUnresolved++;
                        addDetail(details, detailLimit, stageLabel, "required_item", required, resolution.reason());
                    }
                }

                for (String required : stage.requiredCraft()) {
                    craftTotal++;
                    RequirementResolution resolution = resolveItemRequirement(required);
                    if (!resolution.resolved()) {
                        craftUnresolved++;
                        addDetail(details, detailLimit, stageLabel, "required_craft", required, resolution.reason());
                    }
                }

                for (String required : stage.requiredKnowledge()) {
                    knowledgeTotal++;
                    RequirementResolution resolution = resolveKnowledgeRequirement(required);
                    if (!resolution.resolved()) {
                        knowledgeUnresolved++;
                        addDetail(details, detailLimit, stageLabel, "required_knowledge", required, resolution.reason());
                    }
                }
            }
        }

        return new RequirementAuditReport(
                itemTotal,
                itemUnresolved,
                craftTotal,
                craftUnresolved,
                knowledgeTotal,
                knowledgeUnresolved,
                details
        );
    }

    private static void addDetail(
            List<String> details,
            int detailLimit,
            String stageLabel,
            String type,
            String raw,
            String reason
    ) {
        if (details.size() >= detailLimit) {
            return;
        }
        details.add(stageLabel + " " + type + " raw=" + raw + " reason=" + reason);
    }

    private static RequirementResolution resolveItemRequirement(String raw) {
        if (raw == null || raw.isBlank()) {
            return RequirementResolution.unresolved("blank requirement");
        }

        String value = raw.trim().replace('\'', '"');
        if (value.startsWith("oredict:")) {
            String oreName = value.substring("oredict:".length());
            if (oreName.isBlank()) {
                return RequirementResolution.unresolved("blank legacy oredict key");
            }
            return RequirementResolution.ok();
        }

        String[] split = value.split(";");
        String rawId = split[0];
        String damageText = split.length > 2 ? split[2] : "0";
        String mappedId = legacyFlattenedItemId(rawId, damageText);
        ResourceLocation id;

        try {
            id = ResourceLocation.parse(mappedId);
        } catch (Exception ignored) {
            return RequirementResolution.unresolved("invalid resource location after mapping: " + mappedId);
        }

        if (BuiltInRegistries.ITEM.getOptional(id).isEmpty()) {
            return RequirementResolution.unresolved("missing modern item id: " + id);
        }

        int damage = parsePositiveInt(damageText, 0);
        boolean hasNbt = value.contains("{");

        if (hasNbt) {
            return RequirementResolution.unresolved("legacy NBT-sensitive ItemStack requirement not mapped yet");
        }

        if (damage > 0 && mappedId.equals(rawId)) {
            return RequirementResolution.unresolved("legacy metadata value has no explicit flattening mapping: damage=" + damage);
        }

        return RequirementResolution.ok();
    }

    private static RequirementResolution resolveKnowledgeRequirement(String raw) {
        if (raw == null || raw.isBlank()) {
            return RequirementResolution.unresolved("blank requirement");
        }

        String[] split = raw.split(";");
        if (split.length != 3) {
            return RequirementResolution.unresolved("expected type;category;points");
        }

        TCKnowledgeType type = TCKnowledgeType.parse(split[0]);
        int points = parsePositiveInt(split[2], 0);
        if (type == null) {
            return RequirementResolution.unresolved("unknown knowledge type: " + split[0]);
        }
        if (TCPlayerKnowledge.normalizeCategory(split[1]).isBlank()) {
            return RequirementResolution.unresolved("blank category");
        }
        if (points <= 0) {
            return RequirementResolution.unresolved("non-positive point cost");
        }

        return RequirementResolution.ok();
    }

    @SuppressWarnings("unused")
    private static List<TagKey<Item>> oreDictionaryItemTags(String oreName) {
        ArrayList<TagKey<Item>> tags = new ArrayList<>();
        String commonPath = commonTagPath(oreName);
        if (commonPath != null) {
            tags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", commonPath)));
        }
        tags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "legacy_ore_dictionary/" + camelToSnake(oreName))));
        return tags;
    }

    private static String legacyFlattenedItemId(String id, String damageText) {
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
        return id;
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

    private static int parsePositiveInt(String value, int fallback) {
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed < 0 ? fallback : parsed;
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private record RequirementResolution(boolean resolved, String reason) {
        static RequirementResolution ok() {
            return new RequirementResolution(true, "resolved");
        }

        static RequirementResolution unresolved(String reason) {
            return new RequirementResolution(false, reason);
        }
    }

    private record RequirementAuditReport(
            int itemTotal,
            int itemUnresolved,
            int craftTotal,
            int craftUnresolved,
            int knowledgeTotal,
            int knowledgeUnresolved,
            List<String> unresolvedDetails
    ) {
        private RequirementAuditReport {
            unresolvedDetails = List.copyOf(unresolvedDetails);
        }

        int itemResolved() {
            return itemTotal - itemUnresolved;
        }

        int craftResolved() {
            return craftTotal - craftUnresolved;
        }

        int knowledgeResolved() {
            return knowledgeTotal - knowledgeUnresolved;
        }

        int unresolvedTotal() {
            return itemUnresolved + craftUnresolved + knowledgeUnresolved;
        }
    }
}
