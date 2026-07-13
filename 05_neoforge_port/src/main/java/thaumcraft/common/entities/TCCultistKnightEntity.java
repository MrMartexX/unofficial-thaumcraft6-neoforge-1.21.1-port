package thaumcraft.common.entities;

import java.util.Optional;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import thaumcraft.common.registry.TCEntityTypes;
import thaumcraft.common.registry.TCItems;

public class TCCultistKnightEntity extends TCCultistEntity {
    public static final double LEGACY_MAX_HEALTH = 30.0D;
    public static final float LEGACY_HARD_RARE_WEAPON_CHANCE = 0.05F;
    public static final float LEGACY_NORMAL_RARE_WEAPON_CHANCE = 0.01F;

    public TCCultistKnightEntity(EntityType<? extends TCCultistKnightEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createCultistAttributes(LEGACY_MAX_HEALTH);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 0.8D));
        goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TCEldritchGuardianEntity.class, true));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractIllager.class, true));
    }

    @Override
    protected void populateLegacyEquipment(DifficultyInstance difficulty) {
        float rareChance = level().getDifficulty() == Difficulty.HARD
                ? LEGACY_HARD_RARE_WEAPON_CHANCE
                : LEGACY_NORMAL_RARE_WEAPON_CHANCE;
        if (random.nextFloat() < rareChance) {
            if (random.nextInt(5) == 0) {
                setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(TCItems.VOID_SWORD.get()));
            } else {
                setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(TCItems.THAUMIUM_SWORD.get()));
                if (random.nextBoolean()) {
                    setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                }
            }
        } else {
            setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
        }
    }

    @Override
    protected void enchantLegacyEquipment(ServerLevelAccessor level, net.minecraft.util.RandomSource random, DifficultyInstance difficulty) {
        float localDifficulty = difficulty.getSpecialMultiplier();
        ItemStack held = getMainHandItem();
        if (!held.isEmpty() && random.nextFloat() < 0.25F * localDifficulty) {
            int enchantLevel = (int) (5.0F + localDifficulty * random.nextInt(18));
            setItemSlot(EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem(random, held, enchantLevel, level.registryAccess(), Optional.empty()));
        }
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof TCCultistEntity) && super.canAttack(target);
    }
}
