# Research, player knowledge and scanning design

Status: design and legacy audit stage.
Target branch: research-knowledge-scanning-design.

This document defines the next subsystem after aspects and the first aura slice. The goal is to port Thaumcraft 6 research, player knowledge and scanning as close to the 1.12.2 behavior as possible, while keeping the implementation compatible with later Thaumonomicon, recipes, networking and gameplay systems.

## Scope

In scope for the next implementation phase:

- Player knowledge storage.
- Knowledge categories and raw point accounting.
- Basic research flag/key storage skeleton.
- Legacy-compatible command structure.
- Debug commands for knowledge and scanning.
- Minimal scanning service API.
- No full Thaumonomicon GUI yet.
- No full research page rendering yet.
- No crucible, infusion or arcane crafting integration yet.

This phase must not mix knowledge storage, full research GUI, recipe systems and gameplay effects in one patch.

## Legacy source files to inspect

- src/main/java/thaumcraft/common/lib/CommandThaumcraft.java
- src/main/java/thaumcraft/api/capabilities/IPlayerKnowledge.java
- src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledge.java
- src/main/java/thaumcraft/common/lib/research/ResearchManager.java
- src/main/java/thaumcraft/api/research/ResearchCategories.java
- src/main/java/thaumcraft/api/research/ResearchCategory.java
- src/main/java/thaumcraft/api/research/ResearchEntry.java
- src/main/java/thaumcraft/api/research/ResearchStage.java
- src/main/java/thaumcraft/api/research/ScanningManager.java
- src/main/java/thaumcraft/api/research/IScanThing.java
- src/main/java/thaumcraft/api/research/ScanItem.java
- src/main/java/thaumcraft/api/research/ScanBlock.java
- src/main/java/thaumcraft/api/research/ScanEntity.java
- src/main/java/thaumcraft/api/research/ScanAspect.java
- src/main/java/thaumcraft/api/research/ScanOreDictionary.java
- src/main/java/thaumcraft/common/lib/research/ScanGeneric.java
- src/main/java/thaumcraft/common/lib/research/ScanPotion.java
- src/main/java/thaumcraft/common/lib/research/ScanEnchantment.java
- src/main/java/thaumcraft/common/lib/research/ScanSky.java
- src/main/java/thaumcraft/common/config/ConfigResearch.java
- src/main/java/thaumcraft/common/items/tools/ItemThaumometer.java
- src/main/java/thaumcraft/common/lib/network/misc/PacketKnowledgeGain.java
- src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncKnowledge.java

## Legacy behavior summary

Legacy commands were exposed mainly through the thaumcraft command root with aliases. The important command groups were research, warp and reload.

For this port, research commands should stay legacy-compatible where the dependent subsystem exists. Warp commands should be reserved but not implemented until the warp subsystem exists.

Legacy PlayerKnowledge stored several different concepts:

- Completed research keys.
- Research stages.
- Research flags.
- Observation knowledge.
- Theory knowledge.

Observation and theory knowledge were not simply generic aspect unlocks. They were category-based knowledge values. The implementation must preserve this distinction, otherwise later research progression and theorycrafting will not match legacy behavior.

Legacy raw knowledge accounting used internal raw values instead of only user-facing integer points. The first implementation should expose both safe player-facing commands and internal raw serialization.

## Modern equivalent decisions

Data storage:

- First implementation should use a server-side player knowledge store keyed by player UUID.
- The storage must be independent from client GUI and from recipe systems.
- The storage must be serializable and stable enough to migrate later if a NeoForge player attachment approach is selected.
- The public API should be kept narrow, so later internal storage changes do not break research, scanning or Thaumonomicon code.

OreDictionary scanning:

- Legacy ScanOreDictionary must be mapped to tags and explicit item or block mappings.
- The current port already uses data/c tags, so scan predicates must use modern tags instead of trying to emulate Forge OreDictionary directly.

Networking:

- First patch can be server-side plus command output only.
- Client sync should be a later small patch.
- Do not start Thaumonomicon UI before the player knowledge model is stable.

## Command design

Aliases to preserve:

- /thaumcraft
- /thaum
- /tc

Legacy-compatible research command skeleton:

