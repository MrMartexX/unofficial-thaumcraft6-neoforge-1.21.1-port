package thaumcraft.client.lib.events;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.client.event.RenderTooltipEvent.PostBackground;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.items.IArchitect;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketNote;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import thaumcraft.common.tiles.devices.TileArcaneEar;
import thaumcraft.common.tiles.devices.TileRedstoneRelay;

@EventBusSubscriber(Side.CLIENT)
public class RenderEventHandler {
   public static RenderEventHandler INSTANCE = new RenderEventHandler();
   @SideOnly(Side.CLIENT)
   public static HudHandler hudHandler = new HudHandler();
   @SideOnly(Side.CLIENT)
   public static WandRenderingHandler wandHandler = new WandRenderingHandler();
   @SideOnly(Side.CLIENT)
   static ShaderHandler shaderhandler = new ShaderHandler();
   public static List blockTags = new ArrayList();
   public static float tagscale = 0.0F;
   public static int tickCount = 0;
   static boolean checkedDate = false;
   private Random random = new Random();
   public static boolean resetShaders = false;
   private static int oldDisplayWidth = 0;
   private static int oldDisplayHeight = 0;
   public static Entity thaumTarget = null;
   static final ResourceLocation CFRAME = new ResourceLocation("thaumcraft", "textures/misc/frame_corner.png");
   static final ResourceLocation MIDDLE = new ResourceLocation("thaumcraft", "textures/misc/seal_area.png");
   static EnumFacing[][] rotfaces = new EnumFacing[][]{
      {EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.WEST},
      {EnumFacing.UP, EnumFacing.NORTH, EnumFacing.WEST},
      {EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST},
      {EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST},
      {EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.EAST},
      {EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.EAST},
      {EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.WEST},
      {EnumFacing.UP, EnumFacing.SOUTH, EnumFacing.WEST}
   };
   static int[][] rotmat = new int[][]{{0, 270, 0}, {270, 180, 270}, {90, 0, 90}, {180, 90, 180}, {180, 180, 0}, {90, 270, 270}, {270, 90, 90}, {0, 0, 180}};
   public static HashMap<Integer, ShaderGroup> shaderGroups = new HashMap<>();
   public static boolean fogFiddled = false;
   public static float fogTarget = 0.0F;
   public static int fogDuration = 0;
   public static float prevVignetteBrightness = 0.0F;
   public static float targetBrightness = 1.0F;
   protected static final ResourceLocation vignetteTexPath = new ResourceLocation("thaumcraft", "textures/misc/vignette.png");

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void playerTick(PlayerTickEvent event) {
      Minecraft mc = Minecraft.func_71410_x();
      if (event.side != Side.SERVER && event.player.func_145782_y() == mc.field_71439_g.func_145782_y()) {
         if (event.phase == Phase.START) {
            try {
               shaderhandler.checkShaders(event, mc);
               if (ShaderHandler.warpVignette > 0) {
                  ShaderHandler.warpVignette--;
                  targetBrightness = 0.0F;
               } else {
                  targetBrightness = 1.0F;
               }

               if (fogFiddled) {
                  if (fogDuration < 100) {
                     fogTarget = 0.1F * (fogDuration / 100.0F);
                  } else if (fogTarget < 0.1F) {
                     fogTarget += 0.001F;
                  }

                  fogDuration--;
                  if (fogDuration < 0) {
                     fogFiddled = false;
                  }
               }
            } catch (Exception var3) {
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void clientWorldTick(ClientTickEvent event) {
      if (event.side != Side.SERVER) {
         Minecraft mc = FMLClientHandler.instance().getClient();
         World world = mc.field_71441_e;
         if (event.phase == Phase.START) {
            tickCount++;

            for (String fxk : EssentiaHandler.sourceFX.keySet().toArray(new String[0])) {
               EssentiaHandler.EssentiaSourceFX fx = EssentiaHandler.sourceFX.get(fxk);
               if (world != null) {
                  int mod = 0;
                  TileEntity tile = world.func_175625_s(fx.start);
                  if (tile != null && tile instanceof TileInfusionMatrix) {
                     mod = -1;
                  }

                  FXDispatcher.INSTANCE.essentiaTrailFx(fx.end, fx.start.func_177981_b(mod), tickCount, fx.color, 0.1F, fx.ext);
                  EssentiaHandler.sourceFX.remove(fxk);
               }
            }
         } else {
            LinkedBlockingQueue<HudHandler.KnowledgeGainTracker> temp = new LinkedBlockingQueue<>();
            if (hudHandler.knowledgeGainTrackers.isEmpty()) {
               if (hudHandler.kgFade > 0.0F) {
                  hudHandler.kgFade--;
               }
            } else {
               hudHandler.kgFade += 10.0F;
               if (hudHandler.kgFade > 40.0F) {
                  hudHandler.kgFade = 40.0F;
               }

               while (!hudHandler.knowledgeGainTrackers.isEmpty()) {
                  HudHandler.KnowledgeGainTracker current = hudHandler.knowledgeGainTrackers.poll();
                  if (current != null && current.progress > 0) {
                     current.progress--;
                     temp.offer(current);
                  }
               }

               while (!temp.isEmpty()) {
                  hudHandler.knowledgeGainTrackers.offer(temp.poll());
               }
            }

            if (mc.field_71441_e != null && !checkedDate) {
               checkedDate = true;
               Calendar calendar = mc.field_71441_e.func_83015_S();
               if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
                  ModConfig.isHalloween = true;
               }
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void renderTick(RenderTickEvent event) {
      if (event.phase == Phase.START) {
         UtilsFX.sysPartialTicks = event.renderTickTime;
      } else {
         Minecraft mc = FMLClientHandler.instance().getClient();
         if (Minecraft.func_71410_x().func_175606_aa() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)Minecraft.func_71410_x().func_175606_aa();
            long time = System.currentTimeMillis();
            if (player != null) {
               hudHandler.renderHuds(mc, event.renderTickTime, player, time);
            }

            if (player.func_184614_ca() != null && player.func_184614_ca().func_77973_b() instanceof IArchitect
               || player.func_184592_cb() != null && player.func_184592_cb().func_77973_b() instanceof IArchitect) {
               ItemStack stack = player.func_184614_ca() != null && player.func_184614_ca().func_77973_b() instanceof IArchitect
                  ? player.func_184614_ca()
                  : player.func_184592_cb();
               if (!((IArchitect)stack.func_77973_b()).useBlockHighlight(stack)) {
                  RayTraceResult target2 = ((IArchitect)stack.func_77973_b()).getArchitectMOP(stack, player.field_70170_p, player);
                  if (target2 != null) {
                     wandHandler.handleArchitectOverlay(stack, player, event.renderTickTime, player.field_70173_aa, target2);
                  }
               }
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void tooltipEvent(ItemTooltipEvent event) {
      Minecraft mc = FMLClientHandler.instance().getClient();
      GuiScreen gui = mc.field_71462_r;
      if (gui instanceof GuiContainer && GuiScreen.func_146272_n() != ModConfig.CONFIG_GRAPHICS.showTags && !Mouse.isGrabbed() && event.getItemStack() != null) {
         AspectList tags = ThaumcraftCraftingManager.getObjectTags(event.getItemStack());
         int index = 0;
         if (tags != null && tags.size() > 0) {
            for (Aspect tag : tags.getAspects()) {
               if (tag != null) {
                  index++;
               }
            }
         }

         int width = index * 18;
         if (width > 0) {
            double sw = mc.field_71466_p.func_78256_a(" ");
            int t = MathHelper.func_76143_f(width / sw);
            int l = MathHelper.func_76143_f(18.0 / mc.field_71466_p.field_78288_b);

            for (int a = 0; a < l; a++) {
               event.getToolTip()
                  .add(
                     "                                                                                                                                            "
                        .substring(0, Math.min(120, t))
                  );
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void tooltipEvent(PostBackground event) {
      Minecraft mc = FMLClientHandler.instance().getClient();
      GuiScreen gui = mc.field_71462_r;
      if (gui instanceof GuiContainer && GuiScreen.func_146272_n() != ModConfig.CONFIG_GRAPHICS.showTags && !Mouse.isGrabbed()) {
         int bot = event.getHeight();
         if (!event.getLines().isEmpty()) {
            for (int a = event.getLines().size() - 1; a >= 0; a--) {
               if (event.getLines().get(a) != null && !((String)event.getLines().get(a)).contains("    ")) {
                  bot -= 10;
               } else if (a > 0 && event.getLines().get(a - 1) != null && ((String)event.getLines().get(a - 1)).contains("    ")) {
                  hudHandler.renderAspectsInGui((GuiContainer)gui, mc.field_71439_g, event.getStack(), bot, event.getX(), event.getY());
                  break;
               }
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void renderOverlay(RenderGameOverlayEvent event) {
      Minecraft mc = Minecraft.func_71410_x();
      long time = System.nanoTime() / 1000000L;
      if (event.getType() == ElementType.TEXT) {
         wandHandler.handleFociRadial(mc, time, event);
      }

      if (event.getType() == ElementType.PORTAL) {
         renderVignette(targetBrightness, event.getResolution().func_78327_c(), event.getResolution().func_78324_d());
      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void renderShaders(Pre event) {
      if (!ModConfig.CONFIG_GRAPHICS.disableShaders && event.getType() == ElementType.ALL) {
         Minecraft mc = Minecraft.func_71410_x();
         if (OpenGlHelper.field_148824_g && shaderGroups.size() > 0) {
            updateShaderFrameBuffers(mc);
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();

            for (ShaderGroup sg : shaderGroups.values()) {
               GL11.glPushMatrix();

               try {
                  sg.func_148018_a(event.getPartialTicks());
               } catch (Exception var5) {
               }

               GL11.glPopMatrix();
            }

            mc.func_147110_a().func_147610_a(true);
         }
      }
   }

   private static void updateShaderFrameBuffers(Minecraft mc) {
      if (resetShaders || mc.field_71443_c != oldDisplayWidth || oldDisplayHeight != mc.field_71440_d) {
         for (ShaderGroup sg : shaderGroups.values()) {
            sg.func_148026_a(mc.field_71443_c, mc.field_71440_d);
         }

         oldDisplayWidth = mc.field_71443_c;
         oldDisplayHeight = mc.field_71440_d;
         resetShaders = false;
      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void blockHighlight(DrawBlockHighlightEvent event) {
      int ticks = event.getPlayer().field_70173_aa;
      RayTraceResult target = event.getTarget();
      if (blockTags.size() > 0) {
         int x = (Integer)blockTags.get(0);
         int y = (Integer)blockTags.get(1);
         int z = (Integer)blockTags.get(2);
         AspectList ot = (AspectList)blockTags.get(3);
         EnumFacing dir = EnumFacing.field_82609_l[blockTags.get(4)];
         if (x == target.func_178782_a().func_177958_n() && y == target.func_178782_a().func_177956_o() && z == target.func_178782_a().func_177952_p()) {
            if (tagscale < 0.5F) {
               tagscale = tagscale + (0.031F - tagscale / 10.0F);
            }

            drawTagsOnContainer(
               target.func_178782_a().func_177958_n() + dir.func_82601_c() / 2.0F,
               target.func_178782_a().func_177956_o() + dir.func_96559_d() / 2.0F,
               target.func_178782_a().func_177952_p() + dir.func_82599_e() / 2.0F,
               ot,
               220,
               dir,
               event.getPartialTicks()
            );
         }
      }

      if (target != null && target.func_178782_a() != null) {
         TileEntity te = event.getPlayer().field_70170_p.func_175625_s(target.func_178782_a());
         if (te != null && te instanceof TileRedstoneRelay) {
            RayTraceResult hit = RayTracer.retraceBlock(event.getPlayer().field_70170_p, event.getPlayer(), target.func_178782_a());
            if (hit != null) {
               if (hit.subHit == 0) {
                  drawTextInAir(
                     target.func_178782_a().func_177958_n(),
                     target.func_178782_a().func_177956_o() + 0.3,
                     target.func_178782_a().func_177952_p(),
                     event.getPartialTicks(),
                     "Out: " + ((TileRedstoneRelay)te).getOut()
                  );
               } else if (hit.subHit == 1) {
                  drawTextInAir(
                     target.func_178782_a().func_177958_n(),
                     target.func_178782_a().func_177956_o() + 0.3,
                     target.func_178782_a().func_177952_p(),
                     event.getPartialTicks(),
                     "In: " + ((TileRedstoneRelay)te).getIn()
                  );
               }
            }
         }

         if (EntityUtils.hasGoggles(event.getPlayer())) {
            float to = 0.0F;
            if (te instanceof IGogglesDisplayExtended) {
               GL11.glDisable(2929);
               Vec3d v = ((IGogglesDisplayExtended)te).getIGogglesTextOffset();
               String[] sa = ((IGogglesDisplayExtended)te).getIGogglesText();

               for (String s : sa) {
                  drawTextInAir(
                     target.func_178782_a().func_177958_n() + v.field_72450_a,
                     target.func_178782_a().func_177956_o() + v.field_72448_b - (to - sa.length / 2.0F) / 5.5F,
                     target.func_178782_a().func_177952_p() + v.field_72449_c,
                     event.getPartialTicks(),
                     s
                  );
                  to++;
               }

               GL11.glEnable(2929);
            } else {
               Block b = event.getPlayer().field_70170_p.func_180495_p(target.func_178782_a()).func_177230_c();
               if (b instanceof IGogglesDisplayExtended) {
                  GL11.glDisable(2929);
                  Vec3d v = ((IGogglesDisplayExtended)b).getIGogglesTextOffset();
                  String[] sa = ((IGogglesDisplayExtended)b).getIGogglesText();

                  for (String s : sa) {
                     drawTextInAir(
                        target.func_178782_a().func_177958_n() + v.field_72450_a,
                        target.func_178782_a().func_177956_o() + v.field_72448_b + (to - sa.length / 2.0F) / 5.5F,
                        target.func_178782_a().func_177952_p() + v.field_72449_c,
                        event.getPartialTicks(),
                        s
                     );
                     to++;
                  }

                  GL11.glEnable(2929);
               }
            }

            boolean spaceAbove = event.getPlayer().field_70170_p.func_175623_d(target.func_178782_a().func_177984_a());
            if (te != null) {
               int note = -1;
               if (te instanceof TileEntityNote) {
                  note = ((TileEntityNote)te).field_145879_a;
               } else if (te instanceof TileArcaneEar) {
                  note = ((TileArcaneEar)te).note;
               } else if (te instanceof IAspectContainer && ((IAspectContainer)te).getAspects() != null && ((IAspectContainer)te).getAspects().size() > 0) {
                  float shift = 0.0F;
                  if (tagscale < 0.3F) {
                     tagscale = tagscale + (0.031F - tagscale / 10.0F);
                  }

                  drawTagsOnContainer(
                     target.func_178782_a().func_177958_n(),
                     target.func_178782_a().func_177956_o() + (spaceAbove ? 0.4F : 0.0F) + shift,
                     target.func_178782_a().func_177952_p(),
                     ((IAspectContainer)te).getAspects(),
                     220,
                     spaceAbove ? EnumFacing.UP : event.getTarget().field_178784_b,
                     event.getPartialTicks()
                  );
               }

               if (note >= 0) {
                  if (ticks % 5 == 0) {
                     PacketHandler.INSTANCE
                        .sendToServer(
                           new PacketNote(
                              target.func_178782_a().func_177958_n(),
                              target.func_178782_a().func_177956_o(),
                              target.func_178782_a().func_177952_p(),
                              event.getPlayer().field_70170_p.field_73011_w.getDimension()
                           )
                        );
                  }

                  drawTextInAir(
                     target.func_178782_a().func_177958_n(),
                     target.func_178782_a().func_177956_o() + 1,
                     target.func_178782_a().func_177952_p(),
                     event.getPartialTicks(),
                     "Note: " + note
                  );
               }
            }
         }
      }

      if (target.field_72313_a == Type.BLOCK
         && (
            event.getPlayer().func_184614_ca() != null && event.getPlayer().func_184614_ca().func_77973_b() instanceof IArchitect
               || event.getPlayer().func_184592_cb() != null && event.getPlayer().func_184592_cb().func_77973_b() instanceof IArchitect
         )) {
         ItemStack stack = event.getPlayer().func_184614_ca() != null && event.getPlayer().func_184614_ca().func_77973_b() instanceof IArchitect
            ? event.getPlayer().func_184614_ca()
            : event.getPlayer().func_184592_cb();
         if (((IArchitect)stack.func_77973_b()).useBlockHighlight(stack)) {
            RayTraceResult target2 = ((IArchitect)stack.func_77973_b()).getArchitectMOP(stack, event.getPlayer().field_70170_p, event.getPlayer());
            if (target2 != null
               && wandHandler.handleArchitectOverlay(stack, event.getPlayer(), event.getPartialTicks(), event.getPlayer().field_70173_aa, target2)) {
               event.setCanceled(true);
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void renderLast(RenderWorldLastEvent event) {
      if (tagscale > 0.0F) {
         tagscale -= 0.005F;
      }

      float partialTicks = event.getPartialTicks();
      Minecraft mc = Minecraft.func_71410_x();
      if (Minecraft.func_71410_x().func_175606_aa() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)mc.func_175606_aa();
         if (player.func_184614_ca() != null && player.func_184614_ca().func_77973_b() instanceof ISealDisplayer) {
            drawSeals(partialTicks, player);
         } else if (player.func_184592_cb() != null && player.func_184592_cb().func_77973_b() instanceof ISealDisplayer) {
            drawSeals(partialTicks, player);
         }

         if (player.func_184614_ca() != null && player.func_184614_ca().func_77973_b() instanceof IArchitect) {
            RayTraceResult target = ((IArchitect)player.func_184614_ca().func_77973_b()).getArchitectMOP(player.func_184614_ca(), player.field_70170_p, player);
            wandHandler.handleArchitectOverlay(player.func_184614_ca(), player, partialTicks, player.field_70173_aa, target);
         } else if (player.func_184592_cb() != null && player.func_184592_cb().func_77973_b() instanceof IArchitect) {
            RayTraceResult target = ((IArchitect)player.func_184592_cb().func_77973_b()).getArchitectMOP(player.func_184592_cb(), player.field_70170_p, player);
            wandHandler.handleArchitectOverlay(player.func_184592_cb(), player, partialTicks, player.field_70173_aa, target);
         }

         if (thaumTarget != null) {
            AspectList ot = AspectHelper.getEntityAspects(thaumTarget);
            if (ot != null && !ot.aspects.isEmpty()) {
               if (tagscale < 0.5F) {
                  tagscale = tagscale + (0.031F - tagscale / 10.0F);
               }

               double iPX = thaumTarget.field_70169_q + (thaumTarget.field_70165_t - thaumTarget.field_70169_q) * partialTicks;
               double iPY = thaumTarget.field_70167_r + (thaumTarget.field_70163_u - thaumTarget.field_70167_r) * partialTicks;
               double iPZ = thaumTarget.field_70166_s + (thaumTarget.field_70161_v - thaumTarget.field_70166_s) * partialTicks;
               drawTagsOnContainer(iPX, iPY + thaumTarget.field_70131_O, iPZ, ot, 220, null, event.getPartialTicks());
            }
         }
      }
   }

   private static void drawSeals(float partialTicks, EntityPlayer player) {
      ConcurrentHashMap<SealPos, SealEntity> seals = SealHandler.sealEntities.get(player.field_70170_p.field_73011_w.getDimension());
      if (seals != null && seals.size() > 0) {
         GL11.glPushMatrix();
         if (player.func_70093_af()) {
            GL11.glDisable(2929);
         }

         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glDisable(2884);
         double iPX = player.field_70169_q + (player.field_70165_t - player.field_70169_q) * partialTicks;
         double iPY = player.field_70167_r + (player.field_70163_u - player.field_70167_r) * partialTicks;
         double iPZ = player.field_70166_s + (player.field_70161_v - player.field_70166_s) * partialTicks;
         GL11.glTranslated(-iPX, -iPY, -iPZ);

         for (ISealEntity seal : seals.values()) {
            double dis = player.func_174831_c(seal.getSealPos().pos);
            if (dis <= 256.0) {
               float alpha = 1.0F - (float)(dis / 256.0);
               boolean ia = false;
               if (seal.isStoppedByRedstone(player.field_70170_p)) {
                  ia = true;
                  if (player.field_70170_p.field_73012_v.nextFloat() < partialTicks / 12.0F) {
                     FXDispatcher.INSTANCE
                        .spark(
                           seal.getSealPos().pos.func_177958_n() + 0.5F + seal.getSealPos().face.func_82601_c() * 0.66F,
                           seal.getSealPos().pos.func_177956_o() + 0.5F + seal.getSealPos().face.func_96559_d() * 0.66F,
                           seal.getSealPos().pos.func_177952_p() + 0.5F + seal.getSealPos().face.func_82599_e() * 0.66F,
                           2.0F,
                           0.8F - player.field_70170_p.field_73012_v.nextFloat() * 0.2F,
                           0.0F,
                           0.0F,
                           1.0F
                        );
                     ia = false;
                  }
               }

               renderSeal(
                  seal.getSealPos().pos.func_177958_n(),
                  seal.getSealPos().pos.func_177956_o(),
                  seal.getSealPos().pos.func_177952_p(),
                  alpha,
                  seal.getSealPos().face,
                  seal.getSeal().getSealIcon(),
                  ia
               );
               drawSealArea(player, seal, alpha, partialTicks);
            }
         }

         GL11.glDisable(3042);
         GL11.glEnable(2884);
         if (player.func_70093_af()) {
            GL11.glEnable(2929);
         }

         GL11.glPopMatrix();
      }
   }

   private static void drawSealArea(EntityPlayer player, ISealEntity seal, float alpha, float partialTicks) {
      GL11.glPushMatrix();
      float r = 0.0F;
      float g = 0.0F;
      float b = 0.0F;
      if (seal.getColor() > 0) {
         Color c = new Color(EnumDyeColor.func_176764_b(seal.getColor() - 1).func_193350_e());
         r = c.getRed() / 255.0F;
         g = c.getGreen() / 255.0F;
         b = c.getBlue() / 255.0F;
      } else {
         r = 0.7F + MathHelper.func_76126_a((player.field_70173_aa + partialTicks + seal.getSealPos().pos.func_177958_n()) / 4.0F) * 0.1F;
         g = 0.7F + MathHelper.func_76126_a((player.field_70173_aa + partialTicks + seal.getSealPos().pos.func_177956_o()) / 5.0F) * 0.1F;
         b = 0.7F + MathHelper.func_76126_a((player.field_70173_aa + partialTicks + seal.getSealPos().pos.func_177952_p()) / 6.0F) * 0.1F;
      }

      GL11.glPushMatrix();
      GL11.glTranslated(seal.getSealPos().pos.func_177958_n() + 0.5, seal.getSealPos().pos.func_177956_o() + 0.5, seal.getSealPos().pos.func_177952_p() + 0.5);
      GL11.glRotatef(90.0F, -seal.getSealPos().face.func_96559_d(), seal.getSealPos().face.func_82601_c(), -seal.getSealPos().face.func_82599_e());
      if (seal.getSealPos().face.func_82599_e() < 0) {
         GL11.glTranslated(0.0, 0.0, -0.51F);
      } else {
         GL11.glTranslated(0.0, 0.0, 0.51F);
      }

      GL11.glRotatef(player.field_70173_aa % 360 + partialTicks, 0.0F, 0.0F, 1.0F);
      UtilsFX.renderQuadCentered(MIDDLE, 0.9F, r, g, b, 200, 771, alpha * 0.8F);
      GL11.glPopMatrix();
      if (seal.getSeal() instanceof ISealConfigArea) {
         GL11.glDepthMask(false);
         AxisAlignedBB area = new AxisAlignedBB(
               seal.getSealPos().pos.func_177958_n(),
               seal.getSealPos().pos.func_177956_o(),
               seal.getSealPos().pos.func_177952_p(),
               seal.getSealPos().pos.func_177958_n() + 1,
               seal.getSealPos().pos.func_177956_o() + 1,
               seal.getSealPos().pos.func_177952_p() + 1
            )
            .func_72317_d(seal.getSealPos().face.func_82601_c(), seal.getSealPos().face.func_96559_d(), seal.getSealPos().face.func_82599_e())
            .func_72321_a(
               seal.getSealPos().face.func_82601_c() != 0 ? (seal.getArea().func_177958_n() - 1) * seal.getSealPos().face.func_82601_c() : 0.0,
               seal.getSealPos().face.func_96559_d() != 0 ? (seal.getArea().func_177956_o() - 1) * seal.getSealPos().face.func_96559_d() : 0.0,
               seal.getSealPos().face.func_82599_e() != 0 ? (seal.getArea().func_177952_p() - 1) * seal.getSealPos().face.func_82599_e() : 0.0
            )
            .func_72314_b(
               seal.getSealPos().face.func_82601_c() == 0 ? seal.getArea().func_177958_n() - 1 : 0.0,
               seal.getSealPos().face.func_96559_d() == 0 ? seal.getArea().func_177956_o() - 1 : 0.0,
               seal.getSealPos().face.func_82599_e() == 0 ? seal.getArea().func_177952_p() - 1 : 0.0
            );
         double[][] locs = new double[][]{
            {area.field_72340_a, area.field_72338_b, area.field_72339_c},
            {area.field_72340_a, area.field_72337_e - 1.0, area.field_72339_c},
            {area.field_72336_d - 1.0, area.field_72338_b, area.field_72339_c},
            {area.field_72336_d - 1.0, area.field_72337_e - 1.0, area.field_72339_c},
            {area.field_72336_d - 1.0, area.field_72338_b, area.field_72334_f - 1.0},
            {area.field_72336_d - 1.0, area.field_72337_e - 1.0, area.field_72334_f - 1.0},
            {area.field_72340_a, area.field_72338_b, area.field_72334_f - 1.0},
            {area.field_72340_a, area.field_72337_e - 1.0, area.field_72334_f - 1.0}
         };
         int q = 0;

         for (double[] loc : locs) {
            GL11.glPushMatrix();
            GL11.glTranslated(loc[0] + 0.5, loc[1] + 0.5, loc[2] + 0.5);
            int w = 0;

            for (EnumFacing face : rotfaces[q]) {
               GL11.glPushMatrix();
               GL11.glRotatef(90.0F, -face.func_96559_d(), face.func_82601_c(), -face.func_82599_e());
               if (face.func_82599_e() < 0) {
                  GL11.glTranslated(0.0, 0.0, -0.49F);
               } else {
                  GL11.glTranslated(0.0, 0.0, 0.49F);
               }

               GL11.glRotatef(90.0F, 0.0F, 0.0F, -1.0F);
               GL11.glRotatef(rotmat[q][w], 0.0F, 0.0F, 1.0F);
               UtilsFX.renderQuadCentered(CFRAME, 1.0F, r, g, b, 200, 771, alpha * 0.7F);
               GL11.glPopMatrix();
               w++;
            }

            GL11.glPopMatrix();
            q++;
         }

         GL11.glDepthMask(true);
      }

      GL11.glPopMatrix();
   }

   static void renderSeal(int x, int y, int z, float alpha, EnumFacing face, ResourceLocation resourceLocation, boolean ia) {
      GL11.glPushMatrix();
      GL11.glColor4f(ia ? 0.5F : 1.0F, ia ? 0.5F : 1.0F, ia ? 0.5F : 1.0F, alpha);
      translateSeal(x, y, z, face.ordinal(), -0.05F);
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      UtilsFX.renderItemIn2D(resourceLocation.toString(), Minecraft.func_71410_x().func_175606_aa().func_70093_af() ? 0.0F : 0.1F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
   }

   private static void translateSeal(float x, float y, float z, int orientation, float off) {
      if (orientation == 1) {
         GL11.glTranslatef(x + 0.25F, y + 1.0F, z + 0.75F);
         GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
      } else if (orientation == 0) {
         GL11.glTranslatef(x + 0.25F, y, z + 0.25F);
         GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      } else if (orientation == 3) {
         GL11.glTranslatef(x + 0.25F, y + 0.25F, z + 1.0F);
      } else if (orientation == 2) {
         GL11.glTranslatef(x + 0.75F, y + 0.25F, z);
         GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
      } else if (orientation == 5) {
         GL11.glTranslatef(x + 1.0F, y + 0.25F, z + 0.75F);
         GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
      } else if (orientation == 4) {
         GL11.glTranslatef(x, y + 0.25F, z + 0.25F);
         GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
      }

      GL11.glTranslatef(0.0F, 0.0F, -off);
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void fogDensityEvent(RenderFogEvent event) {
      if (fogFiddled && fogTarget > 0.0F) {
         GL11.glFogi(2917, 2048);
         GL11.glFogf(2914, fogTarget);
      }
   }

   @SubscribeEvent
   public static void livingTick(LivingUpdateEvent event) {
      if (event.getEntity().field_70170_p.field_72995_K && event.getEntity() instanceof EntityCreature && !event.getEntity().field_70128_L) {
         EntityCreature mob = (EntityCreature)event.getEntity();
         if (mob.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD) != null) {
            Integer t = (int)mob.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD).func_111126_e();
            if (t != null && t >= 0 && t < ChampionModifier.mods.length) {
               ChampionModifier.mods[t].effect.showFX(mob);
            }
         }
      }
   }

   @SubscribeEvent
   public static void renderLivingPre(net.minecraftforge.client.event.RenderLivingEvent.Pre event) {
      if (event.getEntity().field_70170_p.field_72995_K && event.getEntity() instanceof EntityCreature && !event.getEntity().field_70128_L) {
         EntityCreature mob = (EntityCreature)event.getEntity();
         if (mob.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD) != null) {
            Integer t = (int)mob.func_110148_a(ThaumcraftApiHelper.CHAMPION_MOD).func_111126_e();
            if (t != null && t >= 0 && t < ChampionModifier.mods.length) {
               ChampionModifier.mods[t].effect.preRender(mob, event.getRenderer());
            }
         }
      }
   }

   public static void drawTagsOnContainer(double x, double y, double z, AspectList tags, int bright, EnumFacing dir, float partialTicks) {
      if (Minecraft.func_71410_x().func_175606_aa() instanceof EntityPlayer && tags != null && tags.size() > 0) {
         int fox = 0;
         int foy = 0;
         int foz = 0;
         if (dir != null) {
            fox = dir.func_82601_c();
            foy = dir.func_96559_d();
            foz = dir.func_82599_e();
         } else {
            x -= 0.5;
            z -= 0.5;
         }

         EntityPlayer player = (EntityPlayer)Minecraft.func_71410_x().func_175606_aa();
         double iPX = player.field_70169_q + (player.field_70165_t - player.field_70169_q) * partialTicks;
         double iPY = player.field_70167_r + (player.field_70163_u - player.field_70167_r) * partialTicks;
         double iPZ = player.field_70166_s + (player.field_70161_v - player.field_70166_s) * partialTicks;
         int rowsize = 5;
         int current = 0;
         float shifty = 0.0F;
         int left = tags.size();

         for (Aspect tag : tags.getAspects()) {
            int div = Math.min(left, rowsize);
            if (current >= rowsize) {
               current = 0;
               shifty -= tagscale * 1.05F;
               left -= rowsize;
               if (left < rowsize) {
                  div = left % rowsize;
               }
            }

            float shift = (current - div / 2.0F + 0.5F) * tagscale * 4.0F;
            shift *= tagscale;
            Color color = new Color(tag.getColor());
            GL11.glPushMatrix();
            GL11.glDisable(2929);
            GL11.glTranslated(-iPX + x + 0.5 + tagscale * 2.0F * fox, -iPY + y - shifty + 0.5 + tagscale * 2.0F * foy, -iPZ + z + 0.5 + tagscale * 2.0F * foz);
            float xd = (float)(iPX - (x + 0.5));
            float zd = (float)(iPZ - (z + 0.5));
            float rotYaw = (float)(Math.atan2(xd, zd) * 180.0 / Math.PI);
            GL11.glRotatef(rotYaw + 180.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslated(shift, 0.0, 0.0);
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(tagscale, tagscale, tagscale);
            UtilsFX.renderQuadCentered(tag.getImage(), 1.0F, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, bright, 771, 0.75F);
            if (tags.getAmount(tag) >= 0) {
               GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
               String am = "" + tags.getAmount(tag);
               GL11.glScalef(0.04F, 0.04F, 0.04F);
               GL11.glTranslated(0.0, 6.0, -0.1);
               int sw = Minecraft.func_71410_x().field_71466_p.func_78256_a(am);
               GL11.glEnable(3042);
               GL11.glBlendFunc(770, 771);
               Minecraft.func_71410_x().field_71466_p.func_78276_b(am, 14 - sw, 1, 1118481);
               GL11.glTranslated(0.0, 0.0, -0.1);
               Minecraft.func_71410_x().field_71466_p.func_78276_b(am, 13 - sw, 0, 16777215);
            }

            GL11.glEnable(2929);
            GL11.glPopMatrix();
            current++;
         }
      }
   }

   public static void drawTextInAir(double x, double y, double z, float partialTicks, String text) {
      if (Minecraft.func_71410_x().func_175606_aa() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)Minecraft.func_71410_x().func_175606_aa();
         double iPX = player.field_70169_q + (player.field_70165_t - player.field_70169_q) * partialTicks;
         double iPY = player.field_70167_r + (player.field_70163_u - player.field_70167_r) * partialTicks;
         double iPZ = player.field_70166_s + (player.field_70161_v - player.field_70166_s) * partialTicks;
         GL11.glPushMatrix();
         GL11.glTranslated(-iPX + x + 0.5, -iPY + y + 0.5, -iPZ + z + 0.5);
         float xd = (float)(iPX - (x + 0.5));
         float zd = (float)(iPZ - (z + 0.5));
         float rotYaw = (float)(Math.atan2(xd, zd) * 180.0 / Math.PI);
         GL11.glRotatef(rotYaw + 180.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
         GL11.glScalef(0.0125F, 0.0125F, 0.0125F);
         int sw = Minecraft.func_71410_x().field_71466_p.func_78256_a(text);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         Minecraft.func_71410_x().field_71466_p.func_175065_a(text, 1 - sw / 2, 1.0F, 16777215, true);
         GL11.glPopMatrix();
      }
   }

   protected static void renderVignette(float brightness, double sw, double sh) {
      int k = (int)sw;
      int l = (int)sh;
      brightness = 1.0F - brightness;
      prevVignetteBrightness = (float)(prevVignetteBrightness + (brightness - prevVignetteBrightness) * 0.01);
      if (prevVignetteBrightness > 0.0F) {
         float b = prevVignetteBrightness * (1.0F + MathHelper.func_76126_a(Minecraft.func_71410_x().field_71439_g.field_70173_aa / 2.0F) * 0.1F);
         GL11.glPushMatrix();
         GL11.glClear(256);
         GL11.glMatrixMode(5889);
         GL11.glLoadIdentity();
         GL11.glOrtho(0.0, sw, sh, 0.0, 1000.0, 3000.0);
         Minecraft.func_71410_x().func_110434_K().func_110577_a(vignetteTexPath);
         GL11.glMatrixMode(5888);
         GL11.glLoadIdentity();
         GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
         GL11.glDisable(2929);
         GL11.glDepthMask(false);
         OpenGlHelper.func_148821_a(0, 769, 1, 0);
         GL11.glColor4f(b, b, b, 1.0F);
         Tessellator tessellator = Tessellator.func_178181_a();
         tessellator.func_178180_c().func_181668_a(7, DefaultVertexFormats.field_181707_g);
         tessellator.func_178180_c().func_181662_b(0.0, l, -90.0).func_187315_a(0.0, 1.0).func_181675_d();
         tessellator.func_178180_c().func_181662_b(k, l, -90.0).func_187315_a(1.0, 1.0).func_181675_d();
         tessellator.func_178180_c().func_181662_b(k, 0.0, -90.0).func_187315_a(1.0, 0.0).func_181675_d();
         tessellator.func_178180_c().func_181662_b(0.0, 0.0, -90.0).func_187315_a(0.0, 0.0).func_181675_d();
         tessellator.func_78381_a();
         GL11.glDepthMask(true);
         GL11.glEnable(2929);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);
         GL11.glPopMatrix();
      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void textureStitchEventPre(net.minecraftforge.client.event.TextureStitchEvent.Pre event) {
      event.getMap().func_174942_a(new ResourceLocation("thaumcraft", "research/quill"));
      event.getMap().func_174942_a(new ResourceLocation("thaumcraft", "blocks/crystal"));
      event.getMap().func_174942_a(new ResourceLocation("thaumcraft", "blocks/taint_growth_1"));
      event.getMap().func_174942_a(new ResourceLocation("thaumcraft", "blocks/taint_growth_2"));
      event.getMap().func_174942_a(new ResourceLocation("thaumcraft", "blocks/taint_growth_3"));
      event.getMap().func_174942_a(new ResourceLocation("thaumcraft", "blocks/taint_growth_4"));
   }

   public static class ChargeEntry {
      public long time;
      public long tickTime;
      public ItemStack item;
      float charge = 0.0F;
      byte diff = 0;

      public ChargeEntry(long time, ItemStack item, float charge) {
         this.time = time;
         this.item = item;
         this.charge = charge;
      }
   }
}
