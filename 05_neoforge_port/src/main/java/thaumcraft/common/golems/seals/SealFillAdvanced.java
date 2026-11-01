package thaumcraft.common.golems.seals;

import net.minecraft.resources.ResourceLocation;

public class SealFillAdvanced extends SealFill {

    @Override
    public String getKey() {
        return "thaumcraft:fill_advanced";
    }

    @Override
    public int getFilterSize() {
        return 9;
    }

    @Override
    public ResourceLocation getSealIcon() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/seals/seal_fill_advanced.png");
    }
}
