# Thaumcraft 6 NeoForge 1.21.1 Current Port Status

Last reviewed branch: `main`
Last reviewed checkpoint: `2026-07-13` Lesser cultist portal minion spawning
Reviewed target module: `05_neoforge_port`

## State Snapshot

This section records the current repository state. The lower "Changelog Notes" section records how the project reached this state.

### Active parity baseline

- Aspect, item-level scan, and entity-level scan parity dumps are clean for all comparable runtime keys.
- Thaumometer scan-key mutation and legacy-shaped client highlight/overlay behavior are active for the current predicate layer.
- Current comparable item aspect parity is `1139/1139`; item-level scan parity is `1139/1139`; entity scan parity has `83/85` parity-ok rows plus `2` documented expected modern entity-policy rows.
- The cross-cutting item/block framework now extracts a fingerprinted primary legacy manifest and a live port manifest, then validates registry/resource, source-quality, behavior/data-boundary and visual-boundary reports. The `quick` preset includes `texture_color`, `item_visual_parity`, `legacy_shape_parity` and `legacy_visual_collision_parity`; the focused `visual` preset runs model transforms, item visual assets, legacy shape, legacy visual/collision/outline, texture/color, sound/particle/FX and visual completion checks together. Current `visual` report is report-only: `legacy_shape_parity` has `114` rows with `10` review rows, `legacy_visual_collision_parity` has `585` rows with `2` facing-domain mismatches (`golem_builder`, `research_table`), `0` missing rows and `341` unknown rows, `outline_contract` has `68` match / `0` mismatch / `46` unknown, `item_visual_parity` has `2009` rows with `34` missing item models and `219` review rows, `texture_color` has `201` active texture refs with `176` exact matches and `25` review rows, and `visual_equivalence_completion` has `17` rows with `11` pass / `6` review / `0` errors. These reports still do not claim full gameplay or measured pixel parity.

### Research, scanning and theorycraft

- The research-table/scribing-tools slice includes storage, conversion, the first modern menu/screen boundary, server-owned theory data, validated table action payloads, server action-result screen refresh, and legacy-asset card-sheet choice rendering.
- Current research aids/cards include the first vanilla bookshelf/enchanting-table/beacon family, safe Eldritch glyphed-stone/Nether-portal/End-portal aids, basic block aids for crucible/arcane workbench/infusion matrix/Focal Manipulator/golem press, first Artifice, Basic Auromancy, Basic Golemancy, safe Eldritch theory cards including registered `CardTruth`, `CardInfuse`, `CardScripting`, and `CardAwareness`.
- Brain-in-a-Jar is now a real theorycraft aid source: `jar_brain` registers a real block, BlockItem and BlockEntity, preserves legacy XP storage/pull/absorb/release behavior, exposes the legacy comparator/enchanting bonus contracts, preserves XP on the item stack, and contributes `CardDarkWhispers` through `AidBrainInAJar`. `TCBrainJarBehaviorAudit` passes `10/10`. Exact animated brain rendering and broader golem/mind systems remain separate follow-up work; the legacy `jar_brain` item-warp entry now belongs to the focused warp bridge.
- `CardTruth` is now active as a registered Eldritch option card and is covered by the research table diagnostic audit. `AidBasicEldritch` is still deferred because the legacy `BlocksTC.eldritch` owner is ambiguous in the available source/jar evidence; Crimson portal aid still waits for the real Crimson portal entity layer; Dragon Egg theorycraft remains unregistered because original TC6 source declares the class but does not register it in `ConfigResearch`.
- Server-authoritative warp mutation/sync is active for current warp-producing gameplay. `TCWarpManager` owns add/reduce/set/clear, research unlock side effects, sync payloads and client action-bar messages, and the legacy `jar_brain` crafting warp bridge applies `+1` normal warp while respecting `wussMode`. The server-side `WarpEvents` slice owns the legacy 2000-tick scheduler, temporary-warp decay before roll, trigger/counter math, potion/status outcomes, Death Gaze range/cone basics, Unnatural Hunger relief, BATHSALTS/ELDRITCH unlock thresholds, real spawn attempts for the Eldritch Guardian, Mind Spider and lesser cultist portal foundations, the Eldritch Guardian no-gravity orb projectile attack path, lesser portal spawning for registered CultistKnight/CultistCleric minions, and the CultistCleric red targeted GolemOrb branch. `TCWarpBehaviorAudit` passes `9/9`; `TCWarpEventBehaviorAudit` passes `35/35`. Client hallucination/stress visuals, exact Guardian/orb/cultist/portal renderer pixel parity and fortress mask mitigation remain deferred.

### Crafting, recipes and page data

- The first server-authoritative Arcane Workbench crafting path exists with server-owned base/discounted cost, aura, crystal GUI feedback, missing-vis ghost output, first player vis-discount service, and Workbench Charger 3 x 3 aura behavior.
- Exact active arcane recipes include `thaumometer`, `vis_resonator`, `workbenchcharger`, `goggles`, `mechanism_simple`, `mechanism_complex`, `wand_workbench`/Focal Manipulator, `caster_basic`, `enchantedfabric`, `mirrorglass`, `filter`, `morphicresonator`, `essentiasmelter`, and `infusionmatrix`, with passing server runtime audits.
- Arcane recipe-derived aspect generation is active for the current `TCArcaneRecipe` family and reload-validates `filter` plus `morphic_resonator`, including the legacy `praecantatio` vis bonus.
- The `thaumcraft:crucible` serializer/page-data boundary covers HEDGE_ALCHEMY, METAL_PURIFICATION, Alumentum, Nitor, Brass Ingot, Thaumium Ingot, all 37 vis-crystal recipes, and lowercase legacy OreDictionary catalyst tag bridges. The seven legacy dynamic HEDGE_ALCHEMY costs are resolved into explicit JSON aspect costs from current parity data so runtime recipe matching never sees zero-cost placeholders.
- The focused special alchemy behavior slice is active for item/projectile/fluid/effect-owned side effects: `bath_salts` preserves the legacy dropped-item lifespan and water-source conversion, `bottle_taint` preserves legacy stack size, throw constants, Flux Taint splash predicate/effect and Flux Goo support/fallback placement, `liquid_death` / `purifying_fluid` are real NeoForge fluids/blocks with real bucket source mappings, `liquid_death` uses the legacy `thaumcraft:dissolve` damage identity and dissolve-crystal living-drop bridge, Warp Ward follows the legacy permanent-warp duration formula, and Sanity Soap uses the legacy Warp Ward/Purifying Fluid bonuses. The dedicated runtime audit passes `24/24`. Exact client fluid particles/render translucency and remaining automation consumers remain follow-up slices. Legacy crucible item absorption is collision-only, not radius pulling.
- The first broader alchemy automation device slice is active. `spa` and `everfull_urn` are real BlockItem-backed blocks with BlockEntities, modern blockstates/models/loot/tags/lang, sided NeoForge capabilities and legacy-shaped server behavior. Everfull Urn preserves the top-only drain/no-fill fluid capability, 5x3x5 cached target scan, aura-to-water refill quanta, 333 mB cauldron/glass-bottle costs and direct player fluid-container interaction. Arcane Spa preserves the bath-salts slot, 5000 mB fluid tank, redstone pause, mix-mode Purifying Fluid placement, liquid-only source placement and adjacent 5x5 expansion behavior. `TCAlchemyAutomationDeviceAudit` passes `11/11`; Spa GUI, Botania Petal Apothecary support, exact client water-trail/splash particles and measured model pixel parity remain separate focused work.
- The `thaumcraft:infusion` boundary has `42/42` valid JSON recipes, page snapshots, active matrix/pedestal BlockEntities, all three pillar variants, both matrix modifier stones and a real `jar_normal` source container. Server-authoritative caster interaction activates the matrix on the first click and starts a researched catalyst/component match on the second without incorrectly requiring essentia up front. Recipe cost is frozen in the plan; relevant altar changes and a bounded fallback refresh live delay/replenishment, inactive active matrices recharge stability, and invalid center/pillars deactivate safely. The server persists craft/stability state, drains one aspect point nearest-first, waits 200 ticks after a failed source pass, spends six cycles per component, preserves component remainders, carries damaged-catalyst ratio to an undamaged damageable result, completes on the following cycle, and emits completion/failure sounds. Candle/skull/pedestal symmetry follows legacy diminishing-return and mismatch rules. Exact stability categories/loss/clamp/trigger math and all 24 event effects are active. Flux Goo, Flux Taint/Vis Exhaust, inlay charge propagation, Stabilizer energy/recharge and pre-mutation pedestal mitigation are implemented. Runtime audit passes `95/95`.
- Clientbound equivalents of legacy `PacketFXEssentiaSource` and `PacketFXInfusionSource` preserve the 32-block send range, source fields, payload coalescing and 15/60-tick lifetimes. The billboard bridge has been replaced by the legacy-shaped matrix BER/crafting halo, persistent eight-sided tube streams and item/block/entity source effects. Final pixel-level active-altar comparison is still required.

### Item and equipment behavior

- The first safe item/equipment behavior pass is active for already registered legacy ids. `goggles` is now a real `ArmorItem` helmet with legacy `350` durability, rare rarity, thaumium repair, `IVisDiscountGear` `5%`, `IGoggles`, and `IRevealer` contracts.
- `cloth_boots`, `cloth_legs`, and `cloth_chest` are real armor pieces with legacy `2/3/3%` vis discounts, uncommon rarity and fabric repair. `void_robe_helm`, `void_robe_chest`, and `void_robe_legs` are real armor pieces with legacy `5%` discount each, `IWarpingGear` `3` each, void-ingot repair, epic rarity and one-point-per-second self repair; only the helm reveals nodes/popups like legacy.
- `sanity_checker` is the real modern item id. The legacy `sanitychecker` key remains a recipe/page id only, and the arcane recipe now outputs `thaumcraft:sanity_checker`.
- `sane_soap` has the legacy use-duration/blocking action boundary and server-side warp cleansing behavior for the current warp store: after a full use it consumes one item, reduces normal warp by one plus the legacy Warp Ward/Purifying Fluid bonuses, and clears temporary warp.
- `curio_rites` carries the legacy curio/rites metadata component and enforces the legacy `actual warp > 20` Crimson Rites gate before completing `CrimsonRites`, granting Eldritch/random knowledge, adding normal/temporary/possible permanent warp and consuming the item outside creative mode.
- `TCEquipmentHelper` provides a no-hard-dependency bridge for armor plus future accessory-provider stacks. The item/equipment runtime audit passes `17/17`; final armor model geometry, Curios slot integration, rechargeable caster tiers and bauble-specific movement/status effects remain separate subsystem work.

### Focus, caster and Focal Manipulator

- The first focus/caster core is active for `caster_basic`, `focus_1`, `focus_2`, `focus_3` and `focus_pouch`. Focus package state and caster-selected focus state are stored with Data Components, not copied legacy NBT/classes.
- The registered legacy focus element set is represented as root plus 20 TC6 element definitions. Complexity/default-setting math, duplicate element complexity multiplier, crystal costs, focus color, vis cost `complexity / 5.0F`, activation time and sorting hash inputs are covered by the focused runtime audit.
- `caster_basic` consumes aura from the legacy area-0 current chunk path, applies raw vis-discount gear to caster costs with the legacy 10% minimum consumption clamp, stores its selected focus stack component, sends aura HUD data while selected, applies focus activation cooldown, and executes the first audited `ROOT -> TOUCH -> FIRE` cast-effect path.
- `TCFocusCastExecutor` now owns the first server-side medium/effect execution boundary. Unsupported focus packages fail before vis/aura drain; supported caster use drains vis and sets cooldown; the Fire effect applies legacy `(3 + power) * finalPower` entity damage, `(1 + duration^2) * finalPower` fire duration and side-offset block fire placement for duration-bearing casts.
- `wand_workbench` now owns the Focal Manipulator BlockEntity/menu/screen boundary: one focus slot, validated design-intent payload, research checks, focus max-complexity check, XP/crystal consumption, timed 20-vis aura drain, Workbench Charger 3 x 3 aura drain support, saved pending craft state and focus package/name writeback.
- Current validation: `TCFocusCasterCoreAudit` passes `10/10` and writes `07_Test_Instance_and_Comparisons/focus_caster/thaumcraft_1_21_focus_caster_core_audit.md`; `TCFocusCastExecutionAudit` passes `5/5` and writes `06_docs/audits/generated/thaumcraft_1_21_focus_cast_execution_audit.md`. Remaining cast effects, projectile/cloud/mine behavior, Focus Pouch inventory GUI and final Focal Manipulator editor visual parity remain deferred.

### Custom entity foundation

