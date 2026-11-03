package thaumcraft.common.golems;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.parts.*;
import thaumcraft.common.entities.TCGolemEntity;
import thaumcraft.common.golems.tasks.*;
import thaumcraft.common.registry.TCEntityTypes;

final class TCGolemMovementAudit {
    static void run(ServerLevel level, List<TCGolemBehaviorAudit.Row> rows) {
        BlockPos origin = new BlockPos(1106, 210, 1060);
        level.getChunkAt(origin);
        var golem = new TCGolemEntity(TCEntityTypes.GOLEM.get(), level);
        try {
            rows.add(new TCGolemBehaviorAudit.Row("golem_legacy_dimensions_and_eye_height", golem.getBbWidth() == .4F
                    && golem.getBbHeight() == .9F && golem.getEyeHeight() == .7F,
                    "Original0.4x0.9 body and fixed0.7 eye height, not the modern default0.765"));
            GolemProperties props = new GolemProperties();
            for (var leg : GolemLeg.getLegs()) if (Arrays.asList(leg.traits).contains(EnumGolemTrait.FLYER)) props.setLegs(leg);
            golem.setProperties(props);
            golem.setPos(origin.getX() + .5, origin.getY(), origin.getZ() + .5);
            golem.setFollowingOwner(true);
            golem.setDeltaMovement(Vec3.ZERO);
            golem.getMoveControl().setWantedPosition(golem.getX() + 3, golem.getY() + 4, golem.getZ(), 1);
            golem.getMoveControl().tick();
            boolean acceleration = Math.abs(golem.getDeltaMovement().x - .0198) < 1e-8
                    && Math.abs(golem.getDeltaMovement().y - .01) < 1e-8 && golem.isNoGravity();
            golem.getMoveControl().setWantedPosition(golem.getX(), golem.getY(), golem.getZ(), 1);
            golem.getMoveControl().tick();
            acceleration &= Math.abs(golem.getDeltaMovement().x - .0099) < 1e-8 && !golem.getMoveControl().hasWanted();
            rows.add(new TCGolemBehaviorAudit.Row("golem_flight_acceleration_and_damping", acceleration,
                    "Actual controller applies normalized .033/.0125 acceleration and halves motion on arrival"));
            golem.setDeltaMovement(Vec3.ZERO);
            var path = golem.getNavigation().createPath(origin.offset(6, 4, 0), 0);
            boolean sixAxis = path != null && path.canReach();
            if (path != null) sixAxis &= path.getEndNode().x == origin.getX() + 6 && path.getEndNode().z == origin.getZ();
            if (path != null) for (int i = 1; i < path.getNodeCount(); i++) {
                var a = path.getNode(i - 1); var b = path.getNode(i);
                sixAxis &= Math.abs(a.x - b.x) + Math.abs(a.y - b.y) + Math.abs(a.z - b.z) == 1;
            }
            var navigation = golem.getNavigation();
            var control = golem.getMoveControl();
            props.setRank(3); golem.setProperties(props);
            sixAxis &= navigation == golem.getNavigation() && control == golem.getMoveControl();
            rows.add(new TCGolemBehaviorAudit.Row("golem_six_axis_air_path_and_refresh", sixAxis,
                    "Real pathfinder reaches an elevated target using only six-axis steps; rank refresh preserves navigation"));
            Vec3 target = Vec3.atBottomCenterOf(origin.offset(6, 4, 0));
            double before = golem.position().distanceToSqr(target);
            boolean started = golem.getNavigation().moveTo(target.x, target.y, target.z, golem.getGolemMoveSpeed());
            StringBuilder trajectory = new StringBuilder();
            double closest = before;
            for (int tick = 0; tick < 250; tick++) {
                golem.tickCount++; golem.tick();
                closest = Math.min(closest, golem.position().distanceToSqr(target));
                if (tick == 30 || tick == 100 || tick == 249) trajectory.append(" t").append(tick).append('=').append(golem.position())
                        .append(" pathDone=").append(golem.getNavigation().isDone()).append(" hp=").append(golem.getHealth());
            }
            rows.add(new TCGolemBehaviorAudit.Row("golem_registered_flight_motion", started && golem.getY() > origin.getY() + 1
                    && closest < 4,
                    "Real ticks reach the elevated work radius (not an invented stationary-hover promise); before=" + before + ", closest=" + closest + trajectory));
            GolemProperties climber = new GolemProperties();
            for (var leg : GolemLeg.getLegs()) if (Arrays.asList(leg.traits).contains(EnumGolemTrait.CLIMBER)) climber.setLegs(leg);
            golem.setProperties(climber);
            boolean climbMode = golem.getNavigation() instanceof WallClimberNavigation && !golem.isNoGravity()
                    && !golem.causeFallDamage(20, 1, level.damageSources().fall());
            golem.setProperties(new GolemProperties());
            climbMode &= !(golem.getNavigation() instanceof WallClimberNavigation) && !(golem.getNavigation() instanceof TCGolemAirNavigation)
                    && !golem.isNoGravity() && !golem.onClimbable();
            rows.add(new TCGolemBehaviorAudit.Row("golem_climber_mode_and_fall_policy", climbMode,
                    "Climber installs modern vanilla successor navigation and fall immunity; switching back restores ground/gravity"));
            boolean emotes = true;
            for (byte event = 5; event <= 9; event++) {
                var emote = TCGolemEmotes.create(event, net.minecraft.util.RandomSource.create(event));
                emotes &= emote.data().gridSize == 64 && emote.data().layer == 1 && emote.data().alphaKeys[0] == .5F && !emote.data().loop;
                if (event == 5) emotes &= emote.data().maxAge == 6 && emote.data().numParticles == 3;
                if (event == 6) emotes &= emote.data().startParticle == 15 && emote.y() == .025;
                if (event == 7) emotes &= emote.data().startParticle == 640 && emote.data().numParticles == 10;
                if (event == 8) emotes &= emote.data().startParticle == 14 && emote.data().maxAge == 20;
                if (event == 9) emotes &= emote.data().maxAge >= 20 && emote.data().maxAge < 40 && emote.data().scaleKeys[0] >= .3F && emote.data().scaleKeys[0] < .7F;
            }
            rows.add(new TCGolemBehaviorAudit.Row("golem_emote_legacy_atlas_contract", emotes,
                    "All five original status effects retain atlas cells, frames, alpha, lifetimes and motion ranges"));
        } finally { golem.discard(); }
        ownerTargets(level, rows, origin);
        climbingAndFollow(level, rows, origin.offset(0, 0, 8));
    }

