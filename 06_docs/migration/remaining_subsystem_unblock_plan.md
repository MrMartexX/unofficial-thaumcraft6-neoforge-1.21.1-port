# Remaining Subsystem Unblock Plan

Status: working order after the Arcane Workbench regular-recipe and equipment-discount bridge closure.

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
| 4 | Remaining essentia utility devices | After transport controls exist, devices that consume/produce essentia can be implemented without inventing ad hoc paths. | Dedicated behavior audits per device family, combined transport audit, server smoke. | Void Jar overflow, importer/exporter if kept, essentia mirror path, Thaumatorium input/output dependency blockers. |
| 5 | Arcane Workbench remaining recipes and equipment discount bridge | Completed for the regular legacy recipe id set and first equipment-discount bridge. Keep this row as a regression guard before item/equipment behavior work. | Arcane recipe audit `109/109`, workbench behavior audit `28/28`, generated-aspect cache reload count, runClient GUI pass for final visual review. | Regular arcane page/recipe gaps, vanilla armor-slot vis discount, first accessory-provider bridge, recipe-derived aspect blockers. |
| 6 | Item/equipment behavior pass | Many registered items currently exist as identities/recipes but still lack real behavior. Close them before focus/golem/worldgen systems depend on them. | Item behavior audit, creative tab check, tooltip/aspect check, runClient smoke. | Goggles/robes/baubles-equivalent behavior, sanity checker, utility item behavior, non-final item placeholders. |
| 7 | Focus/caster/Focal Manipulator core | Caster/focus is a central gameplay API and blocks many combat, utility and research rewards. It depends on items, aura/vis, research and networking. | Focus data model audit, server-authoritative cast payload audit, client FX leak checks, runClient combat/utility smoke. | Caster gauntlet behavior, focus pouch, focus modifiers, Focal Manipulator, focus recipes/research consumers. |
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

The current cut line is after row 5:

1. Bellows is implemented and should remain regression-guarded.
2. Alembic/Jar label filters, phial transfer and jar item transfer are implemented through Data Components and covered by the combined runtime audit.
3. Tube caster side/choke/facing controls, manual/redstone valve state and vent state sync are implemented and covered by the combined runtime audit.
4. Remaining essentia utility devices have already been partially advanced by later batches; keep any unfinished devices scoped to row 4 audits.
5. Arcane Workbench regular recipes are parity-closed at `89/89`; the recipe audit passes `109/109`, and the workbench behavior audit passes `28/28` including goggles, cloth robe, void robe and provider-bridge vis discounts.
6. The next real blocker is row 6: item/equipment behavior pass.

## Immediate Next Batch

The next large safe batch should start row 6, not reopen row 5:

- audit the registered item/equipment identities that still behave as placeholders;
- implement behavior by dependency family, starting with safe wearable/utility items before focus/caster gameplay;
- keep robe/goggles wearable/rendering behavior separate from the already-audited vis-discount math;
- add item behavior audit coverage and creative-tab/tooltip checks with each family;
- do not start Focus/Focal Manipulator gameplay until row 6 confirms the required item/equipment contracts.

Final valve/vent and Arcane Workbench visual parity should be handled under row 17 with screenshot or pixel-level evidence, because their behavior/state contracts are already closed.
