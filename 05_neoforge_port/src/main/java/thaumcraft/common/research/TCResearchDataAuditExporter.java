package thaumcraft.common.research;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCResearchDataAuditExporter {
    private TCResearchDataAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.researchDataAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.researchDataAuditPath",
                    "research_data_parity/thaumcraft_1_21_research_data.json"
            )).toAbsolutePath().normalize();
            TCResearchDataAudit.Report report = TCResearchDataAudit.writeJson(output);
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft research data audit to {}: categories={}, entries={}, stages={}, addenda={}, progression_checks={}/{} passed.",
                    output,
                    report.categories(),
                    report.entries(),
                    report.stages(),
                    report.addenda(),
                    report.progressionChecksPassed(),
                    report.progressionChecksPassed() + report.progressionChecksFailed()
            );
            if (report.progressionChecksFailed() > 0) {
                throw new IllegalStateException("Thaumcraft research progression parity checks failed");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to write Thaumcraft research data audit", e);
        } finally {
            event.getServer().halt(false);
        }
    }
}
