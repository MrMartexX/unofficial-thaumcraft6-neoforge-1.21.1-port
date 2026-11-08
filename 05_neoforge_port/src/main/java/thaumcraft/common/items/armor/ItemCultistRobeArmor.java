package thaumcraft.common.items.armor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.registry.TCArmorMaterials;

public class ItemCultistRobeArmor extends ArmorItem implements IVisDiscountGear, IWarpingGear {
    public ItemCultistRobeArmor(Type type) {
        super(
                TCArmorMaterials.CULTIST_ROBE,
                type,
                TCArmorMaterials.armorProperties(type, 15).rarity(Rarity.UNCOMMON)
        );
    }

    @Override
    public int getVisDiscount(ItemStack stack, Player player) {
        return 1;
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return 1;
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/armor/cultist_robe_armor.png");
    }
}
