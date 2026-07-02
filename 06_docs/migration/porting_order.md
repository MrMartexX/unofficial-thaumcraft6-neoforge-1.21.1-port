# Thaumcraft 6 to NeoForge 1.21.1 Porting Order

Source basis: `06_docs/migration/subsystem_inventory.md`

Migration guide basis: `06_docs/migration/NeoForge_legacy_migration_guide.md`

This document defines a staged porting order. It is not an implementation plan for copying legacy classes directly. The legacy Thaumcraft 6 source should be used as behavioral reference. NeoForge 1.21.1 work must rebuild registration, data loading, storage, networking, screens, worldgen, and client rendering through current APIs.

## Ordering Rules

- Start with a clean NeoForge 1.21.1 project that launches with mod id `thaumcraft`.
- Port stable identity/data layers before stateful gameplay.
- Prefer `DeferredRegister`, data/datagen, tags, recipe serializers, attachments, saved data, and custom payloads over legacy Forge 1.12.2 APIs.
- Keep gameplay concepts before preserving class names.
- Avoid porting GUI/rendering/worldgen early; they depend on stable registries, data models, and sync.
- Some requested stages are intentionally split into skeleton and completion work. For example, GUI can be scaffolded before final networking, but cannot be considered complete until payloads are stable.

## Stage Status Snapshot

This table is a roadmap snapshot, not the live task queue. Use `06_docs/CURRENT_TASK.md` for current priorities and `06_docs/current_port_status.md` for detailed implementation state.

| Stage | Status | Current note |
|---|---|---|
| Stage 1. Minimal Launch | Done | Client and dedicated server bootstrap exist and remain guarded by build/server smoke checks. |
| Stage 2. Items / Materials | In progress | Identity, bridge items, selected materials, models, lang, tags, creative ordering, the first audited wearable/utility behavior contracts and the first caster/focus item Data Component core are active; many dependency-heavy item behaviors remain deferred. |
| Stage 3. Blocks / Basic Resources | In progress | Basic blocks, candles, tables and Arcane Workbench exist; all six tube blocks now use legacy multipart connection topology and shapes. Broad machine behavior remains incomplete. |
| Stage 4. Aspects | In progress | Core aspect model, item/entity assignments, parity harnesses and current generated cache are active; gameplay-heavy consumers remain staged. |
| Stage 5. Aura | In progress | Server-side saved data, chunk initialization, update loop, debug commands and Workbench Charger aura usage exist; HUD/FX/rifts/consumers remain deferred. |
| Stage 6. Research | In progress | Data/progression/page-catalog core, scan knowledge, research table slice and first Thaumonomicon flow exist; full UI/search/warp polish remains blocked. |
| Stage 7. Crafting | In progress | Vanilla fixtures, exact arcane recipes, Arcane Workbench behavior, crucible gameplay slices, and infusion through structure-derived timing/cost, persistent stability, exact 24-way event selection, essentia/component consumption and output placement are active; six dependency-owned instability rolls and remaining recipe families are deferred. |
| Stage 8. Tile Entities | In progress | Research Table, Arcane Workbench, Crucible, Infusion Matrix/Pedestals, Focal Manipulator, tubes, Warded Jar, Void Jar, Essentia Mirror, Alembic, Bellows, all three smelter tiers and the first Thaumatorium machine foundation own persisted server state. The combined transport/smelter/label/phial/caster-control/Void-Jar/essentia-mirror audit passes `61/61`; Thaumatorium passes `16/16`; Bellows and focus/caster core have dedicated audits. |
| Stage 9. GUI | In progress | Research Table, Arcane Workbench, smelter, Focal Manipulator minimal core screen and first Thaumonomicon screens exist as functional slices; final visual parity and recipe drilldown remain incomplete. |
| Stage 10. Networking | In progress | Aura, knowledge, research table, Thaumonomicon, both clientbound infusion FX payload contracts and the Focal Manipulator design-intent payload exist; future gameplay payloads need subsystem-specific design. |
| Stage 11. Entities / Golems | Deferred | Vanilla/entity scan parity is handled; custom Thaumcraft entities, golems, AI and renderers are not started. |
| Stage 12. Worldgen | In progress | Sapling-grown Greatwood/Silverwood behavior exists; biome/world placement and structures are not started. |
| Stage 13. Rendering / Particles | In progress | Thaumometer effects, table renderer, infusion FX, crucible FX, legacy multipart tube geometry and valve/vent state rendering exist; measured visual parity and broad render/BEWLR/shader work remain high risk. |
| Stage 14. Integrations | Deferred | Optional accessory/recipe-viewer integrations are intentionally held until core systems stabilize. |

