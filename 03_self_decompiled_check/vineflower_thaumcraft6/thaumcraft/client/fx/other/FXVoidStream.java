package thaumcraft.client.fx.other;

import com.sasmaster.glelwjgl.java.CoreGLE;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.lib.ender.ShaderCallback;
import thaumcraft.client.lib.ender.ShaderHelper;
import thaumcraft.codechicken.lib.vec.Quat;

public class FXVoidStream extends Particle {
   private double targetX;
   private double targetY;
   private double targetZ;
   private double startX;
   private double startY;
   private double startZ;
   private int seed = 0;
   public int length = 20;
   private final ShaderCallback shaderCallback;
   private static final ResourceLocation starsTexture = new ResourceLocation("textures/entity/end_portal.png");
   CoreGLE gle = new CoreGLE();
   int layer = 1;
   double[][] points;
   float[][] colours;
   double[] radii;
   int growing = -1;
   ArrayList<Quat> vecs = new ArrayList<>();

   public FXVoidStream(World w, double par2, double par4, double par6, double tx, double ty, double tz, int seed, float scale) {
      super(w, par2, par4, par6, 0.0, 0.0, 0.0);
      this.shaderCallback = new ShaderCallback() {
         @Override
         public void call(int shader) {
            Minecraft mc = Minecraft.func_71410_x();
            int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
            ARBShaderObjects.glUniform1fARB(x, (float)(mc.field_71439_g.field_70177_z * 2.0F * Math.PI / 360.0));
            int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
            ARBShaderObjects.glUniform1fARB(z, -((float)(mc.field_71439_g.field_70125_A * 2.0F * Math.PI / 360.0)));
         }
      };
      this.field_70544_f = (float)(scale * (1.0 + this.field_187136_p.nextGaussian() * 0.15F));
      this.length = 40;
      this.seed = seed;
      this.targetX = tx;
      this.targetY = ty;
      this.targetZ = tz;
      double dx = tx - this.field_187126_f;
      double dy = ty - this.field_187127_g;
      double dz = tz - this.field_187128_h;
      int base = (int)(MathHelper.func_76133_a(dx * dx + dy * dy + dz * dz) * 21.0F);
      if (base < 1) {
         base = 1;
      }

      this.field_70547_e = base * 2;
      this.field_187129_i = MathHelper.func_76126_a(seed / 4.0F) * 0.025F;
      this.field_187130_j = MathHelper.func_76126_a(seed / 3.0F) * 0.025F;
      this.field_187131_k = MathHelper.func_76126_a(seed / 2.0F) * 0.025F;
      this.field_70545_g = 0.2F;
      this.vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
      this.vecs.add(new Quat(0.0, 0.0, 0.0, 0.001));
      this.startX = this.field_187126_f;
      this.startY = this.field_187127_g;
      this.startZ = this.field_187128_h;
   }

   public void func_180434_a(BufferBuilder wr, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      Tessellator.func_178181_a().func_78381_a();
      GL11.glPushMatrix();
      double ePX = this.startX - field_70556_an;
      double ePY = this.startY - field_70554_ao;
      double ePZ = this.startZ - field_70555_ap;
      GL11.glTranslated(ePX, ePY, ePZ);

      for (int q = 0; q <= 1; q++) {
         if (q < 1) {
            GlStateManager.func_179132_a(false);
         }

         GL11.glBlendFunc(770, q < 1 ? 1 : 771);
         if (this.points != null && this.points.length > 2) {
            Minecraft.func_71410_x().field_71446_o.func_110577_a(starsTexture);
            ShaderHelper.useShader(ShaderHelper.endShader, this.shaderCallback);
            double[] r2 = new double[this.radii.length];
            int ri = 0;
            float m = (1.5F - q) / 1.0F;

            for (double d : this.radii) {
               r2[ri] = this.radii[ri] * m;
               ri++;
            }

            this.gle.set_POLYCYL_TESS(3);
            this.gle.set__ROUND_TESS_PIECES(1);
            this.gle.gleSetJoinStyle(1042);
            this.gle
               .glePolyCone(
                  this.points.length, this.points, this.colours, r2, 0.075F, this.growing < 0 ? 0.0F : 0.075F * (this.field_70546_d - this.growing + f)
               );
            ShaderHelper.releaseShader();
         }

         if (q < 1) {
            GlStateManager.func_179132_a(true);
         }
      }

      GlStateManager.func_179132_a(false);
      GL11.glBlendFunc(770, 771);
      GL11.glPopMatrix();
      Minecraft.func_71410_x().field_71446_o.func_110577_a(ParticleEngine.particleTexture);
      wr.func_181668_a(7, DefaultVertexFormats.field_181704_d);
   }

