package thaumcraft.common.lib.research.theorycraft;

import thaumcraft.Thaumcraft;
import thaumcraft.api.research.theorycraft.TheorycraftManager;

/**
 * Registers all default theorycraft cards and aids.
 * Called during mod initialization.
 */
public class TheoryRegistry {

    /**
     * Register all default theorycraft cards and aids.
     */
    public static void init() {
        Thaumcraft.LOGGER.info("Registering theorycraft cards and aids");
        
        // ==================== Basic Cards ====================
        // These are available in normal draw rotation
        TheorycraftManager.registerCard(CardAnalyze.class);
        TheorycraftManager.registerCard(CardExperimentation.class);
        TheorycraftManager.registerCard(CardInspired.class);
        TheorycraftManager.registerCard(CardPonder.class);
        TheorycraftManager.registerCard(CardReject.class);
        TheorycraftManager.registerCard(CardRethink.class);
        
        // ==================== Category-Specific Cards ====================
        // Auromancy
        TheorycraftManager.registerCard(CardAwareness.class);
        TheorycraftManager.registerCard(CardFocus.class);
        TheorycraftManager.registerCard(CardChannel.class);
        
        // Alchemy
        TheorycraftManager.registerCard(CardConcentrate.class);
        TheorycraftManager.registerCard(CardReactions.class);
        TheorycraftManager.registerCard(CardSynthesis.class);
        
        // Artifice
        TheorycraftManager.registerCard(CardCalibrate.class);
        TheorycraftManager.registerCard(CardMeasure.class);
        TheorycraftManager.registerCard(CardTinker.class);
        
        // Golemancy
        TheorycraftManager.registerCard(CardMindOverMatter.class);
        TheorycraftManager.registerCard(CardScripting.class);
        TheorycraftManager.registerCard(CardSculpting.class);
        
        // Infusion
        TheorycraftManager.registerCard(CardSpellbinding.class);
        TheorycraftManager.registerCard(CardSynergy.class);
        TheorycraftManager.registerCard(CardInfuse.class);
        
        // Eldritch
        TheorycraftManager.registerCard(CardRevelation.class);
        
        // ==================== Special Cards ====================
        // These have special requirements or effects
        TheorycraftManager.registerCard(CardRealization.class);
        TheorycraftManager.registerCard(CardTruth.class);
        TheorycraftManager.registerCard(CardCurio.class);
        TheorycraftManager.registerCard(CardCelestial.class);
        
        // ==================== Aid-Only Cards ====================
        // These are only added by specific aids
        TheorycraftManager.registerCard(CardBalance.class);
        TheorycraftManager.registerCard(CardNotation.class);
        TheorycraftManager.registerCard(CardStudy.class);
        TheorycraftManager.registerCard(CardDarkWhispers.class);
        TheorycraftManager.registerCard(CardEnchantment.class);
        TheorycraftManager.registerCard(CardGlyphs.class);
        TheorycraftManager.registerCard(CardBeacon.class);
        TheorycraftManager.registerCard(CardDragonEgg.class);
        TheorycraftManager.registerCard(CardPortal.class);
        
        // ==================== Register Aids ====================
        // Bookshelf - basic aid for any research table
        TheorycraftManager.registerAid(new AidBookshelf());
        
        // Enchanting table - grants enchantment cards (cost XP)
        TheorycraftManager.registerAid(new AidEnchantmentTable());
        
        // Beacon - grants powerful auromancy/artifice cards
        TheorycraftManager.registerAid(new AidBeacon());
        
        // Dragon egg - rare, grants powerful eldritch cards with warp
        TheorycraftManager.registerAid(new AidDragonEgg());
        
        // Brain in a jar - grants dark whisper cards (eldritch with warp)
        TheorycraftManager.registerAid(new AidBrainInAJar());
        
        // Glyphed ancient stone - grants glyph cards for eldritch research
        TheorycraftManager.registerAid(new AidGlyphedStone());
        
        // Portals - grant portal cards
        TheorycraftManager.registerAid(new AidPortal.AidPortalEnd());
        TheorycraftManager.registerAid(new AidPortal.AidPortalNether());
        TheorycraftManager.registerAid(new AidPortal.AidPortalCrimson());
        
        // Category-specific aids - grants category cards when near relevant blocks
        TheorycraftManager.registerAid(new AidBasicAlchemy());      // Crucible
        TheorycraftManager.registerAid(new AidBasicArtifice());     // Arcane Workbench
        TheorycraftManager.registerAid(new AidBasicAuromancy());    // Focal Manipulator
        TheorycraftManager.registerAid(new AidBasicGolemancy());    // Golem Builder
        TheorycraftManager.registerAid(new AidBasicInfusion());     // Infusion Matrix
        TheorycraftManager.registerAid(new AidBasicEldritch());     // Eldritch Stone
        
        Thaumcraft.LOGGER.info("Registered {} theorycraft cards and {} aids", 
                TheorycraftManager.cards.size(),
                TheorycraftManager.aids.size());
    }
}
