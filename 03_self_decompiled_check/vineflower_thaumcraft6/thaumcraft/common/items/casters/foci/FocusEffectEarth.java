package thaumcraft.common.items.casters.foci;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
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
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;

public class FocusEffectEarth extends FocusEffect {
   @Override
   public String getResearch() {
      return "FOCUSELEMENTAL";
   }

   @Override
   public String getKey() {
      return "thaumcraft.EARTH";
   }

   @Override
   public Aspect getAspect() {
      return Aspect.EARTH;
   }

   @Override
   public int getComplexity() {
      return this.getSettingValue("power") * 3;
   }

   @Override
   public float getDamageForDisplay(float finalPower) {
      return 2 * this.getSettingValue("power") * finalPower;
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
         float damage = this.getDamageForDisplay(finalPower);
         target.field_72308_g
            .func_70097_a(
               DamageSource.func_76356_a(
                  (Entity)(target.field_72308_g != null ? target.field_72308_g : this.getPackage().getCaster()), this.getPackage().getCaster()
               ),
               damage
            );
         return true;
      }

      if (target.field_72313_a == Type.BLOCK) {
         BlockPos pos = target.func_178782_a();
         if (this.getPackage().getCaster() instanceof EntityPlayer
            && this.getPackage().world.func_180495_p(pos).func_185887_b(this.getPackage().world, pos) <= this.getDamageForDisplay(finalPower) / 25.0F) {
            ServerEvents.addBreaker(
               this.getPackage().world,
               pos,
               this.getPackage().world.func_180495_p(pos),
               (EntityPlayer)this.getPackage().getCaster(),
               false,
               false,
               0,
               1.0F,
               0.0F,
               1.0F,
               num,
               0.1F,
               null
            );
         }
      }

      return false;
   }

   @Override
   public NodeSetting[] createSettings() {
      return new NodeSetting[]{new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5))};
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void renderParticleFX(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
      FXDispatcher.GenPart pp = new FXDispatcher.GenPart();
      pp.grav = 0.4F;
      pp.layer = 1;
      pp.age = 20 + world.field_73012_v.nextInt(10);
      pp.alpha = new float[]{1.0F, 0.0F};
      pp.partStart = 75 + world.field_73012_v.nextInt(4);
      pp.partInc = 1;
      pp.partNum = 1;
      pp.slowDown = 0.9;
      pp.rot = (float)world.field_73012_v.nextGaussian();
      float s = (float)(1.0 + world.field_73012_v.nextGaussian() * 0.2F);
      pp.scale = new float[]{s, s / 2.0F};
      FXDispatcher.INSTANCE.drawGenericParticles(posX, posY, posZ, motionX, motionY, motionZ, pp);
   }

   @Override
   public void onCast(Entity caster) {
      caster.field_70170_p
         .func_184133_a(
            null,
            caster.func_180425_c().func_177984_a(),
            SoundEvents.field_187523_aM,
            SoundCategory.PLAYERS,
            0.25F,
            1.0F + (float)(caster.field_70170_p.field_73012_v.nextGaussian() * 0.05F)
         );
   }
}
