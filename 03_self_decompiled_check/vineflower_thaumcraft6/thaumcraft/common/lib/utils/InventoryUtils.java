package thaumcraft.common.lib.utils;

import java.util.List;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IngredientNBT;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.crafting.IngredientNBTTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.EntityFollowingItem;

public class InventoryUtils {
   public static ItemStack copyMaxedStack(ItemStack stack) {
      return copyLimitedStack(stack, stack.func_77976_d());
   }

   public static ItemStack copyLimitedStack(ItemStack stack, int limit) {
      if (stack == null) {
         return ItemStack.field_190927_a;
      }

      ItemStack s = stack.func_77946_l();
      if (s.func_190916_E() > limit) {
         s.func_190920_e(limit);
      }

      return s;
   }

   public static boolean consumeItemsFromAdjacentInventoryOrPlayer(World world, BlockPos pos, EntityPlayer player, boolean sim, ItemStack... items) {
      for (ItemStack stack : items) {
         boolean b = checkAdjacentChests(world, pos, stack);
         if (!b) {
            b = isPlayerCarryingAmount(player, stack, true);
         }

         if (!b) {
            return false;
         }
      }

      if (!sim) {
         for (ItemStack stack : items) {
            if (!consumeFromAdjacentChests(world, pos, stack.func_77946_l())) {
               consumePlayerItem(player, stack, true, true);
            }
         }
      }

      return true;
   }

