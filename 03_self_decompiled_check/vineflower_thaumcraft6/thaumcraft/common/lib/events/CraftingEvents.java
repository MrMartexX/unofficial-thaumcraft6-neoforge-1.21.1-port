package thaumcraft.common.lib.events;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.lib.research.ResearchManager;

@EventBusSubscriber
public class CraftingEvents implements IFuelHandler {
   public int getBurnTime(ItemStack fuel) {
      if (fuel.func_77969_a(new ItemStack(ItemsTC.alumentum))) {
         return 4800;
      } else if (fuel.func_77969_a(new ItemStack(BlocksTC.logGreatwood))) {
         return 500;
      } else {
         return fuel.func_77969_a(new ItemStack(BlocksTC.logSilverwood)) ? 400 : 0;
      }
   }

   @SubscribeEvent
   public static void onCrafting(ItemCraftedEvent event) {
      int warp = ThaumcraftApi.getWarp(event.crafting);
      if (!ModConfig.CONFIG_MISC.wussMode && warp > 0 && !event.player.field_70170_p.field_72995_K) {
         ThaumcraftApi.internalMethods.addWarpToPlayer(event.player, warp, IPlayerWarp.EnumWarpType.NORMAL);
      }

      if (event.crafting.func_77973_b() == ItemsTC.label && event.crafting.func_77942_o()) {
         for (int var2 = 0; var2 < 9; var2++) {
            ItemStack var3 = event.craftMatrix.func_70301_a(var2);
            if (var3 != null && var3.func_77973_b() instanceof ItemPhial) {
               var3.func_190917_f(1);
               event.craftMatrix.func_70299_a(var2, var3);
            }
         }
      }

      if (event.player != null && !event.player.field_70170_p.field_72995_K) {
         int stackHash = ResearchManager.createItemStackHash(event.crafting.func_77946_l());
         if (ResearchManager.craftingReferences.contains(stackHash)) {
            ResearchManager.completeResearch(event.player, "[#]" + stackHash);
         } else {
            stackHash = ResearchManager.createItemStackHash(
               new ItemStack(event.crafting.func_77973_b(), event.crafting.func_190916_E(), event.crafting.func_77952_i())
            );
            if (ResearchManager.craftingReferences.contains(stackHash)) {
               ResearchManager.completeResearch(event.player, "[#]" + stackHash);
            }
         }

         try {
            int[] ids = OreDictionary.getOreIDs(event.crafting.func_77946_l());

            for (int id : ids) {
               String ore = OreDictionary.getOreName(id);
               if (ore != null) {
                  int cd = ("oredict:" + ore).hashCode();
                  if (ore != null && ResearchManager.craftingReferences.contains(cd)) {
                     ResearchManager.completeResearch(event.player, "[#]" + cd);
                  }
               }
            }
         } catch (Exception var10) {
         }
      }
   }

   @SubscribeEvent
   public static void onAnvil(AnvilUpdateEvent event) {
      if (event.getLeft().func_77973_b() == ItemsTC.primordialPearl || event.getRight().func_77973_b() == ItemsTC.primordialPearl) {
         event.setCanceled(true);
      }
   }
}
