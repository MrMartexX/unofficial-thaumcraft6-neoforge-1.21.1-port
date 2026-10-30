package thaumcraft.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ItemElementalPickaxe extends PickaxeItem {
    public ItemElementalPickaxe() {
        super(
                TCToolTiers.ELEMENTAL,
                new Item.Properties()
                        .rarity(Rarity.RARE)
                        .attributes(PickaxeItem.createAttributes(TCToolTiers.ELEMENTAL, 1.0F, -2.8F))
        );
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (!level.isClientSide() && player != null) {
            // Pickaxe of the Core: sonar detection for nearby valuable ores within 16 blocks
            BlockPos nearestOre = null;
            double nearestDistSq = Double.MAX_VALUE;

            for (BlockPos p : BlockPos.betweenClosed(pos.offset(-8, -8, -8), pos.offset(8, 8, 8))) {
                BlockState s = level.getBlockState(p);
                if (s.is(BlockTags.GOLD_ORES) || s.is(BlockTags.DIAMOND_ORES) || s.is(BlockTags.EMERALD_ORES) || s.is(BlockTags.LAPIS_ORES) || s.is(BlockTags.REDSTONE_ORES)) {
                    double d = p.distSqr(pos);
                    if (d < nearestDistSq) {
                        nearestDistSq = d;
                        nearestOre = p.immutable();
                    }
                }
            }

            if (nearestOre != null) {
                level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.2F);
                player.displayClientMessage(Component.translatable("tc.corepick.found",
                        (int) Math.sqrt(nearestDistSq)), true);
            } else {
                level.playSound(null, pos, SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.5F, 1.5F);
            }
            player.getCooldowns().addCooldown(this, 15);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
