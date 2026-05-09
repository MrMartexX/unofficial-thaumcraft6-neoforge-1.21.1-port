package thaumcraft.client.gui;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

@EventBusSubscriber(modid = Thaumcraft.MODID, value = Dist.CLIENT)
public final class TCAspectTooltipEvents {
    private TCAspectTooltipEvents() {
    }

    @SubscribeEvent
    public static void gatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.screen instanceof AbstractContainerScreen<?>)
                || minecraft.mouseHandler.isMouseGrabbed()
                || !Screen.hasShiftDown()
                || event.getItemStack().isEmpty()) {
            return;
        }

        AspectList aspects = AspectHelper.getObjectAspects(event.getItemStack());
        AspectTooltipComponent component = new AspectTooltipComponent(aspects);
        if (!component.isEmpty()) {
            event.getTooltipElements().add(Either.right(component));
        }
    }
}
