package thaumcraft.common.items.baubles;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.items.ItemTCBase;

public class ItemAmuletVis extends ItemTCBase implements IBauble {
   public ItemAmuletVis() {
      super("amulet_vis", "found", "crafted");
      this.field_77777_bU = 1;
      this.func_77656_e(0);
      this.func_77627_a(true);
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return itemstack.func_77952_i() == 0 ? EnumRarity.UNCOMMON : EnumRarity.RARE;
   }

   @Override
   public BaubleType getBaubleType(ItemStack itemstack) {
      return BaubleType.AMULET;
   }

   @Override
   public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
      if (player instanceof EntityPlayer && !player.field_70170_p.field_72995_K && player.field_70173_aa % (itemstack.func_77952_i() == 0 ? 40 : 5) == 0) {
         NonNullList<ItemStack> inv = ((EntityPlayer)player).field_71071_by.field_70462_a;

         for (int a = 0; a < InventoryPlayer.func_70451_h(); a++) {
            if (RechargeHelper.rechargeItem(player.field_70170_p, (ItemStack)inv.get(a), player.func_180425_c(), (EntityPlayer)player, 1) > 0.0F) {
               return;
            }
         }

         IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer)player);

         for (int a = 0; a < baubles.getSlots(); a++) {
            if (RechargeHelper.rechargeItem(player.field_70170_p, baubles.getStackInSlot(a), player.func_180425_c(), (EntityPlayer)player, 1) > 0.0F) {
               return;
            }
         }

         inv = ((EntityPlayer)player).field_71071_by.field_70460_b;

         for (int a = 0; a < inv.size(); a++) {
            if (RechargeHelper.rechargeItem(player.field_70170_p, (ItemStack)inv.get(a), player.func_180425_c(), (EntityPlayer)player, 1) > 0.0F) {
               return;
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(TextFormatting.AQUA + I18n.func_74838_a("item.amulet_vis.text"));
   }
}
