package thaumcraft.common.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * TC6 EntitySpellBat: mystical focus-summoned bat variant of firebat.
 */
public class TCSpellbatEntity extends TCFirebatEntity {
    public TCSpellbatEntity(EntityType<? extends TCSpellbatEntity> type, Level level) {
        super(type, level);
    }
}
