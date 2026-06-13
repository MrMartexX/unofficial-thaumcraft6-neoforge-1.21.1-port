package thaumcraft.common.lib.research.theorycraft;

import net.minecraft.world.level.block.Blocks;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;

/**
 * Portal aid - adds portal cards when near various portal types.
 */
public class AidPortal implements ITheorycraftAid {
    
    private final Object portal;
    
    public AidPortal(Object o) {
        this.portal = o;
    }

    @Override
    public Object getAidObject() {
        return portal;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends TheorycraftCard>[] getCards() {
        return new Class[] { CardPortal.class };
    }
    
    /**
     * End portal variant
     */
    public static class AidPortalEnd extends AidPortal {
        public AidPortalEnd() {
            super(Blocks.END_PORTAL);
        }
    }
    
    /**
     * Nether portal variant
     */
    public static class AidPortalNether extends AidPortal {
        public AidPortalNether() {
            super(Blocks.NETHER_PORTAL);
        }
    }
    
    /**
     * Crimson cult portal variant
     */
    public static class AidPortalCrimson extends AidPortal {
        public AidPortalCrimson() {
            super(EntityCultistPortalLesser.class);
        }
    }
}
