package thaumcraft.api.golems.parts;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;

public class GolemArm {
    protected static GolemArm[] arms = new GolemArm[1];
    private static byte lastID = 0;

    public byte id;
    public final String key;
    public final String[] research;
    public final ResourceLocation icon;
    public final Object[] components;
    public final EnumGolemTrait[] traits;
    public final IArmFunction function;
    public final PartModel model;

    public GolemArm(String key, String[] research, ResourceLocation icon, PartModel model, Object[] comp, EnumGolemTrait[] tags) {
        this(key, research, icon, model, comp, null, tags);
    }

    public GolemArm(String key, String[] research, ResourceLocation icon, PartModel model, Object[] comp, IArmFunction function, EnumGolemTrait[] tags) {
        this.key = key;
        this.research = research;
        this.icon = icon;
        this.components = comp;
        this.traits = tags;
        this.model = model;
        this.function = function;
    }

    public static void register(GolemArm thing) {
        thing.id = lastID++;
        if (thing.id >= arms.length) {
            GolemArm[] temp = new GolemArm[thing.id + 1];
            System.arraycopy(arms, 0, temp, 0, arms.length);
            arms = temp;
        }
        arms[thing.id] = thing;
    }

    public static void reset() {
        arms = new GolemArm[1];
        lastID = 0;
    }

    public String getTranslationKey() {
        return "golem.arm." + key.toLowerCase();
    }

    public String getDescriptionTranslationKey() {
        return "golem.arm.text." + key.toLowerCase();
    }

    public Component getLocalizedName() {
        return Component.translatable(getTranslationKey());
    }

    public Component getLocalizedDescription() {
        return Component.translatable(getDescriptionTranslationKey());
    }

    public static GolemArm[] getArms() {
        return arms;
    }

    public interface IArmFunction extends IGenericFunction {
        default void onMeleeAttack(IGolemAPI golem, Entity target) {}

        default void onRangedAttack(IGolemAPI golem, LivingEntity target, float distance) {}

        default Goal getRangedAttackGoal(RangedAttackMob mob) {
            return null;
        }
    }
}
