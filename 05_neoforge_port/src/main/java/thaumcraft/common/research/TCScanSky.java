package thaumcraft.common.research;

import java.util.ArrayList;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import thaumcraft.api.research.IScanThing;
import thaumcraft.common.items.curios.ItemCelestialNotes;
import thaumcraft.common.registry.TCItems;

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

    @Override
    public void onSuccess(ServerPlayer player, Object object) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
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

        int worldDay = (int)(level.getDayTime() / 24000L);
        String pk = "CEL_" + worldDay + "_";

        if (inRangeYaw && inRangePitch) {
            String key = pk + (night ? ("Moon" + level.getMoonPhase()) : "Sun");
            if (TCPlayerKnowledgeStore.get(player).hasResearch(key)) {
                player.displayClientMessage(Component.translatable("tc.celestial.fail.1"), true);
                return;
            }
            if (hasScribingTools(player) && consumePaper(player)) {
                int variant = night ? (5 + level.getMoonPhase()) : 0;
                ItemStack stack = ItemCelestialNotes.createStack(variant, 1);
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
                TCResearchManager.progressResearch(player, key);
            } else {
                player.displayClientMessage(Component.translatable("tc.celestial.fail.2"), true);
            }
            cleanResearch(player, pk);
        } else if (night) {
            Direction face = player.getDirection();
            int num = Math.floorMod(face.get2DDataValue(), 4);
            String key2 = pk + "Star" + num;
            if (TCPlayerKnowledgeStore.get(player).hasResearch(key2)) {
                player.displayClientMessage(Component.translatable("tc.celestial.fail.1"), true);
                return;
            }
            if (hasScribingTools(player) && consumePaper(player)) {
                ItemStack stack2 = ItemCelestialNotes.createStack(1 + num, 1);
                if (!player.getInventory().add(stack2)) {
                    player.drop(stack2, false);
                }
                TCResearchManager.progressResearch(player, key2);
            } else {
                player.displayClientMessage(Component.translatable("tc.celestial.fail.2"), true);
            }
            cleanResearch(player, pk);
        }
    }

    private static boolean hasScribingTools(ServerPlayer player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(TCItems.SCRIBING_TOOLS.get())) {
                return true;
            }
        }
        return false;
    }

    private static boolean consumePaper(ServerPlayer player) {
        if (player.getAbilities().instabuild) {
            return true;
        }
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.PAPER)) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    private static void cleanResearch(ServerPlayer player, String pk) {
        TCPlayerKnowledgeStore.mutate(player, knowledge -> {
            for (String key : new ArrayList<>(knowledge.completedResearch())) {
                if (key.startsWith("CEL_") && !key.startsWith(pk)) {
                    knowledge.revokeResearch(key);
                }
            }
        });
    }
}
