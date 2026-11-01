package thaumcraft.api.golems.tasks;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import thaumcraft.api.golems.ProvisionRequest;
import thaumcraft.api.golems.seals.SealPos;

public class Task {
    private UUID golemUUID;
    private final int id;
    private final byte type; // 0 = Block, 1 = Entity
    private final SealPos sealPos;
    private BlockPos pos;
    private Entity entity;
    private boolean reserved;
    private boolean suspended;
    private boolean completed;
    private int data;
    private ProvisionRequest linkedProvision;
    private short lifespan;
    private byte priority = 0;

    public Task(SealPos sealPos, BlockPos pos) {
        this.sealPos = sealPos;
        this.pos = pos;
        if (sealPos == null) {
            this.id = (System.currentTimeMillis() + "/BNPOS/" + pos.toString()).hashCode();
        } else {
            this.id = (System.currentTimeMillis() + "/B/" + sealPos.face.toString() + "/" + sealPos.pos.toString() + "/" + pos.toString()).hashCode();
        }
        this.type = 0;
        this.lifespan = 300;
    }

    public Task(SealPos sealPos, Entity entity) {
        this.sealPos = sealPos;
        this.entity = entity;
        if (sealPos == null) {
            this.id = (System.currentTimeMillis() + "/ENPOS/" + entity.getId()).hashCode();
        } else {
            this.id = (System.currentTimeMillis() + "/E/" + sealPos.face.toString() + "/" + sealPos.pos.toString() + "/" + entity.getId()).hashCode();
        }
        this.type = 1;
        this.lifespan = 300;
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
        this.completed = fulfilled;
        this.lifespan += 1;
    }

    public UUID getGolemUUID() {
        return golemUUID;
    }

    public void setGolemUUID(UUID golemUUID) {
        this.golemUUID = golemUUID;
    }

    public BlockPos getPos() {
        return type == 1 && entity != null ? entity.blockPosition() : pos;
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

    public void setReserved(boolean res) {
        this.reserved = res;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean sus) {
        this.suspended = sus;
    }

    public SealPos getSealPos() {
        return sealPos;
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

    public short getLifespan() {
        return lifespan;
    }

    public void setLifespan(short lifespan) {
        this.lifespan = lifespan;
    }
}
