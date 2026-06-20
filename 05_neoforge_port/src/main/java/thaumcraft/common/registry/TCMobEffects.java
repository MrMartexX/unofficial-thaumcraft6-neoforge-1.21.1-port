package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.api.potions.PotionVisExhaust;

public final class TCMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Thaumcraft.MODID);

    public static final DeferredHolder<MobEffect, PotionFluxTaint> FLUX_TAINT =
            MOB_EFFECTS.register("flux_taint", PotionFluxTaint::new);
    public static final DeferredHolder<MobEffect, PotionVisExhaust> VIS_EXHAUST =
            MOB_EFFECTS.register("vis_exhaust", PotionVisExhaust::new);

    private TCMobEffects() {
    }
}
