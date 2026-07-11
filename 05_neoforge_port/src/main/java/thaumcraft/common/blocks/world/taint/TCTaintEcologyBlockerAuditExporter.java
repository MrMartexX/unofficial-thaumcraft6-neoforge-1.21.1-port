package thaumcraft.common.blocks.world.taint;

import java.nio.file.Path;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCTaintEcologyBlockerAuditExporter {
    private TCTaintEcologyBlockerAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.getBoolean(TCTaintEcologyBlockerAudit.ENABLE_PROPERTY)) {
            return;
        }
        String output = System.getProperty(
                TCTaintEcologyBlockerAudit.OUTPUT_PROPERTY,
                "../../06_docs/audits/generated/thaumcraft_1_21_taint_ecology_blocker_audit.md"
        );
        try {
            TCTaintEcologyBlockerAudit.Report report =
                    TCTaintEcologyBlockerAudit.writeMarkdown(Path.of(output), event.getServer());
            Thaumcraft.LOGGER.info("Taint ecology blocker audit complete: {} passed, {} failed", report.passed(), report.failed());
            event.getServer().halt(false);
        } catch (Exception exception) {
            Thaumcraft.LOGGER.error("Taint ecology blocker audit failed", exception);
            event.getServer().halt(false);
        }
    }
}
