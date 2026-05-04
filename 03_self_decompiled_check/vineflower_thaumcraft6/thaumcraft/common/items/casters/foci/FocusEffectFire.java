package thaumcraft.common.items.casters.foci;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
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
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;

public class FocusEffectFire extends FocusEffect {
   @Override
   public String getResearch() {
      return "BASEAUROMANCY";
   }

   @Override
   public String getKey() {
      return "thaumcraft.FIRE";
   }

   @Override
   public Aspect getAspect() {
      return Aspect.FIRE;
   }

   @Override
   public int getComplexity() {
      return this.getSettingValue("duration") + this.getSettingValue("power") * 2;
   }

   @Override
   public float getDamageForDisplay(float finalPower) {
      return (3 + this.getSettingValue("power")) * finalPower;
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
      if (target.field_72313_a == Type.ENTITY && target.field_72308_g != null) {
         if (target.field_72308_g.func_70045_F()) {
            return false;
         }

         float fire = 1 + this.getSettingValue("duration") * this.getSettingValue("duration");
         float damage = this.getDamageForDisplay(finalPower);
         fire *= finalPower;
         target.field_72308_g
            .func_70097_a(
               new EntityDamageSourceIndirect(
                     "fireball", (Entity)(target.field_72308_g != null ? target.field_72308_g : this.getPackage().getCaster()), this.getPackage().getCaster()
                  )
                  .func_76361_j(),
               damage
            );
         if (fire > 0.0F) {
            target.field_72308_g.func_70015_d(Math.round(fire));
         }

         return true;
      } else {
         if (target.field_72313_a == Type.BLOCK && this.getSettingValue("duration") > 0) {
            BlockPos pos = target.func_178782_a();
            pos = pos.func_177972_a(target.field_178784_b);
            if (this.getPackage().world.func_175623_d(pos) && this.getPackage().world.field_73012_v.nextFloat() < finalPower) {
               this.getPackage()
                  .world
                  .func_184133_a(
                     null, pos, SoundEvents.field_187649_bu, SoundCategory.BLOCKS, 1.0F, this.getPackage().world.field_73012_v.nextFloat() * 0.4F + 0.8F
                  );
               this.getPackage().world.func_180501_a(pos, Blocks.field_150480_ab.func_176223_P(), 11);
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public NodeSetting[] createSettings() {
      return new NodeSetting[]{
         new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)),
         new NodeSetting("duration", "focus.fire.burn", new NodeSetting.NodeSettingIntRange(0, 5))
      };
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void renderParticleFX(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
      FXDispatcher.GenPart pp = new FXDispatcher.GenPart();
      pp.grav = -0.2F;
      pp.age = 10;
      pp.alpha = new float[]{0.7F};
      pp.partStart = 640;
      pp.partInc = 1;
      pp.partNum = 10;
      pp.slowDown = 0.75;
      pp.scale = new float[]{(float)(1.5 + world.field_73012_v.nextGaussian() * 0.2F)};
      FXDispatcher.INSTANCE.drawGenericParticles(posX, posY, posZ, motionX, motionY, motionZ, pp);
   }

   @Override
   public void onCast(Entity caster) {
      caster.field_70170_p
         .func_184133_a(
            null,
            caster.func_180425_c().func_177984_a(),
            SoundEvents.field_187616_bj,
            SoundCategory.PLAYERS,
            1.0F,
            1.0F + (float)(caster.field_70170_p.field_73012_v.nextGaussian() * 0.05F)
         );
   }
}
