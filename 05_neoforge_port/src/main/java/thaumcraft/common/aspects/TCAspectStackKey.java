package thaumcraft.common.aspects;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

record TCAspectStackKey(ResourceLocation itemId, DataComponentPatch components) {
    static TCAspectStackKey from(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException("Cannot create an aspect stack key for an empty stack");
        }
        return new TCAspectStackKey(BuiltInRegistries.ITEM.getKey(stack.getItem()), stack.getComponentsPatch());
    }
}
