package thaumcraft.common.items.armor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import thaumcraft.Thaumcraft;
import thaumcraft.common.entities.TCInhabitedZombieEntity;
import thaumcraft.common.registry.TCArmorMaterials;

public class ItemCultistPlateArmor extends ArmorItem {
    public ItemCultistPlateArmor(Type type) {
        super(
                TCArmorMaterials.CULTIST_PLATE,
                type,
                TCArmorMaterials.armorProperties(type, 18).rarity(Rarity.UNCOMMON)
        );
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        if (entity instanceof TCInhabitedZombieEntity) {
            return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/armor/zombie_plate_armor.png");
        }
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/armor/cultist_plate_armor.png");
    }
}
