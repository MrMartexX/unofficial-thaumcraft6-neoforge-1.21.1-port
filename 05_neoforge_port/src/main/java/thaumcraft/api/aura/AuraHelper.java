package thaumcraft.api.aura;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thaumcraft.common.world.aura.AuraHandler;

public final class AuraHelper {
    private AuraHelper() {
    }

    public static float drainVis(Level level, BlockPos pos, float amount, boolean simulate) {
        return AuraHandler.drainVis(level, pos, amount, simulate);
    }

    public static float drainFlux(Level level, BlockPos pos, float amount, boolean simulate) {
        return AuraHandler.drainFlux(level, pos, amount, simulate);
    }

    public static void addVis(Level level, BlockPos pos, float amount) {
        AuraHandler.addVis(level, pos, amount);
    }

    public static float getVis(Level level, BlockPos pos) {
        return AuraHandler.getVis(level, pos);
    }

    public static void polluteAura(Level level, BlockPos pos, float amount, boolean showEffect) {
        AuraHandler.addFlux(level, pos, amount, showEffect);
    }

    public static float getFlux(Level level, BlockPos pos) {
        return AuraHandler.getFlux(level, pos);
    }

    public static int getAuraBase(Level level, BlockPos pos) {
        return AuraHandler.getAuraBase(level, pos);
    }

    public static boolean shouldPreserveAura(Level level, Player player, BlockPos pos) {
        return AuraHandler.shouldPreserveAura(level, player, pos);
    }
}
