package thaumcraft.common.tiles.devices;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCMirrorBehaviorAuditExporter {
    private TCMirrorBehaviorAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.mirrorBehaviorAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.mirrorBehaviorAuditPath",
                    "audits/mirror_behavior_audit.md"
            )).toAbsolutePath().normalize();
            TCMirrorBehaviorAudit.Report report = TCMirrorBehaviorAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft mirror behavior audit to {}: checks={}/{} passed.",
                    output,
                    report.passed(),
                    report.passed() + report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft mirror behavior audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft mirror behavior audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
