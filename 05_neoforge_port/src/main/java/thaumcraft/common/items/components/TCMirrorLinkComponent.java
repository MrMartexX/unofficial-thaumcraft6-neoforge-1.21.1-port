package thaumcraft.common.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/** Persistent mirror-link payload replacing legacy ItemStack NBT linkX/linkY/linkZ/linkDim. */
public record TCMirrorLinkComponent(String dimension, int x, int y, int z) {
    public static final Codec<TCMirrorLinkComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("dimension").forGetter(TCMirrorLinkComponent::dimension),
            Codec.INT.fieldOf("x").forGetter(TCMirrorLinkComponent::x),
            Codec.INT.fieldOf("y").forGetter(TCMirrorLinkComponent::y),
            Codec.INT.fieldOf("z").forGetter(TCMirrorLinkComponent::z)
    ).apply(instance, TCMirrorLinkComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TCMirrorLinkComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            TCMirrorLinkComponent::dimension,
            ByteBufCodecs.VAR_INT,
            TCMirrorLinkComponent::x,
            ByteBufCodecs.VAR_INT,
            TCMirrorLinkComponent::y,
            ByteBufCodecs.VAR_INT,
            TCMirrorLinkComponent::z,
            TCMirrorLinkComponent::new
    );

    public TCMirrorLinkComponent {
        dimension = dimension == null || dimension.isBlank() ? Level.OVERWORLD.location().toString() : dimension.trim();
    }

    public static TCMirrorLinkComponent of(Level level, BlockPos pos) {
        return new TCMirrorLinkComponent(level.dimension().location().toString(), pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos pos() {
        return new BlockPos(x, y, z);
    }

    public ResourceKey<Level> dimensionKey() {
        ResourceLocation id = ResourceLocation.tryParse(dimension);
        if (id == null) {
            id = Level.OVERWORLD.location();
        }
        return ResourceKey.create(Registries.DIMENSION, id);
    }
}
