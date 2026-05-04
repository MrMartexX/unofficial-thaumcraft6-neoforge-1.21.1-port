package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class FXVisSparkle extends Particle {
   private double targetX;
   private double targetY;
   private double targetZ;
   float sizeMod = 0.0F;

   public FXVisSparkle(World par1World, double par2, double par4, double par6, double tx, double ty, double tz) {
      super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
      this.field_70552_h = this.field_70553_i = this.field_70551_j = 0.6F;
      this.field_70544_f = 0.0F;
      this.targetX = tx;
      this.targetY = ty;
      this.targetZ = tz;
      this.field_70547_e = 1000;
      float f3 = 0.01F;
      this.field_187129_i = (float)this.field_187136_p.nextGaussian() * f3;
      this.field_187130_j = (float)this.field_187136_p.nextGaussian() * f3;
      this.field_187131_k = (float)this.field_187136_p.nextGaussian() * f3;
      this.sizeMod = 45 + this.field_187136_p.nextInt(15);
      this.field_70552_h = 0.2F;
      this.field_70553_i = 0.6F + this.field_187136_p.nextFloat() * 0.3F;
      this.field_70551_j = 0.2F;
      this.field_70545_g = 0.2F;
   }

   public void func_180434_a(BufferBuilder wr, Entity p_180434_2_, float f, float f1, float f2, float f3, float f4, float f5) {
      float bob = MathHelper.func_76126_a(this.field_70546_d / 3.0F) * 0.3F + 6.0F;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75F);
      int part = this.field_70546_d % 16;
      float var8x = part / 64.0F;
      float var9 = var8x + 0.015625F;
      float var10 = 0.125F;
      float var11 = var10 + 0.015625F;
      float var12 = 0.1F * this.field_70544_f * bob;
      float var13 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * f - field_70556_an);
      float var14 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * f - field_70554_ao);
      float var15 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * f - field_70555_ap);
      float var16 = 1.0F;
      int i = 240;
      int j = i >> 16 & 65535;
      int k = i & 65535;
      wr.func_181662_b(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12)
         .func_187315_a(var9, var11)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, 0.5F)
         .func_187314_a(j, k)
         .func_181675_d();
      wr.func_181662_b(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12)
         .func_187315_a(var9, var10)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, 0.5F)
         .func_187314_a(j, k)
         .func_181675_d();
      wr.func_181662_b(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12)
         .func_187315_a(var8x, var10)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, 0.5F)
         .func_187314_a(j, k)
         .func_181675_d();
      wr.func_181662_b(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12)
         .func_187315_a(var8x, var11)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, 0.5F)
         .func_187314_a(j, k)
         .func_181675_d();
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      this.field_187129_i *= 0.985;
      this.field_187130_j *= 0.985;
      this.field_187131_k *= 0.985;
      double dx = this.targetX - this.field_187126_f;
      double dy = this.targetY - this.field_187127_g;
      double dz = this.targetZ - this.field_187128_h;
      double d13 = 0.1F;
      double d11 = MathHelper.func_76133_a(dx * dx + dy * dy + dz * dz);
      if (d11 < 2.0) {
         this.field_70544_f *= 0.95F;
      }

      if (d11 < 0.2) {
         this.field_70547_e = this.field_70546_d;
      }

      if (this.field_70546_d < 10) {
         this.field_70544_f = this.field_70546_d / this.sizeMod;
      }

      dx /= d11;
      dy /= d11;
      dz /= d11;
      this.field_187129_i += dx * d13;
      this.field_187130_j += dy * d13;
      this.field_187131_k += dz * d13;
      this.field_187129_i = MathHelper.func_76131_a((float)this.field_187129_i, -0.1F, 0.1F);
      this.field_187130_j = MathHelper.func_76131_a((float)this.field_187130_j, -0.1F, 0.1F);
      this.field_187131_k = MathHelper.func_76131_a((float)this.field_187131_k, -0.1F, 0.1F);
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      }
   }

   public void setGravity(float value) {
      this.field_70545_g = value;
   }
}
