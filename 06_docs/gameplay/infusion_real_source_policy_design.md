# Infusion Real Source Policy Design

Last updated: 2026-06-20

## Legacy contract

Legacy `TileInfusionMatrix.craftCycle()` calls `EssentiaHandler.drainEssentia(matrix, aspect, null, 12, extension)`.

The relevant contract is:

- scan `x=-12..12`, `z=-12..12`, `y=-12..11`;
- discover `IAspectSource`, not arbitrary tube buffers;
- sort by squared distance;
- skip blocked sources;
- drain exactly one point from the first usable source;
- cache discovered positions;
- after an exhausted/invalid pass, invalidate the cache and wait 10 seconds before discovery;
- emit `PacketFXEssentiaSource` only after a successful drain.

## Modern implementation

- `TCAspectSourceContainer` represents stable server-side source storage.
- `TCWardedJarBlockEntity` stores one aspect, capacity `250`, blocked/filter state, comparator fullness and top-face transport access.
- `TCInfusionAspectSourceResolver` preserves the exact legacy volume and nearest-first ordering.
- The matrix stores only a transient ordered position cache. BlockEntities are resolved again before every simulated/committed drain.
- A one-point drain is simulated before mutation. Failed commit or invalid cache fails closed.
- The failed-pass delay is `200` game ticks. It is transient, matching the legacy global cache rather than recipe save data.
- Successful drains send the source/matrix/color/extension payload to nearby clients; client handling is display-only.
- Tube buffers and `TCTransportInfusionAspectSource` are not resolver candidates.

## Supported and deferred sources

Supported now:

- `thaumcraft:jar_normal` / `TCWardedJarBlockEntity`.

Deferred until separately audited:

- essentia mirrors;
- alembics and storage machines;
- Brain in a Jar;
- Thaumatorium automation;
- addon-defined source containers.

Aura, vis and flux are not infusion essentia sources.

## Safety rules

- Client payloads never authorize a drain.
- Discovery never scans every game tick; it uses the cached list and legacy failed-pass delay.
- Unknown source BlockEntities fail closed.
- Transient transport buffers cannot satisfy a recipe merely because they expose transport methods.
- Player-facing matrix activation remains disabled until the six dependency-blocked instability rolls and activation safety checks are complete; persistent stability and structure modifiers are runtime-audited.

## Validation

`tools/audits/audit-infusion-behavior.ps1` covers nearest ordering, blocked jars, range exclusion, insufficient-source no-op behavior, one-point drain, cache invalidation, delayed recovery, persistent stability and instability event boundaries. Current total: `83/83` passing checks.
