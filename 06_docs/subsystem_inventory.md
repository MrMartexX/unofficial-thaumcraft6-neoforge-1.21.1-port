# Thaumcraft 6 Legacy Subsystem Inventory

Source audited: `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master`

Original jar cross-check: `01_original_jar/Thaumcraft-1.12.2-6.1.BETA26.jar`

API reference cross-check: `04_api_reference/thaumcraft-api-master`

Migration guide constraint: inventory is organized by subsystem role, not by class names alone. Legacy Forge 1.12.2 APIs should be treated as reference behavior only; NeoForge 1.21.1 work must rebuild registration, data loading, networking, state storage, client code, and worldgen through current APIs.

## Overall Notes

- Mod id: `thaumcraft`.
- Main mod class: `thaumcraft.Thaumcraft`.
- Required legacy dependency: Baubles `1.5.2`.
- Access transformer exists: `src/main/resources/META-INF/tc_at.cfg`.
- No coremod/ASM/mixin entrypoint was found in the Java source audit.
- The TheDarkTower source matches the original jar by top-level class list except `thaumcraft/api/package-info.java`.
- Resource names mostly match the jar; meaningful metadata differences exist in `mcmod.info` and `pack.mcmeta`.

## 1. Core Mod Initialization

**Main classes**

- `thaumcraft.Thaumcraft`
- `thaumcraft.Registrar`
- `thaumcraft.proxies.IProxy`
- `thaumcraft.proxies.CommonProxy`
- `thaumcraft.proxies.ClientProxy`
- `thaumcraft.proxies.ServerProxy`
- `thaumcraft.proxies.ProxyBlock`
- `thaumcraft.proxies.ProxyEntities`
- `thaumcraft.proxies.ProxyGUI`
- `thaumcraft.proxies.ProxyTESR`
- `thaumcraft.common.lib.CreativeTabThaumcraft`
- `thaumcraft.common.lib.SoundsTC`

**What it does**

- Owns Forge mod lifecycle setup.
- Initializes config, registries, worldgen, networking, capabilities, research, recipes, sounds, creative tab, and client/server proxy behavior.
- Connects internal implementation to public API through `ThaumcraftApi.internalMethods`.

**Legacy Forge/Minecraft API used**

- `@Mod`, `@SidedProxy`, `@Mod.EventHandler`
- `FMLPreInitializationEvent`, `FMLInitializationEvent`, `FMLPostInitializationEvent`, `FMLServerStartingEvent`
- `MinecraftForge.EVENT_BUS`, `FMLCommonHandler`
- `RegistryEvent.Register<T>`, `IForgeRegistry`, `IForgeRegistryEntry`
- `GameRegistry.registerWorldGenerator`, `GameRegistry.registerTileEntity`
- `CapabilityManager`
- Legacy access transformer `META-INF/tc_at.cfg`

**Migration difficulty**

- High.

**What can be preserved**

- Mod id and high-level initialization order.
- Registry inventory and lifecycle intent.
- Separation between common/server/client concerns, but not the proxy implementation pattern.

**What must be rewritten**

- Replace `@Mod.EventHandler` lifecycle with NeoForge mod constructor and event bus registration.
- Replace proxies with explicit common/client init and `Dist`-safe client registration.
- Replace direct static registration and `GameRegistry` usage with `DeferredRegister` and modern event hooks.
- Re-check every access transformer entry; most should become API-based rewrites instead of new ATs.

## 2. Aspects

**Main classes**

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

**What it does**

- Defines primal/compound aspects and aspect lists.
- Associates aspects with items, blocks, entities, research, recipes, and essentia containers.
- Provides addon-facing registration hooks for object/entity aspect tags.

**Legacy Forge/Minecraft API used**

- `ResourceLocation`
- `ItemStack` NBT and metadata/damage values
- `OreDictionary`
- Forge event bus through `AspectRegistryEvent`
- Static maps in `CommonInternals`

**Migration difficulty**

- High.

**What can be preserved**

