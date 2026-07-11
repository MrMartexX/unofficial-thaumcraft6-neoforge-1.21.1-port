package thaumcraft.common.blocks.world.taint;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;

/** Common TC6 taint terrain behavior for crust, soil, rock and geyser blocks. */
public final class TCTaintTerrainBlock extends Block {
    public static final MapCodec<TCTaintTerrainBlock> CODEC = simpleCodec(properties -> new TCTaintTerrainBlock(properties, Kind.CRUST));
    private final Kind kind;

    public TCTaintTerrainBlock(BlockBehaviour.Properties properties, Kind kind) {
        super(properties);
        this.kind = kind;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public Kind kind() {
        return kind;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!TCTaintHelper.isNearTaintSeed(level, pos) && random.nextInt(10) == 0) {
            die(level, pos, state);
            return;
        }
        if (kind == Kind.ROCK) {
            TCTaintHelper.spreadFibres(level, pos);
        } else if (kind == Kind.GEYSER && AuraHelper.getFlux(level, pos) < 2.0F) {
            AuraHelper.polluteAura(level, pos, 0.25F, true);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide
                && entity instanceof LivingEntity living
                && level.getRandom().nextInt(250) == 0) {
            TCTaintFibreBlock.applyWalkTaint(living);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        if (kind == Kind.ROCK && level.getRandom().nextInt(15) == 0) {
            popResource(level, pos, new ItemStack(TCItems.CRYSTAL_ESSENCE_VITIUM.get()));
        }
        super.spawnAfterBreak(state, level, pos, tool, dropExperience);
    }

    public void die(Level level, BlockPos pos, BlockState state) {
        switch (kind) {
            case ROCK -> level.setBlock(pos, TCBlocks.STONE_POROUS.get().defaultBlockState(), Block.UPDATE_ALL);
            case SOIL -> level.setBlock(pos, net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState(), Block.UPDATE_ALL);
            case CRUST, GEYSER -> level.setBlock(pos, TCBlocks.FLUX_GOO.get().defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    public enum Kind {
        CRUST,
        SOIL,
        ROCK,
        GEYSER
    }
}
