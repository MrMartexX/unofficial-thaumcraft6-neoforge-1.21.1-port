# Remaining Subsystem Unblock Plan

Status: working order after the Arcane Workbench regular-recipe/equipment-discount bridge and item/equipment behavior closure.

Basis: `06_docs/migration/NeoForge_legacy_migration_guide.md`, `06_docs/migration/migration_matrix.md`, `06_docs/migration/porting_order.md`, `06_docs/current_port_status.md`.

## Rules

- Keep existing parity/audit harnesses green before starting a new subsystem family.
- Do not copy large legacy classes directly; port behavior into modern NeoForge registration, data, capability, menu, payload, saved-data and renderer boundaries.
- Prefer closing already-started dependency blockers before opening new broad systems.
- Keep bridge recipes, debug commands and placeholder identities marked as tooling until the owning gameplay subsystem exists.

## Ordered Blocks

This is a blocker-removal order, not a promise that every row is one commit. Several rows should be implemented as two or three large, related batches when the dependency graph is clear.

| Order | Subsystem block | Why this order | Required validation | Main blockers removed |
|---:|---|---|---|---|
| 0 | Keep baseline gates green | Every later subsystem depends on the current parity/audit baseline. | `build`, server smoke when relevant, aspect/scan diffs, research/page catalog audits, item/block quick preset. | Prevents silent regressions while large batches land. |
| 1 | Bellows device boundary | Completed focused device slice; keep it as a regression guard before expanding essentia devices. | `audit-bellows-device.ps1`, build, server smoke, in-client visual check. | Bellows dynamic behavior/rendering. |
| 2 | Alembic label/phial/jar item transfer | Completed behavior slice over stable Warded Jar/Alembic storage and aspect Data Components. | Combined transport/machine runtime audit `46/46`. | Label application/removal, labeled jar UX, phial/jar transfer, aspect container item behavior. |
| 3 | Tube caster sub-parts, valve controls and vent/valve visuals | Completed behavior/state slice over stable tube state/shape and Bellows. Final measured visual parity remains under row 17. | Combined transport/machine runtime audit `46/46`, visual shape audit, runClient visual review when polishing. | Caster tube side closure/choke/facing controls, valve state clarity, vent state sync. |
| 4 | Remaining essentia utility devices | After transport controls exist, devices that consume/produce essentia can be implemented without inventing ad hoc paths. Essentia Input/Output transfusers are now implemented as real blocks. | Dedicated behavior audits per device family, combined transport audit `52/52`, server smoke. | Void Jar overflow, importer/exporter if kept, essentia mirror path, Thaumatorium input/output dependency blockers. |
| 5 | Arcane Workbench remaining recipes and equipment discount bridge | Completed for the regular legacy recipe id set and first equipment-discount bridge. Keep this row as a regression guard before item/equipment behavior work. | Arcane recipe audit `109/109`, workbench behavior audit `28/28`, generated-aspect cache reload count, runClient GUI pass for final visual review. | Regular arcane page/recipe gaps, vanilla armor-slot vis discount, first accessory-provider bridge, recipe-derived aspect blockers. |
| 6 | Item/equipment behavior pass | Completed for the first safe wearable/utility contract slice. Keep it as a regression guard before focus/caster gameplay depends on it. | Item/equipment behavior audit `17/17`, Arcane Workbench behavior audit `28/28`, build, server smoke; final armor model geometry remains row 17. | Goggles and robe ArmorItem contracts, legacy vis/warp/reveal interfaces, sanity checker identity, sanity soap warp cleansing, Crimson Rites gate, accessory-provider bridge. |
| 7 | Focus/caster/Focal Manipulator core | Caster/focus is a central gameplay API and blocks many combat, utility and research rewards. It depends on items, aura/vis, research and networking. The data/craft core and first `ROOT -> TOUCH -> FIRE` cast execution slice are complete; additional medium/effect families remain row-7 work. | Focus/caster core audit `10/10`, focus cast execution audit `5/5`, build, then dedicated audits before projectile/cloud/mine expansion. | Data Component focus packages, caster selected-focus state, Focal Manipulator craft cycle, aura costs and the first touch/fire cast mutation path are unblocked; remaining cast effects, Focus Pouch GUI and final editor visual parity remain. |
| 8 | Thaumonomicon final navigation and page renderers | Research data/protocol/page catalog are stable; the UI can now be expanded over real recipe/device state instead of placeholders. | Thaumonomicon protocol audit, research data/page catalog audits, runClient visual/navigation pass. | Search, drilldown/history, remaining fake/blueprint/special/custom recipe pages, final book UX blockers. |
| 9 | Crucible special alchemy and automation | Basic crucible behavior and recipe-page data exist; special effects need their own side-effect and world-mutation plan. | Crucible behavior audit, recipe data audit, aura flux checks, item-entity and spill regression tests. | Bath Salts, Bottled Taint, Liquid Death, Sane Soap, item pulling radius, special alchemy side effects, crucible-derived aspect generation. |
| 10 | Thaumatorium / advanced alchemy machines | Depends on crucible semantics, essentia transport, labels/phials and research pages. Do not implement before those are stable. | Thaumatorium design, recipe/input audit, transport capability audit, server smoke. | Automated alchemy, alchemical construct identities, advanced alchemy machine blockers. |
| 11 | Flux Goo, taint transforms and rifts | Depends on aura, crucible pollution, particles and safe world mutation rules. High-risk world behavior should not precede those foundations. | Dedicated flux/taint/rift design, aura/world mutation audit, server smoke, runClient FX review. | Flux ecology, finite taint spread/transforms, Flux Rift lifecycle, aura pollution consequences. |
| 12 | Infusion remaining dependency rolls and measured visual parity | Core infusion cycle is audited; residual instability rolls need their owning systems instead of placeholders. | Infusion behavior audit, FX payload audit, visual comparison pass, performance check around active altars. | Remaining instability effects, dependency-owned mutation rolls, final active altar visual parity. |
| 13 | Mirrors, lamps, bore, infernal furnace and other standalone devices | These are gameplay machines/devices, but they should land after transport, focus, alchemy and aura rules are less volatile. | One design/audit per family, item/block visual parity, server smoke. | Arcane Bore, Infernal Furnace, Lamp Fertility/Growth, mirrors, Void Siphon and other device blockers. |
| 14 | Custom Thaumcraft entities and mobs | Needs item/aspect/scan/research/rendering foundations. Entity AI/render is a large server/client split and should not block earlier machine work. | Entity registration/AI/render design, attributes/spawn audit, scan/aspect exporter update, dedicated server safety. | Custom mobs, projectiles, Eldritch/Crimson entities, entity scan/aspect gaps. |
| 15 | Golems and golem logistics | Golems need custom entities, inventories, item transport semantics, research and a lot of GUI/rendering. They should follow the entity/device foundation. | Golem data/AI task audit, logistics behavior tests, renderer smoke, save/load tests. | Golem Builder, seals/modules, logistics AI, golem inventory/task blockers. |
| 16 | Worldgen, ores, biomes and structures | Worldgen should wait until generated blocks/items/resources are stable enough to place in real worlds. | Datagen/biome modifier audit, new-world server smoke, structure/feature placement checks. | Biome placement, ore/tree generation beyond saplings, structures and world feature blockers. |
| 17 | Broad rendering, BEWLR, shaders and final model polish | Final broad visual work depends on stable item/block/entity identities. Keep it iterative and measured. | Visual parity framework, runClient screenshot review, render-state leak checks, dedicated server no-client-import check. | Held item models, legacy shader/Fx replacements, broad particle polish, final visual parity blockers. |
| 18 | Optional integrations | Integration APIs should be last because content ids and data contracts can still shift until core systems settle. | Optional dependency smoke, no-hard-dependency checks, missing-mod startup check. | Curios/accessory, recipe-viewer and other compatibility blockers. |
| 19 | Release hardening and migration cleanup | Only after core gameplay is stable enough to test like a mod, not a porting scratchpad. | Full build, client/server smoke, generated report cleanup, performance profiling, docs index review. | Placeholder/debug leakage, oversized generated artifacts, performance and release-readiness blockers. |

