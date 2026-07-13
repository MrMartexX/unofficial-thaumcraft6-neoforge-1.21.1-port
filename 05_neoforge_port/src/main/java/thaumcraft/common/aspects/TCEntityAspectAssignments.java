package thaumcraft.common.aspects;

import java.util.IdentityHashMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.entities.TCWispEntity;

public final class TCEntityAspectAssignments {
    private static final Map<EntityType<?>, AspectList> LEGACY_VANILLA_ASSIGNMENTS = createAssignments();
    private static final Map<ResourceLocation, AspectList> LEGACY_CUSTOM_ASSIGNMENTS = createCustomAssignments();

    private TCEntityAspectAssignments() {
    }

    public static AspectList getEntityAspects(Entity entity) {
        if (entity instanceof TCWispEntity wisp) {
            Aspect type = Aspect.getAspect(wisp.getWispType());
            return type == null ? null : tags(type, 5, Aspect.AURA, 5, Aspect.FLIGHT, 5);
        }
        AspectList aspects = LEGACY_VANILLA_ASSIGNMENTS.get(entity.getType());
        if (aspects == null) {
            aspects = LEGACY_CUSTOM_ASSIGNMENTS.get(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
            if (aspects == null) {
                return null;
            }
        }

        AspectList copy = aspects.copy();
        if (entity instanceof Creeper creeper && creeper.isPowered()) {
            copy.add(Aspect.ENERGY, 15);
        }
        return copy;
    }

    public static AspectList getEntityTypeAspectsForValidation(EntityType<?> type) {
        AspectList aspects = LEGACY_VANILLA_ASSIGNMENTS.get(type);
        if (aspects == null) {
            aspects = LEGACY_CUSTOM_ASSIGNMENTS.get(BuiltInRegistries.ENTITY_TYPE.getKey(type));
        }
        return aspects == null ? null : aspects.copy();
    }

    private static Map<EntityType<?>, AspectList> createAssignments() {
        IdentityHashMap<EntityType<?>, AspectList> map = new IdentityHashMap<>();

        // Exact Thaumcraft 6 / Forge 1.12.2 vanilla entity aspect contracts.
        put(map, EntityType.ZOMBIE, tags(Aspect.UNDEAD, 20, Aspect.MAN, 10, Aspect.EARTH, 5));
        put(map, EntityType.HUSK, tags(Aspect.UNDEAD, 20, Aspect.MAN, 10, Aspect.FIRE, 5));
        put(map, EntityType.GIANT, tags(Aspect.UNDEAD, 25, Aspect.MAN, 15, Aspect.EARTH, 10));
        put(map, EntityType.SKELETON, tags(Aspect.UNDEAD, 20, Aspect.MAN, 5, Aspect.EARTH, 5));
        put(map, EntityType.WITHER_SKELETON, tags(Aspect.UNDEAD, 25, Aspect.MAN, 5, Aspect.ENTROPY, 10));
        put(map, EntityType.CREEPER, tags(Aspect.PLANT, 15, Aspect.FIRE, 15));
        put(map, EntityType.HORSE, tags(Aspect.BEAST, 15, Aspect.EARTH, 5, Aspect.AIR, 5));
        put(map, EntityType.DONKEY, tags(Aspect.BEAST, 15, Aspect.EARTH, 5, Aspect.AIR, 5));
        put(map, EntityType.MULE, tags(Aspect.BEAST, 15, Aspect.EARTH, 5, Aspect.AIR, 5));
        put(map, EntityType.SKELETON_HORSE, tags(Aspect.BEAST, 5, Aspect.UNDEAD, 10, Aspect.EARTH, 5, Aspect.AIR, 5));
        put(map, EntityType.ZOMBIE_HORSE, tags(Aspect.BEAST, 10, Aspect.UNDEAD, 5, Aspect.EARTH, 5, Aspect.AIR, 5));
        put(map, EntityType.PIG, tags(Aspect.BEAST, 10, Aspect.EARTH, 10, Aspect.DESIRE, 5));
        put(map, EntityType.EXPERIENCE_ORB, tags(Aspect.MIND, 10));
        put(map, EntityType.SHEEP, tags(Aspect.BEAST, 10, Aspect.EARTH, 10));
        put(map, EntityType.COW, tags(Aspect.BEAST, 15, Aspect.EARTH, 15));
        put(map, EntityType.MOOSHROOM, tags(Aspect.BEAST, 15, Aspect.PLANT, 15, Aspect.EARTH, 15));
        put(map, EntityType.SNOW_GOLEM, tags(Aspect.COLD, 10, Aspect.MAN, 5, Aspect.MECHANISM, 5, Aspect.MAGIC, 5));
        put(map, EntityType.OCELOT, tags(Aspect.BEAST, 10, Aspect.ENTROPY, 10));
        put(map, EntityType.CHICKEN, tags(Aspect.BEAST, 5, Aspect.FLIGHT, 5, Aspect.AIR, 5));
        put(map, EntityType.SQUID, tags(Aspect.BEAST, 5, Aspect.WATER, 10));
        put(map, EntityType.WOLF, tags(Aspect.BEAST, 15, Aspect.EARTH, 10, Aspect.AVERSION, 5));
        put(map, EntityType.BAT, tags(Aspect.BEAST, 5, Aspect.FLIGHT, 5, Aspect.DARKNESS, 5));
        put(map, EntityType.SPIDER, tags(Aspect.BEAST, 10, Aspect.ENTROPY, 10, Aspect.TRAP, 10));
        put(map, EntityType.SLIME, tags(Aspect.LIFE, 10, Aspect.WATER, 10, Aspect.ALCHEMY, 5));
        put(map, EntityType.GHAST, tags(Aspect.UNDEAD, 15, Aspect.FIRE, 15));
        put(map, EntityType.ZOMBIFIED_PIGLIN, tags(Aspect.UNDEAD, 15, Aspect.FIRE, 15, Aspect.BEAST, 10));
        put(map, EntityType.ENDERMAN, tags(Aspect.ELDRITCH, 10, Aspect.MOTION, 15, Aspect.DESIRE, 5));
        put(map, EntityType.CAVE_SPIDER, tags(Aspect.BEAST, 5, Aspect.DEATH, 10, Aspect.TRAP, 10));
        put(map, EntityType.SILVERFISH, tags(Aspect.BEAST, 5, Aspect.EARTH, 10));
        put(map, EntityType.BLAZE, tags(Aspect.ELDRITCH, 5, Aspect.FIRE, 15, Aspect.FLIGHT, 5));
        put(map, EntityType.MAGMA_CUBE, tags(Aspect.WATER, 5, Aspect.FIRE, 10, Aspect.ALCHEMY, 5));
        put(map, EntityType.ENDER_DRAGON, tags(Aspect.ELDRITCH, 50, Aspect.BEAST, 30, Aspect.ENTROPY, 50, Aspect.FLIGHT, 10));
        put(map, EntityType.WITHER, tags(Aspect.UNDEAD, 50, Aspect.ENTROPY, 25, Aspect.FIRE, 25));
        put(map, EntityType.WITCH, tags(Aspect.MAN, 15, Aspect.MAGIC, 5, Aspect.ALCHEMY, 10));
        put(map, EntityType.VILLAGER, tags(Aspect.MAN, 15));
        put(map, EntityType.IRON_GOLEM, tags(Aspect.METAL, 15, Aspect.MAN, 5, Aspect.MECHANISM, 5, Aspect.MAGIC, 5));
        put(map, EntityType.END_CRYSTAL, tags(Aspect.ELDRITCH, 15, Aspect.AURA, 15, Aspect.LIFE, 15));
        put(map, EntityType.ITEM_FRAME, tags(Aspect.SENSES, 5, Aspect.CRAFT, 5));
        put(map, EntityType.PAINTING, tags(Aspect.SENSES, 10, Aspect.CRAFT, 5));
        put(map, EntityType.GUARDIAN, tags(Aspect.BEAST, 10, Aspect.ELDRITCH, 10, Aspect.WATER, 10));
        put(map, EntityType.ELDER_GUARDIAN, tags(Aspect.BEAST, 10, Aspect.ELDRITCH, 15, Aspect.WATER, 15));
        put(map, EntityType.RABBIT, tags(Aspect.BEAST, 5, Aspect.EARTH, 5, Aspect.MOTION, 5));
        put(map, EntityType.ENDERMITE, tags(Aspect.BEAST, 5, Aspect.ELDRITCH, 5, Aspect.MOTION, 5));
        put(map, EntityType.POLAR_BEAR, tags(Aspect.BEAST, 15, Aspect.COLD, 10));
        put(map, EntityType.SHULKER, tags(Aspect.ELDRITCH, 10, Aspect.TRAP, 5, Aspect.FLIGHT, 5, Aspect.PROTECT, 5));
        put(map, EntityType.EVOKER, tags(Aspect.ELDRITCH, 5, Aspect.MAGIC, 5, Aspect.MAN, 10));
        put(map, EntityType.VINDICATOR, tags(Aspect.AVERSION, 5, Aspect.MAGIC, 5, Aspect.MAN, 10));
        put(map, EntityType.ILLUSIONER, tags(Aspect.SENSES, 5, Aspect.MAGIC, 5, Aspect.MAN, 10));
        put(map, EntityType.LLAMA, tags(Aspect.BEAST, 15, Aspect.WATER, 5));
        put(map, EntityType.PARROT, tags(Aspect.BEAST, 5, Aspect.FLIGHT, 5, Aspect.SENSES, 5));
        put(map, EntityType.STRAY, tags(Aspect.UNDEAD, 20, Aspect.MAN, 5, Aspect.TRAP, 5));
        put(map, EntityType.VEX, tags(Aspect.ELDRITCH, 5, Aspect.FLIGHT, 5, Aspect.MAGIC, 5, Aspect.MAN, 5));

        // Post-1.12 vanilla entities, assigned by nearest legacy Thaumcraft semantics.
        put(map, EntityType.ALLAY, tags(Aspect.SOUL, 10, Aspect.MAGIC, 10, Aspect.FLIGHT, 5));
        put(map, EntityType.ARMADILLO, tags(Aspect.BEAST, 10, Aspect.EARTH, 10, Aspect.PROTECT, 5));
        put(map, EntityType.AXOLOTL, tags(Aspect.BEAST, 5, Aspect.WATER, 10, Aspect.LIFE, 5));
        put(map, EntityType.BEE, tags(Aspect.BEAST, 5, Aspect.FLIGHT, 5, Aspect.PLANT, 5, Aspect.TRAP, 5));
        put(map, EntityType.BOGGED, tags(Aspect.UNDEAD, 20, Aspect.PLANT, 5, Aspect.TRAP, 5));
        put(map, EntityType.BREEZE, tags(Aspect.AIR, 15, Aspect.MOTION, 10, Aspect.FLIGHT, 5));
        put(map, EntityType.CAMEL, tags(Aspect.BEAST, 15, Aspect.EARTH, 10, Aspect.MOTION, 5));
        put(map, EntityType.CAT, tags(Aspect.BEAST, 10, Aspect.ENTROPY, 10));
        put(map, EntityType.COD, tags(Aspect.BEAST, 5, Aspect.WATER, 10));
        put(map, EntityType.DOLPHIN, tags(Aspect.BEAST, 15, Aspect.WATER, 10, Aspect.SENSES, 5));
        put(map, EntityType.DROWNED, tags(Aspect.UNDEAD, 20, Aspect.WATER, 10, Aspect.MAN, 5));
        put(map, EntityType.FOX, tags(Aspect.BEAST, 10, Aspect.ENTROPY, 10, Aspect.DESIRE, 5));
        put(map, EntityType.FROG, tags(Aspect.BEAST, 5, Aspect.WATER, 5, Aspect.EARTH, 5, Aspect.MOTION, 5));
        put(map, EntityType.GLOW_ITEM_FRAME, tags(Aspect.SENSES, 5, Aspect.CRAFT, 5, Aspect.LIGHT, 5));
        put(map, EntityType.GLOW_SQUID, tags(Aspect.BEAST, 5, Aspect.WATER, 10, Aspect.LIGHT, 5));
        put(map, EntityType.GOAT, tags(Aspect.BEAST, 15, Aspect.EARTH, 10, Aspect.AVERSION, 5));
        put(map, EntityType.HOGLIN, tags(Aspect.BEAST, 20, Aspect.FIRE, 10, Aspect.AVERSION, 10));
        put(map, EntityType.PANDA, tags(Aspect.BEAST, 15, Aspect.PLANT, 10, Aspect.EARTH, 5));
        put(map, EntityType.PHANTOM, tags(Aspect.UNDEAD, 10, Aspect.FLIGHT, 10, Aspect.DARKNESS, 10));
        put(map, EntityType.PIGLIN, tags(Aspect.MAN, 10, Aspect.FIRE, 10, Aspect.DESIRE, 10, Aspect.AVERSION, 5));
        put(map, EntityType.PIGLIN_BRUTE, tags(Aspect.MAN, 10, Aspect.FIRE, 10, Aspect.AVERSION, 15, Aspect.DESIRE, 5));
        put(map, EntityType.PILLAGER, tags(Aspect.MAN, 10, Aspect.AVERSION, 10, Aspect.TOOL, 5));
        put(map, EntityType.PUFFERFISH, tags(Aspect.BEAST, 5, Aspect.WATER, 10, Aspect.AVERSION, 5));
        put(map, EntityType.RAVAGER, tags(Aspect.BEAST, 25, Aspect.AVERSION, 15, Aspect.EARTH, 10));
        put(map, EntityType.SALMON, tags(Aspect.BEAST, 5, Aspect.WATER, 10));
        put(map, EntityType.SNIFFER, tags(Aspect.BEAST, 20, Aspect.PLANT, 15, Aspect.SENSES, 10, Aspect.EARTH, 10));
        put(map, EntityType.STRIDER, tags(Aspect.BEAST, 10, Aspect.FIRE, 10, Aspect.MOTION, 5));
        put(map, EntityType.TADPOLE, tags(Aspect.BEAST, 3, Aspect.WATER, 5, Aspect.LIFE, 5));
        put(map, EntityType.TRADER_LLAMA, tags(Aspect.BEAST, 15, Aspect.WATER, 5));
        put(map, EntityType.TROPICAL_FISH, tags(Aspect.BEAST, 5, Aspect.WATER, 10, Aspect.SENSES, 5));
        put(map, EntityType.TURTLE, tags(Aspect.BEAST, 10, Aspect.WATER, 10, Aspect.PROTECT, 5));
        put(map, EntityType.WANDERING_TRADER, tags(Aspect.MAN, 15, Aspect.EXCHANGE, 10, Aspect.DESIRE, 5));
        put(map, EntityType.WARDEN, tags(Aspect.BEAST, 30, Aspect.DARKNESS, 30, Aspect.SENSES, 20, Aspect.AVERSION, 30, Aspect.SOUL, 15));
        put(map, EntityType.ZOMBIE_VILLAGER, tags(Aspect.UNDEAD, 20, Aspect.MAN, 15, Aspect.EARTH, 5));
        put(map, EntityType.ZOGLIN, tags(Aspect.BEAST, 15, Aspect.UNDEAD, 10, Aspect.FIRE, 10, Aspect.AVERSION, 10));

        return Map.copyOf(map);
    }

    private static Map<ResourceLocation, AspectList> createCustomAssignments() {
        HashMap<ResourceLocation, AspectList> map = new HashMap<>();

        // Exact Thaumcraft 6 custom taint mob contracts from legacy ConfigAspects.
        put(map, "thaum_slime", tags(Aspect.LIFE, 5, Aspect.WATER, 5, Aspect.FLUX, 5, Aspect.ALCHEMY, 5));
        put(map, "taintacle", tags(Aspect.FLUX, 15, Aspect.BEAST, 10));
        put(map, "taintacle_tiny", tags(Aspect.FLUX, 5, Aspect.BEAST, 5));
        put(map, "taint_swarm", tags(Aspect.FLUX, 15, Aspect.AIR, 5));
        put(map, "mind_spider", tags(Aspect.FLUX, 5, Aspect.FIRE, 5));
        put(map, "eldritch_guardian", tags(Aspect.ELDRITCH, 20, Aspect.DEATH, 20, Aspect.UNDEAD, 20));

        return Map.copyOf(map);
    }

    private static void put(Map<EntityType<?>, AspectList> map, EntityType<?> type, AspectList aspects) {
        map.put(type, aspects);
    }

    private static void put(Map<ResourceLocation, AspectList> map, String path, AspectList aspects) {
        map.put(ResourceLocation.fromNamespaceAndPath(Thaumcraft.MODID, path), aspects);
    }

    private static AspectList tags(Object... pairs) {
        AspectList list = new AspectList();
        for (int i = 0; i < pairs.length; i += 2) {
            list.add((Aspect) pairs[i], (Integer) pairs[i + 1]);
        }
        return list;
    }
}
