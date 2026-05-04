package thaumcraft.api;

import java.util.List;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;
import thaumcraft.common.lib.utils.InventoryUtils;

public class ThaumcraftInvHelper {
   public static IItemHandler getItemHandlerAt(World world, BlockPos pos, EnumFacing side) {
      Pair<IItemHandler, Object> dest = VanillaInventoryCodeHooks.getItemHandler(world, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), side);
      if (dest != null && dest.getLeft() != null) {
         return (IItemHandler)dest.getLeft();
      }

      TileEntity tileentity = world.func_175625_s(pos);
      return tileentity != null && tileentity instanceof IInventory ? wrapInventory((IInventory)tileentity, side) : null;
   }

   public static IItemHandler wrapInventory(IInventory inventory, EnumFacing side) {
      return (IItemHandler)(inventory instanceof ISidedInventory ? new SidedInvWrapper((ISidedInventory)inventory, side) : new InvWrapper(inventory));
   }

   public static boolean areItemStackTagsEqualRelaxed(ItemStack prime, ItemStack other) {
      if (prime.func_190926_b() && other.func_190926_b()) {
         return true;
      } else {
         return !prime.func_190926_b() && !other.func_190926_b()
            ? prime.func_77978_p() == null || compareTagsRelaxed(prime.func_77978_p(), other.func_77978_p())
            : false;
      }
   }

   public static boolean compareTagsRelaxed(NBTTagCompound prime, NBTTagCompound other) {
      for (String key : prime.func_150296_c()) {
         if (!other.func_74764_b(key) || !prime.func_74781_a(key).equals(other.func_74781_a(key))) {
            return false;
         }
      }

      return true;
   }

   public static boolean areItemStacksEqualForCrafting(ItemStack stack0, Object in) {
      if (stack0 == null && in != null) {
         return false;
      }

      if (stack0 != null && in == null) {
         return false;
      }

      if (stack0 == null && in == null) {
         return true;
      }

      if (in instanceof Object[]) {
         return true;
      }

      if (in instanceof String) {
         List<ItemStack> l = OreDictionary.getOres((String)in, false);
         return containsMatch(false, new ItemStack[]{stack0}, l);
      }

      if (!(in instanceof ItemStack)) {
         return false;
      }

      boolean t1 = !stack0.func_77942_o() || areItemStackTagsEqualForCrafting(stack0, (ItemStack)in);
      return !t1 ? false : OreDictionary.itemMatches((ItemStack)in, stack0, false);
   }

   public static boolean containsMatch(boolean strict, ItemStack[] inputs, List<ItemStack> targets) {
      for (ItemStack input : inputs) {
         for (ItemStack target : targets) {
            if (OreDictionary.itemMatches(target, input, strict) && ItemStack.func_77970_a(target, input)) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean areItemsEqual(ItemStack s1, ItemStack s2) {
      return s1.func_77984_f() && s2.func_77984_f()
         ? s1.func_77973_b() == s2.func_77973_b()
         : s1.func_77973_b() == s2.func_77973_b() && s1.func_77952_i() == s2.func_77952_i();
   }

   public static boolean areItemStackTagsEqualForCrafting(ItemStack slotItem, ItemStack recipeItem) {
      if (recipeItem != null && slotItem != null) {
         if (recipeItem.func_77978_p() != null && slotItem.func_77978_p() == null) {
            return false;
         }

         if (recipeItem.func_77978_p() == null) {
            return true;
         }

         for (String s : recipeItem.func_77978_p().func_150296_c()) {
            if (!slotItem.func_77978_p().func_74764_b(s)) {
               return false;
            }

            if (!slotItem.func_77978_p().func_74781_a(s).toString().equals(recipeItem.func_77978_p().func_74781_a(s).toString())) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static ItemStack insertStackAt(World world, BlockPos pos, EnumFacing side, ItemStack stack, boolean simulate) {
      IItemHandler inventory = getItemHandlerAt(world, pos, side);
      return inventory != null ? ItemHandlerHelper.insertItemStacked(inventory, stack, simulate) : stack;
   }

   public static ItemStack hasRoomFor(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
      ItemStack testStack = insertStackAt(world, pos, side, stack.func_77946_l(), true);
      if (testStack.func_190926_b()) {
         return stack.func_77946_l();
      }

      testStack.func_190920_e(stack.func_190916_E() - testStack.func_190916_E());
      return testStack;
   }

   public static boolean hasRoomForSome(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
      ItemStack testStack = insertStackAt(world, pos, side, stack.func_77946_l(), true);
      return stack.func_190916_E() == 0 || testStack.func_190916_E() != stack.func_190916_E();
   }

   public static boolean hasRoomForAll(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
      return insertStackAt(world, pos, side, stack.func_77946_l(), true).func_190926_b();
   }

   public static int countTotalItemsIn(IItemHandler inventory, ItemStack stack, ThaumcraftInvHelper.InvFilter filter) {
      int count = 0;
      if (inventory != null) {
         for (int a = 0; a < inventory.getSlots(); a++) {
            if (InventoryUtils.areItemStacksEqual(stack, inventory.getStackInSlot(a), filter)) {
               count += inventory.getStackInSlot(a).func_190916_E();
            }
         }
      }

      return count;
   }

   public static int countTotalItemsIn(World world, BlockPos pos, EnumFacing side, ItemStack stack, ThaumcraftInvHelper.InvFilter filter) {
      return countTotalItemsIn(getItemHandlerAt(world, pos, side), stack, filter);
   }

   public static class InvFilter {
      public boolean igDmg;
      public boolean igNBT;
      public boolean useOre;
      public boolean useMod;
      public boolean relaxedNBT = false;
      public static final ThaumcraftInvHelper.InvFilter STRICT = new ThaumcraftInvHelper.InvFilter(false, false, false, false);
      public static final ThaumcraftInvHelper.InvFilter BASEORE = new ThaumcraftInvHelper.InvFilter(false, false, true, false);

      public InvFilter(boolean ignoreDamage, boolean ignoreNBT, boolean useOre, boolean useMod) {
         this.igDmg = ignoreDamage;
         this.igNBT = ignoreNBT;
         this.useOre = useOre;
         this.useMod = useMod;
      }

      public ThaumcraftInvHelper.InvFilter setRelaxedNBT() {
         this.relaxedNBT = true;
         return this;
      }
   }
}
