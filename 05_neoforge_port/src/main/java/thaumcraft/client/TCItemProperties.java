package thaumcraft.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.components.TCLegacyItemComponent;
import thaumcraft.common.items.TCEssentiaItemHelper;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.tiles.essentia.TCWardedJarBlockEntity;

public final class TCItemProperties {
    private TCItemProperties() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(TCItemProperties::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    TCItems.CASTER_BASIC.get(),
                    ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "focus"),
                    (stack, level, entity, seed) -> stack.getItem() instanceof ItemCaster caster && caster.hasFocus(stack) ? 1.0F : 0.0F
            );
            ResourceLocation fill = ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "fill");
            ItemProperties.register(TCItems.JAR_NORMAL.get(), fill, (stack, level, entity, seed) -> jarFill(stack));
            ItemProperties.register(TCItems.JAR_VOID.get(), fill, (stack, level, entity, seed) -> jarFill(stack));

            ResourceLocation type = ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "type");
            ItemProperties.register(TCItems.PRIMORDIAL_PEARL.get(), type, (stack, level, entity, seed) -> legacyPearlType(stack));
        });
    }

    private static float jarFill(ItemStack stack) {
        int amount = TCEssentiaItemHelper.aspectAmount(stack);
        if (amount <= 0) {
            return 0.0F;
        }
        float ratio = amount / (float) TCWardedJarBlockEntity.CAPACITY;
        if (ratio <= 0.25F) {
            return 1.0F;
        }
        if (ratio <= 0.5F) {
            return 2.0F;
        }
        if (ratio <= 0.75F) {
            return 3.0F;
        }
        return 4.0F;
    }

    private static float legacyPearlType(ItemStack stack) {
        TCLegacyItemComponent legacyItem = stack.get(TCDataComponents.LEGACY_ITEM.get());
        int metadata = legacyItem == null ? 0 : legacyItem.metadata();
        if (metadata < 3) {
            return 0.0F;
        }
        if (metadata < 6) {
            return 1.0F;
        }
        return 2.0F;
    }
}
