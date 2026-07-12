package thaumcraft.common.entities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.aspects.TCEntityAspectAssignments;
import thaumcraft.common.registry.TCEntityTypes;

public final class TCWispBehaviorAudit {
    public static final String ENABLE_PROPERTY = "tc.wispBehaviorAudit";
    public static final String OUTPUT_PROPERTY = "tc.wispBehaviorAuditPath";

    private TCWispBehaviorAudit() {
    }

    public static Report writeMarkdown(Path output, MinecraftServer server) throws IOException {
        if (output.getParent() != null) {
            Files.createDirectories(output.getParent());
        }
        Report report = run(server);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("# Wisp Behavior and Render Contract Audit");
        lines.add("");
        lines.add("Runtime checks for the TC6 Wisp slice after Flux Rift event 0 began spawning real Wisps.");
        lines.add("This covers server AI constants, type/aspect persistence, targeting, zap cadence and the");
        lines.add("legacy billboard render contract and the modern source/target-id equivalent of PacketFXWispZap.");
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
        lines.add("- Implemented: legacy Wisp type persistence, aspect crystal drop, peaceful despawn, hurt aggro cooldown, free-flight waypoint AI, chase motion, zap attack cadence/sound/damage, dynamic type aspects and source/target-id zap payload.");
        lines.add("- Implemented: client renderer registration uses TC6 billboard frame indices from `particles.png` and `auranodes.png` with fullbright additive blending; ambient Wisp motes use the legacy `drawWispParticles` particle parameters.");
        lines.add("- Implemented with modern renderer constraints: Wisp zap bolt uses the same TC6 source/target id packet contract, color extraction and short-lived bolt point math, but renders through PoseStack/BufferBuilder instead of legacy CoreGLE.");
        Files.write(output, lines, StandardCharsets.UTF_8);
        return report;
    }

    public static Report run(MinecraftServer server) {
        ArrayList<Check> checks = new ArrayList<>();
        ServerLevel level = server.overworld();
        BlockPos origin = level.getSharedSpawnPos().offset(288, 12, 288);
        Difficulty originalDifficulty = level.getDifficulty();
        boolean originalMobGriefing = level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        cleanup(level, origin);
        prepareAirBox(level, origin);
        try {
            server.setDifficulty(Difficulty.NORMAL, true);
            level.getGameRules().getRule(GameRules.RULE_MOBGRIEFING).set(false, server);
            addRegistrationChecks(level, checks);
            addStateAspectAndScanChecks(level, checks);
        addBehaviorChecks(level, origin, checks);
        addRenderContractChecks(checks);
        } finally {
            server.setDifficulty(originalDifficulty, true);
            level.getGameRules().getRule(GameRules.RULE_MOBGRIEFING).set(originalMobGriefing, server);
            cleanup(level, origin);
        }
        return new Report(List.copyOf(checks));
    }

    private static void addRegistrationChecks(ServerLevel level, ArrayList<Check> checks) {
        TCWispEntity wisp = TCEntityTypes.WISP.get().create(level);
        checks.add(check("wisp_type_registered_with_legacy_tracking",
                entityId(TCEntityTypes.WISP.get()).equals(id("wisp"))
                        && legacySpecRegistered("Wisp", "wisp", 64, 3, false)
                        && typeShape(TCEntityTypes.WISP.get(), MobCategory.MONSTER, 0.9F, 0.9F, 64, 3, false),
                "entity=" + entityId(TCEntityTypes.WISP.get())));
        checks.add(check("wisp_attributes_match_legacy",
                wisp != null
                        && close(wisp.getAttributeBaseValue(Attributes.MAX_HEALTH), 22.0D)
                        && close(wisp.getAttributeBaseValue(Attributes.ATTACK_DAMAGE), 3.0D)
                        && close(wisp.getAttributeBaseValue(Attributes.MOVEMENT_SPEED), 0.15D)
                        && close(wisp.getAttributeBaseValue(Attributes.FLYING_SPEED), 0.15D)
                        && close(wisp.getAttributeBaseValue(Attributes.FOLLOW_RANGE), 16.0D),
                "health=22, attack=3, move/fly=0.15, follow=16"));
    }

