package thaumcraft.client.fx.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

public class FXBoreSparkle extends Particle {
   private Entity target;
   private double targetX;
   private double targetY;
   private double targetZ;
   public int particle = 24;

   public FXBoreSparkle(World par1World, double par2, double par4, double par6, double tx, double ty, double tz) {
      super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
      this.field_70552_h = this.field_70553_i = this.field_70551_j = 0.6F;
      this.field_70544_f = this.field_187136_p.nextFloat() * 0.5F + 0.5F;
      this.targetX = tx;
      this.targetY = ty;
      this.targetZ = tz;
      double dx = tx - this.field_187126_f;
      double dy = ty - this.field_187127_g;
      double dz = tz - this.field_187128_h;
      int base = (int)(MathHelper.func_76133_a(dx * dx + dy * dy + dz * dz) * 10.0F);
      if (base < 1) {
         base = 1;
      }

      this.field_70547_e = base / 2 + this.field_187136_p.nextInt(base);
      float f3 = 0.01F;
      this.field_187129_i = (float)this.field_187136_p.nextGaussian() * f3;
      this.field_187130_j = (float)this.field_187136_p.nextGaussian() * f3;
      this.field_187131_k = (float)this.field_187136_p.nextGaussian() * f3;
      this.field_70552_h = 0.2F;
      this.field_70553_i = 0.6F + this.field_187136_p.nextFloat() * 0.3F;
      this.field_70551_j = 0.2F;
      this.field_70545_g = 0.2F;
      Entity renderentity = FMLClientHandler.instance().getClient().func_175606_aa();
      int visibleDistance = 64;
      if (!FMLClientHandler.instance().getClient().field_71474_y.field_74347_j) {
         visibleDistance = 32;
      }

      if (renderentity.func_70011_f(this.field_187126_f, this.field_187127_g, this.field_187128_h) > visibleDistance) {
         this.field_70547_e = 0;
      }
   }

   public FXBoreSparkle(World par1World, double par2, double par4, double par6, Entity t) {
      this(par1World, par2, par4, par6, t.field_70165_t, t.field_70163_u + t.func_70047_e(), t.field_70161_v);
      this.target = t;
   }

   public void func_180434_a(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      float bob = MathHelper.func_76126_a(this.field_70546_d / 3.0F) * 0.5F + 1.0F;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75F);
      int part = this.field_70546_d % 4;
      float var8x = part / 64.0F;
      float var9 = var8x + 0.015625F;
      float var10 = 0.0625F;
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
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, 1.0F)
         .func_187314_a(j, k)
         .func_181675_d();
      wr.func_181662_b(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12)
         .func_187315_a(var9, var10)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, 1.0F)
         .func_187314_a(j, k)
         .func_181675_d();
      wr.func_181662_b(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12)
         .func_187315_a(var8x, var10)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, 1.0F)
         .func_187314_a(j, k)
         .func_181675_d();
      wr.func_181662_b(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12)
         .func_187315_a(var8x, var11)
         .func_181666_a(this.field_70552_h * var16, this.field_70553_i * var16, this.field_70551_j * var16, 1.0F)
         .func_187314_a(j, k)
         .func_181675_d();
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      if (this.target != null) {
         this.targetX = this.target.field_70165_t;
         this.targetY = this.target.field_70163_u + this.target.func_70047_e();
         this.targetZ = this.target.field_70161_v;
      }

      if (this.field_70546_d++ < this.field_70547_e
         && (
            MathHelper.func_76128_c(this.field_187126_f) != MathHelper.func_76128_c(this.targetX)
               || MathHelper.func_76128_c(this.field_187127_g) != MathHelper.func_76128_c(this.targetY)
               || MathHelper.func_76128_c(this.field_187128_h) != MathHelper.func_76128_c(this.targetZ)
         )) {
         this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
         this.field_187129_i *= 0.985;
         this.field_187130_j *= 0.95;
         this.field_187131_k *= 0.985;
         double dx = this.targetX - this.field_187126_f;
         double dy = this.targetY - this.field_187127_g;
         double dz = this.targetZ - this.field_187128_h;
         double d11 = MathHelper.func_76133_a(dx * dx + dy * dy + dz * dz);
         double clamp = Math.min(0.25, d11 / 15.0);
         if (d11 < 2.0) {
            this.field_70544_f *= 0.9F;
         }

         dx /= d11;
         dy /= d11;
         dz /= d11;
         this.field_187129_i += dx * clamp;
         this.field_187130_j += dy * clamp;
         this.field_187131_k += dz * clamp;
         this.field_187129_i = MathHelper.func_151237_a((float)this.field_187129_i, -clamp, clamp);
         this.field_187130_j = MathHelper.func_151237_a((float)this.field_187130_j, -clamp, clamp);
         this.field_187131_k = MathHelper.func_151237_a((float)this.field_187131_k, -clamp, clamp);
         this.field_187129_i = this.field_187129_i + this.field_187136_p.nextGaussian() * 0.01;
         this.field_187130_j = this.field_187130_j + this.field_187136_p.nextGaussian() * 0.01;
         this.field_187131_k = this.field_187131_k + this.field_187136_p.nextGaussian() * 0.01;
      } else {
         this.func_187112_i();
      }
   }

   public void setGravity(float value) {
      this.field_70545_g = value;
   }
}
