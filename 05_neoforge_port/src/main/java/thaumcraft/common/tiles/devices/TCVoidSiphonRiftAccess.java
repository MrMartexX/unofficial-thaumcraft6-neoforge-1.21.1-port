package thaumcraft.common.tiles.devices;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Adapter boundary for future Flux Rift entities consumed by the legacy Void Siphon. */
public interface TCVoidSiphonRiftAccess {
    Vec3 voidSiphonPosition();

    int voidSiphonRiftSize();

    void voidSiphonSetRiftSize(int size);

    double voidSiphonStability();

    void voidSiphonSetStability(double stability);

    boolean voidSiphonAlive();

    boolean voidSiphonCanBeSeenFrom(Level level, Vec3 source);
}
