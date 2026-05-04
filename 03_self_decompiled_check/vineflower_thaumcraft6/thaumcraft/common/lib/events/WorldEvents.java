package thaumcraft.common.lib.events;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.NoteBlockEvent.Play;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.tiles.devices.TileArcaneEar;
import thaumcraft.common.world.aura.AuraHandler;

@EventBusSubscriber
public class WorldEvents {
   public static WorldEvents INSTANCE = new WorldEvents();

   @SubscribeEvent
   public static void worldLoad(Load event) {
      if (!event.getWorld().field_72995_K) {
         AuraHandler.addAuraWorld(event.getWorld().field_73011_w.getDimension());
      }
   }

   @SubscribeEvent
   public static void worldSave(Save event) {
      if (!event.getWorld().field_72995_K) {
      }
   }

   @SubscribeEvent
   public static void worldUnload(Unload event) {
      if (!event.getWorld().field_72995_K) {
         SealHandler.sealEntities.remove(event.getWorld().field_73011_w.getDimension());
         AuraHandler.removeAuraWorld(event.getWorld().field_73011_w.getDimension());
      }
   }

   @SubscribeEvent
   public static void placeBlockEvent(PlaceEvent event) {
      if (isNearActiveBoss(event.getWorld(), event.getPlayer(), event.getPos().func_177958_n(), event.getPos().func_177956_o(), event.getPos().func_177952_p())
         )
       {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void placeBlockEvent(MultiPlaceEvent event) {
      if (isNearActiveBoss(event.getWorld(), event.getPlayer(), event.getPos().func_177958_n(), event.getPos().func_177956_o(), event.getPos().func_177952_p())
         )
       {
         event.setCanceled(true);
      }
   }

   private static boolean isNearActiveBoss(World world, EntityPlayer player, int x, int y, int z) {
      return false;
   }

   @SubscribeEvent
   public static void noteEvent(Play event) {
      if (!event.getWorld().field_72995_K) {
         if (!TileArcaneEar.noteBlockEvents.containsKey(event.getWorld().field_73011_w.getDimension())) {
            TileArcaneEar.noteBlockEvents.put(event.getWorld().field_73011_w.getDimension(), new ArrayList<>());
         }

         ArrayList<Integer[]> list = TileArcaneEar.noteBlockEvents.get(event.getWorld().field_73011_w.getDimension());
         list.add(
            new Integer[]{
               event.getPos().func_177958_n(),
               event.getPos().func_177956_o(),
               event.getPos().func_177952_p(),
               event.getInstrument().ordinal(),
               event.getVanillaNoteId()
            }
         );
         TileArcaneEar.noteBlockEvents.put(event.getWorld().field_73011_w.getDimension(), list);
      }
   }
}
