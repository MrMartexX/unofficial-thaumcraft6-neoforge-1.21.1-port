package thaumcraft.common.items.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.CustomData;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.TCRechargeHelper;
import thaumcraft.common.items.components.TCInfusionEnchantmentsComponent;
import thaumcraft.common.items.curios.TCBaubleItem;
import thaumcraft.common.registry.TCDataComponents;

public enum TCEnumInfusionEnchantment {
    COLLECTOR("collector", 1, "axe,pickaxe,shovel,weapon", new AspectList().add(Aspect.AIR, 10).add(Aspect.DESIRE, 10)),
    DESTRUCTIVE("destructive", 1, "axe,pickaxe,shovel", new AspectList().add(Aspect.EARTH, 10).add(Aspect.ENTROPY, 10)),
    BURROWING("burrowing", 1, "axe,pickaxe", new AspectList().add(Aspect.EARTH, 10).add(Aspect.MOTION, 10)),
    SOUNDING("sounding", 2, "pickaxe", new AspectList().add(Aspect.AIR, 5).add(Aspect.SENSES, 5)),
    REFINING("refining", 4, "pickaxe", new AspectList().add(Aspect.ORDER, 10).add(Aspect.METAL, 10)),
    ARCING("arcing", 2, "weapon", new AspectList().add(Aspect.AIR, 10).add(Aspect.AVERSION, 10)),
    ESSENCE("essence", 5, "weapon", new AspectList().add(Aspect.WATER, 10).add(Aspect.MAGIC, 10)),
    VISBATTERY("visbattery", 3, "chargable", new AspectList().add(Aspect.AURA, 10).add(Aspect.ENERGY, 10)),
    VISCHARGE("vischarge", 1, "chargable", new AspectList().add(Aspect.AURA, 10).add(Aspect.VOID, 10)),
    SWIFT("swift", 4, "boots", new AspectList().add(Aspect.AIR, 10).add(Aspect.MOTION, 10)),
    AGILE("agile", 1, "legs", new AspectList().add(Aspect.AIR, 10).add(Aspect.MOTION, 10)),
    INFESTED("infested", 1, "armor", new AspectList().add(Aspect.EARTH, 10).add(Aspect.BEAST, 10)),
    LAMPLIGHT("lamplight", 1, "axe,pickaxe,shovel,weapon", new AspectList().add(Aspect.FIRE, 10).add(Aspect.LIGHT, 10));

    public final String id;
    public final int maxLevel;
    public final List<String> toolClasses;
    public final AspectList aspects;

    TCEnumInfusionEnchantment(String id, int maxLevel, String toolClasses, AspectList aspects) {
        this.id = id;
        this.maxLevel = maxLevel;
        this.toolClasses = Arrays.asList(toolClasses.split(","));
        this.aspects = aspects;
    }

    public Component getDisplayName(int level) {
        String key = "enchantment.infusion." + this.id;
        if (this.maxLevel > 1) {
            return Component.translatable(key).append(" ").append(Component.translatable("enchantment.level." + level));
        }
        return Component.translatable(key);
    }

