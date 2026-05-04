package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.models.block.ModelResearchTable;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.crafting.TileResearchTable;

@SideOnly(Side.CLIENT)
public class TileResearchTableRenderer extends TileEntitySpecialRenderer<TileResearchTable> {
   private ModelResearchTable tableModel = new ModelResearchTable();
   private static final ResourceLocation TEX = new ResourceLocation("thaumcraft", "textures/blocks/research_table_model.png");

   public void render(TileResearchTable table, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      GL11.glPushMatrix();
      this.func_147499_a(TEX);
      GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0F, (float)z + 0.5F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      switch (BlockStateUtils.getFacing(table.func_145832_p())) {
         case EAST:
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            break;
         case WEST:
            GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
            break;
         case SOUTH:
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
      }

      if (table.data != null) {
         this.tableModel.renderScroll(Aspect.ALCHEMY.getColor());
      }

      if (!table.getSyncedStackInSlot(0).func_190926_b() && table.getSyncedStackInSlot(0).func_77973_b() instanceof IScribeTools) {
         this.tableModel.renderInkwell();
         GL11.glPushMatrix();
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
         GL11.glTranslated(-0.5, 0.1, 0.125);
         GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
         GL11.glScaled(0.5, 0.5, 0.5);
         UtilsFX.renderItemIn2D("thaumcraft:research/quill", 0.0625F);
         GL11.glDisable(3042);
         GL11.glPopMatrix();
      }

      GL11.glPopMatrix();
      GL11.glPushMatrix();
      GL11.glPopMatrix();
   }
}
