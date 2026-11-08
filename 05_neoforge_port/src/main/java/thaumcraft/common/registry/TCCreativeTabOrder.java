package thaumcraft.common.registry;



import net.minecraft.world.item.CreativeModeTab;

import net.minecraft.world.level.ItemLike;



/**

 * Controls the visible Thaumcraft creative tab order.

 *

 * <p>Do not sort this class alphabetically and do not rely on registry declaration order. The visible order follows

 * the Thaumcraft 6 1.12.2 registration order. Registered compatibility placeholders remain visible so incomplete

 * behavior can be found during parity testing instead of silently disappearing from the catalog.</p>

 */

public final class TCCreativeTabOrder {

    private TCCreativeTabOrder() {

    }



    public static void addThaumcraftItems(CreativeModeTab.Output output) {

        addWorldAndDecorativeBlocks(output);

        addDeviceAndCraftingBlocks(output);

        addLegacyItemSequence(output);

        addLegacySpawnEggs(output);

    }



    private static void addWorldAndDecorativeBlocks(CreativeModeTab.Output output) {

        acceptVisible(output, TCItems.ORE_AMBER.get());

        acceptVisible(output, TCItems.ORE_CINNABAR.get());

        acceptVisible(output, TCItems.ORE_QUARTZ.get());

        acceptVisible(output, TCItems.DEEPSLATE_AMBER_ORE.get());

        acceptVisible(output, TCItems.DEEPSLATE_CINNABAR_ORE.get());

        acceptVisible(output, TCItems.DEEPSLATE_QUARTZ_ORE.get());



        acceptVisible(output, TCItems.CRYSTAL_AER.get());

        acceptVisible(output, TCItems.CRYSTAL_IGNIS.get());

        acceptVisible(output, TCItems.CRYSTAL_AQUA.get());

        acceptVisible(output, TCItems.CRYSTAL_TERRA.get());

        acceptVisible(output, TCItems.CRYSTAL_ORDO.get());

        acceptVisible(output, TCItems.CRYSTAL_PERDITIO.get());

        acceptVisible(output, TCItems.CRYSTAL_VITIUM.get());



        acceptVisible(output, TCItems.STONE_ARCANE.get());

        acceptVisible(output, TCItems.STONE_ARCANE_BRICK.get());

        acceptVisible(output, TCItems.STONE_ANCIENT.get());

        acceptVisible(output, TCItems.STONE_ANCIENT_TILE.get());

        acceptVisible(output, TCItems.STONE_ANCIENT_ROCK.get());

        acceptVisible(output, TCItems.STONE_ANCIENT_GLYPHED.get());

        acceptVisible(output, TCItems.STONE_ANCIENT_DOORWAY.get());

        acceptVisible(output, TCItems.STONE_ELDRITCH_TILE.get());

        acceptVisible(output, TCItems.STONE_POROUS.get());



        acceptVisible(output, TCItems.STAIRS_ARCANE.get());

        acceptVisible(output, TCItems.STAIRS_ARCANE_BRICK.get());

        acceptVisible(output, TCItems.STAIRS_ANCIENT.get());



        acceptVisible(output, TCItems.SLAB_ARCANE_STONE.get());

        acceptVisible(output, TCItems.SLAB_ARCANE_BRICK.get());

        acceptVisible(output, TCItems.SLAB_ANCIENT.get());

        acceptVisible(output, TCItems.SLAB_ELDRITCH.get());



        acceptVisible(output, TCItems.SAPLING_GREATWOOD.get());

        acceptVisible(output, TCItems.SAPLING_SILVERWOOD.get());

        acceptVisible(output, TCItems.LOG_GREATWOOD.get());

        acceptVisible(output, TCItems.LOG_SILVERWOOD.get());

        acceptVisible(output, TCItems.LEAVES_GREATWOOD.get());

        acceptVisible(output, TCItems.LEAVES_SILVERWOOD.get());

        acceptVisible(output, TCItems.SHIMMERLEAF.get());

        acceptVisible(output, TCItems.CINDERPEARL.get());

        acceptVisible(output, TCItems.VISHROOM.get());

        acceptVisible(output, TCItems.PLANK_GREATWOOD.get());

        acceptVisible(output, TCItems.PLANK_SILVERWOOD.get());

        acceptVisible(output, TCItems.STAIRS_GREATWOOD.get());

        acceptVisible(output, TCItems.STAIRS_SILVERWOOD.get());

        acceptVisible(output, TCItems.SLAB_GREATWOOD.get());

        acceptVisible(output, TCItems.SLAB_SILVERWOOD.get());



        acceptVisible(output, TCItems.AMBER_BLOCK.get());

        acceptVisible(output, TCItems.AMBER_BRICK.get());



        acceptVisible(output, TCItems.CANDLE_BLACK.get());

        acceptVisible(output, TCItems.CANDLE_BLUE.get());

        acceptVisible(output, TCItems.CANDLE_BROWN.get());

        acceptVisible(output, TCItems.CANDLE_CYAN.get());

        acceptVisible(output, TCItems.CANDLE_GRAY.get());

        acceptVisible(output, TCItems.CANDLE_GREEN.get());

        acceptVisible(output, TCItems.CANDLE_YELLOW.get());

        acceptVisible(output, TCItems.CANDLE_LIGHTBLUE.get());

        acceptVisible(output, TCItems.CANDLE_LIME.get());

        acceptVisible(output, TCItems.CANDLE_MAGENTA.get());

        acceptVisible(output, TCItems.CANDLE_ORANGE.get());

        acceptVisible(output, TCItems.CANDLE_PINK.get());

        acceptVisible(output, TCItems.CANDLE_PURPLE.get());

        acceptVisible(output, TCItems.CANDLE_RED.get());

        acceptVisible(output, TCItems.CANDLE_SILVER.get());

        acceptVisible(output, TCItems.CANDLE_WHITE.get());

        TCBlocks.BANNERS.forEach((name, block) -> acceptVisible(output, block.get()));



        acceptVisible(output, TCItems.METAL_BRASS.get());

        acceptVisible(output, TCItems.METAL_THAUMIUM.get());

        acceptVisible(output, TCItems.METAL_VOID.get());

        acceptVisible(output, TCItems.METAL_ALCHEMICAL.get());

        acceptVisible(output, TCItems.METAL_ALCHEMICAL_ADVANCED.get());



        addNitorVariants(output);

    }



