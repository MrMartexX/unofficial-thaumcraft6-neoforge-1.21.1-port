package thaumcraft.common.golems.seals;

import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.GolemInteractionHelper;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.network.FakeNetHandlerPlayServer;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.CropUtils;

public class SealHarvest implements ISeal, ISealGui, ISealConfigArea, ISealConfigToggles {
   int delay = new Random(System.nanoTime()).nextInt(33);
   int count = 0;
   HashMap<Long, SealHarvest.ReplantInfo> replantTasks = new HashMap<>();
   ResourceLocation icon = new ResourceLocation("thaumcraft", "items/seals/seal_harvest");
   protected ISealConfigToggles.SealToggle[] props = new ISealConfigToggles.SealToggle[]{
      new ISealConfigToggles.SealToggle(true, "prep", "golem.prop.replant"), new ISealConfigToggles.SealToggle(false, "ppro", "golem.prop.provision")
   };

   @Override
   public String getKey() {
      return "thaumcraft:harvest";
   }

   @Override
   public void tickSeal(World world, ISealEntity seal) {
      if (this.delay % 100 == 0) {
         AxisAlignedBB area = GolemHelper.getBoundsForArea(seal);
         Iterator<Long> rt = this.replantTasks.keySet().iterator();

         while (rt.hasNext()) {
            BlockPos pp = BlockPos.func_177969_a(rt.next());
            if (!area.func_72318_a(new Vec3d(pp.func_177958_n() + 0.5, pp.func_177956_o() + 0.5, pp.func_177952_p() + 0.5))) {
               if (this.replantTasks.get(rt) != null) {
                  Task tt = TaskHandler.getTask(world.field_73011_w.getDimension(), this.replantTasks.get(rt).taskid);
                  if (tt != null) {
                     tt.setSuspended(true);
                  }
               }

               rt.remove();
            }
         }
      }

      if (this.delay++ % 5 == 0) {
         BlockPos p = GolemHelper.getPosInArea(seal, this.count++);
         if (CropUtils.isGrownCrop(world, p)) {
            Task task = new Task(seal.getSealPos(), p);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask(world.field_73011_w.getDimension(), task);
         } else if (this.getToggles()[0].value && this.replantTasks.containsKey(p.func_177986_g()) && world.func_175623_d(p)) {
            Task t = TaskHandler.getTask(world.field_73011_w.getDimension(), this.replantTasks.get(p.func_177986_g()).taskid);
            if (t == null) {
               Task tt = new Task(seal.getSealPos(), this.replantTasks.get(p.func_177986_g()).pos);
               tt.setPriority(seal.getPriority());
               TaskHandler.addTask(world.field_73011_w.getDimension(), tt);
               this.replantTasks.get(p.func_177986_g()).taskid = tt.getId();
            }
         }
      }
   }