   public static boolean checkAdjacentChests(World world, BlockPos pos, ItemStack itemStack) {
      int c = itemStack.func_190916_E();

      for (EnumFacing face : EnumFacing.field_82609_l) {
         if (face != EnumFacing.UP) {
            c -= ThaumcraftInvHelper.countTotalItemsIn(
               world, pos.func_177972_a(face), face.func_176734_d(), itemStack.func_77946_l(), ThaumcraftInvHelper.InvFilter.BASEORE
            );
            if (c <= 0) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean consumeFromAdjacentChests(World world, BlockPos pos, ItemStack itemStack) {
      for (EnumFacing face : EnumFacing.field_82609_l) {
         if (face != EnumFacing.UP && !itemStack.func_190926_b()) {
            ItemStack os = removeStackFrom(world, pos.func_177972_a(face), face.func_176734_d(), itemStack, ThaumcraftInvHelper.InvFilter.BASEORE, false);
            itemStack.func_190920_e(itemStack.func_190916_E() - os.func_190916_E());
            if (itemStack.func_190926_b()) {
               break;
            }
         }
      }

      return itemStack.func_190926_b();
   }

   /** @deprecated */
   public static ItemStack insertStackAt(World world, BlockPos pos, EnumFacing side, ItemStack stack, boolean simulate) {
      return ThaumcraftInvHelper.insertStackAt(world, pos, side, stack, simulate);
   }

   public static void ejectStackAt(World world, BlockPos pos, EnumFacing side, ItemStack out) {
      ejectStackAt(world, pos, side, out, false);
   }

   public static ItemStack ejectStackAt(World world, BlockPos pos, EnumFacing side, ItemStack out, boolean smart) {
      out = ThaumcraftInvHelper.insertStackAt(world, pos.func_177972_a(side), side.func_176734_d(), out, false);
      if (smart && ThaumcraftInvHelper.getItemHandlerAt(world, pos.func_177972_a(side), side.func_176734_d()) != null) {
         return out;
      }

      if (!out.func_190926_b()) {
         if (world.func_175665_u(pos.func_177972_a(side))) {
            pos = pos.func_177972_a(side.func_176734_d());
         }

         EntityItem entityitem2 = new EntityItem(
            world,
            pos.func_177958_n() + 0.5 + 1 * side.func_82601_c(),
            (float)pos.func_177956_o() + 1 * side.func_96559_d(),
            pos.func_177952_p() + 0.5 + 1 * side.func_82599_e(),
            out
         );
         entityitem2.field_70159_w = 0.3 * side.func_82601_c();
         entityitem2.field_70181_x = 0.3 * side.func_96559_d();
         entityitem2.field_70179_y = 0.3 * side.func_82599_e();
         world.func_72838_d(entityitem2);
      }

      return ItemStack.field_190927_a;
   }

   public static ItemStack removeStackFrom(World world, BlockPos pos, EnumFacing side, ItemStack stack, ThaumcraftInvHelper.InvFilter filter, boolean simulate) {
      return removeStackFrom(ThaumcraftInvHelper.getItemHandlerAt(world, pos, side), stack, filter, simulate);
   }

   public static ItemStack removeStackFrom(IItemHandler inventory, ItemStack stack, ThaumcraftInvHelper.InvFilter filter, boolean simulate) {
      int amount = stack.func_190916_E();
      int removed = 0;
      if (inventory != null) {
         for (int a = 0; a < inventory.getSlots(); a++) {
            if (areItemStacksEqual(stack, inventory.getStackInSlot(a), filter)) {
               int s = Math.min(amount - removed, inventory.getStackInSlot(a).func_190916_E());
               ItemStack es = inventory.extractItem(a, s, simulate);
               if (es != null && !es.func_190926_b()) {
                  removed += es.func_190916_E();
               }
            }

            if (removed >= amount) {
               break;
            }
         }
      }

      if (removed == 0) {
         return ItemStack.field_190927_a;
      }

      ItemStack s = stack.func_77946_l();
      s.func_190920_e(removed);
      return s;
   }

   public static int countStackInWorld(World world, BlockPos pos, ItemStack stack, double range, ThaumcraftInvHelper.InvFilter filter) {
      int count = 0;

      for (EntityItem ei : EntityUtils.getEntitiesInRange(world, pos, null, EntityItem.class, range)) {
         if (ei.func_92059_d() != null && ei.func_92059_d().func_190926_b() && areItemStacksEqual(stack, ei.func_92059_d(), filter)) {
            count += ei.func_92059_d().func_190916_E();
         }
      }

      return count;
   }

   public static void dropItems(World world, BlockPos pos) {
      TileEntity tileEntity = world.func_175625_s(pos);
      if (tileEntity instanceof IInventory) {
         IInventory inventory = (IInventory)tileEntity;
         InventoryHelper.func_180175_a(world, pos, inventory);
      }
   }

   public static boolean consumePlayerItem(EntityPlayer player, ItemStack item, boolean nocheck, boolean ore) {
      if (!nocheck && !isPlayerCarryingAmount(player, item, ore)) {
         return false;
      }

      int count = item.func_190916_E();

      for (int var2x = 0; var2x < player.field_71071_by.field_70462_a.size(); var2x++) {
         if (checkEnchantedPlaceholder(item, (ItemStack)player.field_71071_by.field_70462_a.get(var2x))
            || areItemStacksEqual(
               (ItemStack)player.field_71071_by.field_70462_a.get(var2x),
               item,
               new ThaumcraftInvHelper.InvFilter(false, !item.func_77942_o(), ore, false).setRelaxedNBT()
            )) {
            if (((ItemStack)player.field_71071_by.field_70462_a.get(var2x)).func_190916_E() > count) {
               ((ItemStack)player.field_71071_by.field_70462_a.get(var2x)).func_190918_g(count);
               count = 0;
            } else {
               count -= ((ItemStack)player.field_71071_by.field_70462_a.get(var2x)).func_190916_E();
               player.field_71071_by.field_70462_a.set(var2x, ItemStack.field_190927_a);
            }

            if (count <= 0) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean consumePlayerItem(EntityPlayer player, Item item, int md, int amt) {
      if (!isPlayerCarryingAmount(player, new ItemStack(item, amt, md), false)) {
         return false;
      }

      int count = amt;

      for (int var2x = 0; var2x < player.field_71071_by.field_70462_a.size(); var2x++) {
         if (((ItemStack)player.field_71071_by.field_70462_a.get(var2x)).func_77973_b() == item
            && ((ItemStack)player.field_71071_by.field_70462_a.get(var2x)).func_77952_i() == md) {
            if (((ItemStack)player.field_71071_by.field_70462_a.get(var2x)).func_190916_E() > count) {
               ((ItemStack)player.field_71071_by.field_70462_a.get(var2x)).func_190918_g(count);
               count = 0;
            } else {
               count -= ((ItemStack)player.field_71071_by.field_70462_a.get(var2x)).func_190916_E();
               player.field_71071_by.field_70462_a.set(var2x, ItemStack.field_190927_a);
            }

            if (count <= 0) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean consumePlayerItem(EntityPlayer player, Item item, int md) {
      for (int var2x = 0; var2x < player.field_71071_by.field_70462_a.size(); var2x++) {
         if (((ItemStack)player.field_71071_by.field_70462_a.get(var2x)).func_77973_b() == item
            && ((ItemStack)player.field_71071_by.field_70462_a.get(var2x)).func_77952_i() == md) {
            ((ItemStack)player.field_71071_by.field_70462_a.get(var2x)).func_190918_g(1);
            if (((ItemStack)player.field_71071_by.field_70462_a.get(var2x)).func_190916_E() <= 0) {
               player.field_71071_by.field_70462_a.set(var2x, ItemStack.field_190927_a);
            }

            return true;
         }
      }

      return false;
   }

   public static boolean isPlayerCarryingAmount(EntityPlayer player, ItemStack stack, boolean ore) {
      if (stack != null && !stack.func_190926_b()) {
         int count = stack.func_190916_E();

         for (int var2x = 0; var2x < player.field_71071_by.field_70462_a.size(); var2x++) {
            if (checkEnchantedPlaceholder(stack, (ItemStack)player.field_71071_by.field_70462_a.get(var2x))
               || areItemStacksEqual(
                  (ItemStack)player.field_71071_by.field_70462_a.get(var2x),
                  stack,
                  new ThaumcraftInvHelper.InvFilter(false, !stack.func_77942_o(), ore, false).setRelaxedNBT()
               )) {
               count -= ((ItemStack)player.field_71071_by.field_70462_a.get(var2x)).func_190916_E();
               if (count <= 0) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean checkEnchantedPlaceholder(ItemStack stack, ItemStack stack2) {
      if (stack.func_77973_b() != ItemsTC.enchantedPlaceholder) {
         return false;
      }

      Map<Enchantment, Integer> en = EnchantmentHelper.func_82781_a(stack);
      boolean b = !en.isEmpty();

      for (Enchantment e : en.keySet()) {
         Map<Enchantment, Integer> en2 = EnchantmentHelper.func_82781_a(stack2);
         if (en2.isEmpty()) {
            return false;
         }

         b = false;

         for (Enchantment e2 : en2.keySet()) {
            if (e2.equals(e)) {
               b = true;
               if (en2.get(e2) < en.get(e)) {
                  b = false;
                  return b;
               }
            }
         }
      }

      return b;
   }

   public static EntityEquipmentSlot isHoldingItem(EntityPlayer player, Item item) {
      if (player != null && item != null) {
         if (player.func_184614_ca() != null && player.func_184614_ca().func_77973_b() == item) {
            return EntityEquipmentSlot.MAINHAND;
         } else {
            return player.func_184592_cb() != null && player.func_184592_cb().func_77973_b() == item ? EntityEquipmentSlot.OFFHAND : null;
         }
      } else {
         return null;
      }
   }

   public static EntityEquipmentSlot isHoldingItem(EntityPlayer player, Class item) {
      if (player == null || item == null) {
         return null;
      } else if (player.func_184614_ca() != null && item.isAssignableFrom(player.func_184614_ca().func_77973_b().getClass())) {
         return EntityEquipmentSlot.MAINHAND;
      } else {
         return player.func_184592_cb() != null && item.isAssignableFrom(player.func_184592_cb().func_77973_b().getClass())
            ? EntityEquipmentSlot.OFFHAND
            : null;
      }
   }

   public static int getPlayerSlotFor(EntityPlayer player, ItemStack stack) {
      for (int i = 0; i < player.field_71071_by.field_70462_a.size(); i++) {
         if (!((ItemStack)player.field_71071_by.field_70462_a.get(i)).func_190926_b()
            && stackEqualExact(stack, (ItemStack)player.field_71071_by.field_70462_a.get(i))) {
            return i;
         }
      }

      return -1;
   }

   public static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
      return stack1.func_77973_b() == stack2.func_77973_b()
         && (!stack1.func_77981_g() || stack1.func_77960_j() == stack2.func_77960_j())
         && ItemStack.func_77970_a(stack1, stack2);
   }

   public static boolean areItemStacksEqualStrict(ItemStack stack0, ItemStack stack1) {
      return areItemStacksEqual(stack0, stack1, ThaumcraftInvHelper.InvFilter.STRICT);
   }

   public static ItemStack findFirstMatchFromFilter(
      NonNullList<ItemStack> filterStacks, boolean blacklist, IItemHandler inv, EnumFacing face, ThaumcraftInvHelper.InvFilter filter
   ) {
      return findFirstMatchFromFilter(filterStacks, blacklist, inv, face, filter, false);
   }

   public static ItemStack findFirstMatchFromFilter(
      NonNullList<ItemStack> filterStacks, boolean blacklist, IItemHandler inv, EnumFacing face, ThaumcraftInvHelper.InvFilter filter, boolean leaveOne
   ) {
      label56:
      for (int a = 0; a < inv.getSlots(); a++) {
         ItemStack is = inv.getStackInSlot(a);
         if (is != null && !is.func_190926_b() && is.func_190916_E() > 0 && (!leaveOne || ThaumcraftInvHelper.countTotalItemsIn(inv, is, filter) >= 2)) {
            boolean allow = false;
            boolean allEmpty = true;

            for (ItemStack fs : filterStacks) {
               if (fs != null && !fs.func_190926_b()) {
                  allEmpty = false;
                  boolean r = areItemStacksEqual(fs.func_77946_l(), is.func_77946_l(), filter);
                  if (blacklist) {
                     if (r) {
                        continue label56;
                     }

                     allow = true;
                  } else if (r) {
                     return is;
                  }
               }
            }

            if (blacklist && (allow || allEmpty)) {
               return is;
            }
         }
      }

      return ItemStack.field_190927_a;
   }

   public static boolean matchesFilters(NonNullList<ItemStack> nonNullList, boolean blacklist, ItemStack is, ThaumcraftInvHelper.InvFilter filter) {
      if (is != null && !is.func_190926_b() && is.func_190916_E() > 0) {
         boolean allow = false;
         boolean allEmpty = true;

         for (ItemStack fs : nonNullList) {
            if (fs != null && !fs.func_190926_b()) {
               allEmpty = false;
               boolean r = areItemStacksEqual(fs.func_77946_l(), is.func_77946_l(), filter);
               if (blacklist) {
                  if (r) {
                     return false;
                  }

                  allow = true;
               } else if (r) {
                  return true;
               }
            }
         }

         return blacklist && (allow || allEmpty);
      } else {
         return false;
      }
   }

   public static ItemStack findFirstMatchFromFilter(
      NonNullList<ItemStack> filterStacks,
      NonNullList<Integer> filterStacksSizes,
      boolean blacklist,
      NonNullList<ItemStack> itemStacks,
      ThaumcraftInvHelper.InvFilter filter
   ) {
      return (ItemStack)findFirstMatchFromFilterTuple(filterStacks, filterStacksSizes, blacklist, itemStacks, filter).func_76341_a();
   }

   public static Tuple<ItemStack, Integer> findFirstMatchFromFilterTuple(
      NonNullList<ItemStack> filterStacks,
      NonNullList<Integer> filterStacksSizes,
      boolean blacklist,
      NonNullList<ItemStack> stacks,
      ThaumcraftInvHelper.InvFilter filter
   ) {
      label63:
      for (ItemStack is : stacks) {
         if (is != null && !is.func_190926_b() && is.func_190916_E() > 0) {
            boolean allow = false;
            boolean allEmpty = true;

            for (int idx = 0; idx < filterStacks.size(); idx++) {
               ItemStack fs = (ItemStack)filterStacks.get(idx);
               if (fs != null && !fs.func_190926_b()) {
                  allEmpty = false;
                  boolean r = areItemStacksEqual(fs.func_77946_l(), is.func_77946_l(), filter);
                  if (blacklist) {
                     if (r) {
                        continue label63;
                     }

                     allow = true;
                  } else if (r) {
                     return new Tuple(is, filterStacksSizes.get(idx));
                  }
               }
            }

            if (blacklist && (allow || allEmpty)) {
               return new Tuple(is, 0);
            }
         }
      }

      return new Tuple(ItemStack.field_190927_a, 0);
   }

   public static boolean areItemStacksEqual(ItemStack stack0, ItemStack stack1, ThaumcraftInvHelper.InvFilter filter) {
      if (stack0 == null && stack1 != null) {
         return false;
      }

      if (stack0 != null && stack1 == null) {
         return false;
      }

      if (stack0 == null && stack1 == null) {
         return true;
      }

      if (stack0.func_190926_b() && !stack1.func_190926_b()) {
         return false;
      }

      if (!stack0.func_190926_b() && stack1.func_190926_b()) {
         return false;
      }

      if (stack0.func_190926_b() && stack1.func_190926_b()) {
         return true;
      }

      if (filter.useMod) {
         String m1 = "A";
         String m2 = "B";
         String a = stack0.func_77973_b().getRegistryName().func_110624_b();
         if (a != null) {
            m1 = a;
         }

         String b = stack1.func_77973_b().getRegistryName().func_110624_b();
         if (b != null) {
            m2 = b;
         }

         return m1.equals(m2);
      } else {
         if (filter.useOre && !stack0.func_190926_b()) {
            int[] od = OreDictionary.getOreIDs(stack0);

            for (int i : od) {
               if (ThaumcraftInvHelper.containsMatch(false, new ItemStack[]{stack1}, OreDictionary.getOres(OreDictionary.getOreName(i), false))) {
                  return true;
               }
            }
         }

         boolean t1 = true;
         if (!filter.igNBT) {
            t1 = filter.relaxedNBT ? ThaumcraftInvHelper.areItemStackTagsEqualRelaxed(stack0, stack1) : ItemStack.func_77970_a(stack0, stack1);
         }

         if (stack0.func_77952_i() == 32767 || stack1.func_77952_i() == 32767) {
            filter.igDmg = true;
         }

         boolean t2 = !filter.igDmg && stack0.func_77952_i() != stack1.func_77952_i();
         return stack0.func_77973_b() != stack1.func_77973_b() ? false : (t2 ? false : t1);
      }
   }

   public static void dropHarvestsAtPos(World worldIn, BlockPos pos, List<ItemStack> list) {
      dropHarvestsAtPos(worldIn, pos, list, false, 0, null);
   }

   public static void dropHarvestsAtPos(World worldIn, BlockPos pos, List<ItemStack> list, boolean followItem, int color, Entity target) {
      for (ItemStack item : list) {
         if (!worldIn.field_72995_K && worldIn.func_82736_K().func_82766_b("doTileDrops") && !worldIn.restoringBlockSnapshots) {
            float f = 0.5F;
            double d0 = worldIn.field_73012_v.nextFloat() * f + (1.0F - f) * 0.5;
            double d1 = worldIn.field_73012_v.nextFloat() * f + (1.0F - f) * 0.5;
            double d2 = worldIn.field_73012_v.nextFloat() * f + (1.0F - f) * 0.5;
            EntityItem entityitem = null;
            if (followItem) {
               entityitem = new EntityFollowingItem(worldIn, pos.func_177958_n() + d0, pos.func_177956_o() + d1, pos.func_177952_p() + d2, item, target, color);
            } else {
               entityitem = new EntityItem(worldIn, pos.func_177958_n() + d0, pos.func_177956_o() + d1, pos.func_177952_p() + d2, item);
            }

            entityitem.func_174869_p();
            worldIn.func_72838_d(entityitem);
         }
      }
   }

   public static void dropItemAtPos(World world, ItemStack item, BlockPos pos) {
      if (!world.field_72995_K && item != null && !item.func_190926_b() && item.func_190916_E() > 0) {
         EntityItem entityItem = new EntityItem(world, pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, item.func_77946_l());
         world.func_72838_d(entityItem);
      }
   }

   public static void dropItemAtEntity(World world, ItemStack item, Entity entity) {
      if (!world.field_72995_K && item != null && !item.func_190926_b() && item.func_190916_E() > 0) {
         EntityItem entityItem = new EntityItem(
            world, entity.field_70165_t, entity.field_70163_u + entity.func_70047_e() / 2.0F, entity.field_70161_v, item.func_77946_l()
         );
         world.func_72838_d(entityItem);
      }
   }

   public static void dropItemsAtEntity(World world, BlockPos pos, Entity entity) {
      TileEntity tileEntity = world.func_175625_s(pos);
      if (tileEntity instanceof IInventory && !world.field_72995_K) {
         IInventory inventory = (IInventory)tileEntity;

         for (int i = 0; i < inventory.func_70302_i_(); i++) {
            ItemStack item = inventory.func_70301_a(i);
            if (!item.func_190926_b() && item.func_190916_E() > 0) {
               EntityItem entityItem = new EntityItem(
                  world, entity.field_70165_t, entity.field_70163_u + entity.func_70047_e() / 2.0F, entity.field_70161_v, item.func_77946_l()
               );
               world.func_72838_d(entityItem);
               inventory.func_70299_a(i, ItemStack.field_190927_a);
            }
         }
      }
   }

   public static ItemStack cycleItemStack(Object input) {
      return cycleItemStack(input, 0);
   }

   public static ItemStack cycleItemStack(Object input, int counter) {
      ItemStack it = ItemStack.field_190927_a;
      if (input instanceof Ingredient) {
         boolean b = !((Ingredient)input).isSimple() && !(input instanceof IngredientNBTTC) && !(input instanceof IngredientNBT);
         input = ((Ingredient)input).func_193365_a();
         if (b) {
            ItemStack[] q = (ItemStack[])input;
            ItemStack[] r = new ItemStack[q.length];

            for (int a = 0; a < q.length; a++) {
               r[a] = q[a].func_77946_l();
               r[a].func_77964_b(32767);
            }

            input = r;
         }
      }

      if (input instanceof ItemStack[]) {
         ItemStack[] q = (ItemStack[])input;
         if (q != null && q.length > 0) {
            int idx = (int)((counter + System.currentTimeMillis() / 1000L) % q.length);
            it = cycleItemStack(q[idx], counter++);
         }
      } else if (input instanceof ItemStack) {
         it = (ItemStack)input;
         if (it != null && !it.func_190926_b() && it.func_77973_b() != null && it.func_77984_f() && it.func_77952_i() == 32767) {
            int q = 5000 / it.func_77958_k();
            int md = (int)((counter + System.currentTimeMillis() / q) % it.func_77958_k());
            ItemStack it2 = new ItemStack(it.func_77973_b(), 1, md);
            it2.func_77982_d(it.func_77978_p());
            it = it2;
         }
      } else if (input instanceof List) {
         List<ItemStack> q = (List<ItemStack>)input;
         if (q != null && q.size() > 0) {
            int idx = (int)((counter + System.currentTimeMillis() / 1000L) % q.size());
            it = cycleItemStack(q.get(idx), counter++);
         }
      } else if (input instanceof String) {
         List<ItemStack> q = OreDictionary.getOres((String)input, false);
         if (q != null && q.size() > 0) {
            int idx = (int)((counter + System.currentTimeMillis() / 1000L) % q.size());
            it = cycleItemStack(q.get(idx), counter++);
         }
      }

      return it;
   }
}