    private static void addNitorVariants(CreativeModeTab.Output output) {

        acceptVisible(output, TCItems.NITOR_BLACK.get());

        acceptVisible(output, TCItems.NITOR_BLUE.get());

        acceptVisible(output, TCItems.NITOR_BROWN.get());

        acceptVisible(output, TCItems.NITOR_CYAN.get());

        acceptVisible(output, TCItems.NITOR_GRAY.get());

        acceptVisible(output, TCItems.NITOR_GREEN.get());

        acceptVisible(output, TCItems.NITOR_YELLOW.get());

        acceptVisible(output, TCItems.NITOR_LIGHTBLUE.get());

        acceptVisible(output, TCItems.NITOR_LIME.get());

        acceptVisible(output, TCItems.NITOR_MAGENTA.get());

        acceptVisible(output, TCItems.NITOR_ORANGE.get());

        acceptVisible(output, TCItems.NITOR_PINK.get());

        acceptVisible(output, TCItems.NITOR_PURPLE.get());

        acceptVisible(output, TCItems.NITOR_RED.get());

        acceptVisible(output, TCItems.NITOR_SILVER.get());

        acceptVisible(output, TCItems.NITOR_WHITE.get());

    }



    private static void addDeviceAndCraftingBlocks(CreativeModeTab.Output output) {

        acceptVisible(output, TCItems.TABLE_WOOD.get());

        acceptVisible(output, TCItems.TABLE_STONE.get());

        acceptVisible(output, TCItems.ARCANE_WORKBENCH.get());

        acceptVisible(output, TCItems.ARCANE_WORKBENCH_CHARGER.get());

        acceptVisible(output, TCItems.RESEARCH_TABLE.get());

        acceptVisible(output, TCItems.CRUCIBLE.get());

        acceptVisible(output, TCItems.BELLOWS.get());

        acceptVisible(output, TCItems.SMELTER_BASIC.get());

        acceptVisible(output, TCItems.SMELTER_THAUMIUM.get());

        acceptVisible(output, TCItems.SMELTER_VOID.get());

        acceptVisible(output, TCItems.SMELTER_AUX.get());

        acceptVisible(output, TCItems.SMELTER_VENT.get());

        acceptVisible(output, TCItems.ALEMBIC.get());

        acceptVisible(output, TCItems.CENTRIFUGE.get());

        acceptVisible(output, TCItems.JAR_NORMAL.get());

        acceptVisible(output, TCItems.JAR_VOID.get());

        acceptVisible(output, TCItems.JAR_BRAIN.get());

        acceptVisible(output, TCItems.ESSENTIA_TRANSPORT_IN.get());

        acceptVisible(output, TCItems.ESSENTIA_TRANSPORT_OUT.get());

        acceptVisible(output, TCItems.THAUMATORIUM.get());

        acceptVisible(output, TCItems.MIRROR.get());

        acceptVisible(output, TCItems.MIRROR_ESSENTIA.get());

        acceptVisible(output, TCItems.LAMP_ARCANE.get());

        acceptVisible(output, TCItems.LAMP_GROWTH.get());

        acceptVisible(output, TCItems.LAMP_FERTILITY.get());

        acceptVisible(output, TCItems.VOID_SIPHON.get());

        acceptVisible(output, TCItems.SPA.get());
        acceptVisible(output, TCItems.VIS_GENERATOR.get());
        acceptVisible(output, TCItems.VIS_BATTERY.get());
        acceptVisible(output, TCItems.CONDENSER.get());
        acceptVisible(output, TCItems.CONDENSER_LATTICE.get());
        acceptVisible(output, TCItems.CONDENSER_LATTICE_DIRTY.get());
        acceptVisible(output, TCItems.PATTERN_CRAFTER.get());
        acceptVisible(output, TCItems.POTION_SPRAYER.get());
        acceptVisible(output, TCItems.GRAPPLE_GUN.get());
        acceptVisible(output, TCItems.GRAPPLE_GUN_TIP.get());
        acceptVisible(output, TCItems.GRAPPLE_GUN_SPOOL.get());


        acceptVisible(output, TCItems.EVERFULL_URN.get());

        acceptVisible(output, TCItems.WAND_WORKBENCH.get());

        acceptVisible(output, TCItems.INFUSION_MATRIX.get());

        acceptVisible(output, TCItems.PILLAR_ARCANE.get());

        acceptVisible(output, TCItems.PILLAR_ANCIENT.get());

        acceptVisible(output, TCItems.PILLAR_ELDRITCH.get());

        acceptVisible(output, TCItems.MATRIX_SPEED.get());

        acceptVisible(output, TCItems.MATRIX_COST.get());

        acceptVisible(output, TCItems.ARCANE_PEDESTAL.get());

        acceptVisible(output, TCItems.ANCIENT_PEDESTAL.get());

        acceptVisible(output, TCItems.ELDRITCH_PEDESTAL.get());

        acceptVisible(output, TCItems.RECHARGE_PEDESTAL.get());

        acceptVisible(output, TCItems.PAVING_STONE_TRAVEL.get());

        acceptVisible(output, TCItems.PAVING_STONE_BARRIER.get());

        acceptVisible(output, TCItems.LOOT_CRATE_COMMON.get());

        acceptVisible(output, TCItems.LOOT_CRATE_UNCOMMON.get());

        acceptVisible(output, TCItems.LOOT_CRATE_RARE.get());

        acceptVisible(output, TCItems.LOOT_URN_COMMON.get());

        acceptVisible(output, TCItems.LOOT_URN_UNCOMMON.get());

        acceptVisible(output, TCItems.LOOT_URN_RARE.get());

        acceptVisible(output, TCItems.VIS_GENERATOR.get());

        acceptVisible(output, TCItems.HUNGRY_CHEST.get());

        acceptVisible(output, TCItems.DIOPTRA.get());

        acceptVisible(output, TCItems.ARCANE_EAR.get());

        acceptVisible(output, TCItems.ARCANE_EAR_TOGGLE.get());

        acceptVisible(output, TCItems.LEVITATOR.get());



        acceptVisible(output, TCItems.INLAY.get());

        acceptVisible(output, TCItems.STABILIZER.get());

        acceptVisible(output, TCItems.GOLEM_BUILDER.get());

        acceptVisible(output, TCItems.INFERNAL_FURNACE.get());

        acceptVisible(output, TCItems.FLUX_GOO.get());

        acceptVisible(output, TCItems.TAINT_FIBRE.get());

        acceptVisible(output, TCItems.TAINT_CRUST.get());

        acceptVisible(output, TCItems.TAINT_SOIL.get());

        acceptVisible(output, TCItems.TAINT_ROCK.get());

        acceptVisible(output, TCItems.TAINT_GEYSER.get());

        acceptVisible(output, TCItems.TAINT_LOG.get());

        acceptVisible(output, TCItems.TAINT_FEATURE.get());

    }