- Aspect names, colors, composition graph, and `AspectList` concept.
- Public concept of registering item/entity aspect data.
- Essentia transport/container roles.

**What must be rewritten**

- Replace `OreDictionary` and damage/meta matching with tags, ingredients, data components, and item predicates.
- Move aspect data to data-driven JSON/reloadable registries where possible.
- Replace global static mutable maps with explicit services and reload lifecycle.
- Rework tile/entity storage through BlockEntity state, attachments, or capabilities.

## 3. Aura

**Main classes**

- `thaumcraft.api.aura.AuraHelper`
- `thaumcraft.common.world.aura.AuraHandler`
- `thaumcraft.common.world.aura.AuraWorld`
- `thaumcraft.common.world.aura.AuraChunk`
- `thaumcraft.common.world.aura.AuraThread`
- `thaumcraft.common.lib.events.ChunkEvents`
- `thaumcraft.common.lib.events.WorldEvents`
- `thaumcraft.common.lib.events.ServerEvents`
- `thaumcraft.common.lib.network.misc.PacketAuraToClient`

**What it does**

- Stores and updates local vis, flux, aura base, and aura instability.
- Ties aura values to world/chunk positions.
- Sends aura data to clients for HUD and visual feedback.

**Legacy Forge/Minecraft API used**

- `World`, `WorldServer`, `Chunk`, `ChunkPos`, `BlockPos`
- Chunk load/save events
- Server tick/world tick events
- Manual background thread
- Legacy packet sync through `SimpleNetworkWrapper`

**Migration difficulty**

- Very high.

**What can be preserved**

- Gameplay model: local vis, flux, aura base, pollution, preservation checks.
- Approximate chunk/area granularity.
- Server-authoritative aura state.

**What must be rewritten**

- Replace `AuraThread` and static world maps with modern server-owned state.
- Use `SavedData` or attachment-based storage for per-level/per-chunk data.
- Rewrite sync as explicit NeoForge custom payloads.
- Re-evaluate threading; avoid unsafely touching world data off-thread.

## 4. Research

**Main classes**

- `thaumcraft.api.research.ResearchCategories`
- `thaumcraft.api.research.ResearchCategory`
- `thaumcraft.api.research.ResearchEntry`
- `thaumcraft.api.research.ResearchStage`
- `thaumcraft.api.research.ResearchAddendum`
- `thaumcraft.api.research.ResearchEvent`
- `thaumcraft.api.research.IScanThing`
- `thaumcraft.api.research.ScanningManager`
- `thaumcraft.api.research.ScanItem`
- `thaumcraft.api.research.ScanBlock`
- `thaumcraft.api.research.ScanEntity`
- `thaumcraft.api.research.ScanOreDictionary`
- `thaumcraft.common.config.ConfigResearch`
- `thaumcraft.common.lib.research.ResearchManager`
- `thaumcraft.common.lib.capabilities.PlayerKnowledge`
- `thaumcraft.client.gui.GuiResearchBrowser`
- `thaumcraft.client.gui.GuiResearchPage`

**What it does**

- Defines research categories, entries, stages, requirements, rewards, and scanning.
- Loads research JSON from assets.
- Tracks per-player research knowledge and flags.
- Drives Thaumonomicon UI pages and unlock progression.

**Legacy Forge/Minecraft API used**

- Asset JSON loaded manually from `assets/thaumcraft/research`
- `ResourceLocation`
- `IRecipe` display integration
- `EntityPlayer`
- Forge capabilities through `IPlayerKnowledge`
- Legacy GUI classes
- Legacy packet sync for research state

**Migration difficulty**

- Very high.

**What can be preserved**

- Research graph/content model.
- JSON-driven research content idea.
- Player knowledge/flag concepts.
- Scanning concept.

**What must be rewritten**

- Replace manual asset loading with reload listeners/datapack-aware data loading.
- Store player research in NeoForge attachments/capabilities with explicit sync.
- Rewrite UI for modern screen/render APIs.
- Replace ore dictionary scan predicates with tags/ingredients.

