package thaumcraft.common.registry;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class TCArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, Thaumcraft.MODID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> GOGGLES = ARMOR_MATERIALS.register(
            "goggles",
            () -> material(
                    25,
                    defense(1, 2, 3, 1),
                    25,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(TCItems.THAUMIUM_INGOT.get()),
                    List.of(layer("goggles")),
                    1.0F,
                    0.0F
            )
    );

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> THAUMATURGE_ROBE = ARMOR_MATERIALS.register(
            "thaumaturge_robe",
            () -> material(
                    25,
                    defense(1, 2, 3, 1),
                    25,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(TCItems.FABRIC.get()),
                    List.of(dyeableLayer("robes"), overlayLayer("robes")),
                    1.0F,
                    0.0F
            )
    );

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> TRAVELLER = ARMOR_MATERIALS.register(
            "traveller",
            () -> material(
                    25,
                    defense(1, 2, 3, 1),
                    25,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(net.minecraft.world.item.Items.LEATHER),
                    List.of(layer("bootstraveler")),
                    1.0F,
                    0.0F
            )
    );

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> THAUMIUM = ARMOR_MATERIALS.register(
            "thaumium",
            () -> material(
                    25,
                    defense(2, 5, 6, 2),
                    25,
                    SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(TCItems.THAUMIUM_INGOT.get()),
                    List.of(layer("thaumium")),
                    1.0F,
                    0.0F
            )
    );

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VOID = ARMOR_MATERIALS.register(
            "void",
            () -> material(
                    10,
                    defense(3, 6, 8, 3),
                    10,
                    SoundEvents.ARMOR_EQUIP_CHAIN,
                    () -> Ingredient.of(TCItems.VOID_INGOT.get()),
                    List.of(layer("void")),
                    1.0F,
                    0.0F
            )
    );

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VOID_ROBE = ARMOR_MATERIALS.register(
            "void_robe",
            () -> material(
                    18,
                    defense(4, 7, 9, 4),
                    10,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(TCItems.VOID_INGOT.get()),
                    List.of(dyeableOverlayLayer("void_robe_armor"), layer("void_robe_armor")),
                    2.0F,
                    0.0F
            )
    );

    private TCArmorMaterials() {
    }

    private static ArmorMaterial material(
            int durabilityFactor,
            Map<ArmorItem.Type, Integer> defense,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            Supplier<Ingredient> repairIngredient,
            List<ArmorMaterial.Layer> layers,
            float toughness,
            float knockbackResistance
    ) {
        return new ArmorMaterial(defense, enchantmentValue, equipSound, repairIngredient, layers, toughness, knockbackResistance);
    }

    private static Map<ArmorItem.Type, Integer> defense(int boots, int leggings, int chestplate, int helmet) {
        EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.BOOTS, boots);
        defense.put(ArmorItem.Type.LEGGINGS, leggings);
        defense.put(ArmorItem.Type.CHESTPLATE, chestplate);
        defense.put(ArmorItem.Type.HELMET, helmet);
        return defense;
    }

    private static ArmorMaterial.Layer layer(String path) {
        return new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path));
    }

    private static ArmorMaterial.Layer dyeableLayer(String path) {
        return new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path), "", true);
    }

    private static ArmorMaterial.Layer overlayLayer(String path) {
        return new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path), "_overlay", false);
    }

    private static ArmorMaterial.Layer dyeableOverlayLayer(String path) {
        return new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path), "_overlay", true);
    }

    public static Item.Properties armorProperties(ArmorItem.Type type, int durabilityFactor) {
        return new Item.Properties().durability(type.getDurability(durabilityFactor));
    }
}
