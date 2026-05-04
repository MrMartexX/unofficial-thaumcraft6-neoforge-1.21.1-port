package thaumcraft.common.golems;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.common.lib.network.FakeNetHandlerPlayServer;
import thaumcraft.common.lib.utils.InventoryUtils;

public class GolemInteractionHelper {
   public static void golemClick(World world, IGolemAPI golem, BlockPos pos, EnumFacing face, ItemStack clickStack, boolean sneaking, boolean rightClick) {
      FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile((UUID)null, "FakeThaumcraftGolem"));
      fp.field_71135_a = new FakeNetHandlerPlayServer(fp.field_71133_b, new NetworkManager(EnumPacketDirection.CLIENTBOUND), fp);
      fp.func_70080_a(
         golem.getGolemEntity().field_70165_t,
         golem.getGolemEntity().field_70163_u,
         golem.getGolemEntity().field_70161_v,
         golem.getGolemEntity().field_70177_z,
         golem.getGolemEntity().field_70125_A
      );
      IBlockState bs = world.func_180495_p(pos);
      fp.func_184611_a(EnumHand.MAIN_HAND, clickStack);
      fp.func_70095_a(sneaking);
      if (!rightClick) {
         try {
            fp.field_71134_c.func_180784_a(pos, face);
         } catch (Exception var11) {
         }
      } else {
         if (fp.func_184614_ca().func_77973_b() instanceof ItemBlock
            && !mayPlace(world, ((ItemBlock)fp.func_184614_ca().func_77973_b()).func_179223_d(), pos, face)) {
            golem.getGolemEntity()
               .func_70107_b(
                  golem.getGolemEntity().field_70165_t + face.func_82601_c(),
                  golem.getGolemEntity().field_70163_u + face.func_96559_d(),
                  golem.getGolemEntity().field_70161_v + face.func_82599_e()
               );
         }

         try {
            fp.field_71134_c.func_187251_a(fp, world, fp.func_184614_ca(), EnumHand.MAIN_HAND, pos, face, 0.5F, 0.5F, 0.5F);
         } catch (Exception var10) {
         }
      }

      golem.addRankXp(1);
      if (!fp.func_184614_ca().func_190926_b() && fp.func_184614_ca().func_190916_E() <= 0) {
         fp.func_184611_a(EnumHand.MAIN_HAND, ItemStack.field_190927_a);
      }

      dropSomeItems(fp, golem);
      golem.swingArm();
   }

   private static boolean mayPlace(World world, Block blockIn, BlockPos pos, EnumFacing side) {
      IBlockState block = world.func_180495_p(pos);
      AxisAlignedBB axisalignedbb = blockIn.func_185496_a(blockIn.func_176223_P(), world, pos);
      return axisalignedbb == null || world.func_72917_a(axisalignedbb, null);
   }

   private static void dropSomeItems(FakePlayer fp2, IGolemAPI golem) {
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

      for (int var3 = 0; var3 < fp2.field_71071_by.field_70460_b.size(); var3++) {
         if (!((ItemStack)fp2.field_71071_by.field_70460_b.get(var3)).func_190926_b()) {
            if (golem.canCarry((ItemStack)fp2.field_71071_by.field_70460_b.get(var3), true)) {
               fp2.field_71071_by.field_70460_b.set(var3, golem.holdItem((ItemStack)fp2.field_71071_by.field_70460_b.get(var3)));
            }

            if (!((ItemStack)fp2.field_71071_by.field_70462_a.get(var3)).func_190926_b()
               && ((ItemStack)fp2.field_71071_by.field_70460_b.get(var3)).func_190916_E() > 0) {
               InventoryUtils.dropItemAtEntity(golem.getGolemWorld(), (ItemStack)fp2.field_71071_by.field_70460_b.get(var3), golem.getGolemEntity());
            }

            fp2.field_71071_by.field_70460_b.set(var3, ItemStack.field_190927_a);
         }
      }
   }
}
