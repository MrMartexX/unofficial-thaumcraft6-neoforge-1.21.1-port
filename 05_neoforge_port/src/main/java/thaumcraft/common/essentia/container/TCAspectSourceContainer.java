package thaumcraft.common.essentia.container;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/**
 * Modern server-side boundary for legacy {@code IAspectSource} containers.
 *
 * <p>Infusion source discovery must target stable containers such as warded jars,
 * not transient tube transport buffers.</p>
 */
public interface TCAspectSourceContainer {
    boolean isSourceBlocked();

    AspectList storedAspects();

    int drainAspect(Aspect aspect, int amount, boolean simulate);
}
