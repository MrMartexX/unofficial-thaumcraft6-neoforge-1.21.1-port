package thaumcraft.common.warp;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import thaumcraft.common.config.TCConfig;
import thaumcraft.common.entities.TCCultistPortalLesserEntity;
import thaumcraft.common.entities.TCEldritchGuardianEntity;
import thaumcraft.common.entities.TCMindSpiderEntity;
import thaumcraft.common.items.equipment.TCEquipmentHelper;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;
import thaumcraft.common.registry.TCMobEffects;
import thaumcraft.common.research.TCPlayerKnowledge;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.research.TCResearchManager;

public final class TCWarpEvents {
    public static final int CHECK_INTERVAL_TICKS = 2000;
    public static final int DEATH_GAZE_INTERVAL_TICKS = 20;
    private static final double DEATH_GAZE_APERTURE = 0.75D;

    private TCWarpEvents() {
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (!TCConfig.WUSS_MODE.get()
                    && player.tickCount > 0
                    && player.tickCount % CHECK_INTERVAL_TICKS == 0
                    && !player.hasEffect(TCMobEffects.WARP_WARD)) {
                checkWarpEvent(player, player.getRandom(), true);
            }
            if (player.tickCount > 0
                    && player.tickCount % DEATH_GAZE_INTERVAL_TICKS == 0
                    && player.hasEffect(TCMobEffects.DEATH_GAZE)) {
                checkDeathGaze(player);
            }
        }
    }

    public static void onFinishUsingItem(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.hasEffect(TCMobEffects.UNNATURAL_HUNGER)) {
            return;
        }

        ItemStack consumed = event.getItem();
        MobEffectInstance current = player.getEffect(TCMobEffects.UNNATURAL_HUNGER);
        if (current == null) {
            return;
        }

        if (isUnnaturalHungerCurative(consumed)) {
            int duration = current.getDuration() - 600;
            int amplifier = current.getAmplifier() - 1;
            player.removeEffect(TCMobEffects.UNNATURAL_HUNGER);
            if (duration > 0 && amplifier >= 0) {
                player.addEffect(uncured(new MobEffectInstance(TCMobEffects.UNNATURAL_HUNGER, duration, amplifier, true, true)));
            }
            status(player, "warp.text.hunger.2", ChatFormatting.GREEN);
        } else if (consumed.getFoodProperties(player) != null) {
            status(player, "warp.text.hunger.1", ChatFormatting.RED);
        }
    }

    public static TCWarpEventResult checkWarpEvent(ServerPlayer player, RandomSource random, boolean executeEffects) {
        TCWarpManager.add(player, TCWarpType.TEMPORARY, -1, false);
        TCPlayerWarp stored = TCWarpManager.get(player);
        int actualWarp = stored.actualWarp();
        int gearWarp = TCEquipmentHelper.getWarp(player);
        int warp = stored.totalWarp() + gearWarp;
        int preCounter = stored.getCounter();
        int chanceRoll = random.nextInt(100);
        if (!shouldTrigger(preCounter, warp, chanceRoll)) {
            return new TCWarpEventResult(false, preCounter, preCounter, warp, actualWarp, gearWarp, chanceRoll, 0, TCWarpEventOutcome.NO_EVENT);
        }

        int adjustedWarp = adjustedWarp(warp, preCounter);
        int postCounter = reducedCounter(preCounter, gearWarp);
        TCWarpManager.setCounter(player, postCounter);
        int effectRoll = random.nextInt(Math.max(1, adjustedWarp)) + gearWarp;
        TCWarpEventOutcome outcome = outcomeForEffect(effectRoll);
        if (executeEffects && effectRoll > 0) {
            executeOutcome(player, adjustedWarp, outcome);
        }
        if (executeEffects) {
            unlockWarpResearch(player, actualWarp);
        }
        return new TCWarpEventResult(true, preCounter, postCounter, adjustedWarp, actualWarp, gearWarp, chanceRoll, effectRoll, outcome);
    }

    public static boolean shouldTrigger(int warpCounter, int warp, int chanceRoll) {
        return warpCounter > 0 && warp > 0 && chanceRoll <= Math.sqrt(warpCounter);
    }

    public static int adjustedWarp(int warpIncludingGear, int warpCounter) {
        return Math.min(100, (warpIncludingGear + warpIncludingGear + warpCounter) / 3);
    }

    public static int reducedCounter(int warpCounter, int gearWarp) {
        int reduction = (int) Math.max(5.0D, Math.sqrt(warpCounter) * 2.0D - gearWarp * 2.0D);
        return Math.max(0, warpCounter - reduction);
    }

    public static int legacyAmplifier(int adjustedWarp) {
        return Math.min(3, adjustedWarp / 15);
    }

    public static int deathGazeRange(int amplifier) {
        return Math.min(8 + Math.max(0, amplifier) * 3, 24);
    }

    public static TCWarpEventOutcome outcomeForEffect(int effectRoll) {
        if (effectRoll <= 0) {
            return TCWarpEventOutcome.NO_EVENT;
        }
        if (effectRoll <= 4) {
            return TCWarpEventOutcome.CREEPER_SOUND;
        }
        if (effectRoll <= 8) {
            return TCWarpEventOutcome.EXPLOSION_SOUND;
        }
        if (effectRoll <= 12) {
            return TCWarpEventOutcome.NOISE_BEHIND;
        }
        if (effectRoll <= 16) {
            return TCWarpEventOutcome.VIS_EXHAUST;
        }
        if (effectRoll <= 20) {
            return TCWarpEventOutcome.THAUMARHIA;
        }
        if (effectRoll <= 24) {
            return TCWarpEventOutcome.UNNATURAL_HUNGER_SHORT;
        }
        if (effectRoll <= 28) {
            return TCWarpEventOutcome.SOMETHING_FOLLOWING;
        }
        if (effectRoll <= 32) {
            return TCWarpEventOutcome.MIST_ONE_GUARDIAN;
        }
        if (effectRoll <= 36) {
            return TCWarpEventOutcome.BLURRED_VISION;
        }
        if (effectRoll <= 40) {
            return TCWarpEventOutcome.SUN_SCORNED;
        }
        if (effectRoll <= 44) {
            return TCWarpEventOutcome.MINING_FATIGUE;
        }
        if (effectRoll <= 48) {
            return TCWarpEventOutcome.INFECTIOUS_VIS_EXHAUST;
        }
        if (effectRoll <= 52) {
            return TCWarpEventOutcome.NIGHT_VISION;
        }
        if (effectRoll <= 56) {
            return TCWarpEventOutcome.DEATH_GAZE;
        }
        if (effectRoll <= 60) {
            return TCWarpEventOutcome.MIND_SPIDERS_FAKE;
        }
        if (effectRoll <= 64) {
            return TCWarpEventOutcome.SOMETHING_WATCHING;
        }
        if (effectRoll <= 68) {
            return TCWarpEventOutcome.MIST_GUARDIANS_LIGHT;
        }
        if (effectRoll <= 72) {
            return TCWarpEventOutcome.BLINDNESS;
        }
        if (effectRoll == 76) {
            return TCWarpEventOutcome.MOMENT_OF_CLARITY;
        }
        if (effectRoll <= 80) {
            return TCWarpEventOutcome.UNNATURAL_HUNGER_LONG;
        }
        if (effectRoll <= 88) {
            return TCWarpEventOutcome.CULTIST_PORTAL;
        }
        if (effectRoll <= 92) {
            return TCWarpEventOutcome.MIND_SPIDERS_REAL;
        }
        return TCWarpEventOutcome.MIST_GUARDIANS_HEAVY;
    }

    public static void executeOutcomeForValidation(ServerPlayer player, int adjustedWarp, TCWarpEventOutcome outcome) {
        executeOutcome(player, adjustedWarp, outcome);
    }

    static boolean unlockWarpResearch(ServerPlayer player, int actualWarp) {
        TCPlayerKnowledge knowledge = TCPlayerKnowledgeStore.get(player);
        boolean changed = false;
        if (actualWarp > 10
                && !TCResearchManager.isResearchComplete(knowledge, "BATHSALTS")
                && !TCResearchManager.isResearchComplete(knowledge, "!BATHSALTS")) {
            status(player, "warp.text.8", ChatFormatting.DARK_PURPLE);
            changed |= TCResearchManager.completeResearch(player, "!BATHSALTS");
            knowledge = TCPlayerKnowledgeStore.get(player);
        }
        if (actualWarp > 25 && !TCResearchManager.isResearchComplete(knowledge, "ELDRITCHMINOR")) {
            changed |= TCResearchManager.completeResearch(player, "ELDRITCHMINOR");
            knowledge = TCPlayerKnowledgeStore.get(player);
        }
        if (actualWarp > 50 && !TCResearchManager.isResearchComplete(knowledge, "ELDRITCHMAJOR")) {
            changed |= TCResearchManager.completeResearch(player, "ELDRITCHMAJOR");
        }
        return changed;
    }

    public static void checkDeathGaze(ServerPlayer player) {
        MobEffectInstance effect = player.getEffect(TCMobEffects.DEATH_GAZE);
        if (effect == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }

        int range = deathGazeRange(effect.getAmplifier());
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(range))) {
            if (target == player
                    || !target.isAlive()
                    || !target.isPickable()
                    || target.hasEffect(MobEffects.WITHER)
                    || target instanceof ServerPlayer && !player.server.isPvpAllowed()
                    || !isVisibleInDeathGazeCone(player, target, range)
                    || !player.hasLineOfSight(target)) {
                continue;
            }
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 0));
            if (target instanceof Mob mob) {
                mob.setTarget(player);
            }
        }
    }

    public static boolean isVisibleInDeathGazeCone(Entity viewer, Entity target, float range) {
        Vec3 apex = new Vec3(viewer.getX(), viewer.getEyeY(), viewer.getZ());
        Vec3 axis = viewer.getLookAngle().scale(range);
        Vec3 toTarget = new Vec3(target.getX(), target.getBoundingBox().minY + target.getBbHeight() / 2.0D, target.getZ()).subtract(apex);
        double toTargetLength = toTarget.length();
        double axisLength = axis.length();
        if (toTargetLength <= 0.0001D || axisLength <= 0.0001D) {
            return true;
        }
        double projection = toTarget.dot(axis) / axisLength;
        double cosine = toTarget.dot(axis) / toTargetLength / axisLength;
        return cosine > Math.cos(DEATH_GAZE_APERTURE / 2.0D) && projection < axisLength;
    }

    private static void executeOutcome(ServerPlayer player, int adjustedWarp, TCWarpEventOutcome outcome) {
        switch (outcome) {
            case CREEPER_SOUND -> player.level().playSound(null, player.blockPosition(), SoundEvents.CREEPER_PRIMED, SoundSource.AMBIENT, 1.0F, 0.5F);
            case EXPLOSION_SOUND -> player.level().playSound(
                    null,
                    player.getX() + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 10.0F,
                    player.getY() + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 10.0F,
                    player.getZ() + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 10.0F,
                    SoundEvents.GENERIC_EXPLODE.value(),
                    SoundSource.AMBIENT,
                    4.0F,
                    (1.0F + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F) * 0.7F);
            case NOISE_BEHIND -> status(player, "warp.text.11", ChatFormatting.DARK_PURPLE);
            case VIS_EXHAUST -> {
                player.addEffect(uncured(new MobEffectInstance(TCMobEffects.VIS_EXHAUST, 5000, legacyAmplifier(adjustedWarp), true, true)));
                status(player, "warp.text.1", ChatFormatting.DARK_PURPLE);
            }
            case THAUMARHIA -> {
                player.addEffect(uncured(new MobEffectInstance(TCMobEffects.THAUMARHIA, Math.min(32000, 10 * adjustedWarp), 0, true, true)));
                status(player, "warp.text.15", ChatFormatting.DARK_PURPLE);
            }
            case UNNATURAL_HUNGER_SHORT -> addUnnaturalHunger(player, 5000, adjustedWarp);
            case SOMETHING_FOLLOWING -> status(player, "warp.text.12", ChatFormatting.DARK_PURPLE);
            case MIST_ONE_GUARDIAN, MIST_GUARDIANS_LIGHT, MIST_GUARDIANS_HEAVY -> {
                spawnMist(player, adjustedWarp, guardianCountForOutcome(outcome, adjustedWarp));
                status(player, "warp.text.6", ChatFormatting.DARK_PURPLE);
            }
            case BLURRED_VISION -> player.addEffect(new MobEffectInstance(TCMobEffects.BLURRED_VISION, Math.min(32000, 10 * adjustedWarp), 0, true, true));
            case SUN_SCORNED -> {
                player.addEffect(uncured(new MobEffectInstance(TCMobEffects.SUN_SCORNED, 5000, legacyAmplifier(adjustedWarp), true, true)));
                status(player, "warp.text.5", ChatFormatting.DARK_PURPLE);
            }
            case MINING_FATIGUE -> {
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1200, legacyAmplifier(adjustedWarp), true, true));
                status(player, "warp.text.9", ChatFormatting.DARK_PURPLE);
            }
            case INFECTIOUS_VIS_EXHAUST -> {
                player.addEffect(uncured(new MobEffectInstance(TCMobEffects.INFECTIOUS_VIS_EXHAUST, 6000, legacyAmplifier(adjustedWarp))));
                status(player, "warp.text.1", ChatFormatting.DARK_PURPLE);
            }
            case NIGHT_VISION -> {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, Math.min(40 * adjustedWarp, 6000), 0, true, true));
                status(player, "warp.text.10", ChatFormatting.DARK_PURPLE);
            }
            case DEATH_GAZE -> {
                player.addEffect(uncured(new MobEffectInstance(TCMobEffects.DEATH_GAZE, 6000, legacyAmplifier(adjustedWarp), true, true)));
                status(player, "warp.text.4", ChatFormatting.DARK_PURPLE);
            }
            case MIND_SPIDERS_FAKE, MIND_SPIDERS_REAL -> {
                suddenlySpiders(player, adjustedWarp, outcome == TCWarpEventOutcome.MIND_SPIDERS_REAL);
                status(player, "warp.text.7", ChatFormatting.DARK_PURPLE);
            }
            case SOMETHING_WATCHING -> status(player, "warp.text.13", ChatFormatting.DARK_PURPLE);
            case BLINDNESS -> player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, Math.min(32000, 5 * adjustedWarp), 0, true, true));
            case MOMENT_OF_CLARITY -> {
                if (TCWarpManager.get(player).get(TCWarpType.NORMAL) > 0) {
                    TCWarpManager.reduce(player, TCWarpType.NORMAL, 1);
                }
                status(player, "warp.text.14", ChatFormatting.DARK_PURPLE);
            }
            case UNNATURAL_HUNGER_LONG -> addUnnaturalHunger(player, 6000, adjustedWarp);
            case CULTIST_PORTAL -> {
                if (spawnPortal(player)) {
                    status(player, "warp.text.16", ChatFormatting.DARK_PURPLE);
                }
            }
            case NO_EVENT -> {
            }
        }
    }

    public static int guardianCountForOutcome(TCWarpEventOutcome outcome, int adjustedWarp) {
        return switch (outcome) {
            case MIST_ONE_GUARDIAN -> 1;
            case MIST_GUARDIANS_LIGHT -> adjustedWarp / 30;
            case MIST_GUARDIANS_HEAVY -> adjustedWarp / 15;
            default -> 0;
        };
    }

    static int boundedGuardianCount(int guardianCount) {
        return Math.min(8, Math.max(0, guardianCount));
    }

    private static void spawnMist(ServerPlayer player, int adjustedWarp, int guardianCount) {
        int count = boundedGuardianCount(guardianCount);
        for (int i = 0; i < count; i++) {
            spawnGuardian(player);
        }
    }

    private static boolean spawnGuardian(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }
        TCEldritchGuardianEntity guardian = TCEntityTypes.ELDRITCH_GUARDIAN.get().create(level);
        if (guardian == null) {
            return false;
        }

        BlockPos origin = player.blockPosition();
        for (int i = 0; i < 50; i++) {
            BlockPos candidate = randomLegacyWarpSpawnPos(origin, player.getRandom());
            guardian.moveTo(candidate.getX(), candidate.getY(), candidate.getZ(), player.getRandom().nextFloat() * 360.0F, 0.0F);
            if (isLegacyWarpSpawnPositionValid(level, guardian, candidate)) {
                guardian.setTarget(player);
                return level.addFreshEntity(guardian);
            }
        }
        return false;
    }

    private static boolean spawnPortal(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }
        TCCultistPortalLesserEntity portal = TCEntityTypes.CULTIST_PORTAL_LESSER.get().create(level);
        if (portal == null) {
            return false;
        }

        BlockPos origin = player.blockPosition();
        for (int i = 0; i < 50; i++) {
            BlockPos candidate = randomLegacyWarpSpawnPos(origin, player.getRandom());
            portal.moveTo(candidate.getX() + 0.5D, candidate.getY() + 1.0D, candidate.getZ() + 0.5D, 0.0F, 0.0F);
            if (isLegacyWarpSpawnPositionValid(level, portal, candidate)) {
                return level.addFreshEntity(portal);
            }
        }
        return false;
    }

    private static int suddenlySpiders(ServerPlayer player, int adjustedWarp, boolean real) {
        if (!(player.level() instanceof ServerLevel level)) {
            return 0;
        }

        int spawned = 0;
        int spawnCount = Math.min(50, adjustedWarp);
        BlockPos origin = player.blockPosition();
        for (int i = 0; i < spawnCount; i++) {
            TCMindSpiderEntity spider = TCEntityTypes.MIND_SPIDER.get().create(level);
            if (spider == null) {
                continue;
            }

            boolean success = false;
            for (int attempt = 0; attempt < 50; attempt++) {
                BlockPos candidate = randomLegacyWarpSpawnPos(origin, player.getRandom());
                spider.moveTo(candidate.getX(), candidate.getY(), candidate.getZ(), player.getRandom().nextFloat() * 360.0F, 0.0F);
                if (isLegacyWarpSpawnPositionValid(level, spider, candidate)) {
                    success = true;
                    break;
                }
            }

            if (success) {
                spider.setTarget(player);
                if (!real) {
                    spider.setViewer(player.getName().getString());
                    spider.setHarmless(true);
                }
                if (level.addFreshEntity(spider)) {
                    spawned++;
                }
            }
        }
        return spawned;
    }

    static BlockPos randomLegacyWarpSpawnPos(BlockPos origin, RandomSource random) {
        return origin.offset(randomLegacySpawnOffset(random), randomLegacySpawnOffset(random), randomLegacySpawnOffset(random));
    }

    static int randomLegacySpawnOffset(RandomSource random) {
        return Mth.nextInt(random, 7, 24) * Mth.nextInt(random, -1, 1);
    }

    static boolean isLegacyWarpSpawnPositionValid(ServerLevel level, Entity entity, BlockPos candidate) {
        return level.getBlockState(candidate.below()).isSolid()
                && level.noCollision(entity)
                && !level.containsAnyLiquid(entity.getBoundingBox());
    }

    private static void addUnnaturalHunger(ServerPlayer player, int duration, int adjustedWarp) {
        player.addEffect(uncured(new MobEffectInstance(TCMobEffects.UNNATURAL_HUNGER, duration, legacyAmplifier(adjustedWarp), true, true)));
        status(player, "warp.text.2", ChatFormatting.DARK_PURPLE);
    }

    private static MobEffectInstance uncured(MobEffectInstance effect) {
        effect.getCures().clear();
        return effect;
    }

    private static boolean isUnnaturalHungerCurative(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(Items.ROTTEN_FLESH) || stack.is(TCItems.BRAIN.get()));
    }

    private static void status(ServerPlayer player, String translationKey, ChatFormatting color) {
        player.displayClientMessage(Component.translatable(translationKey).withStyle(color, ChatFormatting.ITALIC), true);
    }

    public enum TCWarpEventOutcome {
        NO_EVENT(false),
        CREEPER_SOUND(false),
        EXPLOSION_SOUND(false),
        NOISE_BEHIND(false),
        VIS_EXHAUST(false),
        THAUMARHIA(false),
        UNNATURAL_HUNGER_SHORT(false),
        SOMETHING_FOLLOWING(false),
        MIST_ONE_GUARDIAN(true),
        BLURRED_VISION(false),
        SUN_SCORNED(false),
        MINING_FATIGUE(false),
        INFECTIOUS_VIS_EXHAUST(false),
        NIGHT_VISION(false),
        DEATH_GAZE(false),
        MIND_SPIDERS_FAKE(true),
        SOMETHING_WATCHING(false),
        MIST_GUARDIANS_LIGHT(true),
        BLINDNESS(false),
        MOMENT_OF_CLARITY(false),
        UNNATURAL_HUNGER_LONG(false),
        CULTIST_PORTAL(true),
        MIND_SPIDERS_REAL(true),
        MIST_GUARDIANS_HEAVY(true);

        private final boolean entityOutcome;

        TCWarpEventOutcome(boolean entityOutcome) {
            this.entityOutcome = entityOutcome;
        }

        public boolean entityOutcome() {
            return entityOutcome;
        }
    }

    public record TCWarpEventResult(
            boolean triggered,
            int preCounter,
            int postCounter,
            int adjustedWarp,
            int actualWarp,
            int gearWarp,
            int chanceRoll,
            int effectRoll,
            TCWarpEventOutcome outcome
    ) {
    }
}
