package thaumcraft.common.container;

import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.container.slot.SlotLimitedByClass;
import thaumcraft.common.container.slot.SlotLimitedByItemstack;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.crafting.TileResearchTable;

public class ContainerResearchTable extends Container {
   public TileResearchTable tileEntity;
   String[] aspects;
   EntityPlayer player;
   static HashMap<Integer, Long> antiSpam = new HashMap<>();

   public ContainerResearchTable(InventoryPlayer iinventory, TileResearchTable iinventory1) {
      this.player = iinventory.field_70458_d;
      this.tileEntity = iinventory1;
      this.aspects = Aspect.aspects.keySet().toArray(new String[0]);
      this.func_75146_a(new SlotLimitedByClass(IScribeTools.class, iinventory1, 0, 16, 15));
      this.func_75146_a(new SlotLimitedByItemstack(new ItemStack(Items.field_151121_aF), iinventory1, 1, 224, 16));
      this.bindPlayerInventory(iinventory);
   }

   protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.func_75146_a(new Slot(inventoryPlayer, j + i * 9 + 9, 77 + j * 18, 190 + i * 18));
         }
      }

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            this.func_75146_a(new Slot(inventoryPlayer, i + j * 3, 20 + i * 18, 190 + j * 18));
         }
      }
   }

   public boolean func_75140_a(EntityPlayer playerIn, int button) {
      if (button == 1) {
         if (this.tileEntity.data.lastDraw != null) {
            this.tileEntity.data.savedCards.add(this.tileEntity.data.lastDraw.card.getSeed());
         }

         for (ResearchTableData.CardChoice cc : this.tileEntity.data.cardChoices) {
            if (cc.selected) {
               this.tileEntity.data.lastDraw = cc;
               break;
            }
         }

         this.tileEntity.data.cardChoices.clear();
         this.tileEntity.syncTile(false);
         return true;
      } else {
         if (button == 4 || button == 5 || button == 6) {
            long tn = System.currentTimeMillis();
            long to = 0L;
            if (antiSpam.containsKey(playerIn.func_145782_y())) {
               to = antiSpam.get(playerIn.func_145782_y());
            }

            if (tn - to < 333L) {
               return false;
            }

            antiSpam.put(playerIn.func_145782_y(), tn);

            try {
               TheorycraftCard card = this.tileEntity.data.cardChoices.get(button - 4).card;
               if (card.getRequiredItems() != null) {
                  for (ItemStack stack : card.getRequiredItems()) {
                     if (stack != null && !stack.func_190926_b() && !InventoryUtils.isPlayerCarryingAmount(this.player, stack, true)) {
                        return false;
                     }
                  }

                  if (card.getRequiredItemsConsumed() != null && card.getRequiredItemsConsumed().length == card.getRequiredItems().length) {
                     for (int a = 0; a < card.getRequiredItems().length; a++) {
                        if (card.getRequiredItemsConsumed()[a] && card.getRequiredItems()[a] != null && !card.getRequiredItems()[a].func_190926_b()) {
                           InventoryUtils.consumePlayerItem(this.player, card.getRequiredItems()[a], true, true);
                        }
                     }
                  }
               }

               if (card.activate(playerIn, this.tileEntity.data)) {
                  this.tileEntity.consumeInkFromTable();
                  this.tileEntity.data.cardChoices.get(button - 4).selected = true;
                  this.tileEntity.data.addInspiration(-card.getInspirationCost());
                  this.tileEntity.syncTile(false);
                  return true;
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
         }

         if (button == 7 && this.tileEntity.data.isComplete()) {
            this.tileEntity.finishTheory(playerIn);
            this.tileEntity.syncTile(false);
            return true;
         }

         if (button == 9 && !this.tileEntity.data.isComplete()) {
            this.tileEntity.data = null;
            this.tileEntity.syncTile(false);
            return true;
         }

         if (button != 2 && button != 3) {
            return false;
         }

         if (this.tileEntity.data != null && !this.tileEntity.data.isComplete() && this.tileEntity.consumepaperFromTable()) {
            this.tileEntity.data.drawCards(button, playerIn);
            this.tileEntity.syncTile(false);
         }

         return true;
      }
   }

   public ItemStack func_82846_b(EntityPlayer par1EntityPlayer, int slot) {
      ItemStack stack = ItemStack.field_190927_a;
      Slot slotObject = (Slot)this.field_75151_b.get(slot);
      if (slotObject != null && slotObject.func_75216_d()) {
         ItemStack stackInSlot = slotObject.func_75211_c();
         stack = stackInSlot.func_77946_l();
         if (slot < 2) {
            if (!this.tileEntity.func_94041_b(slot, stackInSlot) || !this.func_75135_a(stackInSlot, 2, this.field_75151_b.size(), true)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.tileEntity.func_94041_b(slot, stackInSlot) || !this.func_75135_a(stackInSlot, 0, 2, false)) {
            return ItemStack.field_190927_a;
         }

         if (stackInSlot.func_190916_E() == 0) {
            slotObject.func_75215_d(ItemStack.field_190927_a);
         } else {
            slotObject.func_75218_e();
         }
      }

      return stack;
   }

   public boolean func_75145_c(EntityPlayer player) {
      return this.tileEntity.func_70300_a(player);
   }
}