    private static void addLegacyItemSequence(CreativeModeTab.Output output) {

        acceptVisible(output, TCItems.THAUMONOMICON.get());

        acceptVisible(output, TCItems.CURIO_RITES.get());

        acceptVisible(output, TCItems.CURIO_ARCANE.get());

        acceptVisible(output, TCItems.CURIO_PRESERVED.get());

        acceptVisible(output, TCItems.CURIO_ANCIENT.get());

        acceptVisible(output, TCItems.CURIO_ELDRITCH.get());

        acceptVisible(output, TCItems.CURIO_KNOWLEDGE.get());

        acceptVisible(output, TCItems.CURIO_TWISTED.get());



        acceptVisible(output, TCItems.LOOT_BAG_COMMON.get());

        acceptVisible(output, TCItems.LOOT_BAG_UNCOMMON.get());

        acceptVisible(output, TCItems.LOOT_BAG_RARE.get());



        acceptVisible(output, TCItems.CRIMSON_BLADE.get());

        acceptVisible(output, TCItems.CRIMSON_PLATE_HELM.get());

        acceptVisible(output, TCItems.CRIMSON_PLATE_CHEST.get());

        acceptVisible(output, TCItems.CRIMSON_PLATE_LEGS.get());

        acceptVisible(output, TCItems.CRIMSON_BOOTS.get());

        acceptVisible(output, TCItems.CRIMSON_ROBE_HELM.get());

        acceptVisible(output, TCItems.CRIMSON_ROBE_CHEST.get());

        acceptVisible(output, TCItems.CRIMSON_ROBE_LEGS.get());

        acceptVisible(output, TCItems.CRIMSON_PRAETOR_HELM.get());

        acceptVisible(output, TCItems.CRIMSON_PRAETOR_CHEST.get());

        acceptVisible(output, TCItems.CRIMSON_PRAETOR_LEGS.get());



        acceptVisible(output, TCItems.PECH_WAND.get());



        acceptVisible(output, TCItems.AMBER.get());

        acceptVisible(output, TCItems.QUICKSILVER.get());

        acceptVisible(output, TCItems.QUICKSILVER_NUGGET.get());

        acceptVisible(output, TCItems.THAUMIUM_INGOT.get());

        acceptVisible(output, TCItems.BRASS_INGOT.get());

        acceptVisible(output, TCItems.BRASS_NUGGET.get());

        acceptVisible(output, TCItems.COPPER_NUGGET.get());

        acceptVisible(output, TCItems.TIN_NUGGET.get());

        acceptVisible(output, TCItems.SILVER_NUGGET.get());

        acceptVisible(output, TCItems.LEAD_NUGGET.get());

        acceptVisible(output, TCItems.THAUMIUM_NUGGET.get());

        acceptVisible(output, TCItems.VOID_NUGGET.get());

        acceptVisible(output, TCItems.QUARTZ_NUGGET.get());

        acceptVisible(output, TCItems.RARE_EARTH.get());

        acceptVisible(output, TCItems.FABRIC.get());

        acceptVisible(output, TCItems.VIS_RESONATOR.get());

        acceptVisible(output, TCItems.TALLOW.get());

        acceptVisible(output, TCItems.MECHANISM_SIMPLE.get());

        acceptVisible(output, TCItems.MECHANISM_COMPLEX.get());

        acceptVisible(output, TCItems.BRASS_PLATE.get());

        acceptVisible(output, TCItems.IRON_PLATE.get());

        acceptVisible(output, TCItems.THAUMIUM_PLATE.get());

        acceptVisible(output, TCItems.VOID_PLATE.get());

        acceptVisible(output, TCItems.FILTER.get());

        acceptVisible(output, TCItems.MORPHIC_RESONATOR.get());

        acceptVisible(output, TCItems.SALIS_MUNDUS.get());

        acceptVisible(output, TCItems.MIRRORED_GLASS.get());

        acceptVisible(output, TCItems.HAND_MIRROR.get());

        acceptVisible(output, TCItems.PRIMORDIAL_PEARL.get());



        acceptVisible(output, TCItems.CLUSTER_IRON.get());

        acceptVisible(output, TCItems.CLUSTER_GOLD.get());

        acceptVisible(output, TCItems.CLUSTER_COPPER.get());

        acceptVisible(output, TCItems.CLUSTER_TIN.get());

        acceptVisible(output, TCItems.CLUSTER_SILVER.get());

        acceptVisible(output, TCItems.CLUSTER_LEAD.get());

        acceptVisible(output, TCItems.CLUSTER_CINNABAR.get());



        addCrystalEssenceVariants(output);



        acceptVisible(output, TCItems.BRAIN.get());



        addPhialVariants(output);

        acceptVisible(output, TCItems.PHIAL.get());

        acceptVisible(output, TCItems.JAR_LABEL.get());

        acceptVisible(output, TCItems.JAR_LABEL_ESSENCE.get());



        acceptVisible(output, TCItems.ALUMENTUM.get());

        acceptVisible(output, TCItems.CHUNK_CHICKEN.get());

        acceptVisible(output, TCItems.CHUNK_BEEF.get());

        acceptVisible(output, TCItems.CHUNK_PORK.get());

        acceptVisible(output, TCItems.CHUNK_FISH.get());

        acceptVisible(output, TCItems.CHUNK_RABBIT.get());

        acceptVisible(output, TCItems.CHUNK_MUTTON.get());

        acceptVisible(output, TCItems.BATH_SALTS.get());

        acceptVisible(output, TCItems.BOTTLE_TAINT.get());

        acceptVisible(output, TCItems.LIQUID_DEATH_BUCKET.get());

        acceptVisible(output, TCItems.PURIFYING_FLUID_BUCKET.get());

        acceptVisible(output, TCItems.SANE_SOAP.get());



        acceptVisible(output, TCItems.SCRIBING_TOOLS.get());

        acceptVisible(output, TCItems.THAUMOMETER.get());

        acceptVisible(output, TCItems.SANITY_CHECKER.get());



        acceptVisible(output, TCItems.THAUMIUM_AXE.get());

        acceptVisible(output, TCItems.THAUMIUM_SWORD.get());

        acceptVisible(output, TCItems.THAUMIUM_SHOVEL.get());

        acceptVisible(output, TCItems.THAUMIUM_PICK.get());

        acceptVisible(output, TCItems.THAUMIUM_HOE.get());



        acceptVisible(output, TCItems.VOID_INGOT.get());

        acceptVisible(output, TCItems.VOID_AXE.get());

        acceptVisible(output, TCItems.VOID_SWORD.get());

        acceptVisible(output, TCItems.VOID_SHOVEL.get());

        acceptVisible(output, TCItems.VOID_PICK.get());

        acceptVisible(output, TCItems.VOID_HOE.get());



        acceptVisible(output, TCItems.ELEMENTAL_AXE.get());

        acceptVisible(output, TCItems.ELEMENTAL_SWORD.get());

        acceptVisible(output, TCItems.ELEMENTAL_SHOVEL.get());

        acceptVisible(output, TCItems.ELEMENTAL_PICK.get());

        acceptVisible(output, TCItems.ELEMENTAL_HOE.get());

        acceptVisible(output, TCItems.PRIMAL_CRUSHER.get());



        acceptVisible(output, TCItems.GOGGLES.get());

        acceptVisible(output, TCItems.THAUMIUM_HELM.get());

        acceptVisible(output, TCItems.THAUMIUM_CHEST.get());

        acceptVisible(output, TCItems.THAUMIUM_LEGS.get());

        acceptVisible(output, TCItems.THAUMIUM_BOOTS.get());

        acceptVisible(output, TCItems.VOID_HELM.get());

        acceptVisible(output, TCItems.VOID_CHEST.get());

        acceptVisible(output, TCItems.VOID_LEGS.get());

        acceptVisible(output, TCItems.VOID_BOOTS.get());

        acceptVisible(output, TCItems.CLOTH_BOOTS.get());

        acceptVisible(output, TCItems.CLOTH_LEGS.get());

        acceptVisible(output, TCItems.CLOTH_CHEST.get());

        acceptVisible(output, TCItems.VOID_ROBE_HELM.get());

        acceptVisible(output, TCItems.VOID_ROBE_CHEST.get());

        acceptVisible(output, TCItems.VOID_ROBE_LEGS.get());



        acceptVisible(output, TCItems.TRAVELLER_BOOTS.get());

        acceptVisible(output, TCItems.THAUMIUM_FORTRESS_HELM.get());

        acceptVisible(output, TCItems.THAUMIUM_FORTRESS_HELM_GOGGLES.get());

        acceptVisible(output, TCItems.THAUMIUM_FORTRESS_CHEST.get());

        acceptVisible(output, TCItems.THAUMIUM_FORTRESS_LEGS.get());



        acceptVisible(output, TCItems.GOLEM_BELL.get());

        acceptVisible(output, TCItems.SEAL_PICKUP.get());

        acceptVisible(output, TCItems.SEAL_PICKUP_ADVANCED.get());

        acceptVisible(output, TCItems.SEAL_FILL.get());

        acceptVisible(output, TCItems.SEAL_FILL_ADVANCED.get());

        acceptVisible(output, TCItems.SEAL_EMPTY.get());

        acceptVisible(output, TCItems.SEAL_EMPTY_ADVANCED.get());

        acceptVisible(output, TCItems.SEAL_GUARD.get());

        acceptVisible(output, TCItems.SEAL_GUARD_ADVANCED.get());

        acceptVisible(output, TCItems.SEAL_BREAKER.get());

        acceptVisible(output, TCItems.SEAL_BREAKER_ADVANCED.get());

        acceptVisible(output, TCItems.SEAL_LUMBER.get());

        acceptVisible(output, TCItems.SEAL_PROVIDER.get());

        acceptVisible(output, TCItems.SEAL_STOCK.get());

        acceptVisible(output, TCItems.SEAL_USE.get());

        acceptVisible(output, TCItems.SEAL_BUTCHER.get());

        acceptVisible(output, TCItems.SEAL_HARVEST.get());



        acceptVisible(output, TCItems.CLOUD_RING.get());
        acceptVisible(output, TCItems.CHARM_UNDYING.get());
        acceptVisible(output, TCItems.VERDANT_HEART.get());
        acceptVisible(output, TCItems.VERDANT_HEART_LIFE.get());
        acceptVisible(output, TCItems.VERDANT_HEART_SUSTAIN.get());
        acceptVisible(output, TCItems.TRIPLE_MEAT_TREAT.get());

        acceptVisible(output, TCItems.MASK_GRINNING_DEVIL.get());

        acceptVisible(output, TCItems.MASK_ANGRY_GHOST.get());

        acceptVisible(output, TCItems.MASK_SIPPING_FIEND.get());

        acceptVisible(output, TCItems.MIND_CLOCKWORK_ADVANCED.get());



        acceptVisible(output, TCItems.GOLEM_PRESS_BLUEPRINT.get());

        acceptVisible(output, TCItems.INFUSION_ALTAR_BLUEPRINT.get());

        acceptVisible(output, TCItems.INFUSION_ALTAR_ANCIENT_BLUEPRINT.get());

        acceptVisible(output, TCItems.INFUSION_ALTAR_ELDRITCH_BLUEPRINT.get());



        acceptVisible(output, TCItems.ALCHEMICAL_CONSTRUCT.get());

        acceptVisible(output, TCItems.ESSENTIA_EXPORTER.get());

        acceptVisible(output, TCItems.ESSENTIA_IMPORTER.get());

        acceptVisible(output, TCItems.CAUSALITY_COLLAPSER.get());

        acceptVisible(output, TCItems.VOIDSEER_PEARL.get());
        acceptVisible(output, TCItems.VOID_SEED.get());

        acceptVisible(output, TCItems.FLESH_BLOCK.get());



        acceptVisible(output, TCItems.ARCANE_EAR_TOGGLE.get());

        acceptVisible(output, TCItems.BAUBLE_AMULET.get());
        acceptVisible(output, TCItems.VIS_AMULET.get());

        acceptVisible(output, TCItems.BAUBLE_AMULET_FANCY.get());
        acceptVisible(output, TCItems.BAUBLE_GIRDLE.get());
        acceptVisible(output, TCItems.BAUBLE_GIRDLE_FANCY.get());
        acceptVisible(output, TCItems.BAUBLE_RING.get());
        acceptVisible(output, TCItems.BAUBLE_RING_FANCY.get());
        acceptVisible(output, TCItems.RING_APPRENTICE.get());
        acceptVisible(output, TCItems.BAUBLE_CHARM.get());
        acceptVisible(output, TCItems.CURIOSITY_BAND.get());

        acceptVisible(output, TCItems.BRASS_BRACE.get());



        acceptVisible(output, TCItems.CASTER_BASIC.get());

        acceptVisible(output, TCItems.FOCUS_1.get());

        acceptVisible(output, TCItems.FOCUS_2.get());

        acceptVisible(output, TCItems.FOCUS_3.get());

        acceptVisible(output, TCItems.FOCUS_POUCH.get());

        acceptVisible(output, TCItems.ARCANE_BORE.get());

    }



