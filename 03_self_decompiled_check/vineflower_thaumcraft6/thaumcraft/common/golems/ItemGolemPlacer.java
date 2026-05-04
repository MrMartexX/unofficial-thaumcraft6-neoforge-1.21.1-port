package thaumcraft.common.golems;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCBase;

public class ItemGolemPlacer extends ItemTCBase implements ISealDisplayer {
   public ItemGolemPlacer() {
      super("golem");
   }

   @Override
   public void func_150895_a(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (tab == ConfigItems.TABTC || tab == CreativeTabs.field_78027_g) {
         ItemStack is = new ItemStack(this, 1, 0);
         is.func_77983_a("props", new NBTTagLong(0L));
         items.add(is.func_77946_l());
         IGolemProperties props = new GolemProperties();
         props.setHead(GolemHead.getHeads()[1]);
         props.setArms(GolemArm.getArms()[1]);
         is.func_77983_a("props", new NBTTagLong(props.toLong()));
         items.add(is.func_77946_l());
         props = new GolemProperties();
         props.setMaterial(GolemMaterial.getMaterials()[1]);
         props.setHead(GolemHead.getHeads()[1]);
         props.setArms(GolemArm.getArms()[2]);
         is.func_77983_a("props", new NBTTagLong(props.toLong()));
         items.add(is.func_77946_l());
         props = new GolemProperties();
         props.setMaterial(GolemMaterial.getMaterials()[4]);
         props.setHead(GolemHead.getHeads()[1]);
         props.setArms(GolemArm.getArms()[3]);
         is.func_77983_a("props", new NBTTagLong(props.toLong()));
         items.add(is.func_77946_l());
      }
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (stack.func_77942_o() && stack.func_77978_p().func_74764_b("props")) {
         IGolemProperties props = GolemProperties.fromLong(stack.func_77978_p().func_74763_f("props"));
         if (props.hasTrait(EnumGolemTrait.SMART)) {
            if (props.getRank() >= 10) {
               tooltip.add("§6" + I18n.func_74838_a("golem.rank") + " " + props.getRank());
            } else {
               int rx = stack.func_77978_p().func_74762_e("xp");
               int xn = (props.getRank() + 1) * (props.getRank() + 1) * 1000;
               tooltip.add("§6" + I18n.func_74838_a("golem.rank") + " " + props.getRank() + " §2(" + rx + "/" + xn + ")");
            }
         }

         tooltip.add("§a" + props.getMaterial().getLocalizedName());

         for (EnumGolemTrait tag : props.getTraits()) {
            tooltip.add("§9-" + tag.getLocalizedName());
         }
      }

      super.func_77624_a(stack, worldIn, tooltip, flagIn);
   }

   public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
      IBlockState bs = world.func_180495_p(pos);
      if (!bs.func_185904_a().func_76220_a()) {
         return EnumActionResult.FAIL;
      }

      if (world.field_72995_K) {
         return EnumActionResult.PASS;
      }

      pos = pos.func_177972_a(side);
      bs = world.func_180495_p(pos);
      if (!player.func_175151_a(pos, side, player.func_184586_b(hand))) {
         return EnumActionResult.FAIL;
      }

      EntityThaumcraftGolem golem = new EntityThaumcraftGolem(world);
      golem.func_70080_a(pos.func_177958_n() + 0.5, pos.func_177956_o(), pos.func_177952_p() + 0.5, 0.0F, 0.0F);
      if (golem != null && world.func_72838_d(golem)) {
         golem.setOwned(true);
         golem.setValidSpawn();
         golem.setOwnerId(player.func_110124_au());
         if (player.func_184586_b(hand).func_77942_o() && player.func_184586_b(hand).func_77978_p().func_74764_b("props")) {
            golem.setProperties(GolemProperties.fromLong(player.func_184586_b(hand).func_77978_p().func_74763_f("props")));
         }

         if (player.func_184586_b(hand).func_77942_o() && player.func_184586_b(hand).func_77978_p().func_74764_b("xp")) {
            golem.rankXp = player.func_184586_b(hand).func_77978_p().func_74762_e("xp");
         }

         golem.func_180482_a(world.func_175649_E(pos), (IEntityLivingData)null);
         if (!player.field_71075_bZ.field_75098_d) {
            player.func_184586_b(hand).func_190918_g(1);
         }
      }

      return EnumActionResult.SUCCESS;
   }
}
