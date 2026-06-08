package thaumcraft.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.tiles.crafting.TCArcaneWorkbenchBlockEntity;
import thaumcraft.common.tiles.crafting.TCResearchTableBlockEntity;
import thaumcraft.common.tiles.misc.TCNitorBlockEntity;

public final class TCBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Thaumcraft.MODID);

    public static final Supplier<BlockEntityType<TCResearchTableBlockEntity>> RESEARCH_TABLE =
            BLOCK_ENTITY_TYPES.register("research_table", () ->
                    BlockEntityType.Builder.of(TCResearchTableBlockEntity::new, TCBlocks.RESEARCH_TABLE.get()).build(null));
    public static final Supplier<BlockEntityType<TCArcaneWorkbenchBlockEntity>> ARCANE_WORKBENCH =
            BLOCK_ENTITY_TYPES.register("arcane_workbench", () ->
                    BlockEntityType.Builder.of(TCArcaneWorkbenchBlockEntity::new, TCBlocks.ARCANE_WORKBENCH.get()).build(null));

    public static final Supplier<BlockEntityType<TCNitorBlockEntity>> NITOR =
            BLOCK_ENTITY_TYPES.register("nitor", () ->
                    BlockEntityType.Builder.of(TCNitorBlockEntity::new, TCBlocks.NITOR_YELLOW.get()).build(null));
    private TCBlockEntities() {
    }
}
