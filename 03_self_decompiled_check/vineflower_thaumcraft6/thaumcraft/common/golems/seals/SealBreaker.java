package thaumcraft.common.golems.seals;

import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.network.FakeNetHandlerPlayServer;
import thaumcraft.common.lib.utils.BlockUtils;

public class SealBreaker extends SealFiltered implements ISealConfigArea, ISealConfigToggles {
   int delay = new Random(System.nanoTime()).nextInt(42);
   HashMap<Integer, Long> cache = new HashMap<>();
   ResourceLocation icon = new ResourceLocation("thaumcraft", "items/seals/seal_breaker");
   protected ISealConfigToggles.SealToggle[] props = new ISealConfigToggles.SealToggle[]{new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta")};

   @Override
   public String getKey() {
      return "thaumcraft:breaker";
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

      this.delay++;
      BlockPos p = GolemHelper.getPosInArea(seal, this.delay);
      if (!this.cache.containsValue(p.func_177986_g()) && this.isValidBlock(world, p)) {
         Task task = new Task(seal.getSealPos(), p);
         task.setPriority(seal.getPriority());
         task.setData((int)(world.func_180495_p(p).func_185887_b(world, p) * 10.0F));
         TaskHandler.addTask(world.field_73011_w.getDimension(), task);
         this.cache.put(task.getId(), p.func_177986_g());
      }
   }

   private boolean isValidBlock(World world, BlockPos p) {
      IBlockState bs = world.func_180495_p(p);
      if (!world.func_175623_d(p) && bs.func_185887_b(world, p) >= 0.0F) {
         for (ItemStack ts : this.getInv()) {
            if (ts != null && !ts.func_190926_b()) {
               ItemStack fs = BlockUtils.getSilkTouchDrop(bs);
               if (fs == null || !fs.func_190926_b()) {
                  fs = new ItemStack(bs.func_177230_c(), 1, !this.getToggles()[0].value ? 32767 : bs.func_177230_c().func_176201_c(bs));
               }

               if (!this.getToggles()[0].value) {
                  fs.func_77964_b(32767);
               }

               if (this.isBlacklist()) {
                  if (OreDictionary.itemMatches(fs, ts, this.getToggles()[0].value)) {
                     return false;
                  }
               } else if (!OreDictionary.itemMatches(fs, ts, this.getToggles()[0].value)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean onTaskCompletion(World world, IGolemAPI golem, Task task) {
      IBlockState bs = world.func_180495_p(task.getPos());
      if (this.cache.containsKey(task.getId()) && this.isValidBlock(world, task.getPos())) {
         FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile((UUID)null, "FakeThaumcraftGolem"));
         fp.field_71135_a = new FakeNetHandlerPlayServer(fp.field_71133_b, new NetworkManager(EnumPacketDirection.CLIENTBOUND), fp);
         fp.func_70107_b(golem.getGolemEntity().field_70165_t, golem.getGolemEntity().field_70163_u, golem.getGolemEntity().field_70161_v);
         golem.swingArm();
         boolean silky = this.getToggles().length > 1 && this.getToggles()[1].value;
         int bspd = silky ? 7 : 21;
         if (task.getData() > bspd) {
            float bh = bs.func_185887_b(world, task.getPos()) * 10.0F;
            task.setLifespan((short)Math.max(task.getLifespan(), 10L));
            task.setData(task.getData() - bspd);
            int progress = (int)(9.0F * (1.0F - task.getData() / bh));
            world.func_184133_a(
               null,
               task.getPos(),
               bs.func_177230_c().func_185467_w().func_185845_c(),
               SoundCategory.BLOCKS,
               (bs.func_177230_c().func_185467_w().func_185843_a() + 0.7F) / 8.0F,
               bs.func_177230_c().func_185467_w().func_185847_b() * 0.5F
            );
            BlockUtils.destroyBlockPartially(world, golem.getGolemEntity().func_145782_y(), task.getPos(), progress);
            return false;
         }

         BlockUtils.destroyBlockPartially(world, golem.getGolemEntity().func_145782_y(), task.getPos(), 10);
         BlockUtils.harvestBlock(world, fp, task.getPos(), true, silky, 0, true);
         golem.addRankXp(1);
         this.cache.remove(task.getId());
      }

      task.setSuspended(true);
      return true;
   }

   @Override
   public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
      if (this.cache.containsKey(task.getId()) && this.isValidBlock(golem.getGolemWorld(), task.getPos())) {
         return true;
      }

      task.setSuspended(true);
      return false;
   }

   @Override
   public void onTaskSuspension(World world, Task task) {
      this.cache.remove(task.getId());
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
      return new int[]{2, 1, 3, 0, 4};
   }

   @Override
   public EnumGolemTrait[] getRequiredTags() {
      return new EnumGolemTrait[]{EnumGolemTrait.BREAKER};
   }

   @Override
   public EnumGolemTrait[] getForbiddenTags() {
      return null;
   }

   @Override
   public void onTaskStarted(World world, IGolemAPI golem, Task task) {
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
