package thaumcraft.common.items.curios;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
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
                    ItemEntity entity = new ItemEntity(level, player.getX(), player.getY() + 0.5D, player.getZ(), loot);
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

    private static ItemStack generateLoot(Level level, Tier tier) {
        int roll = level.random.nextInt(100);
        return switch (tier) {
            case COMMON -> {
                if (roll < 20) yield new ItemStack(Items.IRON_NUGGET, 1 + level.random.nextInt(4));
                if (roll < 35) yield new ItemStack(Items.GOLD_NUGGET, 1 + level.random.nextInt(3));
                if (roll < 50) yield new ItemStack(TCItems.BRASS_NUGGET.get(), 1 + level.random.nextInt(3));
                if (roll < 65) yield new ItemStack(TCItems.AMBER.get(), 1);
                if (roll < 80) yield new ItemStack(TCItems.SALIS_MUNDUS.get(), 1);
                if (roll < 90) yield new ItemStack(TCItems.TALLOW.get(), 1 + level.random.nextInt(2));
                yield new ItemStack(TCItems.CRYSTAL_AER.get(), 1);
            }
            case UNCOMMON -> {
                if (roll < 20) yield new ItemStack(TCItems.THAUMIUM_NUGGET.get(), 1 + level.random.nextInt(3));
                if (roll < 35) yield new ItemStack(TCItems.QUICKSILVER.get(), 1 + level.random.nextInt(2));
                if (roll < 50) yield new ItemStack(TCItems.ALUMENTUM.get(), 1);
                if (roll < 65) yield new ItemStack(TCItems.PHIAL.get(), 1 + level.random.nextInt(2));
                if (roll < 80) yield new ItemStack(TCItems.CRYSTAL_IGNIS.get(), 1 + level.random.nextInt(2));
                if (roll < 90) yield new ItemStack(TCItems.AMBER.get(), 2);
                yield new ItemStack(TCItems.BRASS_INGOT.get(), 1);
            }
            case RARE -> {
                if (roll < 20) yield new ItemStack(TCItems.VOID_NUGGET.get(), 1 + level.random.nextInt(2));
                if (roll < 35) yield new ItemStack(TCItems.VOID_SEED.get(), 1);
                if (roll < 50) yield new ItemStack(TCItems.THAUMIUM_INGOT.get(), 1);
                if (roll < 65) yield new ItemStack(TCItems.CURIO_ARCANE.get(), 1);
                if (roll < 80) yield new ItemStack(TCItems.CURIO_PRESERVED.get(), 1);
                if (roll < 90) yield new ItemStack(TCItems.CURIO_KNOWLEDGE.get(), 1);
                yield new ItemStack(TCItems.PRIMORDIAL_PEARL.get(), 1);
            }
        };
    }
}
