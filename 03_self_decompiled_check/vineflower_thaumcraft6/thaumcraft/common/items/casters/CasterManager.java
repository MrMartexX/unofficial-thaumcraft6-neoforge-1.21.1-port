package thaumcraft.common.items.casters;

import baubles.api.BaublesApi;
import java.util.HashMap;
import java.util.TreeMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;

public class CasterManager {
   static HashMap<Integer, Long> cooldownServer = new HashMap<>();
   static HashMap<Integer, Long> cooldownClient = new HashMap<>();

   public static float getTotalVisDiscount(EntityPlayer player) {
      int total = 0;
      if (player == null) {
         return 0.0F;
      }

      IInventory baubles = BaublesApi.getBaubles(player);

      for (int a = 0; a < baubles.func_70302_i_(); a++) {
         if (baubles.func_70301_a(a) != null && baubles.func_70301_a(a).func_77973_b() instanceof IVisDiscountGear) {
            total += ((IVisDiscountGear)baubles.func_70301_a(a).func_77973_b()).getVisDiscount(baubles.func_70301_a(a), player);
         }
      }

      for (int a = 0; a < 4; a++) {
         if (((ItemStack)player.field_71071_by.field_70460_b.get(a)).func_77973_b() instanceof IVisDiscountGear) {
            total += ((IVisDiscountGear)((ItemStack)player.field_71071_by.field_70460_b.get(a)).func_77973_b())
               .getVisDiscount((ItemStack)player.field_71071_by.field_70460_b.get(a), player);
         }
      }

      if (player.func_70644_a(PotionVisExhaust.instance) || player.func_70644_a(PotionInfectiousVisExhaust.instance)) {
         int level1 = 0;
         int level2 = 0;
         if (player.func_70644_a(PotionVisExhaust.instance)) {
            level1 = player.func_70660_b(PotionVisExhaust.instance).func_76458_c();
         }

         if (player.func_70644_a(PotionInfectiousVisExhaust.instance)) {
            level2 = player.func_70660_b(PotionInfectiousVisExhaust.instance).func_76458_c();
         }

         total -= (Math.max(level1, level2) + 1) * 10;
      }

      return total / 100.0F;
   }

   public static boolean consumeVisFromInventory(EntityPlayer player, float cost) {
      for (int a = player.field_71071_by.field_70462_a.size() - 1; a >= 0; a--) {
         ItemStack item = (ItemStack)player.field_71071_by.field_70462_a.get(a);
         if (item.func_77973_b() instanceof ICaster) {
            boolean done = ((ICaster)item.func_77973_b()).consumeVis(item, player, cost, true, false);
            if (done) {
               return true;
            }
         }
      }

      return false;
   }

   public static void changeFocus(ItemStack is, World w, EntityPlayer player, String focus) {
      ICaster wand = (ICaster)is.func_77973_b();
      TreeMap<String, Integer> foci = new TreeMap<>();
      HashMap<Integer, Integer> pouches = new HashMap<>();
      int pouchcount = 0;
      ItemStack item = ItemStack.field_190927_a;
      IInventory baubles = BaublesApi.getBaubles(player);

      for (int a = 0; a < baubles.func_70302_i_(); a++) {
         if (baubles.func_70301_a(a).func_77973_b() instanceof ItemFocusPouch) {
            pouchcount++;
            item = baubles.func_70301_a(a);
            pouches.put(pouchcount, a - 4);
            NonNullList<ItemStack> inv = ((ItemFocusPouch)item.func_77973_b()).getInventory(item);

            for (int q = 0; q < inv.size(); q++) {
               item = (ItemStack)inv.get(q);
               if (item.func_77973_b() instanceof ItemFocus) {
                  String sh = ((ItemFocus)item.func_77973_b()).getSortingHelper(item);
                  if (sh != null) {
                     foci.put(sh, q + pouchcount * 1000);
                  }
               }
            }
         }
      }

      for (int a = 0; a < 36; a++) {
         item = (ItemStack)player.field_71071_by.field_70462_a.get(a);
         if (item.func_77973_b() instanceof ItemFocus) {
            String sh = ((ItemFocus)item.func_77973_b()).getSortingHelper(item);
            if (sh == null) {
               continue;
            }

            foci.put(sh, a);
         }

         if (item.func_77973_b() instanceof ItemFocusPouch) {
            pouches.put(++pouchcount, a);
            NonNullList<ItemStack> inv = ((ItemFocusPouch)item.func_77973_b()).getInventory(item);

            for (int q = 0; q < inv.size(); q++) {
               item = (ItemStack)inv.get(q);
               if (item.func_77973_b() instanceof ItemFocus) {
                  String sh = ((ItemFocus)item.func_77973_b()).getSortingHelper(item);
                  if (sh != null) {
                     foci.put(sh, q + pouchcount * 1000);
                  }
               }
            }
         }
      }

      if (!focus.equals("REMOVE") && foci.size() != 0) {
         if (foci != null && foci.size() > 0 && focus != null) {
            String newkey = focus;
            if (foci.get(newkey) == null) {
               newkey = foci.higherKey(newkey);
            }

            if (newkey == null || foci.get(newkey) == null) {
               newkey = foci.firstKey();
            }

            if (foci.get(newkey) < 1000 && foci.get(newkey) >= 0) {
               item = ((ItemStack)player.field_71071_by.field_70462_a.get(foci.get(newkey))).func_77946_l();
            } else {
               int pid = foci.get(newkey) / 1000;
               if (pouches.containsKey(pid)) {
                  int pouchslot = pouches.get(pid);
                  int focusslot = foci.get(newkey) - pid * 1000;
                  ItemStack tmp = ItemStack.field_190927_a;
                  if (pouchslot >= 0) {
                     tmp = ((ItemStack)player.field_71071_by.field_70462_a.get(pouchslot)).func_77946_l();
                  } else {
                     tmp = baubles.func_70301_a(pouchslot + 4).func_77946_l();
                  }

                  item = fetchFocusFromPouch(player, focusslot, tmp, pouchslot);
               }
            }

            if (item == null || item.func_190926_b()) {
               return;
            }

            if (foci.get(newkey) < 1000 && foci.get(newkey) >= 0) {
               player.field_71071_by.func_70299_a(foci.get(newkey), ItemStack.field_190927_a);
            }

            player.func_184185_a(SoundsTC.ticks, 0.3F, 1.0F);
            if (wand.getFocus(is) != null
               && (
                  addFocusToPouch(player, wand.getFocusStack(is).func_77946_l(), pouches)
                     || player.field_71071_by.func_70441_a(wand.getFocusStack(is).func_77946_l())
               )) {
               wand.setFocus(is, ItemStack.field_190927_a);
            }

            if (wand.getFocus(is) == null) {
               wand.setFocus(is, item);
            } else if (!addFocusToPouch(player, item, pouches)) {
               player.field_71071_by.func_70441_a(item);
            }
         }
      } else {
         if (wand.getFocus(is) != null
            && (
               addFocusToPouch(player, wand.getFocusStack(is).func_77946_l(), pouches)
                  || player.field_71071_by.func_70441_a(wand.getFocusStack(is).func_77946_l())
            )) {
            wand.setFocus(is, null);
            player.func_184185_a(SoundsTC.ticks, 0.3F, 0.9F);
         }
      }
   }

