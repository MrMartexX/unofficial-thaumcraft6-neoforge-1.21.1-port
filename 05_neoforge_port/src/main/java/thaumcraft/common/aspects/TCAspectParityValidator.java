package thaumcraft.common.aspects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

final class TCAspectParityValidator {
    private static final AspectSpec[] ASPECTS = new AspectSpec[] {
            primal("aer", 0xffff7e, "e", 1),
            primal("terra", 0x56c000, "2", 1),
            primal("ignis", 0xff5a01, "c", 1),
            primal("aqua", 0x3cd4fc, "3", 1),
            primal("ordo", 0xd5d4ec, "7", 1),
            primal("perditio", 0x404040, "8", 771),
            compound("vacuos", 0x888888, 771, "aer", "perditio"),
            compound("lux", 0xffffc0, 1, "aer", "ignis"),
            compound("motus", 0xcdccf4, 1, "aer", "ordo"),
            compound("gelum", 0xe1ffff, 1, "ignis", "perditio"),
            compound("vitreus", 0x80ffff, 1, "terra", "aer"),
            compound("metallum", 0xb5b5cd, 1, "terra", "ordo"),
            compound("victus", 0xde0005, 1, "terra", "aqua"),
            compound("mortuus", 0x6a0005, 1, "aqua", "perditio"),
            compound("potentia", 0xc0ffff, 1, "ordo", "ignis"),
            compound("permutatio", 0x578357, 1, "perditio", "ordo"),
            compound("praecantatio", 0xcf00ff, 1, "potentia", "aer"),
            compound("auram", 0xffc0ff, 1, "praecantatio", "aer"),
            compound("alkimia", 0x23ac9d, 1, "praecantatio", "aqua"),
            compound("vitium", 0x800080, 1, "perditio", "praecantatio"),
            compound("tenebrae", 0x222222, 1, "vacuos", "lux"),
            compound("alienis", 0x805080, 1, "vacuos", "tenebrae"),
            compound("volatus", 0xe7e7d7, 1, "aer", "motus"),
            compound("herba", 0x01ac00, 1, "victus", "terra"),
            compound("instrumentum", 0x4040ee, 1, "metallum", "potentia"),
            compound("fabrico", 0x809d80, 1, "permutatio", "instrumentum"),
            compound("machina", 0x8080a0, 1, "motus", "instrumentum"),
            compound("vinculum", 0x9a8080, 1, "motus", "perditio"),
            compound("spiritus", 0xebebfb, 1, "victus", "mortuus"),
            compound("cognitio", 0xf9967f, 1, "ignis", "spiritus"),
            compound("sensus", 0xc0ffc0, 1, "aer", "spiritus"),
            compound("aversio", 0xc05050, 1, "spiritus", "perditio"),
            compound("praemunio", 0x00c0c0, 1, "spiritus", "terra"),
            compound("desiderium", 0xe6be44, 1, "spiritus", "vacuos"),
            compound("exanimis", 0x3a4000, 1, "motus", "mortuus"),
            compound("bestia", 0x9f6409, 1, "motus", "victus"),
            compound("humanus", 0xffd7c0, 1, "spiritus", "victus")
    };

    static void validate(boolean allowAddonAssignments) {
        validateAspectRegistry();
        validateAspectListSemantics();
        validateAspectHelperSemantics();
        validateEntityAspectAssignments();
        validateDirectAssignments(allowAddonAssignments);
        validateTagAssignments(allowAddonAssignments);
        validateComplexAssignments(allowAddonAssignments);
        validateGeneratedCacheScaffold();
    }

