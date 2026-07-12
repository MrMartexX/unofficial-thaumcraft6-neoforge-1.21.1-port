package thaumcraft.common.crafting.crucible;

import java.nio.file.Path;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCSpecialAlchemyBehaviorAuditExporter {
    private TCSpecialAlchemyBehaviorAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.getBoolean(TCSpecialAlchemyBehaviorAudit.ENABLE_PROPERTY)) {
            return;
        }

        try {
            String output = System.getProperty(
                    TCSpecialAlchemyBehaviorAudit.OUTPUT_PROPERTY,
                    "../../06_docs/audits/generated/thaumcraft_1_21_special_alchemy_behavior_audit.md"
            );
            TCSpecialAlchemyBehaviorAudit.Report report =
                    TCSpecialAlchemyBehaviorAudit.writeMarkdown(Path.of(output), event.getServer());
            Thaumcraft.LOGGER.info("Special alchemy behavior audit complete: {} passed, {} failed", report.passed(), report.failed());
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumcraft special alchemy behavior audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft special alchemy behavior audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
