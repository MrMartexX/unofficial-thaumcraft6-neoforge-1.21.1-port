package thaumcraft.common.lib.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.internal.WeightedRandomLoot;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketBiomeChange;

public class Utils {
   public static HashMap<List<Object>, ItemStack> specialMiningResult = new HashMap<>();
   public static HashMap<List<Object>, Float> specialMiningChance = new HashMap<>();
   public static final String[] colorNames = new String[]{
      "White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray", "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"
   };
   public static final int[] colors = new int[]{
      15790320, 15435844, 12801229, 6719955, 14602026, 4312372, 14188952, 4408131, 10526880, 2651799, 8073150, 2437522, 5320730, 3887386, 11743532, 1973019
   };
   public static ArrayList<List> oreDictLogs = new ArrayList<>();

   public static boolean isChunkLoaded(World world, int x, int z) {
      Chunk chunk = world.func_72863_F().func_186026_b(x >> 4, z >> 4);
      return chunk != null && !chunk.func_76621_g();
   }

   public static boolean useBonemealAtLoc(World world, EntityPlayer player, BlockPos pos) {
      ItemStack is = new ItemStack(Items.field_151100_aR, 1, 15);
      return ItemDye.applyBonemeal(is, world, pos, player, EnumHand.MAIN_HAND);
   }

   public static boolean hasColor(byte[] colors) {
      for (byte col : colors) {
         if (col >= 0) {
            return true;
         }
      }

      return false;
   }

   public static void copyFile(File sourceFile, File destFile) throws IOException {
      if (!destFile.exists()) {
         destFile.createNewFile();
      }

      FileChannel source = null;
      FileChannel destination = null;

      try {
         source = new FileInputStream(sourceFile).getChannel();
         destination = new FileOutputStream(destFile).getChannel();
         destination.transferFrom(source, 0L, source.size());
      } finally {
         if (source != null) {
            source.close();
         }

         if (destination != null) {
            destination.close();
         }
      }
   }

   public static void addSpecialMiningResult(ItemStack in, ItemStack out, float chance) {
      specialMiningResult.put(Arrays.asList(in.func_77973_b(), in.func_77952_i()), out);
      specialMiningChance.put(Arrays.asList(in.func_77973_b(), in.func_77952_i()), chance);
   }

   public static ItemStack findSpecialMiningResult(ItemStack is, float chance, Random rand) {
      ItemStack dropped = is.func_77946_l();
      float r = rand.nextFloat();
      List ik = Arrays.asList(is.func_77973_b(), is.func_77952_i());
      if (specialMiningResult.containsKey(ik) && r <= chance * specialMiningChance.get(ik)) {
         dropped = specialMiningResult.get(ik).func_77946_l();
         dropped.func_190920_e(dropped.func_190916_E() * is.func_190916_E());
      }

      return dropped;
   }

   public static float clamp_float(float par0, float par1, float par2) {
      return par0 < par1 ? par1 : (par0 > par2 ? par2 : par0);
   }

   public static void setBiomeAt(World world, BlockPos pos, Biome biome) {
      setBiomeAt(world, pos, biome, true);
   }

   public static void setBiomeAt(World world, BlockPos pos, Biome biome, boolean sync) {
      if (biome != null) {
         Chunk chunk = world.func_175726_f(pos);
         byte[] array = chunk.func_76605_m();
         array[(pos.func_177952_p() & 15) << 4 | pos.func_177958_n() & 15] = (byte)(Biome.func_185362_a(biome) & 0xFF);
         chunk.func_76616_a(array);
         if (sync && !world.field_72995_K) {
            PacketHandler.INSTANCE
               .sendToAllAround(
                  new PacketBiomeChange(pos.func_177958_n(), pos.func_177952_p(), (short)Biome.func_185362_a(biome)),
                  new TargetPoint(world.field_73011_w.getDimension(), pos.func_177958_n(), world.func_175645_m(pos).func_177956_o(), pos.func_177952_p(), 32.0)
               );
         }
      }
   }

   public static boolean resetBiomeAt(World world, BlockPos pos) {
      return resetBiomeAt(world, pos, true);
   }

   public static boolean resetBiomeAt(World world, BlockPos pos, boolean sync) {
      Biome[] biomesForGeneration = null;
      biomesForGeneration = world.func_72959_q().func_76937_a(biomesForGeneration, pos.func_177958_n(), pos.func_177952_p(), 1, 1);
      if (biomesForGeneration != null && biomesForGeneration[0] != null) {
         Biome biome = biomesForGeneration[0];
         if (biome != world.func_180494_b(pos)) {
            setBiomeAt(world, pos, biome, sync);
            return true;
         }
      }

      return false;
   }

