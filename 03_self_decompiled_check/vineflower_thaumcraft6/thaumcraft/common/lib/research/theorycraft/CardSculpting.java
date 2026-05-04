package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardSculpting extends TheorycraftCard {
   @Override
   public int getInspirationCost() {
      return 1;
   }

   @Override
   public String getResearchCategory() {
      return "GOLEMANCY";
   }

   @Override
   public String getLocalizedName() {
      return new TextComponentTranslation("card.sculpting.name", new Object[0]).func_150254_d();
   }

   @Override
   public String getLocalizedText() {
      return new TextComponentTranslation("card.sculpting.text", new Object[0]).func_150254_d();
   }

   @Override
   public ItemStack[] getRequiredItems() {
      return new ItemStack[]{new ItemStack(Items.field_151119_aD)};
   }

   @Override
   public boolean[] getRequiredItemsConsumed() {
      return new boolean[]{true};
   }

   @Override
   public boolean activate(EntityPlayer player, ResearchTableData data) {
      data.addTotal(this.getResearchCategory(), 20);
      data.bonusDraws++;
      return true;
   }
}
