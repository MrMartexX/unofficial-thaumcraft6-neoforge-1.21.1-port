package thaumcraft.common.research.theorycraft;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thaumcraft.common.registry.TCBlocks;

public final class TCTheorycraftManager {
    public static final String AID_BOOKSHELF = "thaumcraft.api.research.theorycraft.AidBookshelf";
    public static final String AID_GLYPHED_STONE = "thaumcraft.common.lib.research.theorycraft.AidGlyphedStone";
    public static final String AID_PORTAL_END = "thaumcraft.common.lib.research.theorycraft.AidPortal$AidPortalEnd";
    public static final String AID_PORTAL_NETHER = "thaumcraft.common.lib.research.theorycraft.AidPortal$AidPortalNether";
    public static final String AID_BASIC_ALCHEMY = "thaumcraft.common.lib.research.theorycraft.AidBasicAlchemy";
    public static final String AID_BASIC_ARTIFICE = "thaumcraft.common.lib.research.theorycraft.AidBasicArtifice";
    public static final String AID_BASIC_AUROMANCY = "thaumcraft.common.lib.research.theorycraft.AidBasicAuromancy";
    public static final String AID_BASIC_INFUSION = "thaumcraft.common.lib.research.theorycraft.AidBasicInfusion";
    public static final String AID_BASIC_GOLEMANCY = "thaumcraft.common.lib.research.theorycraft.AidBasicGolemancy";
    public static final String AID_BRAIN_IN_A_JAR = "thaumcraft.common.lib.research.theorycraft.AidBrainInAJar";
    public static final String AID_ENCHANTMENT_TABLE = "thaumcraft.common.lib.research.theorycraft.AidEnchantmentTable";
    public static final String AID_BEACON = "thaumcraft.common.lib.research.theorycraft.AidBeacon";
    static final int AID_HORIZONTAL_RADIUS = 4;
    static final int AID_VERTICAL_RADIUS = 1;
    static final double AID_ENTITY_RANGE = 5.0D;

    private static final LinkedHashMap<String, Supplier<? extends TCTheorycraftCard>> CARDS = new LinkedHashMap<>();
    private static final LinkedHashMap<String, TCTheorycraftAid> AIDS = new LinkedHashMap<>();

    private TCTheorycraftManager() {
    }

