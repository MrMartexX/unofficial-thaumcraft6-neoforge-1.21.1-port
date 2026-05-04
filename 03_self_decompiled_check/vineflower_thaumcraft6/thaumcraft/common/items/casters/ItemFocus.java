package thaumcraft.common.items.casters;

import java.awt.Color;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.common.items.ItemTCBase;

public class ItemFocus extends ItemTCBase {
   private int maxComplexity;

   public ItemFocus(String name, int complexity) {
      super(name);
      this.field_77777_bU = 1;
      this.func_77656_e(0);
      this.maxComplexity = complexity;
   }

   public int getFocusColor(ItemStack focusstack) {
      if (focusstack != null && !focusstack.func_190926_b() && focusstack.func_77978_p() != null) {
         int color = 16777215;
         if (!focusstack.func_77978_p().func_74764_b("color")) {
            FocusPackage core = getPackage(focusstack);
            if (core != null) {
               FocusEffect[] fe = core.getFocusEffects();
               int r = 0;
               int g = 0;
               int b = 0;

               for (FocusEffect ef : fe) {
                  Color c = new Color(FocusEngine.getElementColor(ef.getKey()));
                  r += c.getRed();
                  g += c.getGreen();
                  b += c.getBlue();
               }

               if (fe.length > 0) {
                  r /= fe.length;
                  g /= fe.length;
                  b /= fe.length;
               }

               Color c = new Color(r, g, b);
               color = c.getRGB();
               focusstack.func_77983_a("color", new NBTTagInt(color));
            }
         } else {
            color = focusstack.func_77978_p().func_74762_e("color");
         }

         return color;
      } else {
         return 16777215;
      }
   }

   public String getSortingHelper(ItemStack focusstack) {
      if (focusstack != null && !focusstack.func_190926_b() && focusstack.func_77942_o()) {
         int sh = focusstack.func_77978_p().func_74762_e("srt");
         if (sh == 0) {
            sh = getPackage(focusstack).getSortingHelper();
            focusstack.func_77983_a("srt", new NBTTagInt(sh));
         }

         return focusstack.func_82833_r() + sh;
      } else {
         return null;
      }
   }

   public static void setPackage(ItemStack focusstack, FocusPackage core) {
      NBTTagCompound tag = core.serialize();
      focusstack.func_77983_a("package", tag);
   }

   public static FocusPackage getPackage(ItemStack focusstack) {
      if (focusstack != null && !focusstack.func_190926_b()) {
         NBTTagCompound tag = focusstack.func_179543_a("package");
         if (tag != null) {
            FocusPackage p = new FocusPackage();
            p.deserialize(tag);
            return p;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @SideOnly(Side.CLIENT)
   public void func_77624_a(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      this.addFocusInformation(stack, worldIn, tooltip, flagIn);
   }

   @SideOnly(Side.CLIENT)
   public void addFocusInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      FocusPackage p = getPackage(stack);
      if (p != null) {
         float al = this.getVisCost(stack);
         String amount = ItemStack.field_111284_a.format(al);
         tooltip.add(amount + " " + I18n.func_74838_a("item.Focus.cost1"));

         for (IFocusElement fe : p.nodes) {
            if (fe instanceof FocusNode && !(fe instanceof FocusMediumRoot)) {
               this.buildInfo(tooltip, (FocusNode)fe, 0);
            }
         }
      }
   }

   private void buildInfo(List list, FocusNode node, int depth) {
      if (node instanceof FocusNode && !(node instanceof FocusMediumRoot)) {
         String t0 = "";

         for (int a = 0; a < depth; a++) {
            t0 = t0 + "  ";
         }

         t0 = t0 + TextFormatting.DARK_PURPLE + I18n.func_74838_a(node.getUnlocalizedName());
         if (!node.getSettingList().isEmpty()) {
            t0 = t0 + TextFormatting.DARK_AQUA + " [";
            boolean q = false;

            for (String st : node.getSettingList()) {
               NodeSetting ns = node.getSetting(st);
               t0 = t0 + (q ? ", " : "") + ns.getLocalizedName() + " " + ns.getValueText();
               q = true;
            }

            t0 = t0 + "]";
         }

         list.add(t0);
         if (node instanceof FocusModSplit) {
            FocusModSplit split = (FocusModSplit)node;

            for (FocusPackage p : split.getSplitPackages()) {
               for (IFocusElement fe : p.nodes) {
                  if (fe instanceof FocusNode && !(fe instanceof FocusMediumRoot)) {
                     this.buildInfo(list, (FocusNode)fe, depth + 1);
                  }
               }
            }
         }
      }
   }

   public EnumRarity func_77613_e(ItemStack focusstack) {
      return EnumRarity.RARE;
   }

   public float getVisCost(ItemStack focusstack) {
      FocusPackage p = getPackage(focusstack);
      return p == null ? 0.0F : p.getComplexity() / 5.0F;
   }

   public int getActivationTime(ItemStack focusstack) {
      FocusPackage p = getPackage(focusstack);
      return p == null ? 0 : Math.max(5, p.getComplexity() / 5 * (p.getComplexity() / 4));
   }

   public int getMaxComplexity() {
      return this.maxComplexity;
   }
}
