package thaumcraft.common.tiles.devices;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCLampDeviceAuditExporter {
    private TCLampDeviceAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.lampDeviceAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.lampDeviceAuditPath",
                    "audits/lamp_device_audit.md"
            )).toAbsolutePath().normalize();
            TCLampDeviceAudit.Report report = TCLampDeviceAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft lamp device audit to {}: checks={}/{} passed.",
                    output,
                    report.passed(),
                    report.passed() + report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft lamp device audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft lamp device audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
