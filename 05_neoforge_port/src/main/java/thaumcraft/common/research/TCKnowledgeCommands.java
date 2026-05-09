package thaumcraft.common.research;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
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
                        .executes(TCKnowledgeCommands::listKnownResearchKeysUnavailable))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("list")
                                .executes(TCKnowledgeCommands::listPlayerResearch))
                        .then(Commands.literal("all")
                                .executes(TCKnowledgeCommands::researchAllUnavailable))
                        .then(Commands.literal("reset")
                                .executes(TCKnowledgeCommands::resetPlayerResearch))
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

    private static int listKnownResearchKeysUnavailable(CommandContext<CommandSourceStack> context) {
        context.getSource().sendFailure(Component.literal(
                "Research data registry is not ported yet. Use /thaumcraft research <player> list for stored player research keys."
        ));
        return 0;
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
                player.getGameProfile().getName() + " stored Thaumcraft research keys: " + String.join(", ", keys)
        ), false);

        return keys.size();
    }

    private static int researchAllUnavailable(CommandContext<CommandSourceStack> context) {
        context.getSource().sendFailure(Component.literal(
                "Research all is blocked until the research category and entry loader is ported."
        ));
        return 0;
    }

    private static int resetPlayerResearch(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");

        TCPlayerKnowledgeStore.mutate(player, TCPlayerKnowledge::clearResearch);

        context.getSource().sendSuccess(() -> Component.literal(
                "Cleared stored Thaumcraft research keys for " + player.getGameProfile().getName() + "."
        ), true);

        return 1;
    }

    private static int grantPlayerResearch(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        String key = TCPlayerKnowledge.normalizeResearchKey(StringArgumentType.getString(context, "research_key"));

        TCPlayerKnowledgeStore.mutate(player, knowledge -> knowledge.addResearch(key));

        context.getSource().sendSuccess(() -> Component.literal(
                "Granted stored Thaumcraft research key " + key + " to " + player.getGameProfile().getName() + "."
        ), true);

        return 1;
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