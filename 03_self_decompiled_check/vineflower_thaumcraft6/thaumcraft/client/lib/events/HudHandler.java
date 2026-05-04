package thaumcraft.client.lib.events;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.items.tools.ItemSanityChecker;
import thaumcraft.common.items.tools.ItemThaumometer;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.world.aura.AuraChunk;

public class HudHandler {
   final ResourceLocation HUD = new ResourceLocation("thaumcraft", "textures/gui/hud.png");
   public LinkedBlockingQueue<HudHandler.KnowledgeGainTracker> knowledgeGainTrackers = new LinkedBlockingQueue<>();
   public static final ResourceLocation BOOK = new ResourceLocation("thaumcraft", "textures/items/thaumonomicon.png");
   public static final ResourceLocation[] KNOW_TYPE = new ResourceLocation[]{
      new ResourceLocation("thaumcraft", "textures/research/knowledge_theory.png"),
      new ResourceLocation("thaumcraft", "textures/research/knowledge_observation.png")
   };
   float kgFade = 0.0F;
   public static AuraChunk currentAura = new AuraChunk(null, (short)0, 0.0F, 0.0F);
   private final float VISCON = 525.0F;
   long nextsync = 0L;
   DecimalFormat secondsFormatter = new DecimalFormat("#######.#");
   ItemStack lastItem = null;
   int lastCount = 0;
   final ResourceLocation TAGBACK = new ResourceLocation("thaumcraft", "textures/aspects/_back.png");

   @SideOnly(Side.CLIENT)
   void renderHuds(Minecraft mc, float renderTickTime, EntityPlayer player, long time) {
      GL11.glPushMatrix();
      ScaledResolution sr = new ScaledResolution(Minecraft.func_71410_x());
      GL11.glClear(256);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0, sr.func_78327_c(), sr.func_78324_d(), 0.0, 1000.0, 3000.0);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
      int ww = sr.func_78326_a();
      int hh = sr.func_78328_b();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.renderHudsInGUI(mc, renderTickTime, player, time, ww, hh);
      if (mc.field_71415_G && Minecraft.func_71382_s()) {
         mc.field_71446_o.func_110577_a(this.HUD);
         ItemStack handStack = player.func_184614_ca();
         boolean rC = false;
         boolean rT = false;
         boolean rS = false;
         int start = 0;

         for (int a = 0; a < 2; a++) {
            if (handStack != null && !handStack.func_190926_b()) {
               if (!rC && handStack.func_77973_b() instanceof ICaster) {
                  this.renderCastingWandHud(mc, renderTickTime, player, time, handStack, start);
                  rC = true;
                  if (!ModConfig.CONFIG_GRAPHICS.dialBottom) {
                     start += 33;
                  }
               } else if (!rT && handStack.func_77973_b() instanceof ItemThaumometer) {
                  this.renderThaumometerHud(mc, renderTickTime, player, time, ww, hh, start);
                  rT = true;
                  start += 80;
               } else if (!rS && handStack.func_77973_b() instanceof ItemSanityChecker) {
                  this.renderSanityHud(mc, renderTickTime, player, time, start);
                  rS = true;
                  start += 75;
               }
            }

            handStack = player.func_184592_cb();
         }
      }

