package thaumcraft.client.fx.particles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBoreParticles extends Particle {
   private IBlockState blockInstance;
   private ItemStack itemInstance;
   private int side;
   private Entity target;
   private double targetX;
   private double targetY;
   private double targetZ;

   public FXBoreParticles(World par1World, double par2, double par4, double par6, double tx, double ty, double tz, IBlockState par14Block, int par15) {
      super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
      this.blockInstance = par14Block;

      try {
         this.func_187117_a(Minecraft.func_71410_x().func_175599_af().func_175037_a().func_178087_a(Item.func_150898_a(par14Block.func_177230_c()), par15));
      } catch (Exception e) {
         this.func_187117_a(Minecraft.func_71410_x().func_175599_af().func_175037_a().func_178087_a(Item.func_150898_a(Blocks.field_150348_b), 0));
         this.field_70547_e = 0;
      }

      this.field_70545_g = par14Block.func_177230_c().field_149763_I;
      this.field_70552_h = this.field_70553_i = this.field_70551_j = 0.6F;
      this.field_70544_f = this.field_187136_p.nextFloat() * 0.3F + 0.4F;
      this.side = par15;
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
      this.field_187129_i = (float)this.field_187122_b.field_73012_v.nextGaussian() * f3;
      this.field_187130_j = (float)this.field_187122_b.field_73012_v.nextGaussian() * f3;
      this.field_187131_k = (float)this.field_187122_b.field_73012_v.nextGaussian() * f3;
      this.field_70545_g = 0.01F;
   }

   public FXBoreParticles(
      World par1World, double par2, double par4, double par6, double tx, double ty, double tz, double sx, double sy, double sz, ItemStack item
   ) {
      super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
      this.itemInstance = item;
      this.func_187117_a(Minecraft.func_71410_x().func_175599_af().func_175037_a().func_178087_a(item.func_77973_b(), item.func_77952_i()));
      this.field_70545_g = Blocks.field_150431_aC.field_149763_I;
      this.field_70552_h = this.field_70553_i = this.field_70551_j = 0.6F;
      this.field_70544_f = this.field_187136_p.nextFloat() * 0.3F + 0.4F;
      this.side = 0;
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
      this.field_187129_i = sx + (float)this.field_187122_b.field_73012_v.nextGaussian() * f3;
      this.field_187130_j = sy + (float)this.field_187122_b.field_73012_v.nextGaussian() * f3;
      this.field_187131_k = sz + (float)this.field_187122_b.field_73012_v.nextGaussian() * f3;
      this.field_70545_g = 0.01F;
      Entity renderentity = FMLClientHandler.instance().getClient().func_175606_aa();
      int visibleDistance = 64;
      if (!FMLClientHandler.instance().getClient().field_71474_y.field_74347_j) {
         visibleDistance = 32;
      }

      if (renderentity.func_70011_f(this.field_187126_f, this.field_187127_g, this.field_187128_h) > visibleDistance) {
         this.field_70547_e = 0;
      }
   }

   public void setTarget(Entity target) {
      this.target = target;
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
         this.field_187129_i = this.field_187129_i + this.field_187136_p.nextGaussian() * 0.005;
         this.field_187130_j = this.field_187130_j + this.field_187136_p.nextGaussian() * 0.005;
         this.field_187131_k = this.field_187131_k + this.field_187136_p.nextGaussian() * 0.005;
      } else {
         this.func_187112_i();
      }
   }

   public int func_70537_b() {
      return 1;
   }

   public FXBoreParticles getObjectColor(BlockPos pos) {
      if (this.blockInstance == null || this.field_187122_b.func_180495_p(pos) != this.blockInstance) {
         try {
            int var4 = Minecraft.func_71410_x().getItemColors().func_186728_a(this.itemInstance, 0);
            this.field_70552_h *= (var4 >> 16 & 0xFF) / 255.0F;
            this.field_70553_i *= (var4 >> 8 & 0xFF) / 255.0F;
            this.field_70551_j *= (var4 & 0xFF) / 255.0F;
         } catch (Exception var41) {
         }

         return this;
      } else {
         if (this.blockInstance == Blocks.field_150349_c && this.side != 1) {
            return this;
         }

         try {
            int var4 = Minecraft.func_71410_x().func_184125_al().func_186724_a(this.blockInstance, this.field_187122_b, pos, 0);
            this.field_70552_h *= (var4 >> 16 & 0xFF) / 255.0F;
            this.field_70553_i *= (var4 >> 8 & 0xFF) / 255.0F;
            this.field_70551_j *= (var4 & 0xFF) / 255.0F;
         } catch (Exception var3) {
         }

         return this;
      }
   }

   public void func_180434_a(
      BufferBuilder p_180434_1_,
      Entity p_180434_2_,
      float p_180434_3_,
      float p_180434_4_,
      float p_180434_5_,
      float p_180434_6_,
      float p_180434_7_,
      float p_180434_8_
   ) {
      float f6 = (this.field_94054_b + this.field_70548_b / 4.0F) / 16.0F;
      float f7 = f6 + 0.015609375F;
      float f8 = (this.field_94055_c + this.field_70549_c / 4.0F) / 16.0F;
      float f9 = f8 + 0.015609375F;
      float f10 = 0.1F * this.field_70544_f;
      if (this.field_187119_C != null) {
         f6 = this.field_187119_C.func_94214_a(this.field_70548_b / 4.0F * 16.0F);
         f7 = this.field_187119_C.func_94214_a((this.field_70548_b + 1.0F) / 4.0F * 16.0F);
         f8 = this.field_187119_C.func_94207_b(this.field_70549_c / 4.0F * 16.0F);
         f9 = this.field_187119_C.func_94207_b((this.field_70549_c + 1.0F) / 4.0F * 16.0F);
      }

      int i = this.func_189214_a(p_180434_3_);
      int j = i >> 16 & 65535;
      int k = i & 65535;
      float f11 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * p_180434_3_ - field_70556_an);
      float f12 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * p_180434_3_ - field_70554_ao);
      float f13 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * p_180434_3_ - field_70555_ap);
      p_180434_1_.func_181662_b(f11 - p_180434_4_ * f10 - p_180434_7_ * f10, f12 - p_180434_5_ * f10, f13 - p_180434_6_ * f10 - p_180434_8_ * f10)
         .func_187315_a(f6, f9)
         .func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F)
         .func_187314_a(j, k)
         .func_181675_d();
      p_180434_1_.func_181662_b(f11 - p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 - p_180434_6_ * f10 + p_180434_8_ * f10)
         .func_187315_a(f6, f8)
         .func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F)
         .func_187314_a(j, k)
         .func_181675_d();
      p_180434_1_.func_181662_b(f11 + p_180434_4_ * f10 + p_180434_7_ * f10, f12 + p_180434_5_ * f10, f13 + p_180434_6_ * f10 + p_180434_8_ * f10)
         .func_187315_a(f7, f8)
         .func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F)
         .func_187314_a(j, k)
         .func_181675_d();
      p_180434_1_.func_181662_b(f11 + p_180434_4_ * f10 - p_180434_7_ * f10, f12 - p_180434_5_ * f10, f13 + p_180434_6_ * f10 - p_180434_8_ * f10)
         .func_187315_a(f7, f9)
         .func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F)
         .func_187314_a(j, k)
         .func_181675_d();
   }
}
