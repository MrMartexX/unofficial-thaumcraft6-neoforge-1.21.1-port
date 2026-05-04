package thaumcraft.common.lib.research;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.IScanThing;
import thaumcraft.common.lib.utils.InventoryUtils;

public class ScanSky implements IScanThing {
   @Override
   public boolean checkThing(EntityPlayer player, Object obj) {
      if (obj == null
         && !(player.field_70125_A > 0.0F)
         && player.field_70170_p.func_175678_i(player.func_180425_c().func_177984_a())
         && player.field_70170_p.field_73011_w.func_186058_p() == DimensionType.OVERWORLD
         && ThaumcraftCapabilities.knowsResearchStrict(player, "CELESTIALSCANNING")) {
         int yaw = (int)(player.field_70177_z + 90.0F) % 360;
         int pitch = (int)Math.abs(player.field_70125_A);
         int ca = (int)((player.field_70170_p.func_72826_c(0.0F) + 0.25) * 360.0) % 360;
         boolean night = ca > 180;
         boolean inRangeYaw = false;
         boolean inRangePitch = false;
         if (night) {
            ca -= 180;
         }

         if (ca > 90) {
            inRangeYaw = Math.abs(Math.abs(yaw) - 180) < 10;
            inRangePitch = Math.abs(180 - ca - pitch) < 7;
         } else {
            inRangeYaw = Math.abs(yaw) < 10;
            inRangePitch = Math.abs(ca - pitch) < 7;
         }

         return inRangeYaw && inRangePitch ? true : night;
      } else {
         return false;
      }
   }

   @Override
   public void onSuccess(EntityPlayer player, Object object) {
      if (object == null
         && !(player.field_70125_A > 0.0F)
         && player.field_70170_p.func_175678_i(player.func_180425_c().func_177984_a())
         && ThaumcraftCapabilities.knowsResearchStrict(player, "CELESTIALSCANNING")) {
         int yaw = (int)(player.field_70177_z + 90.0F) % 360;
         int pitch = (int)Math.abs(player.field_70125_A);
         int ca = (int)((player.field_70170_p.func_72826_c(0.0F) + 0.25) * 360.0) % 360;
         boolean night = ca > 180;
         boolean inRangeYaw = false;
         boolean inRangePitch = false;
         if (night) {
            ca -= 180;
         }

         if (ca > 90) {
            inRangeYaw = Math.abs(Math.abs(yaw) - 180) < 10;
            inRangePitch = Math.abs(180 - ca - pitch) < 7;
         } else {
            inRangeYaw = Math.abs(yaw) < 10;
            inRangePitch = Math.abs(ca - pitch) < 7;
         }

         int worldDay = (int)(player.field_70170_p.func_82737_E() / 24000L);
         if (inRangeYaw && inRangePitch) {
            String pk = "CEL_" + worldDay + "_";
            String key = pk + (night ? "Moon" + player.field_70170_p.field_73011_w.func_76559_b(player.field_70170_p.func_72820_D()) : "Sun");
            if (ThaumcraftCapabilities.knowsResearch(player, key)) {
               player.func_146105_b(new TextComponentTranslation("tc.celestial.fail.1", new Object[]{""}), true);
            } else {
               if (InventoryUtils.isPlayerCarryingAmount(player, new ItemStack(ItemsTC.scribingTools, 1, 32767), true)
                  && InventoryUtils.consumePlayerItem(player, new ItemStack(Items.field_151121_aF), false, true)) {
                  ItemStack stack = new ItemStack(
                     ItemsTC.celestialNotes, 1, night ? 5 + player.field_70170_p.field_73011_w.func_76559_b(player.field_70170_p.func_72820_D()) : 0
                  );
                  if (!player.field_71071_by.func_70441_a(stack)) {
                     player.func_71019_a(stack, false);
                  }

                  ThaumcraftApi.internalMethods.progressResearch(player, key);
               } else {
                  player.func_146105_b(new TextComponentTranslation("tc.celestial.fail.2", new Object[]{""}), true);
               }

               this.cleanResearch(player, pk);
            }
         } else if (night) {
            EnumFacing face = player.func_184172_bi();
            int num = face.func_176745_a() - 2;
            String pk = "CEL_" + worldDay + "_";
            String key = pk + "Star" + num;
            if (ThaumcraftCapabilities.knowsResearch(player, key)) {
               player.func_146105_b(new TextComponentTranslation("tc.celestial.fail.1", new Object[]{""}), true);
            } else {
               if (InventoryUtils.isPlayerCarryingAmount(player, new ItemStack(ItemsTC.scribingTools, 1, 32767), true)
                  && InventoryUtils.consumePlayerItem(player, new ItemStack(Items.field_151121_aF), false, true)) {
                  ItemStack stack = new ItemStack(ItemsTC.celestialNotes, 1, 1 + num);
                  if (!player.field_71071_by.func_70441_a(stack)) {
                     player.func_71019_a(stack, false);
                  }

                  ThaumcraftApi.internalMethods.progressResearch(player, key);
               } else {
                  player.func_146105_b(new TextComponentTranslation("tc.celestial.fail.2", new Object[]{""}), true);
               }

               this.cleanResearch(player, pk);
            }
         }
      }
   }

   private void cleanResearch(EntityPlayer player, String pk) {
      ArrayList<String> list = new ArrayList<>();

      for (String key : ThaumcraftCapabilities.getKnowledge(player).getResearchList()) {
         if (key.startsWith("CEL_") && !key.startsWith(pk)) {
            list.add(key);
         }
      }

      for (String key : list) {
         ThaumcraftCapabilities.getKnowledge(player).removeResearch(key);
      }

      ResearchManager.syncList.put(player.func_70005_c_(), true);
   }

   @Override
   public String getResearchKey(EntityPlayer player, Object object) {
      return "";
   }
}
