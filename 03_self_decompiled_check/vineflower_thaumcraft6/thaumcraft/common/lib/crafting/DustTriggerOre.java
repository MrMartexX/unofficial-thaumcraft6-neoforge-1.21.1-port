package thaumcraft.common.lib.crafting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.lib.events.ServerEvents;

public class DustTriggerOre implements IDustTrigger {
   String target;
   ItemStack result;
   String research;

   public DustTriggerOre(String research, String target, ItemStack result) {
      this.target = target;
      this.result = result;
      this.research = research;
   }

   @Override
   public IDustTrigger.Placement getValidFace(World world, EntityPlayer player, BlockPos pos, EnumFacing face) {
      IBlockState bs = world.func_180495_p(pos);
      boolean b = false;

      try {
         int[] ods = OreDictionary.getOreIDs(new ItemStack(bs.func_177230_c(), 1, bs.func_177230_c().func_180651_a(bs)));

         for (int q : ods) {
            if (q == OreDictionary.getOreID(this.target)) {
               b = true;
               break;
            }
         }
      } catch (Exception var12) {
      }

      return !b || this.research != null && !ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(this.research)
         ? null
         : new IDustTrigger.Placement(0, 0, 0, null);
   }

   @Override
   public void execute(final World world, final EntityPlayer player, final BlockPos pos, IDustTrigger.Placement placement, EnumFacing side) {
      FMLCommonHandler.instance().firePlayerCraftingEvent(player, this.result, new InventoryFake(1));
      final IBlockState state = world.func_180495_p(pos);
      ServerEvents.addRunnableServer(
         world,
         new Runnable() {
            @Override
            public void run() {
               ServerEvents.addSwapper(
                  world, pos, state, DustTriggerOre.this.result, false, 0, player, true, true, -9999, false, false, 0, ServerEvents.DEFAULT_PREDICATE, 0.0F
               );
            }
         },
         50
      );
   }
}
