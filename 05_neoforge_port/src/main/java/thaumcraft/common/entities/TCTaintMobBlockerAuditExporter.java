package thaumcraft.common.entities;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCTaintMobBlockerAuditExporter {
    private TCTaintMobBlockerAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty(TCTaintMobBlockerAudit.ENABLE_PROPERTY, "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    TCTaintMobBlockerAudit.OUTPUT_PROPERTY,
                    "../../06_docs/audits/generated/thaumcraft_1_21_taint_mob_blocker_audit.md"
            )).toAbsolutePath().normalize();
            TCTaintMobBlockerAudit.Report report = TCTaintMobBlockerAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft taint mob blocker audit to {}: checks={}/{} passed.",
                    output,
                    report.passed(),
                    report.passed() + report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft taint mob blocker audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft taint mob blocker audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
