package thaumcraft.common.research;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import thaumcraft.Thaumcraft;
import thaumcraft.common.crafting.arcane.TCArcaneRecipe;
import thaumcraft.common.crafting.arcane.TCShapedArcaneRecipe;
import thaumcraft.common.crafting.arcane.TCShapelessArcaneRecipe;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCRecipes;

final class TCArcaneRecipeAudit {
    private static final ResourceLocation THAUMOMETER =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "thaumometer");
    private static final ResourceLocation VIS_RESONATOR =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "vis_resonator");
    private static final ResourceLocation WORKBENCH_CHARGER =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "workbenchcharger");
    private static final ResourceLocation GOGGLES =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "goggles");
    private static final ResourceLocation WAND_WORKBENCH =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "wand_workbench");
    private static final ResourceLocation CASTER_BASIC =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "caster_basic");
    private static final ResourceLocation ENCHANTED_FABRIC =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "enchantedfabric");
    private static final ResourceLocation MIRROR_GLASS =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "mirrorglass");
    private static final ResourceLocation ARCANE_WORKBENCH_CHARGER =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "arcane_workbench_charger");
    private static final ResourceLocation FABRIC =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "fabric");
    private static final ResourceLocation MIRRORED_GLASS =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "mirrored_glass");
    private static final ResourceLocation THAUMOMETER_BRIDGE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_bridge/thaumometer");
    private static final ResourceLocation VIS_RESONATOR_BRIDGE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_bridge/vis_resonator");
    private static final ResourceLocation WAND_WORKBENCH_BRIDGE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_bridge/wand_workbench");
    private static final ResourceLocation CASTER_BASIC_BRIDGE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_bridge/caster_basic");
    private static final ResourceLocation MIRRORED_GLASS_BRIDGE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_bridge/mirrored_glass");

    private TCArcaneRecipeAudit() {
    }

    static Report writeMarkdown(Path output, RecipeManager recipeManager, HolderLookup.Provider registries)
            throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = buildReport(recipeManager, registries);
        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            writer.write("# Arcane Recipe Runtime Audit\n\n");
            writer.write("| Check | Status | Detail |\n");
            writer.write("|---|---|---|\n");
            for (Check check : report.checks()) {
                writer.write("| `" + check.name() + "` | `" + (check.passed() ? "PASS" : "FAIL") + "` | "
                        + check.detail().replace("|", "\\|") + " |\n");
            }
            writer.write("\n");
            writer.write("- Loaded arcane recipes: `" + report.arcaneRecipeCount() + "`\n");
        }
        return report;
    }

    static Report buildReport(RecipeManager recipeManager, HolderLookup.Provider registries) {
        ArrayList<Check> checks = new ArrayList<>();
        int arcaneRecipeCount = recipeManager.getAllRecipesFor(TCRecipes.ARCANE_TYPE.get()).size();
        checks.add(check("arcane_recipe_type_has_loaded_recipes", arcaneRecipeCount >= 8, "count=" + arcaneRecipeCount));
        checks.add(check(
                "thaumometer_wrong_vanilla_bridge_removed",
                recipeManager.byKey(THAUMOMETER_BRIDGE).isEmpty(),
                THAUMOMETER_BRIDGE.toString()
        ));
        checks.add(check(
                "vis_resonator_wrong_vanilla_bridge_removed",
                recipeManager.byKey(VIS_RESONATOR_BRIDGE).isEmpty(),
                VIS_RESONATOR_BRIDGE.toString()
        ));
        checks.add(check(
                "wand_workbench_wrong_vanilla_bridge_removed",
                recipeManager.byKey(WAND_WORKBENCH_BRIDGE).isEmpty(),
                WAND_WORKBENCH_BRIDGE.toString()
        ));
        checks.add(check(
                "caster_basic_wrong_vanilla_bridge_removed",
                recipeManager.byKey(CASTER_BASIC_BRIDGE).isEmpty(),
                CASTER_BASIC_BRIDGE.toString()
        ));
        checks.add(check(
                "mirrored_glass_wrong_vanilla_bridge_removed",
                recipeManager.byKey(MIRRORED_GLASS_BRIDGE).isEmpty(),
                MIRRORED_GLASS_BRIDGE.toString()
        ));

        Optional<TCArcaneRecipe> thaumometer = recipeManager.byKey(THAUMOMETER)
                .filter(holder -> holder.value() instanceof TCArcaneRecipe)
                .map(holder -> (TCArcaneRecipe) holder.value());
        checks.add(check("thaumometer_arcane_recipe_loaded", thaumometer.isPresent(), THAUMOMETER.toString()));
        checks.add(check(
                "thaumometer_is_not_vanilla_crafting_recipe",
                recipeManager.byKey(THAUMOMETER)
                        .filter(holder -> holder.value() instanceof CraftingRecipe)
                        .isEmpty(),
                THAUMOMETER.toString()
        ));

        if (thaumometer.isPresent()) {
            TCArcaneRecipe recipe = thaumometer.get();
            checks.add(check(
                    "thaumometer_custom_type",
                    recipe.getType() == TCRecipes.ARCANE_TYPE.get(),
                    recipe.getType().toString()
            ));
            checks.add(check(
                    "thaumometer_research_and_vis",
                    "FIRSTSTEPS@2".equals(recipe.getResearch()) && recipe.getVis() == 20,
                    "research=" + recipe.getResearch() + ", vis=" + recipe.getVis()
            ));
            checks.add(check(
                    "thaumometer_ordered_crystal_costs",
                    crystalSummary(recipe).equals(List.of(
                            "aer:1",
                            "terra:1",
                            "aqua:1",
                            "ignis:1",
                            "ordo:1",
                            "perditio:1"
                    )),
                    crystalSummary(recipe).toString()
            ));
            checks.add(check(
                    "thaumometer_result",
                    THAUMOMETER.equals(BuiltInRegistries.ITEM.getKey(recipe.getResultItem(registries).getItem()))
                            && recipe.getResultItem(registries).getCount() == 1,
                    recipe.getResultItem(registries).toString()
            ));
            checks.add(check(
                    "thaumometer_shaped_pattern",
                    matchesThaumometerPattern(recipe),
                    "width=" + recipe.width() + ", height=" + recipe.height()
            ));
        }

        TCResearchPageAvailability thaumometerAvailability = TCResearchPageCatalogManager.availability(
                THAUMOMETER.toString(),
                recipeManager
        );
        checks.add(check(
                "thaumometer_catalog_arcane_snapshot_ready",
                thaumometerAvailability == TCResearchPageAvailability.READY
                        && TCResearchPageCatalogManager.buildArcanePage(THAUMOMETER, recipeManager, registries).isPresent(),
                "availability=" + thaumometerAvailability
        ));

        Optional<TCArcaneRecipe> visResonator = recipeManager.byKey(VIS_RESONATOR)
                .filter(holder -> holder.value() instanceof TCArcaneRecipe)
                .map(holder -> (TCArcaneRecipe) holder.value());
        checks.add(check("vis_resonator_arcane_recipe_loaded", visResonator.isPresent(), VIS_RESONATOR.toString()));
        checks.add(check(
                "vis_resonator_is_not_vanilla_crafting_recipe",
                recipeManager.byKey(VIS_RESONATOR)
                        .filter(holder -> holder.value() instanceof CraftingRecipe)
                        .isEmpty(),
                VIS_RESONATOR.toString()
        ));
        if (visResonator.isPresent()) {
            TCArcaneRecipe recipe = visResonator.get();
            checks.add(check(
                    "vis_resonator_research_and_vis",
                    "UNLOCKAUROMANCY@2".equals(recipe.getResearch()) && recipe.getVis() == 50,
                    "research=" + recipe.getResearch() + ", vis=" + recipe.getVis()
            ));
            checks.add(check(
                    "vis_resonator_ordered_crystal_costs",
                    crystalSummary(recipe).equals(List.of("aer:1", "aqua:1")),
                    crystalSummary(recipe).toString()
            ));
            checks.add(check(
                    "vis_resonator_result",
                    VIS_RESONATOR.equals(BuiltInRegistries.ITEM.getKey(recipe.getResultItem(registries).getItem()))
                            && recipe.getResultItem(registries).getCount() == 1,
                    recipe.getResultItem(registries).toString()
            ));
            checks.add(check(
                    "vis_resonator_shapeless_plate_iron_quartz",
                    matchesVisResonatorIngredients(recipe),
                    "ingredients=" + recipe.getIngredients().size()
            ));
        }

        TCResearchPageAvailability visResonatorAvailability = TCResearchPageCatalogManager.availability(
                VIS_RESONATOR.toString(),
                recipeManager
        );
        checks.add(check(
                "vis_resonator_catalog_arcane_snapshot_ready",
                visResonatorAvailability == TCResearchPageAvailability.READY
                        && TCResearchPageCatalogManager.buildArcanePage(VIS_RESONATOR, recipeManager, registries).isPresent(),
                "availability=" + visResonatorAvailability
        ));

        Optional<TCArcaneRecipe> workbenchCharger = recipeManager.byKey(WORKBENCH_CHARGER)
                .filter(holder -> holder.value() instanceof TCArcaneRecipe)
                .map(holder -> (TCArcaneRecipe) holder.value());
        checks.add(check("workbenchcharger_arcane_recipe_loaded", workbenchCharger.isPresent(), WORKBENCH_CHARGER.toString()));
        checks.add(check(
                "workbenchcharger_is_not_vanilla_crafting_recipe",
                recipeManager.byKey(WORKBENCH_CHARGER)
                        .filter(holder -> holder.value() instanceof CraftingRecipe)
                        .isEmpty(),
                WORKBENCH_CHARGER.toString()
        ));
        if (workbenchCharger.isPresent()) {
            TCArcaneRecipe recipe = workbenchCharger.get();
            checks.add(check(
                    "workbenchcharger_research_and_vis",
                    "WORKBENCHCHARGER".equals(recipe.getResearch()) && recipe.getVis() == 200,
                    "research=" + recipe.getResearch() + ", vis=" + recipe.getVis()
            ));
            checks.add(check(
                    "workbenchcharger_ordered_crystal_costs",
                    crystalSummary(recipe).equals(List.of("aer:2", "ordo:2")),
                    crystalSummary(recipe).toString()
            ));
            checks.add(check(
                    "workbenchcharger_result",
                    ARCANE_WORKBENCH_CHARGER.equals(BuiltInRegistries.ITEM.getKey(recipe.getResultItem(registries).getItem()))
                            && recipe.getResultItem(registries).getCount() == 1,
                    recipe.getResultItem(registries).toString()
            ));
            checks.add(check(
                    "workbenchcharger_shaped_pattern",
                    matchesWorkbenchChargerPattern(recipe),
                    "width=" + recipe.width() + ", height=" + recipe.height()
            ));
        }

        TCResearchPageAvailability workbenchChargerAvailability = TCResearchPageCatalogManager.availability(
                WORKBENCH_CHARGER.toString(),
                recipeManager
        );
        checks.add(check(
                "workbenchcharger_catalog_arcane_snapshot_ready",
                workbenchChargerAvailability == TCResearchPageAvailability.READY
                        && TCResearchPageCatalogManager.buildArcanePage(WORKBENCH_CHARGER, recipeManager, registries).isPresent(),
                "availability=" + workbenchChargerAvailability
        ));

        Optional<TCArcaneRecipe> wandWorkbench = recipeManager.byKey(WAND_WORKBENCH)
                .filter(holder -> holder.value() instanceof TCArcaneRecipe)
                .map(holder -> (TCArcaneRecipe) holder.value());
        checks.add(check("wand_workbench_arcane_recipe_loaded", wandWorkbench.isPresent(), WAND_WORKBENCH.toString()));
        checks.add(check(
                "wand_workbench_is_not_vanilla_crafting_recipe",
                recipeManager.byKey(WAND_WORKBENCH)
                        .filter(holder -> holder.value() instanceof CraftingRecipe)
                        .isEmpty(),
                WAND_WORKBENCH.toString()
        ));
        if (wandWorkbench.isPresent()) {
            TCArcaneRecipe recipe = wandWorkbench.get();
            checks.add(check(
                    "wand_workbench_research_and_vis",
                    "BASEAUROMANCY@2".equals(recipe.getResearch()) && recipe.getVis() == 100,
                    "research=" + recipe.getResearch() + ", vis=" + recipe.getVis()
            ));
            checks.add(check(
                    "wand_workbench_ordered_crystal_costs",
                    crystalSummary(recipe).equals(List.of("terra:1", "aqua:1")),
                    crystalSummary(recipe).toString()
            ));
            checks.add(check(
                    "wand_workbench_result",
                    WAND_WORKBENCH.equals(BuiltInRegistries.ITEM.getKey(recipe.getResultItem(registries).getItem()))
                            && recipe.getResultItem(registries).getCount() == 1,
                    recipe.getResultItem(registries).toString()
            ));
            checks.add(check(
                    "wand_workbench_shaped_pattern",
                    matchesWandWorkbenchPattern(recipe),
                    wandWorkbenchPatternDetail(recipe)
            ));
        }

        TCResearchPageAvailability wandWorkbenchAvailability = TCResearchPageCatalogManager.availability(
                WAND_WORKBENCH.toString(),
                recipeManager
        );
        checks.add(check(
                "wand_workbench_catalog_arcane_snapshot_ready",
                wandWorkbenchAvailability == TCResearchPageAvailability.READY
                        && TCResearchPageCatalogManager.buildArcanePage(WAND_WORKBENCH, recipeManager, registries).isPresent(),
                "availability=" + wandWorkbenchAvailability
        ));

        Optional<TCArcaneRecipe> casterBasic = recipeManager.byKey(CASTER_BASIC)
                .filter(holder -> holder.value() instanceof TCArcaneRecipe)
                .map(holder -> (TCArcaneRecipe) holder.value());
        checks.add(check("caster_basic_arcane_recipe_loaded", casterBasic.isPresent(), CASTER_BASIC.toString()));
        checks.add(check(
                "caster_basic_is_not_vanilla_crafting_recipe",
                recipeManager.byKey(CASTER_BASIC)
                        .filter(holder -> holder.value() instanceof CraftingRecipe)
                        .isEmpty(),
                CASTER_BASIC.toString()
        ));
        if (casterBasic.isPresent()) {
            TCArcaneRecipe recipe = casterBasic.get();
            checks.add(check(
                    "caster_basic_research_and_vis",
                    "UNLOCKAUROMANCY@2".equals(recipe.getResearch()) && recipe.getVis() == 100,
                    "research=" + recipe.getResearch() + ", vis=" + recipe.getVis()
            ));
            checks.add(check(
                    "caster_basic_ordered_crystal_costs",
                    crystalSummary(recipe).equals(List.of(
                            "aer:1",
                            "terra:1",
                            "aqua:1",
                            "ignis:1",
                            "ordo:1",
                            "perditio:1"
                    )),
                    crystalSummary(recipe).toString()
            ));
            checks.add(check(
                    "caster_basic_result",
                    CASTER_BASIC.equals(BuiltInRegistries.ITEM.getKey(recipe.getResultItem(registries).getItem()))
                            && recipe.getResultItem(registries).getCount() == 1,
                    recipe.getResultItem(registries).toString()
            ));
            checks.add(check(
                    "caster_basic_shaped_pattern",
                    matchesCasterBasicPattern(recipe),
                    "width=" + recipe.width() + ", height=" + recipe.height()
            ));
        }

        TCResearchPageAvailability casterBasicAvailability = TCResearchPageCatalogManager.availability(
                CASTER_BASIC.toString(),
                recipeManager
        );
        checks.add(check(
                "caster_basic_catalog_arcane_snapshot_ready",
                casterBasicAvailability == TCResearchPageAvailability.READY
                        && TCResearchPageCatalogManager.buildArcanePage(CASTER_BASIC, recipeManager, registries).isPresent(),
                "availability=" + casterBasicAvailability
        ));

        Optional<TCArcaneRecipe> enchantedFabric = recipeManager.byKey(ENCHANTED_FABRIC)
                .filter(holder -> holder.value() instanceof TCArcaneRecipe)
                .map(holder -> (TCArcaneRecipe) holder.value());
        checks.add(check("enchantedfabric_arcane_recipe_loaded", enchantedFabric.isPresent(), ENCHANTED_FABRIC.toString()));
        checks.add(check(
                "enchantedfabric_is_not_vanilla_crafting_recipe",
                recipeManager.byKey(ENCHANTED_FABRIC)
                        .filter(holder -> holder.value() instanceof CraftingRecipe)
                        .isEmpty(),
                ENCHANTED_FABRIC.toString()
        ));
        if (enchantedFabric.isPresent()) {
            TCArcaneRecipe recipe = enchantedFabric.get();
            checks.add(check(
                    "enchantedfabric_research_and_vis",
                    "UNLOCKINFUSION".equals(recipe.getResearch()) && recipe.getVis() == 5,
                    "research=" + recipe.getResearch() + ", vis=" + recipe.getVis()
            ));
            checks.add(check(
                    "enchantedfabric_no_crystal_costs",
                    crystalSummary(recipe).isEmpty(),
                    crystalSummary(recipe).toString()
            ));
            checks.add(check(
                    "enchantedfabric_result",
                    FABRIC.equals(BuiltInRegistries.ITEM.getKey(recipe.getResultItem(registries).getItem()))
                            && recipe.getResultItem(registries).getCount() == 1,
                    recipe.getResultItem(registries).toString()
            ));
            checks.add(check(
                    "enchantedfabric_shaped_pattern",
                    matchesEnchantedFabricPattern(recipe),
                    "width=" + recipe.width() + ", height=" + recipe.height()
            ));
        }

        TCResearchPageAvailability enchantedFabricAvailability = TCResearchPageCatalogManager.availability(
                ENCHANTED_FABRIC.toString(),
                recipeManager
        );
        checks.add(check(
                "enchantedfabric_catalog_arcane_snapshot_ready",
                enchantedFabricAvailability == TCResearchPageAvailability.READY
                        && TCResearchPageCatalogManager.buildArcanePage(ENCHANTED_FABRIC, recipeManager, registries).isPresent(),
                "availability=" + enchantedFabricAvailability
        ));

        Optional<TCArcaneRecipe> mirrorGlass = recipeManager.byKey(MIRROR_GLASS)
                .filter(holder -> holder.value() instanceof TCArcaneRecipe)
                .map(holder -> (TCArcaneRecipe) holder.value());
        checks.add(check("mirrorglass_arcane_recipe_loaded", mirrorGlass.isPresent(), MIRROR_GLASS.toString()));
        checks.add(check(
                "mirrorglass_is_not_vanilla_crafting_recipe",
                recipeManager.byKey(MIRROR_GLASS)
                        .filter(holder -> holder.value() instanceof CraftingRecipe)
                        .isEmpty(),
                MIRROR_GLASS.toString()
        ));
        if (mirrorGlass.isPresent()) {
            TCArcaneRecipe recipe = mirrorGlass.get();
            checks.add(check(
                    "mirrorglass_research_and_vis",
                    "BASEARTIFICE".equals(recipe.getResearch()) && recipe.getVis() == 50,
                    "research=" + recipe.getResearch() + ", vis=" + recipe.getVis()
            ));
            checks.add(check(
                    "mirrorglass_ordered_crystal_costs",
                    crystalSummary(recipe).equals(List.of("aqua:1", "ordo:1")),
                    crystalSummary(recipe).toString()
            ));
            checks.add(check(
                    "mirrorglass_result",
                    MIRRORED_GLASS.equals(BuiltInRegistries.ITEM.getKey(recipe.getResultItem(registries).getItem()))
                            && recipe.getResultItem(registries).getCount() == 1,
                    recipe.getResultItem(registries).toString()
            ));
            checks.add(check(
                    "mirrorglass_shapeless_quicksilver_glass_pane",
                    matchesMirrorGlassIngredients(recipe),
                    "ingredients=" + recipe.getIngredients().size()
            ));
        }

        TCResearchPageAvailability mirrorGlassAvailability = TCResearchPageCatalogManager.availability(
                MIRROR_GLASS.toString(),
                recipeManager
        );
        checks.add(check(
                "mirrorglass_catalog_arcane_snapshot_ready",
                mirrorGlassAvailability == TCResearchPageAvailability.READY
                        && TCResearchPageCatalogManager.buildArcanePage(MIRROR_GLASS, recipeManager, registries).isPresent(),
                "availability=" + mirrorGlassAvailability
        ));

        Optional<TCArcaneRecipe> goggles = recipeManager.byKey(GOGGLES)
                .filter(holder -> holder.value() instanceof TCArcaneRecipe)
                .map(holder -> (TCArcaneRecipe) holder.value());
        checks.add(check("goggles_arcane_recipe_loaded", goggles.isPresent(), GOGGLES.toString()));
        checks.add(check(
                "goggles_is_not_vanilla_crafting_recipe",
                recipeManager.byKey(GOGGLES)
                        .filter(holder -> holder.value() instanceof CraftingRecipe)
                        .isEmpty(),
                GOGGLES.toString()
        ));
        if (goggles.isPresent()) {
            TCArcaneRecipe recipe = goggles.get();
            checks.add(check(
                    "goggles_research_and_vis",
                    "UNLOCKARTIFICE".equals(recipe.getResearch()) && recipe.getVis() == 50,
                    "research=" + recipe.getResearch() + ", vis=" + recipe.getVis()
            ));
            checks.add(check(
                    "goggles_no_crystal_costs",
                    crystalSummary(recipe).isEmpty(),
                    crystalSummary(recipe).toString()
            ));
            checks.add(check(
                    "goggles_result",
                    GOGGLES.equals(BuiltInRegistries.ITEM.getKey(recipe.getResultItem(registries).getItem()))
                            && recipe.getResultItem(registries).getCount() == 1,
                    recipe.getResultItem(registries).toString()
            ));
            checks.add(check(
                    "goggles_shaped_pattern",
                    matchesGogglesPattern(recipe),
                    "width=" + recipe.width() + ", height=" + recipe.height()
            ));
        }

        TCResearchPageAvailability gogglesAvailability = TCResearchPageCatalogManager.availability(
                GOGGLES.toString(),
                recipeManager
        );
        checks.add(check(
                "goggles_catalog_arcane_snapshot_ready",
                gogglesAvailability == TCResearchPageAvailability.READY
                        && TCResearchPageCatalogManager.buildArcanePage(GOGGLES, recipeManager, registries).isPresent(),
                "availability=" + gogglesAvailability
        ));

        return new Report(checks, arcaneRecipeCount);
    }

    private static List<String> crystalSummary(TCArcaneRecipe recipe) {
        return recipe.crystalCosts().stream()
                .map(cost -> cost.aspect() + ":" + cost.amount())
                .toList();
    }

    private static boolean matchesThaumometerPattern(TCArcaneRecipe recipe) {
        if (!(recipe instanceof TCShapedArcaneRecipe) || recipe.width() != 3 || recipe.height() != 3) {
            return false;
        }
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        return ingredients.size() == 9
                && ingredients.get(0).isEmpty()
                && ingredients.get(1).test(new ItemStack(Items.GOLD_INGOT))
                && ingredients.get(2).isEmpty()
                && ingredients.get(3).test(new ItemStack(Items.GOLD_INGOT))
                && ingredients.get(4).test(new ItemStack(Items.GLASS_PANE))
                && ingredients.get(5).test(new ItemStack(Items.GOLD_INGOT))
                && ingredients.get(6).isEmpty()
                && ingredients.get(7).test(new ItemStack(Items.GOLD_INGOT))
                && ingredients.get(8).isEmpty();
    }

    private static boolean matchesVisResonatorIngredients(TCArcaneRecipe recipe) {
        if (!(recipe instanceof TCShapelessArcaneRecipe) || recipe.getIngredients().size() != 2) {
            return false;
        }
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        return ingredients.get(0).test(new ItemStack(TCItems.IRON_PLATE.get()))
                && ingredients.get(1).test(new ItemStack(Items.QUARTZ));
    }

    private static boolean matchesWorkbenchChargerPattern(TCArcaneRecipe recipe) {
        if (!(recipe instanceof TCShapedArcaneRecipe) || recipe.width() != 3 || recipe.height() != 3) {
            return false;
        }
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        return ingredients.size() == 9
                && ingredients.get(0).isEmpty()
                && ingredients.get(1).test(new ItemStack(TCItems.VIS_RESONATOR.get()))
                && ingredients.get(2).isEmpty()
                && ingredients.get(3).test(new ItemStack(TCItems.PLANK_GREATWOOD.get()))
                && ingredients.get(4).isEmpty()
                && ingredients.get(5).test(new ItemStack(TCItems.PLANK_GREATWOOD.get()))
                && ingredients.get(6).test(new ItemStack(Items.IRON_INGOT))
                && ingredients.get(7).isEmpty()
                && ingredients.get(8).test(new ItemStack(Items.IRON_INGOT));
    }

    private static boolean matchesWandWorkbenchPattern(TCArcaneRecipe recipe) {
        if (!(recipe instanceof TCShapedArcaneRecipe) || recipe.width() != 3 || recipe.height() != 3) {
            return false;
        }
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        return ingredients.size() == 9
                && ingredients.get(0).test(new ItemStack(TCItems.IRON_PLATE.get()))
                && ingredients.get(1).test(new ItemStack(TCItems.SLAB_ARCANE_STONE.get()))
                && ingredients.get(2).test(new ItemStack(TCItems.IRON_PLATE.get()))
                && ingredients.get(3).test(new ItemStack(TCItems.STONE_ARCANE.get()))
                && ingredients.get(4).test(new ItemStack(TCItems.VIS_RESONATOR.get()))
                && ingredients.get(5).test(new ItemStack(TCItems.STONE_ARCANE.get()))
                && ingredients.get(6).test(new ItemStack(Items.GOLD_INGOT))
                && ingredients.get(7).test(new ItemStack(TCItems.TABLE_STONE.get()))
                && ingredients.get(8).test(new ItemStack(Items.GOLD_INGOT));
    }

    private static boolean matchesCasterBasicPattern(TCArcaneRecipe recipe) {
        if (!(recipe instanceof TCShapedArcaneRecipe) || recipe.width() != 3 || recipe.height() != 3) {
            return false;
        }
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        return ingredients.size() == 9
                && ingredients.get(0).test(new ItemStack(Items.IRON_INGOT))
                && ingredients.get(1).test(new ItemStack(Items.IRON_INGOT))
                && ingredients.get(2).test(new ItemStack(Items.IRON_INGOT))
                && ingredients.get(3).test(new ItemStack(Items.LEATHER))
                && ingredients.get(4).test(new ItemStack(TCItems.VIS_RESONATOR.get()))
                && ingredients.get(5).test(new ItemStack(Items.LEATHER))
                && ingredients.get(6).test(new ItemStack(Items.LEATHER))
                && ingredients.get(7).test(new ItemStack(TCItems.THAUMOMETER.get()))
                && ingredients.get(8).test(new ItemStack(Items.LEATHER));
    }

    private static boolean matchesEnchantedFabricPattern(TCArcaneRecipe recipe) {
        if (!(recipe instanceof TCShapedArcaneRecipe) || recipe.width() != 3 || recipe.height() != 3) {
            return false;
        }
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        return ingredients.size() == 9
                && ingredients.get(0).isEmpty()
                && ingredients.get(1).test(new ItemStack(Items.STRING))
                && ingredients.get(2).isEmpty()
                && ingredients.get(3).test(new ItemStack(Items.STRING))
                && ingredients.get(4).test(new ItemStack(Items.WHITE_WOOL))
                && ingredients.get(4).test(new ItemStack(Items.RED_WOOL))
                && ingredients.get(5).test(new ItemStack(Items.STRING))
                && ingredients.get(6).isEmpty()
                && ingredients.get(7).test(new ItemStack(Items.STRING))
                && ingredients.get(8).isEmpty();
    }

    private static boolean matchesMirrorGlassIngredients(TCArcaneRecipe recipe) {
        if (!(recipe instanceof TCShapelessArcaneRecipe) || recipe.getIngredients().size() != 2) {
            return false;
        }
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        return ingredients.get(0).test(new ItemStack(TCItems.QUICKSILVER.get()))
                && ingredients.get(1).test(new ItemStack(Items.GLASS_PANE));
    }

    private static String wandWorkbenchPatternDetail(TCArcaneRecipe recipe) {
        if (!(recipe instanceof TCShapedArcaneRecipe) || recipe.getIngredients().size() != 9) {
            return "width=" + recipe.width() + ", height=" + recipe.height()
                    + ", ingredients=" + recipe.getIngredients().size();
        }
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        return "width=" + recipe.width()
                + ", height=" + recipe.height()
                + ", slots="
                + List.of(
                        ingredients.get(0).test(new ItemStack(TCItems.IRON_PLATE.get())),
                        ingredients.get(1).test(new ItemStack(TCItems.SLAB_ARCANE_STONE.get())),
                        ingredients.get(2).test(new ItemStack(TCItems.IRON_PLATE.get())),
                        ingredients.get(3).test(new ItemStack(TCItems.STONE_ARCANE.get())),
                        ingredients.get(4).test(new ItemStack(TCItems.VIS_RESONATOR.get())),
                        ingredients.get(5).test(new ItemStack(TCItems.STONE_ARCANE.get())),
                        ingredients.get(6).test(new ItemStack(Items.GOLD_INGOT)),
                        ingredients.get(7).test(new ItemStack(TCItems.TABLE_STONE.get())),
                        ingredients.get(8).test(new ItemStack(Items.GOLD_INGOT))
                );
    }

    private static boolean matchesGogglesPattern(TCArcaneRecipe recipe) {
        if (!(recipe instanceof TCShapedArcaneRecipe) || recipe.width() != 3 || recipe.height() != 3) {
            return false;
        }
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        return ingredients.size() == 9
                && ingredients.get(0).test(new ItemStack(Items.LEATHER))
                && ingredients.get(1).test(new ItemStack(TCItems.BRASS_INGOT.get()))
                && ingredients.get(2).test(new ItemStack(Items.LEATHER))
                && ingredients.get(3).test(new ItemStack(Items.LEATHER))
                && ingredients.get(4).isEmpty()
                && ingredients.get(5).test(new ItemStack(Items.LEATHER))
                && ingredients.get(6).test(new ItemStack(TCItems.THAUMOMETER.get()))
                && ingredients.get(7).test(new ItemStack(TCItems.BRASS_INGOT.get()))
                && ingredients.get(8).test(new ItemStack(TCItems.THAUMOMETER.get()));
    }

    private static Check check(String name, boolean passed, String detail) {
        return new Check(name, passed, detail);
    }

    record Check(String name, boolean passed, String detail) {
    }

    record Report(List<Check> checks, int arcaneRecipeCount) {
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
