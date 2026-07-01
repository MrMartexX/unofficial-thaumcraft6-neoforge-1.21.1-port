package thaumcraft.common.items.armor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.common.registry.TCArmorMaterials;

public class ItemRobeArmor extends ArmorItem implements IVisDiscountGear {
    public ItemRobeArmor(Type type) {
        super(
                TCArmorMaterials.THAUMATURGE_ROBE,
                type,
                TCArmorMaterials.armorProperties(type, 25).rarity(Rarity.UNCOMMON)
        );
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return getType() == Type.BOOTS ? 2 : 3;
    }
}