   public static boolean isWoodLog(IBlockAccess world, BlockPos pos) {
      IBlockState bs = world.func_180495_p(pos);
      Block bi = bs.func_177230_c();
      return !bi.isWood(world, pos) && !bi.canSustainLeaves(bs, world, pos) ? oreDictLogs.contains(Arrays.asList(bi, bi.func_176201_c(bs))) : true;
   }

   public static boolean isOreBlock(World world, BlockPos pos) {
      IBlockState bi = world.func_180495_p(pos);
      if (bi.func_177230_c() != Blocks.field_150350_a && bi.func_177230_c() != Blocks.field_150357_h) {
         ItemStack is = BlockUtils.getSilkTouchDrop(bi);
         if (is == null || is.func_190926_b()) {
            int md = bi.func_177230_c().func_176201_c(bi);
            is = new ItemStack(bi.func_177230_c(), 1, md);
         }

         if (is == null || is.func_190926_b() || is.func_77973_b() == null) {
            return false;
         }

         int[] od = OreDictionary.getOreIDs(is);
         if (od != null && od.length > 0) {
            for (int id : od) {
               if (OreDictionary.getOreName(id) != null && OreDictionary.getOreName(id).toUpperCase().contains("ORE")) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static int setNibble(int data, int nibble, int nibbleIndex) {
      int shift = nibbleIndex * 4;
      return data & ~(15 << shift) | nibble << shift;
   }

   public static int getNibble(int data, int nibbleIndex) {
      return data >> (nibbleIndex << 2) & 15;
   }

   public static boolean getBit(int value, int bit) {
      return (value & 1 << bit) != 0;
   }

   public static int setBit(int value, int bit) {
      return value | 1 << bit;
   }

   public static int clearBit(int value, int bit) {
      return value & ~(1 << bit);
   }

   public static int toggleBit(int value, int bit) {
      return value ^ 1 << bit;
   }

   public static byte pack(boolean... vals) {
      byte result = 0;

      for (boolean bit : vals) {
         result = (byte)(result << 1 | (bit ? 1 : 0) & 1);
      }

      return result;
   }

   public static boolean[] unpack(byte val) {
      boolean[] result = new boolean[8];

      for (int i = 0; i < 8; i++) {
         result[i] = (byte)(val >> 7 - i & 1) == 1;
      }

      return result;
   }

   public static final byte[] intToByteArray(int value) {
      return new byte[]{(byte)(value >>> 24), (byte)(value >>> 16), (byte)(value >>> 8), (byte)value};
   }

   public static int byteArraytoInt(byte[] bytes) {
      return bytes[0] << 24 | bytes[1] << 16 | bytes[2] << 8 | bytes[3];
   }

   public static final byte[] shortToByteArray(short value) {
      return new byte[]{(byte)(value >>> 8), (byte)value};
   }

   public static short byteArraytoShort(byte[] bytes) {
      return (short)(bytes[0] << 8 | bytes[1]);
   }

   public static boolean isLyingInCone(double[] x, double[] t, double[] b, float aperture) {
      double halfAperture = aperture / 2.0F;
      double[] apexToXVect = dif(t, x);
      double[] axisVect = dif(t, b);
      boolean isInInfiniteCone = dotProd(apexToXVect, axisVect) / magn(apexToXVect) / magn(axisVect) > Math.cos(halfAperture);
      return !isInInfiniteCone ? false : dotProd(apexToXVect, axisVect) / magn(axisVect) < magn(axisVect);
   }

   public static double dotProd(double[] a, double[] b) {
      return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
   }

   public static double[] dif(double[] a, double[] b) {
      return new double[]{a[0] - b[0], a[1] - b[1], a[2] - b[2]};
   }

   public static double magn(double[] a) {
      return Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
   }

   public static Vec3d calculateVelocity(Vec3d from, Vec3d to, double heightGain, double gravity) {
      double endGain = to.field_72448_b - from.field_72448_b;
      double horizDist = Math.sqrt(distanceSquared2d(from, to));
      double gain = heightGain;
      double maxGain = gain > endGain + gain ? gain : endGain + gain;
      double a = -horizDist * horizDist / (4.0 * maxGain);
      double b = horizDist;
      double c = -endGain;
      double slope = -b / (2.0 * a) - Math.sqrt(b * b - 4.0 * a * c) / (2.0 * a);
      double vy = Math.sqrt(maxGain * gravity);
      double vh = vy / slope;
      double dx = to.field_72450_a - from.field_72450_a;
      double dz = to.field_72449_c - from.field_72449_c;
      double mag = Math.sqrt(dx * dx + dz * dz);
      double dirx = dx / mag;
      double dirz = dz / mag;
      double vx = vh * dirx;
      double vz = vh * dirz;
      return new Vec3d(vx, vy, vz);
   }

   public static double distanceSquared2d(Vec3d from, Vec3d to) {
      double dx = to.field_72450_a - from.field_72450_a;
      double dz = to.field_72449_c - from.field_72449_c;
      return dx * dx + dz * dz;
   }

   public static double distanceSquared3d(Vec3d from, Vec3d to) {
      double dx = to.field_72450_a - from.field_72450_a;
      double dy = to.field_72448_b - from.field_72448_b;
      double dz = to.field_72449_c - from.field_72449_c;
      return dx * dx + dy * dy + dz * dz;
   }

   public static ItemStack generateLoot(int rarity, Random rand) {
      ItemStack is = ItemStack.field_190927_a;
      if (rarity > 0 && rand.nextFloat() < 0.025F * rarity) {
         is = genGear(rarity, rand);
         if (is.func_190926_b()) {
            is = generateLoot(rarity, rand);
         }
      } else {
         switch (rarity) {
            case 1:
               is = ((WeightedRandomLoot)WeightedRandom.func_76271_a(rand, WeightedRandomLoot.lootBagUncommon)).item;
               break;
            case 2:
               is = ((WeightedRandomLoot)WeightedRandom.func_76271_a(rand, WeightedRandomLoot.lootBagRare)).item;
               break;
            default:
               is = ((WeightedRandomLoot)WeightedRandom.func_76271_a(rand, WeightedRandomLoot.lootBagCommon)).item;
         }
      }

      if (is.func_77973_b() == Items.field_151122_aG) {
         EnchantmentHelper.func_77504_a(rand, is, (int)(5.0F + rarity * 0.75F * rand.nextInt(18)), false);
      }

      return is.func_77946_l();
   }

   private static ItemStack genGear(int rarity, Random rand) {
      ItemStack is = ItemStack.field_190927_a;
      int quality = rand.nextInt(2);
      if (rand.nextFloat() < 0.2F) {
         quality++;
      }

      if (rand.nextFloat() < 0.15F) {
         quality++;
      }

      if (rand.nextFloat() < 0.1F) {
         quality++;
      }

      if (rand.nextFloat() < 0.095F) {
         quality++;
      }

      if (rand.nextFloat() < 0.095F) {
         quality++;
      }

      Item item = getGearItemForSlot(rand.nextInt(5), quality);
      if (item != null) {
         is = new ItemStack(item, 1, rand.nextInt(1 + item.func_77612_l() / 6));
         if (rand.nextInt(4) < rarity) {
            EnchantmentHelper.func_77504_a(rand, is, (int)(5.0F + rarity * 0.75F * rand.nextInt(18)), false);
         }

         return is.func_77946_l();
      } else {
         return ItemStack.field_190927_a;
      }
   }

   private static Item getGearItemForSlot(int slot, int quality) {
      switch (slot) {
         case 4:
            if (quality == 0) {
               return Items.field_151024_Q;
            } else if (quality == 1) {
               return Items.field_151169_ag;
            } else if (quality == 2) {
               return Items.field_151020_U;
            } else if (quality == 3) {
               return Items.field_151028_Y;
            } else if (quality == 4) {
               return ItemsTC.thaumiumHelm;
            } else if (quality == 5) {
               return Items.field_151161_ac;
            } else if (quality == 6) {
               return ItemsTC.voidHelm;
            }
         case 3:
            if (quality == 0) {
               return Items.field_151027_R;
            } else if (quality == 1) {
               return Items.field_151171_ah;
            } else if (quality == 2) {
               return Items.field_151023_V;
            } else if (quality == 3) {
               return Items.field_151030_Z;
            } else if (quality == 4) {
               return ItemsTC.thaumiumChest;
            } else if (quality == 5) {
               return Items.field_151163_ad;
            } else if (quality == 6) {
               return ItemsTC.voidChest;
            }
         case 2:
            if (quality == 0) {
               return Items.field_151026_S;
            } else if (quality == 1) {
               return Items.field_151149_ai;
            } else if (quality == 2) {
               return Items.field_151022_W;
            } else if (quality == 3) {
               return Items.field_151165_aa;
            } else if (quality == 4) {
               return ItemsTC.thaumiumLegs;
            } else if (quality == 5) {
               return Items.field_151173_ae;
            } else if (quality == 6) {
               return ItemsTC.voidLegs;
            }
         case 1:
            if (quality == 0) {
               return Items.field_151021_T;
            } else if (quality == 1) {
               return Items.field_151151_aj;
            } else if (quality == 2) {
               return Items.field_151029_X;
            } else if (quality == 3) {
               return Items.field_151167_ab;
            } else if (quality == 4) {
               return ItemsTC.thaumiumBoots;
            } else if (quality == 5) {
               return Items.field_151175_af;
            } else if (quality == 6) {
               return ItemsTC.voidBoots;
            }
         case 0:
            if (quality == 0) {
               return Items.field_151036_c;
            } else if (quality == 1) {
               return Items.field_151040_l;
            } else if (quality == 2) {
               return Items.field_151006_E;
            } else if (quality == 3) {
               return Items.field_151010_B;
            } else if (quality == 4) {
               return ItemsTC.thaumiumSword;
            } else if (quality == 5) {
               return Items.field_151048_u;
            } else if (quality == 6) {
               return ItemsTC.voidSword;
            }
         default:
            return null;
      }
   }

   public static void writeItemStackToBuffer(ByteBuf bb, ItemStack stack) {
      if (stack != null && !stack.func_190926_b()) {
         bb.writeShort(Item.func_150891_b(stack.func_77973_b()));
         bb.writeShort(stack.func_190916_E());
         bb.writeShort(stack.func_77960_j());
         NBTTagCompound nbttagcompound = null;
         if (stack.func_77973_b().func_77645_m() || stack.func_77973_b().func_77651_p()) {
            nbttagcompound = stack.func_77978_p();
         }

         writeNBTTagCompoundToBuffer(bb, nbttagcompound);
      } else {
         bb.writeShort(-1);
      }
   }

   public static ItemStack readItemStackFromBuffer(ByteBuf bb) {
      ItemStack itemstack = ItemStack.field_190927_a;
      short short1 = bb.readShort();
      if (short1 >= 0) {
         short b0 = bb.readShort();
         short short2 = bb.readShort();
         itemstack = new ItemStack(Item.func_150899_d(short1), b0, short2);
         itemstack.func_77982_d(readNBTTagCompoundFromBuffer(bb));
      }

      return itemstack;
   }

   public static void writeNBTTagCompoundToBuffer(ByteBuf bb, NBTTagCompound nbt) {
      if (nbt == null) {
         bb.writeByte(0);
      } else {
         try {
            CompressedStreamTools.func_74800_a(nbt, new ByteBufOutputStream(bb));
         } catch (IOException ioexception) {
            throw new EncoderException(ioexception);
         }
      }
   }

   public static NBTTagCompound readNBTTagCompoundFromBuffer(ByteBuf bb) {
      int i = bb.readerIndex();
      byte b0 = bb.readByte();
      if (b0 == 0) {
         return null;
      }

      bb.readerIndex(i);

      try {
         return CompressedStreamTools.func_152456_a(new ByteBufInputStream(bb), new NBTSizeTracker(2097152L));
      } catch (IOException var4) {
         return null;
      }
   }

   public static Vec3d rotateAsBlock(Vec3d vec, EnumFacing side) {
      return rotate(vec.func_178786_a(0.5, 0.5, 0.5), side).func_72441_c(0.5, 0.5, 0.5);
   }

   public static Vec3d rotateAsBlockRev(Vec3d vec, EnumFacing side) {
      return revRotate(vec.func_178786_a(0.5, 0.5, 0.5), side).func_72441_c(0.5, 0.5, 0.5);
   }

   public static Vec3d rotate(Vec3d vec, EnumFacing side) {
      switch (side) {
         case DOWN:
            return new Vec3d(vec.field_72450_a, -vec.field_72448_b, -vec.field_72449_c);
         case UP:
            return new Vec3d(vec.field_72450_a, vec.field_72448_b, vec.field_72449_c);
         case NORTH:
            return new Vec3d(vec.field_72450_a, vec.field_72449_c, -vec.field_72448_b);
         case SOUTH:
            return new Vec3d(vec.field_72450_a, -vec.field_72449_c, vec.field_72448_b);
         case WEST:
            return new Vec3d(-vec.field_72448_b, vec.field_72450_a, vec.field_72449_c);
         case EAST:
            return new Vec3d(vec.field_72448_b, -vec.field_72450_a, vec.field_72449_c);
         default:
            return null;
      }
   }

   public static Vec3d revRotate(Vec3d vec, EnumFacing side) {
      switch (side) {
         case DOWN:
            return new Vec3d(vec.field_72450_a, -vec.field_72448_b, -vec.field_72449_c);
         case UP:
            return new Vec3d(vec.field_72450_a, vec.field_72448_b, vec.field_72449_c);
         case NORTH:
            return new Vec3d(vec.field_72450_a, -vec.field_72449_c, vec.field_72448_b);
         case SOUTH:
            return new Vec3d(vec.field_72450_a, vec.field_72449_c, -vec.field_72448_b);
         case WEST:
            return new Vec3d(vec.field_72448_b, -vec.field_72450_a, vec.field_72449_c);
         case EAST:
            return new Vec3d(-vec.field_72448_b, vec.field_72450_a, vec.field_72449_c);
         default:
            return null;
      }
   }

   public static Vec3d rotateAroundX(Vec3d vec, float angle) {
      float var2 = MathHelper.func_76134_b(angle);
      float var3 = MathHelper.func_76126_a(angle);
      double var4 = vec.field_72450_a;
      double var6 = vec.field_72448_b * var2 + vec.field_72449_c * var3;
      double var8 = vec.field_72449_c * var2 - vec.field_72448_b * var3;
      return new Vec3d(var4, var6, var8);
   }

   public static Vec3d rotateAroundY(Vec3d vec, float angle) {
      float var2 = MathHelper.func_76134_b(angle);
      float var3 = MathHelper.func_76126_a(angle);
      double var4 = vec.field_72450_a * var2 + vec.field_72449_c * var3;
      double var6 = vec.field_72448_b;
      double var8 = vec.field_72449_c * var2 - vec.field_72450_a * var3;
      return new Vec3d(var4, var6, var8);
   }

   public static Vec3d rotateAroundZ(Vec3d vec, float angle) {
      float var2 = MathHelper.func_76134_b(angle);
      float var3 = MathHelper.func_76126_a(angle);
      double var4 = vec.field_72450_a * var2 + vec.field_72448_b * var3;
      double var6 = vec.field_72448_b * var2 - vec.field_72450_a * var3;
      double var8 = vec.field_72449_c;
      return new Vec3d(var4, var6, var8);
   }

   public static RayTraceResult rayTrace(World worldIn, Entity entityIn, boolean useLiquids) {
      double d3 = 5.0;
      if (entityIn instanceof EntityPlayerMP) {
         d3 = ((EntityPlayerMP)entityIn).field_71134_c.getBlockReachDistance();
      }

      return rayTrace(worldIn, entityIn, useLiquids, d3);
   }

   public static RayTraceResult rayTrace(World worldIn, Entity entityIn, boolean useLiquids, double range) {
      float f = entityIn.field_70125_A;
      float f1 = entityIn.field_70177_z;
      double d0 = entityIn.field_70165_t;
      double d1 = entityIn.field_70163_u + entityIn.func_70047_e();
      double d2 = entityIn.field_70161_v;
      Vec3d vec3d = new Vec3d(d0, d1, d2);
      float f2 = MathHelper.func_76134_b(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f3 = MathHelper.func_76126_a(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f4 = -MathHelper.func_76134_b(-f * (float) (Math.PI / 180.0));
      float f5 = MathHelper.func_76126_a(-f * (float) (Math.PI / 180.0));
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      Vec3d vec3d1 = vec3d.func_72441_c(f6 * range, f5 * range, f7 * range);
      return worldIn.func_147447_a(vec3d, vec3d1, useLiquids, !useLiquids, false);
   }

   public static RayTraceResult rayTrace(World worldIn, Entity entityIn, Vec3d lookvec, boolean useLiquids, double range) {
      double d0 = entityIn.field_70165_t;
      double d1 = entityIn.field_70163_u + entityIn.func_70047_e();
      double d2 = entityIn.field_70161_v;
      Vec3d vec3d = new Vec3d(d0, d1, d2);
      Vec3d vec3d1 = vec3d.func_72441_c(lookvec.field_72450_a * range, lookvec.field_72448_b * range, lookvec.field_72449_c * range);
      return worldIn.func_147447_a(vec3d, vec3d1, useLiquids, !useLiquids, false);
   }

   public static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
      try {
         return clazz.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
         Class superClass = clazz.getSuperclass();
         if (superClass == null) {
            throw e;
         } else {
            return getField(superClass, fieldName);
         }
      }
   }

   public static AxisAlignedBB rotateBlockAABB(AxisAlignedBB aabb, EnumFacing facing) {
      Cuboid6 c = new Cuboid6(aabb).add(new Vector3(-0.5, -0.5, -0.5)).apply(Rotation.sideRotations[facing.func_176745_a()]).add(new Vector3(0.5, 0.5, 0.5));
      return c.aabb();
   }
}
