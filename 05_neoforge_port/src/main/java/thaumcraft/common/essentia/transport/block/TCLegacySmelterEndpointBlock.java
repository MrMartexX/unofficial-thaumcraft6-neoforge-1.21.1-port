package thaumcraft.common.essentia.transport.block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.registry.TCBlockEntities;

import javax.annotation.Nullable;

public class TCLegacySmelterEndpointBlock extends Block implements EntityBlock {
    
    public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");
private final TCLegacySmelterEndpoint endpoint;

    public TCLegacySmelterEndpointBlock(TCLegacySmelterEndpoint endpoint, BlockBehaviour.Properties properties) {
        super(properties);
        this.endpoint = endpoint;
        this.registerDefaultState(this.stateDefinition.any().setValue(ENABLED, Boolean.FALSE));
    }

    public TCLegacySmelterEndpoint endpoint() {
        return endpoint;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TCBlockEntities.createSmelterEndpointBlockEntity(endpoint, pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ENABLED);
    }}