package thaumcraft.common.items.curios;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.Utils;

public class ItemLootBag extends ItemTCBase {
   public ItemLootBag() {
      super("loot_bag", "common", "uncommon", "rare");
      this.func_77625_d(16);
   }

   public EnumRarity func_77613_e(ItemStack stack) {
      switch (stack.func_77952_i()) {
         case 1:
            return EnumRarity.UNCOMMON;
         case 2:
            return EnumRarity.RARE;
         default:
            return EnumRarity.COMMON;
      }
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.func_77624_a(stack, worldIn, tooltip, flagIn);
      tooltip.add(I18n.func_74838_a("tc.lootbag"));
   }

   public ActionResult<ItemStack> func_77659_a(World world, EntityPlayer player, EnumHand hand) {
      if (!world.field_72995_K) {
         int q = 8 + world.field_73012_v.nextInt(5);

         for (int a = 0; a < q; a++) {
            ItemStack is = Utils.generateLoot(player.func_184586_b(hand).func_77952_i(), world.field_73012_v);
            if (is != null && !is.func_190926_b()) {
               world.func_72838_d(new EntityItem(world, player.field_70165_t, player.field_70163_u, player.field_70161_v, is.func_77946_l()));
            }
         }

         player.func_184185_a(SoundsTC.coins, 0.75F, 1.0F);
      }

      player.func_184586_b(hand).func_190918_g(1);
      return new ActionResult(EnumActionResult.SUCCESS, player.func_184586_b(hand));
   }
}
