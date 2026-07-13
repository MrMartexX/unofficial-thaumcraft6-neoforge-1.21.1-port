package thaumcraft.common.registry;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCArcaneBoreEntity;
import thaumcraft.common.entities.TCAlumentumEntity;
import thaumcraft.common.entities.TCBottleTaintEntity;
import thaumcraft.common.entities.TCBrainyZombieEntity;
import thaumcraft.common.entities.TCCausalityCollapserEntity;
import thaumcraft.common.entities.TCCultistClericEntity;
import thaumcraft.common.entities.TCCultistKnightEntity;
import thaumcraft.common.entities.TCCultistPortalLesserEntity;
import thaumcraft.common.entities.TCEldritchGuardianEntity;
import thaumcraft.common.entities.TCEldritchOrbEntity;
import thaumcraft.common.entities.TCFallingTaintEntity;
import thaumcraft.common.entities.TCFirebatEntity;
import thaumcraft.common.entities.TCFollowingItemEntity;
import thaumcraft.common.entities.TCFluxRiftEntity;
import thaumcraft.common.entities.TCGiantBrainyZombieEntity;
import thaumcraft.common.entities.TCGolemOrbEntity;
import thaumcraft.common.entities.TCMindSpiderEntity;
import thaumcraft.common.entities.TCPechEntity;
import thaumcraft.common.entities.TCSpecialItemEntity;
import thaumcraft.common.entities.TCTaintCrawlerEntity;
import thaumcraft.common.entities.TCTaintSeedEntity;
import thaumcraft.common.entities.TCTaintSwarmEntity;
import thaumcraft.common.entities.TCTaintacleEntity;
import thaumcraft.common.entities.TCTaintacleTinyEntity;
import thaumcraft.common.entities.TCThaumicSlimeEntity;
import thaumcraft.common.entities.TCWispEntity;

