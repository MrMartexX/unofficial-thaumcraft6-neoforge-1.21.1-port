package thaumcraft.common.tiles;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class TileThaumcraftInventory extends TileThaumcraft implements ISidedInventory, ITickable {
   private NonNullList<ItemStack> stacks = NonNullList.func_191197_a(1, ItemStack.field_190927_a);
   protected int[] syncedSlots = new int[0];
   private NonNullList<ItemStack> syncedStacks = NonNullList.func_191197_a(1, ItemStack.field_190927_a);
   protected String customName;
   private int[] faceSlots;
   boolean initial = true;
   IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
   IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
   IItemHandler handlerWest = new SidedInvWrapper(this, EnumFacing.WEST);
   IItemHandler handlerEast = new SidedInvWrapper(this, EnumFacing.EAST);
   IItemHandler handlerNorth = new SidedInvWrapper(this, EnumFacing.NORTH);
   IItemHandler handlerSouth = new SidedInvWrapper(this, EnumFacing.SOUTH);

   public TileThaumcraftInventory(int size) {
      this.stacks = NonNullList.func_191197_a(size, ItemStack.field_190927_a);
      this.syncedStacks = NonNullList.func_191197_a(size, ItemStack.field_190927_a);
      this.faceSlots = new int[size];
      int a = 0;

      while (a < size) {
         this.faceSlots[a] = a++;
      }
   }

   public int func_70302_i_() {
      return this.stacks.size();
   }

   protected NonNullList<ItemStack> getItems() {
      return this.stacks;
   }

   public ItemStack getSyncedStackInSlot(int index) {
      return (ItemStack)this.syncedStacks.get(index);
   }

   public ItemStack func_70301_a(int index) {
      return (ItemStack)this.getItems().get(index);
   }

   public ItemStack func_70298_a(int index, int count) {
      ItemStack itemstack = ItemStackHelper.func_188382_a(this.getItems(), index, count);
      if (!itemstack.func_190926_b() && this.isSyncedSlot(index)) {
         this.syncSlots(null);
      }

      this.func_70296_d();
      return itemstack;
   }

   public ItemStack func_70304_b(int index) {
      ItemStack s = ItemStackHelper.func_188383_a(this.getItems(), index);
      if (this.isSyncedSlot(index)) {
         this.syncSlots(null);
      }

      this.func_70296_d();
      return s;
   }

   public void func_70299_a(int index, @Nullable ItemStack stack) {
      this.getItems().set(index, stack);
      if (stack.func_190916_E() > this.func_70297_j_()) {
         stack.func_190920_e(this.func_70297_j_());
      }

      this.func_70296_d();
      if (this.isSyncedSlot(index)) {
         this.syncSlots(null);
      }
   }

   public String func_70005_c_() {
      return this.func_145818_k_() ? this.customName : "container.thaumcraft";
   }

   public boolean func_145818_k_() {
      return this.customName != null && this.customName.length() > 0;
   }

   public ITextComponent func_145748_c_() {
      return null;
   }

   private boolean isSyncedSlot(int slot) {
      for (int s : this.syncedSlots) {
         if (s == slot) {
            return true;
         }
      }

      return false;
   }

   protected void syncSlots(EntityPlayerMP player) {
      if (this.syncedSlots.length > 0) {
         NBTTagCompound nbt = new NBTTagCompound();
         NBTTagList nbttaglist = new NBTTagList();

         for (int i = 0; i < this.stacks.size(); i++) {
            if (!((ItemStack)this.stacks.get(i)).func_190926_b() && this.isSyncedSlot(i)) {
               NBTTagCompound nbttagcompound1 = new NBTTagCompound();
               nbttagcompound1.func_74774_a("Slot", (byte)i);
               ((ItemStack)this.stacks.get(i)).func_77955_b(nbttagcompound1);
               nbttaglist.func_74742_a(nbttagcompound1);
            }
         }

         nbt.func_74782_a("ItemsSynced", nbttaglist);
         this.sendMessageToClient(nbt, player);
      }
   }

   @Override
   public void syncTile(boolean rerender) {
      super.syncTile(rerender);
      this.syncSlots(null);
   }

   @Override
   public void messageFromClient(NBTTagCompound nbt, EntityPlayerMP player) {
      super.messageFromClient(nbt, player);
      if (nbt.func_74764_b("requestSync")) {
         this.syncSlots(player);
      }
   }

   @Override
   public void messageFromServer(NBTTagCompound nbt) {
      super.messageFromServer(nbt);
      if (nbt.func_74764_b("ItemsSynced")) {
         this.syncedStacks = NonNullList.func_191197_a(this.func_70302_i_(), ItemStack.field_190927_a);
         NBTTagList nbttaglist = nbt.func_150295_c("ItemsSynced", 10);

         for (int i = 0; i < nbttaglist.func_74745_c(); i++) {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
            byte b0 = nbttagcompound1.func_74771_c("Slot");
            if (this.isSyncedSlot(b0)) {
               this.syncedStacks.set(b0, new ItemStack(nbttagcompound1));
            }
         }
      }
   }

   @Override
   public void func_145839_a(NBTTagCompound nbtCompound) {
      super.func_145839_a(nbtCompound);
      if (nbtCompound.func_74764_b("CustomName")) {
         this.customName = nbtCompound.func_74779_i("CustomName");
      }

      this.stacks = NonNullList.func_191197_a(this.func_70302_i_(), ItemStack.field_190927_a);
      ItemStackHelper.func_191283_b(nbtCompound, this.stacks);
   }

   @Override
   public NBTTagCompound func_189515_b(NBTTagCompound nbtCompound) {
      super.func_189515_b(nbtCompound);
      if (this.func_145818_k_()) {
         nbtCompound.func_74778_a("CustomName", this.customName);
      }

      ItemStackHelper.func_191282_a(nbtCompound, this.stacks);
      return nbtCompound;
   }

   public int func_70297_j_() {
      return 64;
   }

   public boolean func_70300_a(EntityPlayer par1EntityPlayer) {
      return this.field_145850_b.func_175625_s(this.func_174877_v()) != this ? false : par1EntityPlayer.func_174831_c(this.func_174877_v()) <= 64.0;
   }

   public boolean func_94041_b(int par1, ItemStack stack2) {
      return true;
   }

   public int[] func_180463_a(EnumFacing par1) {
      return this.faceSlots;
   }

   public void func_174889_b(EntityPlayer player) {
   }

   public void func_174886_c(EntityPlayer player) {
   }

   public int func_174887_a_(int id) {
      return 0;
   }

   public void func_174885_b(int id, int value) {
   }

   public int func_174890_g() {
      return 0;
   }

   public void func_174888_l() {
   }

   public boolean func_180462_a(int par1, ItemStack stack2, EnumFacing par3) {
      return this.func_94041_b(par1, stack2);
   }

   public boolean func_180461_b(int par1, ItemStack stack2, EnumFacing par3) {
      return true;
   }

   public boolean func_191420_l() {
      for (ItemStack itemstack : this.stacks) {
         if (!itemstack.func_190926_b()) {
            return false;
         }
      }

      return true;
   }

   public void func_73660_a() {
      if (this.initial) {
         this.initial = false;
         if (!this.field_145850_b.field_72995_K) {
            this.syncSlots(null);
         } else {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.func_74757_a("requestSync", true);
            this.sendMessageToServer(nbt);
         }
      }
   }

   @Nullable
   public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      if (facing == null || capability != CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         return (T)super.getCapability(capability, facing);
      } else if (facing == EnumFacing.DOWN) {
         return (T)this.handlerBottom;
      } else if (facing == EnumFacing.UP) {
         return (T)this.handlerTop;
      } else if (facing == EnumFacing.WEST) {
         return (T)this.handlerWest;
      } else if (facing == EnumFacing.EAST) {
         return (T)this.handlerEast;
      } else {
         return (T)(facing == EnumFacing.NORTH ? this.handlerNorth : this.handlerSouth);
      }
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
   }
}
