package thaumcraft.common.golems.seals;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.utils.InventoryUtils;

public class SealFill extends SealFiltered {
   int delay = new Random(System.nanoTime()).nextInt(50);
   int watchedTask = Integer.MIN_VALUE;
   protected ISealConfigToggles.SealToggle[] props = new ISealConfigToggles.SealToggle[]{
      new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"),
      new ISealConfigToggles.SealToggle(true, "pnbt", "golem.prop.nbt"),
      new ISealConfigToggles.SealToggle(false, "pore", "golem.prop.ore"),
      new ISealConfigToggles.SealToggle(false, "pmod", "golem.prop.mod"),
      new ISealConfigToggles.SealToggle(false, "pexist", "golem.prop.exist")
   };
   ResourceLocation icon = new ResourceLocation("thaumcraft", "items/seals/seal_fill");

   @Override
   public String getKey() {
      return "thaumcraft:fill";
   }

   @Override
   public void tickSeal(World world, ISealEntity seal) {
      if (this.delay++ % 20 == 0) {
         Task oldTask = TaskHandler.getTask(world.field_73011_w.getDimension(), this.watchedTask);
         if (oldTask == null || oldTask.isReserved() || oldTask.isSuspended() || oldTask.isCompleted()) {
            Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask(world.field_73011_w.getDimension(), task);
            this.watchedTask = task.getId();
         }
      }
   }

   @Override
   public void onTaskStarted(World world, IGolemAPI golem, Task task) {
      ISealEntity se = SealHandler.getSealEntity(world.field_73011_w.getDimension(), task.getSealPos());
      if (se != null && !se.isStoppedByRedstone(world)) {
         Task newTask = new Task(task.getSealPos(), task.getSealPos().pos);
         newTask.setPriority(se.getPriority());
         TaskHandler.addTask(world.field_73011_w.getDimension(), newTask);
         this.watchedTask = newTask.getId();
      }
   }

   @Override
   public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
      ThaumcraftInvHelper.InvFilter filter = new ThaumcraftInvHelper.InvFilter(
         !this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value
      );
      Tuple<ItemStack, Integer> tuple = InventoryUtils.findFirstMatchFromFilterTuple(
         this.getInv(), this.getSizes(), this.isBlacklist(), golem.getCarrying(), filter
      );
      if (tuple.func_76341_a() != null && !((ItemStack)tuple.func_76341_a()).func_190926_b()) {
         IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(world, task.getSealPos().pos, task.getSealPos().face);
         int limit = ((ItemStack)tuple.func_76341_a()).func_190916_E();
         if (this.hasStacksizeLimiters() && tuple.func_76340_b() != null && (Integer)tuple.func_76340_b() > 0) {
            int c = inv == null
               ? InventoryUtils.countStackInWorld(golem.getGolemWorld(), task.getSealPos().pos, (ItemStack)tuple.func_76341_a(), 1.5, filter)
               : ThaumcraftInvHelper.countTotalItemsIn(inv, (ItemStack)tuple.func_76341_a(), filter);
            if (c < (Integer)tuple.func_76340_b()) {
               limit = (Integer)tuple.func_76340_b() - c;
            } else {
               limit = 0;
            }
         }

         if (limit > 0) {
            ItemStack t = ((ItemStack)tuple.func_76341_a()).func_77946_l();
            t.func_190920_e(limit);
            ItemStack s = golem.dropItem(t);
            if (inv == null) {
               EntityItem ie = new EntityItem(
                  world,
                  task.getSealPos().pos.func_177958_n() + 0.5 + task.getSealPos().face.func_82601_c(),
                  task.getSealPos().pos.func_177956_o() + 0.5 + task.getSealPos().face.func_96559_d(),
                  task.getSealPos().pos.func_177952_p() + 0.5 + task.getSealPos().face.func_82599_e(),
                  s
               );
               ie.field_70159_w /= 5.0;
               ie.field_70181_x /= 2.0;
               ie.field_70179_y /= 5.0;
               world.func_72838_d(ie);
            } else {
               golem.holdItem(ItemHandlerHelper.insertItemStacked(inv, s, false));
            }

            ((Entity)golem)
               .func_184185_a(SoundEvents.field_187638_cR, 0.125F, ((world.field_73012_v.nextFloat() - world.field_73012_v.nextFloat()) * 0.7F + 1.0F) * 1.0F);
            golem.addRankXp(1);
            golem.swingArm();
         }
      }

      task.setSuspended(true);
      return true;
   }

   @Override
   public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
      ThaumcraftInvHelper.InvFilter filter = new ThaumcraftInvHelper.InvFilter(
         !this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value
      );
      Tuple<ItemStack, Integer> tuple = InventoryUtils.findFirstMatchFromFilterTuple(
         this.getInv(), this.getSizes(), this.isBlacklist(), golem.getCarrying(), filter
      );
      if (tuple.func_76341_a() != null && !((ItemStack)tuple.func_76341_a()).func_190926_b()) {
         IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(golem.getGolemWorld(), task.getSealPos().pos, task.getSealPos().face);
         if (inv != null) {
            if (tuple.func_76341_a() != null
               && !((ItemStack)tuple.func_76341_a()).func_190926_b()
               && this.props[4].value
               && ThaumcraftInvHelper.countTotalItemsIn(inv, (ItemStack)tuple.func_76341_a(), filter) <= 0) {
               return false;
            }

            if (tuple.func_76341_a() != null
               && !((ItemStack)tuple.func_76341_a()).func_190926_b()
               && ThaumcraftInvHelper.hasRoomForSome(golem.getGolemWorld(), task.getSealPos().pos, task.getSealPos().face, (ItemStack)tuple.func_76341_a())) {
               if (!this.hasStacksizeLimiters() || tuple.func_76340_b() == null || (Integer)tuple.func_76340_b() <= 0) {
                  return true;
               }

               if (ThaumcraftInvHelper.countTotalItemsIn(inv, (ItemStack)tuple.func_76341_a(), filter) < (Integer)tuple.func_76340_b()) {
                  return true;
               }
            }
         } else if (tuple.func_76341_a() != null && !((ItemStack)tuple.func_76341_a()).func_190926_b()) {
            if (this.hasStacksizeLimiters() && tuple.func_76340_b() != null && (Integer)tuple.func_76340_b() > 0) {
               return InventoryUtils.countStackInWorld(golem.getGolemWorld(), task.getSealPos().pos, (ItemStack)tuple.func_76341_a(), 1.5, filter)
                  < (Integer)tuple.func_76340_b();
            }

            return true;
         }
      }

      return false;
   }

   @Override
   public boolean canPlaceAt(World world, BlockPos pos, EnumFacing side) {
      return !world.func_175623_d(pos);
   }

   @Override
   public ResourceLocation getSealIcon() {
      return this.icon;
   }

   @Override
   public int[] getGuiCategories() {
      return new int[]{1, 0, 4};
   }

   @Override
   public EnumGolemTrait[] getRequiredTags() {
      return null;
   }

   @Override
   public EnumGolemTrait[] getForbiddenTags() {
      return new EnumGolemTrait[]{EnumGolemTrait.CLUMSY};
   }

   @Override
   public void onTaskSuspension(World world, Task task) {
   }

   @Override
   public void onRemoval(World world, BlockPos pos, EnumFacing side) {
   }

   @Override
   public boolean hasStacksizeLimiters() {
      return !this.isBlacklist();
   }
}