- The first custom entity registry blocker is active without broad mob/focus/golem implementation. `TCEntityTypes` owns the modern `DeferredRegister<EntityType<?>>` boundary and preserves the exact 43-entry legacy `ConfigEntities.registerModEntity` catalog as explicit metadata with legacy id, class, tracking range, update interval, velocity flag, status and blocker notes.
- `SpecialItem` and `FollowItem` are implemented as real NeoForge item-entity types: `thaumcraft:special_item` preserves the legacy `0.25 x 0.25` item entity size, tracking `64`, update interval `20`, velocity updates, upward/anti-gravity tick adjustment and explosion immunity; `thaumcraft:follow_item` preserves the same size/tracking/update values, disables velocity updates like legacy, carries target coordinates/type spawn data and follows the legacy target-approach/damped-stop movement contract.
- Both active item-entity types use the vanilla item entity renderer for now. `falling_taint` uses a block-model renderer foundation for the falling crust block. Newly server-complete taint mobs use explicit no-op renderer placeholders until measured renderer/model parity is implemented. The remaining deferred legacy custom entities are classified by owning subsystem: projectiles, focus projectile/cloud/mine, constructs/turrets, Eldritch/Crimson mobs and golems.
- `FluxRift` is now registered as `thaumcraft:flux_rift` with legacy tracking `64`, update interval `20` and no velocity updates. It keeps the legacy seed/size/stability/collapse data contract, rebuilds seeded points/width geometry, clamps stability to `[-100, 100]`, ticks the basic aura growth/collapse path and exposes `TCVoidSiphonRiftAccess` so Void Siphon consumes real rift entities.
- `ArcaneBore` is now registered as `thaumcraft:arcane_bore` with legacy tracking `64`, update interval `3` and velocity updates. The flattened `arcane_bore` item replaces the legacy turret metadata variant for placement, spawns an entity with owner/facing intent, opens a one-pickaxe-slot GUI, charges from aura vis, reacts to redstone below and mines with a server-owned target/delay/drop path.
- `BottleTaint` is now registered as `thaumcraft:bottle_taint` with legacy tracking `64`, update interval `20`, velocity updates and a `0.25 x 0.25` projectile size. Its item owns the legacy stack/use/throw constants and the projectile owns the Flux Taint splash plus Flux Goo placement side effects.
- `FallingTaint` is now registered as `thaumcraft:falling_taint` with legacy tracking `64`, update interval `3`, velocity updates and a `0.98 x 0.98` entity size. The focused row-11/14 slice ports crusted-taint direct and side-overhang fall gates, `canFallBelow` log/flux-goo/replaceable/fluid rules, first-tick source removal, TC6 gravity/damping, landing placement, timeout discard, NBT persistence and the GORE landing sound path.
- `Wisp` is now registered as `thaumcraft:wisp` with legacy tracking `64`, update interval `3`, no velocity updates, `0.9 x 0.9` size, dynamic aspect `Type` persistence, legacy random primal/compound type initialization and crystal drop identity. Its focused row-14/17 slice now ports the legacy free-flight waypoint AI, hurt aggro cooldown, player retarget cooldown, chase motion, zap attack cadence/damage/sound, ambient motes, billboard render layers, source/target-id zap FX payload and the exact legacy Nether natural-spawn row (`#minecraft:is_nether`, weight `5`, `1-1`) behind the `allowSpawnWisp` config gate. Non-Wisp natural spawn tables and measured in-world renderer pixel parity remain separate validation work.
- `CultistPortalLesser`, `CultistKnight`, `CultistCleric`, `MindSpider`, `EldritchGuardian`, `EldritchOrb` and `GolemOrb` are now registered as real WarpEvents outcome/mob/projectile foundations with legacy tracking/update/velocity values, sizes and server contracts. Lesser portal preserves active state, collision damage, fire immunity, no-push/no-move behavior, spawn timing budget, nearby-cultist difficulty budget, Knight/Cleric selection, spawn event and self-damage behavior. Cultist Knight and Cleric preserve their first server-side legacy attributes, team rules, spawn data, scan keys and custom aspects; Cleric now fires the legacy red targeted GolemOrb branch. Mind Spider preserves harmless/viewer state, fake lifespan and explicit legacy custom aspects. Eldritch Guardian preserves attributes, team rules, spawn gates, sound ids, the no-gravity orb branch and the wither/temporary-warp sonic curse branch; EldritchOrb preserves lifetime, impact AoE, magic damage multiplier, Weakness payload and source-informed particle/tendril renderer contract. GolemOrb preserves no-gravity homing, red/blue lifetime and damage multipliers, reflect-on-hit behavior, shock/zap sounds and the legacy electric-orb particle renderer foundation.
- The first taint mob foundation is active. `thaum_slime`, `taint_crawler`, `taintacle`, `taintacle_tiny` and `taint_swarm` are registered with legacy tracking/update/velocity contracts. The server slice covers crawler fibre trail, Flux Taint bite, `taint_feature` break-spawn crawler hook, `taint_geyser` swarm spawn hook, taintacle tiny-spawn/lifetime, swarm summoned/damage-bonus NBT, Thaumic Slime size/xp and ranged split foundation, and legacy scan/aspect identities where TC6 source has explicit data.
- Current validation: `TCEntityFoundationAudit` passes `41/41` and writes `06_docs/audits/generated/thaumcraft_1_21_entity_foundation_audit.md`; `TCWarpEventBehaviorAudit` passes `35/35`; `TCSpecialAlchemyBehaviorAudit` passes `24/24` and writes `06_docs/audits/generated/thaumcraft_1_21_special_alchemy_behavior_audit.md`. The focused Flux/Bore/Thaumatorium blocker audit passes `11/11` and writes `06_docs/audits/generated/thaumcraft_1_21_flux_bore_thaumatorium_blocker_audit.md`; the focused Flux Rift consequence audit passes `11/11` and writes `06_docs/audits/generated/thaumcraft_1_21_flux_rift_consequence_audit.md`; the focused Wisp behavior/render contract audit passes `11/11` and writes `06_docs/audits/generated/thaumcraft_1_21_wisp_behavior_audit.md`; the focused Wisp Nether spawn-policy audit passes `10/10` and writes `06_docs/audits/generated/thaumcraft_1_21_entity_spawn_policy_audit.md`; the focused taint ecology blocker audit passes `15/15`; the focused taint mob blocker audit passes `14/14` and writes `06_docs/audits/generated/thaumcraft_1_21_taint_mob_blocker_audit.md`; the focused FallingTaint blocker audit passes `10/10` and writes `06_docs/audits/generated/thaumcraft_1_21_falling_taint_blocker_audit.md`. This does not claim final custom mob renderer/model/particle pixel parity, exact BottleTaint break FX pixel parity, exact FallingTaint landing particles/render pixel parity, non-Wisp natural spawn placement, exact Guardian/orb/cultist/portal renderer pixel parity, Flux Rift focus-cloud event execution, full turrets or golem logistics parity.

### Flux Goo, Taint Fibre and Taint Ecology

- The finite Flux Goo world-mutation blocker now includes the legacy level-zero alternate result. `flux_goo` still decrements and pollutes aura as before, but when its level-zero random branch does not remove the block, it resolves to real `thaumcraft:taint_fibre` instead of doing nothing.
- `taint_fibre` is registered as a block and BlockItem with the legacy ten boolean state properties `north/east/south/west/up/down/growth1/growth2/growth3/growth4`, deterministic `pos.asLong()` growth selection, support-derived face flags, legacy 0.05-block face strips, the four legacy growth AABBs, growth light levels `12/6/6`, no standard loot table and non-undead walk taint.
- The legacy multipart resources are modernized to `thaumcraft:block/...` paths so the active model no longer points at pre-1.13 shorthand ids.
- The first TaintSeed/terrain ecology slice is active. `taint_seed` and `taint_seed_prime` are real registered entities with legacy tracking/size intent, immobile seed behavior, boost/state persistence, duplicate exclusion, seed-radius bookkeeping and Flux Taint healing through the `ITaintedMob` marker. `TCTaintHelper` owns near-seed pruning and legacy-shaped spread transforms for surface fibre, dirt, stone, logs, leaves/features, crust and edge seed spawning.
- Taint terrain blocks are real blocks/BlockItems: `taint_crust`, `taint_soil`, `taint_rock`, `taint_geyser`, `taint_log` and `taint_feature`. Their active blockstates/models/item models use modern paths. The TaintSeed renderer is intentionally a safe no-op placeholder; exact animated model parity remains separate row-17 visual/entity-render work.
- Crusted taint now uses the focused FallingTaint path: random ticking can spawn `falling_taint`, source crust is removed on the first entity tick, TC6 fall constants are preserved, and landing restores taint crust when supported.
- Current validation: `TCFluxTaintBlockerAudit` passes `13/13` and writes `06_docs/audits/generated/thaumcraft_1_21_flux_taint_blocker_audit.md`; `TCTaintEcologyBlockerAudit` passes `15/15`; `TCTaintMobBlockerAudit` passes `14/14`; `TCFallingTaintBlockerAudit` passes `10/10`; `TCFluxRiftConsequenceAudit` passes `11/11`; `TCWispBehaviorAudit` passes `11/11`; `TCEntitySpawnPolicyAudit` passes `10/10` for the Wisp Nether row. Full custom taint mob renderers/animations, exact Wisp/FallingTaint landing particles/render pixel parity, non-Wisp natural spawn placement and Flux Rift focus-cloud event execution are still deferred.

### Bridge recipe cleanup and identity alignment

- Recent bridge cleanup replaced duplicated caster-shaped placeholder JSONs across transport, alchemy/essentia, utilities, infusion support, pedestal/jar/artifice/vis/stability/grapple/banner/focus-pouch/mind/turret, robe, sanity checker, and golem-module recipe families.
- Explicit bridge identities now cover legacy mind metadata 1, primordial pearl, `brass_nugget`, and `quicksilver_nugget`; Advanced Crossbow and Advanced Alchemical Construct placeholders are resolved.
- Recipe registry/tag audit tooling verifies local Thaumcraft item/block identities, simple item registrations, and lowercase legacy tag bridges.

### Assets and visuals

- Active resource/mining-tag cleanup and legacy-id alignment are current for registered candle, tube, smelter auxiliary, charger, nitor, creative-tab, and `smelter_basic` model paths. Tube blocks now use six-direction legacy multipart topology over modern model paths instead of static/full-cube placeholders; the normal tube item uses the legacy 2D icon.
- The item/block `visual` audit preset is the current local visual evidence pass. It checks registered item model/placeholder risk, legacy non-full/custom shape evidence, source-backed facing/occlusion/outline/collision contracts, block-item display transforms, active texture SHA/color/alpha against legacy `textures/blocks`/`textures/items`, and sound/particle/FX evidence before `visual_equivalence_completion` allows any strict visual certification discussion.
- Fresh in-game visual review is still separate from recipe cleanup and should be performed before treating visual parity as final.

### Essentia transport

- The six registered tube variants have server tickers, exact normal/buffer capacities (`1`/`10`), two/five-tick cadence, compatible suction propagation, restrict halving, filter/one-way/valve/choke rules, six persisted open sides, facing/filter/flow state, reciprocal side closure and comparator output for the buffer.
- Tube and Warded Jar sided access is exposed through `TCEssentiaCapabilities.BLOCK`; internal neighbor discovery uses the NeoForge capability boundary.
- Warded Jar preserves top-only access, capacity `250`, suction `32`/filtered `64`, all-or-nothing transport take and one-point pull every five ticks.
- Alembic and all three smelter tiers now have their real server-owned machine boundary: two-slot inventory, aspect slurry conversion, exact tier efficiency/output cadence, Alumentum fuel boost, cumulative vent mitigation, matching-first Alembic columns, attached auxiliary output, sided item automation and legacy-layout menu/screen.
- Alembic/Jar label filters, empty phial extraction, filled-phial-to-jar transfer, jar-item filling from Alembics, caster tube sub-part side/choke/facing controls, manual/redstone valve state and vent sync are implemented over modern Data Components and BlockEntity sync.
- Essentia Input and Essentia Output are now real blocks/BlockItems behind the current `essentiatransportin` / `essentiatransportout` recipe ids. They use the legacy `essentia_input` / `essentia_output` model assets, clicked-face placement, exact six-facing AABBs, sided capability only on the back face, input suction `128`, output suction `0`, the legacy 16-block source prism and five-tick one-point transfer cadence.
- Void Jar is now a real `jar_void` block/BlockItem with its own BlockEntityType, top transport capability, legacy filtered/unfiltered suction behavior while full, overflow clamping to `250`, and modern item fill predicate resources. The `jarvoid` recipe id remains the legacy recipe/page key, but its active output is now `thaumcraft:jar_void`.
- Essentia Mirror is now a real wall-mounted `mirror_essentia` block/BlockItem with legacy six-facing AABBs, Data Component link payloads, bidirectional link restore, remote source-zone aspect bridge, one-point add/drain semantics, instability flux cadence, and no direct pipe endpoint capability.
- Normal item Mirror and Magic Hand Mirror now have a real standalone device slice: `mirror` is a wall-mounted Block/BlockItem/BlockEntity using the same legacy six-facing AABBs, linked-block-item Data Component preservation, bidirectional link restore, item-entity transport to the remote output queue, delayed ejection and instability flux behavior. `hand_mirror` links to normal mirrors and opens a one-slot sender menu/screen that rejects mirror self-insertion, sends stacks to the linked mirror and breaks stale links when the target is missing. The dedicated runtime audit passes `12/12`.
- Arcane Lamp, Lamp of Growth and Lamp of Fertility now have a real standalone device slice. `lamp_arcane`, `lamp_growth` and `lamp_fertility` are BlockItems backed by a shared server-owned lamp BlockEntity; they preserve the legacy non-full lamp shape, enabled light value, facing/support/redstone boundary, Arcane Lamp `effect_glimmer` placement/removal, Growth Lamp Herba suction/charge/reserve-style plant tick behavior and Fertility Lamp Desiderium suction/animal-love behavior. The dedicated runtime audit passes `10/10`.
- Infernal Furnace now has a real standalone device runtime slice. `infernal_furnace` is a BlockItem-backed half-height/lighted/facing block with a 32-slot top-only BlockEntity buffer, legacy-style center nudge/lava contact, vanilla smelting lookup, front ejection or adjacent item-handler output, XP award, aura-speed drain, distance-2 Bellows acceleration formula, non-smeltable destruction, and an internal legacy default bonus table that targets flattened nugget/chunk outputs. The dedicated runtime audit passes `12/12`. Salis Mundus now implements the legacy IDustTrigger-style Infernal Furnace multiblock activation, hidden nether-brick/obsidian placeholders, iron-bars output clearing and structure rollback to lava/nether brick/obsidian/iron bars.
- Void Siphon now has its first real standalone device slice. `void_siphon` is a BlockItem-backed block with the legacy main/base/top/orb shape contract, redstone-driven `enabled` state, one extract-only void-seed output slot, persisted progress/counter, a minimal legacy GUI texture screen, the exact `sqrt(riftSize)` progress and `stability -= sqrt(size)/15` drain math, 1/33 shrink chance hook and a future `TCVoidSiphonRiftAccess` adapter for real Flux Rift entities. The dedicated standalone-device blocker audit passes `9/9`. Full Flux Rift spawning/lifecycle/rendering is still deferred.
- The combined transport/machine runtime audit passes `61/61`, including venting pause, multipart state, capability visibility, NBT round-trip, label/filter sync, phial transfer quanta, caster sub-hit controls, Void Jar overflow/suction, essentia mirror link/source checks, transfuser shape/capability checks, input-to-remote-container transfer and output-from-remote-container transfer.
- Thaumatorium has its first real advanced-alchemy machine foundation and functional recipe-selection screen. `thaumatorium` is now a placeable BlockItem that forms a bottom/top two-block machine over a crucible; `thaumatorium_top` is an invisible delegate block. The bottom BlockEntity owns the catalyst slot, selected crucible recipe ids, stored essentia, heat/redstone state, current missing-aspect suction and completion/ejection. The top BlockEntity delegates sided item and essentia input to the bottom. The screen uses the legacy Thaumatorium GUI texture, lists researched catalyst-matching crucible recipes from the current client knowledge cache, sends only server-validated recipe toggle intents, shows selected capacity/heated state, and displays the current suction aspect/storage when synced. The server behavior audit passes `19/19`, covering registration, hidden importer/exporter alias policy, heat two blocks below, front-face exclusion, sided capabilities, top delegation, `thaumcraft:alumentum` recipe fixture loading, research-gated recipe listing, server-side add/remove toggle validation, suction `128`, manual input clamping, one-point tube pull, redstone pause, craft completion and the current mnemonic-matrix placeholder slot bonus.
- Bellows, lamps, Infernal Furnace, Salis Mundus Infernal Furnace activation, Void Siphon, Arcane Spa and Everfull Urn now have focused device slices with dedicated runtime audits. Remaining standalone-device blockers are device families still represented only by bridge/page ids or visual-only reviews; final measured device item/block pixel parity remains visual review work.
- Flux Rift / Arcane Bore / Thaumatorium blocker update: Void Siphon now consumes real `thaumcraft:flux_rift` entities through `TCVoidSiphonRiftAccess`; Arcane Bore has its first real placer item, entity, pickaxe menu and server mining loop; Thaumatorium now uses the legacy OBJ/MTL asset instead of the cube fallback and has a BER that renders the selected recipe output at the front. `TCFluxBoreThaumatoriumBlockerAudit` passes `11/11`, including a guard against legacy `textures/blocks` MTL paths. Full Flux Rift event consequences, full turrets and measured Thaumatorium pixel parity remain deferred.
- Final measured valve/vent pixel parity remains an in-client visual review task.

### Deferred boundaries

- Remaining alchemy automation families beyond Arcane Spa and Everfull Urn, crucible-derived aspect generation, Spa GUI/Botania support, Brain-in-a-Jar animated brain/pixel parity, exact Thaumatorium model/renderer/pixel parity, special void-jar stack-copy crafting, exact FallingTaint landing particles/render pixel parity, final taint mob renderer/animation/natural-spawn parity, custom projectile/construct/golem AI and renderers, broad worldgen, broad rendering polish, full equipment/Curios discount integration, remaining dependency-heavy recipe/page families, exact client fluid particles/render translucency, WarpEvents client hallucination/stress visuals, exact Guardian/orb/cultist/portal renderer pixel parity, and final measured valve/vent/mirror/Spa/Thaumatorium visual parity remain deferred.

## Purpose

This is the current implementation status document. Use it together with the migration guide before starting new work. The `State Snapshot` and `High-level status` sections record current state; `Changelog Notes` records historical updates. Older planning files remain useful, but some status sections are behind the actual code.

## Document priority

