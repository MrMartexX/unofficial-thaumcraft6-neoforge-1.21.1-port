# Thaumcraft 6 Runtime Asset Audit

Last reviewed branch: `main`
Last reviewed commit: `70ec2f06ff06d53f7119f7db9adb83b792368874`
Target module: `05_neoforge_port`
Date: 2026-05-06

## Scope

This audit is limited to startup/runtime warnings for the current NeoForge 1.21.1 port after the legacy asset corpus import. It does not attempt to fully validate unported legacy resources.

Guide rule applied: active 1.21 resources for registered content are authoritative. Imported legacy resources are reference/base material until their subsystem is ported.

## Validation Runs

| Check | Result | Notes |
|---|---|---|
| `.\gradlew.bat build --no-daemon` | Passed | Run from `05_neoforge_port` before the asset checkpoint commit. |
| `.\gradlew.bat runClient --no-daemon` | Passed startup and integrated-world smoke check | Client log from `2026-05-06-1.log.gz`; player joined and client stopped cleanly. A second short resource smoke after fixes reached model/audio initialization with no Thaumcraft missing texture warnings. |
| `.\gradlew.bat runServer --no-daemon` | Started successfully | Dedicated server reached `Done (5.095s)! For help, type "help"`. The Gradle process did not consume piped `stop`, so the server/Gradle Java processes were terminated after successful startup. |

## Audit Table

| Category | Findings | Current action | Notes |
|---|---|---|---|
| Missing models | No active registered missing-model warning was found in the reviewed logs. | No change. | This does not validate every imported legacy model. |
| Missing textures | Client reported missing textures for `thaumcraft:fabric#inventory`, `thaumcraft:amber#inventory`, and `thaumcraft:quicksilver#inventory`. | Fixed active item models to use `thaumcraft:item/fabric`, `thaumcraft:item/amber`, and `thaumcraft:item/quicksilver`, then copied those three active PNGs into `textures/item`. | The modern atlas includes `textures/item`; the legacy `textures/items` copies remain reference/base assets. Fresh client resource smoke no longer reports these Thaumcraft missing texture warnings. |
| Shader warnings | Client reported vanilla/NeoForge shader warning: `Shader rendertype_entity_translucent_emissive could not find sampler named Sampler2`. | No change. | Not tied to active Thaumcraft shader wiring. Imported legacy shader files remain reference material. |
| Lang fallback | Active registered items and blocks have `en_us.json` entries. Server debug log loaded `en_us` language files. | No change. | Legacy `.lang` files stay as reference until each content slice is ported. |
| Sound warnings | Client reported missing vanilla goat horn sounds: `minecraft:item.goat_horn.play` and `minecraft:entity.goat.screaming.horn_break`. | No Thaumcraft change. | Not caused by Thaumcraft registered content. |
| OBJ/MTL issues | No active OBJ/MTL warning was found in the reviewed startup logs. | No change. | Imported OBJ/MTL models are not considered ported by import alone. |
| Server-only resource issues | Dedicated server startup had no Thaumcraft-specific resource error. | No change. | Server warnings were OSHI/Windows counters, vanilla command ambiguity, NeoForge asset URL schema, offline-mode notice, and LAN pinger network reachability. |

## Active Resource Coverage Snapshot

| Resource surface | Status | Notes |
|---|---|---|
| Registered block ids | 43 block ids in `TCBlocks`. | Current blockstate/model/loot/lang coverage was checked for registered ids only. |
| Registered item ids | 47 item/block-item ids in `TCItems`. | Includes block items plus `goggles`, `amber`, `quicksilver`, and `fabric`. |
| Active item models | Covered after the three texture path fixes and active PNG copies into `textures/item`. | Do not mass-convert unregistered legacy `thaumcraft:items/*` references yet. |
| Active blockstates/models | Covered for registered blocks. | Modern `blockstates`, `models/block`, and `textures/block` paths remain authoritative. |
| Active loot tables | Present under modern data path for registered blocks. | Legacy `assets/thaumcraft/loot_tables` is not the 1.21 data path and remains reference. |

## Follow-Up

1. Use the next full visual pass to inspect the creative tab and active item icons manually.
2. Keep legacy `textures/blocks`, `textures/items`, old `.lang`, `research`, `shader`, OBJ/MTL, and legacy loot resources untouched until their subsystem is intentionally ported.
3. Fix only startup-breaking or active-content warnings before expanding into new gameplay systems.
