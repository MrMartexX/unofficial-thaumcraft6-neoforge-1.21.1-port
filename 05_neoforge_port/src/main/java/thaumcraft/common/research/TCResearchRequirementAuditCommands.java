package thaumcraft.common.research;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import thaumcraft.common.config.TCConfig;
import thaumcraft.common.research.TCResearchRequirementResolver.ItemRequirementResolution;
import thaumcraft.common.research.TCResearchRequirementResolver.KnowledgeRequirementResolution;

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

        if (!report.unresolvedSummary().isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("Unresolved requirement summary:"), false);
            report.unresolvedSummary().entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                            .thenComparing(Map.Entry.comparingByKey()))
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
        LinkedHashMap<String, Integer> summary = new LinkedHashMap<>();

        for (TCResearchEntryDefinition entry : TCResearchManager.entries()) {
            for (int stageIndex = 0; stageIndex < entry.stages().size(); stageIndex++) {
                TCResearchStageDefinition stage = entry.stages().get(stageIndex);
                String stageLabel = entry.key() + " stage " + (stageIndex + 1);

                for (String required : stage.requiredItem()) {
                    itemTotal++;
                    ItemRequirementResolution resolution = TCResearchRequirementResolver.resolveItemRequirement(required);
                    if (!resolution.resolved()) {
                        itemUnresolved++;
                        recordUnresolved(details, summary, detailLimit, stageLabel, "required_item", required, resolution.reason(), resolution.summaryKey());
                    }
                }

                for (String required : stage.requiredCraft()) {
                    craftTotal++;
                    ItemRequirementResolution resolution = TCResearchRequirementResolver.resolveItemRequirement(required);
                    if (!resolution.resolved()) {
                        craftUnresolved++;
                        recordUnresolved(details, summary, detailLimit, stageLabel, "required_craft", required, resolution.reason(), resolution.summaryKey());
                    }
                }

                for (String required : stage.requiredKnowledge()) {
                    knowledgeTotal++;
                    KnowledgeRequirementResolution resolution = TCResearchRequirementResolver.resolveKnowledgeRequirement(required);
                    if (!resolution.resolved()) {
                        knowledgeUnresolved++;
                        recordUnresolved(details, summary, detailLimit, stageLabel, "required_knowledge", required, resolution.reason(), resolution.summaryKey());
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
                details,
                summary
        );
    }

    private static void recordUnresolved(
            List<String> details,
            LinkedHashMap<String, Integer> summary,
            int detailLimit,
            String stageLabel,
            String type,
            String raw,
            String reason,
            String summaryKey
    ) {
        String bucket = type + " " + summaryKey;
        summary.merge(bucket, 1, Integer::sum);

        if (details.size() >= detailLimit) {
            return;
        }
        details.add(stageLabel + " " + type + " raw=" + raw + " reason=" + reason);
    }

    private record RequirementAuditReport(
            int itemTotal,
            int itemUnresolved,
            int craftTotal,
            int craftUnresolved,
            int knowledgeTotal,
            int knowledgeUnresolved,
            List<String> unresolvedDetails,
            Map<String, Integer> unresolvedSummary
    ) {
        private RequirementAuditReport {
            unresolvedDetails = List.copyOf(unresolvedDetails);
            unresolvedSummary = Map.copyOf(unresolvedSummary);
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
