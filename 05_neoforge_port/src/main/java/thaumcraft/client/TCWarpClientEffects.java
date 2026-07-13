package thaumcraft.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.warp.TCWarpClientCache;
import thaumcraft.common.warp.TCWarpMessagePayload;
import thaumcraft.common.warp.TCWarpType;

public final class TCWarpClientEffects {
    private TCWarpClientEffects() {
    }

    public static void onClientTick(Minecraft minecraft) {
        if (minecraft.level == null || minecraft.player == null) {
            TCWarpClientCache.clear();
            return;
        }

        TCWarpMessagePayload payload;
        while ((payload = TCWarpClientCache.pollMessage()) != null) {
            showMessage(minecraft, payload);
        }
    }

    private static void showMessage(Minecraft minecraft, TCWarpMessagePayload payload) {
        String key = translationKey(payload.warpType(), payload.change());
        minecraft.player.displayClientMessage(Component.translatable(key).withStyle(ChatFormatting.DARK_PURPLE), true);
        if (payload.change() > 0) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(TCSounds.WHISPERS.get(), 1.0F, 0.5F));
        }
    }

    private static String translationKey(TCWarpType type, int change) {
        return switch (type) {
            case PERMANENT -> change >= 0 ? "tc.addwarp" : "tc.removewarp";
            case NORMAL -> change >= 0 ? "tc.addwarpsticky" : "tc.removewarpsticky";
            case TEMPORARY -> change >= 0 ? "tc.addwarptemp" : "tc.removewarptemp";
        };
    }
}