   private static ItemStack fetchFocusFromPouch(EntityPlayer player, int focusid, ItemStack pouch, int pouchslot) {
      ItemStack focus = ItemStack.field_190927_a;
      NonNullList<ItemStack> inv = ((ItemFocusPouch)pouch.func_77973_b()).getInventory(pouch);
      ItemStack contents = (ItemStack)inv.get(focusid);
      if (contents.func_77973_b() instanceof ItemFocus) {
         focus = contents.func_77946_l();
         inv.set(focusid, ItemStack.field_190927_a);
         ((ItemFocusPouch)pouch.func_77973_b()).setInventory(pouch, inv);
         if (pouchslot >= 0) {
            player.field_71071_by.func_70299_a(pouchslot, pouch);
            player.field_71071_by.func_70296_d();
         } else {
            IInventory baubles = BaublesApi.getBaubles(player);
            baubles.func_70299_a(pouchslot + 4, pouch);
            BaublesApi.getBaublesHandler(player).setChanged(pouchslot + 4, true);
            baubles.func_70296_d();
         }
      }

      return focus;
   }

   private static boolean addFocusToPouch(EntityPlayer player, ItemStack focus, HashMap<Integer, Integer> pouches) {
      IInventory baubles = BaublesApi.getBaubles(player);

      for (Integer pouchslot : pouches.values()) {
         ItemStack pouch;
         if (pouchslot >= 0) {
            pouch = (ItemStack)player.field_71071_by.field_70462_a.get(pouchslot);
         } else {
            pouch = baubles.func_70301_a(pouchslot + 4);
         }

         NonNullList<ItemStack> inv = ((ItemFocusPouch)pouch.func_77973_b()).getInventory(pouch);

         for (int q = 0; q < inv.size(); q++) {
            ItemStack contents = (ItemStack)inv.get(q);
            if (contents.func_190926_b()) {
               inv.set(q, focus.func_77946_l());
               ((ItemFocusPouch)pouch.func_77973_b()).setInventory(pouch, inv);
               if (pouchslot >= 0) {
                  player.field_71071_by.func_70299_a(pouchslot, pouch);
                  player.field_71071_by.func_70296_d();
               } else {
                  baubles.func_70299_a(pouchslot + 4, pouch);
                  BaublesApi.getBaublesHandler(player).setChanged(pouchslot + 4, true);
                  baubles.func_70296_d();
               }

               return true;
            }
         }
      }

      return false;
   }

