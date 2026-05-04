package thaumcraft.common.lib.capabilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncKnowledge;

public class PlayerKnowledge {
   public static void preInit() {
      CapabilityManager.INSTANCE.register(IPlayerKnowledge.class, new IStorage<IPlayerKnowledge>() {
         public NBTTagCompound writeNBT(Capability<IPlayerKnowledge> capability, IPlayerKnowledge instance, EnumFacing side) {
            return (NBTTagCompound)instance.serializeNBT();
         }

         public void readNBT(Capability<IPlayerKnowledge> capability, IPlayerKnowledge instance, EnumFacing side, NBTBase nbt) {
            if (nbt instanceof NBTTagCompound) {
               instance.deserializeNBT((NBTTagCompound)nbt);
            }
         }
      }, () -> new PlayerKnowledge.DefaultImpl());
   }

   private static class DefaultImpl implements IPlayerKnowledge {
      private final HashSet<String> research = new HashSet<>();
      private final Map<String, Integer> stages = new HashMap<>();
      private final Map<String, HashSet<IPlayerKnowledge.EnumResearchFlag>> flags = new HashMap<>();
      private final Map<String, Integer> knowledge = new HashMap<>();

      private DefaultImpl() {
      }

      @Override
      public void clear() {
         this.research.clear();
         this.flags.clear();
         this.stages.clear();
         this.knowledge.clear();
      }

      @Override
      public IPlayerKnowledge.EnumResearchStatus getResearchStatus(@Nonnull String res) {
         if (!this.isResearchKnown(res)) {
            return IPlayerKnowledge.EnumResearchStatus.UNKNOWN;
         }

         ResearchEntry entry = ResearchCategories.getResearch(res);
         return entry != null && entry.getStages() != null && this.getResearchStage(res) <= entry.getStages().length
            ? IPlayerKnowledge.EnumResearchStatus.IN_PROGRESS
            : IPlayerKnowledge.EnumResearchStatus.COMPLETE;
      }

      @Override
      public boolean isResearchKnown(String res) {
         if (res == null) {
            return false;
         }

         if (res.equals("")) {
            return true;
         }

         String[] ss = res.split("@");
         return ss.length > 1 && this.getResearchStage(ss[0]) < MathHelper.func_82715_a(ss[1], 0) ? false : this.research.contains(ss[0]);
      }

      @Override
      public boolean isResearchComplete(String res) {
         return this.getResearchStatus(res) == IPlayerKnowledge.EnumResearchStatus.COMPLETE;
      }

      @Override
      public int getResearchStage(String res) {
         if (res != null && this.research.contains(res)) {
            Integer stage = this.stages.get(res);
            return stage == null ? 0 : stage;
         } else {
            return -1;
         }
      }

