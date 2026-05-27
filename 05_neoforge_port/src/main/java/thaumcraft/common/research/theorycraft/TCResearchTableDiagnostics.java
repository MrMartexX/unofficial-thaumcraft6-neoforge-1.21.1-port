package thaumcraft.common.research.theorycraft;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.research.TCKnowledgeType;
import thaumcraft.common.research.TCPlayerKnowledge;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;

public final class TCResearchTableDiagnostics {
    private static final String[] LEGACY_DATA_KEYS = {
            "player",
            "inspiration",
            "inspirationStart",
            "placedCards",
            "bonusDraws",
            "aidsChosen",
            "penaltyStart",
            "savedCards",
            "categoriesBlocked",
            "categoryTotals",
            "aidCards",
            "cardChoices"
    };

    private TCResearchTableDiagnostics() {
    }

    public static TCResearchTableDiagnosticReport buildStaticReport() {
        TCResearchTableDiagnosticReport.Builder report = TCResearchTableDiagnosticReport.builder();
        TCTheorycraftManager.bootstrap();

        TCResearchTableData data = seededData();
        CompoundTag serialized = data.serialize();

        for (String key : LEGACY_DATA_KEYS) {
            report.check("legacy_nbt_key_" + key, serialized.contains(key), "ResearchTableData serialize() should preserve key `" + key + "`.");
        }
        report.check("legacy_optional_last_draw_absent", !serialized.contains("lastDraw"), "`lastDraw` is optional in legacy and absent when null.");

        TCResearchTableData copy = new TCResearchTableData();
        copy.deserialize(serialized);
        report.check("roundtrip_player", "Martin".equals(copy.player), "player=" + copy.player);
        report.check("roundtrip_inspiration", copy.inspiration == 3 && copy.inspirationStart == 6, "inspiration=" + copy.inspiration + "/" + copy.inspirationStart);
        report.check("roundtrip_saved_cards", copy.savedCards.size() == 2 && copy.savedCards.get(0) == 11L && copy.savedCards.get(1) == 22L, "savedCards=" + copy.savedCards);
        report.check("roundtrip_blocked_categories", copy.categoriesBlocked.size() == 1 && copy.categoriesBlocked.contains("ELDRITCH"), "categoriesBlocked=" + copy.categoriesBlocked);
        report.check("roundtrip_category_totals", copy.getTotal("BASICS") == 100 && copy.getTotal("AUROMANCY") == 45 && copy.getTotal("ALCHEMY") == 10, "categoryTotals=" + copy.categoryTotals);
        report.check("roundtrip_card_choice", copy.cardChoices.size() == 1 && copy.cardChoices.getFirst().card.getSeed() == 12345L, "cardChoices=" + copy.cardChoices.size());

        copy.addTotal("ALCHEMY", -10);
        report.check("add_total_removes_zero_or_negative", !copy.hasTotal("ALCHEMY"), "Legacy addTotal removes category totals at zero or below.");
        copy.addInspiration(99);
        report.check("add_inspiration_clamps_to_start", copy.inspiration == copy.inspirationStart, "inspiration=" + copy.inspiration + "/" + copy.inspirationStart);

        Map<String, Integer> awards = TCResearchTableBlockEntity.calculateTheoryRawAwards(data);
        report.check("finish_theory_basics_raw", awards.getOrDefault("BASICS", -1) == 32, "100% BASICS should award one THEORY point = 32 raw.");
        report.check("finish_theory_second_category_raw", awards.getOrDefault("AUROMANCY", -1) == 14, "45% AUROMANCY rounds to 14 raw.");
        report.check("finish_theory_penalty_raw", awards.getOrDefault("ALCHEMY", -1) == 2, "10% ALCHEMY after penalty rounds down from 3 to 2 raw.");

        report.check("core_card_registry_count", TCTheorycraftManager.cards().size() == 9, "First core-card slice should register exactly 9 legacy public/API card ids.");
        report.check("card_analyze_deferred_by_legacy_bug", !new CardAnalyze().initialize(null, new TCResearchTableData()), "Legacy decompiled CardAnalyze initializes from a null category lookup; kept out of random draws until corrected from a stronger source.");

        TCResearchTableSyncPayload syncPayload = new TCResearchTableSyncPayload(BlockPos.ZERO, true, serialized);
        TCResearchTableBlockEntity table = new TCResearchTableBlockEntity(BlockPos.ZERO, TCBlocks.RESEARCH_TABLE.get().defaultBlockState());
        table.applyTheoryDataFromSync(syncPayload);
        report.check("sync_payload_roundtrip", table.getTheoryData() != null && table.getTheoryData().getTotal("BASICS") == 100, "Client cache payload can reconstruct theory data.");

        return report.build();
    }

