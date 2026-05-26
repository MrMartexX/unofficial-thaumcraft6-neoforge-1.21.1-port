package thaumcraft.common.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Modern stack payload for legacy aspect-bearing item stacks.
 *
 * <p>Legacy TC6 encoded crystal essence and filled phials as one item id plus NBT aspect data. In the NeoForge port we
 * preserve that semantic with an explicit DataComponent payload so gameplay code can reason about the aspect and amount
 * without parsing legacy NBT strings.</p>
 */
public record TCAspectStackComponent(String aspect, int amount) {
    public static final Codec<TCAspectStackComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("aspect").forGetter(TCAspectStackComponent::aspect),
            Codec.INT.fieldOf("amount").forGetter(TCAspectStackComponent::amount)
    ).apply(instance, TCAspectStackComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TCAspectStackComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            TCAspectStackComponent::aspect,
            ByteBufCodecs.VAR_INT,
            TCAspectStackComponent::amount,
            TCAspectStackComponent::new
    );

    public TCAspectStackComponent {
        aspect = aspect == null ? "" : aspect.trim().toLowerCase(java.util.Locale.ROOT);
        amount = Math.max(0, amount);
    }

    public boolean isEmpty() {
        return aspect.isBlank() || amount <= 0;
    }
}
