package thaumcraft.common.entities.monster.mods;

import java.util.UUID;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.renderers.entity.mob.LayerTainted;
import thaumcraft.common.entities.ai.combat.EntityCritterAIAttackMelee;

public class ChampionModTainted implements IChampionModifierEffect {
   public static final IAttribute TAINTED_MOD = new RangedAttribute((IAttribute)null, "tc.mobmodtaint", 0.0, 0.0, 1.0).func_111117_a("Tainted modifier");

   @Override
   public float performEffect(EntityLivingBase boss, EntityLivingBase target, DamageSource source, float amount) {
      resetAI((EntityCreature)boss);
      return amount;
   }

   public static void resetAI(EntityCreature critter) {
      IAttributeInstance modai = critter.func_110148_a(TAINTED_MOD);
      if (!(critter instanceof EntityMob) && modai.func_111126_e() == 0.0) {
         try {
            critter.field_70714_bg.field_75782_a.clear();
            critter.field_70715_bh.field_75782_a.clear();
            critter.field_70714_bg.func_75776_a(0, new EntityAISwimming(critter));
            critter.field_70714_bg.func_75776_a(2, new EntityCritterAIAttackMelee(critter, 1.2, false));
            critter.field_70714_bg.func_75776_a(5, new EntityAIMoveTowardsRestriction(critter, 1.0));
            critter.field_70714_bg.func_75776_a(7, new EntityAIWander(critter, 1.0));
            critter.field_70714_bg.func_75776_a(8, new EntityAIWatchClosest(critter, EntityPlayer.class, 8.0F));
            critter.field_70714_bg.func_75776_a(8, new EntityAILookIdle(critter));
            critter.field_70714_bg.func_75776_a(6, new EntityAIMoveThroughVillage(critter, 1.0, false));
            critter.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(critter, true, new Class[]{EntityPigZombie.class}));
            critter.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(critter, EntityPlayer.class, true));
            modai.func_111124_b(new AttributeModifier(UUID.fromString("2cb22137-a9d8-4417-ae06-de0e70f11b4c"), "istainted", 0.0, 0));
            modai.func_111121_a(new AttributeModifier(UUID.fromString("2cb22137-a9d8-4417-ae06-de0e70f11b4c"), "istainted", 1.0, 0));
         } catch (Exception var3) {
         }
      }

      IAttributeInstance iattributeinstance2 = critter.func_110148_a(SharedMonsterAttributes.field_111264_e);
      if (iattributeinstance2 == null) {
         critter.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e);
         critter.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(Math.max(2.0F, (critter.field_70131_O + critter.field_70130_N) * 2.0F));
      }
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void preRender(EntityLivingBase boss, RenderLivingBase renderLivingBase) {
      if (!LayerTainted.taintLayers.contains(boss.func_145782_y())) {
         renderLivingBase.func_177094_a(new LayerTainted(boss.func_145782_y(), renderLivingBase, renderLivingBase.func_177087_b()));
      }
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void showFX(EntityLivingBase boss) {
      float w = boss.field_70170_p.field_73012_v.nextFloat() * boss.field_70130_N;
      float d = boss.field_70170_p.field_73012_v.nextFloat() * boss.field_70130_N;
      float h = boss.field_70170_p.field_73012_v.nextFloat() * boss.field_70131_O;
      FXDispatcher.INSTANCE
         .drawGenericParticles(
            boss.func_174813_aQ().field_72340_a + w,
            boss.func_174813_aQ().field_72338_b + h,
            boss.func_174813_aQ().field_72339_c + d,
            0.0,
            -0.01,
            0.0,
            0.1F + boss.field_70170_p.field_73012_v.nextFloat() * 0.2F,
            0.0F,
            0.1F + boss.field_70170_p.field_73012_v.nextFloat() * 0.1F,
            0.25F,
            false,
            1,
            5,
            1,
            6 + boss.field_70170_p.field_73012_v.nextInt(6),
            0,
            2.0F + boss.field_70170_p.field_73012_v.nextFloat(),
            0.5F,
            1
         );
   }
}
