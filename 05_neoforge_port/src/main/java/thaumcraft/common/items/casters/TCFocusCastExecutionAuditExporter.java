package thaumcraft.common.items.casters;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCFocusCastExecutionAuditExporter {
    private TCFocusCastExecutionAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.focusCastExecutionAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.focusCastExecutionAuditPath",
                    "focus_caster/thaumcraft_1_21_focus_cast_execution_audit.md"
            )).toAbsolutePath().normalize();
            TCFocusCastExecutionAudit.Report report = TCFocusCastExecutionAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft focus cast execution audit to {}: checks={}/{} passed.",
                    output,
                    report.passed(),
                    report.passed() + report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft focus cast execution audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft focus cast execution audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
