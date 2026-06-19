package thaumcraft.common.crafting.infusion;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCInfusionBehaviorAuditExporter {
    private TCInfusionBehaviorAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.infusionBehaviorAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.infusionBehaviorAuditPath",
                    "infusion/thaumcraft_1_21_infusion_behavior_audit.md"
            )).toAbsolutePath().normalize();
            TCInfusionBehaviorAudit.Report report = TCInfusionBehaviorAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft infusion behavior audit to {}: checks={}/{} passed.",
                    output,
                    report.passed(),
                    report.passed() + report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft infusion behavior audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft infusion behavior audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
