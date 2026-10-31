package thaumcraft.common.tiles.devices;

import java.nio.file.Path;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCArtificeDeviceBehaviorAuditExporter {
    private TCArtificeDeviceBehaviorAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty(TCArtificeDeviceBehaviorAudit.ENABLE_PROPERTY, "false"))) {
            return;
        }
        try {
            Path output = Path.of(System.getProperty(
                    TCArtificeDeviceBehaviorAudit.PATH_PROPERTY,
                    TCArtificeDeviceBehaviorAudit.DEFAULT_OUTPUT.toString()
            ));
            TCArtificeDeviceBehaviorAudit.Report report = TCArtificeDeviceBehaviorAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Artifice device behavior audit complete: {} passed, {} failed",
                    report.passed(),
                    report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft artifice device behavior audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft artifice device behavior audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