- /thaumcraft research list
- /thaumcraft research <player> list
- /thaumcraft research <player> all
- /thaumcraft research <player> reset
- /thaumcraft research <player> <research_key>
- /thaumcraft research <player> revoke <research_key>

Implementation note: research all, grant and revoke should remain skeleton or guarded until research categories, entries, parents, siblings, stages and rewards are ported. A command that lies about research completion is worse than a missing command.

Knowledge debug commands for the port:

- /thaumcraft knowledge <player> get
- /thaumcraft knowledge <player> get observation
- /thaumcraft knowledge <player> get theory
- /thaumcraft knowledge <player> get observation <category>
- /thaumcraft knowledge <player> get theory <category>
- /thaumcraft knowledge <player> add observation <category> <points>
- /thaumcraft knowledge <player> add theory <category> <points>
- /thaumcraft knowledge <player> set observation <category> <points>
- /thaumcraft knowledge <player> set theory <category> <points>
- /thaumcraft knowledge <player> clear
- /thaumcraft knowledge <player> clear observation
- /thaumcraft knowledge <player> clear theory
- /thaumcraft knowledge <player> clear observation <category>
- /thaumcraft knowledge <player> clear theory <category>

Aspect commands:

- Do not implement grant_aspect as the primary command in the first patch.
- Thaumcraft 6 player knowledge is not simply a set of discovered aspects.
- If aspect discovery is later needed for GUI/tooltips, it should be designed after checking all legacy research and scan usage.

Scanning debug commands:

- /thaumcraft scan held
- /thaumcraft scan looking
- /thaumcraft scan test_held

The first scan command can report the object key and computed aspects without permanently changing knowledge, unless explicitly named scan or grant behavior is ready.

## Initial code architecture

Proposed classes for first implementation patch:

- thaumcraft.common.research.TCResearchCommands
- thaumcraft.common.research.TCKnowledgeCommands
- thaumcraft.common.research.TCScanningCommands
- thaumcraft.common.research.TCPlayerKnowledge
- thaumcraft.common.research.TCPlayerKnowledgeStore
- thaumcraft.common.research.TCKnowledgeType
- thaumcraft.common.research.TCResearchCategoryKey
- thaumcraft.common.research.TCScanResult
- thaumcraft.common.research.TCScanningManager

The public helper/API layer should be small:

- getKnowledge(player)
- addKnowledge(player, type, category, amount)
- setKnowledge(player, type, category, amount)
- clearKnowledge(player)
- hasResearch(player, key)
- addResearch(player, key)
- revokeResearch(player, key)

Do not expose mutable internal maps directly.

## Implementation slices

Patch 1: design and audit only.

- Create this document.
- Copy legacy sources into the audit folder.
- Record exact open questions.

Patch 2: knowledge storage and commands.

- Server-side player knowledge store.
- Observation and theory categories.
- get, add, set and clear commands.
- Basic serialization.
- No client GUI.

Patch 3: research command skeleton.

- list/reset/basic grant/revoke skeleton.
- Placeholder protection where research data model is missing.
- Compatible command names and aliases.

Patch 4: scanning manager skeleton.

- held item scan.
- looking block/entity scan.
- map ScanOreDictionary to modern tags.
- report aspects through AspectHelper.
- do not implement full Thaumometer GUI yet.

Patch 5: minimal Thaumometer item behavior.

- Right-click or use action for scan.
- Server-side validation.
- Command/debug output first.
- Client feedback later.
- Current implementation: `thaumcraft:thaumometer` is registered with its legacy runtime aspect value, appears in the creative tab, uses the legacy 3D `scanner.obj` item model with the separate alpha `scanscreen` pane texture, plays `thaumcraft:scan`, and right-click runs the validated server-side scan target path. The item calls `scanTheThing`, and scan-key mutation is routed through `TCResearchManager.progressResearch`.
- Current implementation: legacy scan learning side effects have started. `ScanAspect` grants raw observation units exactly like 1.12 (`+1` raw to AUROMANCY, BASICS and ALCHEMY), and `TCScanGeneric.onSuccess` applies the legacy category formula to the scanned aspect list before adding raw OBSERVATION knowledge.

