package thaumcraft.common.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TCLegacyItemComponent(String family, String variant, int metadata) {
    public static final Codec<TCLegacyItemComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("family").forGetter(TCLegacyItemComponent::family),
            Codec.STRING.fieldOf("variant").forGetter(TCLegacyItemComponent::variant),
            Codec.INT.fieldOf("metadata").forGetter(TCLegacyItemComponent::metadata)
    ).apply(instance, TCLegacyItemComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TCLegacyItemComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            TCLegacyItemComponent::family,
            ByteBufCodecs.STRING_UTF8,
            TCLegacyItemComponent::variant,
            ByteBufCodecs.VAR_INT,
            TCLegacyItemComponent::metadata,
            TCLegacyItemComponent::new
    );

    public TCLegacyItemComponent {
        family = family == null ? "" : family.trim().toLowerCase(java.util.Locale.ROOT);
        variant = variant == null ? "" : variant.trim().toLowerCase(java.util.Locale.ROOT);
        metadata = Math.max(0, metadata);
    }

    public boolean isEmpty() {
        return family.isBlank() || variant.isBlank();
    }
}
