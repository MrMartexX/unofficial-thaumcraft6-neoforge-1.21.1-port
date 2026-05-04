package thaumcraft.common.items.casters.foci;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.tiles.misc.TileHole;

public class FocusEffectRift extends FocusEffect {
   @Override
   public String getResearch() {
      return "FOCUSRIFT";
   }

   @Override
   public String getKey() {
      return "thaumcraft.RIFT";
   }

   @Override
   public Aspect getAspect() {
      return Aspect.ELDRITCH;
   }

   @Override
   public int getComplexity() {
      return 3 + this.getSettingValue("duration") / 2 + this.getSettingValue("depth") / 4;
   }

   @Override
   public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
      if (target.field_72313_a != Type.BLOCK) {
         return false;
      }

      if (this.getPackage().world.field_73011_w.getDimension() == ModConfig.CONFIG_WORLD.dimensionOuterId) {
         this.getPackage()
            .world
            .func_184148_a(
               null,
               target.func_178782_a().func_177958_n() + 0.5,
               target.func_178782_a().func_177956_o() + 0.5,
               target.func_178782_a().func_177952_p() + 0.5,
               SoundsTC.wandfail,
               SoundCategory.PLAYERS,
               1.0F,
               1.0F
            );
         return false;
      }

      float maxdis = this.getSettingValue("depth") * finalPower;
      int dur = 20 * this.getSettingValue("duration");
      int distance = 0;
      BlockPos pos = new BlockPos(target.func_178782_a());

      for (distance = 0; distance < maxdis; distance++) {
         IBlockState bi = this.getPackage().world.func_180495_p(pos);
         if (BlockUtils.isPortableHoleBlackListed(bi)
            || bi.func_177230_c() == Blocks.field_150357_h
            || bi.func_177230_c() == BlocksTC.hole
            || bi.func_177230_c().isAir(bi, this.getPackage().world, pos)
            || bi.func_185887_b(this.getPackage().world, pos) == -1.0F) {
            break;
         }

         pos = pos.func_177972_a(target.field_178784_b.func_176734_d());
      }

      createHole(this.getPackage().world, target.func_178782_a(), target.field_178784_b, (byte)Math.round(distance + 1), dur);
      return true;
   }

   public static boolean createHole(World world, BlockPos pos, EnumFacing side, byte count, int max) {
      IBlockState bs = world.func_180495_p(pos);
      if (!world.field_72995_K
         && world.func_175625_s(pos) == null
         && !BlockUtils.isPortableHoleBlackListed(bs)
         && bs.func_177230_c() != Blocks.field_150357_h
         && bs.func_177230_c() != BlocksTC.hole
         && (bs.func_177230_c().isAir(bs, world, pos) || !bs.func_177230_c().func_176196_c(world, pos))
         && bs.func_185887_b(world, pos) != -1.0F) {
         if (world.func_175656_a(pos, BlocksTC.hole.func_176223_P())) {
            TileHole ts = (TileHole)world.func_175625_s(pos);
            ts.oldblock = bs;
            ts.countdownmax = (short)max;
            ts.count = count;
            ts.direction = side;
            ts.func_70296_d();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public NodeSetting[] createSettings() {
      int[] depth = new int[]{8, 16, 24, 32};
      String[] depthDesc = new String[]{"8", "16", "24", "32"};
      return new NodeSetting[]{
         new NodeSetting("depth", "focus.rift.depth", new NodeSetting.NodeSettingIntList(depth, depthDesc)),
         new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(2, 10))
      };
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void renderParticleFX(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
      FXGeneric fb = new FXGeneric(world, posX, posY, posZ, motionX, motionY, motionZ);
      fb.func_187114_a(16 + world.field_73012_v.nextInt(16));
      fb.setParticles(384 + world.field_73012_v.nextInt(16), 1, 1);
      fb.setSlowDown(0.75);
      fb.setAlphaF(1.0F, 0.0F);
      fb.setScale((float)(0.7F + world.field_73012_v.nextGaussian() * 0.3F));
      fb.func_70538_b(0.25F, 0.25F, 1.0F);
      fb.setRandomMovementScale(0.01F, 0.01F, 0.01F);
      ParticleEngine.addEffectWithDelay(world, fb, 0);
   }

   @Override
   public void onCast(Entity caster) {
      caster.field_70170_p.func_184133_a(null, caster.func_180425_c().func_177984_a(), SoundEvents.field_190021_aL, SoundCategory.PLAYERS, 0.2F, 0.7F);
   }
}
