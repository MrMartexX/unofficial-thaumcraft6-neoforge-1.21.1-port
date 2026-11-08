package thaumcraft.common.blocks.basic;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.common.lib.fx.TCFXDispatcher;

public class TCPavingStoneBlock extends Block {
    public enum Kind {
        TRAVEL,
        BARRIER
    }

    private static final VoxelShape BASE_SHAPE = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D);
    private static final VoxelShape BARRIER_COLLISION_SHAPE = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 3.0D, 1.0D);

    private final Kind kind;

    public TCPavingStoneBlock(Kind kind, BlockBehaviour.Properties properties) {
        super(properties);
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BASE_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (kind == Kind.BARRIER) {
            boolean powered = false;
            if (level instanceof SignalGetter sg) {
                powered = sg.hasNeighborSignal(pos);
            }
            if (!powered && context instanceof EntityCollisionContext ecc) {
                Entity entity = ecc.getEntity();
                if (entity != null) {
                    boolean isPlayerOrCarriedByPlayer = entity instanceof Player
                            || entity.getPassengers().stream().anyMatch(p -> p instanceof Player);
                    if (!isPlayerOrCarriedByPlayer) {
                        return BARRIER_COLLISION_SHAPE;
                    }
                }
            }
        }
        return BASE_SHAPE;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (kind == Kind.TRAVEL) {
            if (!level.isClientSide() && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false));
                living.addEffect(new MobEffectInstance(MobEffects.JUMP, 40, 0, false, false));
            }
        } else if (kind == Kind.BARRIER) {
            if (!level.isClientSide() && !level.hasNeighborSignal(pos)) {
                if (entity instanceof LivingEntity living && !(living instanceof Player)) {
                    boolean passengerIsPlayer = living.getPassengers().stream().anyMatch(p -> p instanceof Player);
                    if (!passengerIsPlayer) {
                        float yawRad = (living.getYRot() + 180.0F) * (float) (Math.PI / 180.0D);
                        living.setDeltaMovement(
                                -Mth.sin(yawRad) * 0.2F,
                                -0.1D,
                                Mth.cos(yawRad) * 0.2F
                        );
                        living.hurtMarked = true;
                    }
                }
            }
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (kind == Kind.BARRIER) {
            if (level.hasNeighborSignal(pos)) {
                for (int a = 0; a < 4; ++a) {
                    TCFXDispatcher.blockRunes(level, pos.getX(), pos.getY() + 0.7D, pos.getZ(),
                            0.2F + random.nextFloat() * 0.4F, random.nextFloat() * 0.3F, 0.8F + random.nextFloat() * 0.2F, 20, -0.02F);
                }
            } else {
                AABB check = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(1.0D);
                List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, check, e -> !(e instanceof Player));
                if (!list.isEmpty()) {
                    for (LivingEntity entity : list) {
                        TCFXDispatcher.blockRunes(level, pos.getX(), pos.getY() + 0.6D + random.nextFloat() * Math.max(0.8F, entity.getEyeHeight()), pos.getZ(),
                                0.6F + random.nextFloat() * 0.4F, 0.0F, 0.3F + random.nextFloat() * 0.7F, 20, 0.0F);
                        break;
                    }
                }
            }
        }
    }
}
