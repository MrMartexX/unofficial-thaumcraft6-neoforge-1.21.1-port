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

    public static final Supplier<BlockItem> CRYSTAL_AER = ITEMS.register("crystal_aer", () ->
            new BlockItem(TCBlocks.CRYSTAL_AER.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> CRYSTAL_IGNIS = ITEMS.register("crystal_ignis", () ->
            new BlockItem(TCBlocks.CRYSTAL_IGNIS.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> CRYSTAL_AQUA = ITEMS.register("crystal_aqua", () ->
            new BlockItem(TCBlocks.CRYSTAL_AQUA.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> CRYSTAL_TERRA = ITEMS.register("crystal_terra", () ->
            new BlockItem(TCBlocks.CRYSTAL_TERRA.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> CRYSTAL_ORDO = ITEMS.register("crystal_ordo", () ->
            new BlockItem(TCBlocks.CRYSTAL_ORDO.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> CRYSTAL_PERDITIO = ITEMS.register("crystal_perditio", () ->
            new BlockItem(TCBlocks.CRYSTAL_PERDITIO.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> CRYSTAL_VITIUM = ITEMS.register("crystal_vitium", () ->
            new BlockItem(TCBlocks.CRYSTAL_VITIUM.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> STONE_ARCANE = ITEMS.register("stone_arcane", () ->
            new BlockItem(TCBlocks.STONE_ARCANE.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> STONE_ARCANE_BRICK = ITEMS.register("stone_arcane_brick", () ->
            new BlockItem(TCBlocks.STONE_ARCANE_BRICK.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> STONE_ANCIENT = ITEMS.register("stone_ancient", () ->
            new BlockItem(TCBlocks.STONE_ANCIENT.get(), new Item.Properties())
    );

    public static final Supplier<Item> GOGGLES = ITEMS.register("goggles", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> AMBER = ITEMS.register("amber", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> QUICKSILVER = ITEMS.register("quicksilver", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> FABRIC = ITEMS.register("fabric", () -> new Item(new Item.Properties()));

    private TCItems() {
    }
}