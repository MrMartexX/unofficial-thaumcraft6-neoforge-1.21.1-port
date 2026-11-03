# Seal, Golem And Press Remediation

This records a checked implementation slice, not a certificate of complete TC6
golemancy or whole-port parity. The migration guide's sections 10-12 govern
inventory mutation, registry-aware serialization, server authority and payloads.

## Legacy Sources

- `common/golems/client/gui/SealBaseContainer.java`: button IDs, ghost clicks,
  count adjustments, inventory coordinates and allowed ranges.
- `common/golems/client/gui/SealBaseGUI.java` and its button classes: 176x232
  layout, 160x160 seal background, radial categories, texture cells and captions.
- `common/lib/network/misc/PacketSealToClient.java`: seal/filter/options snapshot.
- `common/golems/seals/SealEntity.java`: neutral color, face-dependent initial
  area, redstone on support OR attached face, suspension on stopped transition.
- `common/golems/ai/AIGoto.java`, `AIGotoHome.java`, `AIFollowOwner.java`:
  task pauses, five-attempt stuck recovery, home thresholds and follow teleport.
- `common/golems/client/RenderThaumcraftGolem.java`, `PartModelWheel`,
  `PartModelClaws`, `PartModelBreakers`, `PartModelDarts`, `PartModelHauler`:
  OBJ groups, texture selection, attachment offsets, rotations and carrying.

All paths above are relative to the primary 1.12.2 decompiled source tree.

## Implemented

- Per-seal clientbound updates/removal and chunk-watch snapshots/reset. Client
  caches clear on level unload; server data stays in per-world SavedData.
- Real server-opened seal menu and legacy-textured screen, replacing chat output.
  Buttons use vanilla container actions. Clients never submit replacement NBT.
- Owner, distance, current seal identity, spectator, loaded-chunk, button/category
  and value-bound validation. Ghost slots never give/take inventory items.
- Priority -5..5, color 0..16, 1..8 area dimensions, lock/redstone state,
  whitelist/blacklist, supported seal toggles, filter copies and stock quotas.
  Count quotas are bounded to 32767, the nonnegative signed-short menu range.
- Dynamic-registry-aware filter serialization. Potion/enchantment components no
  longer disappear behind swallowed serialization exceptions.
- Neutral dye zero; real dyes map to 1..16. Legacy initial areas depend on face.
  Redstone suspends jobs; invalid supports are checked at the legacy cadence.
- Home/follow state, restrictions and persistence; GOLEMDIRECT-gated bell toggle;
  queued jobs release on follow/remove/death. Legacy home/follow timing, owner
  distance thresholds, water path cost and loaded-area teleport fallback.
- Stuck-task rambling and legacy completion/retry-counter semantics. No blanket
  idle wandering goal competes with returning home.
- Actual articulated OBJ renderer instead of scaled PLAYER geometry: all 17
  original models, six material skins, flag dye, head/arm/leg/body origins,
  claws, grinders, wheel rotation and displayed carrying. Meshes reload safely.
  Displayed items are copies, not extra equipment/inventory contents.
- Golem Press cumulative extraction plan, actual extraction escrow and rollback
  on refusal/short extraction. A failed commit does not start crafting.
- Advanced Pickup/Fill/Empty matching options, existing-only Fill, cyclic Empty
  with leave-one, single/leave Provider, matching-aware Stock quotas and Use
  left/right click, air-target, empty-hand, sneak and whitelist provisioning.
- Metadata identities come from 38 explicit item families exported through
  Mojang's real ITEM_STACK DataFixer, from data version 1343 to 3955. They are
  not inferred from modern item names. The source exporter remains reproducible.
