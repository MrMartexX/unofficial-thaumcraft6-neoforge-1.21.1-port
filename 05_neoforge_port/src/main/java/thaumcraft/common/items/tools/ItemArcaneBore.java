package thaumcraft.common.items.tools;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thaumcraft.common.entities.TCArcaneBoreEntity;

public final class ItemArcaneBore extends Item {
    public ItemArcaneBore() {
        super(new Item.Properties().stacksTo(16));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction side = context.getClickedFace();
        if (side == Direction.DOWN) {
            return InteractionResult.PASS;
        }
        Level level = context.getLevel();
        BlockPos clicked = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clicked);
        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        BlockPos place = clickedState.canBeReplaced(placeContext) ? clicked : clicked.relative(side);
        BlockPos top = place.above();
        if (!level.getBlockState(place).canBeReplaced(placeContext) || !level.getBlockState(top).canBeReplaced(placeContext)) {
            return InteractionResult.PASS;
        }
        List<Entity> collisions = level.getEntities(null, new AABB(place).expandTowards(0.0D, 1.0D, 0.0D));
        if (!collisions.isEmpty()) {
            return InteractionResult.PASS;
        }
        Player player = context.getPlayer();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }
        Direction facing = player == null ? Direction.NORTH : player.getDirection();
        TCArcaneBoreEntity bore = new TCArcaneBoreEntity(serverLevel, place, facing, player == null ? null : player.getUUID());
        serverLevel.addFreshEntity(bore);
        serverLevel.playSound(null, bore.getX(), bore.getY(), bore.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
        if (player == null || !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}
