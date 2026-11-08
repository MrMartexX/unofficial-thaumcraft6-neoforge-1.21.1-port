package thaumcraft.client.renderer.gear;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import thaumcraft.Thaumcraft;

public final class TCModelLayers {
    public static final ModelLayerLocation FORTRESS_ARMOR_OUTER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "fortress_armor"), "outer");
    public static final ModelLayerLocation FORTRESS_ARMOR_INNER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "fortress_armor"), "inner");

    public static final ModelLayerLocation ROBE_ARMOR_OUTER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "robe_armor"), "outer");
    public static final ModelLayerLocation ROBE_ARMOR_INNER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "robe_armor"), "inner");

    public static final ModelLayerLocation KNIGHT_ARMOR_OUTER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "knight_armor"), "outer");
    public static final ModelLayerLocation KNIGHT_ARMOR_INNER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "knight_armor"), "inner");

    public static final ModelLayerLocation LEADER_ARMOR_OUTER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "leader_armor"), "outer");
    public static final ModelLayerLocation LEADER_ARMOR_INNER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "leader_armor"), "inner");

    private TCModelLayers() {
    }

    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FORTRESS_ARMOR_OUTER, TCFortressArmorModel::createOuterLayer);
        event.registerLayerDefinition(FORTRESS_ARMOR_INNER, TCFortressArmorModel::createInnerLayer);
        event.registerLayerDefinition(ROBE_ARMOR_OUTER, TCRobeArmorModel::createOuterLayer);
        event.registerLayerDefinition(ROBE_ARMOR_INNER, TCRobeArmorModel::createInnerLayer);
        event.registerLayerDefinition(KNIGHT_ARMOR_OUTER, TCKnightArmorModel::createOuterLayer);
        event.registerLayerDefinition(KNIGHT_ARMOR_INNER, TCKnightArmorModel::createInnerLayer);
        event.registerLayerDefinition(LEADER_ARMOR_OUTER, TCLeaderArmorModel::createOuterLayer);
        event.registerLayerDefinition(LEADER_ARMOR_INNER, TCLeaderArmorModel::createInnerLayer);
    }
}
