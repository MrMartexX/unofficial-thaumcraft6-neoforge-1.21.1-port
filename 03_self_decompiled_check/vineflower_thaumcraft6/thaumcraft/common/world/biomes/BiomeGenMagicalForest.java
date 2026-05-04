package thaumcraft.common.world.biomes;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockTallGrass.EnumType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.world.objects.WorldGenBigMagicTree;
import thaumcraft.common.world.objects.WorldGenGreatwoodTrees;
import thaumcraft.common.world.objects.WorldGenSilverwoodTrees;

public class BiomeGenMagicalForest extends Biome {
   protected WorldGenBigMagicTree bigTree;
   private static final WorldGenBlockBlob blobs = new WorldGenBlockBlob(Blocks.field_150341_Y, 0);

   public BiomeGenMagicalForest(BiomeProperties par1) {
      super(par1);
      this.setRegistryName("thaumcraft", "magical_forest");
      this.bigTree = new WorldGenBigMagicTree(false);
      this.field_76762_K.add(new SpawnListEntry(EntityWolf.class, 2, 1, 3));
      this.field_76762_K.add(new SpawnListEntry(EntityHorse.class, 2, 1, 3));
      this.field_76761_J.add(new SpawnListEntry(EntityWitch.class, 3, 1, 1));
      this.field_76761_J.add(new SpawnListEntry(EntityEnderman.class, 3, 1, 1));
      this.field_76761_J.add(new SpawnListEntry(EntityVex.class, 1, 1, 1));
      if (ModConfig.CONFIG_WORLD.allowSpawnPech) {
         this.field_76761_J.add(new SpawnListEntry(EntityPech.class, 20, 1, 2));
      }

      if (ModConfig.CONFIG_WORLD.allowSpawnWisp) {
         this.field_76761_J.add(new SpawnListEntry(EntityWisp.class, 20, 1, 2));
      }

      this.field_76760_I.field_76832_z = 2;
      this.field_76760_I.field_76802_A = 10;
      this.field_76760_I.field_76803_B = 12;
      this.field_76760_I.field_76833_y = 6;
      this.field_76760_I.field_76798_D = 6;
   }

   public WorldGenAbstractTree func_150567_a(Random par1Random) {
      return (WorldGenAbstractTree)(par1Random.nextInt(18) == 0
         ? new WorldGenSilverwoodTrees(false, 8, 5)
         : (par1Random.nextInt(12) == 0 ? new WorldGenGreatwoodTrees(false, par1Random.nextInt(8) == 0) : this.bigTree));
   }

   public WorldGenerator func_76730_b(Random par1Random) {
      return par1Random.nextInt(4) == 0 ? new WorldGenTallGrass(EnumType.FERN) : new WorldGenTallGrass(EnumType.GRASS);
   }

   @SideOnly(Side.CLIENT)
   public int func_180627_b(BlockPos p_180627_1_) {
      return ModConfig.CONFIG_GRAPHICS.blueBiome ? 6728396 : 5635969;
   }

   @SideOnly(Side.CLIENT)
   public int func_180625_c(BlockPos p_180625_1_) {
      return ModConfig.CONFIG_GRAPHICS.blueBiome ? 7851246 : 6750149;
   }

   public int getWaterColorMultiplier() {
      return 30702;
   }

   public void func_180624_a(World world, Random random, BlockPos pos) {
      for (int a = 0; a < 3; a++) {
         BlockPos pp = new BlockPos(pos);
         pp = pp.func_177982_a(4 + random.nextInt(8), 0, 4 + random.nextInt(8));
         pp = world.func_175645_m(pp);

         while (pp.func_177956_o() > 30 && world.func_180495_p(pp).func_177230_c() != Blocks.field_150349_c) {
            pp = pp.func_177977_b();
         }

         Block l1 = world.func_180495_p(pp).func_177230_c();
         if (l1 == Blocks.field_150349_c) {
            world.func_180501_a(pp, BlocksTC.grassAmbient.func_176223_P(), 2);
            break;
         }
      }

      int k = random.nextInt(3);

      for (int l = 0; l < k; l++) {
         BlockPos p2 = new BlockPos(pos);
         p2 = p2.func_177982_a(random.nextInt(16) + 8, 0, random.nextInt(16) + 8);
         p2 = world.func_175645_m(p2);
         blobs.func_180709_b(world, random, p2);
      }

      for (int var10 = 0; var10 < 4; var10++) {
         for (int var11 = 0; var11 < 4; var11++) {
            if (random.nextInt(40) == 0) {
               BlockPos p2 = new BlockPos(pos);
               p2 = p2.func_177982_a(var10 * 4 + 1 + 8 + random.nextInt(3), 0, var11 * 4 + 1 + 8 + random.nextInt(3));
               p2 = world.func_175645_m(p2);
               WorldGenBigMushroom worldgenbigmushroom = new WorldGenBigMushroom();
               worldgenbigmushroom.func_180709_b(world, random, p2);
            }
         }
      }

      try {
         super.func_180624_a(world, random, pos);
      } catch (Exception var9) {
      }

      for (int a = 0; a < 8; a++) {
         BlockPos p2 = new BlockPos(pos);
         p2 = p2.func_177982_a(random.nextInt(16), 0, random.nextInt(16));
         p2 = world.func_175645_m(p2);

         while (p2.func_177956_o() > 50 && world.func_180495_p(p2).func_177230_c() != Blocks.field_150349_c) {
            p2 = p2.func_177977_b();
         }

         Block l2 = world.func_180495_p(p2).func_177230_c();
         if (l2 == Blocks.field_150349_c
            && world.func_180495_p(p2.func_177984_a()).func_177230_c().func_176200_f(world, p2.func_177984_a())
            && this.isBlockAdjacentToWood(world, p2.func_177984_a())) {
            world.func_180501_a(p2.func_177984_a(), BlocksTC.vishroom.func_176223_P(), 2);
         }
      }
   }

   private boolean isBlockAdjacentToWood(IBlockAccess world, BlockPos pos) {
      int count = 0;

      for (int xx = -1; xx <= 1; xx++) {
         for (int yy = -1; yy <= 1; yy++) {
            for (int zz = -1; zz <= 1; zz++) {
               if ((xx != 0 || yy != 0 || zz != 0) && Utils.isWoodLog(world, pos.func_177982_a(xx, yy, zz))) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public EnumFlowerType func_180623_a(Random rand, BlockPos pos) {
      double d0 = MathHelper.func_151237_a((1.0 + field_180281_af.func_151601_a(pos.func_177958_n() / 48.0, pos.func_177952_p() / 48.0)) / 2.0, 0.0, 0.9999);
      return EnumFlowerType.values()[(int)(d0 * EnumFlowerType.values().length)];
   }
}
