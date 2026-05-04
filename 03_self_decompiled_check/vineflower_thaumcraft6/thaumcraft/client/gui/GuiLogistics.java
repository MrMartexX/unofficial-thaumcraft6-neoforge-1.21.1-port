package thaumcraft.client.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.gui.plugins.GuiImageButton;
import thaumcraft.client.gui.plugins.GuiPlusMinusButton;
import thaumcraft.client.gui.plugins.GuiScrollButton;
import thaumcraft.client.gui.plugins.GuiSliderTC;
import thaumcraft.common.container.ContainerLogistics;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketLogisticsRequestToServer;
import thaumcraft.common.lib.network.misc.PacketMiscStringToServer;

@SideOnly(Side.CLIENT)
public class GuiLogistics extends GuiContainer {
   int selectedSlot = -1;
   ContainerLogistics con = null;
   World world;
   EntityPlayer player;
   BlockPos target;
   EnumFacing side;
   ResourceLocation tex = new ResourceLocation("thaumcraft", "textures/gui/gui_logistics.png");
   long lu = 0L;
   int lastStackSize = 1;
   int stackSize = 1;
   boolean stacksizeUpdated = false;
   ItemStack selectedStack = null;
   int lastScrollPos = 0;
   GuiSliderTC scrollbar;
   GuiSliderTC countbar;
   GuiPlusMinusButton countbutton1;
   GuiPlusMinusButton countbutton2;
   GuiImageButton requestbutton;
   GuiTextField searchField;