## 5. Crafting

**Main classes**

- `thaumcraft.api.crafting.IThaumcraftRecipe`
- `thaumcraft.api.crafting.IArcaneRecipe`
- `thaumcraft.api.crafting.ShapedArcaneRecipe`
- `thaumcraft.api.crafting.ShapelessArcaneRecipe`
- `thaumcraft.api.crafting.IArcaneWorkbench`
- `thaumcraft.api.crafting.IngredientNBTTC`
- `thaumcraft.api.crafting.IDustTrigger`
- `thaumcraft.common.config.ConfigRecipes`
- `thaumcraft.common.lib.crafting.ThaumcraftCraftingManager`
- `thaumcraft.common.lib.crafting.RecipeMagicDust`
- `thaumcraft.common.lib.crafting.ShapedArcaneVoidJar`
- `thaumcraft.common.tiles.crafting.TileArcaneWorkbench`
- `thaumcraft.common.container.ContainerArcaneWorkbench`

**What it does**

- Registers and resolves arcane crafting, special recipes, dust triggers, and recipe catalog data used by research pages.
- Adds crafting recipes to the old Forge recipe registry.

**Legacy Forge/Minecraft API used**

- `net.minecraft.item.crafting.IRecipe`
- `InventoryCrafting`
- `NonNullList<Ingredient>`
- `GameData.register_impl`
- `OreDictionary`
- `ResourceLocation`
- Legacy `Container`

**Migration difficulty**

- High.

**What can be preserved**

- Recipe categories and gameplay requirements.
- Arcane workbench concept.
- Research recipe display linkage.

**What must be rewritten**

- Implement modern `RecipeType`, `RecipeSerializer`, and recipe input/container abstractions.
- Move recipes to datapack JSON/datagen.
- Replace `OreDictionary` and metadata matching.
- Remove direct use of internal Forge `GameData`.

## 6. Infusion

**Main classes**

- `thaumcraft.api.crafting.InfusionRecipe`
- `thaumcraft.api.crafting.IInfusionStabiliser`
- `thaumcraft.api.crafting.IInfusionStabiliserExt`
- `thaumcraft.api.crafting.IStabilizable`
- `thaumcraft.common.tiles.crafting.TileInfusionMatrix`
- `thaumcraft.common.tiles.crafting.TilePedestal`
- `thaumcraft.common.tiles.devices.TileStabilizer`
- `thaumcraft.common.lib.crafting.InfusionEnchantmentRecipe`
- `thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe`
- `thaumcraft.common.lib.network.fx.PacketFXInfusionSource`

**What it does**

- Handles central infusion matrix crafting, pedestal inputs, instability, stabilizers, special infusion recipes, and infusion visual effects.

**Legacy Forge/Minecraft API used**

- `TileEntity`
- `IInventory`/`ItemStackHandler`-style legacy inventory assumptions
- `IRecipe`-adjacent recipe catalog
- Block state/meta checks
- Legacy packets and client particles

**Migration difficulty**

- Very high.

**What can be preserved**

- Infusion recipe model: central item, components, aspects, instability, research gate.
- Stabilizer gameplay concept.
- Pedestal/matrix block layout.

**What must be rewritten**

- Modern BlockEntity logic and menu sync.
- Recipe serializer and matching logic.
- Instability scanning around the matrix using modern block/entity APIs.
- Particle/sound/network synchronization.

## 7. Crucible / Alchemy

**Main classes**

- `thaumcraft.api.crafting.CrucibleRecipe`
- `thaumcraft.common.tiles.crafting.TileCrucible`
- `thaumcraft.common.tiles.crafting.TileThaumatorium`
- `thaumcraft.common.tiles.crafting.TileThaumatoriumTop`
- `thaumcraft.common.tiles.essentia.TileAlembic`
- `thaumcraft.common.tiles.essentia.TileSmelter`
- `thaumcraft.common.tiles.essentia.TileCentrifuge`
- `thaumcraft.common.lib.crafting.ThaumcraftCraftingManager`
- `thaumcraft.client.gui.GuiThaumatorium`
- `thaumcraft.client.gui.GuiSmelter`

