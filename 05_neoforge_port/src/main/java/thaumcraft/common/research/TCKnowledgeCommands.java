package thaumcraft.common.research;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import thaumcraft.common.config.TCConfig;

public final class TCKnowledgeCommands {
    private static final String[] ROOT_ALIASES = {"thaumcraft", "thaum", "tc"};

    private TCKnowledgeCommands() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (!TCConfig.ENABLE_KNOWLEDGE_DEBUG_COMMANDS.get()) {
            return;
        }

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        for (String root : ROOT_ALIASES) {
            dispatcher.register(Commands.literal(root)
                    .requires(source -> source.hasPermission(2))
                    .then(knowledgeTree())
                    .then(researchTree()));
        }
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> knowledgeTree() {
        return Commands.literal("knowledge")
                .then(getTree())
                .then(addTree())
                .then(setTree())
                .then(clearTree())
                .then(Commands.argument("player", EntityArgument.player())
                        .then(getTree())
                        .then(addTree())
                        .then(setTree())
                        .then(clearTree()));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> getTree() {
        return Commands.literal("get")
                .executes(TCKnowledgeCommands::getAllKnowledge)
                .then(Commands.argument("type", StringArgumentType.word())
                        .executes(TCKnowledgeCommands::getTypeKnowledge)
                        .then(Commands.argument("category", StringArgumentType.word())
                                .executes(TCKnowledgeCommands::getCategoryKnowledge)));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> addTree() {
        return Commands.literal("add")
                .then(Commands.argument("type", StringArgumentType.word())
                        .then(Commands.argument("category", StringArgumentType.word())
                                .then(Commands.argument("points", IntegerArgumentType.integer(0))
                                        .executes(context -> addKnowledge(
                                                context,
                                                parseTypeOrFail(context),
                                                StringArgumentType.getString(context, "category"),
                                                IntegerArgumentType.getInteger(context, "points"))))));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> setTree() {
        return Commands.literal("set")
                .then(Commands.argument("type", StringArgumentType.word())
                        .then(Commands.argument("category", StringArgumentType.word())
                                .then(Commands.argument("points", IntegerArgumentType.integer(0))
                                        .executes(context -> setKnowledge(
                                                context,
                                                parseTypeOrFail(context),
                                                StringArgumentType.getString(context, "category"),
                                                IntegerArgumentType.getInteger(context, "points"))))));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> clearTree() {
        return Commands.literal("clear")
                .executes(TCKnowledgeCommands::clearAllKnowledge)
                .then(Commands.argument("type", StringArgumentType.word())
                        .executes(TCKnowledgeCommands::clearTypeKnowledge)
                        .then(Commands.argument("category", StringArgumentType.word())
                                .executes(TCKnowledgeCommands::clearCategoryKnowledge)));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> researchTree() {
        return Commands.literal("research")
                .then(Commands.literal("list")
                        .executes(TCKnowledgeCommands::listKnownResearchEntries))
                .then(Commands.literal("validate")
                        .executes(TCKnowledgeCommands::validateResearchData))
                .then(Commands.literal("info")
                        .then(Commands.argument("research_key", StringArgumentType.word())
                                .executes(TCKnowledgeCommands::showResearchEntryInfo)))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("list")
                                .executes(TCKnowledgeCommands::listPlayerResearch))
                        .then(Commands.literal("visible")
                                .executes(TCKnowledgeCommands::listVisibleResearch)
                                .then(Commands.argument("category", StringArgumentType.word())
                                        .executes(TCKnowledgeCommands::listVisibleResearchInCategory)))
                        .then(Commands.literal("all")
                                .executes(TCKnowledgeCommands::completeAllResearch))
                        .then(Commands.literal("reset")
                                .executes(TCKnowledgeCommands::resetPlayerResearch))
                        .then(Commands.literal("status")
                                .then(Commands.argument("research_key", StringArgumentType.word())
                                        .executes(TCKnowledgeCommands::showPlayerResearchStatus)))
                        .then(Commands.literal("stage")
                                .then(Commands.argument("research_key", StringArgumentType.word())
                                        .executes(TCKnowledgeCommands::showPlayerCurrentStageRequirements)
                                        .then(Commands.literal("check")
                                                .executes(TCKnowledgeCommands::showPlayerCurrentStageRequirements))
                                        .then(Commands.literal("advance")
                                                .executes(TCKnowledgeCommands::advancePlayerCurrentStage))))
                        .then(Commands.literal("revoke")
                                .then(Commands.argument("research_key", StringArgumentType.word())
                                        .executes(TCKnowledgeCommands::revokePlayerResearch)))
                        .then(Commands.argument("research_key", StringArgumentType.word())
                                .executes(TCKnowledgeCommands::grantPlayerResearch)));
    }

    private static int getAllKnowledge(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = resolvePlayer(context);
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);

        context.getSource().sendSuccess(() -> Component.literal("Thaumcraft knowledge for " + player.getGameProfile().getName() + ":"), false);

        for (TCKnowledgeType type : TCKnowledgeType.values()) {
            sendTypeSummary(context.getSource(), knowledge, type);
        }

        return 1;
    }

    private static int getTypeKnowledge(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = resolvePlayer(context);
        TCKnowledgeType type = parseTypeOrFail(context);
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);

        context.getSource().sendSuccess(() -> Component.literal(
                "Thaumcraft " + type.id() + " knowledge for " + player.getGameProfile().getName() + ":"
        ), false);

        sendTypeSummary(context.getSource(), knowledge, type);
        return 1;
    }

    private static int getCategoryKnowledge(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = resolvePlayer(context);
        TCKnowledgeType type = parseTypeOrFail(context);
        String category = TCPlayerKnowledge.normalizeCategory(StringArgumentType.getString(context, "category"));
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);

        int points = knowledge.getPoints(type, category);
        int raw = knowledge.getRaw(type, category);

        context.getSource().sendSuccess(() -> Component.literal(
                player.getGameProfile().getName() + " " + type.id() + " " + category + ": points=" + points + ", raw=" + raw
        ), false);

        return points;
    }