## Stage 1. Minimal Launch

**Goal**

Create a clean NeoForge 1.21.1 mod that launches client and server with no Thaumcraft gameplay content yet.

**Scope**

- Gradle/NeoForge project setup.
- Java 21 compatibility.
- Mod metadata.
- Main mod class.
- Common/client setup split.
- Empty registry holder classes.
- Empty config skeleton.
- Basic datagen skeleton.
- Logging and development run configs.

**Legacy references**

- `thaumcraft.Thaumcraft`
- `thaumcraft.Registrar`
- `thaumcraft.proxies.*`
- `thaumcraft.common.config.ModConfig`
- `src/main/resources/mcmod.info`
- `src/main/resources/META-INF/tc_at.cfg`

**Dependency blockers**

- Choose exact NeoForge 1.21.1 version and mappings.
- Decide target package namespace for the port.
- Decide whether the port keeps `modid = thaumcraft`.
- No content stages should begin until the empty mod launches in client and dedicated server.
- Access transformer entries must be treated as audit items, not blindly recreated.
- Old proxy pattern must be replaced before client-only code is introduced.

**Exit criteria**

- Client starts with the new mod loaded.
- Dedicated server starts with the new mod loaded.
- No legacy Thaumcraft code is required for startup.
- Empty registries and config specs compile.

## Stage 2. Items / Materials

**Goal**

Establish the item/material identity layer before block entities, recipes, research, or rendering depend on it.

**Scope**

- Basic item registry.
- Materials and tool/armor constants.
- Simple item classes with minimal behavior.
- First safe wearable/utility behavior contracts where dependencies are available.
- Item tags.
- Data component strategy for old item NBT.
- Placeholder creative tab contents.

**Legacy references**

- `thaumcraft.api.items.ItemsTC`
- `thaumcraft.api.ThaumcraftMaterials`
- `thaumcraft.common.config.ConfigItems`
- `thaumcraft.common.items.*`
- `thaumcraft.common.items.casters.ItemCaster`
- `thaumcraft.common.items.casters.ItemFocus`
- `thaumcraft.api.items.IRechargable`
- `thaumcraft.api.items.IVisDiscountGear`
- `thaumcraft.api.items.IWarpingGear`
- `thaumcraft.api.items.IGoggles`
- `thaumcraft.api.items.IRevealer`
- `thaumcraft.api.items.IScribeTools`

**Dependency blockers**

- Stage 1 registry scaffold must be complete.
- Decide item naming and registry id compatibility policy.
- Decide which old metadata variants become separate items and which become data components.
- Tags must replace legacy `OreDictionary` usage.
- Accessory integration must not block this stage; Baubles replacement belongs to Stage 14.
- Aspect-bearing item data should be stubbed until Stage 4.

**Exit criteria**

- Basic items register and appear in a creative tab.
- Materials compile without old Forge 1.12.2 APIs.
- Goggles, robe armor, sanity checker, sane soap and Crimson Rites behavior contracts stay covered by the item/equipment runtime audit.
- Caster/focus item identity and core focus package/selected-focus Data Components stay covered by the focus/caster runtime audit before cast effects expand.
- The base item/material layer does not require broad recipe, aspect, aura or research systems; any targeted behavior coupling must have dedicated audit coverage.

## Stage 3. Blocks / Basic Resources

**Goal**

Port simple block identity and resource structure before stateful BlockEntities.

**Scope**