**What it does**

- Performs crucible recipes, aspect dissolution, alchemical automation, essentia smelting, and thaumatorium recipe selection.

**Legacy Forge/Minecraft API used**

- `TileEntity` ticking
- `ItemStack` NBT/meta
- `Fluid`/block water assumptions through old world APIs
- Legacy container/menu classes
- Legacy packet sync
- `OreDictionary`

**Migration difficulty**

- Very high.

**What can be preserved**

- Crucible recipe semantics.
- Alchemy progression and automation roles.
- Essentia production/consumption flow.

**What must be rewritten**

- Modern recipe serializers for crucible/alchemy.
- BlockEntity ticking, inventory, and sync.
- Fluid/block interaction logic against current Minecraft APIs.
- Client GUI and progress synchronization.

## 8. Items

**Main classes**

- `thaumcraft.api.items.ItemsTC`
- `thaumcraft.api.items.IRechargable`
- `thaumcraft.api.items.IVisDiscountGear`
- `thaumcraft.api.items.IWarpingGear`
- `thaumcraft.api.items.IGoggles`
- `thaumcraft.api.items.IGogglesDisplayExtended`
- `thaumcraft.api.items.IRevealer`
- `thaumcraft.api.items.IArchitect`
- `thaumcraft.api.items.IScribeTools`
- `thaumcraft.api.items.RechargeHelper`
- `thaumcraft.common.config.ConfigItems`
- `thaumcraft.common.items.*`
- `thaumcraft.common.items.casters.*`

**What it does**

- Defines all Thaumcraft items, tools, armor, baubles, curios, caster items, foci, seals, golem items, materials, and item behavior hooks.

**Legacy Forge/Minecraft API used**

- `Item`, `ItemStack`, item damage/meta variants
- `IItemColor`, model/resource registration
- `OreDictionary`
- Baubles API
- `NBTTagCompound`
- Legacy right-click/use callbacks

**Migration difficulty**

- High.

**What can be preserved**

- Item catalog and gameplay roles.
- Public behavior marker concepts like goggles, vis discount gear, warping gear.
- Focus/caster item concept.

**What must be rewritten**

- Replace static `ItemsTC` assignment with `DeferredRegister`.
- Replace damage/meta variants with separate items, data components, or item properties.
- Replace Baubles integration with a modern optional accessory integration.
- Rework item NBT into data components where appropriate.

## 9. Blocks

**Main classes**

- `thaumcraft.api.blocks.BlocksTC`
- `thaumcraft.api.blocks.ILabelable`
- `thaumcraft.common.config.ConfigBlocks`
- `thaumcraft.common.blocks.BlockTC`
- `thaumcraft.common.blocks.BlockTCDirectional`
- `thaumcraft.common.blocks.BlockTCTile`
- `thaumcraft.common.blocks.basic.*`
- `thaumcraft.common.blocks.crafting.*`
- `thaumcraft.common.blocks.devices.*`
- `thaumcraft.common.blocks.essentia.*`
- `thaumcraft.common.blocks.world.*`

**What it does**

- Defines Thaumcraft blocks, block items, multiblock pieces, ores, crystals, devices, essentia transport blocks, plants, world blocks, and block state behavior.

**Legacy Forge/Minecraft API used**

- `Block`, `IBlockState`, `Property*`
- metadata-based variants
- `TileEntity` provider hooks
- `RegistryEvent.Register<Block>`
- `OreDictionary`
- legacy model/blockstate JSON format

**Migration difficulty**

- High.

**What can be preserved**

- Block catalog and block behavior concepts.
- Physical layouts and recipes.
- Labelable/interaction concepts.

**What must be rewritten**

- Replace `BlocksTC` static assignment with `DeferredRegister`.
- Modernize block state definitions and item registration.
- Replace metadata variant logic.
- Rebuild block entity creation and ticker hooks.

