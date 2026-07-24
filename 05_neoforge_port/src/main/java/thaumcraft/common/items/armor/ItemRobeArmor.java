package thaumcraft.common.items.armor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.common.registry.TCArmorMaterials;

public class ItemRobeArmor extends ArmorItem implements IVisDiscountGear {
    public static final int LEGACY_DEFAULT_COLOR = 6961280;

    public ItemRobeArmor(Type type) {
        super(
                TCArmorMaterials.THAUMATURGE_ROBE,
                type,
                TCArmorMaterials.armorProperties(type, 25)
                        .rarity(Rarity.UNCOMMON)
                        .component(DataComponents.DYED_COLOR, new DyedItemColor(LEGACY_DEFAULT_COLOR, false))
        );
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return getType() == Type.BOOTS ? 2 : 3;
    }
}
