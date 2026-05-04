package thaumcraft.client.renderers.entity.mob;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.renderers.models.entity.ModelEldritchCrab;

@SideOnly(Side.CLIENT)
public class RenderEldritchCrab extends RenderLiving {
   private static final ResourceLocation skin = new ResourceLocation("thaumcraft", "textures/entity/crab.png");

   public RenderEldritchCrab(RenderManager renderManager) {
      super(renderManager, new ModelEldritchCrab(), 0.5F);
   }

   protected ResourceLocation func_110775_a(Entity entity) {
      return skin;
   }

   public void renderCrab(EntityLiving crab, double par2, double par4, double par6, float par8, float par9) {
      super.func_76986_a(crab, par2, par4, par6, par8, par9);
   }

   public void func_76986_a(EntityLiving par1Entity, double par2, double par4, double par6, float par8, float par9) {
      this.renderCrab(par1Entity, par2, par4, par6, par8, par9);
   }

   protected void func_77041_b(EntityLivingBase entitylivingbaseIn, float partialTickTime) {
      GlStateManager.func_179152_a(0.8F, 0.8F, 0.8F);
      super.func_77041_b(entitylivingbaseIn, partialTickTime);
   }
}
