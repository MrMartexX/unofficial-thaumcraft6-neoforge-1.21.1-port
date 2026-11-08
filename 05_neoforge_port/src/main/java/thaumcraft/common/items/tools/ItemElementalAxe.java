package thaumcraft.common.items.tools;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.items.components.TCInfusionEnchantmentsComponent;
import thaumcraft.common.registry.TCDataComponents;

public class ItemElementalAxe extends AxeItem {
    public ItemElementalAxe() {
        super(
                TCToolTiers.ELEMENTAL,
                new Item.Properties()
                        .rarity(Rarity.RARE)
                        .attributes(AxeItem.createAttributes(TCToolTiers.ELEMENTAL, 5.0F, -3.0F))
                        .component(
                                TCDataComponents.INFUSION_ENCHANTS.get(),
                                new TCInfusionEnchantmentsComponent(Map.of(
                                        TCEnumInfusionEnchantment.BURROWING, 1,
                                        TCEnumInfusionEnchantment.COLLECTOR, 1
                                ))
                        )
        );
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        if (livingEntity instanceof Player player) {
            AABB box = player.getBoundingBox().inflate(10.0D);
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box);
            for (ItemEntity item : items) {
                if (item.isAlive()) {
                    Vec3 toPlayer = player.position().add(0, player.getBbHeight() / 2.0D, 0).subtract(item.position());
                    double dist = toPlayer.length();
                    if (dist > 0.5D) {
                        Vec3 pull = toPlayer.normalize().scale(0.35D);
                        item.setDeltaMovement(item.getDeltaMovement().scale(0.8D).add(pull));
                    }
                }
            }
        }
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!level.isClientSide() && !TCToolEvents.isHarvesting() && !entityLiving.isShiftKeyDown() && state.is(BlockTags.LOGS)) {
            // Tree felling: recursively destroy adjacent connected log blocks up to 64
            fellTree(level, pos, stack, entityLiving);
        }
        return super.mineBlock(stack, level, state, pos, entityLiving);
    }

    private void fellTree(Level level, BlockPos startPos, ItemStack stack, LivingEntity entity) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(startPos);
        visited.add(startPos);

        int count = 0;
        while (!queue.isEmpty() && count < 64 && !stack.isEmpty()) {
            BlockPos current = queue.poll();
            for (BlockPos neighbor : BlockPos.betweenClosed(current.offset(-1, -1, -1), current.offset(1, 1, 1))) {
                BlockPos immutable = neighbor.immutable();
                if (!visited.contains(immutable)) {
                    visited.add(immutable);
                    BlockState s = level.getBlockState(immutable);
                    if (s.is(BlockTags.LOGS)) {
                        boolean destroyed = TCToolEvents.destroyNeighborBlock(entity, level, immutable, stack);
                        if (destroyed) {
                            count++;
                            queue.add(immutable);
                        }
                        if (stack.isEmpty()) {
                            break;
                        }
                    }
                }
            }
        }
    }
}
