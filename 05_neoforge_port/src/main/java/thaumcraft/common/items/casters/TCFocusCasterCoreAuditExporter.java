package thaumcraft.common.items.casters;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCFocusCasterCoreAuditExporter {
    private TCFocusCasterCoreAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.focusCasterCoreAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.focusCasterCoreAuditPath",
                    "focus_caster/thaumcraft_1_21_focus_caster_core_audit.md"
            )).toAbsolutePath().normalize();
            TCFocusCasterCoreAudit.Report report = TCFocusCasterCoreAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft focus/caster core audit to {}: checks={}/{} passed.",
                    output,
                    report.passed(),
                    report.passed() + report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft focus/caster core audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft focus/caster core audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
