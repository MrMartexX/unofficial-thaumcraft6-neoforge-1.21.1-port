package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.lib.ender.ShaderCallback;
import thaumcraft.client.lib.ender.ShaderHelper;
import thaumcraft.common.lib.utils.BlockStateUtils;

public class TileVoidSiphonRenderer extends TileEntitySpecialRenderer {
   private final ShaderCallback shaderCallback = new ShaderCallback() {
      @Override
      public void call(int shader) {
         Minecraft mc = Minecraft.func_71410_x();
         int x = ARBShaderObjects.glGetUniformLocationARB(shader, "yaw");
         ARBShaderObjects.glUniform1fARB(x, (float)(mc.field_71439_g.field_70177_z * 2.0F * Math.PI / 360.0));
         int z = ARBShaderObjects.glGetUniformLocationARB(shader, "pitch");
         ARBShaderObjects.glUniform1fARB(z, -((float)(mc.field_71439_g.field_70125_A * 2.0F * Math.PI / 360.0)));
      }
   };
   private static final ResourceLocation starsTexture = new ResourceLocation("textures/entity/end_portal.png");

   public void func_192841_a(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      super.func_192841_a(te, x, y, z, partialTicks, destroyStage, alpha);
      if (BlockStateUtils.isEnabled(te.func_145832_p())) {
         GL11.glPushMatrix();
         this.func_147499_a(starsTexture);
         ShaderHelper.useShader(ShaderHelper.endShader, this.shaderCallback);
         GL11.glPushMatrix();
         GL11.glTranslated(x + 0.5, y + 0.875, z + 0.5);
         GlStateManager.func_179132_a(false);

         for (EnumFacing face : EnumFacing.values()) {
            GL11.glPushMatrix();
            GL11.glRotatef(90.0F, -face.func_96559_d(), face.func_82601_c(), -face.func_82599_e());
            if (face.func_82599_e() < 0) {
               GL11.glTranslated(0.0, 0.0, 0.126);
               GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            } else {
               GL11.glTranslated(0.0, 0.0, -0.126);
            }

            GL11.glRotatef(90.0F, 0.0F, 0.0F, -1.0F);
            GL11.glScaled(0.2, 0.2, 0.2);
            UtilsFX.renderQuadCentered(1, 1, 0, 1.0F, 1.0F, 1.0F, 1.0F, 200, 1, 1.0F);
            GL11.glPopMatrix();
         }

         GL11.glPopMatrix();
         GL11.glPushMatrix();
         GL11.glTranslated(x + 0.5, y + 0.3125, z + 0.5);
         GlStateManager.func_179132_a(false);

         for (EnumFacing face : EnumFacing.values()) {
            GL11.glPushMatrix();
            GL11.glRotatef(90.0F, -face.func_96559_d(), face.func_82601_c(), -face.func_82599_e());
            if (face.func_82599_e() < 0) {
               GL11.glTranslated(0.0, 0.0, 0.26);
               GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            } else {
               GL11.glTranslated(0.0, 0.0, -0.26);
            }

            if (face.func_82599_e() != 0) {
               GL11.glRotatef(90.0F, 0.0F, 0.0F, -1.0F);
            }

            GL11.glScaled(0.25, 0.5, 0.25);
            UtilsFX.renderQuadCentered(1, 1, 0, 1.0F, 1.0F, 1.0F, 1.0F, 200, 771, 1.0F);
            GL11.glPopMatrix();
         }

         GL11.glPopMatrix();
         GlStateManager.func_179132_a(true);
         ShaderHelper.releaseShader();
         GL11.glPopMatrix();
      }
   }
}