    private static void validateAspectRegistry() {
        expectEquals(37, Aspect.aspects.size(), "aspect registry size");
        expectEquals(6, Aspect.getPrimalAspects().size(), "primal aspect count");
        expectEquals(31, Aspect.getCompoundAspects().size(), "compound aspect count");

        List<String> actualOrder = new ArrayList<>(Aspect.aspects.keySet());
        List<String> expectedOrder = Arrays.stream(ASPECTS).map(AspectSpec::tag).toList();
        expectEquals(expectedOrder, actualOrder, "aspect registration order");

        for (AspectSpec spec : ASPECTS) {
            Aspect aspect = Aspect.getAspect(spec.tag());
            expectNotNull(aspect, "missing aspect " + spec.tag());
            expectEquals(spec.color(), aspect.getColor(), spec.tag() + " color");
            expectEquals(spec.blend(), aspect.getBlend(), spec.tag() + " blend");
            expectEquals(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, spec.tag()), aspect.getId(), spec.tag() + " id");
            expectEquals("thaumcraft:textures/aspects/" + spec.tag() + ".png", aspect.getImage().toString(), spec.tag() + " image");
            expectEquals(capitalize(spec.tag()), aspect.getName(), spec.tag() + " name");

            if (spec.chatColor() != null) {
                expectEquals(spec.chatColor(), aspect.getChatcolor(), spec.tag() + " chat color");
            }

            if (spec.isPrimal()) {
                expect(aspect.isPrimal(), spec.tag() + " should be primal");
                expect(aspect.getComponents() == null, spec.tag() + " should have null legacy components");
            } else {
                expect(!aspect.isPrimal(), spec.tag() + " should be compound");
                Aspect[] components = aspect.getComponents();
                expectEquals(2, components.length, spec.tag() + " component count");
                expectSame(Aspect.getAspect(spec.component0()), components[0], spec.tag() + " first component");
                expectSame(Aspect.getAspect(spec.component1()), components[1], spec.tag() + " second component");

                int mixHash = (spec.component0() + spec.component1()).hashCode();
                expectSame(aspect, Aspect.mixList.get(mixHash), spec.tag() + " mixList entry");
                expectSame(aspect, AspectHelper.getCombinationResult(components[0], components[1]), spec.tag() + " combination");
                expectSame(aspect, AspectHelper.getCombinationResult(components[1], components[0]), spec.tag() + " reverse combination");
            }
        }
    }

    private static void validateAspectListSemantics() {
        AspectList list = new AspectList();
        list.add(Aspect.AIR, 2).add(Aspect.AIR, 3).merge(Aspect.AIR, 4).merge(Aspect.FIRE, 7);
        expectEquals(5, list.getAmount(Aspect.AIR), "AspectList add then lower merge keeps old amount");
        expectEquals(7, list.getAmount(Aspect.FIRE), "AspectList merge inserts amount");
        expectEquals(12, list.visSize(), "AspectList visSize after add/merge");

        expect(list.reduce(Aspect.AIR, 3), "AspectList reduce should succeed");
        expectEquals(2, list.getAmount(Aspect.AIR), "AspectList reduce amount");
        expect(!list.reduce(Aspect.AIR, 3), "AspectList reduce should fail when amount is too small");
        expect(list.reduce(Aspect.AIR, 2), "AspectList reduce to zero should succeed");
        expectEquals(0, list.getAmount(Aspect.AIR), "AspectList reduce leaves zero amount");
        expectEquals(2, list.size(), "AspectList reduce to zero keeps key");
        list.remove(Aspect.AIR, 1);
        expectEquals(1, list.size(), "AspectList remove deletes key at zero or below");

        AspectList sorted = new AspectList();
        sorted.add(Aspect.WATER, 5).add(Aspect.AIR, 1).add(Aspect.FIRE, 3);
        expectAspectOrder(new Aspect[] { Aspect.AIR, Aspect.WATER, Aspect.FIRE }, sorted.getAspectsSortedByName(), "AspectList name sort");
        expectAspectOrder(new Aspect[] { Aspect.WATER, Aspect.FIRE, Aspect.AIR }, sorted.getAspectsSortedByAmount(), "AspectList amount sort");

        AspectList copy = sorted.copy();
        copy.add(Aspect.AIR, 10);
        expectEquals(1, sorted.getAmount(Aspect.AIR), "AspectList copy does not mutate source");
        expectEquals(11, copy.getAmount(Aspect.AIR), "AspectList copy receives mutation");

        AspectList merged = new AspectList();
        merged.add(Aspect.AIR, 1).merge(new AspectList().add(Aspect.AIR, 7).add(Aspect.FLUX, 2));
        expectEquals(7, merged.getAmount(Aspect.AIR), "AspectList merge list keeps max amount");
        expectEquals(2, merged.getAmount(Aspect.FLUX), "AspectList merge list inserts new aspect");
        merged.remove(new AspectList().add(Aspect.AIR, 3).add(Aspect.FLUX, 2));
        expectEquals(4, merged.getAmount(Aspect.AIR), "AspectList remove list subtracts amount");
        expectEquals(0, merged.getAmount(Aspect.FLUX), "AspectList remove list removes depleted aspect");
        expectEquals(1, merged.size(), "AspectList remove list deletes depleted key");

        CompoundTag tag = new CompoundTag();
        sorted.writeToNBT(tag, "Custom");
        AspectList read = new AspectList();
        read.readFromNBT(tag, "Custom");
        expectEquals(5, read.getAmount(Aspect.WATER), "AspectList NBT water amount");
        expectEquals(1, read.getAmount(Aspect.AIR), "AspectList NBT air amount");
        expectEquals(3, read.getAmount(Aspect.FIRE), "AspectList NBT fire amount");

        CompoundTag unknownRoot = new CompoundTag();
        ListTag unknownList = new ListTag();
        CompoundTag unknownAspect = new CompoundTag();
        unknownAspect.putString("key", "missing_legacy_aspect");
        unknownAspect.putInt("amount", 8);
        unknownList.add(unknownAspect);
        unknownRoot.put("Aspects", unknownList);

        AspectList unknownRead = new AspectList();
        unknownRead.readFromNBT(unknownRoot);
        expectEquals(1, unknownRead.size(), "AspectList unknown NBT aspect keeps null key");
        expectEquals(8, unknownRead.getAmount(null), "AspectList unknown NBT amount");
    }

    private static void validateAspectHelperSemantics() {
        AspectList reduced = AspectHelper.reduceToPrimals(new AspectList().add(Aspect.AURA, 1).add(Aspect.CRYSTAL, 2));
        expectEquals(4, reduced.getAmount(Aspect.AIR), "reduceToPrimals air amount");
        expectEquals(2, reduced.getAmount(Aspect.EARTH), "reduceToPrimals earth amount");
        expectEquals(1, reduced.getAmount(Aspect.ORDER), "reduceToPrimals order amount");
        expectEquals(1, reduced.getAmount(Aspect.FIRE), "reduceToPrimals fire amount");
        expectEquals(8, reduced.visSize(), "reduceToPrimals total amount");

        AspectList primalOnly = AspectHelper.getPrimalAspects(new AspectList().add(Aspect.AIR, 4).add(Aspect.FLUX, 2));
        expectEquals(6, primalOnly.size(), "getPrimalAspects includes six primal keys");
        expectEquals(4, primalOnly.getAmount(Aspect.AIR), "getPrimalAspects keeps air amount");
        expectEquals(0, primalOnly.getAmount(Aspect.ENTROPY), "getPrimalAspects adds zero entropy");
        expectEquals(0, primalOnly.getAmount(Aspect.FLUX), "getPrimalAspects excludes flux");

        AspectList auraOnly = AspectHelper.getAuraAspects(new AspectList().add(Aspect.AIR, 4).add(Aspect.FLUX, 2));
        expectEquals(7, auraOnly.size(), "getAuraAspects includes six primals and flux");
        expectEquals(2, auraOnly.getAmount(Aspect.FLUX), "getAuraAspects keeps flux amount");

        AspectList culledPrimal = AspectHelper.cullTags(new AspectList().add(Aspect.AIR, 10).add(Aspect.FIRE, 11), 1);
        expectEquals(1, culledPrimal.size(), "cullTags primal weighting result size");
        expectEquals(11, culledPrimal.getAmount(Aspect.FIRE), "cullTags removes lower weighted primal");

        AspectList culledCompound = AspectHelper.cullTags(new AspectList().add(Aspect.AIR, 10).add(Aspect.FIRE, 9).add(Aspect.MAGIC, 1), 2);
        expectEquals(2, culledCompound.size(), "cullTags compound result size");
        expectEquals(10, culledCompound.getAmount(Aspect.AIR), "cullTags keeps air");
        expectEquals(9, culledCompound.getAmount(Aspect.FIRE), "cullTags keeps fire");
        expectEquals(0, culledCompound.getAmount(Aspect.MAGIC), "cullTags removes low weighted compound");

        expect(AspectHelper.generateTags(ItemStack.EMPTY) == null, "generateTags must remain unavailable until exact legacy generation is ported");
    }

    private static void validateEntityAspectAssignments() {
        expectEntity(EntityType.SPIDER, amount(Aspect.BEAST, 10), amount(Aspect.ENTROPY, 10), amount(Aspect.TRAP, 10));
        expectEntity(EntityType.BAT, amount(Aspect.BEAST, 5), amount(Aspect.FLIGHT, 5), amount(Aspect.DARKNESS, 5));
        expectEntity(EntityType.ENDERMAN, amount(Aspect.ELDRITCH, 10), amount(Aspect.MOTION, 15), amount(Aspect.DESIRE, 5));
        expectEntity(EntityType.CREEPER, amount(Aspect.PLANT, 15), amount(Aspect.FIRE, 15));
        expectEntity(EntityType.ELDER_GUARDIAN, amount(Aspect.BEAST, 10), amount(Aspect.ELDRITCH, 15), amount(Aspect.WATER, 15));
        expectEntity(EntityType.ZOMBIE_VILLAGER, amount(Aspect.UNDEAD, 20), amount(Aspect.MAN, 15), amount(Aspect.EARTH, 5));
        expectEntity(EntityType.WARDEN, amount(Aspect.BEAST, 30), amount(Aspect.DARKNESS, 30), amount(Aspect.SENSES, 20), amount(Aspect.AVERSION, 30), amount(Aspect.SOUL, 15));
    }

    private static void validateDirectAssignments(boolean allowAddonAssignments) {
        Map<ResourceLocation, AspectList> tags = TCAspectAssignments.directObjectTags();
        expectAssignmentCount(702, tags.size(), "direct object assignment count", allowAddonAssignments);

        expectDirect(tags, "ore_quartz", amount(Aspect.EARTH, 5), amount(Aspect.CRYSTAL, 10));
        expectDirect(tags, "ore_cinnabar", amount(Aspect.EARTH, 5), amount(Aspect.METAL, 10), amount(Aspect.ALCHEMY, 5), amount(Aspect.DEATH, 5));
        expectDirect(tags, "ore_amber", amount(Aspect.EARTH, 5), amount(Aspect.TRAP, 10), amount(Aspect.CRYSTAL, 10));
        expectDirect(tags, "quicksilver", amount(Aspect.METAL, 10), amount(Aspect.DEATH, 5), amount(Aspect.ALCHEMY, 5));
        expectDirect(tags, "rare_earth", amount(Aspect.EARTH, 5), amount(Aspect.ORDER, 5), amount(Aspect.METAL, 5));
        expectDirect(tags, "chunk_beef", amount(Aspect.LIFE, 5), amount(Aspect.ENTROPY, 1));
        expectDirect(tags, "chunk_chicken", amount(Aspect.LIFE, 5), amount(Aspect.ENTROPY, 1));
        expectDirect(tags, "chunk_fish", amount(Aspect.LIFE, 5), amount(Aspect.ENTROPY, 1));
        expectDirect(tags, "chunk_mutton", amount(Aspect.LIFE, 5), amount(Aspect.ENTROPY, 1));
        expectDirect(tags, "chunk_pork", amount(Aspect.LIFE, 5), amount(Aspect.ENTROPY, 1));
        expectDirect(tags, "chunk_rabbit", amount(Aspect.LIFE, 5), amount(Aspect.ENTROPY, 1));
        expectDirect(tags, "amber", amount(Aspect.TRAP, 10), amount(Aspect.CRYSTAL, 10));
        expectDirect(tags, "table_wood", amount(Aspect.TOOL, 1), amount(Aspect.PLANT, 6));
        expectDirect(tags, "table_stone", amount(Aspect.TOOL, 1), amount(Aspect.EARTH, 9));
        expectDirect(tags, "research_table", amount(Aspect.TOOL, 1), amount(Aspect.PLANT, 6), amount(Aspect.MIND, 5));
        expectDirect(tags, "inlay", amount(Aspect.ENERGY, 3), amount(Aspect.METAL, 3), amount(Aspect.DESIRE, 3), amount(Aspect.MAGIC, 1));
        expectDirect(tags, "stabilizer", amount(Aspect.EARTH, 15), amount(Aspect.ENERGY, 58), amount(Aspect.METAL, 59), amount(Aspect.MAGIC, 23), amount(Aspect.DESIRE, 4), amount(Aspect.MECHANISM, 5), amount(Aspect.MOTION, 5));
        expectDirect(
                tags,
                "thaumonomicon",
                amount(Aspect.MIND, 17),
                amount(Aspect.PLANT, 27),
                amount(Aspect.WATER, 9),
                amount(Aspect.AIR, 4),
                amount(Aspect.BEAST, 6),
                amount(Aspect.PROTECT, 6),
                amount(Aspect.MAGIC, 10)
        );

        expectDirect(tags, "crystal_aer", amount(Aspect.AIR, 15), amount(Aspect.CRYSTAL, 10));
        expectDirect(tags, "crystal_ignis", amount(Aspect.FIRE, 15), amount(Aspect.CRYSTAL, 10));
        expectDirect(tags, "crystal_aqua", amount(Aspect.WATER, 15), amount(Aspect.CRYSTAL, 10));
        expectDirect(tags, "crystal_terra", amount(Aspect.EARTH, 15), amount(Aspect.CRYSTAL, 10));
        expectDirect(tags, "crystal_ordo", amount(Aspect.ORDER, 15), amount(Aspect.CRYSTAL, 10));
        expectDirect(tags, "crystal_perditio", amount(Aspect.ENTROPY, 15), amount(Aspect.CRYSTAL, 10));
        expectDirect(tags, "crystal_vitium", amount(Aspect.FLUX, 15), amount(Aspect.CRYSTAL, 10));

        expectDirect(tags, "log_greatwood", amount(Aspect.PLANT, 20), amount(Aspect.LIFE, 5));
        expectDirect(tags, "log_silverwood", amount(Aspect.PLANT, 20), amount(Aspect.AURA, 5));
        expectDirect(tags, "leaves_greatwood", amount(Aspect.PLANT, 5));
        expectDirect(tags, "leaves_silverwood", amount(Aspect.PLANT, 5));
        expectDirect(tags, "sapling_greatwood", amount(Aspect.PLANT, 15), amount(Aspect.LIFE, 5));
        expectDirect(tags, "sapling_silverwood", amount(Aspect.PLANT, 15), amount(Aspect.AURA, 5));
        expectDirect(tags, "shimmerleaf", amount(Aspect.PLANT, 5), amount(Aspect.AURA, 10), amount(Aspect.ENERGY, 5));
        expectDirect(tags, "cinderpearl", amount(Aspect.PLANT, 5), amount(Aspect.AURA, 5), amount(Aspect.FIRE, 10));
        expectDirect(tags, "vishroom", amount(Aspect.PLANT, 2), amount(Aspect.DEATH, 1), amount(Aspect.MAGIC, 1), amount(Aspect.ENTROPY, 1));

        expectDirect(tags, "stone_ancient", amount(Aspect.EARTH, 5), amount(Aspect.ELDRITCH, 5));
        expectDirect(tags, "stone_ancient_tile", amount(Aspect.EARTH, 5), amount(Aspect.ELDRITCH, 5));
        expectDirect(tags, "stone_ancient_rock", amount(Aspect.EARTH, 5), amount(Aspect.ELDRITCH, 5));
        expectDirect(tags, "stone_eldritch_tile", amount(Aspect.EARTH, 5), amount(Aspect.ELDRITCH, 5));
        expectDirect(tags, "stone_ancient_doorway", amount(Aspect.METAL, 5), amount(Aspect.ELDRITCH, 5), amount(Aspect.TRAP, 5));
        expectDirect(tags, "stone_ancient_glyphed", amount(Aspect.METAL, 5), amount(Aspect.ELDRITCH, 5), amount(Aspect.MIND, 5));
        expectDirect(tags, "stone_porous", amount(Aspect.EARTH, 5), amount(Aspect.VOID, 5));
        expectDirect(tags, "stone_arcane", amount(Aspect.EARTH, 3));
        expectDirect(tags, "stone_arcane_brick", amount(Aspect.EARTH, 2));
        expectDirect(tags, "pillar_arcane");
        expectDirect(tags, "pillar_ancient");
        expectDirect(tags, "pillar_eldritch");
        expectDirect(tags, "matrix_speed",
                amount(Aspect.EARTH, 9),
                amount(Aspect.SENSES, 9),
                amount(Aspect.WATER, 3),
                amount(Aspect.CRYSTAL, 75),
                amount(Aspect.DESIRE, 75),
                amount(Aspect.MAGIC, 15));
        expectDirect(tags, "matrix_cost",
                amount(Aspect.EARTH, 9),
                amount(Aspect.ENERGY, 39),
                amount(Aspect.FIRE, 39),
                amount(Aspect.ENTROPY, 6),
                amount(Aspect.CRYSTAL, 75),
                amount(Aspect.DESIRE, 75),
                amount(Aspect.MAGIC, 15));
        expectDirect(tags, "slab_ancient", amount(Aspect.EARTH, 1), amount(Aspect.ELDRITCH, 1));
        expectDirect(tags, "slab_arcane_stone", amount(Aspect.EARTH, 1));
        expectDirect(tags, "slab_eldritch", amount(Aspect.EARTH, 1), amount(Aspect.ELDRITCH, 1));
        expectDirect(tags, "stairs_ancient", amount(Aspect.EARTH, 5), amount(Aspect.ELDRITCH, 5));
        expectDirect(tags, "stairs_arcane", amount(Aspect.EARTH, 3));
        expectDirect(tags, "stairs_arcane_brick", amount(Aspect.EARTH, 2));
        expectDirect(tags, "amber_brick");
        expectDirect(tags, "fabric", amount(Aspect.BEAST, 26), amount(Aspect.CRAFT, 6), amount(Aspect.MAGIC, 1));
        expectDirect(tags, "salis_mundus", amount(Aspect.MAGIC, 5), amount(Aspect.ENERGY, 5));
        expectDirect(tags, "brain", amount(Aspect.LIFE, 5), amount(Aspect.MIND, 20), amount(Aspect.UNDEAD, 10));
        expectDirect(tags, "alumentum", amount(Aspect.ENERGY, 13), amount(Aspect.FIRE, 13), amount(Aspect.ENTROPY, 2));
        expectDirect(tags, "liquid_death_bucket",
                amount(Aspect.VOID, 5),
                amount(Aspect.METAL, 33),
                amount(Aspect.DEATH, 15),
                amount(Aspect.ENTROPY, 15));
        expectDirect(tags, "purifying_fluid_bucket",
                amount(Aspect.VOID, 5),
                amount(Aspect.METAL, 33),
                amount(Aspect.MIND, 15),
                amount(Aspect.ORDER, 15));
        expectDirect(tags, "nitor_yellow", amount(Aspect.SENSES, 5), amount(Aspect.LIGHT, 13), amount(Aspect.ENERGY, 3), amount(Aspect.FIRE, 3));
        expectDirect(tags, "thaumium_ingot", amount(Aspect.METAL, 15), amount(Aspect.MAGIC, 2), amount(Aspect.EARTH, 2));
        expectDirect(tags, "brass_ingot", amount(Aspect.METAL, 10), amount(Aspect.TOOL, 5));
        expectDirect(tags, "brass_plate", amount(Aspect.METAL, 7), amount(Aspect.TOOL, 3));
        expectDirect(tags, "mechanism_simple",
                amount(Aspect.METAL, 27),
                amount(Aspect.TOOL, 4),
                amount(Aspect.MAGIC, 2));
        expectDirect(tags, "mechanism_complex",
                amount(Aspect.METAL, 65),
                amount(Aspect.TOOL, 6),
                amount(Aspect.MAGIC, 9),
                amount(Aspect.EARTH, 12),
                amount(Aspect.MECHANISM, 7),
                amount(Aspect.MOTION, 7),
                amount(Aspect.ENERGY, 5));
        expectDirect(tags, "goggles",
                amount(Aspect.SENSES, 25),
                amount(Aspect.AURA, 25),
                amount(Aspect.BEAST, 15),
                amount(Aspect.PROTECT, 15),
                amount(Aspect.METAL, 60),
                amount(Aspect.DESIRE, 45),
                amount(Aspect.MAGIC, 9));
        expectDirect(tags, "thaumometer",
                amount(Aspect.SENSES, 10),
                amount(Aspect.AURA, 10),
                amount(Aspect.METAL, 30),
                amount(Aspect.DESIRE, 30),
                amount(Aspect.MAGIC, 3));

        expectDirect(tags, "minecraft", "coal_ore", amount(Aspect.EARTH, 5), amount(Aspect.ENERGY, 15), amount(Aspect.FIRE, 15));
        expectDirect(tags, "minecraft", "coal", amount(Aspect.ENERGY, 10), amount(Aspect.FIRE, 10));
        expectDirect(tags, "minecraft", "charcoal", amount(Aspect.ENERGY, 10), amount(Aspect.FIRE, 10));
        expectDirect(tags, "minecraft", "bedrock", amount(Aspect.VOID, 25), amount(Aspect.ENTROPY, 25), amount(Aspect.EARTH, 25), amount(Aspect.DARKNESS, 25));
        expectDirect(tags, "minecraft", "clay_ball", amount(Aspect.WATER, 5), amount(Aspect.EARTH, 5));
        expectDirect(tags, "minecraft", "brick", amount(Aspect.WATER, 5), amount(Aspect.EARTH, 5), amount(Aspect.FIRE, 1));
        expectDirect(tags, "minecraft", "bucket", amount(Aspect.VOID, 5), amount(Aspect.METAL, 33));
        expectDirect(tags, "minecraft", "water_bucket", amount(Aspect.VOID, 5), amount(Aspect.METAL, 33), amount(Aspect.WATER, 20));
        expectDirect(tags, "minecraft", "lava_bucket", amount(Aspect.VOID, 5), amount(Aspect.METAL, 33), amount(Aspect.FIRE, 15), amount(Aspect.EARTH, 5));
        expectDirect(tags, "minecraft", "milk_bucket", amount(Aspect.VOID, 5), amount(Aspect.METAL, 33), amount(Aspect.LIFE, 10), amount(Aspect.BEAST, 5), amount(Aspect.WATER, 5));
        expectDirect(tags, "minecraft", "leather", amount(Aspect.BEAST, 5), amount(Aspect.PROTECT, 5));
        expectDirect(tags, "minecraft", "melon", amount(Aspect.PLANT, 10));
    }

    private static void validateTagAssignments(boolean allowAddonAssignments) {
        Map<TagKey<Item>, AspectList> tags = TCAspectAssignments.tagObjectTags();
        expectAssignmentCount(46, tags.size(), "tag object assignment count", allowAddonAssignments);

        expectTag(tags, "c", "ores/amber", amount(Aspect.EARTH, 5), amount(Aspect.TRAP, 10), amount(Aspect.CRYSTAL, 10));
        expectTag(tags, "c", "ores/cinnabar", amount(Aspect.EARTH, 5), amount(Aspect.METAL, 10), amount(Aspect.ALCHEMY, 5), amount(Aspect.DEATH, 5));
        expectTag(tags, "c", "ores/quartz", amount(Aspect.EARTH, 5), amount(Aspect.CRYSTAL, 10));
        expectTag(tags, "c", "ores/copper", amount(Aspect.METAL, 10), amount(Aspect.EARTH, 5), amount(Aspect.EXCHANGE, 5));
        expectTag(tags, "c", "ingots/copper", amount(Aspect.METAL, 10), amount(Aspect.EXCHANGE, 5));
        expectTag(tags, "c", "gems/amber", amount(Aspect.TRAP, 10), amount(Aspect.CRYSTAL, 10));
        expectTag(tags, "c", "ores/lapis", amount(Aspect.EARTH, 5), amount(Aspect.SENSES, 15));
        expectTag(tags, "c", "ores/diamond", amount(Aspect.EARTH, 5), amount(Aspect.DESIRE, 15), amount(Aspect.CRYSTAL, 15));
        expectTag(tags, "c", "gems/diamond", amount(Aspect.CRYSTAL, 15), amount(Aspect.DESIRE, 15));
        expectTag(tags, "c", "ores/redstone", amount(Aspect.EARTH, 5), amount(Aspect.ENERGY, 15));
        expectTag(tags, "c", "ores/emerald", amount(Aspect.EARTH, 5), amount(Aspect.DESIRE, 10), amount(Aspect.CRYSTAL, 15));
        expectTag(tags, "c", "gems/emerald", amount(Aspect.CRYSTAL, 15), amount(Aspect.DESIRE, 10));
        expectTag(tags, "c", "gems/quartz", amount(Aspect.CRYSTAL, 5));
        expectTag(tags, "c", "ores/iron", amount(Aspect.EARTH, 5), amount(Aspect.METAL, 15));
        expectTag(tags, "c", "ingots/iron", amount(Aspect.METAL, 15));
        expectTag(tags, "c", "raw_materials/iron", amount(Aspect.EARTH, 5), amount(Aspect.METAL, 15));
        expectTag(tags, "c", "ores/gold", amount(Aspect.EARTH, 5), amount(Aspect.METAL, 10), amount(Aspect.DESIRE, 10));
        expectTag(tags, "c", "ingots/gold", amount(Aspect.METAL, 10), amount(Aspect.DESIRE, 10));
        expectTag(tags, "c", "raw_materials/gold", amount(Aspect.EARTH, 5), amount(Aspect.METAL, 10), amount(Aspect.DESIRE, 10));
        expectTag(tags, "c", "raw_materials/copper", amount(Aspect.METAL, 10), amount(Aspect.EARTH, 5), amount(Aspect.EXCHANGE, 5));
        expectTag(tags, "c", "dusts/redstone", amount(Aspect.ENERGY, 10));
        expectTag(tags, "c", "dusts/glowstone", amount(Aspect.SENSES, 5), amount(Aspect.LIGHT, 10));
        expectTag(tags, "c", "stones", amount(Aspect.EARTH, 5));
        expectTag(tags, "c", "cobblestones", amount(Aspect.EARTH, 5), amount(Aspect.ENTROPY, 1));
        expectTag(tags, "c", "dirts", amount(Aspect.EARTH, 5));
        expectTag(tags, "c", "sands", amount(Aspect.EARTH, 5), amount(Aspect.ENTROPY, 5));
        expectTag(tags, "c", "gravels", amount(Aspect.EARTH, 5), amount(Aspect.ENTROPY, 2));
        expectTag(tags, "minecraft", "logs", amount(Aspect.PLANT, 20));
        expectTag(tags, "minecraft", "saplings", amount(Aspect.PLANT, 15), amount(Aspect.LIFE, 5));
        expectTag(tags, "minecraft", "leaves", amount(Aspect.PLANT, 5));
        expectTag(tags, "minecraft", "wool", amount(Aspect.BEAST, 15), amount(Aspect.CRAFT, 5));
        expectTag(tags, Thaumcraft.MODID, "legacy_ore_dictionary/block_glass", amount(Aspect.CRYSTAL, 5));
        expectTag(tags, "c", "ores/coal", amount(Aspect.EARTH, 5), amount(Aspect.ENERGY, 15), amount(Aspect.FIRE, 15));
        expectTag(tags, "minecraft", "flowers", amount(Aspect.PLANT, 5), amount(Aspect.LIFE, 1), amount(Aspect.SENSES, 5));
        expectTag(tags, "minecraft", "bamboo_blocks", amount(Aspect.PLANT, 20));
        expectTag(tags, "minecraft", "soul_fire_base_blocks", amount(Aspect.EARTH, 3), amount(Aspect.TRAP, 1), amount(Aspect.SOUL, 3));
        expectTag(tags, "minecraft", "terracotta", amount(Aspect.EARTH, 5), amount(Aspect.FIRE, 1), amount(Aspect.SENSES, 1));
        expectTag(tags, "minecraft", "anvil", amount(Aspect.METAL, 33));

        expectTag(tags, Thaumcraft.MODID, "legacy_ore_dictionary/ore_amber", amount(Aspect.EARTH, 5), amount(Aspect.TRAP, 10), amount(Aspect.CRYSTAL, 10));
        expectTag(tags, Thaumcraft.MODID, "legacy_ore_dictionary/ore_cinnabar", amount(Aspect.EARTH, 5), amount(Aspect.METAL, 10), amount(Aspect.ALCHEMY, 5), amount(Aspect.DEATH, 5));
        expectTag(tags, Thaumcraft.MODID, "legacy_ore_dictionary/ore_quartz", amount(Aspect.EARTH, 5), amount(Aspect.CRYSTAL, 10));
        expectTag(tags, Thaumcraft.MODID, "legacy_ore_dictionary/log_wood", amount(Aspect.PLANT, 20));
        expectTag(tags, Thaumcraft.MODID, "legacy_ore_dictionary/tree_sapling", amount(Aspect.PLANT, 15), amount(Aspect.LIFE, 5));
        expectTag(tags, Thaumcraft.MODID, "legacy_ore_dictionary/tree_leaves", amount(Aspect.PLANT, 5));
        expectTag(tags, Thaumcraft.MODID, "legacy_ore_dictionary/gem_amber", amount(Aspect.TRAP, 10), amount(Aspect.CRYSTAL, 10));
        expectTag(tags, Thaumcraft.MODID, "legacy_ore_dictionary/quicksilver", amount(Aspect.METAL, 10), amount(Aspect.DEATH, 5), amount(Aspect.ALCHEMY, 5));
    }

    private static void validateComplexAssignments(boolean allowAddonAssignments) {
        Map<ResourceLocation, AspectList> direct = TCAspectAssignments.complexDirectObjectTags();
        expectAssignmentCount(32, direct.size(), "complex direct object assignment count", allowAddonAssignments);

        expectComplexDirect(direct, "minecraft", "cookie", amount(Aspect.DESIRE, 1));
        expectComplexDirect(direct, "minecraft", "bowl", amount(Aspect.VOID, 5));
        expectComplexDirect(direct, "minecraft", "minecart", amount(Aspect.MOTION, 15));
        expectComplexDirect(direct, "minecraft", "flint_and_steel", amount(Aspect.FIRE, 10), amount(Aspect.TOOL, 5));
        expectComplexDirect(direct, "minecraft", "fishing_rod", amount(Aspect.WATER, 10), amount(Aspect.TOOL, 5));
        expectComplexDirect(direct, "minecraft", "stone_button", amount(Aspect.MECHANISM, 5));
        expectComplexDirect(direct, "minecraft", "rail", amount(Aspect.MOTION, 10));
        expectComplexDirect(direct, "minecraft", "powered_rail", amount(Aspect.MECHANISM, 5), amount(Aspect.ENERGY, 1));
        expectComplexDirect(direct, "minecraft", "detector_rail", amount(Aspect.MECHANISM, 5), amount(Aspect.SENSES, 1));
        expectComplexDirect(direct, "minecraft", "activator_rail", amount(Aspect.MECHANISM, 5));
        expectComplexDirect(direct, "minecraft", "crafting_table", amount(Aspect.CRAFT, 20));
        expectComplexDirect(direct, "minecraft", "carrot_on_a_stick", amount(Aspect.MOTION, 5), amount(Aspect.DESIRE, 10));
        expectComplexDirect(direct, "minecraft", "blaze_powder", amount(Aspect.ALCHEMY, 5));
        expectComplexDirect(direct, "minecraft", "ender_eye", amount(Aspect.SENSES, 10), amount(Aspect.MAGIC, 5));
        expectComplexDirect(direct, "minecraft", "oak_boat", amount(Aspect.WATER, 10), amount(Aspect.MOTION, 15));
        expectComplexDirect(direct, "minecraft", "acacia_boat", amount(Aspect.WATER, 10), amount(Aspect.MOTION, 15));
        expectComplexDirect(direct, "minecraft", "iron_door", amount(Aspect.TRAP, 5), amount(Aspect.MECHANISM, 5));
        expectComplexDirect(direct, "minecraft", "oak_door", amount(Aspect.TRAP, 5), amount(Aspect.MECHANISM, 5));
        expectComplexDirect(direct, "minecraft", "acacia_fence_gate", amount(Aspect.TRAP, 5), amount(Aspect.MECHANISM, 5));

        int complexTagCount = TCAspectAssignments.complexTagObjectTags().size();
        if (allowAddonAssignments) {
            expect(complexTagCount >= 0, "complex tag object assignment count should be non-negative");
        } else {
            expectEquals(0, complexTagCount, "complex tag object assignment count");
        }
    }

    private static void validateGeneratedCacheScaffold() {
        expectEquals(0, TCGeneratedAspectCache.size(), "generated aspect cache must start empty");
        expectEquals(
                TCAspectStackKey.from(new ItemStack(Items.STICK, 1)),
                TCAspectStackKey.from(new ItemStack(Items.STICK, 64)),
                "generated aspect stack key ignores count");

        TCGeneratedAspectCache.replaceForValidation(Map.of(
                TCAspectStackKey.from(new ItemStack(Items.STICK)), new AspectList().add(Aspect.AIR, 1)));
        try {
            AspectList generated = TCAspectAssignments.getObjectAspects(new ItemStack(Items.STICK, 64));
            expectNotNull(generated, "generated aspect cache fallback should resolve validation seed");
            expectEquals(1, generated.size(), "generated aspect cache fallback aspect count");
            expectEquals(1, generated.getAmount(Aspect.AIR), "generated aspect cache fallback amount");
        } finally {
            TCGeneratedAspectCache.clear();
        }

        TCGeneratedAspectCache.replaceForValidation(Map.of(
                TCAspectStackKey.from(new ItemStack(Items.BOW)), new AspectList().add(Aspect.PLANT, 2)));
        try {
            ItemStack damagedBow = new ItemStack(Items.BOW);
            damagedBow.setDamageValue(1);
            AspectList damagedGenerated = TCAspectAssignments.getObjectAspects(damagedBow);
            expectNotNull(damagedGenerated, "generated aspect cache should strip damage components for lookup");
            expectEquals(2, damagedGenerated.getAmount(Aspect.PLANT), "damaged generated lookup keeps base aspects");
            expectEquals(10, damagedGenerated.getAmount(Aspect.AVERSION), "damaged generated lookup keeps bow bonus");
            expectEquals(5, damagedGenerated.getAmount(Aspect.FLIGHT), "damaged generated lookup keeps bow flight bonus");
        } finally {
            TCGeneratedAspectCache.clear();
        }

        expectEquals(0, TCGeneratedAspectCache.size(), "generated aspect cache validation seed must be cleared");
        AspectList noGenerated = TCAspectAssignments.getObjectAspects(new ItemStack(Items.STICK));
        expectNotNull(noGenerated, "legacy no-aspect non-empty stacks return an empty AspectList");
        expectEquals(0, noGenerated.size(), "generated aspect cache remains empty without a generated seed");
    }

    private static void expectDirect(Map<ResourceLocation, AspectList> tags, String path, Amount... expected) {
        expectDirect(tags, Thaumcraft.MODID, path, expected);
    }

    private static void expectDirect(Map<ResourceLocation, AspectList> tags, String namespace, String path, Amount... expected) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, path);
        AspectList actual = tags.get(id);
        expectNotNull(actual, "missing direct assignment " + id);
        Aspect[] actualAspects = actual.getAspects();
        expectEquals(expected.length, actualAspects.length, id + " aspect count");
        for (int i = 0; i < expected.length; i++) {
            expectSame(expected[i].aspect(), actualAspects[i], id + " aspect order " + i);
            expectEquals(expected[i].amount(), actual.getAmount(expected[i].aspect()), id + " amount for " + expected[i].aspect().getTag());
        }
    }

    private static void expectComplexDirect(Map<ResourceLocation, AspectList> tags, String namespace, String path, Amount... expected) {
        expectDirect(tags, namespace, path, expected);
    }

    private static void expectTag(Map<TagKey<Item>, AspectList> tags, String namespace, String path, Amount... expected) {
        TagKey<Item> key = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, path));
        AspectList actual = tags.get(key);
        expectNotNull(actual, "missing tag assignment #" + namespace + ":" + path);
        Aspect[] actualAspects = actual.getAspects();
        expectEquals(expected.length, actualAspects.length, namespace + ":" + path + " aspect count");
        for (int i = 0; i < expected.length; i++) {
            expectSame(expected[i].aspect(), actualAspects[i], namespace + ":" + path + " aspect order " + i);
            expectEquals(expected[i].amount(), actual.getAmount(expected[i].aspect()), namespace + ":" + path + " amount for " + expected[i].aspect().getTag());
        }
    }

    private static void expectEntity(EntityType<?> type, Amount... expected) {
        AspectList actual = TCEntityAspectAssignments.getEntityTypeAspectsForValidation(type);
        expectNotNull(actual, "missing entity aspect assignment " + EntityType.getKey(type));
        Aspect[] actualAspects = actual.getAspects();
        expectEquals(expected.length, actualAspects.length, EntityType.getKey(type) + " aspect count");
        for (int i = 0; i < expected.length; i++) {
            expectSame(expected[i].aspect(), actualAspects[i], EntityType.getKey(type) + " aspect order " + i);
            expectEquals(expected[i].amount(), actual.getAmount(expected[i].aspect()), EntityType.getKey(type) + " amount for " + expected[i].aspect().getTag());
        }
    }

    private static void expectNoEntity(EntityType<?> type) {
        AspectList actual = TCEntityAspectAssignments.getEntityTypeAspectsForValidation(type);
        expect(actual == null, "unexpected entity aspect assignment " + EntityType.getKey(type));
    }

    private static void expectAspectOrder(Aspect[] expected, Aspect[] actual, String message) {
        expectEquals(expected.length, actual.length, message + " length");
        for (int i = 0; i < expected.length; i++) {
            expectSame(expected[i], actual[i], message + " index " + i);
        }
    }

    private static AspectSpec primal(String tag, int color, String chatColor, int blend) {
        return new AspectSpec(tag, color, chatColor, blend, null, null);
    }

    private static AspectSpec compound(String tag, int color, int blend, String component0, String component1) {
        return new AspectSpec(tag, color, null, blend, component0, component1);
    }

    private static Amount amount(Aspect aspect, int amount) {
        return new Amount(aspect, amount);
    }

    private static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }

    private static void expect(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException("Aspect parity validation failed: " + message);
        }
    }

    private static void expectNotNull(Object actual, String message) {
        expect(actual != null, message);
    }

    private static void expectSame(Object expected, Object actual, String message) {
        if (expected != actual) {
            throw new IllegalStateException("Aspect parity validation failed: " + message + " expected same instance");
        }
    }

    private static void expectEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new IllegalStateException("Aspect parity validation failed: " + message + " expected " + expected + " got " + actual);
        }
    }

    private static void expectAssignmentCount(int expectedBase, int actual, String message, boolean allowAddonAssignments) {
        if (allowAddonAssignments) {
            if (actual < expectedBase) {
                throw new IllegalStateException("Aspect parity validation failed: " + message + " expected at least " + expectedBase + " got " + actual);
            }
        } else {
            expectEquals(expectedBase, actual, message);
        }
    }

    private record AspectSpec(String tag, int color, String chatColor, int blend, String component0, String component1) {
        boolean isPrimal() {
            return component0 == null && component1 == null;
        }
    }

    private record Amount(Aspect aspect, int amount) {
    }

    private TCAspectParityValidator() {
    }
}
