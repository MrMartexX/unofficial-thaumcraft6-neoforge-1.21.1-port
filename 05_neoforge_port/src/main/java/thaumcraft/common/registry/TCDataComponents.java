package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.components.TCAspectStackComponent;
import thaumcraft.common.items.components.TCStoredEnchantComponent;

public final class TCDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Thaumcraft.MODID);
    public static final Supplier<DataComponentType<TCAspectStackComponent>> ASPECT_STACK = DATA_COMPONENT_TYPES.register("aspect_stack", () -> DataComponentType.<TCAspectStackComponent>builder().persistent(TCAspectStackComponent.CODEC).networkSynchronized(TCAspectStackComponent.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<TCStoredEnchantComponent>> STORED_MAGIC = DATA_COMPONENT_TYPES.register("stored_magic", () -> DataComponentType.<TCStoredEnchantComponent>builder().persistent(TCStoredEnchantComponent.CODEC).networkSynchronized(TCStoredEnchantComponent.STREAM_CODEC).build());

    private TCDataComponents() {
    }
}
