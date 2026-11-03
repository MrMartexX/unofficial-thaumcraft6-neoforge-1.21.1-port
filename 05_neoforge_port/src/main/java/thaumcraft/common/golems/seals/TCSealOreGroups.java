package thaumcraft.common.golems.seals;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Explicit counterparts of Forge 1.12 OreDictionary.initVanillaEntries groups. */
final class TCSealOreGroups {
    private static final String[][] BRIDGES = {
            {"minecraft:logs", "thaumcraft:legacy_ore_dictionary/log_wood"},
            {"minecraft:planks", "thaumcraft:legacy_ore_dictionary/plank_wood"},
            {"minecraft:wooden_slabs", "thaumcraft:legacy_ore_dictionary/slab_wood"},
            {"minecraft:wooden_stairs", "thaumcraft:legacy_ore_dictionary/stair_wood"},
            {"minecraft:wooden_fences", "thaumcraft:legacy_ore_dictionary/fence_wood"},
            {"minecraft:fence_gates", "thaumcraft:legacy_ore_dictionary/fence_gate_wood"},
            {"minecraft:wooden_doors", "thaumcraft:legacy_ore_dictionary/door_wood"},
            {"minecraft:saplings", "thaumcraft:legacy_ore_dictionary/tree_sapling"},
            {"minecraft:leaves", "thaumcraft:legacy_ore_dictionary/tree_leaves"},
            {"minecraft:wool", "thaumcraft:legacy_ore_dictionary/wool"},
            {"minecraft:sand", "thaumcraft:legacy_ore_dictionary/sand"},
            {"c:glass_blocks", "thaumcraft:legacy_ore_dictionary/block_glass"},
            {"c:glass_panes", "thaumcraft:legacy_ore_dictionary/pane_glass"}
    };
    private TCSealOreGroups() {}

    static boolean matches(ItemStack a, ItemStack b) {
        for (String[] bridge : BRIDGES) if (member(a, bridge) && member(b, bridge)) return true;
        if (chest(a) && chest(b)) return true;
        if (sandstone(a) && sandstone(b)) return true;
        if (dye(a) && dye(b)) return true;
        return a.has(net.minecraft.core.component.DataComponents.JUKEBOX_PLAYABLE)
                && b.has(net.minecraft.core.component.DataComponents.JUKEBOX_PLAYABLE);
    }
    private static boolean member(ItemStack stack, String[] tags) {
        for (String tag : tags) if (stack.is(tag(tag))) return true;
        return false;
    }
    private static boolean chest(ItemStack stack) {
        return stack.is(Items.CHEST) || stack.is(Items.TRAPPED_CHEST) || stack.is(Items.ENDER_CHEST)
                || stack.is(tag("thaumcraft:legacy_ore_dictionary/chest"));
    }
    private static boolean sandstone(ItemStack stack) {
        return TCSealItemIdentity.belongs(stack, "minecraft:sandstone") || TCSealItemIdentity.belongs(stack, "minecraft:red_sandstone")
                || stack.is(tag("c:sandstone/blocks"));
    }
    private static boolean dye(ItemStack stack) {
        return TCSealItemIdentity.belongs(stack, "minecraft:dye") || stack.is(tag("c:dyes"));
    }
    private static TagKey<Item> tag(String id) { return TagKey.create(Registries.ITEM, ResourceLocation.parse(id)); }
}
