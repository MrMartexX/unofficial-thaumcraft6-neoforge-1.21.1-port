package thaumcraft.client.renderers.models.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelGrappler extends ModelBase {
   ModelRenderer core;
   ModelRenderer prong1;
   ModelRenderer prong2;
   ModelRenderer prong3;

   public ModelGrappler() {
      this.field_78090_t = 64;
      this.field_78089_u = 32;
      this.core = new ModelRenderer(this, 0, 0);
      this.core.func_78789_a(-1.5F, -1.5F, -1.5F, 3, 3, 3);
      this.core.func_78793_a(0.0F, 0.0F, 0.0F);
      this.core.func_78787_b(this.field_78090_t, this.field_78089_u);
      this.setRotation(this.core, 0.0F, 0.0F, 0.0F);
      this.prong1 = new ModelRenderer(this, 0, 10);
      this.prong1.func_78789_a(-0.5F, -0.5F, -2.5F, 1, 1, 5);
      this.prong1.func_78793_a(0.0F, 0.0F, 0.0F);
      this.prong1.func_78787_b(this.field_78090_t, this.field_78089_u);
      this.setRotation(this.prong1, 0.0F, 0.0F, 0.0F);
      this.prong2 = new ModelRenderer(this, 0, 10);
      this.prong2.func_78789_a(-0.5F, -0.5F, -2.5F, 1, 1, 5);
      this.prong2.func_78793_a(0.0F, 0.0F, 0.0F);
      this.prong2.func_78787_b(this.field_78090_t, this.field_78089_u);
      this.setRotation(this.prong2, 0.0F, (float) (Math.PI / 2), 0.0F);
      this.prong3 = new ModelRenderer(this, 0, 10);
      this.prong3.func_78789_a(-0.5F, -0.5F, -2.5F, 1, 1, 5);
      this.prong3.func_78793_a(0.0F, 0.0F, 0.0F);
      this.prong3.func_78787_b(this.field_78090_t, this.field_78089_u);
      this.setRotation(this.prong3, (float) (Math.PI / 2), (float) (Math.PI / 2), 0.0F);
   }

   public void render() {
      this.core.func_78785_a(0.0625F);
      this.prong1.func_78785_a(0.0625F);
      this.prong2.func_78785_a(0.0625F);
      this.prong3.func_78785_a(0.0625F);
   }

   private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.field_78795_f = x;
      model.field_78796_g = y;
      model.field_78808_h = z;
   }
}
