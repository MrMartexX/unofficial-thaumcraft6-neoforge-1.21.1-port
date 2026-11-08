package thaumcraft.common.items.armor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import thaumcraft.Thaumcraft;
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

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/entity/armor/cultist_leader_armor.png");
    }
}
