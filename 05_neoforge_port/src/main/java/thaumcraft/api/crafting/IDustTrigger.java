package thaumcraft.api.crafting;

import net.minecraft.world.item.context.UseOnContext;

/**
 * Extension point for Salis Mundus transformations.
 *
 * <p>The callback is queried on both logical sides. Implementations must only mutate the world
 * when {@code context.getLevel().isClientSide()} is false. A matched but research-blocked trigger
 * must return {@link DustTriggerResult#blocked(String)} so a lower-priority trigger cannot consume
 * the same click.</p>
 */
@FunctionalInterface
public interface IDustTrigger {
    DustTriggerResult tryActivate(UseOnContext context);

    record DustTriggerResult(boolean matched, boolean activated, String key, String reason) {
        public static DustTriggerResult none() {
            return new DustTriggerResult(false, false, "", "");
        }

        public static DustTriggerResult blocked(String reason) {
            return new DustTriggerResult(true, false, "", reason);
        }

        public static DustTriggerResult activated(String key) {
            return new DustTriggerResult(true, true, key, "");
        }
    }
}