## 10. Tile Entities

**Main classes**

- `thaumcraft.common.tiles.TileThaumcraft`
- `thaumcraft.common.tiles.TileThaumcraftInventory`
- `thaumcraft.common.tiles.crafting.TileArcaneWorkbench`
- `thaumcraft.common.tiles.crafting.TileCrucible`
- `thaumcraft.common.tiles.crafting.TileFocalManipulator`
- `thaumcraft.common.tiles.crafting.TileGolemBuilder`
- `thaumcraft.common.tiles.crafting.TileInfusionMatrix`
- `thaumcraft.common.tiles.crafting.TileResearchTable`
- `thaumcraft.common.tiles.devices.*`
- `thaumcraft.common.tiles.essentia.*`
- `thaumcraft.common.tiles.misc.*`

**What it does**

- Owns persistent block state, inventories, crafting progress, essentia storage/transport, device ticking, and server/client block sync.

**Legacy Forge/Minecraft API used**

- `TileEntity`
- `ITickable`
- `IInventory`
- `NBTTagCompound`
- `SPacketUpdateTileEntity`
- `getUpdateTag`, `markDirty`, old chunk save lifecycle
- `GameRegistry.registerTileEntity`

**Migration difficulty**

- Very high.

**What can be preserved**

- BlockEntity responsibilities and data fields after audit.
- Essentia/device/crafting behavior.
- Base class idea for shared sync helpers.

**What must be rewritten**

- Rename/rebuild as modern `BlockEntity`.
- Use `BlockEntityType` registration.
- Replace `IInventory`/manual sync with modern item handlers, menus, data slots, and custom payloads.
- Audit every tick method for server/client separation.

## 11. GUI

**Main classes**

- `thaumcraft.proxies.ProxyGUI`
- `thaumcraft.common.container.*`
- `thaumcraft.client.gui.GuiArcaneWorkbench`
- `thaumcraft.client.gui.GuiFocalManipulator`
- `thaumcraft.client.gui.GuiResearchBrowser`
- `thaumcraft.client.gui.GuiResearchPage`
- `thaumcraft.client.gui.GuiResearchTable`
- `thaumcraft.client.gui.GuiGolemBuilder`
- `thaumcraft.client.gui.GuiThaumatorium`
- `thaumcraft.client.gui.plugins.*`
- `thaumcraft.common.golems.client.gui.*`

**What it does**

- Provides container-backed machine UIs, research UI, golem/seal configuration UI, focus manipulation UI, HUD toasts, and custom buttons/sliders.

**Legacy Forge/Minecraft API used**

- `GuiScreen`, `GuiContainer`
- `Container`
- `IGuiHandler`
- `FontRenderer`, `RenderItem`, raw GL state
- `GlStateManager`
- direct packet sends from GUI widgets

**Migration difficulty**

- Very high.

**What can be preserved**

- UX flow and screen layouts as reference.
- Container/menu semantics.
- Research page content model.

**What must be rewritten**

- Use modern `Screen`, `AbstractContainerMenu`, `MenuType`, and `RegisterMenuScreensEvent`.
- Replace raw GL calls with current pose/render APIs.
- Rebuild widget classes against modern UI APIs.
- Rework all client-to-server actions as validated payloads.

## 12. Networking

**Main classes**

- `thaumcraft.common.lib.network.PacketHandler`
- `thaumcraft.common.lib.network.EventHandlerNetwork`
- `thaumcraft.common.lib.network.FakeNetHandlerPlayServer`
- `thaumcraft.common.lib.network.fx.*`
- `thaumcraft.common.lib.network.misc.*`
- `thaumcraft.common.lib.network.playerdata.*`
- `thaumcraft.common.lib.network.tiles.PacketTileToClient`
- `thaumcraft.common.lib.network.tiles.PacketTileToServer`

**What it does**

- Synchronizes player knowledge/warp, tile data, aura, biome changes, GUI actions, focus changes, seal filters, logistics, and client FX.