1. `06_docs/migration/NeoForge_legacy_migration_guide.md` - main architecture guide.
2. `06_docs/current_port_status.md` - current repository status.
3. `06_docs/CURRENT_TASK.md` - live task queue and immediate guardrails.
4. `06_docs/documentation_index.md` - docs folder navigation and cleanup rules.
5. `06_docs/migration/migration_matrix.md` - subsystem matrix and gate rules.
6. `06_docs/migration/porting_order.md` - staged roadmap.
7. `06_docs/migration/remaining_subsystem_unblock_plan.md` - current blocker-removal order after Bellows.
8. `06_docs/resources/creative_tab_order_reference.md` - creative tab order rules.
9. `06_docs/migration/subsystem_inventory.md` - legacy subsystem audit.
10. `06_docs/data/aspects/aspect_assignment_tag_audit.md` - exact OreDictionary-to-tag audit for aspect assignments.
11. `06_docs/data/aspects/aspect_generate_tags_audit.md` - exact legacy recipe-derived aspect generation audit and blockers.
12. `06_docs/data/aspects/aspect_assignment_data_format.md` - current data-driven aspect assignment format.
13. `06_docs/data/aspects/vanilla_aspect_policy.md` - policy for exact vanilla seeds, legacy OreDictionary tag bridges, and 1.21-only content.
14. `06_docs/data/aspects/vanilla_1_21_aspect_assignments.md` - complete current manual 1.21 vanilla assignment table and rationale.
15. `06_docs/data/aspects/vanilla_post_1_12_aspect_rationale.md` - complete modern-only/flattened/component stack table with aspect amounts and rationale.
16. `06_docs/data/aspects/aspect_legacy_gap_audit.md` - gap audit against 1.12 legacy and the rough 1.20.1 attempt.
17. `06_docs/data/aspects/aspect_generated_cache_design.md` - generated aspect stack key/cache scaffold and invalidation rules.
18. `06_docs/data/aspects/aspect_legacy_runtime_logic_audit.md` - detailed 1.12 runtime aspect lookup/bonus/generation/scanning audit.
19. `06_docs/data/aspects/aspect_parity_comparison_harness.md` - runtime dump and comparison method for 1.12.2 vs 1.21.1 aspect parity.
20. `06_docs/gameplay/aura_design.md` - server-side aura storage/query/tick design for the first aura slice.
21. `06_docs/research/research_knowledge_scanning_design.md` - current research/knowledge/scanning design slice.
22. `06_docs/research/research_table_scribing_tools_design.md` - first research table/scribing tools BlockEntity slice boundary.
23. `06_docs/research/research_progression_parity_audit.md` - exact research progression, warp, reward, addendum, and data-parity checkpoint.
24. `06_docs/research/thaumonomicon_ui_design.md` - server-authoritative item/open/browser/entry UI boundary and deferred recipe-page scope.
25. `06_docs/research/scanning_parity_validation.md` - runtime dump and comparison method for scan predicate parity.
26. `06_docs/data/aspects/entity_aspect_assignment_audit.md` - entity aspect assignment parity/policy audit for scanning.
27. `06_docs/rendering/rendering_model_pipeline_audit.md` - model/resource/rendering pipeline audit for 1.12 -> NeoForge 1.21.1.
28. `06_docs/migration/gate1_items_plan.md` - historical Gate 1 workflow, not a full current inventory.

## High-level status

| Area | Status | Notes |
|---|---|---|
| Gate 0 bootstrap | Complete enough to continue | NeoForge module exists in `05_neoforge_port`; Java 21 and ModDevGradle are configured. |
| Main mod class | Implemented | `Thaumcraft` registers blocks, block entities, items, creative tabs, config, and current event listeners. |
| Item registry | Partially implemented | More than the original Gate 1 item slice exists. |
| Block registry | Partially implemented | Simple blocks, ores, stones, wood blocks, plants, all sixteen legacy `candle_<color>` ids, current smelters, and all six legacy-named tube blocks exist; tube multipart connection state and shapes are active. |
| Creative tab | Implemented, needs visual review | `TCCreativeTabOrder` owns visible order. Do not use registry order. |
| Assets | Runtime audited for active content | Missing original `assets` files were copied into the port without overwriting adapted 1.21 resources. Registered active content has model/lang/blockstate/loot coverage; `amber`, `quicksilver`, `fabric`, `scribing_tools`, `thaumonomicon`, `table_wood`, `table_stone`, `research_table`, `iron_plate`, `brass_plate`, `thaumium_plate`, `void_plate`, `mechanism_simple`, `mechanism_complex`, and `vis_resonator` item/model texture paths were fixed from legacy `items/`/`blocks/` to modern `item`/`block`, with active PNGs copied into `textures/item` or `textures/block`. Active research text required by the first Thaumonomicon screen is merged into modern `en_us.json`. The `2026-06-17` identity cleanup realigned active candle, tube and smelter-auxiliary resources to legacy public ids while keeping modern 1.21 blockstate/model paths authoritative. `thaumometer` now uses the legacy 3D `scanner.obj` through the NeoForge OBJ loader with modern `textures/item/thaumometer.png`, alpha-pane `textures/item/scanscreen.png`, explicit OBJ texture aliases and translucent render type; in-game transform tuning still needs visual comparison against 1.12.2. |
| Loot tables | Active registered content covered | Modern simple-block loot tables exist under `data/thaumcraft/loot_table`; legacy `assets/thaumcraft/loot_tables` is imported as reference material and is not the 1.21 data path. |
| Tags | Aspect tag audit expanded | Tags replace old `OreDictionary` patterns. `aspect_assignment_tag_audit.md` maps current legacy aspect-related keys; safe current common tag resources exist for amber/cinnabar/quartz ores, amber gems, vanilla ore/gem/ingot/dust/raw-material bridges, plate bridges, brass/thaumium ingots, rods/wooden, and copper material bridges; exact `thaumcraft:legacy_ore_dictionary/*` item/block tags now preserve all already registered 1.12 OreDictionary entries, including `plateIron`, `plateBrass`, `plateThaumium`, `plateVoid`, `stickWood`, `ingotGold`, `ingotBrass`, `ingotThaumium`, and `nitor` for current exact arcane recipes, plus lowercase `coal`, `dust_glowstone`, `ingot_iron`, and `nugget_quartz` bridges for current crucible page-data recipes. |
| Aspects | Core/API slice parity-clean for comparable stacks/entities, with two documented modern entity policy rows | `Aspect`, `AspectList`, pure `AspectHelper` logic, reload-safe data-driven exact/tag/manual assignments, vanilla material tag bridges, crafting and current arcane generated-cache slice, legacy stack-sensitive bonus rules, component-aware potion and enchanted-book lookup, spawn-egg exclusion, vanilla entity aspect assignments, bootstrap parity validation, server-data-load tag validation, OreDictionary-to-tag audit, `generateTags` audit, read-only Shift inventory tooltip rendering, and assignment/cache/manual-policy docs are implemented/documented. All assignable current `minecraft:*` item ids have aspects after reload validation; spawn eggs, firework star/rocket, infested blocks, and empty component-only potion carriers remain intentionally excluded for legacy runtime parity. Current registered Thaumcraft option items used by theorycraft now include dump-derived exact values for `alumentum`, `salis_mundus`, `brain`, `nitor_yellow`, `thaumium_ingot`, and `brass_ingot`. `filter` and `morphic_resonator` now reload-validate generated arcane recipe aspects, including the legacy `praecantatio` bonus from vis. `elder_guardian` and `zombie_villager` now have documented living-mob aspect rows as intentional modern-policy corrections. The runtime dump harness now runs on both original Forge 1.12.2 Thaumcraft and the 1.21.1 port; the comparers separate expected version differences from real port gaps. Current comparable item aspect parity is `1139/1139` with `0` amount/set/order/kind/null gaps; current comparable entity scan report has `83/85` fully parity-ok rows plus `2` expected modern entity aspect policy rows and `0` actionable gaps. Current registered Thaumcraft custom entity aspect rows are explicit for the active WarpEvents/taint slices; crucible recipe-page aspects are data/display costs only; crucible-derived `generateTags`, infusion, essentia transport, remaining unregistered custom entity aspects, and gameplay-heavy consumers remain blocked until their own design slices. |
| Aura | Started with server-side core | `AuraHelper`, per-level `SavedData`, chunk `base/vis/flux`, automatic chunk initialization, legacy formula for aura base generation, main-thread 20-tick legacy-like update loop, permission-level-2 debug commands, Flux Rift entity consumer/source bridge, Flux Rift consequences except the focus-cloud owner event, Flux Goo/Taint Fibre and first TaintSeed/terrain ecology consumers are implemented. The biome category mapper is legacy-like because 1.21 has no `BiomeDictionary`. HUD sync, broad aura FX, research-aware preservation, final rift visuals and taint mob ecology remain intentionally narrow or deferred. |
| Research | Progression/page-catalog core parity-closed; first real Thaumonomicon UI, vanilla, arcane, crucible, infusion and fake display page snapshots active | Player knowledge storage, reload-safe research data, requirements, scan-key mutation, table/theorycraft slice, GUI-ready knowledge sync, permanent research recipe/page catalog, server-authoritative warp mutation/sync, first server-side WarpEvents behavior slice, server-authoritative revision-gated Thaumonomicon protocol, real Thaumonomicon item/open flow, browser, entry screen, server-visible browser search/filter, server-owned recipe drilldown/history, vanilla crafting-page renderer, first arcane recipe page renderer, first crucible recipe page renderer, infusion recipe page renderer, and legacy fake/display page renderer exist. Checked stage completion preserves exact legacy ordering. Browser start preserves `first=true`/`checks=false`/`noFlags=true`, stage advance preserves `first=false`/`checks=true`/`noFlags=true`, and entry acknowledgement clears `RESEARCH`/`PAGE` before attempting the legacy known-entry final-stage checked progression with `noFlags=false`. The research data harness reports `148/148` entries and `10/10` semantic checks. The page catalog reports `253` research occurrences, `203` direct references, `325` total entries including group members, and `0` parity/structural differences; latest live availability is `203 READY`, `0 DEFERRED`, `0 LEGACY_MISSING`. The protocol audit passes `36/36`; stale client action and drilldown revisions are rejected without progression mutation, and live vanilla crafting, arcane, crucible, infusion and fake display catalog entries produce valid server-resolved snapshots. WarpEvents client hallucination/stress visuals, cancellable research/knowledge events, final visual/search pixel parity, and the remaining blueprint/special recipe-page systems/renderers remain blocked. |
| Recipes | Vanilla crafting fixtures, research bridges, arcane workbench path, crucible path, and infusion active-cycle/stability boundary started | Simple modern `data/thaumcraft/recipe` crafting recipes exist for generated-aspect validation and the first research table slice: `tablewood`, `tablestone`, `scribingtoolscraft2`, `scribingtoolsrefill`, and exact legacy `ironplate`, `brassplate`, and `thaumiumplate` recipes. The candle/tube/smelter-auxiliary cleanup keeps legacy recipe filenames where useful, but all active outputs now resolve to legacy public ids such as `candle_white`, `tube_buffer`, `tube_filter`, `tube_oneway`, `tube_restrict`, `tube_valve`, `smelter_aux`, and `smelter_vent`. Remaining `data/thaumcraft/recipe/research_bridge` recipes make registered/placeholder research requirement outputs craft-detectable for the modern `required_craft` marker path, but exact arcane recipes remove their obsolete bridges. The custom arcane boundary exists: `thaumcraft:arcane` `RecipeType`, `thaumcraft:arcane_shaped`/`thaumcraft:arcane_shapeless` serializers, public `IArcaneRecipe`, public `IArcaneWorkbench` marker, and the complete regular legacy `89/89` arcane recipe id set. The audit enforces all loaded arcane recipes building page snapshots, non-empty result ids, and the legacy robe outputs `robeboots -> cloth_boots`, `robechest -> cloth_chest`, `robelegs -> cloth_legs`. Arcane Workbench server crafting now has research/vis/crystal checks, no-charger current-chunk aura drain, Workbench Charger 3x3 aura query/drain, legacy `IVisDiscountGear` discount path for goggles, cloth robes, void robes and an external accessory-provider bridge, atomic output-take consumption, crystal consumption, vanilla 3x3 fallback, missing-vis ghost output, and server-owned menu feedback for base/discounted cost, available aura and required crystal slots. Current `TCArcaneRecipe` outputs feed the generated aspect cache with the exact legacy ingredient formula and vis-derived `praecantatio` bonus. The custom crucible page boundary now exists: `thaumcraft:crucible` `RecipeType`/serializer, catalyst ingredient, research key, explicit aspect costs, result stack, server page snapshot, codec/payload wiring, HEDGE_ALCHEMY, METAL_PURIFICATION, Alumentum, Nitor, Brass Ingot, Thaumium Ingot, all 37 vis-crystal data recipes, and special alchemy recipes for BottleTaint/BathSalts/LiquidDeath/SaneSoap. Legacy dynamic HEDGE costs are explicit data now, and the crucible recipe data audit reports `77/77` valid files. Current crucible in-world slices use server-owned water/heat/aspect state, manual top-side item insertion, item-entity collision absorption, hot living contact damage, existing item-aspect dissolution, research-gated highest-cost recipe lookup, aspect/water consumption, result ejection, special-result reabsorption protection, comparator output, and legacy-style spill pollution into aura flux. The focused special alchemy behavior audit passes `24/24` for Bath Salts item lifespan/water conversion, Bottled Taint projectile/Flux Taint/Flux Goo behavior, LiquidDeath/PurifyingFluid fluids/blocks and bucket mappings, Liquid Death dissolve damage/drop identity, Warp Ward behavior and Sanity Soap bonuses. The custom infusion boundary has `thaumcraft:infusion` `RecipeType`/serializer, `42/42` valid recipe JSON files, page snapshots, `TCInfusionRecipeMatcher`, `TCInfusionAssembly`, `TCInfusionValidationResult`, `TCInfusionCraftingPlan`, `TCInfusionCompletionPlan`, active pedestal blocks, one-slot pedestal BlockEntities, matrix legacy-range pedestal discovery, persisted server-owned active crafting state, two-click caster activation, bounded live surroundings refresh, and a `95/95` runtime behavior audit. The current infusion matcher uses legacy-compatible exact-count unordered component semantics. Remaining blockers: optional Curios/accessory slot adapter wiring, final Arcane Workbench GUI polish, special void-jar stack-copy behavior, final taint mob/Thaumic Slime spawn/render parity, Flux Rift focus-cloud event execution, remaining alchemy automation families, crucible-derived aspect generation, Spa GUI/Botania support, essentia/Thaumatorium integration, exact fluid particles/render translucency and remaining goo/taint/rift visual/world polish; crucible and infusion visual topology is implemented but still needs measured in-world parity review. |
| BlockEntities | Started with audited machine slices | Research Table, Arcane Workbench, Crucible and Infusion Matrix/Pedestal own their current server state. The tube family has server tickers, persisted/synced transport state and sided capability access; Warded Jar persists and transports one aspect up to `250`; Alembic stores/filters/outputs one aspect up to `128`; all smelter tiers persist inventory, slurry, fuel/cook and output state. Bellows now owns a focused device BlockEntity with target classification, client animation state, tube-buffer extension state and vanilla-furnace boost counters. Do not copy legacy `TileEntity` classes directly. |
| Menus/screens | Started with research table, Arcane Workbench, smelter, and first Thaumonomicon screens/pages | `TCResearchTableMenu`, `TCArcaneWorkbenchMenu` and `TCSmelterMenu` are server-owned modern menus. The smelter screen uses the original background, two legacy slot positions and five synced progress values. Thaumonomicon browser/entry screens consume authoritative server view models; browser search filters only the server-visible index; recipe stack click-through uses a server-owned drilldown payload and local history stack. Exact visual parity, final Arcane Workbench GUI polish, and remaining custom recipe pages remain incomplete. |
| Networking | Started narrowly | Modern custom payloads exist for aura sync, GUI-ready research knowledge sync, research-table action/state/result sync, the Thaumonomicon index/entry/action/drilldown flow and Wisp zap FX. Thaumonomicon protocol version `6` carries separate vanilla crafting, arcane, crucible and infusion recipe page snapshots plus a server-built index revision; item use sends an explicit server-owned open intent; ordinary index refreshes cannot reopen the screen; stale client entry/action/drilldown revisions force authoritative refresh without mutation. Wisp zap uses the legacy source/target-id packet contract through a clientbound custom payload. Do not treat this as a complete networking subsystem yet; every new payload still needs focused design and server validation. |
| Entities/golems | Foundation started | `TCEntityTypes` registers the first two non-mob item-entity types, `special_item` and `follow_item`, plus focused `bottle_taint`, `eldritch_orb`, `golem_orb`, `flux_rift`, `arcane_bore`, `falling_taint`, `taint_seed`, `taint_seed_prime`, `wisp`, `thaum_slime`, `taint_crawler`, `taintacle`, `taintacle_tiny`, `taint_swarm`, `cultist_portal_lesser`, `cultist_knight`, `cultist_cleric`, `mind_spider` and `eldritch_guardian` foundations, and keeps the full 43-entry legacy entity catalog explicit for future batches. Runtime entity foundation audit passes `41/41`; focused WarpEvents behavior audit passes `35/35`; focused Special Alchemy behavior audit passes `24/24`; focused Flux/Bore/Thaumatorium blocker audit passes `11/11`; Flux Rift consequence audit passes `11/11`; Wisp behavior/render contract audit passes `11/11`; Wisp Nether spawn-policy audit passes `10/10`; taint ecology blocker audit passes `15/15`; taint mob blocker audit passes `14/14`; FallingTaint blocker audit passes `10/10`. Remaining non-Wisp natural spawn placement, exact measured entity render pixel parity, exact Guardian/orb/cultist/portal renderer pixel parity, focus projectile/cloud/mine entities, full turrets and golems remain deferred. |
| Worldgen | Started early, not as a system | Sapling-grown Greatwood/Silverwood tree generators exist; biome modifiers, configured features, and structure/world placement are not implemented. |
| Rendering/FX | Started early, still high risk | Legacy-style FX dispatcher/particle scaffolding exists and `rendering_model_pipeline_audit.md` documents the 1.12 -> 1.21 resource/model split. Thaumometer right-click runes, held target highlight, and living-mob aspect icon overlay are started with legacy target ranges, wild block highlight behavior, legacy icon UV order, and separate known-vs-unknown gating. `TCResearchTableRenderer` bakes the legacy table-top models from synced BlockEntity state. Bellows has a legacy-texture BlockEntityRenderer with animated bag/planks and tube-buffer extension. Infusion has an eight-cube matrix BER, deterministic crafting halo, persistent eight-sided tube stream and item/block/entity source effects. Crucible has the synced legacy-height/recolored liquid plane, boiling/overflow/aspect particles and mutation block-event FX. Wisp now has source-informed billboard layers, ambient mote particles and a modern BufferBuilder zap-bolt equivalent of `PacketFXWispZap`. Final measured visual comparisons, broad rendering systems, BEWLR work, remaining overlays, old shader wiring, and polished research-table/card animation still require focused validation. |

