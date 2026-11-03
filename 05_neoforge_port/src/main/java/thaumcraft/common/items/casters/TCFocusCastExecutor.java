package thaumcraft.common.items.casters;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.entities.TCEntityFXNetwork;
import thaumcraft.common.entities.TCFocusCloudEntity;
import thaumcraft.common.entities.TCFocusProjectileEntity;
import thaumcraft.common.entities.TCFocusmineEntity;
import thaumcraft.common.entities.TCSpellbatEntity;
import thaumcraft.common.items.components.TCFocusPackageComponent;
import thaumcraft.common.registry.TCSounds;
import thaumcraft.common.tiles.misc.TCHoleBlockEntity;

public final class TCFocusCastExecutor {
    private static final String ROOT = "root";
    private static final String TOUCH = "thaumcraft.touch";
    private static final String BOLT = "thaumcraft.bolt";
    private static final String PROJECTILE = "thaumcraft.projectile";
    private static final String CLOUD = "thaumcraft.cloud";
    private static final String MINE = "thaumcraft.mine";
    private static final String SPELLBAT = "thaumcraft.spellbat";
    private static final String PLAN = "thaumcraft.plan";
    private static final String SCATTER = "thaumcraft.scatter";
    private static final String SPLITTARGET = "thaumcraft.splittarget";
    private static final String SPLITTRAJECTORY = "thaumcraft.splittrajectory";

    private static final String FIRE = "thaumcraft.fire";
    private static final String FROST = "thaumcraft.frost";
    private static final String AIR = "thaumcraft.air";
    private static final String EARTH = "thaumcraft.earth";
    private static final String FLUX = "thaumcraft.flux";
    private static final String CURSE = "thaumcraft.curse";
    private static final String HEAL = "thaumcraft.heal";
    private static final String BREAK = "thaumcraft.break";
    private static final String EXCHANGE = "thaumcraft.exchange";
    private static final String RIFT = "thaumcraft.rift";

    private TCFocusCastExecutor() {
    }

    public static CastPlan plan(ItemStack focusStack) {
        TCFocusPackageComponent packageData = TCFocusPackageHelper.getPackage(focusStack);
        if (packageData.isEmpty()) {
            return CastPlan.unsupported("missing focus package");
        }
        List<TCFocusPackageHelper.NodeInstance> nodes = TCFocusPackageHelper.decode(packageData.nodes());
        if (nodes.isEmpty()) {
            return CastPlan.unsupported("empty focus package");
        }

        TCFocusPackageHelper.NodeInstance root = findFirst(nodes, ROOT).orElse(null);
        if (root == null) {
            return CastPlan.unsupported("missing ROOT node");
        }

        TCFocusPackageHelper.NodeInstance firstMedium = null;
        TCFocusPackageHelper.NodeInstance firstEffect = null;

        for (TCFocusPackageHelper.NodeInstance node : nodes) {
            String norm = TCFocusElements.normalizeKey(node.key());
            TCFocusElementDefinition def = TCFocusElements.get(norm).orElse(null);
            if (def == null) {
                return CastPlan.unsupported("unknown element: " + node.key());
            }
            if (def.kind() == TCFocusElementDefinition.Kind.MEDIUM && firstMedium == null) {
                firstMedium = node;
            } else if (def.kind() == TCFocusElementDefinition.Kind.EFFECT && firstEffect == null) {
                firstEffect = node;
            }
        }

        if (firstMedium == null) {
            return CastPlan.unsupported("focus package missing medium");
        }
        if (firstEffect == null) {
            return CastPlan.unsupported("focus package missing effect");
        }

        String reason = "VALID";
        if (nodes.size() == 3
                && TOUCH.equals(TCFocusElements.normalizeKey(firstMedium.key()))
                && FIRE.equals(TCFocusElements.normalizeKey(firstEffect.key()))) {
            reason = "ROOT->TOUCH->FIRE";
        }

        return new CastPlan(true, reason, packageData, firstMedium, firstEffect);
    }

    public static boolean canExecute(ItemStack focusStack) {
        return plan(focusStack).supported();
    }

