package thaumcraft.common.items.consumables;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.blocks.ILabelable;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCEssentiaContainer;

public class ItemLabel extends ItemTCEssentiaContainer {
   public ItemLabel() {
      super("label", 1, "blank", "filled");
   }

   public String func_77667_c(ItemStack stack) {
      return super.func_77658_a() + "." + this.getVariantNames()[stack.func_77952_i()];
   }

   @Override
   public void func_150895_a(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (tab == ConfigItems.TABTC || tab == CreativeTabs.field_78027_g) {
         items.add(new ItemStack(this, 1, 0));
      }
   }

   public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
      if (world.field_72995_K) {
         return EnumActionResult.PASS;
      }

      IBlockState bs = world.func_180495_p(pos);
      if (bs.func_177230_c() instanceof ILabelable) {
         if (((ILabelable)bs.func_177230_c()).applyLabel(player, pos, side, player.func_184586_b(hand))) {
            player.func_184586_b(hand).func_190918_g(1);
            player.field_71069_bz.func_75142_b();
         }

         return EnumActionResult.SUCCESS;
      } else {
         TileEntity te = world.func_175625_s(pos);
         if (te instanceof ILabelable) {
            if (((ILabelable)te).applyLabel(player, pos, side, player.func_184586_b(hand))) {
               player.func_184586_b(hand).func_190918_g(1);
               player.field_71069_bz.func_75142_b();
            }

            return EnumActionResult.SUCCESS;
         } else {
            return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
         }
      }
   }

   @Override
   public void func_77663_a(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
   }

   @Override
   public void func_77622_d(ItemStack stack, World world, EntityPlayer player) {
   }

   @Override
   public boolean ignoreContainedAspects() {
      return true;
   }
}
