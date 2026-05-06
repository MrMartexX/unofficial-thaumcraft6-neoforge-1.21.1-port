# Legacy FX engine notes

This document describes the current compatibility approach for Thaumcraft 6 legacy particles in the NeoForge 1.21.1 port.

## Why this exists

Thaumcraft 6 on Minecraft 1.12.2 used a custom particle system:

- `FXDispatcher`
- `FXGeneric`
- `ParticleEngine`
- raw atlas texture `thaumcraft:textures/misc/particles.png`
- manual UV frame selection
- custom layer blend states

This does not map cleanly to modern vanilla `ParticleType` rendering.

The port therefore uses a small legacy compatibility layer.

## Current architecture

Common side:

- `TCFXDispatcher`
- `TCLegacyFXData`

Client side:

- `TCClientEvents`
- `TCLegacyParticleEngine`
- `TCLegacyFXGeneric`

Flow:

```text
game/block code
TCFXDispatcher
client sink
TCLegacyParticleEngine
TCLegacyFXGeneric
manual raw atlas rendering
```

## Source texture rule

Always use the original asset:

```text
assets/thaumcraft/textures/misc/particles.png
```

Do not modify it.

Do not generate `particles_alpha.png`.

Do not pre-slice the atlas for the main legacy renderer.

Earlier generated atlas experiments changed transparency and made particles look square or incorrect.

## Correct UV mapping

Use `gridSize` for both X and Y frame index calculation.

```java
int particleTextureIndexX = Math.floorMod(frame, gridSize);
int particleTextureIndexY = Math.floorDiv(frame, gridSize);

float u0 = particleTextureIndexX / (float) gridSize;
float u1 = (particleTextureIndexX + 1.0F) / (float) gridSize;
float v0 = particleTextureIndexY / (float) gridSize;
float v1 = (particleTextureIndexY + 1.0F) / (float) gridSize;
```

Do not use hardcoded 16-column mapping.

For wispy mote frames `512..527` with `gridSize = 64`, hardcoded 16-column mapping selects empty frames.

## Implemented effect: drawWispyMotes

Current data:

```text
maxAge = age + age / 2 * random
gridSize = 64
startParticle = 512
numParticles = 16
particleInc = 1
loop = true
alpha = 0.0, 0.6, 0.6, 0.0
scale = 1.0, 0.5
slowDown = 0.9800000190734863
gravity = grav
randomMovementScale = 0.0025, 0.0, 0.0025
wind = 0.001, 0.0
rotationSpeed = 0.0
layer = 0
```

## Plant wrappers

### Shimmerleaf

```text
chance = 1 in 3
position = center with gaussian X/Y/Z offset
motion = gaussian * 0.01 on X/Y/Z
age = 10
color = cyan range
gravity = 0.0
```

Exact values:

```text
x = blockX + 0.5 + gaussian * 0.1
y = blockY + 0.4 + gaussian * 0.1
z = blockZ + 0.5 + gaussian * 0.1
red = 0.3 + random * 0.3
green = 0.7 + random * 0.3
blue = 0.7 + random * 0.3
```

### Vishroom

```text
chance = 1 in 3
position = center with random X/Z spread
motion = 0.0, 0.0, 0.0
age = 10
color = 0.5, 0.3, 0.8
gravity = 0.001
```

Exact values:

```text
x = blockX + 0.5 + (randomFloat - randomFloat) * 0.4
y = blockY + 0.3
z = blockZ + 0.5 + (randomFloat - randomFloat) * 0.4
```

## Blend state

Legacy world particle layers should behave like this:

```text
layer 0: SRC_ALPHA, ONE
layer 1: SRC_ALPHA, ONE_MINUS_SRC_ALPHA
layer 2: SRC_ALPHA, ONE, depth disabled
layer 3: SRC_ALPHA, ONE_MINUS_SRC_ALPHA, depth disabled
```

Wispy motes use layer 0, so additive blending is expected.

If the particle looks too flat and not like a soft glint, check layer 0 blending first.

## Pending hardening

Not implemented yet:

- particle setting culling
- FPS-based culling
- particle limit handling
- delayed effects
- full screen layer rendering
- full layer 4 and 5 support

Legacy limits to add later:

```text
minimal particles = 500
decreased particles = 1000
all particles = 2000
```

## Future FXGeneric features

Add these only when a real legacy effect requires them:

```text
finalFrames
flipped
angled
texture override
rotation variants
screen-space rendering
non-fullbright lighting
collision or noClip behavior
```

## Future particle port workflow

1. Find the legacy `FXDispatcher` method.
2. Extract exact `FXGeneric` configuration.
3. Check frame range and `gridSize`.
4. Verify frame preview if the range was not used before.
5. Create a `TCLegacyFXData` factory only if the effect is reused.
6. Call `TCFXDispatcher.drawLegacyFX(...)`.
7. Test client render.
8. Test dedicated server safety.

## Do not repeat these mistakes

Do not replace the original atlas.

Do not use generated alpha atlases for core legacy FX.

Do not use fixed 16-column UV mapping.

Do not convert wispy motes to vanilla dust particles.

Do not mix tree generation work with particle renderer changes.
