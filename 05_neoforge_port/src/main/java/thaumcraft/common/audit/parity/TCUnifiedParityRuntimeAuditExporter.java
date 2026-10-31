package thaumcraft.common.audit.parity;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCUnifiedParityRuntimeAuditExporter {
    private TCUnifiedParityRuntimeAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty(TCUnifiedParityRuntimeAudit.ENABLE_PROPERTY, "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    TCUnifiedParityRuntimeAudit.OUTPUT_PROPERTY,
                    "06_docs/audits/generated/tc6_runtime_parity_matrix.md"
            )).toAbsolutePath().normalize();

            TCUnifiedParityRuntimeAudit.Report report = TCUnifiedParityRuntimeAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft master runtime parity matrix to {}: checks={}/{} passed ({}%).",
                    output,
                    report.passed(),
                    report.passed() + report.failed(),
                    String.format("%.1f", report.percent())
            );
            if (report.failed() > 0 && Boolean.parseBoolean(System.getProperty("thaumcraft.audit.fail_on_error", "false"))) {
                throw new IllegalStateException("Parity runtime audit failed with " + report.failed() + " failures!");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft master runtime parity report", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
