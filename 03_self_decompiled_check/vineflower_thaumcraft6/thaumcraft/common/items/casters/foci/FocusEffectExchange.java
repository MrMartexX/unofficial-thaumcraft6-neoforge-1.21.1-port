package thaumcraft.common.items.casters.foci;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.IFocusBlockPicker;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.lib.events.ServerEvents;

public class FocusEffectExchange extends FocusEffect implements IFocusBlockPicker {
   @Override
   public String getResearch() {
      return "FOCUSEXCHANGE";
   }

   @Override
   public String getKey() {
      return "thaumcraft.EXCHANGE";
   }

   @Override
   public Aspect getAspect() {
      return Aspect.EXCHANGE;
   }

   @Override
   public int getComplexity() {
      return 5 + this.getSettingValue("silk") * 4 + this.getSettingValue("fortune") == 0 ? 0 : (this.getSettingValue("fortune") + 1) * 3;
   }

   @Override
   public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int num) {
      if (target.field_72313_a != Type.BLOCK) {
         return false;
      }

      ItemStack casterStack = ItemStack.field_190927_a;
      if (this.getPackage().getCaster().func_184614_ca() != null && this.getPackage().getCaster().func_184614_ca().func_77973_b() instanceof ItemCaster) {
         casterStack = this.getPackage().getCaster().func_184614_ca();
      } else if (this.getPackage().getCaster().func_184592_cb() != null && this.getPackage().getCaster().func_184592_cb().func_77973_b() instanceof ItemCaster) {
         casterStack = this.getPackage().getCaster().func_184592_cb();
      }

      if (casterStack.func_190926_b()) {
         return false;
      }

      boolean silk = this.getSettingValue("silk") > 0;
      int fortune = this.getSettingValue("fortune");
      if (this.getPackage().getCaster() instanceof EntityPlayer
         && ((ItemCaster)casterStack.func_77973_b()).getPickedBlock(casterStack) != null
         && !((ItemCaster)casterStack.func_77973_b()).getPickedBlock(casterStack).func_190926_b()) {
         ServerEvents.addSwapper(
            this.getPackage().world,
            target.func_178782_a(),
            this.getPackage().world.func_180495_p(target.func_178782_a()),
            ((ItemCaster)casterStack.func_77973_b()).getPickedBlock(casterStack),
            true,
            0,
            (EntityPlayer)this.getPackage().getCaster(),
            true,
            false,
            8038177,
            true,
            silk,
            fortune,
            ServerEvents.DEFAULT_PREDICATE,
            0.25F + (silk ? 0.25F : 0.0F) + fortune * 0.1F
         );
      }

      return true;
   }

   @Override
   public NodeSetting[] createSettings() {
      int[] silk = new int[]{0, 1};
      String[] silkDesc = new String[]{"focus.common.no", "focus.common.yes"};
      int[] fortune = new int[]{0, 1, 2, 3, 4};
      String[] fortuneDesc = new String[]{"focus.common.no", "I", "II", "III", "IV"};
      return new NodeSetting[]{
         new NodeSetting("fortune", "focus.common.fortune", new NodeSetting.NodeSettingIntList(fortune, fortuneDesc)),
         new NodeSetting("silk", "focus.common.silk", new NodeSetting.NodeSettingIntList(silk, silkDesc))
      };
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
      fb.func_187114_a(9);
      fb.func_70538_b(
         0.25F + world.field_73012_v.nextFloat() * 0.25F, 0.25F + world.field_73012_v.nextFloat() * 0.25F, 0.25F + world.field_73012_v.nextFloat() * 0.25F
      );
      fb.setAlphaF(0.0F, 0.6F, 0.6F, 0.0F);
      fb.setGridSize(64);
      fb.setParticles(448, 9, 1);
      fb.setScale(0.5F, 0.25F);
      fb.setGravity((float)(world.field_73012_v.nextGaussian() * 0.01F));
      fb.setRandomMovementScale(0.0025F, 0.0025F, 0.0025F);
      ParticleEngine.addEffect(world, fb);
   }

   @Override
   public void onCast(Entity caster) {
      caster.field_70170_p
         .func_184133_a(
            null,
            caster.func_180425_c().func_177984_a(),
            SoundEvents.field_190021_aL,
            SoundCategory.PLAYERS,
            0.2F,
            2.0F + (float)(caster.field_70170_p.field_73012_v.nextGaussian() * 0.05F)
         );
   }
}
