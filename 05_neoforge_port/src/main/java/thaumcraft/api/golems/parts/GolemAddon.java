package thaumcraft.api.golems.parts;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.golems.EnumGolemTrait;

public class GolemAddon {
    protected static GolemAddon[] addons = new GolemAddon[1];
    private static byte lastID = 0;

    public byte id;
    public final String key;
    public final String[] research;
    public final ResourceLocation icon;
    public final Object[] components;
    public final EnumGolemTrait[] traits;
    public final IAddonFunction function;
    public final PartModel model;

    public GolemAddon(String key, String[] research, ResourceLocation icon, PartModel model, Object[] comp, EnumGolemTrait[] tags) {
        this(key, research, icon, model, comp, null, tags);
    }

    public GolemAddon(String key, String[] research, ResourceLocation icon, PartModel model, Object[] comp, IAddonFunction function, EnumGolemTrait[] tags) {
        this.key = key;
        this.research = research;
        this.icon = icon;
        this.components = comp;
        this.traits = tags;
        this.model = model;
        this.function = function;
    }

    public static void register(GolemAddon thing) {
        thing.id = lastID++;
        if (thing.id >= addons.length) {
            GolemAddon[] temp = new GolemAddon[thing.id + 1];
            System.arraycopy(addons, 0, temp, 0, addons.length);
            addons = temp;
        }
        addons[thing.id] = thing;
    }

    public static void reset() {
        addons = new GolemAddon[1];
        lastID = 0;
    }

    public String getTranslationKey() {
        return "golem.addon." + key.toLowerCase();
    }

    public String getDescriptionTranslationKey() {
        return "golem.addon.text." + key.toLowerCase();
    }

    public Component getLocalizedName() {
        return Component.translatable(getTranslationKey());
    }

    public Component getLocalizedDescription() {
        return Component.translatable(getDescriptionTranslationKey());
    }

    public static GolemAddon[] getAddons() {
        return addons;
    }

    public interface IAddonFunction extends IGenericFunction {
    }
}
