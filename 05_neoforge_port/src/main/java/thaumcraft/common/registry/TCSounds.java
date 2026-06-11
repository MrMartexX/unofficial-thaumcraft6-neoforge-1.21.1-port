package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class TCSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, Thaumcraft.MODID);

    public static final Supplier<SoundEvent> SCAN = SOUND_EVENTS.register("scan", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "scan")));
    public static final Supplier<SoundEvent> PAGE = SOUND_EVENTS.register("page", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "page")));
    public static final Supplier<SoundEvent> PAGETURN = SOUND_EVENTS.register("pageturn", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "pageturn")));
    public static final Supplier<SoundEvent> WRITE = SOUND_EVENTS.register("write", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "write")));

    private TCSounds() {
    }
}
