package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardDragonEgg extends TheorycraftCard {
   @Override
   public int getInspirationCost() {
      return 1;
   }

   @Override
   public boolean isAidOnly() {
      return true;
   }

   @Override
   public String getLocalizedName() {
      return new TextComponentTranslation("card.dragonegg.name", new Object[0]).func_150254_d();
   }

   @Override
   public String getLocalizedText() {
      return new TextComponentTranslation("card.dragonegg.text", new Object[0]).func_150254_d();
   }

   @Override
   public boolean activate(EntityPlayer player, ResearchTableData data) {
      String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);

      for (int a = 0; a < 10; a++) {
         String cat = s[player.func_70681_au().nextInt(s.length)];
         data.addTotal(cat, MathHelper.func_76136_a(player.func_70681_au(), 2, 5));
      }

      return true;
   }
}