- Basic block registry.
- Block items.
- Simple non-ticking blocks.
- Ores, logs/planks, decorative blocks, plants where possible.
- Basic block/item model and blockstate generation.
- Tags for blocks/items.

**Legacy references**

- `thaumcraft.api.blocks.BlocksTC`
- `thaumcraft.common.config.ConfigBlocks`
- `thaumcraft.common.blocks.BlockTC`
- `thaumcraft.common.blocks.BlockTCDirectional`
- `thaumcraft.common.blocks.BlockTCTile`
- `thaumcraft.common.blocks.basic.*`
- `thaumcraft.common.blocks.world.*`
- `assets/thaumcraft/blockstates`
- `assets/thaumcraft/models`
- `assets/thaumcraft/textures`

**Dependency blockers**

- Stage 1 registry scaffold must exist.
- Stage 2 item/block item conventions must be decided.
- Datagen paths and resource naming must be stable.
- Old metadata block variants must be mapped to modern block states or separate blocks.
- Ticking machines, inventories, essentia containers, and crafting blocks must wait for Stage 8.
- World generation for ores/plants must wait for Stage 12.

**Exit criteria**

- Simple blocks and block items register.
- Basic generated models/blockstates load.
- No BlockEntity logic is required.

## Stage 4. Aspects

**Goal**

Rebuild the aspect domain model and data registration before aura, research, essentia, and alchemy depend on it.

**Scope**

- Aspect registry/model.
- Aspect composition graph.
- Aspect list/value object.
- Item/block/entity aspect assignment format.
- Data loading and validation.
- Public API surface for addon-provided aspect data.

**Legacy references**

- `thaumcraft.api.aspects.Aspect`
- `thaumcraft.api.aspects.AspectList`
- `thaumcraft.api.aspects.AspectHelper`
- `thaumcraft.api.aspects.AspectRegistryEvent`
- `thaumcraft.api.aspects.AspectEventProxy`
- `thaumcraft.api.aspects.IAspectContainer`
- `thaumcraft.api.aspects.IAspectSource`
- `thaumcraft.api.aspects.IEssentiaContainerItem`
- `thaumcraft.api.aspects.IEssentiaTransport`
- `thaumcraft.common.config.ConfigAspects`
- `thaumcraft.common.lib.events.EssentiaHandler`

**Dependency blockers**

- Stage 2 item ids must be stable.
- Stage 3 block ids must be stable.
- Tag strategy must be in place to replace `OreDictionary`.
- Decide whether aspects are a custom registry, reloadable data, or both.
- Decide how addon aspect registration works in NeoForge.
- Essentia transport interfaces cannot be completed until Stage 8 BlockEntities exist.

**Exit criteria**

- Core aspects load and validate.
- Items/blocks can be assigned aspects through data.
- Basic public API for querying aspect data exists.

## Stage 5. Aura

**Goal**

Implement server-owned aura storage and update rules before systems consume vis/flux.

**Scope**

- Aura data model: vis, flux, base, instability.
- Per-level/per-chunk or area storage.
- Server tick/update rules.
- Save/load behavior.
- Minimal debug/query API.
- No final HUD or particle dependency yet.

**Legacy references**

- `thaumcraft.api.aura.AuraHelper`
- `thaumcraft.common.world.aura.AuraHandler`
- `thaumcraft.common.world.aura.AuraWorld`
- `thaumcraft.common.world.aura.AuraChunk`
- `thaumcraft.common.world.aura.AuraThread`
- `thaumcraft.common.lib.events.ChunkEvents`
- `thaumcraft.common.lib.events.WorldEvents`
- `thaumcraft.common.lib.events.ServerEvents`
- `thaumcraft.common.lib.network.misc.PacketAuraToClient`

**Dependency blockers**

- Stage 4 must define vis/flux aspect semantics.
- Choose storage mechanism: `SavedData`, attachments, or another current NeoForge-compatible model.
- Chunk/level lifecycle hooks must be understood before save/load implementation.
- Threading model must be redesigned; legacy `AuraThread` should not be copied blindly.
- Client HUD/FX sync is blocked by Stage 10 and Stage 13.
- Worldgen aura seeding is blocked by Stage 12.