- The OreDictionary bridge uses explicit vanilla groups from the
  [original Forge registrations](https://raw.githubusercontent.com/MinecraftForge/MinecraftForge/1.12.x/src/main/java/net/minecraftforge/oredict/OreDictionary.java),
  separate from aspect assignment tags. New modern role equivalents use tags;
  arbitrary historical addon OreDictionary registrations are not reconstructed.
- Use metadata-wildcard requests retain the requested identity for deduplication
  while Provider resolves a concrete matching variant before moving anything.
  NBT remains significant. Old oak/birch variants match; new cherry does not
  silently become an old metadata variant.
- Required/forbidden traits now match the examined legacy logistics seals.
- Breaker no longer has an empty tick or instant-destroy stub: actual area scan,
  position cache, hardness work counter, 21/7 work speeds, crack animation cleanup,
  normal/Silk Touch loot and cancellation are tested. See
  [the scoped design and safety adaptation](../design/golem_breaker_remediation.md).
- Stock quota labels now use vanilla item decorations at the slot baseline;
  their previous depth intersected item geometry and clipped the numerals.
- Original-jar `javap -p -c` inspection of EntityThaumcraftGolem confirmed that
  FRAGILE multiplies health/armor by double 0.75, then truncates to integer. The
  decompiler's `value *= (int)0.75` is misleading and must not be copied literally.
  ARMORED uses integer max(base*1.5,base+1), not an invented flat +4.
- Movement retains base attribute 0.3 and applies the legacy additive rank/trait
  multiplier at work/home navigation requests. Step height is 0.5 for wheels,
  otherwise 0.6. Automatic repair heals one every100 ticks, or40 with REPAIR.
- Flight now uses the legacy six-axis evaluator, midpoint/six-node path shortcut,
  normalized .033/.0125 acceleration and half-motion arrival damping. Modern
  integer pathfinder targets are converted to legacy X/Z centers; copying the
  old evaluator without this adaptation shifted destinations by one block.
- Climber installs vanilla WallClimberNavigation, the direct modern successor
  of legacy PathNavigateClimber, with synced wall contact and fall immunity.
  A real four-block-wall ascent is exercised, not only a navigation-class check.
- FIGHTER retaliation and following-owner defense/assistance replace invented
  proactive nearest-hostile targeting. Owner and same-owner pets stay protected.
  The original 1.15 melee task is retained alongside the ranged task.
- The original fixed eye height0.7 is now explicit alongside body dimensions
  0.4x0.9; the modern default0.765 changed line-of-sight and targeting origins.
- Dart no longer uses a fixed-damage ThrowableProjectile or invisible renderer.
  It inherits AbstractArrow flight, hit/embedding, lifetime, damage persistence and
  NPC pickup restrictions. GolemArmDart's attackDamage/3+range+Gaussian*.25,
  eye+range-squared aim, speed1.6, spread3 and AIArrowAttack20..25 cadence are
  restored. The real renderer uses matching arrow geometry at legacy scale0.033.
- Original entity status5..9 emotes use the original atlas, timing and motion;
  showGolemEmotes gates their presentation. Acquisition, aborted work, follow,
  stay and rank-up have their corresponding status triggers.
- Sneak + seal-displayer + obstructed line-of-sight now produces the legacy
  gray/alpha.25 silhouette through a buffered no-depth/color-only render type.
  No global depth/write state is changed. Leashes were already supplied by
  modern EntityRenderer; the previous blanket "missing leash" boundary was
  incorrect and has been replaced by an actual emitted-geometry check.

## Evidence

- Dedicated-server golem/seal/press audit: **71/71 PASS**. Attribute/work-speed
  assertions cover all 7200 registered part combinations at ranks 0, 3 and 10.
- Focus regression: **16/16 PASS**.
- Aggregate audit: **40/40 PASS**. Its worldgen entries include registration
  checks, not full ecosystem or generated-shape equivalence.
- Live client loaded **17 OBJ models / 884 faces**.
- Real renderer contract emits **760 reveal vertices**, legacy gray/alpha values,
  and **100 vanilla leash vertices**. Standing or removing the displayer disables
  reveal again. This is geometry/gating evidence, not a matching legacy screenshot.
- Dart emits24 vertices with measured maximum span0.528. The legacy and modern
  vanilla arrow PNGs differ as files, but their rendered UV regions have zero
  visible-pixel differences; the removed old unused texture rows are irrelevant.
- GPU depth-state checks establish unconditional depth acceptance and verify
  cleanup. NeoForge's NO_DEPTH_TEST shard is a no-op, so reveal uses an explicit
  ALWAYS depth state rather than depending on whatever the preceding pass left.
- Ten captured client phases: two views of six material/part combinations,
  then priority, filter, area, toggles, required/forbidden traits, nine Use options,
  Stock quotas and advanced Pickup filters.
- Menus in the final visual test are opened by the integrated server through the
  real network, not constructed client-only. Priority/color/lock requests require
  both menu data acknowledgement and matching world seal-cache state. Final seal
  removal must disappear from the client cache before the client exits.
- Real ghost click packets additionally confirm quota 15+1+10=26, right-click
  filter clearing, and empty carried cursor after those non-consuming operations.
- Real integrated-client chunk unwatch removes the seal cache after a1024-block
  spectator teleport; returning restores the current server snapshot. Original
  position and game mode are restored in the disposable fixture. A subsequent
  disconnect clears the cache, stops/saves the server and reopens the same world;
  chunk-watch restores the persisted priority4 seal before final cleanup.
- Captures were inspected; the automated pixel gate verifies twelve nonblank
  model views and absence of magenta missing-texture checker pixels. Initial
  missing translations were found visually and copied from the legacy language
  file. No original model/texture assets were recolored or replaced.

Raw reports, client log and PNGs are ignored under
`tools/reports/local/review-20261102/`; no player world is used.
The combined server gate is `tools/audits/server/test-golem-remediation-evidence.ps1`.
Pass the run's start timestamp as `-NotBefore`: it rejects stale/missing reports,
unexpected totals and missing successful exporter markers. A later server launch
returned exit0 after registry-loading failure and did not export fresh reports;
that attempt was rejected, not counted as a pass. A serial retry with fresh
configuration exported all three reports successfully. Concurrent rebuilds of the
same live class/resource directories must not be treated as a stable test snapshot.

## Reproduce Visual Evidence

First prepare a disposable copy of the existing server audit fixture under
`05_neoforge_port/run/saves/tc_visual_review_20261103`. Do not copy a player save.
From `05_neoforge_port`, run:

```powershell
.\gradlew.bat build runClient --no-daemon --no-build-cache -PtcClientVisualAuditWorld=tc_visual_review_20261103
```

The opt-in probe captures its ten phases, cleans up its temporary placed seal
and support block, then stops only that client normally. Other client launches
do not enable the probe. The world name must match `tc_visual_[A-Za-z0-9_-]+`.
Run `tools/audits/client/test-golem-visual-evidence.ps1` from the repository to
check captures and the real server-menu/removal acknowledgement marker.
Gradle rejects a missing fixture before launch. The opt-in probe also captures
its startup screen and fails on disconnection instead of silently waiting there.

## Not Certified

- Arbitrary addon OreDictionary equivalence and metadata encoded only in block
  states without an explicit modern mapping. The DataFixer families and current
  behavior fixtures do not certify those cases.
- Full navigation/combat campaigns across arbitrary terrain and addon targets.
  The follow teleport fixture exercises the production fallback after a controlled
  path failure; it does not certify every natural path-failure situation. Flight
  tests assert entering the work radius, not an invented stationary-hover rule.
- Pixel equality against matching 1.12.2 frames. The captures establish actual
  nonblank rendering and working network acknowledgements, not that equality.
- Dedicated remote/two-client testing. Integrated-client watch/unwatch, cache
  clearing, world reopen and persisted chunk snapshots are now exercised; these
  are not a claim that two simultaneous remote clients have been run.
- Strict cross-mod inventory atomicity: NeoForge IItemHandler has no common
  transaction API. If a handler rejects reinsertion after extracting, escrow
  goes to the explicit player/drop refund path. Conservation is tested; unchanged
  original slots are not falsely promised for such a handler.
- Unrelated uncommitted worldgen changes, crystal ecology/distribution and
  remaining Exchange compatibility/fortune/FX acceptance from the parent audit.

## Remaining Item Disposition

| Previous report item | Disposition |
| --- | --- |
| Flying/climbing implementation | Implemented; controller, path graph, actual flight/work radius and four-block wall ascent checked. |
| Owner combat | Implemented; registered selector does not invent proactive aggro; defense/assistance and owner/pet exclusions checked. |
| Dart stub found during follow-up | Replaced with arrow physics/dynamic damage and visible legacy-scale renderer; actual hits, embedding, NBT, cadence and rendered geometry checked. |
| Emotes, silhouette, leash | Emotes/reveal implemented; original atlas contracts, actual vertices and GPU depth setup/cleanup checked. Leash was already provided by the vanilla superclass. |
| Follow fallback | Production branch checked with controlled path failure and safe landing/water-cost restoration; arbitrary terrain campaigns remain unmeasured. |
| Watch/unwatch/reconnect | Actual integrated-client cycle checked; two simultaneous dedicated-server clients remain a separate acceptance scenario. |
| Exact legacy pixels | Still unverified; requires matched legacy/modern camera, lighting, animation and player state. Nonblank captures are not that proof. |
| Arbitrary addon dictionary and foreign inventory transactions | Explicit compatibility boundaries, not defects that a local fixture can universally eliminate. |
| Concurrent crystal/worldgen and Exchange fortune/FX | Not closed by this golem batch; retain the parent report's open classification. |
