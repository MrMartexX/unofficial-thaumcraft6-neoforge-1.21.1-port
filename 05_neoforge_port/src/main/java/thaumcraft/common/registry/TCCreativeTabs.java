package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class TCCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Thaumcraft.MODID);

    public static final Supplier<CreativeModeTab> THAUMCRAFT = CREATIVE_MODE_TABS.register("thaumcraft", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.thaumcraft"))
                    .icon(() -> new ItemStack(TCItems.GOGGLES.get()))
                    .displayItems((parameters, output) -> TCCreativeTabOrder.addThaumcraftItems(output))
                    .build()
    );

    private TCCreativeTabs() {
    }
}