**Exit criteria**

- Aura state persists on server.
- Server-side aura query/mutation APIs work.
- No client visual sync is required yet.

## Stage 6. Research

**Goal**

Port research content loading and player progression state before recipe gating and research GUI completion.

**Scope**

- Research category/entry/stage model.
- Research JSON format or converted datapack format.
- Reload listener/datagen support.
- Player knowledge/flags/scan state.
- Server-side progression APIs.
- Minimal command/debug validation.

**Legacy references**

- `thaumcraft.api.research.ResearchCategories`
- `thaumcraft.api.research.ResearchCategory`
- `thaumcraft.api.research.ResearchEntry`
- `thaumcraft.api.research.ResearchStage`
- `thaumcraft.api.research.ResearchAddendum`
- `thaumcraft.api.research.IScanThing`
- `thaumcraft.api.research.ScanningManager`
- `thaumcraft.common.config.ConfigResearch`
- `thaumcraft.common.lib.research.ResearchManager`
- `thaumcraft.common.lib.capabilities.PlayerKnowledge`
- `assets/thaumcraft/research`

**Dependency blockers**

- Stage 2 and Stage 3 ids must exist for research references.
- Stage 4 aspects must exist for research categories and requirements.
- Decide player state storage: attachments/capabilities.
- Data loading must be reload-safe.
- Recipe references are blocked until Stage 7 serializers exist.
- Full GUI is blocked by Stage 9 and Stage 10.
- Scan predicates that used `OreDictionary` need tag/predicate replacements.

**Exit criteria**

- Research data loads and validates.
- Player research state can be granted, saved, loaded, and queried server-side.
- Missing recipe/UI references fail clearly or are stubbed.

## Stage 7. Crafting

**Goal**

Create modern recipe infrastructure for arcane crafting, crucible recipes, infusion recipes, and research display references.

**Scope**

- `RecipeType` and `RecipeSerializer` definitions.
- Arcane shaped/shapeless recipes.
- Crucible recipe serializer.
- Infusion recipe serializer.
- Recipe unlock/research requirements.
- Datagen for recipes.
- Initial recipe query helpers.

**Legacy references**

- `thaumcraft.api.crafting.IThaumcraftRecipe`
- `thaumcraft.api.crafting.IArcaneRecipe`
- `thaumcraft.api.crafting.ShapedArcaneRecipe`
- `thaumcraft.api.crafting.ShapelessArcaneRecipe`
- `thaumcraft.api.crafting.CrucibleRecipe`
- `thaumcraft.api.crafting.InfusionRecipe`
- `thaumcraft.api.crafting.IngredientNBTTC`
- `thaumcraft.common.config.ConfigRecipes`
- `thaumcraft.common.lib.crafting.ThaumcraftCraftingManager`
- `thaumcraft.common.lib.crafting.RecipeMagicDust`

**Dependency blockers**

- Stage 2 item registry must be stable.
- Stage 3 block registry must be stable.
- Stage 4 aspects must be available for crucible/infusion costs.
- Stage 6 research ids must be available for recipe gates.
- Modern ingredient/tag strategy must replace `OreDictionary`.
- Actual workbench/crucible/infusion machine execution is blocked by Stage 8.
- Research page recipe rendering is blocked by Stage 9.

**Exit criteria**

- Recipe JSON loads through Minecraft's recipe system.
- Recipe validation can catch missing item/block/aspect/research ids.
- Server can query matching recipes without legacy `GameData.register_impl`.

## Stage 8. Tile Entities

**Goal**

Port stateful blocks as modern BlockEntities after items, blocks, aspects, aura, research, and recipes are stable enough to depend on.

**Scope**

- BlockEntity base classes.
- BlockEntityType registration.
- Inventories and data storage.
- Tickers.
- Menus/data slots for machines.
- Arcane workbench, crucible, infusion matrix, essentia devices, and core utility devices.

**Legacy references**