**Legacy Forge/Minecraft API used**

- `SimpleNetworkWrapper`
- `IMessage`, `IMessageHandler`
- `ByteBuf`
- side-specific handlers
- direct `EntityPlayerMP` sends
- occasional fake net handler behavior

**Migration difficulty**

- Very high.

**What can be preserved**

- Packet inventory and semantic categories.
- Server-authoritative command boundaries.
- Client-only FX notifications as concept.

**What must be rewritten**

- Replace all `IMessage` packets with NeoForge custom payloads.
- Validate every client-to-server action server-side.
- Split gameplay sync from visual-only effects.
- Remove fake net handler patterns where possible.

## 13. Entities

**Main classes**

- `thaumcraft.common.config.ConfigEntities`
- `thaumcraft.api.entities.IEldritchMob`
- `thaumcraft.api.entities.ITaintedMob`
- `thaumcraft.common.entities.monster.*`
- `thaumcraft.common.entities.construct.*`
- `thaumcraft.common.entities.projectile.*`
- `thaumcraft.common.entities.EntityFluxRift`
- `thaumcraft.common.entities.EntitySpecialItem`
- `thaumcraft.common.lib.events.EntityEvents`
- `thaumcraft.client.renderers.entity.*`

**What it does**

- Defines Thaumcraft mobs, constructs, projectiles, special item entities, taint/eldritch markers, AI, spawning rules, and renderers.

**Legacy Forge/Minecraft API used**

- `Entity`, `EntityLiving`, `EntityMob`, `EntityThrowable`
- `DataParameter`/`EntityDataManager`
- `EntityRegistry`
- legacy AI task system
- legacy spawn registration
- old damage source classes

**Migration difficulty**

- High to very high.

**What can be preserved**

- Entity catalog, behavior goals, projectile concepts, marker concepts.
- Renderer design as visual reference.

**What must be rewritten**

- Register modern `EntityType`.
- Port AI tasks to modern goals/brain where appropriate.
- Replace damage sources with damage type system.
- Rewrite renderers against current client APIs.

## 14. Golems

**Main classes**

- `thaumcraft.api.golems.IGolemAPI`
- `thaumcraft.api.golems.IGolemProperties`
- `thaumcraft.api.golems.EnumGolemTrait`
- `thaumcraft.api.golems.parts.*`
- `thaumcraft.api.golems.seals.*`
- `thaumcraft.api.golems.tasks.Task`
- `thaumcraft.common.golems.EntityThaumcraftGolem`
- `thaumcraft.common.golems.GolemProperties`
- `thaumcraft.common.golems.ai.*`
- `thaumcraft.common.golems.seals.*`
- `thaumcraft.common.golems.tasks.TaskHandler`
- `thaumcraft.common.golems.client.*`

**What it does**

- Implements programmable golems, golem parts, traits, seals, tasks, logistics requests, AI/pathfinding, golem builder, and seal GUIs.

**Legacy Forge/Minecraft API used**

- legacy entity AI/path navigation
- `EntityCreature`/`EntityLiving`
- `IInventory`
- `BlockPos`, `EnumFacing`
- NBT-heavy golem property storage
- legacy GUI/network packets

**Migration difficulty**

- Very high.

**What can be preserved**

- User-facing golem/seal/task model.
- Part/trait system concept.
- Existing behavior as reference tests.

**What must be rewritten**

- Entity registration, navigation, and AI.
- Seal/task persistence and sync.
- Golem property storage.
- GUI/menu and client rendering.
- Decide whether `thaumcraft.api.golems.parts.*` is public API; it exists in jar/source but not in separate API reference.

## 15. Worldgen

**Main classes**

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

**What it does**

- Generates ores, crystals, plants, magical trees, mounds, and Thaumcraft biome-related features.

**Legacy Forge/Minecraft API used**

- `IWorldGenerator`
- `GameRegistry.registerWorldGenerator`
- `WorldGenerator`
- direct chunk generation hooks
- `Biome` registration/modification
- old dimension/world APIs

