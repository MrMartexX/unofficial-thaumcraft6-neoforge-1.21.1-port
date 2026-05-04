package thaumcraft.client.renderers.models.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;

public class ModelTaintSeed extends ModelBase {
   public ModelRenderer tentacle = new ModelRendererTaintSeed(this);
   public ModelRenderer[] tents;
   public ModelRenderer orb = new ModelRendererTaintSeed(this);
   private int length = 8;

   public ModelTaintSeed() {
      this.field_78089_u = 64;
      this.field_78090_t = 64;
      this.tentacle = new ModelRendererTaintSeed(this, 0, 0);
      this.tentacle.func_78789_a(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      this.tentacle.field_78800_c = 0.0F;
      this.tentacle.field_78798_e = 0.0F;
      this.tentacle.field_78797_d = 12.0F;
      this.tents = new ModelRendererTaintSeed[this.length];

      for (int k = 0; k < this.length - 1; k++) {
         this.tents[k] = new ModelRendererTaintSeed(this, 0, k < this.length - 4 ? 16 : (k == this.length - 4 ? 48 : 56));
         if (k < this.length - 4) {
            this.tents[k].func_78789_a(-4.0F, -4.0F, -4.0F, 8, 8, 8);
            this.tents[k].field_78797_d = -8.0F;
         } else {
            this.tents[k].func_78789_a(-2.0F, -2.0F, -2.0F, 4, 4, 4);
            this.tents[k].field_78797_d = k == this.length - 4 ? -8.0F : -4.0F;
         }

         if (k == 0) {
            this.tentacle.func_78792_a(this.tents[k]);
         } else {
            this.tents[k - 1].func_78792_a(this.tents[k]);
         }
      }
   }

   public void func_78087_a(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
      float flail = 0.0F;
      float ht = 0.0F;
      int at = 0;
      EntityTaintSeed seed = (EntityTaintSeed)entity;
      ht = seed.field_70737_aN / 200.0F;
      flail = 0.1F;
      float mod = par6 * 0.2F;
      if (flail > 1.0F) {
         float fs = 3.0F;
      } else {
         float fs = 1.0F + (flail > 1.0F ? mod : -mod);
      }

      float fi = flail + (!(ht > 0.0F) && at <= 0 ? -mod : mod);
      fi *= 3.0F;
      this.tentacle.field_78795_f = 0.0F;

      for (int k = 0; k < this.length - 1; k++) {
         this.tents[k].field_78795_f = 0.1F / fi * MathHelper.func_76126_a(par3 * 0.06F - k / 2.0F) / 5.0F + ht + seed.attackAnim;
         this.tents[k].field_78808_h = 0.1F / fi * MathHelper.func_76126_a(par3 * 0.05F - k / 2.0F) / 5.0F;
      }
   }

   public void func_78088_a(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
      this.func_78087_a(par2, par3, par4, par5, par6, par7, par1Entity);
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      float height = 0.0F;
      float hc = par1Entity.field_70131_O * 10.0F;
      if (par1Entity.field_70173_aa < hc) {
         height = (hc - par1Entity.field_70173_aa) / hc * par1Entity.field_70131_O;
      }

      GL11.glTranslatef(0.0F, (par1Entity.field_70131_O == 3.0F ? 0.6F : 1.2F) + height, 0.0F);
      GL11.glScalef(par1Entity.field_70131_O / 2.0F, par1Entity.field_70131_O / 2.0F, par1Entity.field_70131_O / 2.0F);
      ((ModelRendererTaintSeed)this.tentacle).render(par7, par1Entity.field_70173_aa + Minecraft.func_71410_x().func_184121_ak(), 1.6F);
      GL11.glDisable(3042);
      GL11.glPopMatrix();
   }
}
