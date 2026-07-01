package thaumcraft.common.items.armor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.common.registry.TCArmorMaterials;

public class ItemGoggles extends ArmorItem implements IVisDiscountGear, IRevealer, IGoggles {
    public ItemGoggles() {
        super(TCArmorMaterials.GOGGLES, Type.HELMET, new Properties().durability(350).rarity(Rarity.RARE));
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return 5;
    }

    @Override
    public boolean showNodes(ItemStack stack, LivingEntity wearer) {
        return true;
    }

    @Override
    public boolean showIngamePopups(ItemStack stack, LivingEntity wearer) {
        return true;
    }
}
