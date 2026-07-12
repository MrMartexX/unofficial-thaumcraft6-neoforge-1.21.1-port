package thaumcraft.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.minecraft.sounds.SoundEvents;
import thaumcraft.Thaumcraft;

public final class TCFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Thaumcraft.MODID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, Thaumcraft.MODID);

    public static final DeferredHolder<FluidType, FluidType> LIQUID_DEATH_TYPE =
            FLUID_TYPES.register("liquid_death", () -> new FluidType(waterLikeProperties()
                    .descriptionId("fluid_type.thaumcraft.liquid_death")
                    .rarity(Rarity.UNCOMMON)
                    .viscosity(1300)));
    public static final DeferredHolder<FluidType, FluidType> PURIFYING_FLUID_TYPE =
            FLUID_TYPES.register("purifying_fluid", () -> new FluidType(waterLikeProperties()
                    .descriptionId("fluid_type.thaumcraft.purifying_fluid")
                    .rarity(Rarity.UNCOMMON)
                    .viscosity(1100)));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> LIQUID_DEATH =
            FLUIDS.register("liquid_death", () -> new BaseFlowingFluid.Source(liquidDeathProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_LIQUID_DEATH =
            FLUIDS.register("flowing_liquid_death", () -> new BaseFlowingFluid.Flowing(liquidDeathProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> PURIFYING_FLUID =
            FLUIDS.register("purifying_fluid", () -> new BaseFlowingFluid.Source(purifyingFluidProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_PURIFYING_FLUID =
            FLUIDS.register("flowing_purifying_fluid", () -> new BaseFlowingFluid.Flowing(purifyingFluidProperties()));

    private TCFluids() {
    }

    private static FluidType.Properties waterLikeProperties() {
        return FluidType.Properties.create()
                .canSwim(true)
                .canDrown(true)
                .canPushEntity(true)
                .canExtinguish(false)
                .supportsBoating(false)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
    }

    private static BaseFlowingFluid.Properties liquidDeathProperties() {
        return new BaseFlowingFluid.Properties(LIQUID_DEATH_TYPE, LIQUID_DEATH, FLOWING_LIQUID_DEATH)
                .bucket(TCItems.LIQUID_DEATH_BUCKET)
                .block(TCBlocks.LIQUID_DEATH)
                .slopeFindDistance(4)
                .levelDecreasePerBlock(2)
                .tickRate(5)
                .explosionResistance(100.0F);
    }

    private static BaseFlowingFluid.Properties purifyingFluidProperties() {
        return new BaseFlowingFluid.Properties(PURIFYING_FLUID_TYPE, PURIFYING_FLUID, FLOWING_PURIFYING_FLUID)
                .bucket(TCItems.PURIFYING_FLUID_BUCKET)
                .block(TCBlocks.PURIFYING_FLUID)
                .slopeFindDistance(4)
                .levelDecreasePerBlock(1)
                .tickRate(5)
                .explosionResistance(100.0F);
    }
}
