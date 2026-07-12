package thaumcraft.common.entities;

import java.nio.file.Path;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCWispBehaviorAuditExporter {
    private TCWispBehaviorAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.getBoolean(TCWispBehaviorAudit.ENABLE_PROPERTY)) {
            return;
        }
        String output = System.getProperty(
                TCWispBehaviorAudit.OUTPUT_PROPERTY,
                "../../06_docs/audits/generated/thaumcraft_1_21_wisp_behavior_audit.md"
        );
        try {
            TCWispBehaviorAudit.Report report =
                    TCWispBehaviorAudit.writeMarkdown(Path.of(output), event.getServer());
            Thaumcraft.LOGGER.info("Wisp behavior audit complete: {} passed, {} failed", report.passed(), report.failed());
            event.getServer().halt(false);
        } catch (Exception exception) {
            Thaumcraft.LOGGER.error("Wisp behavior audit failed", exception);
            event.getServer().halt(false);
        }
    }
}
