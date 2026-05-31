# Research Table GUI parity audit

Reviewed branch: `research-knowledge-scanning-design`

Primary goal: compare the legacy Thaumcraft 6 1.12.2 research table GUI and the current NeoForge 1.21.1 implementation side by side, focused on the unfinished GUI polish items:

- full 1.12 page flip, hover and zoom animation;
- category icon column;
- research aid polish;
- final card animation.

This audit is intentionally limited to GUI-visible behavior and the minimal server/menu/network/data paths required by that GUI. It does not approve broad GUI, Thaumonomicon, recipe unlock, warp, essentia, or full networking expansion.

## Source files compared

Legacy sources:

- `03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/client/gui/GuiResearchTable.java`
- `03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/common/tiles/crafting/TileResearchTable.java`
- `03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/api/research/theorycraft/ResearchTableData.java`
- `03_self_decompiled_check/vineflower_thaumcraft6/thaumcraft/api/research/theorycraft/TheorycraftManager.java`

Current port sources:

- `05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java`
- `05_neoforge_port/src/main/java/thaumcraft/common/menu/TCResearchTableMenu.java`
- `05_neoforge_port/src/main/java/thaumcraft/common/tiles/crafting/TCResearchTableBlockEntity.java`
- `05_neoforge_port/src/main/java/thaumcraft/common/research/theorycraft/TCResearchTableData.java`
- `05_neoforge_port/src/main/java/thaumcraft/common/research/theorycraft/TCResearchTableActions.java`
- `05_neoforge_port/src/main/java/thaumcraft/common/research/theorycraft/TCResearchTableNetwork.java`
- `05_neoforge_port/src/main/java/thaumcraft/common/research/theorycraft/TCTheorycraftManager.java`
- `05_neoforge_port/src/main/java/thaumcraft/common/research/theorycraft/TCTheorycraftAid.java`

## Overall state

Current implementation is structurally correct for a modern NeoForge slice: server-owned data, menu boundary, explicit payloads, authoritative action result sync, and a client screen that no longer mutates table data silently. The main parity gaps are not registry or data availability gaps. They are mostly client-side animation, timing, sound, tooltip, sparkle and visual feedback gaps.

Current code already has most of the data needed for legacy parity:

- `TCResearchTableData` keeps legacy-shaped fields: `inspiration`, `inspirationStart`, `bonusDraws`, `placedCards`, `aidsChosen`, `penaltyStart`, `savedCards`, `aidCards`, `categoryTotals`, `categoriesBlocked`, `cardChoices`, and `lastDraw`.
- `TCResearchTableScreen` keeps client animation arrays for card hover, zoom out and zoom in.
- `TCTheorycraftManager` keeps legacy class-name keys for cards and aids.
- `TCResearchTableActions` validates server-side state before mutating table data.

The next patch should not add a new subsystem. It should refine the already-started research table GUI slice.

## 1. GUI base layout and texture identity

Legacy behavior:

- Screen size is `255x255`.
- Uses `textures/gui/gui_research_table.png` as background.
- Uses `textures/gui/gui_base.png`, `paper.png`, `papergilded.png`, and `textures/aspects/_unknown.png` for buttons, inspiration icons, papers and unknown marker.
- The screen hides normal labels and draws custom contents directly.

Current behavior:

- Screen size is also `255x255`.
- Same texture paths are used.
- Normal title/inventory labels are moved offscreen.
- Background render order already follows the intended high-level sequence: background, inspiration icons, action buttons, aid selection, card animations, sheets, category panel.

Status: mostly OK.

Needed polish:

- Keep the render order stable while making the missing visual feedback changes.
- Do not replace this screen with a generic widget/button layout. The current manual draw path is appropriate for parity.

## 2. Research table data model parity

Legacy behavior:

- `ResearchTableData` has the same core mutable fields that drive the GUI and theorycraft result.
- `drawCards` chooses 2 or 3 cards depending on bonus draws, has a 25 percent chance to draw from aid cards, filters cards by inspiration cost, blocked category and available category, and avoids duplicate card keys in one draw.
- `initialize` sets `inspirationStart` from known research and subtracts the number of selected aids.

Current behavior:

- `TCResearchTableData` mirrors the same core fields and draw filtering.
- Current `initialize` also computes `inspirationStart` from the player and subtracts accepted aid count.
- Current implementation serializes card choices and last draw through modern NBT equivalents.

