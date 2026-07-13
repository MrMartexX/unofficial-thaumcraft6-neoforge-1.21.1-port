package thaumcraft.common.warp;

import java.nio.file.Path;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCWarpEventBehaviorAuditExporter {
    private TCWarpEventBehaviorAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.getBoolean(TCWarpEventBehaviorAudit.ENABLE_PROPERTY)) {
            return;
        }
        String output = System.getProperty(
                TCWarpEventBehaviorAudit.OUTPUT_PROPERTY,
                "../../06_docs/audits/generated/thaumcraft_1_21_warp_event_behavior_audit.md"
        );
        try {
            TCWarpEventBehaviorAudit.Report report = TCWarpEventBehaviorAudit.writeMarkdown(Path.of(output), event.getServer());
            Thaumcraft.LOGGER.info("Warp event behavior audit complete: {} passed, {} failed", report.passed(), report.failed());
            if (report.failed() > 0) {
                throw new IllegalStateException("Warp event behavior audit failed");
            }
        } catch (Exception exception) {
            Thaumcraft.LOGGER.error("Warp event behavior audit failed", exception);
            throw new IllegalStateException("Warp event behavior audit failed", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
