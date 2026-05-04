package thaumcraft.api.golems;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;

public class ProvisionRequest {
   private ISealEntity seal;
   private Entity entity;
   private BlockPos pos;
   private EnumFacing side;
   private ItemStack stack;
   private int id;
   private int ui = 0;
   private Task linkedTask;
   private boolean invalid;
   private long timeout;

   ProvisionRequest(ISealEntity seal, ItemStack stack) {
      this.seal = seal;
      this.stack = stack.func_77946_l();
      String s = seal.getSealPos().pos.toString() + seal.getSealPos().face.name() + stack.toString();
      if (stack.func_77942_o()) {
         s = s + stack.func_77978_p().toString();
      }

      this.id = s.hashCode();
      this.timeout = System.currentTimeMillis() + 10000L;
   }

   ProvisionRequest(BlockPos pos, EnumFacing side, ItemStack stack) {
      this.pos = pos;
      this.side = side;
      this.stack = stack.func_77946_l();
      String s = pos.toString() + side.name() + stack.toString();
      if (stack.func_77942_o()) {
         s = s + stack.func_77978_p().toString();
      }

      this.id = s.hashCode();
      this.timeout = System.currentTimeMillis() + 10000L;
   }

   ProvisionRequest(Entity entity, ItemStack stack) {
      this.entity = entity;
      this.stack = stack.func_77946_l();
      String s = entity.func_145782_y() + stack.toString();
      if (stack.func_77942_o()) {
         s = s + stack.func_77978_p().toString();
      }

      this.id = s.hashCode();
      this.timeout = System.currentTimeMillis() + 10000L;
   }

   public long getTimeout() {
      return this.timeout;
   }

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public void setUI(int ui) {
      this.ui = ui;
   }

   public ISealEntity getSeal() {
      return this.seal;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public void setPos(BlockPos pos) {
      this.pos = pos;
   }

   public EnumFacing getSide() {
      return this.side;
   }

   public void setSide(EnumFacing side) {
      this.side = side;
   }

   public Task getLinkedTask() {
      return this.linkedTask;
   }

   public void setLinkedTask(Task linkedTask) {
      this.linkedTask = linkedTask;
      this.timeout = System.currentTimeMillis() + 120000L;
   }

   public boolean isInvalid() {
      return this.invalid;
   }

   public void setInvalid(boolean invalid) {
      this.invalid = invalid;
   }

   @Override
   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      }

      if (!(p_equals_1_ instanceof ProvisionRequest)) {
         return false;
      }

      ProvisionRequest pr = (ProvisionRequest)p_equals_1_;
      return this.id == pr.id && this.ui == pr.ui;
   }

   private boolean isItemStackEqual(ItemStack first, ItemStack other) {
      return first.func_190916_E() != other.func_190916_E()
         ? false
         : (
            first.func_77973_b() != other.func_77973_b()
               ? false
               : (
                  first.func_77952_i() != other.func_77952_i()
                     ? false
                     : (
                        first.func_77978_p() == null && other.func_77978_p() != null
                           ? false
                           : first.func_77978_p() == null || first.func_77978_p().equals(other.func_77978_p())
                     )
               )
         );
   }
}