    public static TCResearchTableDiagnosticReport buildPlayerReport(ServerPlayer player) {
        TCResearchTableDiagnosticReport.Builder report = TCResearchTableDiagnosticReport.builder();
        TCTheorycraftManager.bootstrap();

        TCPlayerKnowledge before = TCPlayerKnowledgeStore.get(player);
        try {
            TCResearchTableBlockEntity table = new TCResearchTableBlockEntity(BlockPos.ZERO, TCBlocks.RESEARCH_TABLE.get().defaultBlockState());
            table.setItem(TCResearchTableBlockEntity.SLOT_SCRIBING_TOOLS, new ItemStack(TCItems.SCRIBING_TOOLS.get()));
            table.setItem(TCResearchTableBlockEntity.SLOT_PAPER, new ItemStack(Items.PAPER, 2));

            report.check("has_usable_scribing_tools", table.hasUsableScribingTools(), "Scribing tools are accepted by slot 0 and usable before max damage.");
            report.check("consume_paper", table.consumePaperFromTable() && table.getPaperCount() == 1, "consumePaperFromTable should decrement slot 1 by one.");
            report.check("consume_ink", table.consumeInkFromTable() && table.getScribingTools().getDamageValue() == 1, "consumeInkFromTable should add exactly one damage.");

            TCResearchTableData data = new TCResearchTableData(player);
            data.initialize(player, java.util.List.of());
            report.check("available_inspiration_floor", data.inspirationStart >= 5 && data.inspiration <= data.inspirationStart, "inspiration=" + data.inspiration + "/" + data.inspirationStart);

            data.addTotal("BASICS", 80);
            data.addTotal("AUROMANCY", 25);
            data.drawCards(2, player);
            report.check("draw_cards_non_empty", !data.cardChoices.isEmpty(), "Draw should produce at least one currently ported valid card from seeded totals.");
            report.check("draw_cards_max_two", data.cardChoices.size() <= 2, "Legacy draw count caps this request at two choices; actual=" + data.cardChoices.size());

            table.setTheoryData(seededCompleteTheory());
            Map<String, Integer> awards = TCResearchTableBlockEntity.calculateTheoryRawAwards(table.getTheoryData());
            table.finishTheory(player);
            TCPlayerKnowledge after = TCPlayerKnowledgeStore.get(player);
            boolean awardsApplied = after.getRaw(TCKnowledgeType.THEORY, "BASICS") >= before.getRaw(TCKnowledgeType.THEORY, "BASICS") + awards.get("BASICS");
            report.check("finish_theory_applies_raw_knowledge", awardsApplied, "finishTheory should add calculated raw THEORY knowledge to player data.");
            report.check("finish_theory_clears_data", table.getTheoryData() == null, "finishTheory should clear table theory data.");
        } finally {
            TCPlayerKnowledgeStore.set(player, before, false);
        }

        return report.build();
    }

    public static void writeMarkdown(Path output, TCResearchTableDiagnosticReport report) throws IOException {
        Path parent = output.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("# Research Table Theorycraft Diagnostic\n\n");
        builder.append("| Check | Status | Notes |\n");
        builder.append("|---|---|---|\n");
        for (TCResearchTableDiagnosticReport.Row row : report.rows()) {
            builder.append("| ")
                    .append(escape(row.check()))
                    .append(" | ")
                    .append(row.passed() ? "PASS" : "FAIL")
                    .append(" | ")
                    .append(escape(row.notes()))
                    .append(" |\n");
        }
        builder.append("\nPassed: ").append(report.passedCount()).append("\n");
        builder.append("Failed: ").append(report.failedCount()).append("\n");
        Files.writeString(output, builder.toString(), StandardCharsets.UTF_8);
    }

    private static TCResearchTableData seededData() {
        TCResearchTableData data = seededCompleteTheory();
        data.player = "Martin";
        data.inspiration = 3;
        data.inspirationStart = 6;
        data.bonusDraws = 1;
        data.placedCards = 2;
        data.aidsChosen = 0;
        data.savedCards.add(11L);
        data.savedCards.add(22L);
        data.categoriesBlocked.add("ELDRITCH");
        data.aidCards.add("thaumcraft.api.research.theorycraft.CardStudy");
        CardStudy card = new CardStudy();
        card.setSeed(12345L);
        data.cardChoices.add(new TCResearchTableData.CardChoice("thaumcraft.api.research.theorycraft.CardStudy", card, true, false));
        return data;
    }

    private static TCResearchTableData seededCompleteTheory() {
        TCResearchTableData data = new TCResearchTableData();
        data.player = "Martin";
        data.inspiration = 0;
        data.inspirationStart = 5;
        data.penaltyStart = 1;
        data.addTotal("BASICS", 100);
        data.addTotal("AUROMANCY", 45);
        data.addTotal("ALCHEMY", 10);
        return data;
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("|", "\\|").replace("\n", " ");
    }
}
