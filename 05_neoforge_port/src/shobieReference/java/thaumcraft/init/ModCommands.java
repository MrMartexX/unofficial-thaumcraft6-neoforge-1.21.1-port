package thaumcraft.init;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.CommandThaumcraft;

@Mod.EventBusSubscriber(modid = Thaumcraft.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandThaumcraft.register(event.getDispatcher());
    }
}
