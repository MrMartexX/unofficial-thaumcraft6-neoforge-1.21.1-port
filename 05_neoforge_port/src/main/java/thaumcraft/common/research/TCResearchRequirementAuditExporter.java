package thaumcraft.common.research;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCResearchRequirementAuditExporter {
    private TCResearchRequirementAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.researchRequirementAudit", "false"))) {
            return;
        }

        try {
            int detailLimit = TCResearchRequirementResolver.parsePositiveInt(
                    System.getProperty("tc.researchRequirementAuditDetailLimit", "200"),
                    200
            );
            Path output = Paths.get(System.getProperty(
                    "tc.researchRequirementAuditPath",
                    "research_requirement_audit/thaumcraft_1_21_research_requirements.md"
            )).toAbsolutePath().normalize();
            TCResearchRequirementAudit.Report report = TCResearchRequirementAudit.writeMarkdown(output, detailLimit);
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft research requirement audit to {}: required_item={}/{} resolved, required_craft={}/{} modern-matchable, required_knowledge={}/{} resolved, unresolved={}, bridge_warnings={}",
                    output,
                    report.itemResolved(),
                    report.itemTotal(),
                    report.craftResolved(),
                    report.craftTotal(),
                    report.knowledgeResolved(),
                    report.knowledgeTotal(),
                    report.unresolvedTotal(),
                    report.bridgeWarningTotal()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to write Thaumcraft research requirement audit", e);
        } finally {
            event.getServer().halt(false);
        }
    }
}