    private static void addStateAspectAndScanChecks(ServerLevel level, ArrayList<Check> checks) {
        TCWispEntity wisp = new TCWispEntity(level, 0.0D, 80.0D, 0.0D);
        wisp.setWispType(Aspect.FIRE.getTag());
        CompoundTag tag = new CompoundTag();
        wisp.addAdditionalSaveData(tag);
        TCWispEntity restored = new TCWispEntity(level, 0.0D, 80.0D, 0.0D);
        restored.readAdditionalSaveData(tag);
        checks.add(check("wisp_type_nbt_roundtrip_matches_legacy_key",
                Aspect.FIRE.getTag().equals(restored.getWispType()) && Aspect.FIRE.getTag().equals(tag.getString("Type")),
                "tag.Type=" + tag.getString("Type")));

        wisp.setWispType(Aspect.FLUX.getTag());
        checks.add(check("wisp_dynamic_entity_aspects_match_legacy",
                hasAspectList(TCEntityAspectAssignments.getEntityAspects(wisp), Aspect.FLUX, 5, Aspect.AURA, 5, Aspect.FLIGHT, 5),
                "type=" + wisp.getWispType()));

        String scannables = resourceText("data/thaumcraft/scannables/legacy_core.json");
        checks.add(check("wisp_scan_keys_match_legacy",
                scannables.contains("\"!Wisp\"")
                        && scannables.contains("\"f_FLY\"")
                        && scannables.contains("\"thaumcraft:wisp\""),
                "legacy_core.json contains !Wisp and f_FLY mappings"));
    }

    private static void addBehaviorChecks(ServerLevel level, BlockPos origin, ArrayList<Check> checks) {
        TCWispEntity aggroWisp = new TCWispEntity(level, origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D);
        Zombie attacker = EntityType.ZOMBIE.create(level);
        if (attacker != null) {
            attacker.moveTo(origin.getX() + 2.5D, origin.getY(), origin.getZ() + 0.5D);
            level.addFreshEntity(attacker);
        }
        level.addFreshEntity(aggroWisp);
        boolean hurt = attacker != null && aggroWisp.hurt(level.damageSources().mobAttack(attacker), 1.0F);
        checks.add(check("wisp_hurt_sets_target_and_legacy_aggro_cooldown",
                hurt
                        && aggroWisp.getTarget() == attacker
                        && aggroWisp.getAggroCooldownForValidation() == 200,
                "target=" + (aggroWisp.getTarget() == null ? "null" : aggroWisp.getTarget().getType()) + ", aggro=" + aggroWisp.getAggroCooldownForValidation()));

        TCWispEntity wanderWisp = new TCWispEntity(level, origin.getX() + 6.5D, origin.getY(), origin.getZ() + 0.5D);
        wanderWisp.setWispType(Aspect.AIR.getTag());
        level.addFreshEntity(wanderWisp);
        wanderWisp.tickLegacyAiForValidation();
        checks.add(check("wisp_wander_selects_flight_target_and_updates_motion",
                wanderWisp.getCurrentFlightTargetForValidation() != null
                        && wanderWisp.getDeltaMovement().lengthSqr() > 0.0D,
                "target=" + wanderWisp.getCurrentFlightTargetForValidation() + ", motion=" + wanderWisp.getDeltaMovement()));

        TCWispEntity zapWisp = new TCWispEntity(level, origin.getX() + 12.5D, origin.getY(), origin.getZ() + 0.5D);
        Zombie target = EntityType.ZOMBIE.create(level);
        if (target != null) {
            target.moveTo(origin.getX() + 14.5D, origin.getY(), origin.getZ() + 0.5D);
            level.addFreshEntity(target);
        }
        zapWisp.setWispType(Aspect.AIR.getTag());
        zapWisp.setAttackCounterForValidation(19);
        zapWisp.setTarget(target);
        level.addFreshEntity(zapWisp);
        zapWisp.tickLegacyAiForValidation();
        checks.add(check("wisp_zap_cadence_resets_after_twentieth_visible_tick",
                target != null
                        && zapWisp.getPreviousAttackCounterForValidation() == 19
                        && zapWisp.getAttackCounterForValidation() <= -1
                        && zapWisp.getAttackCounterForValidation() >= -20,
                "prev=" + zapWisp.getPreviousAttackCounterForValidation() + ", current=" + zapWisp.getAttackCounterForValidation()));
    }

