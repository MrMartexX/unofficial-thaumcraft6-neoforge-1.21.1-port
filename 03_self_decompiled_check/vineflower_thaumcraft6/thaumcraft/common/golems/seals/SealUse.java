package thaumcraft.common.golems.seals;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.GolemInteractionHelper;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.utils.InventoryUtils;

public class SealUse extends SealFiltered implements ISealConfigToggles {
   int delay = new Random(System.nanoTime()).nextInt(49);
   int watchedTask = Integer.MIN_VALUE;
   ResourceLocation icon = new ResourceLocation("thaumcraft", "items/seals/seal_use");
   protected ISealConfigToggles.SealToggle[] props = new ISealConfigToggles.SealToggle[]{
      new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"),
      new ISealConfigToggles.SealToggle(true, "pnbt", "golem.prop.nbt"),
      new ISealConfigToggles.SealToggle(false, "pore", "golem.prop.ore"),
      new ISealConfigToggles.SealToggle(false, "pmod", "golem.prop.mod"),
      new ISealConfigToggles.SealToggle(false, "pleft", "golem.prop.left"),
      new ISealConfigToggles.SealToggle(false, "pempty", "golem.prop.empty"),
      new ISealConfigToggles.SealToggle(false, "pemptyhand", "golem.prop.emptyhand"),
      new ISealConfigToggles.SealToggle(false, "psneak", "golem.prop.sneak"),
      new ISealConfigToggles.SealToggle(false, "ppro", "golem.prop.provision.wl")
   };

   @Override
   public String getKey() {
      return "thaumcraft:use";
   }

   @Override
   public void tickSeal(World world, ISealEntity seal) {
      if (this.delay++ % 5 == 0) {
         Task oldTask = TaskHandler.getTask(world.field_73011_w.getDimension(), this.watchedTask);
         if (oldTask == null || oldTask.isSuspended() || oldTask.isCompleted()) {
            if (this.getToggles()[5].value != world.func_175623_d(seal.getSealPos().pos)) {
               return;
            }

            Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask(world.field_73011_w.getDimension(), task);
            this.watchedTask = task.getId();
         }
      }
   }

   @Override
   public void onTaskStarted(World world, IGolemAPI golem, Task task) {
   }

   public boolean mayPlace(World world, Block blockIn, BlockPos pos, EnumFacing side) {
      IBlockState block = world.func_180495_p(pos);
      AxisAlignedBB axisalignedbb = blockIn.func_185496_a(blockIn.func_176223_P(), world, pos);
      return axisalignedbb == null || world.func_72917_a(axisalignedbb, null);
   }

   @Override
   public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
      if (this.getToggles()[5].value == world.func_175623_d(task.getPos())) {
         ItemStack clickStack = (ItemStack)golem.getCarrying().get(0);
         if (!((ItemStack)this.filter.get(0)).func_190926_b()) {
            clickStack = InventoryUtils.findFirstMatchFromFilter(
               this.filter,
               this.filterSize,
               this.blacklist,
               golem.getCarrying(),
               new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value)
            );
         }

