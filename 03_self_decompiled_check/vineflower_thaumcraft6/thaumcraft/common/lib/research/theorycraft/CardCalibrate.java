package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardCalibrate extends TheorycraftCard {
   @Override
   public int getInspirationCost() {
      return 1;
   }

   @Override
   public String getResearchCategory() {
      return "ARTIFICE";
   }

   @Override
   public String getLocalizedName() {
      return new TextComponentTranslation("card.calibrate.name", new Object[0]).func_150254_d();
   }

   @Override
   public String getLocalizedText() {
      return new TextComponentTranslation("card.calibrate.text", new Object[0]).func_150254_d();
   }

   @Override
   public boolean activate(EntityPlayer player, ResearchTableData data) {
      data.addTotal(this.getResearchCategory(), 15);
      data.bonusDraws++;
      return true;
   }
}
