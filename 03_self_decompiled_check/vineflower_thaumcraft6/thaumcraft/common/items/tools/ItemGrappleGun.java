package thaumcraft.common.items.tools;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.entities.projectile.EntityGrapple;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;

public class ItemGrappleGun extends ItemTCBase implements IRechargable {
   public ItemGrappleGun() {
      super("grapple_gun");
      this.func_77625_d(1);
      this.func_185043_a(new ResourceLocation("type"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float func_185085_a(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            return stack.func_77942_o() && stack.func_77978_p().func_74771_c("loaded") == 1 ? 1.0F : 0.0F;
         }
      });
   }

   @Override
   public int getMaxCharge(ItemStack stack, EntityLivingBase player) {
      return 100;
   }

   @Override
   public IRechargable.EnumChargeDisplay showInHud(ItemStack stack, EntityLivingBase player) {
      return IRechargable.EnumChargeDisplay.NORMAL;
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.UNCOMMON;
   }

   public void func_77663_a(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      if (!EntityGrapple.grapples.containsKey(entityIn.func_145782_y()) && stack.func_77942_o() && stack.func_77978_p().func_74771_c("loaded") == 1) {
         stack.func_77983_a("loaded", new NBTTagByte((byte)0));
      }
   }

   public ActionResult<ItemStack> func_77659_a(World world, EntityPlayer player, EnumHand hand) {
      player.func_184185_a(SoundsTC.ice, 3.0F, 0.8F + world.field_73012_v.nextFloat() * 0.1F);
      if (!world.field_72995_K && RechargeHelper.getCharge(player.func_184586_b(hand)) > 0) {
         EntityGrapple grapple = new EntityGrapple(world, player, hand);
         grapple.func_184538_a(player, player.field_70125_A, player.field_70177_z, -5.0F, 1.5F, 0.0F);
         double px = -MathHelper.func_76134_b((player.field_70177_z - 0.5F) / 180.0F * 3.141593F) * 0.2F * (grapple.hand == EnumHand.MAIN_HAND ? 1 : -1);
         double pz = -MathHelper.func_76126_a((player.field_70177_z - 0.5F) / 180.0F * 3.141593F) * 0.3F * (grapple.hand == EnumHand.MAIN_HAND ? 1 : -1);
         Vec3d vl = player.func_70040_Z();
         grapple.field_70165_t = grapple.field_70165_t + (px + vl.field_72450_a);
         grapple.field_70161_v = grapple.field_70161_v + (pz + vl.field_72448_b);
         if (world.func_72838_d(grapple)) {
            RechargeHelper.consumeCharge(player.func_184586_b(hand), player, 1);
            player.func_184586_b(hand).func_77983_a("loaded", new NBTTagByte((byte)1));
         }
      }

      return new ActionResult(EnumActionResult.SUCCESS, player.func_184586_b(hand));
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      if (oldStack.func_77973_b() != null
         && oldStack.func_77973_b() == this
         && newStack.func_77973_b() != null
         && newStack.func_77973_b() == this
         && !ItemStack.func_77970_a(oldStack, newStack)) {
         boolean b1 = !oldStack.func_77942_o() || oldStack.func_77978_p().func_74771_c("loaded") == 0;
         boolean b2 = !newStack.func_77942_o() || newStack.func_77978_p().func_74771_c("loaded") == 0;
         return b1 != b2;
      } else {
         return newStack.func_77973_b() != oldStack.func_77973_b();
      }
   }
}
