package thaumcraft.common.lib.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.internal.WorldCoordinates;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXEssentiaSource;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;

public class EssentiaHandler {
   static final int DELAY = 10000;
   private static HashMap<WorldCoordinates, ArrayList<WorldCoordinates>> sources = new HashMap<>();
   private static HashMap<WorldCoordinates, Long> sourcesDelay = new HashMap<>();
   private static TileEntity lat = null;
   private static TileEntity las = null;
   private static Aspect lasp = null;
   private static int lext = 0;
   public static ConcurrentHashMap<String, EssentiaHandler.EssentiaSourceFX> sourceFX = new ConcurrentHashMap<>();

   public static boolean drainEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range, int ext) {
      return drainEssentia(tile, aspect, direction, range, false, ext);
   }

   public static boolean drainEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range, boolean ignoreMirror, int ext) {
      WorldCoordinates tileLoc = new WorldCoordinates(tile.func_174877_v(), tile.func_145831_w().field_73011_w.getDimension());
      if (!sources.containsKey(tileLoc)) {
         getSources(tile.func_145831_w(), tileLoc, direction, range);
         return sources.containsKey(tileLoc) ? drainEssentia(tile, aspect, direction, range, ignoreMirror, ext) : false;
      }

      for (WorldCoordinates source : sources.get(tileLoc)) {
         TileEntity sourceTile = tile.func_145831_w().func_175625_s(source.pos);
         if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
            break;
         }

         IAspectSource as = (IAspectSource)sourceTile;
         if (!as.isBlocked() && (!ignoreMirror || !(sourceTile instanceof TileMirrorEssentia)) && as.takeFromContainer(aspect, 1)) {
            PacketHandler.INSTANCE
               .sendToAllAround(
                  new PacketFXEssentiaSource(
                     tile.func_174877_v(),
                     (byte)(tile.func_174877_v().func_177958_n() - source.pos.func_177958_n()),
                     (byte)(tile.func_174877_v().func_177956_o() - source.pos.func_177956_o()),
                     (byte)(tile.func_174877_v().func_177952_p() - source.pos.func_177952_p()),
                     aspect.getColor(),
                     ext
                  ),
                  new TargetPoint(
                     tile.func_145831_w().field_73011_w.getDimension(),
                     tile.func_174877_v().func_177958_n(),
                     tile.func_174877_v().func_177956_o(),
                     tile.func_174877_v().func_177952_p(),
                     32.0
                  )
               );
            return true;
         }
      }

      sources.remove(tileLoc);
      sourcesDelay.put(tileLoc, System.currentTimeMillis() + 10000L);
      return false;
   }

   public static boolean drainEssentiaWithConfirmation(TileEntity tile, Aspect aspect, EnumFacing direction, int range, boolean ignoreMirror, int ext) {
      WorldCoordinates tileLoc = new WorldCoordinates(tile.func_174877_v(), tile.func_145831_w().field_73011_w.getDimension());
      if (!sources.containsKey(tileLoc)) {
         getSources(tile.func_145831_w(), tileLoc, direction, range);
         return sources.containsKey(tileLoc) ? drainEssentiaWithConfirmation(tile, aspect, direction, range, ignoreMirror, ext) : false;
      }

      for (WorldCoordinates source : sources.get(tileLoc)) {
         TileEntity sourceTile = tile.func_145831_w().func_175625_s(source.pos);
         if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
            break;
         }

         IAspectSource as = (IAspectSource)sourceTile;
         if (!as.isBlocked() && (!ignoreMirror || !(sourceTile instanceof TileMirrorEssentia)) && as.doesContainerContainAmount(aspect, 1)) {
            las = sourceTile;
            lasp = aspect;
            lat = tile;
            lext = ext;
            return true;
         }
      }

      sources.remove(tileLoc);
      sourcesDelay.put(tileLoc, System.currentTimeMillis() + 10000L);
      return false;
   }

   public static void confirmDrain() {
      if (las != null && lasp != null && lat != null) {
         IAspectSource as = (IAspectSource)las;
         if (as.takeFromContainer(lasp, 1)) {
            PacketHandler.INSTANCE
               .sendToAllAround(
                  new PacketFXEssentiaSource(
                     lat.func_174877_v(),
                     (byte)(lat.func_174877_v().func_177958_n() - las.func_174877_v().func_177958_n()),
                     (byte)(lat.func_174877_v().func_177956_o() - las.func_174877_v().func_177956_o()),
                     (byte)(lat.func_174877_v().func_177952_p() - las.func_174877_v().func_177952_p()),
                     lasp.getColor(),
                     lext
                  ),
                  new TargetPoint(
                     lat.func_145831_w().field_73011_w.getDimension(),
                     lat.func_174877_v().func_177958_n(),
                     lat.func_174877_v().func_177956_o(),
                     lat.func_174877_v().func_177952_p(),
                     32.0
                  )
               );
         }
      }

      las = null;
      lasp = null;
      lat = null;
   }

   public static boolean addEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range, boolean ignoreMirror, int ext) {
      WorldCoordinates tileLoc = new WorldCoordinates(tile.func_174877_v(), tile.func_145831_w().field_73011_w.getDimension());
      if (!sources.containsKey(tileLoc)) {
         getSources(tile.func_145831_w(), tileLoc, direction, range);
         return sources.containsKey(tileLoc) ? addEssentia(tile, aspect, direction, range, ignoreMirror, ext) : false;
      }

      ArrayList<WorldCoordinates> es = sources.get(tileLoc);
      ArrayList<WorldCoordinates> empties = new ArrayList<>();

      for (WorldCoordinates source : es) {
         TileEntity sourceTile = tile.func_145831_w().func_175625_s(source.pos);
         if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
            break;
         }

         IAspectSource as = (IAspectSource)sourceTile;
         if (!as.isBlocked() && (!ignoreMirror || !(sourceTile instanceof TileMirrorEssentia))) {
            if (!as.doesContainerAccept(aspect) || as.getAspects() != null && as.getAspects().visSize() != 0) {
               if (as.doesContainerAccept(aspect) && as.addToContainer(aspect, 1) <= 0) {
                  PacketHandler.INSTANCE
                     .sendToAllAround(
                        new PacketFXEssentiaSource(
                           source.pos,
                           (byte)(source.pos.func_177958_n() - tile.func_174877_v().func_177958_n()),
                           (byte)(source.pos.func_177956_o() - tile.func_174877_v().func_177956_o()),
                           (byte)(source.pos.func_177952_p() - tile.func_174877_v().func_177952_p()),
                           aspect.getColor(),
                           ext
                        ),
                        new TargetPoint(
                           tile.func_145831_w().field_73011_w.getDimension(),
                           tile.func_174877_v().func_177958_n(),
                           tile.func_174877_v().func_177956_o(),
                           tile.func_174877_v().func_177952_p(),
                           32.0
                        )
                     );
                  return true;
               }
            } else {
               empties.add(source);
            }
         }
      }

      for (WorldCoordinates source : empties) {
         if (source != null && source.pos != null) {
            TileEntity sourceTile = tile.func_145831_w().func_175625_s(source.pos);
            if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
               break;
            }

            IAspectSource as = (IAspectSource)sourceTile;
            if (aspect != null && as.doesContainerAccept(aspect) && as.addToContainer(aspect, 1) <= 0) {
               PacketHandler.INSTANCE
                  .sendToAllAround(
                     new PacketFXEssentiaSource(
                        source.pos,
                        (byte)(source.pos.func_177958_n() - tile.func_174877_v().func_177958_n()),
                        (byte)(source.pos.func_177956_o() - tile.func_174877_v().func_177956_o()),
                        (byte)(source.pos.func_177952_p() - tile.func_174877_v().func_177952_p()),
                        aspect.getColor(),
                        ext
                     ),
                     new TargetPoint(
                        tile.func_145831_w().field_73011_w.getDimension(),
                        tile.func_174877_v().func_177958_n(),
                        tile.func_174877_v().func_177956_o(),
                        tile.func_174877_v().func_177952_p(),
                        32.0
                     )
                  );
               return true;
            }
         }
      }

      sources.remove(tileLoc);
      sourcesDelay.put(tileLoc, System.currentTimeMillis() + 10000L);
      return false;
   }

   public static boolean findEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range, boolean ignoreMirror) {
      WorldCoordinates tileLoc = new WorldCoordinates(tile.func_174877_v(), tile.func_145831_w().field_73011_w.getDimension());
      if (!sources.containsKey(tileLoc)) {
         getSources(tile.func_145831_w(), tileLoc, direction, range);
         return sources.containsKey(tileLoc) ? findEssentia(tile, aspect, direction, range, ignoreMirror) : false;
      }

      for (WorldCoordinates source : sources.get(tileLoc)) {
         TileEntity sourceTile = tile.func_145831_w().func_175625_s(source.pos);
         if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
            break;
         }

         IAspectSource as = (IAspectSource)sourceTile;
         if (!as.isBlocked() && (!ignoreMirror || !(sourceTile instanceof TileMirrorEssentia)) && as.doesContainerContainAmount(aspect, 1)) {
            return true;
         }
      }

      return false;
   }

   public static boolean canAcceptEssentia(TileEntity tile, Aspect aspect, EnumFacing direction, int range, boolean ignoreMirror) {
      WorldCoordinates tileLoc = new WorldCoordinates(tile.func_174877_v(), tile.func_145831_w().field_73011_w.getDimension());
      if (!sources.containsKey(tileLoc)) {
         getSources(tile.func_145831_w(), tileLoc, direction, range);
         return sources.containsKey(tileLoc) ? findEssentia(tile, aspect, direction, range, ignoreMirror) : false;
      }

      for (WorldCoordinates source : sources.get(tileLoc)) {
         TileEntity sourceTile = tile.func_145831_w().func_175625_s(source.pos);
         if (sourceTile == null || !(sourceTile instanceof IAspectSource)) {
            break;
         }

         if (!ignoreMirror || !(sourceTile instanceof TileMirrorEssentia)) {
            IAspectSource as = (IAspectSource)sourceTile;
            if (!as.isBlocked() && as.doesContainerAccept(aspect)) {
               return true;
            }
         }
      }

      return false;
   }

   private static void getSources(World world, WorldCoordinates tileLoc, EnumFacing direction, int range) {
      if (sourcesDelay.containsKey(tileLoc)) {
         long d = sourcesDelay.get(tileLoc);
         if (d > System.currentTimeMillis()) {
            return;
         }

         sourcesDelay.remove(tileLoc);
      }

      TileEntity sourceTile = world.func_175625_s(tileLoc.pos);
      ArrayList<WorldCoordinates> sourceList = new ArrayList<>();
      int start = 0;
      if (direction == null) {
         start = -range;
         direction = EnumFacing.UP;
      }

      int xx = 0;
      int yy = 0;
      int zz = 0;

      for (int aa = -range; aa <= range; aa++) {
         for (int bb = -range; bb <= range; bb++) {
            for (int cc = start; cc < range; cc++) {
               if (aa != 0 || bb != 0 || cc != 0) {
                  xx = tileLoc.pos.func_177958_n();
                  yy = tileLoc.pos.func_177956_o();
                  zz = tileLoc.pos.func_177952_p();
                  if (direction.func_96559_d() != 0) {
                     xx += aa;
                     yy += cc * direction.func_96559_d();
                     zz += bb;
                  } else if (direction.func_82601_c() == 0) {
                     xx += aa;
                     yy += bb;
                     zz += cc * direction.func_82599_e();
                  } else {
                     xx += cc * direction.func_82601_c();
                     yy += aa;
                     zz += bb;
                  }

                  TileEntity te = world.func_175625_s(new BlockPos(xx, yy, zz));
                  if (te != null
                     && te instanceof IAspectSource
                     && (
                        !(sourceTile instanceof TileMirrorEssentia)
                           || !(te instanceof TileMirrorEssentia)
                           || sourceTile.func_174877_v().func_177958_n() != ((TileMirrorEssentia)te).linkX
                           || sourceTile.func_174877_v().func_177956_o() != ((TileMirrorEssentia)te).linkY
                           || sourceTile.func_174877_v().func_177952_p() != ((TileMirrorEssentia)te).linkZ
                           || sourceTile.func_145831_w().field_73011_w.getDimension() != ((TileMirrorEssentia)te).linkDim
                     )) {
                     sourceList.add(new WorldCoordinates(new BlockPos(xx, yy, zz), world.field_73011_w.getDimension()));
                  }
               }
            }
         }
      }

      if (sourceList.size() > 0) {
         ArrayList<WorldCoordinates> sourceList2 = new ArrayList<>();

         label74:
         for (WorldCoordinates wc : sourceList) {
            double dist = wc.getDistanceSquaredToWorldCoordinates(tileLoc);
            if (!sourceList2.isEmpty()) {
               for (int a = 0; a < sourceList2.size(); a++) {
                  double d2 = sourceList2.get(a).getDistanceSquaredToWorldCoordinates(tileLoc);
                  if (dist < d2) {
                     sourceList2.add(a, wc);
                     continue label74;
                  }
               }
            }

            sourceList2.add(wc);
         }

         sources.put(tileLoc, sourceList2);
      } else {
         sourcesDelay.put(tileLoc, System.currentTimeMillis() + 10000L);
      }
   }

   public static void refreshSources(TileEntity tile) {
      sources.remove(new WorldCoordinates(tile.func_174877_v(), tile.func_145831_w().field_73011_w.getDimension()));
   }

   public static class EssentiaSourceFX {
      public BlockPos start;
      public BlockPos end;
      public int color;
      public int ext;

      public EssentiaSourceFX(BlockPos start, BlockPos end, int color, int ext) {
         this.start = start;
         this.end = end;
         this.color = color;
         this.ext = ext;
      }
   }
}