## Research/scanning stabilization split

| Bucket | Current contents | Notes |
|---|---|---|
| Ready | Aspect/scan parity harnesses, reload-safe research data, current Thaumometer scan path, knowledge sync, research table and first server-authoritative Thaumonomicon flow, server-authoritative warp mutation/sync plus the legacy `jar_brain` item-warp crafting bridge, the `35/35` server-side `WarpEvents` behavior slice with Guardian/Spider/lesser-portal spawn foundations, Guardian orb projectile path, GolemOrb branch and lesser portal minion spawning, complete regular `89/89` Arcane Workbench recipe id set/behavior, crucible behavior slices, infusion recipe/page data, persistent Warded Jar/Void Jar storage, the `61/61` tube/jar/Alembic/smelter/transfuser/label/phial/caster-control/Void-Jar/essentia-mirror runtime boundary, the `12/12` normal mirror/hand mirror behavior boundary, the `19/19` Thaumatorium server-machine and recipe-selection screen boundary, the `11/11` Arcane Spa/Everfull Urn alchemy automation device boundary, the focused Bellows device/rendering audit, the audited default infusion cycle through output placement, the first focus/caster/Focal Manipulator core, the audited `ROOT -> TOUCH -> FIRE` cast execution slice, the `41/41` custom entity foundation slice, the `24/24` special alchemy item/projectile/fluid/effect behavior slice, the `11/11` Flux/Bore/Thaumatorium blocker slice, the `11/11` Flux Rift consequence slice, the `11/11` Wisp behavior/render contract slice, the `10/10` Wisp Nether spawn-policy slice, the `13/13` Flux Goo/Taint Fibre blocker slice, the `15/15` TaintSeed/terrain ecology blocker slice, the `14/14` taint mob/Thaumic Slime server-foundation slice, and the `10/10` FallingTaint crust-physics slice. | Keep these covered by build/server/client smoke checks before expanding consumers. |
| Placeholder / Bridge | Remaining research requirement bridges, unfinished advanced theorycraft cards/aids, unresolved machine identities, the classified-but-deferred legacy entity catalog, and unreviewed machine/entity visual identities. Research bridge recipes, generated parity reports, and debug-only completion commands remain tooling. | These are scaffolds for validation and migration. Do not treat them as final gameplay or visual parity. |
| Blocked | Thaumonomicon final search/visual parity, final Arcane Workbench GUI polish, optional Curios/accessory slot adapter wiring, final robe/goggles armor model geometry, special void-jar stack-copy behavior, Spa GUI/Botania support, exact LiquidDeath/PurifyingFluid/Spa client fluid particles/render translucency, exact BottleTaint/Wisp/FallingTaint/entity render pixel parity, exact Guardian/orb/cultist/portal renderer pixel parity, final taint mob renderers/particles/non-Wisp natural-spawn parity, Flux Rift focus-cloud event execution, measured infusion/mirror/Spa/Thaumatorium visual parity, blueprint/special recipe-page systems, WarpEvents client hallucination/stress visuals, fortress mask mitigation, custom projectile/construct/golem AI/renderers, remaining focus effect execution/projectiles/clouds/mines, Focus Pouch GUI and ScanSky celestial-note side effects. | These need focused design and validation slices. |
| Next subsystem | Continue non-focus blocker removal from `06_docs/migration/remaining_subsystem_unblock_plan.md`: likely next choices are remaining alchemy automation families, taint/rift visual parity, Thaumonomicon final navigation/page renderers, or the next focused entity/projectile/device family. | Focus/caster row 7 is paused by user request; do not expand medium/effect execution until explicitly resumed. |

## Legacy asset corpus import

The original Thaumcraft 6 asset tree from `03_self_decompiled_check/vineflower_thaumcraft6/assets` has been imported into `05_neoforge_port/src/main/resources/assets`.

| Asset import detail | Value |
|---|---:|
| Source asset files | `1531` |
| Files copied into the port | `828` |
| Existing port files preserved | `703` |
| Port asset files after import | `1669` |
| Thaumcraft namespace files after import | `1658` |
| Minecraft shader namespace files after import | `11` |

Import rule: do not overwrite already-adapted 1.21 resources. Some shared legacy paths differ from the current port versions, especially `blockstates`, `models/block`, and `models/item`; the port versions remain authoritative for currently registered content.

Imported legacy resources include old `.lang` files, `research`, `shader`, `sounds`, `textures/gui`, `textures/entity`, `textures/research`, OBJ/MTL models, legacy `loot_tables`, and legacy `textures/blocks`. These are reference/base assets until each subsystem adapts them to NeoForge/Minecraft 1.21.1 conventions.

The copied-file manifest is `06_docs/resources/asset_bulk_import_manifest.txt`.

The runtime asset audit is `06_docs/resources/runtime_asset_audit.md`.

## Last local validation

| Check | Result | Notes |
|---|---|---|
| `.\gradlew.bat build --no-daemon` | Passed | Latest server-side `WarpEvents` behavior slice builds successfully. Re-run after each recipe, UI, menu, networking, gameplay or asset expansion batch. |
| `.\gradlew.bat runClient --no-daemon` | Startup/resource smoke reached client resource reload and atlas creation | Client reached mod bootstrap, resource loading, sound engine startup, and texture atlas creation after the Wisp renderer/FX payload batch. The process was stopped intentionally after the smoke window, before world-join gameplay testing. Latest reviewed startup did not show a Wisp renderer/payload crash. Known deferred missing model/texture warnings for unported item families remain separate visual backlog items and are not introduced by the Wisp batch. |
| `runServer` plus focused infusion audit | Passed | Dedicated server reached `Done`; aspect bootstrap/reload validated `702` exact assignments, `46` tag assignments and `32` complex exact assignments, and coverage remained `1230/1230`. The focused infusion exporter completed and stopped cleanly with `95/95` checks. |
| Arcane Workbench behavior audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcArcaneWorkbenchAudit=true "-PtcArcaneWorkbenchAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\arcane_crafting\thaumcraft_1_21_arcane_workbench_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `28/28` checks validate distinct Arcane Workbench and Focal Manipulator (`wand_workbench`) identities, Workbench Charger support placement, empty resolution, fixed primal crystal slots, vis simulation, no-charger current-chunk aura, Charger 3 x 3 aura query/drain, missing-research fallback, missing/wrong crystal blocking, missing-vis blocking plus non-pickup/non-craftable ghost output, successful `vis_resonator` resolution, matrix/crystal/vis consumption, goggles/cloth robe/void robe/accessory-provider discount behavior, 50% public discount cap, combined discounted-cost formula, vanilla `iron_plate` fallback consumption, and server-owned menu feedback for arcane cost/aura, discounted cost, missing vis, missing crystals, and vanilla fallback. |
| Item/equipment behavior audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcItemEquipmentBehaviorAudit=true "-PtcItemEquipmentBehaviorAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\item_equipment\thaumcraft_1_21_item_equipment_behavior_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `17/17` checks validate goggles, cloth robes, void robes, sanity checker identity, sane soap warp cleansing, Crimson Rites warp gate, `curio_rites` component identity, recipe output identity and the armor/future-accessory equipment helper bridge. |
| Focus/caster core audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcFocusCasterCoreAudit=true "-PtcFocusCasterCoreAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\focus_caster\thaumcraft_1_21_focus_caster_core_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `10/10` checks validate root + 20 focus element definitions, focus/caster item identities, package math/default setting roundtrip, caster component and area-0 aura drain, Focal Manipulator BlockEntity/menu/item registries, server-side design request validation, max-complexity rejection, XP/crystal/vis start contract and package writeback. |
| Focus cast execution audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcFocusCastExecutionAudit=true "-PtcFocusCastExecutionAuditPath=D:\Thaumcraft_6_port_to_1.21.1\06_docs\audits\generated\thaumcraft_1_21_focus_cast_execution_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `5/5` checks validate `ROOT -> TOUCH -> FIRE` plan support, unsupported package fail-before-drain, supported caster vis drain/cooldown, entity fire damage/burn and duration-bearing block fire placement. |
| Thaumatorium behavior audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcThaumatoriumBehaviorAudit=true "-PtcThaumatoriumBehaviorAuditPath=audits/thaumatorium_behavior_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `19/19` checks validate the two-block Block/BlockEntity identity, hidden importer/exporter alias policy, heat source two blocks below, redstone pause, sided capabilities/top delegation, `thaumcraft:alumentum` recipe fixture, research-gated recipe list exposure, server-side select/remove toggle validation, suction `128`, manual input clamping, one-point tube pull, completion/ejection and the current mnemonic-matrix placeholder slot bonus. |
| Alchemy automation device audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcAlchemyAutomationDeviceAudit=true "-PtcAlchemyAutomationDeviceAuditPath=..\\..\\06_docs\\audits\\generated\\thaumcraft_1_21_alchemy_automation_device_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `11/11` checks validate `spa` and `everfull_urn` real block/item identities, Everfull Urn legacy shape, top-only drain/no-fill capability, aura refill, cauldron `333 mB` cost, glass bottle cost, Arcane Spa bath-salts/fluid capability contract, mix-mode Purifying Fluid placement, liquid-only source placement and 5x5 adjacent expansion. |
| Flux/Bore/Thaumatorium blocker audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcFluxBoreThaumatoriumAudit=true "-PtcFluxBoreThaumatoriumAuditPath=..\\..\\06_docs\\audits\\generated\\thaumcraft_1_21_flux_bore_thaumatorium_blocker_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `11/11` checks validate Flux Rift and Arcane Bore registration/tracking metadata, seeded Flux Rift geometry/stability clamps, real-rift Void Siphon drain, rift collapse discard, Arcane Bore pickaxe/menu/mining contracts, Thaumatorium OBJ/blockstate resource wiring, and modern Thaumatorium MTL texture paths. |
| Flux Goo/Taint Fibre blocker audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcFluxTaintBlockerAudit=true "-PtcFluxTaintBlockerAuditPath=..\\..\\06_docs\\audits\\generated\\thaumcraft_1_21_flux_taint_blocker_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `13/13` checks validate `taint_fibre` block/item ids, `flux_goo` BlockItem id/model, ten-property default state, legacy face/growth shapes, deterministic growth state, Flux Goo alternate result, walk-taint behavior, explicit TaintSeed deferral and modern resource paths. |
| Taint ecology blocker audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcTaintEcologyBlockerAudit=true "-PtcTaintEcologyBlockerAuditPath=..\\..\\06_docs\\audits\\generated\\thaumcraft_1_21_taint_ecology_blocker_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `15/15` checks validate terrain block/item ids, TaintSeed and TaintSeedPrime entity ids/type contracts, config defaults, live/stale seed-radius checks, duplicate spawn exclusion, deterministic spread transforms, Flux Taint healing/damage behavior, modern resource paths and taint-feature directional bounds. |
| Taint mob blocker audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcTaintMobBlockerAudit=true "-PtcTaintMobBlockerAuditPath=..\\..\\06_docs\\audits\\generated\\thaumcraft_1_21_taint_mob_blocker_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `14/14` checks validate Thaumic Slime, TaintCrawler, Taintacle, Tiny Taintacle and TaintSwarm ids/tracking/type contracts, legacy attribute baselines, explicit custom entity aspect assignments, scan keys, crawler fibre trail, feature break crawler spawn, geyser swarm spawn, tiny taintacle spawn, swarm NBT and Thaumic Slime size/xp. |
| FallingTaint blocker audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcFallingTaintBlockerAudit=true "-PtcFallingTaintBlockerAuditPath=..\\..\\06_docs\\audits\\generated\\thaumcraft_1_21_falling_taint_blocker_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `10/10` checks validate entity registration/tracking, legacy catalog status, GORE sound registration, `canFallBelow` replaceable/fluid/log/flux-goo rules, actual spawned falling entity, first-tick source removal, landing placement, side-overhang fall target/original-source behavior and NBT round-trip fields. |
| Wisp behavior/render contract audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcWispBehaviorAudit=true "-PtcWispBehaviorAuditPath=..\\..\\06_docs\\audits\\generated\\thaumcraft_1_21_wisp_behavior_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `11/11` checks validate registration/tracking, legacy attributes, `Type` NBT, dynamic entity aspects, scan keys, hurt aggro, wander motion, zap cadence, render texture resources, legacy billboard layer frame/scale/alpha values and the source/target-id zap payload contract. |
| Wisp Nether spawn-policy audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcEntitySpawnPolicyAudit=true "-PtcEntitySpawnPolicyAuditPath=..\\..\\06_docs\\audits\\generated\\thaumcraft_1_21_entity_spawn_policy_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `10/10` checks validate that only the Wisp Nether row is active, non-Wisp legacy spawn rows are deferred, the biome modifier uses `#minecraft:is_nether` with weight `5` and `1-1`, spawn placement is `NO_RESTRICTIONS` / `MOTION_BLOCKING_NO_LEAVES`, dark unobstructed Nether placement passes, obstructed/bright/local-cap/peaceful gates deny, and `allowSpawnWisp` owns the config switch. |
| Infernal Furnace behavior audit exporter | Passed | `tools/audits/audit-infernal-furnace-behavior.ps1` reached `Done`, wrote `06_docs/audits/generated/thaumcraft_1_21_infernal_furnace_behavior_audit.md`, and passed `12/12` checks for registration, block shape/light, top-only item capability, non-smeltable destruction, smelting/ejection/XP, aura-speed drain, Bellows distance formula and legacy default bonus candidates. |
| Essentia transport behavior audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcEssentiaTransportBehaviorAudit=true "-PtcEssentiaTransportBehaviorAuditPath=audits/essentia_transport_behavior_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `61/61` checks confirm the combined tube/jar/Alembic/smelter/transfuser/Void-Jar/essentia-mirror contract still passes after Thaumatorium capability registration. |
| Arcane recipe audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcArcaneRecipeAudit=true "-PtcArcaneRecipeAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\arcane_crafting\thaumcraft_1_21_arcane_recipe_audit.md"` reached `Done`, wrote the audit, and stopped automatically. Current `109/109` checks validate `89/89` regular legacy arcane recipe ids, no missing/extra ids, page snapshot construction for every loaded arcane recipe, non-empty result ids, and the legacy cloth robe outputs. |
| Infusion behavior audit exporter | Passed | The focused exporter passes `95/95`. It verifies all 24 executable event dependencies, Flux Goo placement, inlay charge attenuation, Stabilizer NBT/pre-mutation ejection absorption, two-click activation readiness, idle charging, live modifier refresh, active-state sync, invalid-structure deactivation and one-shot FX payload queue drain/coalescing. |
| Infusion recipe data audit | Passed | `tools/audits/audit-infusion-recipe-data.ps1` reports `42` infusion recipe files and `0` invalid files. |
| Research recipe page gap audit | Passed | `tools/audits/audit-research-recipe-page-gaps.ps1` reports `253` stage/addendum recipe page refs, `238` resolved recipe refs, and `0` missing recipe page refs. |
| `/tc research validate` | Reload-equivalent validation passed | The server reload validator reported `201` resolved entry references, `95` external scan/flag trigger references, and `0` unresolved research references. Direct console command execution from the Codex Gradle terminal did not reach the server stdin, so this row records the equivalent reload-time validation, not a typed command transcript. |
| Research requirement audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcResearchRequirementAudit=true "-PtcResearchRequirementAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_requirement_audit\thaumcraft_1_21_research_requirements.md" -PtcResearchRequirementAuditDetailLimit=200` reached `Done`, wrote the audit, and stopped automatically. Current result: `required_item=69/69`, `required_craft=34/34`, `required_knowledge=170/170`, `0` identity-unresolved requirements, and `16` remaining bridge/placeholder warnings. |
| Research table diagnostic exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcResearchTableAudit=true "-PtcResearchTableAuditPath=..\..\06_docs\audits\generated\thaumcraft_1_21_research_table_audit.md"` reached `Done`, wrote `06_docs/audits/generated/thaumcraft_1_21_research_table_audit.md`, reported `59` passed / `0` failed static checks, and stopped automatically. New checks cover `CardAwareness`, warp storage round-trip/clamping, `CardInfuse`, `CardTruth`, the basic Alchemy/Artifice/Infusion/Auromancy/Golemancy block aids, safe Eldritch glyphed-stone/Nether-portal/End-portal aids, safe Eldritch cards, GUI-ready knowledge sync cache contents, authoritative research-table action-result payloads, and the fact that Dragon Egg theorycraft code remains unregistered like original TC6. In-game `/tc research_table validate player` adds live player checks for paper, scribing-tool damage, required item checks/consumption, draw-card availability, XP-gated card activation including `CardDarkWhispers`, and finish-theory knowledge mutation. |
| Warp behavior audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcWarpBehaviorAudit=true "-PtcWarpBehaviorAuditPath=..\..\06_docs\audits\generated\thaumcraft_1_21_warp_behavior_audit.md"` reached `Done`, wrote `06_docs/audits/generated/thaumcraft_1_21_warp_behavior_audit.md`, reported `9` passed / `0` failed checks, and stopped automatically. Current checks cover legacy warp array order/clamping, manager add/reduce/set/clear behavior, sync payloads, FIRSTSTEPS-gated WARP unlock, and the legacy `jar_brain` item-warp crafting bridge with `wussMode` suppression. |
| Warp event behavior audit exporter | Passed | `.\gradlew.bat runServer --no-daemon -PtcWarpEventBehaviorAudit=true "-PtcWarpEventBehaviorAuditPath=..\..\06_docs\audits\generated\thaumcraft_1_21_warp_event_behavior_audit.md"` reached `Done`, wrote `06_docs/audits/generated/thaumcraft_1_21_warp_event_behavior_audit.md`, reported `25` passed / `0` failed checks, and stopped automatically. Current checks cover the 2000-tick scheduler constants, temporary-warp decay/trigger/counter math, exact legacy outcome thresholds including `73 -> UNNATURAL_HUNGER_LONG` and `76 -> MOMENT_OF_CLARITY`, warp-only potion ids/colors, Guardian/Spider/lesser-portal entity foundation contracts, warp entity aspect contracts, Vis Exhaust/Death Gaze/Unnatural Hunger outcome durations/amplifiers, rotten-flesh relief, normal-food rejection and BATHSALTS/ELDRITCH unlock thresholds. |
| Aspect runtime dumps | Passed mapped harness run | Original Forge 1.12.2 Thaumcraft server wrote `1798` entries; NeoForge 1.21.1 server wrote `1987` entries. With `legacy_to_modern_stack_map.json`, the comparer has `1139` comparable keys: `1139` identical, including `283` legacy-to-modern mapped parity entries. Current real mapped gaps are `0`; potion content/order, mapped Sweeping Edge stored books, and currently registered Thaumcraft set differences are closed. |
| Scan runtime dumps | Passed item-level and entity-level scan harness runs | Original Forge 1.12.2 scan exporter wrote `1798` item entries and `129` entity entries; NeoForge 1.21.1 server wrote `1987` item entries and `131` entity entries. With stack/research-key/entity-id normalization, item scans have `1139/1139` comparable parity-ok rows. Entity scans now have `83/85` comparable parity-ok rows plus `2` expected modern-policy living-mob rows (`elder_guardian`, `zombie_villager`). Both reports have `0` actionable scan key/set/found/aspect gaps. |