    public static CastResult cast(ServerPlayer player, InteractionHand hand, ItemStack focusStack) {
        CastPlan plan = plan(focusStack);
        if (!plan.supported()) {
            return CastResult.unsupported(plan.reason());
        }

        List<TCFocusPackageHelper.NodeInstance> nodes = TCFocusPackageHelper.decode(plan.packageData().nodes());
        TCFocusPackageHelper.NodeInstance root = findFirst(nodes, ROOT).orElse(null);
        if (root == null) {
            return CastResult.unsupported("missing ROOT node");
        }

        Vec3 source = legacySourceVector(player, hand);
        Vec3 direction = player.getLookAngle().normalize();
        Trajectory baseTrajectory = new Trajectory(source, direction);
        int color = plan.packageData().color();

        List<TCFocusPackageHelper.NodeInstance> rootChildren = getChildren(nodes, root);
        if (rootChildren.isEmpty()) {
            for (TCFocusPackageHelper.NodeInstance n : nodes) {
                if (n.parent() == root.id()) {
                    rootChildren.add(n);
                }
            }
        }
        if (rootChildren.isEmpty() && plan.medium() != null) {
            rootChildren.add(plan.medium());
        }

        boolean anyApplied = false;
        StringBuilder notes = new StringBuilder();

        for (TCFocusPackageHelper.NodeInstance child : rootChildren) {
            CastResult res = executeNodeFromCaster(player, hand, nodes, child, baseTrajectory, color, 1.0F);
            if (res.applied()) {
                anyApplied = true;
            }
            if (!notes.isEmpty()) {
                notes.append("; ");
            }
            notes.append(res.notes());
        }

        return new CastResult(true, anyApplied, notes.toString());
    }

