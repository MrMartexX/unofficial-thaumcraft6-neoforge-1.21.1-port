package thaumcraft.common.blocks.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.WeightedRandomLoot;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.config.ModConfig;

public class BlockStonePorous extends BlockTC {
   static Random r = new Random(System.currentTimeMillis());
   static ArrayList<WeightedRandomLoot> pdrops = null;

   public BlockStonePorous() {
      super(Material.field_151576_e, "stone_porous");
      this.func_149711_c(1.0F);
      this.func_149752_b(5.0F);
      this.func_149672_a(SoundType.field_185851_d);
   }

   public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      List<ItemStack> ret = new ArrayList<>();
      int rr = r.nextInt(15) + fortune;
      if (rr > 13) {
         if (pdrops == null || pdrops.size() <= 0) {
            this.createDrops();
         }

         ItemStack s = ((WeightedRandomLoot)WeightedRandom.func_76271_a(r, pdrops)).item.func_77946_l();
         ret.add(s);
      } else {
         ret.add(new ItemStack(Blocks.field_150351_n));
      }

      return ret;
   }

   private void createDrops() {
      pdrops = new ArrayList<>();

      for (Aspect aspect : Aspect.getCompoundAspects()) {
         ItemStack is = new ItemStack(ItemsTC.crystalEssence);
         ((ItemGenericEssentiaContainer)ItemsTC.crystalEssence)
            .setAspects(is, new AspectList().add(aspect, aspect == Aspect.FLUX ? 100 : (aspect.isPrimal() ? 20 : 1)));
         pdrops.add(new WeightedRandomLoot(is, 1));
      }

      pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.amber), 20));
      pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1, 0), 20));
      pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1, 1), 10));
      pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1, 6), 10));
      if (ModConfig.foundCopperIngot) {
         pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1, 2), 10));
      }

      if (ModConfig.foundTinIngot) {
         pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1, 3), 10));
      }

      if (ModConfig.foundSilverIngot) {
         pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1, 4), 8));
      }

      if (ModConfig.foundLeadIngot) {
         pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1, 5), 10));
      }

      pdrops.add(new WeightedRandomLoot(new ItemStack(Items.field_151045_i), 2));
      pdrops.add(new WeightedRandomLoot(new ItemStack(Items.field_151166_bC), 4));
      pdrops.add(new WeightedRandomLoot(new ItemStack(Items.field_151137_ax), 8));
      pdrops.add(new WeightedRandomLoot(new ItemStack(Items.field_179563_cD), 3));
      pdrops.add(new WeightedRandomLoot(new ItemStack(Items.field_179562_cC), 3));
      pdrops.add(new WeightedRandomLoot(new ItemStack(Items.field_151119_aD), 30));
      pdrops.add(new WeightedRandomLoot(new ItemStack(Items.field_151128_bU), 15));
   }
}
