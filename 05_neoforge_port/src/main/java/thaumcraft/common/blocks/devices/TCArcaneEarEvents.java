package thaumcraft.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.NoteBlockEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.tiles.devices.TCArcaneEarBlockEntity;

/** Listens to world note-block play events and notifies matching in-range Arcane Ears. */
@EventBusSubscriber(modid = Thaumcraft.MODID)
public final class TCArcaneEarEvents {
    private static final double MAX_DISTANCE_SQ = 4096.0D; // 64 blocks radius, matching legacy

    private TCArcaneEarEvents() {
    }

    @SubscribeEvent
    public static void onNoteBlockPlay(NoteBlockEvent.Play event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        BlockPos notePos = event.getPos();
        int instrumentId = event.getInstrument().ordinal();
        int noteId = event.getVanillaNoteId();

        for (TCArcaneEarBlockEntity ear : TCArcaneEarBlockEntity.LOADED_EARS) {
            if (ear.getLevel() == event.getLevel() && !ear.isRemoved()) {
                if (ear.getTone() == instrumentId && ear.getNote() == noteId) {
                    if (ear.getBlockPos().distSqr(notePos) <= MAX_DISTANCE_SQ) {
                        ear.trigger();
                    }
                }
            }
        }
    }
}
