package thaumcraft.common.items.tools;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.items.components.TCInfusionEnchantmentsComponent;
import thaumcraft.common.registry.TCDataComponents;

public class ItemPrimalCrusher extends DiggerItem implements IWarpingGear {
    public ItemPrimalCrusher() {
        super(
                TCToolTiers.PRIMALVOID,
                BlockTags.MINEABLE_WITH_PICKAXE,
                new Item.Properties()
                        .rarity(Rarity.EPIC)
                        .attributes(PickaxeItem.createAttributes(TCToolTiers.PRIMALVOID, 3.5F, -2.8F))
                        .component(
                                TCDataComponents.INFUSION_ENCHANTS.get(),
                                new TCInfusionEnchantmentsComponent(Map.of(
                                        TCEnumInfusionEnchantment.DESTRUCTIVE, 1,
                                        TCEnumInfusionEnchantment.REFINING, 1
                                ))
                        )
        );
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return TCToolTiers.PRIMALVOID.getSpeed();
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!level.isClientSide() && stack.isDamaged() && entity instanceof LivingEntity && entity.tickCount % 20 == 0) {
            stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1));
        }
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!level.isClientSide() && !TCToolEvents.isHarvesting() && !entityLiving.isShiftKeyDown()
                && (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL))) {
            // 3x3 excavation oriented along the face mined
            Player player = entityLiving instanceof Player p ? p : null;
            Direction face = player != null ? TCToolEvents.getMiningFace(player, pos) : entityLiving.getDirection();

            for (int aa = -1; aa <= 1; aa++) {
                for (int bb = -1; bb <= 1; bb++) {
                    if (aa == 0 && bb == 0) continue;
                    int xx = 0;
                    int yy = 0;
                    int zz = 0;
                    if (face.getAxis() == Direction.Axis.Y) {
                        xx = aa;
                        zz = bb;
                    } else if (face.getAxis() == Direction.Axis.Z) {
                        xx = aa;
                        yy = bb;
                    } else {
                        zz = aa;
                        yy = bb;
                    }
                    BlockPos target = pos.offset(xx, yy, zz);
                    BlockState s = level.getBlockState(target);
                    if (s.getDestroySpeed(level, target) >= 0.0F
                            && (s.is(BlockTags.MINEABLE_WITH_PICKAXE) || s.is(BlockTags.MINEABLE_WITH_SHOVEL))) {
                        TCToolEvents.destroyNeighborBlock(entityLiving, level, target, stack);
                        if (stack.isEmpty()) {
                            break;
                        }
                    }
                }
                if (stack.isEmpty()) {
                    break;
                }
            }
        }
        return super.mineBlock(stack, level, state, pos, entityLiving);
    }

    @Override
    public int getWarp(ItemStack stack, Player player) {
        return 2;
    }
}
