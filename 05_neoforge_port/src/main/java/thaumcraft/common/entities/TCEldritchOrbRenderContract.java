package thaumcraft.common.entities;

import net.minecraft.resources.ResourceLocation;
import thaumcraft.Thaumcraft;

public final class TCEldritchOrbRenderContract {
    public static final ResourceLocation PARTICLE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "textures/misc/particles.png");
    public static final int LEGACY_RANDOM_SEED = 187;
    public static final int LEGACY_TENDRIL_COUNT = 12;
    public static final int LEGACY_FRAME_COUNT = 13;
    public static final int LEGACY_GRID_SIZE = 64;
    public static final int LEGACY_FRAME_V = 3;
    public static final float LEGACY_BILLBOARD_SCALE = 0.75F;

    private TCEldritchOrbRenderContract() {
    }

    public static float frameU1(int tickCount) {
        return (tickCount % LEGACY_FRAME_COUNT) / (float) LEGACY_GRID_SIZE;
    }

    public static float frameU2(int tickCount) {
        return frameU1(tickCount) + 1.0F / LEGACY_GRID_SIZE;
    }

    public static float frameV1() {
        return LEGACY_FRAME_V / (float) LEGACY_GRID_SIZE;
    }

    public static float frameV2() {
        return frameV1() + 1.0F / LEGACY_GRID_SIZE;
    }
}
