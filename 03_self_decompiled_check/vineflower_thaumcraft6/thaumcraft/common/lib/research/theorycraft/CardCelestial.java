package thaumcraft.common.lib.research.theorycraft;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardCelestial extends TheorycraftCard {
   int md1;
   int md2;
   String cat = "BASICS";

   @Override
   public NBTTagCompound serialize() {
      NBTTagCompound nbt = super.serialize();
      nbt.func_74768_a("md1", this.md1);
      nbt.func_74768_a("md2", this.md2);
      nbt.func_74778_a("cat", this.cat);
      return nbt;
   }

   @Override
   public void deserialize(NBTTagCompound nbt) {
      super.deserialize(nbt);
      this.md1 = nbt.func_74762_e("md1");
      this.md2 = nbt.func_74762_e("md2");
      this.cat = nbt.func_74779_i("cat");
   }

   @Override
   public String getResearchCategory() {
      return this.cat;
   }

   @Override
   public boolean initialize(EntityPlayer player, ResearchTableData data) {
      if (!data.categoryTotals.isEmpty() && ThaumcraftCapabilities.knowsResearch(player, "CELESTIALSCANNING")) {
         Random r = new Random(this.getSeed());
         this.md1 = MathHelper.func_76136_a(r, 0, 12);
         this.md2 = this.md1;

         while (this.md1 == this.md2) {
            this.md2 = MathHelper.func_76136_a(r, 0, 12);
         }

         int hVal = 0;
         String hKey = "";

         for (String category : data.categoryTotals.keySet()) {
            int q = data.getTotal(category);
            if (q > hVal) {
               hVal = q;
               hKey = category;
            }
         }

         this.cat = hKey;
         return this.cat != null;
      } else {
         return false;
      }
   }

   @Override
   public int getInspirationCost() {
      return 1;
   }

   @Override
   public String getLocalizedName() {
      return new TextComponentTranslation("card.celestial.name", new Object[0]).func_150254_d();
   }

   @Override
   public String getLocalizedText() {
      return new TextComponentTranslation(
            "card.celestial.text",
            new Object[]{
               TextFormatting.BOLD + new TextComponentTranslation("tc.research_category." + this.cat, new Object[0]).func_150254_d() + TextFormatting.RESET
            }
         )
         .func_150260_c();
   }

   @Override
   public ItemStack[] getRequiredItems() {
      return new ItemStack[]{new ItemStack(ItemsTC.celestialNotes, 1, this.md1), new ItemStack(ItemsTC.celestialNotes, 1, this.md2)};
   }

   @Override
   public boolean[] getRequiredItemsConsumed() {
      return new boolean[]{true, true};
   }

   @Override
   public boolean activate(EntityPlayer player, ResearchTableData data) {
      data.addTotal(this.getResearchCategory(), MathHelper.func_76136_a(player.func_70681_au(), 25, 50));
      boolean sun = this.md1 == 0 || this.md2 == 0;
      boolean moon = this.md1 > 4 || this.md2 > 4;
      boolean stars = this.md1 > 0 && this.md1 < 5 || this.md2 > 0 && this.md2 < 5;
      if (stars) {
         int amt = MathHelper.func_76136_a(player.func_70681_au(), 0, 5);
         data.addTotal("ELDRITCH", amt * 2);
         ThaumcraftApi.internalMethods.addWarpToPlayer(player, amt, IPlayerWarp.EnumWarpType.TEMPORARY);
      }

      if (sun) {
         data.penaltyStart++;
      }

      if (moon) {
         data.bonusDraws++;
      }

      return true;
   }
}