Status: mostly OK.

Important parity concern:

- The modern client preview before creating a theory uses `BASE_INSPIRATION_PREVIEW = 5`, not the actual player-specific `availableTheoryInspiration`. The server later computes the correct value, but the pre-theory GUI can show the wrong number of inspiration icons and can allow the wrong number of selected aids from the client side.

Required fix:

- Add a client-visible available-inspiration value for the pre-theory state.
- Lowest-risk option: include a server-synced knowledge value already available from `TCKnowledgeSyncPayload`, or compute through the same client cache if it has enough research data.
- Until then, clamp selected aids on the server as already done, but visually mark this as partial parity.

## 3. Research aid discovery and aid selection

Legacy behavior:

- `TileResearchTable.checkSurroundingAids` scans blocks in a 9 by 9 by 3 area around the table: x and z from -4 to +4, y from -1 to +1.
- It checks block aids either by block identity or by picked block `ItemStack` equivalence.
- It checks entity aids within range 5.
- GUI refreshes available aids every 100 ticks before a theory is started.
- Aid icons are arranged in rows with up to 6 icons per row.
- Hover draws a translucent base highlight.
- Selected aids draw a full base highlight.
- Clicking toggles selected aids, limited by available inspiration.

Current behavior:

- `TCTheorycraftManager.collectNearbyAidKeys` scans the same x/z/y volume and entity range.
- Current aid matcher supports block predicates and entity predicates, but dropped stack matching exists only in `TCTheorycraftAid` and is not used by `collectNearbyAidKeys`.
- Current screen refreshes aids every 100 ticks.
- Current screen uses the same 6-per-row layout and similar 16x16 icons.
- Selected/hover overlay exists.
- Selection is limited by `BASE_INSPIRATION_PREVIEW` instead of actual available inspiration.

Status: partial.

Required fixes:

- Replace `BASE_INSPIRATION_PREVIEW` with actual available inspiration for visual preview and click limit.
- Decide whether legacy ItemStack-based aid matching is needed now. If not, document it as deferred because current registered aids are block-based.
- Consider adding hover tooltip for aid name or legacy card family only if 1.12 showed one. Do not invent extra UI unless needed.
- Ensure selected aid order is deterministic. Modern `LinkedHashSet` is likely better than legacy `HashSet`, but if visual order is compared against screenshots, document the intentional deterministic order.

## 4. Create, Complete and Scrap buttons

Legacy behavior:

- Buttons use `GuiImageButton` with texture `gui_base.png`, UV 37,66, size 51x13, hit size 49x11.
- Create at center x 128 y 22.
- Complete at center x 191 y 96.
- Scrap at center x 128 y 168.
- Create is visible when no theory exists and active only if paper and usable scribing tools exist.
- Complete is visible only for complete theories.
- Scrap is visible only for incomplete active theories.
- Button click sound is `clack` with volume 0.4, pitch 1.0.

Current behavior:

- Manual button rendering uses the same texture coordinates, sizes, center positions and color tints.
- Visibility and active rules are broadly equivalent.
- Current create and scrap play `clack`, but with UI sound volume/pitch path rather than legacy world/player sound path.
- Current complete plays `learn` directly from the screen click path, which differs from legacy button click path. Legacy complete button itself plays `clack`; learn is handled separately by tile event logic.

Status: partial.

Required fixes:

- Match legacy click sound behavior for buttons: create, complete and scrap should play clack on click.
- If learn sound is still needed on theory completion, trigger it from accepted server result or a dedicated completion event, not immediately on local click.
- Match approximate legacy volumes: clack 0.4, write 0.3, page and pageturn 1.0.

## 5. Missing no-ink and no-paper tooltip feedback

Legacy behavior:

- If a theory is active and scribing tools are missing or fully damaged, the GUI draws a custom tooltip with `tile.researchtable.noink.0` and `tile.researchtable.noink.1`.
- If paper is missing, the GUI draws a custom tooltip with `tile.researchtable.nopaper.0`.
- These warnings are drawn over the table view while a theory exists.

Current behavior:

- Current button active state and server rejection keys handle missing tools/paper, but the visible legacy warning tooltips are not implemented in the same way.

Status: missing.

Required fix:

- Add a `renderMissingSuppliesWarnings` step after base background and before or after card stack render, using the same approximate positions as legacy.
- Use current translatable keys if they already exist; otherwise add the missing lang keys.
- Do not show intrusive status text from action results, because legacy does not render transient action-status text over the table.

