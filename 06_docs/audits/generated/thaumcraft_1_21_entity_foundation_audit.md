# Thaumcraft entity foundation audit

This audit intentionally verifies only the entity registry/foundation slice. AI, mob spawn rules, boss behavior, golems, focus projectile gameplay and custom renderers remain subsystem-specific blockers.

## Checks

| Check | Status | Notes |
| --- | --- | --- |
| legacy entity catalog count | PASS | expected 43, got 43 |
| registered foundation count | PASS | expected SpecialItem and FollowItem only in this batch |
| SpecialItem registry id | PASS | expected thaumcraft:special_item, got thaumcraft:special_item |
| FollowItem registry id | PASS | expected thaumcraft:follow_item, got thaumcraft:follow_item |
| SpecialItem type parameters | PASS | category=MISC, size=0.25x0.25, tracking=64, update=20, velocity=true |
| FollowItem type parameters | PASS | category=MISC, size=0.25x0.25, tracking=64, update=20, velocity=false |
| foundation constructors | PASS | registry factories plus special/follow item stack and target coordinates |

## Legacy entity catalog

| Legacy id | Legacy class | Modern id | Tracking | Update | Velocity | Status | Notes |
| --- | --- | --- | ---: | ---: | --- | --- | --- |
| CultistPortalGreater | EntityCultistPortalGreater |  | 64 | 20 | false | defer | Eldritch/cult portal behavior and renderer |
| CultistPortalLesser | EntityCultistPortalLesser |  | 64 | 20 | false | defer | Eldritch/cult portal behavior and renderer |
| FluxRift | EntityFluxRift |  | 64 | 20 | false | defer | Flux/aura pollution and rift renderer |
| SpecialItem | EntitySpecialItem | thaumcraft:special_item | 64 | 20 | true | registered_foundation | Legacy item-entity lift and explosion immunity |
| FollowItem | EntityFollowingItem | thaumcraft:follow_item | 64 | 20 | false | registered_foundation | Legacy following item movement and spawn data |
| FallingTaint | EntityFallingTaint |  | 64 | 3 | true | defer | Taint block physics and taint world mutation |
| Alumentum | EntityAlumentum |  | 64 | 20 | true | defer | Projectile item behavior and impact effects |
| GolemDart | EntityGolemDart |  | 64 | 20 | false | defer | Golem ranged combat |
| EldritchOrb | EntityEldritchOrb |  | 64 | 20 | true | defer | Eldritch projectile behavior and renderer |
| BottleTaint | EntityBottleTaint |  | 64 | 20 | true | defer | Taint bottle projectile and taint spread |
| GolemOrb | EntityGolemOrb |  | 64 | 3 | true | defer | Golem combat/projectile behavior |
| Grapple | EntityGrapple |  | 64 | 20 | true | defer | Grapple tool physics and rope renderer |
| CausalityCollapser | EntityCausalityCollapser |  | 64 | 20 | true | defer | Rift/causality item projectile effects |
| FocusProjectile | EntityFocusProjectile |  | 64 | 20 | true | defer | Focus/caster execution |
| FocusCloud | EntityFocusCloud |  | 64 | 20 | true | defer | Focus/caster cloud execution |
| Focusmine | EntityFocusMine |  | 64 | 20 | true | defer | Focus/caster mine execution |
| TurretBasic | EntityTurretCrossbow |  | 64 | 3 | true | defer | Construct/turret AI and renderer |
| TurretAdvanced | EntityTurretCrossbowAdvanced |  | 64 | 3 | true | defer | Construct/turret AI and renderer |
| ArcaneBore | EntityArcaneBore |  | 64 | 3 | true | defer | Arcane Bore mining and renderer |
| Golem | EntityThaumcraftGolem |  | 64 | 3 | true | defer | Golem material/part/AI/seal subsystem |
| EldritchWarden | EntityEldritchWarden |  | 64 | 3 | true | defer | Eldritch boss AI and renderer |
| EldritchGolem | EntityEldritchGolem |  | 64 | 3 | true | defer | Eldritch boss AI and renderer |
| CultistLeader | EntityCultistLeader |  | 64 | 3 | true | defer | Cultist boss AI and renderer |
| TaintacleGiant | EntityTaintacleGiant |  | 96 | 3 | false | defer | Taint mob AI and renderer |
| BrainyZombie | EntityBrainyZombie |  | 64 | 3 | true | defer | Thaumcraft mob AI, loot and spawn rules |
| GiantBrainyZombie | EntityGiantBrainyZombie |  | 64 | 3 | true | defer | Thaumcraft mob AI, loot and spawn rules |
| Wisp | EntityWisp |  | 64 | 3 | false | defer | Wisp AI, aura interaction and renderer |
| Firebat | EntityFireBat |  | 64 | 3 | false | defer | Bat AI variant and renderer |
| Spellbat | EntitySpellBat |  | 64 | 3 | false | defer | Bat AI variant and renderer |
| Pech | EntityPech |  | 64 | 3 | true | defer | Pech AI, trading and renderer |
| MindSpider | EntityMindSpider |  | 64 | 3 | true | defer | Mob AI/effects and renderer |
| EldritchGuardian | EntityEldritchGuardian |  | 64 | 3 | true | defer | Eldritch mob AI and renderer |
| CultistKnight | EntityCultistKnight |  | 64 | 3 | true | defer | Cultist mob AI and renderer |
| CultistCleric | EntityCultistCleric |  | 64 | 3 | true | defer | Cultist mob AI and renderer |
| EldritchCrab | EntityEldritchCrab |  | 64 | 3 | true | defer | Eldritch mob AI and renderer |
| InhabitedZombie | EntityInhabitedZombie |  | 64 | 3 | true | defer | Eldritch mob AI and renderer |
| ThaumSlime | EntityThaumicSlime |  | 64 | 3 | true | defer | Slime variant AI and renderer |
| TaintCrawler | EntityTaintCrawler |  | 64 | 3 | true | defer | Taint mob AI and renderer |
| Taintacle | EntityTaintacle |  | 64 | 3 | false | defer | Taint mob AI and renderer |
| TaintacleTiny | EntityTaintacleSmall |  | 64 | 3 | false | defer | Taint mob AI and renderer |
| TaintSwarm | EntityTaintSwarm |  | 64 | 3 | false | defer | Taint mob AI and renderer |
| TaintSeed | EntityTaintSeed |  | 64 | 20 | false | defer | Taint spread and seed AI |
| TaintSeedPrime | EntityTaintSeedPrime |  | 64 | 20 | false | defer | Taint spread and seed AI |
