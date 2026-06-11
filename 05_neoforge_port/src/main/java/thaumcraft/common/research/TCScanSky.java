package thaumcraft.common.research;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import thaumcraft.api.research.IScanThing;

final class TCScanSky implements IScanThing {
    @Override
    public boolean checkThing(ServerPlayer player, Object object) {
        if (object != null || player.getXRot() > 0.0F || !(player.level() instanceof ServerLevel level)) {
            return false;
        }
        if (level.dimension() != Level.OVERWORLD || !level.canSeeSky(player.blockPosition().above())) {
            return false;
        }
        if (!TCPlayerKnowledgeStore.get(player).hasResearch("CELESTIALSCANNING")) {
            return false;
        }

        int yaw = Math.floorMod((int)(player.getYRot() + 90.0F), 360);
        int pitch = (int)Math.abs(player.getXRot());
        int celestialAngle = Math.floorMod((int)((level.getTimeOfDay(0.0F) + 0.25F) * 360.0F), 360);
        boolean night = celestialAngle > 180;

        if (night) {
            celestialAngle -= 180;
        }

        boolean inRangeYaw;
        boolean inRangePitch;
        if (celestialAngle > 90) {
            inRangeYaw = Math.abs(Math.abs(yaw) - 180) < 10;
            inRangePitch = Math.abs(180 - celestialAngle - pitch) < 7;
        } else {
            inRangeYaw = Math.abs(yaw) < 10;
            inRangePitch = Math.abs(celestialAngle - pitch) < 7;
        }

        return (inRangeYaw && inRangePitch) || night;
    }

    @Override
    public String getResearchKey(ServerPlayer player, Object object) {
        return "";
    }
}
