package thaumcraft.common.items.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Rarity;
import thaumcraft.common.registry.TCArmorMaterials;

public class ItemCultistPlateArmor extends ArmorItem {
    public ItemCultistPlateArmor(Type type) {
        super(
                TCArmorMaterials.CULTIST_PLATE,
                type,
                TCArmorMaterials.armorProperties(type, 18).rarity(Rarity.UNCOMMON)
        );
    }
}
