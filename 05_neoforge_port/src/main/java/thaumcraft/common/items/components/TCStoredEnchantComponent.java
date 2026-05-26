package thaumcraft.common.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TCStoredEnchantComponent(String id, int level) {
    public static final Codec<TCStoredEnchantComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(TCStoredEnchantComponent::id),
            Codec.INT.fieldOf("level").forGetter(TCStoredEnchantComponent::level)
    ).apply(instance, TCStoredEnchantComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TCStoredEnchantComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            TCStoredEnchantComponent::id,
            ByteBufCodecs.VAR_INT,
            TCStoredEnchantComponent::level,
            TCStoredEnchantComponent::new
    );

    public TCStoredEnchantComponent {
        id = id == null ? "" : id.trim().toLowerCase(java.util.Locale.ROOT);
        level = Math.max(0, level);
    }

    public boolean isEmpty() {
        return id.isBlank() || level <= 0;
    }
}