**Migration difficulty**

- Very high.

**What can be preserved**

- Feature catalog and generation rules as design reference.
- Tree/mound shapes and biome concepts.

**What must be rewritten**

- Use modern configured/placed features and biome modifiers.
- Move worldgen data to JSON/datagen.
- Rebuild custom biome integration against current biome APIs.
- Ensure generation is deterministic and data-pack compatible.

## 16. Rendering

**Main classes**

- `thaumcraft.client.ColorHandler`
- `thaumcraft.proxies.ProxyBlock`
- `thaumcraft.proxies.ProxyEntities`
- `thaumcraft.proxies.ProxyTESR`
- `thaumcraft.client.renderers.block.CrystalModel`
- `thaumcraft.client.renderers.entity.*`
- `thaumcraft.client.renderers.tile.*`
- `thaumcraft.client.renderers.models.*`
- `thaumcraft.client.lib.CustomRenderItem`
- `thaumcraft.client.lib.RenderCubes`
- `thaumcraft.client.lib.UtilsFX`
- `thaumcraft.client.lib.obj.*`
- `thaumcraft.codechicken.lib.*`

**What it does**

- Handles block/entity/tile renderers, custom models, OBJ loading, item rendering, shader hooks, color handlers, and visual overlays.

**Legacy Forge/Minecraft API used**

- `TileEntitySpecialRenderer`
- `Render<T>`
- `ModelLoader`
- `IRenderFactory`
- raw OpenGL/`GlStateManager`
- old baked model hooks
- bundled CodeChicken-style math/render helpers

**Migration difficulty**

- Very high.

**What can be preserved**

- Visual targets, model assets, renderer behavior as reference.
- Some math/model helper concepts if still useful.

**What must be rewritten**

- Use modern renderer registration events.
- Replace TESR with modern block entity renderers.
- Replace raw GL calls with pose stack/render type APIs.
- Re-evaluate bundled CodeChicken helpers; likely reference only.

## 17. Particles

**Main classes**

- `thaumcraft.client.fx.FXDispatcher`
- `thaumcraft.client.fx.ParticleEngine`
- `thaumcraft.client.fx.beams.*`
- `thaumcraft.client.fx.other.*`
- `thaumcraft.client.fx.particles.*`
- `thaumcraft.common.lib.network.fx.*`

**What it does**

- Provides custom particles, beams, essentia streams, focus impact effects, shield runes, scan effects, pollution effects, and network-triggered visual events.

**Legacy Forge/Minecraft API used**

- `Particle`
- `ParticleManager`
- manual texture binding
- Forge client render/tick events
- raw GL state
- legacy network packets for FX

**Migration difficulty**

- High.

**What can be preserved**

- Visual identity and effect timing as reference.
- Server-to-client FX event categories.

**What must be rewritten**

- Use modern particle types/providers where possible.
- Separate deterministic client particles from server-authoritative gameplay.
- Rewrite beam/stream renderers for modern rendering APIs.
- Replace FX packets with custom payloads or level events as appropriate.

## 18. Sounds

**Main classes**

- `thaumcraft.common.lib.SoundsTC`
- `assets/thaumcraft/sounds.json`
- `assets/thaumcraft/sounds/*`
- usages across blocks, items, entities, GUI, particles, and world events.

**What it does**

- Declares and plays Thaumcraft sound events for casting, machines, mobs, UI, aura/flux effects, and ambience.

**Legacy Forge/Minecraft API used**

- `SoundEvent`
- `ResourceLocation`
- old registry events
- access transformer entry for `SoundEvent.registerSound`
- `World.playSound`

**Migration difficulty**

- Medium.

**What can be preserved**

- Sound asset files and event names where compatible.
- Playback intent and sound categories.

**What must be rewritten**

- Register sound events with `DeferredRegister`.
- Remove reliance on access-transformed sound internals.
- Validate `sounds.json` format for current Minecraft.

## 19. Configs

**Main classes**

