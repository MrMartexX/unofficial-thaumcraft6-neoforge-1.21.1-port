package thaumcraft.api.golems.parts;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.golems.EnumGolemTrait;

public class GolemHead {
    protected static GolemHead[] heads = new GolemHead[1];
    private static byte lastID = 0;

    public byte id;
    public final String key;
    public final String[] research;
    public final ResourceLocation icon;
    public final Object[] components;
    public final EnumGolemTrait[] traits;
    public final IHeadFunction function;
    public final PartModel model;

    public GolemHead(String key, String[] research, ResourceLocation icon, PartModel model, Object[] comp, EnumGolemTrait[] tags) {
        this(key, research, icon, model, comp, null, tags);
    }

    public GolemHead(String key, String[] research, ResourceLocation icon, PartModel model, Object[] comp, IHeadFunction function, EnumGolemTrait[] tags) {
        this.key = key;
        this.research = research;
        this.icon = icon;
        this.components = comp;
        this.traits = tags;
        this.model = model;
        this.function = function;
    }

    public static void register(GolemHead thing) {
        thing.id = lastID++;
        if (thing.id >= heads.length) {
            GolemHead[] temp = new GolemHead[thing.id + 1];
            System.arraycopy(heads, 0, temp, 0, heads.length);
            heads = temp;
        }
        heads[thing.id] = thing;
    }

    public static void reset() {
        heads = new GolemHead[1];
        lastID = 0;
    }

    public String getTranslationKey() {
        return "golem.head." + key.toLowerCase();
    }

    public String getDescriptionTranslationKey() {
        return "golem.head.text." + key.toLowerCase();
    }

    public Component getLocalizedName() {
        return Component.translatable(getTranslationKey());
    }

    public Component getLocalizedDescription() {
        return Component.translatable(getDescriptionTranslationKey());
    }

    public static GolemHead[] getHeads() {
        return heads;
    }

    public interface IHeadFunction extends IGenericFunction {
    }
}
