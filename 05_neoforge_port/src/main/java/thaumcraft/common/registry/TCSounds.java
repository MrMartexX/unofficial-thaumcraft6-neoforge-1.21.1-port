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

    private TCSounds() {
    }
}
