package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public final class TCItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Thaumcraft.MODID);

    public static final Supplier<BlockItem> ORE_AMBER = blockItem("ore_amber", TCBlocks.ORE_AMBER);
    public static final Supplier<BlockItem> ORE_CINNABAR = blockItem("ore_cinnabar", TCBlocks.ORE_CINNABAR);
    public static final Supplier<BlockItem> ORE_QUARTZ = blockItem("ore_quartz", TCBlocks.ORE_QUARTZ);

    public static final Supplier<BlockItem> CRYSTAL_AER = blockItem("crystal_aer", TCBlocks.CRYSTAL_AER);
    public static final Supplier<BlockItem> CRYSTAL_IGNIS = blockItem("crystal_ignis", TCBlocks.CRYSTAL_IGNIS);
    public static final Supplier<BlockItem> CRYSTAL_AQUA = blockItem("crystal_aqua", TCBlocks.CRYSTAL_AQUA);
    public static final Supplier<BlockItem> CRYSTAL_TERRA = blockItem("crystal_terra", TCBlocks.CRYSTAL_TERRA);
    public static final Supplier<BlockItem> CRYSTAL_ORDO = blockItem("crystal_ordo", TCBlocks.CRYSTAL_ORDO);
    public static final Supplier<BlockItem> CRYSTAL_PERDITIO = blockItem("crystal_perditio", TCBlocks.CRYSTAL_PERDITIO);
    public static final Supplier<BlockItem> CRYSTAL_VITIUM = blockItem("crystal_vitium", TCBlocks.CRYSTAL_VITIUM);

    public static final Supplier<BlockItem> STONE_ARCANE = blockItem("stone_arcane", TCBlocks.STONE_ARCANE);
    public static final Supplier<BlockItem> STONE_ARCANE_BRICK = blockItem("stone_arcane_brick", TCBlocks.STONE_ARCANE_BRICK);
    public static final Supplier<BlockItem> STONE_ANCIENT = blockItem("stone_ancient", TCBlocks.STONE_ANCIENT);
    public static final Supplier<BlockItem> STONE_ANCIENT_TILE = blockItem("stone_ancient_tile", TCBlocks.STONE_ANCIENT_TILE);
    public static final Supplier<BlockItem> STONE_ANCIENT_ROCK = blockItem("stone_ancient_rock", TCBlocks.STONE_ANCIENT_ROCK);
    public static final Supplier<BlockItem> STONE_ANCIENT_GLYPHED = blockItem("stone_ancient_glyphed", TCBlocks.STONE_ANCIENT_GLYPHED);
    public static final Supplier<BlockItem> STONE_ANCIENT_DOORWAY = blockItem("stone_ancient_doorway", TCBlocks.STONE_ANCIENT_DOORWAY);
    public static final Supplier<BlockItem> STONE_ELDRITCH_TILE = blockItem("stone_eldritch_tile", TCBlocks.STONE_ELDRITCH_TILE);
    public static final Supplier<BlockItem> STONE_POROUS = blockItem("stone_porous", TCBlocks.STONE_POROUS);

    public static final Supplier<Item> GOGGLES = ITEMS.register("goggles", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> AMBER = ITEMS.register("amber", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> QUICKSILVER = ITEMS.register("quicksilver", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> FABRIC = ITEMS.register("fabric", () -> new Item(new Item.Properties()));

    private static Supplier<BlockItem> blockItem(String id, Supplier<? extends net.minecraft.world.level.block.Block> block) {
        return ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private TCItems() {
    }
}