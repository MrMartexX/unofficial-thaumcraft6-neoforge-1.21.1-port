package thaumcraft.common.entities;

import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import thaumcraft.common.config.TCConfig;
import thaumcraft.common.registry.TCItems;

public class TCBrainyZombieEntity extends Zombie {
    public static final double LEGACY_MAX_HEALTH = 25.0D;
    public static final double LEGACY_ATTACK_DAMAGE = 5.0D;
    public static final double LEGACY_ARMOR_BONUS = 1.0D;
    public static final double LEGACY_REINFORCEMENT_CHANCE = 0.0D;
    public static final int LEGACY_BRAIN_DROP_ROLL_BOUND = 10;
    public static final int LEGACY_BRAIN_DROP_THRESHOLD = 4;
    public static final float LEGACY_BRAIN_DROP_OFFSET = 1.5F;

    public TCBrainyZombieEntity(EntityType<? extends TCBrainyZombieEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, LEGACY_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, LEGACY_ATTACK_DAMAGE)
                .add(Attributes.ARMOR, LEGACY_ARMOR_BONUS)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, LEGACY_REINFORCEMENT_CHANCE);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        if (legacyShouldDropBrain(random, lootingModifier(level, damageSource))) {
            spawnAtLocation(new ItemStack(TCItems.BRAIN.get()), LEGACY_BRAIN_DROP_OFFSET);
        }
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
    }

    private static int lootingModifier(ServerLevel level, DamageSource damageSource) {
        Entity attacker = damageSource.getEntity();
        if (!(attacker instanceof LivingEntity living)) {
            return 0;
        }
        Holder.Reference<Enchantment> looting = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.LOOTING);
        return EnchantmentHelper.getEnchantmentLevel(looting, living);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    public static boolean checkBrainyZombieSpawnRules(
            EntityType<TCBrainyZombieEntity> type,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return TCConfig.ALLOW_SPAWN_ANGRY_ZOMBIE.get()
                && Monster.checkMonsterSpawnRules(type, level, spawnType, pos, random);
    }

    public static boolean legacyShouldDropBrain(RandomSource random, int lootingModifier) {
        return legacyShouldDropBrainRoll(random.nextInt(LEGACY_BRAIN_DROP_ROLL_BOUND), lootingModifier);
    }

    public static boolean legacyShouldDropBrainRoll(int roll, int lootingModifier) {
        return roll - lootingModifier <= LEGACY_BRAIN_DROP_THRESHOLD;
    }

    public static boolean testLegacySpawnGatesForValidation(
            boolean configEnabled,
            Difficulty difficulty,
            boolean monsterRulesAllow
    ) {
        return configEnabled && difficulty != Difficulty.PEACEFUL && monsterRulesAllow;
    }
}
