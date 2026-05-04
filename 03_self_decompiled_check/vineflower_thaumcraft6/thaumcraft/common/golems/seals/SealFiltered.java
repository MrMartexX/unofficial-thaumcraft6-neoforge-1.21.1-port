package thaumcraft.common.golems.seals;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;

public abstract class SealFiltered implements ISeal, ISealGui, ISealConfigFilter {
   NonNullList<ItemStack> filter = NonNullList.func_191197_a(this.getFilterSize(), ItemStack.field_190927_a);
   NonNullList<Integer> filterSize = NonNullList.func_191197_a(this.getFilterSize(), 0);
   boolean blacklist = true;

   @Override
   public void readCustomNBT(NBTTagCompound nbt) {
      this.filter = NonNullList.func_191197_a(this.getFilterSize(), ItemStack.field_190927_a);
      ItemStackHelper.func_191283_b(nbt, this.filter);

      for (ItemStack s : this.filter) {
         if (s.func_190916_E() > 1) {
            s.func_190920_e(1);
         }
      }

      this.blacklist = nbt.func_74767_n("bl");
      this.filterSize = NonNullList.func_191197_a(this.getFilterSize(), 0);
      NBTTagList nbttaglist = nbt.func_150295_c("Sizes", 10);

      for (int i = 0; i < nbttaglist.func_74745_c(); i++) {
         NBTTagCompound nbttagcompound = nbttaglist.func_150305_b(i);
         int j = nbttagcompound.func_74771_c("Slot") & 255;
         if (j >= 0 && j < this.filterSize.size()) {
            this.filterSize.set(j, nbttagcompound.func_74762_e("Size"));
         }
      }
   }

   @Override
   public void writeCustomNBT(NBTTagCompound nbt) {
      ItemStackHelper.func_191282_a(nbt, this.filter);
      nbt.func_74757_a("bl", this.blacklist);
      NBTTagList nbttaglist = new NBTTagList();

      for (int i = 0; i < this.filterSize.size(); i++) {
         int size = (Integer)this.filterSize.get(i);
         if (size != 0) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.func_74774_a("Slot", (byte)i);
            nbttagcompound.func_74768_a("Size", size);
            nbttaglist.func_74742_a(nbttagcompound);
         }
      }

      nbt.func_74782_a("Sizes", nbttaglist);
   }

   @Override
   public Object returnContainer(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
      return new SealBaseContainer(player.field_71071_by, world, seal);
   }

   @SideOnly(Side.CLIENT)
   @Override
   public Object returnGui(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
      return new SealBaseGUI(player.field_71071_by, world, seal);
   }

   @Override
   public int[] getGuiCategories() {
      return new int[]{0};
   }

   @Override
   public int getFilterSize() {
      return 1;
   }

   @Override
   public NonNullList<ItemStack> getInv() {
      return this.filter;
   }

   @Override
   public NonNullList<Integer> getSizes() {
      return this.filterSize;
   }

   @Override
   public ItemStack getFilterSlot(int i) {
      return (ItemStack)this.filter.get(i);
   }

   @Override
   public int getFilterSlotSize(int i) {
      return (Integer)this.filterSize.get(i);
   }

   @Override
   public void setFilterSlot(int i, ItemStack stack) {
      this.filter.set(i, stack.func_77946_l());
   }

   @Override
   public void setFilterSlotSize(int i, int size) {
      this.filterSize.set(i, size);
   }

   @Override
   public boolean isBlacklist() {
      return this.blacklist;
   }

   @Override
   public void setBlacklist(boolean black) {
      this.blacklist = black;
   }

   @Override
   public boolean hasStacksizeLimiters() {
      return false;
   }
}
