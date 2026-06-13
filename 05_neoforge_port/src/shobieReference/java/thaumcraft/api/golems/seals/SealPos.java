package thaumcraft.api.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.Objects;

/**
 * Represents a seal's position in the world, including the block face it's attached to.
 */
public class SealPos {

    public BlockPos pos;
    public Direction face;

    public SealPos(BlockPos pos, Direction face) {
        this.pos = pos;
        this.face = face;
    }

    @Override
    public int hashCode() {
        byte faceOrdinal = (byte) (face.ordinal() + 1);
        int i = 31 * faceOrdinal + pos.getX();
        i = 31 * i + pos.getY();
        i = 31 * i + pos.getZ();
        return i;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SealPos other)) {
            return false;
        }
        return Objects.equals(pos, other.pos) && face == other.face;
    }

    @Override
    public String toString() {
        return "SealPos{pos=" + pos + ", face=" + face + "}";
    }

    /**
     * Converts this SealPos to a unique long value for storage
     */
    public long toLong() {
        return pos.asLong() ^ ((long) face.ordinal() << 60);
    }

    /**
     * Creates a SealPos from a long value
     */
    public static SealPos fromLong(long value) {
        int faceOrdinal = (int) (value >>> 60);
        long posLong = value & 0x0FFFFFFFFFFFFFFFL;
        return new SealPos(BlockPos.of(posLong), Direction.values()[faceOrdinal]);
    }
}
