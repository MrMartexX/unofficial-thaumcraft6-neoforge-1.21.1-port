package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class FXBlockRunes extends Particle {
   double ofx = 0.0;
   double ofy = 0.0;
   float rotation = 0.0F;
   int runeIndex = 0;

   public FXBlockRunes(World world, double d, double d1, double d2, float f1, float f2, float f3, int m) {
      super(world, d, d1, d2, 0.0, 0.0, 0.0);
      if (f1 == 0.0F) {
         f1 = 1.0F;
      }

      this.rotation = this.field_187136_p.nextInt(4) * 90;
      this.field_70552_h = f1;
      this.field_70553_i = f2;
      this.field_70551_j = f3;
      this.field_70545_g = 0.0F;
      this.field_187129_i = this.field_187130_j = this.field_187131_k = 0.0;
      this.field_70547_e = 3 * m;
      this.func_187115_a(0.01F, 0.01F);
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      this.runeIndex = (int)(Math.random() * 16.0 + 224.0);
      this.ofx = this.field_187136_p.nextFloat() * 0.2;
      this.ofy = -0.3 + this.field_187136_p.nextFloat() * 0.6;
      this.field_70544_f = (float)(1.0 + this.field_187136_p.nextGaussian() * 0.1F);
      this.field_82339_as = 0.0F;
   }

   public void setScale(float s) {
      this.field_70544_f = s;
   }

   public void setOffsetX(double f) {
      this.ofx = f;
   }

   public void func_180434_a(BufferBuilder wr, Entity p_180434_2_, float f, float f1, float f2, float f3, float f4, float f5) {
      Tessellator.func_178181_a().func_78381_a();
      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, this.field_82339_as / 2.0F);
      float var13 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * f - field_70556_an);
      float var14 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * f - field_70554_ao);
      float var15 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * f - field_70555_ap);
      GL11.glTranslated(var13, var14, var15);
      GL11.glRotatef(this.rotation, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
      GL11.glTranslated(this.ofx, this.ofy, -0.51);
      float var8x = this.runeIndex % 16 / 64.0F;
      float var9 = var8x + 0.015625F;
      float var10 = 0.09375F;
      float var11 = var10 + 0.015625F;
      float var12 = 0.3F * this.field_70544_f;
      float var16 = 1.0F;
      wr.func_181668_a(7, DefaultVertexFormats.field_181704_d);
      int i = 240;
      int j = i >> 16 & 65535;
      int k = i & 65535;
      wr.func_181662_b(-0.5 * var12, 0.5 * var12, 0.0)
         .func_187315_a(var9, var11)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, this.field_82339_as / 2.0F)
         .func_187314_a(j, k)
         .func_181675_d();
      wr.func_181662_b(0.5 * var12, 0.5 * var12, 0.0)
         .func_187315_a(var9, var10)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, this.field_82339_as / 2.0F)
         .func_187314_a(j, k)
         .func_181675_d();
      wr.func_181662_b(0.5 * var12, -0.5 * var12, 0.0)
         .func_187315_a(var8x, var10)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, this.field_82339_as / 2.0F)
         .func_187314_a(j, k)
         .func_181675_d();
      wr.func_181662_b(-0.5 * var12, -0.5 * var12, 0.0)
         .func_187315_a(var8x, var11)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, this.field_82339_as / 2.0F)
         .func_187314_a(j, k)
         .func_181675_d();
      Tessellator.func_178181_a().func_78381_a();
      GL11.glPopMatrix();
      wr.func_181668_a(7, DefaultVertexFormats.field_181704_d);
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      float threshold = this.field_70547_e / 5.0F;
      if (this.field_70546_d <= threshold) {
         this.field_82339_as = this.field_70546_d / threshold;
      } else {
         this.field_82339_as = (float)(this.field_70547_e - this.field_70546_d) / this.field_70547_e;
      }

      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      }

      this.field_187130_j = this.field_187130_j - 0.04 * this.field_70545_g;
      this.field_187126_f = this.field_187126_f + this.field_187129_i;
      this.field_187127_g = this.field_187127_g + this.field_187130_j;
      this.field_187128_h = this.field_187128_h + this.field_187131_k;
   }

   public void setGravity(float value) {
      this.field_70545_g = value;
   }
}
