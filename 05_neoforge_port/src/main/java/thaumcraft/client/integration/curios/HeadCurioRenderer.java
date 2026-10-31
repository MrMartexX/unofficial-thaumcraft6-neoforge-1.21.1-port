package thaumcraft.client.integration.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class HeadCurioRenderer implements ICurioRenderer {
    private final ResourceLocation texture;

    public HeadCurioRenderer(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack matrixStack,
            RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource renderTypeBuffer,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (!(renderLayerParent.getModel() instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        LivingEntity wearer = slotContext.entity();
        boolean hasHelmet = !wearer.getItemBySlot(EquipmentSlot.HEAD).isEmpty();

        matrixStack.pushPose();
        humanoidModel.head.translateAndRotate(matrixStack);

        float offsetZ = hasHelmet ? -0.285F : -0.252F;
        float x0 = -0.255F;
        float x1 = 0.255F;
        float y0 = -0.505F;
        float y1 = 0.005F;

        VertexConsumer consumer = renderTypeBuffer.getBuffer(RenderType.entityCutout(texture));
        Matrix4f matrix = matrixStack.last().pose();

        consumer.addVertex(matrix, x0, y1, offsetZ)
                .setColor(255, 255, 255, 255)
                .setUv(1.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0.0F, 0.0F, -1.0F);
        consumer.addVertex(matrix, x1, y1, offsetZ)
                .setColor(255, 255, 255, 255)
                .setUv(0.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0.0F, 0.0F, -1.0F);
        consumer.addVertex(matrix, x1, y0, offsetZ)
                .setColor(255, 255, 255, 255)
                .setUv(0.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0.0F, 0.0F, -1.0F);
        consumer.addVertex(matrix, x0, y0, offsetZ)
                .setColor(255, 255, 255, 255)
                .setUv(1.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0.0F, 0.0F, -1.0F);

        matrixStack.popPose();
    }
}
