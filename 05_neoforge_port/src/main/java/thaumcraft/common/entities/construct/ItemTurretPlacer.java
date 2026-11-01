package thaumcraft.common.entities.construct;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thaumcraft.common.registry.TCEntityTypes;

public class ItemTurretPlacer extends Item {
    private final boolean advanced;

    public ItemTurretPlacer(boolean advanced, Properties properties) {
        super(properties);
        this.advanced = advanced;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction side = context.getClickedFace();
        if (side == Direction.DOWN) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        BlockPos placePos = clickedState.canBeReplaced() ? clickedPos : clickedPos.relative(side);
        BlockPos abovePos = placePos.above();

        Player player = context.getPlayer();
        if (player != null && !player.mayUseItemAt(placePos, side, context.getItemInHand())) {
            return InteractionResult.PASS;
        }

        BlockState placeState = level.getBlockState(placePos);
        BlockState aboveState = level.getBlockState(abovePos);
        if (!placeState.canBeReplaced() || !aboveState.canBeReplaced()) {
            return InteractionResult.PASS;
        }

        AABB box = new AABB(placePos.getX(), placePos.getY(), placePos.getZ(),
                placePos.getX() + 1.0D, placePos.getY() + 2.0D, placePos.getZ() + 1.0D);
        List<Entity> colliding = level.getEntities((Entity) null, box);
        if (!colliding.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            TCOwnedConstructEntity turret = advanced
                    ? new TCTurretCrossbowAdvancedEntity(TCEntityTypes.TURRET_ADVANCED.get(), level)
                    : new TCTurretCrossbowEntity(TCEntityTypes.TURRET_BASIC.get(), level);

            turret.moveTo(placePos.getX() + 0.5D, placePos.getY(), placePos.getZ() + 0.5D,
                    player != null ? player.getYRot() : 0.0F, 0.0F);
            turret.setValidSpawn(true);
            turret.setOwned(true);
            if (player != null) {
                turret.setOwnerUUID(player.getUUID());
            }

            level.addFreshEntity(turret);
            level.playSound(null, turret.getX(), turret.getY(), turret.getZ(),
                    SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);

            context.getItemInHand().shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
