package thaumcraft.common.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TCCasterFocusComponent(String focusItem, String customName, TCFocusPackageComponent packageData) {
    public static final TCCasterFocusComponent EMPTY = new TCCasterFocusComponent("", "", TCFocusPackageComponent.EMPTY);

    public static final Codec<TCCasterFocusComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("focus_item").forGetter(TCCasterFocusComponent::focusItem),
            Codec.STRING.optionalFieldOf("custom_name", "").forGetter(TCCasterFocusComponent::customName),
            TCFocusPackageComponent.CODEC.fieldOf("package").forGetter(TCCasterFocusComponent::packageData)
    ).apply(instance, TCCasterFocusComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TCCasterFocusComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            TCCasterFocusComponent::focusItem,
            ByteBufCodecs.STRING_UTF8,
            TCCasterFocusComponent::customName,
            TCFocusPackageComponent.STREAM_CODEC,
            TCCasterFocusComponent::packageData,
            TCCasterFocusComponent::new
    );

    public TCCasterFocusComponent {
        focusItem = focusItem == null ? "" : focusItem.trim();
        customName = customName == null ? "" : customName.trim();
        packageData = packageData == null ? TCFocusPackageComponent.EMPTY : packageData;
    }

    public boolean isEmpty() {
        return focusItem.isBlank();
    }
}
