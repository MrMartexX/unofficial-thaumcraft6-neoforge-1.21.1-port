package thaumcraft.common.items.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Rarity;
import thaumcraft.common.registry.TCArmorMaterials;

/** Standard Thaumium armor with the TC6 material values and repair ingredient. */
public class ItemThaumiumArmor extends ArmorItem {
    public ItemThaumiumArmor(Type type) {
        super(
                TCArmorMaterials.THAUMIUM,
                type,
                TCArmorMaterials.armorProperties(type, 25).rarity(Rarity.UNCOMMON)
        );
    }
}