   public void setFXLayer(int l) {
      this.layer = l;
   }

   public int func_70537_b() {
      return this.layer;
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      if (this.field_70546_d++ < this.field_70547_e && this.length >= 1) {
         this.field_187130_j = this.field_187130_j + 0.01 * this.field_70545_g;
         this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
         this.field_187129_i *= 0.985;
         this.field_187130_j *= 0.985;
         this.field_187131_k *= 0.985;
         this.field_187129_i = MathHelper.func_76131_a((float)this.field_187129_i, -0.04F, 0.04F);
         this.field_187130_j = MathHelper.func_76131_a((float)this.field_187130_j, -0.04F, 0.04F);
         this.field_187131_k = MathHelper.func_76131_a((float)this.field_187131_k, -0.04F, 0.04F);
         double dx = this.targetX - this.field_187126_f;
         double dy = this.targetY - this.field_187127_g;
         double dz = this.targetZ - this.field_187128_h;
         double d13 = 0.01;
         double d11 = MathHelper.func_76133_a(dx * dx + dy * dy + dz * dz);
         dx /= d11;
         dy /= d11;
         dz /= d11;
         this.field_187129_i = this.field_187129_i + (dx * (d13 / Math.min(1.0, d11)) + this.field_187136_p.nextGaussian() * 0.015F);
         this.field_187130_j = this.field_187130_j + (dy * (d13 / Math.min(1.0, d11)) + this.field_187136_p.nextGaussian() * 0.015F);
         this.field_187131_k = this.field_187131_k + (dz * (d13 / Math.min(1.0, d11)) + this.field_187136_p.nextGaussian() * 0.015F);
         float scale = this.field_70544_f * (0.75F + MathHelper.func_76126_a((this.seed + this.field_70546_d) / 2.0F) * 0.25F);
         if (d11 < 0.5) {
            float f = MathHelper.func_76126_a((float)(d11 * (Math.PI / 2)));
            scale *= f;
            this.field_70544_f *= f;
         }

         if (this.field_70544_f > 0.001) {
            this.vecs.add(new Quat(scale, this.field_187126_f - this.startX, this.field_187127_g - this.startY, this.field_187128_h - this.startZ));
         } else {
            if (this.growing < 0) {
               this.growing = this.field_70546_d;
            }

            this.length--;
         }

         if (this.vecs.size() > this.length) {
            this.vecs.remove(0);
         }

         this.points = new double[this.vecs.size()][3];
         this.colours = new float[this.vecs.size()][4];
         this.radii = new double[this.vecs.size()];
         int c = this.vecs.size();

         for (Quat v : this.vecs) {
            c--;
            float variance = 1.0F + MathHelper.func_76126_a((c + this.field_70546_d) / 3.0F) * 0.2F;
            float xx = MathHelper.func_76126_a((c + this.field_70546_d) / 6.0F) * 0.01F;
            float yy = MathHelper.func_76126_a((c + this.field_70546_d) / 7.0F) * 0.01F;
            float zz = MathHelper.func_76126_a((c + this.field_70546_d) / 8.0F) * 0.01F;
            this.points[c][0] = v.x + xx;
            this.points[c][1] = v.y + yy;
            this.points[c][2] = v.z + zz;
            this.radii[c] = v.s * variance;
            if (c > this.vecs.size() - 10) {
               this.radii[c] = this.radii[c] * MathHelper.func_76134_b((float)((c - (this.vecs.size() - 12)) / 10.0F * (Math.PI / 2)));
            }

            if (c == 0) {
               this.radii[c] = 0.0;
            } else if (c == 1) {
               this.radii[c] = 0.0;
            } else if (c == 2) {
               this.radii[c] = (this.field_70544_f * 0.5 + this.radii[c]) / 2.0;
            } else if (c == 3) {
               this.radii[c] = (this.field_70544_f + this.radii[c]) / 2.0;
            } else if (c == 4) {
               this.radii[c] = (this.field_70544_f + this.radii[c] * 2.0) / 3.0;
            }

            this.colours[c][0] = 1.0F;
            this.colours[c][1] = 1.0F;
            this.colours[c][2] = 1.0F;
            this.colours[c][3] = 1.0F;
         }

         if (this.vecs.size() > 2 && this.field_187136_p.nextBoolean()) {
            int q = this.field_187136_p.nextInt(3);
            if (this.field_187136_p.nextBoolean()) {
               q = this.vecs.size() - 2;
            }
         }
      } else {
         this.func_187112_i();
      }
   }

   public void setGravity(float value) {
      this.field_70545_g = value;
   }
}
