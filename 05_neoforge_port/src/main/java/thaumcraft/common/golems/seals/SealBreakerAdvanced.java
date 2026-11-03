package thaumcraft.common.golems.seals;

import net.minecraft.resources.ResourceLocation;

public class SealBreakerAdvanced extends SealBreaker {
    @Override
    public thaumcraft.api.golems.EnumGolemTrait[] getRequiredTags() {
        return new thaumcraft.api.golems.EnumGolemTrait[]{thaumcraft.api.golems.EnumGolemTrait.BREAKER, thaumcraft.api.golems.EnumGolemTrait.SMART};
    }

    @Override
    public String getKey() {
        return "thaumcraft:breaker_advanced";
    }

    @Override
    public int getFilterSize() {
        return 9;
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_breaker_advanced.png");
    }
}
