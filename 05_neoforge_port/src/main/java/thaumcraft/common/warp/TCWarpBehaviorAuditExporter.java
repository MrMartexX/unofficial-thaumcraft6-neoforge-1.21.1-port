package thaumcraft.common.warp;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCWarpBehaviorAuditExporter {
    private TCWarpBehaviorAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.warpBehaviorAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.warpBehaviorAuditPath",
                    "warp_behavior_audit.md"
            )).toAbsolutePath().normalize();
            TCWarpBehaviorAudit.Report report = TCWarpBehaviorAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft warp behavior audit to {}: checks={}/{} passed.",
                    output,
                    report.passed(),
                    report.checks().size()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft warp behavior audit failed");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to write Thaumcraft warp behavior audit", e);
        } finally {
            event.getServer().halt(false);
        }
    }
}
