package thaumcraft.api.golems.parts;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.EnumGolemTrait;

public class GolemMaterial {
    protected static GolemMaterial[] materials = new GolemMaterial[1];
    private static byte lastID = 0;

    public byte id;
    public final String key;
    public final String[] research;
    public final ResourceLocation texture;
    public final int itemColor;
    public final int healthMod;
    public final int armor;
    public final int damage;
    public final ItemStack componentBase;
    public final ItemStack componentMechanism;
    public final EnumGolemTrait[] traits;

    public GolemMaterial(String key, String[] research, ResourceLocation texture, int itemColor, int hp, int armor, int damage,
                         ItemStack compBase, ItemStack compMech, EnumGolemTrait[] tags) {
        this.key = key;
        this.research = research;
        this.texture = texture;
        this.itemColor = itemColor;
        this.componentBase = compBase;
        this.componentMechanism = compMech;
        this.healthMod = hp;
        this.armor = armor;
        this.damage = damage;
        this.traits = tags;
    }

    public static void register(GolemMaterial thing) {
        thing.id = lastID++;
        if (thing.id >= materials.length) {
            GolemMaterial[] temp = new GolemMaterial[thing.id + 1];
            System.arraycopy(materials, 0, temp, 0, materials.length);
            materials = temp;
        }
        materials[thing.id] = thing;
    }

    public static void reset() {
        materials = new GolemMaterial[1];
        lastID = 0;
    }

    public String getTranslationKey() {
        return "golem.material." + key.toLowerCase();
    }

    public String getDescriptionTranslationKey() {
        return "golem.material.text." + key.toLowerCase();
    }

    public Component getLocalizedName() {
        return Component.translatable(getTranslationKey());
    }

    public Component getLocalizedDescription() {
        return Component.translatable(getDescriptionTranslationKey());
    }

    public static GolemMaterial[] getMaterials() {
        return materials;
    }
}
