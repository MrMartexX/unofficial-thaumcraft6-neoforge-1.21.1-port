package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCBrainyZombieEntity;
import thaumcraft.common.entities.TCGiantBrainyZombieEntity;

final class TCBrainyZombieRenderer<T extends TCBrainyZombieEntity> extends HumanoidMobRenderer<T, ZombieModel<T>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/bzombie.png");

    TCBrainyZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
        addLayer(new HumanoidArmorLayer<>(
                this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)),
                context.getModelManager()
        ));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTickTime) {
        super.scale(entity, poseStack, partialTickTime);
        if (entity instanceof TCGiantBrainyZombieEntity giant) {
            float scale = 1.0F + giant.getAnger();
            poseStack.scale(scale, scale, scale);
        }
    }
}
