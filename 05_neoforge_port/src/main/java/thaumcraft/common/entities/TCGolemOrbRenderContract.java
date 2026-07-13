package thaumcraft.common.entities;

import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public final class TCGolemOrbRenderContract {
    public static final ResourceLocation PARTICLE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/particles.png");
    public static final int LEGACY_GRID_SIZE = 32;
    public static final int LEGACY_FRAME_COUNT = 6;
    public static final int LEGACY_FIRST_FRAME_U = 1;
    public static final int LEGACY_RED_V = 6;
    public static final int LEGACY_BLUE_V = 7;
    public static final int LEGACY_LIGHTMAP = 220;
    public static final float LEGACY_ALPHA = 0.8F;
    public static final float LEGACY_BOB_AMPLITUDE = 0.2F;
    public static final float LEGACY_BOB_OFFSET = 0.2F;

    private TCGolemOrbRenderContract() {
    }

    public static float frameU1(int tickCount) {
        return (LEGACY_FIRST_FRAME_U + Math.floorMod(tickCount, LEGACY_FRAME_COUNT)) / (float) LEGACY_GRID_SIZE;
    }

    public static float frameU2(int tickCount) {
        return frameU1(tickCount) + 1.0F / LEGACY_GRID_SIZE;
    }

    public static float frameV1(boolean red) {
        return (red ? LEGACY_RED_V : LEGACY_BLUE_V) / (float) LEGACY_GRID_SIZE;
    }

    public static float frameV2(boolean red) {
        return frameV1(red) + 1.0F / LEGACY_GRID_SIZE;
    }
}
