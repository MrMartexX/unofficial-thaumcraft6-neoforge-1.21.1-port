package thaumcraft.common.research.theorycraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import thaumcraft.common.config.TCConfig;

public final class TCResearchTableAuditCommands {
    private static final String[] ROOT_ALIASES = {"thaumcraft", "thaum", "tc"};

    private TCResearchTableAuditCommands() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (!TCConfig.ENABLE_KNOWLEDGE_DEBUG_COMMANDS.get()) {
            return;
        }

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        for (String root : ROOT_ALIASES) {
            dispatcher.register(Commands.literal(root)
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.literal("research_table")
                            .then(Commands.literal("validate")
                                    .executes(TCResearchTableAuditCommands::validateStatic)
                                    .then(Commands.literal("player")
                                            .executes(TCResearchTableAuditCommands::validatePlayer)))));
        }
    }

    private static int validateStatic(CommandContext<CommandSourceStack> context) {
        TCResearchTableDiagnosticReport report = TCResearchTableDiagnostics.buildStaticReport();
        sendReport(context.getSource(), "Research table static diagnostic", report);
        return report.passed() ? 1 : 0;
    }

    private static int validatePlayer(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        TCResearchTableDiagnosticReport report = TCResearchTableDiagnostics.buildPlayerReport(player);
        sendReport(context.getSource(), "Research table player diagnostic for " + player.getGameProfile().getName(), report);
        return report.passed() ? 1 : 0;
    }

    private static void sendReport(CommandSourceStack source, String title, TCResearchTableDiagnosticReport report) {
        source.sendSuccess(() -> Component.literal(
                title + ": " + report.passedCount() + " passed, " + report.failedCount() + " failed."
        ), false);

        for (TCResearchTableDiagnosticReport.Row row : report.rows()) {
            Component line = Component.literal((row.passed() ? "[PASS] " : "[FAIL] ") + row.check() + " - " + row.notes());
            if (row.passed()) {
                source.sendSuccess(() -> line, false);
            } else {
                source.sendFailure(line);
            }
        }
    }
}
