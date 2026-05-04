package thaumcraft.client.renderers.entity.mob;

import java.util.ArrayList;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class LayerTainted implements LayerRenderer<EntityLiving> {
   private static final ResourceLocation TAINT_TEXTURE = new ResourceLocation("thaumcraft:textures/models/taint_fibres.png");
   private final RenderLivingBase renderer;
   private final ModelBase model;
   public static ArrayList<Integer> taintLayers = new ArrayList<>();

   public LayerTainted(int i, RenderLivingBase witherRendererIn, ModelBase model) {
      this.renderer = witherRendererIn;
      this.model = model;
      taintLayers.add(i);
   }

   public void doRenderLayer(
      EntityLiving entitylivingbaseIn,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch,
      float scale
   ) {
      if (taintLayers.contains(entitylivingbaseIn.func_145782_y())) {
         boolean flag = entitylivingbaseIn.func_82150_aj();
         GlStateManager.func_179132_a(!flag);
         this.renderer.func_110776_a(TAINT_TEXTURE);
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179096_D();
         float f = entitylivingbaseIn.func_145782_y();
         float f1 = MathHelper.func_76134_b(f * 2.5E-4F);
         float f2 = f * 0.001F;
         GlStateManager.func_179152_a(8.0F, 4.0F, 4.0F);
         GlStateManager.func_179109_b(f1, f2, 0.0F);
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179147_l();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.66F);
         GL11.glBlendFunc(770, 771);
         this.model.func_78086_a(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
         this.model.func_178686_a(this.renderer.func_177087_b());
         this.model.func_78088_a(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179096_D();
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179084_k();
         GlStateManager.func_179132_a(flag);
      }
   }

   public boolean func_177142_b() {
      return false;
   }
}
