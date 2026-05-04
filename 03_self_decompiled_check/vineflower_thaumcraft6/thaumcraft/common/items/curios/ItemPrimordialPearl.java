package thaumcraft.common.items.curios;

import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.items.ItemTCBase;

public class ItemPrimordialPearl extends ItemTCBase {
   public ItemPrimordialPearl() {
      super("primordial_pearl");
      this.field_77777_bU = 1;
      this.func_77656_e(8);
      this.func_185043_a(new ResourceLocation("type"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float func_185085_a(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            if (stack.func_77952_i() < 3) {
               return 0.0F;
            } else {
               return stack.func_77952_i() < 6 ? 1.0F : 2.0F;
            }
         }
      });
      this.setNoRepair();
   }

   public boolean func_82789_a(ItemStack toRepair, ItemStack repair) {
      return false;
   }

   public boolean isRepairable() {
      return false;
   }

   @Override
   public String func_77667_c(ItemStack stack) {
      if (stack.func_77952_i() < 3) {
         return super.func_77658_a() + ".pearl";
      } else {
         return stack.func_77952_i() < 6 ? super.func_77658_a() + ".nodule" : super.func_77658_a() + ".mote";
      }
   }

   public ItemStack getContainerItem(ItemStack itemStack) {
      return !this.hasContainerItem(itemStack)
         ? ItemStack.field_190927_a
         : new ItemStack(itemStack.func_77973_b(), itemStack.func_190916_E(), itemStack.func_77952_i() + 1);
   }

   public boolean hasContainerItem(ItemStack stack) {
      return stack.func_77952_i() < 7;
   }

   public EnumRarity func_77613_e(ItemStack stack) {
      return EnumRarity.UNCOMMON;
   }

   public boolean func_77616_k(ItemStack stack) {
      return false;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return false;
   }
}