Patch 6: knowledge sync.

- Small payload for client cache.
- Required only when UI/tooltips need player-specific knowledge.
- Avoid broad networking framework until needed.
- Current implementation: a narrow `knowledge_sync` payload sends completed research keys on login, respawn, dimension change, and server-side knowledge mutation. It exists so Thaumometer highlight can filter already-known scan keys without starting the full Thaumonomicon/networking subsystem.

## Open questions before coding

- Does the current branch already have an agreed player-data storage pattern, or should knowledge use SavedData first?
- Should knowledge be stored per UUID globally, or in player persistent data?
- Which research categories should exist before full research JSON is ported?
- Should BASICS be hardcoded as a bootstrap category or loaded from research data?
- Which exact legacy packets must be ported first for knowledge sync?
- Which scan types are required before Thaumometer is useful?
- Should scan_held mutate knowledge or only simulate scan in the first patch?
- How should old OreDictionary names be mapped to current tags?
- Which commands should require permission level 2, and which should require level 4?

## Immediate next action

After this design patch is committed, the next code patch should implement only player knowledge storage and knowledge commands.

Do not implement full research data, Thaumometer GUI, warp, theorycrafting or research pages in the same patch.

## Research data model skeleton

Started:
- Added reload-safe server data loader for legacy-style research JSON under `data/thaumcraft/research/*.json`.
- Copied the eight legacy Thaumcraft research entry files into the modern server-data path: `alchemy`, `artifice`, `auromancy`, `basics`, `eldritch`, `golemancy`, `infusion`, and `scans`.
- Added minimal model records for categories, entries and stages.
- Added hardcoded legacy category definitions and formulas from `ConfigResearch.initCategories`, because the original category metadata was Java registration code, not JSON.
- Added `/thaumcraft research list` summary and `/thaumcraft research info <key>` inspection commands.
- Server reload validates the current skeleton as `7` categories, `148` entries, `271` stages, and `16` addenda.
- Added read-only research reference validation for `parents`, `siblings`, and `required_research`. The validator strips legacy stage suffixes like `@2`, preserves case-sensitive keys, separates scan/flag triggers such as `!OREAMBER`, `f_toomuchflux`, and `m_deepdown`, and currently reports `201` resolved entry references, `95` external trigger references, and `0` unresolved research references.
- Added legacy-shaped visibility helpers for category visibility, research entry visibility, and unlockability. These are server-side helpers for the future Thaumonomicon screen and command audit, not the GUI itself.
- Added the first server-side checked-stage advancement layer, matching the role of legacy `PacketSyncProgressToServer.checkRequisites`.
  - Normal `progressResearch` still mirrors the legacy command/scan progression path and checks only parent requisites.
  - `completeCurrentStageWithChecks` is the future Thaumonomicon page-click path: it checks the current stored stage, verifies `required_item`, `required_craft`, `required_research`, and `required_knowledge`, consumes item/knowledge requirements only after all checks pass, then advances the research.
  - `required_research` uses the existing strict `&&`, `||`, and `@stage` logic.
  - `required_knowledge` consumes raw points by `points * type.progression`, preserving the legacy distinction between displayed points and raw storage.
  - `required_item` currently supports resolvable modern item ids, legacy flattening bridges for old metadata-shaped ids, component-backed aspect crystal/phial requirements, component-backed legacy material-family mappings, legacy enchanted-placeholder matching against real modern enchantment components, and tag-backed `oredict:*` checks.
  - `required_craft` currently checks stored craft markers and reports missing markers. The non-interactive requirement audit exporter reports `0` identity-unresolved item/craft/knowledge stage requirements, but also reports bridge/placeholder warnings where final item semantics, recipes or subsystem behavior are not complete. OreDictionary craft markers use the exact Java string hash of `oredict:<name>`; direct ItemStack craft hashes still need a dedicated legacy exporter/mapping before they can be claimed exact.
