# Remaining Subsystem Unblock Plan

Status: working order after the Bellows device slice.

Basis: `06_docs/migration/NeoForge_legacy_migration_guide.md`, `06_docs/migration/migration_matrix.md`, `06_docs/migration/porting_order.md`, `06_docs/current_port_status.md`.

## Rules

- Keep existing parity/audit harnesses green before starting a new subsystem family.
- Do not copy large legacy classes directly; port behavior into modern NeoForge registration, data, capability, menu, payload, saved-data and renderer boundaries.
- Prefer closing already-started dependency blockers before opening new broad systems.
- Keep bridge recipes, debug commands and placeholder identities marked as tooling until the owning gameplay subsystem exists.

## Ordered Blocks

| Order | Subsystem block | Why this order | Required validation | Main blockers removed |
|---:|---|---|---|---|
| 1 | Bellows device boundary | Already selected next device; closes smelter/tube/furnace Bellows blocker. | `audit-bellows-device.ps1`, build, server smoke, in-client visual check. | Bellows dynamic behavior/rendering. |
| 2 | Alembic label/phial transfer | Depends on the now-stable jar/Alembic aspect Data Components and transport storage. | Warded Jar/Alembic runtime checks plus new label/phial audit. | Aspect container item interaction, labeled jar UX. |
| 3 | Tube caster sub-parts and vent/valve visuals | Depends on tube state/shape already being stable; needed before polishing essentia network interaction. | Tube interaction audit, visual shape audit, runClient. | Caster controls, valve state clarity, vent visual parity. |
| 4 | Arcane Workbench remaining recipe families and discount bridge | Workbench behavior exists and can now absorb exact recipe batches without destabilizing research. | Arcane recipe audit, workbench behavior audit, aspect generated-cache diff. | Remaining arcane page/recipe gaps, equipment discount blocker. |
| 5 | Thaumonomicon final navigation polish | Research data/protocol/page catalog are stable; UI can now be expanded without guessing data semantics. | Thaumonomicon protocol audit, research page catalog audit, runClient visual pass. | Search, drilldown/history, final page renderer gaps. |
| 6 | Crucible special alchemy effects | Crucible recipe/page data and in-world manual/collision behavior exist; special outputs need their own safe side-effect plan. | Crucible behavior audit, aura flux checks, recipe data audit. | Bath Salts, Bottled Taint, Liquid Death, Sane Soap and related behavior. |
| 7 | Flux Goo, taint transforms and rifts | Depends on aura, crucible pollution, particles and world mutation rules; high-risk world behavior should not precede those. | Dedicated flux/taint/rift design, server smoke, world mutation audit. | Full flux ecology and taint/rift blockers. |
| 8 | Infusion remaining instability/dependency rolls and measured visual parity | Core infusion cycle is audited; the remaining six dependency-owned rolls need their subsystem effects. | Infusion behavior audit, FX payload audit, runClient visual pass. | Infusion residual parity gaps. |
| 9 | Golems and custom Thaumcraft entities | Needs stable research, items, rendering and saved data; large AI/render system should come after machine blockers. | Entity registration/AI/render design, scan/aspect exporter update. | Golem logistics, custom mobs, entity scan extensions. |
| 10 | Worldgen and structures | Needs blocks/items/resources stable enough to generate in real worlds. | Datagen/biome modifier audit, server worldgen smoke. | Biome placement, structures, ores/trees beyond saplings. |
| 11 | Broad rendering, BEWLR and shader/Fx polish | Depends on final item/block/machine/entity identities; should be iterative after gameplay state is stable. | Visual parity framework, runClient screenshot review, render-state leak checks. | Held-item models, legacy shaders, broad particle polish. |
| 12 | Integrations | Should be last because APIs and content ids can still shift until core systems settle. | Optional dependency smoke, no-hard-dependency checks. | Curios/accessory/recipe-viewer style integration blockers. |

## Immediate Next Batch

After Bellows validates, the next large safe batch is Alembic label/phial transfer:

- preserve aspect identity through existing aspect Data Components, not NBT;
- make Warded Jar/Alembic item interactions server-owned;
- add a dedicated runtime/static audit for label/phial behavior;
- keep caster/tube sub-part work out of that batch unless the audit proves it is required.