    private static int addKnowledge(CommandContext<CommandSourceStack> context, TCKnowledgeType type, String category, int points) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = resolvePlayer(context);
        String normalizedCategory = TCPlayerKnowledge.normalizeCategory(category);

        TCPlayerKnowledgeStore.mutate(player, knowledge -> knowledge.addPoints(type, normalizedCategory, points));

        TCPlayerKnowledge updated = TCPlayerKnowledgeStore.get(player);
        int newPoints = updated.getPoints(type, normalizedCategory);
        int newRaw = updated.getRaw(type, normalizedCategory);

        context.getSource().sendSuccess(() -> Component.literal(
                "Added " + points + " " + type.id() + " point(s) to " + player.getGameProfile().getName()
                        + " category " + normalizedCategory + ". Now points=" + newPoints + ", raw=" + newRaw
        ), true);

        return newPoints;
    }

    private static int setKnowledge(CommandContext<CommandSourceStack> context, TCKnowledgeType type, String category, int points) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = resolvePlayer(context);
        String normalizedCategory = TCPlayerKnowledge.normalizeCategory(category);

        TCPlayerKnowledgeStore.mutate(player, knowledge -> knowledge.setPoints(type, normalizedCategory, points));

        TCPlayerKnowledge updated = TCPlayerKnowledgeStore.get(player);
        int newRaw = updated.getRaw(type, normalizedCategory);

        context.getSource().sendSuccess(() -> Component.literal(
                "Set " + player.getGameProfile().getName() + " " + type.id() + " " + normalizedCategory
                        + " to points=" + points + ", raw=" + newRaw
        ), true);

        return points;
    }

    private static int clearAllKnowledge(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = resolvePlayer(context);
        TCPlayerKnowledgeStore.mutate(player, TCPlayerKnowledge::clearAllKnowledge);

        context.getSource().sendSuccess(() -> Component.literal(
                "Cleared all observation and theory knowledge for " + player.getGameProfile().getName() + "."
        ), true);

        return 1;
    }

    private static int clearTypeKnowledge(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = resolvePlayer(context);
        TCKnowledgeType type = parseTypeOrFail(context);

        TCPlayerKnowledgeStore.mutate(player, knowledge -> knowledge.clearKnowledge(type));

        context.getSource().sendSuccess(() -> Component.literal(
                "Cleared all " + type.id() + " knowledge for " + player.getGameProfile().getName() + "."
        ), true);

        return 1;
    }

    private static int clearCategoryKnowledge(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = resolvePlayer(context);
        TCKnowledgeType type = parseTypeOrFail(context);
        String category = TCPlayerKnowledge.normalizeCategory(StringArgumentType.getString(context, "category"));

        TCPlayerKnowledgeStore.mutate(player, knowledge -> knowledge.clearKnowledge(type, category));

        context.getSource().sendSuccess(() -> Component.literal(
                "Cleared " + type.id() + " knowledge category " + category + " for " + player.getGameProfile().getName() + "."
        ), true);

        return 1;
    }

    private static int listKnownResearchEntries(CommandContext<CommandSourceStack> context) {
        TCResearchData data = TCResearchManager.data();
        context.getSource().sendSuccess(() -> Component.literal(
                "Loaded Thaumcraft research: " + data.categories().size() + " categories, "
                        + data.entries().size() + " entries, " + data.stageCount() + " stages, "
                        + data.addendumCount() + " addenda."
        ), false);

        data.categories().values().stream()
                .sorted(Comparator.comparing(TCResearchCategoryDefinition::key))
                .forEach(category -> {
                    List<TCResearchEntryDefinition> entries = TCResearchManager.entriesByCategory(category.key());
                    context.getSource().sendSuccess(() -> Component.literal(
                            "- " + category.key() + ": " + entries.size() + " entries"
                    ), false);
                });
        return data.entries().size();
    }

    private static int validateResearchData(CommandContext<CommandSourceStack> context) {
        TCResearchValidationReport report = TCResearchManager.validateReferences();
        context.getSource().sendSuccess(() -> Component.literal(
                "Research references: entry=" + report.entryReferenceCount()
                        + ", external_scan_or_flag=" + report.externalTriggerReferenceCount()
                        + ", unresolved=" + report.unresolvedReferenceCount()
        ), false);

        if (report.hasUnresolvedReferences()) {
            report.unresolvedReferences().stream()
                    .limit(10)
                    .forEach(reference -> context.getSource().sendFailure(Component.literal(
                            reference.ownerKey() + " " + reference.location()
                                    + " raw=" + reference.rawReference()
                                    + " normalized=" + reference.normalizedReference()
                    )));
            return report.unresolvedReferenceCount();
        }

        return 1;
    }

    private static int showResearchEntryInfo(CommandContext<CommandSourceStack> context) {
        String key = TCPlayerKnowledge.normalizeResearchKey(StringArgumentType.getString(context, "research_key"));
        return TCResearchManager.getEntry(key).map(entry -> {
            context.getSource().sendSuccess(() -> Component.literal(
                    entry.key() + " [" + entry.category() + "] at " + entry.locationX() + "," + entry.locationY()
                            + " stages=" + entry.stages().size()
                            + " addenda=" + entry.addenda().size()
            ), false);
            if (!entry.parents().isEmpty()) {
                context.getSource().sendSuccess(() -> Component.literal("parents: " + String.join(", ", entry.parents())), false);
            }
            if (!entry.siblings().isEmpty()) {
                context.getSource().sendSuccess(() -> Component.literal("siblings: " + String.join(", ", entry.siblings())), false);
            }
            if (!entry.meta().isEmpty()) {
                context.getSource().sendSuccess(() -> Component.literal("meta: " + String.join(", ", entry.meta())), false);
            }
            return 1;
        }).orElseGet(() -> {
            context.getSource().sendFailure(Component.literal("Unknown loaded research entry: " + key));
            return 0;
        });
    }

    private static int listPlayerResearch(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        Set<String> keys = new TreeSet<>(knowledge.completedResearch());

        if (keys.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal(player.getGameProfile().getName() + " has no stored Thaumcraft research keys."), false);
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal(
                player.getGameProfile().getName() + " stored Thaumcraft research keys:"
        ), false);

        for (String key : keys) {
            context.getSource().sendSuccess(() -> Component.literal("- " + formatResearchState(knowledge, key)), false);
        }

        return keys.size();
    }

    private static int listVisibleResearch(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int count = 0;

        context.getSource().sendSuccess(() -> Component.literal(
                "Visible Thaumcraft research for " + player.getGameProfile().getName() + ":"
        ), false);

        for (TCResearchCategoryDefinition category : TCResearchManager.categories().stream()
                .sorted(Comparator.comparing(TCResearchCategoryDefinition::key))
                .toList()) {
            if (!TCResearchManager.isCategoryVisible(TCPlayerKnowledgeStore.get(player), category.key())) {
                continue;
            }
            List<TCResearchEntryDefinition> visible = TCResearchManager.visibleEntriesByCategory(player, category.key());
            if (!visible.isEmpty()) {
                int categoryCount = visible.size();
                count += categoryCount;
                context.getSource().sendSuccess(() -> Component.literal("- " + category.key() + ": " + categoryCount), false);
            }
        }

        int total = count;
        context.getSource().sendSuccess(() -> Component.literal("Total visible entries: " + total), false);
        return count;
    }

    private static int listVisibleResearchInCategory(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String category = TCPlayerKnowledge.normalizeCategory(StringArgumentType.getString(context, "category"));

        if (!TCResearchManager.isCategoryVisible(TCPlayerKnowledgeStore.get(player), category)) {
            context.getSource().sendFailure(Component.literal("Research category is not visible: " + category));
            return 0;
        }

        List<TCResearchEntryDefinition> visible = TCResearchManager.visibleEntriesByCategory(player, category);
        context.getSource().sendSuccess(() -> Component.literal(
                "Visible Thaumcraft research in " + category + " for " + player.getGameProfile().getName() + ":"
        ), false);

        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        for (TCResearchEntryDefinition entry : visible) {
            context.getSource().sendSuccess(() -> Component.literal("- " + formatResearchState(knowledge, entry.key())), false);
        }

        return visible.size();
    }

    private static int completeAllResearch(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        int progressed = TCResearchManager.completeAllResearchForDebug(player, true);

        int progressedCount = progressed;
        context.getSource().sendSuccess(() -> Component.literal(
                "Legacy-recursive debug grant completed/progressed " + progressedCount + " loaded Thaumcraft research entr"
                        + (progressedCount == 1 ? "y" : "ies") + " for " + player.getGameProfile().getName()
                        + ". Parent, stage-required, sibling and PAGE/RESEARCH flag behavior follows the current legacy data model."
        ), true);
        return progressed;
    }

    private static int resetPlayerResearch(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");

        TCPlayerKnowledgeStore.mutate(player, TCPlayerKnowledge::clearResearch);

        context.getSource().sendSuccess(() -> Component.literal(
                "Cleared stored Thaumcraft research keys for " + player.getGameProfile().getName() + "."
        ), true);

        return 1;
    }

    private static int showPlayerResearchStatus(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String key = TCPlayerKnowledge.normalizeResearchKey(StringArgumentType.getString(context, "research_key"));
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        boolean requisites = TCResearchManager.doesPlayerHaveRequisites(player, key);
        boolean visible = TCResearchManager.isResearchVisible(player, key);
        boolean unlockable = TCResearchManager.canUnlockResearch(player, key);

        context.getSource().sendSuccess(() -> Component.literal(
                player.getGameProfile().getName() + " " + formatResearchState(knowledge, key)
                        + ", visible=" + visible
                        + ", can_unlock=" + unlockable
                        + ", requisites=" + requisites
        ), false);

        sendStageRequirementReport(context.getSource(), TCResearchManager.checkCurrentStageRequirements(player, key), 3);
        return TCResearchManager.isResearchKnown(knowledge, key) ? 1 : 0;
    }

    private static int showPlayerCurrentStageRequirements(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String key = TCPlayerKnowledge.normalizeResearchKey(StringArgumentType.getString(context, "research_key"));
        TCResearchStageRequirementResult result = TCResearchManager.checkCurrentStageRequirements(player, key);
        sendStageRequirementReport(context.getSource(), result, 12);
        return result.passed() ? 1 : 0;
    }

    private static int advancePlayerCurrentStage(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String key = TCPlayerKnowledge.normalizeResearchKey(StringArgumentType.getString(context, "research_key"));
        TCResearchStageRequirementResult result = TCResearchManager.checkCurrentStageRequirements(player, key);

        if (!result.hasStage() || !result.passed()) {
            context.getSource().sendFailure(Component.literal(
                    "Cannot advance current Thaumcraft research stage for " + player.getGameProfile().getName() + "."
            ));
            sendStageRequirementReport(context.getSource(), result, 12);
            return 0;
        }

        boolean progressed = TCResearchManager.completeCurrentStageWithChecks(player, key, true, true);
        if (!progressed) {
            context.getSource().sendFailure(Component.literal(
                    "Stage requirements passed, but checked progression failed for " + key + "."
            ));
            return 0;
        }

        TCPlayerKnowledge updated = TCPlayerKnowledgeStore.get(player);
        context.getSource().sendSuccess(() -> Component.literal(
                "Advanced current Thaumcraft research stage for " + player.getGameProfile().getName()
                        + ": " + formatResearchState(updated, key)
        ), true);
        return Math.max(1, updated.getResearchStage(key));
    }

    private static int grantPlayerResearch(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String key = TCPlayerKnowledge.normalizeResearchKey(StringArgumentType.getString(context, "research_key"));

        boolean progressed = TCResearchManager.completeResearch(player, key, true);

        if (!progressed && TCResearchManager.isResearchComplete(TCPlayerKnowledgeStore.get(player), key)) {
            context.getSource().sendFailure(Component.literal(
                    player.getGameProfile().getName() + " already has complete Thaumcraft research key " + key + "."
            ));
            return 0;
        }

        if (!progressed) {
            context.getSource().sendFailure(Component.literal(
                    "Could not progress Thaumcraft research key " + key + " for " + player.getGameProfile().getName()
                            + ". Missing parent requisites or invalid key."
            ));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal(
                "Completed/progressed Thaumcraft research key " + key + " for " + player.getGameProfile().getName() + "."
        ), true);

        return Math.max(1, TCPlayerKnowledgeStore.get(player).getResearchStage(key));
    }

    private static int revokePlayerResearch(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String key = TCPlayerKnowledge.normalizeResearchKey(StringArgumentType.getString(context, "research_key"));

        TCPlayerKnowledgeStore.mutate(player, knowledge -> knowledge.revokeResearch(key));

        context.getSource().sendSuccess(() -> Component.literal(
                "Revoked stored Thaumcraft research key " + key + " from " + player.getGameProfile().getName() + "."
        ), true);

        return 1;
    }

    private static void sendTypeSummary(CommandSourceStack source, TCPlayerKnowledge knowledge, TCKnowledgeType type) {
        Map<String, Integer> rawByCategory = knowledge.getRawByCategory(type);

        if (rawByCategory.isEmpty()) {
            source.sendSuccess(() -> Component.literal("- " + type.id() + ": none"), false);
            return;
        }

        for (Map.Entry<String, Integer> entry : rawByCategory.entrySet()) {
            int points = type.rawToPoints(entry.getValue());
            source.sendSuccess(() -> Component.literal(
                    "- " + type.id() + " " + entry.getKey() + ": points=" + points + ", raw=" + entry.getValue()
            ), false);
        }
    }

    private static String formatResearchState(TCPlayerKnowledge knowledge, String key) {
        TCResearchStatus status = TCResearchManager.getResearchStatus(knowledge, key);
        int stage = knowledge.getResearchStage(key);
        Set<TCResearchFlag> flags = knowledge.researchFlags().getOrDefault(key, Set.of());
        String suffix = flags.isEmpty() ? "" : ", flags=" + flags;
        return key + " [" + status + ", stage=" + stage + suffix + "]";
    }

    private static void sendStageRequirementReport(CommandSourceStack source, TCResearchStageRequirementResult result, int detailLimit) {
        int displayStage = result.stageIndex() + 1;
        source.sendSuccess(() -> Component.literal(
                "Stage requirements for " + result.researchKey()
                        + ": stage=" + displayStage + "/" + result.totalStages()
                        + ", passed=" + result.passed()
                        + ", satisfied=" + result.satisfied().size()
                        + ", missing=" + result.missing().size()
                        + ", blocked=" + result.blocked().size()
        ), false);

        result.missing().stream()
                .limit(detailLimit)
                .forEach(line -> source.sendFailure(Component.literal("- missing " + line)));
        result.blocked().stream()
                .limit(detailLimit)
                .forEach(line -> source.sendFailure(Component.literal("- blocked " + line)));
    }

    private static TCKnowledgeType parseTypeOrFail(CommandContext<CommandSourceStack> context) {
        String value = StringArgumentType.getString(context, "type");
        TCKnowledgeType type = TCKnowledgeType.parse(value);

        if (type == null) {
            context.getSource().sendFailure(Component.literal("Unknown knowledge type: " + value + ". Expected observation or theory."));
            throw new IllegalArgumentException("Unknown knowledge type: " + value);
        }

        return type;
    }

    private static ServerPlayer resolvePlayer(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        try {
            return EntityArgument.getPlayer(context, "player");
        } catch (IllegalArgumentException ignored) {
            return context.getSource().getPlayerOrException();
        }
    }
}
