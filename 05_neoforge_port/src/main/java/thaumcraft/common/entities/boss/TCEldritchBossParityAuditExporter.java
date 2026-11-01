package thaumcraft.common.entities.boss;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

public final class TCEldritchBossParityAuditExporter {
    private static final Logger LOGGER = LogUtils.getLogger();

    private TCEldritchBossParityAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        String prop = System.getProperty("tc.eldritchBossAudit");
        if (!"true".equalsIgnoreCase(prop) && !Boolean.getBoolean("tcEldritchBossAudit")) {
            return;
        }

        MinecraftServer server = event.getServer();
        String outPath = System.getProperty("tc.eldritchBossAuditPath");
        if (outPath == null || outPath.isBlank()) {
            outPath = "eldritch_boss_parity_audit.md";
        }

        try {
            Path output = Paths.get(outPath).toAbsolutePath().normalize();
            TCEldritchBossParityAudit.Report report = TCEldritchBossParityAudit.writeMarkdown(output, server);
            LOGGER.info("[TCEldritchBossParityAudit] Exported audit report to {} (allPass={})", output, report.allPass());
            if (!report.allPass()) {
                throw new IllegalStateException("Thaumcraft Eldritch bosses parity audit failed");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to export Eldritch boss parity audit report", e);
        } finally {
            server.halt(false);
        }
    }
}
