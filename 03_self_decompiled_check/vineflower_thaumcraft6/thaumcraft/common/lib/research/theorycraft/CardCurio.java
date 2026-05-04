package thaumcraft.common.lib.research.theorycraft;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.items.curios.ItemCurio;

public class CardCurio extends TheorycraftCard {
   ItemStack curio = ItemStack.field_190927_a;

   @Override
   public NBTTagCompound serialize() {
      NBTTagCompound nbt = super.serialize();
      nbt.func_74782_a("stack", this.curio.serializeNBT());
      return nbt;
   }

   @Override
   public void deserialize(NBTTagCompound nbt) {
      super.deserialize(nbt);
      this.curio = new ItemStack(nbt.func_74775_l("stack"));
   }

   @Override
   public int getInspirationCost() {
      return 1;
   }

   @Override
   public String getLocalizedName() {
      return new TextComponentTranslation("card.curio.name", new Object[0]).func_150254_d();
   }

   @Override
   public String getLocalizedText() {
      return new TextComponentTranslation("card.curio.text", new Object[0]).func_150254_d();
   }

   @Override
   public ItemStack[] getRequiredItems() {
      return new ItemStack[]{this.curio};
   }

   @Override
   public boolean[] getRequiredItemsConsumed() {
      return new boolean[]{true};
   }

   @Override
   public boolean initialize(EntityPlayer player, ResearchTableData data) {
      Random r = new Random(this.getSeed());
      ArrayList<ItemStack> curios = new ArrayList<>();

      for (ItemStack stack : player.field_71071_by.field_70462_a) {
         if (stack != null && !stack.func_190926_b() && stack.func_77973_b() instanceof ItemCurio) {
            ItemStack c = stack.func_77946_l();
            c.func_190920_e(1);
            curios.add(c);
         }
      }

      if (!curios.isEmpty()) {
         this.curio = curios.get(r.nextInt(curios.size()));
      }

      return !this.curio.func_190926_b();
   }

   @Override
   public boolean activate(EntityPlayer player, ResearchTableData data) {
      data.addTotal("BASICS", 5);
      String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);
      data.addTotal(s[player.func_70681_au().nextInt(s.length)], 5);
      String type = ((ItemCurio)this.getRequiredItems()[0].func_77973_b()).getVariantNames()[this.getRequiredItems()[0].func_77952_i()];
      switch (type) {
         case "arcane":
            data.addTotal("AUROMANCY", MathHelper.func_76136_a(player.func_70681_au(), 25, 35));
            break;
         case "preserved":
            data.addTotal("ALCHEMY", MathHelper.func_76136_a(player.func_70681_au(), 25, 35));
            break;
         case "ancient":
            data.addTotal("GOLEMANCY", MathHelper.func_76136_a(player.func_70681_au(), 25, 35));
            break;
         case "eldritch":
            data.addTotal("ELDRITCH", MathHelper.func_76136_a(player.func_70681_au(), 25, 35));
            break;
         case "knowledge":
            data.addTotal("INFUSION", MathHelper.func_76136_a(player.func_70681_au(), 25, 35));
            break;
         case "twisted":
            data.addTotal("ARTIFICE", MathHelper.func_76136_a(player.func_70681_au(), 25, 35));
            break;
         case "rites":
            data.addTotal("ELDRITCH", MathHelper.func_76136_a(player.func_70681_au(), 15, 20));
            data.addTotal("AUROMANCY", MathHelper.func_76136_a(player.func_70681_au(), 10, 15));
            break;
         default:
            data.addTotal("BASICS", MathHelper.func_76136_a(player.func_70681_au(), 25, 35));
      }

      if (player.func_70681_au().nextBoolean()) {
         data.bonusDraws++;
      }

      if (player.func_70681_au().nextBoolean()) {
         data.bonusDraws++;
      }

      return true;
   }
}
