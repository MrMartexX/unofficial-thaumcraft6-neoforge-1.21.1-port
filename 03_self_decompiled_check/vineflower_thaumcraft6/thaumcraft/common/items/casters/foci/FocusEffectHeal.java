package thaumcraft.common.items.casters.foci;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;

public class FocusEffectHeal extends FocusEffect {
   @Override
   public String getResearch() {
      return "FOCUSHEAL";
   }

   @Override
   public String getKey() {
      return "thaumcraft.HEAL";
   }

   @Override
   public Aspect getAspect() {
      return Aspect.LIFE;
   }

   @Override
   public int getComplexity() {
      return this.getSettingValue("power") * 4;
   }

   @Override
   public float getDamageForDisplay(float finalPower) {
      return -this.getSettingValue("power") * finalPower;
   }

   @Override
   public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
      PacketHandler.INSTANCE
         .sendToAllAround(
            new PacketFXFocusPartImpact(
               target.field_72307_f.field_72450_a, target.field_72307_f.field_72448_b, target.field_72307_f.field_72449_c, new String[]{this.getKey()}
            ),
            new TargetPoint(
               this.getPackage().world.field_73011_w.getDimension(),
               target.field_72307_f.field_72450_a,
               target.field_72307_f.field_72448_b,
               target.field_72307_f.field_72449_c,
               64.0
            )
         );
      if (target.field_72313_a == Type.ENTITY && target.field_72308_g != null && target.field_72308_g instanceof EntityLivingBase) {
         if (((EntityLivingBase)target.field_72308_g).func_70662_br()) {
            target.field_72308_g
               .func_70097_a(
                  DamageSource.func_76354_b(this.getPackage().getCaster(), this.getPackage().getCaster()), this.getSettingValue("power") * finalPower * 1.5F
               );
         } else {
            ((EntityLivingBase)target.field_72308_g).func_70691_i(this.getSettingValue("power") * finalPower);
         }
      }

      return false;
   }

   @Override
   public NodeSetting[] createSettings() {
      return new NodeSetting[]{new NodeSetting("power", "focus.heal.power", new NodeSetting.NodeSettingIntRange(1, 5))};
   }

   @Override
   public void onCast(Entity caster) {
      caster.field_70170_p
         .func_184133_a(
            null,
            caster.func_180425_c().func_177984_a(),
            SoundEvents.field_187542_ac,
            SoundCategory.PLAYERS,
            2.0F,
            2.0F + (float)(caster.field_70170_p.field_73012_v.nextGaussian() * 0.1F)
         );
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
      FXGeneric fb = new FXGeneric(
         world,
         x,
         y,
         z,
         vx + world.field_73012_v.nextGaussian() * 0.01,
         vy + world.field_73012_v.nextGaussian() * 0.01,
         vz + world.field_73012_v.nextGaussian() * 0.01
      );
      fb.func_187114_a((int)(10.0F + 10.0F * world.field_73012_v.nextFloat()));
      fb.func_70538_b(1.0F, 1.0F, 1.0F);
      fb.setAlphaF(0.0F, 0.7F, 0.7F, 0.0F);
      fb.setGridSize(64);
      fb.setParticles(0, 1, 1);
      fb.setScale(world.field_73012_v.nextFloat() * 2.0F, world.field_73012_v.nextFloat());
      fb.setSlowDown(0.8);
      fb.setGravity((float)(world.field_73012_v.nextGaussian() * 0.1F));
      fb.setRandomMovementScale(0.0125F, 0.0125F, 0.0125F);
      fb.setRotationSpeed((float)world.field_73012_v.nextGaussian());
      ParticleEngine.addEffectWithDelay(world, fb, world.field_73012_v.nextInt(4));
   }
}
