# FX, particle and scan parity status

This document records the current compatibility status for the Thaumcraft 6 to Minecraft 1.21.1 NeoForge port.

## Completed compatibility work

### Knowledge gain HUD flare

The knowledge gain HUD flare now uses a legacy-compatible shader path instead of the vanilla GUI shader path. The checked parity factors are:

- `thaumcraft:textures/misc/particles.png` atlas;
- `1024x1024` atlas size;
- frame range `320..335`;
- `64x64` grid;
- additive blend for the flare;
- alpha cutoff equivalent to legacy `1/255` (`0.003921569`);
- no direct RGB multiplication by UV2/lightmap coordinates.

### Shared generic FX foundation

The shared generic FX base has been stabilized for future TC6 particles. The relevant classes are:

- `TCLegacyFXGeneric`
- `TCLegacyFXGenericGui`
- `TCLegacyParticleEngine`
- `TCLegacyFXData`
- `TCFXDispatcher`
- `TCLegacyShaders`

The role-aware validator reported no failing checks after the compatibility pass.

Covered compatibility points:

- age and lifetime handling;
- motion, gravity, slowdown, random movement and wind;
- scale and alpha key sampling;
- start/end color interpolation;
- frame/grid UV selection;
- manual quad rendering for world and GUI generic FX;
- low-alpha shader for generic particles;
- world layers `0/2` use additive blending;
- world layers `1/3` use normal alpha blending;
- GUI layer `4` uses additive blending;
- GUI layer `5` uses normal alpha blending;
- world layers `2/3` use the no-depth behavior;
- non-fullbright generic FX now have an approximate world-light contribution.

### Scan highlight and aspect overlay

The scan highlight and aspect overlay source-level audit found the required modern-side signals:

- scan highlight uses block `VoxelShape` / `AABB` instead of a hardcoded full cube;
- scan highlight distributes particles around block faces;
- scan highlight uses the shared FX sparkle path;
- entity highlight uses the target bounding box;
- aspect overlay requires a ready thaumometer path;
- aspect overlay checks entity aspects before rendering;
- aspect overlay renders aspect icons and amounts above entities;
- block and entity scan logic use stable registry-based identities;
- scan mutation is server-side through thaumometer use;
- client-side potential-scan lookup exists for highlight behavior.

## Remaining manual checks

Run in-game smoke tests for:

1. knowledge gain HUD flare;
2. cinderpearl smoke/flame;
3. shimmerleaf and vishroom motes;
4. scan highlight on a full cube block;
5. scan highlight on a non-full-cube block;
6. aspect overlay on scanned and unscanned mobs;
7. block runes and generic sparkle effects.

## Known limitations

These checks are source-level and validator-based. They do not prove exact framebuffer-level brightness, gamma, or pixel-perfect icon placement. Non-generic special effects still need their own parity checks.

## Recommended next audits

Suggested next subsystem audits:

1. server authority, networking and payload validation;
2. block entity persistence and sync;
3. research rewards and recipe unlocks;
4. `required_craft` and legacy scan-key compatibility;
5. aura, vis and flux saved data;
6. warp storage and effects;
7. essentia containers and transport;
8. infusion multiblock and recipe execution;
9. item data components for caster/focus-like items;
10. dedicated-server client-only class boundary audit.
