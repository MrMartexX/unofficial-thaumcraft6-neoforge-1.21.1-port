package thaumcraft.client;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.ItemAspectVariant;
import thaumcraft.common.items.TCEssentiaItemHelper;
import thaumcraft.common.items.components.TCAspectStackComponent;
import thaumcraft.common.items.armor.ItemRobeArmor;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;

@EventBusSubscriber(modid = Thaumcraft.MODID, value = Dist.CLIENT)
public final class TCColorHandlers {
    private static final int WHITE = 0xFFFFFF;

    private static final int AIR = 0xFFFF7E;
    private static final int FIRE = 0xFF5A01;
    private static final int WATER = 0x3CD4FC;
    private static final int EARTH = 0x56C000;
    private static final int ORDER = 0xD5D4EC;
    private static final int ENTROPY = 0x404040;
    private static final int FLUX = 0x800080;

    private static final int DYE_BLACK = 0x1D1D21;
    private static final int DYE_BLUE = 0x3C44AA;
    private static final int DYE_BROWN = 0x835432;
    private static final int DYE_CYAN = 0x169C9C;
    private static final int DYE_GRAY = 0x474F52;
    private static final int DYE_GREEN = 0x5E7C16;
    private static final int DYE_LIGHT_BLUE = 0x3AB3DA;
    private static final int DYE_LIME = 0x80C71F;
    private static final int DYE_MAGENTA = 0xC74EBD;
    private static final int DYE_ORANGE = 0xF9801D;
    private static final int DYE_PINK = 0xF38BAA;
    private static final int DYE_PURPLE = 0x8932B8;
    private static final int DYE_RED = 0xB02E26;
    private static final int DYE_SILVER = 0x9D9D97;
    private static final int DYE_WHITE = 0xF9FFFE;
    private static final int DYE_YELLOW = 0xFED83D;

