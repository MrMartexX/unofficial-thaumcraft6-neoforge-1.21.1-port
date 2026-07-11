package thaumcraft.common.tiles.devices;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCStandaloneDeviceBlockerAuditExporter {
    private TCStandaloneDeviceBlockerAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.standaloneDeviceBlockerAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.standaloneDeviceBlockerAuditPath",
                    "audits/standalone_device_blocker_audit.md"
            )).toAbsolutePath().normalize();
            TCStandaloneDeviceBlockerAudit.Report report =
                    TCStandaloneDeviceBlockerAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft standalone-device blocker audit to {}: checks={}/{} passed.",
                    output,
                    report.passed(),
                    report.passed() + report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft standalone-device blocker audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft standalone-device blocker audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
