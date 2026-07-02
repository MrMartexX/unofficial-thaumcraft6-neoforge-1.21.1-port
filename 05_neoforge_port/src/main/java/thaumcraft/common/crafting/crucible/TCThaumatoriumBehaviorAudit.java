package thaumcraft.common.crafting.crucible;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.crafting.TCThaumatoriumBlock;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.TCEssentiaSuction;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;
import thaumcraft.common.essentia.transport.block.TCLegacyTubeVariant;
import thaumcraft.common.essentia.transport.blockentity.TCLegacyTubeBlockEntity;
import thaumcraft.common.registry.TCBlockEntities;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCRecipes;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.tiles.crafting.TCThaumatoriumBlockEntity;
import thaumcraft.common.tiles.crafting.TCThaumatoriumTopBlockEntity;

public final class TCThaumatoriumBehaviorAudit {
    private static final ResourceLocation ALUMENTUM =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "alumentum");

    private TCThaumatoriumBehaviorAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Thaumatorium Behavior Audit");
        lines.add("");
        lines.add("Runtime checks for the first NeoForge Thaumatorium slice against TC6 legacy machine semantics.");
        lines.add("");
        lines.add("## Summary");
        lines.add("");
        lines.add("| Check | Result |");
        lines.add("|---|---:|");
        lines.add("| Passed | " + report.passed() + " |");
        lines.add("| Failed | " + report.failed() + " |");
        lines.add("");
        lines.add("## Checks");
        lines.add("");
        lines.add("| Name | Result | Notes |");
        lines.add("|---|---|---|");
        for (Check check : report.checks()) {
            lines.add("| " + check.name() + " | " + (check.passed() ? "PASS" : "FAIL")
                    + " | " + check.notes().replace("|", "\\|") + " |");
        }
        lines.add("");
        lines.add("## Boundary");
        lines.add("");
        lines.add("- Covers the server-owned two-block machine foundation, not final GUI/screen parity.");
        lines.add("- Uses the loaded `thaumcraft:alumentum` crucible recipe as a stable catalyst/aspect fixture.");
        lines.add("- Verifies legacy-relevant heat, redstone, suction, input-only transport, top delegation and craft completion.");
        lines.add("- Mnemonic Matrix support is currently mapped to the existing `golem_builder` placeholder until the real brain box block is ported.");
        Files.write(output, lines);
        return report;
    }

    private static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = new BlockPos(384, level.getMinBuildHeight() + 18, 384);
        clearArea(level, origin, 8);

        checks.add(check("thaumatorium_block_item_is_real_block_item",
                TCItems.THAUMATORIUM.get().getBlock() == TCBlocks.THAUMATORIUM.get(),
                "item=" + TCItems.THAUMATORIUM.get()));
        checks.add(check("thaumatorium_block_entities_registered",
                TCBlockEntities.THAUMATORIUM.get().isValid(TCBlocks.THAUMATORIUM.get().defaultBlockState())
                        && TCBlockEntities.THAUMATORIUM_TOP.get().isValid(TCBlocks.THAUMATORIUM_TOP.get().defaultBlockState()),
                "bottom/top block entity types valid"));
        checks.add(check("legacy_placeholder_importer_exporter_not_player_facing_blocks",
                !(TCItems.ESSENTIA_IMPORTER.get() instanceof net.minecraft.world.item.BlockItem)
                        && !(TCItems.ESSENTIA_EXPORTER.get() instanceof net.minecraft.world.item.BlockItem),
                "importer/exporter retained as non-block reference aliases"));

        level.setBlock(origin.below(2), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(origin.below(), TCBlocks.CRUCIBLE.get().defaultBlockState(), 3);
        BlockState bottomState = TCBlocks.THAUMATORIUM.get().defaultBlockState()
                .setValue(TCThaumatoriumBlock.FACING, Direction.NORTH);
        BlockState topState = TCBlocks.THAUMATORIUM_TOP.get().defaultBlockState()
                .setValue(TCThaumatoriumBlock.FACING, Direction.NORTH);
        level.setBlock(origin, bottomState, 3);
        level.setBlock(origin.above(), topState, 3);

        TCThaumatoriumBlockEntity thaumatorium = blockEntity(level, origin, TCThaumatoriumBlockEntity.class);
        TCThaumatoriumTopBlockEntity top = blockEntity(level, origin.above(), TCThaumatoriumTopBlockEntity.class);
        checks.add(check("runtime_thaumatorium_block_entities_created",
                thaumatorium != null && top != null && top.bottom() == thaumatorium,
                "bottom=" + (thaumatorium != null) + ", top=" + (top != null)));
        if (thaumatorium == null || top == null) {
            clearArea(level, origin, 8);
            return new Report(List.copyOf(checks));
        }

        checks.add(check("thaumatorium_heat_source_is_two_blocks_below",
                thaumatorium.checkHeat(),
                "heat block=" + level.getBlockState(origin.below(2)).getBlock()));
        level.setBlock(origin.below(2), Blocks.STONE.defaultBlockState(), 3);
        checks.add(check("thaumatorium_without_crucible_heat_source_is_cold",
                !thaumatorium.checkHeat(),
                "heat block=" + level.getBlockState(origin.below(2)).getBlock()));
        level.setBlock(origin.below(2), Blocks.LAVA.defaultBlockState(), 3);

        checks.add(check("thaumatorium_front_face_does_not_connect",
                !thaumatorium.isConnectable(Direction.NORTH)
                        && thaumatorium.isConnectable(Direction.EAST)
                        && thaumatorium.canInputFrom(Direction.UP)
                        && !thaumatorium.canOutputTo(Direction.EAST),
                "front=NORTH, east=" + thaumatorium.isConnectable(Direction.EAST)
                        + ", output=" + thaumatorium.canOutputTo(Direction.EAST)));

        TCEssentiaTransport bottomCapability = level.getCapability(
                TCEssentiaCapabilities.BLOCK,
                origin,
                Direction.EAST
        );
        TCEssentiaTransport frontCapability = level.getCapability(
                TCEssentiaCapabilities.BLOCK,
                origin,
                Direction.NORTH
        );
        TCEssentiaTransport topCapability = level.getCapability(
                TCEssentiaCapabilities.BLOCK,
                origin.above(),
                Direction.EAST
        );
        IItemHandler bottomItems = level.getCapability(Capabilities.ItemHandler.BLOCK, origin, Direction.EAST);
        IItemHandler topItems = level.getCapability(Capabilities.ItemHandler.BLOCK, origin.above(), Direction.EAST);
        checks.add(check("thaumatorium_capabilities_are_sided_and_top_delegates",
                bottomCapability == thaumatorium
                        && frontCapability == null
                        && topCapability == top
                        && bottomItems != null
                        && topItems != null
                        && bottomItems.getSlots() == 1
                        && topItems.getSlots() == 1,
                "bottomTransport=" + (bottomCapability != null)
                        + ", frontTransport=" + (frontCapability != null)
                        + ", topTransport=" + (topCapability != null)
                        + ", itemSlots=" + (bottomItems == null ? -1 : bottomItems.getSlots())
                        + "/" + (topItems == null ? -1 : topItems.getSlots())));

        Optional<RecipeHolder<TCCrucibleRecipe>> alumentum = recipe(level, ALUMENTUM);
        checks.add(check("alumentum_crucible_recipe_loaded_for_fixture",
                alumentum.isPresent(),
                "recipe=" + ALUMENTUM));
        if (alumentum.isEmpty()) {
            clearArea(level, origin, 8);
            return new Report(List.copyOf(checks));
        }

        ServerPlayer player = FakePlayerFactory.getMinecraft(level);
        player.getInventory().clearContent();
        thaumatorium.setCatalystForValidation(new ItemStack(Items.COAL, 1));
        checks.add(check("thaumatorium_recipe_list_is_research_gated",
                thaumatorium.availableRecipes(player).stream().noneMatch(holder -> holder.id().equals(ALUMENTUM)),
                "beforeResearch=" + thaumatorium.availableRecipes(player).size()));
        TCPlayerKnowledgeStore.mutate(player, knowledge -> {
            knowledge.addResearch("ALUMENTUM");
            knowledge.setResearchStage("ALUMENTUM", 99);
        }, false);
        checks.add(check("thaumatorium_recipe_list_exposes_known_catalyst_recipe",
                thaumatorium.availableRecipes(player).stream().anyMatch(holder -> holder.id().equals(ALUMENTUM)),
                "afterResearch=" + thaumatorium.availableRecipes(player).size()));
        boolean selected = thaumatorium.toggleRecipe(player, ALUMENTUM);
        boolean removedByToggle = thaumatorium.toggleRecipe(player, ALUMENTUM);
        checks.add(check("thaumatorium_menu_toggle_selects_and_removes_recipe",
                selected
                        && removedByToggle
                        && thaumatorium.selectedRecipeCount() == 0,
                "selected=" + selected + ", removed=" + removedByToggle
                        + ", count=" + thaumatorium.selectedRecipeCount()));

        thaumatorium.setHeatedForValidation(true);
        thaumatorium.setCatalystForValidation(new ItemStack(Items.COAL, 3));
        thaumatorium.selectRecipeForValidation(ALUMENTUM, "audit");
        tickThaumatorium(level, origin, bottomState, thaumatorium, 5);
        Aspect firstMissing = thaumatorium.currentSuctionAspect();
        TCEssentiaSuction suction = thaumatorium.getSuction(Direction.EAST);
        checks.add(check("thaumatorium_suction_is_128_for_first_missing_aspect",
                firstMissing != null
                        && suction.amount() == TCThaumatoriumBlockEntity.SUCTION_AMOUNT
                        && suction.aspect().equals(firstMissing.getTag()),
                "aspect=" + (firstMissing == null ? "" : firstMissing.getTag())
                        + ", suction=" + suction.amount()));
        checks.add(check("thaumatorium_top_reports_same_suction",
                firstMissing != null
                        && top.getSuction(Direction.EAST).amount() == TCThaumatoriumBlockEntity.SUCTION_AMOUNT
                        && top.getSuction(Direction.EAST).aspect().equals(firstMissing.getTag()),
                "topSuction=" + top.getSuction(Direction.EAST).amount()));

        int accepted = thaumatorium.addEssentia(firstMissing == null ? "" : firstMissing.getTag(), 64, Direction.EAST, false);
        int requiredFirst = amountFor(alumentum.get().value(), firstMissing);
        checks.add(check("thaumatorium_manual_input_clamps_to_current_recipe_missing_amount",
                firstMissing != null
                        && accepted == requiredFirst
                        && thaumatorium.storedAmount(firstMissing) == requiredFirst,
                "accepted=" + accepted + ", required=" + requiredFirst));

        Aspect nextMissing = firstMissingAspect(alumentum.get().value(), thaumatorium.storedEssentia());
        thaumatorium.setCurrentCraftForValidation(0);
        tickThaumatorium(level, origin, bottomState, thaumatorium, 5);
        nextMissing = thaumatorium.currentSuctionAspect();
        BlockPos tubePos = origin.east();
        TCLegacyTubeBlockEntity tube = placeTube(level, tubePos, TCLegacyTubeVariant.TUBE);
        int beforeFill = nextMissing == null ? 0 : thaumatorium.storedAmount(nextMissing);
        if (tube != null && nextMissing != null) {
            tube.addEssentia(nextMissing.getTag(), 1, Direction.EAST, false);
            tickThaumatorium(level, origin, bottomState, thaumatorium, 5);
        }
        checks.add(check("thaumatorium_pulls_one_point_from_adjacent_tube",
                tube != null
                        && nextMissing != null
                        && thaumatorium.storedAmount(nextMissing) == beforeFill + 1
                        && tube.transportNode().storage().totalAmount() == 0,
                "aspect=" + (nextMissing == null ? "" : nextMissing.getTag())
                        + ", before=" + beforeFill
                        + ", after=" + (nextMissing == null ? 0 : thaumatorium.storedAmount(nextMissing))
                        + ", tube=" + (tube == null ? -1 : tube.transportNode().storage().totalAmount())));

        Aspect redstoneAspect = firstMissingAspect(alumentum.get().value(), thaumatorium.storedEssentia());
        int beforePowered = redstoneAspect == null ? 0 : thaumatorium.storedAmount(redstoneAspect);
        level.setBlock(origin.west(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
        if (tube != null && redstoneAspect != null) {
            tube.addEssentia(redstoneAspect.getTag(), 1, Direction.EAST, false);
            tickThaumatorium(level, origin, bottomState, thaumatorium, 5);
        }
        checks.add(check("thaumatorium_redstone_power_pauses_filling",
                redstoneAspect != null && thaumatorium.storedAmount(redstoneAspect) == beforePowered,
                "aspect=" + (redstoneAspect == null ? "" : redstoneAspect.getTag())
                        + ", before=" + beforePowered
                        + ", after=" + (redstoneAspect == null ? 0 : thaumatorium.storedAmount(redstoneAspect))));
        level.removeBlock(origin.west(), false);

        thaumatorium.setStoredEssentiaForValidation(requiredAspects(alumentum.get().value()));
        thaumatorium.setCatalystForValidation(new ItemStack(Items.COAL, 2));
        thaumatorium.selectRecipeForValidation(ALUMENTUM, "audit");
        int entitiesBefore = itemEntityCount(level, origin, 4);
        tickThaumatorium(level, origin, bottomState, thaumatorium, 5);
        int entitiesAfter = itemEntityCount(level, origin, 4);
        checks.add(check("thaumatorium_completion_consumes_one_catalyst_resets_essentia_and_ejects_output",
                thaumatorium.getItem(TCThaumatoriumBlockEntity.SLOT_CATALYST).getCount() == 1
                        && thaumatorium.storedEssentia().visSize() == 0
                        && entitiesAfter > entitiesBefore,
                "catalysts=" + thaumatorium.getItem(TCThaumatoriumBlockEntity.SLOT_CATALYST).getCount()
                        + ", essentia=" + thaumatorium.storedEssentia().visSize()
                        + ", itemEntities=" + entitiesBefore + ">" + entitiesAfter));

        thaumatorium.setCatalystForValidation(new ItemStack(Items.COAL, 1));
        thaumatorium.selectRecipeForValidation(ALUMENTUM, "audit");
        int baseCapacity = thaumatorium.maxRecipes();
        level.setBlock(origin.south(), TCBlocks.GOLEM_BUILDER.get().defaultBlockState(), 3);
        tickThaumatorium(level, origin, bottomState, thaumatorium, 40);
        checks.add(check("thaumatorium_mnemonic_matrix_placeholder_adds_two_recipe_slots",
                baseCapacity == TCThaumatoriumBlockEntity.BASE_MAX_RECIPES
                        && thaumatorium.maxRecipes() == TCThaumatoriumBlockEntity.BASE_MAX_RECIPES
                        + TCThaumatoriumBlockEntity.MNEMONIC_MATRIX_BONUS,
                "base=" + baseCapacity + ", upgraded=" + thaumatorium.maxRecipes()));

        clearArea(level, origin, 8);
        return new Report(List.copyOf(checks));
    }

    private static Optional<RecipeHolder<TCCrucibleRecipe>> recipe(ServerLevel level, ResourceLocation id) {
        for (RecipeHolder<TCCrucibleRecipe> holder : level.getRecipeManager().getAllRecipesFor(TCRecipes.CRUCIBLE_TYPE.get())) {
            if (holder.id().equals(id)) {
                return Optional.of(holder);
            }
        }
        return Optional.empty();
    }

    private static AspectList requiredAspects(TCCrucibleRecipe recipe) {
        AspectList list = new AspectList();
        for (TCCrucibleAspectCost cost : recipe.aspectCosts()) {
            list.add(cost.resolvedAspect(), cost.amount());
        }
        return list;
    }

    private static Aspect firstMissingAspect(TCCrucibleRecipe recipe, AspectList stored) {
        AspectList required = requiredAspects(recipe);
        for (Aspect aspect : required.getAspectsSortedByName()) {
            if (stored.getAmount(aspect) < required.getAmount(aspect)) {
                return aspect;
            }
        }
        return null;
    }

    private static int amountFor(TCCrucibleRecipe recipe, Aspect aspect) {
        if (aspect == null) {
            return 0;
        }
        for (TCCrucibleAspectCost cost : recipe.aspectCosts()) {
            if (cost.resolvedAspect() == aspect) {
                return cost.amount();
            }
        }
        return 0;
    }

    private static void tickThaumatorium(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            TCThaumatoriumBlockEntity thaumatorium,
            int ticks
    ) {
        for (int index = 0; index < ticks; index++) {
            TCThaumatoriumBlockEntity.serverTick(level, pos, state, thaumatorium);
        }
    }

    private static int itemEntityCount(ServerLevel level, BlockPos origin, int radius) {
        return level.getEntitiesOfClass(
                ItemEntity.class,
                new net.minecraft.world.phys.AABB(origin).inflate(radius)
        ).size();
    }

    @Nullable
    private static TCLegacyTubeBlockEntity placeTube(ServerLevel level, BlockPos pos, TCLegacyTubeVariant variant) {
        level.setBlock(pos, blockForTube(variant).defaultBlockState(), 3);
        return blockEntity(level, pos, TCLegacyTubeBlockEntity.class);
    }

    private static net.minecraft.world.level.block.Block blockForTube(TCLegacyTubeVariant variant) {
        return switch (variant) {
            case TUBE -> TCBlocks.TUBE.get();
            case BUFFER -> TCBlocks.TUBE_BUFFER.get();
            case FILTER -> TCBlocks.TUBE_FILTER.get();
            case ONEWAY -> TCBlocks.TUBE_ONEWAY.get();
            case RESTRICT -> TCBlocks.TUBE_RESTRICT.get();
            case VALVE -> TCBlocks.TUBE_VALVE.get();
        };
    }

    @Nullable
    private static <T> T blockEntity(ServerLevel level, BlockPos pos, Class<T> type) {
        Object blockEntity = level.getBlockEntity(pos);
        return type.isInstance(blockEntity) ? type.cast(blockEntity) : null;
    }

    private static void clearArea(ServerLevel level, BlockPos origin, int radius) {
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-radius, -4, -radius), origin.offset(radius, 5, radius))) {
            level.removeBlock(pos, false);
        }
    }

    private static Check check(String name, boolean passed, String notes) {
        return new Check(name, passed, notes == null ? "" : notes);
    }

    public record Report(List<Check> checks) {
        public long passed() {
            return checks.stream().filter(Check::passed).count();
        }

        public long failed() {
            return checks.size() - passed();
        }
    }

    public record Check(String name, boolean passed, String notes) {
    }
}
