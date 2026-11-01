package thaumcraft.api.golems.parts;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.api.golems.EnumGolemTrait;

public class GolemLeg {
    protected static GolemLeg[] legs = new GolemLeg[1];
    private static byte lastID = 0;

    public byte id;
    public final String key;
    public final String[] research;
    public final ResourceLocation icon;
    public final Object[] components;
    public final EnumGolemTrait[] traits;
    public final ILegFunction function;
    public final PartModel model;

    public GolemLeg(String key, String[] research, ResourceLocation icon, PartModel model, Object[] comp, EnumGolemTrait[] tags) {
        this(key, research, icon, model, comp, null, tags);
    }

    public GolemLeg(String key, String[] research, ResourceLocation icon, PartModel model, Object[] comp, ILegFunction function, EnumGolemTrait[] tags) {
        this.key = key;
        this.research = research;
        this.icon = icon;
        this.components = comp;
        this.traits = tags;
        this.model = model;
        this.function = function;
    }

    public static void register(GolemLeg thing) {
        thing.id = lastID++;
        if (thing.id >= legs.length) {
            GolemLeg[] temp = new GolemLeg[thing.id + 1];
            System.arraycopy(legs, 0, temp, 0, legs.length);
            legs = temp;
        }
        legs[thing.id] = thing;
    }

    public static void reset() {
        legs = new GolemLeg[1];
        lastID = 0;
    }

    public String getTranslationKey() {
        return "golem.leg." + key.toLowerCase();
    }

    public String getDescriptionTranslationKey() {
        return "golem.leg.text." + key.toLowerCase();
    }

    public Component getLocalizedName() {
        return Component.translatable(getTranslationKey());
    }

    public Component getLocalizedDescription() {
        return Component.translatable(getDescriptionTranslationKey());
    }

    public static GolemLeg[] getLegs() {
        return legs;
    }

    public interface ILegFunction extends IGenericFunction {
    }
}
