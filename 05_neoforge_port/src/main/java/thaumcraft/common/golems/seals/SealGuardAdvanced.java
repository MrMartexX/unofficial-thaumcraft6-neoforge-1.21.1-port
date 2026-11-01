package thaumcraft.common.golems.seals;

import net.minecraft.resources.ResourceLocation;

public class SealGuardAdvanced extends SealGuard {

    @Override
    public String getKey() {
        return "thaumcraft:guard_advanced";
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_guard_advanced.png");
    }
}
