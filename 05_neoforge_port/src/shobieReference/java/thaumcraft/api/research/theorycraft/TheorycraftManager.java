package thaumcraft.api.research.theorycraft;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for theorycraft aids and cards.
 * 
 * Addons can register their own cards and aids using this class to extend
 * the research table gameplay.
 */
public class TheorycraftManager {
    
    /**
     * Registry of all theorycraft aids, keyed by class name.
     */
    public static final Map<String, ITheorycraftAid> aids = new HashMap<>();
    
    /**
     * Registry of all theorycraft card classes, keyed by class name.
     */
    public static final Map<String, Class<? extends TheorycraftCard>> cards = new HashMap<>();
    
    /**
     * Register a theorycraft aid.
     * Aids add bonus cards to the draw rotation when their trigger object is near the research table.
     * @param aid The aid to register
     */
    public static void registerAid(ITheorycraftAid aid) {
        String key = aid.getClass().getName();
        if (!aids.containsKey(key)) {
            aids.put(key, aid);
        }
    }
    
    /**
     * Register a theorycraft card class.
     * @param cardClass The card class to register
     */
    public static void registerCard(Class<? extends TheorycraftCard> cardClass) {
        String key = cardClass.getName();
        if (!cards.containsKey(key)) {
            cards.put(key, cardClass);
        }
    }
    
    /**
     * Get a registered card class by its key (class name).
     * @param key The class name key
     * @return The card class, or null if not found
     */
    public static Class<? extends TheorycraftCard> getCardClass(String key) {
        return cards.get(key);
    }
    
    /**
     * Get a registered aid by its key (class name).
     * @param key The class name key
     * @return The aid instance, or null if not found
     */
    public static ITheorycraftAid getAid(String key) {
        return aids.get(key);
    }
    
    /**
     * Create a new instance of a card from its class key.
     * @param key The class name key
     * @return A new card instance, or null if creation failed
     */
    public static TheorycraftCard createCard(String key) {
        Class<? extends TheorycraftCard> cardClass = cards.get(key);
        if (cardClass == null) return null;
        try {
            return cardClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
