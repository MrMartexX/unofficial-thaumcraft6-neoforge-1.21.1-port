package thaumcraft.common.items.consumables;

import java.util.Collection;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.TCAspectVariantStacks;
import thaumcraft.common.lib.damage.TCDamageTypes;

public final class TCLiquidDeathEvents {
    private TCLiquidDeathEvents() {
    }

    public static void onLivingDrops(LivingDropsEvent event) {
        addDissolveCrystalDrops(event.getDrops(), event.getEntity(), event.getSource());
    }

    public static int addDissolveCrystalDrops(Collection<ItemEntity> drops, LivingEntity entity, DamageSource source) {
        if (drops == null || entity == null || source == null || !source.is(TCDamageTypes.DISSOLVE) || entity.level().isClientSide) {
            return 0;
        }

        AspectList aspects = AspectHelper.getEntityAspects(entity);
        if (aspects == null || aspects.size() == 0) {
            return 0;
        }

        Aspect[] present = aspects.getAspects();
        if (present.length == 0) {
            return 0;
        }

        int count = 1 + entity.getRandom().nextInt(1 + aspects.visSize() / 10);
        int added = 0;
        for (int index = 0; index < count; index++) {
            Aspect aspect = present[entity.getRandom().nextInt(present.length)];
            ItemStack stack = TCAspectVariantStacks.crystal(aspect);
            if (!stack.isEmpty()) {
                drops.add(new ItemEntity(
                        entity.level(),
                        entity.getX(),
                        entity.getY() + entity.getEyeHeight(),
                        entity.getZ(),
                        stack
                ));
                added++;
            }
        }
        return added;
    }
}