   public GuiLogistics(InventoryPlayer par1InventoryPlayer, World world, BlockPos pos, EnumFacing side) {
      super(new ContainerLogistics(par1InventoryPlayer, world));
      this.world = world;
      this.player = par1InventoryPlayer.field_70458_d;
      this.field_146999_f = 215;
      this.field_147000_g = 215;
      this.con = (ContainerLogistics)this.field_147002_h;
      this.target = pos;
      this.side = side;
   }

   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
      this.func_146276_q_();
      super.func_73863_a(mouseX, mouseY, partialTicks);
      this.func_191948_b(mouseX, mouseY);
   }

   protected void func_146979_b(int mouseX, int mouseY) {
      long ct = System.currentTimeMillis();
      if (ct > this.lu) {
         this.lu = ct + 1000L;
         this.field_146297_k.field_71442_b.func_78756_a(this.field_147002_h.field_75152_c, 22);
      }

      this.field_146297_k.field_71446_o.func_110577_a(this.tex);
      GL11.glEnable(3042);
      if (this.scrollbar != null) {
         if (this.scrollbar.getMax() != this.con.end) {
            this.scrollbar.setMax(this.con.end);
         }

         int sv = Math.round(this.scrollbar.getSliderValue());
         if (sv != this.lastScrollPos) {
            this.lastScrollPos = sv;
            this.field_146297_k.field_71442_b.func_78756_a(this.field_147002_h.field_75152_c, 100 + this.lastScrollPos);
         } else if (this.con.updated && sv != this.con.start) {
            this.scrollbar.setSliderValue(this.con.start, false);
            this.con.updated = false;
         }
      }

      this.countbar.field_146125_m = this.selectedSlot >= 0;
      this.countbutton1.field_146125_m = this.selectedSlot >= 0;
      this.countbutton2.field_146125_m = this.selectedSlot >= 0;
      this.requestbutton.field_146125_m = this.selectedSlot >= 0;
      if (this.selectedSlot >= 0
         && this.selectedStack != null
         && !this.selectedStack.func_190926_b()
         && (
            !this.selectedStack.func_77969_a(this.field_147002_h.func_75139_a(this.selectedSlot).func_75211_c())
               || !ItemStack.func_77970_a(this.selectedStack, this.field_147002_h.func_75139_a(this.selectedSlot).func_75211_c())
         )) {
         this.selectedSlot = -1;

         for (Slot slot : this.field_147002_h.field_75151_b) {
            if (this.selectedStack.func_77969_a(slot.func_75211_c()) && ItemStack.func_77970_a(this.selectedStack, slot.func_75211_c())) {
               this.selectedSlot = slot.field_75222_d;
               break;
            }
         }
      }

      if (this.selectedSlot >= 0 && !this.field_147002_h.func_75139_a(this.selectedSlot).func_75216_d()) {
         this.selectedSlot = -1;
      }

      if (this.selectedSlot >= 0
         && this.field_147002_h.func_75139_a(this.selectedSlot) != null
         && this.field_147002_h.func_75139_a(this.selectedSlot).func_75216_d()) {
         ItemStack stack = this.field_147002_h.func_75139_a(this.selectedSlot).func_75211_c();
         if (this.countbar.getMax() != stack.func_190916_E()) {
            this.countbar.setMax(stack.func_190916_E());
         }

         int sv = Math.round(this.countbar.getSliderValue());
         if (sv != this.lastStackSize) {
            this.lastStackSize = sv;
            this.stackSize = this.lastStackSize;
         } else if (this.stacksizeUpdated && sv != this.stackSize) {
            this.countbar.setSliderValue(this.stackSize, false);
            this.stacksizeUpdated = false;
         }

         String s = "" + this.stackSize;
         this.field_146289_q.func_78276_b(s, 83 - this.field_146289_q.func_78256_a(s) / 2, 196, 3355443);
      }

      GL11.glDisable(3042);
   }

   protected boolean func_146983_a(int par1) {
      return false;
   }

   protected void func_146976_a(float par1, int par2, int par3) {
      this.field_146297_k.field_71446_o.func_110577_a(this.tex);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(3042);
      this.func_73729_b(this.field_147003_i, this.field_147009_r, 0, 0, this.field_146999_f, this.field_147000_g);
      if (this.selectedSlot >= 0) {
         this.func_73729_b(this.field_147003_i + 17 + this.selectedSlot % 9 * 19, this.field_147009_r + 17 + this.selectedSlot / 9 * 19, 222, 46, 20, 20);
      }

      GL11.glDisable(3042);
      this.searchField.func_146194_f();
      if (!this.searchField.func_146206_l() && this.searchField.func_146179_b().isEmpty()) {
         this.field_146289_q
            .func_78276_b(
               new TextComponentTranslation("tc.logistics.search", new Object[0]).func_150254_d(),
               this.field_147003_i + 146,
               this.field_147009_r + 197,
               2236962
            );
      }
   }

   public void func_146274_d() throws IOException {
      super.func_146274_d();
      int k = Mouse.getDWheel();
      if (k < 0) {
         this.field_146297_k.field_71442_b.func_78756_a(this.field_147002_h.field_75152_c, 0);
      } else if (k > 0) {
         this.field_146297_k.field_71442_b.func_78756_a(this.field_147002_h.field_75152_c, 1);
      }
   }

   protected void func_184098_a(Slot slotIn, int slotId, int mouseButton, ClickType type) {
      super.func_184098_a(slotIn, slotId, mouseButton, type);
      if (slotIn != null && slotId < 81 && slotIn.func_75216_d()) {
         Minecraft.func_71410_x().field_71439_g.func_184185_a(SoundsTC.clack, 0.66F, 1.0F);
         this.selectedSlot = slotId;
         this.selectedStack = slotIn.func_75211_c();
      }
   }

   protected void func_73864_a(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.func_73864_a(mouseX, mouseY, mouseButton);
      this.searchField.func_146192_a(mouseX, mouseY, mouseButton);
   }

   protected void func_146284_a(GuiButton button) throws IOException {
      if (button.field_146127_k == 1) {
         this.field_146297_k.field_71442_b.func_78756_a(this.field_147002_h.field_75152_c, 0);
      }

      if (button.field_146127_k == 0) {
         this.field_146297_k.field_71442_b.func_78756_a(this.field_147002_h.field_75152_c, 1);
      }

      if (this.selectedSlot >= 0
         && this.field_147002_h.func_75139_a(this.selectedSlot) != null
         && this.field_147002_h.func_75139_a(this.selectedSlot).func_75216_d()) {
         ItemStack stack = this.field_147002_h.func_75139_a(this.selectedSlot).func_75211_c();
         if (button.field_146127_k == 2) {
            this.stackSize--;
            if (this.stackSize < 1) {
               this.stackSize = 1;
            }

            this.stacksizeUpdated = true;
         }

         if (button.field_146127_k == 3) {
            this.stackSize++;
            if (this.stackSize > stack.func_190916_E()) {
               this.stackSize = stack.func_190916_E();
            }

            this.stacksizeUpdated = true;
         }

         if (button.field_146127_k == 7) {
            ItemStack s2 = stack.func_77946_l();
            s2.func_190920_e(1);
            PacketHandler.INSTANCE.sendToServer(new PacketLogisticsRequestToServer(this.target, this.side, s2, this.stackSize));
         }
      }
   }

   public void func_73866_w_() {
      super.func_73866_w_();
      this.field_146292_n.clear();
      this.field_146292_n.add(new GuiScrollButton(0, this.field_147003_i + 195, this.field_147009_r + 16, 10, 10, true, true));
      this.field_146292_n.add(new GuiScrollButton(1, this.field_147003_i + 195, this.field_147009_r + 180, 10, 10, false, true));
      this.countbutton1 = new GuiPlusMinusButton(2, this.field_147003_i + 13, this.field_147009_r + 195, 10, 10, true);
      this.countbutton2 = new GuiPlusMinusButton(3, this.field_147003_i + 57, this.field_147009_r + 195, 10, 10, false);
      this.field_146292_n.add(this.countbutton1);
      this.field_146292_n.add(this.countbutton2);
      this.scrollbar = new GuiSliderTC(
         5, this.field_147003_i + 196, this.field_147009_r + 28, 8, 149, "logistics.scrollbar", 0.0F, this.con.end, this.con.start, true
      );
      this.countbar = new GuiSliderTC(6, this.field_147003_i + 24, this.field_147009_r + 196, 32, 8, "logistics.countbar", 1.0F, 64.0F, 1.0F, false);
      this.field_146292_n.add(this.scrollbar);
      this.field_146292_n.add(this.countbar);
      this.requestbutton = new GuiImageButton(
         this,
         7,
         this.field_147003_i + 116,
         this.field_147009_r + 200,
         40,
         13,
         "tc.logistics.request",
         "logistics.request",
         new ResourceLocation("thaumcraft", "textures/gui/gui_base.png"),
         37,
         82,
         40,
         13
      );
      this.field_146292_n.add(this.requestbutton);
      this.searchField = new GuiTextField(8, this.field_146289_q, this.field_147003_i + 143, this.field_147009_r + 196, 55, this.field_146289_q.field_78288_b);
      this.searchField.func_146203_f(10);
      this.searchField.func_146185_a(true);
      this.searchField.func_146189_e(false);
      this.searchField.func_146193_g(16777215);
      this.searchField.func_146189_e(true);
      this.searchField.func_146205_d(true);
      this.searchField.func_146180_a("");
      Keyboard.enableRepeatEvents(true);
   }

   public void func_146281_b() {
      super.func_146281_b();
      Keyboard.enableRepeatEvents(false);
   }

   protected void func_73869_a(char typedChar, int keyCode) throws IOException {
      if (this.searchField.func_146201_a(typedChar, keyCode)) {
         PacketHandler.INSTANCE.sendToServer(new PacketMiscStringToServer(0, this.searchField.func_146179_b()));
      } else {
         super.func_73869_a(typedChar, keyCode);
      }
   }
}
