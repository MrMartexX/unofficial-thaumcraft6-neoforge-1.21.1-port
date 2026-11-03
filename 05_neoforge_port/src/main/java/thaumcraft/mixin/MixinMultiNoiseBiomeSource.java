package thaumcraft.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.common.registry.TCBiomes;
import thaumcraft.common.world.biome.TCTerraBlenderCompat;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MixinMultiNoiseBiomeSource {
    @Shadow
    protected abstract Climate.ParameterList<Holder<Biome>> parameters();

    @WrapMethod(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;")
    private Holder<Biome> thaumcraft$filterMagicalForestShape(
            int quartX,
            int quartY,
            int quartZ,
            Climate.Sampler sampler,
            Operation<Holder<Biome>> original) {
        Holder<Biome> result = original.call(quartX, quartY, quartZ, sampler);
        if (result != null && result.is(TCBiomes.MAGICAL_FOREST) && TCTerraBlenderCompat.isEnabled()) {
            return TCTerraBlenderCompat.filterMagicalForest(this.parameters(), quartX, quartY, quartZ, sampler, result);
        }
        return result;
    }
}
