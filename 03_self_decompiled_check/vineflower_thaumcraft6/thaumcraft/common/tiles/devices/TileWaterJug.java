package thaumcraft.common.tiles.devices;

import java.util.ArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.tiles.TileThaumcraft;
import vazkii.botania.api.item.IPetalApothecary;

public class TileWaterJug extends TileThaumcraft implements ITickable, IFluidHandler {
   int zone = 0;
   int counter = 0;
   ArrayList<Integer> handlers = new ArrayList<>();
   int zc = 0;
   int tcount = 0;
   public FluidTank tank = new FluidTank(new FluidStack(FluidRegistry.WATER, 0), 1000);

   @Override
   public void readSyncNBT(NBTTagCompound nbttagcompound) {
      this.tank.readFromNBT(nbttagcompound);
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
      this.tank.writeToNBT(nbttagcompound);
      return nbttagcompound;
   }

   @Override
   public void func_145839_a(NBTTagCompound nbt) {
      super.func_145839_a(nbt);
      NBTTagList nbttaglist = nbt.func_150295_c("handlers", 3);
      this.handlers = new ArrayList<>();

      for (int i = 0; i < nbttaglist.func_74745_c(); i++) {
         NBTTagInt tag = (NBTTagInt)nbttaglist.func_179238_g(i);
         this.handlers.add(tag.func_150287_d());
      }
   }

   @Override
   public NBTTagCompound func_189515_b(NBTTagCompound nbt) {
      super.func_189515_b(nbt);
      NBTTagList nbttaglist = new NBTTagList();

      for (int i = 0; i < this.handlers.size(); i++) {
         new NBTTagInt(this.handlers.get(i));
      }

      nbt.func_74782_a("handlers", nbttaglist);
      return nbt;
   }

   public void func_73660_a() {
      this.counter++;
      if (this.field_145850_b.field_72995_K) {
         if (this.tcount > 0) {
            if (this.tcount % 5 == 0) {
               int x = this.zc / 5 % 5;
               int y = this.zc / 5 / 5 % 3;
               int z = this.zc % 5;
               FXDispatcher.INSTANCE.waterTrailFx(this.func_174877_v(), this.func_174877_v().func_177982_a(x - 2, y - 1, z - 2), this.counter, 2650102, 0.1F);
            }

            this.tcount--;
         }
      } else if (this.counter % 5 == 0) {
         this.zone++;
         int x = this.zone / 5 % 5;
         int y = this.zone / 5 / 5 % 3;
         int z = this.zone % 5;
         IBlockState bs = this.field_145850_b.func_180495_p(this.func_174877_v().func_177982_a(x - 2, y - 1, z - 2));
         TileEntity te = this.field_145850_b.func_175625_s(this.func_174877_v().func_177982_a(x - 2, y - 1, z - 2));
         if ((
               te != null
                     && (
                        te instanceof IFluidHandler
                           || te instanceof IPetalApothecary
                           || te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP)
                     )
                  || bs.func_177230_c() == Blocks.field_150383_bp
            )
            && !this.handlers.contains(this.zone)) {
            this.handlers.add(this.zone);
            this.func_70296_d();
         }

         int i = 0;

         while (i < this.handlers.size() && this.tank.getFluidAmount() >= 25) {
            int zz = this.handlers.get(i);
            x = zz / 5 % 5;
            y = zz / 5 / 5 % 3;
            z = zz % 5;
            IBlockState bs2 = this.field_145850_b.func_180495_p(this.func_174877_v().func_177982_a(x - 2, y - 1, z - 2));
            TileEntity tile = this.field_145850_b.func_175625_s(this.func_174877_v().func_177982_a(x - 2, y - 1, z - 2));
            if (tile == null || !(tile instanceof IFluidHandler) && !tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP)) {
               if (tile != null && tile instanceof IPetalApothecary && this.tank.getFluidAmount() >= 1000) {
                  IPetalApothecary pa = (IPetalApothecary)tile;
                  if (!pa.hasWater()) {
                     pa.setWater(true);
                     this.field_145850_b.func_175641_c(this.func_174877_v(), this.func_145838_q(), 1, zz);
                     this.drain(new FluidStack(FluidRegistry.WATER, 1000), true);
                  }
               } else {
                  if (bs2.func_177230_c() != Blocks.field_150383_bp || this.tank.getFluidAmount() < 333) {
                     this.handlers.remove(i);
                     this.func_70296_d();
                     continue;
                  }

                  if ((Integer)bs2.func_177229_b(BlockCauldron.field_176591_a) < 3) {
                     BlockPos pp = this.func_174877_v().func_177982_a(x - 2, y - 1, z - 2);
                     this.field_145850_b.func_180501_a(pp, bs2.func_177231_a(BlockCauldron.field_176591_a), 2);
                     this.field_145850_b.func_175666_e(pp, bs2.func_177230_c());
                     this.field_145850_b.func_175641_c(this.func_174877_v(), this.func_145838_q(), 1, zz);
                     this.drain(new FluidStack(FluidRegistry.WATER, 333), true);
                  }
               }
            } else {
               IFluidHandler fh = null;
               if (tile instanceof IFluidHandler) {
                  fh = (IFluidHandler)tile;
               } else {
                  fh = (IFluidHandler)tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
               }

               if (fh != null) {
                  int q = fh.fill(new FluidStack(FluidRegistry.WATER, 25), true);
                  if (q > 0) {
                     this.drain(new FluidStack(FluidRegistry.WATER, q), true);
                     this.func_70296_d();
                     this.field_145850_b.func_175641_c(this.func_174877_v(), this.func_145838_q(), 1, zz);
                     break;
                  }
               }
            }

            i++;
         }

         if (this.tank.getFluidAmount() < 1000) {
            float da = (1000 - this.tank.getFluidAmount()) / 1000.0F;
            if (da > 0.1F) {
               da = 0.1F;
            }

            float dv = AuraHelper.drainVis(this.func_145831_w(), this.func_174877_v(), da, false);
            int wa = (int)(1000.0F * dv);
            if (wa > 0) {
               this.tank.fill(new FluidStack(FluidRegistry.WATER, wa), true);
               this.func_70296_d();
               if (this.tank.getFluidAmount() >= this.tank.getCapacity()) {
                  this.syncTile(false);
               }
            }
         }
      }
   }

   public boolean func_145842_c(int i, int j) {
      if (i == 1) {
         if (this.field_145850_b.field_72995_K) {
            this.zc = j;
            this.tcount = 5;
         }

         return true;
      } else {
         return super.func_145842_c(i, j);
      }
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return facing == EnumFacing.UP && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(facing == EnumFacing.UP && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? this.tank : super.getCapability(capability, facing));
   }

   public IFluidTankProperties[] getTankProperties() {
      return this.tank.getTankProperties();
   }

   public int fill(FluidStack resource, boolean doFill) {
      return 0;
   }

   public FluidStack drain(FluidStack resource, boolean doDrain) {
      boolean f = this.tank.getFluidAmount() >= this.tank.getCapacity();
      FluidStack fs = this.tank.drain(resource, doDrain);
      this.func_70296_d();
      if (f && this.tank.getFluidAmount() < this.tank.getCapacity()) {
         this.syncTile(false);
      }

      return fs;
   }

   public FluidStack drain(int maxDrain, boolean doDrain) {
      boolean f = this.tank.getFluidAmount() >= this.tank.getCapacity();
      FluidStack fs = this.tank.drain(maxDrain, doDrain);
      this.func_70296_d();
      if (f && this.tank.getFluidAmount() < this.tank.getCapacity()) {
         this.syncTile(false);
      }

      return fs;
   }
}
