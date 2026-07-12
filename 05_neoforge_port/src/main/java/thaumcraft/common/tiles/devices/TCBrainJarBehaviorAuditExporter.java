package thaumcraft.common.tiles.devices;

import java.nio.file.Path;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCBrainJarBehaviorAuditExporter {
    private TCBrainJarBehaviorAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty(TCBrainJarBehaviorAudit.ENABLE_PROPERTY, "false"))) {
            return;
        }
        try {
            Path output = Path.of(System.getProperty(
                    TCBrainJarBehaviorAudit.PATH_PROPERTY,
                    TCBrainJarBehaviorAudit.DEFAULT_OUTPUT.toString()
            ));
            TCBrainJarBehaviorAudit.Report report = TCBrainJarBehaviorAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Brain-in-a-Jar behavior audit complete: {} passed, {} failed",
                    report.passed(),
                    report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft Brain-in-a-Jar behavior audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft Brain-in-a-Jar behavior audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
