# Shobie 1.20.1 Port Merge Experiment

Date: 2026-06-12  
Branch: `codex/experiment-shobie-1-20-merge`  
External source: `ShobieShy/Thaumcraft-6-Source-Code-1.20.1`, HEAD observed as `55975deacf94f651739d74bb88c15dfae0e0a983`  
Local source used: `C:\Users\Martin\Desktop\Thaumcraft-6-Source-Code-1.20.1-master`

## Goal

Test whether the current NeoForge 1.21.1 port can be safely combined with the Forge 1.20.1 port as an experimental branch.

The migration guide explicitly warns against copying old or cross-loader code directly. For this experiment, the current NeoForge project remains the base. The 1.20.1 port is treated as a secondary reference and import candidate, not as authoritative source.

## Baseline

Before the experiment, the current NeoForge project built successfully:

```text
.\gradlew.bat build --no-daemon
BUILD SUCCESSFUL
```

Current project scale:

| Source | Java files | Resource files | Notes |
| --- | ---: | ---: | --- |
| Current NeoForge 1.21.1 port | 237 | 2493 | Aspect, aura, scanning, research, early crafting work present |
| Shobie Forge 1.20.1 port | 744 | 2447 | Much larger surface, Forge-specific runtime and client/common code |

## Compatibility Findings

Direct Java merge is not safe in one pass.

| Risk | Evidence | Decision |
| --- | --- | --- |
| Forge API usage | `net.minecraftforge` references: 721 | Do not copy Java directly |
| Forge registry model | `RegistryObject` references: 726, `ForgeRegistries`: 32 | Needs NeoForge registry rewrite |
| Client/common mixing | `net.minecraft.client` references: 739, `Minecraft.getInstance`: 100 | Must be split into client-only handlers |
| Forge JEI integration | `mezz.jei` references: 33 | Needs optional integration layer for target loader/version |
| Curios dependency | Curios references present | Needs dependency decision for 1.21.1 |
| Dedicated server safety | Shobie `runServer` failed with client-only class on dedicated server | Not safe as architecture source |

Data-pack import is also not safe in one pass.

| Data area in Shobie port | File count | Reason not imported runtime |
| --- | ---: | --- |
| `data/thaumcraft/recipes` | 270 | Recipe type ids and serializers must match our 1.21 registry first |
| `data/thaumcraft/worldgen` | 41 | Worldgen JSON format changed between 1.20.1 and 1.21.1 |
| `data/thaumcraft/tags` | 29 | Uses 1.20/Forge path conventions such as plural `tags/items` |
| `data/forge` | 15 | Forge namespace, not NeoForge-native |
| `data/minecraft/tags` | 10 | Needs manual review before runtime inclusion |

## Applied Runtime Import

Only non-overwriting assets were imported from the 1.20.1 port:

| Asset area | Imported files | Notes |
| --- | ---: | --- |
| `assets/thaumcraft/models` | 330 | Extra models only, no existing model overwritten |
| `assets/thaumcraft/textures` | 116 | Extra textures only |
| `assets/thaumcraft/blockstates` | 64 | Extra blockstate files only |
| `assets/thaumcraft/lang` | 8 | Extra modern `.json` language files |
| Total | 518 | Existing adapted 1.21 assets were preserved |

Texture references in imported models were checked after import. Legacy naming mismatches were adapted to existing textures where possible, for example:

| Imported reference | Adapted to |
| --- | --- |
| `thaumcraft:item/celestial/moon_1` | `thaumcraft:item/celestial/moon1` |
| `thaumcraft:item/celestial/stars_1` | `thaumcraft:item/celestial/stars1` |
| `thaumcraft:item/seals/seal_provide` | `thaumcraft:item/seals/seal_provider` |
| `thaumcraft:item/phial_empty` / `phial_filled` | `thaumcraft:item/phial` |
| `thaumcraft:item/label_blank` / `label_filled` | `thaumcraft:item/label` |
| `thaumcraft:item/research_notes` | `thaumcraft:research/research1` |
| `thaumcraft:item/research_complete` | `thaumcraft:research/research5` |

Post-import model texture reference check:

```text
missing_thaumcraft_texture_refs=0
```

## Not Applied

The following were intentionally not imported into runtime:

- Forge 1.20.1 Java classes.
- Shobie recipe JSON files.
- Shobie worldgen JSON files.
- Shobie Forge tags and biome modifiers.
- Shobie Gradle/build files.
- Shobie `META-INF/tc_at.cfg` access transformer.

This keeps the branch testable and avoids hiding real porting work behind a large broken compile diff.

## Next Safe Merge Step

If this branch builds and client resource loading is acceptable, the next step is not a full Java import. The next useful merge step is a generated comparison table:

1. Registered ids in current `TCBlocks`/`TCItems` versus Shobie `ModBlocks`/`ModItems`.
2. Shobie recipes classified by type and output id.
3. For recipes whose output item already exists in our port, convert one recipe family at a time to our NeoForge 1.21 recipe serializers.
4. Only after recipe serializers are stable, import selected recipe JSON into `data/thaumcraft/recipe`.

Recommended first code subsystem candidate: recipe data and recipe catalog comparison, not blocks/entities/golems.