      GL11.glDisable(3042);
      GL11.glPopMatrix();
   }

   @SideOnly(Side.CLIENT)
   void renderHudsInGUI(Minecraft mc, float renderTickTime, EntityPlayer player, long time, int ww, int hh) {
      if (this.kgFade > 0.0F) {
         this.renderKnowledgeGains(mc, renderTickTime, player, time, ww, hh);
      }
   }

   @SideOnly(Side.CLIENT)
   void renderKnowledgeGains(Minecraft mc, float renderTickTime, EntityPlayer player, long time, int ww, int hh) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, this.kgFade / 40.0F);
      mc.field_71446_o.func_110577_a(BOOK);
      UtilsFX.drawTexturedQuadFull(ww - 17, hh - 17, -90.0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      LinkedBlockingQueue<HudHandler.KnowledgeGainTracker> temp = new LinkedBlockingQueue<>();

      for (int a = 0; !this.knowledgeGainTrackers.isEmpty(); a++) {
         HudHandler.KnowledgeGainTracker current = this.knowledgeGainTrackers.poll();
         if (current != null) {
            mc.field_71446_o.func_110577_a(KNOW_TYPE[current.type.ordinal()]);
            Random rand = new Random(current.seed);
            GL11.glPushMatrix();
            float s = 16.0F;
            float x = ww / 4 + rand.nextInt(32);
            float y = hh / 3 + rand.nextInt(32);
            float wot = 0.0F;
            if (current.progress < current.max * 0.66F) {
               float q = (current.progress - renderTickTime) / (current.max * 0.66F);
               s *= q;
               float m = (float)Math.sin(q * Math.PI - (Math.PI / 2)) * 0.5F + 0.5F;
               y *= m;
               float d = (float)Math.sin(m * Math.PI * 0.5);
               x *= d;
            } else {
               wot = current.max - current.progress + renderTickTime;
               float wot2 = wot / (current.max * 0.33F);
               float m = (float)Math.sin(wot2 * Math.PI * 2.0 - (Math.PI / 2)) * 0.5F + 1.5F;
               if (wot2 < 0.5) {
                  s *= wot2 * 2.0F;
               }

               s *= m;
            }

            float xx = ww - 12 + rand.nextInt(8) - x;
            float yy = hh - 12 + rand.nextInt(8) - y;
            if (current.sparks && player.func_70681_au().nextInt((int)(1.0F + (float)current.progress / current.max * 10.0F)) == 0) {
               float r = MathHelper.func_76136_a(player.field_70170_p.field_73012_v, 255, 255) / 255.0F;
               float g = MathHelper.func_76136_a(player.field_70170_p.field_73012_v, 189, 255) / 255.0F;
               float b = MathHelper.func_76136_a(player.field_70170_p.field_73012_v, 64, 255) / 255.0F;
               FXDispatcher.INSTANCE
                  .drawSimpleSparkleGui(
                     player.field_70170_p.field_73012_v,
                     xx + player.field_70170_p.field_73012_v.nextGaussian() * 5.0,
                     yy + player.field_70170_p.field_73012_v.nextGaussian() * 5.0,
                     player.field_70170_p.field_73012_v.nextGaussian(),
                     player.field_70170_p.field_73012_v.nextGaussian(),
                     24.0F,
                     r,
                     g,
                     b,
                     player.field_70170_p.field_73012_v.nextInt(5),
                     0.9F,
                     -1.0F
                  );
            }

            GL11.glTranslatef(xx, yy, -80 + a);
            GL11.glRotatef(84 + rand.nextInt(12), 0.0F, 0.0F, -1.0F);
            UtilsFX.renderQuadCentered(1, 1, 0, s, 1.0F, 1.0F, 1.0F, 200, 771, 1.0F);
            if (current.category != null) {
               mc.field_71446_o.func_110577_a(current.category.icon);
               GL11.glTranslatef(0.0F, 0.0F, 1.0F);
               UtilsFX.renderQuadCentered(1, 1, 0, s * 0.75F, 1.0F, 1.0F, 1.0F, 200, 771, 1.0F);
            }

            if (current.progress > current.max * 0.9F) {
               float wot3 = wot / (current.max * 0.1F);
               float m2 = (float)Math.sin(wot3 * Math.PI * 2.0 - (Math.PI / 2)) * 0.25F + 0.25F;
               float size = 64.0F * m2;
               GL11.glRotatef(rand.nextInt(360), 0.0F, 0.0F, -1.0F);
               mc.field_71446_o.func_110577_a(ParticleEngine.particleTexture);
               float r = MathHelper.func_76136_a(rand, 255, 255) / 255.0F;
               float g = MathHelper.func_76136_a(rand, 189, 255) / 255.0F;
               float b = MathHelper.func_76136_a(rand, 64, 255) / 255.0F;
               UtilsFX.renderQuadCentered(64, 64, 320 + rand.nextInt(16), size, r, g, b, 200, 1, 1.0F);
            }

            if (current.progress < current.max * 0.1F) {
               float wot3 = 1.0F - (current.progress - renderTickTime) / (current.max * 0.1F);
               float m2 = (float)Math.sin(wot3 * Math.PI * 2.0 - (Math.PI / 2)) * 0.25F + 0.25F;
               float size = 32.0F * m2;
               GL11.glRotatef(rand.nextInt(360), 0.0F, 0.0F, -1.0F);
               mc.field_71446_o.func_110577_a(ParticleEngine.particleTexture);
               float r = MathHelper.func_76136_a(rand, 255, 255) / 255.0F;
               float g = MathHelper.func_76136_a(rand, 189, 255) / 255.0F;
               float b = MathHelper.func_76136_a(rand, 64, 255) / 255.0F;
               UtilsFX.renderQuadCentered(64, 64, 320 + rand.nextInt(16), size, r, g, b, 200, 1, 1.0F);
            }

            temp.offer(current);
            GL11.glPopMatrix();
         }
      }

      while (!temp.isEmpty()) {
         this.knowledgeGainTrackers.offer(temp.poll());
      }
   }

   @SideOnly(Side.CLIENT)
   void renderThaumometerHud(Minecraft mc, float partialTicks, EntityPlayer player, long time, int ww, int hh, int shifty) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      float base = MathHelper.func_76131_a(currentAura.getBase() / 525.0F, 0.0F, 1.0F);
      float vis = MathHelper.func_76131_a(currentAura.getVis() / 525.0F, 0.0F, 1.0F);
      float flux = MathHelper.func_76131_a(currentAura.getFlux() / 525.0F, 0.0F, 1.0F);
      float count = Minecraft.func_71410_x().func_175606_aa().field_70173_aa + partialTicks;
      float count2 = Minecraft.func_71410_x().func_175606_aa().field_70173_aa / 3.0F + partialTicks;
      if (flux + vis > 1.0F) {
         float m = 1.0F / (flux + vis);
         base *= m;
         vis *= m;
         flux *= m;
      }

      float start = 10.0F + (1.0F - vis) * 64.0F;
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glTranslated(2.0, shifty, 0.0);
      if (vis > 0.0F) {
         GL11.glPushMatrix();
         GL11.glColor4f(0.7F, 0.4F, 0.9F, 1.0F);
         GL11.glTranslated(5.0, start, 0.0);
         GL11.glScaled(1.0, vis, 1.0);
         UtilsFX.drawTexturedQuad(0.0F, 0.0F, 88.0F, 56.0F, 8.0F, 64.0F, -90.0);
         GL11.glPopMatrix();
         GL11.glPushMatrix();
         GL11.glBlendFunc(770, 1);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
         GL11.glTranslated(5.0, start, 0.0);
         UtilsFX.drawTexturedQuad(0.0F, 0.0F, 96.0F, 56.0F + count % 64.0F, 8.0F, vis * 64.0F, -90.0);
         GL11.glBlendFunc(770, 771);
         GL11.glPopMatrix();
         if (player.func_70093_af()) {
            GL11.glPushMatrix();
            GL11.glTranslated(16.0, start, 0.0);
            GL11.glScaled(0.5, 0.5, 0.5);
            String msg = this.secondsFormatter.format(currentAura.getVis());
            mc.field_71456_v.func_73731_b(mc.field_71466_p, msg, 0, 0, 15641343);
            GL11.glPopMatrix();
            mc.field_71446_o.func_110577_a(this.HUD);
         }
      }

      if (flux > 0.0F) {
         start = 10.0F + (1.0F - flux - vis) * 64.0F;
         GL11.glPushMatrix();
         GL11.glColor4f(0.25F, 0.1F, 0.3F, 1.0F);
         GL11.glTranslated(5.0, start, 0.0);
         GL11.glScaled(1.0, flux, 1.0);
         UtilsFX.drawTexturedQuad(0.0F, 0.0F, 88.0F, 56.0F, 8.0F, 64.0F, -90.0);
         GL11.glPopMatrix();
         GL11.glPushMatrix();
         GL11.glBlendFunc(770, 1);
         GL11.glColor4f(0.7F, 0.4F, 1.0F, 0.5F);
         GL11.glTranslated(5.0, start, 0.0);
         UtilsFX.drawTexturedQuad(0.0F, 0.0F, 104.0F, 120.0F - count2 % 64.0F, 8.0F, flux * 64.0F, -90.0);
         GL11.glBlendFunc(770, 771);
         GL11.glPopMatrix();
         if (player.func_70093_af()) {
            GL11.glPushMatrix();
            GL11.glTranslated(16.0, start - 4.0F, 0.0);
            GL11.glScaled(0.5, 0.5, 0.5);
            String msg = this.secondsFormatter.format(currentAura.getFlux());
            mc.field_71456_v.func_73731_b(mc.field_71466_p, msg, 0, 0, 11145659);
            GL11.glPopMatrix();
            mc.field_71446_o.func_110577_a(this.HUD);
         }
      }

      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      UtilsFX.drawTexturedQuad(1.0F, 1.0F, 72.0F, 48.0F, 16.0F, 80.0F, -90.0);
      GL11.glPopMatrix();
      start = 8.0F + (1.0F - base) * 64.0F;
      GL11.glPushMatrix();
      UtilsFX.drawTexturedQuad(2.0F, start, 117.0F, 61.0F, 14.0F, 5.0F, -90.0);
      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }

   @SideOnly(Side.CLIENT)
   void renderSanityHud(Minecraft mc, Float partialTicks, EntityPlayer player, long time, int shifty) {
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glTranslated(0.0, shifty, 0.0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      UtilsFX.drawTexturedQuad(1.0F, 1.0F, 152.0F, 0.0F, 20.0F, 76.0F, -90.0);
      int p = ThaumcraftCapabilities.getWarp(player).get(IPlayerWarp.EnumWarpType.PERMANENT);
      int s = ThaumcraftCapabilities.getWarp(player).get(IPlayerWarp.EnumWarpType.NORMAL);
      int t = ThaumcraftCapabilities.getWarp(player).get(IPlayerWarp.EnumWarpType.TEMPORARY);
      float tw = p + s + t;
      float mod = 1.0F;
      if (tw > 100.0F) {
         mod = 100.0F / tw;
         tw = 100.0F;
      }

      int gap = (int)((100.0F - tw) / 100.0F * 48.0F);
      int wt = (int)(t / 100.0F * 48.0F * mod);
      int ws = (int)(s / 100.0F * 48.0F * mod);
      if (t > 0) {
         GL11.glPushMatrix();
         GL11.glColor4f(1.0F, 0.5F, 1.0F, 1.0F);
         UtilsFX.drawTexturedQuad(7.0F, 21 + gap, 200.0F, gap, 8.0F, wt + gap, -90.0);
         GL11.glPopMatrix();
      }

      if (s > 0) {
         GL11.glPushMatrix();
         GL11.glColor4f(0.75F, 0.0F, 0.75F, 1.0F);
         UtilsFX.drawTexturedQuad(7.0F, 21 + wt + gap, 200.0F, wt + gap, 8.0F, wt + ws + gap, -90.0);
         GL11.glPopMatrix();
      }

      if (p > 0) {
         GL11.glPushMatrix();
         GL11.glColor4f(0.5F, 0.0F, 0.5F, 1.0F);
         UtilsFX.drawTexturedQuad(7.0F, 21 + wt + ws + gap, 200.0F, wt + ws + gap, 8.0F, 48.0F, -90.0);
         GL11.glPopMatrix();
      }

      GL11.glPushMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      UtilsFX.drawTexturedQuad(1.0F, 1.0F, 176.0F, 0.0F, 20.0F, 76.0F, -90.0);
      GL11.glPopMatrix();
      if (tw >= 100.0F) {
         GL11.glPushMatrix();
         GL11.glScaled(0.75, 0.75, 1.0);
         GL11.glTranslated(mc.field_71439_g.func_70681_au().nextInt(2), mc.field_71439_g.func_70681_au().nextInt(2), 0.0);
         UtilsFX.drawTexturedQuad(3.0F, 3.0F, 216.0F, 0.0F, 20.0F, 16.0F, -90.0);
         GL11.glPopMatrix();
      }

      GL11.glPopMatrix();
   }

   @SideOnly(Side.CLIENT)
   void renderChargeMeters(Minecraft mc, float renderTickTime, EntityPlayer player, long time, int ww, int hh) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int start = 0;
      int total = 0;
   }

   @SideOnly(Side.CLIENT)
   void renderCastingWandHud(Minecraft mc, float partialTicks, EntityPlayer player, long time, ItemStack wandstack, int shifty) {
      ICaster wand = (ICaster)wandstack.func_77973_b();
      short short1 = 240;
      short short2 = 240;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, short1 / 1.0F, short2 / 1.0F);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, shifty, 0.0F);
      ScaledResolution sr = new ScaledResolution(Minecraft.func_71410_x());
      GL11.glClear(256);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0, sr.func_78327_c(), sr.func_78324_d(), 0.0, 1000.0, 3000.0);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      int l = sr.func_78328_b();
      int dailLocation = ModConfig.CONFIG_GRAPHICS.dialBottom ? l - 32 : 0;
      GL11.glTranslatef(0.0F, dailLocation, -2000.0F);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      mc.field_71446_o.func_110577_a(this.HUD);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glScaled(0.5, 0.5, 0.5);
      UtilsFX.drawTexturedQuad(0.0F, 0.0F, 0.0F, 0.0F, 64.0F, 64.0F, -90.0);
      GL11.glPopMatrix();
      GL11.glTranslatef(16.0F, 16.0F, 0.0F);
      int max = currentAura.getBase();
      int amt = (int)currentAura.getVis();
      ItemFocus focus = (ItemFocus)wand.getFocus(wandstack);
      ItemStack focusStack = wand.getFocusStack(wandstack);
      GL11.glPushMatrix();
      GL11.glTranslatef(16.0F, -10.0F, 0.0F);
      GL11.glScaled(0.5, 0.5, 0.5);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int loc = (int)(30.0F * amt / max);
      GL11.glPushMatrix();
      Color ac = new Color(Aspect.ENERGY.getColor());
      GL11.glColor4f(ac.getRed() / 255.0F, ac.getGreen() / 255.0F, ac.getBlue() / 255.0F, 0.8F);
      UtilsFX.drawTexturedQuad(-4.0F, 35 - loc, 104.0F, 0.0F, 8.0F, loc, -90.0);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
      GL11.glPushMatrix();
      UtilsFX.drawTexturedQuad(-8.0F, -3.0F, 72.0F, 0.0F, 16.0F, 42.0F, -90.0);
      GL11.glPopMatrix();
      int sh = 0;
      if (player.func_70093_af()) {
         GL11.glPushMatrix();
         GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
         String msg = this.secondsFormatter.format(amt);
         mc.field_71456_v.func_73731_b(mc.field_71466_p, msg, -32, -4, 16777215);
         GL11.glPopMatrix();
         if (focus != null && focus.getVisCost(focusStack) > 0.0F) {
            float mod = wand.getConsumptionModifier(wandstack, player, false);
            GL11.glPushMatrix();
            msg = this.secondsFormatter.format(focus.getVisCost(focusStack) * mod);
            mc.field_71456_v.func_73731_b(mc.field_71466_p, msg, -32 - mc.field_71456_v.func_175179_f().func_78256_a(msg) / 2, 32, 16777215);
            GL11.glPopMatrix();
         }

         mc.field_71446_o.func_110577_a(this.HUD);
      }

      GL11.glPopMatrix();
      if (focus != null) {
         ItemStack pickedStack = wand.getPickedBlock(player.field_71071_by.func_70448_g());
         if (pickedStack != null && !pickedStack.func_190926_b()) {
            this.renderWandTradeHud(partialTicks, player, time, pickedStack);
         } else {
            GL11.glPushMatrix();
            GL11.glTranslatef(-24.0F, -24.0F, 90.0F);
            RenderHelper.func_74520_c();
            GL11.glDisable(2896);
            GL11.glEnable(32826);
            GL11.glEnable(2903);
            GL11.glEnable(2896);

            try {
               mc.func_175599_af().func_180450_b(wand.getFocusStack(wandstack), 16, 16);
            } catch (Exception var23) {
            }

            GL11.glDisable(2896);
            GL11.glPopMatrix();
         }
      }

      GL11.glDisable(3042);
      GL11.glPopMatrix();
   }

   @SideOnly(Side.CLIENT)
   public void renderWandTradeHud(float partialTicks, EntityPlayer player, long time, ItemStack picked) {
      if (picked != null) {
         Minecraft mc = Minecraft.func_71410_x();
         int amount = this.lastCount;
         if (this.lastItem == null || this.lastItem.func_190926_b() || player.field_71071_by.func_194015_p() > 0 || !picked.func_77969_a(this.lastItem)) {
            amount = 0;

            for (ItemStack is : player.field_71071_by.field_70462_a) {
               if (is != null && !is.func_190926_b() && is.func_77969_a(picked)) {
                  amount += is.func_190916_E();
               }
            }

            this.lastItem = picked;
            player.field_71071_by.func_70296_d();
         }

         this.lastCount = amount;
         GL11.glPushMatrix();
         RenderHelper.func_74520_c();
         GL11.glDisable(2896);
         GL11.glEnable(32826);
         GL11.glEnable(2903);
         GL11.glEnable(2896);

         try {
            mc.func_175599_af().func_180450_b(picked, -8, -8);
         } catch (Exception var12) {
         }

         GL11.glDisable(2896);
         GL11.glPushMatrix();
         String am = "" + amount;
         int sw = mc.field_71466_p.func_78256_a(am);
         GL11.glTranslatef(0.0F, -mc.field_71466_p.field_78288_b, 500.0F);
         GL11.glScalef(0.5F, 0.5F, 0.5F);

         for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
               if ((a == 0 || b == 0) && (a != 0 || b != 0)) {
                  mc.field_71466_p.func_78276_b(am, a + 16 - sw, b + 24, 0);
               }
            }
         }

         mc.field_71466_p.func_78276_b(am, 16 - sw, 24, 16777215);
         GL11.glPopMatrix();
         GL11.glPopMatrix();
      }
   }

   public void renderAspectsInGui(GuiContainer gui, EntityPlayer player, ItemStack stack, int sd, int sx, int sy) {
      AspectList tags = ThaumcraftCraftingManager.getObjectTags(stack);
      if (tags != null) {
         GL11.glPushMatrix();
         int x = 0;
         int y = 0;
         int index = 0;
         if (tags.size() > 0) {
            for (Aspect tag : tags.getAspectsSortedByAmount()) {
               if (tag != null) {
                  x = sx + index * 18;
                  y = sy + sd - 16;
                  UtilsFX.drawTag(x, y, tag, tags.getAmount(tag), 0, gui.field_73735_i);
                  index++;
               }
            }
         }

         GL11.glPopMatrix();
      }
   }

   private boolean isMouseOverSlot(Slot par1Slot, int par2, int par3, int par4, int par5) {
      int var4x = par4;
      int var5x = par5;
      par2 -= var4x;
      par3 -= var5x;
      return par2 >= par1Slot.field_75223_e - 1
         && par2 < par1Slot.field_75223_e + 16 + 1
         && par3 >= par1Slot.field_75221_f - 1
         && par3 < par1Slot.field_75221_f + 16 + 1;
   }

   public static class KnowledgeGainTracker {
      IPlayerKnowledge.EnumKnowledgeType type;
      ResearchCategory category;
      int progress;
      int max;
      long seed;
      boolean sparks = false;

      public KnowledgeGainTracker(IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int progress, long seed) {
         this.type = type;
         this.category = category;
         if (type == IPlayerKnowledge.EnumKnowledgeType.THEORY) {
            progress += 10;
         }

         this.progress = progress;
         this.max = progress;
         this.seed = seed;
      }
   }
}
