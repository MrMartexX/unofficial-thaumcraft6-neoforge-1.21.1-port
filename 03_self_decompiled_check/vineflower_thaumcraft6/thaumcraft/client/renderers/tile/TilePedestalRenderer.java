package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.crafting.TilePedestal;

@SideOnly(Side.CLIENT)
public class TilePedestalRenderer extends TileEntitySpecialRenderer<TilePedestal> {
   public void render(TilePedestal ped, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      super.func_192841_a(ped, x, y, z, partialTicks, destroyStage, alpha);
      if (ped != null && !ped.getSyncedStackInSlot(0).func_190926_b()) {
         EntityItem entityitem = null;
         float ticks = Minecraft.func_71410_x().func_175606_aa().field_70173_aa + partialTicks;
         GL11.glPushMatrix();
         GL11.glTranslatef((float)x + 0.5F, (float)y + 0.75F, (float)z + 0.5F);
         GL11.glScaled(1.25, 1.25, 1.25);
         GL11.glRotatef(ticks % 360.0F, 0.0F, 1.0F, 0.0F);
         ItemStack is = ped.getSyncedStackInSlot(0).func_77946_l();
         is.func_190920_e(1);
         entityitem = new EntityItem(Minecraft.func_71410_x().field_71441_e, 0.0, 0.0, 0.0, is);
         entityitem.field_70290_d = 0.0F;
         RenderManager rendermanager = Minecraft.func_71410_x().func_175598_ae();
         rendermanager.func_188391_a(entityitem, 0.0, 0.0, 0.0, 0.0F, 0.0F, false);
         GL11.glPopMatrix();
      }
   }
}