## Implemented identity entries seen in `TCItems`

| Group | Entries |
|---|---|
| Ores | `ore_amber`, `ore_cinnabar`, `ore_quartz` |
| Crystals | `crystal_aer`, `crystal_ignis`, `crystal_aqua`, `crystal_terra`, `crystal_ordo`, `crystal_perditio`, `crystal_vitium` |
| Stone blocks | `stone_arcane`, `stone_arcane_brick`, `stone_ancient`, `stone_ancient_tile`, `stone_ancient_rock`, `stone_ancient_glyphed`, `stone_ancient_doorway`, `stone_eldritch_tile`, `stone_porous` |
| Stairs and slabs | `stairs_arcane`, `stairs_arcane_brick`, `stairs_ancient`, `stairs_greatwood`, `stairs_silverwood`, `slab_arcane_stone`, `slab_arcane_brick`, `slab_ancient`, `slab_eldritch`, `slab_greatwood`, `slab_silverwood` |
| Wood, leaves, plants | `log_greatwood`, `log_silverwood`, `leaves_greatwood`, `leaves_silverwood`, `sapling_greatwood`, `sapling_silverwood`, `shimmerleaf`, `cinderpearl`, `vishroom`, `plank_greatwood`, `plank_silverwood` |
| Other blocks/items | `amber_block`, `amber_brick`, `table_wood`, `table_stone`, `research_table`, `arcane_workbench`, `arcane_workbench_charger`, `wand_workbench`, `golem_builder`, `smelter_basic`, `smelter_thaumium`, `smelter_void`, `infernal_furnace`, `essentiatransportin`, `essentiatransportout`, `tube`, `tube_buffer`, `tube_filter`, `tube_oneway`, `tube_restrict`, `tube_valve`, `infusion_matrix`, `arcane_pedestal`, `ancient_pedestal`, `eldritch_pedestal`, all registered `nitor_*` and `candle_*` color variants, `thaumometer`, `vis_resonator`, `goggles`, `sanity_checker`, `sane_soap`, `curio_rites`, `caster_basic`, `focus_1`, `focus_2`, `focus_3`, `focus_pouch`, `mirrored_glass`, `amber`, `quicksilver`, `fabric`, `salis_mundus`, `alumentum`, `rare_earth`, `filter`, `morphic_resonator`, flattened legacy nugget/chunk outputs, `cloth_boots`, `cloth_chest`, `cloth_legs`, `void_robe_helm`, `void_robe_chest`, `void_robe_legs`, `iron_plate`, `brass_plate`, `thaumium_plate`, `void_plate`, `mechanism_simple`, `mechanism_complex`, placeholder `smelter_aux`, and placeholder `smelter_vent` |
| Research/progression bridge identities | Thaumium/brass materials, aspect crystal essence variants, phial variants, stored-enchantment requirements and legacy metadata-family requirements now carry component-level semantics for requirement matching. Scribing tools and research table now have their first legacy-backed storage/conversion slice, `arcane_workbench` has its server-owned regular legacy `89/89` arcane recipe crafting path, `arcane_workbench_charger` has its Workbench 3 x 3 aura behavior and exact recipe, `goggles`, `cloth_*` and `void_robe_*` are real armor/equipment items with legacy discount/reveal/warp contracts, `sane_soap` has its current warp-cleansing behavior, `curio_rites` has its Crimson Rites gate/knowledge/warp behavior, `wand_workbench` is the TC6 Focal Manipulator id with its exact legacy arcane recipe, page snapshot and first audited BlockEntity/menu/focus-craft cycle, `caster_basic` and all three focus items own the first Data Component/aura-cost core, `golem_builder` exists as the hidden Golem Press identity for Basic Golemancy aid detection, and Alumentum/Salis Mundus exist as identity/requirement items for theorycraft, but their real behavior is not implemented. Filter/resonator behavior, full focus effect execution, Focus Pouch inventory GUI, optional Curios/accessory slot adapter wiring, Golem Press multiblock/GUI/essentia logic, special thaumium tool behavior, mirror and broader curio/brain behavior remain subsystem bridges until their behavior slices are implemented. |

## Aspect assignment data resources

The current aspect assignment source of truth is split across bundled data files under `05_neoforge_port/src/main/resources/data/thaumcraft/aspect_assignments/`.

| Assignment layer | Count | Notes |
|---|---:|---|
| Exact item assignments | `702` | `current_registered.json` covers normally authored registered Thaumcraft ids, including exact `rare_earth` (`terra 5`, `ordo 5`, `metallum 5`) for the Morphic Resonator fixture and exact legacy meat chunk assignments (`victus 5`, `perditio 1`) for the flattened Infernal Furnace bonus outputs; `current_registered_runtime_parity.json` preserves dump-derived registered Thaumcraft final values, including the registered `thaumonomicon`, `thaumometer`, table/research items, current materials, legacy universal-bucket parity for `liquid_death_bucket` and `purifying_fluid_bucket`, the three deliberately empty pillar stacks, and exact generated `matrix_speed` / `matrix_cost` values; `legacy_vanilla_core.json` and `legacy_vanilla_modern_exact.json` preserve direct vanilla seeds; `legacy_vanilla_modern_manual.json` covers 1.21-only vanilla ids by audited Thaumcraft-style category; `legacy_vanilla_runtime_parity.json` preserves dump-derived final 1.12 values for shared plain vanilla stacks affected by metadata flattening, generated recipes, complex extras, wildcard specificity, or stack bonuses. Spawn eggs, firework star/rocket, and infested blocks are excluded because 1.12 gave those comparable stacks no aspects. |
| Item tag assignments | `46` | Includes safe current common `c:` tags, vanilla material bridges for legacy ore/gem/ingot/dust/base-block keys, ore-derived 1.21 raw materials, `blockGlass`, plus exact `thaumcraft:legacy_ore_dictionary/*` compatibility tags where legacy used string-key aspect assignments. |
| Complex exact assignments | `32` | Current complex extras cover audited buckets, boats, doors, fence gates, and related legacy complex additions. Runtime diff proves this layer must continue to be source-vs-runtime reviewed before broad expansion because legacy exact/generated/wildcard lookup order can mask wildcard complex values. |
| Generated crafting assignments | `636` | Built after server data/tag reload from current `RecipeType.CRAFTING` recipes and current `thaumcraft:arcane` recipes for `minecraft:*` and `thaumcraft:*` outputs that have known ingredient aspects. Exact/tag/manual/runtime-parity assignments still win over generated values. The current count includes remaining safe research bridge recipes used to make modern `required_craft` markers observable, exact normal plate recipes, and current arcane outputs without stronger exact assignments; bridge recipes are not final gameplay implementations. Crucible page-data recipes intentionally do not feed this generated-aspect cache yet. |

## Generated aspect recipe cache

`TCAspectStackKey`, `TCGeneratedAspectCache`, and `TCGeneratedAspectRecipeGenerator` define the current generated-aspect cache boundary. The key uses item registry id plus the stack data component patch and ignores stack count, matching the legacy normalization intent. The cache is cleared on aspect assignment bootstrap/reload, then rebuilt after server data/tag reload from loaded vanilla crafting and current arcane recipes.

The normal crafting slice does not depend on whether a recipe is crafted from the 2x2 inventory grid or the 3x3 crafting table; it scans `RecipeType.CRAFTING`, and each recipe's own dimensions decide where it can be crafted. The current arcane slice scans the custom `thaumcraft:arcane` recipe type and adds the legacy `praecantatio` bonus from `vis` after the ingredient formula.

Validation proves:

- exact item assignment wins over generated cache;
- tag assignment wins over generated cache after tags are loaded;
- generated fallback works for recipe-derived outputs;
- `AspectHelper.generateTags` returns generated cache entries without doing lookup-time recipe scans.
- every assignable current vanilla `minecraft:*` item registry id has aspects after server data/tag reload;
- conservative vanilla direct and tag seeds work for coal, buckets, ores, gems, ingots, dusts, and copper;
- 1.21 raw iron/gold/copper are intentionally ore-derived from corresponding legacy `ore*` entries;
- spawn eggs intentionally return no aspects;
- potions, splash potions, lingering potions, tipped arrows, and enchanted books use stack components for legacy parity instead of plain id-only lookup;
- scan-specific long slowness potion quirks are isolated in `AspectHelper.getScanAspects` so the normal object/tooltip aspect dump remains identical to legacy;
- shapeless crafting and remaining-item subtraction match the legacy crafting formula.
- current arcane recipe-derived outputs match the legacy ingredient formula plus `sqrt(1 + vis / 2) / output count` `praecantatio` bonus for `filter` and `morphic_resonator`.

## Current gate interpretation