    private static void addCrystalEssenceVariants(CreativeModeTab.Output output) {

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_AER.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_TERRA.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_IGNIS.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_AQUA.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_ORDO.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_PERDITIO.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_VACUOS.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_LUX.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_MOTUS.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_GELUM.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_VITREUS.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_METALLUM.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_VICTUS.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_MORTUUS.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_POTENTIA.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_PERMUTATIO.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_PRAECANTATIO.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_AURAM.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_ALKIMIA.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_VITIUM.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_TENEBRAE.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_ALIENIS.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_VOLATUS.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_HERBA.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_INSTRUMENTUM.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_FABRICO.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_MACHINA.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_VINCULUM.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_SPIRITUS.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_COGNITIO.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_SENSUS.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_AVERSIO.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_PRAEMUNIO.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_DESIDERIUM.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_EXANIMIS.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_BESTIA.get());

        acceptVisible(output, TCItems.CRYSTAL_ESSENCE_HUMANUS.get());

    }



    private static void addPhialVariants(CreativeModeTab.Output output) {

        acceptVisible(output, TCItems.PHIAL_AER.get());

        acceptVisible(output, TCItems.PHIAL_TERRA.get());

        acceptVisible(output, TCItems.PHIAL_IGNIS.get());

        acceptVisible(output, TCItems.PHIAL_AQUA.get());

        acceptVisible(output, TCItems.PHIAL_ORDO.get());

        acceptVisible(output, TCItems.PHIAL_PERDITIO.get());

        acceptVisible(output, TCItems.PHIAL_VACUOS.get());

        acceptVisible(output, TCItems.PHIAL_LUX.get());

        acceptVisible(output, TCItems.PHIAL_MOTUS.get());

        acceptVisible(output, TCItems.PHIAL_GELUM.get());

        acceptVisible(output, TCItems.PHIAL_VITREUS.get());

        acceptVisible(output, TCItems.PHIAL_METALLUM.get());

        acceptVisible(output, TCItems.PHIAL_VICTUS.get());

        acceptVisible(output, TCItems.PHIAL_MORTUUS.get());

        acceptVisible(output, TCItems.PHIAL_POTENTIA.get());

        acceptVisible(output, TCItems.PHIAL_PERMUTATIO.get());

        acceptVisible(output, TCItems.PHIAL_PRAECANTATIO.get());

        acceptVisible(output, TCItems.PHIAL_AURAM.get());

        acceptVisible(output, TCItems.PHIAL_ALKIMIA.get());

        acceptVisible(output, TCItems.PHIAL_VITIUM.get());

        acceptVisible(output, TCItems.PHIAL_TENEBRAE.get());

        acceptVisible(output, TCItems.PHIAL_ALIENIS.get());

        acceptVisible(output, TCItems.PHIAL_VOLATUS.get());

        acceptVisible(output, TCItems.PHIAL_HERBA.get());

        acceptVisible(output, TCItems.PHIAL_INSTRUMENTUM.get());

        acceptVisible(output, TCItems.PHIAL_FABRICO.get());

        acceptVisible(output, TCItems.PHIAL_MACHINA.get());

        acceptVisible(output, TCItems.PHIAL_VINCULUM.get());

        acceptVisible(output, TCItems.PHIAL_SPIRITUS.get());

        acceptVisible(output, TCItems.PHIAL_COGNITIO.get());

        acceptVisible(output, TCItems.PHIAL_SENSUS.get());

        acceptVisible(output, TCItems.PHIAL_AVERSIO.get());

        acceptVisible(output, TCItems.PHIAL_PRAEMUNIO.get());

        acceptVisible(output, TCItems.PHIAL_DESIDERIUM.get());

        acceptVisible(output, TCItems.PHIAL_EXANIMIS.get());

        acceptVisible(output, TCItems.PHIAL_BESTIA.get());

        acceptVisible(output, TCItems.PHIAL_HUMANUS.get());

    }



    private static void addLegacySpawnEggs(CreativeModeTab.Output output) {

        acceptVisible(output, TCItems.CULTIST_LEADER_SPAWN_EGG.get());

        acceptVisible(output, TCItems.TAINTACLE_GIANT_SPAWN_EGG.get());

        acceptVisible(output, TCItems.BRAINY_ZOMBIE_SPAWN_EGG.get());

        acceptVisible(output, TCItems.GIANT_BRAINY_ZOMBIE_SPAWN_EGG.get());

        acceptVisible(output, TCItems.WISP_SPAWN_EGG.get());

        acceptVisible(output, TCItems.FIREBAT_SPAWN_EGG.get());

        acceptVisible(output, TCItems.PECH_SPAWN_EGG.get());

        acceptVisible(output, TCItems.MIND_SPIDER_SPAWN_EGG.get());

        acceptVisible(output, TCItems.ELDRITCH_GUARDIAN_SPAWN_EGG.get());

        acceptVisible(output, TCItems.CULTIST_KNIGHT_SPAWN_EGG.get());

        acceptVisible(output, TCItems.CULTIST_CLERIC_SPAWN_EGG.get());

        acceptVisible(output, TCItems.ELDRITCH_CRAB_SPAWN_EGG.get());

        acceptVisible(output, TCItems.INHABITED_ZOMBIE_SPAWN_EGG.get());

        acceptVisible(output, TCItems.THAUM_SLIME_SPAWN_EGG.get());

        acceptVisible(output, TCItems.TAINT_CRAWLER_SPAWN_EGG.get());

        acceptVisible(output, TCItems.TAINTACLE_SPAWN_EGG.get());

        acceptVisible(output, TCItems.TAINT_SWARM_SPAWN_EGG.get());

        acceptVisible(output, TCItems.TAINT_SEED_SPAWN_EGG.get());

        acceptVisible(output, TCItems.TAINT_SEED_PRIME_SPAWN_EGG.get());

    }



    private static void acceptVisible(CreativeModeTab.Output output, ItemLike item) {

        output.accept(item);

    }

}
