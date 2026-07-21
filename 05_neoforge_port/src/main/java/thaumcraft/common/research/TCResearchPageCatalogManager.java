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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.crafting.arcane.TCArcaneCrystalCost;
import thaumcraft.common.crafting.arcane.TCArcaneRecipe;
import thaumcraft.common.crafting.crucible.TCCrucibleAspectCost;
import thaumcraft.common.crafting.crucible.TCCrucibleRecipe;
import thaumcraft.common.crafting.infusion.TCInfusionRecipe;
import thaumcraft.common.registry.TCItems;

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

    static Optional<ItemStack> recipeOutput(TCResearchPageView page) {
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
        if (page.blueprintRecipe().isPresent()) {
            return Optional.of(page.blueprintRecipe().get().displayStack());
        }
        if (page.displayRecipe().isPresent()) {
            return Optional.of(page.displayRecipe().get().result());
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
        Optional<TCBlueprintRecipePageView> blueprintRecipe = Optional.empty();
        Optional<TCDisplayRecipePageView> displayRecipe = Optional.empty();
        if (availability == TCResearchPageAvailability.READY) {
            if (entry.legacySource() == TCResearchPageLegacySource.FAKE_CATALOG) {
                displayRecipe = buildDisplayPage(entry.id());
            } else if (entry.kind() == TCResearchPageKind.CRAFTING) {
                craftingRecipe = buildCraftingPage(entry.id(), recipeManager, registries);
            } else if (entry.kind() == TCResearchPageKind.ARCANE) {
                arcaneRecipe = buildArcanePage(entry.id(), recipeManager, registries);
            } else if (entry.kind() == TCResearchPageKind.CRUCIBLE) {
                crucibleRecipe = buildCruciblePage(entry.id(), recipeManager, registries);
            } else if (entry.kind() == TCResearchPageKind.INFUSION) {
                infusionRecipe = buildInfusionPage(entry.id(), recipeManager, registries);
            } else if (entry.kind() == TCResearchPageKind.BLUEPRINT) {
                blueprintRecipe = buildBlueprintPage(entry.id());
            }
        }
        if (availability == TCResearchPageAvailability.READY
                && craftingRecipe.isEmpty()
                && arcaneRecipe.isEmpty()
                && crucibleRecipe.isEmpty()
                && infusionRecipe.isEmpty()
                && blueprintRecipe.isEmpty()
                && displayRecipe.isEmpty()) {
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
                infusionRecipe,
                blueprintRecipe,
                displayRecipe
        ));
    }

    static Optional<TCDisplayRecipePageView> buildDisplayPage(ResourceLocation id) {
        if (id == null || !Thaumcraft.MODID.equals(id.getNamespace())) {
            return Optional.empty();
        }
        return switch (id.getPath()) {
            case "salismundusfake" -> Optional.of(displayCrafting(
                    id,
                    stack(TCItems.SALIS_MUNDUS.get()),
                    "recipe.display.thaumcraft.salismundusfake",
                    stack(Items.FLINT),
                    stack(Items.BOWL),
                    stack(Items.REDSTONE),
                    stack(TCItems.CRYSTAL_ESSENCE_AER.get()),
                    stack(TCItems.CRYSTAL_ESSENCE_TERRA.get()),
                    stack(TCItems.CRYSTAL_ESSENCE_IGNIS.get())
            ));
            case "triplemeattreatfake" -> Optional.of(displayCrafting(
                    id,
                    stack(TCItems.TRIPLE_MEAT_TREAT.get()),
                    "recipe.display.thaumcraft.triplemeattreatfake",
                    stack(TCItems.CHUNK_BEEF.get()),
                    stack(TCItems.CHUNK_PORK.get()),
                    stack(TCItems.CHUNK_CHICKEN.get()),
                    stack(Items.SUGAR)
            ));
            case "ieburrowingfake" -> Optional.of(displayInfusionEnchantment(
                    id,
                    stack(Items.WOODEN_PICKAXE),
                    "recipe.display.thaumcraft.ieburrowingfake",
                    stack(Items.RABBIT_FOOT),
                    aspect("senses", 80),
                    aspect("earth", 150)
            ));
            case "iecollectorfake" -> Optional.of(displayInfusionEnchantment(
                    id,
                    stack(Items.STONE_AXE),
                    "recipe.display.thaumcraft.iecollectorfake",
                    stack(Items.LEAD),
                    aspect("desire", 80),
                    aspect("water", 100)
            ));
            case "iedestructivefake" -> Optional.of(displayInfusionEnchantment(
                    id,
                    stack(Items.STONE_PICKAXE),
                    "recipe.display.thaumcraft.iedestructivefake",
                    stack(Blocks.TNT),
                    aspect("aversion", 200),
                    aspect("entropy", 250)
            ));
            case "ierefiningfake" -> Optional.of(displayInfusionEnchantment(
                    id,
                    stack(Items.IRON_PICKAXE),
                    "recipe.display.thaumcraft.ierefiningfake",
                    stack(TCItems.SALIS_MUNDUS.get()),
                    aspect("order", 80),
                    aspect("exchange", 60)
            ));
            case "iesoundingfake" -> Optional.of(displayInfusionEnchantment(
                    id,
                    stack(Items.GOLDEN_PICKAXE),
                    "recipe.display.thaumcraft.iesoundingfake",
                    stack(Items.MAP),
                    aspect("senses", 40),
                    aspect("fire", 60)
            ));
            case "iearcingfake" -> Optional.of(displayInfusionEnchantment(
                    id,
                    stack(Items.WOODEN_SWORD),
                    "recipe.display.thaumcraft.iearcingfake",
                    stack(Blocks.REDSTONE_BLOCK),
                    aspect("energy", 40),
                    aspect("air", 60)
            ));
            case "ieessencefake" -> Optional.of(displayInfusionEnchantment(
                    id,
                    stack(Items.STONE_SWORD),
                    "recipe.display.thaumcraft.ieessencefake",
                    stack(TCItems.CRYSTAL_ESSENCE_AER.get()),
                    aspect("beast", 40),
                    aspect("flux", 60)
            ));
            case "ielamplightfake" -> Optional.of(displayInfusionEnchantment(
                    id,
                    stack(Items.GOLDEN_PICKAXE),
                    "recipe.display.thaumcraft.ielamplightfake",
                    stack(TCItems.NITOR_YELLOW.get()),
                    aspect("light", 80),
                    aspect("air", 20)
            ));
            case "runicarmorfake0" -> Optional.of(displayRunicAugment(id, 0));
            case "runicarmorfake1" -> Optional.of(displayRunicAugment(id, 1));
            case "runicarmorfake2" -> Optional.of(displayRunicAugment(id, 2));
            default -> Optional.empty();
        };
    }

    private static TCDisplayRecipePageView displayCrafting(
            ResourceLocation id,
            ItemStack result,
            String titleKey,
            ItemStack... inputs
    ) {
        return new TCDisplayRecipePageView(
                id,
                TCDisplayRecipePageType.FAKE_CRAFTING,
                result,
                List.of(),
                List.of(inputs),
                List.of(),
                titleKey,
                0
        );
    }

    private static TCDisplayRecipePageView displayInfusionEnchantment(
            ResourceLocation id,
            ItemStack base,
            String titleKey,
            ItemStack component,
            ItemStack... aspects
    ) {
        return new TCDisplayRecipePageView(
                id,
                TCDisplayRecipePageType.INFUSION_ENCHANTMENT,
                base.copy(),
                List.of(base),
                List.of(stack(Items.ENCHANTED_BOOK), component),
                List.of(aspects),
                titleKey,
                4
        );
    }

    private static TCDisplayRecipePageView displayRunicAugment(ResourceLocation id, int currentCharge) {
        ArrayList<ItemStack> components = new ArrayList<>();
        components.add(stack(TCItems.SALIS_MUNDUS.get()));
        components.add(stack(TCItems.AMBER.get()));
        for (int count = 0; count < currentCharge; count++) {
            components.add(stack(TCItems.AMBER.get()));
        }
        int vis = 20 + (int) (20.0D * Math.pow(2.0D, currentCharge));
        return new TCDisplayRecipePageView(
                id,
                TCDisplayRecipePageType.RUNIC_AUGMENT,
                stack(TCItems.BAUBLE_RING.get()),
                List.of(stack(TCItems.BAUBLE_RING.get())),
                components,
                List.of(
                        aspect("protect", vis),
                        aspect("crystal", vis / 2),
                        aspect("energy", vis / 2)
                ),
                "recipe.display.thaumcraft.runicarmorfake" + currentCharge,
                5 + currentCharge / 2
        );
    }

    private static ItemStack stack(ItemLike item) {
        return new ItemStack(item);
    }

    private static ItemStack stack(ItemLike item, int count) {
        return new ItemStack(item, count);
    }

    private static ItemStack aspect(String aspect, int amount) {
        return new TCCrucibleAspectCost(aspect, amount).displayStack();
    }

    static Optional<TCBlueprintRecipePageView> buildBlueprintPage(ResourceLocation id) {
        if (id == null || !Thaumcraft.MODID.equals(id.getNamespace())) {
            return Optional.empty();
        }
        return switch (id.getPath()) {
            case "infernalfurnace" -> Optional.of(blueprint(
                    id,
                    "INFERNALFURNACE",
                    stack(TCItems.INFERNAL_FURNACE.get()),
                    List.of(
                            stack(Blocks.NETHER_BRICKS, 12),
                            stack(Blocks.OBSIDIAN, 12),
                            stack(Blocks.IRON_BARS),
                            stack(Items.LAVA_BUCKET)
                    ),
                    layer(
                            row(source(Blocks.NETHER_BRICKS), source(Blocks.OBSIDIAN), source(Blocks.NETHER_BRICKS)),
                            row(source(Blocks.OBSIDIAN), emptyCell(), source(Blocks.OBSIDIAN)),
                            row(source(Blocks.NETHER_BRICKS), source(Blocks.OBSIDIAN), source(Blocks.NETHER_BRICKS))
                    ),
                    layer(
                            row(source(Blocks.NETHER_BRICKS), source(Blocks.OBSIDIAN), source(Blocks.NETHER_BRICKS)),
                            row(source(Blocks.OBSIDIAN), source(Items.LAVA_BUCKET, TCItems.INFERNAL_FURNACE.get()), source(Blocks.OBSIDIAN)),
                            row(source(Blocks.NETHER_BRICKS), source(Blocks.IRON_BARS), source(Blocks.NETHER_BRICKS))
                    ),
                    layer(
                            row(source(Blocks.NETHER_BRICKS), source(Blocks.OBSIDIAN), source(Blocks.NETHER_BRICKS)),
                            row(source(Blocks.OBSIDIAN), source(Blocks.OBSIDIAN), source(Blocks.OBSIDIAN)),
                            row(source(Blocks.NETHER_BRICKS), source(Blocks.OBSIDIAN), source(Blocks.NETHER_BRICKS))
                    )
            ));
            case "infusionaltar" -> Optional.of(infusionAltarBlueprint(
                    id,
                    "INFUSION",
                    stack(TCItems.INFUSION_ALTAR_BLUEPRINT.get()),
                    TCItems.STONE_ARCANE.get(),
                    TCItems.PILLAR_ARCANE.get(),
                    TCItems.ARCANE_PEDESTAL.get()
            ));
            case "infusionaltarancient" -> Optional.of(infusionAltarBlueprint(
                    id,
                    "INFUSIONANCIENT",
                    stack(TCItems.INFUSION_ALTAR_ANCIENT_BLUEPRINT.get()),
                    TCItems.STONE_ANCIENT.get(),
                    TCItems.PILLAR_ANCIENT.get(),
                    TCItems.ANCIENT_PEDESTAL.get()
            ));
            case "infusionaltareldritch" -> Optional.of(infusionAltarBlueprint(
                    id,
                    "INFUSIONELDRITCH",
                    stack(TCItems.INFUSION_ALTAR_ELDRITCH_BLUEPRINT.get()),
                    TCItems.STONE_ELDRITCH_TILE.get(),
                    TCItems.PILLAR_ELDRITCH.get(),
                    TCItems.ELDRITCH_PEDESTAL.get()
            ));
            case "thaumatorium" -> Optional.of(blueprint(
                    id,
                    "THAUMATORIUM",
                    stack(TCItems.THAUMATORIUM.get()),
                    List.of(stack(TCItems.METAL_ALCHEMICAL.get(), 2), stack(TCItems.CRUCIBLE.get())),
                    layer(row(source(TCItems.METAL_ALCHEMICAL.get(), TCItems.THAUMATORIUM.get()))),
                    layer(row(source(TCItems.METAL_ALCHEMICAL.get(), TCItems.THAUMATORIUM.get()))),
                    layer(row(source(TCItems.CRUCIBLE.get())))
            ));
            case "golempress" -> Optional.of(blueprint(
                    id,
                    "MINDCLOCKWORK",
                    stack(TCItems.GOLEM_BUILDER.get()),
                    List.of(
                            stack(Blocks.IRON_BARS),
                            stack(Items.CAULDRON),
                            stack(Blocks.PISTON),
                            stack(Blocks.ANVIL),
                            stack(TCItems.TABLE_STONE.get())
                    ),
                    layer(
                            row(emptyCell(), emptyCell()),
                            row(source(Blocks.IRON_BARS), emptyCell())
                    ),
                    layer(
                            row(source(Items.CAULDRON), source(Blocks.ANVIL)),
                            row(source(Blocks.PISTON, TCItems.GOLEM_BUILDER.get()), source(TCItems.TABLE_STONE.get()))
                    )
            ));
            default -> Optional.empty();
        };
    }

    private static TCBlueprintRecipePageView infusionAltarBlueprint(
            ResourceLocation id,
            String research,
            ItemStack displayStack,
            ItemLike stone,
            ItemLike pillar,
            ItemLike pedestal
    ) {
        return blueprint(
                id,
                research,
                displayStack,
                List.of(stack(stone, 8), stack(pedestal), stack(TCItems.INFUSION_MATRIX.get())),
                layer(
                        row(emptyCell(), source(TCItems.INFUSION_MATRIX.get()), emptyCell()),
                        row(emptyCell(), emptyCell(), emptyCell()),
                        row(emptyCell(), emptyCell(), emptyCell())
                ),
                layer(
                        row(source(stone), emptyCell(), source(stone)),
                        row(emptyCell(), emptyCell(), emptyCell()),
                        row(source(stone), emptyCell(), source(stone))
                ),
                layer(
                        row(source(stone, pillar), emptyCell(), source(stone, pillar)),
                        row(emptyCell(), source(pedestal), emptyCell()),
                        row(source(stone, pillar), emptyCell(), source(stone, pillar))
                )
        );
    }

    @SafeVarargs
    private static TCBlueprintRecipePageView blueprint(
            ResourceLocation id,
            String research,
            ItemStack displayStack,
            List<ItemStack> ingredients,
            List<List<TCBlueprintRecipePageView.Cell>>... layers
    ) {
        return new TCBlueprintRecipePageView(id, displayStack, ingredients, List.of(layers), research);
    }

    @SafeVarargs
    private static List<List<TCBlueprintRecipePageView.Cell>> layer(List<TCBlueprintRecipePageView.Cell>... rows) {
        return List.of(rows);
    }

    @SafeVarargs
    private static List<TCBlueprintRecipePageView.Cell> row(TCBlueprintRecipePageView.Cell... cells) {
        return List.of(cells);
    }

    private static TCBlueprintRecipePageView.Cell source(ItemLike source) {
        return source(source, null);
    }

    private static TCBlueprintRecipePageView.Cell source(ItemLike source, ItemLike target) {
        return new TCBlueprintRecipePageView.Cell(stack(source), target == null ? ItemStack.EMPTY : stack(target));
    }

    private static TCBlueprintRecipePageView.Cell emptyCell() {
        return TCBlueprintRecipePageView.Cell.empty();
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
            if (entry.legacySource() == TCResearchPageLegacySource.FAKE_CATALOG) {
                return buildDisplayPage(entry.id()).isPresent()
                        ? TCResearchPageAvailability.READY
                        : TCResearchPageAvailability.DEFERRED;
            }
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
            if (entry.kind() == TCResearchPageKind.BLUEPRINT) {
                return buildBlueprintPage(entry.id()).isPresent()
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
