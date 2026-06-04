package thaumcraft.common.research;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;

final class TCThaumonomiconProtocolAudit {
    private TCThaumonomiconProtocolAudit() {
    }

    static Report writeMarkdown(Path output, ServerPlayer player) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = buildReport(player);
        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            writer.write("# Thaumonomicon Protocol Foundation Audit\n\n");
            writer.write("| Check | Status | Detail |\n");
            writer.write("|---|---|---|\n");
            for (Check check : report.checks()) {
                writer.write("| `" + check.name() + "` | `" + (check.passed() ? "PASS" : "FAIL") + "` | "
                        + check.detail().replace("|", "\\|") + " |\n");
            }
            writer.write("\n");
            writer.write("- Visible categories in empty-knowledge index: `" + report.categoryCount() + "`\n");
            writer.write("- Visible research entries in empty-knowledge index: `" + report.entryCount() + "`\n");
            writer.write("- Entry views inspected: `" + report.entryViewsInspected() + "`\n");
            writer.write("- Bookmarks inspected: `" + report.bookmarksInspected() + "`\n");
            writer.write("- Pages inspected: `" + report.pagesInspected() + "`\n");
        }
        return report;
    }

    static Report buildReport(ServerPlayer player) {
        TCPlayerKnowledgeStore.set(player, new TCPlayerKnowledge(), false);
        ArrayList<Check> checks = new ArrayList<>();
        TCThaumonomiconIndexPayload index = TCThaumonomiconService.buildIndex(player);
        Set<String> categoryKeys = new HashSet<>();
        for (TCThaumonomiconCategoryView category : index.categories()) {
            categoryKeys.add(category.key());
        }
        Set<String> entryKeys = new HashSet<>();
        for (TCThaumonomiconResearchView entry : index.entries()) {
            entryKeys.add(entry.key());
        }

        checks.add(check("index_has_visible_category", !index.categories().isEmpty(), "count=" + index.categories().size()));
        checks.add(check("index_has_visible_entry", !index.entries().isEmpty(), "count=" + index.entries().size()));

        boolean categoriesServerFiltered = true;
        for (TCResearchCategoryDefinition category : TCResearchManager.categories()) {
            boolean expected = TCResearchManager.isCategoryVisible(TCPlayerKnowledgeStore.get(player), category.key());
            if (categoryKeys.contains(category.key()) != expected) {
                categoriesServerFiltered = false;
                break;
            }
        }
        checks.add(check("category_visibility_server_filtered", categoriesServerFiltered, "visible=" + categoryKeys.size()));

        boolean entriesServerFiltered = true;
        boolean unlockableServerOwned = true;
        boolean flagsServerOwned = true;
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        for (TCResearchEntryDefinition entry : TCResearchManager.entries()) {
            if (entryKeys.contains(entry.key()) != TCResearchManager.isResearchVisible(player, entry.key())) {
                entriesServerFiltered = false;
                break;
            }
        }
        for (TCThaumonomiconResearchView entry : index.entries()) {
            if (entry.unlockable() != TCResearchManager.canUnlockResearch(player, entry.key())) {
                unlockableServerOwned = false;
            }
            Set<TCResearchFlag> expectedFlags = knowledge.researchFlags().getOrDefault(entry.key(), Set.of());
            if (!Set.copyOf(entry.flags()).equals(expectedFlags)) {
                flagsServerOwned = false;
            }
        }
        checks.add(check("entry_visibility_server_filtered", entriesServerFiltered, "visible=" + entryKeys.size()));
        checks.add(check("unlockable_state_server_owned", unlockableServerOwned, "visible=" + entryKeys.size()));
        checks.add(check("research_flags_server_owned", flagsServerOwned, "visible=" + entryKeys.size()));

        int entryViewsInspected = 0;
        int bookmarksInspected = 0;
        int pagesInspected = 0;
        boolean noLegacyMissingPages = true;
        boolean readyPageViewsHaveSnapshots = true;
        boolean deferredPageViewsHaveNoSnapshots = true;
        Optional<TCThaumonomiconEntryView> sample = Optional.empty();
        for (TCThaumonomiconResearchView entry : index.entries()) {
            Optional<TCThaumonomiconEntryView> view = TCThaumonomiconService.buildEntry(player, entry.key());
            if (view.isEmpty()) {
                continue;
            }
            entryViewsInspected++;
            if (sample.isEmpty()) {
                sample = view;
            }
            for (TCResearchPageBookmark bookmark : view.get().bookmarks()) {
                bookmarksInspected++;
                for (TCResearchPageView page : bookmark.pages()) {
                    pagesInspected++;
                    if (page.availability() == TCResearchPageAvailability.LEGACY_MISSING) {
                        noLegacyMissingPages = false;
                    }
                    if (page.availability() == TCResearchPageAvailability.READY
                            && page.kind() == TCResearchPageKind.CRAFTING
                            && page.craftingRecipe().isEmpty()) {
                        readyPageViewsHaveSnapshots = false;
                    }
                    if (page.availability() != TCResearchPageAvailability.READY
                            && page.craftingRecipe().isPresent()) {
                        deferredPageViewsHaveNoSnapshots = false;
                    }
                }
            }
        }
        checks.add(check("visible_entry_view_available", sample.isPresent(), "inspected=" + entryViewsInspected));
        checks.add(check("legacy_missing_pages_filtered", noLegacyMissingPages, "pages=" + pagesInspected));
        checks.add(check(
                "ready_page_views_have_server_crafting_snapshots",
                readyPageViewsHaveSnapshots,
                "pages=" + pagesInspected
        ));
        checks.add(check(
                "non_ready_page_views_have_no_crafting_snapshots",
                deferredPageViewsHaveNoSnapshots,
                "pages=" + pagesInspected
        ));

        int readyCraftingEntries = 0;
        boolean readyCatalogSnapshotsValid = true;
        for (TCResearchPageCatalogEntry catalogEntry : TCResearchPageCatalogManager.entries()) {
            if (catalogEntry.kind() != TCResearchPageKind.CRAFTING) {
                continue;
            }
            TCResearchPageAvailability availability = TCResearchPageCatalogManager.availability(
                    catalogEntry.id().toString(),
                    player.server.getRecipeManager()
            );
            Optional<TCCraftingRecipePageView> snapshot = TCResearchPageCatalogManager.buildCraftingPage(
                    catalogEntry.id(),
                    player.server.getRecipeManager(),
                    player.server.registryAccess()
            );
            if (availability == TCResearchPageAvailability.READY) {
                readyCraftingEntries++;
                readyCatalogSnapshotsValid &= snapshot.isPresent()
                        && snapshot.get().recipeId().equals(catalogEntry.id())
                        && !snapshot.get().result().isEmpty()
                        && snapshot.get().ingredients().size() <= 9;
            } else if (snapshot.isPresent()) {
                readyCatalogSnapshotsValid = false;
            }
        }
        checks.add(check(
                "ready_crafting_catalog_entries_have_valid_server_snapshots",
                readyCatalogSnapshotsValid && readyCraftingEntries > 0,
                "ready_crafting_entries=" + readyCraftingEntries
        ));

        boolean rejectedUnknown = TCThaumonomiconService.buildEntry(player, "AUDIT_MISSING_RESEARCH").isEmpty();
        checks.add(check("unknown_entry_rejected", rejectedUnknown, "key=AUDIT_MISSING_RESEARCH"));

        boolean canonicalCatalogLookup = TCResearchPageCatalogManager.get("thaumcraft:AdvAlchemyConstruct").isPresent()
                && TCResearchPageCatalogManager.get("thaumcraft:advalchemyconstruct").isPresent();
        checks.add(check("legacy_resource_location_canonicalization", canonicalCatalogLookup, "uppercase/lowercase lookup"));

        boolean cacheRoundTrip = false;
        boolean indexInvalidatesEntryCache = false;
        if (sample.isPresent()) {
            TCThaumonomiconClientCache.accept(index);
            TCThaumonomiconClientCache.accept(new TCThaumonomiconEntryPayload(
                    true,
                    "audit",
                    sample.get().research().key(),
                    sample
            ));
            cacheRoundTrip = TCThaumonomiconClientCache.entry(sample.get().research().key()).isPresent()
                    && TCThaumonomiconClientCache.index().entries().size() == index.entries().size();
            TCThaumonomiconClientCache.accept(index);
            indexInvalidatesEntryCache = TCThaumonomiconClientCache.entry(sample.get().research().key()).isEmpty();
            TCThaumonomiconClientCache.clear();
        }
        checks.add(check("client_cache_accepts_authoritative_views", cacheRoundTrip, "sample_present=" + sample.isPresent()));
        checks.add(check(
                "index_refresh_invalidates_entry_cache",
                indexInvalidatesEntryCache,
                "sample_present=" + sample.isPresent()
        ));
        TCThaumonomiconClientCache.clear();
        TCThaumonomiconClientCache.accept(new TCThaumonomiconIndexPayload(
                index.categories(),
                index.entries(),
                true
        ));
        boolean explicitOpenIntent = TCThaumonomiconClientCache.pollOpenRequested()
                && !TCThaumonomiconClientCache.pollOpenRequested();
        TCThaumonomiconClientCache.accept(index);
        boolean refreshDoesNotOpen = !TCThaumonomiconClientCache.pollOpenRequested();
        TCThaumonomiconClientCache.clear();
        checks.add(check(
                "explicit_open_intent_is_separate_from_refresh",
                explicitOpenIntent && refreshDoesNotOpen,
                "open_once=" + explicitOpenIntent + ", refresh_open=" + !refreshDoesNotOpen
        ));

        Optional<TCThaumonomiconResearchView> startCandidate = index.entries().stream()
                .filter(entry -> entry.status() == TCResearchStatus.UNKNOWN)
                .filter(entry -> prepareUnlockableCandidate(player, entry.key(), new HashSet<>()))
                .findFirst();
        boolean startActionSemantics = false;
        boolean acknowledgeActionSemantics = false;
        if (startCandidate.isPresent()) {
            String key = startCandidate.get().key();
            boolean started = TCResearchManager.startResearchFromBrowser(player, key);
            TCPlayerKnowledge startedKnowledge = TCPlayerKnowledgeStore.get(player);
            TCResearchStatus status = TCResearchManager.getResearchStatus(startedKnowledge, key);
            boolean popupMatches = status == TCResearchStatus.COMPLETE
                    ? startedKnowledge.hasResearchFlag(key, TCResearchFlag.POPUP)
                    : !startedKnowledge.hasResearchFlag(key, TCResearchFlag.POPUP);
            startActionSemantics = started
                    && startedKnowledge.isResearchKnown(key)
                    && !startedKnowledge.hasResearchFlag(key, TCResearchFlag.RESEARCH)
                    && popupMatches;

            startedKnowledge.setResearchFlag(key, TCResearchFlag.RESEARCH);
            startedKnowledge.setResearchFlag(key, TCResearchFlag.PAGE);
            startedKnowledge.setResearchFlag(key, TCResearchFlag.POPUP);
            TCPlayerKnowledgeStore.set(player, startedKnowledge, false);
            boolean acknowledged = TCResearchManager.acknowledgeResearchEntry(player, key);
            TCPlayerKnowledge acknowledgedKnowledge = TCPlayerKnowledgeStore.get(player);
            acknowledgeActionSemantics = acknowledged
                    && !acknowledgedKnowledge.hasResearchFlag(key, TCResearchFlag.RESEARCH)
                    && !acknowledgedKnowledge.hasResearchFlag(key, TCResearchFlag.PAGE)
                    && acknowledgedKnowledge.hasResearchFlag(key, TCResearchFlag.POPUP);
        }
        checks.add(check(
                "legacy_start_action_semantics",
                startActionSemantics,
                "candidate=" + startCandidate.map(TCThaumonomiconResearchView::key).orElse("none")
        ));
        checks.add(check(
                "legacy_acknowledge_action_semantics",
                acknowledgeActionSemantics,
                "candidate=" + startCandidate.map(TCThaumonomiconResearchView::key).orElse("none")
        ));

        Optional<TCResearchEntryDefinition> finalStageCandidate = TCResearchManager.entries().stream()
                .filter(entry -> entry.stages().size() > 1)
                .filter(entry -> isRequirementFree(entry.stages().getLast()))
                .filter(entry -> prepareUnlockableCandidate(player, entry.key(), new HashSet<>()))
                .findFirst();
        boolean finalStageAutoProgression = false;
        if (finalStageCandidate.isPresent()) {
            TCResearchEntryDefinition entry = finalStageCandidate.get();
            TCPlayerKnowledge finalStageKnowledge = TCPlayerKnowledgeStore.get(player);
            finalStageKnowledge.addResearch(entry.key());
            finalStageKnowledge.setResearchStage(entry.key(), entry.stages().size());
            finalStageKnowledge.setResearchFlag(entry.key(), TCResearchFlag.RESEARCH);
            finalStageKnowledge.setResearchFlag(entry.key(), TCResearchFlag.PAGE);
            finalStageKnowledge.setResearchFlag(entry.key(), TCResearchFlag.POPUP);
            TCPlayerKnowledgeStore.set(player, finalStageKnowledge, false);

            boolean acknowledged = TCResearchManager.acknowledgeResearchEntry(player, entry.key());
            TCPlayerKnowledge completedKnowledge = TCPlayerKnowledgeStore.get(player);
            finalStageAutoProgression = acknowledged
                    && completedKnowledge.getResearchStage(entry.key()) == entry.stages().size() + 1
                    && TCResearchManager.isResearchComplete(completedKnowledge, entry.key())
                    && completedKnowledge.hasResearchFlag(entry.key(), TCResearchFlag.RESEARCH)
                    && !completedKnowledge.hasResearchFlag(entry.key(), TCResearchFlag.PAGE)
                    && completedKnowledge.hasResearchFlag(entry.key(), TCResearchFlag.POPUP);
        }
        checks.add(check(
                "legacy_known_entry_final_stage_auto_progression",
                finalStageAutoProgression,
                "candidate=" + finalStageCandidate.map(TCResearchEntryDefinition::key).orElse("none")
        ));

        return new Report(
                checks,
                index.categories().size(),
                index.entries().size(),
                entryViewsInspected,
                bookmarksInspected,
                pagesInspected
        );
    }

    private static boolean prepareUnlockableCandidate(ServerPlayer player, String key, Set<String> visiting) {
        String researchKey = TCPlayerKnowledge.baseResearchKey(key);
        if (!visiting.add(researchKey)) {
            return false;
        }
        TCResearchManager.getEntry(researchKey).ifPresent(entry -> {
            for (String parent : entry.parents()) {
                satisfyReference(player, parent, visiting);
            }
        });
        visiting.remove(researchKey);
        return TCResearchManager.canUnlockResearch(player, researchKey);
    }

    private static boolean isRequirementFree(TCResearchStageDefinition stage) {
        return stage.requiredResearch().isEmpty()
                && stage.requiredCraft().isEmpty()
                && stage.requiredItem().isEmpty()
                && stage.requiredKnowledge().isEmpty();
    }

    private static void satisfyReference(ServerPlayer player, String rawReference, Set<String> visiting) {
        String reference = rawReference == null ? "" : rawReference.trim();
        while (reference.startsWith("~")) {
            reference = reference.substring(1);
        }
        if (reference.isBlank()) {
            return;
        }
        if (reference.contains("&&")) {
            for (String part : reference.split("&&")) {
                satisfyReference(player, part, visiting);
            }
            return;
        }
        if (reference.contains("||")) {
            satisfyReference(player, reference.split("\\|\\|")[0], visiting);
            return;
        }

        String key = TCPlayerKnowledge.baseResearchKey(reference);
        if (!prepareUnlockableCandidate(player, key, visiting) && TCResearchManager.getEntry(key).isPresent()) {
            return;
        }
        TCResearchManager.completeResearch(player, key, false);
        int stageSeparator = reference.indexOf('@');
        if (stageSeparator >= 0) {
            int requiredStage = Integer.parseInt(reference.substring(stageSeparator + 1));
            TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
            knowledge.setResearchStage(key, requiredStage);
            TCPlayerKnowledgeStore.set(player, knowledge, false);
        }
    }

    private static Check check(String name, boolean passed, String detail) {
        return new Check(name, passed, detail);
    }

    record Check(String name, boolean passed, String detail) {
    }

    record Report(
            List<Check> checks,
            int categoryCount,
            int entryCount,
            int entryViewsInspected,
            int bookmarksInspected,
            int pagesInspected
    ) {
        Report {
            checks = List.copyOf(checks);
        }

        int passed() {
            return (int) checks.stream().filter(Check::passed).count();
        }

        int failed() {
            return checks.size() - passed();
        }
    }
}
