package thaumcraft.api.research;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class ScanEntity implements IScanThing {
    private final String research;
    private final Class<? extends Entity> entityClass;
    private final EntityType<?> entityType;
    private final boolean inheritedClasses;

    public ScanEntity(String research, Class<? extends Entity> entityClass, boolean inheritedClasses) {
        this.research = research;
        this.entityClass = entityClass;
        this.entityType = null;
        this.inheritedClasses = inheritedClasses;
    }

    public ScanEntity(String research, EntityType<?> entityType) {
        this.research = research;
        this.entityClass = null;
        this.entityType = entityType;
        this.inheritedClasses = false;
    }

    @Override
    public boolean checkThing(ServerPlayer player, Object object) {
        if (!(object instanceof Entity entity)) {
            return false;
        }

        if (entityType != null) {
            return entity.getType() == entityType;
        }

        if (entityClass == null) {
            return false;
        }

        return inheritedClasses ? entityClass.isInstance(entity) : entity.getClass() == entityClass;
    }

    @Override
    public String getResearchKey(ServerPlayer player, Object object) {
        return research;
    }
}
