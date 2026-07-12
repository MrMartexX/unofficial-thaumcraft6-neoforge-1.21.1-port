package thaumcraft.common.lib.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import thaumcraft.Thaumcraft;

public final class TCDamageTypes {
    public static final ResourceKey<DamageType> DISSOLVE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "dissolve")
    );

    private TCDamageTypes() {
    }

    public static DamageSource dissolve(Level level) {
        return level.damageSources().source(DISSOLVE);
    }
}
