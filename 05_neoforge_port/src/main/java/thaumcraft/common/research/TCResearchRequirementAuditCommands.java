package thaumcraft.common.research;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import thaumcraft.common.config.TCConfig;

public final class TCResearchRequirementAuditCommands {
    private static final String[] ROOT_ALIASES = {"thaumcraft", "thaum", "tc"};
    private static final int DEFAULT_DETAIL_LIMIT = 20;
    private static final int MAX_DETAIL_LIMIT = 200;
    private static final int SUMMARY_LIMIT = 30;

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
        TCResearchRequirementAudit.Report report = TCResearchRequirementAudit.buildReport(detailLimit);

        context.getSource().sendSuccess(() -> Component.literal(
                "Research stage requirement audit: required_item=" + report.itemResolved() + "/" + report.itemTotal()
                        + " resolved, required_craft=" + report.craftResolved() + "/" + report.craftTotal()
                        + " modern-matchable, required_knowledge=" + report.knowledgeResolved() + "/" + report.knowledgeTotal()
                        + " resolved. unresolved=" + report.unresolvedTotal()
                        + ", bridge/placeholder warnings=" + report.bridgeWarningTotal()
        ), false);

        context.getSource().sendSuccess(() -> Component.literal(
                "Note: required_craft modern-matchable means the current ItemCraftedEvent marker path can identify the crafted stack. Exact legacy ItemStack.toString().hashCode() parity is still a separate exporter task."
        ), false);
        context.getSource().sendSuccess(() -> Component.literal(
                "Bridge warnings mean the registry identity is resolvable, but final gameplay source, item semantics or container/component behavior may still be blocked."
        ), false);

        if (!report.unresolvedSummary().isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("Unresolved requirement summary:"), false);
            report.sortedUnresolvedSummary().stream()
                    .limit(SUMMARY_LIMIT)
                    .forEach(entry -> context.getSource().sendFailure(Component.literal(
                            "- " + entry.getValue() + "x " + entry.getKey()
                    )));
        }

        if (!report.unresolvedDetails().isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal(
                    "First unresolved requirement details, limit=" + detailLimit + ":"
            ), false);
        }

        for (String detail : report.unresolvedDetails()) {
            context.getSource().sendFailure(Component.literal("- " + detail));
        }

        if (!report.bridgeWarningSummary().isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("Bridge/placeholder warning summary:"), false);
            report.sortedBridgeWarningSummary().stream()
                    .limit(SUMMARY_LIMIT)
                    .forEach(entry -> context.getSource().sendFailure(Component.literal(
                            "- " + entry.getValue() + "x " + entry.getKey()
                    )));
        }

        return report.unresolvedTotal() == 0 ? 1 : report.unresolvedTotal();
    }
}
