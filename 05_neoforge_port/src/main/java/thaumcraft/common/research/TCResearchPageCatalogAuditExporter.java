package thaumcraft.common.research;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;

public final class TCResearchPageCatalogAuditExporter {
    private TCResearchPageCatalogAuditExporter() {
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.parseBoolean(System.getProperty("tc.researchPageCatalogAudit", "false"))) {
            return;
        }

        try {
            Path output = Paths.get(System.getProperty(
                    "tc.researchPageCatalogAuditPath",
                    "research_recipe_catalog/thaumcraft_1_21_research_recipe_catalog.md"
            )).toAbsolutePath().normalize();
            TCResearchPageCatalogAudit.Report report = TCResearchPageCatalogAudit.writeMarkdown(
                    output,
                    event.getServer().getRecipeManager()
            );
            Thaumcraft.LOGGER.info(
                    "Wrote Thaumcraft research page catalog audit to {}: direct={}, total={}, occurrences={}, ready={}, deferred={}, legacy_missing={}, structural_valid={}.",
                    output,
                    report.validation().directReferenceCount(),
                    report.validation().totalEntryCount(),
                    report.validation().researchOccurrenceCount(),
                    report.availabilityCounts().getOrDefault(TCResearchPageAvailability.READY, 0),
                    report.availabilityCounts().getOrDefault(TCResearchPageAvailability.DEFERRED, 0),
                    report.availabilityCounts().getOrDefault(TCResearchPageAvailability.LEGACY_MISSING, 0),
                    report.validation().isValid()
            );
            if (!report.validation().isValid()) {
                throw new IllegalStateException("Thaumcraft research page catalog structural validation failed");
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write Thaumcraft research page catalog audit", exception);
        } finally {
            event.getServer().halt(false);
        }
    }
}
