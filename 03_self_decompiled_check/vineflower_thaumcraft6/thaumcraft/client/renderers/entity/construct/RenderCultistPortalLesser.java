package thaumcraft.client.renderers.entity.construct;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;

@SideOnly(Side.CLIENT)
public class RenderCultistPortalLesser extends Render {
   public static final ResourceLocation portaltex = new ResourceLocation("thaumcraft", "textures/misc/cultist_portal.png");

   public RenderCultistPortalLesser(RenderManager renderManager) {
      super(renderManager);
      this.field_76989_e = 0.0F;
      this.field_76987_f = 0.0F;
   }

   public void renderPortal(EntityCultistPortalLesser portal, double px, double py, double pz, float par8, float f) {
      if (portal.isActive()) {
         long nt = System.nanoTime();
         long time = nt / 50000000L;
         float scaley = 1.4F;
         int e = (int)Math.min(50.0F, portal.activeCounter + f);
         if (portal.field_70737_aN > 0) {
            double d = Math.sin(portal.field_70737_aN * 72 * Math.PI / 180.0);
            scaley = (float)(scaley - d / 4.0);
            e = (int)(e + 6.0 * d);
         }

         if (portal.pulse > 0) {
            double d = Math.sin(portal.pulse * 36 * Math.PI / 180.0);
            scaley = (float)(scaley + d / 4.0);
            e = (int)(e + 12.0 * d);
         }

         float scale = e / 50.0F * 1.25F;
         py += portal.field_70131_O / 2.0F;
         float m = (1.0F - portal.func_110143_aJ() / portal.func_110138_aP()) / 3.0F;
         float bob = MathHelper.func_76126_a(portal.activeCounter / (5.0F - 12.0F * m)) * m + m;
         float bob2 = MathHelper.func_76126_a(portal.activeCounter / (6.0F - 15.0F * m)) * m + m;
         float alpha = 1.0F - bob;
         scaley -= bob / 4.0F;
         scale -= bob2 / 3.0F;
         this.func_110776_a(portaltex);
         GL11.glPushMatrix();
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
         if (Minecraft.func_71410_x().func_175606_aa() instanceof EntityPlayer) {
            GL11.glDepthMask(false);
            Tessellator tessellator = Tessellator.func_178181_a();
            float arX = ActiveRenderInfo.func_178808_b();
            float arZ = ActiveRenderInfo.func_178803_d();
            float arYZ = ActiveRenderInfo.func_178805_e();
            float arXY = ActiveRenderInfo.func_178807_f();
            float arXZ = ActiveRenderInfo.func_178809_c();
            tessellator.func_178180_c().func_181668_a(7, UtilsFX.VERTEXFORMAT_POS_TEX_CO_LM_NO);
            Vec3d v1 = new Vec3d(-arX - arYZ, -arXZ, -arZ - arXY);
            Vec3d v2 = new Vec3d(-arX + arYZ, arXZ, -arZ + arXY);
            Vec3d v3 = new Vec3d(arX + arYZ, arXZ, arZ + arXY);
            Vec3d v4 = new Vec3d(arX - arYZ, -arXZ, arZ - arXY);
            int frame = 15 - (int)time % 16;
            float f2 = frame / 16.0F;
            float f3 = f2 + 0.0625F;
            float f4 = 0.0F;
            float f5 = 1.0F;
            int i = 220;
            int j = i >> 16 & 65535;
            int k = i & 65535;
            tessellator.func_178180_c()
               .func_181662_b(px + v1.field_72450_a * scale, py + v1.field_72448_b * scaley, pz + v1.field_72449_c * scale)
               .func_187315_a(f3, f4)
               .func_181666_a(1.0F, 1.0F, 1.0F, alpha)
               .func_187314_a(j, k)
               .func_181663_c(0.0F, 0.0F, -1.0F)
               .func_181675_d();
            tessellator.func_178180_c()
               .func_181662_b(px + v2.field_72450_a * scale, py + v2.field_72448_b * scaley, pz + v2.field_72449_c * scale)
               .func_187315_a(f3, f5)
               .func_181666_a(1.0F, 1.0F, 1.0F, alpha)
               .func_187314_a(j, k)
               .func_181663_c(0.0F, 0.0F, -1.0F)
               .func_181675_d();
            tessellator.func_178180_c()
               .func_181662_b(px + v3.field_72450_a * scale, py + v3.field_72448_b * scaley, pz + v3.field_72449_c * scale)
               .func_187315_a(f2, f5)
               .func_181666_a(1.0F, 1.0F, 1.0F, alpha)
               .func_187314_a(j, k)
               .func_181663_c(0.0F, 0.0F, -1.0F)
               .func_181675_d();
            tessellator.func_178180_c()
               .func_181662_b(px + v4.field_72450_a * scale, py + v4.field_72448_b * scaley, pz + v4.field_72449_c * scale)
               .func_187315_a(f2, f4)
               .func_181666_a(1.0F, 1.0F, 1.0F, alpha)
               .func_187314_a(j, k)
               .func_181663_c(0.0F, 0.0F, -1.0F)
               .func_181675_d();
            tessellator.func_78381_a();
            GL11.glDepthMask(true);
         }

         GL11.glDisable(32826);
         GL11.glDisable(3042);
         GL11.glPopMatrix();
      }
   }

   public void func_76986_a(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
      this.renderPortal((EntityCultistPortalLesser)par1Entity, par2, par4, par6, par8, par9);
   }

   protected ResourceLocation func_110775_a(Entity entity) {
      return portaltex;
   }
}
