package thaumcraft.client.gui.plugins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiSliderTC extends GuiButton {
   private float sliderPosition = 1.0F;
   public boolean isMouseDown;
   private final String name;
   private final float min;
   private float max;
   private final boolean vertical;
   static ResourceLocation tex = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");

   public GuiSliderTC(int idIn, int x, int y, int w, int h, String name, float min, float max, float defaultValue, boolean vertical) {
      super(idIn, x, y, w, h, "");
      this.name = name;
      this.min = min;
      this.max = max;
      this.sliderPosition = (defaultValue - min) / (max - min);
      this.vertical = vertical;
   }

   public float getMax() {
      return this.max;
   }

   public float getMin() {
      return this.min;
   }

   public void setMax(float max) {
      this.max = max;
      this.sliderPosition = 0.0F;
   }

   public float getSliderValue() {
      return this.min + (this.max - this.min) * this.sliderPosition;
   }

   public void setSliderValue(float p_175218_1_, boolean p_175218_2_) {
      this.sliderPosition = (p_175218_1_ - this.min) / (this.max - this.min);
   }

   public float getSliderPosition() {
      return this.sliderPosition;
   }

   protected int func_146114_a(boolean mouseOver) {
      return 0;
   }

   protected void func_146119_b(Minecraft mc, int mouseX, int mouseY) {
      if (this.field_146125_m) {
         if (this.isMouseDown) {
            if (this.vertical) {
               this.sliderPosition = (float)(mouseY - (this.field_146129_i + 4)) / (this.field_146121_g - 8);
            } else {
               this.sliderPosition = (float)(mouseX - (this.field_146128_h + 4)) / (this.field_146120_f - 8);
            }

            if (this.sliderPosition < 0.0F) {
               this.sliderPosition = 0.0F;
            }

            if (this.sliderPosition > 1.0F) {
               this.sliderPosition = 1.0F;
            }
         }

         mc.func_110434_K().func_110577_a(tex);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         if (this.vertical) {
            this.func_73729_b(this.field_146128_h, this.field_146129_i + (int)(this.sliderPosition * (this.field_146121_g - 8)), 20, 20, 8, 8);
         } else {
            this.func_73729_b(this.field_146128_h + (int)(this.sliderPosition * (this.field_146120_f - 8)), this.field_146129_i, 20, 20, 8, 8);
         }
      }
   }

   public void setSliderPosition(float p_175219_1_) {
      this.sliderPosition = p_175219_1_;
   }

   public boolean func_146116_c(Minecraft mc, int mouseX, int mouseY) {
      if (super.func_146116_c(mc, mouseX, mouseY)) {
         if (this.vertical) {
            this.sliderPosition = (float)(mouseY - (this.field_146129_i + 4)) / (this.field_146121_g - 8);
         } else {
            this.sliderPosition = (float)(mouseX - (this.field_146128_h + 4)) / (this.field_146120_f - 8);
         }

         if (this.sliderPosition < 0.0F) {
            this.sliderPosition = 0.0F;
         }

         if (this.sliderPosition > 1.0F) {
            this.sliderPosition = 1.0F;
         }

         this.isMouseDown = true;
         return true;
      } else {
         return false;
      }
   }

   public void func_146118_a(int mouseX, int mouseY) {
      this.isMouseDown = false;
   }

   public void func_191745_a(Minecraft mc, int mouseX, int mouseY, float pt) {
      if (this.field_146125_m) {
         mc.func_110434_K().func_110577_a(tex);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_146123_n = mouseX >= this.field_146128_h
            && mouseY >= this.field_146129_i
            && mouseX < this.field_146128_h + this.field_146120_f
            && mouseY < this.field_146129_i + this.field_146121_g;
         int i = this.func_146114_a(this.field_146123_n);
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.func_187401_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         GlStateManager.func_179094_E();
         if (this.vertical) {
            GlStateManager.func_179109_b(this.field_146128_h + 2, this.field_146129_i, 0.0F);
            GlStateManager.func_179152_a(1.0F, this.field_146121_g / 32.0F, 1.0F);
            this.func_73729_b(0, 0, 240, 176, 4, 32);
         } else {
            GlStateManager.func_179109_b(this.field_146128_h, this.field_146129_i + 2, 0.0F);
            GlStateManager.func_179152_a(this.field_146120_f / 32.0F, 1.0F, 1.0F);
            this.func_73729_b(0, 0, 208, 176, 32, 4);
         }

         GlStateManager.func_179121_F();
         this.func_146119_b(mc, mouseX, mouseY);
      }
   }

   @SideOnly(Side.CLIENT)
   public interface FormatHelper {
      String getText(int var1, String var2, float var3);
   }
}
