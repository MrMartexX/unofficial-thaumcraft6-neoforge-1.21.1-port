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
    private static final ResourceLocation THAUMOMETER_BRIDGE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_bridge/thaumometer");
    private static final ResourceLocation VIS_RESONATOR_BRIDGE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "research_bridge/vis_resonator");

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
        checks.add(check("arcane_recipe_type_has_loaded_recipes", arcaneRecipeCount >= 2, "count=" + arcaneRecipeCount));
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
