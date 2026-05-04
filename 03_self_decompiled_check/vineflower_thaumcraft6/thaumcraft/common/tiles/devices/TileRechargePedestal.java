package thaumcraft.common.tiles.devices;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TileRechargePedestal extends TileThaumcraftInventory implements IAspectContainer {
   private static final int[] slots = new int[]{0};
   int counter = 0;

   public TileRechargePedestal() {
      super(1);
      this.syncedSlots = new int[]{0};
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB(
            this.func_174877_v().func_177958_n(),
            this.func_174877_v().func_177956_o(),
            this.func_174877_v().func_177952_p(),
            this.func_174877_v().func_177958_n() + 1,
            this.func_174877_v().func_177956_o() + 1,
            this.func_174877_v().func_177952_p() + 1
         )
         .func_72314_b(2.0, 2.0, 2.0);
   }

   @Override
   public void func_73660_a() {
      super.func_73660_a();
      if (!this.func_145831_w().field_72995_K
         && this.counter++ % 10 == 0
         && this.func_70301_a(0) != null
         && RechargeHelper.rechargeItem(this.func_145831_w(), this.func_70301_a(0), this.field_174879_c, null, 5) > 0.0F) {
         this.syncTile(false);
         this.func_70296_d();
         ArrayList<Aspect> al = Aspect.getPrimalAspects();
         this.field_145850_b
            .func_175641_c(this.field_174879_c, this.func_145838_q(), 5, al.get(this.func_145831_w().field_73012_v.nextInt(al.size())).getColor());
      }
   }

   public void setInventorySlotContentsFromInfusion(int par1, ItemStack stack2) {
      this.func_70299_a(par1, stack2);
      this.func_70296_d();
      if (!this.field_145850_b.field_72995_K) {
         this.syncTile(false);
      }
   }

   @Override
   public boolean func_94041_b(int par1, ItemStack stack) {
      return stack.func_77973_b() instanceof IRechargable;
   }

   @Override
   public int[] func_180463_a(EnumFacing side) {
      return slots;
   }

   @Override
   public boolean func_180462_a(int par1, ItemStack stack, EnumFacing par3) {
      return stack.func_77973_b() instanceof IRechargable;
   }

   @Override
   public boolean func_180461_b(int par1, ItemStack stack2, EnumFacing par3) {
      return true;
   }

   @Override
   public AspectList getAspects() {
      ItemStack s = this.field_145850_b != null && !this.field_145850_b.field_72995_K ? this.func_70301_a(0) : this.getSyncedStackInSlot(0);
      if (s != null && s.func_77973_b() instanceof IRechargable) {
         float c = RechargeHelper.getCharge(s);
         return new AspectList().add(Aspect.ENERGY, Math.round(c));
      } else {
         return null;
      }
   }

   @Override
   public void setAspects(AspectList aspects) {
   }

   @Override
   public int addToContainer(Aspect tag, int amount) {
      return 0;
   }

   @Override
   public boolean takeFromContainer(Aspect tag, int amount) {
      return false;
   }

   @Override
   public boolean takeFromContainer(AspectList ot) {
      return false;
   }

   @Override
   public boolean doesContainerContainAmount(Aspect tag, int amount) {
      return false;
   }

   @Override
   public boolean doesContainerContain(AspectList ot) {
      return false;
   }

   @Override
   public int containerContains(Aspect tag) {
      return 0;
   }

   @Override
   public boolean doesContainerAccept(Aspect tag) {
      return true;
   }

   public boolean func_145842_c(int i, int j) {
      if (i == 5) {
         if (this.field_145850_b.field_72995_K) {
            FXDispatcher.INSTANCE
               .visSparkle(
                  this.field_174879_c.func_177958_n() + this.func_145831_w().field_73012_v.nextInt(3) - this.func_145831_w().field_73012_v.nextInt(3),
                  this.field_174879_c.func_177984_a().func_177956_o() + this.func_145831_w().field_73012_v.nextInt(3),
                  this.field_174879_c.func_177952_p() + this.func_145831_w().field_73012_v.nextInt(3) - this.func_145831_w().field_73012_v.nextInt(3),
                  this.field_174879_c.func_177958_n(),
                  this.field_174879_c.func_177984_a().func_177956_o(),
                  this.field_174879_c.func_177952_p(),
                  j
               );
         }

         return true;
      } else {
         return super.func_145842_c(i, j);
      }
   }
}
