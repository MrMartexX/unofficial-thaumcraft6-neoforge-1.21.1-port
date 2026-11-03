package thaumcraft.client.renderer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = "thaumcraft", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class TCGolemModelReload {
    @SubscribeEvent
    public static void register(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((net.minecraft.server.packs.resources.ResourceManagerReloadListener) manager -> {
            TCGolemObjMesh.clear();
            int faces = 0;
            for (String model : new String[]{"base", "head_basic", "head_smart", "head_smart_armor", "head_scout", "head_scout_smart",
                    "arms_basic", "arms_fine", "arms_claws", "arms_breakers", "arms_darter", "legs_walker", "legs_wheel",
                    "legs_climber", "legs_floater", "armor", "hauler"}) {
                var mesh = TCGolemObjMesh.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_" + model + ".obj"));
                int count = mesh.groups.values().stream().mapToInt(java.util.List::size).sum();
                if (count == 0) throw new IllegalStateException("Empty golem model " + model);
                faces += count;
            }
            com.mojang.logging.LogUtils.getLogger().info("TC_GOLEM_MODELS_READY models=17 faces={}", faces);
        });
    }
}
