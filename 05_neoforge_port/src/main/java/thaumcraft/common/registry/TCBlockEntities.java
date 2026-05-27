package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;

public final class TCBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Thaumcraft.MODID);

    public static final Supplier<BlockEntityType<TCResearchTableBlockEntity>> RESEARCH_TABLE =
            BLOCK_ENTITY_TYPES.register("research_table", () ->
                    BlockEntityType.Builder.of(TCResearchTableBlockEntity::new, TCBlocks.RESEARCH_TABLE.get()).build(null));

    private TCBlockEntities() {
    }
}
