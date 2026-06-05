package thaumcraft.common.crafting.arcane;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blocks.crafting.TCArcaneWorkbenchBlock;
import thaumcraft.common.items.TCAspectVariantStacks;
import thaumcraft.common.menu.TCArcaneWorkbenchMenu;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.research.TCPlayerKnowledge;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.tiles.crafting.TCArcaneWorkbenchBlockEntity;
import thaumcraft.common.world.aura.AuraHandler;

public final class TCArcaneWorkbenchAudit {
    private static final BlockPos AUDIT_POS = new BlockPos(0, 80, 0);
    private static final ResourceLocation IRON_PLATE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "iron_plate");
    private static final ResourceLocation VIS_RESONATOR =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "vis_resonator");

    private TCArcaneWorkbenchAudit() {
    }

    static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = buildReport(server);
        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            writer.write("# Arcane Workbench Runtime Audit\n\n");
            writer.write("| Check | Status | Detail |\n");
            writer.write("|---|---|---|\n");
            for (Check check : report.checks()) {
                writer.write("| `" + check.name() + "` | `" + (check.passed() ? "PASS" : "FAIL") + "` | "
                        + check.detail().replace("|", "\\|") + " |\n");
            }
            writer.write("\n");
            writer.write("- Passed: `" + report.passed() + "`\n");
            writer.write("- Failed: `" + report.failed() + "`\n");
        }
        return report;
    }

    static Report buildReport(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        ServerPlayer player = FakePlayerFactory.getMinecraft(level);
        TCPlayerKnowledge previousKnowledge = TCPlayerKnowledgeStore.get(player);
        try {
            player.getInventory().clearContent();
            TCPlayerKnowledgeStore.set(player, new TCPlayerKnowledge(), false);
            TCArcaneWorkbenchBlockEntity workbench = new TCArcaneWorkbenchBlockEntity(
                    AUDIT_POS,
                    TCBlocks.ARCANE_WORKBENCH.get().defaultBlockState()
            );
            workbench.setLevel(level);
            level.setBlock(AUDIT_POS, TCBlocks.ARCANE_WORKBENCH.get().defaultBlockState(), 3);
            level.setBlock(AUDIT_POS.above(), Blocks.AIR.defaultBlockState(), 3);

            checks.add(check(
                    "arcane_and_wand_workbench_blocks_are_distinct",
                    TCBlocks.ARCANE_WORKBENCH.get() instanceof TCArcaneWorkbenchBlock
                            && !(TCBlocks.WAND_WORKBENCH.get() instanceof TCArcaneWorkbenchBlock),
                    "arcane=" + TCBlocks.ARCANE_WORKBENCH.get().getClass().getSimpleName()
                            + ", wand=" + TCBlocks.WAND_WORKBENCH.get().getClass().getSimpleName()
            ));
            checks.add(check(
                    "workbench_charger_block_survives_above_arcane_or_wand_workbench",
                    chargerSurvivesAboveArcaneOrWandWorkbench(level),
                    "block=" + TCBlocks.ARCANE_WORKBENCH_CHARGER.get().getClass().getSimpleName()
            ));
            checks.add(check(
                    "empty_workbench_resolves_empty",
                    TCArcaneWorkbenchCrafting.resolve(player, workbench).kind() == TCArcaneWorkbenchCrafting.Kind.EMPTY,
                    "kind=" + TCArcaneWorkbenchCrafting.resolve(player, workbench).kind()
            ));
            checks.add(check(
                    "crystal_slots_accept_only_matching_primal_aspects",
                    crystalSlotsAcceptOnlyMatchingAspects(workbench),
                    "order=" + TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER
            ));
            checks.add(check(
                    "can_spend_vis_simulation_does_not_drain",
                    canSpendVisSimulationDoesNotDrain(level, workbench),
                    "vis=" + AuraHandler.getVis(level, AUDIT_POS)
            ));
            checks.add(check(
                    "workbench_without_charger_uses_current_chunk_vis",
                    workbenchWithoutChargerUsesCurrentChunkVis(level, workbench),
                    "available=" + workbench.availableVis()
            ));
            checks.add(check(
                    "workbench_charger_sums_nine_chunk_vis",
                    workbenchChargerSumsNineChunkVis(level, workbench),
                    "available=" + workbench.availableVis()
            ));
            checks.add(check(
                    "workbench_charger_spends_vis_across_nine_chunks",
                    workbenchChargerSpendsVisAcrossNineChunks(level, workbench),
                    "available=" + workbench.availableVis()
            ));
            checks.add(check(
                    "vis_resonator_missing_research_falls_back_to_empty_vanilla",
                    missingResearchFallsBackToEmpty(player, level, workbench),
                    "kind=" + TCArcaneWorkbenchCrafting.resolve(player, workbench).kind()
            ));
            checks.add(check(
                    "vis_resonator_missing_crystals_blocks_fallback",
                    missingCrystalsBlocksFallback(player, level, workbench),
                    "kind=" + TCArcaneWorkbenchCrafting.resolve(player, workbench).kind()
            ));
            checks.add(check(
                    "vis_resonator_wrong_crystal_aspect_blocks_recipe",
                    wrongCrystalAspectBlocksRecipe(player, level, workbench),
                    "kind=" + TCArcaneWorkbenchCrafting.resolve(player, workbench).kind()
            ));
            checks.add(check(
                    "vis_resonator_missing_vis_blocks_fallback",
                    missingVisBlocksFallback(player, level, workbench),
                    "kind=" + TCArcaneWorkbenchCrafting.resolve(player, workbench).kind()
            ));
            checks.add(check(
                    "missing_vis_ghost_output_is_not_craftable",
                    missingVisGhostOutputIsNotCraftable(player, level, workbench),
                    "slot=" + menuFor(player, workbench).getSlot(TCArcaneWorkbenchMenu.SLOT_RESULT).getItem()
            ));
            checks.add(check(
                    "vis_resonator_resolves_when_requirements_met",
                    visResonatorResolves(player, level, workbench),
                    "output=" + TCArcaneWorkbenchCrafting.resolve(player, workbench).output()
            ));
            checks.add(check(
                    "vis_resonator_craft_consumes_matrix_crystals_and_vis",
                    visResonatorCraftConsumesInputs(player, level, workbench),
                    "vis=" + AuraHandler.getVis(level, AUDIT_POS)
            ));
            checks.add(check(
                    "vis_discount_reduces_arcane_cost",
                    visDiscountReducesArcaneCost(player, level, workbench),
                    "cost=" + TCArcaneWorkbenchCrafting.resolve(player, workbench).vis()
            ));
            checks.add(check(
                    "discounted_arcane_craft_drains_discounted_vis",
                    discountedArcaneCraftDrainsDiscountedVis(player, level, workbench),
                    "vis=" + AuraHandler.getVis(level, AUDIT_POS)
            ));
            checks.add(check(
                    "vanilla_fallback_ironplate_output_and_consumption",
                    vanillaFallbackIronPlateCrafts(player, level, workbench),
                    "vis=" + AuraHandler.getVis(level, AUDIT_POS)
            ));
            checks.add(check(
                    "menu_feedback_reports_arcane_cost_and_aura",
                    menuFeedbackReportsArcaneCost(player, level, workbench),
                    "cost=" + menuFor(player, workbench).visCost()
            ));
            checks.add(check(
                    "menu_feedback_reports_discounted_arcane_cost",
                    menuFeedbackReportsDiscountedArcaneCost(player, level, workbench),
                    "cost=" + menuFor(player, workbench).visCost()
            ));
            checks.add(check(
                    "menu_feedback_marks_missing_vis",
                    menuFeedbackMarksMissingVis(player, level, workbench),
                    "available=" + menuFor(player, workbench).availableVis()
            ));
            checks.add(check(
                    "menu_feedback_marks_missing_crystals",
                    menuFeedbackMarksMissingCrystals(player, level, workbench),
                    "mask=" + menuFor(player, workbench).requiredCrystalMask()
            ));
            checks.add(check(
                    "menu_feedback_keeps_vanilla_fallback_costless",
                    menuFeedbackKeepsVanillaFallbackCostless(player, level, workbench),
                    "kind=" + menuFor(player, workbench).recipeKind()
            ));
        } finally {
            player.getInventory().clearContent();
            level.setBlock(AUDIT_POS.above(), Blocks.AIR.defaultBlockState(), 3);
            level.setBlock(AUDIT_POS, Blocks.AIR.defaultBlockState(), 3);
            TCPlayerKnowledgeStore.set(player, previousKnowledge, false);
        }
        return new Report(checks);
    }

    private static boolean chargerSurvivesAboveArcaneOrWandWorkbench(ServerLevel level) {
        level.setBlock(AUDIT_POS, TCBlocks.ARCANE_WORKBENCH.get().defaultBlockState(), 3);
        boolean aboveArcane = TCBlocks.ARCANE_WORKBENCH_CHARGER.get().defaultBlockState().canSurvive(level, AUDIT_POS.above());
        level.setBlock(AUDIT_POS, TCBlocks.WAND_WORKBENCH.get().defaultBlockState(), 3);
        boolean aboveWand = TCBlocks.ARCANE_WORKBENCH_CHARGER.get().defaultBlockState().canSurvive(level, AUDIT_POS.above());
        level.setBlock(AUDIT_POS, TCBlocks.ARCANE_WORKBENCH.get().defaultBlockState(), 3);
        return aboveArcane && aboveWand;
    }

    private static boolean crystalSlotsAcceptOnlyMatchingAspects(TCArcaneWorkbenchBlockEntity workbench) {
        for (int index = 0; index < TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.size(); index++) {
            String aspectTag = TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.get(index);
            String wrongAspectTag = TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.get(
                    (index + 1) % TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.size()
            );
            int slot = TCArcaneWorkbenchBlockEntity.CRYSTAL_SLOT_START + index;
            if (!workbench.canPlaceItem(slot, crystal(aspectTag))) {
                return false;
            }
            if (workbench.canPlaceItem(slot, crystal(wrongAspectTag))) {
                return false;
            }
        }
        return true;
    }

    private static boolean canSpendVisSimulationDoesNotDrain(ServerLevel level, TCArcaneWorkbenchBlockEntity workbench) {
        clearDiscountGear(level, null, workbench);
        resetWorkbench(workbench);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 100);
        boolean canSpend = workbench.canSpendVis(50);
        return canSpend && (int) AuraHandler.getVis(level, AUDIT_POS) == 100;
    }

    private static boolean workbenchWithoutChargerUsesCurrentChunkVis(
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, null, workbench);
        seedNineChunks(level, 100);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 30);
        return !workbench.hasWorkbenchCharger() && workbench.availableVis() == 30 && !workbench.canSpendVis(50);
    }

    private static boolean workbenchChargerSumsNineChunkVis(
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, null, workbench);
        seedNineChunks(level, 10);
        setCharger(level, true);
        return workbench.hasWorkbenchCharger() && workbench.availableVis() == 90 && workbench.canSpendVis(50);
    }

    private static boolean workbenchChargerSpendsVisAcrossNineChunks(
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, null, workbench);
        seedNineChunks(level, 10);
        setCharger(level, true);
        boolean spent = workbench.spendVis(50);
        return spent && workbench.availableVis() == 40;
    }

    private static boolean missingResearchFallsBackToEmpty(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, false);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 100);
        prepareVisResonator(workbench, true, true);
        TCArcaneWorkbenchCrafting.ResolvedCraft craft = TCArcaneWorkbenchCrafting.resolve(player, workbench);
        return craft.kind() == TCArcaneWorkbenchCrafting.Kind.EMPTY && craft.output().isEmpty();
    }

    private static boolean missingCrystalsBlocksFallback(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, true);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 100);
        prepareVisResonator(workbench, false, false);
        TCArcaneWorkbenchCrafting.ResolvedCraft craft = TCArcaneWorkbenchCrafting.resolve(player, workbench);
        return craft.kind() == TCArcaneWorkbenchCrafting.Kind.ARCANE_BLOCKED
                && craft.output().isEmpty()
                && !craft.hasCrystals();
    }

    private static boolean wrongCrystalAspectBlocksRecipe(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, true);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 100);
        prepareVisResonator(workbench, true, true);
        workbench.setItem(TCArcaneWorkbenchCrafting.crystalSlotForAspect("aqua"), crystal("terra"));
        TCArcaneWorkbenchCrafting.ResolvedCraft craft = TCArcaneWorkbenchCrafting.resolve(player, workbench);
        return craft.kind() == TCArcaneWorkbenchCrafting.Kind.ARCANE_BLOCKED
                && craft.output().isEmpty()
                && !craft.hasCrystals();
    }

    private static boolean missingVisBlocksFallback(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, true);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 10);
        prepareVisResonator(workbench, true, true);
        TCArcaneWorkbenchCrafting.ResolvedCraft craft = TCArcaneWorkbenchCrafting.resolve(player, workbench);
        return craft.kind() == TCArcaneWorkbenchCrafting.Kind.ARCANE_BLOCKED
                && stackId(craft.output()).equals(VIS_RESONATOR)
                && !craft.hasVis();
    }

    private static boolean missingVisGhostOutputIsNotCraftable(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, true);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 10);
        prepareVisResonator(workbench, true, true);
        TCArcaneWorkbenchCrafting.ResolvedCraft craft = TCArcaneWorkbenchCrafting.resolve(player, workbench);
        TCArcaneWorkbenchMenu menu = menuFor(player, workbench);
        ItemStack quickMove = menu.quickMoveStack(player, TCArcaneWorkbenchMenu.SLOT_RESULT);
        return craft.kind() == TCArcaneWorkbenchCrafting.Kind.ARCANE_BLOCKED
                && stackId(craft.output()).equals(VIS_RESONATOR)
                && menu.shouldShowMissingVisGhost()
                && stackId(menu.getSlot(TCArcaneWorkbenchMenu.SLOT_RESULT).getItem()).equals(VIS_RESONATOR)
                && !menu.getSlot(TCArcaneWorkbenchMenu.SLOT_RESULT).mayPickup(player)
                && quickMove.isEmpty()
                && !TCArcaneWorkbenchCrafting.craft(player, workbench, craft);
    }

    private static boolean visResonatorResolves(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, true);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 100);
        prepareVisResonator(workbench, true, true);
        TCArcaneWorkbenchCrafting.ResolvedCraft craft = TCArcaneWorkbenchCrafting.resolve(player, workbench);
        return craft.kind() == TCArcaneWorkbenchCrafting.Kind.ARCANE
                && stackId(craft.output()).equals(VIS_RESONATOR)
                && craft.output().getCount() == 1;
    }

    private static boolean visResonatorCraftConsumesInputs(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, true);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 100);
        prepareVisResonator(workbench, true, true);
        TCArcaneWorkbenchCrafting.ResolvedCraft craft = TCArcaneWorkbenchCrafting.resolve(player, workbench);
        boolean crafted = TCArcaneWorkbenchCrafting.craft(player, workbench, craft);
        return crafted
                && workbench.getItem(0).isEmpty()
                && workbench.getItem(1).isEmpty()
                && workbench.getItem(TCArcaneWorkbenchCrafting.crystalSlotForAspect("aer")).isEmpty()
                && workbench.getItem(TCArcaneWorkbenchCrafting.crystalSlotForAspect("aqua")).isEmpty()
                && (int) AuraHandler.getVis(level, AUDIT_POS) == 50;
    }

    private static boolean visDiscountReducesArcaneCost(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, true);
        setGogglesInHeadSlot(player);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 100);
        prepareVisResonator(workbench, true, true);
        TCArcaneWorkbenchCrafting.ResolvedCraft craft = TCArcaneWorkbenchCrafting.resolve(player, workbench);
        return craft.kind() == TCArcaneWorkbenchCrafting.Kind.ARCANE
                && craft.baseVis() == 50
                && craft.vis() == 47
                && craft.hasVis();
    }

    private static boolean discountedArcaneCraftDrainsDiscountedVis(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, true);
        setGogglesInHeadSlot(player);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 100);
        prepareVisResonator(workbench, true, true);
        TCArcaneWorkbenchCrafting.ResolvedCraft craft = TCArcaneWorkbenchCrafting.resolve(player, workbench);
        boolean crafted = TCArcaneWorkbenchCrafting.craft(player, workbench, craft);
        return crafted && (int) AuraHandler.getVis(level, AUDIT_POS) == 53;
    }

    private static boolean vanillaFallbackIronPlateCrafts(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, false);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 100);
        resetWorkbench(workbench);
        workbench.setItem(0, new ItemStack(Items.IRON_INGOT));
        workbench.setItem(1, new ItemStack(Items.IRON_INGOT));
        workbench.setItem(2, new ItemStack(Items.IRON_INGOT));
        TCArcaneWorkbenchCrafting.ResolvedCraft craft = TCArcaneWorkbenchCrafting.resolve(player, workbench);
        boolean resolved = craft.kind() == TCArcaneWorkbenchCrafting.Kind.VANILLA
                && stackId(craft.output()).equals(IRON_PLATE)
                && craft.output().getCount() == 3;
        boolean crafted = TCArcaneWorkbenchCrafting.craft(player, workbench, craft);
        return resolved
                && crafted
                && workbench.getItem(0).isEmpty()
                && workbench.getItem(1).isEmpty()
                && workbench.getItem(2).isEmpty()
                && (int) AuraHandler.getVis(level, AUDIT_POS) == 100;
    }

    private static boolean menuFeedbackReportsArcaneCost(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, true);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 100);
        prepareVisResonator(workbench, true, true);
        TCArcaneWorkbenchMenu menu = menuFor(player, workbench);
        return menu.visCost() == 50
                && menu.baseVisCost() == 50
                && menu.availableVis() == 100
                && menu.hasArcaneRecipe()
                && !menu.isBlocked()
                && menu.hasVis()
                && menu.hasCrystals()
                && menu.hasResearch()
                && menu.requiredCrystalMask() == visResonatorCrystalMask();
    }

    private static boolean menuFeedbackReportsDiscountedArcaneCost(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, true);
        setGogglesInHeadSlot(player);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 100);
        prepareVisResonator(workbench, true, true);
        TCArcaneWorkbenchMenu menu = menuFor(player, workbench);
        return menu.visCost() == 47
                && menu.baseVisCost() == 50
                && menu.availableVis() == 100
                && menu.hasArcaneRecipe()
                && !menu.isBlocked()
                && menu.hasVis();
    }

    private static boolean menuFeedbackMarksMissingVis(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, true);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 10);
        prepareVisResonator(workbench, true, true);
        TCArcaneWorkbenchMenu menu = menuFor(player, workbench);
        return menu.visCost() == 50
                && menu.availableVis() == 10
                && menu.hasArcaneRecipe()
                && menu.isBlocked()
                && menu.shouldShowMissingVisGhost()
                && stackId(menu.getSlot(TCArcaneWorkbenchMenu.SLOT_RESULT).getItem()).equals(VIS_RESONATOR)
                && !menu.hasVis()
                && menu.hasCrystals();
    }

    private static boolean menuFeedbackMarksMissingCrystals(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, true);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 100);
        prepareVisResonator(workbench, false, false);
        TCArcaneWorkbenchMenu menu = menuFor(player, workbench);
        return menu.visCost() == 50
                && menu.availableVis() == 100
                && menu.hasArcaneRecipe()
                && menu.isBlocked()
                && menu.hasVis()
                && !menu.hasCrystals()
                && menu.requiredCrystalMask() == visResonatorCrystalMask();
    }

    private static boolean menuFeedbackKeepsVanillaFallbackCostless(
            ServerPlayer player,
            ServerLevel level,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        clearDiscountGear(level, player, workbench);
        setResearch(player, false);
        AuraHandler.seedAuraChunk(level, AUDIT_POS, 100);
        resetWorkbench(workbench);
        workbench.setItem(0, new ItemStack(Items.IRON_INGOT));
        workbench.setItem(1, new ItemStack(Items.IRON_INGOT));
        workbench.setItem(2, new ItemStack(Items.IRON_INGOT));
        TCArcaneWorkbenchMenu menu = menuFor(player, workbench);
        return menu.recipeKind() == TCArcaneWorkbenchCrafting.Kind.VANILLA.ordinal()
                && menu.visCost() == 0
                && menu.requiredCrystalMask() == 0
                && !menu.hasArcaneRecipe();
    }

    private static void seedNineChunks(ServerLevel level, int vis) {
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                AuraHandler.seedAuraChunk(level, AUDIT_POS.offset(xx * 16, 0, zz * 16), vis);
            }
        }
    }

    private static void setCharger(ServerLevel level, boolean charger) {
        level.setBlock(AUDIT_POS, TCBlocks.ARCANE_WORKBENCH.get().defaultBlockState(), 3);
        level.setBlock(AUDIT_POS.above(), charger
                ? TCBlocks.ARCANE_WORKBENCH_CHARGER.get().defaultBlockState()
                : Blocks.AIR.defaultBlockState(), 3);
    }

    private static void clearDiscountGear(
            ServerLevel level,
            ServerPlayer player,
            TCArcaneWorkbenchBlockEntity workbench
    ) {
        setCharger(level, false);
        if (player != null) {
            player.getInventory().armor.set(3, ItemStack.EMPTY);
        }
        resetWorkbench(workbench);
    }

    private static void setGogglesInHeadSlot(ServerPlayer player) {
        player.getInventory().armor.set(3, new ItemStack(TCItems.GOGGLES.get()));
    }

    private static void prepareVisResonator(
            TCArcaneWorkbenchBlockEntity workbench,
            boolean aerCrystal,
            boolean aquaCrystal
    ) {
        resetWorkbench(workbench);
        workbench.setItem(0, new ItemStack(TCItems.IRON_PLATE.get()));
        workbench.setItem(1, new ItemStack(Items.QUARTZ));
        if (aerCrystal) {
            workbench.setItem(TCArcaneWorkbenchCrafting.crystalSlotForAspect("aer"), crystal("aer"));
        }
        if (aquaCrystal) {
            workbench.setItem(TCArcaneWorkbenchCrafting.crystalSlotForAspect("aqua"), crystal("aqua"));
        }
    }

    private static void resetWorkbench(TCArcaneWorkbenchBlockEntity workbench) {
        workbench.clearContent();
    }

    private static void setResearch(ServerPlayer player, boolean unlockAuromancyStageTwo) {
        TCPlayerKnowledge knowledge = new TCPlayerKnowledge();
        if (unlockAuromancyStageTwo) {
            knowledge.addResearch("UNLOCKAUROMANCY");
            knowledge.setResearchStage("UNLOCKAUROMANCY", 2);
        }
        TCPlayerKnowledgeStore.set(player, knowledge, false);
    }

    private static TCArcaneWorkbenchMenu menuFor(ServerPlayer player, TCArcaneWorkbenchBlockEntity workbench) {
        TCArcaneWorkbenchMenu menu = new TCArcaneWorkbenchMenu(0, player.getInventory(), workbench);
        menu.broadcastChanges();
        return menu;
    }

    private static int visResonatorCrystalMask() {
        return TCArcaneWorkbenchMenu.crystalMask(TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.indexOf("aer"))
                | TCArcaneWorkbenchMenu.crystalMask(TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.indexOf("aqua"));
    }

    private static ItemStack crystal(String aspectTag) {
        Aspect aspect = Aspect.getAspect(aspectTag);
        return aspect == null ? ItemStack.EMPTY : TCAspectVariantStacks.crystal(aspect);
    }

    private static ResourceLocation stackId(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem());
    }

    private static Check check(String name, boolean passed, String detail) {
        return new Check(name, passed, detail);
    }

    record Check(String name, boolean passed, String detail) {
    }

    record Report(List<Check> checks) {
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
