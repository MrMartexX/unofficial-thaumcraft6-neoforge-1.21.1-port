package thaumcraft.common.crafting.crucible;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCCrucibleBehaviorAuditExporter {
    private TCCrucibleBehaviorAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.crucibleBehaviorAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.crucibleBehaviorAuditPath",
                    "crucible/thaumcraft_1_21_crucible_behavior_audit.md"
            )).toAbsolutePath().normalize();
            TCCrucibleBehaviorAudit.Report report = TCCrucibleBehaviorAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft crucible behavior audit to {}: checks={}/{} passed.",
                    output,
                    report.passed(),
                    report.passed() + report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft crucible behavior audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft crucible behavior audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
