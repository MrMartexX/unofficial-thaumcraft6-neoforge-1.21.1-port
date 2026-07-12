package thaumcraft.common.tiles.devices;

import java.nio.file.Path;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCAlchemyAutomationDeviceAuditExporter {
    private TCAlchemyAutomationDeviceAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty(TCAlchemyAutomationDeviceAudit.ENABLE_PROPERTY, "false"))) {
            return;
        }
        try {
            Path output = Path.of(System.getProperty(
                    TCAlchemyAutomationDeviceAudit.PATH_PROPERTY,
                    TCAlchemyAutomationDeviceAudit.DEFAULT_OUTPUT.toString()
            ));
            TCAlchemyAutomationDeviceAudit.Report report = TCAlchemyAutomationDeviceAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Alchemy automation device audit complete: {} passed, {} failed",
                    report.passed(),
                    report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft alchemy automation device audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft alchemy automation device audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