    private TCColorHandlers() {
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        BlockColor leafColor = (state, level, pos, tintIndex) -> {
            if (state.is(TCBlocks.LEAVES_SILVERWOOD.get())) {
                return 0xFFFFFFFF;
            }

            if (level != null && pos != null) {
                return BiomeColors.getAverageFoliageColor(level, pos);
            }

            return FoliageColor.getDefaultColor();
        };

        event.register(leafColor,
                TCBlocks.LEAVES_GREATWOOD.get(),
                TCBlocks.LEAVES_SILVERWOOD.get());

        event.register((state, level, pos, tintIndex) -> crystalColor(state.getBlock()),
                TCBlocks.CRYSTAL_AER.get(),
                TCBlocks.CRYSTAL_IGNIS.get(),
                TCBlocks.CRYSTAL_AQUA.get(),
                TCBlocks.CRYSTAL_TERRA.get(),
                TCBlocks.CRYSTAL_ORDO.get(),
                TCBlocks.CRYSTAL_PERDITIO.get(),
                TCBlocks.CRYSTAL_VITIUM.get());

        event.register((state, level, pos, tintIndex) -> candleColor(state.getBlock(), tintIndex),
                TCBlocks.CANDLE_BLACK.get(),
                TCBlocks.CANDLE_BLUE.get(),
                TCBlocks.CANDLE_BROWN.get(),
                TCBlocks.CANDLE_CYAN.get(),
                TCBlocks.CANDLE_GRAY.get(),
                TCBlocks.CANDLE_GREEN.get(),
                TCBlocks.CANDLE_LIGHTBLUE.get(),
                TCBlocks.CANDLE_LIME.get(),
                TCBlocks.CANDLE_MAGENTA.get(),
                TCBlocks.CANDLE_ORANGE.get(),
                TCBlocks.CANDLE_PINK.get(),
                TCBlocks.CANDLE_PURPLE.get(),
                TCBlocks.CANDLE_RED.get(),
                TCBlocks.CANDLE_SILVER.get(),
                TCBlocks.CANDLE_WHITE.get(),
                TCBlocks.CANDLE_YELLOW.get());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        ItemColor greatwoodLeavesItem = (stack, tintIndex) -> FoliageColor.getDefaultColor();
        ItemColor silverwoodLeavesItem = (stack, tintIndex) -> WHITE;

        event.register(greatwoodLeavesItem, TCBlocks.LEAVES_GREATWOOD.get());
        event.register(silverwoodLeavesItem, TCBlocks.LEAVES_SILVERWOOD.get());

        event.register((stack, tintIndex) -> AIR, TCBlocks.CRYSTAL_AER.get());
        event.register((stack, tintIndex) -> FIRE, TCBlocks.CRYSTAL_IGNIS.get());
        event.register((stack, tintIndex) -> WATER, TCBlocks.CRYSTAL_AQUA.get());
        event.register((stack, tintIndex) -> EARTH, TCBlocks.CRYSTAL_TERRA.get());
        event.register((stack, tintIndex) -> ORDER, TCBlocks.CRYSTAL_ORDO.get());
        event.register((stack, tintIndex) -> ENTROPY, TCBlocks.CRYSTAL_PERDITIO.get());
        event.register((stack, tintIndex) -> FLUX, TCBlocks.CRYSTAL_VITIUM.get());
        event.register((stack, tintIndex) -> 0xFF1D1D21, TCBlocks.NITOR_BLACK.get());
        event.register((stack, tintIndex) -> 0xFF3C44AA, TCBlocks.NITOR_BLUE.get());
        event.register((stack, tintIndex) -> 0xFF835432, TCBlocks.NITOR_BROWN.get());
        event.register((stack, tintIndex) -> 0xFF169C9C, TCBlocks.NITOR_CYAN.get());
        event.register((stack, tintIndex) -> 0xFF474F52, TCBlocks.NITOR_GRAY.get());
        event.register((stack, tintIndex) -> 0xFF5E7C16, TCBlocks.NITOR_GREEN.get());
        event.register((stack, tintIndex) -> 0xFFFFFF55, TCBlocks.NITOR_YELLOW.get());
        event.register((stack, tintIndex) -> 0xFF3AB3DA, TCBlocks.NITOR_LIGHTBLUE.get());
        event.register((stack, tintIndex) -> 0xFF80C71F, TCBlocks.NITOR_LIME.get());
        event.register((stack, tintIndex) -> 0xFFC74EBD, TCBlocks.NITOR_MAGENTA.get());
        event.register((stack, tintIndex) -> 0xFFF9801D, TCBlocks.NITOR_ORANGE.get());
        event.register((stack, tintIndex) -> 0xFFF38BAA, TCBlocks.NITOR_PINK.get());
        event.register((stack, tintIndex) -> 0xFF8932B8, TCBlocks.NITOR_PURPLE.get());
        event.register((stack, tintIndex) -> 0xFFB02E26, TCBlocks.NITOR_RED.get());
        event.register((stack, tintIndex) -> 0xFF9D9D97, TCBlocks.NITOR_SILVER.get());
        event.register((stack, tintIndex) -> 0xFFF9FFFE, TCBlocks.NITOR_WHITE.get());
        event.register((stack, tintIndex) -> candleColor(Block.byItem(stack.getItem()), tintIndex),
                TCBlocks.CANDLE_BLACK.get(),
                TCBlocks.CANDLE_BLUE.get(),
                TCBlocks.CANDLE_BROWN.get(),
                TCBlocks.CANDLE_CYAN.get(),
                TCBlocks.CANDLE_GRAY.get(),
                TCBlocks.CANDLE_GREEN.get(),
                TCBlocks.CANDLE_LIGHTBLUE.get(),
                TCBlocks.CANDLE_LIME.get(),
                TCBlocks.CANDLE_MAGENTA.get(),
                TCBlocks.CANDLE_ORANGE.get(),
                TCBlocks.CANDLE_PINK.get(),
                TCBlocks.CANDLE_PURPLE.get(),
                TCBlocks.CANDLE_RED.get(),
                TCBlocks.CANDLE_SILVER.get(),
                TCBlocks.CANDLE_WHITE.get(),
                TCBlocks.CANDLE_YELLOW.get());
        ItemColor aspectVariantColor = TCColorHandlers::aspectVariantColor;
        for (var entry : TCItems.ITEMS.getEntries()) {
            Item item = entry.get();
            if (item instanceof ItemAspectVariant) {
                event.register(aspectVariantColor, item);
            }
        }
        event.register((stack, tintIndex) -> {
            if (tintIndex != 1) {
                return 0xFFFFFFFF;
            }
            Aspect aspect = TCEssentiaItemHelper.aspectFromStack(stack);
            return aspect == null ? 0xFFFFFFFF : (0xFF000000 | (aspect.getColor() & 0xFFFFFF));
        }, TCItems.JAR_LABEL_ESSENCE.get());
        event.register((stack, tintIndex) -> {
            if (tintIndex != 1) {
                return 0xFFFFFFFF;
            }
            Aspect aspect = TCEssentiaItemHelper.aspectFromStack(stack);
            return aspect == null ? 0xFFFFFFFF : (0xFF000000 | (aspect.getColor() & 0xFFFFFF));
        }, TCItems.JAR_NORMAL.get(), TCItems.JAR_VOID.get());
        ItemColor robeColor = (stack, tintIndex) -> tintIndex == 0
                ? DyedItemColor.getOrDefault(stack, ItemRobeArmor.LEGACY_DEFAULT_COLOR)
                : WHITE;
        event.register(robeColor,
                TCItems.CLOTH_BOOTS.get(),
                TCItems.CLOTH_LEGS.get(),
                TCItems.CLOTH_CHEST.get(),
                TCItems.VOID_ROBE_LEGS.get(),
                TCItems.VOID_ROBE_CHEST.get(),
                TCItems.VOID_ROBE_HELM.get());
    }

