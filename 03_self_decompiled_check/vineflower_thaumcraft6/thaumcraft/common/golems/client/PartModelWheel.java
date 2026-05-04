package thaumcraft.common.golems.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.PartModel;
import thaumcraft.common.golems.parts.GolemLegWheels;

public class PartModelWheel extends PartModel {
   public PartModelWheel(ResourceLocation objModel, ResourceLocation objTexture, PartModel.EnumAttachPoint attachPoint) {
      super(objModel, objTexture, attachPoint);
   }

   @Override
   public void preRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, PartModel.EnumLimbSide side) {
      if (partName.equals("wheel")) {
         float lastRot = 0.0F;
         if (GolemLegWheels.ani.containsKey(golem.getGolemEntity().func_145782_y())) {
            lastRot = GolemLegWheels.ani.get(golem.getGolemEntity().func_145782_y());
         }

         GlStateManager.func_179137_b(0.0, -0.375, 0.0);
         GlStateManager.func_179114_b(lastRot, -1.0F, 0.0F, 0.0F);
      }
   }
}
