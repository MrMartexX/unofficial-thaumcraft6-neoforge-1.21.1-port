package thaumcraft.common.container;

import java.util.TreeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.common.container.slot.SlotGhostFull;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.seals.SealProvide;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketItemToClientContainer;

public class ContainerLogistics extends Container implements IInventoryChangedListener {
   private World worldObj;
   EntityPlayer player = null;
   public IInventory input = new InventoryLogistics(this);
   TreeMap<String, ItemStack> items = new TreeMap<>();
   int lastTotal = 0;
   public int start = 0;
   public int end = 0;
   public String searchText = "";
   int lastStart = 0;
   int lastEnd = 0;
   public boolean updated = false;

   public ContainerLogistics(InventoryPlayer iinventory, World par2World) {
      this.worldObj = par2World;
      this.player = iinventory.field_70458_d;

      for (int a = 0; a < this.input.func_70302_i_(); a++) {
         this.func_75146_a(new SlotGhostFull(this.input, a, 19 + a % 9 * 19, 19 + a / 9 * 19));
      }

      this.refreshItemList(true);
   }

   public void refreshItemList(boolean full) {
      int newTotal = this.lastTotal;
      TreeMap<String, ItemStack> ti = new TreeMap<>();
      if (full) {
         newTotal = 0;

         for (SealEntity seal : SealHandler.getSealsInRange(this.worldObj, this.player.func_180425_c(), 32)) {
            if (seal.getSeal() instanceof SealProvide && seal.getOwner().equals(this.player.func_110124_au().toString())) {
               IItemHandler handler = ThaumcraftInvHelper.getItemHandlerAt(this.worldObj, seal.getSealPos().pos, seal.getSealPos().face);

               for (int slot = 0; slot < handler.getSlots(); slot++) {
                  ItemStack stack = handler.getStackInSlot(slot).func_77946_l();
                  if (((SealProvide)seal.getSeal()).matchesFilters(stack)
                     && (this.searchText.isEmpty() || stack.func_82833_r().toLowerCase().contains(this.searchText.toLowerCase()))) {
                     String key = stack.func_82833_r() + stack.func_77952_i() + stack.func_77978_p();
                     if (ti.containsKey(key)) {
                        stack.func_190917_f(ti.get(key).func_190916_E());
                     }

                     ti.put(key, stack);
                     newTotal += stack.func_190916_E();
                  }
               }
            }
         }
      }

      if (this.lastTotal != newTotal || this.start != this.lastStart) {
         this.lastTotal = newTotal;
         if (full) {
            this.items = ti;
         }

         this.input.func_174888_l();
         int j = 0;
         int q = 0;

         for (String key : this.items.keySet()) {
            if (++j > this.start * 9) {
               this.input.func_70299_a(q, this.items.get(key));
               if (++q >= this.input.func_70302_i_()) {
                  break;
               }
            }
         }

         this.end = this.items.size() / 9 - 8;
      }
   }

   public void func_75132_a(IContainerListener listener) {
      super.func_75132_a(listener);
      listener.func_175173_a(this, this.input);
      listener.func_71112_a(this, 0, this.start);
   }

   public void func_75142_b() {
      this.sendLargeSlotsToClient();
      super.func_75142_b();

      for (int i = 0; i < this.field_75149_d.size(); i++) {
         IContainerListener icrafting = (IContainerListener)this.field_75149_d.get(i);
         if (this.lastStart != this.start) {
            icrafting.func_71112_a(this, 0, this.start);
         }

         if (this.lastEnd != this.end) {
            icrafting.func_71112_a(this, 1, this.end);
         }
      }

      this.lastStart = this.start;
      this.lastEnd = this.end;
   }

   private void sendLargeSlotsToClient() {
      for (int i = 0; i < this.field_75151_b.size(); i++) {
         if (this.func_75139_a(i) instanceof SlotGhostFull) {
            ItemStack itemstack = ((Slot)this.field_75151_b.get(i)).func_75211_c();
            ItemStack itemstack1 = (ItemStack)this.field_75153_a.get(i);
            if (itemstack.func_190916_E() > itemstack.func_77976_d()) {
               for (int j = 0; j < this.field_75149_d.size(); j++) {
                  if (this.field_75149_d.get(j) instanceof EntityPlayerMP) {
                     EntityPlayerMP p = (EntityPlayerMP)this.field_75149_d.get(j);
                     PacketHandler.INSTANCE.sendTo(new PacketItemToClientContainer(this.field_75152_c, i, itemstack), p);
                  }
               }
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public void func_75137_b(int par1, int par2) {
      if (par1 == 0) {
         this.start = par2;
         this.updated = true;
      }

      if (par1 == 1) {
         this.end = par2;
         this.updated = true;
      }
   }

   public boolean func_75140_a(EntityPlayer par1EntityPlayer, int par2) {
      if (par2 == 22) {
         this.refreshItemList(true);
         return true;
      }

      if (par2 == 0) {
         if (this.start < this.items.size() / 9 - 8) {
            this.start++;
            this.refreshItemList(false);
         }

         return true;
      } else if (par2 == 1) {
         if (this.start > 0) {
            this.start--;
            this.refreshItemList(false);
         }

         return true;
      } else if (par2 >= 100) {
         int s = par2 - 100;
         if (s >= 0 && s <= this.items.size() / 9 - 8) {
            this.start = s;
            this.refreshItemList(false);
         }

         return true;
      } else {
         return super.func_75140_a(par1EntityPlayer, par2);
      }
   }

   public ItemStack func_82846_b(EntityPlayer par1EntityPlayer, int slot) {
      ItemStack stack = ItemStack.field_190927_a;
      Slot slotObject = (Slot)this.field_75151_b.get(slot);
      if (slotObject != null && slotObject.func_75216_d()) {
         ItemStack stackInSlot = slotObject.func_75211_c();
         stack = stackInSlot.func_77946_l();
         if (slot < this.input.func_70302_i_()) {
            if (!this.input.func_94041_b(slot, stackInSlot) || !this.func_75135_a(stackInSlot, this.input.func_70302_i_(), this.field_75151_b.size(), true)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.input.func_94041_b(slot, stackInSlot) || !this.func_75135_a(stackInSlot, 0, this.input.func_70302_i_(), false)) {
            return ItemStack.field_190927_a;
         }

         if (stackInSlot.func_190916_E() == 0) {
            slotObject.func_75215_d(ItemStack.field_190927_a);
         } else {
            slotObject.func_75218_e();
         }
      }

      return stack;
   }

   public boolean func_75145_c(EntityPlayer var1) {
      return true;
   }

   public void func_76316_a(IInventory invBasic) {
      this.func_75142_b();
   }
}
