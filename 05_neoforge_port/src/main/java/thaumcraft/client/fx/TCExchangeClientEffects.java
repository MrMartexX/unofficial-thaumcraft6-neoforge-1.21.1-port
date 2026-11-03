package thaumcraft.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import thaumcraft.client.fx.legacy.TCLegacyParticleEngine;
import thaumcraft.common.items.casters.TCExchangeBamf;
import thaumcraft.common.items.casters.TCExchangeFXPayload;
import thaumcraft.common.registry.TCSounds;

public final class TCExchangeClientEffects {
    private TCExchangeClientEffects() {}
    public static void accept(TCExchangeFXPayload payload) {
        var level = Minecraft.getInstance().level;
        if (level == null || !level.dimension().equals(payload.dimension())) return;
        var burst = TCExchangeBamf.generate(level.random);
        double x = payload.pos().getX() + .5, y = payload.pos().getY() + .5, z = payload.pos().getZ() + .5;
        level.playLocalSound(x, y, z, TCSounds.POOF.get(), SoundSource.BLOCKS, .4F, burst.soundPitch(), false);
        for (var particle : burst.particles()) TCLegacyParticleEngine.addConfiguredLegacyEffect(level, particle.data(),
                x + particle.x(), y + particle.y(), z + particle.z(), particle.vx(), particle.vy(), particle.vz(),
                particle.rotationStart(), particle.rotationDegrees(), particle.yaw(), particle.pitch());
        if (Boolean.getBoolean("tc.golemVisualAudit")) com.mojang.logging.LogUtils.getLogger()
                .info("TC_EXCHANGE_PACKET_CLIENT_PASS particles={} dimension=true", burst.particles().size());
    }
}