- `thaumcraft.common.tiles.TileThaumcraft`
- `thaumcraft.common.tiles.TileThaumcraftInventory`
- `thaumcraft.common.tiles.crafting.TileArcaneWorkbench`
- `thaumcraft.common.tiles.crafting.TileCrucible`
- `thaumcraft.common.tiles.crafting.TileInfusionMatrix`
- `thaumcraft.common.tiles.crafting.TilePedestal`
- `thaumcraft.common.tiles.crafting.TileResearchTable`
- `thaumcraft.common.tiles.essentia.*`
- `thaumcraft.common.tiles.devices.*`
- `thaumcraft.common.tiles.misc.*`

**Dependency blockers**

- Stage 3 must provide blocks that own BlockEntities.
- Stage 4 must provide aspects/essentia concepts.
- Stage 5 must provide aura APIs for vis consumption/generation.
- Stage 6 must provide research access checks.
- Stage 7 must provide recipe lookup.
- Decide inventory API approach before machine implementation.
- Final client sync is blocked by Stage 10.
- Full GUI is blocked by Stage 9.

**Exit criteria**

- Selected core BlockEntities save/load and tick server-side.
- Machines can run with minimal debug interaction.
- No legacy `TileEntity`, `IInventory`, or update packet assumptions remain.

## Stage 9. GUI

**Goal**

Port menu and screen structure for core interactions. This stage can scaffold screens, but final behavior depends on Stage 10 networking.

**Scope**

- Menu types.
- Container/menu classes.
- Basic machine screens.
- Research browser/page skeleton.
- Focus manipulator skeleton.
- Golem/seal UI skeleton only if golem systems are not deferred.
- Shared widgets.

**Legacy references**

- `thaumcraft.proxies.ProxyGUI`
- `thaumcraft.common.container.*`
- `thaumcraft.client.gui.GuiArcaneWorkbench`
- `thaumcraft.client.gui.GuiFocalManipulator`
- `thaumcraft.client.gui.GuiResearchBrowser`
- `thaumcraft.client.gui.GuiResearchPage`
- `thaumcraft.client.gui.GuiResearchTable`
- `thaumcraft.client.gui.GuiGolemBuilder`
- `thaumcraft.client.gui.plugins.*`
- `thaumcraft.common.golems.client.gui.*`

**Dependency blockers**

- Stage 8 must define menus and BlockEntity data to display.
- Stage 6 must define research data model for research GUI.
- Stage 7 must define recipe display data.
- Stage 10 is required for final client-to-server actions and state sync.
- Stage 13 is required for polished custom rendering, particles, and shader-heavy UI effects.
- Old raw GL rendering must be replaced with current screen rendering APIs.

**Exit criteria**

- Screens open without crashing.
- Static and locally available data renders.
- Interactive actions that require server mutation are stubbed or routed through temporary safe hooks until Stage 10.

## Stage 10. Networking

**Goal**

Formalize all gameplay and visual synchronization with modern custom payloads.

**Scope**

- Payload registration.
- Server-to-client sync for knowledge, warp, aura, BlockEntities, FX events.
- Client-to-server requests for GUI actions, focus editing, research actions, machine selection.
- Validation and rate limiting where relevant.
- Removal of temporary sync stubs from earlier stages.

**Legacy references**

- `thaumcraft.common.lib.network.PacketHandler`
- `thaumcraft.common.lib.network.EventHandlerNetwork`
- `thaumcraft.common.lib.network.fx.*`
- `thaumcraft.common.lib.network.misc.*`
- `thaumcraft.common.lib.network.playerdata.*`
- `thaumcraft.common.lib.network.tiles.PacketTileToClient`
- `thaumcraft.common.lib.network.tiles.PacketTileToServer`

**Dependency blockers**

- Stage 5 aura state must exist before aura sync.
- Stage 6 player research/warp state must exist before player data sync.
- Stage 8 BlockEntities and menus must exist before machine sync.
- Stage 9 screens must define required user actions.
- Stage 13 may add visual-only FX payloads later.
- Every client-to-server packet must have a server-side authority check.

