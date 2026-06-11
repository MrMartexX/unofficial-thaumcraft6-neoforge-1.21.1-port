package thaumcraft.common.research;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;

final class TCScanMobEffect implements IScanThing {
    private final Holder<MobEffect> effect;
    private final String researchKey;

    TCScanMobEffect(Holder<MobEffect> effect) {
        this.effect = effect;
        this.researchKey = "!" + effect.unwrapKey()
                .map(key -> key.location().toString())
                .orElseGet(() -> effect.value().getDescriptionId());
    }

    @Override
    public boolean checkThing(ServerPlayer player, Object object) {
        if (object instanceof LivingEntity living) {
            for (MobEffectInstance activeEffect : living.getActiveEffects()) {
                if (activeEffect.getEffect().equals(effect)) {
                    return true;
                }
            }
        }

        ItemStack stack = ScanningManager.getItemFromParams(player, object);
        if (stack.isEmpty()) {
            return false;
        }

        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null) {
            return false;
        }

        for (MobEffectInstance potionEffect : contents.getAllEffects()) {
            if (potionEffect.getEffect().equals(effect)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getResearchKey(ServerPlayer player, Object object) {
        return researchKey;
    }
}
