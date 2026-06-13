package thaumcraft.api.golems;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Enumeration of all golem traits.
 * Traits define special behaviors and attributes for golems.
 * Some traits have opposites that cancel each other out.
 */
public enum EnumGolemTrait {
    SMART(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_smart.png")),
    DEFT(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_deft.png")),
    CLUMSY(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_clumsy.png")),
    FIGHTER(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_fighter.png")),
    WHEELED(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_wheeled.png")),
    FLYER(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_flyer.png")),
    CLIMBER(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_climber.png")),
    HEAVY(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_heavy.png")),
    LIGHT(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_light.png")),
    FRAGILE(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_fragile.png")),
    REPAIR(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_repair.png")),
    SCOUT(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_scout.png")),
    ARMORED(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_armored.png")),
    BRUTAL(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_brutal.png")),
    FIREPROOF(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_fireproof.png")),
    BREAKER(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_breaker.png")),
    HAULER(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_hauler.png")),
    RANGED(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_ranged.png")),
    BLASTPROOF(new ResourceLocation("thaumcraft", "textures/misc/golem/tag_blastproof.png"));

    static {
        // Set up opposing traits that cancel each other out
        CLUMSY.opposite = DEFT;
        DEFT.opposite = CLUMSY;

        HEAVY.opposite = LIGHT;
        LIGHT.opposite = HEAVY;

        FRAGILE.opposite = ARMORED;
        ARMORED.opposite = FRAGILE;
    }

    public final ResourceLocation icon;
    public EnumGolemTrait opposite;

    EnumGolemTrait(ResourceLocation icon) {
        this.icon = icon;
    }

    public Component getLocalizedName() {
        return Component.translatable("golem.trait." + name().toLowerCase());
    }

    public Component getLocalizedDescription() {
        return Component.translatable("golem.trait.text." + name().toLowerCase());
    }
}
