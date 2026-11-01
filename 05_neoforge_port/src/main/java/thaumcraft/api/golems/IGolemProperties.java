package thaumcraft.api.golems;

import java.util.Set;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.parts.GolemAddon;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.GolemLeg;
import thaumcraft.api.golems.parts.GolemMaterial;

public interface IGolemProperties {
    Set<EnumGolemTrait> getTraits();

    boolean hasTrait(EnumGolemTrait tag);

    long toLong();

    ItemStack[] generateComponents();

    void setMaterial(GolemMaterial mat);

    GolemMaterial getMaterial();

    void setHead(GolemHead head);

    GolemHead getHead();

    void setArms(GolemArm arm);

    GolemArm getArms();

    void setLegs(GolemLeg leg);

    GolemLeg getLegs();

    void setAddon(GolemAddon addon);

    GolemAddon getAddon();

    void setRank(int r);

    int getRank();
}
