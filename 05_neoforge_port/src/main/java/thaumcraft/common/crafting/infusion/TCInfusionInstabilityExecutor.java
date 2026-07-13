package thaumcraft.common.crafting.infusion;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.config.TCConfig;
import thaumcraft.common.research.TCPlayerKnowledge;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.tiles.crafting.TCInfusionMatrixBlockEntity;
import thaumcraft.common.tiles.crafting.TCInfusionPedestalBlockEntity;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCMobEffects;
import thaumcraft.common.tiles.devices.TCStabilizerBlockEntity;
import thaumcraft.common.warp.TCWarpManager;
import thaumcraft.common.warp.TCWarpType;

/** Reviewed server effects for legacy infusion instability events. */
public final class TCInfusionInstabilityExecutor {
    private static final double LEGACY_EFFECT_RANGE = 10.0D;

    private TCInfusionInstabilityExecutor() {
    }

    public static ExecutionResult execute(
            TCInfusionMatrixBlockEntity matrix,
            TCInfusionInstabilityEvent event
    ) {
        if (matrix == null || matrix.getLevel() == null) {
            return ExecutionResult.blocked(event, "missing_server_matrix");
        }
        return execute(matrix, event, TCInfusionRandomSource.wrap(matrix.getLevel().getRandom()));
    }

    static ExecutionResult execute(
            TCInfusionMatrixBlockEntity matrix,
            TCInfusionInstabilityEvent event,
            TCInfusionRandomSource random
    ) {
        if (matrix == null || event == null || !(matrix.getLevel() instanceof ServerLevel level)) {
            return ExecutionResult.blocked(event, "missing_server_matrix");
        }
        if (!event.isSupportedByCurrentPort()) {
            return ExecutionResult.blocked(event, "missing_dependency:" + event.missingDependency());
        }

        BlockPos target = switch (event) {
            case EJECT_ITEM_DROP -> ejectFromPedestal(matrix, EjectEffect.NONE, random);
            case EJECT_FLUX_DROP -> ejectFromPedestal(matrix, EjectEffect.FLUX_DROP, random);
            case EJECT_FLUX_GOO_DROP -> ejectFromPedestal(matrix, EjectEffect.FLUX_GOO_DROP, random);
            case EJECT_FLUX_DELETE -> ejectFromPedestal(matrix, EjectEffect.FLUX_DELETE, random);
            case EJECT_FLUX_GOO_DELETE -> ejectFromPedestal(matrix, EjectEffect.FLUX_GOO_DELETE, random);
            case EJECT_EXPLOSIVE -> ejectFromPedestal(matrix, EjectEffect.EXPLOSIVE, random);
            case WARP -> {
                applyWarp(level, matrix.getBlockPos(), random);
                yield null;
            }
            case ZAP_ONE -> {
                zap(level, matrix.getBlockPos(), false, random);
                yield null;
            }
            case ZAP_ALL -> {
                zap(level, matrix.getBlockPos(), true, random);
                yield null;
            }
            case HARM_ONE -> {
                harm(level, matrix.getBlockPos(), false, random);
                yield null;
            }
            case HARM_ALL -> {
                harm(level, matrix.getBlockPos(), true, random);
                yield null;
            }
            case MATRIX_EXPLOSION -> {
                BlockPos pos = matrix.getBlockPos();
                level.explode(
                        null,
                        pos.getX() + 0.5D,
                        pos.getY() + 0.5D,
                        pos.getZ() + 0.5D,
                        1.5F + random.nextFloat(),
                        Level.ExplosionInteraction.NONE
                );
                yield pos;
            }
        };
        return ExecutionResult.executed(event, target);
    }

    public static void grantInstabilityResearch(TCInfusionMatrixBlockEntity matrix) {
        if (!(matrix.getLevel() instanceof ServerLevel level)) {
            return;
        }
        for (ServerPlayer player : nearbyPlayers(level, matrix.getBlockPos())) {
            TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
            if (knowledge.isResearchKnown("!INSTABILITY")) {
                continue;
            }
            knowledge.addResearch("!INSTABILITY");
            TCPlayerKnowledgeStore.set(player, knowledge, true);
            player.displayClientMessage(
                    Component.translatable("got.instability").withStyle(ChatFormatting.DARK_PURPLE),
                    true
            );
        }
    }

