package thaumcraft.common.items.armor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.common.registry.TCArmorMaterials;

public class ItemFortressArmor extends ArmorItem implements IVisDiscountGear {
    public ItemFortressArmor(Type type) {
        super(
                TCArmorMaterials.FORTRESS,
                type,
                TCArmorMaterials.armorProperties(type, 40).rarity(Rarity.RARE)
        );
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return 2;
    }
}
