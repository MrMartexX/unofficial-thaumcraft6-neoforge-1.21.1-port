package thaumcraft.common.golems;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

public final class TCGolemBehaviorAuditExporter {
    private static final Logger LOGGER = LogUtils.getLogger();

    private TCGolemBehaviorAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        String prop = System.getProperty("tc.golemBehaviorAudit");
        if (!"true".equalsIgnoreCase(prop) && !Boolean.getBoolean("tcGolemBehaviorAudit")) {
            return;
        }

        MinecraftServer server = event.getServer();
        String outPath = System.getProperty("tc.golemBehaviorAuditPath");
        if (outPath == null || outPath.isBlank()) {
            outPath = "golem_behavior_audit.md";
        }

        try {
            Path output = Paths.get(outPath).toAbsolutePath().normalize();
            TCGolemBehaviorAudit.Report report = TCGolemBehaviorAudit.writeMarkdown(output, server);
            LOGGER.info("[TCGolemBehaviorAudit] Exported audit report to {} (allPass={})", output, report.allPass());
            if (!report.allPass()) {
                throw new IllegalStateException("Thaumcraft golem behavior audit failed");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to export golem behavior audit report", e);
        } finally {
            server.halt(false);
        }
    }
}
