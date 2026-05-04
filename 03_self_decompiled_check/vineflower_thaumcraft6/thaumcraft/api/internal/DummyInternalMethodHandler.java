package thaumcraft.api.internal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.api.research.ResearchCategory;

public class DummyInternalMethodHandler implements IInternalMethodHandler {
   @Override
   public boolean completeResearch(EntityPlayer player, String researchkey) {
      return false;
   }

   @Override
   public void addWarpToPlayer(EntityPlayer player, int amount, IPlayerWarp.EnumWarpType type) {
   }

   @Override
   public AspectList getObjectAspects(ItemStack is) {
      return null;
   }

   @Override
   public AspectList generateTags(ItemStack is) {
      return null;
   }

   @Override
   public float drainVis(World world, BlockPos pos, float amount, boolean simulate) {
      return 0.0F;
   }

   @Override
   public float drainFlux(World world, BlockPos pos, float amount, boolean simulate) {
      return 0.0F;
   }

   @Override
   public void addVis(World world, BlockPos pos, float amount) {
   }

   @Override
   public void addFlux(World world, BlockPos pos, float amount, boolean showEffect) {
   }

   @Override
   public float getTotalAura(World world, BlockPos pos) {
      return 0.0F;
   }

   @Override
   public float getVis(World world, BlockPos pos) {
      return 0.0F;
   }

   @Override
   public float getFlux(World world, BlockPos pos) {
      return 0.0F;
   }

   @Override
   public int getAuraBase(World world, BlockPos pos) {
      return 0;
   }

   @Override
   public void registerSeal(ISeal seal) {
   }

   @Override
   public ISeal getSeal(String key) {
      return null;
   }

   @Override
   public ISealEntity getSealEntity(int dim, SealPos pos) {
      return null;
   }

   @Override
   public void addGolemTask(int dim, Task task) {
   }

   @Override
   public boolean shouldPreserveAura(World world, EntityPlayer player, BlockPos pos) {
      return false;
   }

   @Override
   public ItemStack getSealStack(String key) {
      return null;
   }

   @Override
   public boolean doesPlayerHaveRequisites(EntityPlayer player, String researchkey) {
      return false;
   }

   @Override
   public boolean addKnowledge(EntityPlayer player, IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory field, int amount) {
      return false;
   }

   @Override
   public boolean progressResearch(EntityPlayer player, String researchkey) {
      return false;
   }

   @Override
   public int getActualWarp(EntityPlayer player) {
      return 0;
   }
}
