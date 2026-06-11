package thaumcraft.common.research;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.LinkedHashSet;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ScanAspect;
import thaumcraft.api.research.ScanningManager;

public final class TCScanningManager {
    private static final double DEFAULT_BLOCK_SCAN_RANGE = 5.0D;
    private static final double DEFAULT_ENTITY_SCAN_RANGE = 9.0D;

    private static TCScannableData activeData = TCScannableData.empty();
    private static boolean serverDynamicScannablesRegistered;

    private TCScanningManager() {
    }

    public static void bootstrap() {
        rebuildScannables();
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new TCScannableReloadListener());
    }

    public static void onServerStarted(ServerStartedEvent event) {
        ensureServerDynamicScannables(event.getServer().registryAccess());
        TCScanningAuditExporter.onServerStarted(event);
    }

    static void reload(TCScannableData data) {
        activeData = data == null ? TCScannableData.empty() : data;
        rebuildScannables();
    }

    public static boolean hasClientPotentialScannable(Entity entity) {
        return !clientPotentialResearchKeys(entity).isEmpty();
    }

    public static boolean hasClientPotentialScannable(BlockState state) {
        return !clientPotentialResearchKeys(state).isEmpty();
    }

    public static boolean hasClientPotentialScannable(ItemStack stack) {
        return !clientPotentialResearchKeys(stack).isEmpty();
    }

    public static List<String> clientPotentialResearchKeys(Entity entity) {
        LinkedHashSet<String> keys = new LinkedHashSet<>();
        if (entity == null) {
            return List.of();
        }

        addClientDataResearchKeys(keys, entity);

        if (entity instanceof ItemEntity itemEntity) {
            addClientStackResearchKeys(keys, itemEntity.getItem());
            return List.copyOf(keys);
        }

        AspectList aspects = AspectHelper.getEntityAspects(entity);
        addAspectResearchKeys(keys, aspects);
        if (hasAspects(aspects)) {
            addResearchKey(keys, "!" + BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
        }

        if (entity instanceof LivingEntity living) {
            for (MobEffectInstance activeEffect : living.getActiveEffects()) {
                addMobEffectResearchKey(keys, activeEffect.getEffect());
            }
        }

        return List.copyOf(keys);
    }

    public static List<String> clientPotentialResearchKeys(BlockState state) {
        LinkedHashSet<String> keys = new LinkedHashSet<>();
        if (state == null) {
            return List.of();
        }

        addClientDataResearchKeys(keys, state);

        if (state.getBlock().asItem() != Items.AIR) {
            addClientStackResearchKeys(keys, new ItemStack(state.getBlock().asItem()));
        }

        return List.copyOf(keys);
    }

    public static List<String> clientPotentialResearchKeys(ItemStack stack) {
        LinkedHashSet<String> keys = new LinkedHashSet<>();
        addClientStackResearchKeys(keys, stack);
        return List.copyOf(keys);
    }

    static void ensureServerDynamicScannables(HolderLookup.Provider registries) {
        if (serverDynamicScannablesRegistered) {
            return;
        }

        registries.lookupOrThrow(Registries.MOB_EFFECT)
                .listElements()
                .forEach(effect -> ScanningManager.addScannableThing(new TCScanMobEffect(effect)));
        registries.lookupOrThrow(Registries.ENCHANTMENT)
                .listElements()
                .forEach(enchantment -> ScanningManager.addScannableThing(new TCScanEnchantment(enchantment)));
        ScanningManager.addScannableThing(new TCScanSky());
        serverDynamicScannablesRegistered = true;

        Thaumcraft.LOGGER.info(
                "Thaumcraft server dynamic scan predicates registered; active predicates: {}",
                ScanningManager.getScannableThings().size()
        );
    }

    public static TCScanResult scanHeld(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();

        if (stack.isEmpty()) {
            stack = player.getOffhandItem();
        }

        if (stack.isEmpty()) {
            return TCScanResult.failure("No item in either hand.");
        }

        return scanItemStack(player, stack);
    }

    public static TCScanResult scanLooking(ServerPlayer player) {
        return scanLooking(player, false);
    }

    public static TCScanResult scanLookingAndMutate(ServerPlayer player) {
        return scanLooking(player, true);
    }

    private static TCScanResult scanLooking(ServerPlayer player, boolean mutate) {
        Entity entity = findLookedEntity(player, DEFAULT_ENTITY_SCAN_RANGE);
        if (entity != null) {
            if (entity instanceof ItemEntity itemEntity) {
                return scanItemEntity(player, itemEntity, mutate);
            }

            AspectList aspects = AspectHelper.getEntityAspects(entity);
            ScanningManager.ScanEvaluation scan = evaluateOrMutate(player, entity, mutate);
            if (!hasAspects(aspects) && !scan.found()) {
                return TCScanResult.failure("No aspects or scan predicate found for entity target: "
                        + BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
            }
            if (mutate && !scan.found() && !scan.suppressMessage()) {
                return TCScanResult.failure("No new knowledge can be learned from entity target: "
                        + BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
            }

            return TCScanResult.success(
                    "entity:" + BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()),
                    entity.getDisplayName().getString(),
                    aspects,
                    false,
                    scan.researchKeys(),
                    scan.suppressMessage()
            );
        }

        HitResult blockHit = TCScanTargeting.rayTrace(player.level(), player, DEFAULT_BLOCK_SCAN_RANGE, true);
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            return scanBlockHit(player, (BlockHitResult) blockHit, mutate);
        }

        ScanningManager.ScanEvaluation skyScan = evaluateOrMutate(player, null, mutate);
        if (skyScan.found()) {
            return TCScanResult.success("sky", "Sky", new AspectList(), false, skyScan.researchKeys(), skyScan.suppressMessage());
        }

        return TCScanResult.failure("No block target found within scan range.");
    }

    public static TCScanResult scanLookingBlock(ServerPlayer player) {
        return scanLookingBlock(player, false);
    }

    private static TCScanResult scanLookingBlock(ServerPlayer player, boolean mutate) {
        HitResult hit = TCScanTargeting.rayTrace(player.level(), player, DEFAULT_BLOCK_SCAN_RANGE, true);

        if (hit.getType() != HitResult.Type.BLOCK) {
            return TCScanResult.failure("No block target found within scan range.");
        }

        return scanBlockHit(player, (BlockHitResult) hit, mutate);
    }

    private static TCScanResult scanBlockHit(ServerPlayer player, BlockHitResult blockHit, boolean mutate) {
        BlockPos pos = blockHit.getBlockPos();
        BlockState state = player.level().getBlockState(pos);
        Block block = state.getBlock();
        ScanningManager.ScanEvaluation scan = evaluateOrMutate(player, pos, mutate);

        if (block.asItem() == Items.AIR) {
            if (scan.found()) {
                return TCScanResult.success(
                        "block:" + BuiltInRegistries.BLOCK.getKey(block),
                        state.getBlock().getName().getString(),
                        new AspectList(),
                        false,
                        scan.researchKeys(),
                        scan.suppressMessage()
                );
            }
            return TCScanResult.failure("Target block has no item form: " + BuiltInRegistries.BLOCK.getKey(block));
        }

        ItemStack stack = new ItemStack(block.asItem());
        AspectLookup aspects = lookupStackAspects(stack);

        if (!hasAspects(aspects.aspects()) && !scan.found()) {
            return TCScanResult.failure("No aspects or scan predicate found for block target: "
                    + BuiltInRegistries.BLOCK.getKey(block));
        }
        if (mutate && !scan.found() && !scan.suppressMessage()) {
            return TCScanResult.failure("No new knowledge can be learned from block target: "
                    + BuiltInRegistries.BLOCK.getKey(block));
        }

        return TCScanResult.success(
                "block:" + BuiltInRegistries.BLOCK.getKey(block),
                state.getBlock().getName().getString(),
                aspects.aspects(),
                aspects.generatedFallback(),
                scan.researchKeys(),
                scan.suppressMessage()
        );
    }

    private static TCScanResult scanItemStack(ServerPlayer player, ItemStack stack) {
        return scanItemLike(player, stack, stack, "item:" + BuiltInRegistries.ITEM.getKey(stack.getItem()), false);
    }

    private static TCScanResult scanItemEntity(ServerPlayer player, ItemEntity itemEntity, boolean mutate) {
        ItemStack stack = itemEntity.getItem();
        if (stack.isEmpty()) {
            return TCScanResult.failure("Dropped item target has no item stack.");
        }

        return scanItemLike(player, stack, itemEntity, "item_entity:" + BuiltInRegistries.ITEM.getKey(stack.getItem()), mutate);
    }

    private static TCScanResult scanItemLike(ServerPlayer player, ItemStack stack, Object scanObject, String objectKey, boolean mutate) {
        String displayName = stack.getHoverName().getString();
        AspectLookup aspects = lookupStackAspects(stack);
        ScanningManager.ScanEvaluation scan = evaluateOrMutate(player, scanObject, mutate);

        if (!hasAspects(aspects.aspects()) && !scan.found()) {
            return TCScanResult.failure("No aspects or scan predicate found for " + objectKey + ".");
        }
        if (mutate && !scan.found() && !scan.suppressMessage()) {
            return TCScanResult.failure("No new knowledge can be learned from " + objectKey + ".");
        }

        return TCScanResult.success(
                objectKey,
                displayName,
                aspects.aspects(),
                aspects.generatedFallback(),
                scan.researchKeys(),
                scan.suppressMessage()
        );
    }

    private static void addClientStackResearchKeys(LinkedHashSet<String> keys, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        addClientDataResearchKeys(keys, stack);

        AspectList directAspects = AspectHelper.getScanAspects(stack);
        addAspectResearchKeys(keys, directAspects);
        if (hasAspects(directAspects) || hasAspects(AspectHelper.generateTags(stack))) {
            addResearchKey(keys, "!" + BuiltInRegistries.ITEM.getKey(stack.getItem()));
        }

        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents != null) {
            for (MobEffectInstance potionEffect : contents.getAllEffects()) {
                addMobEffectResearchKey(keys, potionEffect.getEffect());
            }
        }

        ItemEnchantments enchantments = stack.is(Items.ENCHANTED_BOOK)
                ? stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
                : stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            addEnchantmentResearchKey(keys, entry.getKey());
        }
    }

    private static void addClientDataResearchKeys(LinkedHashSet<String> keys, ItemStack stack) {
        for (TCScannableDefinition definition : activeData.definitions()) {
            if (definition.matchesClientStack(stack)) {
                addResearchKey(keys, clientResearchKey(definition));
            }
        }
    }

    private static void addClientDataResearchKeys(LinkedHashSet<String> keys, BlockState state) {
        for (TCScannableDefinition definition : activeData.definitions()) {
            if (definition.matchesClientBlock(state)) {
                addResearchKey(keys, clientResearchKey(definition));
            }
        }
    }

    private static void addClientDataResearchKeys(LinkedHashSet<String> keys, Entity entity) {
        for (TCScannableDefinition definition : activeData.definitions()) {
            if (definition.matchesClientEntity(entity)) {
                addResearchKey(keys, clientResearchKey(definition));
            }
        }
    }

    private static String clientResearchKey(TCScannableDefinition definition) {
        return switch (definition) {
            case TCScannableDefinition.ItemDefinition item -> item.research();
            case TCScannableDefinition.BlockDefinition block -> block.research();
            case TCScannableDefinition.EntityDefinition entity -> entity.research();
            case TCScannableDefinition.OreDictionaryDefinition ore -> ore.research();
            case TCScannableDefinition.TagDefinition tag -> tag.research();
        };
    }

    private static void addAspectResearchKeys(LinkedHashSet<String> keys, AspectList aspects) {
        if (!hasAspects(aspects)) {
            return;
        }

        for (Aspect aspect : Aspect.aspects.values()) {
            if (aspects.getAmount(aspect) > 0) {
                addResearchKey(keys, "!" + aspect.getTag());
            }
        }
    }

    private static void addMobEffectResearchKey(LinkedHashSet<String> keys, Holder<MobEffect> effect) {
        addResearchKey(keys, "!" + effect.unwrapKey()
                .map(key -> key.location().toString())
                .orElseGet(() -> effect.value().getDescriptionId()));
    }

    private static void addEnchantmentResearchKey(LinkedHashSet<String> keys, Holder<Enchantment> enchantment) {
        addResearchKey(keys, "!" + enchantment.unwrapKey()
                .map(key -> key.location().toString())
                .orElseGet(() -> enchantment.value().description().getString()));
    }

    private static void addResearchKey(LinkedHashSet<String> keys, String key) {
        String normalized = TCPlayerKnowledge.normalizeResearchKey(key);
        if (!normalized.isBlank()) {
            keys.add(normalized);
        }
    }

    private static ScanningManager.ScanEvaluation evaluateOrMutate(ServerPlayer player, Object object, boolean mutate) {
        return mutate ? ScanningManager.scanTheThing(player, object) : ScanningManager.evaluateScan(player, object);
    }

    private static AspectLookup lookupStackAspects(ItemStack stack) {
        AspectList aspects = AspectHelper.getScanAspects(stack);
        boolean generatedFallback = false;

        if (!hasAspects(aspects)) {
            AspectList generated = AspectHelper.generateTags(stack);
            if (hasAspects(generated)) {
                aspects = generated;
                generatedFallback = true;
            }
        }

        return new AspectLookup(aspects == null ? new AspectList() : aspects, generatedFallback);
    }

    private static Entity findLookedEntity(ServerPlayer player, double range) {
        return TCScanTargeting.findPointedEntity(player.level(), player, 1.0D, range, 0.0F, true);
    }

    private static void rebuildScannables() {
        ScanningManager.clearScannableThings();
        registerBaseScannables();

        for (TCScannableDefinition definition : activeData.definitions()) {
            definition.register();
        }

        serverDynamicScannablesRegistered = false;
        Thaumcraft.LOGGER.info(
                "Thaumcraft scan predicates rebuilt: {} data definitions, {} active predicates before server dynamic predicates.",
                activeData.definitions().size(),
                ScanningManager.getScannableThings().size()
        );
    }

    private static void registerBaseScannables() {
        for (Aspect aspect : Aspect.aspects.values()) {
            ScanningManager.addScannableThing(new ScanAspect("!" + aspect.getTag(), aspect));
        }
        ScanningManager.addScannableThing(new TCScanGeneric());
    }

    private static boolean hasAspects(AspectList aspects) {
        return aspects != null && aspects.size() > 0 && aspects.visSize() > 0;
    }

    private record AspectLookup(AspectList aspects, boolean generatedFallback) {
    }
}
