package thaumcraft.common.essentia.transport;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCEssentiaTransportBehaviorAuditExporter {
    private TCEssentiaTransportBehaviorAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.essentiaTransportBehaviorAudit", "false"))) {
            return;
        }
        try {
            Path output = Paths.get(System.getProperty(
                    "tc.essentiaTransportBehaviorAuditPath",
                    "essentia/thaumcraft_1_21_essentia_transport_behavior_audit.md"
            )).toAbsolutePath().normalize();
            TCEssentiaTransportBehaviorAudit.Report report =
                    TCEssentiaTransportBehaviorAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info("Wrote essentia transport behavior audit to {}: checks={}/{} passed.",
                    output, report.passed(), report.passed() + report.failed());
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft essentia transport behavior audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write essentia transport behavior audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
