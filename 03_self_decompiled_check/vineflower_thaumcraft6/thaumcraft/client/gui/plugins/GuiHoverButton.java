package thaumcraft.client.gui.plugins;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

@SideOnly(Side.CLIENT)
public class GuiHoverButton extends GuiButton {
   String description;
   GuiScreen screen;
   int color;
   Object tex = null;

   public GuiHoverButton(GuiScreen screen, int buttonId, int x, int y, int width, int height, String buttonText, String description, Object tex) {
      super(buttonId, x, y, width, height, buttonText);
      this.description = description;
      this.tex = tex;
      this.screen = screen;
      this.color = 16777215;
   }

   public GuiHoverButton(GuiScreen screen, int buttonId, int x, int y, int width, int height, String buttonText, String description, Object tex, int color) {
      super(buttonId, x, y, width, height, buttonText);
      this.description = description;
      this.tex = tex;
      this.screen = screen;
      this.color = color;
   }

   public void func_191745_a(Minecraft mc, int xx, int yy, float pt) {
      if (this.field_146125_m) {
         FontRenderer fontrenderer = mc.field_71466_p;
         Color c = new Color(this.color);
         GlStateManager.func_179131_c(0.9F * (c.getRed() / 255.0F), 0.9F * (c.getGreen() / 255.0F), 0.9F * (c.getBlue() / 255.0F), 0.9F);
         this.field_146123_n = xx >= this.field_146128_h - this.field_146120_f / 2
            && yy >= this.field_146129_i - this.field_146121_g / 2
            && xx < this.field_146128_h - this.field_146120_f / 2 + this.field_146120_f
            && yy < this.field_146129_i - this.field_146121_g / 2 + this.field_146121_g;
         int k = this.func_146114_a(this.field_146123_n);
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         GlStateManager.func_179112_b(770, 771);
         if (k == 2) {
            GlStateManager.func_179131_c(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, 1.0F);
         }

         if (this.tex instanceof Aspect) {
            mc.func_110434_K().func_110577_a(((Aspect)this.tex).getImage());
            Color c2 = new Color(((Aspect)this.tex).getColor());
            if (k != 2) {
               GlStateManager.func_179131_c(c2.getRed() / 290.0F, c2.getGreen() / 290.0F, c2.getBlue() / 290.0F, 0.9F);
            } else {
               GlStateManager.func_179131_c(c2.getRed() / 255.0F, c2.getGreen() / 255.0F, c2.getBlue() / 255.0F, 1.0F);
            }

            func_146110_a(this.field_146128_h - this.field_146120_f / 2, this.field_146129_i - this.field_146121_g / 2, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
         }

         if (this.tex instanceof ResourceLocation) {
            mc.func_110434_K().func_110577_a((ResourceLocation)this.tex);
            func_146110_a(this.field_146128_h - this.field_146120_f / 2, this.field_146129_i - this.field_146121_g / 2, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
         }

         if (this.tex instanceof TextureAtlasSprite) {
            this.func_175175_a(
               this.field_146128_h - this.field_146120_f / 2, this.field_146129_i - this.field_146121_g / 2, (TextureAtlasSprite)this.tex, 16, 16
            );
         }

         if (this.tex instanceof ItemStack) {
            this.field_73735_i -= 90.0F;
            UtilsFX.renderItemStackShaded(
               mc,
               (ItemStack)this.tex,
               this.field_146128_h - this.field_146120_f / 2,
               this.field_146129_i - this.field_146121_g / 2 - (k == 2 ? 1 : 0),
               null,
               1.0F
            );
            this.field_73735_i += 90.0F;
         }

         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.func_146119_b(mc, xx, yy);
      }
   }

   public void func_146111_b(int xx, int yy) {
      FontRenderer fontrenderer = Minecraft.func_71410_x().field_71466_p;
      this.field_73735_i += 90.0F;
      List<String> text = new ArrayList<>();
      if (this.tex instanceof ItemStack) {
         text = ((ItemStack)this.tex)
            .func_82840_a(
               Minecraft.func_71410_x().field_71439_g, Minecraft.func_71410_x().field_71474_y.field_82882_x ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL
            );
         int qq = 0;

         for (String s : text) {
            if (s.endsWith(" " + TextFormatting.RESET)) {
               text = text.subList(0, qq);
               break;
            }

            qq++;
         }
      } else {
         text.add(this.field_146126_j);
      }

      int m = 8;
      if (this.description != null) {
         m = 0;
         text.add("§o§9" + this.description);
      }

      UtilsFX.drawCustomTooltip(this.screen, fontrenderer, text, xx + 4, yy + m, -99);
      this.field_73735_i -= 90.0F;
      RenderHelper.func_74518_a();
      GlStateManager.func_179140_f();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public boolean func_146116_c(Minecraft mc, int mouseX, int mouseY) {
      return false;
   }
}
