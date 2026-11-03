package thaumcraft.common.golems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.registry.TCSounds;

public class ItemGolemBell extends Item implements ISealDisplayer {

    public ItemGolemBell(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        Player player = context.getPlayer();

        if (player != null) {
            player.swing(context.getHand());
        }

        if (!level.isClientSide) {
            ISealEntity seal = SealHandler.getSealEntity(level, new SealPos(pos, face));
            if (seal != null) {
                if (player == null || !player.getStringUUID().equals(seal.getOwner())) {
                    return InteractionResult.FAIL;
                }
                if (player != null && player.isShiftKeyDown()) {
                    SealHandler.removeSealEntity(level, seal.getSealPos(), false);
                    level.playSound(null, pos, TCSounds.ZAP.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
                } else if (player != null) {
                    SealHandler.openSealGui(player, seal);
                }
                return InteractionResult.SUCCESS;
            }
        } else {
            level.playSound(player, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6F, 1.0F);
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.swing(hand);
        if (level.isClientSide) {
            level.playSound(player, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6F, 1.1F);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}
