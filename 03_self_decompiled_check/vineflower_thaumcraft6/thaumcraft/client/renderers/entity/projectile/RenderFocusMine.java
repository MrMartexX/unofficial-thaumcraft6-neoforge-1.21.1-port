package thaumcraft.client.renderers.entity.projectile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.entity.ModelGrappler;
import thaumcraft.common.entities.projectile.EntityFocusMine;

public class RenderFocusMine extends Render {
   ResourceLocation beam = new ResourceLocation("thaumcraft", "textures/entity/mine.png");
   private ModelGrappler model;

   public RenderFocusMine(RenderManager rm) {
      super(rm);
      this.field_76989_e = 0.0F;
      this.model = new ModelGrappler();
   }

   public void renderEntityAt(Entity entity, double x, double y, double z, float fq, float pticks) {
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glTranslated(x, y, z);
      EntityFocusMine mine = (EntityFocusMine)entity;
      float f = (mine.counter + pticks) % 8.0F / 8.0F;
      int i = 61680;
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, j, k);
      GL11.glColor4f(1.0F, 1.0F - f, 1.0F - f, 1.0F);
      this.func_110776_a(this.beam);
      GlStateManager.func_179114_b(entity.field_70126_B + (entity.field_70177_z - entity.field_70126_B) * pticks - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(entity.field_70127_C + (entity.field_70125_A - entity.field_70127_C) * pticks, 0.0F, 0.0F, 1.0F);
      this.model.render();
      GL11.glDisable(3042);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
   }

   public void func_76986_a(Entity entity, double d, double d1, double d2, float f, float f1) {
      this.renderEntityAt(entity, d, d1, d2, f, f1);
   }

   protected ResourceLocation func_110775_a(Entity entity) {
      return TextureMap.field_110575_b;
   }
}
