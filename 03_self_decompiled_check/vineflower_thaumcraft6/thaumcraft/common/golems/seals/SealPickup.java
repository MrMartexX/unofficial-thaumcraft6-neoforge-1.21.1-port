package thaumcraft.common.golems.seals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.utils.InventoryUtils;

public class SealPickup extends SealFiltered implements ISealConfigArea {
   int delay = new Random(System.nanoTime()).nextInt(100);
   HashMap<Integer, Integer> itemEntities = new HashMap<>();
   ResourceLocation icon = new ResourceLocation("thaumcraft", "items/seals/seal_pickup");
   protected ISealConfigToggles.SealToggle[] props = new ISealConfigToggles.SealToggle[]{
      new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"),
      new ISealConfigToggles.SealToggle(true, "pnbt", "golem.prop.nbt"),
      new ISealConfigToggles.SealToggle(false, "pore", "golem.prop.ore"),
      new ISealConfigToggles.SealToggle(false, "pmod", "golem.prop.mod")
   };

   @Override
   public String getKey() {
      return "thaumcraft:pickup";
   }

   @Override
   public void tickSeal(World world, ISealEntity seal) {
      if (this.delay++ % 5 == 0) {
         AxisAlignedBB area = GolemHelper.getBoundsForArea(seal);
         List list = world.func_72872_a(EntityItem.class, area);
         if (list.size() > 0) {
            for (Object e : list) {
               EntityItem ent = (EntityItem)e;
               if (ent != null
                  && ent.field_70122_E
                  && !ent.func_174874_s()
                  && ent.func_92059_d() != null
                  && !this.itemEntities.containsValue(ent.func_145782_y())) {
                  ItemStack stack = InventoryUtils.findFirstMatchFromFilter(
                     this.filter,
                     this.filterSize,
                     this.isBlacklist(),
                     NonNullList.func_191197_a(1, ent.func_92059_d()),
                     new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value)
                  );
                  if (stack != null && !stack.func_190926_b()) {
                     Task task = new Task(seal.getSealPos(), ent);
                     task.setPriority(seal.getPriority());
                     this.itemEntities.put(task.getId(), ent.func_145782_y());
                     TaskHandler.addTask(world.field_73011_w.getDimension(), task);
                     break;
                  }
               }
            }
         }

         if (this.delay % 100 != 0) {
            Iterator<Integer> it = this.itemEntities.values().iterator();

            while (it.hasNext()) {
               Entity e = world.func_73045_a(it.next());
               if (e == null || e.field_70128_L) {
                  try {
                     it.remove();
                  } catch (Exception var10) {
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
      EntityItem ei = this.getItemEntity(world, task);
      if (ei != null && !ei.func_92059_d().func_190926_b()) {
         ItemStack stack = InventoryUtils.findFirstMatchFromFilter(
            this.filter,
            this.filterSize,
            this.isBlacklist(),
            NonNullList.func_191197_a(1, ei.func_92059_d()),
            new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value)
         );
         if (stack != null && !stack.func_190926_b()) {
            ItemStack is = golem.holdItem(ei.func_92059_d());
            if (is != null && !is.func_190926_b() && is.func_190916_E() > 0) {
               ei.func_92058_a(is);
            }

            if (is == null || is.func_190926_b() || is.func_190916_E() <= 0) {
               ei.func_70106_y();
            }

            ((Entity)golem)
               .func_184185_a(SoundEvents.field_187638_cR, 0.125F, ((world.field_73012_v.nextFloat() - world.field_73012_v.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            golem.swingArm();
         }
      }

      task.setSuspended(true);
      this.itemEntities.remove(task.getId());

      for (Task ticket : TaskHandler.getEntityTasksSorted(world.field_73011_w.getDimension(), null, (Entity)golem)) {
         if (this.itemEntities.containsKey(ticket.getId())
            && ticket.canGolemPerformTask(golem)
            && ((EntityThaumcraftGolem)golem).func_180485_d(ticket.getEntity().func_180425_c())) {
            ((EntityThaumcraftGolem)golem).setTask(ticket);
            ((EntityThaumcraftGolem)golem).getTask().setReserved(true);
            if (ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
               world.func_72960_a((EntityThaumcraftGolem)golem, (byte)5);
            }
            break;
         }
      }

      return true;
   }

   protected EntityItem getItemEntity(World world, Task task) {
      Integer ei = this.itemEntities.get(task.getId());
      if (ei != null) {
         Entity ent = world.func_73045_a(ei);
         if (ent != null && ent instanceof EntityItem) {
            return (EntityItem)ent;
         }
      }

      return null;
   }

   @Override
   public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
      EntityItem ei = this.getItemEntity(golem.getGolemWorld(), task);
      if (ei == null || ei.func_92059_d() == null) {
         return false;
      } else if (ei.field_70128_L) {
         task.setSuspended(true);
         return false;
      } else {
         return golem.canCarry(ei.func_92059_d(), true);
      }
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
      return new int[]{2, 1, 0, 4};
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
   }

   @Override
   public void onRemoval(World world, BlockPos pos, EnumFacing side) {
   }
}
