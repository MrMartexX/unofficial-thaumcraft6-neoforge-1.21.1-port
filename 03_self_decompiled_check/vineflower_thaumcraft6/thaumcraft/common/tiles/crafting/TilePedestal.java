package thaumcraft.common.tiles.crafting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.devices.BlockInlay;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TilePedestal extends TileThaumcraftInventory {
   public TilePedestal() {
      super(1);
      this.syncedSlots = new int[]{0};
   }

   @Override
   public int func_70297_j_() {
      return 1;
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB(
         this.func_174877_v().func_177958_n(),
         this.func_174877_v().func_177956_o(),
         this.func_174877_v().func_177952_p(),
         this.func_174877_v().func_177958_n() + 1,
         this.func_174877_v().func_177956_o() + 2,
         this.func_174877_v().func_177952_p() + 1
      );
   }

   @Override
   public boolean func_94041_b(int par1, ItemStack stack2) {
      return stack2.func_190926_b() || this.func_70301_a(par1).func_190926_b();
   }

   public void setInventorySlotContentsFromInfusion(int par1, ItemStack stack2) {
      this.func_70299_a(par1, stack2);
      this.func_70296_d();
      if (!this.field_145850_b.field_72995_K) {
         this.syncTile(false);
      }
   }

   public BlockPos findInstabilityMitigator() {
      if (this.func_145832_p() > 0) {
         BlockPos pp = this.seekSourceRecursive(this.field_174879_c, this.func_145832_p());
         if (pp != null) {
            return pp;
         }
      }

      return null;
   }

   private BlockPos seekSourceRecursive(BlockPos pos, int lastCharge) {
      for (EnumFacing face : EnumFacing.field_176754_o) {
         BlockPos pp = pos.func_177972_a(face);
         int ss = BlockInlay.getSourceStrengthAt(this.field_145850_b, pp);
         if (ss >= 5) {
            return pp;
         }

         IBlockState bs = this.field_145850_b.func_180495_p(pp);
         if (bs.func_177228_b().containsKey(BlockInlay.CHARGE)) {
            int charge = (Integer)bs.func_177229_b(BlockInlay.CHARGE);
            if (charge > lastCharge) {
               BlockPos ob = this.seekSourceRecursive(pp, charge);
               if (ob != null) {
                  return ob;
               }
            }
         }
      }

      return null;
   }

   public boolean func_145842_c(int i, int j) {
      if (i == 11) {
         if (this.field_145850_b.field_72995_K) {
            FXDispatcher.INSTANCE.drawBamf(this.field_174879_c.func_177984_a(), 0.75F, 0.0F, 0.5F, true, true, null);
         }

         return true;
      } else if (i == 12) {
         if (this.field_145850_b.field_72995_K) {
            FXDispatcher.INSTANCE.drawBamf(this.field_174879_c.func_177984_a(), true, true, null);
         }

         return true;
      } else if (i == 5) {
         if (this.field_145850_b.field_72995_K) {
            FXDispatcher.INSTANCE.drawPedestalShield(this.field_174879_c);
         }

         return true;
      } else {
         return super.func_145842_c(i, j);
      }
   }
}
