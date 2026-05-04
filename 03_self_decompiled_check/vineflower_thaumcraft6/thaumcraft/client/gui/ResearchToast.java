package thaumcraft.client.gui;

import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.IToast.Visibility;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.research.ResearchEntry;

public class ResearchToast implements IToast {
   ResearchEntry entry;
   private long firstDrawTime;
   private boolean newDisplay;
   ResourceLocation tex = new ResourceLocation("thaumcraft", "textures/gui/hud.png");

   public ResearchToast(ResearchEntry entry) {
      this.entry = entry;
   }

   public Visibility func_193653_a(GuiToast toastGui, long delta) {
      if (this.newDisplay) {
         this.firstDrawTime = delta;
         this.newDisplay = false;
      }

      toastGui.func_192989_b().func_110434_K().func_110577_a(this.tex);
      GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
      toastGui.func_73729_b(0, 0, 0, 224, 160, 32);
      GuiResearchBrowser.drawResearchIcon(this.entry, 6, 8, 0.0F, false);
      toastGui.func_192989_b().field_71466_p.func_78276_b(I18n.func_74838_a("research.complete"), 30, 7, 10631665);
      String s = this.entry.getLocalizedName();
      float w = toastGui.func_192989_b().field_71466_p.func_78256_a(s);
      if (w > 124.0F) {
         w = 124.0F / w;
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(30.0F, 18.0F, 0.0F);
         GlStateManager.func_179152_a(w, w, w);
         toastGui.func_192989_b().field_71466_p.func_78276_b(s, 0, 0, 16755465);
         GlStateManager.func_179121_F();
      } else {
         toastGui.func_192989_b().field_71466_p.func_78276_b(s, 30, 18, 16755465);
      }

      return delta - this.firstDrawTime < 5000L ? Visibility.SHOW : Visibility.HIDE;
   }
}
