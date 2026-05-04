package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class TCItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Thaumcraft.MODID);

    public static final Supplier<BlockItem> ORE_AMBER = ITEMS.register("ore_amber", () ->
            new BlockItem(TCBlocks.ORE_AMBER.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> ORE_CINNABAR = ITEMS.register("ore_cinnabar", () ->
            new BlockItem(TCBlocks.ORE_CINNABAR.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> ORE_QUARTZ = ITEMS.register("ore_quartz", () ->
            new BlockItem(TCBlocks.ORE_QUARTZ.get(), new Item.Properties())
    );

    public static final Supplier<Item> GOGGLES = ITEMS.register("goggles", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> AMBER = ITEMS.register("amber", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> QUICKSILVER = ITEMS.register("quicksilver", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> FABRIC = ITEMS.register("fabric", () -> new Item(new Item.Properties()));

    private TCItems() {
    }
}