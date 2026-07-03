package thaumcraft.common.tiles.devices;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCInfernalFurnaceBehaviorAuditExporter {
    private TCInfernalFurnaceBehaviorAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.infernalFurnaceBehaviorAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.infernalFurnaceBehaviorAuditPath",
                    "audits/infernal_furnace_behavior_audit.md"
            )).toAbsolutePath().normalize();
            TCInfernalFurnaceBehaviorAudit.Report report =
                    TCInfernalFurnaceBehaviorAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft Infernal Furnace behavior audit to {}: checks={}/{} passed.",
                    output,
                    report.passed(),
                    report.passed() + report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft Infernal Furnace behavior audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft Infernal Furnace behavior audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
