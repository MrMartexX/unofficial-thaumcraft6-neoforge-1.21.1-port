package thaumcraft.common.golems.client;

import java.util.HashMap;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.PartModel;

public class PartModelBreakers extends PartModel {
   private HashMap<Integer, Float[]> ani = new HashMap<>();

   public PartModelBreakers(ResourceLocation objModel, ResourceLocation objTexture, PartModel.EnumAttachPoint attachPoint) {
      super(objModel, objTexture, attachPoint);
   }

   @Override
   public void preRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, PartModel.EnumLimbSide side) {
      if (partName.equals("grinder")) {
         float lastSpeed = 0.0F;
         float lastRot = 0.0F;
         if (this.ani.containsKey(golem.getGolemEntity().func_145782_y())) {
            lastSpeed = this.ani.get(golem.getGolemEntity().func_145782_y())[0];
            lastRot = this.ani.get(golem.getGolemEntity().func_145782_y())[1];
         }

         float f = Math.max(lastSpeed, golem.getGolemEntity().func_70678_g(partialTicks) * 20.0F);
         float rot = lastRot + f;
         lastSpeed = f * 0.99F;
         this.ani.put(golem.getGolemEntity().func_145782_y(), new Float[]{lastSpeed, rot});
         GlStateManager.func_179137_b(0.0, -0.34, 0.0);
         GlStateManager.func_179114_b(
            (golem.getGolemEntity().field_70173_aa + partialTicks) / 2.0F + rot + (side == PartModel.EnumLimbSide.LEFT ? 22 : 0),
            side == PartModel.EnumLimbSide.LEFT ? -1.0F : 1.0F,
            0.0F,
            0.0F
         );
      }
   }
}
