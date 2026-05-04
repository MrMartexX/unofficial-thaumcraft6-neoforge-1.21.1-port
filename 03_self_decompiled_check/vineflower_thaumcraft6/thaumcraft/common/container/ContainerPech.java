package thaumcraft.common.container;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.container.slot.SlotOutput;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.lib.SoundsTC;

public class ContainerPech extends Container implements IInventoryChangedListener {
   private EntityPech pech;
   private InventoryPech inventory;
   private EntityPlayer player;
   private final World theWorld;

   public ContainerPech(InventoryPlayer par1InventoryPlayer, World par3World, EntityPech par2IMerchant) {
      this.pech = par2IMerchant;
      this.theWorld = par3World;
      this.player = par1InventoryPlayer.field_70458_d;
      this.inventory = new InventoryPech(this, par1InventoryPlayer.field_70458_d, par2IMerchant);
      this.pech.trading = true;
      this.func_75146_a(new Slot(this.inventory, 0, 36, 29));

      for (int i = 0; i < 2; i++) {
         for (int j = 0; j < 2; j++) {
            this.func_75146_a(new SlotOutput(this.inventory, 1 + j + i * 2, 106 + 18 * j, 20 + 18 * i));
         }
      }

      for (int var6 = 0; var6 < 3; var6++) {
         for (int j = 0; j < 9; j++) {
            this.func_75146_a(new Slot(par1InventoryPlayer, j + var6 * 9 + 9, 8 + j * 18, 84 + var6 * 18));
         }
      }

      for (int var7 = 0; var7 < 9; var7++) {
         this.func_75146_a(new Slot(par1InventoryPlayer, var7, 8 + var7 * 18, 142));
      }
   }

   public InventoryPech getMerchantInventory() {
      return this.inventory;
   }

   public void func_76316_a(IInventory invBasic) {
   }

   public boolean func_75140_a(EntityPlayer par1EntityPlayer, int par2) {
      if (par2 == 0) {
         this.generateContents();
         return true;
      } else {
         return super.func_75140_a(par1EntityPlayer, par2);
      }
   }

   private boolean hasStuffInPack() {
      for (ItemStack stack : this.pech.loot) {
         if (stack != null && !stack.func_190926_b() && stack.func_190916_E() > 0) {
            return true;
         }
      }

      return false;
   }

   private void generateContents() {
      if (!this.theWorld.field_72995_K
         && !this.inventory.func_70301_a(0).func_190926_b()
         && this.inventory.func_70301_a(1).func_190926_b()
         && this.inventory.func_70301_a(2).func_190926_b()
         && this.inventory.func_70301_a(3).func_190926_b()
         && this.inventory.func_70301_a(4).func_190926_b()
         && this.pech.isValued(this.inventory.func_70301_a(0))) {
         int value = this.pech.getValue(this.inventory.func_70301_a(0));
         if (this.theWorld.field_73012_v.nextInt(100) <= value / 2) {
            this.pech.setTamed(false);
            this.pech.func_184185_a(SoundsTC.pech_trade, 0.4F, 1.0F);
         }

         if (this.theWorld.field_73012_v.nextInt(5) == 0) {
            value += this.theWorld.field_73012_v.nextInt(3);
         } else if (this.theWorld.field_73012_v.nextBoolean()) {
            value -= this.theWorld.field_73012_v.nextInt(3);
         }

         ArrayList<List> pos = EntityPech.tradeInventory.get(this.pech.getPechType());

         while (value > 0) {
            int am = Math.min(5, Math.max((value + 1) / 2, this.theWorld.field_73012_v.nextInt(value) + 1));
            value -= am;
            if (am == 1 && this.theWorld.field_73012_v.nextBoolean() && this.hasStuffInPack()) {
               ArrayList<Integer> loot = new ArrayList<>();

               for (int a = 0; a < this.pech.loot.size(); a++) {
                  if (this.pech.loot.get(a) != null
                     && !((ItemStack)this.pech.loot.get(a)).func_190926_b()
                     && ((ItemStack)this.pech.loot.get(a)).func_190916_E() > 0) {
                     loot.add(a);
                  }
               }

               int r = loot.get(this.theWorld.field_73012_v.nextInt(loot.size()));
               ItemStack is = ((ItemStack)this.pech.loot.get(r)).func_77946_l();
               is.func_190920_e(1);
               this.addStack(is);
               ((ItemStack)this.pech.loot.get(r)).func_190918_g(1);
               if (((ItemStack)this.pech.loot.get(r)).func_190916_E() <= 0) {
                  this.pech.loot.set(r, ItemStack.field_190927_a);
               }
            } else if (am < 4 || !this.theWorld.field_73012_v.nextBoolean()) {
               List it = null;

               do {
                  it = pos.get(this.theWorld.field_73012_v.nextInt(pos.size()));
               } while (it.get(0) != am);

               ItemStack is = ((ItemStack)it.get(1)).func_77946_l();
               is.func_77980_a(this.theWorld, this.player, 0);
               this.addStack(is);
            }
         }

         this.inventory.func_70298_a(0, 1);
      }
   }

   private void addStack(ItemStack s) {
      for (int a = 1; a < 5; a++) {
         if (this.inventory.func_70301_a(a).func_190926_b()) {
            this.inventory.func_70299_a(a, s);
            break;
         }

         if (this.inventory.func_70301_a(a).func_77969_a(s)
            && this.inventory.func_70301_a(a).func_190916_E() + s.func_190916_E() < this.inventory.func_70301_a(a).func_77976_d()) {
            this.inventory.func_70301_a(a).func_190917_f(s.func_190916_E());
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public void func_75137_b(int par1, int par2) {
   }

   public boolean func_75145_c(EntityPlayer par1EntityPlayer) {
      return this.pech.isTamed();
   }

   public ItemStack func_82846_b(EntityPlayer par1EntityPlayer, int par2) {
      ItemStack itemstack = ItemStack.field_190927_a;
      Slot slot = (Slot)this.field_75151_b.get(par2);
      if (slot != null && slot.func_75216_d()) {
         ItemStack itemstack1 = slot.func_75211_c();
         itemstack = itemstack1.func_77946_l();
         if (par2 == 0) {
            if (!this.func_75135_a(itemstack1, 5, 41, true)) {
               return ItemStack.field_190927_a;
            }
         } else if (par2 >= 1 && par2 < 5) {
            if (!this.func_75135_a(itemstack1, 5, 41, true)) {
               return ItemStack.field_190927_a;
            }
         } else if (par2 != 0 && par2 >= 5 && par2 < 41 && !this.func_75135_a(itemstack1, 0, 1, true)) {
            return ItemStack.field_190927_a;
         }

         if (itemstack1.func_190916_E() == 0) {
            slot.func_75215_d(ItemStack.field_190927_a);
         } else {
            slot.func_75218_e();
         }

         if (itemstack1.func_190916_E() == itemstack.func_190916_E()) {
            return ItemStack.field_190927_a;
         }

         slot.func_190901_a(par1EntityPlayer, itemstack1);
      }

      return itemstack;
   }

   public void func_75134_a(EntityPlayer par1EntityPlayer) {
      super.func_75134_a(par1EntityPlayer);
      this.pech.trading = false;
      if (!this.theWorld.field_72995_K) {
         for (int a = 0; a < 5; a++) {
            ItemStack itemstack = this.inventory.func_70304_b(a);
            if (itemstack != null) {
               EntityItem ei = par1EntityPlayer.func_71019_a(itemstack, false);
               if (ei != null) {
                  ei.func_145799_b("PechDrop");
               }
            }
         }
      }
   }
}
