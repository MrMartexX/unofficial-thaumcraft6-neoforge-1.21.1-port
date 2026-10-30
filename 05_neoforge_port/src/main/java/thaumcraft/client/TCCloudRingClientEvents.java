package thaumcraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.curios.TCBaubleEvents;
import thaumcraft.common.items.curios.TCCloudRingJumpPayload;
import thaumcraft.common.lib.fx.TCFXDispatcher;

@EventBusSubscriber(modid = Thaumcraft.MODID, value = Dist.CLIENT)
public final class TCCloudRingClientEvents {
    private static boolean jumpKeyLatched;

    private TCCloudRingClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            jumpKeyLatched = false;
            return;
        }

        boolean jumpPressed = minecraft.options.keyJump.isDown();
        if (!jumpPressed) {
            jumpKeyLatched = false;
            return;
        }
        if (jumpKeyLatched) {
            return;
        }
        jumpKeyLatched = true;

        Player player = minecraft.player;
        if (!TCBaubleEvents.canAttemptCloudRingJump(player) || !TCBaubleEvents.hasCloudRing(player)) {
            return;
        }

        PacketDistributor.sendToServer(new TCCloudRingJumpPayload());
        TCBaubleEvents.applyCloudRingImpulse(player);
        TCFXDispatcher.drawCrucibleBamf(player.level(), player.getX(), player.getY() + 0.5D, player.getZ());
        player.level().playLocalSound(
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS,
                0.1F,
                1.0F + (float) player.level().random.nextGaussian() * 0.05F,
                false
        );
    }
}
