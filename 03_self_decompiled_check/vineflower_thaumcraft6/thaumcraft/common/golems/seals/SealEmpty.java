package thaumcraft.common.golems.seals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.utils.InventoryUtils;

public class SealEmpty extends SealFiltered {
   int delay = new Random(System.nanoTime()).nextInt(30);
   int filterInc = 0;
   HashMap<Integer, ItemStack> cache = new HashMap<>();
   ResourceLocation icon = new ResourceLocation("thaumcraft", "items/seals/seal_empty");
   protected ISealConfigToggles.SealToggle[] props = new ISealConfigToggles.SealToggle[]{
      new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"),
      new ISealConfigToggles.SealToggle(true, "pnbt", "golem.prop.nbt"),
      new ISealConfigToggles.SealToggle(false, "pore", "golem.prop.ore"),
      new ISealConfigToggles.SealToggle(false, "pmod", "golem.prop.mod"),
      new ISealConfigToggles.SealToggle(false, "pcycle", "golem.prop.cycle"),
      new ISealConfigToggles.SealToggle(false, "pleave", "golem.prop.leave")
   };

   @Override
   public String getKey() {
      return "thaumcraft:empty";
   }

   @Override
   public void tickSeal(World world, ISealEntity seal) {
      if (this.delay % 100 == 0) {
         Iterator<Integer> it = this.cache.keySet().iterator();

         while (it.hasNext()) {
            Task t = TaskHandler.getTask(world.field_73011_w.getDimension(), it.next());
            if (t == null) {
               it.remove();
            }
         }
      }

      if (this.delay++ % 20 == 0) {
         ItemStack stack = InventoryUtils.findFirstMatchFromFilter(
            this.getInv(this.filterInc),
            this.isBlacklist(),
            ThaumcraftInvHelper.getItemHandlerAt(world, seal.getSealPos().pos, seal.getSealPos().face),
            seal.getSealPos().face,
            new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value),
            this.props[5].value
         );
         if (stack != null && !stack.func_190926_b()) {
            Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
            task.setPriority(seal.getPriority());
            task.setLifespan((short)5);
            TaskHandler.addTask(world.field_73011_w.getDimension(), task);
            this.cache.put(task.getId(), stack);
         }
      }
   }

   @Override
   public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
      ItemStack stack = this.cache.get(task.getId());
      int sa = ThaumcraftInvHelper.countTotalItemsIn(
         world,
         task.getSealPos().pos,
         task.getSealPos().face,
         stack,
         new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value)
      );
      if (stack != null && !stack.func_190926_b() && this.props[5].value && sa <= stack.func_190916_E()) {
         stack = stack.func_77946_l();
         stack.func_190920_e(sa - 1);
      }

      if (stack != null && !stack.func_190926_b()) {
         int limit = golem.canCarryAmount(stack);
         if (limit > 0) {
            ItemStack s = golem.holdItem(
               InventoryUtils.removeStackFrom(
                  world,
                  task.getSealPos().pos,
                  task.getSealPos().face,
                  InventoryUtils.copyLimitedStack(stack, limit),
                  new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value),
                  false
               )
            );
            if (!s.func_190926_b()) {
               InventoryUtils.ejectStackAt(world, task.getSealPos().pos.func_177972_a(task.getSealPos().face), task.getSealPos().face.func_176734_d(), s);
            }

            ((Entity)golem)
               .func_184185_a(SoundEvents.field_187638_cR, 0.125F, ((world.field_73012_v.nextFloat() - world.field_73012_v.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            golem.swingArm();
         }
      }

      this.cache.remove(task.getId());
      this.filterInc++;
      task.setSuspended(true);
      return true;
   }

   @Override
   public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
      ItemStack stack = this.cache.get(task.getId());
      return stack != null && !stack.func_190926_b() && golem.canCarry(stack, true);
   }

   @Override
   public boolean canPlaceAt(World world, BlockPos pos, EnumFacing side) {
      IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(world, pos, side);
      return inv != null;
   }

   public NonNullList<ItemStack> getInv(int c) {
      return super.getInv();
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
   public void onTaskStarted(World world, IGolemAPI golem, Task task) {
   }

   @Override
   public void onTaskSuspension(World world, Task task) {
      this.cache.remove(task.getId());
   }

   @Override
   public void onRemoval(World world, BlockPos pos, EnumFacing side) {
   }
}
