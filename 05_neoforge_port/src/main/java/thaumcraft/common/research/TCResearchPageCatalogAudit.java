package thaumcraft.common.research;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.item.crafting.RecipeManager;

final class TCResearchPageCatalogAudit {
    private TCResearchPageCatalogAudit() {
    }

    static Report writeMarkdown(Path output, RecipeManager recipeManager) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = buildReport(recipeManager);
        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            writer.write("# NeoForge Research Recipe/Page Catalog Audit\n\n");
            writer.write("| Check | Result |\n");
            writer.write("|---|---:|\n");
            writer.write("| Structural validation | `" + (report.validation().isValid() ? "OK" : "FAILED") + "` |\n");
            writer.write("| Direct research references | `" + report.validation().directReferenceCount() + "` |\n");
            writer.write("| Total entries including group members | `" + report.validation().totalEntryCount() + "` |\n");
            writer.write("| Research occurrences | `" + report.validation().researchOccurrenceCount() + "` |\n");
            writer.write("| Missing catalog references | `" + report.validation().missingResearchReferences().size() + "` |\n");
            writer.write("| Unresolved group targets | `" + report.validation().unresolvedGroupTargets().size() + "` |\n");
            writer.write("| Cyclic groups | `" + report.validation().cyclicGroups().size() + "` |\n\n");

            writeCounts(writer, "Direct-reference legacy sources", report.sourceCounts());
            writeCounts(writer, "Direct-reference page kinds", report.kindCounts());
            writeCounts(writer, "Direct-reference live availability", report.availabilityCounts());

            writeDetails(writer, "Missing catalog references", report.validation().missingResearchReferences());
            writeDetails(writer, "Unresolved group targets", report.validation().unresolvedGroupTargets());
            writeDetails(writer, "Cyclic groups", report.validation().cyclicGroups());
        }
        return report;
    }

    static Report buildReport(RecipeManager recipeManager) {
        TCResearchPageCatalogValidationReport validation = TCResearchPageCatalogManager.validate();
        EnumMap<TCResearchPageLegacySource, Integer> sourceCounts = new EnumMap<>(TCResearchPageLegacySource.class);
        EnumMap<TCResearchPageKind, Integer> kindCounts = new EnumMap<>(TCResearchPageKind.class);
        EnumMap<TCResearchPageAvailability, Integer> availabilityCounts = new EnumMap<>(TCResearchPageAvailability.class);

        for (TCResearchPageCatalogEntry entry : TCResearchPageCatalogManager.entries()) {
            if (!entry.directReference()) {
                continue;
            }
            sourceCounts.merge(entry.legacySource(), 1, Integer::sum);
            kindCounts.merge(entry.kind(), 1, Integer::sum);
            availabilityCounts.merge(
                    TCResearchPageCatalogManager.availability(entry.id().toString(), recipeManager),
                    1,
                    Integer::sum
            );
        }
        return new Report(validation, sourceCounts, kindCounts, availabilityCounts);
    }

    private static void writeCounts(
            BufferedWriter writer,
            String title,
            Map<? extends Enum<?>, Integer> counts
    ) throws IOException {
        writer.write("## " + title + "\n\n");
        writer.write("| Value | Count |\n");
        writer.write("|---|---:|\n");
        for (Map.Entry<? extends Enum<?>, Integer> entry : counts.entrySet()) {
            writer.write("| `" + entry.getKey().name() + "` | `" + entry.getValue() + "` |\n");
        }
        writer.write("\n");
    }

    private static void writeDetails(BufferedWriter writer, String title, java.util.List<String> details) throws IOException {
        if (details.isEmpty()) {
            return;
        }
        writer.write("## " + title + "\n\n");
        for (String detail : details) {
            writer.write("- `" + detail.replace("`", "\\`") + "`\n");
        }
        writer.write("\n");
    }

    record Report(
            TCResearchPageCatalogValidationReport validation,
            Map<TCResearchPageLegacySource, Integer> sourceCounts,
            Map<TCResearchPageKind, Integer> kindCounts,
            Map<TCResearchPageAvailability, Integer> availabilityCounts
    ) {
        Report {
            sourceCounts = Map.copyOf(sourceCounts);
            kindCounts = Map.copyOf(kindCounts);
            availabilityCounts = Map.copyOf(availabilityCounts);
        }
    }
}
