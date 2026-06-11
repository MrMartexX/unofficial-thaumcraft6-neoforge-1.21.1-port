package thaumcraft.common.crafting.arcane;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCArcaneWorkbenchAuditExporter {
    private TCArcaneWorkbenchAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.arcaneWorkbenchAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.arcaneWorkbenchAuditPath",
                    "arcane_crafting/thaumcraft_1_21_arcane_workbench_audit.md"
            )).toAbsolutePath().normalize();
            TCArcaneWorkbenchAudit.Report report = TCArcaneWorkbenchAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft arcane workbench audit to {}: checks={}/{} passed.",
                    output,
                    report.passed(),
                    report.passed() + report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft arcane workbench audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft arcane workbench audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
