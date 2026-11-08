package thaumcraft.common.items.curios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import thaumcraft.common.items.components.TCLegacyItemComponent;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCItems;

public class TCLootBagItem extends Item {
    public enum Tier {
        COMMON("common", 0, Rarity.COMMON),
        UNCOMMON("uncommon", 1, Rarity.UNCOMMON),
        RARE("rare", 2, Rarity.RARE);

        public final String name;
        public final int meta;
        public final Rarity rarity;

        Tier(String name, int meta, Rarity rarity) {
            this.name = name;
            this.meta = meta;
            this.rarity = rarity;
        }
    }

    private final Tier tier;

    public TCLootBagItem(Tier tier) {
        super(new Item.Properties()
                .stacksTo(16)
                .rarity(tier.rarity)
                .component(TCDataComponents.LEGACY_ITEM.get(), new TCLegacyItemComponent("loot_bag", tier.name, tier.meta)));
        this.tier = tier;
    }

    public Tier getTier() {
        return this.tier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide()) {
            int count = 8 + level.random.nextInt(5);
            for (int i = 0; i < count; i++) {
                ItemStack loot = generateLoot(level, this.tier);
                if (!loot.isEmpty()) {
                    ItemEntity entity = new ItemEntity(level, player.getX(), player.getY() + 0.5D, player.getZ(), loot.copy());
                    entity.setPickUpDelay(0);
                    level.addFreshEntity(entity);
                }
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.75F, 1.0F);
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tc.lootbag").withStyle(ChatFormatting.GRAY));
    }

    private static record WeightedEntry(java.util.function.Supplier<ItemStack> supplier, int weight) {
    }

    public static ItemStack generateLoot(Level level, Tier tier) {
        RandomSource rand = level.random;

        // Legacy: chance to roll gear for uncommon and rare
        if (tier != Tier.COMMON && rand.nextFloat() < 0.025F * tier.meta) {
            ItemStack gear = genGear(tier, rand, level);
            if (!gear.isEmpty()) {
                return gear;
            }
        }

        List<WeightedEntry> pool = switch (tier) {
            case COMMON -> COMMON_POOL;
            case UNCOMMON -> UNCOMMON_POOL;
            case RARE -> RARE_POOL;
        };

        int totalWeight = 0;
        for (WeightedEntry entry : pool) {
            totalWeight += entry.weight;
        }

        ItemStack result = ItemStack.EMPTY;
        if (totalWeight > 0) {
            int target = rand.nextInt(totalWeight);
            int running = 0;
            for (WeightedEntry entry : pool) {
                running += entry.weight;
                if (target < running) {
                    result = entry.supplier.get();
                    break;
                }
            }
        }

        if (result.is(Items.BOOK)) {
            int enchantLevel = (int) (5.0F + tier.meta * 0.75F * rand.nextInt(18));
            result = EnchantmentHelper.enchantItem(rand, result, enchantLevel, level.registryAccess(), Optional.empty());
        }

        return result;
    }

    private static ItemStack genGear(Tier tier, RandomSource rand, Level level) {
        int quality = rand.nextInt(2);
        if (rand.nextFloat() < 0.20F) quality++;
        if (rand.nextFloat() < 0.15F) quality++;
        if (rand.nextFloat() < 0.10F) quality++;
        if (rand.nextFloat() < 0.095F) quality++;
        if (rand.nextFloat() < 0.095F) quality++;

        Item item = getGearItemForSlot(rand.nextInt(5), quality);
        if (item != null) {
            ItemStack gear = new ItemStack(item);
            int maxDamage = gear.getMaxDamage();
            if (maxDamage > 0) {
                gear.setDamageValue(rand.nextInt(1 + maxDamage / 6));
            }
            if (rand.nextInt(4) < tier.meta) {
                int enchantLevel = (int) (5.0F + tier.meta * 0.75F * rand.nextInt(18));
                gear = EnchantmentHelper.enchantItem(rand, gear, enchantLevel, level.registryAccess(), Optional.empty());
            }
            return gear;
        }
        return ItemStack.EMPTY;
    }

