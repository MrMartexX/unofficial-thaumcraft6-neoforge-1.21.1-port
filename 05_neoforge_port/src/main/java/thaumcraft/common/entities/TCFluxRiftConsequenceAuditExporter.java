package thaumcraft.common.entities;

import java.nio.file.Path;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCFluxRiftConsequenceAuditExporter {
    private TCFluxRiftConsequenceAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.getBoolean(TCFluxRiftConsequenceAudit.ENABLE_PROPERTY)) {
            return;
        }
        String output = System.getProperty(
                TCFluxRiftConsequenceAudit.OUTPUT_PROPERTY,
                "../../06_docs/audits/generated/thaumcraft_1_21_flux_rift_consequence_audit.md"
        );
        try {
            TCFluxRiftConsequenceAudit.Report report =
                    TCFluxRiftConsequenceAudit.writeMarkdown(Path.of(output), event.getServer());
            Thaumcraft.LOGGER.info("Flux Rift consequence audit complete: {} passed, {} failed", report.passed(), report.failed());
            event.getServer().halt(false);
        } catch (Exception exception) {
            Thaumcraft.LOGGER.error("Flux Rift consequence audit failed", exception);
            event.getServer().halt(false);
        }
    }
}
