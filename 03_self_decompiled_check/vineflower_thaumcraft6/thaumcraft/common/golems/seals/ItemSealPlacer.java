package thaumcraft.common.golems.seals;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCBase;

public class ItemSealPlacer extends ItemTCBase implements ISealDisplayer {
   public ItemSealPlacer() {
      super("seal", "blank");
      this.func_77625_d(64);
      this.func_77627_a(true);
      this.func_77656_e(0);
   }

   @Override
   public void func_150895_a(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (tab == ConfigItems.TABTC || tab == CreativeTabs.field_78027_g) {
         String[] vn = this.getVariantNames();

         for (int a = 0; a < vn.length; a++) {
            items.add(new ItemStack(this, 1, a));
         }
      }
   }

   @Override
   public String[] getVariantNames() {
      if (SealHandler.types.size() + 1 != this.VARIANTS.length) {
         String[] rs = SealHandler.getRegisteredSeals();
         String[] out = new String[rs.length + 1];
         out[0] = "blank";
         int indx = 1;

         for (String s : rs) {
            String[] sp = s.split(":");
            out[indx] = sp.length > 1 ? sp[1] : sp[0];
            indx++;
         }

         this.VARIANTS = out;
         this.VARIANTS_META = new int[this.VARIANTS.length];
         int m = 0;

         while (m < this.VARIANTS.length) {
            this.VARIANTS_META[m] = m++;
         }
      }

      return this.VARIANTS;
   }

   public static ItemStack getSealStack(String sealKey) {
      String[] rs = SealHandler.getRegisteredSeals();
      int indx = 1;

      for (String s : rs) {
         if (s.equals(sealKey)) {
            return new ItemStack(ItemsTC.seals, 1, indx);
         }

         indx++;
      }

      return null;
   }

   public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
      if (world.field_72995_K || player.func_184586_b(hand).func_77952_i() == 0 || player.func_70093_af()) {
         return EnumActionResult.PASS;
      }

      if (!player.func_175151_a(pos, side, player.func_184586_b(hand))) {
         return EnumActionResult.FAIL;
      }

      String[] rs = SealHandler.getRegisteredSeals();
      ISeal seal = null;

      try {
         seal = (ISeal)SealHandler.getSeal(rs[player.func_184586_b(hand).func_77952_i() - 1]).getClass().newInstance();
      } catch (Exception e) {
         e.printStackTrace();
      }

      if (seal != null && seal.canPlaceAt(world, pos, side)) {
         if (SealHandler.addSealEntity(world, pos, side, seal, player) && !player.field_71075_bZ.field_75098_d) {
            player.func_184586_b(hand).func_190918_g(1);
         }

         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.FAIL;
      }
   }

   public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
      return true;
   }
}
