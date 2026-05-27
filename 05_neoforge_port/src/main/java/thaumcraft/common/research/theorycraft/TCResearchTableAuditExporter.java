package thaumcraft.common.research.theorycraft;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCResearchTableAuditExporter {
    private TCResearchTableAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.researchTableAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.researchTableAuditPath",
                    "research_table_audit/thaumcraft_1_21_research_table.md"
            )).toAbsolutePath().normalize();
            TCResearchTableDiagnosticReport report = TCResearchTableDiagnostics.buildStaticReport();
            TCResearchTableDiagnostics.writeMarkdown(output, report);
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft research table diagnostic to {}: {} passed, {} failed.",
                    output,
                    report.passedCount(),
                    report.failedCount()
            );
            if (!report.passed()) {
                throw new IllegalStateException("Thaumcraft research table diagnostic failed");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to write Thaumcraft research table diagnostic", e);
        } finally {
            event.getServer().halt(false);
        }
    }
}
