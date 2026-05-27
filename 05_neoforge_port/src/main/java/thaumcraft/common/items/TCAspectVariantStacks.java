package thaumcraft.common.items;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.components.TCAspectStackComponent;
import thaumcraft.common.registry.TCDataComponents;

public final class TCAspectVariantStacks {
    private TCAspectVariantStacks() {
    }

    public static ItemStack crystal(Aspect aspect) {
        return stack("crystal_essence_", aspect, 1);
    }

    public static ItemStack phial(Aspect aspect) {
        return stack("phial_", aspect, 10);
    }

    private static ItemStack stack(String prefix, Aspect aspect, int amount) {
        if (aspect == null) {
            return ItemStack.EMPTY;
        }

        String aspectTag = aspect.getTag();
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, prefix + aspectTag);
        Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        if (item == null) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(item);
        stack.set(TCDataComponents.ASPECT_STACK.get(), new TCAspectStackComponent(aspectTag, amount));
        return stack;
    }
}
