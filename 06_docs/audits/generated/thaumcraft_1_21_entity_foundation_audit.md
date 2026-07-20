# Thaumcraft entity foundation audit

This audit intentionally verifies only the entity registry/foundation slice. AI, mob spawn rules, boss behavior, golems, focus projectile gameplay and custom renderers remain subsystem-specific blockers.

## Checks

| Check | Status | Notes |
| --- | --- | --- |
| legacy entity catalog count | PASS | expected 43, got 43 |
| registered foundation count | PASS | expected item entities, projectile foundations, FluxRift, ArcaneBore, FallingTaint, Wisp, Firebat, Pech, BrainyZombie pair, TaintSeed pair, taint mob/boss foundations, cultist portal/minion/leader foundations and eldritch mob foundations |
| SpecialItem registry id | PASS | expected thaumcraft:special_item, got thaumcraft:special_item |
| FollowItem registry id | PASS | expected thaumcraft:follow_item, got thaumcraft:follow_item |
| FluxRift registry id | PASS | expected thaumcraft:flux_rift, got thaumcraft:flux_rift |
| FocusCloud registry id | PASS | expected thaumcraft:focus_cloud, got thaumcraft:focus_cloud |
| ArcaneBore registry id | PASS | expected thaumcraft:arcane_bore, got thaumcraft:arcane_bore |
| FallingTaint registry id | PASS | expected thaumcraft:falling_taint, got thaumcraft:falling_taint |
| Wisp registry id | PASS | expected thaumcraft:wisp, got thaumcraft:wisp |
| Firebat registry id | PASS | expected thaumcraft:firebat, got thaumcraft:firebat |
| Pech registry id | PASS | expected thaumcraft:pech, got thaumcraft:pech |
| BrainyZombie registry id | PASS | expected thaumcraft:brainy_zombie, got thaumcraft:brainy_zombie |
| GiantBrainyZombie registry id | PASS | expected thaumcraft:giant_brainy_zombie, got thaumcraft:giant_brainy_zombie |
| TaintSeed registry id | PASS | expected thaumcraft:taint_seed, got thaumcraft:taint_seed |
| TaintSeedPrime registry id | PASS | expected thaumcraft:taint_seed_prime, got thaumcraft:taint_seed_prime |
| CultistPortalLesser registry id | PASS | expected thaumcraft:cultist_portal_lesser, got thaumcraft:cultist_portal_lesser |
| CultistPortalGreater registry id | PASS | expected thaumcraft:cultist_portal_greater, got thaumcraft:cultist_portal_greater |
| CultistKnight registry id | PASS | expected thaumcraft:cultist_knight, got thaumcraft:cultist_knight |
| CultistCleric registry id | PASS | expected thaumcraft:cultist_cleric, got thaumcraft:cultist_cleric |
| CultistLeader registry id | PASS | expected thaumcraft:cultist_leader, got thaumcraft:cultist_leader |
| MindSpider registry id | PASS | expected thaumcraft:mind_spider, got thaumcraft:mind_spider |
| EldritchGuardian registry id | PASS | expected thaumcraft:eldritch_guardian, got thaumcraft:eldritch_guardian |
| EldritchCrab registry id | PASS | expected thaumcraft:eldritch_crab, got thaumcraft:eldritch_crab |
| InhabitedZombie registry id | PASS | expected thaumcraft:inhabited_zombie, got thaumcraft:inhabited_zombie |
| ThaumSlime registry id | PASS | expected thaumcraft:thaum_slime, got thaumcraft:thaum_slime |
| TaintCrawler registry id | PASS | expected thaumcraft:taint_crawler, got thaumcraft:taint_crawler |
| Taintacle registry id | PASS | expected thaumcraft:taintacle, got thaumcraft:taintacle |
| TaintacleTiny registry id | PASS | expected thaumcraft:taintacle_tiny, got thaumcraft:taintacle_tiny |
| TaintacleGiant registry id | PASS | expected thaumcraft:taintacle_giant, got thaumcraft:taintacle_giant |
| TaintSwarm registry id | PASS | expected thaumcraft:taint_swarm, got thaumcraft:taint_swarm |
| Alumentum registry id | PASS | expected thaumcraft:alumentum, got thaumcraft:alumentum |
| CausalityCollapser registry id | PASS | expected thaumcraft:causality_collapser, got thaumcraft:causality_collapser |
| BottleTaint registry id | PASS | expected thaumcraft:bottle_taint, got thaumcraft:bottle_taint |
| EldritchOrb registry id | PASS | expected thaumcraft:eldritch_orb, got thaumcraft:eldritch_orb |
| GolemOrb registry id | PASS | expected thaumcraft:golem_orb, got thaumcraft:golem_orb |
| SpecialItem type parameters | PASS | category=MISC, size=0.25x0.25, tracking=64, update=20, velocity=true |
| FollowItem type parameters | PASS | category=MISC, size=0.25x0.25, tracking=64, update=20, velocity=false |
| FocusCloud type parameters | PASS | category=MISC, size=0.15x0.15, tracking=64, update=20, velocity=true |
| FallingTaint type parameters | PASS | category=MISC, size=0.98x0.98, tracking=64, update=3, velocity=true |
| Wisp mob type parameters | PASS | category=MONSTER, size=0.9x0.9, tracking=64, update=3, velocity=false |
| Firebat mob type parameters | PASS | category=MONSTER, size=0.5x0.9, tracking=64, update=3, velocity=false |
| Pech mob type parameters | PASS | category=MONSTER, size=0.6x1.8, tracking=64, update=3, velocity=true |
| BrainyZombie mob type parameters | PASS | category=MONSTER, size=0.6x1.95, tracking=64, update=3, velocity=true |
| GiantBrainyZombie mob type parameters | PASS | category=MONSTER, size=0.6x1.95, tracking=64, update=3, velocity=true |
| ThaumSlime mob type parameters | PASS | category=MONSTER, size=2.04x2.04, tracking=64, update=3, velocity=true |
| TaintCrawler mob type parameters | PASS | category=MONSTER, size=0.5x0.4, tracking=64, update=3, velocity=true |
| Taintacle mob type parameters | PASS | category=MONSTER, size=0.8x3.0, tracking=64, update=3, velocity=false |
| TaintacleTiny mob type parameters | PASS | category=MONSTER, size=0.22x1.0, tracking=64, update=3, velocity=false |
| TaintacleGiant mob type parameters | PASS | category=MONSTER, size=1.1x6.0, tracking=96, update=3, velocity=false |
| TaintSwarm mob type parameters | PASS | category=MONSTER, size=2.0x2.0, tracking=64, update=3, velocity=false |
| Alumentum type parameters | PASS | category=MISC, size=0.25x0.25, tracking=64, update=20, velocity=true |
| CausalityCollapser type parameters | PASS | category=MISC, size=0.25x0.25, tracking=64, update=20, velocity=true |
| BottleTaint type parameters | PASS | category=MISC, size=0.25x0.25, tracking=64, update=20, velocity=true |
| EldritchOrb type parameters | PASS | category=MISC, size=0.25x0.25, tracking=64, update=20, velocity=true |
| GolemOrb type parameters | PASS | category=MISC, size=0.25x0.25, tracking=64, update=3, velocity=true |
| CultistPortalLesser mob type parameters | PASS | category=MONSTER, size=1.5x3.0, tracking=64, update=20, velocity=false |
| CultistPortalGreater mob type parameters | PASS | category=MONSTER, size=1.5x3.0, tracking=64, update=20, velocity=false |
| CultistKnight mob type parameters | PASS | category=MONSTER, size=0.6x1.8, tracking=64, update=3, velocity=true |
| CultistCleric mob type parameters | PASS | category=MONSTER, size=0.6x1.8, tracking=64, update=3, velocity=true |
| CultistLeader mob type parameters | PASS | category=MONSTER, size=0.75x2.25, tracking=64, update=3, velocity=true |
| MindSpider mob type parameters | PASS | category=MONSTER, size=0.7x0.5, tracking=64, update=3, velocity=true |
| EldritchGuardian mob type parameters | PASS | category=MONSTER, size=0.8x2.25, tracking=64, update=3, velocity=true |
| EldritchCrab mob type parameters | PASS | category=MONSTER, size=0.8x0.6, tracking=64, update=3, velocity=true |
| InhabitedZombie mob type parameters | PASS | category=MONSTER, size=0.6x1.95, tracking=64, update=3, velocity=true |
| BrainyZombie legacy contracts | PASS | attributes, reinforcement gate, brain-drop roll and ConfigAspects contract |
| GiantBrainyZombie legacy contracts | PASS | attributes, anger damage/size/eye-height, inherited brain drop, rotten flesh loops and ConfigAspects contract |
| Firebat legacy contracts | PASS | resting state, attributes, fire/explosion profile, Halloween gate, light roll gate and ConfigAspects contract |
| Pech legacy contracts | PASS | type/tamed/anger state, attributes, pack size, explicit ender pearl value, trade tier coverage, spawn gates and subtype aspects |
| remaining eldritch/taint entity contracts | PASS | Greater portal, CultistLeader, Giant Taintacle, EldritchCrab, InhabitedZombie and exact ConfigAspects assignments |
| active entity renderer texture resources exist | PASS | missing=[] |
| foundation constructors | PASS | registry factories plus special/follow item stack and target coordinates |

