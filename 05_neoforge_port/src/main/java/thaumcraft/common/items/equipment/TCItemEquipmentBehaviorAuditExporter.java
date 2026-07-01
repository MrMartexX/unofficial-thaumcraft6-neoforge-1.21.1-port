package thaumcraft.common.items.equipment;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCItemEquipmentBehaviorAuditExporter {
    private TCItemEquipmentBehaviorAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.itemEquipmentBehaviorAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.itemEquipmentBehaviorAuditPath",
                    "item_equipment/thaumcraft_1_21_item_equipment_behavior_audit.md"
            )).toAbsolutePath().normalize();
            TCItemEquipmentBehaviorAudit.Report report = TCItemEquipmentBehaviorAudit.writeMarkdown(output, event.getServer());
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft item/equipment behavior audit to {}: checks={}/{} passed.",
                    output,
                    report.passed(),
                    report.passed() + report.failed()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft item/equipment behavior audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft item/equipment behavior audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