         if (!clickStack.func_190926_b() || this.props[6].value) {
            ItemStack ss = ItemStack.field_190927_a;
            if (!clickStack.func_190926_b()) {
               ss = clickStack.func_77946_l();
               golem.dropItem(clickStack.func_77946_l());
            }

            BlockPos var10002 = task.getPos();
            ItemStack var10004 = this.props[6].value ? ItemStack.field_190927_a : ss;
            GolemInteractionHelper.golemClick(world, golem, var10002, task.getSealPos().face, var10004, this.props[7].value, !this.getToggles()[4].value);
         }
      }

      task.setSuspended(true);
      return true;
   }

   private void dropSomeItems(FakePlayer fp2, IGolemAPI golem) {
      for (int i = 0; i < fp2.field_71071_by.field_70462_a.size(); i++) {
         if (!((ItemStack)fp2.field_71071_by.field_70462_a.get(i)).func_190926_b()) {
            if (golem.canCarry((ItemStack)fp2.field_71071_by.field_70462_a.get(i), true)) {
               fp2.field_71071_by.field_70462_a.set(i, golem.holdItem((ItemStack)fp2.field_71071_by.field_70462_a.get(i)));
            }

            if (!((ItemStack)fp2.field_71071_by.field_70462_a.get(i)).func_190926_b()
               && ((ItemStack)fp2.field_71071_by.field_70462_a.get(i)).func_190916_E() > 0) {
               InventoryUtils.dropItemAtEntity(golem.getGolemWorld(), (ItemStack)fp2.field_71071_by.field_70462_a.get(i), golem.getGolemEntity());
            }

            fp2.field_71071_by.field_70462_a.set(i, ItemStack.field_190927_a);
         }
      }

      for (int var4 = 0; var4 < fp2.field_71071_by.field_70460_b.size(); var4++) {
         if (!((ItemStack)fp2.field_71071_by.field_70460_b.get(var4)).func_190926_b()) {
            if (golem.canCarry((ItemStack)fp2.field_71071_by.field_70460_b.get(var4), true)) {
               fp2.field_71071_by.field_70460_b.set(var4, golem.holdItem((ItemStack)fp2.field_71071_by.field_70460_b.get(var4)));
            }

            if (!((ItemStack)fp2.field_71071_by.field_70462_a.get(var4)).func_190926_b()
               && ((ItemStack)fp2.field_71071_by.field_70460_b.get(var4)).func_190916_E() > 0) {
               InventoryUtils.dropItemAtEntity(golem.getGolemWorld(), (ItemStack)fp2.field_71071_by.field_70460_b.get(var4), golem.getGolemEntity());
            }

            fp2.field_71071_by.field_70460_b.set(var4, ItemStack.field_190927_a);
         }
      }
   }

   @Override
   public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
      if (!this.props[6].value) {
         boolean found = !InventoryUtils.findFirstMatchFromFilter(
               this.filter,
               this.filterSize,
               this.blacklist,
               golem.getCarrying(),
               new ThaumcraftInvHelper.InvFilter(!this.props[0].value, !this.props[1].value, this.props[2].value, this.props[3].value)
            )
            .func_190926_b();
         if (!found && this.getToggles()[8].value && !this.blacklist && this.getInv().get(0) != null) {
            ISealEntity se = SealHandler.getSealEntity(golem.getGolemWorld().field_73011_w.getDimension(), task.getSealPos());
            if (se != null) {
               ItemStack stack = ((ItemStack)this.getInv().get(0)).func_77946_l();
               if (!this.props[0].value) {
                  stack.func_77964_b(32767);
               }

               GolemHelper.requestProvisioning(golem.getGolemWorld(), se, stack);
            }
         }

         return found;
      } else {
         return true;
      }
   }

   @Override
   public void onTaskSuspension(World world, Task task) {
   }

   @Override
   public boolean canPlaceAt(World world, BlockPos pos, EnumFacing side) {
      return true;
   }

   @Override
   public ResourceLocation getSealIcon() {
      return this.icon;
   }

   @Override
   public void onRemoval(World world, BlockPos pos, EnumFacing side) {
   }

   @Override
   public Object returnContainer(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
      return new SealBaseContainer(player.field_71071_by, world, seal);
   }

   @SideOnly(Side.CLIENT)
   @Override
   public Object returnGui(World world, EntityPlayer player, BlockPos pos, EnumFacing side, ISealEntity seal) {
      return new SealBaseGUI(player.field_71071_by, world, seal);
   }

   @Override
   public int[] getGuiCategories() {
      return new int[]{1, 3, 0, 4};
   }

   @Override
   public EnumGolemTrait[] getRequiredTags() {
      return new EnumGolemTrait[]{EnumGolemTrait.DEFT, EnumGolemTrait.SMART};
   }

   @Override
   public EnumGolemTrait[] getForbiddenTags() {
      return null;
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