    public static List<TCEnumInfusionEnchantment> getInfusionEnchantments(ItemStack stack) {
        List<TCEnumInfusionEnchantment> list = new ArrayList<>();
        if (stack == null || stack.isEmpty()) {
            return list;
        }

        TCInfusionEnchantmentsComponent component = stack.get(TCDataComponents.INFUSION_ENCHANTS.get());
        if (component != null) {
            list.addAll(component.enchantments().keySet());
            return list;
        }

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("infench")) {
            ListTag tagList = customData.copyTag().getList("infench", Tag.TAG_COMPOUND);
            for (int i = 0; i < tagList.size(); i++) {
                CompoundTag tag = tagList.getCompound(i);
                int id = tag.getShort("id");
                if (id >= 0 && id < values().length) {
                    TCEnumInfusionEnchantment ench = values()[id];
                    if (!list.contains(ench)) {
                        list.add(ench);
                    }
                }
            }
        }
        return list;
    }

    public static int getInfusionEnchantmentLevel(ItemStack stack, TCEnumInfusionEnchantment enchantment) {
        if (stack == null || stack.isEmpty() || enchantment == null) {
            return 0;
        }
        TCInfusionEnchantmentsComponent component = stack.get(TCDataComponents.INFUSION_ENCHANTS.get());
        if (component != null && component.has(enchantment)) {
            return component.level(enchantment);
        }

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("infench")) {
            ListTag tagList = customData.copyTag().getList("infench", Tag.TAG_COMPOUND);
            for (int i = 0; i < tagList.size(); i++) {
                CompoundTag tag = tagList.getCompound(i);
                int id = tag.getShort("id");
                if (id == enchantment.ordinal()) {
                    return tag.getShort("lvl");
                }
            }
        }
        return 0;
    }

    public static int getEnchantmentLevel(ItemStack stack, TCEnumInfusionEnchantment enchantment) {
        return getInfusionEnchantmentLevel(stack, enchantment);
    }

    public static void addInfusionEnchantment(ItemStack stack, TCEnumInfusionEnchantment ie, int level) {
        if (stack == null || stack.isEmpty() || ie == null || level <= 0) {
            return;
        }
        int clampedLevel = Math.min(level, ie.maxLevel);
        TCInfusionEnchantmentsComponent existing = stack.getOrDefault(
                TCDataComponents.INFUSION_ENCHANTS.get(),
                TCInfusionEnchantmentsComponent.EMPTY
        );
        stack.set(TCDataComponents.INFUSION_ENCHANTS.get(), existing.with(ie, clampedLevel));
    }

    public static boolean matchesToolClass(ItemStack stack, TCEnumInfusionEnchantment enchantment) {
        if (stack == null || stack.isEmpty() || enchantment == null) {
            return false;
        }
        if (enchantment.toolClasses.contains("all")) {
            return true;
        }

        var item = stack.getItem();
        if (enchantment.toolClasses.contains("weapon")) {
            if (item instanceof SwordItem || item instanceof AxeItem || stack.is(ItemTags.SWORDS) || stack.is(ItemTags.AXES)) {
                return true;
            }
        }
        if (enchantment.toolClasses.contains("pickaxe")) {
            if (item instanceof PickaxeItem || stack.is(ItemTags.PICKAXES)) {
                return true;
            }
        }
        if (enchantment.toolClasses.contains("axe")) {
            if (item instanceof AxeItem || stack.is(ItemTags.AXES)) {
                return true;
            }
        }
        if (enchantment.toolClasses.contains("shovel")) {
            if (item instanceof ShovelItem || stack.is(ItemTags.SHOVELS)) {
                return true;
            }
        }
        if (enchantment.toolClasses.contains("hoe")) {
            if (item instanceof HoeItem || stack.is(ItemTags.HOES)) {
                return true;
            }
        }
        if (item instanceof DiggerItem) {
            return true;
        }

        if (item instanceof ArmorItem armor) {
            if (enchantment.toolClasses.contains("armor")) {
                return true;
            }
            return switch (armor.getType()) {
                case HELMET -> enchantment.toolClasses.contains("helm");
                case CHESTPLATE -> enchantment.toolClasses.contains("chest");
                case LEGGINGS -> enchantment.toolClasses.contains("legs");
                case BOOTS -> enchantment.toolClasses.contains("boots");
                default -> false;
            };
        }

        if (item instanceof TCBaubleItem bauble) {
            if (enchantment.toolClasses.contains("bauble")) {
                return true;
            }
            return switch (bauble.getBaubleType()) {
                case AMULET -> enchantment.toolClasses.contains("amulet");
                case BELT -> enchantment.toolClasses.contains("belt");
                case RING -> enchantment.toolClasses.contains("ring");
                case CHARM -> enchantment.toolClasses.contains("charm");
                default -> false;
            };
        }

        if (enchantment.toolClasses.contains("chargable") && TCRechargeHelper.canRecharge(stack)) {
            return true;
        }

        return false;
    }
}
