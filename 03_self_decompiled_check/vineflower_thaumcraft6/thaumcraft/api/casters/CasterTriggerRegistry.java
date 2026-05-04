package thaumcraft.api.casters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CasterTriggerRegistry {
   private static HashMap<String, LinkedHashMap<IBlockState, List<CasterTriggerRegistry.Trigger>>> triggers = new HashMap<>();
   private static final String DEFAULT = "default";

   public static void registerWandBlockTrigger(ICasterTriggerManager manager, int event, IBlockState state, String modid) {
      if (!triggers.containsKey(modid)) {
         triggers.put(modid, new LinkedHashMap<>());
      }

      LinkedHashMap<IBlockState, List<CasterTriggerRegistry.Trigger>> temp = triggers.get(modid);
      List<CasterTriggerRegistry.Trigger> ts = temp.get(state);
      if (ts == null) {
         ts = new ArrayList<>();
      }

      ts.add(new CasterTriggerRegistry.Trigger(manager, event));
      temp.put(state, ts);
      triggers.put(modid, temp);
   }

   public static void registerCasterBlockTrigger(ICasterTriggerManager manager, int event, IBlockState state) {
      registerWandBlockTrigger(manager, event, state, "default");
   }

   public static boolean hasTrigger(IBlockState state) {
      for (String modid : triggers.keySet()) {
         LinkedHashMap<IBlockState, List<CasterTriggerRegistry.Trigger>> temp = triggers.get(modid);
         if (temp.containsKey(state)) {
            return true;
         }
      }

      return false;
   }

   public static boolean hasTrigger(IBlockState state, String modid) {
      if (!triggers.containsKey(modid)) {
         return false;
      }

      LinkedHashMap<IBlockState, List<CasterTriggerRegistry.Trigger>> temp = triggers.get(modid);
      return temp.containsKey(state);
   }

   public static boolean performTrigger(World world, ItemStack casterStack, EntityPlayer player, BlockPos pos, EnumFacing side, IBlockState state) {
      for (String modid : triggers.keySet()) {
         LinkedHashMap<IBlockState, List<CasterTriggerRegistry.Trigger>> temp = triggers.get(modid);
         List<CasterTriggerRegistry.Trigger> l = temp.get(state);
         if (l != null && l.size() != 0) {
            for (CasterTriggerRegistry.Trigger trig : l) {
               boolean result = trig.manager.performTrigger(world, casterStack, player, pos, side, trig.event);
               if (result) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static boolean performTrigger(World world, ItemStack casterStack, EntityPlayer player, BlockPos pos, EnumFacing side, IBlockState state, String modid) {
      if (!triggers.containsKey(modid)) {
         return false;
      }

      LinkedHashMap<IBlockState, List<CasterTriggerRegistry.Trigger>> temp = triggers.get(modid);
      List<CasterTriggerRegistry.Trigger> l = temp.get(state);
      if (l != null && l.size() != 0) {
         for (CasterTriggerRegistry.Trigger trig : l) {
            boolean result = trig.manager.performTrigger(world, casterStack, player, pos, side, trig.event);
            if (result) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static class Trigger {
      ICasterTriggerManager manager;
      int event;

      public Trigger(ICasterTriggerManager manager, int event) {
         this.manager = manager;
         this.event = event;
      }
   }
}
