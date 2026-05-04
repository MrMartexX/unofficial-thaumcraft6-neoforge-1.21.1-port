package thaumcraft.api.casters;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class FocusEffect extends FocusNode {
   @Override
   public IFocusElement.EnumUnitType getType() {
      return IFocusElement.EnumUnitType.EFFECT;
   }

   @Override
   public final FocusNode.EnumSupplyType[] mustBeSupplied() {
      return new FocusNode.EnumSupplyType[]{FocusNode.EnumSupplyType.TARGET};
   }

   @Override
   public FocusNode.EnumSupplyType[] willSupply() {
      return null;
   }

   public abstract boolean execute(RayTraceResult var1, @Nullable Trajectory var2, float var3, int var4);

   public float getDamageForDisplay(float finalPower) {
      return 0.0F;
   }

   public abstract void renderParticleFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12);

   public void onCast(Entity caster) {
   }
}
