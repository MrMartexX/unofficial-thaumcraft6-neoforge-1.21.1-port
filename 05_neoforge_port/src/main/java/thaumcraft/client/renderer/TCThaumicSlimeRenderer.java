package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCThaumicSlimeEntity;

final class TCThaumicSlimeRenderer extends MobRenderer<TCThaumicSlimeEntity, SlimeModel<TCThaumicSlimeEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/tslime.png");

    TCThaumicSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, new SlimeModel<>(context.bakeLayer(ModelLayers.SLIME)), 0.25F);
    }

    @Override
    public ResourceLocation getTextureLocation(TCThaumicSlimeEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(TCThaumicSlimeEntity entity, PoseStack poseStack, float partialTickTime) {
        float size = entity.getSize();
        poseStack.scale(size, size, size);
        shadowRadius = 0.25F * size;
    }
}
