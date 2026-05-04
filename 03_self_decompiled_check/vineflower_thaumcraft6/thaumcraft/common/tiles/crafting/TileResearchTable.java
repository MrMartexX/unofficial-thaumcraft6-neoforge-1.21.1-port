package thaumcraft.common.tiles.crafting;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftManager;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;

public class TileResearchTable extends TileThaumcraftInventory {
   public ResearchTableData data = null;

   public TileResearchTable() {
      super(2);
      this.syncedSlots = new int[]{0, 1};
   }

   @Override
   public void readSyncNBT(NBTTagCompound nbttagcompound) {
      super.readSyncNBT(nbttagcompound);
      if (nbttagcompound.func_74764_b("note")) {
         this.data = new ResearchTableData(this);
         this.data.deserialize(nbttagcompound.func_74775_l("note"));
      } else {
         this.data = null;
      }
   }

   @Override
   public NBTTagCompound writeSyncNBT(NBTTagCompound nbttagcompound) {
      if (this.data != null) {
         nbttagcompound.func_74782_a("note", this.data.serialize());
      } else {
         nbttagcompound.func_82580_o("note");
      }

      return super.writeSyncNBT(nbttagcompound);
   }

   protected void func_190201_b(World worldIn) {
      super.func_190201_b(worldIn);
      if (!this.func_145830_o()) {
         this.func_145834_a(worldIn);
      }
   }

   public void startNewTheory(EntityPlayer player, Set<String> mutators) {
      this.data = new ResearchTableData(player, this);
      this.data.initialize(player, mutators);
      this.syncTile(false);
      this.func_70296_d();
   }

   public void finishTheory(EntityPlayer player) {
      Comparator<Entry<String, Integer>> valueComparator = (e1, e2) -> e2.getValue().compareTo(e1.getValue());
      Map<String, Integer> sortedMap = this.data
         .categoryTotals
         .entrySet()
         .stream()
         .sorted(valueComparator)
         .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
      int i = 0;

      for (String cat : sortedMap.keySet()) {
         int tot = Math.round(sortedMap.get(cat).intValue() / 100.0F * IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression());
         if (i > this.data.penaltyStart) {
            tot = (int)Math.max(1.0, tot * 0.666666667);
         }

         ResearchCategory rc = ResearchCategories.getResearchCategory(cat);
         ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, rc, tot);
         i++;
      }

      this.data = null;
   }

   public Set<String> checkSurroundingAids() {
      HashMap<String, ITheorycraftAid> mutators = new HashMap<>();

      for (int y = -1; y <= 1; y++) {
         for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
               for (String muk : TheorycraftManager.aids.keySet()) {
                  ITheorycraftAid mu = TheorycraftManager.aids.get(muk);
                  IBlockState state = this.field_145850_b.func_180495_p(this.func_174877_v().func_177982_a(x, y, z));
                  if (mu.getAidObject() instanceof Block) {
                     if (state.func_177230_c() == (Block)mu.getAidObject()) {
                        mutators.put(muk, mu);
                     }
                  } else if (mu.getAidObject() instanceof ItemStack) {
                     ItemStack is = state.func_177230_c().func_185473_a(this.func_145831_w(), this.func_174877_v().func_177982_a(x, y, z), state);
                     if (is != null && !is.func_190926_b() && is.func_185136_b((ItemStack)mu.getAidObject())) {
                        mutators.put(muk, mu);
                     }
                  }
               }
            }
         }
      }

      List<Entity> l = EntityUtils.getEntitiesInRange(this.func_145831_w(), this.func_174877_v(), null, Entity.class, 5.0);
      if (l != null && !l.isEmpty()) {
         for (Entity e : l) {
            for (String muk : TheorycraftManager.aids.keySet()) {
               ITheorycraftAid mu = TheorycraftManager.aids.get(muk);
               if (mu.getAidObject() instanceof Class && e.getClass().isAssignableFrom((Class<?>)mu.getAidObject())) {
                  mutators.put(muk, mu);
               }
            }
         }
      }

      return mutators.keySet();
   }

   public boolean consumeInkFromTable() {
      if (this.func_70301_a(0).func_77973_b() instanceof IScribeTools && this.func_70301_a(0).func_77952_i() < this.func_70301_a(0).func_77958_k()) {
         this.func_70301_a(0).func_77964_b(this.func_70301_a(0).func_77952_i() + 1);
         this.syncTile(false);
         this.func_70296_d();
         return true;
      } else {
         return false;
      }
   }

   public boolean consumepaperFromTable() {
      if (this.func_70301_a(1).func_77973_b() == Items.field_151121_aF && this.func_70301_a(1).func_190916_E() > 0) {
         this.func_70298_a(1, 1);
         this.syncTile(false);
         this.func_70296_d();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public String func_70005_c_() {
      return "Research Table";
   }

   @Override
   public boolean func_94041_b(int i, ItemStack itemstack) {
      switch (i) {
         case 0:
            if (itemstack.func_77973_b() instanceof IScribeTools) {
               return true;
            }
            break;
         case 1:
            if (itemstack.func_77973_b() == Items.field_151121_aF && itemstack.func_77952_i() == 0) {
               return true;
            }
      }

      return false;
   }

   @Override
   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      super.onDataPacket(net, pkt);
      if (this.field_145850_b != null && this.field_145850_b.field_72995_K) {
         this.syncTile(false);
      }
   }

   public boolean func_145842_c(int i, int j) {
      if (i == 1) {
         if (this.field_145850_b.field_72995_K) {
            this.field_145850_b
               .func_184134_a(
                  this.func_174877_v().func_177958_n(),
                  this.func_174877_v().func_177956_o(),
                  this.func_174877_v().func_177952_p(),
                  SoundsTC.learn,
                  SoundCategory.BLOCKS,
                  1.0F,
                  1.0F,
                  false
               );
         }

         return true;
      } else {
         return super.func_145842_c(i, j);
      }
   }
}