    private static Item getGearItemForSlot(int slot, int quality) {
        return switch (slot) {
            case 4 -> switch (quality) {
                case 0 -> Items.LEATHER_HELMET;
                case 1 -> Items.GOLDEN_HELMET;
                case 2 -> Items.CHAINMAIL_HELMET;
                case 3 -> Items.IRON_HELMET;
                case 4 -> TCItems.THAUMIUM_HELM.get();
                case 5 -> Items.DIAMOND_HELMET;
                default -> TCItems.VOID_HELM.get();
            };
            case 3 -> switch (quality) {
                case 0 -> Items.LEATHER_CHESTPLATE;
                case 1 -> Items.GOLDEN_CHESTPLATE;
                case 2 -> Items.CHAINMAIL_CHESTPLATE;
                case 3 -> Items.IRON_CHESTPLATE;
                case 4 -> TCItems.THAUMIUM_CHEST.get();
                case 5 -> Items.DIAMOND_CHESTPLATE;
                default -> TCItems.VOID_CHEST.get();
            };
            case 2 -> switch (quality) {
                case 0 -> Items.LEATHER_LEGGINGS;
                case 1 -> Items.GOLDEN_LEGGINGS;
                case 2 -> Items.CHAINMAIL_LEGGINGS;
                case 3 -> Items.IRON_LEGGINGS;
                case 4 -> TCItems.THAUMIUM_LEGS.get();
                case 5 -> Items.DIAMOND_LEGGINGS;
                default -> TCItems.VOID_LEGS.get();
            };
            case 1 -> switch (quality) {
                case 0 -> Items.LEATHER_BOOTS;
                case 1 -> Items.GOLDEN_BOOTS;
                case 2 -> Items.CHAINMAIL_BOOTS;
                case 3 -> Items.IRON_BOOTS;
                case 4 -> TCItems.THAUMIUM_BOOTS.get();
                case 5 -> Items.DIAMOND_BOOTS;
                default -> TCItems.VOID_BOOTS.get();
            };
            default -> switch (quality) {
                case 0 -> Items.IRON_AXE;
                case 1 -> Items.IRON_SWORD;
                case 2 -> Items.GOLDEN_AXE;
                case 3 -> Items.GOLDEN_SWORD;
                case 4 -> TCItems.THAUMIUM_SWORD.get();
                case 5 -> Items.DIAMOND_SWORD;
                default -> TCItems.VOID_SWORD.get();
            };
        };
    }

    private static ItemStack createPearlWithDamage(int damage) {
        ItemStack pearl = new ItemStack(TCItems.PRIMORDIAL_PEARL.get());
        pearl.setDamageValue(Math.min(7, Math.max(0, damage)));
        return pearl;
    }

