package thaumcraft.common.blocks.basic;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.TCEssentiaItemHelper;
import thaumcraft.common.tiles.misc.TCBannerBlockEntity;

/** Port of legacy BlockBannerTCItem: stand-on-top or hang-on-wall placement and aspect tooltip. */
public class TCBannerBlockItem extends BlockItem {
    public TCBannerBlockItem(TCBannerBlock block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public String getDescriptionId() {
        return "item." + BuiltInRegistries.ITEM.getKey(this).toLanguageKey();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Aspect aspect = TCEssentiaItemHelper.filterAspect(stack);
        if (aspect != null) {
            tooltip.add(Component.literal(aspect.getName()));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction side = context.getClickedFace();
        Level level = context.getLevel();
        BlockPos clicked = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (side == Direction.DOWN || !level.getBlockState(clicked).isFaceSturdy(level, clicked, side)) {
            return InteractionResult.FAIL;
        }
        BlockPos pos = clicked.relative(side);
        if (!level.getBlockState(pos).canBeReplaced() || (player != null && !player.mayUseItemAt(pos, side, stack))) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        level.setBlock(pos, getBlock().defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof TCBannerBlockEntity banner) {
            if (side == Direction.UP) {
                float yaw = player == null ? 0.0F : player.getYRot();
                banner.setBannerFacing((byte) (Mth.floor((yaw + 180.0F) * 16.0F / 360.0F + 0.5D) & 15));
            } else {
                banner.setWall(true);
                int facing = switch (side) {
                    case NORTH -> 8;
                    case WEST -> 4;
                    case EAST -> 12;
                    default -> 0;
                };
                banner.setBannerFacing((byte) facing);
            }
            Aspect aspect = TCEssentiaItemHelper.filterAspect(stack);
            if (aspect != null) {
                banner.setAspect(aspect);
            }
        }
        level.playSound(null, pos, getBlock().defaultBlockState().getSoundType().getPlaceSound(),
                SoundSource.BLOCKS, 1.0F, 1.0F);
        if (player == null || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
