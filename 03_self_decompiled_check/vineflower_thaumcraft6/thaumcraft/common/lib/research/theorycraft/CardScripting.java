package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.tiles.crafting.TileResearchTable;

public class CardScripting extends TheorycraftCard {
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
      return new TextComponentTranslation("card.scripting.name", new Object[0]).func_150254_d();
   }

   @Override
   public String getLocalizedText() {
      return new TextComponentTranslation("card.scripting.text", new Object[0]).func_150254_d();
   }

   @Override
   public boolean activate(EntityPlayer player, ResearchTableData data) {
      if (data.table != null
         && ((TileResearchTable)data.table).func_70301_a(0) != null
         && ((TileResearchTable)data.table).func_70301_a(0).func_77952_i() < ((TileResearchTable)data.table).func_70301_a(0).func_77958_k()
         && ((TileResearchTable)data.table).func_70301_a(1) != null) {
         ((TileResearchTable)data.table).consumeInkFromTable();
         ((TileResearchTable)data.table).consumepaperFromTable();
         data.addTotal(this.getResearchCategory(), 25);
         return true;
      } else {
         return false;
      }
   }
}
