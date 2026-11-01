package thaumcraft.common.items.casters;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

public final class TCFocusMediumEffectParityAuditExporter {
    private static final Logger LOGGER = LogUtils.getLogger();

    private TCFocusMediumEffectParityAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        String prop = System.getProperty("tc.focusMediumEffectAudit");
        if (!"true".equalsIgnoreCase(prop) && !Boolean.getBoolean("tcFocusMediumEffectAudit")) {
            return;
        }

        MinecraftServer server = event.getServer();
        String outPath = System.getProperty("tc.focusMediumEffectAuditPath");
        if (outPath == null || outPath.isBlank()) {
            outPath = "focus_medium_effect_parity_audit.md";
        }

        try {
            Path output = Paths.get(outPath).toAbsolutePath().normalize();
            TCFocusMediumEffectParityAudit.Report report = TCFocusMediumEffectParityAudit.writeMarkdown(output, server);
            LOGGER.info("[TCFocusMediumEffectAudit] Exported audit report to {} (allPass={})", output, report.allPass());
            if (!report.allPass()) {
                throw new IllegalStateException("Thaumcraft focus medium/effect parity audit failed");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to export focus medium/effect parity audit report", e);
        } finally {
            server.halt(false);
        }
    }
}
