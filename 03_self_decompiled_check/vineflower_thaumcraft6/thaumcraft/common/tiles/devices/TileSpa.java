package thaumcraft.common.tiles.devices;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.items.consumables.ItemBathSalts;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TileSpa extends TileThaumcraftInventory implements IFluidHandler {
   private boolean mix = true;
   private int counter = 0;
   public FluidTank tank = new FluidTank(5000);

   public TileSpa() {
      super(1);
   }

   public void toggleMix() {
      this.mix = !this.mix;
      this.syncTile(false);
      this.func_70296_d();
   }

   public boolean getMix() {
      return this.mix;
   }

   @Override
   public void readSyncNBT(NBTTagCompound nbttagcompound) {
      this.mix = nbttagcompound.func_74767_n("mix");
      this.tank.readFromNBT(nbttagcompound);
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
      nbttagcompound.func_74757_a("mix", this.mix);
      this.tank.writeToNBT(nbttagcompound);
      return nbttagcompound;
   }

   @Override
   public boolean func_94041_b(int par1, ItemStack stack) {
      return stack.func_77973_b() instanceof ItemBathSalts;
   }

   @Override
   public String func_70005_c_() {
      return "thaumcraft.spa";
   }

   @Override
   public boolean func_145818_k_() {
      return false;
   }

   @Override
   public ITextComponent func_145748_c_() {
      return null;
   }

   @Override
   public int[] func_180463_a(EnumFacing side) {
      return side != EnumFacing.UP ? new int[]{0} : new int[0];
   }

   @Override
   public boolean func_180462_a(int index, ItemStack itemStackIn, EnumFacing side) {
      return side != EnumFacing.UP;
   }

   @Override
   public boolean func_180461_b(int index, ItemStack stack, EnumFacing side) {
      return side != EnumFacing.UP;
   }

   @Override
   public void func_73660_a() {
      super.func_73660_a();
      if (!this.field_145850_b.field_72995_K && this.counter++ % 40 == 0 && !this.field_145850_b.func_175640_z(this.field_174879_c) && this.hasIngredients()) {
         Block b = this.field_145850_b.func_180495_p(this.field_174879_c.func_177984_a()).func_177230_c();
         int m = b.func_176201_c(this.field_145850_b.func_180495_p(this.field_174879_c.func_177984_a()));
         Block tb = null;
         if (this.mix) {
            tb = BlocksTC.purifyingFluid;
         } else {
            tb = this.tank.getFluid().getFluid().getBlock();
         }

         if (b == tb && m == 0) {
            for (int xx = -2; xx <= 2; xx++) {
               for (int zz = -2; zz <= 2; zz++) {
                  BlockPos p = this.func_174877_v().func_177982_a(xx, 1, zz);
                  if (this.isValidLocation(p, true, tb)) {
                     this.consumeIngredients();
                     this.field_145850_b.func_175656_a(p, tb.func_176223_P());
                     this.checkQuanta(p);
                     return;
                  }
               }
            }
         } else if (this.isValidLocation(this.field_174879_c.func_177984_a(), false, tb)) {
            this.consumeIngredients();
            this.field_145850_b.func_175656_a(this.field_174879_c.func_177984_a(), tb.func_176223_P());
            this.checkQuanta(this.field_174879_c.func_177984_a());
         }
      }
   }

   private void checkQuanta(BlockPos pos) {
      Block b = this.field_145850_b.func_180495_p(pos).func_177230_c();
      if (b instanceof BlockFluidBase) {
         float p = ((BlockFluidBase)b).getQuantaPercentage(this.field_145850_b, pos);
         if (p < 1.0F) {
            int md = (int)(1.0F / p) - 1;
            if (md >= 0 && md < 16) {
               this.field_145850_b.func_175656_a(pos, b.func_176203_a(md));
            }
         }
      }
   }

   private boolean hasIngredients() {
      if (this.mix) {
         if (this.tank.getInfo().fluid == null || !this.tank.getInfo().fluid.containsFluid(new FluidStack(FluidRegistry.WATER, 1000))) {
            return false;
         }

         if (!(this.func_70301_a(0).func_77973_b() instanceof ItemBathSalts)) {
            return false;
         }
      } else if (this.tank.getInfo().fluid == null || !this.tank.getFluid().getFluid().canBePlacedInWorld() || this.tank.getFluidAmount() < 1000) {
         return false;
      }

      return true;
   }

   private void consumeIngredients() {
      if (this.mix) {
         this.func_70298_a(0, 1);
      }

      this.drain(1000, true);
   }

   private boolean isValidLocation(BlockPos pos, boolean mustBeAdjacent, Block target) {
      if ((target == Blocks.field_150355_j || target == Blocks.field_150358_i) && this.field_145850_b.field_73011_w.func_177500_n()) {
         return false;
      } else {
         Block b = this.field_145850_b.func_180495_p(pos).func_177230_c();
         IBlockState bb = this.field_145850_b.func_180495_p(pos.func_177977_b());
         int m = b.func_176201_c(this.field_145850_b.func_180495_p(pos));
         if (bb.isSideSolid(this.field_145850_b, pos.func_177977_b(), EnumFacing.UP) && b.func_176200_f(this.field_145850_b, pos) && (b != target || m != 0)) {
            return !mustBeAdjacent ? true : BlockUtils.isBlockTouching(this.field_145850_b, pos, target.func_176203_a(0));
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
   }

   @Nullable
   @Override
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? this.tank : super.getCapability(capability, facing));
   }

   public IFluidTankProperties[] getTankProperties() {
      return this.tank.getTankProperties();
   }

   public int fill(FluidStack resource, boolean doFill) {
      this.func_70296_d();
      this.syncTile(false);
      return this.tank.fill(resource, doFill);
   }

   public FluidStack drain(FluidStack resource, boolean doDrain) {
      FluidStack fs = this.tank.drain(resource, doDrain);
      this.func_70296_d();
      this.syncTile(false);
      return fs;
   }

   public FluidStack drain(int maxDrain, boolean doDrain) {
      FluidStack fs = this.tank.drain(maxDrain, doDrain);
      this.func_70296_d();
      this.syncTile(false);
      return fs;
   }
}
