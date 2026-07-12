package thaumcraft.common.entities;

import java.nio.file.Path;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCEntitySpawnPolicyAuditExporter {
    private TCEntitySpawnPolicyAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.getBoolean(TCEntitySpawnPolicyAudit.ENABLE_PROPERTY)) {
            return;
        }
        String output = System.getProperty(
                TCEntitySpawnPolicyAudit.OUTPUT_PROPERTY,
                "../../06_docs/audits/generated/thaumcraft_1_21_entity_spawn_policy_audit.md"
        );
        try {
            TCEntitySpawnPolicyAudit.Report report =
                    TCEntitySpawnPolicyAudit.writeMarkdown(Path.of(output), event.getServer());
            Thaumcraft.LOGGER.info("Entity spawn policy audit complete: {} passed, {} failed", report.passed(), report.failed());
            event.getServer().halt(false);
        } catch (Exception exception) {
            Thaumcraft.LOGGER.error("Entity spawn policy audit failed", exception);
            event.getServer().halt(false);
        }
    }
}
