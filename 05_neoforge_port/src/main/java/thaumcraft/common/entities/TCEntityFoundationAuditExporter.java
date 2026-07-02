package thaumcraft.common.entities;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCEntityFoundationAuditExporter {
    private TCEntityFoundationAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty(TCEntityFoundationAudit.ENABLE_PROPERTY, "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    TCEntityFoundationAudit.OUTPUT_PROPERTY,
                    "audits/entity_foundation_audit.md"
            )).toAbsolutePath().normalize();
            TCEntityFoundationAudit.Report report = TCEntityFoundationAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft entity foundation audit to {}: checks={}/{} passed.",
                    output,
                    report.passed(),
                    report.passed() + report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft entity foundation audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft entity foundation audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
