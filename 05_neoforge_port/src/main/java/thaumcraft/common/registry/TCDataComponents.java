package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.components.TCAspectStackComponent;

public final class TCDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Thaumcraft.MODID);

    public static final Supplier<DataComponentType<TCAspectStackComponent>> ASPECT_STACK = DATA_COMPONENT_TYPES.register(
            "aspect_stack",
            () -> DataComponentType.<TCAspectStackComponent>builder()
                    .persistent(TCAspectStackComponent.CODEC)
                    .networkSynchronized(TCAspectStackComponent.STREAM_CODEC)
                    .build()
    );

    private TCDataComponents() {
    }
}
