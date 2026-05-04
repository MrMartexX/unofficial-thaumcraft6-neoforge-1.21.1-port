package thaumcraft.api.research;

import java.util.ArrayList;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

public class ScanningManager {
   static ArrayList<IScanThing> things = new ArrayList<>();

   public static void addScannableThing(IScanThing obj) {
      things.add(obj);
   }

   public static void scanTheThing(EntityPlayer player, Object object) {
      boolean found = false;
      boolean suppress = false;

      for (IScanThing thing : things) {
         if (thing.checkThing(player, object)
            && (
               thing.getResearchKey(player, object) == null
                  || thing.getResearchKey(player, object).isEmpty()
                  || ThaumcraftApi.internalMethods.progressResearch(player, thing.getResearchKey(player, object))
            )) {
            if (thing.getResearchKey(player, object) == null || thing.getResearchKey(player, object).isEmpty()) {
               suppress = true;
            }

            found = true;
            thing.onSuccess(player, object);
         }
      }

      if (!suppress) {
         if (!found) {
            player.func_146105_b(new TextComponentString("§5§o" + I18n.func_74838_a("tc.unknownobject")), true);
         } else {
            player.func_146105_b(new TextComponentString("§a§o" + I18n.func_74838_a("tc.knownobject")), true);
         }
      }

      if (object instanceof BlockPos) {
         IItemHandler handler = ThaumcraftInvHelper.getItemHandlerAt(player.func_130014_f_(), (BlockPos)object, EnumFacing.UP);
         if (handler != null) {
            int scanned = 0;

            for (int slot = 0; slot < handler.getSlots(); slot++) {
               ItemStack stack = handler.getStackInSlot(slot);
               if (stack != null && !stack.func_190926_b()) {
                  scanTheThing(player, stack);
                  scanned++;
               }

               if (scanned >= 100) {
                  player.func_146105_b(new TextComponentString("§5§o" + I18n.func_74838_a("tc.invtoolarge")), true);
                  break;
               }
            }
         }
      }
   }

   public static boolean isThingStillScannable(EntityPlayer player, Object object) {
      for (IScanThing thing : things) {
         if (thing.checkThing(player, object)) {
            try {
               if (!ThaumcraftCapabilities.knowsResearch(player, thing.getResearchKey(player, object))) {
                  return true;
               }
            } catch (Exception var5) {
            }
         }
      }

      return false;
   }

   public static ItemStack getItemFromParms(EntityPlayer player, Object obj) {
      ItemStack is = ItemStack.field_190927_a;
      if (obj instanceof ItemStack) {
         is = (ItemStack)obj;
      }

      if (obj instanceof EntityItem && ((EntityItem)obj).func_92059_d() != null) {
         is = ((EntityItem)obj).func_92059_d();
      }

      if (obj instanceof BlockPos) {
         IBlockState state = player.field_70170_p.func_180495_p((BlockPos)obj);
         is = state.func_177230_c().func_185473_a(player.field_70170_p, (BlockPos)obj, state);

         try {
            if (is == null || is.func_190926_b()) {
               is = state.func_177230_c().getPickBlock(state, rayTrace(player), player.field_70170_p, (BlockPos)obj, player);
            }
         } catch (Exception var6) {
         }

         try {
            if ((is == null || is.func_190926_b()) && state.func_185904_a() == Material.field_151586_h) {
               is = new ItemStack(Items.field_151131_as);
            }

            if ((is == null || is.func_190926_b()) && state.func_185904_a() == Material.field_151587_i) {
               is = new ItemStack(Items.field_151129_at);
            }
         } catch (Exception var5) {
         }
      }

      return is;
   }

   private static RayTraceResult rayTrace(EntityPlayer player) {
      Vec3d vec3d = player.func_174824_e(0.0F);
      Vec3d vec3d1 = player.func_70676_i(0.0F);
      Vec3d vec3d2 = vec3d.func_72441_c(vec3d1.field_72450_a * 4.0, vec3d1.field_72448_b * 4.0, vec3d1.field_72449_c * 4.0);
      return player.field_70170_p.func_147447_a(vec3d, vec3d2, true, false, true);
   }
}
