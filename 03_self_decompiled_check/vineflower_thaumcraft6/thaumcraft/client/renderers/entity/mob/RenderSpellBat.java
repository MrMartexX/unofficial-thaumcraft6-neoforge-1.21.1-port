package thaumcraft.client.renderers.entity.mob;

import java.awt.Color;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.entity.ModelFireBat;
import thaumcraft.common.entities.monster.EntitySpellBat;

@SideOnly(Side.CLIENT)
public class RenderSpellBat extends RenderLiving {
   private int renderedBatSize = ((ModelFireBat)this.field_77045_g).getBatSize();
   private static final ResourceLocation rl = new ResourceLocation("thaumcraft", "textures/entity/spellbat.png");

   public RenderSpellBat(RenderManager rm) {
      super(rm, new ModelFireBat(), 0.25F);
   }

   public void func_82443_a(EntitySpellBat bat, double par2, double par4, double par6, float par8, float par9) {
      int var10 = ((ModelFireBat)this.field_77045_g).getBatSize();
      if (var10 != this.renderedBatSize) {
         this.renderedBatSize = var10;
         this.field_77045_g = new ModelBat();
      }

      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      Color c = new Color(bat.color);
      float r = c.getRed() / 255.0F;
      float g = c.getGreen() / 255.0F;
      float b = c.getBlue() / 255.0F;
      GL11.glColor4f(r, g, b, 0.5F);
      super.func_76986_a(bat, par2, par4, par6, par8, par9);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(3042);
      GL11.glPopMatrix();
   }

   protected void func_82442_a(EntitySpellBat par1EntityBat, float par2) {
      GL11.glScalef(0.35F, 0.35F, 0.35F);
   }

   protected void func_82445_a(EntitySpellBat par1EntityBat, double par2, double par4, double par6) {
      super.func_77039_a(par1EntityBat, par2, par4, par6);
   }

   protected void func_82444_a(EntitySpellBat par1EntityBat, float par2, float par3, float par4) {
      GL11.glTranslatef(0.0F, -0.1F, 0.0F);
      super.func_77043_a(par1EntityBat, par2, par3, par4);
   }

   protected void func_77041_b(EntityLivingBase par1EntityLiving, float par2) {
      this.func_82442_a((EntitySpellBat)par1EntityLiving, par2);
   }

   protected void func_77043_a(EntityLivingBase par1EntityLiving, float par2, float par3, float par4) {
      this.func_82444_a((EntitySpellBat)par1EntityLiving, par2, par3, par4);
   }

   protected void func_77039_a(EntityLivingBase par1EntityLiving, double par2, double par4, double par6) {
      this.func_82445_a((EntitySpellBat)par1EntityLiving, par2, par4, par6);
   }

   public void func_76986_a(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
      this.func_82443_a((EntitySpellBat)par1EntityLiving, par2, par4, par6, par8, par9);
   }

   protected ResourceLocation func_110775_a(Entity entity) {
      return rl;
   }
}
