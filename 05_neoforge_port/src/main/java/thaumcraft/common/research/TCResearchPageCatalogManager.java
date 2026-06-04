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

public final class TCResearchPageCatalogManager {
    private static TCResearchPageCatalogData activeData = TCResearchPageCatalogData.empty();

    private TCResearchPageCatalogManager() {
    }

    public static void bootstrap() {
        activeData = TCResearchPageCatalogData.empty();
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new TCResearchPageCatalogReloadListener());
    }

    static void reload(TCResearchPageCatalogData data) {
        activeData = data == null ? TCResearchPageCatalogData.empty() : data;
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

            ArrayList<TCResearchPageView> pages = new ArrayList<>();
            if (catalogEntry.kind() == TCResearchPageKind.GROUP) {
                for (ResourceLocation target : catalogEntry.targets()) {
                    addVisiblePage(pages, target, recipeManager, player.server.registryAccess(), knowledge);
                }
            } else {
                addVisiblePage(pages, bookmarkId, recipeManager, player.server.registryAccess(), knowledge);
            }
            if (!pages.isEmpty()) {
                bookmarks.add(new TCResearchPageBookmark(bookmarkId, pages));
            }
        }
        return List.copyOf(bookmarks);
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
        Optional<TCCraftingRecipePageView> craftingRecipe = availability == TCResearchPageAvailability.READY
                ? buildCraftingPage(entry.id(), recipeManager, registries)
                : Optional.empty();
        if (availability == TCResearchPageAvailability.READY && craftingRecipe.isEmpty()) {
            availability = TCResearchPageAvailability.DEFERRED;
        }
        pages.add(new TCResearchPageView(
                entry.id(),
                entry.kind(),
                availability,
                entry.requiredResearch(),
                entry.legacyOutput(),
                craftingRecipe
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