    private static void addRenderContractChecks(ArrayList<Check> checks) {
        checks.add(check("wisp_render_textures_exist",
                resourceExists("assets/thaumcraft/textures/misc/particles.png")
                        && resourceExists("assets/thaumcraft/textures/misc/auranodes.png"),
                "particles=" + TCWispRenderContract.PARTICLE_TEXTURE + ", nodes=" + TCWispRenderContract.NODE_TEXTURE));
        checks.add(check("wisp_render_layers_use_legacy_frames_scales_and_alpha",
                layer(TCWispRenderContract.CORE, "core", 64, 64, 512, 0.4F, 1.0F)
                        && layer(TCWispRenderContract.HALO, "halo", 64, 64, 320, 0.75F, 0.25F)
                        && layer(TCWispRenderContract.ASPECT_NODE, "aspect_node", 32, 32, 800, 0.75F, 0.5F),
                "core=512/0.4/1.0, halo=320/0.75/0.25, node=800/0.75/0.5"));
        checks.add(check("wisp_zap_payload_matches_legacy_source_target_contract",
                TCWispZapPayload.TYPE.id().equals(id("wisp_zap_fx"))
                        && close(TCEntityFXNetwork.LEGACY_WISP_ZAP_RANGE_SQR, 32.0D * 32.0D),
                "payload=" + TCWispZapPayload.TYPE.id() + ", range=32"));
    }

    private static boolean hasAspectList(AspectList list, Aspect first, int firstAmount, Aspect second, int secondAmount, Aspect third, int thirdAmount) {
        return list != null
                && list.getAspects().length == 3
                && list.getAspects()[0] == first
                && list.getAmount(first) == firstAmount
                && list.getAspects()[1] == second
                && list.getAmount(second) == secondAmount
                && list.getAspects()[2] == third
                && list.getAmount(third) == thirdAmount;
    }

    private static boolean layer(TCWispRenderContract.Layer layer, String name, int gridX, int gridY, int frameBase, float scale, float alpha) {
        return layer.name().equals(name)
                && layer.gridX() == gridX
                && layer.gridY() == gridY
                && layer.frameBase() == frameBase
                && Float.compare(layer.scale(), scale) == 0
                && Float.compare(layer.alpha(), alpha) == 0;
    }

    private static void prepareAirBox(ServerLevel level, BlockPos origin) {
        BlockPos.betweenClosed(origin.offset(-8, -2, -8), origin.offset(20, 8, 8)).forEach(pos ->
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
        BlockPos.betweenClosed(origin.offset(-8, -3, -8), origin.offset(20, -3, 8)).forEach(pos ->
                level.setBlock(pos, Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL));
    }

    private static void cleanup(ServerLevel level, BlockPos origin) {
        level.getEntities(null, new AABB(origin).inflate(48.0D)).forEach(Entity::discard);
    }

    private static boolean resourceExists(String path) {
        return Thread.currentThread().getContextClassLoader().getResource(path) != null;
    }

    private static String resourceText(String path) {
        try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return "";
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            return "";
        }
    }

    private static boolean legacySpecRegistered(String legacyId, String modernId, int tracking, int update, boolean velocity) {
        return TCEntityTypes.byLegacyId(legacyId)
                .filter(spec -> modernId.equals(spec.modernId()))
                .filter(spec -> spec.trackingRange() == tracking)
                .filter(spec -> spec.updateInterval() == update)
                .filter(spec -> spec.velocityUpdates() == velocity)
                .filter(TCEntityTypes.LegacyEntitySpec::isRegisteredFoundation)
                .isPresent();
    }

    private static boolean typeShape(EntityType<?> type, MobCategory category, float width, float height, int tracking, int update, boolean velocity) {
        return type.getCategory() == category
                && Float.compare(type.getWidth(), width) == 0
                && Float.compare(type.getHeight(), height) == 0
                && type.clientTrackingRange() == tracking
                && type.updateInterval() == update
                && type.trackDeltas() == velocity;
    }

    private static ResourceLocation entityId(EntityType<?> type) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(type);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path);
    }

    private static boolean close(double left, double right) {
        return Math.abs(left - right) < 0.0001D;
    }

    private static Check check(String name, boolean passed, String notes) {
        return new Check(name, passed, notes);
    }

    public record Check(String name, boolean passed, String notes) {
    }

    public record Report(List<Check> checks) {
        public int passed() {
            return (int) checks.stream().filter(Check::passed).count();
        }

        public int failed() {
            return checks.size() - passed();
        }
    }
}
