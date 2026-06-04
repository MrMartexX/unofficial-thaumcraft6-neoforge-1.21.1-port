package thaumcraft.common.research;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import thaumcraft.common.config.TCConfig;

public final class TCResearchPageCatalogCommands {
    private static final String[] ROOT_ALIASES = {"thaumcraft", "thaum", "tc"};

    private TCResearchPageCatalogCommands() {
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
                            .then(Commands.literal("catalog")
                                    .executes(TCResearchPageCatalogCommands::summary)
                                    .then(Commands.argument("research_key", StringArgumentType.word())
                                            .executes(TCResearchPageCatalogCommands::bookmarks)))));
        }
    }

    private static int summary(CommandContext<CommandSourceStack> context) {
        TCResearchPageCatalogAudit.Report report = TCResearchPageCatalogAudit.buildReport(
                context.getSource().getServer().getRecipeManager()
        );
        context.getSource().sendSuccess(() -> Component.literal(
                "Research page catalog: direct=" + report.validation().directReferenceCount()
                        + ", total=" + report.validation().totalEntryCount()
                        + ", occurrences=" + report.validation().researchOccurrenceCount()
                        + ", ready=" + report.availabilityCounts().getOrDefault(TCResearchPageAvailability.READY, 0)
                        + ", deferred=" + report.availabilityCounts().getOrDefault(TCResearchPageAvailability.DEFERRED, 0)
                        + ", legacy_missing=" + report.availabilityCounts().getOrDefault(TCResearchPageAvailability.LEGACY_MISSING, 0)
                        + ", structural_valid=" + report.validation().isValid()
        ), false);
        return report.validation().isValid() ? 1 : 0;
    }

    private static int bookmarks(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String researchKey = StringArgumentType.getString(context, "research_key");
        var bookmarks = TCResearchPageCatalogManager.bookmarksForCurrentStage(player, researchKey);
        context.getSource().sendSuccess(() -> Component.literal(
                "Current-stage catalog bookmarks for " + researchKey + ": " + bookmarks.size()
        ), false);
        for (TCResearchPageBookmark bookmark : bookmarks) {
            String pages = bookmark.pages().stream()
                    .map(page -> page.id() + "[" + page.kind() + "/" + page.availability() + "]")
                    .collect(java.util.stream.Collectors.joining(", "));
            context.getSource().sendSuccess(() -> Component.literal("- " + bookmark.id() + ": " + pages), false);
        }
        return bookmarks.size();
    }
}
