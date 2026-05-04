package thaumcraft.client.renderers.entity.projectile;

import com.sasmaster.glelwjgl.java.CoreGLE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.ender.ShaderCallback;
import thaumcraft.client.lib.ender.ShaderHelper;
import thaumcraft.common.entities.projectile.EntityRiftBlast;

public class RenderRiftBlast extends Render {
   private final ShaderCallback shaderCallback;
   private static final ResourceLocation starsTexture = new ResourceLocation("textures/entity/end_portal.png");
   CoreGLE gle = new CoreGLE();

   public RenderRiftBlast(RenderManager rm) {
      super(rm);
      this.field_76989_e = 0.0F;
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
   }

   public void renderEntityAt(EntityRiftBlast entity, double x, double y, double z, float fq, float pticks) {
      Tessellator tessellator = Tessellator.func_178181_a();
      GL11.glPushMatrix();
      GL11.glTranslated(x, y, z);
      float xx = (float)(entity.field_70169_q + (entity.field_70165_t - entity.field_70169_q) * pticks);
      float yy = (float)(entity.field_70167_r + (entity.field_70163_u - entity.field_70167_r) * pticks);
      float zz = (float)(entity.field_70166_s + (entity.field_70161_v - entity.field_70166_s) * pticks);
      GL11.glTranslated(-xx, -yy, -zz);
      GL11.glEnable(3042);

      for (int q = 0; q <= 1; q++) {
         if (q < 1) {
            GlStateManager.func_179132_a(false);
         }

         GL11.glBlendFunc(770, q < 1 ? 1 : 771);
         if (entity.points != null && entity.points.length > 2) {
            Minecraft.func_71410_x().field_71446_o.func_110577_a(starsTexture);
            ShaderHelper.useShader(ShaderHelper.endShader, this.shaderCallback);
            double[] r2 = new double[entity.radii.length];
            int ri = 0;
            float m = (1.5F - q) / 1.0F;

            for (double d : entity.radii) {
               r2[ri] = entity.radii[ri] * m;
               ri++;
            }

            this.gle.set_POLYCYL_TESS(3);
            this.gle.set__ROUND_TESS_PIECES(1);
            this.gle.gleSetJoinStyle(1042);
            this.gle.glePolyCone(entity.points.length, entity.points, entity.colours, r2, 1.0F / entity.points.length, 0.0F);
            ShaderHelper.releaseShader();
         }

         if (q < 1) {
            GlStateManager.func_179132_a(true);
         }
      }

      GL11.glDisable(3042);
      GL11.glDisable(32826);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
   }

   public void func_76986_a(Entity entity, double d, double d1, double d2, float f, float f1) {
      this.renderEntityAt((EntityRiftBlast)entity, d, d1, d2, f, f1);
   }

   protected ResourceLocation func_110775_a(Entity entity) {
      return TextureMap.field_110575_b;
   }
}
