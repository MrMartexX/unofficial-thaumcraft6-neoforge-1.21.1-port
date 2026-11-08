package thaumcraft.common.items.components;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import thaumcraft.common.items.tools.TCEnumInfusionEnchantment;

/**
 * Data component holding infusion enchantments applied to an item.
 */
public record TCInfusionEnchantmentsComponent(Map<TCEnumInfusionEnchantment, Integer> enchantments) {
    public static final TCInfusionEnchantmentsComponent EMPTY = new TCInfusionEnchantmentsComponent(Map.of());

    public static final Codec<TCInfusionEnchantmentsComponent> CODEC = Codec.unboundedMap(
            Codec.STRING.xmap(TCEnumInfusionEnchantment::valueOf, TCEnumInfusionEnchantment::name),
            Codec.INT
    ).xmap(TCInfusionEnchantmentsComponent::new, TCInfusionEnchantmentsComponent::enchantments);

    public static final StreamCodec<ByteBuf, TCInfusionEnchantmentsComponent> STREAM_CODEC =
            ByteBufCodecs.fromCodec(CODEC);

    public TCInfusionEnchantmentsComponent {
        enchantments = Map.copyOf(enchantments == null ? Map.of() : enchantments);
    }

    public int level(TCEnumInfusionEnchantment enchantment) {
        if (enchantment == null) {
            return 0;
        }
        return enchantments.getOrDefault(enchantment, 0);
    }

    public boolean has(TCEnumInfusionEnchantment enchantment) {
        return level(enchantment) > 0;
    }

    public TCInfusionEnchantmentsComponent with(TCEnumInfusionEnchantment enchantment, int level) {
        if (enchantment == null) {
            return this;
        }
        Map<TCEnumInfusionEnchantment, Integer> copy = new LinkedHashMap<>(enchantments);
        if (level <= 0) {
            copy.remove(enchantment);
        } else {
            copy.put(enchantment, level);
        }
        return new TCInfusionEnchantmentsComponent(Collections.unmodifiableMap(copy));
    }
}