- Added modern `PlayerEvent.ItemCraftedEvent` handling for craft markers. When a crafted result matches a resolvable `required_craft` entry, the port stores the same hidden research-marker role that legacy used for `[#]...` craft completion. This covers resolvable modern ids and tag-backed `oredict:*` markers; exact direct legacy ItemStack hash ids remain a separate parity task.
- Added debug commands:
  - `/thaumcraft research <player> stage <research_key> check`
  - `/thaumcraft research <player> stage <research_key> advance`
  These are server-authoritative diagnostics for the future Thaumonomicon action path, not a final UI.

Not implemented by this skeleton:
- Thaumonomicon final visual parity. Player-visible filtering, browser search over the server-visible index, server-owned recipe drilldown/history, and first browser/entry rendering are server-authoritative and implemented.
- Exact direct `required_craft` ItemStack hash parity for legacy marker ids.
- Producer/container behavior for legacy metadata/NBT-heavy items such as phials, crystal essence, material-family outputs, and unported Thaumcraft ids.
- Custom recipe-page rendering for blueprint, fake/display-only, special and grouped pages.
- Full warp events/effects/client sync and cancellable research/knowledge events.

## Progression parity closure checkpoint

- Checked stage advancement now plans all item and knowledge consumption before mutating inventory, knowledge, or stage state.
- Exact legacy stage advancement, empty-gate rules, warp calculation/split, `wussMode`, completion flags, entry rewards, addendum `PAGE` notifications, siblings, XP, and final sync ordering are implemented.
- `reward_item` and `reward_knowledge` are preserved by the runtime parser even though the built-in TC6 research files do not currently use them.
- `07_Test_Instance_and_Comparisons/research_data_parity` compares legacy JSON, NeoForge source JSON, NeoForge runtime parser output, and all seven Java-registered legacy categories.
- Latest parity result: `148/148` entries, `7/7` categories, `0` source differences, `0` runtime differences, and `10/10` progression/parser checks passed.
- The permanent research recipe/page catalog, server-authoritative Thaumonomicon protocol, real item/open flow, first browser/entry/search/drilldown screens, and first vanilla crafting, arcane, crucible and infusion page renderers are implemented. The catalog preserves `253` research occurrences, `203` direct references, and `325` entries including group members with `0` parity differences; the protocol audit passes `34/34`, including the legacy known-entry final-stage progression path, explicit-open-versus-refresh separation, server-built index revision, stale-action and stale-drilldown rejection without mutation, server-owned crafting/arcane/crucible/infusion snapshots, and recipe output-stack drilldown resolution.
- The next research/crafting boundary should continue through audited recipe-page families or a focused in-world crucible/alchemy design slice, not client-side recipe resolution or invented deferred content.

Runtime note:
- The loader intentionally preserves legacy references as strings. Many recipe/item references still point at 1.12 ids or unported Thaumcraft content, so resolving them eagerly would make the skeleton unusable until much later gates.

## Patch 4 status

