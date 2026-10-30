package thaumcraft.common.items.armor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.registry.TCArmorMaterials;

public class ItemCultistLeaderArmor extends ArmorItem implements IVisDiscountGear, IWarpingGear {
    public ItemCultistLeaderArmor(Type type) {
        super(
                TCArmorMaterials.CULTIST_PRAETOR,
                type,
                TCArmorMaterials.armorProperties(type, 22).rarity(Rarity.RARE)
        );
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return 1;
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return 2;
    }
}
