package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.menu.TCResearchTableMenu;

public final class TCMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Thaumcraft.MODID);

    public static final Supplier<MenuType<TCResearchTableMenu>> RESEARCH_TABLE =
            MENUS.register("research_table", () -> IMenuTypeExtension.create(TCResearchTableMenu::new));

    private TCMenus() {
    }
}
