package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.client.renderers.models.block.ModelTubeValve;
import thaumcraft.common.tiles.essentia.TileTubeOneway;

public class TileTubeOnewayRenderer extends TileEntitySpecialRenderer {
   private ModelTubeValve model;
   private static final ResourceLocation TEX_VALVE = new ResourceLocation("thaumcraft", "textures/models/valve.png");
   EnumFacing fd = null;

   public TileTubeOnewayRenderer() {
      this.model = new ModelTubeValve();
   }

   public void renderEntityAt(TileTubeOneway valve, double x, double y, double z, float fq) {
      this.func_147499_a(TEX_VALVE);
      if (valve.func_145831_w() == null
         || ThaumcraftApiHelper.getConnectableTile(valve.func_145831_w(), valve.func_174877_v(), valve.facing.func_176734_d()) != null) {
         GL11.glPushMatrix();
         this.fd = valve.facing;
         GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
         if (this.fd.func_96559_d() == 0) {
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
            GL11.glRotatef(90.0F, this.fd.func_96559_d(), 0.0F, 0.0F);
         }

         GL11.glRotatef(90.0F, this.fd.func_82601_c(), this.fd.func_96559_d(), this.fd.func_82599_e());
         GL11.glPushMatrix();
         GL11.glColor3f(0.45F, 0.5F, 1.0F);
         GL11.glScaled(2.0, 2.0, 2.0);
         GL11.glTranslated(0.0, -0.32F, 0.0);
         this.model.renderRod();
         GL11.glPopMatrix();
         GL11.glColor3f(1.0F, 1.0F, 1.0F);
         GL11.glPopMatrix();
      }
   }

   public void func_192841_a(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      super.func_192841_a(te, x, y, z, partialTicks, destroyStage, alpha);
      this.renderEntityAt((TileTubeOneway)te, x, y, z, partialTicks);
   }
}
