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

## Evidence

- Dedicated-server golem/seal/press audit: **32/32 PASS**.
- Focus regression: **16/16 PASS**.
- Aggregate audit: **40/40 PASS**. Its worldgen entries include registration
  checks, not full ecosystem or generated-shape equivalence.
- Live client loaded **17 OBJ models / 884 faces**.
- Seven captured client phases: two views of six material/part combinations,
  then priority, filter, area, toggles and required/forbidden traits.
- Menus in the final visual test are opened by the integrated server through the
  real network, not constructed client-only. Priority/color/lock requests require
  both menu data acknowledgement and matching world seal-cache state. Final seal
  removal must disappear from the client cache before the client exits.
- Captures were inspected; the automated pixel gate verifies twelve nonblank
  model views and absence of magenta missing-texture checker pixels. Initial
  missing translations were found visually and copied from the legacy language
  file. No original model/texture assets were recolored or replaced.

Raw reports, client log and PNGs are ignored under
`tools/reports/local/review-20261102/`; no player world is used.

## Reproduce Visual Evidence

First prepare a disposable copy of the existing server audit fixture under
`05_neoforge_port/run/saves/tc_visual_review_20261103`. Do not copy a player save.
From `05_neoforge_port`, run:

```powershell
.\gradlew.bat build runClient --no-daemon --no-build-cache -PtcClientVisualAuditWorld=tc_visual_review_20261103
```

The opt-in probe captures its seven phases, cleans up its temporary placed seal
and support block, then stops only that client normally. Other client launches
do not enable the probe. The world name must match `tc_visual_[A-Za-z0-9_-]+`.
Run `tools/audits/client/test-golem-visual-evidence.ps1` from the repository to
check captures and the real server-menu/removal acknowledgement marker.

## Not Certified

- All advanced Pickup/Fill/Empty/Use/Provider/Stock filter and action options;
  their full metadata-flattening/OreDictionary-to-modern-tag semantics still
  need explicit migration and individual behavior fixtures. A menu existing is
  not proof that every legacy option has been implemented.
- Flying/climbing movement, owner-combat assistance, emotes, leash rendering,
  through-wall golem silhouettes, and full end-to-end follow teleport scenarios.
- Pixel equality against matching 1.12.2 frames. The captures establish actual
  nonblank rendering and working network acknowledgements, not that equality.
- Dedicated remote/two-client chunk-watch/unwatch and disconnect/reconnect tests.
- Strict cross-mod inventory atomicity: NeoForge IItemHandler has no common
  transaction API. If a handler rejects reinsertion after extracting, escrow
  goes to the explicit player/drop refund path. Conservation is tested; unchanged
  original slots are not falsely promised for such a handler.
- Unrelated uncommitted worldgen changes, crystal ecology/distribution and
  remaining Exchange compatibility/fortune/FX acceptance from the parent audit.
