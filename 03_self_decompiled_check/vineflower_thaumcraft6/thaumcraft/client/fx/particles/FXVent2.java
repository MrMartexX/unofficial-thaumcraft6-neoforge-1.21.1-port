package thaumcraft.client.fx.particles;

import java.awt.Color;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

public class FXVent2 extends Particle {
   float grav = 0.0F;
   float psm = 1.0F;

   public FXVent2(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, int color) {
      super(par1World, par2, par4, par6, par8, par10, par12);
      this.func_187115_a(0.02F, 0.02F);
      this.field_70544_f = this.field_187136_p.nextFloat() * 0.1F + 0.05F;
      this.field_187129_i = par8;
      this.field_187130_j = par10;
      this.field_187131_k = par12;
      Color c = new Color(color);
      this.field_70552_h = (float)MathHelper.func_151237_a(c.getRed() / 255.0F + this.field_187136_p.nextGaussian() * 0.05, 0.0, 1.0);
      this.field_70551_j = (float)MathHelper.func_151237_a(c.getBlue() / 255.0F + this.field_187136_p.nextGaussian() * 0.05, 0.0, 1.0);
      this.field_70553_i = (float)MathHelper.func_151237_a(c.getGreen() / 255.0F + this.field_187136_p.nextGaussian() * 0.05, 0.0, 1.0);
      Entity renderentity = FMLClientHandler.instance().getClient().func_175606_aa();
      int visibleDistance = 50;
      if (!FMLClientHandler.instance().getClient().field_71474_y.field_74347_j) {
         visibleDistance = 25;
      }

      if (renderentity.func_70011_f(this.field_187126_f, this.field_187127_g, this.field_187128_h) > visibleDistance) {
         this.field_70547_e = 0;
      }

      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      this.grav = (float)(this.field_187136_p.nextGaussian() * 0.0075);
   }

   public void setScale(float f) {
      this.field_70544_f *= f;
      this.psm *= f;
   }

   public void setHeading(double par1, double par3, double par5, float par7, float par8) {
      float f2 = MathHelper.func_76133_a(par1 * par1 + par3 * par3 + par5 * par5);
      par1 /= f2;
      par3 /= f2;
      par5 /= f2;
      par1 += this.field_187136_p.nextGaussian() * (this.field_187136_p.nextBoolean() ? -1 : 1) * 0.0075F * par8;
      par3 += this.field_187136_p.nextGaussian() * (this.field_187136_p.nextBoolean() ? -1 : 1) * 0.0075F * par8;
      par5 += this.field_187136_p.nextGaussian() * (this.field_187136_p.nextBoolean() ? -1 : 1) * 0.0075F * par8;
      par1 *= par7;
      par3 *= par7;
      par5 *= par7;
      this.field_187129_i = par1;
      this.field_187130_j = par3;
      this.field_187131_k = par5;
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      this.field_70546_d++;
      if (this.field_70544_f >= this.psm) {
         this.func_187112_i();
      }

      this.field_187130_j = this.field_187130_j + this.grav;
      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      this.field_187129_i *= 0.8500000190734863;
      this.field_187130_j *= 0.8500000190734863;
      this.field_187131_k *= 0.8500000190734863;
      if (this.field_70544_f < this.psm) {
         this.field_70544_f = (float)(this.field_70544_f * 1.2);
      }

      if (this.field_70544_f > this.psm) {
         this.field_70544_f = this.psm;
      }

      if (this.field_187132_l) {
         this.field_187129_i *= 0.7F;
         this.field_187131_k *= 0.7F;
      }
   }

   public void setRGB(float r, float g, float b) {
      this.field_70552_h = r;
      this.field_70553_i = g;
      this.field_70551_j = b;
   }

   public void func_180434_a(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.33F);
      int part = (int)(1.0F + this.field_70544_f / this.psm * 4.0F);
      float var8x = part % 16 / 64.0F;
      float var9 = var8x + 0.015625F;
      float var10 = part / 64 / 64.0F;
      float var11 = var10 + 0.015625F;
      float var12 = 0.3F * this.field_70544_f;
      float var13 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * f - field_70556_an);
      float var14 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * f - field_70554_ao);
      float var15 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * f - field_70555_ap);
      float var16 = 1.0F;
      int i = this.func_189214_a(f);
      int j = i >> 16 & 65535;
      int k = i & 65535;
      float alpha = this.field_82339_as * ((this.psm - this.field_70544_f) / this.psm);
      wr.func_181662_b(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12)
         .func_187315_a(var9, var11)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, alpha)
         .func_187314_a(j, k)
         .func_181675_d();
      wr.func_181662_b(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12)
         .func_187315_a(var9, var10)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, alpha)
         .func_187314_a(j, k)
         .func_181675_d();
      wr.func_181662_b(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12)
         .func_187315_a(var8x, var10)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, alpha)
         .func_187314_a(j, k)
         .func_181675_d();
      wr.func_181662_b(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12)
         .func_187315_a(var8x, var11)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, alpha)
         .func_187314_a(j, k)
         .func_181675_d();
   }

   public int func_70537_b() {
      return 1;
   }
}