    private static void ownerTargets(ServerLevel level, List<TCGolemBehaviorAudit.Row> rows, BlockPos pos) {
        var owner = net.minecraft.world.entity.EntityType.ZOMBIE.create(level);
        var attacker = net.minecraft.world.entity.EntityType.ZOMBIE.create(level);
        var golem = new TCGolemEntity(TCEntityTypes.GOLEM.get(), level) {
            @Override public LivingEntity getOwner() { return owner; }
            void tickTargets() { targetSelector.tick(); }
        };
        try {
            owner.setPos(pos.getX(), pos.getY(), pos.getZ()); attacker.setPos(pos.getX() + 2, pos.getY(), pos.getZ());
            golem.setPos(pos.getX() + 1, pos.getY(), pos.getZ());
            golem.setOwnerUUID(owner.getUUID());
            owner.tickCount = 20;
            owner.setLastHurtByMob(attacker); owner.setLastHurtMob(attacker);
            var defense = new TCGolemOwnerTargetGoal(golem, true);
            var assist = new TCGolemOwnerTargetGoal(golem, false);
            GolemProperties fighter = new GolemProperties();
            for (var arms : GolemArm.getArms()) if (Arrays.asList(arms.traits).contains(EnumGolemTrait.FIGHTER)) { fighter.setArms(arms); break; }
            golem.setProperties(fighter);
            attacker.setNoAi(true);
            level.addFreshEntity(attacker);
            for (int tick = 0; tick < 40; tick++) golem.tickTargets();
            rows.add(new TCGolemBehaviorAudit.Row("golem_no_invented_nearest_hostile_aggro", golem.getTarget() == null,
                    "Registered target selector ignores a real nearby hostile unless attacked, ordered or defending its owner"));
            boolean gates = !defense.canUse() && !assist.canUse();
            golem.setFollowingOwner(true);
            gates &= defense.canUse(); defense.start();
            gates &= golem.getTarget() == attacker && !defense.canUse();
            defense.stop();
            gates &= assist.canUse(); assist.start();
            gates &= golem.getTarget() == attacker && !assist.canUse();
            assist.stop();
            golem.setProperties(new GolemProperties());
            owner.tickCount++; owner.setLastHurtByMob(attacker);
            gates &= !defense.canUse();
            rows.add(new TCGolemBehaviorAudit.Row("golem_owner_defense_assistance_gates", gates,
                    "Owner goal contracts require follow+FIGHTER, select attacker/attacked target and consume each owner timestamp once"));
            var cat = net.minecraft.world.entity.EntityType.CAT.create(level);
            try {
                cat.setOwnerUUID(owner.getUUID());
                rows.add(new TCGolemBehaviorAudit.Row("golem_owner_and_owned_pet_protection", !golem.canAttack(owner) && !golem.canAttack(cat)
                        && golem.isAlliedTo(owner) && golem.isAlliedTo(cat), "Owner and same-owner pets remain allied and cannot become combat targets"));
            } finally { cat.discard(); }
        } finally { golem.discard(); owner.discard(); attacker.discard(); }
    }

