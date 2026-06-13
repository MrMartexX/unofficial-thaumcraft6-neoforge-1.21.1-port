package thaumcraft.common.items.armor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import thaumcraft.api.ThaumcraftMaterials;

import javax.annotation.Nullable;

/**
 * Crimson Cult Plate Armor - Heavy armor worn by Crimson Cult knights.
 */
public class ItemCultistPlateArmor extends ArmorItem {
    
    public ItemCultistPlateArmor(Type type) {
        super(ThaumcraftMaterials.ARMORMAT_CULTIST_PLATE, type, 
                new Item.Properties()
                        .stacksTo(1)
                        .rarity(Rarity.UNCOMMON));
    }
    
    // Factory methods
    public static ItemCultistPlateArmor createHelmet() {
        return new ItemCultistPlateArmor(Type.HELMET);
    }
    
    public static ItemCultistPlateArmor createChestplate() {
        return new ItemCultistPlateArmor(Type.CHESTPLATE);
    }
    
    public static ItemCultistPlateArmor createLeggings() {
        return new ItemCultistPlateArmor(Type.LEGGINGS);
    }
    
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(Items.IRON_INGOT) || super.isValidRepairItem(toRepair, repair);
    }
    
    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        // TODO: Add check for EntityInhabitedZombie when entities are implemented
        return "thaumcraft:textures/entity/armor/cultist_plate_armor.png";
    }
}
