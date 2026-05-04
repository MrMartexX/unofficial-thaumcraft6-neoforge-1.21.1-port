package thaumcraft.client.renderers.entity;

import com.sasmaster.glelwjgl.java.CoreGLE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.ender.ShaderCallback;
import thaumcraft.client.lib.ender.ShaderHelper;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.EntityUtils;

public class RenderFluxRift extends Render {
   private final ShaderCallback shaderCallback;
   private static final ResourceLocation starsTexture = new ResourceLocation("textures/entity/end_portal.png");
   CoreGLE gle = new CoreGLE();

   public RenderFluxRift(RenderManager rm) {
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

   public void func_76986_a(Entity entity, double x, double y, double z, float yaw, float pt) {
      EntityFluxRift rift = (EntityFluxRift)entity;
      boolean goggles = EntityUtils.hasGoggles(Minecraft.func_71410_x().field_71439_g);
      GL11.glPushMatrix();
      this.func_110776_a(starsTexture);
      ShaderHelper.useShader(ShaderHelper.endShader, this.shaderCallback);
      float amp = 1.0F;
      float stab = MathHelper.func_76131_a(1.0F - rift.getRiftStability() / 50.0F, 0.0F, 1.5F);
      GL11.glEnable(3042);

      for (int q = 0; q <= 3; q++) {
         if (q < 3) {
            GlStateManager.func_179132_a(false);
            if (q == 0 && goggles) {
               GL11.glDisable(2929);
            }
         }

         GL11.glBlendFunc(770, q < 3 ? 1 : 771);
         if (rift.points.size() > 2) {
            GL11.glPushMatrix();
            double[][] pp = new double[rift.points.size()][3];
            float[][] colours = new float[rift.points.size()][4];
            double[] radii = new double[rift.points.size()];

            for (int a = 0; a < rift.points.size(); a++) {
               float var = rift.field_70173_aa + pt;
               if (a > rift.points.size() / 2) {
                  var -= a * 10;
               } else if (a < rift.points.size() / 2) {
                  var += a * 10;
               }

               pp[a][0] = rift.points.get(a).field_72450_a + x + Math.sin(var / 50.0F * amp) * 0.1F * stab;
               pp[a][1] = rift.points.get(a).field_72448_b + y + Math.sin(var / 60.0F * amp) * 0.1F * stab;
               pp[a][2] = rift.points.get(a).field_72449_c + z + Math.sin(var / 70.0F * amp) * 0.1F * stab;
               colours[a][0] = 1.0F;
               colours[a][1] = 1.0F;
               colours[a][2] = 1.0F;
               colours[a][3] = 1.0F;
               double w = 1.0 - Math.sin(var / 8.0F * amp) * 0.1F * stab;
               radii[a] = rift.pointsWidth.get(a).floatValue() * w * (q < 3 ? 1.25F + 0.5F * q : 1.0F);
            }

            this.gle.set_POLYCYL_TESS(6);
            this.gle.gleSetJoinStyle(1026);
            this.gle.glePolyCone(pp.length, pp, colours, radii, 1.0F, 0.0F);
            GL11.glPopMatrix();
         }

         if (q < 3) {
            GlStateManager.func_179132_a(true);
            if (q == 0 && goggles) {
               GL11.glEnable(2929);
            }
         }
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(3042);
      ShaderHelper.releaseShader();
      GL11.glPopMatrix();
   }

   protected ResourceLocation func_110775_a(Entity entity) {
      return TextureMap.field_110575_b;
   }
}
