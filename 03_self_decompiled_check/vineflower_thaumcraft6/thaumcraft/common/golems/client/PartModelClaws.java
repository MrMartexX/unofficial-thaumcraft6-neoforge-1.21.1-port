package thaumcraft.common.golems.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.PartModel;

public class PartModelClaws extends PartModel {
   float f = 0.0F;

   public PartModelClaws(ResourceLocation objModel, ResourceLocation objTexture, PartModel.EnumAttachPoint attachPoint) {
      super(objModel, objTexture, attachPoint);
   }

   @Override
   public void preRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, PartModel.EnumLimbSide side) {
      if (partName.startsWith("claw")) {
         this.f = 0.0F;
         this.f = golem.getGolemEntity().func_70678_g(partialTicks) * 4.1F;
         this.f = this.f * this.f;
         GlStateManager.func_179137_b(0.0, -0.2, 0.0);
         GlStateManager.func_179114_b(this.f, partName.endsWith("1") ? 1.0F : -1.0F, 0.0F, 0.0F);
      }
   }
}