## Hard Dependencies

| Dependent work | Must wait for |
|---|---|
| Thaumatorium and advanced alchemy machines | Label/phial transfer, crucible semantics, essentia transport, research pages. |
| Focus/caster gameplay | Item/equipment behavior, aura/vis access, research unlocks, payload validation. |
| Flux rifts and taint transforms | Aura pollution, crucible spill behavior, particle/world mutation rules. |
| Golems | Custom entity foundation, item/block behavior, research UI/progression, logistics storage model. |
| Worldgen/structures | Stable block identities, loot/tags, visual parity for generated blocks, server datapack validation. |
| Broad rendering polish | Stable gameplay state and final-ish identities; otherwise visuals are reworked repeatedly. |

## Current Blocker Cut Line

The active cut line is currently back on row 4 utility blockers because focus/caster expansion is paused by user request:

1. Bellows is implemented and should remain regression-guarded.
2. Alembic/Jar label filters, phial transfer and jar item transfer are implemented through Data Components and covered by the combined runtime audit.
3. Tube caster side/choke/facing controls, manual/redstone valve state and vent state sync are implemented and covered by the combined runtime audit.
4. Remaining essentia utility devices are partially advanced: `essentiatransportin` and `essentiatransportout` are real blocks using the legacy `TileEssentiaInput` / `TileEssentiaOutput` contract. They place by clicked face, expose capability only on the back face, use the 16-block legacy source prism and transfer one essentia point every five ticks. The combined transport audit passes `52/52`.
5. Arcane Workbench regular recipes are parity-closed at `89/89`; the recipe audit passes `109/109`, and the workbench behavior audit passes `28/28` including goggles, cloth robe, void robe and provider-bridge vis discounts.
6. Item/equipment behavior contracts are closed for goggles, cloth robes, void robes, sanity checker, sane soap and Crimson Rites. The runtime item/equipment audit passes `17/17`; the Arcane Workbench audit remains `28/28` after the armor conversion.
7. Focus/caster/Focal Manipulator core is implemented for the first stable data/behavior boundary. Runtime focus/caster audit passes `10/10`: root + 20 focus element definitions, caster/focus/focus-pouch identities, focus package/default-setting math, selected focus Data Component, area-0 aura drain, Focal Manipulator design request validation, XP/crystal/vis start contract and focus package writeback.
8. The first server-owned cast-effect execution slice is implemented for legacy `ROOT -> TOUCH -> FIRE`. Runtime focus cast execution audit passes `5/5`: supported plan validation, unsupported package fail-before-drain, supported caster vis/cooldown, entity damage/burn and block fire placement.
9. Row 7 remains started but paused: additional focus medium/effect execution, Focus Pouch inventory/menu behavior and final Focal Manipulator editor visual parity are not the immediate active blocker-removal target.

## Immediate Next Batch

The next large safe batch should continue non-focus blocker removal:

- finish row-4 utility-device decisions: Void Jar, essentia mirrors, and whether `essentia_importer` / `essentia_exporter` stay as hidden placeholders or are removed from active recipes/pages;
- keep every utility device covered by focused behavior checks in the combined transport audit or a dedicated device audit;
- then design row 10 Thaumatorium over the now-real input/output transfusers, crucible semantics, jar/alembic/phial behavior and research-page state;
- do not copy legacy machine classes wholesale; preserve legacy ownership/cadence/data flow inside modern BlockEntity, capability, menu and payload boundaries.

Final valve/vent, robe/goggles armor model geometry, Focal Manipulator editor visual parity and Arcane Workbench visual parity should be handled under row 17 with screenshot or pixel-level evidence, because their first behavior/state contracts are already closed.
