package thaumcraft.common.aspects;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

final class TCAspectStackRules {
    private static final ResourceKey<Potion> WATER = potion("water");
    private static final Map<PotionKey, AspectList> LEGACY_POTION_OVERRIDES = createLegacyPotionOverrides();
    private static final Map<ResourceKey<Potion>, PotionMix> POTION_MIXES = createPotionMixes();

    static boolean isLegacyNoAspectStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        return item instanceof SpawnEggItem
                || item == Items.BARRIER
                || item == Items.COMMAND_BLOCK
                || item == Items.REPEATING_COMMAND_BLOCK
                || item == Items.CHAIN_COMMAND_BLOCK
                || item == Items.COMMAND_BLOCK_MINECART
                || item == Items.STRUCTURE_BLOCK
                || item == Items.STRUCTURE_VOID
                || item == Items.KNOWLEDGE_BOOK
                || item == Items.FILLED_MAP
                || item == Items.WRITTEN_BOOK
                || (item == Items.ELYTRA && stack.getDamageValue() > 0)
                || item == Items.FIREWORK_ROCKET
                || item == Items.FIREWORK_STAR
                || item == Items.INFESTED_STONE
                || item == Items.INFESTED_COBBLESTONE
                || item == Items.INFESTED_STONE_BRICKS
                || item == Items.INFESTED_MOSSY_STONE_BRICKS
                || item == Items.INFESTED_CRACKED_STONE_BRICKS
                || item == Items.INFESTED_CHISELED_STONE_BRICKS
                || item == Items.INFESTED_DEEPSLATE;
    }

    static ItemStack generatedLookupBaseStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(stack.getItem());
    }

    static boolean isComponentSensitiveWithoutLegacyBase(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (!stack.is(Items.SPLASH_POTION) && !stack.is(Items.TIPPED_ARROW) && !stack.is(Items.LINGERING_POTION)) {
            return false;
        }

        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return contents == null || contents.potion().isEmpty();
    }

    static AspectList getPotionContentAspects(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !isPotionCarrier(stack.getItem())) {
            return null;
        }

        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null || contents.potion().isEmpty()) {
            AspectList fallback = new AspectList().add(Aspect.MAGIC, 5).add(Aspect.ALCHEMY, 5);
            addPotionCarrierAspects(stack, fallback);
            return finish(fallback);
        }

        ResourceKey<Potion> potion = contents.potion().get().unwrapKey().orElse(null);
        if (potion == null) {
            return null;
        }

        AspectList legacyOverride = LEGACY_POTION_OVERRIDES.get(new PotionKey(stack.getItem(), potion));
        if (legacyOverride != null) {
            return legacyOverride.copy();
        }

        AspectList aspects = getPotionAspects(potion);
        addPotionCarrierAspects(stack, aspects);
        return finish(aspects);
    }

    static AspectList applyStackBonuses(ItemStack stack, AspectList source) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        if (isLegacyNoAspectStack(stack)) {
            return new AspectList();
        }

        AspectList out = source != null ? source.copy() : new AspectList();
        addItemTypeBonuses(stack, out);
        addEnchantmentBonuses(stack, out);
        return finish(out);
    }

    private static void addItemTypeBonuses(ItemStack stack, AspectList out) {
        Item item = stack.getItem();
        if (item instanceof ArmorItem armor && !isHorseArmor(item)) {
            out.merge(Aspect.PROTECT, armor.getDefense() * 4);
        }
        if (item instanceof SwordItem sword) {
            out.merge(Aspect.AVERSION, swordAmount(sword.getTier()));
        } else if (item instanceof BowItem) {
            out.merge(Aspect.AVERSION, 10);
            out.merge(Aspect.FLIGHT, 5);
        } else if (item instanceof DiggerItem && !(item instanceof HoeItem) && item instanceof TieredItem tiered) {
            out.merge(Aspect.TOOL, toolAmount(tiered.getTier()));
        } else if (item instanceof ShearsItem || item instanceof HoeItem) {
            out.merge(Aspect.TOOL, durabilityToolAmount(stack));
        }

        if (item instanceof DyeItem) {
            out.merge(Aspect.SENSES, 5);
        }
    }

    private static void addEnchantmentBonuses(ItemStack stack, AspectList out) {
        ItemEnchantments enchantments = stack.is(Items.ENCHANTED_BOOK)
                ? stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                : stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            Holder<Enchantment> enchantment = entry.getKey();
            int level = entry.getIntValue() * 3;
            addEnchantmentAspect(enchantment.unwrapKey().orElse(null), level, out);

            int magic = rarityMagicBonus(enchantment.value().getWeight());
            magic += level;
            if (magic > 0) {
                out.merge(Aspect.MAGIC, magic);
            }
        }
    }

    private static void addEnchantmentAspect(ResourceKey<Enchantment> key, int level, AspectList out) {
        if (key == null || level <= 0) {
            return;
        }

        if (is(key, Enchantments.AQUA_AFFINITY)) {
            out.merge(Aspect.WATER, level);
        } else if (is(key, Enchantments.BANE_OF_ARTHROPODS)) {
            out.merge(Aspect.BEAST, level / 2);
            out.merge(Aspect.AVERSION, level / 2);
        } else if (is(key, Enchantments.BLAST_PROTECTION)) {
            out.merge(Aspect.PROTECT, level / 2);
            out.merge(Aspect.ENTROPY, level / 2);
        } else if (is(key, Enchantments.EFFICIENCY)) {
            out.merge(Aspect.TOOL, level);
        } else if (is(key, Enchantments.FEATHER_FALLING)) {
            out.merge(Aspect.FLIGHT, level);
        } else if (is(key, Enchantments.FIRE_ASPECT)) {
            out.merge(Aspect.FIRE, level / 2);
            out.merge(Aspect.AVERSION, level / 2);
        } else if (is(key, Enchantments.FIRE_PROTECTION)) {
            out.merge(Aspect.PROTECT, level / 2);
            out.merge(Aspect.FIRE, level / 2);
        } else if (is(key, Enchantments.FLAME)) {
            out.merge(Aspect.FIRE, level);
        } else if (is(key, Enchantments.FORTUNE)) {
            out.merge(Aspect.DESIRE, level);
        } else if (is(key, Enchantments.INFINITY)) {
            out.merge(Aspect.CRAFT, level);
        } else if (is(key, Enchantments.KNOCKBACK)) {
            out.merge(Aspect.AIR, level);
        } else if (is(key, Enchantments.LOOTING)) {
            out.merge(Aspect.DESIRE, level);
        } else if (is(key, Enchantments.POWER)) {
            out.merge(Aspect.AVERSION, level);
        } else if (is(key, Enchantments.PROJECTILE_PROTECTION)) {
            out.merge(Aspect.PROTECT, level);
        } else if (is(key, Enchantments.PROTECTION)) {
            out.merge(Aspect.PROTECT, level);
        } else if (is(key, Enchantments.PUNCH)) {
            out.merge(Aspect.AIR, level);
        } else if (is(key, Enchantments.RESPIRATION)) {
            out.merge(Aspect.AIR, level);
        } else if (is(key, Enchantments.SHARPNESS)) {
            out.merge(Aspect.AVERSION, level);
        } else if (is(key, Enchantments.SILK_TOUCH)) {
            out.merge(Aspect.EXCHANGE, level);
        } else if (is(key, Enchantments.THORNS)) {
            out.merge(Aspect.AVERSION, level);
        } else if (is(key, Enchantments.SMITE)) {
            out.merge(Aspect.UNDEAD, level / 2);
            out.merge(Aspect.AVERSION, level / 2);
        } else if (is(key, Enchantments.UNBREAKING)) {
            out.merge(Aspect.EARTH, level);
        } else if (is(key, Enchantments.DEPTH_STRIDER)) {
            out.merge(Aspect.WATER, level);
        } else if (is(key, Enchantments.LUCK_OF_THE_SEA)) {
            out.merge(Aspect.DESIRE, level);
        } else if (is(key, Enchantments.LURE)) {
            out.merge(Aspect.BEAST, level);
        } else if (is(key, Enchantments.FROST_WALKER)) {
            out.merge(Aspect.COLD, level);
        } else if (is(key, Enchantments.MENDING)) {
            out.merge(Aspect.CRAFT, level);
        } else if (is(key, Enchantments.SOUL_SPEED)) {
            out.merge(Aspect.SOUL, level / 2);
            out.merge(Aspect.MOTION, level / 2);
        } else if (is(key, Enchantments.SWIFT_SNEAK)) {
            out.merge(Aspect.MOTION, level);
        } else if (is(key, Enchantments.LOYALTY)) {
            out.merge(Aspect.TRAP, level);
        } else if (is(key, Enchantments.IMPALING)) {
            out.merge(Aspect.AVERSION, level / 2);
            out.merge(Aspect.WATER, level / 2);
        } else if (is(key, Enchantments.RIPTIDE)) {
            out.merge(Aspect.WATER, level / 2);
            out.merge(Aspect.MOTION, level / 2);
        } else if (is(key, Enchantments.CHANNELING)) {
            out.merge(Aspect.AIR, level / 2);
            out.merge(Aspect.ENERGY, level / 2);
        } else if (is(key, Enchantments.MULTISHOT)) {
            out.merge(Aspect.CRAFT, level / 2);
            out.merge(Aspect.AVERSION, level / 2);
        } else if (is(key, Enchantments.QUICK_CHARGE)) {
            out.merge(Aspect.MOTION, level / 2);
            out.merge(Aspect.AVERSION, level / 2);
        } else if (is(key, Enchantments.PIERCING)) {
            out.merge(Aspect.TOOL, level / 2);
            out.merge(Aspect.AVERSION, level / 2);
        } else if (is(key, Enchantments.DENSITY)) {
            out.merge(Aspect.EARTH, level / 2);
            out.merge(Aspect.AVERSION, level / 2);
        } else if (is(key, Enchantments.BREACH)) {
            out.merge(Aspect.TOOL, level / 2);
            out.merge(Aspect.AVERSION, level / 2);
        } else if (is(key, Enchantments.WIND_BURST)) {
            out.merge(Aspect.AIR, level / 2);
            out.merge(Aspect.AVERSION, level / 2);
        }
    }

    private static boolean is(ResourceKey<Enchantment> actual, ResourceKey<Enchantment> expected) {
        return actual.equals(expected);
    }

    private static AspectList getPotionAspects(ResourceKey<Potion> potion) {
        AspectList out = new AspectList();
        if (potion.equals(WATER)) {
            out.add(Aspect.WATER, 5);
            return out;
        }

        Set<Item> reagents = new LinkedHashSet<>();
        collectPotionReagents(potion, reagents, new LinkedHashSet<>());
        boolean foundPath = false;
        for (Item reagent : reagents) {
            AspectList reagentAspects = TCAspectAssignments.getObjectAspects(new ItemStack(reagent));
            if (reagentAspects == null) {
                continue;
            }

            foundPath = true;
            for (Aspect aspect : reagentAspects.getAspects()) {
                if (aspect != null) {
                    out.add(aspect, reagentAspects.getAmount(aspect));
                }
            }
            out.add(Aspect.ALCHEMY, 3);
        }

        if (!foundPath) {
            out.add(Aspect.MAGIC, 5);
            out.add(Aspect.ALCHEMY, 5);
            return out;
        }

        for (Aspect aspect : out.copy().getAspects()) {
            if (aspect != null) {
                out.remove(aspect, (int)(out.getAmount(aspect) * 0.66F));
            }
        }
        return out;
    }

    private static void collectPotionReagents(ResourceKey<Potion> potion, Set<Item> reagents, Set<ResourceKey<Potion>> history) {
        PotionMix mix = POTION_MIXES.get(potion);
        if (mix == null || !history.add(potion)) {
            return;
        }

        reagents.add(mix.reagent());
        collectPotionReagents(mix.input(), reagents, history);
    }

    private static boolean isPotionCarrier(Item item) {
        return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.TIPPED_ARROW;
    }

    private static void addPotionCarrierAspects(ItemStack stack, AspectList aspects) {
        if (stack.is(Items.POTION)) {
            aspects.add(Aspect.WATER, 5);
        } else if (stack.is(Items.TIPPED_ARROW)) {
            aspects.add(Aspect.AVERSION, 5);
        } else if (stack.is(Items.SPLASH_POTION)) {
            aspects.add(Aspect.ENERGY, 5);
        } else if (stack.is(Items.LINGERING_POTION)) {
            aspects.add(Aspect.TRAP, 5);
        }
    }

    private static AspectList finish(AspectList source) {
        removeNonPositive(source);
        return capAspects(AspectHelper.cullTags(source), 500);
    }

    private static void removeNonPositive(AspectList aspects) {
        for (Aspect aspect : aspects.copy().getAspects()) {
            if (aspect == null || aspects.getAmount(aspect) <= 0) {
                aspects.remove(aspect);
            }
        }
    }

    private static AspectList capAspects(AspectList source, int amount) {
        AspectList out = new AspectList();
        for (Aspect aspect : source.getAspects()) {
            if (aspect != null) {
                out.merge(aspect, Math.min(amount, source.getAmount(aspect)));
            }
        }
        return out;
    }

    private static int toolAmount(Tier tier) {
        if (tier == Tiers.WOOD || tier == Tiers.GOLD) {
            return 4;
        }
        if (tier == Tiers.STONE) {
            return 8;
        }
        if (tier == Tiers.IRON) {
            return 12;
        }
        if (tier == Tiers.DIAMOND) {
            return 16;
        }
        if (tier == Tiers.NETHERITE) {
            return 20;
        }
        return 4;
    }

    private static int swordAmount(Tier tier) {
        return toolAmount(tier);
    }

    private static boolean isHorseArmor(Item item) {
        return item == Items.LEATHER_HORSE_ARMOR
                || item == Items.IRON_HORSE_ARMOR
                || item == Items.GOLDEN_HORSE_ARMOR
                || item == Items.DIAMOND_HORSE_ARMOR;
    }

    private static int durabilityToolAmount(ItemStack stack) {
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= Tiers.WOOD.getUses()) {
            return 4;
        }
        if (maxDamage <= Tiers.STONE.getUses() || maxDamage <= Tiers.GOLD.getUses()) {
            return 8;
        }
        if (maxDamage <= Tiers.IRON.getUses()) {
            return 12;
        }
        return 16;
    }

    private static int rarityMagicBonus(int weight) {
        if (weight <= 1) {
            return 6;
        }
        if (weight <= 2) {
            return 4;
        }
        if (weight <= 5) {
            return 2;
        }
        return 0;
    }

    private static Map<PotionKey, AspectList> createLegacyPotionOverrides() {
        LinkedHashMap<PotionKey, AspectList> overrides = new LinkedHashMap<>();
        putPotionOverride(overrides, Items.LINGERING_POTION, "harming", new AspectList()
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.SENSES, 2)
                .add(Aspect.BEAST, 2)
                .add(Aspect.PLANT, 3)
                .add(Aspect.TRAP, 5));
        putPotionOverride(overrides, Items.LINGERING_POTION, "invisibility", new AspectList()
                .add(Aspect.SENSES, 6)
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.PLANT, 4)
                .add(Aspect.BEAST, 2)
                .add(Aspect.TRAP, 5));
        putPotionOverride(overrides, Items.LINGERING_POTION, "long_invisibility", new AspectList()
                .add(Aspect.SENSES, 6)
                .add(Aspect.ALCHEMY, 9)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.PLANT, 4)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.TRAP, 5));
        putPotionOverride(overrides, Items.LINGERING_POTION, "long_night_vision", new AspectList()
                .add(Aspect.SENSES, 5)
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.PLANT, 2)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.TRAP, 5));
        putPotionOverride(overrides, Items.LINGERING_POTION, "long_regeneration", new AspectList()
                .add(Aspect.UNDEAD, 2)
                .add(Aspect.SOUL, 4)
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.PLANT, 1)
                .add(Aspect.FLUX, 1)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.TRAP, 5));
        putPotionOverride(overrides, Items.LINGERING_POTION, "long_slowness", new AspectList()
                .add(Aspect.ENERGY, 4)
                .add(Aspect.ALCHEMY, 9)
                .add(Aspect.BEAST, 3)
                .add(Aspect.MOTION, 4)
                .add(Aspect.SENSES, 2)
                .add(Aspect.PLANT, 3)
                .add(Aspect.TRAP, 5));
        putPotionOverride(overrides, Items.LINGERING_POTION, "long_strength", new AspectList()
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.FIRE, 2)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.PLANT, 1)
                .add(Aspect.FLUX, 1)
                .add(Aspect.TRAP, 5));
        putPotionOverride(overrides, Items.LINGERING_POTION, "long_weakness", new AspectList()
                .add(Aspect.ALCHEMY, 4)
                .add(Aspect.SENSES, 2)
                .add(Aspect.BEAST, 2)
                .add(Aspect.DEATH, 2)
                .add(Aspect.PLANT, 3)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.TRAP, 5));
        putPotionOverride(overrides, Items.LINGERING_POTION, "mundane", new AspectList()
                .add(Aspect.ALCHEMY, 3)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.TRAP, 5));
        putPotionOverride(overrides, Items.LINGERING_POTION, "strong_leaping", new AspectList()
                .add(Aspect.BEAST, 2)
                .add(Aspect.PROTECT, 2)
                .add(Aspect.MOTION, 4)
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.SENSES, 2)
                .add(Aspect.LIGHT, 4)
                .add(Aspect.TRAP, 5));
        putPotionOverride(overrides, Items.LINGERING_POTION, "strong_regeneration", new AspectList()
                .add(Aspect.UNDEAD, 2)
                .add(Aspect.SOUL, 4)
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.FLUX, 1)
                .add(Aspect.SENSES, 2)
                .add(Aspect.LIGHT, 4)
                .add(Aspect.TRAP, 5));
        putPotionOverride(overrides, Items.LINGERING_POTION, "strong_strength", new AspectList()
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.FIRE, 2)
                .add(Aspect.SENSES, 2)
                .add(Aspect.LIGHT, 4)
                .add(Aspect.PLANT, 1)
                .add(Aspect.FLUX, 1)
                .add(Aspect.TRAP, 5));
        putPotionOverride(overrides, Items.POTION, "harming", new AspectList()
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.SENSES, 2)
                .add(Aspect.BEAST, 2)
                .add(Aspect.PLANT, 3)
                .add(Aspect.WATER, 6));
        putPotionOverride(overrides, Items.POTION, "invisibility", new AspectList()
                .add(Aspect.SENSES, 6)
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.PLANT, 4)
                .add(Aspect.BEAST, 2)
                .add(Aspect.WATER, 6));
        putPotionOverride(overrides, Items.POTION, "long_invisibility", new AspectList()
                .add(Aspect.SENSES, 6)
                .add(Aspect.ALCHEMY, 9)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.PLANT, 4)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.WATER, 6));
        putPotionOverride(overrides, Items.POTION, "long_night_vision", new AspectList()
                .add(Aspect.SENSES, 5)
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.PLANT, 2)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.WATER, 5));
        putPotionOverride(overrides, Items.POTION, "long_regeneration", new AspectList()
                .add(Aspect.UNDEAD, 2)
                .add(Aspect.SOUL, 4)
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.PLANT, 1)
                .add(Aspect.FLUX, 1)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.WATER, 5));
        putPotionOverride(overrides, Items.POTION, "long_slowness", new AspectList()
                .add(Aspect.ENERGY, 4)
                .add(Aspect.ALCHEMY, 9)
                .add(Aspect.BEAST, 3)
                .add(Aspect.MOTION, 4)
                .add(Aspect.SENSES, 2)
                .add(Aspect.PLANT, 3)
                .add(Aspect.WATER, 6));
        putPotionOverride(overrides, Items.POTION, "long_strength", new AspectList()
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.FIRE, 2)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.PLANT, 1)
                .add(Aspect.FLUX, 1)
                .add(Aspect.WATER, 5));
        putPotionOverride(overrides, Items.POTION, "long_weakness", new AspectList()
                .add(Aspect.ALCHEMY, 4)
                .add(Aspect.SENSES, 2)
                .add(Aspect.BEAST, 2)
                .add(Aspect.DEATH, 2)
                .add(Aspect.PLANT, 3)
                .add(Aspect.WATER, 6)
                .add(Aspect.ENERGY, 4));
        putPotionOverride(overrides, Items.POTION, "mundane", new AspectList()
                .add(Aspect.ALCHEMY, 3)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.WATER, 5));
        putPotionOverride(overrides, Items.POTION, "strong_leaping", new AspectList()
                .add(Aspect.BEAST, 2)
                .add(Aspect.PROTECT, 2)
                .add(Aspect.MOTION, 4)
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.SENSES, 2)
                .add(Aspect.LIGHT, 4)
                .add(Aspect.WATER, 5));
        putPotionOverride(overrides, Items.POTION, "strong_regeneration", new AspectList()
                .add(Aspect.UNDEAD, 2)
                .add(Aspect.SOUL, 4)
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.FLUX, 1)
                .add(Aspect.SENSES, 2)
                .add(Aspect.LIGHT, 4)
                .add(Aspect.WATER, 5));
        putPotionOverride(overrides, Items.POTION, "strong_strength", new AspectList()
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.FIRE, 2)
                .add(Aspect.SENSES, 2)
                .add(Aspect.LIGHT, 4)
                .add(Aspect.PLANT, 1)
                .add(Aspect.FLUX, 1)
                .add(Aspect.WATER, 5));
        putPotionOverride(overrides, Items.SPLASH_POTION, "harming", new AspectList()
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.SENSES, 2)
                .add(Aspect.BEAST, 2)
                .add(Aspect.PLANT, 3)
                .add(Aspect.ENERGY, 5));
        putPotionOverride(overrides, Items.SPLASH_POTION, "invisibility", new AspectList()
                .add(Aspect.SENSES, 6)
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.PLANT, 4)
                .add(Aspect.BEAST, 2)
                .add(Aspect.ENERGY, 5));
        putPotionOverride(overrides, Items.SPLASH_POTION, "long_invisibility", new AspectList()
                .add(Aspect.SENSES, 6)
                .add(Aspect.ALCHEMY, 9)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.PLANT, 4)
                .add(Aspect.ENERGY, 9)
                .add(Aspect.BEAST, 2));
        putPotionOverride(overrides, Items.SPLASH_POTION, "long_night_vision", new AspectList()
                .add(Aspect.SENSES, 5)
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.PLANT, 2)
                .add(Aspect.LIFE, 2)
                .add(Aspect.ENERGY, 9));
        putPotionOverride(overrides, Items.SPLASH_POTION, "long_regeneration", new AspectList()
                .add(Aspect.UNDEAD, 2)
                .add(Aspect.SOUL, 4)
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.PLANT, 1)
                .add(Aspect.FLUX, 1)
                .add(Aspect.ENERGY, 9));
        putPotionOverride(overrides, Items.SPLASH_POTION, "long_slowness", new AspectList()
                .add(Aspect.ENERGY, 9)
                .add(Aspect.ALCHEMY, 9)
                .add(Aspect.BEAST, 3)
                .add(Aspect.PROTECT, 2)
                .add(Aspect.MOTION, 4)
                .add(Aspect.SENSES, 2)
                .add(Aspect.PLANT, 3));
        putPotionOverride(overrides, Items.SPLASH_POTION, "long_strength", new AspectList()
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.FIRE, 2)
                .add(Aspect.ENERGY, 9)
                .add(Aspect.PLANT, 1)
                .add(Aspect.FLUX, 1));
        putPotionOverride(overrides, Items.SPLASH_POTION, "long_weakness", new AspectList()
                .add(Aspect.ALCHEMY, 4)
                .add(Aspect.SENSES, 2)
                .add(Aspect.BEAST, 2)
                .add(Aspect.DEATH, 2)
                .add(Aspect.PLANT, 3)
                .add(Aspect.DARKNESS, 1)
                .add(Aspect.ENERGY, 9));
        putPotionOverride(overrides, Items.SPLASH_POTION, "mundane", new AspectList()
                .add(Aspect.ALCHEMY, 3)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.ENERGY, 5));
        putPotionOverride(overrides, Items.SPLASH_POTION, "strong_leaping", new AspectList()
                .add(Aspect.BEAST, 2)
                .add(Aspect.PROTECT, 2)
                .add(Aspect.MOTION, 4)
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.SENSES, 2)
                .add(Aspect.LIGHT, 4)
                .add(Aspect.ENERGY, 5));
        putPotionOverride(overrides, Items.SPLASH_POTION, "strong_regeneration", new AspectList()
                .add(Aspect.UNDEAD, 2)
                .add(Aspect.SOUL, 4)
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.FLUX, 1)
                .add(Aspect.SENSES, 2)
                .add(Aspect.LIGHT, 4)
                .add(Aspect.ENERGY, 5));
        putPotionOverride(overrides, Items.SPLASH_POTION, "strong_strength", new AspectList()
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.FIRE, 2)
                .add(Aspect.ENERGY, 6)
                .add(Aspect.SENSES, 2)
                .add(Aspect.LIGHT, 4)
                .add(Aspect.PLANT, 1)
                .add(Aspect.FLUX, 1));
        putPotionOverride(overrides, Items.TIPPED_ARROW, "harming", new AspectList()
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.SENSES, 2)
                .add(Aspect.BEAST, 2)
                .add(Aspect.PLANT, 3)
                .add(Aspect.AVERSION, 5));
        putPotionOverride(overrides, Items.TIPPED_ARROW, "invisibility", new AspectList()
                .add(Aspect.SENSES, 6)
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.PLANT, 4)
                .add(Aspect.BEAST, 2)
                .add(Aspect.AVERSION, 5));
        putPotionOverride(overrides, Items.TIPPED_ARROW, "long_invisibility", new AspectList()
                .add(Aspect.SENSES, 6)
                .add(Aspect.ALCHEMY, 9)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.PLANT, 4)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.AVERSION, 5));
        putPotionOverride(overrides, Items.TIPPED_ARROW, "long_night_vision", new AspectList()
                .add(Aspect.SENSES, 5)
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.PLANT, 2)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.AVERSION, 5));
        putPotionOverride(overrides, Items.TIPPED_ARROW, "long_regeneration", new AspectList()
                .add(Aspect.UNDEAD, 2)
                .add(Aspect.SOUL, 4)
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.PLANT, 1)
                .add(Aspect.FLUX, 1)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.AVERSION, 5));
        putPotionOverride(overrides, Items.TIPPED_ARROW, "long_slowness", new AspectList()
                .add(Aspect.ENERGY, 4)
                .add(Aspect.ALCHEMY, 9)
                .add(Aspect.BEAST, 3)
                .add(Aspect.MOTION, 4)
                .add(Aspect.SENSES, 2)
                .add(Aspect.PLANT, 3)
                .add(Aspect.AVERSION, 5));
        putPotionOverride(overrides, Items.TIPPED_ARROW, "long_strength", new AspectList()
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.FIRE, 2)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.PLANT, 1)
                .add(Aspect.FLUX, 1)
                .add(Aspect.AVERSION, 5));
        putPotionOverride(overrides, Items.TIPPED_ARROW, "long_weakness", new AspectList()
                .add(Aspect.ALCHEMY, 4)
                .add(Aspect.SENSES, 2)
                .add(Aspect.BEAST, 2)
                .add(Aspect.DEATH, 2)
                .add(Aspect.PLANT, 3)
                .add(Aspect.ENERGY, 4)
                .add(Aspect.AVERSION, 5));
        putPotionOverride(overrides, Items.TIPPED_ARROW, "mundane", new AspectList()
                .add(Aspect.ALCHEMY, 3)
                .add(Aspect.METAL, 3)
                .add(Aspect.DESIRE, 3)
                .add(Aspect.AVERSION, 5));
        putPotionOverride(overrides, Items.TIPPED_ARROW, "strong_leaping", new AspectList()
                .add(Aspect.BEAST, 2)
                .add(Aspect.PROTECT, 2)
                .add(Aspect.MOTION, 4)
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.SENSES, 2)
                .add(Aspect.LIGHT, 4)
                .add(Aspect.AVERSION, 5));
        putPotionOverride(overrides, Items.TIPPED_ARROW, "strong_regeneration", new AspectList()
                .add(Aspect.UNDEAD, 2)
                .add(Aspect.SOUL, 4)
                .add(Aspect.ALCHEMY, 8)
                .add(Aspect.FLUX, 1)
                .add(Aspect.SENSES, 2)
                .add(Aspect.LIGHT, 4)
                .add(Aspect.AVERSION, 5));
        putPotionOverride(overrides, Items.TIPPED_ARROW, "strong_strength", new AspectList()
                .add(Aspect.ALCHEMY, 6)
                .add(Aspect.FIRE, 2)
                .add(Aspect.SENSES, 2)
                .add(Aspect.LIGHT, 4)
                .add(Aspect.PLANT, 1)
                .add(Aspect.FLUX, 1)
                .add(Aspect.AVERSION, 5));
        return Map.copyOf(overrides);
    }

    private static void putPotionOverride(Map<PotionKey, AspectList> overrides, Item item, String potion, AspectList aspects) {
        overrides.put(new PotionKey(item, potion(potion)), aspects);
    }

    private static Map<ResourceKey<Potion>, PotionMix> createPotionMixes() {
        LinkedHashMap<ResourceKey<Potion>, PotionMix> mixes = new LinkedHashMap<>();
        addMix(mixes, "water", Items.GLOWSTONE_DUST, "thick");
        addMix(mixes, "water", Items.REDSTONE, "mundane");
        addMix(mixes, "water", Items.NETHER_WART, "awkward");
        addStartMix(mixes, Items.BREEZE_ROD, "wind_charged");
        addStartMix(mixes, Items.SLIME_BLOCK, "oozing");
        addStartMix(mixes, Items.STONE, "infested");
        addStartMix(mixes, Items.COBWEB, "weaving");
        addMix(mixes, "awkward", Items.GOLDEN_CARROT, "night_vision");
        addMix(mixes, "night_vision", Items.REDSTONE, "long_night_vision");
        addMix(mixes, "night_vision", Items.FERMENTED_SPIDER_EYE, "invisibility");
        addMix(mixes, "long_night_vision", Items.FERMENTED_SPIDER_EYE, "long_invisibility");
        addMix(mixes, "invisibility", Items.REDSTONE, "long_invisibility");
        addStartMix(mixes, Items.MAGMA_CREAM, "fire_resistance");
        addMix(mixes, "fire_resistance", Items.REDSTONE, "long_fire_resistance");
        addStartMix(mixes, Items.RABBIT_FOOT, "leaping");
        addMix(mixes, "leaping", Items.REDSTONE, "long_leaping");
        addMix(mixes, "leaping", Items.GLOWSTONE_DUST, "strong_leaping");
        addMix(mixes, "leaping", Items.FERMENTED_SPIDER_EYE, "slowness");
        addMix(mixes, "long_leaping", Items.FERMENTED_SPIDER_EYE, "long_slowness");
        addMix(mixes, "slowness", Items.REDSTONE, "long_slowness");
        addMix(mixes, "slowness", Items.GLOWSTONE_DUST, "strong_slowness");
        addMix(mixes, "awkward", Items.TURTLE_HELMET, "turtle_master");
        addMix(mixes, "turtle_master", Items.REDSTONE, "long_turtle_master");
        addMix(mixes, "turtle_master", Items.GLOWSTONE_DUST, "strong_turtle_master");
        addMix(mixes, "swiftness", Items.FERMENTED_SPIDER_EYE, "slowness");
        addMix(mixes, "long_swiftness", Items.FERMENTED_SPIDER_EYE, "long_slowness");
        addStartMix(mixes, Items.SUGAR, "swiftness");
        addMix(mixes, "swiftness", Items.REDSTONE, "long_swiftness");
        addMix(mixes, "swiftness", Items.GLOWSTONE_DUST, "strong_swiftness");
        addMix(mixes, "awkward", Items.PUFFERFISH, "water_breathing");
        addMix(mixes, "water_breathing", Items.REDSTONE, "long_water_breathing");
        addStartMix(mixes, Items.GLISTERING_MELON_SLICE, "healing");
        addMix(mixes, "healing", Items.GLOWSTONE_DUST, "strong_healing");
        addMix(mixes, "healing", Items.FERMENTED_SPIDER_EYE, "harming");
        addMix(mixes, "strong_healing", Items.FERMENTED_SPIDER_EYE, "strong_harming");
        addMix(mixes, "harming", Items.GLOWSTONE_DUST, "strong_harming");
        addMix(mixes, "poison", Items.FERMENTED_SPIDER_EYE, "harming");
        addMix(mixes, "long_poison", Items.FERMENTED_SPIDER_EYE, "harming");
        addMix(mixes, "strong_poison", Items.FERMENTED_SPIDER_EYE, "strong_harming");
        addStartMix(mixes, Items.SPIDER_EYE, "poison");
        addMix(mixes, "poison", Items.REDSTONE, "long_poison");
        addMix(mixes, "poison", Items.GLOWSTONE_DUST, "strong_poison");
        addStartMix(mixes, Items.GHAST_TEAR, "regeneration");
        addMix(mixes, "regeneration", Items.REDSTONE, "long_regeneration");
        addMix(mixes, "regeneration", Items.GLOWSTONE_DUST, "strong_regeneration");
        addStartMix(mixes, Items.BLAZE_POWDER, "strength");
        addMix(mixes, "strength", Items.REDSTONE, "long_strength");
        addMix(mixes, "strength", Items.GLOWSTONE_DUST, "strong_strength");
        addMix(mixes, "water", Items.FERMENTED_SPIDER_EYE, "weakness");
        addMix(mixes, "weakness", Items.REDSTONE, "long_weakness");
        addMix(mixes, "awkward", Items.PHANTOM_MEMBRANE, "slow_falling");
        addMix(mixes, "slow_falling", Items.REDSTONE, "long_slow_falling");
        return mixes;
    }

    private static void addStartMix(Map<ResourceKey<Potion>, PotionMix> mixes, Item reagent, String output) {
        addMix(mixes, "water", reagent, "mundane");
        addMix(mixes, "awkward", reagent, output);
    }

    private static void addMix(Map<ResourceKey<Potion>, PotionMix> mixes, String input, Item reagent, String output) {
        mixes.putIfAbsent(potion(output), new PotionMix(potion(input), reagent));
    }

    private static ResourceKey<Potion> potion(String path) {
        return ResourceKey.create(Registries.POTION, ResourceLocation.withDefaultNamespace(path));
    }

    private record PotionMix(ResourceKey<Potion> input, Item reagent) {
    }

    private record PotionKey(Item item, ResourceKey<Potion> potion) {
    }

    private TCAspectStackRules() {
    }
}
