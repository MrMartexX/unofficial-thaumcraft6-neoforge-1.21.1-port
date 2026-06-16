package thaumcraft.common.essentia.transport.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Common legacy-style tube block shell.
 *
 * Registry wiring is intentionally separate from this shell so the migration can attach existing
 * placeholder ids to real blocks incrementally without changing transport logic.
 */
public class TCLegacyTubeBlock extends Block {
    private final TCLegacyTubeVariant variant;

    public TCLegacyTubeBlock(TCLegacyTubeVariant variant, BlockBehaviour.Properties properties) {
        super(properties);
        this.variant = variant;
    }

    public TCLegacyTubeVariant variant() {
        return variant;
    }
}