**Exit criteria**

- No legacy `SimpleNetworkWrapper`, `IMessage`, or `IMessageHandler` remains in port code.
- Core GUI actions work through validated payloads.
- Player and machine state sync works on dedicated server.

## Stage 11. Entities / Golems

**Goal**

Port entity gameplay after core systems are stable, then decide how much of golem automation belongs in the first playable milestone.

**Scope**

- EntityType registration.
- Mobs, projectiles, constructs, special item entities.
- Damage/effect integration.
- Basic AI/goals.
- Golem entity, properties, parts, seals, and tasks if included in this milestone.

**Legacy references**

- `thaumcraft.common.config.ConfigEntities`
- `thaumcraft.api.entities.IEldritchMob`
- `thaumcraft.api.entities.ITaintedMob`
- `thaumcraft.common.entities.*`
- `thaumcraft.common.golems.EntityThaumcraftGolem`
- `thaumcraft.common.golems.GolemProperties`
- `thaumcraft.common.golems.ai.*`
- `thaumcraft.common.golems.seals.*`
- `thaumcraft.common.golems.tasks.TaskHandler`
- `thaumcraft.api.golems.*`
- `thaumcraft.api.golems.parts.*`
- `thaumcraft.api.golems.seals.*`

**Dependency blockers**

- Stage 2 and Stage 3 must define drops, spawn eggs/items, and related blocks.
- Stage 4 aspects may be needed for scan data and mob classification.
- Stage 5 aura may affect taint/flux behavior.
- Stage 6 research may gate entities/items.
- Stage 10 networking is needed for golem/seal state and some projectiles/FX.
- Stage 13 rendering is required for finished entity visuals.
- Golem pathfinding and logistics should be deferred if core gameplay is not stable.

**Exit criteria**

- Basic entities spawn and behave server-side.
- Projectiles and damage work on dedicated server.
- Golem subsystem has an explicit milestone decision: defer, partial port, or full port.

## Stage 12. Worldgen

**Goal**

Port terrain/biome/resource generation after blocks, items, aspects, aura, and entities are stable.

**Scope**

- Ores/crystals.
- Magical trees.
- Flowers/plants.
- Mounds/structures if retained.
- Biome modifiers.
- Aura initialization during generation if needed.
- Datapack/datagen worldgen definitions.

**Legacy references**

- `thaumcraft.common.world.ThaumcraftWorldGenerator`
- `thaumcraft.common.world.biomes.BiomeHandler`
- `thaumcraft.common.world.biomes.BiomeGenEerie`
- `thaumcraft.common.world.biomes.BiomeGenEldritch`
- `thaumcraft.common.world.biomes.BiomeGenMagicalForest`
- `thaumcraft.common.world.objects.WorldGenBigMagicTree`
- `thaumcraft.common.world.objects.WorldGenGreatwoodTrees`
- `thaumcraft.common.world.objects.WorldGenSilverwoodTrees`
- `thaumcraft.common.world.objects.WorldGenCustomFlowers`
- `thaumcraft.common.world.objects.WorldGenMound`

**Dependency blockers**

- Stage 3 blocks must exist.
- Stage 2 item drops must exist.
- Stage 4 aspect data may be needed for crystals/nodes.
- Stage 5 aura storage must exist if generation seeds aura values.
- Stage 11 entities may be needed for biome spawn rules.
- Modern configured/placed feature and biome modifier strategy must be chosen.
- Old `IWorldGenerator` logic cannot be ported directly.

**Exit criteria**

- Worldgen data loads in a new world.
- Features generate through modern data-driven worldgen.
- Dedicated server generation does not require client classes.

## Stage 13. Rendering / Particles

**Goal**

Complete client visual systems after gameplay state and networking boundaries are stable.

**Scope**

- Entity renderers.
- BlockEntity renderers.
- Item render properties.
- Color handlers.
- Custom models and OBJ replacement strategy.
- Particles, beams, essentia streams, focus FX.
- Shaders or shader alternatives.
- HUD overlays.