   @Override
   public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
      if (CropUtils.isGrownCrop(world, task.getPos())) {
         FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile((UUID)null, "FakeThaumcraftGolem"));
         fp.field_71135_a = new FakeNetHandlerPlayServer(fp.field_71133_b, new NetworkManager(EnumPacketDirection.CLIENTBOUND), fp);
         fp.func_70107_b(golem.getGolemEntity().field_70165_t, golem.getGolemEntity().field_70163_u, golem.getGolemEntity().field_70161_v);
         EnumFacing face = EnumFacing.func_190914_a(task.getPos(), golem.getGolemEntity());
         IBlockState bs = world.func_180495_p(task.getPos());
         if (CropUtils.clickableCrops.contains(bs.func_177230_c().func_149739_a() + bs.func_177230_c().func_176201_c(bs))) {
            bs.func_177230_c().func_180639_a(world, task.getPos(), bs, fp, EnumHand.MAIN_HAND, face, 0.0F, 0.0F, 0.0F);
            golem.addRankXp(1);
            golem.swingArm();
         } else {
            GolemInteractionHelper.golemClick(world, golem, task.getPos(), task.getSealPos().face, ItemStack.field_190927_a, false, true);
            if (CropUtils.isGrownCrop(world, task.getPos())) {
               BlockUtils.harvestBlock(world, fp, task.getPos(), true, false, 0, true);
               golem.addRankXp(1);
               golem.swingArm();
               if (this.getToggles()[0].value) {
                  ItemStack seed = ThaumcraftApi.getSeed(bs.func_177230_c());
                  if (seed != null && !seed.func_190926_b()) {
                     IBlockState bb = world.func_180495_p(task.getPos().func_177977_b());
                     EnumFacing rf = null;
                     if (seed.func_77973_b() instanceof IPlantable
                        && bb.func_177230_c().canSustainPlant(bb, world, task.getPos().func_177977_b(), EnumFacing.UP, (IPlantable)seed.func_77973_b())) {
                        rf = EnumFacing.DOWN;
                     } else if (!(seed.func_77973_b() instanceof IPlantable) && bs.func_177230_c() instanceof BlockDirectional) {
                        rf = (EnumFacing)bs.func_177229_b(BlockDirectional.field_176387_N);
                     }

                     if (rf != null) {
                        Task tt = new Task(task.getSealPos(), task.getPos());
                        tt.setPriority(task.getPriority());
                        tt.setLifespan((short)300);
                        this.replantTasks
                           .put(
                              tt.getPos().func_177986_g(),
                              new SealHarvest.ReplantInfo(tt.getPos(), rf, tt.getId(), seed.func_77946_l(), bb.func_177230_c() instanceof BlockFarmland)
                           );
                        TaskHandler.addTask(world.field_73011_w.getDimension(), tt);
                     }
                  }
               }
            }
         }
      } else if (this.replantTasks.containsKey(task.getPos().func_177986_g())
         && this.replantTasks.get(task.getPos().func_177986_g()).taskid == task.getId()
         && world.func_175623_d(task.getPos())
         && golem.isCarrying(this.replantTasks.get(task.getPos().func_177986_g()).stack)) {
         FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile((UUID)null, "FakeThaumcraftGolem"));
         fp.func_70107_b(golem.getGolemEntity().field_70165_t, golem.getGolemEntity().field_70163_u, golem.getGolemEntity().field_70161_v);
         IBlockState bb = world.func_180495_p(task.getPos().func_177977_b());
         SealHarvest.ReplantInfo ri = this.replantTasks.get(task.getPos().func_177986_g());
         if ((bb.func_177230_c() instanceof BlockDirt || bb.func_177230_c() instanceof BlockGrass) && ri.farmland) {
            Items.field_151012_L.func_180614_a(fp, world, task.getPos().func_177977_b(), EnumHand.MAIN_HAND, EnumFacing.UP, 0.5F, 0.5F, 0.5F);
         }

         ItemStack seed = ri.stack.func_77946_l();
         seed.func_190920_e(1);
         if (seed.func_77973_b().func_180614_a(fp, world, task.getPos().func_177972_a(ri.face), EnumHand.MAIN_HAND, ri.face.func_176734_d(), 0.5F, 0.5F, 0.5F)
            == EnumActionResult.SUCCESS) {
            world.func_175669_a(2001, task.getPos(), Block.func_176210_f(world.func_180495_p(task.getPos())));
            golem.dropItem(seed);
            golem.addRankXp(1);
            golem.swingArm();
         }
      }

      task.setSuspended(true);
      return true;
   }

   @Override
   public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
      if (this.replantTasks.containsKey(task.getPos().func_177986_g()) && this.replantTasks.get(task.getPos().func_177986_g()).taskid == task.getId()) {
         boolean carry = golem.isCarrying(this.replantTasks.get(task.getPos().func_177986_g()).stack);
         if (!carry && this.getToggles()[1].value) {
            ISealEntity se = SealHandler.getSealEntity(golem.getGolemWorld().field_73011_w.getDimension(), task.getSealPos());
            if (se != null) {
               GolemHelper.requestProvisioning(golem.getGolemWorld(), se, this.replantTasks.get(task.getPos().func_177986_g()).stack);
            }
         }

         return carry;
      } else {
         return true;
      }
   }

   @Override
   public void onTaskSuspension(World world, Task task) {
   }

   @Override
   public void readCustomNBT(NBTTagCompound nbt) {
      NBTTagList nbttaglist = nbt.func_150295_c("replant", 10);

      for (int i = 0; i < nbttaglist.func_74745_c(); i++) {
         NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
         long loc = nbttagcompound1.func_74763_f("taskloc");
         byte face = nbttagcompound1.func_74771_c("taskface");
         boolean farmland = nbttagcompound1.func_74767_n("farmland");
         ItemStack stack = new ItemStack(nbttagcompound1);
         this.replantTasks.put(loc, new SealHarvest.ReplantInfo(BlockPos.func_177969_a(loc), EnumFacing.field_82609_l[face], 0, stack, farmland));
      }
   }

   @Override
   public void writeCustomNBT(NBTTagCompound nbt) {
      if (this.getToggles()[0].value) {
         NBTTagList nbttaglist = new NBTTagList();

         for (Long key : this.replantTasks.keySet()) {
            SealHarvest.ReplantInfo info = this.replantTasks.get(key);
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.func_74772_a("taskloc", info.pos.func_177986_g());
            nbttagcompound1.func_74774_a("taskface", (byte)info.face.ordinal());
            nbttagcompound1.func_74757_a("farmland", info.farmland);
            info.stack.func_77955_b(nbttagcompound1);
            nbttaglist.func_74742_a(nbttagcompound1);
         }

         nbt.func_74782_a("replant", nbttaglist);
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
      return new int[]{2, 3, 0, 4};
   }

   @Override
   public ISealConfigToggles.SealToggle[] getToggles() {
      return this.props;
   }

   @Override
   public void setToggle(int indx, boolean value) {
      this.props[indx].setValue(value);
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
   public void onTaskStarted(World world, IGolemAPI golem, Task task) {
   }

   private class ReplantInfo {
      EnumFacing face;
      BlockPos pos;
      int taskid;
      ItemStack stack;
      boolean farmland;

      public ReplantInfo(BlockPos pos, EnumFacing face, int taskid, ItemStack stack, boolean farmland) {
         this.pos = pos;
         this.face = face;
         this.taskid = taskid;
         this.stack = stack;
         this.farmland = farmland;
      }
   }
}
