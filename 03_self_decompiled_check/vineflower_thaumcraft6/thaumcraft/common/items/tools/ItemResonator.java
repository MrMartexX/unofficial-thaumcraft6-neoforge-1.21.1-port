package thaumcraft.common.items.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.tiles.devices.TileCondenser;
import thaumcraft.common.tiles.essentia.TileTubeBuffer;

public class ItemResonator extends ItemTCBase {
   public ItemResonator() {
      super("resonator");
      this.func_77625_d(1);
      this.func_77625_d(1);
      this.func_77637_a(ConfigItems.TABTC);
   }

   public EnumRarity func_77613_e(ItemStack itemstack) {
      return EnumRarity.UNCOMMON;
   }

   public boolean func_77636_d(ItemStack stack1) {
      return stack1.func_77942_o();
   }

   public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
      TileEntity tile = world.func_175625_s(pos);
      if (tile == null || !(tile instanceof IEssentiaTransport)) {
         return EnumActionResult.FAIL;
      }

      if (world.field_72995_K) {
         player.func_184609_a(hand);
         return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
      }

      IEssentiaTransport et = (IEssentiaTransport)tile;
      RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
      if (hit != null && hit.subHit >= 0 && hit.subHit < 6) {
         side = EnumFacing.field_82609_l[hit.subHit];
      }

      if (!(tile instanceof TileTubeBuffer) && et.getEssentiaType(side) != null) {
         player.func_145747_a(new TextComponentTranslation("tc.resonator1", new Object[]{"" + et.getEssentiaAmount(side), et.getEssentiaType(side).getName()}));
      } else if (tile instanceof TileTubeBuffer && ((IAspectContainer)tile).getAspects().size() > 0) {
         for (Aspect aspect : ((IAspectContainer)tile).getAspects().getAspectsSortedByName()) {
            player.func_145747_a(
               new TextComponentTranslation("tc.resonator1", new Object[]{"" + ((IAspectContainer)tile).getAspects().getAmount(aspect), aspect.getName()})
            );
         }
      }

      String s = I18n.func_74838_a("tc.resonator3");
      if (et.getSuctionType(side) != null) {
         s = et.getSuctionType(side).getName();
      }

      player.func_145747_a(new TextComponentTranslation("tc.resonator2", new Object[]{"" + et.getSuctionAmount(side), s}));
      world.func_184148_a(
         null,
         pos.func_177958_n(),
         pos.func_177956_o(),
         pos.func_177952_p(),
         SoundEvents.field_187767_eL,
         SoundCategory.BLOCKS,
         0.5F,
         1.9F + world.field_73012_v.nextFloat() * 0.1F
      );
      if (tile != null && tile instanceof TileCondenser) {
         et = (TileCondenser)tile;
         player.func_145747_a(new TextComponentTranslation("tc.condenser1", new Object[]{"" + et.cost}));
         int sx = et.interval / 20;
         player.func_145747_a(new TextComponentTranslation("tc.condenser2", new Object[]{"" + et.interval, "" + sx}));
      }

      return EnumActionResult.SUCCESS;
   }
}
