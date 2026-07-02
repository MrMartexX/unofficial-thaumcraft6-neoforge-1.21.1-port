package thaumcraft.common.essentia.transport;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;
import thaumcraft.Thaumcraft;
import thaumcraft.common.registry.TCBlockEntities;

/** NeoForge-facing sided capability for the legacy-shaped internal transport contract. */
public final class TCEssentiaCapabilities {
    public static final BlockCapability<TCEssentiaTransport, @Nullable Direction> BLOCK =
            BlockCapability.createSided(
                    ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, "essentia_transport"),
                    TCEssentiaTransport.class
            );

    private TCEssentiaCapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(BLOCK, TCBlockEntities.TUBE.get(), TCEssentiaCapabilities::forSide);
        event.registerBlockEntity(BLOCK, TCBlockEntities.TUBE_BUFFER.get(), TCEssentiaCapabilities::forSide);
        event.registerBlockEntity(BLOCK, TCBlockEntities.TUBE_FILTER.get(), TCEssentiaCapabilities::forSide);
        event.registerBlockEntity(BLOCK, TCBlockEntities.TUBE_ONEWAY.get(), TCEssentiaCapabilities::forSide);
        event.registerBlockEntity(BLOCK, TCBlockEntities.TUBE_RESTRICT.get(), TCEssentiaCapabilities::forSide);
        event.registerBlockEntity(BLOCK, TCBlockEntities.TUBE_VALVE.get(), TCEssentiaCapabilities::forSide);
        event.registerBlockEntity(BLOCK, TCBlockEntities.WARDED_JAR.get(), TCEssentiaCapabilities::forSide);
        event.registerBlockEntity(BLOCK, TCBlockEntities.JAR_VOID.get(), TCEssentiaCapabilities::forSide);
        event.registerBlockEntity(BLOCK, TCBlockEntities.ALEMBIC.get(), TCEssentiaCapabilities::forSide);
        event.registerBlockEntity(BLOCK, TCBlockEntities.ESSENTIA_TRANSPORT_IN.get(), TCEssentiaCapabilities::forSide);
        event.registerBlockEntity(BLOCK, TCBlockEntities.ESSENTIA_TRANSPORT_OUT.get(), TCEssentiaCapabilities::forSide);
        event.registerBlockEntity(BLOCK, TCBlockEntities.THAUMATORIUM.get(), TCEssentiaCapabilities::forSide);
        event.registerBlockEntity(BLOCK, TCBlockEntities.THAUMATORIUM_TOP.get(), TCEssentiaCapabilities::forSide);
        event.registerBlockEntity(BLOCK, TCBlockEntities.LAMP.get(), TCEssentiaCapabilities::forSide);
    }

    private static TCEssentiaTransport forSide(TCEssentiaTransport transport, @Nullable Direction side) {
        return side == null || transport.isConnectable(side) ? transport : null;
    }
}