    public static void bootstrap() {
        CARDS.clear();
        AIDS.clear();
        registerCard("thaumcraft.api.research.theorycraft.CardStudy", CardStudy::new);
        registerCard("thaumcraft.api.research.theorycraft.CardAnalyze", CardAnalyze::new);
        registerCard("thaumcraft.api.research.theorycraft.CardBalance", CardBalance::new);
        registerCard("thaumcraft.api.research.theorycraft.CardNotation", CardNotation::new);
        registerCard("thaumcraft.api.research.theorycraft.CardPonder", CardPonder::new);
        registerCard("thaumcraft.api.research.theorycraft.CardRethink", CardRethink::new);
        registerCard("thaumcraft.api.research.theorycraft.CardReject", CardReject::new);
        registerCard("thaumcraft.api.research.theorycraft.CardExperimentation", CardExperimentation::new);
        registerCard("thaumcraft.api.research.theorycraft.CardInspired", CardInspired::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardMeasure", CardMeasure::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardConcentrate", CardConcentrate::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardReactions", CardReactions::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardSynthesis", CardSynthesis::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardCalibrate", CardCalibrate::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardFocus", CardFocus::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardAwareness", CardAwareness::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardSynergy", CardSynergy::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardEnchantment", CardEnchantment::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardBeacon", CardBeacon::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardSpellbinding", CardSpellbinding::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardChannel", CardChannel::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardInfuse", CardInfuse::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardSculpting", CardSculpting::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardTinker", CardTinker::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardMindOverMatter", CardMindOverMatter::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardScripting", CardScripting::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardDarkWhispers", CardDarkWhispers::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardGlyphs", CardGlyphs::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardPortal", CardPortal::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardRevelation", CardRevelation::new);
        registerCard("thaumcraft.common.lib.research.theorycraft.CardRealization", CardRealization::new);

        registerAid(TCTheorycraftAid.block(
                AID_BOOKSHELF,
                new ItemStack(Blocks.BOOKSHELF),
                state -> state.is(Blocks.BOOKSHELF),
                List.of(
                        "thaumcraft.api.research.theorycraft.CardBalance",
                        "thaumcraft.api.research.theorycraft.CardNotation",
                        "thaumcraft.api.research.theorycraft.CardNotation",
                        "thaumcraft.api.research.theorycraft.CardStudy",
                        "thaumcraft.api.research.theorycraft.CardStudy",
                        "thaumcraft.api.research.theorycraft.CardStudy"
                )
        ));
        registerAid(TCTheorycraftAid.block(
                AID_GLYPHED_STONE,
                () -> new ItemStack(TCBlocks.STONE_ANCIENT_GLYPHED.get()),
                state -> state.is(TCBlocks.STONE_ANCIENT_GLYPHED.get()),
                List.of("thaumcraft.common.lib.research.theorycraft.CardGlyphs")
        ));
        registerAid(TCTheorycraftAid.block(
                AID_PORTAL_END,
                new ItemStack(Items.ENDER_EYE),
                state -> state.is(Blocks.END_PORTAL),
                List.of("thaumcraft.common.lib.research.theorycraft.CardPortal")
        ));
        registerAid(TCTheorycraftAid.block(
                AID_PORTAL_NETHER,
                new ItemStack(Items.FLINT_AND_STEEL),
                state -> state.is(Blocks.NETHER_PORTAL),
                List.of("thaumcraft.common.lib.research.theorycraft.CardPortal")
        ));
        registerAid(TCTheorycraftAid.block(
                AID_BASIC_ALCHEMY,
                () -> new ItemStack(TCBlocks.CRUCIBLE.get()),
                state -> state.is(TCBlocks.CRUCIBLE.get()),
                List.of(
                        "thaumcraft.common.lib.research.theorycraft.CardConcentrate",
                        "thaumcraft.common.lib.research.theorycraft.CardReactions",
                        "thaumcraft.common.lib.research.theorycraft.CardSynthesis"
                )
        ));
        registerAid(TCTheorycraftAid.block(
                AID_BASIC_ARTIFICE,
                () -> new ItemStack(TCBlocks.ARCANE_WORKBENCH.get()),
                state -> state.is(TCBlocks.ARCANE_WORKBENCH.get()),
                List.of(
                        "thaumcraft.common.lib.research.theorycraft.CardCalibrate",
                        "thaumcraft.common.lib.research.theorycraft.CardTinker",
                        "thaumcraft.common.lib.research.theorycraft.CardMindOverMatter"
                )
        ));
        registerAid(TCTheorycraftAid.block(
                AID_BASIC_INFUSION,
                () -> new ItemStack(TCBlocks.INFUSION_MATRIX.get()),
                state -> state.is(TCBlocks.INFUSION_MATRIX.get()),
                List.of(
                        "thaumcraft.common.lib.research.theorycraft.CardMeasure",
                        "thaumcraft.common.lib.research.theorycraft.CardChannel",
                        "thaumcraft.common.lib.research.theorycraft.CardInfuse"
                )
        ));
        registerAid(TCTheorycraftAid.block(
                AID_BASIC_AUROMANCY,
                () -> new ItemStack(TCBlocks.WAND_WORKBENCH.get()),
                state -> state.is(TCBlocks.WAND_WORKBENCH.get()),
                List.of(
                        "thaumcraft.common.lib.research.theorycraft.CardFocus",
                        "thaumcraft.common.lib.research.theorycraft.CardAwareness",
                        "thaumcraft.common.lib.research.theorycraft.CardSpellbinding"
                )
        ));
        registerAid(TCTheorycraftAid.block(
                AID_BASIC_GOLEMANCY,
                () -> new ItemStack(TCBlocks.GOLEM_BUILDER.get()),
                state -> state.is(TCBlocks.GOLEM_BUILDER.get()),
                List.of(
                        "thaumcraft.common.lib.research.theorycraft.CardSculpting",
                        "thaumcraft.common.lib.research.theorycraft.CardScripting",
                        "thaumcraft.common.lib.research.theorycraft.CardSynergy"
                )
        ));
        registerAid(TCTheorycraftAid.block(
                AID_BRAIN_IN_A_JAR,
                () -> new ItemStack(TCBlocks.JAR_BRAIN.get()),
                state -> state.is(TCBlocks.JAR_BRAIN.get()),
                List.of("thaumcraft.common.lib.research.theorycraft.CardDarkWhispers")
        ));
        registerAid(TCTheorycraftAid.block(
                AID_ENCHANTMENT_TABLE,
                new ItemStack(Blocks.ENCHANTING_TABLE),
                state -> state.is(Blocks.ENCHANTING_TABLE),
                List.of("thaumcraft.common.lib.research.theorycraft.CardEnchantment")
        ));
        registerAid(TCTheorycraftAid.block(
                AID_BEACON,
                new ItemStack(Blocks.BEACON),
                state -> state.is(Blocks.BEACON),
                List.of("thaumcraft.common.lib.research.theorycraft.CardBeacon")
        ));
    }

    public static void registerCard(String legacyKey, Supplier<? extends TCTheorycraftCard> factory) {
        if (legacyKey == null || legacyKey.isBlank() || factory == null) {
            return;
        }
        CARDS.put(legacyKey, factory);
    }

    public static void registerAid(TCTheorycraftAid aid) {
        if (aid == null || aid.legacyKey() == null || aid.legacyKey().isBlank() || AIDS.containsKey(aid.legacyKey())) {
            return;
        }
        AIDS.put(aid.legacyKey(), aid);
    }

    public static Map<String, TCTheorycraftAid> aids() {
        return AIDS;
    }

    public static LinkedHashSet<String> collectNearbyAidKeys(Level level, BlockPos tablePos) {
        LinkedHashSet<String> keys = new LinkedHashSet<>();
        if (level == null || tablePos == null || AIDS.isEmpty()) {
            return keys;
        }

        for (int y = -AID_VERTICAL_RADIUS; y <= AID_VERTICAL_RADIUS; y++) {
            for (int x = -AID_HORIZONTAL_RADIUS; x <= AID_HORIZONTAL_RADIUS; x++) {
                for (int z = -AID_HORIZONTAL_RADIUS; z <= AID_HORIZONTAL_RADIUS; z++) {
                    BlockPos pos = tablePos.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    for (TCTheorycraftAid aid : AIDS.values()) {
                        if (aid.matchesBlock(state)) {
                            keys.add(aid.legacyKey());
                        }
                    }
                }
            }
        }

        AABB box = new AABB(tablePos).inflate(AID_ENTITY_RANGE);
        for (Entity entity : level.getEntities((Entity) null, box, entity -> true)) {
            for (TCTheorycraftAid aid : AIDS.values()) {
                if (aid.matchesEntity(entity)) {
                    keys.add(aid.legacyKey());
                }
            }
        }
        return keys;
    }

    static Map<String, Supplier<? extends TCTheorycraftCard>> cards() {
        return CARDS;
    }

    static TCTheorycraftCard create(String legacyKey) {
        Supplier<? extends TCTheorycraftCard> factory = CARDS.get(legacyKey);
        return factory == null ? null : factory.get();
    }
}