| Gate | Current interpretation |
|---|---|
| Gate 0 | Complete enough to continue, but still validate `runServer` after client/render changes. |
| Gate 1 | In progress and expanded beyond the first simple item batch. Active registered item resources are covered; creative order still needs visual review. |
| Gate 2 | Started early through simple block and block item identity work. Active registered blockstates, models, loot tables, and translations are covered. |
| Gate 3 | Implementation started carefully. Exact legacy core aspect definitions/list/helper logic, current registered-id assignments, generated crafting cache, vanilla entity aspect lookup, read-only Shift tooltip rendering, and scan-resolved aspect lookup are present and guarded by validation; gameplay-heavy consumers remain blocked. |
| Gate 4+ | Aura is started as an isolated server-side storage/API slice after `aura_design.md`; the first research-table BlockEntity/menu/screen boundary exists; the custom arcane recipe and Arcane Workbench server crafting boundary exists, including Workbench Charger 3 x 3 aura use and the first player vis-discount path; crucible recipe/manual/collision/spill/client-FX slices exist; infusion recipe plus active structure/cycle/stability/client-FX slices exist. Capabilities, broad machine networks, full alchemy side effects, broad networking, worldgen, and large rendering systems remain incomplete. |

## Partially stale documents

| Document | Stale part | Current handling |
|---|---|---|
| `gate1_items_plan.md` | Lists only `amber`, `quicksilver`, and `fabric` as the first implemented slice. | Keep as workflow guidance; use this file for actual inventory. |
| `creative_tab_order_reference.md` | First implemented entries section no longer reflects all implemented entries. | Keep policy; status should point here. |
| `migration_matrix.md` | Matrix now distinguishes imported legacy assets from active adapted resources. | Keep matrix for policy and gate sequencing; use this file for live implementation status. |
| `block_parity_audit.md` | Refreshed for sapling/tree and block property updates. | Still requires exact legacy parity checks before behavior tuning. |

## Do not start without a design note

Do not implement or expand aura, research, arcane crafting, crucible/alchemy, infusion, BlockEntities, menus, networking, worldgen, or large rendering systems by direct legacy class copying. Each one needs a small design document first. Current arcane crafting work is limited to the documented exact-recipe/Arcane Workbench server slice; current crucible work is limited to the documented manual/collision/spill/client-FX boundary plus focused Flux Goo/TaintSeed/FallingTaint/FluxRift world-mutation slices, the focused Bath Salts/Bottled Taint/LiquidDeath/PurifyingFluid/WarpWard special alchemy slice and the Arcane Spa/Everfull Urn automation device slice. It must not grow further into focus-cloud rift event execution, taint mob natural-spawn/rendering parity, Thaumic Slime polish, remaining alchemy automation families or essentia networks without a new focused slice; current aspect work is limited to the documented data layer and read-only inventory tooltip consumer. Legacy crucible item absorption is collision-only.

## Immediate next work

1. Re-run `./gradlew build --no-daemon` after every arcane workbench GUI, discount, charger, or recipe expansion batch.
2. Keep the mapped aspect diff report at `0` real `PORT_GAP_*` buckets before treating current coverage as safe for gameplay consumers.
3. Use the next full `./gradlew runClient --no-daemon` visual pass to inspect creative tab order, active item icons, Shift-held aspect tooltip visuals, and the 3D Thaumometer OBJ transforms in GUI/hand views.
4. Compare creative tab order with the 1.12.2 inventory screenshots.
5. Add future registered item/block aspect values through `data/thaumcraft/aspect_assignments`; entity aspects currently use a legacy Java table until an entity assignment datapack format is designed.
6. Keep vanilla item coverage at `0 missing` after every aspect/tag change; do not broaden third-party modded generated outputs until an addon policy exists.
7. For new arcane, crucible or equipment behavior families, add validation coverage alongside the import so the recipe/page/data path cannot silently accept wrong ingredient mapping; keep crucible recipe-derived aspect generation and infusion recipe-derived generation blocked until their machines and ingredient mappings exist.
8. For the next crucible/Thaumatorium gameplay batch, keep `TCSpecialAlchemyBehaviorAudit` and `TCAlchemyAutomationDeviceAudit` green and audit the remaining automation owner before wiring taint mob natural-spawn/rendering parity, Thaumic Slime polish, additional automation behavior or focus-cloud-owned Flux Rift event execution.
8. Continue vanilla aspect changes only from `ConfigAspects`, audited legacy OreDictionary-to-tag bridges, recipe-derived cache behavior, or documented 1.21-only category policy.
9. Keep parity validation and reload validation passing before expanding aspect consumers.
10. Do not expand aura beyond saved-data/query/debug-command/autogenerated chunk state, and do not begin essentia, broad GUI, broad networking, crafting costs, or gameplay-heavy systems without their own design notes.
11. Keep the research data parity harness at `0` source/runtime/category differences and all progression checks passing after every parser or progression change.
12. Keep the permanent research recipe/page catalog at exact parity before extending Thaumonomicon rendering; do not treat legacy `stages[].recipes` as simple vanilla recipe unlock ids.
13. Keep the research table diagnostic harness passing after every `TCResearchTableData`, card, aid, table menu, or action payload change.
14. Visually check vanilla/basic aid selection, table-top scroll/inkwell/quill placement, and the first Arcane Workbench screen in `runClient`; then port remaining theorycraft cards/aids only by audited dependency family. Advanced cards that require WarpEvents client hallucinations/entity spawns, curios, focus/caster, infusion, celestial notes, portals, or other unported systems must stay deferred or explicitly bridged.
15. For Arcane Workbench, keep full regular legacy recipe id-set parity, player vis discount, Workbench Charger 3 x 3 aura behavior, and missing-vis ghost output covered by the current audits. Current base/discounted cost, aura, crystal-slot GUI feedback, and non-pickup ghost output are server-owned and covered by the workbench audit; final visual tuning remains a separate GUI polish task.
16. For Infusion, keep `audit-infusion-recipe-data.ps1` and `audit-infusion-behavior.ps1` green before adding atomic crafting completion. Preserve legacy exact-count unordered component matching; do not consume items/aspects or trigger instability/FX until the next focused consumption/drain slice has its own plan and audit.

### Latest Bellows device closure

- Added the focused Bellows device/rendering slice: `RenderShape.ENTITYBLOCK_ANIMATED`, client/server BlockEntity tickers, legacy inflation cycle, legacy-texture BlockEntityRenderer, tube-buffer bore extension, and an isolated vanilla furnace cook-progress bridge.
- Expanded `audit-bellows-device.ps1` to require the renderer registration, client animation state, tube-buffer extension and furnace bridge instead of accepting a static placeholder.
- Added `06_docs/migration/remaining_subsystem_unblock_plan.md`; the following Alembic/Jar item-transfer and tube-caster control closure moved the current cut line after rows 2 and 3.

### Latest Alembic/Jar and tube-control closure

- Added Data Component-backed Alembic/Jar label filters, empty phial extraction, filled-phial-to-jar transfer and Warded Jar item payload transfer.
- Added tube caster side/choke/facing controls, fixed buffer choke cycling to match legacy `0 -> 1 -> 2 -> 0`, and synced manual/redstone valve plus vent state.
- Expanded the combined transport/machine runtime audit to cover these interactions; current result is `46/46`.

### Latest Essentia Input/Output transfuser closure

- Replaced the `essentiatransportin` / `essentiatransportout` catalog placeholders with real BlockItems backed by `TCEssentiaTransportBlock` and `TCEssentiaTransfuserBlockEntity`.
- Ported the legacy `TileEssentiaInput` / `TileEssentiaOutput` contract: clicked-face placement, back-face-only connectivity, input suction `128`, output demand relay, five-tick cadence, and the 16-block `EssentiaHandler.getSources` prism ordered by distance.
- Updated the Thaumatorium infusion recipe components to use the real transport input/output ids instead of the temporary `essentia_importer` / `essentia_exporter` placeholder items.
- Expanded the combined transport/machine runtime audit to cover transfuser registration, shape, capability and transfer behavior; current result is `52/52`.

## Local validation commands

From `05_neoforge_port`:

```powershell
.\gradlew.bat build --no-daemon
.\gradlew.bat runClient --no-daemon
.\gradlew.bat runServer --no-daemon
.\gradlew.bat runServer --no-daemon -PtcArcaneRecipeAudit=true "-PtcArcaneRecipeAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\arcane_crafting\thaumcraft_1_21_arcane_recipe_audit.md"
.\gradlew.bat runServer --no-daemon -PtcArcaneWorkbenchAudit=true "-PtcArcaneWorkbenchAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\arcane_crafting\thaumcraft_1_21_arcane_workbench_audit.md"
.\gradlew.bat runServer --no-daemon -PtcResearchPageCatalogAudit=true "-PtcResearchPageCatalogAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_recipe_catalog\thaumcraft_1_21_research_recipe_catalog.md"
.\gradlew.bat runServer --no-daemon -PtcThaumonomiconProtocolAudit=true "-PtcThaumonomiconProtocolAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_recipe_catalog\thaumcraft_1_21_thaumonomicon_protocol_audit.md"
.\gradlew.bat runServer --no-daemon -PtcResearchTableAudit=true "-PtcResearchTableAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_table_audit\thaumcraft_1_21_research_table.md"
```

## Research, knowledge and scanning design

Started:
- Added design document for player knowledge, research commands and scanning.
- Added legacy source audit for CommandThaumcraft, PlayerKnowledge, IPlayerKnowledge, ResearchManager, ScanningManager and scan predicate classes.
- Knowledge storage, commands, progression, page catalog, and the server-authoritative Thaumonomicon protocol are implemented.

The server-authoritative vanilla crafting-page view model, custom arcane recipe type, and first real renderer for catalog pages that are actually `READY` are implemented. The current research/crafting boundary is audited exact arcane recipe expansion by dependency family; deferred page contents must not be invented.
## Research data skeleton

Started:
- Added server data reload listener for `data/thaumcraft/research/*.json`.
- Copied the eight legacy Thaumcraft research files into the server-data path.
- Added category/entry/stage model records and hardcoded legacy category metadata/formulas.
- Added read-only `/thaumcraft research list` and `/thaumcraft research info <key>` commands.
- Latest server reload validates `7` categories, `148` entries, `271` stages, and `16` addenda.
- Added read-only research reference validation through reload logging and `/thaumcraft research validate`; latest server reload reports `201` resolved entry references, `95` external scan/flag trigger references, and `0` unresolved research references.
- Added checked current-stage requirement diagnostics and advancement commands: `/thaumcraft research <player> stage <research_key> check` and `advance`.
- The checked-stage path mirrors legacy `PacketSyncProgressToServer.checkRequisites`: it verifies current-stage item/craft/research/knowledge gates and consumes item/knowledge costs only after all gates pass.
- Added modern crafting-event marker emission for resolvable `required_craft` entries. This preserves the legacy hidden-marker role for future stage checks, while exact direct legacy ItemStack hash ids remain blocked until exported/mapped.
- Added component-aware research requirement semantics for aspect crystal essence, filled phials, legacy material-family metadata and stored enchantment requirements. Enchanted-placeholder requirements match real enchanted item/book stacks like legacy `InventoryUtils.checkEnchantedPlaceholder`, not just fake placeholder ids.

Still blocked:
- Thaumonomicon final search visual parity, exact direct craft-reference hash parity, custom blueprint/special recipe-page renderers, WarpEvents client hallucination/stress visuals, exact Guardian/orb/cultist/portal renderer pixel parity, and cancellable research/knowledge events. The real item/open/browser/entry/search/drilldown/fake-display flow, permanent page catalog, server-authoritative warp mutation/sync, first server-side WarpEvents behavior slice and server-authoritative index/entry/action/drilldown protocol are implemented; entry rewards and addendum notifications are implemented, but built-in TC6 data does not provide a real reward integration fixture.
## Player knowledge command skeleton

Started:
- Added server-side player persistent knowledge storage wrapper.
- Added observation and theory knowledge types with legacy-like raw point conversion.
- Added /thaumcraft, /thaum and /tc knowledge debug command tree.
- Added minimal stored research key skeleton commands.
- Scan observation rewards and Thaumometer scan-key mutation are implemented. Celestial-note side effects, non-observation scan rewards, aura HUD, and final Thaumonomicon consumers remain intentionally incomplete.
## Scanning debug command skeleton

Started:
- Added TCScanningManager and TCScanResult.
- Added /thaumcraft, /thaum and /tc scan debug command tree.
- Added held item aspect lookup.
- Added looking block aspect lookup through block item form.
- Added legacy-shaped `thaumcraft.api.research.IScanThing` and `ScanningManager` shell.
- Added modern `ScanItem`, `ScanBlock`, `ScanEntity`, `ScanOreDictionary`, and `ScanAspect` predicate classes.
- Added initial generic scan predicate for aspect-bearing items, blocks and entities.
- Restored legacy aspect-trigger scan behavior: `Aspect` registers `ScanAspect("!"+tag)` and the reload bootstrap re-adds aspect predicates before generic scan.
- Added reloadable `data/thaumcraft/scannables/*.json` format and documented it in `scannable_data_format.md`.
- Added bundled `legacy_core.json` with 40 currently valid legacy scan definitions, including the currently registered taint, Crimson cultist and Eldritch Guardian entity scan rows.
- Dedicated server reload currently reports 134 active scan predicates before dynamic server predicates and 225 after dynamic mob-effect/enchantment predicates register.
- Added dynamic mob-effect and enchantment scan predicates as modern equivalents for legacy `ScanPotion` and `ScanEnchantment`; server startup currently reports 225 active predicates after dynamic registration.
- Added gated sky scan predicate for `CELESTIALSCANNING`, without celestial note side effects.
- Added vanilla entity aspect assignments for legacy mob/object scan targets, with documented post-1.12 entity policy rows. Runtime parity shows `minecraft:elder_guardian` and `minecraft:zombie_villager` had no effective 1.12 aspects, but the port now intentionally gives them living-mob aspect rows: elder guardian uses the legacy Guardian+Elder NBT intent; zombie villager uses zombie/villager hybrid semantics. Registered custom Thaumcraft entity aspect rows are filled for the current taint and WarpEvents slices; remaining custom rows stay deferred until their entities are registered.
- Thaumometer right-click now plays registered `thaumcraft:scan` sound and spawns legacy-shaped rune particles. Server scan and client use visuals share the legacy entity target resolver: min range `1`, scan range `9`, zone-style inflated hitboxes, and line-of-sight checks. While held, the client uses the longer legacy highlight pass: entity range `16` with `padding=5`, plus separate wild block rays at range `16` with random yaw/pitch spread. Highlight eligibility derives potential scan keys from aspect lookup, active data scannables, potion/effect scans, and enchantment scans, then filters already-known keys through the completed-key portion of the GUI-ready knowledge sync payload. Living-mob aspect icons plus amounts render above normal aspect-bearing living mobs even after known keys; sparkle highlight is the part gated by not-yet-known keys. Right-click scan mutation now uses `TCResearchManager.progressResearch`, grants only newly unknown scan keys, respects parent requisites where loaded entries exist, and preserves blank-key suppress behavior. Aura HUD, celestial-note side effects, and non-observation scan rewards are still pending.
- Added legacy scan learning side effects: `ScanAspect` now grants the same raw `+1` observation unit to AUROMANCY/BASICS/ALCHEMY as 1.12, and `TCScanGeneric` applies the legacy category formula to scanned aspects before adding raw OBSERVATION knowledge.
- Added `post_1_12_scanning_policy.md`: post-1.12 vanilla items use documented aspect policy plus generic scan; bespoke research keys require explicit design.
- Added `/thaumcraft scan audit_items`, automated `-PtcScanDump=true` server dumps, `scanning_parity_validation.md`, and `07_Test_Instance_and_Comparisons/scan_parity` for deterministic item, potion, enchantment and scan-key audit diffs.
- The latest scan report has `1139/1139` comparable item/potion/enchantment rows parity-ok and no aspect-value or scan-logic differences.
- Added `scanning_gap_audit.md`, restored legacy dropped-item scan targeting by allowing `ItemEntity` look targets, and added `/thaumcraft scan audit_entities` plus `-PtcScanEntityDump=true` modern server dumps.
- Added Forge 1.12.2 legacy entity/state-variant exporter and `compare_entity_scan_dumps.py`. Latest entity report: `83/85` comparable vanilla entity/state rows parity-ok, `2` expected modern entity aspect policy rows, `0` actionable gaps, `44` expected legacy-only rows for deferred Thaumcraft entities/guardian NBT probe, and `46` expected modern-only post-1.12 rows.
- Scan commands currently report aspects and matched scan keys without mutating player knowledge; the actual Thaumometer item performs the server-side scan-key mutation.

