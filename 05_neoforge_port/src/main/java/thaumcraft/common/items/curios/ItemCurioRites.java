package thaumcraft.common.items.curios;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import thaumcraft.common.items.components.TCLegacyItemComponent;
import thaumcraft.common.registry.TCDataComponents;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.research.TCKnowledgeType;
import thaumcraft.common.research.TCPlayerKnowledgeStore;
import thaumcraft.common.research.TCResearchCategoryDefinition;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.warp.TCWarpManager;
import thaumcraft.common.warp.TCWarpType;

public class ItemCurioRites extends Item {
    private static final String RESEARCH_KEY = "CrimsonRites";
    private static final String ELDRITCH_CATEGORY = "ELDRITCH";

    public ItemCurioRites() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON)
                .component(TCDataComponents.LEGACY_ITEM.get(), new TCLegacyItemComponent("curio", "rites", 6)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), TCSounds.LEARN.get(), SoundSource.NEUTRAL, 0.5F, 0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        if (!canReadCrimsonRites(serverPlayer)) {
            player.displayClientMessage(Component.translatable("fail.crimsonrites").withStyle(ChatFormatting.DARK_PURPLE), false);
            return InteractionResultHolder.success(stack);
        }

        if (!TCResearchManager.isResearchComplete(TCPlayerKnowledgeStore.get(serverPlayer), RESEARCH_KEY)) {
            TCResearchManager.completeResearch(serverPlayer, RESEARCH_KEY);
        }
        grantLegacyCurioRitesKnowledge(serverPlayer);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        player.displayClientMessage(Component.translatable("tc.knowledge.gained").withStyle(ChatFormatting.DARK_PURPLE), false);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.curio.text").withStyle(ChatFormatting.DARK_PURPLE));
    }

    public static boolean canReadCrimsonRites(ServerPlayer player) {
        return TCWarpManager.get(player).actualWarp() > 20;
    }

    public static void grantLegacyCurioRitesKnowledge(ServerPlayer player) {
        int observationProgression = TCKnowledgeType.OBSERVATION.rawUnitsPerPoint();
        int theoryProgression = TCKnowledgeType.THEORY.rawUnitsPerPoint();
        addRandomKnowledge(player, TCKnowledgeType.OBSERVATION, ELDRITCH_CATEGORY, observationProgression / 2, observationProgression);
        addRandomKnowledge(player, TCKnowledgeType.THEORY, ELDRITCH_CATEGORY, theoryProgression / 3, theoryProgression / 2);

        List<String> categories = new ArrayList<>(TCResearchManager.categories().stream()
                .map(TCResearchCategoryDefinition::key)
                .toList());
        if (!categories.isEmpty()) {
            addRandomKnowledge(player, TCKnowledgeType.OBSERVATION, randomCategory(player, categories), observationProgression / 2, observationProgression);
            addRandomKnowledge(player, TCKnowledgeType.THEORY, randomCategory(player, categories), theoryProgression / 3, theoryProgression / 2);
        }

        TCWarpManager.add(player, TCWarpType.NORMAL, 1);
        TCWarpManager.add(player, TCWarpType.TEMPORARY, 5);
        if (player.getRandom().nextBoolean()) {
            TCWarpManager.add(player, TCWarpType.PERMANENT, 1);
        }
    }

    private static void addRandomKnowledge(ServerPlayer player, TCKnowledgeType type, String category, int minRaw, int maxRaw) {
        TCResearchManager.addKnowledgeRaw(player, type, category, Mth.nextInt(player.getRandom(), Math.max(1, minRaw), Math.max(1, maxRaw)));
    }

    private static String randomCategory(ServerPlayer player, List<String> categories) {
        return categories.get(player.getRandom().nextInt(categories.size()));
    }
}
