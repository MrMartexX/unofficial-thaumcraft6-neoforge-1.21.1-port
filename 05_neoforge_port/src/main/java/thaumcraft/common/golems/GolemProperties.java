package thaumcraft.common.golems;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.parts.GolemAddon;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.GolemLeg;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.api.golems.parts.PartModel;
import thaumcraft.common.registry.TCBlocks;
import thaumcraft.common.registry.TCItems;

public class GolemProperties implements IGolemProperties {
    private long data;
    private Set<EnumGolemTrait> traitCache;

    public GolemProperties() {
        this.data = 0L;
        this.traitCache = null;
    }

    public static long setByteInLong(long data, byte b, int index) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(0, data);
        bb.put(index, b);
        return bb.getLong(0);
    }

    public static byte getByteInLong(long data, int index) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.putLong(0, data);
        return bb.get(index);
    }

    @Override
    public Set<EnumGolemTrait> getTraits() {
        if (traitCache == null) {
            traitCache = new HashSet<>();
            for (EnumGolemTrait trait : getMaterial().traits) {
                addTraitSmart(trait);
            }
            for (EnumGolemTrait trait : getHead().traits) {
                addTraitSmart(trait);
            }
            for (EnumGolemTrait trait : getArms().traits) {
                addTraitSmart(trait);
            }
            for (EnumGolemTrait trait : getLegs().traits) {
                addTraitSmart(trait);
            }
            for (EnumGolemTrait trait : getAddon().traits) {
                addTraitSmart(trait);
            }
        }
        return traitCache;
    }

    private void addTraitSmart(EnumGolemTrait trait) {
        if (trait.opposite != null && traitCache.contains(trait.opposite)) {
            traitCache.remove(trait.opposite);
        } else {
            traitCache.add(trait);
        }
    }

    @Override
    public boolean hasTrait(EnumGolemTrait trait) {
        return getTraits().contains(trait);
    }

    @Override
    public void setMaterial(GolemMaterial mat) {
        data = setByteInLong(data, mat.id, 0);
        traitCache = null;
    }

    @Override
    public GolemMaterial getMaterial() {
        int id = getByteInLong(data, 0);
        GolemMaterial[] mats = GolemMaterial.getMaterials();
        return id >= 0 && id < mats.length && mats[id] != null ? mats[id] : mats[0];
    }

    @Override
    public void setHead(GolemHead head) {
        data = setByteInLong(data, head.id, 1);
        traitCache = null;
    }

    @Override
    public GolemHead getHead() {
        int id = getByteInLong(data, 1);
        GolemHead[] heads = GolemHead.getHeads();
        return id >= 0 && id < heads.length && heads[id] != null ? heads[id] : heads[0];
    }

    @Override
    public void setArms(GolemArm arm) {
        data = setByteInLong(data, arm.id, 2);
        traitCache = null;
    }

    @Override
    public GolemArm getArms() {
        int id = getByteInLong(data, 2);
        GolemArm[] arms = GolemArm.getArms();
        return id >= 0 && id < arms.length && arms[id] != null ? arms[id] : arms[0];
    }

    @Override
    public void setLegs(GolemLeg leg) {
        data = setByteInLong(data, leg.id, 3);
        traitCache = null;
    }

    @Override
    public GolemLeg getLegs() {
        int id = getByteInLong(data, 3);
        GolemLeg[] legs = GolemLeg.getLegs();
        return id >= 0 && id < legs.length && legs[id] != null ? legs[id] : legs[0];
    }

    @Override
    public void setAddon(GolemAddon addon) {
        data = setByteInLong(data, addon.id, 4);
        traitCache = null;
    }

    @Override
    public GolemAddon getAddon() {
        int id = getByteInLong(data, 4);
        GolemAddon[] addons = GolemAddon.getAddons();
        return id >= 0 && id < addons.length && addons[id] != null ? addons[id] : addons[0];
    }

    @Override
    public void setRank(int rank) {
        data = setByteInLong(data, (byte) rank, 5);
    }

    @Override
    public int getRank() {
        return getByteInLong(data, 5);
    }

    public static IGolemProperties fromLong(long d) {
        GolemProperties out = new GolemProperties();
        out.data = d;
        return out;
    }

    @Override
    public long toLong() {
        return data;
    }

    @Override
    public ItemStack[] generateComponents() {
        List<ItemStack> comps = new ArrayList<>();
        ItemStack base = getMaterial().componentBase;
        ItemStack mech = getMaterial().componentMechanism;
        addToList(comps, base, 2);
        addToList(comps, mech, 1);
        addToListFromComps(comps, getArms().components, getMaterial());
        addToListFromComps(comps, getLegs().components, getMaterial());
        addToListFromComps(comps, getHead().components, getMaterial());
        addToListFromComps(comps, getAddon().components, getMaterial());
        return comps.toArray(new ItemStack[0]);
    }

    private static void addToListFromComps(List<ItemStack> comps, Object[] objs, GolemMaterial mat) {
        if (objs == null) return;
        for (Object o : objs) {
            if (o instanceof ItemStack is) {
                addToList(comps, is, 1);
            } else if (o instanceof String s) {
                if ("base".equalsIgnoreCase(s)) {
                    addToList(comps, mat.componentBase, 1);
                } else if ("mech".equalsIgnoreCase(s)) {
                    addToList(comps, mat.componentMechanism, 1);
                }
            }
        }
    }

    private static void addToList(List<ItemStack> comps, ItemStack newItem, int mult) {
        if (newItem == null || newItem.isEmpty()) return;
        for (ItemStack stack : comps) {
            if (ItemStack.isSameItemSameComponents(stack, newItem)) {
                stack.grow(newItem.getCount() * mult);
                return;
            }
        }
        ItemStack copy = newItem.copy();
        copy.setCount(copy.getCount() * mult);
        comps.add(copy);
    }

    private static boolean registered = false;

    private static ItemStack safeStack(java.util.function.Supplier<? extends net.minecraft.world.level.ItemLike> supplier) {
        return safeStack(supplier, 1);
    }

    private static ItemStack safeStack(java.util.function.Supplier<? extends net.minecraft.world.level.ItemLike> supplier, int count) {
        try {
            if (supplier instanceof net.neoforged.neoforge.registries.DeferredHolder<?, ?> holder && !holder.isBound()) {
                return ItemStack.EMPTY;
            }
            var itemLike = supplier.get();
            if (itemLike != null) {
                return new ItemStack(itemLike, count);
            }
        } catch (Throwable ignored) {
        }
        return ItemStack.EMPTY;
    }

    public static synchronized void initParts() {
        if (registered) {
            if (GolemMaterial.getMaterials().length > 1 && !GolemMaterial.getMaterials()[1].componentBase.isEmpty()) {
                return;
            }
        }
        registered = true;

        GolemMaterial.reset();
        GolemHead.reset();
        GolemArm.reset();
        GolemLeg.reset();
        GolemAddon.reset();

        // Materials
        GolemMaterial.register(new GolemMaterial("WOOD", new String[]{"MATSTUDWOOD"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/mat_wood.png"),
                5059370, 6, 2, 1,
                safeStack(TCBlocks.PLANK_GREATWOOD),
                safeStack(TCItems.MECHANISM_SIMPLE),
                new EnumGolemTrait[]{EnumGolemTrait.LIGHT}));

        GolemMaterial.register(new GolemMaterial("IRON", new String[]{"MATSTUDIRON"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/mat_iron.png"),
                16777215, 20, 8, 3,
                safeStack(TCItems.IRON_PLATE),
                safeStack(TCItems.MECHANISM_SIMPLE),
                new EnumGolemTrait[]{EnumGolemTrait.HEAVY, EnumGolemTrait.FIREPROOF, EnumGolemTrait.BLASTPROOF}));

        GolemMaterial.register(new GolemMaterial("CLAY", new String[]{"MATSTUDCLAY"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/mat_clay.png"),
                13071447, 10, 4, 2,
                new ItemStack(Blocks.TERRACOTTA),
                safeStack(TCItems.MECHANISM_SIMPLE),
                new EnumGolemTrait[]{EnumGolemTrait.FIREPROOF}));

        GolemMaterial.register(new GolemMaterial("BRASS", new String[]{"MATSTUDBRASS"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/mat_brass.png"),
                15638812, 16, 6, 3,
                safeStack(TCItems.BRASS_PLATE),
                safeStack(TCItems.MECHANISM_SIMPLE),
                new EnumGolemTrait[]{EnumGolemTrait.LIGHT}));

        GolemMaterial.register(new GolemMaterial("THAUMIUM", new String[]{"MATSTUDTHAUMIUM"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/mat_thaumium.png"),
                5257074, 24, 10, 4,
                safeStack(TCItems.THAUMIUM_PLATE),
                safeStack(TCItems.MECHANISM_SIMPLE),
                new EnumGolemTrait[]{EnumGolemTrait.HEAVY, EnumGolemTrait.FIREPROOF, EnumGolemTrait.BLASTPROOF}));

        GolemMaterial.register(new GolemMaterial("VOID", new String[]{"MATSTUDVOID"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/mat_void.png"),
                1445161, 20, 6, 4,
                safeStack(TCItems.VOID_PLATE),
                safeStack(TCItems.MECHANISM_SIMPLE),
                new EnumGolemTrait[]{EnumGolemTrait.REPAIR}));

        // Heads
        GolemHead.register(new GolemHead("BASIC", new String[]{"MINDCLOCKWORK"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/head_basic.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_head_basic.obj"), null, PartModel.EnumAttachPoint.HEAD),
                new Object[]{safeStack(TCItems.CATALOG_PLACEHOLDER_MINDCLOCKWORK)},
                new EnumGolemTrait[0]));

        GolemHead.register(new GolemHead("SMART", new String[]{"MINDBIOTHAUMIC"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/head_smart.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_head_smart.obj"),
                        ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD),
                new Object[]{safeStack(TCItems.MIND_CLOCKWORK_ADVANCED)},
                new EnumGolemTrait[]{EnumGolemTrait.SMART, EnumGolemTrait.FRAGILE}));

        GolemHead.register(new GolemHead("SMART_ARMORED", new String[]{"MINDBIOTHAUMIC", "GOLEMCOMBATADV"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/head_smartarmor.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_head_smart_armor.obj"), null, PartModel.EnumAttachPoint.HEAD),
                new Object[]{safeStack(TCItems.MIND_CLOCKWORK_ADVANCED), safeStack(TCItems.IRON_PLATE), "base", new ItemStack(Blocks.WHITE_WOOL)},
                new EnumGolemTrait[]{EnumGolemTrait.SMART}));

        GolemHead.register(new GolemHead("SCOUT", new String[]{"GOLEMVISION"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/head_scout.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_head_scout.obj"),
                        ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD),
                new Object[]{safeStack(TCItems.CATALOG_PLACEHOLDER_MINDCLOCKWORK), safeStack(TCItems.CATALOG_PLACEHOLDER_MODVISION)},
                new EnumGolemTrait[]{EnumGolemTrait.SCOUT, EnumGolemTrait.FRAGILE}));

        GolemHead.register(new GolemHead("SMART_SCOUT", new String[]{"GOLEMVISION", "MINDBIOTHAUMIC"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/head_smartscout.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_head_scout_smart.obj"),
                        ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/golem_head_other.png"), PartModel.EnumAttachPoint.HEAD),
                new Object[]{safeStack(TCItems.MIND_CLOCKWORK_ADVANCED), safeStack(TCItems.CATALOG_PLACEHOLDER_MODVISION)},
                new EnumGolemTrait[]{EnumGolemTrait.SCOUT, EnumGolemTrait.SMART, EnumGolemTrait.FRAGILE}));

        // Arms
        GolemArm.register(new GolemArm("BASIC", new String[]{"MINDCLOCKWORK"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/arms_basic.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_arms_basic.obj"), null, PartModel.EnumAttachPoint.ARMS),
                new Object[0], new EnumGolemTrait[0]));

        GolemArm.register(new GolemArm("FINE", new String[]{"MATSTUDBRASS"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/arms_fine.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_arms_fine.obj"), null, PartModel.EnumAttachPoint.ARMS),
                new Object[]{safeStack(TCItems.MECHANISM_SIMPLE), "base"},
                new EnumGolemTrait[]{EnumGolemTrait.DEFT, EnumGolemTrait.FRAGILE}));

        GolemArm.register(new GolemArm("CLAWS", new String[]{"GOLEMCOMBATADV"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/arms_claws.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_arms_claws.obj"),
                        ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/golem_arms_claws.png"), PartModel.EnumAttachPoint.ARMS),
                new Object[]{safeStack(TCItems.CATALOG_PLACEHOLDER_MODAGGRESSION), new ItemStack(Items.SHEARS, 2), "base"},
                new EnumGolemTrait[]{EnumGolemTrait.FIGHTER, EnumGolemTrait.CLUMSY, EnumGolemTrait.BRUTAL}));

        GolemArm.register(new GolemArm("BREAKERS", new String[]{"GOLEMBREAKER"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/arms_breakers.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_arms_breakers.obj"),
                        ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/golem_arms_breakers.png"), PartModel.EnumAttachPoint.ARMS),
                new Object[]{new ItemStack(Items.DIAMOND, 2), "base", new ItemStack(Blocks.PISTON, 2)},
                new EnumGolemTrait[]{EnumGolemTrait.BREAKER, EnumGolemTrait.CLUMSY, EnumGolemTrait.BRUTAL}));

        GolemArm.register(new GolemArm("DARTS", new String[]{"GOLEMCOMBATADV"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/arms_darts.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_arms_darter.obj"),
                        ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/golem_arms_darter.png"), PartModel.EnumAttachPoint.ARMS),
                new Object[]{safeStack(TCItems.CATALOG_PLACEHOLDER_MODAGGRESSION), new ItemStack(Blocks.DISPENSER, 2), new ItemStack(Items.ARROW, 32), "mech"},
                new EnumGolemTrait[]{EnumGolemTrait.FIGHTER, EnumGolemTrait.CLUMSY, EnumGolemTrait.RANGED, EnumGolemTrait.FRAGILE}));

        // Legs
        GolemLeg.register(new GolemLeg("WALKER", new String[]{"MINDCLOCKWORK"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/legs_walker.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_legs_walker.obj"), null, PartModel.EnumAttachPoint.LEGS),
                new Object[]{"base", "mech"}, new EnumGolemTrait[0]));

        GolemLeg.register(new GolemLeg("ROLLER", new String[]{"MINDCLOCKWORK"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/legs_roller.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_legs_wheel.obj"),
                        ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/golem_legs_wheel.png"), PartModel.EnumAttachPoint.BODY),
                new Object[]{new ItemStack(Items.BOWL, 2), new ItemStack(Items.LEATHER), "mech"},
                new EnumGolemTrait[]{EnumGolemTrait.WHEELED}));

        GolemLeg.register(new GolemLeg("CLIMBER", new String[]{"GOLEMCLIMBER"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/legs_climber.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_legs_climber.obj"),
                        ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/blocks/base_metal.png"), PartModel.EnumAttachPoint.LEGS),
                new Object[]{new ItemStack(Items.FLINT, 4), "base", "mech", "mech"},
                new EnumGolemTrait[]{EnumGolemTrait.CLIMBER}));

        GolemLeg.register(new GolemLeg("FLYER", new String[]{"GOLEMFLYER"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/legs_flyer.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_legs_floater.obj"),
                        ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/golem_legs_floater.png"), PartModel.EnumAttachPoint.BODY),
                new Object[]{safeStack(TCBlocks.LEVITATOR), safeStack(TCItems.IRON_PLATE, 4), new ItemStack(Items.SLIME_BALL), "mech"},
                new EnumGolemTrait[]{EnumGolemTrait.FLYER, EnumGolemTrait.FRAGILE}));

        // Addons
        GolemAddon.register(new GolemAddon("NONE", new String[]{"MINDCLOCKWORK"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/blocks/blank.png"), null,
                new Object[0], new EnumGolemTrait[0]));

        GolemAddon.register(new GolemAddon("ARMORED", new String[]{"GOLEMCOMBATADV"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/addon_armored.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_armor.obj"), null, PartModel.EnumAttachPoint.BODY),
                new Object[]{"base", "base", "base", "base"},
                new EnumGolemTrait[]{EnumGolemTrait.ARMORED, EnumGolemTrait.HEAVY}));

        GolemAddon.register(new GolemAddon("FIGHTER", new String[]{"SEALGUARD"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/addon_fighter.png"), null,
                new Object[]{safeStack(TCItems.CATALOG_PLACEHOLDER_MODAGGRESSION), "mech"},
                new EnumGolemTrait[]{EnumGolemTrait.FIGHTER}));

        GolemAddon.register(new GolemAddon("HAULER", new String[]{"MINDCLOCKWORK"},
                ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/golem/addon_hauler.png"),
                new PartModel(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/golem_hauler.obj"),
                        ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/entity/golems/golem_hauler.png"), PartModel.EnumAttachPoint.BODY),
                new Object[]{new ItemStack(Items.LEATHER), new ItemStack(Blocks.CHEST)},
                new EnumGolemTrait[]{EnumGolemTrait.HAULER}));
    }

    static {
        initParts();
    }
}
