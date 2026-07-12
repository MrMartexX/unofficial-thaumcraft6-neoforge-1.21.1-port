package thaumcraft.common.crafting.crucible;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.TCBottleTaintEntity;
import thaumcraft.common.entities.TCTaintCrawlerEntity;
import thaumcraft.common.items.consumables.ItemBathSalts;
import thaumcraft.common.items.consumables.ItemBottleTaint;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMobEffects;

/** Runtime audit for special crucible output behavior that is not owned by the crucible tile itself. */
public final class TCSpecialAlchemyBehaviorAudit {
    public static final String ENABLE_PROPERTY = "tc.specialAlchemyBehaviorAudit";
    public static final String OUTPUT_PROPERTY = "tc.specialAlchemyBehaviorAuditPath";

    private TCSpecialAlchemyBehaviorAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        Files.createDirectories(output.toAbsolutePath().normalize().getParent());
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Special Alchemy Behavior Audit");
        lines.add("");
        lines.add("Runtime checks for TC6 special crucible outputs whose gameplay lives on the produced item/entity rather than in `TileCrucible`.");
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
            lines.add("| " + check.name() + " | " + (check.passed() ? "PASS" : "FAIL") + " | " + escape(check.notes()) + " |");
        }
        lines.add("");
        lines.add("## Boundary");
        lines.add("");
        lines.add("- Implemented in this slice: `bath_salts` legacy dropped-item lifespan, `bottle_taint` stack size/use constants, `bottle_taint` projectile registration, Flux Taint splash predicate/effect and Flux Goo placement support rules.");
        lines.add("- Already data-backed before this slice: special crucible recipes for BottleTaint, BathSalts, LiquidDeath and SaneSoap.");
        lines.add("- Deferred to a later fluid blocker: real `liquid_death` and `purifying_fluid` blocks/fluids, Bath Salts water-source conversion and Warp Ward effect behavior.");
        Files.write(output, lines);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = level.getSharedSpawnPos().offset(352, 6, 352);
        cleanup(level, origin);
        addItemChecks(level, checks);
        addProjectileChecks(level, origin, checks);
        addRecipeChecks(level, checks);
        addBoundaryChecks(checks);
        cleanup(level, origin);
        return new Report(List.copyOf(checks));
    }

    private static void addItemChecks(ServerLevel level, ArrayList<Check> checks) {
        ItemStack bathSalts = new ItemStack(TCItems.BATH_SALTS.get());
        ItemStack bottleTaint = new ItemStack(TCItems.BOTTLE_TAINT.get());
        checks.add(check("bath_salts_item_registered_with_legacy_id_and_lifespan",
                itemId(TCItems.BATH_SALTS.get()).equals(id("bath_salts"))
                        && TCItems.BATH_SALTS.get() instanceof ItemBathSalts
                        && bathSalts.getEntityLifespan(level) == ItemBathSalts.LEGACY_ENTITY_LIFESPAN,
                "item=" + itemId(TCItems.BATH_SALTS.get())
                        + ", lifespan=" + bathSalts.getEntityLifespan(level)));
        checks.add(check("bottle_taint_item_registered_with_legacy_id_and_stack_size",
                itemId(TCItems.BOTTLE_TAINT.get()).equals(id("bottle_taint"))
                        && TCItems.BOTTLE_TAINT.get() instanceof ItemBottleTaint
                        && bottleTaint.getMaxStackSize() == ItemBottleTaint.LEGACY_MAX_STACK_SIZE,
                "item=" + itemId(TCItems.BOTTLE_TAINT.get())
                        + ", maxStack=" + bottleTaint.getMaxStackSize()));
        checks.add(check("bottle_taint_throw_constants_match_legacy",
                close(ItemBottleTaint.LEGACY_THROW_VELOCITY, 0.66F)
                        && close(ItemBottleTaint.LEGACY_THROW_INACCURACY, 1.0F)
                        && close(ItemBottleTaint.LEGACY_THROW_X_ROT_OFFSET, -5.0F),
                "velocity=" + ItemBottleTaint.LEGACY_THROW_VELOCITY
                        + ", inaccuracy=" + ItemBottleTaint.LEGACY_THROW_INACCURACY
                        + ", xRotOffset=" + ItemBottleTaint.LEGACY_THROW_X_ROT_OFFSET));
    }

    private static void addProjectileChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        checks.add(check("bottle_taint_entity_registered_with_legacy_tracking",
                entityId(TCEntityTypes.BOTTLE_TAINT.get()).equals(id("bottle_taint"))
                        && TCEntityTypes.byLegacyId("BottleTaint")
                                .map(spec -> "bottle_taint".equals(spec.modernId())
                                        && spec.isRegisteredFoundation()
                                        && spec.trackingRange() == 64
                                        && spec.updateInterval() == 20
                                        && spec.velocityUpdates())
                                .orElse(false)
                        && TCEntityTypes.BOTTLE_TAINT.get().getCategory() == net.minecraft.world.entity.MobCategory.MISC
                        && close(TCEntityTypes.BOTTLE_TAINT.get().getWidth(), 0.25F)
                        && close(TCEntityTypes.BOTTLE_TAINT.get().getHeight(), 0.25F),
                "entity=" + entityId(TCEntityTypes.BOTTLE_TAINT.get())
                        + ", tracking=" + TCEntityTypes.BOTTLE_TAINT.get().clientTrackingRange()
                        + ", update=" + TCEntityTypes.BOTTLE_TAINT.get().updateInterval()
                        + ", velocity=" + TCEntityTypes.BOTTLE_TAINT.get().trackDeltas()));

        TCBottleTaintEntity entity = new TCBottleTaintEntity(level, origin.getX() + 0.5D, origin.getY() + 1.0D, origin.getZ() + 0.5D);
        entity.setItem(new ItemStack(TCItems.BOTTLE_TAINT.get()));
        checks.add(check("bottle_taint_projectile_default_stack_contract",
                entity.getType() == TCEntityTypes.BOTTLE_TAINT.get()
                        && entity.getItem().is(TCItems.BOTTLE_TAINT.get()),
                "type=" + entityId(entity.getType()) + ", stack=" + itemId(entity.getItem().getItem())));

        var fakePlayer = FakePlayerFactory.getMinecraft(level);
        Zombie zombie = new Zombie(EntityType.ZOMBIE, level);
        TCTaintCrawlerEntity crawler = new TCTaintCrawlerEntity(TCEntityTypes.TAINT_CRAWLER.get(), level);
        boolean appliedToPlayer = TCBottleTaintEntity.applyFluxTaintTo(fakePlayer);
        boolean appliedToUndead = TCBottleTaintEntity.applyFluxTaintTo(zombie);
        boolean appliedToTainted = TCBottleTaintEntity.applyFluxTaintTo(crawler);
        MobEffectInstance playerEffect = fakePlayer.getEffect(TCMobEffects.FLUX_TAINT);
        checks.add(check("bottle_taint_flux_taint_predicate_matches_legacy",
                appliedToPlayer
                        && !appliedToUndead
                        && !appliedToTainted
                        && playerEffect != null
                        && playerEffect.getDuration() == TCBottleTaintEntity.LEGACY_FLUX_TAINT_DURATION
                        && playerEffect.getAmplifier() == TCBottleTaintEntity.LEGACY_FLUX_TAINT_AMPLIFIER,
                "playerApplied=" + appliedToPlayer
                        + ", undeadApplied=" + appliedToUndead
                        + ", taintedApplied=" + appliedToTainted
                        + ", duration=" + (playerEffect == null ? -1 : playerEffect.getDuration())));

        BlockPos supported = origin.offset(2, 0, 0);
        level.setBlock(supported.below(), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(supported, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        boolean placedSupported = TCBottleTaintEntity.tryPlaceFluxGoo(level, supported);
        checks.add(check("bottle_taint_places_flux_goo_on_supported_replaceable_target",
                placedSupported && level.getBlockState(supported).is(TCBlocks.FLUX_GOO.get()),
                "placed=" + placedSupported + ", block=" + blockId(level.getBlockState(supported).getBlock())));

        BlockPos unsupported = origin.offset(4, 0, 0);
        level.setBlock(unsupported.below(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(unsupported, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        boolean placedUnsupported = TCBottleTaintEntity.tryPlaceFluxGoo(level, unsupported);
        BlockPos solidTarget = origin.offset(6, 0, 0);
        level.setBlock(solidTarget.below(), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(solidTarget, Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
        boolean placedSolid = TCBottleTaintEntity.tryPlaceFluxGoo(level, solidTarget);
        checks.add(check("bottle_taint_rejects_unsupported_or_nonreplaceable_goo_targets",
                !placedUnsupported
                        && !placedSolid
                        && level.getBlockState(unsupported).isAir()
                        && level.getBlockState(solidTarget).is(Blocks.STONE),
                "unsupported=" + placedUnsupported + ", solid=" + placedSolid));

        BlockPos fallbackTop = origin.offset(8, 1, 0);
        level.setBlock(fallbackTop.below(2), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(fallbackTop.below(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(fallbackTop, Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
        boolean fallbackPlaced = TCBottleTaintEntity.tryPlaceFluxGooWithLegacyFallback(level, fallbackTop);
        checks.add(check("bottle_taint_one_block_down_fallback_matches_legacy",
                fallbackPlaced && level.getBlockState(fallbackTop.below()).is(TCBlocks.FLUX_GOO.get()),
                "fallbackPlaced=" + fallbackPlaced + ", fallbackBlock=" + blockId(level.getBlockState(fallbackTop.below()).getBlock())));
    }

    private static void addRecipeChecks(ServerLevel level, ArrayList<Check> checks) {
        checks.add(checkRecipe(level, "bottletaint", "BOTTLETAINT", TCItems.BOTTLE_TAINT.get(),
                List.of(cost(Aspect.FLUX, 30), cost(Aspect.WATER, 30))));
        checks.add(checkRecipe(level, "bathsalts", "BATHSALTS", TCItems.BATH_SALTS.get(),
                List.of(cost(Aspect.MIND, 40), cost(Aspect.AIR, 40), cost(Aspect.ORDER, 40), cost(Aspect.LIFE, 40))));
        checks.add(checkRecipe(level, "liquiddeath", "LIQUIDDEATH", TCItems.LIQUID_DEATH_BUCKET.get(),
                List.of(cost(Aspect.DEATH, 100), cost(Aspect.ALCHEMY, 20), cost(Aspect.ENTROPY, 50))));
    }

    private static void addBoundaryChecks(ArrayList<Check> checks) {
        boolean liquidDeathBlockRegistered = BuiltInRegistries.BLOCK.containsKey(id("liquid_death"));
        boolean purifyingFluidBlockRegistered = BuiltInRegistries.BLOCK.containsKey(id("purifying_fluid"));
        boolean warpWardRegistered = BuiltInRegistries.MOB_EFFECT.keySet().stream()
                .anyMatch(location -> Thaumcraft.MODID.equals(location.getNamespace())
                        && ("warp_ward".equals(location.getPath()) || "warpward".equals(location.getPath())));
        checks.add(check("fluid_specials_remain_explicit_blockers_not_fake_placeholders",
                !liquidDeathBlockRegistered && !purifyingFluidBlockRegistered && !warpWardRegistered,
                "liquidDeathBlock=" + liquidDeathBlockRegistered
                        + ", purifyingFluidBlock=" + purifyingFluidBlockRegistered
                        + ", warpWard=" + warpWardRegistered));
    }

    private static Check checkRecipe(ServerLevel level, String path, String research, Item resultItem, List<TCCrucibleAspectCost> expectedCosts) {
        ResourceLocation recipeId = id(path);
        RecipeHolder<?> holder = level.getRecipeManager().byKey(recipeId).orElse(null);
        if (holder == null || !(holder.value() instanceof TCCrucibleRecipe recipe)) {
            return check("special_alchemy_recipe_" + path, false, "missing crucible recipe " + recipeId);
        }
        boolean resultMatches = recipe.result().is(resultItem);
        boolean researchMatches = research.equals(recipe.getResearch());
        boolean aspectsMatch = recipe.aspectCosts().equals(expectedCosts);
        return check("special_alchemy_recipe_" + path,
                resultMatches && researchMatches && aspectsMatch,
                "result=" + itemId(recipe.result().getItem())
                        + ", research=" + recipe.getResearch()
                        + ", aspects=" + recipe.aspectCosts());
    }

    private static TCCrucibleAspectCost cost(Aspect aspect, int amount) {
        return new TCCrucibleAspectCost(aspect.getTag(), amount);
    }

    private static void cleanup(ServerLevel level, BlockPos origin) {
        BlockPos.betweenClosed(origin.offset(-4, -3, -4), origin.offset(16, 4, 4)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path);
    }

    private static ResourceLocation itemId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    private static ResourceLocation blockId(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private static ResourceLocation entityId(EntityType<?> type) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(type);
    }

    private static boolean close(float actual, float expected) {
        return Math.abs(actual - expected) < 0.0001F;
    }

    private static String escape(String value) {
        return value.replace("|", "\\|");
    }

    private static Check check(String name, boolean passed, String notes) {
        return new Check(name, passed, notes);
    }

    public record Report(List<Check> checks) {
        public int passed() {
            return (int) checks.stream().filter(Check::passed).count();
        }

        public int failed() {
            return checks.size() - passed();
        }
    }

    public record Check(String name, boolean passed, String notes) {
    }
}
