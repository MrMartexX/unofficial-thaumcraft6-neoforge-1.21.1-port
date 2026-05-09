package thaumcraft.common.world.aura;

import net.minecraft.world.level.ChunkPos;

public final class AuraChunk {
    public static final int BASE_CEILING = 500;
    public static final float VALUE_CEILING = 32766.0F;

    private final int chunkX;
    private final int chunkZ;
    private int base;
    private float vis;
    private float flux;

    AuraChunk(int chunkX, int chunkZ, int base, float vis, float flux) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        setBase(base);
        setVis(vis);
        setFlux(flux);
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public long getKey() {
        return ChunkPos.asLong(chunkX, chunkZ);
    }

    public ChunkPos getChunkPos() {
        return new ChunkPos(chunkX, chunkZ);
    }

    public int getBase() {
        return base;
    }

    void setBase(int base) {
        this.base = Math.min(BASE_CEILING, Math.max(0, base));
    }

    public float getVis() {
        return vis;
    }

    void setVis(float vis) {
        this.vis = clampValue(vis);
    }

    public float getFlux() {
        return flux;
    }

    void setFlux(float flux) {
        this.flux = clampValue(flux);
    }

    AuraChunk copy() {
        return new AuraChunk(chunkX, chunkZ, base, vis, flux);
    }

    private static float clampValue(float value) {
        if (Float.isNaN(value)) {
            return 0.0F;
        }
        return Math.min(VALUE_CEILING, Math.max(0.0F, value));
    }
}
