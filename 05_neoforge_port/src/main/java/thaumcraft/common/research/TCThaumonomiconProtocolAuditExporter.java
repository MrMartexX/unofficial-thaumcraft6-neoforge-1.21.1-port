package thaumcraft.common.research;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCThaumonomiconProtocolAuditExporter {
    private TCThaumonomiconProtocolAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.thaumonomiconProtocolAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.thaumonomiconProtocolAuditPath",
                    "research_recipe_catalog/thaumonomicon_protocol_audit.md"
            )).toAbsolutePath().normalize();
            var player = FakePlayerFactory.getMinecraft(event.getServer().overworld());
            TCThaumonomiconProtocolAudit.Report report = TCThaumonomiconProtocolAudit.writeMarkdown(output, player);
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumonomicon protocol audit to {}: checks={}/{} passed, categories={}, entries={}, entry_views={}, bookmarks={}, pages={}.",
                    output,
                    report.passed(),
                    report.passed() + report.failed(),
                    report.categoryCount(),
                    report.entryCount(),
                    report.entryViewsInspected(),
                    report.bookmarksInspected(),
                    report.pagesInspected()
            );
            if (report.failed() > 0) {
                throw new IllegalStateException("Thaumonomicon protocol foundation audit failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumonomicon protocol foundation audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
