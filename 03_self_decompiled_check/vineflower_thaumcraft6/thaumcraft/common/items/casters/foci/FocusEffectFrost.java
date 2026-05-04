package thaumcraft.common.items.casters.foci;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
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

public class FocusEffectFrost extends FocusEffect {
   @Override
   public String getResearch() {
      return "FOCUSELEMENTAL";
   }

   @Override
   public String getKey() {
      return "thaumcraft.FROST";
   }

   @Override
   public Aspect getAspect() {
      return Aspect.COLD;
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
         float damage = this.getDamageForDisplay(finalPower);
         int duration = 20 * this.getSettingValue("duration");
         int potency = (int)(1.0F + this.getSettingValue("power") * finalPower / 3.0F);
         target.field_72308_g
            .func_70097_a(
               DamageSource.func_76356_a(
                  (Entity)(target.field_72308_g != null ? target.field_72308_g : this.getPackage().getCaster()), this.getPackage().getCaster()
               ),
               damage
            );
         if (target.field_72308_g instanceof EntityLivingBase) {
            ((EntityLivingBase)target.field_72308_g).func_70690_d(new PotionEffect(MobEffects.field_76421_d, duration, potency));
         }
      } else if (target.field_72313_a == Type.BLOCK) {
         float f = Math.min(16.0F, 2 * this.getSettingValue("power") * finalPower);

         for (MutableBlockPos blockpos$mutableblockpos1 : BlockPos.func_177975_b(
            target.func_178782_a().func_177963_a(-f, -f, -f), target.func_178782_a().func_177963_a(f, f, f)
         )) {
            if (blockpos$mutableblockpos1.func_177957_d(
                  target.field_72307_f.field_72450_a, target.field_72307_f.field_72448_b, target.field_72307_f.field_72449_c
               )
               <= f * f) {
               IBlockState iblockstate1 = this.getPackage().world.func_180495_p(blockpos$mutableblockpos1);
               if (iblockstate1.func_185904_a() == Material.field_151586_h
                  && (Integer)iblockstate1.func_177229_b(BlockLiquid.field_176367_b) == 0
                  && this.getPackage().world.func_190527_a(Blocks.field_185778_de, blockpos$mutableblockpos1, false, EnumFacing.DOWN, (Entity)null)) {
                  this.getPackage().world.func_175656_a(blockpos$mutableblockpos1, Blocks.field_185778_de.func_176223_P());
                  this.getPackage()
                     .world
                     .func_175684_a(
                        blockpos$mutableblockpos1.func_185334_h(),
                        Blocks.field_185778_de,
                        MathHelper.func_76136_a(this.getPackage().world.field_73012_v, 60, 120)
                     );
               }
            }
         }
      }

      return false;
   }

   @Override
   public NodeSetting[] createSettings() {
      return new NodeSetting[]{
         new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)),
         new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(2, 10))
      };
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void renderParticleFX(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
      FXGeneric fb = new FXGeneric(world, posX, posY, posZ, motionX, motionY, motionZ);
      fb.func_187114_a(40 + world.field_73012_v.nextInt(40));
      fb.setAlphaF(1.0F, 0.0F);
      fb.setParticles(8, 1, 1);
      fb.setGravity(0.033F);
      fb.setSlowDown(0.8);
      fb.setRandomMovementScale(0.0025F, 1.0E-4F, 0.0025F);
      fb.setScale((float)(0.7F + world.field_73012_v.nextGaussian() * 0.3F));
      fb.setRotationSpeed(world.field_73012_v.nextFloat() * 3.0F, (float)world.field_73012_v.nextGaussian() / 4.0F);
      ParticleEngine.addEffectWithDelay(world, fb, 0);
   }

   @Override
   public void onCast(Entity caster) {
      caster.field_70170_p
         .func_184133_a(
            null,
            caster.func_180425_c().func_177984_a(),
            SoundEvents.field_187942_hp,
            SoundCategory.PLAYERS,
            0.2F,
            1.0F + (float)(caster.field_70170_p.field_73012_v.nextGaussian() * 0.05F)
         );
   }
}
