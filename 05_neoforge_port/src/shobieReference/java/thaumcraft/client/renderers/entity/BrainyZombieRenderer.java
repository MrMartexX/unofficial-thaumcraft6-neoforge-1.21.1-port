package thaumcraft.client.renderers.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.monster.EntityBrainyZombie;

/**
 * Renderer for Brainy Zombies - zombies with exposed brains.
 * Uses the vanilla zombie model with a custom texture.
 */
@OnlyIn(Dist.CLIENT)
public class BrainyZombieRenderer extends HumanoidMobRenderer<EntityBrainyZombie, ZombieModel<EntityBrainyZombie>> {
    
    private static final ResourceLocation TEXTURE = 
            new ResourceLocation(Thaumcraft.MODID, "textures/entity/brainy_zombie.png");
    
    public BrainyZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
    }
    
    @Override
    public ResourceLocation getTextureLocation(EntityBrainyZombie entity) {
        return TEXTURE;
    }
}
