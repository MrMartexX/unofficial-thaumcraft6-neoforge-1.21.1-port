package thaumcraft.client;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCFluids;

public final class TCClientExtensions {
    private static final ResourceLocation WATER_STILL =
            ResourceLocation.withDefaultNamespace("block/water_still");
    private static final ResourceLocation WATER_FLOW =
            ResourceLocation.withDefaultNamespace("block/water_flow");
    private static final ResourceLocation WATER_OVERLAY =
            ResourceLocation.withDefaultNamespace("block/water_overlay");
    private static final ResourceLocation ANIMATED_GLOW =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "block/animatedglow");

    private TCClientExtensions() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(TCClientExtensions::onRegisterClientExtensions);
    }

    private static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new LegacyFluidExtension(ANIMATED_GLOW, ANIMATED_GLOW, null, -263978855),
                TCFluids.LIQUID_DEATH_TYPE.get());
        event.registerFluidType(new LegacyFluidExtension(WATER_STILL, WATER_FLOW, WATER_OVERLAY, 2013252778),
                TCFluids.PURIFYING_FLUID_TYPE.get());
    }

    private record LegacyFluidExtension(
            ResourceLocation still,
            ResourceLocation flowing,
            ResourceLocation overlay,
            int tint
    ) implements IClientFluidTypeExtensions {
        @Override
        public ResourceLocation getStillTexture() {
            return still;
        }

        @Override
        public ResourceLocation getFlowingTexture() {
            return flowing;
        }

        @Override
        public ResourceLocation getOverlayTexture() {
            return overlay;
        }

        @Override
        public int getTintColor() {
            return tint;
        }
    }
}
