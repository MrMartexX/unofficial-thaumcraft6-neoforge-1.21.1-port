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

Patch 6: knowledge sync.

- Small payload for client cache.
- Required only when UI/tooltips need player-specific knowledge.
- Avoid broad networking framework until needed.

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