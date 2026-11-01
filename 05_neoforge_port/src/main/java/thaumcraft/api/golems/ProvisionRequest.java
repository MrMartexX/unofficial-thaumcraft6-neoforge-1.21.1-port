package thaumcraft.api.golems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;

public class ProvisionRequest {
    private ISealEntity seal;
    private Entity entity;
    private BlockPos pos;
    private Direction side;
    private final ItemStack stack;
    private final int id;
    private Task linkedTask;
    private boolean invalid;
    private long timeout;

    public ProvisionRequest(ISealEntity seal, ItemStack stack) {
        this.seal = seal;
        this.stack = stack.copy();
        String s = seal.getSealPos().pos.toString() + seal.getSealPos().face.name() + stack.toString();
        this.id = s.hashCode();
        this.timeout = System.currentTimeMillis() + 10000;
    }

    public ProvisionRequest(BlockPos pos, Direction side, ItemStack stack) {
        this.pos = pos;
        this.side = side;
        this.stack = stack.copy();
        String s = pos.toString() + side.name() + stack.toString();
        this.id = s.hashCode();
        this.timeout = System.currentTimeMillis() + 10000;
    }

    public ProvisionRequest(Entity entity, ItemStack stack) {
        this.entity = entity;
        this.stack = stack.copy();
        String s = entity.getId() + stack.toString();
        this.id = s.hashCode();
        this.timeout = System.currentTimeMillis() + 10000;
    }

    public int getId() {
        return id;
    }

    public ItemStack getStack() {
        return stack;
    }

    public ISealEntity getSeal() {
        return seal;
    }

    public Entity getEntity() {
        return entity;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Direction getSide() {
        return side;
    }

    public Task getLinkedTask() {
        return linkedTask;
    }

    public void setLinkedTask(Task linkedTask) {
        this.linkedTask = linkedTask;
    }

    public boolean isInvalid() {
        return invalid || System.currentTimeMillis() > timeout;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public void refreshTimeout() {
        this.timeout = System.currentTimeMillis() + 10000;
    }
}