    public static CastResult executeNodeFromCaster(ServerPlayer player, InteractionHand hand, List<TCFocusPackageHelper.NodeInstance> nodes, TCFocusPackageHelper.NodeInstance node, Trajectory trajectory, int color, float power) {
        String norm = TCFocusElements.normalizeKey(node.key());
        TCFocusElementDefinition def = TCFocusElements.get(norm).orElse(null);
        if (def == null) {
            return new CastResult(true, false, "unknown element: " + norm);
        }

        switch (def.kind()) {
            case MOD -> {
                switch (norm) {
                    case SCATTER -> {
                        int forks = clamped(node.settings(), "forks", 2, 2, 10);
                        int cone = clamped(node.settings(), "cone", 10, 10, 360);
                        float scatterPower = power * (2.0F / forks);
                        boolean anyApplied = false;
                        for (int i = 0; i < forks; i++) {
                            Vec3 dV = trajectory.direction();
                            if (forks > 1) {
                                dV = dV.add(
                                        player.getRandom().nextGaussian() * 0.0075D * cone,
                                        player.getRandom().nextGaussian() * 0.0075D * cone,
                                        player.getRandom().nextGaussian() * 0.0075D * cone
                                ).normalize();
                            }
                            Trajectory forkTraj = new Trajectory(trajectory.source(), dV);
                            for (TCFocusPackageHelper.NodeInstance child : getChildren(nodes, node)) {
                                CastResult res = executeNodeFromCaster(player, hand, nodes, child, forkTraj, color, scatterPower);
                                if (res.applied()) {
                                    anyApplied = true;
                                }
                            }
                        }
                        return new CastResult(true, anyApplied, "scatter cast forks=" + forks);
                    }
                    case SPLITTRAJECTORY, SPLITTARGET -> {
                        float splitPower = power * 0.75F;
                        boolean anyApplied = false;
                        for (TCFocusPackageHelper.NodeInstance child : getChildren(nodes, node)) {
                            CastResult res = executeNodeFromCaster(player, hand, nodes, child, trajectory, color, splitPower);
                            if (res.applied()) {
                                anyApplied = true;
                            }
                        }
                        return new CastResult(true, anyApplied, "split cast");
                    }
                    default -> {
                        boolean anyApplied = false;
                        for (TCFocusPackageHelper.NodeInstance child : getChildren(nodes, node)) {
                            CastResult res = executeNodeFromCaster(player, hand, nodes, child, trajectory, color, power);
                            if (res.applied()) {
                                anyApplied = true;
                            }
                        }
                        return new CastResult(true, anyApplied, "mod cast");
                    }
                }
            }
            case MEDIUM -> {
                switch (norm) {
                    case TOUCH -> {
                        FocusTarget target = traceTouchTarget(player, hand);
                        if (target == null) {
                            return new CastResult(true, false, "no touch target");
                        }
                        return executeMediumTargetDownstream(player, nodes, node, target, color, power, true);
                    }
                    case BOLT -> {
                        Vec3 source = trajectory.source();
                        FocusTarget target = traceBoltTarget(player, hand, source);
                        Vec3 end = target.hit();
                        player.level().playSound(null, player.blockPosition(), TCSounds.ZAP.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
                        TCEntityFXNetwork.sendFocusZap(player.serverLevel(), source.x, source.y, source.z, end.x, end.y, end.z, color, 1.0F);
                        return executeMediumTargetDownstream(player, nodes, node, target, color, power, true);
                    }
                    case PROJECTILE -> {
                        playFireCastSound(player);
                        int speedSetting = clamped(node.settings(), "speed", 3, 1, 5);
                        float speed = speedSetting / 3.0F;
                        int option = clamped(node.settings(), "option", 0, 0, 3);
                        String remaining = getRemainingNodesEncoded(nodes, node.id());
                        TCFocusProjectileEntity proj = new TCFocusProjectileEntity(
                                player.level(), player, speed, trajectory.direction(), option, remaining, power, color
                        );
                        player.level().addFreshEntity(proj);
                        return new CastResult(true, true, "projectile launched");
                    }
                    case CLOUD -> {
                        playFireCastSound(player);
                        int radius = clamped(node.settings(), "radius", 2, 1, 3);
                        int duration = clamped(node.settings(), "duration", 10, 5, 30);
                        String remaining = getRemainingNodesEncoded(nodes, node.id());
                        Vec3 targetPos = traceCloudTargetPos(player, hand);
                        float cloudPower = power * 0.5F;
                        TCFocusCloudEntity cloud = new TCFocusCloudEntity(
                                player.level(), targetPos, player, radius, duration, remaining, cloudPower, color
                        );
                        player.level().addFreshEntity(cloud);
                        return new CastResult(true, true, "cloud spawned");
                    }
                    case MINE -> {
                        playFireCastSound(player);
                        int targetFriend = clamped(node.settings(), "target", 0, 0, 1);
                        String remaining = getRemainingNodesEncoded(nodes, node.id());
                        TCFocusmineEntity mine = new TCFocusmineEntity(
                                player.level(), player, trajectory.direction(), targetFriend == 1, remaining, power
                        );
                        player.level().addFreshEntity(mine);
                        return new CastResult(true, true, "mine placed");
                    }
                    case SPELLBAT -> {
                        playFireCastSound(player);
                        int targetFriend = clamped(node.settings(), "target", 0, 0, 1);
                        String remaining = getRemainingNodesEncoded(nodes, node.id());
                        float batPower = power * 0.33F;
                        TCSpellbatEntity bat = new TCSpellbatEntity(
                                player.level(), player, targetFriend == 1, remaining, batPower
                        );
                        bat.setPos(player.getX(), player.getEyeY(), player.getZ());
                        player.level().addFreshEntity(bat);
                        return new CastResult(true, true, "spellbat summoned");
                    }
                    case PLAN -> {
                        FocusTarget target = traceTouchTarget(player, hand);
                        if (target == null || target.blockPos() == null || target.side() == null) {
                            return new CastResult(true, false, "no plan target block");
                        }
                        BlockPos center = target.blockPos();
                        Direction side = target.side();
                        boolean appliedAny = false;
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                BlockPos offsetPos = switch (side.getAxis()) {
                                    case Y -> center.offset(dx, 0, dy);
                                    case Z -> center.offset(dx, dy, 0);
                                    case X -> center.offset(0, dx, dy);
                                };
                                FocusTarget offsetTarget = FocusTarget.block(Vec3.atCenterOf(offsetPos), offsetPos, side);
                                CastResult res = executeMediumTargetDownstream(player, nodes, node, offsetTarget, color, power, false);
                                if (res.applied()) {
                                    appliedAny = true;
                                }
                            }
                        }
                        return new CastResult(true, appliedAny, "plan executed across 3x3 plane");
                    }
                    default -> {
                        return new CastResult(true, false, "unsupported medium: " + norm);
                    }
                }
            }
            case EFFECT -> {
                FocusTarget target = traceTouchTarget(player, hand);
                if (target != null) {
                    executeNodeAtTarget(player.serverLevel(), player, target, nodes, node, power, color);
                    return new CastResult(true, true, "direct effect applied");
                }
                return new CastResult(true, false, "no target");
            }
            default -> {
                boolean applied = false;
                for (TCFocusPackageHelper.NodeInstance child : getChildren(nodes, node)) {
                    CastResult res = executeNodeFromCaster(player, hand, nodes, child, trajectory, color, power);
                    if (res.applied()) {
                        applied = true;
                    }
                }
                return new CastResult(true, applied, "node cast");
            }
        }
    }

    private static CastResult executeMediumTargetDownstream(ServerPlayer player, List<TCFocusPackageHelper.NodeInstance> nodes, TCFocusPackageHelper.NodeInstance mediumNode, FocusTarget target, int color, float power, boolean sendParticles) {
        List<TCFocusPackageHelper.NodeInstance> children = getChildren(nodes, mediumNode);
        if (children.isEmpty()) {
            return new CastResult(true, false, "no downstream nodes");
        }

        if (sendParticles) {
            boolean hasFire = getDescendants(nodes, mediumNode.id()).stream()
                    .anyMatch(n -> FIRE.equals(TCFocusElements.normalizeKey(n.key())));
            if (hasFire) {
                player.serverLevel().sendParticles(
                        ParticleTypes.FLAME,
                        target.hit().x,
                        target.hit().y,
                        target.hit().z,
                        8,
                        0.08D,
                        0.08D,
                        0.08D,
                        0.01D
                );
            }
        }

        for (TCFocusPackageHelper.NodeInstance child : children) {
            executeNodeAtTarget(player.serverLevel(), player, target, nodes, child, power, color);
        }

        String targetNotes = target.entity() == null
                ? "block=" + target.blockPos() + ", side=" + target.side()
                : "entity=" + BuiltInRegistries.ENTITY_TYPE.getKey(target.entity().getType());
        String effNames = children.stream().map(TCFocusPackageHelper.NodeInstance::key).reduce((a, b) -> a + "," + b).orElse("");
        return new CastResult(true, true, targetNotes + ", effect=" + effNames);
    }

    public static List<TCFocusPackageHelper.NodeInstance> getChildren(List<TCFocusPackageHelper.NodeInstance> nodes, TCFocusPackageHelper.NodeInstance parent) {
        ArrayList<TCFocusPackageHelper.NodeInstance> children = new ArrayList<>();
        if (parent == null || nodes == null) {
            return children;
        }
        for (int childId : parent.children()) {
            for (TCFocusPackageHelper.NodeInstance n : nodes) {
                if (n.id() == childId && !children.contains(n)) {
                    children.add(n);
                }
            }
        }
        for (TCFocusPackageHelper.NodeInstance n : nodes) {
            if (n.parent() == parent.id() && !children.contains(n)) {
                children.add(n);
            }
        }
        return children;
    }

    public static List<TCFocusPackageHelper.NodeInstance> getDescendants(List<TCFocusPackageHelper.NodeInstance> nodes, int startId) {
        ArrayList<TCFocusPackageHelper.NodeInstance> descendants = new ArrayList<>();
        if (nodes == null) {
            return descendants;
        }
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(startId);
        visited.add(startId);

        while (!queue.isEmpty()) {
            int currentId = queue.poll();
            for (TCFocusPackageHelper.NodeInstance n : nodes) {
                if (n.id() == currentId) {
                    continue;
                }
                boolean isChild = n.parent() == currentId;
                if (!isChild) {
                    for (TCFocusPackageHelper.NodeInstance cur : nodes) {
                        if (cur.id() == currentId && cur.children().contains(n.id())) {
                            isChild = true;
                            break;
                        }
                    }
                }
                if (isChild && visited.add(n.id())) {
                    descendants.add(n);
                    queue.add(n.id());
                }
            }
        }
        return descendants;
    }

    public static String getRemainingNodesEncoded(List<TCFocusPackageHelper.NodeInstance> nodes, int parentId) {
        List<TCFocusPackageHelper.NodeInstance> descendants = getDescendants(nodes, parentId);
        if (descendants.isEmpty()) {
            ArrayList<TCFocusPackageHelper.NodeInstance> fallback = new ArrayList<>();
            for (TCFocusPackageHelper.NodeInstance n : nodes) {
                TCFocusElementDefinition def = TCFocusElements.get(n.key()).orElse(null);
                if (def != null && def.kind() == TCFocusElementDefinition.Kind.EFFECT) {
                    fallback.add(n);
                }
            }
            return TCFocusPackageHelper.encode(fallback);
        }
        return TCFocusPackageHelper.encode(descendants);
    }

    public static void executeRemainingNodesAt(Level level, LivingEntity caster, HitResult hit, String remainingNodes, float power) {
        if (remainingNodes == null || remainingNodes.isBlank() || level.isClientSide() || hit == null) {
            return;
        }
        List<TCFocusPackageHelper.NodeInstance> nodes = TCFocusPackageHelper.decode(remainingNodes);
        if (nodes.isEmpty()) {
            return;
        }

        List<TCFocusPackageHelper.NodeInstance> entryNodes = new ArrayList<>();
        for (TCFocusPackageHelper.NodeInstance n : nodes) {
            boolean hasParentInList = nodes.stream().anyMatch(other -> other.id() == n.parent());
            if (!hasParentInList) {
                entryNodes.add(n);
            }
        }
        if (entryNodes.isEmpty()) {
            entryNodes.addAll(nodes);
        }

        FocusTarget target = FocusTarget.fromHit(hit);

        for (TCFocusPackageHelper.NodeInstance entry : entryNodes) {
            executeNodeAtTarget(level, caster, target, nodes, entry, power, 0xFFFFFF);
        }
    }

    public static void executeNodeAtTarget(Level level, LivingEntity caster, FocusTarget target, List<TCFocusPackageHelper.NodeInstance> nodes, TCFocusPackageHelper.NodeInstance node, float power, int color) {
        if (node == null || target == null) {
            return;
        }
        String norm = TCFocusElements.normalizeKey(node.key());
        TCFocusElementDefinition def = TCFocusElements.get(norm).orElse(null);
        if (def == null) {
            return;
        }

        switch (def.kind()) {
            case EFFECT -> {
                executeSingleEffect(level, caster, target.asHitResult(), norm, node.settings(), power);
                for (TCFocusPackageHelper.NodeInstance child : getChildren(nodes, node)) {
                    executeNodeAtTarget(level, caster, target, nodes, child, power, color);
                }
            }
            case MEDIUM -> {
                String remaining = getRemainingNodesEncoded(nodes, node.id());
                switch (norm) {
                    case CLOUD -> {
                        int radius = clamped(node.settings(), "radius", 2, 1, 3);
                        int duration = clamped(node.settings(), "duration", 10, 5, 30);
                        float cloudPower = power * 0.5F;
                        TCFocusCloudEntity cloud = new TCFocusCloudEntity(
                                level, target.hit(), caster, radius, duration, remaining, cloudPower, color
                        );
                        level.addFreshEntity(cloud);
                    }
                    case MINE -> {
                        int targetFriend = clamped(node.settings(), "target", 0, 0, 1);
                        TCFocusmineEntity mine = new TCFocusmineEntity(
                                level, target.hit(), caster, targetFriend == 1, remaining, power
                        );
                        level.addFreshEntity(mine);
                    }
                    case SPELLBAT -> {
                        int targetFriend = clamped(node.settings(), "target", 0, 0, 1);
                        float batPower = power * 0.33F;
                        TCSpellbatEntity bat = new TCSpellbatEntity(
                                level, caster, targetFriend == 1, remaining, batPower
                        );
                        bat.setPos(target.hit().x, target.hit().y, target.hit().z);
                        level.addFreshEntity(bat);
                    }
                    case PROJECTILE -> {
                        int speedSetting = clamped(node.settings(), "speed", 3, 1, 5);
                        float speed = speedSetting / 3.0F;
                        int option = clamped(node.settings(), "option", 0, 0, 3);
                        Vec3 dir = target.side() != null ? Vec3.atLowerCornerOf(target.side().getNormal()) : (caster != null ? caster.getLookAngle() : new Vec3(0, 1, 0));
                        TCFocusProjectileEntity proj = new TCFocusProjectileEntity(
                                level, target.hit().add(dir.scale(0.2D)), caster, speed, dir, option, remaining, power, color
                        );
                        level.addFreshEntity(proj);
                    }
                    default -> {
                        for (TCFocusPackageHelper.NodeInstance child : getChildren(nodes, node)) {
                            executeNodeAtTarget(level, caster, target, nodes, child, power, color);
                        }
                    }
                }
            }
            case MOD -> {
                switch (norm) {
                    case SPLITTARGET, SPLITTRAJECTORY -> {
                        float splitPower = power * 0.75F;
                        for (TCFocusPackageHelper.NodeInstance child : getChildren(nodes, node)) {
                            executeNodeAtTarget(level, caster, target, nodes, child, splitPower, color);
                        }
                    }
                    case SCATTER -> {
                        int forks = clamped(node.settings(), "forks", 2, 2, 10);
                        float scatterPower = power * (2.0F / forks);
                        for (int i = 0; i < forks; i++) {
                            for (TCFocusPackageHelper.NodeInstance child : getChildren(nodes, node)) {
                                executeNodeAtTarget(level, caster, target, nodes, child, scatterPower, color);
                            }
                        }
                    }
                    default -> {
                        for (TCFocusPackageHelper.NodeInstance child : getChildren(nodes, node)) {
                            executeNodeAtTarget(level, caster, target, nodes, child, power, color);
                        }
                    }
                }
            }
            case ROOT -> {
                for (TCFocusPackageHelper.NodeInstance child : getChildren(nodes, node)) {
                    executeNodeAtTarget(level, caster, target, nodes, child, power, color);
                }
            }
        }
    }

    private static void executeSingleEffect(Level level, LivingEntity caster, HitResult hit, String effectKey, Map<String, Integer> settings, float power) {
        if (hit.getType() == HitResult.Type.ENTITY && hit instanceof EntityHitResult ehr) {
            Entity target = ehr.getEntity();
            executeEffectOnEntity(level, caster, target, effectKey, settings, power);
        } else if (hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult bhr) {
            executeEffectOnBlock(level, caster, bhr.getBlockPos(), bhr.getDirection(), effectKey, settings, power);
        }
    }

    private static void executeEffectOnEntity(Level level, LivingEntity caster, Entity target, String effectKey, Map<String, Integer> settings, float power) {
        int p = clamped(settings, "power", 1, 1, 5);
        int d = clamped(settings, "duration", 0, 0, 10);
        DamageSource dmgSource = caster != null ? level.damageSources().indirectMagic(caster, caster) : level.damageSources().magic();

        switch (effectKey) {
            case FIRE -> {
                if (!target.fireImmune()) {
                    target.hurt(caster != null ? level.damageSources().source(DamageTypes.FIREBALL, caster, caster) : level.damageSources().inFire(), (3.0F + p) * power);
                    int sec = Math.round((1.0F + d * d) * power);
                    if (sec > 0) target.igniteForSeconds(sec);
                }
            }
            case FROST -> {
                target.hurt(dmgSource, (3.0F + p) * power);
                if (target instanceof LivingEntity living) {
                    int ticks = Math.max(20, 20 * d);
                    int amp = (int)(1.0F + p * power / 3.0F);
                    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ticks, amp));
                }
            }
            case AIR -> {
                float dmg = (1.0F + p) * power;
                target.hurt(dmgSource, dmg);
                if (target instanceof LivingEntity living) {
                    Vec3 look = caster != null ? caster.getLookAngle() : new Vec3(0, 0.2D, 0);
                    living.knockback(dmg * 0.35F, -look.x, -look.z);
                }
            }
            case EARTH -> {
                target.hurt(caster != null ? level.damageSources().mobAttack(caster) : level.damageSources().generic(), 2.0F * p * power);
            }
            case FLUX -> {
                target.hurt(dmgSource, (3.0F + p) * power);
            }
            case CURSE -> {
                target.hurt(dmgSource, (1.0F + p) * power);
                if (target instanceof LivingEntity living) {
                    int ticks = Math.max(20, 20 * d);
                    int amplifier = Math.max(0, (int)(p * power / 2.0F));
                    living.addEffect(new MobEffectInstance(MobEffects.POISON, ticks, amplifier));
                    float chance = 0.85F;
                    if (level.random.nextFloat() < chance) {
                        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ticks, amplifier));
                        chance -= 0.15F;
                    }
                    if (level.random.nextFloat() < chance) {
                        living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, ticks, amplifier));
                        chance -= 0.15F;
                    }
                    if (level.random.nextFloat() < chance) {
                        living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, ticks * 2, amplifier));
                        chance -= 0.15F;
                    }
                    if (level.random.nextFloat() < chance) {
                        living.addEffect(new MobEffectInstance(MobEffects.HUNGER, ticks * 3, amplifier));
                        chance -= 0.15F;
                    }
                    if (level.random.nextFloat() < chance) {
                        living.addEffect(new MobEffectInstance(MobEffects.UNLUCK, ticks * 3, amplifier));
                    }
                }
            }
            case HEAL -> {
                if (target instanceof LivingEntity living) {
                    if (living.isInvertedHealAndHarm()) {
                        living.hurt(dmgSource, p * power * 1.5F);
                    } else {
                        living.heal(p * power);
                    }
                }
            }
        }
    }

    private static void executeEffectOnBlock(Level level, LivingEntity caster, BlockPos pos, Direction side, String effectKey, Map<String, Integer> settings, float power) {
        int p = clamped(settings, "power", 1, 1, 5);
        int d = clamped(settings, "duration", 0, 0, 10);

        switch (effectKey) {
            case FIRE -> {
                BlockPos firePos = pos.relative(side);
                if (level.isEmptyBlock(firePos)) {
                    level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 11);
                }
            }
            case FROST -> {
                BlockState state = level.getBlockState(pos);
                if (state.getFluidState().is(Fluids.WATER)) {
                    level.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                } else if (state.getFluidState().is(Fluids.LAVA)) {
                    level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                } else {
                    BlockPos above = pos.relative(side);
                    if (side == Direction.UP && level.isEmptyBlock(above)) {
                        level.setBlock(above, Blocks.SNOW.defaultBlockState(), 3);
                    }
                }
            }
            case EARTH, BREAK -> {
                BlockState state = level.getBlockState(pos);
                if (!state.isAir() && state.getDestroySpeed(level, pos) >= 0.0F) {
                    if (EARTH.equals(effectKey) && state.getDestroySpeed(level, pos) > 2.0F * p * power / 25.0F) return;
                    if (caster instanceof ServerPlayer sp) {
                        sp.gameMode.destroyBlock(pos);
                    } else {
                        level.destroyBlock(pos, true);
                    }
                }
            }
            case EXCHANGE -> {
                if (caster instanceof ServerPlayer player) {
                    TCExchangeQueue.enqueue(player, pos, side,
                            clamped(settings, "silk", 0, 0, 1) > 0,
                            clamped(settings, "fortune", 0, 0, 4));
                }
            }
            case RIFT -> {
                int depth = clamped(settings, "depth", 8, 8, 32);
                int duration = clamped(settings, "duration", 2, 2, 10);
                TCHoleBlockEntity.createHole(level, pos, side, (byte) Math.round(depth * power), 20 * duration);
            }
        }
    }

    static CastResult executeTouchFireAtEntity(ServerPlayer player, ItemStack focusStack, Entity entity) {
        CastPlan plan = plan(focusStack);
        if (!plan.supported()) {
            return CastResult.unsupported(plan.reason());
        }
        return executeTouchAtTarget(
                player,
                plan,
                FocusTarget.entity(entity, entity.position().add(0.0D, entity.getBbHeight() * 0.5D, 0.0D)),
                false
        );
    }

    static CastResult executeTouchFireAtBlock(ServerPlayer player, ItemStack focusStack, BlockPos blockPos, Direction side) {
        CastPlan plan = plan(focusStack);
        if (!plan.supported()) {
            return CastResult.unsupported(plan.reason());
        }
        Vec3 hit = Vec3.atCenterOf(blockPos.relative(side));
        return executeTouchAtTarget(player, plan, FocusTarget.block(hit, blockPos, side), false);
    }

    private static CastResult executeTouchAtTarget(ServerPlayer player, CastPlan plan, FocusTarget target, boolean sendParticles) {
        List<TCFocusPackageHelper.NodeInstance> nodes = TCFocusPackageHelper.decode(plan.packageData().nodes());
        TCFocusPackageHelper.NodeInstance medium = plan.medium();
        if (medium != null) {
            return executeMediumTargetDownstream(player, nodes, medium, target, plan.packageData().color(), 1.0F, sendParticles);
        }
        if (plan.effect() != null) {
            String effKey = TCFocusElements.normalizeKey(plan.effect().key());
            executeSingleEffect(player.level(), player, target.asHitResult(), effKey, plan.effect().settings(), 1.0F);
            String targetNotes = target.entity() == null
                    ? "block=" + target.blockPos() + ", side=" + target.side()
                    : "entity=" + BuiltInRegistries.ENTITY_TYPE.getKey(target.entity().getType());
            return new CastResult(true, true, targetNotes + ", effect=" + effKey);
        }
        return new CastResult(true, false, "no effect to execute");
    }

    public static Vec3 legacySourceVector(ServerPlayer player, InteractionHand hand) {
        boolean mainHand = hand == InteractionHand.MAIN_HAND;
        double yaw = (player.getYRot() - 0.5F) / 180.0F * Math.PI;
        double x = -Math.cos(yaw) * 0.2D * (mainHand ? 1.0D : -1.0D);
        double z = -Math.sin(yaw) * 0.3D * (mainHand ? 1.0D : -1.0D);
        return new Vec3(player.getX(), player.getY(), player.getZ())
                .add(x, player.getEyeHeight() - 0.4D, z)
                .add(player.getLookAngle());
    }

    private static FocusTarget traceTouchTarget(ServerPlayer player, InteractionHand hand) {
        ServerLevel level = player.serverLevel();
        Vec3 source = legacySourceVector(player, hand);
        Vec3 direction = player.getLookAngle().normalize();
        double range = Math.max(4.5D, player.blockInteractionRange());
        Vec3 end = source.add(direction.scale(range));

        EntityHit entityHit = traceEntity(level, player, source, end, range).orElse(null);
        if (entityHit != null) {
            return FocusTarget.entity(entityHit.entity(), entityHit.hit());
        }

        BlockHitResult blockHit = level.clip(new ClipContext(
                source,
                end,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            return FocusTarget.block(blockHit.getLocation(), blockHit.getBlockPos(), blockHit.getDirection());
        }
        return null;
    }

    private static FocusTarget traceBoltTarget(ServerPlayer player, InteractionHand hand, Vec3 source) {
        ServerLevel level = player.serverLevel();
        Vec3 direction = player.getLookAngle().normalize();
        double range = 16.0D;
        Vec3 end = source.add(direction.scale(range));

        EntityHit entityHit = traceEntity(level, player, source, end, range).orElse(null);
        if (entityHit != null) {
            return FocusTarget.entity(entityHit.entity(), entityHit.hit());
        }

        BlockHitResult blockHit = level.clip(new ClipContext(
                source,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            return FocusTarget.block(blockHit.getLocation(), blockHit.getBlockPos(), blockHit.getDirection());
        }
        return FocusTarget.block(end, BlockPos.containing(end), Direction.UP);
    }

    private static Vec3 traceCloudTargetPos(ServerPlayer player, InteractionHand hand) {
        ServerLevel level = player.serverLevel();
        Vec3 source = legacySourceVector(player, hand);
        Vec3 end = source.add(player.getLookAngle().scale(8.0D));
        BlockHitResult hit = level.clip(new ClipContext(
                source, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player
        ));
        return hit.getType() == HitResult.Type.BLOCK ? hit.getLocation() : end;
    }

    private static Optional<EntityHit> traceEntity(ServerLevel level, ServerPlayer player, Vec3 source, Vec3 end, double range) {
        AABB search = new AABB(source, end).inflate(1.0D);
        double maxDistance = range * range;
        EntityHit best = null;
        for (Entity entity : level.getEntities(player, search, entity -> !entity.isSpectator() && entity.isPickable())) {
            AABB box = entity.getBoundingBox().inflate(Math.max(0.25D, entity.getPickRadius() + 0.25D));
            Optional<Vec3> hit = box.clip(source, end);
            if (box.contains(source)) {
                hit = Optional.of(source);
            }
            if (hit.isEmpty()) {
                continue;
            }
            double distance = source.distanceToSqr(hit.get());
            if (distance <= maxDistance && (best == null || distance < best.distanceSqr())) {
                best = new EntityHit(entity, hit.get(), distance);
            }
        }
        return Optional.ofNullable(best);
    }

    private static void playFireCastSound(ServerPlayer player) {
        player.level().playSound(
                null,
                player.getX(),
                player.getY() + player.getEyeHeight(),
                player.getZ(),
                SoundEvents.FIRECHARGE_USE,
                SoundSource.PLAYERS,
                1.0F,
                1.0F + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.05F
        );
    }

    private static Optional<TCFocusPackageHelper.NodeInstance> findFirst(List<TCFocusPackageHelper.NodeInstance> nodes, String key) {
        return nodes.stream()
                .filter(node -> key.equals(TCFocusElements.normalizeKey(node.key())))
                .min(Comparator.comparingInt(TCFocusPackageHelper.NodeInstance::id));
    }

    private static int clamped(Map<String, Integer> settings, String key, int fallback, int min, int max) {
        return Mth.clamp(settings.getOrDefault(key, fallback), min, max);
    }

    public record CastPlan(
            boolean supported,
            String reason,
            TCFocusPackageComponent packageData,
            TCFocusPackageHelper.NodeInstance medium,
            TCFocusPackageHelper.NodeInstance effect
    ) {
        private static CastPlan unsupported(String reason) {
            return new CastPlan(false, reason, TCFocusPackageComponent.EMPTY, null, null);
        }
    }

    public record CastResult(boolean supported, boolean applied, String notes) {
        private static CastResult unsupported(String notes) {
            return new CastResult(false, false, notes);
        }
    }

    public record Trajectory(Vec3 source, Vec3 direction) {
    }

    public record FocusTarget(Entity entity, Vec3 hit, BlockPos blockPos, Direction side) {
        public static FocusTarget entity(Entity entity, Vec3 hit) {
            return new FocusTarget(entity, hit, null, null);
        }

        public static FocusTarget block(Vec3 hit, BlockPos blockPos, Direction side) {
            return new FocusTarget(null, hit, blockPos, side);
        }

        public static FocusTarget fromHit(HitResult hit) {
            if (hit instanceof EntityHitResult ehr) {
                return entity(ehr.getEntity(), ehr.getLocation());
            } else if (hit instanceof BlockHitResult bhr) {
                return block(bhr.getLocation(), bhr.getBlockPos(), bhr.getDirection());
            } else {
                return block(hit.getLocation(), BlockPos.containing(hit.getLocation()), Direction.UP);
            }
        }

        public HitResult asHitResult() {
            if (entity != null) {
                return new EntityHitResult(entity, hit != null ? hit : entity.position());
            } else {
                BlockPos pos = blockPos != null ? blockPos : (hit != null ? BlockPos.containing(hit) : BlockPos.ZERO);
                Direction d = side != null ? side : Direction.UP;
                Vec3 h = hit != null ? hit : Vec3.atCenterOf(pos);
                return new BlockHitResult(h, d, pos, false);
            }
        }
    }

    private record EntityHit(Entity entity, Vec3 hit, double distanceSqr) {
    }
}
