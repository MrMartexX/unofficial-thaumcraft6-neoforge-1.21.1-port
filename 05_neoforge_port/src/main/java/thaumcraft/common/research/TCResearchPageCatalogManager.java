package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.crafting.arcane.TCArcaneCrystalCost;
import thaumcraft.common.crafting.arcane.TCArcaneRecipe;
import thaumcraft.common.crafting.crucible.TCCrucibleAspectCost;
import thaumcraft.common.crafting.crucible.TCCrucibleRecipe;
import thaumcraft.common.crafting.infusion.TCInfusionRecipe;

public final class TCResearchPageCatalogManager {
    private static TCResearchPageCatalogData activeData = TCResearchPageCatalogData.empty();
    private static int dataRevision;

    private TCResearchPageCatalogManager() {
    }

    public static void bootstrap() {
        activeData = TCResearchPageCatalogData.empty();
        dataRevision = 0;
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new TCResearchPageCatalogReloadListener());
    }

    static void reload(TCResearchPageCatalogData data) {
        activeData = data == null ? TCResearchPageCatalogData.empty() : data;
        dataRevision++;
        TCResearchPageCatalogValidationReport report = validate();
        if (report.isValid()) {
            Thaumcraft.LOGGER.info(
                    "Thaumcraft research page catalog loaded: {} direct references, {} total entries, {} research occurrences, 0 structural errors.",
                    report.directReferenceCount(),
                    report.totalEntryCount(),
                    report.researchOccurrenceCount()
            );
        } else {
            Thaumcraft.LOGGER.warn(
                    "Thaumcraft research page catalog validation failed: missing research refs={}, unresolved group targets={}, cyclic groups={}.",
                    report.missingResearchReferences().size(),
                    report.unresolvedGroupTargets().size(),
                    report.cyclicGroups().size()
            );
        }
    }

    public static Collection<TCResearchPageCatalogEntry> entries() {
        return activeData.entries().values();
    }

    public static int dataRevision() {
        return dataRevision;
    }

    public static Optional<TCResearchPageCatalogEntry> get(String rawId) {
        return Optional.ofNullable(activeData.entries().get(canonicalId(rawId)));
    }

    public static Optional<TCResearchPageCatalogEntry> get(ResourceLocation id) {
        return Optional.ofNullable(activeData.entries().get(id));
    }

    public static TCResearchPageCatalogValidationReport validate() {
        return activeData.validate(TCResearchManager.data());
    }

    public static TCResearchPageAvailability availability(String rawId, RecipeManager recipeManager) {
        return availability(canonicalId(rawId), recipeManager, new HashSet<>());
    }

    public static List<TCResearchPageBookmark> bookmarksForCurrentStage(ServerPlayer player, String researchKey) {
        Optional<TCResearchPageStageSelection> selectionOptional = selectCurrentStage(player, researchKey);
        if (selectionOptional.isEmpty()) {
            return List.of();
        }

        TCResearchPageStageSelection selection = selectionOptional.get();
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        ArrayList<String> references = new ArrayList<>(selection.stage().recipes());
        for (TCResearchStageDefinition addendum : selection.visibleAddenda()) {
            references.addAll(addendum.recipes());
        }

        RecipeManager recipeManager = player.server.getRecipeManager();
        ArrayList<TCResearchPageBookmark> bookmarks = new ArrayList<>();
        for (String reference : references) {
            ResourceLocation bookmarkId = canonicalId(reference);
            TCResearchPageCatalogEntry catalogEntry = activeData.entries().get(bookmarkId);
            if (catalogEntry == null || catalogEntry.legacySource() == TCResearchPageLegacySource.MISSING) {
                continue;
            }

            List<TCResearchPageView> pages = visiblePagesForReference(player, bookmarkId, recipeManager, knowledge);
            if (!pages.isEmpty()) {
                bookmarks.add(new TCResearchPageBookmark(bookmarkId, pages));
            }
        }
        return List.copyOf(bookmarks);
    }

    static Optional<TCResearchPageDrilldownResult> findRecipeDrilldown(ServerPlayer player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) {
            return Optional.empty();
        }

        RecipeManager recipeManager = player.server.getRecipeManager();
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        for (TCResearchEntryDefinition entry : TCResearchManager.entries()) {
            for (TCResearchStageDefinition stage : entry.stages()) {
                for (String reference : stage.recipes()) {
                    ResourceLocation bookmarkId = canonicalId(reference);
                    Optional<TCResearchPageDrilldownResult> result = findRecipeDrilldown(
                            player,
                            bookmarkId,
                            stack,
                            recipeManager,
                            knowledge
                    );
                    if (result.isPresent()) {
                        return result;
                    }
                }
            }
        }
        return Optional.empty();
    }

    static Optional<TCResearchPageStageSelection> selectCurrentStage(ServerPlayer player, String researchKey) {
        if (player == null || !TCResearchManager.isResearchVisible(player, researchKey)) {
            return Optional.empty();
        }

        Optional<TCResearchEntryDefinition> entryOptional = TCResearchManager.getEntry(researchKey);
        if (entryOptional.isEmpty() || entryOptional.get().stages().isEmpty()) {
            return Optional.empty();
        }

        TCResearchEntryDefinition entry = entryOptional.get();
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        int currentStage = knowledge.getResearchStage(entry.key()) - 1;
        boolean complete = false;
        while (currentStage >= entry.stages().size()) {
            currentStage--;
            complete = true;
        }
        if (currentStage < 0) {
            currentStage = 0;
        }

        ArrayList<TCResearchStageDefinition> visibleAddenda = new ArrayList<>();
        if (complete) {
            for (TCResearchStageDefinition addendum : entry.addenda()) {
                String[] requirements = addendum.requiredResearch().toArray(String[]::new);
                if (TCResearchManager.knowsResearchStrict(knowledge, requirements)) {
                    visibleAddenda.add(addendum);
                }
            }
        }
        return Optional.of(new TCResearchPageStageSelection(
                entry,
                entry.stages().get(currentStage),
                currentStage,
                complete,
                visibleAddenda
        ));
    }

    static ResourceLocation canonicalId(String rawId) {
        String normalized = rawId == null ? "" : rawId.trim().toLowerCase(Locale.ROOT);
        if (!normalized.contains(":")) {
            normalized = Thaumcraft.MODID + ":" + normalized;
        }
        ResourceLocation id = ResourceLocation.tryParse(normalized);
        if (id == null) {
            throw new IllegalArgumentException("Invalid research page catalog id: " + rawId);
        }
        return id;
    }

    private static Optional<TCResearchPageDrilldownResult> findRecipeDrilldown(
            ServerPlayer player,
            ResourceLocation bookmarkId,
            ItemStack stack,
            RecipeManager recipeManager,
            TCPlayerKnowledge knowledge
    ) {
        List<TCResearchPageView> pages = visiblePagesForReference(player, bookmarkId, recipeManager, knowledge);
        for (int pageIndex = 0; pageIndex < pages.size(); pageIndex++) {
            TCResearchPageView page = pages.get(pageIndex);
            if (page.availability() == TCResearchPageAvailability.READY && recipeOutputMatches(page, stack)) {
                return Optional.of(new TCResearchPageDrilldownResult(
                        new TCResearchPageBookmark(bookmarkId, pages),
                        pageIndex
                ));
            }
        }
        return Optional.empty();
    }

    private static List<TCResearchPageView> visiblePagesForReference(
            ServerPlayer player,
            ResourceLocation bookmarkId,
            RecipeManager recipeManager,
            TCPlayerKnowledge knowledge
    ) {
        TCResearchPageCatalogEntry catalogEntry = activeData.entries().get(bookmarkId);
        if (catalogEntry == null || catalogEntry.legacySource() == TCResearchPageLegacySource.MISSING) {
            return List.of();
        }

        ArrayList<TCResearchPageView> pages = new ArrayList<>();
        if (catalogEntry.kind() == TCResearchPageKind.GROUP) {
            for (ResourceLocation target : catalogEntry.targets()) {
                addVisiblePage(pages, target, recipeManager, player.server.registryAccess(), knowledge);
            }
        } else {
            addVisiblePage(pages, bookmarkId, recipeManager, player.server.registryAccess(), knowledge);
        }
        return List.copyOf(pages);
    }

    private static boolean recipeOutputMatches(TCResearchPageView page, ItemStack stack) {
        Optional<ItemStack> output = recipeOutput(page);
        return output.isPresent() && !output.get().isEmpty() && output.get().is(stack.getItem());
    }

    private static Optional<ItemStack> recipeOutput(TCResearchPageView page) {
        if (page.craftingRecipe().isPresent()) {
            return Optional.of(page.craftingRecipe().get().result());
        }
        if (page.arcaneRecipe().isPresent()) {
            return Optional.of(page.arcaneRecipe().get().result());
        }
        if (page.crucibleRecipe().isPresent()) {
            return Optional.of(page.crucibleRecipe().get().result());
        }
        if (page.infusionRecipe().isPresent()) {
            return Optional.of(page.infusionRecipe().get().result());
        }
        return Optional.empty();
    }

    private static void addVisiblePage(
            List<TCResearchPageView> pages,
            ResourceLocation pageId,
            RecipeManager recipeManager,
            HolderLookup.Provider registries,
            TCPlayerKnowledge knowledge
    ) {
        TCResearchPageCatalogEntry entry = activeData.entries().get(pageId);
        if (entry == null || entry.legacySource() == TCResearchPageLegacySource.MISSING) {
            return;
        }
        if (!entry.requiredResearch().isBlank()
                && !TCResearchManager.knowsResearchStrict(knowledge, entry.requiredResearch())) {
            return;
        }
        TCResearchPageAvailability availability = availability(entry.id(), recipeManager, new HashSet<>());
        Optional<TCCraftingRecipePageView> craftingRecipe = Optional.empty();
        Optional<TCArcaneRecipePageView> arcaneRecipe = Optional.empty();
        Optional<TCCrucibleRecipePageView> crucibleRecipe = Optional.empty();
        Optional<TCInfusionRecipePageView> infusionRecipe = Optional.empty();
        if (availability == TCResearchPageAvailability.READY) {
            if (entry.kind() == TCResearchPageKind.CRAFTING) {
                craftingRecipe = buildCraftingPage(entry.id(), recipeManager, registries);
            } else if (entry.kind() == TCResearchPageKind.ARCANE) {
                arcaneRecipe = buildArcanePage(entry.id(), recipeManager, registries);
            } else if (entry.kind() == TCResearchPageKind.CRUCIBLE) {
                crucibleRecipe = buildCruciblePage(entry.id(), recipeManager, registries);
            } else if (entry.kind() == TCResearchPageKind.INFUSION) {
                infusionRecipe = buildInfusionPage(entry.id(), recipeManager, registries);
            }
        }
        if (availability == TCResearchPageAvailability.READY
                && craftingRecipe.isEmpty()
                && arcaneRecipe.isEmpty()
                && crucibleRecipe.isEmpty()
                && infusionRecipe.isEmpty()) {
            availability = TCResearchPageAvailability.DEFERRED;
        }
        pages.add(new TCResearchPageView(
                entry.id(),
                entry.kind(),
                availability,
                entry.requiredResearch(),
                entry.legacyOutput(),
                craftingRecipe,
                arcaneRecipe,
                crucibleRecipe,
                infusionRecipe
        ));
    }

    static Optional<TCCraftingRecipePageView> buildCraftingPage(
            ResourceLocation id,
            RecipeManager recipeManager,
            HolderLookup.Provider registries
    ) {
        return recipeManager.byKey(id)
                .filter(holder -> holder.value() instanceof CraftingRecipe)
                .map(holder -> {
                    CraftingRecipe recipe = (CraftingRecipe) holder.value();
                    boolean shaped = recipe instanceof ShapedRecipe;
                    int width = shaped ? ((ShapedRecipe) recipe).getWidth() : 3;
                    int height = shaped ? ((ShapedRecipe) recipe).getHeight() : 3;
                    NonNullList<Ingredient> recipeIngredients = recipe.getIngredients();
                    ArrayList<List<ItemStack>> ingredients = new ArrayList<>(recipeIngredients.size());
                    for (Ingredient ingredient : recipeIngredients) {
                        ItemStack[] variants = ingredient.getItems();
                        ArrayList<ItemStack> copiedVariants = new ArrayList<>(variants.length);
                        for (ItemStack variant : variants) {
                            copiedVariants.add(variant.copy());
                        }
                        ingredients.add(List.copyOf(copiedVariants));
                    }
                    return new TCCraftingRecipePageView(
                            id,
                            shaped,
                            width,
                            height,
                            recipe.getResultItem(registries),
                            ingredients
                    );
                });
    }

    static Optional<TCArcaneRecipePageView> buildArcanePage(
            ResourceLocation id,
            RecipeManager recipeManager,
            HolderLookup.Provider registries
    ) {
        return recipeManager.byKey(id)
                .filter(holder -> holder.value() instanceof TCArcaneRecipe)
                .map(holder -> {
                    TCArcaneRecipe recipe = (TCArcaneRecipe) holder.value();
                    NonNullList<Ingredient> recipeIngredients = recipe.getIngredients();
                    ArrayList<List<ItemStack>> ingredients = new ArrayList<>(recipeIngredients.size());
                    for (Ingredient ingredient : recipeIngredients) {
                        ItemStack[] variants = ingredient.getItems();
                        ArrayList<ItemStack> copiedVariants = new ArrayList<>(variants.length);
                        for (ItemStack variant : variants) {
                            copiedVariants.add(variant.copy());
                        }
                        ingredients.add(List.copyOf(copiedVariants));
                    }
                    ArrayList<ItemStack> crystals = new ArrayList<>(recipe.crystalCosts().size());
                    for (TCArcaneCrystalCost cost : recipe.crystalCosts()) {
                        crystals.add(cost.displayStack());
                    }
                    return new TCArcaneRecipePageView(
                            id,
                            recipe.shaped(),
                            recipe.width(),
                            recipe.height(),
                            recipe.getResultItem(registries),
                            ingredients,
                            recipe.getResearch(),
                            recipe.getVis(),
                            crystals
                    );
                });
    }

    static Optional<TCCrucibleRecipePageView> buildCruciblePage(
            ResourceLocation id,
            RecipeManager recipeManager,
            HolderLookup.Provider registries
    ) {
        return recipeManager.byKey(id)
                .filter(holder -> holder.value() instanceof TCCrucibleRecipe)
                .map(holder -> {
                    TCCrucibleRecipe recipe = (TCCrucibleRecipe) holder.value();
                    ArrayList<ItemStack> catalystVariants = new ArrayList<>();
                    for (ItemStack variant : recipe.catalyst().getItems()) {
                        catalystVariants.add(variant.copy());
                    }
                    ArrayList<ItemStack> aspects = new ArrayList<>(recipe.aspectCosts().size());
                    for (TCCrucibleAspectCost cost : recipe.aspectCosts()) {
                        aspects.add(cost.displayStack());
                    }
                    return new TCCrucibleRecipePageView(
                            id,
                            recipe.getResultItem(registries),
                            catalystVariants,
                            aspects,
                            recipe.getResearch()
                    );
                });
    }

    static Optional<TCInfusionRecipePageView> buildInfusionPage(
            ResourceLocation id,
            RecipeManager recipeManager,
            HolderLookup.Provider registries
    ) {
        return recipeManager.byKey(id)
                .filter(holder -> holder.value() instanceof TCInfusionRecipe)
                .map(holder -> {
                    TCInfusionRecipe recipe = (TCInfusionRecipe) holder.value();
                    ArrayList<ItemStack> catalystVariants = new ArrayList<>();
                    for (ItemStack variant : recipe.catalyst().getItems()) {
                        catalystVariants.add(variant.copy());
                    }
                    ArrayList<List<ItemStack>> components = new ArrayList<>(recipe.components().size());
                    for (Ingredient ingredient : recipe.components()) {
                        ItemStack[] variants = ingredient.getItems();
                        ArrayList<ItemStack> copiedVariants = new ArrayList<>(variants.length);
                        for (ItemStack variant : variants) {
                            copiedVariants.add(variant.copy());
                        }
                        components.add(List.copyOf(copiedVariants));
                    }
                    ArrayList<ItemStack> aspects = new ArrayList<>(recipe.aspectCosts().size());
                    for (TCCrucibleAspectCost cost : recipe.aspectCosts()) {
                        aspects.add(cost.displayStack());
                    }
                    return new TCInfusionRecipePageView(
                            id,
                            recipe.getResultItem(registries),
                            catalystVariants,
                            components,
                            aspects,
                            recipe.getResearch(),
                            recipe.instability()
                    );
                });
    }

    private static TCResearchPageAvailability availability(
            ResourceLocation id,
            RecipeManager recipeManager,
            Set<ResourceLocation> visiting
    ) {
        TCResearchPageCatalogEntry entry = activeData.entries().get(id);
        if (entry == null || entry.legacySource() == TCResearchPageLegacySource.MISSING) {
            return TCResearchPageAvailability.LEGACY_MISSING;
        }
        if (!visiting.add(id)) {
            return TCResearchPageAvailability.DEFERRED;
        }

        try {
            if (entry.kind() == TCResearchPageKind.CRAFTING) {
                return recipeManager.byKey(entry.id())
                        .filter(holder -> holder.value() instanceof CraftingRecipe)
                        .isPresent()
                        ? TCResearchPageAvailability.READY
                        : TCResearchPageAvailability.DEFERRED;
            }
            if (entry.kind() == TCResearchPageKind.ARCANE) {
                return recipeManager.byKey(entry.id())
                        .filter(holder -> holder.value() instanceof TCArcaneRecipe)
                        .isPresent()
                        ? TCResearchPageAvailability.READY
                        : TCResearchPageAvailability.DEFERRED;
            }
            if (entry.kind() == TCResearchPageKind.CRUCIBLE) {
                return recipeManager.byKey(entry.id())
                        .filter(holder -> holder.value() instanceof TCCrucibleRecipe)
                        .isPresent()
                        ? TCResearchPageAvailability.READY
                        : TCResearchPageAvailability.DEFERRED;
            }
            if (entry.kind() == TCResearchPageKind.INFUSION) {
                return recipeManager.byKey(entry.id())
                        .filter(holder -> holder.value() instanceof TCInfusionRecipe)
                        .isPresent()
                        ? TCResearchPageAvailability.READY
                        : TCResearchPageAvailability.DEFERRED;
            }
            if (entry.kind() == TCResearchPageKind.GROUP) {
                for (ResourceLocation target : entry.targets()) {
                    if (availability(target, recipeManager, visiting) != TCResearchPageAvailability.READY) {
                        return TCResearchPageAvailability.DEFERRED;
                    }
                }
                return TCResearchPageAvailability.READY;
            }
            return TCResearchPageAvailability.DEFERRED;
        } finally {
            visiting.remove(id);
        }
    }
}
