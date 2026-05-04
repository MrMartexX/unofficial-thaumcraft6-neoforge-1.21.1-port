package thaumcraft.common.tiles.crafting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TileVoidSiphon extends TileThaumcraftInventory {
   private static final int[] slots = new int[]{0};
   int counter = 0;
   public int progress = 0;
   public final int PROGREQ = 2000;

   public TileVoidSiphon() {
      super(1);
   }

   @Override
   public void func_73660_a() {
      super.func_73660_a();
      this.counter++;
      if ((!this.func_145831_w().field_72995_K || !BlockStateUtils.isEnabled(this.func_145832_p()))
         && !this.func_145831_w().field_72995_K
         && BlockStateUtils.isEnabled(this.func_145832_p())
         && this.counter % 20 == 0
         && this.progress < 2000
         && (
            this.func_70301_a(0).func_190926_b()
               || this.func_70301_a(0).func_77973_b() == ItemsTC.voidSeed && this.func_70301_a(0).func_190916_E() < this.func_70301_a(0).func_77976_d()
         )) {
         List<EntityFluxRift> frl = this.getValidRifts();
         boolean b = false;

         for (EntityFluxRift fr : frl) {
            double d = Math.sqrt(fr.getRiftSize());
            this.progress = (int)(this.progress + d);
            fr.setRiftStability((float)(fr.getRiftStability() - d / 15.0));
            if (this.field_145850_b.field_73012_v.nextInt(33) == 0) {
               fr.setRiftSize(fr.getRiftSize() - 1);
            }

            b = d >= 1.0;
         }

         if (b && this.counter % 40 == 0) {
            this.field_145850_b.func_175641_c(this.field_174879_c, this.func_145838_q(), 5, this.counter);
         }

         for (b = false;
            this.progress >= 2000
               && (
                  this.func_70301_a(0).func_190926_b()
                     || this.func_70301_a(0).func_77973_b() == ItemsTC.voidSeed && this.func_70301_a(0).func_190916_E() < this.func_70301_a(0).func_77976_d()
               );
            b = true
         ) {
            this.progress -= 2000;
            if (this.func_70301_a(0).func_190926_b()) {
               this.func_70299_a(0, new ItemStack(ItemsTC.voidSeed));
            } else {
               this.func_70301_a(0).func_190920_e(this.func_70301_a(0).func_190916_E() + 1);
            }
         }

         if (b) {
            this.syncTile(false);
            this.func_70296_d();
         }
      }
   }

   private List<EntityFluxRift> getValidRifts() {
      ArrayList<EntityFluxRift> ret = new ArrayList<>();

      for (EntityFluxRift fr : EntityUtils.getEntitiesInRange(this.func_145831_w(), this.func_174877_v(), null, EntityFluxRift.class, 8.0)) {
         if (!fr.field_70128_L && fr.getRiftSize() >= 2) {
            double xx = this.func_174877_v().func_177958_n() + 0.5;
            double yy = this.func_174877_v().func_177956_o() + 1;
            double zz = this.func_174877_v().func_177952_p() + 0.5;
            Vec3d v1 = new Vec3d(xx, yy, zz);
            Vec3d v2 = new Vec3d(fr.field_70165_t, fr.field_70163_u, fr.field_70161_v);
            v1 = v1.func_178787_e(v2.func_178788_d(v1).func_72432_b());
            if (EntityUtils.canEntityBeSeen(fr, v1.field_72450_a, v1.field_72448_b, v1.field_72449_c)) {
               ret.add(fr);
            }
         }
      }

      return ret;
   }

   @Override
   public void func_145839_a(NBTTagCompound nbt) {
      super.func_145839_a(nbt);
      this.progress = nbt.func_74765_d("progress");
   }

   @Override
   public NBTTagCompound func_189515_b(NBTTagCompound nbt) {
      super.func_189515_b(nbt);
      nbt.func_74777_a("progress", (short)this.progress);
      return nbt;
   }

   @Override
   public boolean func_94041_b(int par1, ItemStack stack) {
      return stack.func_77973_b() == ItemsTC.voidSeed;
   }

   @Override
   public int[] func_180463_a(EnumFacing side) {
      return slots;
   }

   @Override
   public boolean func_180462_a(int par1, ItemStack stack, EnumFacing par3) {
      return false;
   }

   @Override
   public boolean func_180461_b(int par1, ItemStack stack2, EnumFacing par3) {
      return true;
   }

   public boolean func_145842_c(int i, int j) {
      if (i != 5) {
         return super.func_145842_c(i, j);
      }

      if (this.field_145850_b.field_72995_K) {
         for (EntityFluxRift fr : this.getValidRifts()) {
            FXDispatcher.INSTANCE
               .voidStreak(
                  fr.field_70165_t,
                  fr.field_70163_u,
                  fr.field_70161_v,
                  this.func_174877_v().func_177958_n() + 0.5,
                  this.func_174877_v().func_177956_o() + 0.5625F,
                  this.func_174877_v().func_177952_p() + 0.5,
                  j,
                  0.04F
               );
         }
      }

      return true;
   }
}
