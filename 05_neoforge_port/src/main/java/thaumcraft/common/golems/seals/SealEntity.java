package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;

public class SealEntity implements ISealEntity {

    private SealPos sealPos;
    private ISeal seal;
    private byte priority = 0;
    private BlockPos area = new BlockPos(1, 1, 1);
    private boolean locked = false;
    private boolean redstoneSensitive = false;
    private String owner = "";
    private byte color = (byte) -1;

    public SealEntity() {
    }

    public SealEntity(Level level, SealPos sealPos, ISeal seal) {
        this.sealPos = sealPos;
        this.seal = seal;
    }

    @Override
    public void tickSealEntity(Level level) {
        if (seal != null && !isStoppedByRedstone(level)) {
            seal.tickSeal(level, this);
        }
    }

    @Override
    public ISeal getSeal() {
        return seal;
    }

    public void setSeal(ISeal seal) {
        this.seal = seal;
    }

    @Override
    public SealPos getSealPos() {
        return sealPos;
    }

    public void setSealPos(SealPos sealPos) {
        this.sealPos = sealPos;
    }

    @Override
    public byte getPriority() {
        return priority;
    }

    @Override
    public void setPriority(byte priority) {
        this.priority = priority;
    }

    @Override
    public BlockPos getArea() {
        return area;
    }

    @Override
    public void setArea(BlockPos area) {
        this.area = area;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public boolean isRedstoneSensitive() {
        return redstoneSensitive;
    }

    @Override
    public void setRedstoneSensitive(boolean redstone) {
        this.redstoneSensitive = redstone;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public byte getColor() {
        return color;
    }

    @Override
    public void setColor(byte color) {
        this.color = color;
    }

    @Override
    public boolean isStoppedByRedstone(Level level) {
        if (!redstoneSensitive || sealPos == null) return false;
        return level.hasNeighborSignal(sealPos.pos);
    }

    @Override
    public void syncToClient(Level level) {
        // Reserved for client sync packet when needed
    }

    @Override
    public void readNBT(CompoundTag tag) {
        if (tag.contains("pos") && tag.contains("face")) {
            long p = tag.getLong("pos");
            Direction face = Direction.from3DDataValue(tag.getByte("face"));
            this.sealPos = new SealPos(BlockPos.of(p), face);
        }
        if (tag.contains("type")) {
            String type = tag.getString("type");
            ISeal prototype = SealHandler.getSeal(type);
            if (prototype != null) {
                try {
                    this.seal = prototype.getClass().getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException exception) {
                    throw new IllegalStateException("Cannot restore seal " + type, exception);
                }
            }
        }
        this.priority = tag.getByte("priority");
        if (tag.contains("area")) {
            this.area = BlockPos.of(tag.getLong("area"));
        }
        this.locked = tag.getBoolean("locked");
        this.redstoneSensitive = tag.getBoolean("redstone");
        this.owner = tag.getString("owner");
        this.color = tag.getByte("color");
        if (seal != null) {
            seal.readCustomNBT(tag);
        }
    }

    @Override
    public CompoundTag writeNBT() {
        CompoundTag tag = new CompoundTag();
        if (sealPos != null) {
            tag.putLong("pos", sealPos.pos.asLong());
            tag.putByte("face", (byte) sealPos.face.get3DDataValue());
        }
        if (seal != null) {
            tag.putString("type", seal.getKey());
            seal.writeCustomNBT(tag);
        }
        tag.putByte("priority", priority);
        if (area != null) {
            tag.putLong("area", area.asLong());
        }
        tag.putBoolean("locked", locked);
        tag.putBoolean("redstone", redstoneSensitive);
        tag.putString("owner", owner != null ? owner : "");
        tag.putByte("color", color);
        return tag;
    }
}
