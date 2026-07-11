package thaumcraft.common.blocks.world.taint;

import java.nio.file.Path;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCFluxTaintBlockerAuditExporter {
    private static final String ENABLED_PROPERTY = "tc.fluxTaintBlockerAudit";
    private static final String PATH_PROPERTY = "tc.fluxTaintBlockerAuditPath";
    private static final Path DEFAULT_OUTPUT = Path.of(
            "..",
            "..",
            "06_docs",
            "audits",
            "generated",
            "thaumcraft_1_21_flux_taint_blocker_audit.md"
    );

    private TCFluxTaintBlockerAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty(ENABLED_PROPERTY, "false"))) {
            return;
        }
        Path output = Path.of(System.getProperty(PATH_PROPERTY, DEFAULT_OUTPUT.toString()));
        try {
            TCFluxTaintBlockerAudit.Report report =
                    TCFluxTaintBlockerAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Flux/Taint blocker audit written to {} ({} passed, {} failed)",
                    output,
                    report.passed(),
                    report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Flux/Taint blocker audit failed: " + report.failed());
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to write Flux/Taint blocker audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