      @Override
      public boolean setResearchStage(String res, int stage) {
         if (res != null && this.research.contains(res) && stage > 0) {
            this.stages.put(res, stage);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean addResearch(@Nonnull String res) {
         if (!this.isResearchKnown(res)) {
            this.research.add(res);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean removeResearch(@Nonnull String res) {
         if (this.isResearchKnown(res)) {
            this.research.remove(res);
            return true;
         } else {
            return false;
         }
      }

      @Nonnull
      @Override
      public Set<String> getResearchList() {
         return Collections.unmodifiableSet(this.research);
      }

      @Override
      public boolean setResearchFlag(@Nonnull String res, @Nonnull IPlayerKnowledge.EnumResearchFlag flag) {
         HashSet<IPlayerKnowledge.EnumResearchFlag> list = this.flags.get(res);
         if (list == null) {
            list = new HashSet<>();
            this.flags.put(res, list);
         }

         if (list.contains(flag)) {
            return false;
         }

         list.add(flag);
         return true;
      }

      @Override
      public boolean clearResearchFlag(@Nonnull String res, @Nonnull IPlayerKnowledge.EnumResearchFlag flag) {
         HashSet<IPlayerKnowledge.EnumResearchFlag> list = this.flags.get(res);
         if (list != null) {
            boolean b = list.remove(flag);
            if (list.isEmpty()) {
               this.flags.remove(this.research);
            }

            return b;
         } else {
            return false;
         }
      }

      @Override
      public boolean hasResearchFlag(@Nonnull String res, @Nonnull IPlayerKnowledge.EnumResearchFlag flag) {
         return this.flags.get(res) != null ? this.flags.get(res).contains(flag) : false;
      }

      private String getKey(IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category) {
         return type.getAbbreviation() + "_" + (category == null ? "" : category.key);
      }

      @Override
      public boolean addKnowledge(IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int amount) {
         String key = this.getKey(type, category);
         int c = this.getKnowledgeRaw(type, category);
         if (c + amount < 0) {
            return false;
         }

         c += amount;
         this.knowledge.put(key, c);
         return true;
      }

      @Override
      public int getKnowledge(IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category) {
         String key = this.getKey(type, category);
         int c = !this.knowledge.containsKey(key) ? 0 : this.knowledge.get(key);
         return (int)Math.floor((double)c / type.getProgression());
      }

      @Override
      public int getKnowledgeRaw(IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category) {
         String key = this.getKey(type, category);
         return !this.knowledge.containsKey(key) ? 0 : this.knowledge.get(key);
      }

      @Override
      public void sync(@Nonnull EntityPlayerMP player) {
         PacketHandler.INSTANCE.sendTo(new PacketSyncKnowledge(player), player);
      }

      public NBTTagCompound serializeNBT() {
         NBTTagCompound rootTag = new NBTTagCompound();
         NBTTagList researchList = new NBTTagList();

         for (String resKey : this.research) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.func_74778_a("key", resKey);
            if (this.stages.containsKey(resKey)) {
               tag.func_74768_a("stage", this.stages.get(resKey));
            }

            if (this.flags.containsKey(resKey)) {
               HashSet<IPlayerKnowledge.EnumResearchFlag> list = this.flags.get(resKey);
               if (list != null) {
                  String fs = "";

                  for (IPlayerKnowledge.EnumResearchFlag flag : list) {
                     if (fs.length() > 0) {
                        fs = fs + ",";
                     }

                     fs = fs + flag.name();
                  }

                  tag.func_74778_a("flags", fs);
               }
            }

            researchList.func_74742_a(tag);
         }

         rootTag.func_74782_a("research", researchList);
         NBTTagList knowledgeList = new NBTTagList();

         for (String key : this.knowledge.keySet()) {
            Integer c = this.knowledge.get(key);
            if (c != null && c > 0 && key != null && !key.isEmpty()) {
               NBTTagCompound tag = new NBTTagCompound();
               tag.func_74778_a("key", key);
               tag.func_74768_a("amount", c);
               knowledgeList.func_74742_a(tag);
            }
         }

         rootTag.func_74782_a("knowledge", knowledgeList);
         return rootTag;
      }

      public void deserializeNBT(NBTTagCompound rootTag) {
         if (rootTag != null) {
            this.clear();
            NBTTagList researchList = rootTag.func_150295_c("research", 10);

            for (int i = 0; i < researchList.func_74745_c(); i++) {
               NBTTagCompound tag = researchList.func_150305_b(i);
               String know = tag.func_74779_i("key");
               if (know != null && !this.isResearchKnown(know)) {
                  this.research.add(know);
                  int stage = tag.func_74762_e("stage");
                  if (stage > 0) {
                     this.stages.put(know, stage);
                  }

                  String fs = tag.func_74779_i("flags");
                  if (fs.length() > 0) {
                     String[] ss = fs.split(",");

                     for (String s : ss) {
                        IPlayerKnowledge.EnumResearchFlag flag = null;

                        try {
                           flag = IPlayerKnowledge.EnumResearchFlag.valueOf(s);
                        } catch (Exception var15) {
                        }

                        if (flag != null) {
                           this.setResearchFlag(know, flag);
                        }
                     }
                  }
               }
            }

            NBTTagList knowledgeList = rootTag.func_150295_c("knowledge", 10);

            for (int i = 0; i < knowledgeList.func_74745_c(); i++) {
               NBTTagCompound tag = knowledgeList.func_150305_b(i);
               String key = tag.func_74779_i("key");
               int amount = tag.func_74762_e("amount");
               this.knowledge.put(key, amount);
            }

            this.addAutoUnlockResearch();
         }
      }

      private void addAutoUnlockResearch() {
         for (ResearchCategory cat : ResearchCategories.researchCategories.values()) {
            for (ResearchEntry ri : cat.research.values()) {
               if (ri.hasMeta(ResearchEntry.EnumResearchMeta.AUTOUNLOCK)) {
                  this.addResearch(ri.getKey());
               }
            }
         }
      }
   }

   public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
      public static final ResourceLocation NAME = new ResourceLocation("thaumcraft", "knowledge");
      private final PlayerKnowledge.DefaultImpl knowledge = new PlayerKnowledge.DefaultImpl();

      public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
         return capability == ThaumcraftCapabilities.KNOWLEDGE;
      }

      public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
         return (T)(capability == ThaumcraftCapabilities.KNOWLEDGE ? ThaumcraftCapabilities.KNOWLEDGE.cast(this.knowledge) : null);
      }

      public NBTTagCompound serializeNBT() {
         return this.knowledge.serializeNBT();
      }

      public void deserializeNBT(NBTTagCompound nbt) {
         this.knowledge.deserializeNBT(nbt);
      }
   }
}