## Legacy entity catalog

| Legacy id | Legacy class | Modern id | Tracking | Update | Velocity | Status | Notes |
| --- | --- | --- | ---: | ---: | --- | --- | --- |
| CultistPortalGreater | EntityCultistPortalGreater | thaumcraft:cultist_portal_greater | 64 | 20 | false | registered_foundation | Greater portal stage machine, minion/Praetor spawn, bossbar, touch damage and portal renderer foundation; banner/crate world decoration remains item/block gated |
| CultistPortalLesser | EntityCultistPortalLesser | thaumcraft:cultist_portal_lesser | 64 | 20 | false | registered_foundation | Lesser cultist portal activation, collision, nearby-cultist budget and minion spawn cadence |
| FluxRift | EntityFluxRift | thaumcraft:flux_rift | 64 | 20 | false | registered_foundation | Flux/aura lifecycle, collapse and rift renderer foundation |
| SpecialItem | EntitySpecialItem | thaumcraft:special_item | 64 | 20 | true | registered_foundation | Legacy item-entity lift and explosion immunity |
| FollowItem | EntityFollowingItem | thaumcraft:follow_item | 64 | 20 | false | registered_foundation | Legacy following item movement and spawn data |
| FallingTaint | EntityFallingTaint | thaumcraft:falling_taint | 64 | 3 | true | registered_foundation | Taint crust falling physics and taint world mutation |
| Alumentum | EntityAlumentum | thaumcraft:alumentum | 64 | 20 | true | registered_foundation | Throwable Alumentum item projectile, invisible body, fiery trail and legacy flaming explosion |
| GolemDart | EntityGolemDart |  | 64 | 20 | false | defer | Golem ranged combat |
| EldritchOrb | EntityEldritchOrb | thaumcraft:eldritch_orb | 64 | 20 | true | registered_foundation | Eldritch Guardian/Warden projectile: no-gravity lifetime, impact AoE and source-informed renderer |
| BottleTaint | EntityBottleTaint | thaumcraft:bottle_taint | 64 | 20 | true | registered_foundation | Taint bottle projectile item behavior, Flux Taint splash and Flux Goo spread |
| GolemOrb | EntityGolemOrb | thaumcraft:golem_orb | 64 | 3 | true | registered_foundation | Cultist/Golem homing magic orb projectile behavior and electric-orb renderer foundation |
| Grapple | EntityGrapple |  | 64 | 20 | true | defer | Grapple tool physics and rope renderer |
| CausalityCollapser | EntityCausalityCollapser | thaumcraft:causality_collapser | 64 | 20 | true | registered_foundation | Causality Collapser projectile, invisible body, legacy explosive rift-collapse AABB |
| FocusProjectile | EntityFocusProjectile |  | 64 | 20 | true | defer | Focus/caster execution |
| FocusCloud | EntityFocusCloud | thaumcraft:focus_cloud | 64 | 20 | true | registered_foundation | Rift-owned ROOT->CLOUD->FLUX cloud execution; broad caster cloud authoring remains focus subsystem work |
| Focusmine | EntityFocusMine |  | 64 | 20 | true | defer | Focus/caster mine execution |
| TurretBasic | EntityTurretCrossbow |  | 64 | 3 | true | defer | Construct/turret AI and renderer |
| TurretAdvanced | EntityTurretCrossbowAdvanced |  | 64 | 3 | true | defer | Construct/turret AI and renderer |
| ArcaneBore | EntityArcaneBore | thaumcraft:arcane_bore | 64 | 3 | true | registered_foundation | Arcane Bore entity, menu, vis mining and renderer foundation |
| Golem | EntityThaumcraftGolem |  | 64 | 3 | true | defer | Golem material/part/AI/seal subsystem |
| EldritchWarden | EntityEldritchWarden |  | 64 | 3 | true | defer | Eldritch boss AI and renderer |
| EldritchGolem | EntityEldritchGolem |  | 64 | 3 | true | defer | Eldritch boss AI and renderer |
| CultistLeader | EntityCultistLeader | thaumcraft:cultist_leader | 64 | 3 | true | registered_foundation | Crimson Praetor title, gear placeholder mapping, cultist regen aura, red GolemOrb ranged attack and renderer foundation |
| TaintacleGiant | EntityTaintacleGiant | thaumcraft:taintacle_giant | 96 | 3 | false | registered_foundation | Giant Taintacle bossbar, no natural spawn, damage cap/enrage, regeneration, unique pearl drop and renderer foundation |
| BrainyZombie | EntityBrainyZombie | thaumcraft:brainy_zombie | 64 | 3 | true | registered_foundation | Angry Zombie attributes, brain loot, scan/aspect identity and legacy overworld natural spawn row |
| GiantBrainyZombie | EntityGiantBrainyZombie | thaumcraft:giant_brainy_zombie | 64 | 3 | true | registered_foundation | Furious Zombie anger scaling, leap goal, loot and Eerie-biome spawn dependency |
| Wisp | EntityWisp | thaumcraft:wisp | 64 | 3 | false | registered_foundation | Wisp type/aspect persistence, rift-event spawn dependency, legacy flight/target/zap AI, billboard render contract and PacketFXWispZap-equivalent payload |
| Firebat | EntityFireBat | thaumcraft:firebat | 64 | 3 | false | registered_foundation | Firebat AI, hanging flight, Nether/Halloween spawn rows, aspects and renderer foundation |
| Spellbat | EntitySpellBat |  | 64 | 3 | false | defer | Bat AI variant and renderer |
| Pech | EntityPech | thaumcraft:pech | 64 | 3 | true | registered_foundation | Pech type/anger/tamed state, loot pack, valued-item taming, trade menu, scan/aspect identity, renderer foundation and exact magical-biome spawn tag gate |
| MindSpider | EntityMindSpider | thaumcraft:mind_spider | 64 | 3 | true | registered_foundation | Mind Spider harmless/viewer hallucination state, lifespan and warp spawn foundation; custom renderer deferred |
| EldritchGuardian | EntityEldritchGuardian | thaumcraft:eldritch_guardian | 64 | 3 | true | registered_foundation | Eldritch Guardian attributes, team rules, warp spawn, ranged orb attack, curse branch and renderer foundation |
| CultistKnight | EntityCultistKnight | thaumcraft:cultist_knight | 64 | 3 | true | registered_foundation | Crimson Knight base attributes, team rules, target AI, portal-spawn equipment and renderer foundation |
| CultistCleric | EntityCultistCleric | thaumcraft:cultist_cleric | 64 | 3 | true | registered_foundation | Crimson Cleric base attributes, ritualist state, ranged cadence, portal-spawn foundation, GolemOrb branch and renderer foundation |
| EldritchCrab | EntityEldritchCrab | thaumcraft:eldritch_crab | 64 | 3 | true | registered_foundation | Helm state, speed swap, leap/riding attack, poison immunity, crab sounds, Ender Pearl drop and renderer foundation |
| InhabitedZombie | EntityInhabitedZombie | thaumcraft:inhabited_zombie | 64 | 3 | true | registered_foundation | Eldritch zombie shell, armor placeholder mapping, no conversion, death-crab handoff and renderer foundation |
| ThaumSlime | EntityThaumicSlime | thaumcraft:thaum_slime | 64 | 3 | true | registered_foundation | Thaumic Slime size/xp, ranged spit foundation, scan/aspect identity and renderer foundation |
| TaintCrawler | EntityTaintCrawler | thaumcraft:taint_crawler | 64 | 3 | true | registered_foundation | Crawler AI foundation, fibre trail, Flux Taint bite, break-spawn hook and renderer foundation |
| Taintacle | EntityTaintacle | thaumcraft:taintacle | 64 | 3 | false | registered_foundation | Stationary taintacle AI foundation, tiny-spawn hook and renderer foundation |
| TaintacleTiny | EntityTaintacleSmall | thaumcraft:taintacle_tiny | 64 | 3 | false | registered_foundation | Temporary small taintacle lifetime contract and renderer foundation |
| TaintSwarm | EntityTaintSwarm | thaumcraft:taint_swarm | 64 | 3 | false | registered_foundation | Swarm flight/summoned-state foundation, geyser spawn hook and legacy empty-render contract |
| TaintSeed | EntityTaintSeed | thaumcraft:taint_seed | 64 | 20 | false | registered_foundation | Taint spread seed registry, radius and server spread loop |
| TaintSeedPrime | EntityTaintSeedPrime | thaumcraft:taint_seed_prime | 64 | 20 | false | registered_foundation | Prime Taint Seed spread area, health and damage variant |