public final class TCEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Thaumcraft.MODID);

    public static final Supplier<EntityType<TCSpecialItemEntity>> SPECIAL_ITEM = ENTITY_TYPES.register("special_item", () ->
            EntityType.Builder.<TCSpecialItemEntity>of(TCSpecialItemEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .setTrackingRange(64)
                    .setUpdateInterval(20)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(Thaumcraft.MODID + ":special_item"));

    public static final Supplier<EntityType<TCFollowingItemEntity>> FOLLOW_ITEM = ENTITY_TYPES.register("follow_item", () ->
            EntityType.Builder.<TCFollowingItemEntity>of(TCFollowingItemEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .setTrackingRange(64)
                    .setUpdateInterval(20)
                    .setShouldReceiveVelocityUpdates(false)
                    .build(Thaumcraft.MODID + ":follow_item"));

    public static final Supplier<EntityType<TCFluxRiftEntity>> FLUX_RIFT = ENTITY_TYPES.register("flux_rift", () ->
            EntityType.Builder.<TCFluxRiftEntity>of(TCFluxRiftEntity::new, MobCategory.MISC)
                    .sized(2.0F, 2.0F)
                    .setTrackingRange(64)
                    .setUpdateInterval(20)
                    .setShouldReceiveVelocityUpdates(false)
                    .build(Thaumcraft.MODID + ":flux_rift"));

    public static final Supplier<EntityType<TCCultistPortalLesserEntity>> CULTIST_PORTAL_LESSER =
            ENTITY_TYPES.register("cultist_portal_lesser", () ->
                    EntityType.Builder.<TCCultistPortalLesserEntity>of(TCCultistPortalLesserEntity::new, MobCategory.MONSTER)
                            .sized(1.5F, 3.0F)
                            .setTrackingRange(64)
                            .setUpdateInterval(20)
                            .setShouldReceiveVelocityUpdates(false)
                            .fireImmune()
                            .build(Thaumcraft.MODID + ":cultist_portal_lesser"));

    public static final Supplier<EntityType<TCCultistKnightEntity>> CULTIST_KNIGHT =
            ENTITY_TYPES.register("cultist_knight", () ->
                    EntityType.Builder.<TCCultistKnightEntity>of(TCCultistKnightEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.8F)
                            .setTrackingRange(64)
                            .setUpdateInterval(3)
                            .setShouldReceiveVelocityUpdates(true)
                            .build(Thaumcraft.MODID + ":cultist_knight"));

    public static final Supplier<EntityType<TCCultistClericEntity>> CULTIST_CLERIC =
            ENTITY_TYPES.register("cultist_cleric", () ->
                    EntityType.Builder.<TCCultistClericEntity>of(TCCultistClericEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.8F)
                            .setTrackingRange(64)
                            .setUpdateInterval(3)
                            .setShouldReceiveVelocityUpdates(true)
                            .build(Thaumcraft.MODID + ":cultist_cleric"));

    public static final Supplier<EntityType<TCArcaneBoreEntity>> ARCANE_BORE = ENTITY_TYPES.register("arcane_bore", () ->
            EntityType.Builder.<TCArcaneBoreEntity>of(TCArcaneBoreEntity::new, MobCategory.MISC)
                    .sized(0.9F, 0.9F)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(Thaumcraft.MODID + ":arcane_bore"));

    public static final Supplier<EntityType<TCFallingTaintEntity>> FALLING_TAINT = ENTITY_TYPES.register("falling_taint", () ->
            EntityType.Builder.<TCFallingTaintEntity>of(TCFallingTaintEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(Thaumcraft.MODID + ":falling_taint"));

    public static final Supplier<EntityType<TCTaintSeedEntity>> TAINT_SEED = ENTITY_TYPES.register("taint_seed", () ->
            EntityType.Builder.<TCTaintSeedEntity>of(TCTaintSeedEntity::new, MobCategory.MONSTER)
                    .sized(1.5F, 1.25F)
                    .setTrackingRange(64)
                    .setUpdateInterval(20)
                    .setShouldReceiveVelocityUpdates(false)
                    .build(Thaumcraft.MODID + ":taint_seed"));

    public static final Supplier<EntityType<TCTaintSeedEntity>> TAINT_SEED_PRIME = ENTITY_TYPES.register("taint_seed_prime", () ->
            EntityType.Builder.<TCTaintSeedEntity>of(TCTaintSeedEntity::new, MobCategory.MONSTER)
                    .sized(2.0F, 2.0F)
                    .setTrackingRange(64)
                    .setUpdateInterval(20)
                    .setShouldReceiveVelocityUpdates(false)
                    .build(Thaumcraft.MODID + ":taint_seed_prime"));

    public static final Supplier<EntityType<TCThaumicSlimeEntity>> THAUM_SLIME = ENTITY_TYPES.register("thaum_slime", () ->
            EntityType.Builder.<TCThaumicSlimeEntity>of(TCThaumicSlimeEntity::new, MobCategory.MONSTER)
                    .sized(2.04F, 2.04F)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(Thaumcraft.MODID + ":thaum_slime"));

    public static final Supplier<EntityType<TCTaintCrawlerEntity>> TAINT_CRAWLER = ENTITY_TYPES.register("taint_crawler", () ->
            EntityType.Builder.<TCTaintCrawlerEntity>of(TCTaintCrawlerEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.4F)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(Thaumcraft.MODID + ":taint_crawler"));

    public static final Supplier<EntityType<TCTaintacleEntity>> TAINTACLE = ENTITY_TYPES.register("taintacle", () ->
            EntityType.Builder.<TCTaintacleEntity>of(TCTaintacleEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 3.0F)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(false)
                    .build(Thaumcraft.MODID + ":taintacle"));

    public static final Supplier<EntityType<TCTaintacleTinyEntity>> TAINTACLE_TINY = ENTITY_TYPES.register("taintacle_tiny", () ->
            EntityType.Builder.<TCTaintacleTinyEntity>of(TCTaintacleTinyEntity::new, MobCategory.MONSTER)
                    .sized(0.22F, 1.0F)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(false)
                    .build(Thaumcraft.MODID + ":taintacle_tiny"));

    public static final Supplier<EntityType<TCTaintSwarmEntity>> TAINT_SWARM = ENTITY_TYPES.register("taint_swarm", () ->
            EntityType.Builder.<TCTaintSwarmEntity>of(TCTaintSwarmEntity::new, MobCategory.MONSTER)
                    .sized(2.0F, 2.0F)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(false)
                    .build(Thaumcraft.MODID + ":taint_swarm"));

    public static final Supplier<EntityType<TCWispEntity>> WISP = ENTITY_TYPES.register("wisp", () ->
            EntityType.Builder.<TCWispEntity>of(TCWispEntity::new, MobCategory.MONSTER)
                    .sized(0.9F, 0.9F)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(false)
                    .build(Thaumcraft.MODID + ":wisp"));

    public static final Supplier<EntityType<TCFirebatEntity>> FIREBAT = ENTITY_TYPES.register("firebat", () ->
            EntityType.Builder.<TCFirebatEntity>of(TCFirebatEntity::new, MobCategory.MONSTER)
                    .sized(TCFirebatEntity.LEGACY_WIDTH, TCFirebatEntity.LEGACY_HEIGHT)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(false)
                    .fireImmune()
                    .build(Thaumcraft.MODID + ":firebat"));

    public static final Supplier<EntityType<TCPechEntity>> PECH = ENTITY_TYPES.register("pech", () ->
            EntityType.Builder.<TCPechEntity>of(TCPechEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(Thaumcraft.MODID + ":pech"));

    public static final Supplier<EntityType<TCBrainyZombieEntity>> BRAINY_ZOMBIE =
            ENTITY_TYPES.register("brainy_zombie", () ->
                    EntityType.Builder.<TCBrainyZombieEntity>of(TCBrainyZombieEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .setTrackingRange(64)
                            .setUpdateInterval(3)
                            .setShouldReceiveVelocityUpdates(true)
                            .build(Thaumcraft.MODID + ":brainy_zombie"));

    public static final Supplier<EntityType<TCGiantBrainyZombieEntity>> GIANT_BRAINY_ZOMBIE =
            ENTITY_TYPES.register("giant_brainy_zombie", () ->
                    EntityType.Builder.<TCGiantBrainyZombieEntity>of(TCGiantBrainyZombieEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .setTrackingRange(64)
                            .setUpdateInterval(3)
                            .setShouldReceiveVelocityUpdates(true)
                            .build(Thaumcraft.MODID + ":giant_brainy_zombie"));

    public static final Supplier<EntityType<TCBottleTaintEntity>> BOTTLE_TAINT = ENTITY_TYPES.register("bottle_taint", () ->
            EntityType.Builder.<TCBottleTaintEntity>of(TCBottleTaintEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .setTrackingRange(64)
                    .setUpdateInterval(20)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(Thaumcraft.MODID + ":bottle_taint"));

    public static final Supplier<EntityType<TCAlumentumEntity>> ALUMENTUM = ENTITY_TYPES.register("alumentum", () ->
            EntityType.Builder.<TCAlumentumEntity>of(TCAlumentumEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .setTrackingRange(64)
                    .setUpdateInterval(20)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(Thaumcraft.MODID + ":alumentum"));

    public static final Supplier<EntityType<TCCausalityCollapserEntity>> CAUSALITY_COLLAPSER = ENTITY_TYPES.register("causality_collapser", () ->
            EntityType.Builder.<TCCausalityCollapserEntity>of(TCCausalityCollapserEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .setTrackingRange(64)
                    .setUpdateInterval(20)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(Thaumcraft.MODID + ":causality_collapser"));

    public static final Supplier<EntityType<TCEldritchOrbEntity>> ELDRITCH_ORB = ENTITY_TYPES.register("eldritch_orb", () ->
            EntityType.Builder.<TCEldritchOrbEntity>of(TCEldritchOrbEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .setTrackingRange(64)
                    .setUpdateInterval(20)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(Thaumcraft.MODID + ":eldritch_orb"));

    public static final Supplier<EntityType<TCGolemOrbEntity>> GOLEM_ORB = ENTITY_TYPES.register("golem_orb", () ->
            EntityType.Builder.<TCGolemOrbEntity>of(TCGolemOrbEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(Thaumcraft.MODID + ":golem_orb"));

    public static final Supplier<EntityType<TCMindSpiderEntity>> MIND_SPIDER = ENTITY_TYPES.register("mind_spider", () ->
            EntityType.Builder.<TCMindSpiderEntity>of(TCMindSpiderEntity::new, MobCategory.MONSTER)
                    .sized(0.7F, 0.5F)
                    .eyeHeight(0.45F)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(Thaumcraft.MODID + ":mind_spider"));

    public static final Supplier<EntityType<TCEldritchGuardianEntity>> ELDRITCH_GUARDIAN =
            ENTITY_TYPES.register("eldritch_guardian", () ->
                    EntityType.Builder.<TCEldritchGuardianEntity>of(TCEldritchGuardianEntity::new, MobCategory.MONSTER)
                            .sized(0.8F, 2.25F)
                            .eyeHeight(2.1F)
                            .setTrackingRange(64)
                            .setUpdateInterval(3)
                            .setShouldReceiveVelocityUpdates(true)
                            .build(Thaumcraft.MODID + ":eldritch_guardian"));

    private static final List<LegacyEntitySpec> LEGACY_ENTITY_SPECS = List.of(
            spec("CultistPortalGreater", "EntityCultistPortalGreater", null, 64, 20, false, "defer", "Eldritch/cult portal behavior and renderer"),
            spec("CultistPortalLesser", "EntityCultistPortalLesser", "cultist_portal_lesser", 64, 20, false, "registered_foundation", "Lesser cultist portal activation, collision, nearby-cultist budget and minion spawn cadence"),
            spec("FluxRift", "EntityFluxRift", "flux_rift", 64, 20, false, "registered_foundation", "Flux/aura lifecycle, collapse and rift renderer foundation"),
            spec("SpecialItem", "EntitySpecialItem", "special_item", 64, 20, true, "registered_foundation", "Legacy item-entity lift and explosion immunity"),
            spec("FollowItem", "EntityFollowingItem", "follow_item", 64, 20, false, "registered_foundation", "Legacy following item movement and spawn data"),
            spec("FallingTaint", "EntityFallingTaint", "falling_taint", 64, 3, true, "registered_foundation", "Taint crust falling physics and taint world mutation"),
            spec("Alumentum", "EntityAlumentum", "alumentum", 64, 20, true, "registered_foundation", "Throwable Alumentum item projectile, invisible body, fiery trail and legacy flaming explosion"),
            spec("GolemDart", "EntityGolemDart", null, 64, 20, false, "defer", "Golem ranged combat"),
            spec("EldritchOrb", "EntityEldritchOrb", "eldritch_orb", 64, 20, true, "registered_foundation", "Eldritch Guardian/Warden projectile: no-gravity lifetime, impact AoE and source-informed renderer"),
            spec("BottleTaint", "EntityBottleTaint", "bottle_taint", 64, 20, true, "registered_foundation", "Taint bottle projectile item behavior, Flux Taint splash and Flux Goo spread"),
            spec("GolemOrb", "EntityGolemOrb", "golem_orb", 64, 3, true, "registered_foundation", "Cultist/Golem homing magic orb projectile behavior and electric-orb renderer foundation"),
            spec("Grapple", "EntityGrapple", null, 64, 20, true, "defer", "Grapple tool physics and rope renderer"),
            spec("CausalityCollapser", "EntityCausalityCollapser", "causality_collapser", 64, 20, true, "registered_foundation", "Causality Collapser projectile, invisible body, legacy explosive rift-collapse AABB"),
            spec("FocusProjectile", "EntityFocusProjectile", null, 64, 20, true, "defer", "Focus/caster execution"),
            spec("FocusCloud", "EntityFocusCloud", null, 64, 20, true, "defer", "Focus/caster cloud execution"),
            spec("Focusmine", "EntityFocusMine", null, 64, 20, true, "defer", "Focus/caster mine execution"),
            spec("TurretBasic", "EntityTurretCrossbow", null, 64, 3, true, "defer", "Construct/turret AI and renderer"),
            spec("TurretAdvanced", "EntityTurretCrossbowAdvanced", null, 64, 3, true, "defer", "Construct/turret AI and renderer"),
            spec("ArcaneBore", "EntityArcaneBore", "arcane_bore", 64, 3, true, "registered_foundation", "Arcane Bore entity, menu, vis mining and renderer foundation"),
            spec("Golem", "EntityThaumcraftGolem", null, 64, 3, true, "defer", "Golem material/part/AI/seal subsystem"),
            spec("EldritchWarden", "EntityEldritchWarden", null, 64, 3, true, "defer", "Eldritch boss AI and renderer"),
            spec("EldritchGolem", "EntityEldritchGolem", null, 64, 3, true, "defer", "Eldritch boss AI and renderer"),
            spec("CultistLeader", "EntityCultistLeader", null, 64, 3, true, "defer", "Cultist boss AI and renderer"),
            spec("TaintacleGiant", "EntityTaintacleGiant", null, 96, 3, false, "defer", "Taint mob AI and renderer"),
            spec("BrainyZombie", "EntityBrainyZombie", "brainy_zombie", 64, 3, true, "registered_foundation", "Angry Zombie attributes, brain loot, scan/aspect identity and legacy overworld natural spawn row"),
            spec("GiantBrainyZombie", "EntityGiantBrainyZombie", "giant_brainy_zombie", 64, 3, true, "registered_foundation", "Furious Zombie anger scaling, leap goal, loot and Eerie-biome spawn dependency"),
            spec("Wisp", "EntityWisp", "wisp", 64, 3, false, "registered_foundation", "Wisp type/aspect persistence, rift-event spawn dependency, legacy flight/target/zap AI, billboard render contract and PacketFXWispZap-equivalent payload"),
            spec("Firebat", "EntityFireBat", "firebat", 64, 3, false, "registered_foundation", "Firebat AI, hanging flight, Nether/Halloween spawn rows, aspects and renderer foundation"),
            spec("Spellbat", "EntitySpellBat", null, 64, 3, false, "defer", "Bat AI variant and renderer"),
            spec("Pech", "EntityPech", "pech", 64, 3, true, "registered_foundation", "Pech type/anger/tamed state, loot pack, valued-item taming, trade menu, scan/aspect identity and renderer foundation; natural magical-biome spawn remains deferred"),
            spec("MindSpider", "EntityMindSpider", "mind_spider", 64, 3, true, "registered_foundation", "Mind Spider harmless/viewer hallucination state, lifespan and warp spawn foundation; custom renderer deferred"),
            spec("EldritchGuardian", "EntityEldritchGuardian", "eldritch_guardian", 64, 3, true, "registered_foundation", "Eldritch Guardian attributes, team rules, warp spawn, ranged orb attack and curse branch; custom mob renderer deferred"),
            spec("CultistKnight", "EntityCultistKnight", "cultist_knight", 64, 3, true, "registered_foundation", "Crimson Knight base attributes, team rules, target AI and portal-spawn equipment foundation; custom mob renderer deferred"),
            spec("CultistCleric", "EntityCultistCleric", "cultist_cleric", 64, 3, true, "registered_foundation", "Crimson Cleric base attributes, ritualist state, ranged cadence, portal-spawn foundation and GolemOrb branch; custom mob renderer deferred"),
            spec("EldritchCrab", "EntityEldritchCrab", null, 64, 3, true, "defer", "Eldritch mob AI and renderer"),
            spec("InhabitedZombie", "EntityInhabitedZombie", null, 64, 3, true, "defer", "Eldritch mob AI and renderer"),
            spec("ThaumSlime", "EntityThaumicSlime", "thaum_slime", 64, 3, true, "registered_foundation", "Thaumic Slime size/xp, ranged spit foundation and scan/aspect identity"),
            spec("TaintCrawler", "EntityTaintCrawler", "taint_crawler", 64, 3, true, "registered_foundation", "Crawler AI foundation, fibre trail, Flux Taint bite and break-spawn hook"),
            spec("Taintacle", "EntityTaintacle", "taintacle", 64, 3, false, "registered_foundation", "Stationary taintacle AI foundation and tiny-spawn hook"),
            spec("TaintacleTiny", "EntityTaintacleSmall", "taintacle_tiny", 64, 3, false, "registered_foundation", "Temporary small taintacle lifetime contract"),
            spec("TaintSwarm", "EntityTaintSwarm", "taint_swarm", 64, 3, false, "registered_foundation", "Swarm flight/summoned-state foundation and geyser spawn hook"),
            spec("TaintSeed", "EntityTaintSeed", "taint_seed", 64, 20, false, "registered_foundation", "Taint spread seed registry, radius and server spread loop"),
            spec("TaintSeedPrime", "EntityTaintSeedPrime", "taint_seed_prime", 64, 20, false, "registered_foundation", "Prime Taint Seed spread area, health and damage variant")
    );

    private TCEntityTypes() {
    }

    public static List<LegacyEntitySpec> legacyEntitySpecs() {
        return LEGACY_ENTITY_SPECS;
    }

    public static List<LegacyEntitySpec> registeredFoundationSpecs() {
        return LEGACY_ENTITY_SPECS.stream()
                .filter(LegacyEntitySpec::isRegisteredFoundation)
                .toList();
    }

    public static Optional<LegacyEntitySpec> byLegacyId(String legacyId) {
        return LEGACY_ENTITY_SPECS.stream()
                .filter(spec -> spec.legacyId().equalsIgnoreCase(legacyId))
                .findFirst();
    }

    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ARCANE_BORE.get(), TCArcaneBoreEntity.createAttributes().build());
        event.put(TAINT_SEED.get(), TCTaintSeedEntity.createAttributes().build());
        event.put(TAINT_SEED_PRIME.get(), TCTaintSeedEntity.createPrimeAttributes().build());
        event.put(THAUM_SLIME.get(), TCThaumicSlimeEntity.createAttributes().build());
        event.put(TAINT_CRAWLER.get(), TCTaintCrawlerEntity.createAttributes().build());
        event.put(TAINTACLE.get(), TCTaintacleEntity.createAttributes().build());
        event.put(TAINTACLE_TINY.get(), TCTaintacleTinyEntity.createAttributes().build());
        event.put(TAINT_SWARM.get(), TCTaintSwarmEntity.createAttributes().build());
        event.put(WISP.get(), TCWispEntity.createAttributes().build());
        event.put(FIREBAT.get(), TCFirebatEntity.createAttributes().build());
        event.put(PECH.get(), TCPechEntity.createAttributes().build());
        event.put(BRAINY_ZOMBIE.get(), TCBrainyZombieEntity.createAttributes().build());
        event.put(GIANT_BRAINY_ZOMBIE.get(), TCGiantBrainyZombieEntity.createAttributes().build());
        event.put(CULTIST_PORTAL_LESSER.get(), TCCultistPortalLesserEntity.createAttributes().build());
        event.put(CULTIST_KNIGHT.get(), TCCultistKnightEntity.createAttributes().build());
        event.put(CULTIST_CLERIC.get(), TCCultistClericEntity.createAttributes().build());
        event.put(MIND_SPIDER.get(), TCMindSpiderEntity.createAttributes().build());
        event.put(ELDRITCH_GUARDIAN.get(), TCEldritchGuardianEntity.createAttributes().build());
    }

    private static LegacyEntitySpec spec(
            String legacyId,
            String legacyClass,
            String modernId,
            int trackingRange,
            int updateInterval,
            boolean velocityUpdates,
            String status,
            String notes
    ) {
        return new LegacyEntitySpec(
                legacyId,
                legacyClass,
                modernId,
                trackingRange,
                updateInterval,
                velocityUpdates,
                status,
                notes
        );
    }

    public record LegacyEntitySpec(
            String legacyId,
            String legacyClass,
            String modernId,
            int trackingRange,
            int updateInterval,
            boolean velocityUpdates,
            String status,
            String notes
    ) {
        public boolean isRegisteredFoundation() {
            return "registered_foundation".equals(status);
        }

        public String stableKey() {
            return legacyId.toLowerCase(Locale.ROOT);
        }
    }
}
