package thaumcraft.common.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCBase;

public class ItemVerdantCharm extends ItemTCBase implements IBauble, IRechargable {
   public ItemVerdantCharm() {
      super("verdant_charm");
      this.field_77777_bU = 1;
      this.canRepair = false;
      this.func_77656_e(0);
      this.func_185043_a(new ResourceLocation("type"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float func_185085_a(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            return stack.func_77973_b() instanceof ItemVerdantCharm && stack.func_77942_o() ? stack.func_77978_p().func_74771_c("type") : 0.0F;
         }
      });
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.RARE;
   }

   @Override
   public BaubleType getBaubleType(ItemStack itemstack) {
      return BaubleType.CHARM;
   }

   @Override
   public void func_150895_a(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (tab == ConfigItems.TABTC || tab == CreativeTabs.field_78027_g) {
         items.add(new ItemStack(this));
         ItemStack vhbl = new ItemStack(this);
         vhbl.func_77983_a("type", new NBTTagByte((byte)1));
         items.add(vhbl);
         ItemStack vhbl2 = new ItemStack(this);
         vhbl2.func_77983_a("type", new NBTTagByte((byte)2));
         items.add(vhbl2);
      }
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (stack.func_77942_o() && stack.func_77978_p().func_74771_c("type") == 1) {
         tooltip.add(TextFormatting.GOLD + I18n.func_74838_a("item.verdant_charm.life.text"));
      }

      if (stack.func_77942_o() && stack.func_77978_p().func_74771_c("type") == 2) {
         tooltip.add(TextFormatting.GOLD + I18n.func_74838_a("item.verdant_charm.sustain.text"));
      }
   }

   @Override
   public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
      if (!player.field_70170_p.field_72995_K && player.field_70173_aa % 20 == 0 && player instanceof EntityPlayer) {
         if (player.func_70660_b(MobEffects.field_82731_v) != null && RechargeHelper.consumeCharge(itemstack, player, 20)) {
            player.func_184589_d(MobEffects.field_82731_v);
            return;
         }

         if (player.func_70660_b(MobEffects.field_76436_u) != null && RechargeHelper.consumeCharge(itemstack, player, 10)) {
            player.func_184589_d(MobEffects.field_76436_u);
            return;
         }

         if (player.func_70660_b(PotionFluxTaint.instance) != null && RechargeHelper.consumeCharge(itemstack, player, 5)) {
            player.func_184589_d(PotionFluxTaint.instance);
            return;
         }

         if (itemstack.func_77942_o()
            && itemstack.func_77978_p().func_74771_c("type") == 1
            && player.func_110143_aJ() < player.func_110138_aP()
            && RechargeHelper.consumeCharge(itemstack, player, 5)) {
            player.func_70691_i(1.0F);
            return;
         }

         if (itemstack.func_77942_o() && itemstack.func_77978_p().func_74771_c("type") == 2) {
            if (player.func_70086_ai() < 100 && RechargeHelper.consumeCharge(itemstack, player, 1)) {
               player.func_70050_g(300);
               return;
            }

            if (player instanceof EntityPlayer && ((EntityPlayer)player).func_71043_e(false) && RechargeHelper.consumeCharge(itemstack, player, 1)) {
               ((EntityPlayer)player).func_71024_bL().func_75122_a(1, 0.3F);
               return;
            }
         }
      }
   }

   @Override
   public int getMaxCharge(ItemStack stack, EntityLivingBase player) {
      return 200;
   }

   @Override
   public IRechargable.EnumChargeDisplay showInHud(ItemStack stack, EntityLivingBase player) {
      return IRechargable.EnumChargeDisplay.NORMAL;
   }

   @Override
   public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
      return true;
   }
}