    private static final List<WeightedEntry> COMMON_POOL = List.of(
            new WeightedEntry(() -> new ItemStack(Items.GOLD_INGOT), 100),
            new WeightedEntry(() -> new ItemStack(Items.ENDER_PEARL), 100),
            new WeightedEntry(() -> new ItemStack(Items.EMERALD), 15),
            new WeightedEntry(() -> new ItemStack(Items.DIAMOND), 10),
            new WeightedEntry(() -> new ItemStack(TCItems.SALIS_MUNDUS.get()), 3),
            new WeightedEntry(() -> new ItemStack(Items.CHORUS_FRUIT), 5),
            new WeightedEntry(() -> new ItemStack(Items.COMPASS), 5),
            new WeightedEntry(() -> new ItemStack(Items.COOKIE), 5),
            new WeightedEntry(() -> new ItemStack(TCItems.AMBER.get()), 30),
            new WeightedEntry(() -> new ItemStack(TCItems.BRASS_INGOT.get()), 20),
            new WeightedEntry(() -> new ItemStack(TCItems.QUICKSILVER.get()), 20),
            new WeightedEntry(() -> new ItemStack(TCItems.TALLOW.get()), 20),
            new WeightedEntry(() -> new ItemStack(TCItems.CRYSTAL_AER.get()), 15),
            new WeightedEntry(() -> new ItemStack(TCItems.CRYSTAL_IGNIS.get()), 15),
            new WeightedEntry(() -> new ItemStack(TCItems.CRYSTAL_AQUA.get()), 15),
            new WeightedEntry(() -> new ItemStack(TCItems.CRYSTAL_TERRA.get()), 15),
            new WeightedEntry(() -> new ItemStack(TCItems.CRYSTAL_ORDO.get()), 15),
            new WeightedEntry(() -> new ItemStack(TCItems.CRYSTAL_PERDITIO.get()), 15),
            new WeightedEntry(() -> new ItemStack(TCItems.BAUBLE_RING.get()), 10),
            new WeightedEntry(() -> new ItemStack(TCItems.BAUBLE_AMULET.get()), 10),
            new WeightedEntry(() -> new ItemStack(TCItems.BAUBLE_GIRDLE.get()), 10),
            new WeightedEntry(() -> new ItemStack(Items.EXPERIENCE_BOTTLE), 5),
            new WeightedEntry(() -> new ItemStack(Items.GOLDEN_APPLE), 3),
            new WeightedEntry(() -> new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), 1),
            new WeightedEntry(() -> new ItemStack(Items.BOOK), 10),
            new WeightedEntry(() -> createPearlWithDamage(7), 1) // 1 use left
    );

    private static final List<WeightedEntry> UNCOMMON_POOL = List.of(
            new WeightedEntry(() -> new ItemStack(Items.GOLD_INGOT), 100),
            new WeightedEntry(() -> new ItemStack(Items.ENDER_PEARL), 100),
            new WeightedEntry(() -> new ItemStack(Items.EMERALD), 75),
            new WeightedEntry(() -> new ItemStack(Items.DIAMOND), 50),
            new WeightedEntry(() -> new ItemStack(TCItems.SALIS_MUNDUS.get()), 6),
            new WeightedEntry(() -> new ItemStack(Items.CHORUS_FRUIT), 5),
            new WeightedEntry(() -> new ItemStack(Items.COMPASS), 5),
            new WeightedEntry(() -> new ItemStack(Items.COOKIE), 5),
            new WeightedEntry(() -> new ItemStack(TCItems.AMBER.get()), 20),
            new WeightedEntry(() -> new ItemStack(TCItems.BRASS_INGOT.get()), 30),
            new WeightedEntry(() -> new ItemStack(TCItems.THAUMIUM_INGOT.get()), 15),
            new WeightedEntry(() -> new ItemStack(TCItems.QUICKSILVER.get()), 25),
            new WeightedEntry(() -> new ItemStack(TCItems.ALUMENTUM.get()), 15),
            new WeightedEntry(() -> new ItemStack(TCItems.PHIAL.get()), 20),
            new WeightedEntry(() -> new ItemStack(TCItems.CRYSTAL_AER.get()), 25),
            new WeightedEntry(() -> new ItemStack(TCItems.CRYSTAL_IGNIS.get()), 25),
            new WeightedEntry(() -> new ItemStack(TCItems.CRYSTAL_AQUA.get()), 25),
            new WeightedEntry(() -> new ItemStack(TCItems.CRYSTAL_TERRA.get()), 25),
            new WeightedEntry(() -> new ItemStack(TCItems.CRYSTAL_ORDO.get()), 25),
            new WeightedEntry(() -> new ItemStack(TCItems.CRYSTAL_PERDITIO.get()), 25),
            new WeightedEntry(() -> new ItemStack(TCItems.VIS_AMULET.get()), 6),
            new WeightedEntry(() -> new ItemStack(TCItems.CLOUD_RING.get()), 5),
            new WeightedEntry(() -> new ItemStack(Items.EXPERIENCE_BOTTLE), 10),
            new WeightedEntry(() -> new ItemStack(Items.GOLDEN_APPLE), 6),
            new WeightedEntry(() -> new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), 2),
            new WeightedEntry(() -> new ItemStack(Items.BOOK), 10),
            new WeightedEntry(() -> createPearlWithDamage(7), 3), // 1 use left
            new WeightedEntry(() -> createPearlWithDamage(6), 1)  // 2 uses left
    );

    private static final List<WeightedEntry> RARE_POOL = List.of(
            new WeightedEntry(() -> new ItemStack(Items.GOLD_INGOT), 100),
            new WeightedEntry(() -> new ItemStack(Items.ENDER_PEARL), 100),
            new WeightedEntry(() -> new ItemStack(Items.EMERALD), 75),
            new WeightedEntry(() -> new ItemStack(Items.DIAMOND), 50),
            new WeightedEntry(() -> new ItemStack(TCItems.SALIS_MUNDUS.get()), 9),
            new WeightedEntry(() -> new ItemStack(Items.CHORUS_FRUIT), 5),
            new WeightedEntry(() -> new ItemStack(Items.COMPASS), 5),
            new WeightedEntry(() -> new ItemStack(Items.COOKIE), 5),
            new WeightedEntry(() -> new ItemStack(TCItems.THAUMIUM_INGOT.get()), 30),
            new WeightedEntry(() -> new ItemStack(TCItems.VOID_SEED.get()), 20),
            new WeightedEntry(() -> new ItemStack(TCItems.VOID_INGOT.get()), 15),
            new WeightedEntry(() -> new ItemStack(TCItems.CURIO_ARCANE.get()), 15),
            new WeightedEntry(() -> new ItemStack(TCItems.CURIO_PRESERVED.get()), 15),
            new WeightedEntry(() -> new ItemStack(TCItems.CURIO_KNOWLEDGE.get()), 15),
            new WeightedEntry(() -> new ItemStack(TCItems.VIS_AMULET.get()), 6),
            new WeightedEntry(() -> new ItemStack(TCItems.CLOUD_RING.get()), 5),
            new WeightedEntry(() -> new ItemStack(Items.EXPERIENCE_BOTTLE), 20),
            new WeightedEntry(() -> new ItemStack(Items.GOLDEN_APPLE), 9),
            new WeightedEntry(() -> new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), 3),
            new WeightedEntry(() -> new ItemStack(Items.NETHER_STAR), 1),
            new WeightedEntry(() -> new ItemStack(Items.BOOK), 10),
            new WeightedEntry(() -> createPearlWithDamage(5), 9), // 3 uses left
            new WeightedEntry(() -> createPearlWithDamage(3), 3), // 5 uses left
            new WeightedEntry(() -> createPearlWithDamage(0), 1)  // full 8 uses
    );
}
