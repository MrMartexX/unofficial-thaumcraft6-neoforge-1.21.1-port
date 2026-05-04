package thaumcraft.common.lib;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.entities.projectile.EntityAlumentum;

public class BehaviorDispenseAlumetum extends BehaviorProjectileDispense {
   protected IProjectile func_82499_a(World worldIn, IPosition position, ItemStack stackIn) {
      return new EntityAlumentum(worldIn, position.func_82615_a(), position.func_82617_b(), position.func_82616_c());
   }
}
