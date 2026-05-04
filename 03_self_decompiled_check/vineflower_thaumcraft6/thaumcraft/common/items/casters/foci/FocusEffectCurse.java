package thaumcraft.common.items.casters.foci;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockBamf;

public class FocusEffectCurse extends FocusEffect {
   @Override
   public String getResearch() {
      return "FOCUSCURSE";
   }

   @Override
   public String getKey() {
      return "thaumcraft.CURSE";
   }

   @Override
   public Aspect getAspect() {
      return Aspect.DEATH;
   }

   @Override
   public int getComplexity() {
      return this.getSettingValue("duration") + this.getSettingValue("power") * 3;
   }

   @Override
   public float getDamageForDisplay(float finalPower) {
      return (1.0F + this.getSettingValue("power")) * finalPower;
   }

   @Override
   public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
      PacketHandler.INSTANCE
         .sendToAllAround(
            new PacketFXBlockBamf(
               target.field_72307_f.field_72450_a, target.field_72307_f.field_72448_b, target.field_72307_f.field_72449_c, 6946821, true, true, null
            ),
            new TargetPoint(
               this.getPackage().world.field_73011_w.getDimension(),
               target.field_72307_f.field_72450_a,
               target.field_72307_f.field_72448_b,
               target.field_72307_f.field_72449_c,
               64.0
            )
         );
      if (target.field_72313_a == Type.ENTITY && target.field_72308_g != null) {
         float damage = this.getDamageForDisplay(finalPower);
         int duration = 20 * this.getSettingValue("duration");
         int eff = (int)(this.getSettingValue("power") * finalPower / 2.0F);
         if (eff < 0) {
            eff = 0;
         }

         target.field_72308_g
            .func_70097_a(
               DamageSource.func_76354_b(
                  (Entity)(target.field_72308_g != null ? target.field_72308_g : this.getPackage().getCaster()), this.getPackage().getCaster()
               ),
               damage
            );
         if (target.field_72308_g instanceof EntityLivingBase) {
            ((EntityLivingBase)target.field_72308_g).func_70690_d(new PotionEffect(MobEffects.field_76436_u, duration, Math.round(eff)));
            float c = 0.85F;
            if (this.getPackage().world.field_73012_v.nextFloat() < c) {
               ((EntityLivingBase)target.field_72308_g).func_70690_d(new PotionEffect(MobEffects.field_76421_d, duration, Math.round(eff)));
               c -= 0.15F;
            }

            if (this.getPackage().world.field_73012_v.nextFloat() < c) {
               ((EntityLivingBase)target.field_72308_g).func_70690_d(new PotionEffect(MobEffects.field_76437_t, duration, Math.round(eff)));
               c -= 0.15F;
            }

            if (this.getPackage().world.field_73012_v.nextFloat() < c) {
               ((EntityLivingBase)target.field_72308_g).func_70690_d(new PotionEffect(MobEffects.field_76419_f, duration * 2, Math.round(eff)));
               c -= 0.15F;
            }

            if (this.getPackage().world.field_73012_v.nextFloat() < c) {
               ((EntityLivingBase)target.field_72308_g).func_70690_d(new PotionEffect(MobEffects.field_76438_s, duration * 3, Math.round(eff)));
               c -= 0.15F;
            }

            if (this.getPackage().world.field_73012_v.nextFloat() < c) {
               ((EntityLivingBase)target.field_72308_g).func_70690_d(new PotionEffect(MobEffects.field_189112_A, duration * 3, Math.round(eff)));
            }
         }
      } else if (target.field_72313_a == Type.BLOCK) {
         float f = (float)Math.min(8.0, 1.5 * this.getSettingValue("power") * finalPower);

         for (MutableBlockPos blockpos$mutableblockpos1 : BlockPos.func_177975_b(
            target.func_178782_a().func_177963_a(-f, -f, -f), target.func_178782_a().func_177963_a(f, f, f)
         )) {
            if (blockpos$mutableblockpos1.func_177957_d(
                     target.field_72307_f.field_72450_a, target.field_72307_f.field_72448_b, target.field_72307_f.field_72449_c
                  )
                  <= f * f
               && this.getPackage().world.func_175623_d(blockpos$mutableblockpos1.func_177984_a())
               && this.getPackage().world.func_175665_u(blockpos$mutableblockpos1)) {
               this.getPackage().world.func_175656_a(blockpos$mutableblockpos1.func_177984_a(), BlocksTC.effectSap.func_176223_P());
            }
         }
      }

      return false;
   }

   @Override
   public NodeSetting[] createSettings() {
      return new NodeSetting[]{
         new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)),
         new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(1, 10))
      };
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void renderParticleFX(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
      FXGeneric fb = new FXGeneric(world, posX, posY, posZ, motionX, motionY, motionZ);
      fb.func_187114_a(8);
      fb.func_70538_b(0.41F + world.field_73012_v.nextFloat() * 0.2F, 0.0F, 0.019F + world.field_73012_v.nextFloat() * 0.2F);
      fb.setAlphaF(0.0F, world.field_73012_v.nextFloat(), world.field_73012_v.nextFloat(), world.field_73012_v.nextFloat(), 0.0F);
      fb.setGridSize(16);
      fb.setParticles(72 + world.field_73012_v.nextInt(4), 1, 1);
      fb.setScale(2.0F + world.field_73012_v.nextFloat() * 4.0F);
      fb.setLoop(false);
      fb.setSlowDown(0.9);
      fb.setGravity(0.0F);
      fb.setRotationSpeed(world.field_73012_v.nextFloat(), 0.0F);
      ParticleEngine.addEffectWithDelay(world, fb, world.field_73012_v.nextInt(4));
   }

   @Override
   public void onCast(Entity caster) {
      caster.field_70170_p
         .func_184133_a(
            null,
            caster.func_180425_c().func_177984_a(),
            SoundEvents.field_187514_aD,
            SoundCategory.PLAYERS,
            0.15F,
            1.0F + caster.func_130014_f_().field_73012_v.nextFloat() / 2.0F
         );
   }
}
