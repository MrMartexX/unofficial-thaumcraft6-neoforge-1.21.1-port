package thaumcraft.common.golems.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.PartModel;

public class PartModelHauler extends PartModel {
   public PartModelHauler(ResourceLocation objModel, ResourceLocation objTexture, PartModel.EnumAttachPoint attachPoint) {
      super(objModel, objTexture, attachPoint);
   }

   @Override
   public void postRenderObjectPart(String partName, IGolemAPI golem, float partialTicks, PartModel.EnumLimbSide side) {
      if (golem.getCarrying().size() > 1 && golem.getCarrying().get(1) != null) {
         ItemStack itemstack = (ItemStack)golem.getCarrying().get(1);
         if (itemstack != null && !itemstack.func_190926_b()) {
            GlStateManager.func_179094_E();
            Item item = itemstack.func_77973_b();
            Minecraft minecraft = Minecraft.func_71410_x();
            GlStateManager.func_179139_a(0.375, 0.375, 0.375);
            GlStateManager.func_179109_b(0.0F, 0.33F, 0.825F);
            if (!(item instanceof ItemBlock)) {
               GlStateManager.func_179109_b(0.0F, 0.0F, -0.25F);
            }

            minecraft.func_175597_ag().func_178099_a(golem.getGolemEntity(), itemstack, TransformType.HEAD);
            GlStateManager.func_179121_F();
         }
      }
   }
}
