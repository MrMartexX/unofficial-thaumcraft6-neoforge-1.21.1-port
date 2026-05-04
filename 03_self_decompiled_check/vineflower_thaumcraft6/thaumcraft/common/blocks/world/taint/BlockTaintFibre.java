package thaumcraft.common.blocks.world.taint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.codechicken.lib.raytracer.ExtendedMOP;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.SoundsTC;

@EventBusSubscriber(Side.CLIENT)
public class BlockTaintFibre extends Block implements ITaintBlock {
   public static final PropertyBool NORTH = PropertyBool.func_177716_a("north");
   public static final PropertyBool EAST = PropertyBool.func_177716_a("east");
   public static final PropertyBool SOUTH = PropertyBool.func_177716_a("south");
   public static final PropertyBool WEST = PropertyBool.func_177716_a("west");
   public static final PropertyBool UP = PropertyBool.func_177716_a("up");
   public static final PropertyBool DOWN = PropertyBool.func_177716_a("down");
   public static final PropertyBool GROWTH1 = PropertyBool.func_177716_a("growth1");
   public static final PropertyBool GROWTH2 = PropertyBool.func_177716_a("growth2");
   public static final PropertyBool GROWTH3 = PropertyBool.func_177716_a("growth3");
   public static final PropertyBool GROWTH4 = PropertyBool.func_177716_a("growth4");
   private RayTracer rayTracer = new RayTracer();
   protected static final AxisAlignedBB AABB_EMPTY = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   protected static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0.0, 0.95F, 0.0, 1.0, 1.0, 1.0);
   protected static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.05F, 1.0);
   protected static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.95F, 0.0, 0.0, 1.0, 1.0, 1.0);
   protected static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.0, 0.0, 0.0, 0.05F, 1.0, 1.0);
   protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.0, 0.0, 0.95F, 1.0, 1.0, 1.0);
   protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.05F);

   public BlockTaintFibre() {
      super(ThaumcraftMaterials.MATERIAL_TAINT);
      this.func_149663_c("taint_fibre");
      this.setRegistryName("thaumcraft", "taint_fibre");
      this.func_149711_c(1.0F);
      this.func_149672_a(SoundsTC.GORE);
      this.func_149675_a(true);
      this.func_149647_a(ConfigItems.TABTC);
      this.func_180632_j(
         this.field_176227_L
            .func_177621_b()
            .func_177226_a(NORTH, false)
            .func_177226_a(EAST, false)
            .func_177226_a(SOUTH, false)
            .func_177226_a(WEST, false)
            .func_177226_a(UP, false)
            .func_177226_a(DOWN, false)
            .func_177226_a(GROWTH1, false)
            .func_177226_a(GROWTH2, false)
            .func_177226_a(GROWTH3, false)
            .func_177226_a(GROWTH4, false)
      );
   }

   public SoundType func_185467_w() {
      return SoundsTC.GORE;
   }

   public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 3;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 3;
   }

   public MapColor func_180659_g(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return MapColor.field_151678_z;
   }

   @Override
   public void die(World world, BlockPos pos, IBlockState blockState) {
      world.func_175698_g(pos);
   }

   protected boolean func_149700_E() {
      return false;
   }

   public Item func_180660_a(IBlockState state, Random rand, int fortune) {
      return Item.func_150899_d(0);
   }

   public void func_180653_a(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      state = this.func_176221_a(state, worldIn, pos);
      if (state instanceof IBlockState && (Boolean)state.func_177229_b(GROWTH3)) {
         if (worldIn.field_73012_v.nextInt(5) <= fortune) {
            func_180635_a(worldIn, pos, ConfigItems.FLUX_CRYSTAL.func_77946_l());
         }

         AuraHelper.polluteAura(worldIn, pos, 1.0F, true);
      }
   }

   public void func_180650_b(World world, BlockPos pos, IBlockState state, Random random) {
      if (!world.field_72995_K) {
         state = this.func_176221_a(state, world, pos);
         if (state instanceof IBlockState) {
            if (!(Boolean)state.func_177229_b(GROWTH1)
               && !(Boolean)state.func_177229_b(GROWTH2)
               && !(Boolean)state.func_177229_b(GROWTH3)
               && !(Boolean)state.func_177229_b(GROWTH4)
               && isOnlyAdjacentToTaint(world, pos)) {
               this.die(world, pos, state);
            } else if (!TaintHelper.isNearTaintSeed(world, pos)) {
               this.die(world, pos, state);
            } else {
               TaintHelper.spreadFibres(world, pos);
            }
         }
      }
   }

   public void func_189540_a(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos pos2) {
      state = this.func_176221_a(state, worldIn, pos);
      if (state instanceof IBlockState
         && !(Boolean)state.func_177229_b(GROWTH1)
         && !(Boolean)state.func_177229_b(GROWTH2)
         && !(Boolean)state.func_177229_b(GROWTH3)
         && !(Boolean)state.func_177229_b(GROWTH4)
         && isOnlyAdjacentToTaint(worldIn, pos)) {
         worldIn.func_175698_g(pos);
      }
   }

   public static int getAdjacentTaint(IBlockAccess world, BlockPos pos) {
      int count = 0;

      for (EnumFacing dir : EnumFacing.field_82609_l) {
         if (world.func_180495_p(pos.func_177972_a(dir)).func_185904_a() != ThaumcraftMaterials.MATERIAL_TAINT) {
            count++;
         }
      }

      return count;
   }

   public static boolean isOnlyAdjacentToTaint(World world, BlockPos pos) {
      for (EnumFacing dir : EnumFacing.field_82609_l) {
         if (!world.func_175623_d(pos.func_177972_a(dir))
            && world.func_180495_p(pos.func_177972_a(dir)).func_185904_a() != ThaumcraftMaterials.MATERIAL_TAINT
            && world.func_180495_p(pos.func_177972_a(dir))
               .func_177230_c()
               .isSideSolid(world.func_180495_p(pos.func_177972_a(dir)), world, pos.func_177972_a(dir), dir.func_176734_d())) {
            return false;
         }
      }

      return true;
   }

   public static boolean isHemmedByTaint(World world, BlockPos pos) {
      int c = 0;

      for (EnumFacing dir : EnumFacing.field_82609_l) {
         IBlockState block = world.func_180495_p(pos.func_177972_a(dir));
         if (block.func_185904_a() == ThaumcraftMaterials.MATERIAL_TAINT) {
            c++;
         } else if (world.func_175623_d(pos.func_177972_a(dir))) {
            c--;
         } else if (!block.func_185904_a().func_76224_d() && !block.isSideSolid(world, pos.func_177972_a(dir), dir.func_176734_d())) {
            c--;
         }
      }

      return c > 0;
   }

   public void func_176199_a(World world, BlockPos pos, Entity entity) {
      if (!world.field_72995_K && entity instanceof EntityLivingBase && !((EntityLivingBase)entity).func_70662_br() && world.field_73012_v.nextInt(750) == 0) {
         ((EntityLivingBase)entity).func_70690_d(new PotionEffect(PotionFluxTaint.instance, 200, 0, false, true));
      }
   }

   public boolean func_189539_a(IBlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
      if (eventID == 1) {
         if (worldIn.field_72995_K) {
            worldIn.func_184133_a(null, pos, SoundEvents.field_187540_ab, SoundCategory.BLOCKS, 0.1F, 0.9F + worldIn.field_73012_v.nextFloat() * 0.2F);
         }

         return true;
      } else {
         return super.func_189539_a(state, worldIn, pos, eventID, eventParam);
      }
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      return false;
   }

   private boolean drawAt(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      IBlockState b = worldIn.func_180495_p(pos);
      return b.func_177230_c() != BlocksTC.taintFibre && b.func_177230_c() != BlocksTC.taintFeature && b.isSideSolid(worldIn, pos, side.func_176734_d());
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void onBlockHighlight(DrawBlockHighlightEvent event) {
      if (event.getTarget().field_72313_a == Type.BLOCK
         && event.getPlayer().field_70170_p.func_180495_p(event.getTarget().func_178782_a()).func_177230_c() == this) {
         RayTracer.retraceBlock(event.getPlayer().field_70170_p, event.getPlayer(), event.getTarget().func_178782_a());
      }
   }

   public RayTraceResult func_180636_a(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
      List<IndexedCuboid6> cuboids = new LinkedList<>();
      if (this.drawAt(world, pos.func_177984_a(), EnumFacing.UP)) {
         cuboids.add(new IndexedCuboid6(0, new Cuboid6(AABB_UP.func_186670_a(pos))));
      }

      if (this.drawAt(world, pos.func_177977_b(), EnumFacing.DOWN)) {
         cuboids.add(new IndexedCuboid6(1, new Cuboid6(AABB_DOWN.func_186670_a(pos))));
      }

      if (this.drawAt(world, pos.func_177974_f(), EnumFacing.EAST)) {
         cuboids.add(new IndexedCuboid6(2, new Cuboid6(AABB_EAST.func_186670_a(pos))));
      }

      if (this.drawAt(world, pos.func_177976_e(), EnumFacing.WEST)) {
         cuboids.add(new IndexedCuboid6(3, new Cuboid6(AABB_WEST.func_186670_a(pos))));
      }

      if (this.drawAt(world, pos.func_177968_d(), EnumFacing.SOUTH)) {
         cuboids.add(new IndexedCuboid6(4, new Cuboid6(AABB_SOUTH.func_186670_a(pos))));
      }

      if (this.drawAt(world, pos.func_177978_c(), EnumFacing.NORTH)) {
         cuboids.add(new IndexedCuboid6(5, new Cuboid6(AABB_NORTH.func_186670_a(pos))));
      }

      IBlockState ss = this.func_176221_a(world.func_180495_p(pos), world, pos);
      if (ss.func_177230_c() == this && ss instanceof IBlockState) {
         if ((Boolean)ss.func_177229_b(GROWTH1)) {
            cuboids.add(new IndexedCuboid6(6, new Cuboid6(new AxisAlignedBB(0.1F, 0.0, 0.1F, 0.9F, 0.4F, 0.9F).func_186670_a(pos))));
         } else if ((Boolean)ss.func_177229_b(GROWTH2)) {
            cuboids.add(new IndexedCuboid6(6, new Cuboid6(new AxisAlignedBB(0.2F, 0.0, 0.2F, 0.8F, 1.0, 0.8F).func_186670_a(pos))));
         } else if ((Boolean)ss.func_177229_b(GROWTH3)) {
            cuboids.add(new IndexedCuboid6(6, new Cuboid6(new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.3125, 0.75).func_186670_a(pos))));
         } else if ((Boolean)ss.func_177229_b(GROWTH4)) {
            cuboids.add(new IndexedCuboid6(6, new Cuboid6(new AxisAlignedBB(0.1F, 0.3F, 0.1F, 0.9F, 1.0, 0.9F).func_186670_a(pos))));
         }
      }

      ArrayList<ExtendedMOP> list = new ArrayList<>();
      this.rayTracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(pos), this, list);
      return list.size() > 0 ? list.get(0) : super.func_180636_a(state, world, pos, start, end);
   }

   public AxisAlignedBB func_185496_a(IBlockState s, IBlockAccess source, BlockPos pos) {
      return AABB_EMPTY;
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB func_180640_a(IBlockState s, World world, BlockPos pos) {
      IBlockState state = this.func_176221_a(world.func_180495_p(pos), world, pos);
      if (state.func_177230_c() == this && state instanceof IBlockState) {
         if ((Boolean)state.func_177229_b(GROWTH1)) {
            return new AxisAlignedBB(0.1F, 0.0, 0.1F, 0.9F, 0.4F, 0.9F).func_186670_a(pos);
         }

         if ((Boolean)state.func_177229_b(GROWTH2)) {
            return new AxisAlignedBB(0.2F, 0.0, 0.2F, 0.8F, 1.0, 0.8F).func_186670_a(pos);
         }

         if ((Boolean)state.func_177229_b(GROWTH3)) {
            return new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.3125, 0.75).func_186670_a(pos);
         }

         if ((Boolean)state.func_177229_b(GROWTH4)) {
            return new AxisAlignedBB(0.1F, 0.3F, 0.1F, 0.9F, 1.0, 0.9F).func_186670_a(pos);
         }
      }

      RayTraceResult hit = RayTracer.retraceBlock(world, Minecraft.func_71410_x().field_71439_g, pos);
      if (hit != null) {
         switch (hit.subHit) {
            case 0:
               return AABB_UP.func_186670_a(pos);
            case 1:
               return AABB_DOWN.func_186670_a(pos);
            case 2:
               return AABB_EAST.func_186670_a(pos);
            case 3:
               return AABB_WEST.func_186670_a(pos);
            case 4:
               return AABB_SOUTH.func_186670_a(pos);
            case 5:
               return AABB_NORTH.func_186670_a(pos);
         }
      }

      return AABB_EMPTY;
   }

   public void func_185477_a(
      IBlockState state,
      World worldIn,
      BlockPos pos,
      AxisAlignedBB entityBox,
      List<AxisAlignedBB> collidingBoxes,
      @Nullable Entity entityIn,
      boolean isActualState
   ) {
      if (this.drawAt(worldIn, pos.func_177984_a(), EnumFacing.UP)) {
         func_185492_a(pos, entityBox, collidingBoxes, AABB_UP);
      }

      if (this.drawAt(worldIn, pos.func_177977_b(), EnumFacing.DOWN)) {
         func_185492_a(pos, entityBox, collidingBoxes, AABB_DOWN);
      }

      if (this.drawAt(worldIn, pos.func_177974_f(), EnumFacing.EAST)) {
         func_185492_a(pos, entityBox, collidingBoxes, AABB_EAST);
      }

      if (this.drawAt(worldIn, pos.func_177976_e(), EnumFacing.WEST)) {
         func_185492_a(pos, entityBox, collidingBoxes, AABB_WEST);
      }

      if (this.drawAt(worldIn, pos.func_177968_d(), EnumFacing.SOUTH)) {
         func_185492_a(pos, entityBox, collidingBoxes, AABB_SOUTH);
      }

      if (this.drawAt(worldIn, pos.func_177978_c(), EnumFacing.NORTH)) {
         func_185492_a(pos, entityBox, collidingBoxes, AABB_NORTH);
      }
   }

   public boolean func_176200_f(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   public boolean func_176205_b(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   public boolean func_149662_c(IBlockState state) {
      return false;
   }

   public boolean func_149686_d(IBlockState state) {
      return false;
   }

   public int func_176201_c(IBlockState state) {
      return 0;
   }

   public int getLightValue(IBlockState state2, IBlockAccess world, BlockPos pos) {
      IBlockState state = this.func_176221_a(world.func_180495_p(pos), world, pos);
      if (state.func_177230_c() == this && state instanceof IBlockState) {
         return state.func_177229_b(GROWTH3)
            ? 12
            : (!state.func_177229_b(GROWTH2) && !state.func_177229_b(GROWTH4) ? super.getLightValue(state2, world, pos) : 6);
      } else {
         return super.getLightValue(state2, world, pos);
      }
   }

   private Boolean[] makeConnections(IBlockState state, IBlockAccess world, BlockPos pos) {
      Boolean[] cons = new Boolean[]{false, false, false, false, false, false};
      int a = 0;

      for (EnumFacing face : EnumFacing.field_82609_l) {
         if (this.drawAt(world, pos.func_177972_a(face), face)) {
            cons[a] = true;
         }

         a++;
      }

      return cons;
   }

   public IBlockState func_176221_a(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      Boolean[] cons = this.makeConnections(state, worldIn, pos);
      boolean d = this.drawAt(worldIn, pos.func_177977_b(), EnumFacing.DOWN);
      boolean u = this.drawAt(worldIn, pos.func_177984_a(), EnumFacing.UP);
      int growth = 0;
      Random rand = new Random(pos.func_177986_g());
      int q = rand.nextInt(50);
      if (d) {
         if (q < 4) {
            growth = 1;
         } else if (q == 4 || q == 5) {
            growth = 2;
         } else if (q == 6) {
            growth = 3;
         }
      }

      if (u && q > 47) {
         growth = 4;
      }

      try {
         return state.func_177226_a(DOWN, cons[0])
            .func_177226_a(UP, cons[1])
            .func_177226_a(NORTH, cons[2])
            .func_177226_a(SOUTH, cons[3])
            .func_177226_a(WEST, cons[4])
            .func_177226_a(EAST, cons[5])
            .func_177226_a(GROWTH1, growth == 1)
            .func_177226_a(GROWTH2, growth == 2)
            .func_177226_a(GROWTH3, growth == 3)
            .func_177226_a(GROWTH4, growth == 4);
      } catch (Exception e) {
         return state;
      }
   }

   protected BlockStateContainer func_180661_e() {
      return new BlockStateContainer(this, new IProperty[]{NORTH, EAST, SOUTH, WEST, UP, DOWN, GROWTH1, GROWTH2, GROWTH3, GROWTH4});
   }
}
