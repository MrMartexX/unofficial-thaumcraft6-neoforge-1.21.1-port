# Seal World Rendering

Primary BETA26 references: RenderEventHandler.renderLast/drawSeals/renderSeal,
translateSeal/drawSealArea; UtilsFX.renderTextureIn3D/renderQuadCentered;
FXDispatcher.spark. Follow the migration guide client/common separation.

The existing menu, SavedData and sync are not evidence of an in-world renderer.
Render only the current dimension's synced seals when the camera is a player
holding ISealDisplayer in either hand. Range is squared distance to block center
<=256; alpha is 1-distanceSquared/256. Sneaking reveals through blocks and removes
the icon's extrusion. Preserve all six legacy face transforms, .5 icon scale,
.05 surface offset, sixteen-strip side geometry, animated atlas sprites, rotating
area center, 24 area corner quads, channel colors and redstone stop flicker/spark.

Client render types own blending, depth, culling and write masks. Resolve sprites
from the current atlas (no stale reload cache), and do not mutate server seals or
network state from rendering. Preserve the existing particle engine by making
spark UV flipping opt-in. Validate geometry/visibility/atlas and live client entry;
do not equate those checks with pixel equality against matched original frames.

## Evidence

The opt-in client probe passes actual geometry/atlas checks on all six faces,
both thickness modes and 4,368 emitted vertices. It checks main/offhand displayer
gates, the squared-distance opacity endpoints and all registered seal sprites.
Actual synced seals reach the world render pass; the ten-scene menu/ghost-slot
and chunk-watch/reconnect probe still completes. The same fresh flat fixture
checks real generated surface Y=63 at an off-platform coordinate, sixteen seal
stacks in the player's inventory and sixteen in the near-spawn chest.

The previous world generator used only 64 modern layers from Y=-64, leaving
ground at Y=-1 below the platform. Updated pristine archives use 128 modern
layers. Existing player worlds are NOT rewritten. Reset creates new paired copies
and new login kits, rather than deleting progress automatically on restart.

Live markers are in ignored `tools/reports/local/paired-worlds/seal-render-accepted.log`.
The actual GPU depth-function/write-mask combinations are checked: Shift reveal
does not write depth, matching legacy disabled-depth-test behavior. A pre-UI
framebuffer capture shows the seal, center ring and area corners on a solid block
in the corrected flat world; the client evidence script also checks its pixels.
The enforceable client evidence script requires both seal geometry and actual
world-submission markers. This does not certify matched 1.12 frames or all possible
camera/shader-pack combinations. A running old client needs restart to load code.