**Legacy references**

- `thaumcraft.client.ColorHandler`
- `thaumcraft.proxies.ProxyBlock`
- `thaumcraft.proxies.ProxyEntities`
- `thaumcraft.proxies.ProxyTESR`
- `thaumcraft.client.renderers.*`
- `thaumcraft.client.fx.FXDispatcher`
- `thaumcraft.client.fx.ParticleEngine`
- `thaumcraft.client.fx.beams.*`
- `thaumcraft.client.fx.other.*`
- `thaumcraft.client.fx.particles.*`
- `thaumcraft.client.lib.*`
- `thaumcraft.client.lib.obj.*`
- `thaumcraft.codechicken.lib.*`
- `assets/thaumcraft/textures`
- `assets/thaumcraft/models`
- `assets/minecraft/shaders`

**Dependency blockers**

- Stage 2 and Stage 3 registries must define render targets.
- Stage 8 BlockEntities must exist for BERs.
- Stage 10 payloads must exist for server-triggered FX.
- Stage 11 entities must exist before entity renderers can be completed.
- Asset conversion strategy must be finished.
- Raw GL/`GlStateManager` code must be replaced with current render APIs.
- Bundled CodeChicken-style rendering helpers should be treated as reference unless explicitly reimplemented.

**Exit criteria**

- Client renders core blocks/items/entities without missing models.
- Important particles and HUD overlays work.
- Client-only classes are isolated from dedicated server.

## Stage 14. Integrations

**Goal**

Add optional mod integrations only after Thaumcraft's own systems are stable.

**Scope**

- Accessory equipment replacement for Baubles behavior.
- Optional compatibility APIs.
- Cross-mod item behavior hooks.
- Optional recipe/research/aspect extension points.
- Compatibility test matrix.

**Legacy references**

- `thaumcraft.Thaumcraft` dependency string for Baubles.
- `thaumcraft.client.lib.events.WandRenderingHandler`
- `thaumcraft.api.items.IVisDiscountGear`
- `thaumcraft.api.items.IGoggles`
- `thaumcraft.api.items.IRevealer`
- `thaumcraft.api.items.ItemsTC.baubles`
- `vazkii.botania.api.item.IPetalApothecary`

**Dependency blockers**

- Stage 2 item behavior interfaces must be stable.
- Stage 4 aspect API must be stable for addon data.
- Stage 6 research API must be stable for addon research.
- Stage 7 recipe API must be stable for addon recipes.
- Stage 10 networking must be stable if integration adds synced actions.
- Choose target accessory API before replacing Baubles behavior.
- Integrations must be optional and must not block dedicated server startup.

**Exit criteria**

- Thaumcraft can run without optional integration mods.
- Selected integrations load only when their target mods are present.
- Addon-facing extension points are documented separately from internal implementation.

## Recommended Milestone Boundaries

| Milestone | Includes | Hard blockers |
|---|---|---|
| M0 Boot | Stage 1 | NeoForge project, Java 21, mod metadata |
| M1 Static Content | Stages 2-3 | registries, datagen, tags |
| M2 Core Thaumcraft Data | Stages 4-7 | aspects, research, recipes, server data models |
| M3 Machines | Stages 8-10 | BlockEntities, menus, payload networking |
| M4 Gameplay Expansion | Stages 11-12 | entities/golems decision, worldgen data |
| M5 Presentation | Stage 13 | stable gameplay state and payloads |
| M6 Compatibility | Stage 14 | stable public API and optional integration target |

## Highest-Risk Dependency Blockers

- Aura storage must be designed before any aura-consuming machines or HUD features.
- Research player state must be designed before research-gated crafting or Thaumonomicon UI.
- Recipe serializers must exist before machine execution can be considered real.
- BlockEntity sync must be solved before GUI work is complete.
- Networking must be server-authoritative before focus editing, research actions, golem commands, or machine selection are exposed.
- Worldgen should not start until block ids, tags, and aura seeding rules are stable.
- Rendering and particles should not lead the port; they depend on stable registries and payload events.
