package thaumcraft.common.tiles.devices;

import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;

public class TileStabilizer extends TileThaumcraft implements ITickable {
   private int ticks = 0;
   private int delay = 0;
   int lastEnergy = 0;
   protected int energy = 0;
   protected final int capacity = 15;

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB(
         this.func_174877_v().func_177958_n(),
         this.func_174877_v().func_177956_o(),
         this.func_174877_v().func_177952_p(),
         this.func_174877_v().func_177958_n() + 1,
         this.func_174877_v().func_177956_o() + 1.5,
         this.func_174877_v().func_177952_p() + 1
      );
   }

   public void func_73660_a() {
      if (!this.field_145850_b.field_72995_K) {
         this.ticks++;
         if (this.energy < 15 && this.ticks % 20 == 0) {
            this.energy++;
            AuraHelper.polluteAura(this.func_145831_w(), this.func_174877_v(), 0.25F, true);
            this.func_70296_d();
            this.syncTile(false);
            this.field_145850_b.func_175685_c(this.func_174877_v(), this.field_145850_b.func_180495_p(this.field_174879_c).func_177230_c(), false);
         }

         if (this.energy > 0 && this.delay <= 0 && this.ticks % 5 == 0) {
            int q = this.energy;
            this.tryAddStability();
            if (q != this.energy) {
               this.func_70296_d();
               this.syncTile(false);
            }
         }

         if (this.delay > 0) {
            this.delay--;
         }
      }

      if (this.field_145850_b.field_72995_K && this.energy != this.lastEnergy) {
         this.field_145850_b.func_175704_b(this.func_174877_v(), this.func_174877_v());
         this.lastEnergy = this.energy;
      }
   }

   private void tryAddStability() {
      EnumFacing facing = BlockStateUtils.getFacing(this.func_145832_p());
      List<EntityFluxRift> targets = this.field_145850_b
         .func_72872_a(
            EntityFluxRift.class,
            new AxisAlignedBB(
                  this.field_174879_c.func_177958_n(),
                  this.field_174879_c.func_177956_o(),
                  this.field_174879_c.func_177952_p(),
                  this.field_174879_c.func_177958_n() + 1,
                  this.field_174879_c.func_177956_o() + 1,
                  this.field_174879_c.func_177952_p() + 1
               )
               .func_186662_g(8.0)
         );
      if (targets.size() > 0) {
         for (EntityFluxRift e : targets) {
            if (!e.field_70128_L && e.getStability() != EntityFluxRift.EnumStability.VERY_STABLE && this.mitigate(1)) {
               e.addStability();
               this.delay += 5;
               if (this.energy <= 0) {
                  return;
               }
            }
         }
      }
   }

   @Override
   public void readSyncNBT(NBTTagCompound nbt) {
      this.energy = Math.min(nbt.func_74762_e("energy"), 15);
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
      nbt.func_74768_a("energy", this.energy);
      return nbt;
   }

   public int getEnergy() {
      return this.energy;
   }

   public boolean mitigate(int e) {
      if (this.energy >= e) {
         this.energy -= e;
         this.field_145850_b.func_175685_c(this.func_174877_v(), this.field_145850_b.func_180495_p(this.field_174879_c).func_177230_c(), false);
         return true;
      } else {
         return false;
      }
   }
}
