package thaumcraft.common.items.tools;

import java.util.ArrayList;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.items.IArchitect;
import thaumcraft.common.items.components.TCInfusionEnchantmentsComponent;
import thaumcraft.common.registry.TCDataComponents;

public class ItemElementalShovel extends ShovelItem implements IArchitect {
    public ItemElementalShovel() {
        super(
                TCToolTiers.ELEMENTAL,
                new Item.Properties()
                        .rarity(Rarity.RARE)
                        .attributes(ShovelItem.createAttributes(TCToolTiers.ELEMENTAL, 1.5F, -3.0F))
                        .component(
                                TCDataComponents.INFUSION_ENCHANTS.get(),
                                new TCInfusionEnchantmentsComponent(Map.of(
                                        TCEnumInfusionEnchantment.DESTRUCTIVE, 1
                                ))
                        )
        );
    }

    public static int getOrientation(ItemStack stack) {
        return stack.getOrDefault(TCDataComponents.ARCHITECT_ORIENTATION.get(), 0);
    }

    public static void setOrientation(ItemStack stack, int orientation) {
        stack.set(TCDataComponents.ARCHITECT_ORIENTATION.get(), Math.floorMod(orientation, 3));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            int current = getOrientation(stack);
            int next = (current + 1) % 3;
            setOrientation(stack, next);
            if (!level.isClientSide()) {
                player.displayClientMessage(Component.translatable("tc.shovel.orientation." + next), true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        Direction side = context.getClickedFace();
        ItemStack held = context.getItemInHand();

        if (player != null && !player.isShiftKeyDown()) {
            BlockState targetState = level.getBlockState(pos);
            Item sourceItem = targetState.getBlock().asItem();
            boolean isGrass = targetState.is(Blocks.GRASS_BLOCK);

            ArrayList<BlockPos> targets = getArchitectBlocks(held, level, pos, side, player);
            boolean placedAny = false;

            for (BlockPos target : targets) {
                if (level.getBlockState(target).canBeReplaced() && targetState.canSurvive(level, target)) {
                    boolean consumed = player.getAbilities().instabuild;
                    if (!consumed) {
                        if (isGrass && consumeItem(player, Items.DIRT)) {
                            consumed = true;
                        } else if (sourceItem != Items.AIR && consumeItem(player, sourceItem)) {
                            consumed = true;
                        }
                    }

                    if (consumed) {
                        BlockState toPlace = isGrass ? Blocks.DIRT.defaultBlockState() : targetState;
                        level.setBlock(target, toPlace, 3);
                        level.playSound(null, target, toPlace.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 0.6F, 1.0F);
                        placedAny = true;
                        if (!player.getAbilities().instabuild) {
                            held.hurtAndBreak(1, player, Player.getSlotForHand(context.getHand()));
                            if (held.isEmpty()) {
                                break;
                            }
                        }
                    }
                }
            }

            if (placedAny) {
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return super.useOn(context);
    }

    private static boolean consumeItem(Player player, Item item) {
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack s = player.getInventory().items.get(i);
            if (!s.isEmpty() && s.is(item)) {
                s.shrink(1);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!level.isClientSide() && !TCToolEvents.isHarvesting() && !entityLiving.isShiftKeyDown() && state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            // Dig 3x3 earth oriented according to face mined
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
                    if (s.getDestroySpeed(level, target) >= 0.0F && s.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
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
    public ArrayList<BlockPos> getArchitectBlocks(ItemStack stack, Level world, BlockPos pos, Direction side, Player player) {
        ArrayList<BlockPos> list = new ArrayList<>();
        int orientation = getOrientation(stack);

        for (int aa = -1; aa <= 1; aa++) {
            for (int bb = -1; bb <= 1; bb++) {
                int xx = 0;
                int yy = 0;
                int zz = 0;
                if (orientation == 1) {
                    yy = bb;
                    if (side.getAxis() == Direction.Axis.Y) {
                        int facing = Mth.floor(player.getYRot() * 4.0F / 360.0F + 0.5D) & 3;
                        if (facing == 0 || facing == 2) {
                            xx = aa;
                        } else {
                            zz = aa;
                        }
                    } else if (side.getAxis() == Direction.Axis.Z) {
                        zz = aa;
                    } else {
                        xx = aa;
                    }
                } else if (orientation == 2) {
                    if (side.getAxis() == Direction.Axis.Y) {
                        int facing = Mth.floor(player.getYRot() * 4.0F / 360.0F + 0.5D) & 3;
                        yy = bb;
                        if (facing == 0 || facing == 2) {
                            xx = aa;
                        } else {
                            zz = aa;
                        }
                    } else {
                        zz = bb;
                        xx = aa;
                    }
                } else {
                    if (side.getAxis() == Direction.Axis.Y) {
                        xx = aa;
                        zz = bb;
                    } else if (side.getAxis() == Direction.Axis.Z) {
                        xx = aa;
                        yy = bb;
                    } else {
                        zz = aa;
                        yy = bb;
                    }
                }
                list.add(pos.relative(side).offset(xx, yy, zz));
            }
        }
        return list;
    }

    @Override
    public HitResult getArchitectMOP(ItemStack stack, Level world, LivingEntity player) {
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 view = player.getViewVector(1.0F);
        Vec3 reach = eye.add(view.scale(5.0D));
        return world.clip(new ClipContext(eye, reach, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }

    @Override
    public boolean useBlockHighlight(ItemStack stack) {
        return true;
    }

    @Override
    public boolean showAxis(ItemStack stack, Level world, Player player, Direction side, EnumAxis axis) {
        return false;
    }
}
