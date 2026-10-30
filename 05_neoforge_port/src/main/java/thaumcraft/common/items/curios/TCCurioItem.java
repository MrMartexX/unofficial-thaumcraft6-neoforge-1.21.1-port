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
import thaumcraft.common.research.TCResearchCategoryDefinition;
import thaumcraft.common.research.TCResearchManager;
import thaumcraft.common.warp.TCWarpManager;
import thaumcraft.common.warp.TCWarpType;

public class TCCurioItem extends Item {
    public enum Kind {
        ARCANE("arcane", "AUROMANCY", 0),
        PRESERVED("preserved", "ALCHEMY", 1),
        ANCIENT("ancient", "GOLEMANCY", 2),
        ELDRITCH("eldritch", "ELDRITCH", 3),
        KNOWLEDGE("knowledge", "INFUSION", 4),
        TWISTED("twisted", "ARTIFICE", 5);

        public final String name;
        public final String category;
        public final int meta;

        Kind(String name, String category, int meta) {
            this.name = name;
            this.category = category;
            this.meta = meta;
        }
    }

    private final Kind kind;

    public TCCurioItem(Kind kind) {
        super(new Item.Properties()
                .stacksTo(64)
                .rarity(kind == Kind.ELDRITCH ? Rarity.RARE : Rarity.UNCOMMON)
                .component(TCDataComponents.LEGACY_ITEM.get(), new TCLegacyItemComponent("curio", kind.name, kind.meta)));
        this.kind = kind;
    }

    public Kind getKind() {
        return this.kind;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                TCSounds.LEARN.get(), SoundSource.NEUTRAL, 0.5F, 0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        int observationProgression = TCKnowledgeType.OBSERVATION.rawUnitsPerPoint();
        int theoryProgression = TCKnowledgeType.THEORY.rawUnitsPerPoint();

        // 1. Primary category knowledge
        addRandomKnowledge(serverPlayer, TCKnowledgeType.OBSERVATION, this.kind.category, observationProgression / 2, observationProgression);
        if (this.kind != Kind.ARCANE) { // Legacy: arcane was observation only on primary, theory on secondary
            addRandomKnowledge(serverPlayer, TCKnowledgeType.THEORY, this.kind.category, theoryProgression / 3, theoryProgression / 2);
        }

        // 2. Secondary random category knowledge
        List<String> categories = new ArrayList<>(TCResearchManager.categories().stream()
                .map(TCResearchCategoryDefinition::key)
                .toList());
        if (!categories.isEmpty()) {
            addRandomKnowledge(serverPlayer, TCKnowledgeType.OBSERVATION, randomCategory(serverPlayer, categories), observationProgression / 2, observationProgression);
            addRandomKnowledge(serverPlayer, TCKnowledgeType.THEORY, randomCategory(serverPlayer, categories), theoryProgression / 3, theoryProgression / 2);
        }

        // 3. Eldritch warp
        if (this.kind == Kind.ELDRITCH) {
            TCWarpManager.add(serverPlayer, TCWarpType.NORMAL, 1);
            TCWarpManager.add(serverPlayer, TCWarpType.TEMPORARY, 5);
        }

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

    private static void addRandomKnowledge(ServerPlayer player, TCKnowledgeType type, String category, int minRaw, int maxRaw) {
        TCResearchManager.addKnowledgeRaw(player, type, category, Mth.nextInt(player.getRandom(), Math.max(1, minRaw), Math.max(1, maxRaw)));
    }

    private static String randomCategory(ServerPlayer player, List<String> categories) {
        return categories.get(player.getRandom().nextInt(categories.size()));
    }
}
