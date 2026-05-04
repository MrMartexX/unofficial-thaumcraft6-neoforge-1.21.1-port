package thaumcraft.api.research.theorycraft;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class CardStudy extends TheorycraftCard {
   String cat = "BASICS";

   @Override
   public NBTTagCompound serialize() {
      NBTTagCompound nbt = super.serialize();
      nbt.func_74778_a("cat", this.cat);
      return nbt;
   }

   @Override
   public void deserialize(NBTTagCompound nbt) {
      super.deserialize(nbt);
      this.cat = nbt.func_74779_i("cat");
   }

   @Override
   public String getResearchCategory() {
      return this.cat;
   }

   @Override
   public boolean initialize(EntityPlayer player, ResearchTableData data) {
      Random r = new Random(this.getSeed());
      ArrayList<String> list = data.getAvailableCategories(player);
      this.cat = list.get(r.nextInt(list.size()));
      return this.cat != null;
   }

   @Override
   public boolean isAidOnly() {
      return true;
   }

   @Override
   public int getInspirationCost() {
      return 1;
   }

   @Override
   public String getLocalizedName() {
      return new TextComponentTranslation(
            "card.study.name",
            new Object[]{
               TextFormatting.DARK_BLUE
                  + ""
                  + TextFormatting.BOLD
                  + new TextComponentTranslation("tc.research_category." + this.cat, new Object[0]).func_150254_d()
                  + TextFormatting.RESET
            }
         )
         .func_150260_c();
   }

   @Override
   public String getLocalizedText() {
      return new TextComponentTranslation(
            "card.study.text",
            new Object[]{
               TextFormatting.BOLD + new TextComponentTranslation("tc.research_category." + this.cat, new Object[0]).func_150254_d() + TextFormatting.RESET
            }
         )
         .func_150260_c();
   }

   @Override
   public boolean activate(EntityPlayer player, ResearchTableData data) {
      data.addTotal(this.cat, MathHelper.func_76136_a(player.func_70681_au(), 15, 25));
      return true;
   }
}
