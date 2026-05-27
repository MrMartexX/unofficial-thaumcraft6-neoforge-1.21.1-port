package thaumcraft.common.research.theorycraft;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCDataComponents;
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

        report.check("public_api_card_registry_count", registeredCardCount("thaumcraft.api.research.theorycraft.") == 9, "The public/API theorycraft card slice should keep the original 9 card ids.");
        report.check("safe_bridge_card_registry_count", TCTheorycraftManager.cards().size() == 19
                        && hasCard("thaumcraft.common.lib.research.theorycraft.CardMeasure")
                        && hasCard("thaumcraft.common.lib.research.theorycraft.CardConcentrate")
                        && hasCard("thaumcraft.common.lib.research.theorycraft.CardReactions")
                        && hasCard("thaumcraft.common.lib.research.theorycraft.CardSynthesis")
                        && hasCard("thaumcraft.common.lib.research.theorycraft.CardCalibrate")
                        && hasCard("thaumcraft.common.lib.research.theorycraft.CardFocus")
                        && hasCard("thaumcraft.common.lib.research.theorycraft.CardSynergy")
                        && hasCard("thaumcraft.common.lib.research.theorycraft.CardEnchantment")
                        && hasCard("thaumcraft.common.lib.research.theorycraft.CardBeacon")
                        && hasCard("thaumcraft.common.lib.research.theorycraft.CardSpellbinding"),
                "First common card bridge should add dependency-free, aspect-crystal Alchemy, and vanilla XP/aid cards only.");
        report.check("card_analyze_deferred_by_legacy_bug", !new CardAnalyze().initialize(null, new TCResearchTableData()), "Legacy decompiled CardAnalyze initializes from a null category lookup; kept out of random draws until corrected from a stronger source.");
        addResearchAidChecks(report);
        addSafeBridgeCardActivationChecks(report);
        addAlchemyCardActivationChecks(report);

        TCResearchTableSyncPayload syncPayload = new TCResearchTableSyncPayload(BlockPos.ZERO, true, serialized);
        TCResearchTableBlockEntity table = new TCResearchTableBlockEntity(BlockPos.ZERO, TCBlocks.RESEARCH_TABLE.get().defaultBlockState());
        table.applyTheoryDataFromSync(syncPayload);
        report.check("sync_payload_roundtrip", table.getTheoryData() != null && table.getTheoryData().getTotal("BASICS") == 100, "Client cache payload can reconstruct theory data.");

        return report.build();
    }

    private static void addResearchAidChecks(TCResearchTableDiagnosticReport.Builder report) {
        List<String> expectedBookshelfCards = List.of(
                "thaumcraft.api.research.theorycraft.CardBalance",
                "thaumcraft.api.research.theorycraft.CardNotation",
                "thaumcraft.api.research.theorycraft.CardNotation",
                "thaumcraft.api.research.theorycraft.CardStudy",
                "thaumcraft.api.research.theorycraft.CardStudy",
                "thaumcraft.api.research.theorycraft.CardStudy"
        );
        TCTheorycraftAid bookshelf = TCTheorycraftManager.aids().get(TCTheorycraftManager.AID_BOOKSHELF);
        report.check("bookshelf_aid_registry", bookshelf != null && bookshelf.cardKeys().equals(expectedBookshelfCards),
                "Legacy AidBookshelf adds Balance, two Notation cards and three Study cards.");

        TCResearchTableData aidData = new TCResearchTableData();
        aidData.initializeWithFixedInspirationForDiagnostics("Martin", 6, List.of(TCTheorycraftManager.AID_BOOKSHELF));
        report.check("bookshelf_aid_initialize_data", aidData.aidsChosen == 1
                        && aidData.inspirationStart == 6
                        && aidData.inspiration == 5
                        && aidData.aidCards.equals(expectedBookshelfCards),
                "Selected aids should reduce initial inspiration by one each and append their card ids to aidCards.");

        TCTheorycraftAid enchantmentTable = TCTheorycraftManager.aids().get(TCTheorycraftManager.AID_ENCHANTMENT_TABLE);
        TCTheorycraftAid beacon = TCTheorycraftManager.aids().get(TCTheorycraftManager.AID_BEACON);
        report.check("vanilla_aid_registry", TCTheorycraftManager.aids().size() == 3
                        && enchantmentTable != null
                        && enchantmentTable.cardKeys().equals(List.of("thaumcraft.common.lib.research.theorycraft.CardEnchantment"))
                        && beacon != null
                        && beacon.cardKeys().equals(List.of("thaumcraft.common.lib.research.theorycraft.CardBeacon")),
                "The active vanilla aid slice should contain bookshelf, enchanting table and beacon aids.");
    }

    private static void addSafeBridgeCardActivationChecks(TCResearchTableDiagnosticReport.Builder report) {
        TCResearchTableData measureData = new TCResearchTableData();
        boolean measure = new CardMeasure().activate(null, measureData);
        report.check("card_measure_activation", measure
                        && measureData.getTotal("INFUSION") == 15
                        && measureData.bonusDraws == 1,
                "Legacy CardMeasure adds 15 INFUSION and one bonus draw.");

        TCResearchTableData calibrateData = new TCResearchTableData();
        boolean calibrate = new CardCalibrate().activate(null, calibrateData);
        report.check("card_calibrate_activation", calibrate
                        && calibrateData.getTotal("ARTIFICE") == 15
                        && calibrateData.bonusDraws == 1,
                "Legacy CardCalibrate adds 15 ARTIFICE and one bonus draw.");

        TCResearchTableData focusData = new TCResearchTableData();
        boolean focus = new CardFocus().activate(null, focusData);
        report.check("card_focus_activation", focus
                        && focusData.getTotal("AUROMANCY") == 15
                        && focusData.bonusDraws == 1,
                "Legacy CardFocus adds 15 AUROMANCY and one bonus draw.");

        TCResearchTableData synergyData = new TCResearchTableData();
        synergyData.addTotal("ARTIFICE", 5);
        synergyData.addTotal("ALCHEMY", 5);
        synergyData.addTotal("INFUSION", 5);
        CardSynergy synergyCard = new CardSynergy();
        boolean synergy = synergyCard.initialize(null, synergyData) && synergyCard.activate(null, synergyData);
        report.check("card_synergy_activation", synergy
                        && synergyData.getTotal("ARTIFICE") == 0
                        && synergyData.getTotal("ALCHEMY") == 0
                        && synergyData.getTotal("INFUSION") == 0
                        && synergyData.getTotal("GOLEMANCY") == 30
                        && synergyData.penaltyStart == 1,
                "Legacy CardSynergy drains 15 from ARTIFICE/ALCHEMY/INFUSION, adds 30 GOLEMANCY and increments penaltyStart.");

        TCResearchTableData beaconData = new TCResearchTableData();
        CardBeacon beacon = new CardBeacon();
        boolean beaconActivated = beacon.activate(null, beaconData);
        report.check("card_beacon_activation", beaconActivated
                        && beacon.getInspirationCost() == -2
                        && beacon.isAidOnly()
                        && beaconData.bonusDraws == 1
                        && beaconData.penaltyStart == 1,
                "Legacy CardBeacon is aid-only, restores two inspiration through negative cost, adds one bonus draw and increments penaltyStart.");
    }

    private static void addAlchemyCardActivationChecks(TCResearchTableDiagnosticReport.Builder report) {
        CardConcentrate concentrate = new CardConcentrate();
        concentrate.setSeed(1L);
        TCResearchTableData concentrateData = new TCResearchTableData();
        boolean concentrateInitialized = concentrate.initialize(null, concentrateData);
        boolean concentrateActivated = concentrate.activate(null, concentrateData);
        report.check("card_concentrate_activation", concentrateInitialized
                        && concentrateActivated
                        && concentrateData.getTotal("ALCHEMY") == 15
                        && concentrateData.bonusDraws == 1
                        && hasAspectStackRequirement(concentrate.getRequiredItems(), 1),
                "Legacy CardConcentrate requires one aspect crystal, adds 15 ALCHEMY and one bonus draw.");

        CardReactions reactions = new CardReactions();
        reactions.setSeed(2L);
        TCResearchTableData reactionsData = new TCResearchTableData();
        boolean reactionsInitialized = reactions.initialize(null, reactionsData);
        boolean reactionsActivated = reactions.activate(null, reactionsData);
        report.check("card_reactions_activation", reactionsInitialized
                        && reactionsActivated
                        && reactionsData.getTotal("ALCHEMY") == 25
                        && hasAspectStackRequirement(reactions.getRequiredItems(), 2),
                "Legacy CardReactions requires two different aspect crystals and adds 25 ALCHEMY.");

        CardSynthesis synthesis = new CardSynthesis();
        synthesis.setSeed(3L);
        TCResearchTableData synthesisData = new TCResearchTableData();
        boolean synthesisInitialized = synthesis.initialize(null, synthesisData);
        boolean synthesisActivated = synthesis.activate(null, synthesisData);
        report.check("card_synthesis_activation", synthesisInitialized
                        && synthesisActivated
                        && synthesisData.getTotal("ALCHEMY") == 40
                        && hasAspectStackRequirement(synthesis.getRequiredItems(), 2)
                        && synthesis.getRequiredItemsConsumed().equals(List.of(true, true)),
                "Legacy CardSynthesis consumes two component crystals, adds 40 ALCHEMY and creates the compound crystal when a player is present.");
    }

    public static TCResearchTableDiagnosticReport buildPlayerReport(ServerPlayer player) {
        TCResearchTableDiagnosticReport.Builder report = TCResearchTableDiagnosticReport.builder();
        TCTheorycraftManager.bootstrap();

        TCPlayerKnowledge before = TCPlayerKnowledgeStore.get(player);
        ArrayList<ItemStack> beforeInventory = copyMainInventory(player);
        int beforeExperienceLevel = player.experienceLevel;
        int beforeTotalExperience = player.totalExperience;
        float beforeExperienceProgress = player.experienceProgress;
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

            player.getInventory().items.set(0, new ItemStack(Items.PAPER, 3));
            report.check("required_item_check", TCResearchTableActions.hasRequiredItems(player, List.of(new ItemStack(Items.PAPER, 2))), "Card requirement checks should see matching main-inventory stacks.");
            report.check("required_item_consume", TCResearchTableActions.consumeRequiredItems(player, List.of(new ItemStack(Items.PAPER, 2)), List.of(true))
                            && player.getInventory().items.get(0).getCount() == 1,
                    "Consumed card requirements should shrink matching main-inventory stacks.");

            player.experienceLevel = 7;
            player.totalExperience = 0;
            player.experienceProgress = 0.0F;
            TCResearchTableData spellbindingData = new TCResearchTableData();
            CardSpellbinding spellbinding = new CardSpellbinding();
            boolean spellbindingActivated = spellbinding.initialize(player, spellbindingData) && spellbinding.activate(player, spellbindingData);
            report.check("card_spellbinding_xp_activation", spellbindingActivated
                            && player.experienceLevel == 2
                            && spellbindingData.getTotal("AUROMANCY") == 25,
                    "Legacy CardSpellbinding consumes up to five XP levels and adds 5 AUROMANCY per level.");

            player.experienceLevel = 5;
            player.totalExperience = 0;
            player.experienceProgress = 0.0F;
            TCResearchTableData enchantmentData = new TCResearchTableData();
            CardEnchantment enchantment = new CardEnchantment();
            boolean enchantmentActivated = enchantment.activate(player, enchantmentData);
            int infusion = enchantmentData.getTotal("INFUSION");
            int auromancy = enchantmentData.getTotal("AUROMANCY");
            report.check("card_enchantment_xp_activation", enchantmentActivated
                            && player.experienceLevel == 0
                            && infusion >= 15
                            && infusion <= 20
                            && auromancy >= 15
                            && auromancy <= 20,
                    "Legacy CardEnchantment consumes five XP levels and adds 15-20 INFUSION plus 15-20 AUROMANCY.");

            table.setTheoryData(seededCompleteTheory());
            Map<String, Integer> awards = TCResearchTableBlockEntity.calculateTheoryRawAwards(table.getTheoryData());
            table.finishTheory(player);
            TCPlayerKnowledge after = TCPlayerKnowledgeStore.get(player);
            boolean awardsApplied = after.getRaw(TCKnowledgeType.THEORY, "BASICS") >= before.getRaw(TCKnowledgeType.THEORY, "BASICS") + awards.get("BASICS");
            report.check("finish_theory_applies_raw_knowledge", awardsApplied, "finishTheory should add calculated raw THEORY knowledge to player data.");
            report.check("finish_theory_clears_data", table.getTheoryData() == null, "finishTheory should clear table theory data.");
        } finally {
            TCPlayerKnowledgeStore.set(player, before, false);
            restoreMainInventory(player, beforeInventory);
            player.experienceLevel = beforeExperienceLevel;
            player.totalExperience = beforeTotalExperience;
            player.experienceProgress = beforeExperienceProgress;
        }

        return report.build();
    }

    private static int registeredCardCount(String prefix) {
        int count = 0;
        for (String key : TCTheorycraftManager.cards().keySet()) {
            if (key.startsWith(prefix)) {
                count++;
            }
        }
        return count;
    }

    private static boolean hasCard(String key) {
        return TCTheorycraftManager.cards().containsKey(key);
    }

    private static boolean hasAspectStackRequirement(List<ItemStack> stacks, int expectedSize) {
        if (stacks.size() != expectedSize) {
            return false;
        }
        for (ItemStack stack : stacks) {
            if (stack.isEmpty() || stack.get(TCDataComponents.ASPECT_STACK.get()) == null) {
                return false;
            }
        }
        return true;
    }

    private static ArrayList<ItemStack> copyMainInventory(ServerPlayer player) {
        ArrayList<ItemStack> copy = new ArrayList<>();
        for (ItemStack stack : player.getInventory().items) {
            copy.add(stack.copy());
        }
        return copy;
    }

    private static void restoreMainInventory(ServerPlayer player, List<ItemStack> copy) {
        for (int slot = 0; slot < player.getInventory().items.size() && slot < copy.size(); slot++) {
            player.getInventory().items.set(slot, copy.get(slot).copy());
        }
        player.getInventory().setChanged();
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
