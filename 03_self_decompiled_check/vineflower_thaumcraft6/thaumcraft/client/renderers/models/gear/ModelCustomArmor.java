package thaumcraft.client.renderers.models.gear;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.lib.UtilsFX;

public class ModelCustomArmor extends ModelBiped {
   public ModelCustomArmor(float f, int i, int j, int k) {
      super(f, i, j, k);
   }

   public void func_78087_a(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      if (entityIn instanceof EntityLivingBase) {
         this.field_78095_p = ((EntityLivingBase)entityIn).func_70678_g(UtilsFX.sysPartialTicks);
      }

      if (entityIn instanceof EntityArmorStand) {
         this.setRotationAnglesStand(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      } else if (!(entityIn instanceof EntitySkeleton) && !(entityIn instanceof EntityZombie)) {
         boolean flag = entityIn instanceof EntityLivingBase && ((EntityLivingBase)entityIn).func_184599_cB() > 4;
         this.field_78116_c.field_78796_g = netHeadYaw * (float) (Math.PI / 180.0);
         if (flag) {
            this.field_78116_c.field_78795_f = (float) (-Math.PI / 4);
         } else {
            this.field_78116_c.field_78795_f = headPitch * (float) (Math.PI / 180.0);
         }

         this.field_78115_e.field_78796_g = 0.0F;
         this.field_178723_h.field_78798_e = 0.0F;
         this.field_178723_h.field_78800_c = -5.0F;
         this.field_178724_i.field_78798_e = 0.0F;
         this.field_178724_i.field_78800_c = 5.0F;
         float f = 1.0F;
         if (flag) {
            f = (float)(
               entityIn.field_70159_w * entityIn.field_70159_w
                  + entityIn.field_70181_x * entityIn.field_70181_x
                  + entityIn.field_70179_y * entityIn.field_70179_y
            );
            f /= 0.2F;
            f = f * f * f;
         }

         if (f < 1.0F) {
            f = 1.0F;
         }

         this.field_178723_h.field_78795_f = MathHelper.func_76134_b(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
         this.field_178724_i.field_78795_f = MathHelper.func_76134_b(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
         this.field_178723_h.field_78808_h = 0.0F;
         this.field_178724_i.field_78808_h = 0.0F;
         this.field_178721_j.field_78795_f = MathHelper.func_76134_b(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
         this.field_178722_k.field_78795_f = MathHelper.func_76134_b(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount / f;
         this.field_178721_j.field_78796_g = 0.0F;
         this.field_178722_k.field_78796_g = 0.0F;
         this.field_178721_j.field_78808_h = 0.0F;
         this.field_178722_k.field_78808_h = 0.0F;
         if (this.field_78093_q) {
            this.field_178723_h.field_78795_f += (float) (-Math.PI / 5);
            this.field_178724_i.field_78795_f += (float) (-Math.PI / 5);
            this.field_178721_j.field_78795_f = -1.4137167F;
            this.field_178721_j.field_78796_g = (float) (Math.PI / 10);
            this.field_178721_j.field_78808_h = 0.07853982F;
            this.field_178722_k.field_78795_f = -1.4137167F;
            this.field_178722_k.field_78796_g = (float) (-Math.PI / 10);
            this.field_178722_k.field_78808_h = -0.07853982F;
         }

         this.field_178723_h.field_78796_g = 0.0F;
         this.field_178723_h.field_78808_h = 0.0F;
         switch (this.field_187075_l) {
            case EMPTY:
               this.field_178724_i.field_78796_g = 0.0F;
               break;
            case BLOCK:
               this.field_178724_i.field_78795_f = this.field_178724_i.field_78795_f * 0.5F - 0.9424779F;
               this.field_178724_i.field_78796_g = (float) (Math.PI / 6);
               break;
            case ITEM:
               this.field_178724_i.field_78795_f = this.field_178724_i.field_78795_f * 0.5F - (float) (Math.PI / 10);
               this.field_178724_i.field_78796_g = 0.0F;
         }

         switch (this.field_187076_m) {
            case EMPTY:
               this.field_178723_h.field_78796_g = 0.0F;
               break;
            case BLOCK:
               this.field_178723_h.field_78795_f = this.field_178723_h.field_78795_f * 0.5F - 0.9424779F;
               this.field_178723_h.field_78796_g = (float) (-Math.PI / 6);
               break;
            case ITEM:
               this.field_178723_h.field_78795_f = this.field_178723_h.field_78795_f * 0.5F - (float) (Math.PI / 10);
               this.field_178723_h.field_78796_g = 0.0F;
         }

         if (this.field_78095_p > 0.0F) {
            EnumHandSide enumhandside = this.func_187072_a(entityIn);
            ModelRenderer modelrenderer = this.func_187074_a(enumhandside);
            float f1 = this.field_78095_p;
            this.field_78115_e.field_78796_g = MathHelper.func_76126_a(MathHelper.func_76129_c(f1) * (float) (Math.PI * 2)) * 0.2F;
            if (enumhandside == EnumHandSide.LEFT) {
               this.field_78115_e.field_78796_g *= -1.0F;
            }

            this.field_178723_h.field_78798_e = MathHelper.func_76126_a(this.field_78115_e.field_78796_g) * 5.0F;
            this.field_178723_h.field_78800_c = -MathHelper.func_76134_b(this.field_78115_e.field_78796_g) * 5.0F;
            this.field_178724_i.field_78798_e = -MathHelper.func_76126_a(this.field_78115_e.field_78796_g) * 5.0F;
            this.field_178724_i.field_78800_c = MathHelper.func_76134_b(this.field_78115_e.field_78796_g) * 5.0F;
            this.field_178723_h.field_78796_g = this.field_178723_h.field_78796_g + this.field_78115_e.field_78796_g;
            this.field_178724_i.field_78796_g = this.field_178724_i.field_78796_g + this.field_78115_e.field_78796_g;
            this.field_178724_i.field_78795_f = this.field_178724_i.field_78795_f + this.field_78115_e.field_78796_g;
            f1 = 1.0F - this.field_78095_p;
            f1 *= f1;
            f1 *= f1;
            f1 = 1.0F - f1;
            float f2 = MathHelper.func_76126_a(f1 * (float) Math.PI);
            float f3 = MathHelper.func_76126_a(this.field_78095_p * (float) Math.PI) * -(this.field_78116_c.field_78795_f - 0.7F) * 0.75F;
            modelrenderer.field_78795_f = (float)(modelrenderer.field_78795_f - (f2 * 1.2 + f3));
            modelrenderer.field_78796_g = modelrenderer.field_78796_g + this.field_78115_e.field_78796_g * 2.0F;
            modelrenderer.field_78808_h = modelrenderer.field_78808_h + MathHelper.func_76126_a(this.field_78095_p * (float) Math.PI) * -0.4F;
         }

         if (this.field_78117_n) {
            this.field_78115_e.field_78795_f = 0.5F;
            this.field_178723_h.field_78795_f += 0.4F;
            this.field_178724_i.field_78795_f += 0.4F;
            this.field_178721_j.field_78798_e = 4.0F;
            this.field_178722_k.field_78798_e = 4.0F;
            this.field_178721_j.field_78797_d = 13.0F;
            this.field_178722_k.field_78797_d = 13.0F;
            this.field_78116_c.field_78797_d = 4.5F;
            this.field_78115_e.field_78797_d = 4.5F;
            this.field_178723_h.field_78797_d = 5.0F;
            this.field_178724_i.field_78797_d = 5.0F;
         } else {
            this.field_78115_e.field_78795_f = 0.0F;
            this.field_178721_j.field_78798_e = 0.1F;
            this.field_178722_k.field_78798_e = 0.1F;
            this.field_178721_j.field_78797_d = 12.0F;
            this.field_178722_k.field_78797_d = 12.0F;
            this.field_78116_c.field_78797_d = 0.0F;
            this.field_78115_e.field_78797_d = 0.0F;
            this.field_178723_h.field_78797_d = 2.0F;
            this.field_178724_i.field_78797_d = 2.0F;
         }

         this.field_178723_h.field_78808_h = this.field_178723_h.field_78808_h + (MathHelper.func_76134_b(ageInTicks * 0.09F) * 0.05F + 0.05F);
         this.field_178724_i.field_78808_h = this.field_178724_i.field_78808_h - (MathHelper.func_76134_b(ageInTicks * 0.09F) * 0.05F + 0.05F);
         this.field_178723_h.field_78795_f = this.field_178723_h.field_78795_f + MathHelper.func_76126_a(ageInTicks * 0.067F) * 0.05F;
         this.field_178724_i.field_78795_f = this.field_178724_i.field_78795_f - MathHelper.func_76126_a(ageInTicks * 0.067F) * 0.05F;
         if (this.field_187076_m == ArmPose.BOW_AND_ARROW) {
            this.field_178723_h.field_78796_g = -0.1F + this.field_78116_c.field_78796_g;
            this.field_178724_i.field_78796_g = 0.1F + this.field_78116_c.field_78796_g + 0.4F;
            this.field_178723_h.field_78795_f = (float) (-Math.PI / 2) + this.field_78116_c.field_78795_f;
            this.field_178724_i.field_78795_f = (float) (-Math.PI / 2) + this.field_78116_c.field_78795_f;
         } else if (this.field_187075_l == ArmPose.BOW_AND_ARROW) {
            this.field_178723_h.field_78796_g = -0.1F + this.field_78116_c.field_78796_g - 0.4F;
            this.field_178724_i.field_78796_g = 0.1F + this.field_78116_c.field_78796_g;
            this.field_178723_h.field_78795_f = (float) (-Math.PI / 2) + this.field_78116_c.field_78795_f;
            this.field_178724_i.field_78795_f = (float) (-Math.PI / 2) + this.field_78116_c.field_78795_f;
         }

         func_178685_a(this.field_78116_c, this.field_178720_f);
      } else {
         this.setRotationAnglesZombie(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      }
   }

   public void setRotationAnglesZombie(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      super.func_78087_a(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      boolean flag = entityIn instanceof EntityZombie && ((EntityZombie)entityIn).func_184734_db();
      float f = MathHelper.func_76126_a(this.field_78095_p * (float) Math.PI);
      float f1 = MathHelper.func_76126_a((1.0F - (1.0F - this.field_78095_p) * (1.0F - this.field_78095_p)) * (float) Math.PI);
      this.field_178723_h.field_78808_h = 0.0F;
      this.field_178724_i.field_78808_h = 0.0F;
      this.field_178723_h.field_78796_g = -(0.1F - f * 0.6F);
      this.field_178724_i.field_78796_g = 0.1F - f * 0.6F;
      float f2 = (float) -Math.PI / (flag ? 1.5F : 2.25F);
      this.field_178723_h.field_78795_f = f2;
      this.field_178724_i.field_78795_f = f2;
      this.field_178723_h.field_78795_f += f * 1.2F - f1 * 0.4F;
      this.field_178724_i.field_78795_f += f * 1.2F - f1 * 0.4F;
      this.field_178723_h.field_78808_h = this.field_178723_h.field_78808_h + (MathHelper.func_76134_b(ageInTicks * 0.09F) * 0.05F + 0.05F);
      this.field_178724_i.field_78808_h = this.field_178724_i.field_78808_h - (MathHelper.func_76134_b(ageInTicks * 0.09F) * 0.05F + 0.05F);
      this.field_178723_h.field_78795_f = this.field_178723_h.field_78795_f + MathHelper.func_76126_a(ageInTicks * 0.067F) * 0.05F;
      this.field_178724_i.field_78795_f = this.field_178724_i.field_78795_f - MathHelper.func_76126_a(ageInTicks * 0.067F) * 0.05F;
   }

   public void setRotationAnglesStand(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      if (entityIn instanceof EntityArmorStand) {
         EntityArmorStand entityarmorstand = (EntityArmorStand)entityIn;
         this.field_78116_c.field_78795_f = (float) (Math.PI / 180.0) * entityarmorstand.func_175418_s().func_179415_b();
         this.field_78116_c.field_78796_g = (float) (Math.PI / 180.0) * entityarmorstand.func_175418_s().func_179416_c();
         this.field_78116_c.field_78808_h = (float) (Math.PI / 180.0) * entityarmorstand.func_175418_s().func_179413_d();
         this.field_78116_c.func_78793_a(0.0F, 1.0F, 0.0F);
         this.field_78115_e.field_78795_f = (float) (Math.PI / 180.0) * entityarmorstand.func_175408_t().func_179415_b();
         this.field_78115_e.field_78796_g = (float) (Math.PI / 180.0) * entityarmorstand.func_175408_t().func_179416_c();
         this.field_78115_e.field_78808_h = (float) (Math.PI / 180.0) * entityarmorstand.func_175408_t().func_179413_d();
         this.field_178724_i.field_78795_f = (float) (Math.PI / 180.0) * entityarmorstand.func_175404_u().func_179415_b();
         this.field_178724_i.field_78796_g = (float) (Math.PI / 180.0) * entityarmorstand.func_175404_u().func_179416_c();
         this.field_178724_i.field_78808_h = (float) (Math.PI / 180.0) * entityarmorstand.func_175404_u().func_179413_d();
         this.field_178723_h.field_78795_f = (float) (Math.PI / 180.0) * entityarmorstand.func_175411_v().func_179415_b();
         this.field_178723_h.field_78796_g = (float) (Math.PI / 180.0) * entityarmorstand.func_175411_v().func_179416_c();
         this.field_178723_h.field_78808_h = (float) (Math.PI / 180.0) * entityarmorstand.func_175411_v().func_179413_d();
         this.field_178722_k.field_78795_f = (float) (Math.PI / 180.0) * entityarmorstand.func_175403_w().func_179415_b();
         this.field_178722_k.field_78796_g = (float) (Math.PI / 180.0) * entityarmorstand.func_175403_w().func_179416_c();
         this.field_178722_k.field_78808_h = (float) (Math.PI / 180.0) * entityarmorstand.func_175403_w().func_179413_d();
         this.field_178722_k.func_78793_a(1.9F, 11.0F, 0.0F);
         this.field_178721_j.field_78795_f = (float) (Math.PI / 180.0) * entityarmorstand.func_175407_x().func_179415_b();
         this.field_178721_j.field_78796_g = (float) (Math.PI / 180.0) * entityarmorstand.func_175407_x().func_179416_c();
         this.field_178721_j.field_78808_h = (float) (Math.PI / 180.0) * entityarmorstand.func_175407_x().func_179413_d();
         this.field_178721_j.func_78793_a(-1.9F, 11.0F, 0.0F);
         func_178685_a(this.field_78116_c, this.field_178720_f);
      }
   }
}
