package thaumcraft.client.renderers.models.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.entities.monster.EntityFireBat;

@SideOnly(Side.CLIENT)
public class ModelFireBat extends ModelBase {
   private ModelRenderer batHead;
   private ModelRenderer batBody;
   private ModelRenderer batRightWing;
   private ModelRenderer batLeftWing;
   private ModelRenderer batOuterRightWing;
   private ModelRenderer batOuterLeftWing;

   public ModelFireBat() {
      this.field_78090_t = 64;
      this.field_78089_u = 64;
      this.batHead = new ModelRenderer(this, 0, 0);
      this.batHead.func_78789_a(-3.0F, -3.0F, -3.0F, 6, 6, 6);
      ModelRenderer var1 = new ModelRenderer(this, 24, 0);
      var1.func_78789_a(-4.0F, -6.0F, -2.0F, 3, 4, 1);
      this.batHead.func_78792_a(var1);
      ModelRenderer var2 = new ModelRenderer(this, 24, 0);
      var2.field_78809_i = true;
      var2.func_78789_a(1.0F, -6.0F, -2.0F, 3, 4, 1);
      this.batHead.func_78792_a(var2);
      this.batBody = new ModelRenderer(this, 0, 16);
      this.batBody.func_78789_a(-3.0F, 4.0F, -3.0F, 6, 12, 6);
      this.batBody.func_78784_a(0, 34).func_78789_a(-5.0F, 16.0F, 0.0F, 10, 6, 1);
      this.batRightWing = new ModelRenderer(this, 42, 0);
      this.batRightWing.func_78789_a(-12.0F, 1.0F, 1.5F, 10, 16, 1);
      this.batOuterRightWing = new ModelRenderer(this, 24, 16);
      this.batOuterRightWing.func_78793_a(-12.0F, 1.0F, 1.5F);
      this.batOuterRightWing.func_78789_a(-8.0F, 1.0F, 0.0F, 8, 12, 1);
      this.batLeftWing = new ModelRenderer(this, 42, 0);
      this.batLeftWing.field_78809_i = true;
      this.batLeftWing.func_78789_a(2.0F, 1.0F, 1.5F, 10, 16, 1);
      this.batOuterLeftWing = new ModelRenderer(this, 24, 16);
      this.batOuterLeftWing.field_78809_i = true;
      this.batOuterLeftWing.func_78793_a(12.0F, 1.0F, 1.5F);
      this.batOuterLeftWing.func_78789_a(0.0F, 1.0F, 0.0F, 8, 12, 1);
      this.batBody.func_78792_a(this.batRightWing);
      this.batBody.func_78792_a(this.batLeftWing);
      this.batRightWing.func_78792_a(this.batOuterRightWing);
      this.batLeftWing.func_78792_a(this.batOuterLeftWing);
   }

   public int getBatSize() {
      return 36;
   }

   public void func_78088_a(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
      if (par1Entity instanceof EntityFireBat && ((EntityFireBat)par1Entity).getIsBatHanging()) {
         this.batHead.field_78795_f = par6 / (180.0F / (float)Math.PI);
         this.batHead.field_78796_g = (float) Math.PI - par5 / (180.0F / (float)Math.PI);
         this.batHead.field_78808_h = (float) Math.PI;
         this.batHead.func_78793_a(0.0F, -2.0F, 0.0F);
         this.batRightWing.func_78793_a(-3.0F, 0.0F, 3.0F);
         this.batLeftWing.func_78793_a(3.0F, 0.0F, 3.0F);
         this.batBody.field_78795_f = (float) Math.PI;
         this.batRightWing.field_78795_f = (float) (-Math.PI / 20);
         this.batRightWing.field_78796_g = (float) (-Math.PI * 2.0 / 5.0);
         this.batOuterRightWing.field_78796_g = -1.7278761F;
         this.batLeftWing.field_78795_f = this.batRightWing.field_78795_f;
         this.batLeftWing.field_78796_g = -this.batRightWing.field_78796_g;
         this.batOuterLeftWing.field_78796_g = -this.batOuterRightWing.field_78796_g;
      } else {
         this.batHead.field_78795_f = par6 / (180.0F / (float)Math.PI);
         this.batHead.field_78796_g = par5 / (180.0F / (float)Math.PI);
         this.batHead.field_78808_h = 0.0F;
         this.batHead.func_78793_a(0.0F, 0.0F, 0.0F);
         this.batRightWing.func_78793_a(0.0F, 0.0F, 0.0F);
         this.batLeftWing.func_78793_a(0.0F, 0.0F, 0.0F);
         this.batBody.field_78795_f = (float) (Math.PI / 4) + MathHelper.func_76134_b(par4 * 0.1F) * 0.15F;
         this.batBody.field_78796_g = 0.0F;
         this.batRightWing.field_78796_g = MathHelper.func_76134_b(par4 * 1.3F) * (float) Math.PI * 0.25F;
         this.batLeftWing.field_78796_g = -this.batRightWing.field_78796_g;
         this.batOuterRightWing.field_78796_g = this.batRightWing.field_78796_g * 0.5F;
         this.batOuterLeftWing.field_78796_g = -this.batRightWing.field_78796_g * 0.5F;
      }

      this.batHead.func_78785_a(par7);
      this.batBody.func_78785_a(par7);
   }
}