   public static void toggleMisc(ItemStack itemstack, World world, EntityPlayer player, int mod) {
      if (itemstack.func_77973_b() instanceof ICaster) {
         ICaster caster = (ICaster)itemstack.func_77973_b();
         ItemFocus focus = (ItemFocus)caster.getFocus(itemstack);
         FocusPackage fp = ItemFocus.getPackage(caster.getFocusStack(itemstack));
         if (fp != null && FocusEngine.doesPackageContainElement(fp, "thaumcraft.PLAN")) {
            int dim = getAreaDim(itemstack);
            if (mod == 0) {
               int areax = getAreaX(itemstack);
               int areay = getAreaY(itemstack);
               int areaz = getAreaZ(itemstack);
               int max = getAreaSize(itemstack);
               if (dim == 0) {
                  areax++;
                  areaz++;
                  areay++;
               } else if (dim == 1) {
                  areax++;
               } else if (dim == 2) {
                  areaz++;
               } else if (dim == 3) {
                  areay++;
               }

               if (areax > max) {
                  areax = 0;
               }

               if (areaz > max) {
                  areaz = 0;
               }

               if (areay > max) {
                  areay = 0;
               }

               setAreaX(itemstack, areax);
               setAreaY(itemstack, areay);
               setAreaZ(itemstack, areaz);
            }

            if (mod == 1) {
               if (++dim > 3) {
                  dim = 0;
               }

               setAreaDim(itemstack, dim);
            }
         }
      }
   }

   private static int getAreaSize(ItemStack itemstack) {
      boolean pot = false;
      if (!(itemstack.func_77973_b() instanceof ICaster)) {
         return 0;
      }

      ICaster caster = (ICaster)itemstack.func_77973_b();
      ItemFocus focus = (ItemFocus)caster.getFocus(itemstack);
      return pot ? 6 : 3;
   }

   public static int getAreaDim(ItemStack stack) {
      return stack.func_77942_o() && stack.func_77978_p().func_74764_b("aread") ? stack.func_77978_p().func_74762_e("aread") : 0;
   }

   public static int getAreaX(ItemStack stack) {
      ICaster wand = (ICaster)stack.func_77973_b();
      if (stack.func_77942_o() && stack.func_77978_p().func_74764_b("areax")) {
         int a = stack.func_77978_p().func_74762_e("areax");
         if (a > getAreaSize(stack)) {
            a = getAreaSize(stack);
         }

         return a;
      } else {
         return getAreaSize(stack);
      }
   }

   public static int getAreaY(ItemStack stack) {
      ICaster wand = (ICaster)stack.func_77973_b();
      if (stack.func_77942_o() && stack.func_77978_p().func_74764_b("areay")) {
         int a = stack.func_77978_p().func_74762_e("areay");
         if (a > getAreaSize(stack)) {
            a = getAreaSize(stack);
         }

         return a;
      } else {
         return getAreaSize(stack);
      }
   }

   public static int getAreaZ(ItemStack stack) {
      ICaster wand = (ICaster)stack.func_77973_b();
      if (stack.func_77942_o() && stack.func_77978_p().func_74764_b("areaz")) {
         int a = stack.func_77978_p().func_74762_e("areaz");
         if (a > getAreaSize(stack)) {
            a = getAreaSize(stack);
         }

         return a;
      } else {
         return getAreaSize(stack);
      }
   }

   public static void setAreaX(ItemStack stack, int area) {
      if (stack.func_77942_o()) {
         stack.func_77978_p().func_74768_a("areax", area);
      }
   }

   public static void setAreaY(ItemStack stack, int area) {
      if (stack.func_77942_o()) {
         stack.func_77978_p().func_74768_a("areay", area);
      }
   }

   public static void setAreaZ(ItemStack stack, int area) {
      if (stack.func_77942_o()) {
         stack.func_77978_p().func_74768_a("areaz", area);
      }
   }

   public static void setAreaDim(ItemStack stack, int dim) {
      if (stack.func_77942_o()) {
         stack.func_77978_p().func_74768_a("aread", dim);
      }
   }

   static boolean isOnCooldown(EntityLivingBase entityLiving) {
      if (entityLiving.field_70170_p.field_72995_K && cooldownClient.containsKey(entityLiving.func_145782_y())) {
         return cooldownClient.get(entityLiving.func_145782_y()) > System.currentTimeMillis();
      } else {
         return !entityLiving.field_70170_p.field_72995_K && cooldownServer.containsKey(entityLiving.func_145782_y())
            ? cooldownServer.get(entityLiving.func_145782_y()) > System.currentTimeMillis()
            : false;
      }
   }

   public static float getCooldown(EntityLivingBase entityLiving) {
      return entityLiving.field_70170_p.field_72995_K && cooldownClient.containsKey(entityLiving.func_145782_y())
         ? (float)(cooldownClient.get(entityLiving.func_145782_y()) - System.currentTimeMillis()) / 1000.0F
         : 0.0F;
   }

   public static void setCooldown(EntityLivingBase entityLiving, int cd) {
      if (cd == 0) {
         cooldownClient.remove(entityLiving.func_145782_y());
         cooldownServer.remove(entityLiving.func_145782_y());
      } else if (entityLiving.field_70170_p.field_72995_K) {
         cooldownClient.put(entityLiving.func_145782_y(), System.currentTimeMillis() + cd * 50);
      } else {
         cooldownServer.put(entityLiving.func_145782_y(), System.currentTimeMillis() + cd * 50);
      }
   }
}
