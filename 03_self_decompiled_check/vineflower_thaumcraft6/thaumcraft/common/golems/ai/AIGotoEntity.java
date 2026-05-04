package thaumcraft.common.golems.ai;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.tasks.TaskHandler;

public class AIGotoEntity extends AIGoto {
   public AIGotoEntity(EntityThaumcraftGolem g) {
      super(g, (byte)1);
   }

   @Override
   public void func_75246_d() {
      super.func_75246_d();
      if (this.golem.func_70671_ap() != null && this.golem.getTask() != null && this.golem.getTask().getEntity() != null) {
         this.golem.func_70671_ap().func_75651_a(this.golem.getTask().getEntity(), 10.0F, this.golem.func_70646_bf());
      }
   }

   @Override
   protected void moveTo() {
      if (this.golem.func_70661_as() != null && this.golem.getTask() != null && this.golem.getTask().getEntity() != null) {
         this.golem.func_70661_as().func_75497_a(this.golem.getTask().getEntity(), this.golem.getGolemMoveSpeed());
      }
   }

   @Override
   protected boolean findDestination() {
      for (Task ticket : TaskHandler.getEntityTasksSorted(this.golem.field_70170_p.field_73011_w.getDimension(), this.golem.func_110124_au(), this.golem)) {
         if (this.areGolemTagsValidForTask(ticket)
            && ticket.canGolemPerformTask(this.golem)
            && this.golem.func_180485_d(ticket.getEntity().func_180425_c())
            && this.isValidDestination(this.golem.field_70170_p, ticket.getEntity().func_180425_c())
            && this.canEasilyReach(ticket.getEntity())) {
            this.golem.setTask(ticket);
            this.golem.getTask().setReserved(true);
            this.minDist = 3.5 + this.golem.getTask().getEntity().field_70130_N / 2.0F * (this.golem.getTask().getEntity().field_70130_N / 2.0F);
            if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
               this.golem.field_70170_p.func_72960_a(this.golem, (byte)5);
            }

            return true;
         }
      }

      return false;
   }

   private boolean canEasilyReach(Entity e) {
      if (this.golem.func_70068_e(e) < this.minDist) {
         return true;
      }

      Path pathentity = this.golem.func_70661_as().func_75494_a(e);
      if (pathentity == null) {
         return false;
      }

      PathPoint pathpoint = pathentity.func_75870_c();
      if (pathpoint == null) {
         return false;
      }

      int i = pathpoint.field_75839_a - MathHelper.func_76128_c(e.field_70165_t);
      int j = pathpoint.field_75838_c - MathHelper.func_76128_c(e.field_70161_v);
      return i * i + j * j < this.minDist;
   }
}
