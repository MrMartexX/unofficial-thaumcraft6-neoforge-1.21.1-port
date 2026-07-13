package thaumcraft.common.items.curios;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCKnowledgeType;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.research.TCResearchManager;

public class ItemPechWand extends Item {
    public static final String BASE_RESEARCH = "BASEAUROMANCY";
    public static final String FOCUS_RESEARCH = "FOCUSPECH";

    public ItemPechWand() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        if (!TCResearchManager.isResearchComplete(TCPlayerKnowledgeStore.get(serverPlayer), BASE_RESEARCH)) {
            player.displayClientMessage(Component.translatable("not.pechwand").withStyle(ChatFormatting.RED), false);
            return InteractionResultHolder.success(stack);
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                TCSounds.LEARN.get(),
                SoundSource.NEUTRAL,
                0.5F,
                0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (!TCResearchManager.isResearchKnown(TCPlayerKnowledgeStore.get(serverPlayer), FOCUS_RESEARCH)) {
            TCResearchManager.progressResearch(serverPlayer, FOCUS_RESEARCH);
            player.displayClientMessage(Component.translatable("got.pechwand").withStyle(ChatFormatting.DARK_PURPLE), false);
        }
        grantLegacyKnowledge(serverPlayer);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.curio.text").withStyle(ChatFormatting.DARK_PURPLE));
    }

    public static void grantLegacyKnowledge(ServerPlayer player) {
        List<String> categories = TCResearchManager.categoryKeys();
        if (categories.isEmpty()) {
            return;
        }
        int observationProgression = TCKnowledgeType.OBSERVATION.rawUnitsPerPoint();
        int theoryProgression = TCKnowledgeType.THEORY.rawUnitsPerPoint();
        TCResearchManager.addKnowledgeRaw(
                player,
                TCKnowledgeType.OBSERVATION,
                randomCategory(player, categories),
                Mth.nextInt(player.getRandom(), observationProgression / 3, observationProgression / 2)
        );
        TCResearchManager.addKnowledgeRaw(
                player,
                TCKnowledgeType.THEORY,
                randomCategory(player, categories),
                Mth.nextInt(player.getRandom(), theoryProgression / 5, theoryProgression / 4)
        );
    }

    private static String randomCategory(ServerPlayer player, List<String> categories) {
        return categories.get(player.getRandom().nextInt(categories.size()));
    }
}
