package thaumcraft.common.entities.ai.combat;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

public class EntityCritterAIAttackMelee extends EntityAIAttackMelee {
   public EntityCritterAIAttackMelee(EntityCreature creature, double speedIn, boolean useLongMemory) {
      super(creature, speedIn, useLongMemory);
   }

   protected void func_190102_a(EntityLivingBase target, double range) {
      double d0 = this.func_179512_a(target);
      if (range <= d0 && this.field_75439_d <= 0) {
         this.field_75439_d = 20;
         this.field_75441_b.func_184609_a(EnumHand.MAIN_HAND);
         this.attackEntityAsMob(this.field_75441_b, target);
      }
   }

   protected boolean attackEntityAsMob(EntityLiving attacker, Entity target) {
      float f = Math.max(2.0F, (attacker.field_70131_O + attacker.field_70130_N) * 2.0F);
      if (attacker.func_110148_a(SharedMonsterAttributes.field_111264_e) != null) {
         f = (float)attacker.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e();
      }

      int i = 0;
      if (target instanceof EntityLivingBase) {
         f += EnchantmentHelper.func_152377_a(attacker.func_184614_ca(), ((EntityLivingBase)target).func_70668_bt());
         i += EnchantmentHelper.func_77501_a(attacker);
      }

      boolean flag = target.func_70097_a(DamageSource.func_76358_a(attacker), f);
      if (flag) {
         if (i > 0 && target instanceof EntityLivingBase) {
            ((EntityLivingBase)target)
               .func_70653_a(
                  attacker,
                  i * 0.5F,
                  MathHelper.func_76126_a(attacker.field_70177_z * (float) (Math.PI / 180.0)),
                  -MathHelper.func_76134_b(attacker.field_70177_z * (float) (Math.PI / 180.0))
               );
            attacker.field_70159_w *= 0.6;
            attacker.field_70179_y *= 0.6;
         }

         int j = EnchantmentHelper.func_90036_a(attacker);
         if (j > 0) {
            target.func_70015_d(j * 4);
         }

         if (target instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)target;
            ItemStack itemstack = attacker.func_184614_ca();
            ItemStack itemstack1 = entityplayer.func_184587_cr() ? entityplayer.func_184607_cu() : ItemStack.field_190927_a;
            if (!itemstack.func_190926_b()
               && !itemstack1.func_190926_b()
               && itemstack.func_77973_b().canDisableShield(itemstack, itemstack1, entityplayer, attacker)
               && itemstack1.func_77973_b().isShield(itemstack1, entityplayer)) {
               float f1 = 0.25F + EnchantmentHelper.func_185293_e(attacker) * 0.05F;
               if (attacker.func_70681_au().nextFloat() < f1) {
                  entityplayer.func_184811_cZ().func_185145_a(itemstack1.func_77973_b(), 100);
                  attacker.field_70170_p.func_72960_a(entityplayer, (byte)30);
               }
            }
         }

         if (target instanceof EntityLivingBase) {
            EnchantmentHelper.func_151384_a((EntityLivingBase)target, attacker);
         }

         EnchantmentHelper.func_151385_b(attacker, target);
      }

      return flag;
   }
}
