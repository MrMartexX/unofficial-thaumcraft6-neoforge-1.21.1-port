package thaumcraft.common.items.casters.foci;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
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
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;

public class FocusEffectAir extends FocusEffect {
   @Override
   public String getResearch() {
      return "FOCUSELEMENTAL";
   }

   @Override
   public String getKey() {
      return "thaumcraft.AIR";
   }

   @Override
   public Aspect getAspect() {
      return Aspect.AIR;
   }

   @Override
   public int getComplexity() {
      return this.getSettingValue("power") * 2;
   }

   @Override
   public float getDamageForDisplay(float finalPower) {
      return (1 + this.getSettingValue("power")) * finalPower;
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
      this.getPackage()
         .world
         .func_184148_a(
            null,
            target.field_72307_f.field_72450_a,
            target.field_72307_f.field_72448_b,
            target.field_72307_f.field_72449_c,
            SoundEvents.field_187524_aN,
            SoundCategory.PLAYERS,
            0.5F,
            0.66F
         );
      if (target.field_72313_a == Type.ENTITY && target.field_72308_g != null) {
         float damage = this.getDamageForDisplay(finalPower);
         target.field_72308_g
            .func_70097_a(
               DamageSource.func_76356_a(
                  (Entity)(target.field_72308_g != null ? target.field_72308_g : this.getPackage().getCaster()), this.getPackage().getCaster()
               ),
               damage
            );
         if (target.field_72308_g instanceof EntityLivingBase) {
            if (trajectory != null) {
               ((EntityLivingBase)target.field_72308_g)
                  .func_70653_a(this.getPackage().getCaster(), damage * 0.25F, -trajectory.direction.field_72450_a, -trajectory.direction.field_72449_c);
            } else {
               ((EntityLivingBase)target.field_72308_g)
                  .func_70653_a(
                     this.getPackage().getCaster(),
                     damage * 0.25F,
                     -MathHelper.func_76126_a(target.field_72308_g.field_70177_z * (float) (Math.PI / 180.0)),
                     MathHelper.func_76134_b(target.field_72308_g.field_70177_z * (float) (Math.PI / 180.0))
                  );
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public NodeSetting[] createSettings() {
      return new NodeSetting[]{new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5))};
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void renderParticleFX(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
      FXDispatcher.GenPart pp = new FXDispatcher.GenPart();
      pp.grav = -0.1F;
      pp.age = 20 + world.field_73012_v.nextInt(10);
      pp.alpha = new float[]{0.5F, 0.0F};
      pp.grid = 32;
      pp.partStart = 337;
      pp.partInc = 1;
      pp.partNum = 5;
      pp.slowDown = 0.75;
      pp.rot = (float)world.field_73012_v.nextGaussian() / 2.0F;
      float s = (float)(2.0 + world.field_73012_v.nextGaussian() * 0.5);
      pp.scale = new float[]{s, s * 2.0F};
      FXDispatcher.INSTANCE.drawGenericParticles(posX, posY, posZ, motionX, motionY, motionZ, pp);
   }

   @Override
   public void onCast(Entity caster) {
      caster.field_70170_p.func_184133_a(null, caster.func_180425_c().func_177984_a(), SoundsTC.wind, SoundCategory.PLAYERS, 0.125F, 2.0F);
   }
}
