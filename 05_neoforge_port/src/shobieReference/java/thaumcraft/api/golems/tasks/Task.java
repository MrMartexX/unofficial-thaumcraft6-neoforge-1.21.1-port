package thaumcraft.api.golems.tasks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.ProvisionRequest;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;

import java.util.UUID;

/**
 * Represents a task for a golem to perform.
 * Tasks can target either a block position or an entity.
 */
public class Task {

    public static final byte TYPE_BLOCK = 0;
    public static final byte TYPE_ENTITY = 1;

    private UUID golemUUID;
    private int id;
    private byte type;
    private SealPos sealPos;
    private BlockPos pos;
    private Entity entity;
    private boolean reserved;
    private boolean suspended;
    private boolean completed;
    private int data;
    private ProvisionRequest linkedProvision;
    
    /**
     * Lifespan in seconds. Default 300 seconds (5 minutes)
     */
    private short lifespan;
    private byte priority = 0;

    private Task() {}

    /**
     * Create a task targeting a block position
     */
    public Task(SealPos sealPos, BlockPos pos) {
        this.sealPos = sealPos;
        this.pos = pos;
        if (sealPos == null) {
            id = (System.currentTimeMillis() + "/BNPOS/" + pos.toString()).hashCode();
        } else {
            id = (System.currentTimeMillis() + "/B/" + sealPos.face.toString() + "/" + 
                  sealPos.pos.toString() + "/" + pos.toString()).hashCode();
        }
        type = TYPE_BLOCK;
        lifespan = 300;
    }

    /**
     * Create a task targeting an entity
     */
    public Task(SealPos sealPos, Entity entity) {
        this.sealPos = sealPos;
        this.entity = entity;
        if (sealPos == null) {
            id = (System.currentTimeMillis() + "/ENPOS/" + entity.getId()).hashCode();
        } else {
            id = (System.currentTimeMillis() + "/E/" + sealPos.face.toString() + "/" + 
                  sealPos.pos.toString() + "/" + entity.getId()).hashCode();
        }
        type = TYPE_ENTITY;
        lifespan = 300;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompletion(boolean fulfilled) {
        completed = fulfilled;
        lifespan += 1;
    }

    public UUID getGolemUUID() {
        return golemUUID;
    }

    public void setGolemUUID(UUID golemUUID) {
        this.golemUUID = golemUUID;
    }

    public BlockPos getPos() {
        return type == TYPE_ENTITY ? entity.blockPosition() : pos;
    }

    public byte getType() {
        return type;
    }

    public Entity getEntity() {
        return entity;
    }

    public int getId() {
        return id;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
        lifespan += 120;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        setLinkedProvision(null);
        this.suspended = suspended;
    }

    public SealPos getSealPos() {
        return sealPos;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task t)) {
            return false;
        }
        return t.id == id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public long getLifespan() {
        return lifespan;
    }

    public void setLifespan(short lifespan) {
        this.lifespan = lifespan;
    }

    /**
     * Check if a golem can perform this task
     */
    public boolean canGolemPerformTask(IGolemAPI golem) {
        ISealEntity se = GolemHelper.getSealEntity(golem.getGolemWorld().dimension(), sealPos);
        if (se != null) {
            // Color matching
            if (golem.getGolemColor() > 0 && se.getColor() > 0 && golem.getGolemColor() != se.getColor()) {
                return false;
            }
            return se.getSeal().canGolemPerformTask(golem, this);
        }
        return true;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public ProvisionRequest getLinkedProvision() {
        return linkedProvision;
    }

    public void setLinkedProvision(ProvisionRequest linkedProvision) {
        this.linkedProvision = linkedProvision;
    }
}
