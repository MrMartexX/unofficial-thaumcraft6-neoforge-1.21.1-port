package thaumcraft.common.registry;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCFollowingItemEntity;
import thaumcraft.common.entities.TCSpecialItemEntity;

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

    private static final List<LegacyEntitySpec> LEGACY_ENTITY_SPECS = List.of(
            spec("CultistPortalGreater", "EntityCultistPortalGreater", null, 64, 20, false, "defer", "Eldritch/cult portal behavior and renderer"),
            spec("CultistPortalLesser", "EntityCultistPortalLesser", null, 64, 20, false, "defer", "Eldritch/cult portal behavior and renderer"),
            spec("FluxRift", "EntityFluxRift", null, 64, 20, false, "defer", "Flux/aura pollution and rift renderer"),
            spec("SpecialItem", "EntitySpecialItem", "special_item", 64, 20, true, "registered_foundation", "Legacy item-entity lift and explosion immunity"),
            spec("FollowItem", "EntityFollowingItem", "follow_item", 64, 20, false, "registered_foundation", "Legacy following item movement and spawn data"),
            spec("FallingTaint", "EntityFallingTaint", null, 64, 3, true, "defer", "Taint block physics and taint world mutation"),
            spec("Alumentum", "EntityAlumentum", null, 64, 20, true, "defer", "Projectile item behavior and impact effects"),
            spec("GolemDart", "EntityGolemDart", null, 64, 20, false, "defer", "Golem ranged combat"),
            spec("EldritchOrb", "EntityEldritchOrb", null, 64, 20, true, "defer", "Eldritch projectile behavior and renderer"),
            spec("BottleTaint", "EntityBottleTaint", null, 64, 20, true, "defer", "Taint bottle projectile and taint spread"),
            spec("GolemOrb", "EntityGolemOrb", null, 64, 3, true, "defer", "Golem combat/projectile behavior"),
            spec("Grapple", "EntityGrapple", null, 64, 20, true, "defer", "Grapple tool physics and rope renderer"),
            spec("CausalityCollapser", "EntityCausalityCollapser", null, 64, 20, true, "defer", "Rift/causality item projectile effects"),
            spec("FocusProjectile", "EntityFocusProjectile", null, 64, 20, true, "defer", "Focus/caster execution"),
            spec("FocusCloud", "EntityFocusCloud", null, 64, 20, true, "defer", "Focus/caster cloud execution"),
            spec("Focusmine", "EntityFocusMine", null, 64, 20, true, "defer", "Focus/caster mine execution"),
            spec("TurretBasic", "EntityTurretCrossbow", null, 64, 3, true, "defer", "Construct/turret AI and renderer"),
            spec("TurretAdvanced", "EntityTurretCrossbowAdvanced", null, 64, 3, true, "defer", "Construct/turret AI and renderer"),
            spec("ArcaneBore", "EntityArcaneBore", null, 64, 3, true, "defer", "Arcane Bore mining and renderer"),
            spec("Golem", "EntityThaumcraftGolem", null, 64, 3, true, "defer", "Golem material/part/AI/seal subsystem"),
            spec("EldritchWarden", "EntityEldritchWarden", null, 64, 3, true, "defer", "Eldritch boss AI and renderer"),
            spec("EldritchGolem", "EntityEldritchGolem", null, 64, 3, true, "defer", "Eldritch boss AI and renderer"),
            spec("CultistLeader", "EntityCultistLeader", null, 64, 3, true, "defer", "Cultist boss AI and renderer"),
            spec("TaintacleGiant", "EntityTaintacleGiant", null, 96, 3, false, "defer", "Taint mob AI and renderer"),
            spec("BrainyZombie", "EntityBrainyZombie", null, 64, 3, true, "defer", "Thaumcraft mob AI, loot and spawn rules"),
            spec("GiantBrainyZombie", "EntityGiantBrainyZombie", null, 64, 3, true, "defer", "Thaumcraft mob AI, loot and spawn rules"),
            spec("Wisp", "EntityWisp", null, 64, 3, false, "defer", "Wisp AI, aura interaction and renderer"),
            spec("Firebat", "EntityFireBat", null, 64, 3, false, "defer", "Bat AI variant and renderer"),
            spec("Spellbat", "EntitySpellBat", null, 64, 3, false, "defer", "Bat AI variant and renderer"),
            spec("Pech", "EntityPech", null, 64, 3, true, "defer", "Pech AI, trading and renderer"),
            spec("MindSpider", "EntityMindSpider", null, 64, 3, true, "defer", "Mob AI/effects and renderer"),
            spec("EldritchGuardian", "EntityEldritchGuardian", null, 64, 3, true, "defer", "Eldritch mob AI and renderer"),
            spec("CultistKnight", "EntityCultistKnight", null, 64, 3, true, "defer", "Cultist mob AI and renderer"),
            spec("CultistCleric", "EntityCultistCleric", null, 64, 3, true, "defer", "Cultist mob AI and renderer"),
            spec("EldritchCrab", "EntityEldritchCrab", null, 64, 3, true, "defer", "Eldritch mob AI and renderer"),
            spec("InhabitedZombie", "EntityInhabitedZombie", null, 64, 3, true, "defer", "Eldritch mob AI and renderer"),
            spec("ThaumSlime", "EntityThaumicSlime", null, 64, 3, true, "defer", "Slime variant AI and renderer"),
            spec("TaintCrawler", "EntityTaintCrawler", null, 64, 3, true, "defer", "Taint mob AI and renderer"),
            spec("Taintacle", "EntityTaintacle", null, 64, 3, false, "defer", "Taint mob AI and renderer"),
            spec("TaintacleTiny", "EntityTaintacleSmall", null, 64, 3, false, "defer", "Taint mob AI and renderer"),
            spec("TaintSwarm", "EntityTaintSwarm", null, 64, 3, false, "defer", "Taint mob AI and renderer"),
            spec("TaintSeed", "EntityTaintSeed", null, 64, 20, false, "defer", "Taint spread and seed AI"),
            spec("TaintSeedPrime", "EntityTaintSeedPrime", null, 64, 20, false, "defer", "Taint spread and seed AI")
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