    private static void climbingAndFollow(ServerLevel level, List<TCGolemBehaviorAudit.Row> rows, BlockPos origin) {
        level.getChunkAt(origin);
        var old = new LinkedHashMap<BlockPos, net.minecraft.world.level.block.state.BlockState>();
        var owner = net.minecraft.world.entity.EntityType.ZOMBIE.create(level);
        var golem = new TCGolemEntity(TCEntityTypes.GOLEM.get(), level) { @Override public LivingEntity getOwner() { return owner; } };
        try {
            for (int x = -2; x <= 4; x++) for (int z = -2; z <= 2; z++) for (int y = -1; y <= 6; y++) {
                var pos = origin.offset(x, y, z);
                if (level.getBlockEntity(pos) != null) throw new IllegalStateException("Climbing fixture overlaps BlockEntity");
                old.put(pos, level.getBlockState(pos));
                level.setBlock(pos, y == -1 || (x == 2 && y < 4) ? Blocks.STONE.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
            }
            var props = new GolemProperties();
            for (var leg : GolemLeg.getLegs()) if (Arrays.asList(leg.traits).contains(EnumGolemTrait.CLIMBER)) props.setLegs(leg);
            golem.setProperties(props);
            golem.setPos(origin.getX() + 1.5, origin.getY(), origin.getZ() + .5);
            owner.setPos(origin.getX() + 2.5, origin.getY() + 4, origin.getZ() + .5);
            golem.setFollowingOwner(true);
            golem.getNavigation().moveTo(owner, 1);
            boolean touchedWall = false;
            double maxY = golem.getY();
            for (int tick = 0; tick < 180; tick++) {
                golem.tickCount++; golem.tick();
                touchedWall |= golem.onClimbable();
                maxY = Math.max(maxY, golem.getY());
            }
            rows.add(new TCGolemBehaviorAudit.Row("golem_actual_wall_climbing", touchedWall && maxY > origin.getY() + 2,
                    "Actual entity/navigation ticks collide with and ascend a four-block wall; maxY=" + maxY + ", contact=" + touchedWall));
            golem.discard();
            var returning = new TCGolemEntity(TCEntityTypes.GOLEM.get(), level) {
                @Override public LivingEntity getOwner() { return owner; }
                void failPaths() {
                    navigation = new net.minecraft.world.entity.ai.navigation.GroundPathNavigation(this, level) {
                        @Override public boolean moveTo(net.minecraft.world.entity.Entity target, double speed) { return false; }
                    };
                }
            };
            try {
                returning.setProperties(new GolemProperties());
                returning.setFollowingOwner(true);
                returning.setPos(origin.getX() - 25, origin.getY(), origin.getZ());
                owner.setPos(origin.getX() + .5, origin.getY(), origin.getZ() + .5);
                // Controlled failure exercises the production fallback without changing its API.
                returning.failPaths();
                var follow = new TCGolemFollowOwnerGoal(returning);
                returning.setPathfindingMalus(net.minecraft.world.level.pathfinder.PathType.WATER, 7);
                boolean followed = follow.canUse();
                follow.start(); follow.tick();
                followed &= returning.distanceToSqr(owner) <= 13 && returning.distanceToSqr(owner) >= 4
                        && level.noCollision(returning) && returning.getPathfindingMalus(net.minecraft.world.level.pathfinder.PathType.WATER) == 0;
                follow.stop();
                followed &= returning.getPathfindingMalus(net.minecraft.world.level.pathfinder.PathType.WATER) == 7;
                rows.add(new TCGolemBehaviorAudit.Row("golem_follow_failed_path_safe_teleport", followed,
                        "Production fallback selects the outer5x5 ring, avoids collision and restores water malus after a controlled path failure"));
            } finally { returning.discard(); }
        } finally {
            golem.discard(); owner.discard();
            old.forEach((pos, state) -> level.setBlock(pos, state, 3));
        }
    }
}
