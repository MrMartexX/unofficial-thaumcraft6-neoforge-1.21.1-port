package thaumcraft.client.renderers.entity.mob;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.entities.monster.boss.EntityCultistLeader;

@SideOnly(Side.CLIENT)
public class RenderCultistLeader extends RenderBiped<EntityCultistLeader> {
   private static final ResourceLocation skin = new ResourceLocation("thaumcraft", "textures/entity/cultist.png");
   private static final ResourceLocation fl = new ResourceLocation("thaumcraft", "textures/misc/wispy.png");

   public RenderCultistLeader(RenderManager p_i46127_1_) {
      super(p_i46127_1_, new ModelBiped(), 0.5F);
      this.func_177094_a(new LayerHeldItem(this));
      LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
         protected void func_177177_a() {
            this.field_177189_c = new ModelBiped();
            this.field_177186_d = new ModelBiped();
         }
      };
      this.func_177094_a(layerbipedarmor);
   }

   protected ResourceLocation getEntityTexture(EntityCultistLeader p_110775_1_) {
      return skin;
   }

   protected void preRenderCallback(EntityCultistLeader entitylivingbaseIn, float partialTickTime) {
      super.func_77041_b(entitylivingbaseIn, partialTickTime);
      GL11.glScalef(1.15F, 1.15F, 1.15F);
   }

   private void drawFloatyLine(
      double x, double y, double z, double x2, double y2, double z2, float partialTicks, int color, float speed, float distance, float width
   ) {
      Entity player = Minecraft.func_71410_x().func_175606_aa();
      double iPX = player.field_70169_q + (player.field_70165_t - player.field_70169_q) * partialTicks;
      double iPY = player.field_70167_r + (player.field_70163_u - player.field_70167_r) * partialTicks;
      double iPZ = player.field_70166_s + (player.field_70161_v - player.field_70166_s) * partialTicks;
      double ePX = x2;
      double ePY = y2;
      double ePZ = z2;
      GL11.glTranslated(-iPX + ePX, -iPY + ePY, -iPZ + ePZ);
      float time = (float)(System.nanoTime() / 30000000L);
      Color co = new Color(color);
      float r = co.getRed() / 255.0F;
      float g = co.getGreen() / 255.0F;
      float b = co.getBlue() / 255.0F;
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      Tessellator tessellator = Tessellator.func_178181_a();
      double ds1x = ePX;
      double ds1y = ePY;
      double ds1z = ePZ;
      double dd1x = x;
      double dd1y = y;
      double dd1z = z;
      double dc1x = (float)(dd1x - ds1x);
      double dc1y = (float)(dd1y - ds1y);
      double dc1z = (float)(dd1z - ds1z);
      this.func_110776_a(fl);
      tessellator.func_178180_c().func_181668_a(5, DefaultVertexFormats.field_181709_i);
      double dx2 = 0.0;
      double dy2 = 0.0;
      double dz2 = 0.0;
      double d3 = x - ePX;
      double d4 = y - ePY;
      double d5 = z - ePZ;
      float dist = MathHelper.func_76133_a(d3 * d3 + d4 * d4 + d5 * d5);
      float blocks = Math.round(dist);
      float length = blocks * 6.0F;
      float f9 = 0.0F;
      float f10 = 1.0F;

      for (int i = 0; i <= length * distance; i++) {
         float f2 = i / length;
         float f2a = i * 1.5F / length;
         f2a = Math.min(0.75F, f2a);
         float f3 = 1.0F - Math.abs(i - length / 2.0F) / (length / 2.0F);
         double dx = dc1x + MathHelper.func_76126_a((float)((z % 16.0 + dist * (1.0F - f2) * 6.0F - time % 32767.0F / 5.0F) / 4.0)) * 0.5F * f3;
         double dy = dc1y + MathHelper.func_76126_a((float)((x % 16.0 + dist * (1.0F - f2) * 6.0F - time % 32767.0F / 5.0F) / 3.0)) * 0.5F * f3;
         double dz = dc1z + MathHelper.func_76126_a((float)((y % 16.0 + dist * (1.0F - f2) * 6.0F - time % 32767.0F / 5.0F) / 2.0)) * 0.5F * f3;
         float f13 = (1.0F - f2) * dist - time * speed;
         tessellator.func_178180_c().func_181662_b(dx * f2, dy * f2 - width, dz * f2).func_187315_a(f13, f10).func_181666_a(r, g, b, 0.8F).func_181675_d();
         tessellator.func_178180_c().func_181662_b(dx * f2, dy * f2 + width, dz * f2).func_187315_a(f13, f9).func_181666_a(r, g, b, 0.8F).func_181675_d();
      }

      tessellator.func_78381_a();
      tessellator.func_178180_c().func_181668_a(5, DefaultVertexFormats.field_181709_i);

      for (int var84 = 0; var84 <= length * distance; var84++) {
         float f2 = var84 / length;
         float f2a = var84 * 1.5F / length;
         f2a = Math.min(0.75F, f2a);
         float f3 = 1.0F - Math.abs(var84 - length / 2.0F) / (length / 2.0F);
         double dx = dc1x + MathHelper.func_76126_a((float)((z % 16.0 + dist * (1.0F - f2) * 6.0F - time % 32767.0F / 5.0F) / 4.0)) * 0.5F * f3;
         double dy = dc1y + MathHelper.func_76126_a((float)((x % 16.0 + dist * (1.0F - f2) * 6.0F - time % 32767.0F / 5.0F) / 3.0)) * 0.5F * f3;
         double dz = dc1z + MathHelper.func_76126_a((float)((y % 16.0 + dist * (1.0F - f2) * 6.0F - time % 32767.0F / 5.0F) / 2.0)) * 0.5F * f3;
         float f13 = (1.0F - f2) * dist - time * speed;
         tessellator.func_178180_c().func_181662_b(dx * f2 - width, dy * f2, dz * f2).func_187315_a(f13, f10).func_181666_a(r, g, b, 0.8F).func_181675_d();
         tessellator.func_178180_c().func_181662_b(dx * f2 + width, dy * f2, dz * f2).func_187315_a(f13, f9).func_181666_a(r, g, b, 0.8F).func_181675_d();
      }

      tessellator.func_78381_a();
      GL11.glDisable(3042);
   }
}