    private static int aspectVariantColor(ItemStack stack, int tintIndex) {
        if (!(stack.getItem() instanceof ItemAspectVariant item)) {
            return 0xFFFFFFFF;
        }

        if (item.kind() == ItemAspectVariant.Kind.PHIAL && tintIndex != 1) {
            return 0xFFFFFFFF;
        }

        if (item.kind() == ItemAspectVariant.Kind.CRYSTAL_ESSENCE && tintIndex != 0) {
            return 0xFFFFFFFF;
        }

        Aspect aspect = aspectFromStack(stack, item);
        return aspect == null ? 0xFFFFFFFF : (0xFF000000 | (aspect.getColor() & 0xFFFFFF));
    }

    private static Aspect aspectFromStack(ItemStack stack, ItemAspectVariant item) {
        TCAspectStackComponent aspectStack = stack.get(TCDataComponents.ASPECT_STACK.get());
        String aspectTag = aspectStack != null && !aspectStack.isEmpty()
                ? aspectStack.aspect()
                : item.aspectTag();
        return Aspect.getAspect(aspectTag);
    }

    private static int crystalColor(Block block) {
        if (block == TCBlocks.CRYSTAL_AER.get()) {
            return AIR;
        }
        if (block == TCBlocks.CRYSTAL_IGNIS.get()) {
            return FIRE;
        }
        if (block == TCBlocks.CRYSTAL_AQUA.get()) {
            return WATER;
        }
        if (block == TCBlocks.CRYSTAL_TERRA.get()) {
            return EARTH;
        }
        if (block == TCBlocks.CRYSTAL_ORDO.get()) {
            return ORDER;
        }
        if (block == TCBlocks.CRYSTAL_PERDITIO.get()) {
            return ENTROPY;
        }
        if (block == TCBlocks.CRYSTAL_VITIUM.get()) {
            return FLUX;
        }

        return 0xFFFFFFFF;
    }

    private static int candleColor(Block block, int tintIndex) {
        if (tintIndex != 0) {
            return 0xFFFFFFFF;
        }
        if (block == TCBlocks.CANDLE_BLACK.get()) {
            return DYE_BLACK;
        }
        if (block == TCBlocks.CANDLE_BLUE.get()) {
            return DYE_BLUE;
        }
        if (block == TCBlocks.CANDLE_BROWN.get()) {
            return DYE_BROWN;
        }
        if (block == TCBlocks.CANDLE_CYAN.get()) {
            return DYE_CYAN;
        }
        if (block == TCBlocks.CANDLE_GRAY.get()) {
            return DYE_GRAY;
        }
        if (block == TCBlocks.CANDLE_GREEN.get()) {
            return DYE_GREEN;
        }
        if (block == TCBlocks.CANDLE_LIGHTBLUE.get()) {
            return DYE_LIGHT_BLUE;
        }
        if (block == TCBlocks.CANDLE_LIME.get()) {
            return DYE_LIME;
        }
        if (block == TCBlocks.CANDLE_MAGENTA.get()) {
            return DYE_MAGENTA;
        }
        if (block == TCBlocks.CANDLE_ORANGE.get()) {
            return DYE_ORANGE;
        }
        if (block == TCBlocks.CANDLE_PINK.get()) {
            return DYE_PINK;
        }
        if (block == TCBlocks.CANDLE_PURPLE.get()) {
            return DYE_PURPLE;
        }
        if (block == TCBlocks.CANDLE_RED.get()) {
            return DYE_RED;
        }
        if (block == TCBlocks.CANDLE_SILVER.get()) {
            return DYE_SILVER;
        }
        if (block == TCBlocks.CANDLE_WHITE.get()) {
            return DYE_WHITE;
        }
        if (block == TCBlocks.CANDLE_YELLOW.get()) {
            return DYE_YELLOW;
        }

        return 0xFFFFFFFF;
    }
}