## 6. Blank paper stack and draw-card interaction

Legacy behavior:

- Paper stack count is visualized as `1 + paperCount / 4` random sheets at x 65 y 100.
- Random seed is fixed to 55 for blank stack positions.
- If the stack can be clicked and no card choices are visible, unknown icon is drawn at x 65 y 100 with scale 1.5, or 1.75 on hover.
- Click area is x 25 y 55 width 75 height 90.
- Clicking paper stack calls `drawCards`, which consumes paper server-side through the container action path.

Current behavior:

- Same sheet count formula, same fixed random seed, same unknown icon position/scale, and same click area.
- Server-side action consumes paper and draws cards authoritatively.

Status: mostly OK.

Required polish:

- Check whether current sheet randomization exactly matches legacy. Legacy applies random offset and random rotation, then may randomly rotate the sheet 180 degrees around Z and 180 degrees around Y before drawing. Current render applies Gaussian offset and Z rotation, but does not include the legacy random 180-degree flips.
- Add optional legacy random flips if visual screenshot comparison shows papers look too uniform.

## 7. Page flip, hover and card zoom-out animation

Legacy behavior:

- Card choices animate from the draw stack toward their target positions.
- Card target x formula is based on center `sx = 128`, spacing `cw = 110`, and card count.
- A card starts zooming out when it is the last card or when the next card has already reached `cardZoomOut > 0.6`.
- When a card starts moving from 0, page flip sound plays.
- Hover is only active when `cardZoomOut >= 0.95` and no card is currently selected.
- Hover approaches 0.25 and otherwise decays by `0.1 * partialTicks`.

Current behavior:

- Current card position and scale formulas are close to legacy.
- Current `cardZoomOut` stagger rule matches the `next > 0.6` idea.
- Current hover threshold and target are close.
- Current code does not play page sound at the exact moment each card starts zooming out. It plays page-related sounds on click and selection instead.

Status: partial.

Required fixes:

- Track previous `cardZoomOut[index]` and play `page` when a card begins opening, matching legacy.
- Keep `pageturn` for selected-card transition, not for ordinary page emergence.
- Compare the target x formula visually after sound/timing changes. The modern formula appears mathematically aligned, but screenshot inspection should confirm it.

## 8. Final card selection and commit animation

Legacy behavior:

- Player clicks a visible card.
- Client sends a card-select action to the server through container click id `4 + pressed`.
- Server marks one `cardChoice.selected` in synced table data.
- Client `checkCards()` notices the server-selected choice.
- Client copies selected state into `cardActive[]`, sets `cardSelected = true`, plays pageturn, and sends a separate action id `1` for commit after entering selected state.
- During animation, selected card moves toward the saved stack at x 191 y 100 while non-selected cards fade out through `1.0 - cardZoomIn`.
- When selected zoom reaches 1.0, write sound plays, local `cardChoices` clear, and `lastDraw` becomes the table `lastDraw`.

Current behavior:

- Player click starts local animation immediately through `startCardSelectionAnimation`.
- Once local zoom reaches 0.995, the client sends atomic `ACTION_SELECT_AND_COMMIT` to server.
- Server then validates required items, activates card, consumes required items, consumes ink, marks selected, commits to last draw, clears choices, and returns authoritative state.

Status: functionally safer, but parity mismatch.

Why this matters:

- Modern flow is server-authoritative at final mutation time, but the client animates before it knows whether the server will accept the card.
- If the server rejects the card due to missing required items, cooldown, missing ink or activation failure, the client already played the selection animation.
- Legacy flow animates only after synced server state marks a choice selected.

Required fix:

- Split the current atomic path into two phases while preserving server validation:
  1. On card click, send `ACTION_SELECT_CARD` immediately.
  2. If accepted result returns synced table data with one selected choice, begin local pageturn/zoom-in animation.
  3. When zoom-in completes, play write and send `ACTION_COMMIT_SELECTED`.
  4. If rejected, do not animate selection.
- Keep `ACTION_SELECT_AND_COMMIT` only for diagnostics or remove it once the two-phase flow is stable.

This is the highest-priority parity fix.

## 9. Card sheet rendering

Legacy behavior:

- Sheet render uses paper or gilded paper based on `fromAid`.
- It applies Gaussian offset, Z rotation based on tilt, and random 180-degree flips.
- Category watermark is drawn at alpha / 6.
- Text is black, bold title, wrapped body text.
- Inspiration cost icons use `gui_base` with special handling for negative cost.
- Required item icons render near y 35.
- Consumed required item marker pulses using a sine wave.
- Empty required item draws unknown icon.

Current behavior:

- Paper and gilded paper are used correctly.
- Gaussian offset and Z rotation exist.
- Category watermark exists.
- Text, cost icons, required item icons and unknown icon exist.
- Required item consumed marker is static, not pulsing.
- Required item display is capped at 4 items, while legacy iterates all required item slots.

Status: partial.

Required fixes:

- Add legacy random 180-degree flips if needed for visual parity.
- Add pulsing consumed marker animation.
- Check all cards with required items. If any card can have more than 4 required items in legacy, remove the modern cap or document why 4 is enough for current registered card set.
- Verify title wrapping and text line count against screenshots. Current text is probably acceptable but may need exact y/line tuning.

## 10. Required item hover tooltip

Legacy behavior:

- `drawSheetOverlay` checks hover over each required item slot on a fully opened card and renders the item tooltip, or `tc.card.unknown` for empty placeholders.

Current behavior:

- The current screen has a general tooltip render call, but the required-item hover behavior must be checked against the card’s transformed/scaled coordinates.

Status: needs verification.

Required fix:

- Add or verify exact hover hitboxes for required items after card scale and transform.
- If current tooltip does not resolve these coordinates correctly, implement a direct required-item overlay pass equivalent to legacy.

## 11. Category icon column

Legacy behavior:

- `tempCatTotals` is a client-side display copy of category totals.
- Every tick it moves each shown category value by one point toward the real value.
- If a value increases, the category is added to `sparkle` and two GUI sparkles are drawn near the percentage text.
- Categories are sorted by descending displayed value.
- Each row draws category icon at x 253 and text at x 276.
- Rows after `penaltyStart` display `(-value/3)` and use a different y offset.
- Blocked categories use a dark color, normal unpenalized rows use cyan, penalized rows use white.
- Hovering over a category icon shows the localized category name tooltip.

Current behavior:

- `displayedCategoryTotals` exists and is sorted by descending displayed value.
- Values move by one point toward target.
- Category icon and percentage text are rendered in the right column with the same basic x/y layout.
- Blocked, unpenalized and penalized colors are implemented.
- Penalty text is implemented.
- Sparkle effect is missing.
- Category hover tooltip appears to be missing or incomplete.
- New categories default to target value on first insert, so first-time gains may not animate from 0 and may not sparkle.
- Current render limits to 7 rows; legacy iterates all sorted displayed categories. This may be harmless because the screen only comfortably supports about 7 rows, but it should be documented if kept.

Status: partial.

Required fixes:

- Add a per-tick list of categories that increased this tick.
- Render a modern GUI sparkle approximation near percentage text for those categories.
- Add hover tooltip on category icon with localized category name.
- Initialize newly visible categories at 0 before moving toward target, unless restoring a screen that already had existing totals.
- Decide whether to keep the 7-row cap. If kept, document it as a display safety cap.

## 12. Sound parity

Legacy sounds:

- `page` when a card begins page-flip/opening.
- `pageturn` when a card is selected and selection animation begins.
- `clack` for create, complete and scrap button clicks, volume 0.4, pitch 1.0.
- `write` when selected card animation reaches completion, volume 0.3, pitch 1.0.
- `learn` is triggered by tile event path, not directly by the complete button click in `GuiResearchTable`.

Current sounds:

- `SOUND_CLACK`, `SOUND_PAGE`, `SOUND_PAGETURN`, `SOUND_WRITE`, and `SOUND_LEARN` exist.
- Current complete button plays learn directly.
- Current write pitch is 0.9 and clack is effectively 1.0 through `forUI`.
- Page sound timing is not legacy-exact.

Status: partial.

Required fixes:

- Use sound volumes/pitches closer to legacy.
- Trigger page sound on card opening start.
- Trigger pageturn when server-accepted selected state begins selection animation.
- Trigger write when commit animation finishes.
- Move learn sound to accepted complete-theory result or server event equivalent.

## 13. Server authority and networking boundary

Legacy behavior:

- GUI sends container click ids and packets.
- Client has local animation state but table state is ultimately synced from `TileResearchTable` through NBT and container events.

Current behavior:

- Current architecture is better for NeoForge: explicit payloads, menu validation, table validation and authoritative result payloads.
- Server checks open menu, usable tools, paper, required items, activation result and cooldown.
- Every action result returns authoritative table data.

Status: good, but selection animation should be rearranged for parity.

Required rule:

- Keep the modern server-authoritative design. Do not reintroduce unsafe client-side mutation.
- Make the client wait for accepted selected state before animating the final card movement.

## Recommended implementation order

1. Fix final card selection flow into legacy-shaped two-phase select then animate then commit.
2. Fix page sound timing for card zoom-out start.
3. Change button and write sound volumes/pitches to legacy-like values and move learn sound to accepted completion result.
4. Replace `BASE_INSPIRATION_PREVIEW` with actual player available inspiration in pre-theory GUI.
5. Add missing no-ink and no-paper warning tooltips.
6. Add category column sparkle and category hover tooltip.
7. Make new category totals animate from 0 and sparkle on first gain.
8. Add pulsing consumed-item marker and verify required-item hover tooltips.
9. Add sheet random 180-degree flips if screenshot comparison shows visible mismatch.
10. Decide and document whether ItemStack aid matching and 7-row category cap are intentionally modernized.

## Concrete high-priority patch plan

Patch 1 should only touch GUI behavior and the existing research-table action flow:

- `TCResearchTableScreen`
  - Add state for pending server-selected card animation.
  - On card click, send `ACTION_SELECT_CARD`, do not immediately animate.
  - In `applyLatestSync`, detect accepted selected card result and call `startCardSelectionAnimation`.
  - After animation completes, play write and send `ACTION_COMMIT_SELECTED`.
  - Add page sound trigger when `cardZoomOut` moves from 0.
  - Restore clack/write/page/pageturn volumes closer to legacy.

- `TCResearchTableActions`
  - Keep `ACTION_SELECT_CARD` and `ACTION_COMMIT_SELECTED` as authoritative actions.
  - Keep `ACTION_SELECT_AND_COMMIT` temporarily only if existing tests rely on it; otherwise deprecate after patch 1.

- `TCResearchTableActionResultPayload`
  - If needed, expose result key and action id enough for the screen to distinguish accepted select from other accepted actions.

Patch 2 should add missing visual polish:

- no-ink/no-paper warnings;
- category sparkle;
- category tooltip;
- pulsing consumed-item marker;
- required-item tooltip verification.

Patch 3 should handle pre-theory inspiration preview and aid selection limit:

- Add server/client path for actual available inspiration, or reuse existing knowledge cache if reliable.
- Replace `BASE_INSPIRATION_PREVIEW` use in render and click limit.

## Current risk assessment

Low risk:

- Sound timing and volumes.
- Category tooltip.
- Missing no-paper/no-ink warnings.
- Pulsing required item consumed marker.

Medium risk:

- Category sparkle, because legacy uses `FXDispatcher` GUI particles and the port should use a small modern GUI-only approximation, not the old renderer.
- Pre-theory available inspiration sync, because it touches knowledge display state.

Highest risk:

- Final card selection flow, because it changes timing between client animation and server mutation. This should be done first, with the research table diagnostic harness run immediately afterward.

## Validation after each patch

From `05_neoforge_port`:

```powershell
.\gradlew.bat build --no-daemon
.\gradlew.bat runServer --no-daemon -PtcResearchTableAudit=true "-PtcResearchTableAuditPath=D:\Thaumcraft_6_port_to_1.21.1\07_Test_Instance_and_Comparisons\research_table_audit\thaumcraft_1_21_research_table.md"
.\gradlew.bat runClient --no-daemon
```

Manual client checklist:

- Open research table with no theory.
- Check scribing tools and paper slots.
- Check aid icon row with bookshelf/enchanting table/beacon/basic block aids nearby.
- Select and deselect aids and verify inspiration preview.
- Create theory.
- Draw cards and check page sound timing.
- Hover all cards and required item icons.
- Select a card with and without required items available.
- Verify rejected selection does not animate as accepted.
- Verify selected card animation moves to saved stack and write sound plays only at completion.
- Verify category totals animate, sparkle on gain, sort correctly, show penalty text, and show tooltip on hover.
- Complete theory and verify knowledge gain plus completion sound behavior.