- `thaumcraft.common.config.ModConfig`
- `thaumcraft.common.config.ConfigAspects`
- `thaumcraft.common.config.ConfigBlocks`
- `thaumcraft.common.config.ConfigItems`
- `thaumcraft.common.config.ConfigEntities`
- `thaumcraft.common.config.ConfigRecipes`
- `thaumcraft.common.config.ConfigResearch`

**What it does**

- Centralizes legacy config options and performs large chunks of registration/bootstrap work.
- Controls feature toggles, research, aspects, recipes, block/item/entity setup, and some gameplay values.

**Legacy Forge/Minecraft API used**

- Forge 1.12 `Configuration`
- static config fields
- registration side effects during lifecycle events
- `RegistryEvent` and old registry helpers

**Migration difficulty**

- High.

**What can be preserved**

- Config option semantics.
- Grouping of setup responsibilities as an audit map.

**What must be rewritten**

- Move actual registration out of config classes.
- Use NeoForge config specs for runtime/configurable values.
- Use datagen/datapacks for content lists and recipes.
- Avoid static bootstrap side effects.

## 20. Integrations

**Main classes**

- `thaumcraft.Thaumcraft`
- `thaumcraft.api.items.IVisDiscountGear`
- `thaumcraft.api.items.ItemsTC`
- `thaumcraft.client.lib.events.WandRenderingHandler`
- `vazkii.botania.api.item.IPetalApothecary`
- `thaumcraft.codechicken.lib.*`
- `com.sasmaster.glelwjgl.java.*`
- `net.tofweb.starlite.*`

**What it does**

- Requires Baubles at runtime.
- Exposes item behavior expected to work with armor/baubles.
- Bundles small helper/API/library packages used by rendering or optional compatibility.
- Contains a Botania API interface copy for compatibility checks/reference.

**Legacy Forge/Minecraft API used**

- `@Mod` dependency string: `required-after:baubles@[1.5.2,)`
- Baubles API calls such as `BaublesApi.getBaubles`
- bundled third-party helper classes
- optional/compile-time API stubs

**Migration difficulty**

- Medium to high.

**What can be preserved**

- Integration intent: accessory equipment can provide vis discount, goggles, focus pouch behavior.
- Bundled helper code as historical reference.

**What must be rewritten**

- Replace Baubles with a modern accessory/curios-style integration only after selecting the target mod/API.
- Avoid bundling stale third-party render libraries unless strictly necessary.
- Make integrations optional and side-safe.

## 21. Assets / Resources

**Main files/directories**

- `src/main/resources/assets/thaumcraft/blockstates`
- `src/main/resources/assets/thaumcraft/models`
- `src/main/resources/assets/thaumcraft/textures`
- `src/main/resources/assets/thaumcraft/research`
- `src/main/resources/assets/thaumcraft/lang`
- `src/main/resources/assets/thaumcraft/loot_tables`
- `src/main/resources/assets/thaumcraft/sounds`
- `src/main/resources/assets/thaumcraft/sounds.json`
- `src/main/resources/assets/minecraft/shaders`
- `src/main/resources/META-INF/tc_at.cfg`
- `src/main/resources/mcmod.info`
- `src/main/resources/pack.mcmeta`

**What it does**

- Provides models, textures, blockstates, language, research JSON, loot tables, shaders, sound definitions, metadata, and access transformer configuration.

**Legacy Forge/Minecraft API used**

- Legacy blockstate/model JSON conventions
- `mcmod.info`
- legacy `pack.mcmeta`
- old language format
- old loot table format
- shader resource structure
- access transformer file

**Migration difficulty**

- High.

**What can be preserved**

- Textures, sounds, many model assets, research content text, and visual references.
- Some JSON content after schema conversion.

**What must be rewritten**

- Replace `mcmod.info` with `META-INF/neoforge.mods.toml`.
- Update `pack.mcmeta`.
- Convert models/blockstates/loot tables/tags/recipes to current schemas.
- Move generated content to datagen where practical.
- Rework or remove access transformer dependencies.
