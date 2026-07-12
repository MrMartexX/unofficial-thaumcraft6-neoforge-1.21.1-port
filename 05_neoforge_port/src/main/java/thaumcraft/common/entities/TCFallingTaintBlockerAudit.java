package thaumcraft.common.entities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.world.taint.TCFluxGooBlock;
import thaumcraft.common.blocks.world.taint.TCTaintTerrainBlock;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCSounds;

/** Runtime audit for the focused TC6 FallingTaint/crusted-taint physics slice. */
public final class TCFallingTaintBlockerAudit {
    public static final String ENABLE_PROPERTY = "tc.fallingTaintBlockerAudit";
    public static final String OUTPUT_PROPERTY = "tc.fallingTaintBlockerAuditPath";

    private TCFallingTaintBlockerAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        if (output.getParent() != null) {
            Files.createDirectories(output.getParent());
        }
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# FallingTaint Blocker Audit");
        lines.add("");
        lines.add("Runtime checks for the row-11/14 slice that ports TC6 `EntityFallingTaint` and");
        lines.add("the crusted-taint falling rules from legacy `BlockTaint`.");
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
        lines.add("- Implemented: `falling_taint` entity registration with TC6 tracking `64`, update interval `3` and velocity updates enabled.");
        lines.add("- Implemented: crusted-taint `tryToFall` server path, source-block removal on first tick, TC6 gravity/damping constants, landing placement over solid support and timeout discard.");
        lines.add("- Implemented: TC6 `canFallBelow` blockers for nearby logs, flux-goo level threshold, taint fibre, replaceable blocks, water and lava.");
        lines.add("- Implemented: GORE sound registration for the landing path and a block-model renderer foundation for the falling crust.");
        lines.add("- Deferred: exact landing particles, measured renderer pixel parity and broad natural taint-spawn placement tables.");
        Files.write(output, lines, StandardCharsets.UTF_8);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = level.getSharedSpawnPos().offset(16, 24, 16);
        cleanup(level, origin);
        addRegistrationChecks(checks);
        addCanFallBelowChecks(level, origin, checks);
        addSpawnAndTickChecks(level, origin.offset(16, 0, 0), checks);
        addOverhangFallChecks(level, origin.offset(28, 0, 0), checks);
        addNbtChecks(level, origin.offset(32, 0, 0), checks);
        cleanup(level, origin);
        return new Report(List.copyOf(checks));
    }

    private static void addRegistrationChecks(ArrayList<Check> checks) {
        EntityType<TCFallingTaintEntity> type = TCEntityTypes.FALLING_TAINT.get();
        checks.add(check("falling_taint_entity_registered_with_legacy_tracking",
                BuiltInRegistries.ENTITY_TYPE.getKey(type).equals(id("falling_taint"))
                        && type.clientTrackingRange() == 64
                        && type.updateInterval() == 3
                        && type.trackDeltas()
                        && Float.compare(type.getWidth(), 0.98F) == 0
                        && Float.compare(type.getHeight(), 0.98F) == 0,
                "entity=" + BuiltInRegistries.ENTITY_TYPE.getKey(type)
                        + ", size=" + type.getWidth() + "x" + type.getHeight()
                        + ", tracking=" + type.clientTrackingRange()
                        + ", update=" + type.updateInterval()
                        + ", velocity=" + type.trackDeltas()));
        checks.add(check("falling_taint_legacy_catalog_registered",
                TCEntityTypes.byLegacyId("FallingTaint")
                        .map(spec -> "falling_taint".equals(spec.modernId()) && spec.isRegisteredFoundation())
                        .orElse(false),
                "catalogStatus=" + TCEntityTypes.byLegacyId("FallingTaint")
                        .map(TCEntityTypes.LegacyEntitySpec::status)
                        .orElse("missing")));
        checks.add(check("gore_sound_registered_for_landing_path",
                BuiltInRegistries.SOUND_EVENT.getKey(TCSounds.GORE.get()).equals(id("gore")),
                "sound=" + BuiltInRegistries.SOUND_EVENT.getKey(TCSounds.GORE.get())));
    }

    private static void addCanFallBelowChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        BlockPos air = origin;
        BlockPos fibre = origin.offset(4, 0, 0);
        BlockPos lowGoo = origin.offset(8, 0, 0);
        BlockPos highGoo = origin.offset(12, 0, 0);
        BlockPos water = origin.offset(16, 0, 0);
        BlockPos blockedByLog = origin.offset(20, 0, 0);
        set(level, air, Blocks.AIR.defaultBlockState());
        set(level, fibre, TCBlocks.TAINT_FIBRE.get().defaultBlockState());
        set(level, lowGoo, TCBlocks.FLUX_GOO.get().defaultBlockState().setValue(TCFluxGooBlock.LEVEL, 3));
        set(level, highGoo, TCBlocks.FLUX_GOO.get().defaultBlockState().setValue(TCFluxGooBlock.LEVEL, 4));
        set(level, water, Blocks.WATER.defaultBlockState());
        set(level, blockedByLog, Blocks.AIR.defaultBlockState());
        set(level, blockedByLog.north(), Blocks.OAK_LOG.defaultBlockState());

        checks.add(check("can_fall_below_accepts_legacy_replaceable_targets",
                TCTaintTerrainBlock.canFallBelow(level, air)
                        && TCTaintTerrainBlock.canFallBelow(level, fibre)
                        && TCTaintTerrainBlock.canFallBelow(level, lowGoo)
                        && TCTaintTerrainBlock.canFallBelow(level, water),
                "air/fibre/low-goo/water accepted"));
        checks.add(check("can_fall_below_rejects_high_flux_goo_and_nearby_logs",
                !TCTaintTerrainBlock.canFallBelow(level, highGoo)
                        && !TCTaintTerrainBlock.canFallBelow(level, blockedByLog),
                "highGoo=" + TCTaintTerrainBlock.canFallBelow(level, highGoo)
                        + ", nearbyLog=" + TCTaintTerrainBlock.canFallBelow(level, blockedByLog)));
    }

    private static void addSpawnAndTickChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        TCTaintTerrainBlock crust = (TCTaintTerrainBlock) TCBlocks.TAINT_CRUST.get();
        BlockPos source = origin.above(5);
        set(level, source, TCBlocks.TAINT_CRUST.get().defaultBlockState());
        for (int depth = 1; depth <= 4; depth++) {
            set(level, source.below(depth), Blocks.AIR.defaultBlockState());
        }
        set(level, source.below(5), Blocks.STONE.defaultBlockState());
        setForced(level, source, 32, true);

        boolean spawned = crust.tryToFallForValidation(level, source, source);
        List<TCFallingTaintEntity> spawnedEntities =
                level.getEntitiesOfClass(TCFallingTaintEntity.class, new AABB(source).inflate(4.0D));
        TCFallingTaintEntity entity = spawnedEntities.isEmpty() ? new TCFallingTaintEntity(
                level,
                source.getX() + 0.5D,
                source.getY() + 0.5D,
                source.getZ() + 0.5D,
                level.getBlockState(source),
                source
        ) : spawnedEntities.get(0);
        checks.add(check("crusted_taint_try_to_fall_accepts_and_preserves_source_until_entity_tick",
                spawned
                        && level.getBlockState(source).is(TCBlocks.TAINT_CRUST.get())
                        && spawnedEntities.size() == 1
                        && entity.getFallState().is(TCBlocks.TAINT_CRUST.get())
                        && entity.oldPos().equals(source),
                "spawned=" + spawned
                        + ", entities=" + spawnedEntities.size()
                        + ", source=" + blockId(level.getBlockState(source).getBlock())
                        + ", entityOld=" + entity.oldPos()));
        setForced(level, source, 32, false);
        entity.tickForValidation();
        checks.add(check("falling_taint_first_tick_removes_original_crust",
                level.getBlockState(source).isAir() && entity.fallTime() == 1,
                "source=" + blockId(level.getBlockState(source).getBlock()) + ", fallTime=" + entity.fallTime()));
        for (int index = 0; index < 160 && entity.isAlive(); index++) {
            entity.tickForValidation();
        }
        BlockPos landing = source.below(4);
        checks.add(check("falling_taint_lands_as_crust_above_solid_support",
                !entity.isAlive() && level.getBlockState(landing).is(TCBlocks.TAINT_CRUST.get()),
                "alive=" + entity.isAlive()
                        + ", landing=" + blockId(level.getBlockState(landing).getBlock())
                        + ", y=" + entity.getY()));
    }

    private static void addOverhangFallChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        TCTaintTerrainBlock crust = (TCTaintTerrainBlock) TCBlocks.TAINT_CRUST.get();
        Direction direction = Direction.EAST;
        BlockPos source = origin.above(6);
        BlockPos target = source.relative(direction);
        set(level, source, TCBlocks.TAINT_CRUST.get().defaultBlockState());
        set(level, source.above(), Blocks.AIR.defaultBlockState());
        set(level, target, Blocks.AIR.defaultBlockState());
        for (int depth = 1; depth < 4; depth++) {
            set(level, source.below(depth), TCBlocks.TAINT_CRUST.get().defaultBlockState());
            set(level, target.below(depth), Blocks.AIR.defaultBlockState());
        }
        setForced(level, source, 32, true);
        boolean spawned = crust.tryOverhangFallForValidation(level, source, direction);
        List<TCFallingTaintEntity> spawnedEntities =
                level.getEntitiesOfClass(TCFallingTaintEntity.class, new AABB(target).inflate(4.0D));
        TCFallingTaintEntity entity = spawnedEntities.isEmpty() ? null : spawnedEntities.get(0);
        checks.add(check("crusted_taint_overhang_fall_uses_side_target_and_original_source",
                spawned
                        && spawnedEntities.size() == 1
                        && level.getBlockState(source).is(TCBlocks.TAINT_CRUST.get())
                        && entity != null
                        && entity.blockPosition().equals(target)
                        && entity.oldPos().equals(source),
                "spawned=" + spawned
                        + ", entities=" + spawnedEntities.size()
                        + ", source=" + blockId(level.getBlockState(source).getBlock())
                        + ", target=" + target
                        + ", entityPos=" + (entity == null ? "none" : entity.blockPosition())
                        + ", entityOld=" + (entity == null ? "none" : entity.oldPos())));
        spawnedEntities.forEach(TCFallingTaintEntity::discard);
        setForced(level, source, 32, false);
    }

    private static void addNbtChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        TCFallingTaintEntity entity = new TCFallingTaintEntity(
                level,
                origin.getX() + 0.5D,
                origin.getY() + 0.5D,
                origin.getZ() + 0.5D,
                TCBlocks.TAINT_CRUST.get().defaultBlockState(),
                origin
        );
        entity.tickForValidation();
        CompoundTag tag = new CompoundTag();
        entity.addAdditionalSaveData(tag);
        TCFallingTaintEntity restored = new TCFallingTaintEntity(TCEntityTypes.FALLING_TAINT.get(), level);
        restored.readAdditionalSaveData(tag);
        checks.add(check("falling_taint_nbt_round_trip_preserves_legacy_fields",
                restored.getFallState().is(TCBlocks.TAINT_CRUST.get())
                        && restored.oldPos().equals(origin)
                        && restored.fallTime() == entity.fallTime()
                        && restored.fallHurtMax() == 40
                        && Float.compare(restored.fallHurtAmount(), 2.0F) == 0,
                "block=" + blockId(restored.getFallState().getBlock())
                        + ", old=" + restored.oldPos()
                        + ", time=" + restored.fallTime()
                        + ", hurt=" + restored.fallHurtAmount() + "/" + restored.fallHurtMax()));
    }

    private static void cleanup(ServerLevel level, BlockPos origin) {
        level.getEntitiesOfClass(TCFallingTaintEntity.class, new AABB(origin).inflate(96.0D)).forEach(TCFallingTaintEntity::discard);
        BlockPos.betweenClosed(origin.offset(-8, -8, -8), origin.offset(48, 16, 48)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
    }

    private static void setForced(ServerLevel level, BlockPos center, int radius, boolean forced) {
        int minChunkX = (center.getX() - radius) >> 4;
        int maxChunkX = (center.getX() + radius) >> 4;
        int minChunkZ = (center.getZ() - radius) >> 4;
        int maxChunkZ = (center.getZ() + radius) >> 4;
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                level.setChunkForced(chunkX, chunkZ, forced);
                if (forced) {
                    level.getChunk(chunkX, chunkZ);
                }
            }
        }
    }

    private static void set(ServerLevel level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, Block.UPDATE_ALL);
    }

    private static ResourceLocation blockId(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path);
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
