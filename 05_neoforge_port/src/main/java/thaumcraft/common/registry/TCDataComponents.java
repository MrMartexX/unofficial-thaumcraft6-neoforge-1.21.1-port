package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.components.TCAspectStackComponent;
import thaumcraft.common.items.components.TCCasterFocusComponent;
import thaumcraft.common.items.components.TCFocusPackageComponent;
import thaumcraft.common.items.components.TCLegacyItemComponent;
import thaumcraft.common.items.components.TCMirrorLinkComponent;
import thaumcraft.common.items.components.TCStoredEnchantComponent;

public final class TCDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Thaumcraft.MODID);
    public static final Supplier<DataComponentType<TCAspectStackComponent>> ASPECT_STACK = DATA_COMPONENT_TYPES.register("aspect_stack", () -> DataComponentType.<TCAspectStackComponent>builder().persistent(TCAspectStackComponent.CODEC).networkSynchronized(TCAspectStackComponent.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<String>> ASPECT_FILTER = DATA_COMPONENT_TYPES.register("aspect_filter", () -> DataComponentType.<String>builder().persistent(com.mojang.serialization.Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());
    public static final Supplier<DataComponentType<TCStoredEnchantComponent>> STORED_MAGIC = DATA_COMPONENT_TYPES.register("stored_magic", () -> DataComponentType.<TCStoredEnchantComponent>builder().persistent(TCStoredEnchantComponent.CODEC).networkSynchronized(TCStoredEnchantComponent.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<TCLegacyItemComponent>> LEGACY_ITEM = DATA_COMPONENT_TYPES.register("legacy_item", () -> DataComponentType.<TCLegacyItemComponent>builder().persistent(TCLegacyItemComponent.CODEC).networkSynchronized(TCLegacyItemComponent.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<TCFocusPackageComponent>> FOCUS_PACKAGE = DATA_COMPONENT_TYPES.register("focus_package", () -> DataComponentType.<TCFocusPackageComponent>builder().persistent(TCFocusPackageComponent.CODEC).networkSynchronized(TCFocusPackageComponent.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<TCCasterFocusComponent>> CASTER_FOCUS = DATA_COMPONENT_TYPES.register("caster_focus", () -> DataComponentType.<TCCasterFocusComponent>builder().persistent(TCCasterFocusComponent.CODEC).networkSynchronized(TCCasterFocusComponent.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<TCMirrorLinkComponent>> MIRROR_LINK = DATA_COMPONENT_TYPES.register("mirror_link", () -> DataComponentType.<TCMirrorLinkComponent>builder().persistent(TCMirrorLinkComponent.CODEC).networkSynchronized(TCMirrorLinkComponent.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<Integer>> BRAIN_JAR_XP = DATA_COMPONENT_TYPES.register("brain_jar_xp", () -> DataComponentType.<Integer>builder().persistent(com.mojang.serialization.Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final Supplier<DataComponentType<Integer>> CHARGE = DATA_COMPONENT_TYPES.register("charge", () -> DataComponentType.<Integer>builder().persistent(com.mojang.serialization.Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final Supplier<DataComponentType<Integer>> ACTIVE_ENERGY = DATA_COMPONENT_TYPES.register("active_energy", () -> DataComponentType.<Integer>builder().persistent(com.mojang.serialization.Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    private TCDataComponents() {
    }
}
