package thaumcraft.common.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TCFocusPackageComponent(String nodes, int complexity, int color, int sortingHash) {
    public static final TCFocusPackageComponent EMPTY = new TCFocusPackageComponent("", 0, 0xFFFFFF, 0);

    public static final Codec<TCFocusPackageComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("nodes").forGetter(TCFocusPackageComponent::nodes),
            Codec.INT.fieldOf("complexity").forGetter(TCFocusPackageComponent::complexity),
            Codec.INT.fieldOf("color").forGetter(TCFocusPackageComponent::color),
            Codec.INT.fieldOf("sorting_hash").forGetter(TCFocusPackageComponent::sortingHash)
    ).apply(instance, TCFocusPackageComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TCFocusPackageComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            TCFocusPackageComponent::nodes,
            ByteBufCodecs.VAR_INT,
            TCFocusPackageComponent::complexity,
            ByteBufCodecs.VAR_INT,
            TCFocusPackageComponent::color,
            ByteBufCodecs.VAR_INT,
            TCFocusPackageComponent::sortingHash,
            TCFocusPackageComponent::new
    );

    public TCFocusPackageComponent {
        nodes = nodes == null ? "" : nodes.trim();
        complexity = Math.max(0, complexity);
        color &= 0xFFFFFF;
    }

    public boolean isEmpty() {
        return nodes.isBlank();
    }
}
