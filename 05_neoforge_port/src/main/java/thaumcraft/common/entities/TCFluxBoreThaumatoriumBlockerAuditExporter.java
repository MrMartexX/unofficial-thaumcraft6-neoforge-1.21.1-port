package thaumcraft.common.entities;

import java.nio.file.Path;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCFluxBoreThaumatoriumBlockerAuditExporter {
    private static final String ENABLED_PROPERTY = "tc.fluxBoreThaumatoriumAudit";
    private static final String PATH_PROPERTY = "tc.fluxBoreThaumatoriumAuditPath";
    private static final Path DEFAULT_OUTPUT = Path.of(
            "..",
            "..",
            "06_docs",
            "audits",
            "generated",
            "thaumcraft_1_21_flux_bore_thaumatorium_blocker_audit.md"
    );

    private TCFluxBoreThaumatoriumBlockerAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty(ENABLED_PROPERTY, "false"))) {
            return;
        }
        Path output = Path.of(System.getProperty(PATH_PROPERTY, DEFAULT_OUTPUT.toString()));
        try {
            TCFluxBoreThaumatoriumBlockerAudit.Report report =
                    TCFluxBoreThaumatoriumBlockerAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Flux/Bore/Thaumatorium blocker audit written to {} ({} passed, {} failed)",
                    output,
                    report.passed(),
                    report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Flux/Bore/Thaumatorium blocker audit failed: " + report.failed());
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to write Flux/Bore/Thaumatorium blocker audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
