package thaumcraft.api.research;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;
import thaumcraft.api.capabilities.IPlayerKnowledge;

public class ResearchEvent extends Event {
   private final EntityPlayer player;

   public ResearchEvent(EntityPlayer player) {
      this.player = player;
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }

   public boolean isCancelable() {
      return true;
   }

   public static class Knowledge extends ResearchEvent {
      private final IPlayerKnowledge.EnumKnowledgeType type;
      private final ResearchCategory category;
      private final int amount;

      public Knowledge(EntityPlayer player, IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int amount) {
         super(player);
         this.type = type;
         this.category = category;
         this.amount = amount;
      }

      public IPlayerKnowledge.EnumKnowledgeType getType() {
         return this.type;
      }

      public ResearchCategory getCategory() {
         return this.category;
      }

      public int getAmount() {
         return this.amount;
      }
   }

   public static class Research extends ResearchEvent {
      private final String researchKey;

      public Research(EntityPlayer player, String researchKey) {
         super(player);
         this.researchKey = researchKey;
      }

      public String getResearchKey() {
         return this.researchKey;
      }
   }
}