Next:
- Fill deferred `ConfigResearch.initScannables` entries as their target ids become registered.
- Keep checking scan observation rewards against real Thaumonomicon knowledge costs once the page UI exists.
- Add `ScanSky` celestial-note side effects after celestial notes and scribing tools exist.
- Design and implement the first real custom recipe type before its authoritative page snapshot/renderer; do not duplicate recipe, visibility, requirement, or page-catalog decisions on the client.

## Changelog Notes

The sections below are historical update notes. They should not be read as the current task queue; use `06_docs/CURRENT_TASK.md` for current priorities and the `State Snapshot` plus `High-level status` sections above for current state.

### Latest Brain-in-a-Jar behavior blocker slice

- Replaced the previous JarBrain recipe/page-only boundary with a real `jar_brain` block, BlockItem and BlockEntity.
- Ported the legacy server contracts for XP storage key/max, XP orb pull and close absorption, right-click XP release delay/value split, comparator signal, enchanting bonus and item-stack XP preservation.
- Registered `AidBrainInAJar` so the placed jar contributes `CardDarkWhispers` to theorycraft.
- Added `TCBrainJarBehaviorAudit`; latest runtime report passes `10/10`.
- Kept animated brain model/rotation/sigh behavior, exact spark pixel parity and broader golem/mind behavior out of this focused blocker slice. The legacy `jar_brain` item-warp entry is now covered by the focused warp behavior bridge instead of Brain-in-a-Jar rendering/work.

### Latest server smoke hardening update

- The dedicated server smoke log-quality gate now uses case-sensitive log severity markers instead of a broad generic ERROR regex, avoiding false positives from DEBUG dependency paths such as rror_prone_annotations.
- Datapack, recipe, tag, invalid resource path, crash and startup failure markers remain hard failures. -FailOnWarnings remains opt-in for exact WARN-level markers.

### Latest server smoke stale-lock preflight update

- Dedicated server smoke checks run/world/session.lock before starting runServer.
- If a local stale Java or Gradle process still owns the world lock, smoke fails early with matching process hints instead of a long Minecraft DirectoryLock stacktrace.

### Latest server smoke stale-process cleanup update

- Dedicated server smoke now supports -KillStaleRunServer for local runs.
- With that switch, smoke stops only matching Java or Gradle runServer and NeoForge devlaunch processes from this repository before retrying the world session-lock check.
- CI remains conservative by default; the switch is intended for local developer runs where a previous smoke left run/world/session.lock held.

### Latest CI server smoke properties update