Started:
- Added minimal TCScanningManager.
- Added TCScanResult.
- Added /thaumcraft scan held and /thaumcraft scan looking debug commands.
- Added public legacy-shaped `thaumcraft.api.research.IScanThing` and `ScanningManager` shell.
- Added modern equivalents for `ScanItem`, `ScanBlock`, `ScanEntity`, `ScanAspect` and `ScanOreDictionary`.
- Added initial generic scan predicate equivalent for aspect-bearing item/block/entity objects.
- Restored legacy `Aspect` constructor scan hook semantics and reload-safe aspect predicate re-registration.
- Research keys are now trimmed but keep legacy case because keys like `f_toomuchflux` and `!minecraft:*` are case-sensitive contracts.
- Added reloadable `data/thaumcraft/scannables/*.json` definitions.
- Added bundled `legacy_core.json` with currently valid legacy scan entries.
- Added modern tag-based scan predicate for legacy material/tag bridges.
- Added dynamic modern equivalents for legacy `ScanPotion` and `ScanEnchantment`.
- Added gated `ScanSky` predicate without celestial-note item side effects.
- Added vanilla entity aspect assignments for scan targets, including exact 1.12 vanilla rows, powered creeper bonus, 1.21 entity type remaps, and documented post-1.12 entity policy rows. `elder_guardian` and `zombie_villager` now intentionally diverge from exact 1.12 runtime no-aspect behavior as documented living-mob policy corrections.
- Added registered `thaumcraft:scan` sound playback to Thaumometer use.
- Added shared legacy-shaped Thaumometer target resolver: scan/use entity targets use min range `1`, range `9`, padding `0`, inflated entity hitboxes and line-of-sight checks; held entity highlight uses range `16` with padding `5`; held block highlight uses a separate range `16` wild block ray with random yaw/pitch spread.
- Added client-side Thaumometer scan visuals: right-click rune particles, held-target sparkle highlight for potential not-yet-known scan keys, and floating aspect icons/amounts for normal living mobs only. The aspect overlay remains visible for aspect-bearing living mobs even after their scan keys are known; the sparkle highlight is the part gated by not-yet-known scan keys.
- Added minimal completed-research key sync for client highlight filtering. This is not full research sync, but the Thaumometer now mutates completed scan research keys through the current predicate layer.
- Added a first legacy-like research progression service: known/in-progress/complete status, stage storage, research flags, parent requisites with `&&`, `||`, `@stage`, hidden `~` prefix stripping, `progressResearch`, `completeResearch`, `startResearchWithPopup`, and sibling propagation.
- Updated research commands so `/thaumcraft research <player> <key>` uses `completeResearch`, `/thaumcraft research <player> all` progresses all loaded entries through the same service, `/thaumcraft research <player> status <key>` reports status/stage/visibility/requisites, and `/thaumcraft research <player> visible [category]` audits the current legacy-shaped visibility set.
- Added `post_1_12_scanning_policy.md` for new vanilla item/block/effect/enchantment handling.
- Added `scanning_parity_validation.md`, `/thaumcraft scan audit_items`, automated `-PtcScanDump=true` server dumps, and `07_Test_Instance_and_Comparisons/scan_parity` for deterministic scan/aspect/research-key dumps.
- The scan comparer now reports `1139/1139` comparable item/potion/enchantment stack rows as parity-ok, with `0` aspect-value or scan-logic differences.
- Added `scanning_gap_audit.md`.
- Restored the legacy dropped-item scan path: the modern look-target finder now allows `ItemEntity`, and predicates unwrap the contained `ItemStack` like 1.12.2.
- Added `/thaumcraft scan audit_entities` and automated `-PtcScanEntityDump=true` server dumps.
- Added Forge 1.12.2 entity/state-variant exporter and comparer. The latest entity comparer reports `83/85` comparable entity rows parity-ok, `2` expected modern entity policy rows, and `0` actionable scan/aspect gaps.
- Scan commands currently report aspect lookup results and matched scan keys without mutating player knowledge.
- Thaumometer right-click uses the same legacy target order and mutates scan keys only when `ScanningManager.scanTheThing` finds a new key or a blank/suppressed scan. Already-known keys do not retrigger success/onSuccess, matching legacy `progressResearch` semantics.
- While held, the Thaumometer sends the current aura chunk every 20 ticks and grants the `FLUX` warning key when the legacy flux thresholds are exceeded.
- Generic scan observation rewards now use the same `ResearchCategory.applyFormula` math as 1.12.2 and are added as raw observation units, not full player-facing points.

Still pending:
- Research-gated scan success for future stage-specific gameplay requirements beyond parent requisites.
- Scan success rewards beyond current `ScanAspect` and `ScanGeneric` observation points.
- Full legacy scannable definitions from `ConfigResearch.initScannables` once missing ids exist.
- Thaumonomicon browser/entry rendering over the authoritative filtered view models.
- `ScanSky` celestial-note creation side effects.
- Legacy aura HUD/meter rendering while held.
- Full research/knowledge sync beyond completed research keys, including stages and flags.

## Action plan after GPT web chat import

1. Keep aura frozen as API/storage/debug infrastructure until real consumers exist.
2. Finish the current scanning debug and predicate layer without GUI or gameplay side effects.
3. Add a data-driven scannable definition format after the predicate model is stable.
4. Continue minimal Thaumometer use behavior from dry-run scan feedback toward legacy success mutation only after the research data/model skeleton exists.
5. Add knowledge sync only when client UI/tooltips need player-specific state.
6. Delay Thaumonomicon pages, warp, theorycrafting, recipe gates and recursive research unlocks until research category/entry loading exists.
