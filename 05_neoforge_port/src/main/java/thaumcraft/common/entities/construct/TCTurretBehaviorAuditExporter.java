package thaumcraft.common.entities.construct;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

public final class TCTurretBehaviorAuditExporter {
    private static final Logger LOGGER = LogUtils.getLogger();

    private TCTurretBehaviorAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        String prop = System.getProperty("tc.turretBehaviorAudit");
        if (!"true".equalsIgnoreCase(prop) && !Boolean.getBoolean("tcTurretBehaviorAudit")) {
            return;
        }

        MinecraftServer server = event.getServer();
        String outPath = System.getProperty("tc.turretBehaviorAuditPath");
        if (outPath == null || outPath.isBlank()) {
            outPath = "turret_behavior_parity_audit.md";
        }

        try {
            Path output = Paths.get(outPath).toAbsolutePath().normalize();
            TCTurretBehaviorAudit.Report report = TCTurretBehaviorAudit.writeMarkdown(output, server);
            LOGGER.info("[TCTurretBehaviorAudit] Exported audit report to {} (allPass={})", output, report.allPass());
            if (!report.allPass()) {
                throw new IllegalStateException("Thaumcraft turret behavior parity audit failed");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to export turret behavior parity audit report", e);
        } finally {
            server.halt(false);
        }
    }
}
