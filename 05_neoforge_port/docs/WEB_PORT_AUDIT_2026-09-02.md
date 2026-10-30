# Thaumic Research Web Port Audit — 2026-09-02

Branch: `neutron-21.1.248-alpha4-parity-web`
Base branch: `neutron-21.1.248-alpha4-parity`
Base commit: `1633f0a4d655468b909327a924ca8ffcd96b5d7a`
Target: Minecraft `1.21.1`, NeoForge `21.1.248`, Java `21`

This file is the working parity audit for continuing the main Thaumcraft 6 core port. Addons are intentionally out of scope until the core mechanics are no longer hidden behind decorative or vanilla-placeholder implementations.

## Non-negotiable parity rules

1. Thaumcraft mechanics must not be replaced by ordinary vanilla recipes when the original TC6 used Thaumonomicon progression, Arcane Workbench, infusion, crucible/alchemy, infernal furnace, golem press or other special machinery.
2. Any vanilla fallback recipe for a Thaumcraft output is temporary and must be explicitly marked as such or removed before a release build.
3. Legacy TC6 textures/models/sounds are the source of truth. Adapt loaders, render layers and baked models for 1.21.1 instead of silently replacing missing content with placeholders.
4. Client-only GUI/HUD/render code must remain isolated from dedicated-server code.
5. Creative inventory parity is validated against `07_Test_Instance_and_Comparisons/02_Thaumcraft 1.12.2 Inventory Screenshots`; the 1.21.1 port may contain extra diagnostic/spawn-egg entries, but must not contain fewer core TC6 entries.
6. Research pages and EMI/JEI integration must describe the actual mechanic used by the port, not a simplified placeholder mechanic.

## P0 blockers reported from current alpha testing

### Recipe and research migration

- Replace ordinary crafting/smelting placeholders for TC6 mechanics with the correct special recipe type.
- Audit every Thaumonomicon recipe link and ensure it resolves to a live recipe.
- Arcane Workbench recipes must require vis/crystals/research gates where the original required them.
- Infusion recipes must use central item, pedestal components, instability and essentia cost.
- Alchemy/crucible recipes must consume essentia and catalyst items correctly.
- Infernal furnace must process ore clusters and other TC6-specific smelting outputs, not only vanilla sand/glass.
- EMI/JEI pages must display Thaumonomicon-like layouts: central item, pedestal ring, aspect icons and counts, not jars/flasks pretending to be aspects.

### Salis Mundus and multiblock activation

- Salis Mundus activation must have the original delay, particles, sound and magical feedback instead of instant silent conversion.
- Golem Press multiblock activation is broken: only the piston is consumed/transformed; all structure blocks must be validated and converted/consumed correctly.
- The activated Golem Press / golem table must open a working GUI.
- Infusion matrix stabilizers/columns must face the matrix, not form an incorrect spiral orientation.
- Automation helper blocks around multiblocks must be audited: bellows, tubes, mirrors, buffers, pedestals and related machinery.

### Items, armor, tools and curios

- Traveller's Boots still need charge/vis behavior and all original movement abilities.
- All armor pieces must become real equippable armor with their original defense, modifiers, textures and special effects.
- Magical jewelry must move from the original Baubles concept to Curios-compatible slots while preserving TC6 behavior.
- Runic shielding, vis discount, warp interaction and charm behavior must search equipped curios/armor/items.
- Caster Gauntlet focus/lens attach, removal and enchantment must be restored.
- Focal Manipulator GUI texture is stretched and must be ported with correct UV/layout sizing.
- Cultist armor, weapons and tools must be registered, textured, equippable/usable and gated through the correct research/loot paths.

### HUD, scanning and player overlays

- Thaumometer and Goggles of Revealing must show aspect information for blocks, entities and inventory items.
- Thaumometer/Goggles HUD must display aura/Vis/Flux in the original screen position and style, not only a debug/basic readout.
- Inventory scanning overlay text must be smaller and anchored above the thaumometer item instead of the screen corner.
- Sanity/warp checker must show permanent, normal and temporary warp in the original-style display.
- Player screen overlays/effects from warp, flux, eldritch and related mechanics must be restored.

### Entities, taint, rifts and portals

- Taint rifts, portals and flux/taint effects have missing or broken textures/rendering.
- Many Thaumcraft mob textures are mapped incorrectly.
- Every original TC6 mob must have a spawn egg or another discoverable creative/debug entry.
- Eldritch/cult portals that spawn clerics/warriors are missing or visually broken.
- Entity renderers must be audited against legacy model/texture paths before adding new placeholder art.

### Missing blocks and creative parity

- Hungry Chest and related chest/box/container blocks are missing or not registered correctly.
- Banners/flags and decorative TC6 blocks are missing or not shown in the correct creative order.
- Essentia tubes, buffers, filters and automation blocks need functional transfer behavior, not just blocks/items.
- Relics, eldritch/lunar research items and late-game items are incomplete.
- Nuggets/ingots/shards/quartz fragments and their reverse/compression recipes must be complete.
- Golem modules, seal tools/debugger/turrets and golem-related components must be restored.

## Cycle plan

### Web cycle 1 — audit + blockers into code

1. Add automated legacy-vs-port resource/recipe audit helper.
2. Run the helper locally or in CI to produce missing assets, recipes and suspicious placeholder recipe lists.
3. Convert the highest-impact recipe placeholders first: arcane workbench, infusion, crucible and infernal furnace clusters.
4. Restore missing creative registrations for obvious core blocks/items that already have legacy resources.
5. Fix rendering path registration for rifts/portals/mobs where the resource exists but is not mapped.
6. Fix golem press multiblock validation/conversion and GUI opening.
7. Commit in small groups; do not ship a JAR until the cycle reaches the agreed checkpoint.

### Web cycle 2 — mechanics parity

1. Restore armor/tool/jewelry equip capabilities and Curios slot mapping.
2. Restore Traveller's Boots charge and movement behavior.
3. Restore gauntlet focus/lens attach, enchant and Focal Manipulator GUI layout.
4. Restore Thaumometer/Goggles aspect scan HUD and aura display.
5. Restore Salis Mundus activation delay, particles and sounds.
6. Restore taint/warp/rift/portal visuals and entity texture mapping.
7. Commit in small groups; only create a JAR at the final cycle checkpoint.

## Release-gate before the next JAR

- Dedicated server starts without client-only class loading.
- Client starts with EMI/JEI installed.
- Creative inventory contains at least all core TC6 entries visible in the reference screenshots.
- Audit helper shows no silent vanilla placeholder recipe for special Thaumcraft mechanics.
- Golem Press activates and opens GUI.
- Traveller's Boots equip and apply movement behavior.
- At least one infusion, one arcane, one crucible and one infernal furnace recipe are validated in-game.
- Rifts/portals/mobs render with non-missing textures.

## Current status of this branch

This branch starts from `0.2.1-alpha6` parity work. It is not a final full port. The purpose of this branch is to move from scattered fixes to systematic TC6 core parity work with frequent small commits and a JAR only at a controlled checkpoint.