    private static BlockPos ejectFromPedestal(
            TCInfusionMatrixBlockEntity matrix,
            EjectEffect effect,
            TCInfusionRandomSource random
    ) {
        ServerLevel level = (ServerLevel) matrix.getLevel();
        List<TCInfusionPedestalBlockEntity> pedestals = matrix.findSurroundingPedestals();
        for (int retry = 0; retry < 25 && !pedestals.isEmpty(); retry++) {
            TCInfusionPedestalBlockEntity pedestal = pedestals.get(random.nextInt(pedestals.size()));
            if (pedestal.getStoredStack().isEmpty()) {
                continue;
            }
            BlockPos pos = pedestal.getBlockPos();
            BlockPos stabilizerPos = pedestal.findInstabilityMitigator();
            if (stabilizerPos != null
                    && level.getBlockEntity(stabilizerPos) instanceof TCStabilizerBlockEntity stabilizer
                    && stabilizer.mitigate(5 + random.nextInt(6))) {
                return pos;
            }
            if (effect.deletesItem()) {
                pedestal.extractStored();
            } else {
                pedestal.dropStored(level, pos);
            }
            if (effect.pollutesAura()) {
                AuraHelper.polluteAura(level, pos, 5 + random.nextInt(5), true);
            }
            if (effect.explodes()) {
                level.explode(
                        null,
                        pos.getX() + 0.5D,
                        pos.getY() + 0.5D,
                        pos.getZ() + 0.5D,
                        1.0F,
                        Level.ExplosionInteraction.NONE
                );
            }
            if (effect.placesFluxGoo()) {
                level.setBlock(pos.above(), TCBlocks.FLUX_GOO.get().defaultBlockState(), Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.3F, 1.0F);
            }
            return pos;
        }
        return null;
    }

    private static void harm(ServerLevel level, BlockPos matrixPos, boolean all, TCInfusionRandomSource random) {
        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                new AABB(matrixPos).inflate(LEGACY_EFFECT_RANGE)
        );
        for (LivingEntity target : targets) {
            if (random.nextBoolean()) {
                target.addEffect(new MobEffectInstance(TCMobEffects.FLUX_TAINT, 120, 0, false, true));
            } else {
                MobEffectInstance effect = new MobEffectInstance(TCMobEffects.VIS_EXHAUST, 2400, 0, true, true);
                effect.getCures().clear();
                target.addEffect(effect);
            }
            if (!all) {
                break;
            }
        }
    }

    private static void applyWarp(ServerLevel level, BlockPos matrixPos, TCInfusionRandomSource random) {
        List<ServerPlayer> players = nearbyPlayers(level, matrixPos);
        if (players.isEmpty() || TCConfig.WUSS_MODE.get()) {
            return;
        }
        ServerPlayer target = players.get(random.nextInt(players.size()));
        if (random.nextFloat() < 0.25F) {
            TCWarpManager.add(target, TCWarpType.NORMAL, 1);
        } else {
            TCWarpManager.add(target, TCWarpType.TEMPORARY, 2 + random.nextInt(4));
        }
    }

    private static void zap(
            ServerLevel level,
            BlockPos matrixPos,
            boolean all,
            TCInfusionRandomSource random
    ) {
        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                new AABB(matrixPos).inflate(LEGACY_EFFECT_RANGE)
        );
        for (LivingEntity target : targets) {
            target.hurt(level.damageSources().magic(), 4.0F + random.nextInt(4));
            if (!all) {
                break;
            }
        }
    }

    private static List<ServerPlayer> nearbyPlayers(ServerLevel level, BlockPos matrixPos) {
        return level.getEntitiesOfClass(
                ServerPlayer.class,
                new AABB(matrixPos).inflate(LEGACY_EFFECT_RANGE)
        );
    }

    private enum EjectEffect {
        NONE(false, false, false, false),
        FLUX_GOO_DROP(false, false, false, true),
        FLUX_DROP(false, true, false, false),
        FLUX_GOO_DELETE(true, false, false, true),
        FLUX_DELETE(true, true, false, false),
        EXPLOSIVE(false, false, true, false);

        private final boolean deletesItem;
        private final boolean pollutesAura;
        private final boolean explodes;
        private final boolean placesFluxGoo;

        EjectEffect(boolean deletesItem, boolean pollutesAura, boolean explodes, boolean placesFluxGoo) {
            this.deletesItem = deletesItem;
            this.pollutesAura = pollutesAura;
            this.explodes = explodes;
            this.placesFluxGoo = placesFluxGoo;
        }

        boolean deletesItem() {
            return deletesItem;
        }

        boolean pollutesAura() {
            return pollutesAura;
        }

        boolean explodes() {
            return explodes;
        }

        boolean placesFluxGoo() {
            return placesFluxGoo;
        }
    }

    public record ExecutionResult(
            Status status,
            TCInfusionInstabilityEvent event,
            String reason,
            BlockPos targetPos
    ) {
        public ExecutionResult {
            reason = reason == null ? "" : reason;
        }

        static ExecutionResult executed(TCInfusionInstabilityEvent event, BlockPos targetPos) {
            return new ExecutionResult(Status.EXECUTED, event, "executed", targetPos);
        }

        static ExecutionResult blocked(TCInfusionInstabilityEvent event, String reason) {
            return new ExecutionResult(Status.BLOCKED, event, reason, null);
        }

        public enum Status {
            EXECUTED,
            BLOCKED
        }
    }
}
