package thaumcraft.common.golems.seals;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
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
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.ProvisionRequest;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.utils.InventoryUtils;

public class SealProvide extends SealFiltered implements ISealConfigToggles {
   int delay = new Random(System.nanoTime()).nextInt(88);
   ResourceLocation icon = new ResourceLocation("thaumcraft", "items/seals/seal_provider");
   protected ISealConfigToggles.SealToggle[] props = new ISealConfigToggles.SealToggle[]{
      new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"),
      new ISealConfigToggles.SealToggle(true, "pnbt", "golem.prop.nbt"),
      new ISealConfigToggles.SealToggle(false, "pore", "golem.prop.ore"),
      new ISealConfigToggles.SealToggle(false, "pmod", "golem.prop.mod"),
      new ISealConfigToggles.SealToggle(false, "psing", "golem.prop.single"),
      new ISealConfigToggles.SealToggle(false, "pleave", "golem.prop.leave")
   };

   @Override
   public String getKey() {
      return "thaumcraft:provider";
   }

   @Override
   public int getFilterSize() {
      return 9;
   }

   @Override
   public void tickSeal(World world, ISealEntity seal) {
      if (this.delay % 100 == 0 && GolemHelper.provisionRequests.containsKey(world.field_73011_w.getDimension())) {
         Iterator<ProvisionRequest> it = GolemHelper.provisionRequests.get(world.field_73011_w.getDimension()).iterator();

         while (it.hasNext()) {
            ProvisionRequest pr = it.next();
            if (pr.isInvalid()
               || pr.getLinkedTask() == null
               || pr.getLinkedTask().isSuspended()
               || pr.getLinkedTask().isCompleted()
               || pr.getTimeout() < System.currentTimeMillis()) {
               it.remove();
            }
         }
      }

      if (this.delay++ % 20 == 0) {
         IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(world, seal.getSealPos().pos, seal.getSealPos().face);
         if (inv != null && GolemHelper.provisionRequests.containsKey(world.field_73011_w.getDimension())) {
            ListIterator<ProvisionRequest> it = GolemHelper.provisionRequests.get(world.field_73011_w.getDimension()).listIterator();

            while (it.hasNext()) {
               ProvisionRequest pr = it.next();
               if (pr.isInvalid()) {
                  it.remove();
               } else if (pr.getLinkedTask() == null
                  && (
                     pr.getSeal() != null && pr.getSeal().getSealPos().pos.func_177951_i(seal.getSealPos().pos) < 4096.0
                        || pr.getEntity() != null
                           && seal.getSealPos().pos.func_177954_c(pr.getEntity().field_70165_t, pr.getEntity().field_70163_u, pr.getEntity().field_70161_v)
                              < 4096.0
                        || pr.getPos() != null && seal.getSealPos().pos.func_177951_i(pr.getPos()) < 4096.0
                  )) {
                  NonNullList<ItemStack> stacks = NonNullList.func_191197_a(1, pr.getStack());
                  if (!InventoryUtils.findFirstMatchFromFilter(
                           this.getInv(),
                           this.getSizes(),
                           this.blacklist,
                           stacks,
                           new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value)
                        )
                        .func_190926_b()
                     && ThaumcraftInvHelper.countTotalItemsIn(inv, pr.getStack(), ThaumcraftInvHelper.InvFilter.STRICT) > (this.props[5].value ? 1 : 0)) {
                     Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
                     task.setPriority(pr.getSeal() != null ? pr.getSeal().getPriority() : 5);
                     task.setLifespan((short)(pr.getSeal() != null ? 10 : 31000));
                     TaskHandler.addTask(world.field_73011_w.getDimension(), task);
                     pr.setLinkedTask(task);
                     task.setLinkedProvision(pr);
                     break;
                  }
               }
            }
         }
      }
   }

   public boolean matchesFilters(ItemStack stack) {
      return InventoryUtils.matchesFilters(
         this.getInv(),
         this.blacklist,
         stack,
         new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value)
      );
   }

   @Override
   public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
      if (task.getLinkedProvision() != null) {
         if (task.getData() == 0) {
            IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(world, task.getSealPos().pos, task.getSealPos().face);
            if (inv != null) {
               ItemStack stack = ItemStack.field_190927_a;

               try {
                  stack = task.getLinkedProvision().getStack().func_77946_l();
               } catch (Exception var11) {
               }

               if (stack != null && this.props[4].value) {
                  stack.func_190920_e(1);
               }

               int sa = 0;
               if (stack != null
                  && !stack.func_190926_b()
                  && this.props[5].value
                  && (sa = ThaumcraftInvHelper.countTotalItemsIn(inv, stack, ThaumcraftInvHelper.InvFilter.STRICT)) <= stack.func_190916_E()) {
                  stack.func_190920_e(sa - 1);
               }

               if (stack != null && !stack.func_190926_b()) {
                  int limit = golem.canCarryAmount(stack);
                  if (limit > 0) {
                     ItemStack s = golem.holdItem(
                        InventoryUtils.removeStackFrom(inv, InventoryUtils.copyLimitedStack(stack, limit), ThaumcraftInvHelper.InvFilter.STRICT, false)
                     );
                     if (s != null && !s.func_190926_b()) {
                        InventoryUtils.ejectStackAt(
                           world, task.getSealPos().pos.func_177972_a(task.getSealPos().face), task.getSealPos().face.func_176734_d(), s
                        );
                     }

                     ((Entity)golem)
                        .func_184185_a(
                           SoundEvents.field_187638_cR, 0.125F, ((world.field_73012_v.nextFloat() - world.field_73012_v.nextFloat()) * 0.7F + 1.0F) * 2.0F
                        );
                     golem.addRankXp(1);
                     golem.swingArm();
                     ProvisionRequest pr2 = task.getLinkedProvision();
                     if (pr2.getEntity() != null || pr2.getPos() != null) {
                        Task task2 = null;
                        if (pr2.getEntity() != null) {
                           task2 = new Task(task.getSealPos(), pr2.getEntity());
                        } else {
                           task2 = new Task(task.getSealPos(), pr2.getPos());
                        }

                        task2.setPriority(task.getPriority());
                        task2.setData(pr2.getEntity() != null ? 1 : 2);
                        task2.setLifespan((short)31000);
                        TaskHandler.addTask(world.field_73011_w.getDimension(), task2);
                        pr2.setLinkedTask(task2);
                        task2.setLinkedProvision(pr2);
                     }
                  }
               }
            }
         } else if (task.getLinkedProvision() != null) {
            ProvisionRequest pr = task.getLinkedProvision();
            ItemStack cs = pr.getStack();
            ItemStack s = golem.dropItem(cs);
            if (s.func_190916_E() < cs.func_190916_E()) {
               ItemStack ps = cs.func_77946_l();
               ps.func_190920_e(cs.func_190916_E() - s.func_190916_E());
               if (task.getData() == 1) {
                  GolemHelper.requestProvisioning(world, pr.getEntity(), ps);
               } else {
                  GolemHelper.requestProvisioning(world, pr.getPos(), pr.getSide(), ps);
               }
            }

            if (task.getData() == 1) {
               InventoryUtils.dropItemAtEntity(world, s, pr.getEntity());
            } else {
               ItemStack back = InventoryUtils.ejectStackAt(world, pr.getPos().func_177972_a(pr.getSide()), pr.getSide().func_176734_d(), s, true);
               if (!back.func_190926_b()) {
                  golem.holdItem(back);
               }
            }

            ((Entity)golem)
               .func_184185_a(SoundEvents.field_187638_cR, 0.125F, ((world.field_73012_v.nextFloat() - world.field_73012_v.nextFloat()) * 0.7F + 1.0F) * 1.0F);
            golem.swingArm();
            pr.setInvalid(true);
         }
      }

      task.setSuspended(true);
      return true;
   }

   @Override
   public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
      ProvisionRequest pr = task.getLinkedProvision();
      boolean b = pr != null
         && (
            pr.getSeal() != null && ((EntityThaumcraftGolem)golem).func_180485_d(pr.getSeal().getSealPos().pos)
               || pr.getEntity() != null && ((EntityThaumcraftGolem)golem).func_180485_d(pr.getEntity().func_180425_c())
               || pr.getPos() != null && ((EntityThaumcraftGolem)golem).func_180485_d(pr.getPos())
         );
      return task.getData() == 0
         ? b
            && this.areGolemTagsValidForTask(pr.getSeal(), golem)
            && pr.getStack() != null
            && !golem.isCarrying(pr.getStack())
            && golem.canCarry(pr.getStack(), true)
         : b && this.areGolemTagsValidForTask(pr.getSeal(), golem) && pr.getStack() != null && golem.isCarrying(pr.getStack());
   }

   private boolean areGolemTagsValidForTask(ISealEntity se, IGolemAPI golem) {
      if (se == null) {
         return true;
      }

      if (se.isLocked() && !((IEntityOwnable)golem).func_184753_b().equals(se.getOwner())) {
         return false;
      }

      if (se.getSeal().getRequiredTags() != null && !golem.getProperties().getTraits().containsAll(Arrays.asList(se.getSeal().getRequiredTags()))) {
         return false;
      }

      if (se.getSeal().getForbiddenTags() != null) {
         for (EnumGolemTrait tag : se.getSeal().getForbiddenTags()) {
            if (golem.getProperties().getTraits().contains(tag)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public void onTaskSuspension(World world, Task task) {
      if (task.getLinkedProvision() != null) {
         task.getLinkedProvision().setLinkedTask(null);
      }

      task.setLinkedProvision(null);
   }

   @Override
   public boolean canPlaceAt(World world, BlockPos pos, EnumFacing side) {
      IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(world, pos, side);
      return inv != null;
   }

   @Override
   public ResourceLocation getSealIcon() {
      return this.icon;
   }

   @Override
   public int[] getGuiCategories() {
      return new int[]{1, 3, 0, 4};
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
   public void onRemoval(World world, BlockPos pos, EnumFacing side) {
   }

   @Override
   public ISealConfigToggles.SealToggle[] getToggles() {
      return this.props;
   }

   @Override
   public void setToggle(int indx, boolean value) {
      this.props[indx].setValue(value);
   }
}
