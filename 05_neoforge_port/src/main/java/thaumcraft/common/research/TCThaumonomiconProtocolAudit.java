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
    private static final Set<String> FAKE_CRAFTING_CATALOG_IDS = Set.of(
            "thaumcraft:salismundusfake",
            "thaumcraft:triplemeattreatfake"
    );

    private static final Set<String> ARCANE_DECORATIVE_HINTS = Set.of(
            "banner",
            "pedestal",
            "lamp",
            "rail",
            "pavingstone"
    );

    private static final Set<String> ARCANE_BLOCKENTITY_HINTS = Set.of(
            "alchemicalconstruct",
            "advalchemyconstruct",
            "alembic",
            "bellows",
            "centrifuge",
            "condenser",
            "furnace",
            "infusion",
            "jar",
            "mirror",
            "node",
            "smelter",
            "stabilizer",
            "thaumatorium",
            "workbench"
    );

    private static final Set<String> ARCANE_GAMEPLAY_HINTS = Set.of(
            "advancedcrossbow",
            "arcaneear",
            "arcanespa",
            "automatedcrossbow",
            "crossbow",
            "focus",
            "gauntlet",
            "golem",
            "seal",
            "thaumostatic",
            "turret"
    );

    private static final Set<String> ARCANE_TRANSPORT_HINTS = Set.of(
            "buffer",
            "essentia",
            "pump",
            "tube",
            "transfuser",
            "valve"
    );

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
            writer.write("- Ready crafting catalog entries: `" + report.readyCraftingEntries() + "`\n");
            writer.write("- Fake crafting catalog entries: `" + report.fakeCraftingCatalogEntries().size() + "`\n");
            writer.write("- Deferred crafting catalog entries: `" + report.deferredCraftingCatalogEntries().size() + "`\n");
            writer.write("- Ready arcane catalog entries: `" + report.readyArcaneEntries() + "`\n");
            writer.write("- Deferred arcane catalog entries: `" + report.deferredArcaneCatalogEntries().size() + "`\n");
            writer.write("- Deferred arcane decorative/asset catalog entries: `" + report.deferredArcaneDecorativeCatalogEntries().size() + "`\n");
            writer.write("- Deferred arcane blockentity catalog entries: `" + report.deferredArcaneBlockEntityCatalogEntries().size() + "`\n");
            writer.write("- Deferred arcane gameplay catalog entries: `" + report.deferredArcaneGameplayCatalogEntries().size() + "`\n");
            writer.write("- Deferred arcane transport/essentia catalog entries: `" + report.deferredArcaneTransportCatalogEntries().size() + "`\n");
            writer.write("- Deferred arcane uncategorized catalog entries: `" + report.deferredArcaneUncategorizedCatalogEntries().size() + "`\n");
            writeDeferredList(writer, "Fake crafting catalog ids", report.fakeCraftingCatalogEntries());
            writeDeferredList(writer, "Deferred crafting catalog ids", report.deferredCraftingCatalogEntries());
            writeDeferredList(writer, "Deferred arcane decorative/asset catalog ids", report.deferredArcaneDecorativeCatalogEntries());
            writeDeferredList(writer, "Deferred arcane blockentity catalog ids", report.deferredArcaneBlockEntityCatalogEntries());
            writeDeferredList(writer, "Deferred arcane gameplay catalog ids", report.deferredArcaneGameplayCatalogEntries());
            writeDeferredList(writer, "Deferred arcane transport/essentia catalog ids", report.deferredArcaneTransportCatalogEntries());
            writeDeferredList(writer, "Deferred arcane uncategorized catalog ids", report.deferredArcaneUncategorizedCatalogEntries());
            writeDeferredList(writer, "Deferred arcane catalog ids", report.deferredArcaneCatalogEntries());
        }
        return report;
    }

    private static void writeDeferredList(BufferedWriter writer, String title, List<String> ids) throws IOException {
        if (ids.isEmpty()) {
            return;
        }
        writer.write("\n## " + title + "\n\n");
        for (String id : ids) {
            writer.write("- `" + id + "`\n");
        }
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
        checks.add(check(
                "index_revision_matches_server_state",
                index.revision() == TCThaumonomiconService.buildRevision(player),
                "revision=" + index.revision()
        ));

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
        boolean readyArcanePageViewsHaveSnapshots = true;
        boolean readyPageViewsHaveCorrectSnapshotKind = true;
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
                    if (page.availability() == TCResearchPageAvailability.READY
                            && page.kind() == TCResearchPageKind.ARCANE
                            && page.arcaneRecipe().isEmpty()) {
                        readyArcanePageViewsHaveSnapshots = false;
                    }
                    if ((page.kind() == TCResearchPageKind.CRAFTING && page.arcaneRecipe().isPresent())
                            || (page.kind() == TCResearchPageKind.ARCANE && page.craftingRecipe().isPresent())) {
                        readyPageViewsHaveCorrectSnapshotKind = false;
                    }
                    if (page.availability() != TCResearchPageAvailability.READY
                            && (page.craftingRecipe().isPresent() || page.arcaneRecipe().isPresent())) {
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
                "ready_page_views_have_server_arcane_snapshots",
                readyArcanePageViewsHaveSnapshots,
                "pages=" + pagesInspected
        ));
        checks.add(check(
                "ready_page_views_use_matching_snapshot_kind",
                readyPageViewsHaveCorrectSnapshotKind,
                "pages=" + pagesInspected
        ));
        checks.add(check(
                "non_ready_page_views_have_no_recipe_snapshots",
                deferredPageViewsHaveNoSnapshots,
                "pages=" + pagesInspected
        ));

        int readyCraftingEntries = 0;
        ArrayList<String> fakeCraftingCatalogEntries = new ArrayList<>();
        ArrayList<String> deferredCraftingCatalogEntries = new ArrayList<>();
        boolean readyCatalogSnapshotsValid = true;
        for (TCResearchPageCatalogEntry catalogEntry : TCResearchPageCatalogManager.entries()) {
            if (catalogEntry.kind() != TCResearchPageKind.CRAFTING) {
                continue;
            }
            String catalogId = catalogEntry.id().toString();
            TCResearchPageAvailability availability = TCResearchPageCatalogManager.availability(
                    catalogId,
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
            } else if (FAKE_CRAFTING_CATALOG_IDS.contains(catalogId)) {
                fakeCraftingCatalogEntries.add(catalogId);
                if (snapshot.isPresent()) {
                    readyCatalogSnapshotsValid = false;
                }
            } else {
                deferredCraftingCatalogEntries.add(catalogId);
                if (snapshot.isPresent()) {
                    readyCatalogSnapshotsValid = false;
                }
            }
        }
        checks.add(check(
                "ready_crafting_catalog_entries_have_valid_server_snapshots",
                readyCatalogSnapshotsValid && readyCraftingEntries > 0,
                "ready_crafting_entries=" + readyCraftingEntries
                        + ", fake_crafting_entries=" + fakeCraftingCatalogEntries.size()
                        + ", deferred_crafting_entries=" + deferredCraftingCatalogEntries.size()
        ));
        checks.add(check(
                "fake_crafting_catalog_entries_are_classified",
                fakeCraftingCatalogEntries.size() == FAKE_CRAFTING_CATALOG_IDS.size()
                        && deferredCraftingCatalogEntries.stream().noneMatch(FAKE_CRAFTING_CATALOG_IDS::contains),
                "fake_crafting_entries=" + fakeCraftingCatalogEntries.size()
        ));

        int readyArcaneEntries = 0;
        ArrayList<String> deferredArcaneCatalogEntries = new ArrayList<>();
        ArrayList<String> deferredArcaneDecorativeCatalogEntries = new ArrayList<>();
        ArrayList<String> deferredArcaneBlockEntityCatalogEntries = new ArrayList<>();
        ArrayList<String> deferredArcaneGameplayCatalogEntries = new ArrayList<>();
        ArrayList<String> deferredArcaneTransportCatalogEntries = new ArrayList<>();
        ArrayList<String> deferredArcaneUncategorizedCatalogEntries = new ArrayList<>();
        boolean readyArcaneCatalogSnapshotsValid = true;
        for (TCResearchPageCatalogEntry catalogEntry : TCResearchPageCatalogManager.entries()) {
            if (catalogEntry.kind() != TCResearchPageKind.ARCANE) {
                continue;
            }
            String catalogId = catalogEntry.id().toString();
            TCResearchPageAvailability availability = TCResearchPageCatalogManager.availability(
                    catalogId,
                    player.server.getRecipeManager()
            );
            Optional<TCArcaneRecipePageView> snapshot = TCResearchPageCatalogManager.buildArcanePage(
                    catalogEntry.id(),
                    player.server.getRecipeManager(),
                    player.server.registryAccess()
            );
            if (availability == TCResearchPageAvailability.READY) {
                readyArcaneEntries++;
                readyArcaneCatalogSnapshotsValid &= snapshot.isPresent()
                        && snapshot.get().recipeId().equals(catalogEntry.id())
                        && !snapshot.get().result().isEmpty()
                        && snapshot.get().ingredients().size() <= 9
                        && !snapshot.get().research().isBlank();
            } else {
                deferredArcaneCatalogEntries.add(catalogId);
                switch (classifyDeferredArcaneCatalogEntry(catalogId)) {
                    case "decorative" -> deferredArcaneDecorativeCatalogEntries.add(catalogId);
                    case "blockentity" -> deferredArcaneBlockEntityCatalogEntries.add(catalogId);
                    case "gameplay" -> deferredArcaneGameplayCatalogEntries.add(catalogId);
                    case "transport" -> deferredArcaneTransportCatalogEntries.add(catalogId);
                    default -> deferredArcaneUncategorizedCatalogEntries.add(catalogId);
                }
                if (snapshot.isPresent()) {
                    readyArcaneCatalogSnapshotsValid = false;
                }
            }
        }
        checks.add(check(
                "ready_arcane_catalog_entries_have_valid_server_snapshots",
                readyArcaneCatalogSnapshotsValid && readyArcaneEntries > 0,
                "ready_arcane_entries=" + readyArcaneEntries
                        + ", deferred_arcane_entries=" + deferredArcaneCatalogEntries.size()
        ));
        int classifiedDeferredArcaneEntries = deferredArcaneDecorativeCatalogEntries.size()
                + deferredArcaneBlockEntityCatalogEntries.size()
                + deferredArcaneGameplayCatalogEntries.size()
                + deferredArcaneTransportCatalogEntries.size()
                + deferredArcaneUncategorizedCatalogEntries.size();
        checks.add(check(
                "deferred_arcane_catalog_entries_are_classified",
                classifiedDeferredArcaneEntries == deferredArcaneCatalogEntries.size(),
                "classified=" + classifiedDeferredArcaneEntries
                        + ", deferred_arcane_entries=" + deferredArcaneCatalogEntries.size()
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
        TCThaumonomiconClientCache.accept(index);
        boolean clientCacheStoresRevision = TCThaumonomiconClientCache.revision() == index.revision();
        TCThaumonomiconClientCache.clear();
        checks.add(check(
                "client_cache_stores_authoritative_revision",
                clientCacheStoresRevision,
                "revision=" + index.revision()
        ));
        checks.add(check(
                "index_refresh_invalidates_entry_cache",
                indexInvalidatesEntryCache,
                "sample_present=" + sample.isPresent()
        ));
        TCThaumonomiconClientCache.clear();
        TCThaumonomiconClientCache.accept(new TCThaumonomiconIndexPayload(
                index.categories(),
                index.entries(),
                index.revision(),
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

        Optional<TCThaumonomiconResearchView> staleCandidate = index.entries().stream().findFirst();
        boolean staleActionRejectedWithoutMutation = false;
        if (staleCandidate.isPresent()) {
            int staleRevision = index.revision() == 0 ? 1 : 0;
            String beforeKnowledge = TCPlayerKnowledgeStore.get(player).save().toString();
            TCThaumonomiconNetwork.ActionResult staleResult = TCThaumonomiconNetwork.processAction(
                    player,
                    new TCThaumonomiconActionPayload(
                            TCThaumonomiconActionPayload.START_RESEARCH,
                            staleCandidate.get().key(),
                            staleRevision
                    )
            );
            String afterKnowledge = TCPlayerKnowledgeStore.get(player).save().toString();
            staleActionRejectedWithoutMutation = !staleResult.accepted()
                    && staleResult.refreshIndex()
                    && "stale_revision".equals(staleResult.resultKey())
                    && beforeKnowledge.equals(afterKnowledge);
        }
        checks.add(check(
                "stale_action_revision_rejected_without_mutation",
                staleActionRejectedWithoutMutation,
                "candidate=" + staleCandidate.map(TCThaumonomiconResearchView::key).orElse("none")
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
                pagesInspected,
                readyCraftingEntries,
                fakeCraftingCatalogEntries,
                deferredCraftingCatalogEntries,
                readyArcaneEntries,
                deferredArcaneCatalogEntries,
                deferredArcaneDecorativeCatalogEntries,
                deferredArcaneBlockEntityCatalogEntries,
                deferredArcaneGameplayCatalogEntries,
                deferredArcaneTransportCatalogEntries,
                deferredArcaneUncategorizedCatalogEntries
        );
    }

    private static String classifyDeferredArcaneCatalogEntry(String catalogId) {
        String normalized = catalogId == null ? "" : catalogId.toLowerCase();
        if (containsAny(normalized, ARCANE_TRANSPORT_HINTS)) {
            return "transport";
        }
        if (containsAny(normalized, ARCANE_BLOCKENTITY_HINTS)) {
            return "blockentity";
        }
        if (containsAny(normalized, ARCANE_GAMEPLAY_HINTS)) {
            return "gameplay";
        }
        if (containsAny(normalized, ARCANE_DECORATIVE_HINTS)) {
            return "decorative";
        }
        return "uncategorized";
    }

    private static boolean containsAny(String text, Set<String> hints) {
        for (String hint : hints) {
            if (text.contains(hint)) {
                return true;
            }
        }
        return false;
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
            int pagesInspected,
            int readyCraftingEntries,
            List<String> fakeCraftingCatalogEntries,
            List<String> deferredCraftingCatalogEntries,
            int readyArcaneEntries,
            List<String> deferredArcaneCatalogEntries,
            List<String> deferredArcaneDecorativeCatalogEntries,
            List<String> deferredArcaneBlockEntityCatalogEntries,
            List<String> deferredArcaneGameplayCatalogEntries,
            List<String> deferredArcaneTransportCatalogEntries,
            List<String> deferredArcaneUncategorizedCatalogEntries
    ) {
        Report {
            checks = List.copyOf(checks);
            fakeCraftingCatalogEntries = List.copyOf(fakeCraftingCatalogEntries);
            deferredCraftingCatalogEntries = List.copyOf(deferredCraftingCatalogEntries);
            deferredArcaneCatalogEntries = List.copyOf(deferredArcaneCatalogEntries);
            deferredArcaneDecorativeCatalogEntries = List.copyOf(deferredArcaneDecorativeCatalogEntries);
            deferredArcaneBlockEntityCatalogEntries = List.copyOf(deferredArcaneBlockEntityCatalogEntries);
            deferredArcaneGameplayCatalogEntries = List.copyOf(deferredArcaneGameplayCatalogEntries);
            deferredArcaneTransportCatalogEntries = List.copyOf(deferredArcaneTransportCatalogEntries);
            deferredArcaneUncategorizedCatalogEntries = List.copyOf(deferredArcaneUncategorizedCatalogEntries);
        }

        int passed() {
            return (int) checks.stream().filter(Check::passed).count();
        }

        int failed() {
            return checks.size() - passed();
        }
    }
}
