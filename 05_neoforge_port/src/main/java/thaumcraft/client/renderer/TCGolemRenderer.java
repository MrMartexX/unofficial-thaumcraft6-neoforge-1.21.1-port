package thaumcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.common.entities.TCGolemEntity;

final class TCGolemRenderer extends HumanoidMobRenderer<TCGolemEntity, HumanoidModel<TCGolemEntity>> {
    private static final ResourceLocation TEX_WOOD =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/golems/mat_wood.png");
    private static final ResourceLocation TEX_IRON =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/golems/mat_iron.png");
    private static final ResourceLocation TEX_CLAY =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/golems/mat_clay.png");
    private static final ResourceLocation TEX_BRASS =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/golems/mat_brass.png");
    private static final ResourceLocation TEX_THAUMIUM =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/golems/mat_thaumium.png");
    private static final ResourceLocation TEX_VOID =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/golems/mat_void.png");

    TCGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(TCGolemEntity entity) {
        IGolemProperties props = entity.getProperties();
        if (props != null && props.getMaterial() != null) {
            String key = props.getMaterial().key;
            if (key != null) {
                return switch (key.toLowerCase()) {
                    case "iron" -> TEX_IRON;
                    case "clay" -> TEX_CLAY;
                    case "brass" -> TEX_BRASS;
                    case "thaumium" -> TEX_THAUMIUM;
                    case "void" -> TEX_VOID;
                    default -> TEX_WOOD;
                };
            }
        }
        return TEX_WOOD;
    }

    @Override
    protected void scale(TCGolemEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.5F, 0.5F, 0.5F);
    }
}
