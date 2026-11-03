package thaumcraft.client.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import thaumcraft.common.lib.fx.TCFXDispatcher;
import thaumcraft.common.registry.TCBiomes;
import thaumcraft.common.world.TCForestMoteSampling;

/** One virtual grassAmbient anchor per loaded forest chunk; no saved grass replacement needed. */
public final class TCMagicalForestAmbientClient {
    private TCMagicalForestAmbientClient() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(TCMagicalForestAmbientClient::onClientTick);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.isPaused() || mc.level == null || mc.player == null) return;
        TCForestAmbientAudit.record(tickAround(mc.level, mc.player.blockPosition()));
    }

    public static int tickAround(ClientLevel level, BlockPos player) {
        int spawned = 0;
        for (int cx = (player.getX() - 31) >> 4; cx <= (player.getX() + 31) >> 4; cx++) {
            for (int cz = (player.getZ() - 31) >> 4; cz <= (player.getZ() + 31) >> 4; cz++) {
                RandomSource anchorRandom = RandomSource.create(ChunkPos.asLong(cx, cz) ^ 0x5443364d4f54454cL);
                int x = (cx << 4) + 4 + anchorRandom.nextInt(8);
                int z = (cz << 4) + 4 + anchorRandom.nextInt(8);
                BlockPos anchor = findGrass(level, x, z, 30);
                if (anchor == null || !level.getBiome(anchor).is(TCBiomes.MAGICAL_FOREST)) continue;
                int dx = x - player.getX(), dy = anchor.getY() - player.getY(), dz = z - player.getZ();
                int triggers = TCForestMoteSampling.triggers(level.random,
                        TCForestMoteSampling.positionProbability(dx, dy, dz, 16))
                        + TCForestMoteSampling.triggers(level.random,
                        TCForestMoteSampling.positionProbability(dx, dy, dz, 32));
                for (int n = 0; n < triggers; n++) {
                    int light = TCForestMoteSampling.adjustedLight(level.getBrightness(LightLayer.SKY, anchor.above()),
                            level.getSkyDarken(), level.getSunAngle(1.0F));
                    // Zero skylight under a thick canopy is legal in legacy, especially at night.
                    if (!TCForestMoteSampling.passesLightGate(light, level.random.nextInt(13))) continue;
                    BlockPos candidate = anchor.offset(level.random.nextInt(17) - 8, 5, level.random.nextInt(17) - 8);
                    for (int q = 0; q < 10 && candidate.getY() > 50
                            && !level.getBlockState(candidate).is(Blocks.GRASS_BLOCK); q++) candidate = candidate.below();
                    if (level.getBlockState(candidate).is(Blocks.GRASS_BLOCK) && level.hasChunkAt(candidate)
                            && level.getFluidState(candidate.above()).isEmpty()) {
                        TCFXDispatcher.drawWispyMotesOnBlock(level, candidate.above(), 400, -0.01F);
                        spawned++;
                    }
                }
            }
        }
        return spawned;
    }

    private static BlockPos findGrass(ClientLevel level, int x, int z, int floor) {
        if (!level.hasChunkAt(new BlockPos(x, 64, z))) return null;
        // Only WORLD_SURFACE and MOTION_BLOCKING are sent in chunk packets. NO_LEAVES is server-only.
        int top = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(x, top, z);
        while (cursor.getY() > Math.max(floor, level.getMinBuildHeight())) {
            if (level.getBlockState(cursor).is(Blocks.GRASS_BLOCK)) return cursor.immutable();
            cursor.move(0, -1, 0);
        }
        return null;
    }
}
