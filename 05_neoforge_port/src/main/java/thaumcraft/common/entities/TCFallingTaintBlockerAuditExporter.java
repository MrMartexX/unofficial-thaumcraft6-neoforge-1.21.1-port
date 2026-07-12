package thaumcraft.common.entities;

import java.nio.file.Path;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCFallingTaintBlockerAuditExporter {
    private TCFallingTaintBlockerAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.getBoolean(TCFallingTaintBlockerAudit.ENABLE_PROPERTY)) {
            return;
        }
        String output = System.getProperty(
                TCFallingTaintBlockerAudit.OUTPUT_PROPERTY,
                "../../06_docs/audits/generated/thaumcraft_1_21_falling_taint_blocker_audit.md"
        );
        try {
            TCFallingTaintBlockerAudit.Report report =
                    TCFallingTaintBlockerAudit.writeMarkdown(Path.of(output), event.getServer());
            Thaumcraft.LOGGER.info("FallingTaint blocker audit complete: {} passed, {} failed", report.passed(), report.failed());
            event.getServer().halt(false);
        } catch (Exception exception) {
            Thaumcraft.LOGGER.error("FallingTaint blocker audit failed", exception);
            event.getServer().halt(false);
        }
    }
}
