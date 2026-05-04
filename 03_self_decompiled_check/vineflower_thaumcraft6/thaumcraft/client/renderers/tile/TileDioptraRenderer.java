package thaumcraft.client.renderers.tile;

import java.awt.Color;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.TexturedQuadTC;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileDioptra;

public class TileDioptraRenderer extends TileEntitySpecialRenderer {
   private final ResourceLocation gridTexture = new ResourceLocation("thaumcraft", "textures/misc/gridblock.png");
   private final ResourceLocation sideTexture = new ResourceLocation("thaumcraft", "textures/models/dioptra_side.png");
   private final float[] alphas = new float[]{0.9F, 0.9F, 0.9F, 0.9F};

   public void func_192841_a(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      super.func_192841_a(te, x, y, z, partialTicks, destroyStage, alpha);
      TileDioptra tco = (TileDioptra)te;
      Tessellator tessellator = Tessellator.func_178181_a();
      GL11.glPushMatrix();
      GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      float t = this.field_147501_a.field_147551_g != null ? this.field_147501_a.field_147551_g.field_70173_aa + partialTicks : 0.0F;
      float rc = 1.0F;
      float gc = 1.0F;
      float bc = 1.0F;
      if (BlockStateUtils.isEnabled(tco.func_145832_p())) {
         rc = MathHelper.func_76126_a(t / 12.0F) * 0.05F + 0.85F;
         gc = MathHelper.func_76126_a(t / 11.0F) * 0.05F + 0.9F;
         bc = MathHelper.func_76126_a(t / 10.0F) * 0.05F + 0.95F;
      } else {
         rc = MathHelper.func_76126_a(t / 12.0F) * 0.05F + 0.85F;
         gc = MathHelper.func_76126_a(t / 11.0F) * 0.05F + 0.45F;
         bc = MathHelper.func_76126_a(t / 10.0F) * 0.05F + 0.95F;
      }

      GL11.glShadeModel(7425);
      GL11.glBlendFunc(770, 1);
      GL11.glPushMatrix();
      GL11.glTranslated(-0.495, 0.501, -0.495);
      this.func_147499_a(this.gridTexture);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glScaled(0.99, 1.0, 0.99);

      for (int a = 0; a < 12; a++) {
         for (int b = 0; b < 12; b++) {
            int[] colors = this.calcColorMap(new float[]{0.0F, 0.0F, 0.0F, 0.0F}, rc, gc, bc);
            double d3 = a - 6;
            double d5 = b - 6;
            double dis = Math.sqrt(d3 * d3 + d5 * d5);
            float s = MathHelper.func_76126_a((float)((tco.counter - dis * 10.0) / 8.0));
            TexturedQuadTC quad = new TexturedQuadTC(
               new PositionTextureVertex[]{
                  new PositionTextureVertex(a / 12.0F, tco.grid_amt[a + b * 13] / 96.0F, b / 12.0F, 0.0F, 1.0F),
                  new PositionTextureVertex((a + 1) / 12.0F, tco.grid_amt[a + 1 + b * 13] / 96.0F, b / 12.0F, 1.0F, 1.0F),
                  new PositionTextureVertex((a + 1) / 12.0F, tco.grid_amt[a + 1 + (b + 1) * 13] / 96.0F, (b + 1) / 12.0F, 1.0F, 0.0F),
                  new PositionTextureVertex(a / 12.0F, tco.grid_amt[a + (b + 1) * 13] / 96.0F, (b + 1) / 12.0F, 0.0F, 0.0F)
               }
            );
            quad.flipFace();
            quad.draw(tessellator.func_178180_c(), 1.0F, (int)(200.0F + s * 15.0F), colors, this.alphas);
            if (a == 0) {
               quad = new TexturedQuadTC(
                  new PositionTextureVertex[]{
                     new PositionTextureVertex(0.0F, 0.0F, b / 12.0F, 0.0F, 1.0F),
                     new PositionTextureVertex(0.0F, tco.grid_amt[b * 13] / 96.0F, b / 12.0F, 1.0F, 1.0F),
                     new PositionTextureVertex(0.0F, tco.grid_amt[(b + 1) * 13] / 96.0F, (b + 1) / 12.0F, 1.0F, 0.0F),
                     new PositionTextureVertex(0.0F, 0.0F, (b + 1) / 12.0F, 0.0F, 0.0F)
                  }
               );
               quad.flipFace();
               quad.draw(tessellator.func_178180_c(), 1.0F, (int)(200.0F + s * 15.0F), colors, new float[]{0.0F, 0.9F, 0.9F, 0.0F});
            }

            if (a == 11) {
               quad = new TexturedQuadTC(
                  new PositionTextureVertex[]{
                     new PositionTextureVertex(1.0F, 0.0F, b / 12.0F, 0.0F, 1.0F),
                     new PositionTextureVertex(1.0F, tco.grid_amt[a + 1 + b * 13] / 96.0F, b / 12.0F, 1.0F, 1.0F),
                     new PositionTextureVertex(1.0F, tco.grid_amt[a + 1 + (b + 1) * 13] / 96.0F, (b + 1) / 12.0F, 1.0F, 0.0F),
                     new PositionTextureVertex(1.0F, 0.0F, (b + 1) / 12.0F, 0.0F, 0.0F)
                  }
               );
               quad.draw(tessellator.func_178180_c(), 1.0F, (int)(200.0F + s * 15.0F), colors, new float[]{0.0F, 0.9F, 0.9F, 0.0F});
            }

            if (b == 0) {
               quad = new TexturedQuadTC(
                  new PositionTextureVertex[]{
                     new PositionTextureVertex(a / 12.0F, 0.0F, 0.0F, 0.0F, 1.0F),
                     new PositionTextureVertex((a + 1) / 12.0F, 0.0F, 0.0F, 1.0F, 1.0F),
                     new PositionTextureVertex((a + 1) / 12.0F, tco.grid_amt[a + 1] / 96.0F, 0.0F, 1.0F, 0.0F),
                     new PositionTextureVertex(a / 12.0F, tco.grid_amt[a] / 96.0F, 0.0F, 0.0F, 0.0F)
                  }
               );
               quad.flipFace();
               quad.draw(tessellator.func_178180_c(), 1.0F, (int)(200.0F + s * 15.0F), colors, new float[]{0.0F, 0.0F, 0.9F, 0.9F});
            }

            if (b == 11) {
               quad = new TexturedQuadTC(
                  new PositionTextureVertex[]{
                     new PositionTextureVertex(a / 12.0F, 0.0F, 1.0F, 0.0F, 1.0F),
                     new PositionTextureVertex((a + 1) / 12.0F, 0.0F, 1.0F, 1.0F, 1.0F),
                     new PositionTextureVertex((a + 1) / 12.0F, tco.grid_amt[a + 1 + (b + 1) * 13] / 96.0F, 1.0F, 1.0F, 0.0F),
                     new PositionTextureVertex(a / 12.0F, tco.grid_amt[a + (b + 1) * 13] / 96.0F, 1.0F, 0.0F, 0.0F)
                  }
               );
               quad.draw(tessellator.func_178180_c(), 1.0F, (int)(200.0F + s * 15.0F), colors, new float[]{0.0F, 0.0F, 0.9F, 0.9F});
            }
         }
      }

      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GlStateManager.func_179129_p();
      GL11.glTranslated(0.0, 1.0, 0.0);
      GL11.glRotatef(270.0F, 0.0F, 0.0F, 1.0F);

      for (int q = 0; q < 4; q++) {
         GL11.glPushMatrix();
         GL11.glRotatef(90.0F * q, 1.0F, 0.0F, 0.0F);
         GL11.glTranslated(0.0, 0.0, -0.5);
         UtilsFX.renderQuadCentered(this.sideTexture, 1.0F, rc, gc, bc, 220, 1, 0.8F);
         GL11.glPopMatrix();
      }

      GlStateManager.func_179089_o();
      GL11.glPopMatrix();
      GL11.glShadeModel(7424);
      GL11.glDisable(3042);
      GL11.glPopMatrix();
   }

   int[] calcColorMap(float[] fs, float r, float g, float b) {
      int[] colors = new int[]{0, 0, 0, 0};

      for (int a = 0; a < 4; a++) {
         float g1 = g;
         if (fs[a] > 0.0F) {
            float ll = 1.0F - fs[a];
            g1 *= ll;
         }

         g1 = MathHelper.func_76131_a(g1, 0.0F, 1.0F);
         Color color1 = new Color(r * 0.8F, g1, b);
         colors[a] = color1.getRGB();
      }

      return colors;
   }
}
