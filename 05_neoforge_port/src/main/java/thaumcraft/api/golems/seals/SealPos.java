package thaumcraft.api.golems.seals;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class SealPos {
    public final BlockPos pos;
    public final Direction face;

    public SealPos(BlockPos pos, Direction face) {
        this.pos = pos;
        this.face = face;
    }

    @Override
    public int hashCode() {
        int result = 31 * (face.ordinal() + 1) + pos.getX();
        result = 31 * result + pos.getY();
        return 31 * result + pos.getZ();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SealPos other)) return false;
        return Objects.equals(this.pos, other.pos) && this.face == other.face;
    }

    @Override
    public String toString() {
        return "SealPos[" + pos.toShortString() + ", face=" + face + "]";
    }
}