- Dedicated server smoke now pre-seeds run/server.properties before launching runServer.
- This prevents the clean CI workspace first-run message Failed to load properties from file: server.properties from being logged as a Minecraft ERROR and tripping the strict log-quality gate.
- Build workflow artifact upload already includes 05_neoforge_port/build/ci-logs/** for smoke diagnostics.

### Latest custom recipe boundary audit update

- Added tools/audits/audit-custom-recipe-boundary.ps1 and 06_docs/audits/custom_recipe_boundary_audit.md.
- The audit scans current recipe JSON, research recipe-like references, missing recipe references, and custom recipe keywords to separate READY data recipes from custom behavior that still needs a serializer, page, or behavior design slice.
- Next custom recipe work should use this audit to pick the largest safe target without copying legacy crucible, infusion, fake, blueprint or special recipe classes directly.

### Latest research recipe page gap audit update

- Added tools/audits/audit-research-recipe-page-gaps.ps1 and 06_docs/audits/research_recipe_page_gap_audit.md.
- This audit narrows the broad custom recipe boundary scan to actual Thaumonomicon stage/addendum recipe page references and separates them from icons, required_item, and required_craft gates.
- Use the missing recipe page class distribution to choose the next large serializer/page implementation slice without conflating item requirements with recipe pages.

### Latest legacy alchemy recipe source audit update

- Added tools/audits/audit-legacy-alchemy-recipe-sources.ps1 and 06_docs/audits/legacy_alchemy_recipe_source_audit.md.
- This audit traces the dominant alchemy/crucible/special recipe page gaps back to the local legacy source corpus before implementation.
- Next alchemy work should implement only a recipe data model, serializer, loader audit, and Thaumonomicon page snapshot for the selected family before any crucible or machine behavior.

### Latest hedge alchemy recipe extraction update

- Added tools/audits/extract-legacy-hedge-alchemy-recipes.ps1 and 06_docs/audits/hedge_alchemy_legacy_recipe_blocks.md.
- This extraction captures exact legacy CrucibleRecipe source blocks for the dominant HEDGE_ALCHEMY page-gap family before writing any new NeoForge serializer or page renderer.
- The next implementation slice can use this document to add the first crucible recipe data/page boundary while keeping in-world crucible behavior deferred.

### Latest crucible recipe page boundary update

- Added a first loader/page boundary for thaumcraft:crucible recipes without implementing in-world crucible behavior.
- Added the HEDGE_ALCHEMY legacy crucible recipe family as data recipes and research-page catalog entries using the extracted legacy ResourceLocation ids.
- The first crucible Thaumonomicon page snapshot carries result, catalyst variants, research key, and explicit aspect display stacks. Dynamic AspectList-derived HEDGE legacy costs were initially deferred as empty placeholders, but are now resolved into explicit data from the current parity aspect assignments.

### Latest post-HEDGE recipe page audit refresh

- Refreshed custom recipe boundary, research recipe page gap, and legacy alchemy source audits after the HEDGE_ALCHEMY crucible recipe page boundary landed.
- Use the refreshed missing recipe page class and research-file distributions as the source of truth for the next large family-level recipe/page batch.
- The next batch should target the largest remaining family, not individual recipe ids, unless an audit shows a family needs a separate data-model boundary.

### Latest remaining alchemy recipe extraction update

- Added tools/audits/extract-remaining-alchemy-recipes.ps1 and 06_docs/audits/remaining_alchemy_legacy_recipe_blocks.md.
- This extraction captures exact legacy recipe source blocks for remaining non-HEDGE alchemy recipe-page gaps after the first crucible boundary batch.
- Use the extracted API kind and family distribution to choose the next broad alchemy batch without mixing crucible page recipes with infusion or machine behavior.

### Latest crucible aspect alias fix

- Fixed the first crucible recipe boundary to accept legacy Aspect enum names in recipe JSON by canonicalizing them to the port's active aspect tags.
- This maps legacy names such as fire, air, earth, beast, magic, order and entropy to modern tags such as ignis, aer, terra, bestia, praecantatio, ordo and perditio.
- This keeps current HEDGE_ALCHEMY JSON compatible and reduces future generated alchemy recipe batch risk because legacy source extraction reports Aspect enum names.

### Latest metal purification crucible recipe page update

- Added the METAL_PURIFICATION alchemy family as crucible recipe/page data for iron, gold, copper, tin, silver, lead and cinnabar.
- Added legacy cluster bridge item identities for the old ItemsTC.clusters metadata outputs.
- Added optional legacy ore dictionary catalyst tags so current vanilla/Thaumcraft ores resolve where available while absent legacy-only metal ores remain safe placeholders.

### Latest post-metal-purification audit refresh

- Refreshed recipe/page and alchemy source audits after the METAL_PURIFICATION crucible recipe/page batch.
- Use the refreshed counts to select the next safe alchemy family-level batch.
- Prefer small pure crucible families next, and avoid mixed ALCHEMY_OTHER entries until their false positives and machine/page boundaries are separated.

### Latest base alchemy/metallurgy crucible recipe page update

- Added current pure crucible page-data recipes for Alumentum, Nitor, Brass Ingot, Thaumium Ingot, and all 37 vis-crystal essence variants.
- Added lowercase legacy OreDictionary catalyst tags for `coal`, `dust_glowstone`, `ingot_iron`, and `nugget_quartz` so current recipes avoid invalid uppercase legacy ResourceLocations.
- Refreshed research recipe page and custom recipe boundary audits. Current catalog audit now reports `113 READY`, `86 DEFERRED`, `4 LEGACY_MISSING`; the Thaumonomicon protocol audit passes `27/27`.
- Fixed a malformed generated `brassingot` aspect list before accepting the batch; dedicated server smoke now reaches `Done` and reloads `1558` recipes without datapack/recipe errors.
- Full in-world crucible behavior, essentia/alchemy side effects, item-entity suction, flux/taint effects and crucible-derived generated aspect behavior remain deferred until their own design/validation slice.

### Latest Thaumonomicon recipe-page protocol hardening

- Expanded `TCThaumonomiconProtocolAudit` so READY page views and direct catalog entries must carry the matching server-owned snapshot for vanilla crafting, arcane, crucible and infusion page kinds.
- Current protocol audit passes `36/36`; current research page catalog audit reports `203 READY`, `0 DEFERRED`, `0 LEGACY_MISSING`.
- The legacy fake/display catalog ids (`salismundusfake`, `triplemeattreatfake`, `ie*fake`, `runicarmorfake*`) now render from server-owned display snapshots and remain non-craftable gameplay catalog fixtures.
### Latest special alchemy crucible page batch

- Added bridge identities and crucible recipe/page entries for Bath Salts, Bottled Taint, Liquid Death, and Sane Soap.
- These recipes preserve the legacy research/page ids as canonical lowercase recipe ids and keep full gameplay/fluid/block behavior deferred.
- `liquid_death_bucket` is now a real bucket item for `liquid_death`; `flesh_block` remains a bridge item identity for recipe/page display until its dedicated block behavior is ported.

### Latest post-special-alchemy audit refresh

- Refreshed recipe/page and alchemy source audits after the SPECIAL_ALCHEMY crucible recipe/page batch.
- Use the refreshed counts to confirm the special alchemy gap is closed and select the next safe batch.
- If alchemy crucible/special gaps are closed, avoid mixed ALCHEMY_OTHER entries until EverfullUrn, JarLabelEssence, and Thaumatorium are separated into proper item/page/machine boundaries.

### Latest golemancy boundary source audit

- Added a dedicated source audit for current GOLEMANCY_PAGE_DEFERRED references.
- This audit is analysis-only and is meant to separate real recipe/page work from seal, machine, and behavior boundaries before implementation.
- Next golemancy work should choose a narrow family from this audit instead of implementing all deferred references as recipes.

### Latest golemancy boundary source audit repair

- Rebuilt `golemancy_page_boundary_source_audit.md` with a stricter extractor that fails if no GOLEMANCY_PAGE_DEFERRED references are found or if the output is unexpectedly small.
- Use the repaired audit to select the next narrow golemancy implementation batch.

### Latest focused golemancy recipe source candidate audit

- Added `golemancy_recipe_source_candidates.md`, a filtered audit that extracts likely `ConfigRecipes.java` recipe blocks for current GOLEMANCY_PAGE_DEFERRED references.
- Use this before selecting the first golemancy implementation batch; avoid broad seal behavior until recipe candidates are exhausted.
### Latest golemancy seal crucible page batch

- Added bridge item identities and crucible recipe/page entries for base and advanced golem seals that are data-driven in legacy crucible recipes.
- Real golem seal behavior remains deferred; this batch only makes the recipe/page identities visible and loadable.
- Infusion-based seals, JarBrain, MindBiothaumic, and GolemPress remain out of scope for this batch.
### Latest golemancy seal crucible repair note

- Repaired the first golemancy seal crucible batch after dedicated server smoke failed during TCItems static initialization.
- The repair removes duplicate item registrations from the added seal bridge block when an item id was already registered earlier.

### Latest post-golemancy-seals audit refresh

- Refreshed recipe/page and alchemy source audits after the GOLEMANCY_SEAL_CRUCIBLE recipe/page batch.
- Use the refreshed counts to confirm the golemancy seal crucible page gap reduction and select the next safe batch.
- If golemancy seal crucible gaps are closed, keep infusion-based seals, JarBrain, MindBiothaumic, and GolemPress separated into their own boundary batches.
### Latest focused infusion recipe source candidate audit

- Added `focused_infusion_recipe_source_candidates.md`, extracting legacy `addInfusionCraftingRecipe` blocks for missing infusion-related research page references.
- This prepares the next architecture slice: a modern infusion recipe/page boundary without in-world infusion crafting behavior.
### Latest infusion recipe page boundary slice

- Added 	haumcraft:infusion recipe type/serializer and Thaumonomicon recipe-page view plumbing.
- This is a data/page boundary only; in-world infusion altar gameplay remains deferred.
- No infusion JSON recipe batch is added in this slice.
### Latest golemancy first infusion recipe page batch

- Added the first small infusion recipe/page JSON batch after introducing the 	haumcraft:infusion boundary.
- Covered JarBrain, MindBiothaumic, SealBreak, SealButcher, and SealHarvest as data/page entries only.
- Real infusion altar gameplay and golem seal behavior remain deferred.

### Latest post-golemancy-infusion audit refresh

- Refreshed recipe/page and alchemy source audits after the FIRST_GOLEMANCY_INFUSION recipe/page batch.
- Use the refreshed counts to confirm the first golemancy infusion page gap reduction and select the next safe batch.
- If first golemancy infusion gaps are closed, GolemPress should remain separated as a machine/block boundary.
### Latest utility infusion recipe page batch

- Added first non-golem utility infusion recipe/page entries: BootsTraveller, CLOUDRING, and CHARMUNDYING.
- These remain data/page boundaries only; in-world infusion altar behavior is still deferred.

### Latest post-utility-infusion audit refresh

- Refreshed recipe/page and alchemy source audits after the FIRST_UTILITY_INFUSION recipe/page batch.
- Use the refreshed counts to confirm the first utility infusion page gap reduction and select the next safe batch.
- If first utility infusion gaps are closed, continue with another small infusion JSON batch instead of broad fake/synthetic pages.
### Latest elemental tool infusion recipe page batch

- Added elemental tool infusion recipe/page entries for axe, pick, sword, shovel, and hoe.
- Audit refresh is integrated into this batch script by default, after build/smoke and before commit.
- These remain data/page boundaries only; in-world infusion altar behavior is still deferred.
### Latest fortress mask infusion recipe page batch

- Added fortress mask infusion recipe/page entries for Grinning Devil, Angry Ghost, and Sipping Fiend masks.
- Added a bridge item identity for the Thaumium Fortress Helm catalyst; full fortress armor behavior remains deferred.
- Audit refresh is integrated into this batch script by default, after build/smoke and before commit.
### Latest fortress armor infusion recipe page batch

- Added Thaumium Fortress helm, chestplate, and leggings infusion recipe/page entries.
- Added bridge item identities for thaumium armor catalysts where needed.
- Audit refresh is integrated into this batch script by default, after build/smoke and before commit.
### Latest verdant charm infusion recipe page batch

- Added Verdant Heart, Verdant Heart of Life, and Verdant Heart of Sustenance infusion recipe/page entries.
- Potion-specific legacy ingredients are represented by broad potion item placeholders until richer item/NBT ingredient handling is implemented.
- Audit refresh is integrated into this batch script by default, after build/smoke and before commit.
### Verdant charm infusion repair

- Added the missing auble_charm bridge item used by the VerdantHeart infusion catalyst.
- This fixes the server smoke datapack parse error for 	haumcraft:verdantheart.
### Latest crystal cluster recipe page batch

- Added 7 crystal cluster crafting recipe/page entries for primal and flux crystal clusters.
- This is a larger integrated batch and includes audit refresh after build/smoke.
### Crystal cluster recipe repair

- Rewrote crystal cluster recipe JSONs to use explicit non-empty item ids.
- The previous generated files accidentally used blank item values because of PowerShell hashtable property access.
### Latest simple legacy page recipe batch

- Added larger mixed recipe/catalog batch for arcane stone, arcane brick, Curiosity Band, and Helm Goggles.
- This targets the remaining simple INFUSION_RESEARCH_LEGACY_PAGE_KEY items without touching altar/block behavior.
- Audit refresh is integrated after build/smoke.
### Latest auromancy focus recipe page batch

- Added data-driven recipe/catalog entries for focus_1, focus_2, focus_3, and VisAmulet.
- focus_1 is a conservative crucible page boundary because focused legacy source extraction has no direct source block for it yet.
- This script integrates audit refresh after successful build/smoke.
### Latest eldritch infusion recipe page batch

- Added data-driven infusion JSON/catalog entries for PrimalCrusher, VoidRobeHelm, VoidRobeChest, VoidRobeLegs, and VoidseerPearl.
- Deliberately left voidingot and VoidSiphon for separate follow-up because source classification is ambiguous/block-oriented.
- This script integrates audit refresh after successful build/smoke.
### Latest artifice behavior page recipe batch

- Added recipe/page boundary entries for ArcaneBore, InfernalFurnace, LampFertility, LampGrowth, Mirror, MirrorEssentia, and MirrorHand.
- This is still page/data boundary only; actual machine/block behavior remains deferred.
- This script integrates audit refresh after successful build/smoke.
### Latest remaining non-fake recipe page batch

- Added page-boundary recipes/catalog entries for EverfullUrn, JarLabelEssence, Thaumatorium, voidingot, VoidSiphon, CausalityCollapser, and nitorcolor.
- These are conservative recipe/page placeholders for remaining non-fake missing references; gameplay behavior remains deferred where appropriate.
- This script integrates audit refresh after successful build/smoke.
## Remaining non-fake page recipe repair note

- Registered alchemical_construct, essentia_importer, and essentia_exporter bridge item ids. Thaumatorium recipe components now use the real `essentiatransportin` / `essentiatransportout` block ids; importer/exporter are no longer active recipe dependencies.
- This repairs the previous server smoke datapack parse failure and keeps the same batch commit target.
### Latest blueprint page placeholder batch

- Added explicit blueprint/page placeholder recipes and catalog entries for infusion altar variants and GolemPress.
- These are not gameplay multiblock/machine implementations; they only close recipe page references.
- This script integrates audit refresh after successful build/smoke.
## Blueprint page placeholder repair note

- Registered arcane_pedestal, ancient_pedestal, and eldritch_pedestal bridge item ids used by infusion altar placeholder recipes.
- This repairs the previous server smoke datapack parse failure and keeps the same batch commit target.
## Blueprint placeholder audit finalize note

- The blueprint placeholder batch removed the last GOLEMANCY_PAGE_DEFERRED reference.
- The golemancy page-boundary extractor is now skipped when that class is absent from the page-gap audit.
## Synthetic recipe page audit classification

- FAKE_OR_SYNTHETIC_PAGE references are now reported separately from actionable missing recipe pages.
- This preserves the list of synthetic teaching/UI placeholders while allowing actionable recipe page gaps to reach zero.
## Infusion page boundary audit classification

- Custom recipe boundary audit now treats thaumcraft:infusion as INFUSION_PAGE_READY_NO_GAMEPLAY.
- This reflects the implemented infusion serializer/page snapshot boundary while keeping in-world infusion altar behavior deferred.
## Crucible gameplay boundary design note

- Added a focused design document for the first in-world crucible behavior slice.
- Added a crucible recipe data audit to validate catalyst/aspects/result shape before gameplay activation.
- The first manual in-world crucible behavior checklist is implemented; full alchemy side effects and automation remain deferred.
### Latest crucible behavior validation hardening

- Resolved the seven legacy dynamic HEDGE_ALCHEMY aspect costs into explicit JSON values from current item-aspect parity data.
- `audit-crucible-recipe-data.ps1` now validates `77/77` crucible JSON files.
- `audit-crucible-behavior.ps1` and `server-smoke.ps1` can run against isolated world/port settings, avoiding false local failures when another dev server or client is open; server smoke now also cleans up child runServer processes started by that smoke run.
- The current crucible behavior audit passes `16/16`, including item-entity absorption, special-result reabsorption protection, living contact damage boundaries, spill-remnants aura pollution, overflow `spillRandom`, and all three legacy fluid-height boundaries.
## Crucible contact cooldown scope note

- Moved the living-entity crucible contact damage cooldown from the singleton block instance to TCCrucibleBlockEntity.
- This prevents one crucible position from throttling or advancing another crucible's contact damage cadence.
## Infusion gameplay boundary design note

- Added a focused design document for the first in-world infusion behavior slice.
- Added an infusion recipe data audit to validate catalyst/components/aspects/result shape before behavior activation.
- Full in-world infusion completion, instability events and visual effects remain deferred beyond the current non-consuming matrix/pedestal start-plan plus completion-readiness slice.
## Infusion validation helper note

- Added `TCInfusionRecipeMatcher` as a non-mutating server-side validation helper for loaded `thaumcraft:infusion` recipe data.
- This is a validation/start-plan/readiness boundary only; item/aspect consumption, instability effects and rendering remain deferred.
## Infusion validation boundary audit note

- Added `TCInfusionAssembly` and `TCInfusionValidationResult` as the first server-owned infusion input snapshot/result layer.
- Fixed component validation to match legacy Forge `RecipeMatcher.findMatches`: pedestal component order is flexible, but supplied component count must exactly match the recipe count.
- Added `TCInfusionBehaviorAuditExporter` and `tools/audits/audit-infusion-behavior.ps1`; latest runtime audit passes `25/25`.
- Added active pedestal blocks, one-slot pedestal BlockEntities, matrix BlockEntity legacy-range pedestal discovery, non-consuming start-plan state, read-only completion-readiness state, and world snapshot checks.

## Infusion start-plan boundary note

- Added `TCInfusionCraftingPlan` and `TCInfusionStartResult` for the matrix active crafting-start state.
- The plan records recipe id, research key, instability, catalyst, matched component stacks, component pedestal positions, required aspects, result stack and player name.
- The plan is saved through BlockEntity NBT using modern `ItemStack` serialization and `AspectList` tags, and the behavior audit verifies round-trip load.
- Item consumption, essentia drain, instability rolls, beams, sounds, particles and completion output remain deferred to the next focused slice.
- This still does not implement item/aspect consumption, instability events, essentia transport, beams, particles, sounds or completion behavior.

## Infusion completion-readiness boundary note

- Added `TCInfusionCompletionPlan` as the read-only server-owned check that an active infusion plan still matches the current world/aspect state.
- The matrix rechecks the center catalyst, each originally matched component pedestal position/stack, and available aspect totals before any future mutation.
- The behavior audit now covers valid readiness, missing-aspect rejection, changed catalyst rejection, changed component rejection, and missing component pedestal rejection.
- This still does not consume pedestal items, drain essentia/aspects, replace the catalyst with output, roll instability, or run beams/particles/sounds.
## Infusion legacy cycle semantics audit note

- Added `06_docs/audits/infusion_legacy_cycle_semantics_audit.md` to capture legacy `craftingStart`, `craftCycle`, `craftingFinish`, and `getSurroundings` anchors.
- The next implementation should be a small audited mutation/executor boundary, not a full player-facing infusion craft trigger.
## Infusion container and essentia cycle audit note

- Added a focused legacy container/essentia timing audit for the infusion executor boundary.
- The next implementation should remain audit-only and non-player-facing until mutation timing is validated.
## Infusion mutation executor audit boundary note

- Added audit-only `TCInfusionMutationExecutor` to test the first all-or-nothing item mutation boundary after completion readiness.
- This remains non-player-facing and excludes instability, FX and essentia network behavior.
## Infusion container remainder audit note

- Added a current infusion recipe data audit for known vanilla container/remainder item inputs.
- This keeps the audit-only mutation executor bounded to the currently validated recipe data set.
## Infusion container remainder guard note

- Added a temporary `TCInfusionContainerRemainderPolicy` guard for bucket/bottle/bowl-style infusion inputs.
- Player-facing infusion completion remains deferred until real container-item and essentia-source parity is implemented.
## Infusion aspect source boundary note

- Added audit-only `TCInfusionAspectSource` for all-or-nothing aspect drain semantics.
- This remains isolated from real jars, tubes, aura and essentia transport.
## Infusion aspect-source executor integration note

- Added audit-only executor/source integration for all-or-nothing aspect drain plus item mutation.
- This remains isolated from real jars, tubes, aura and player-facing infusion completion.
## Infusion aspect source interface note

- `TCInfusionAspectSource` is now an interface with an in-memory audit implementation.
- Real essentia source implementations remain deferred.
## Infusion player-facing completion gate note

- Added an explicit disabled player-facing completion gate on the infusion matrix block.
- Normal caster interaction remains validation/status-only while executor/source work is audit-only.
## Infusion component remainder policy note

- Added audit-only component remainder preservation for infusion mutation executor inputs.
- Container/remainder catalysts remain blocked and player-facing completion remains disabled.
## Refreshed infusion container remainder audit note

- Updated the infusion container remainder audit to reflect component-side remainder preservation.
- Player-facing completion remains disabled.
## Infusion tag input expansion audit note

- Added local tag expansion audit for tag-based infusion inputs.
- External/built-in tags remain a future player-facing validation concern.
## Infusion built-in tag fallback audit note

- Added reusable infusion tag input expansion audit tooling.
- `minecraft:wool` is handled as a known built-in fallback for static audit coverage.
## Infusion real aspect source resolver boundary note

- Added explicit no-op `TCInfusionAspectSourceResolver` boundary for future real source discovery.
- Audit-only memory sources remain separate from player-facing completion.
## Infusion real source policy checkpoint

- Added focused design documentation for the future real aspect/essentia source resolver.
- Infusion completion remains audit-only and player-facing completion remains disabled.
## Infusion real source candidate audit note

- Added a reusable audit for finding source candidates before implementing a real infusion aspect/essentia resolver.
- No player-facing infusion completion behavior is enabled by this audit.
## Infusion transport source readiness note

- Added a focused audit for deciding whether current essentia transport classes are source-ready for infusion.
- No player-facing infusion completion behavior is enabled by this audit.
## Infusion transport source adapter note

- Added `TCTransportInfusionAspectSource` and resolver discovery for adjacent `TCEssentiaTransport` sources.
- Runtime infusion audit validates single-aspect drain, insufficient-source no-op and multi-aspect fail-closed behavior.
- Player-facing infusion completion remains disabled.
## Alembic legacy source audit note

- Added a legacy-source audit for the next Alembic transport endpoint batch after the tube/jar transport slice.
- No gameplay behavior is changed by this audit.
## Alembic endpoint implementation note

- Added `TCAlembicBlock`, `TCAlembicBlockEntity`, block/item/entity registration, capability exposure and runtime transport audit coverage.
- This does not complete smelter processing; it only adds the Alembic output endpoint boundary.
## Smelter legacy machine audit note

- Added a legacy-source audit for the smelter machine model after the Alembic endpoint boundary.
- No gameplay behavior is changed by this audit.
## Smelter machine model boundary note

- Added `TCSmelterBlockEntity` and attached it to `smelter_basic` as the first persisted machine model boundary.
- This does not yet perform real smelting or fill Alembics.
## Smelter tick-state progression note

- Added server tick state progression for the basic smelter machine boundary.
- This still does not implement real smelting or Alembic filling.
## Smelter fuel/input conversion note

- Basic smelter now has a first server processing path for fuel/cook progression and item aspect conversion into its buffer.
- Alembic output and legacy flux/vent behavior remain incomplete.
## Smelter Alembic output note

- Basic smelter now has a first direct-above Alembic output path for buffered essentia.
- Legacy flux loss, vent behavior, Bellows discovery and auxiliary smelter output remain incomplete.
## Smelter efficiency/flux note

- Basic smelter now records legacy-style lost aspect points as pending flux during conversion.
- Vent mitigation and aura pollution are still incomplete.
## Smelter pending-flux aura bridge note

- Added a focused audit for smelter pending-flux to aura/flux API wiring.
- No runtime behavior changed in this audit batch.
## Smelter pending flux aura pollution note

- Basic smelter now drains pending flux into the current aura pollution API.
- Vent mitigation and auxiliary smelter output remain incomplete.
## Smelter vent placeholder mitigation note

- Added `smelter_vent` as a minimal block placeholder and wired it into basic smelter pending-flux mitigation.
- Exact legacy facing, auxiliary smelter output and Bellows effects remain incomplete.

## Smelter variant endpoint boundary note

- Added a focused audit for smelter variant versus transport endpoint ownership.
- Basic smelter runtime is active; upgraded smelter variants still need a safe ownership split/bridge.

## Smelter machine type field note

- Basic smelter now routes efficiency and output-speed through an instance `SmelterType` field.
- Upgraded smelter blocks are still not enabled as machine variants until the endpoint ownership bridge is implemented.

## Upgraded smelter endpoint machine bridge note

- Added a dual-role endpoint/machine bridge for thaumium and void smelter ids.
- Transport endpoint compatibility is preserved through delegated `TCEssentiaTransport` methods.

## Smelter Bellows placeholder boost note

- Added Bellows placeholder block and first adjacent-facing smelter speed boost behavior.
- Smelter variants, Alembic output, flux accounting, aura pollution and vent placeholder mitigation are already present in the current smelter path.

## Smelter vent facing mitigation note

- Smelter vent mitigation now uses a facing placeholder block rather than any adjacent vent block.
- Remaining smelter parity gaps include exact vent visuals, auxiliary smelter routing and additional legacy polish.

## Smelter auxiliary Alembic routing note

- Added minimal `smelter_aux` and routed smelter output to Alembics above adjacent aux blocks.
- Remaining smelter parity gaps include exact aux placement/facing, visuals and additional Bellows/vent polish.

## Smelter runtime boundary audit note

- Replaced the incremental placeholder result with one reviewed legacy-parity batch.
- Corrected Alumentum/fuel handling, cumulative vent mitigation, aux facing, matching-first Alembic columns and upgraded-smelter ownership.
- Restored detailed legacy-derived Bellows/aux/vent models, added the smelter menu/screen and registered sided item automation.
- Combined tube/jar/Alembic/smelter runtime result later expanded with label/phial/caster controls and Essentia Input/Output transfusers: `52/52`; Void Jar and Essentia Mirror then expanded the same audit to `61/61`.

## Void Jar and essentia mirror utility note

- Added real `jar_void` Block/BlockItem/BlockEntityType behavior with legacy overflow, suction, top capability and modern fill item predicates.
- Added real `mirror_essentia` Block/BlockItem/BlockEntity behavior with Data Component link payloads, legacy wall shapes, remote source-zone bridge, one-point add/drain behavior and instability flux checks.
- The combined essentia transport/machine audit now passes `61/61`.
- Added real normal item `mirror` Block/BlockItem/BlockEntity behavior and `hand_mirror` sender menu/screen. The dedicated mirror behavior audit passes `12/12`.
- Remaining mirror work is final BER/pixel parity, not the normal/essentia server behavior blocker.
