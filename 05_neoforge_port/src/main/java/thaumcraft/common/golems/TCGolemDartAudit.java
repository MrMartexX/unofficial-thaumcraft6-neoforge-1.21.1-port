package thaumcraft.common.golems;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.common.entities.*;
import thaumcraft.common.golems.tasks.TCGolemRangedAttackGoal;
import thaumcraft.common.registry.TCEntityTypes;

final class TCGolemDartAudit {
    static void run(ServerLevel level, List<TCGolemBehaviorAudit.Row> rows) {
        var spawn = level.getSharedSpawnPos();
        BlockPos origin = new BlockPos(spawn.getX() + 4, Math.min(level.getMaxBuildHeight() - 16, spawn.getY() + 120), spawn.getZ() + 4), wall = origin.east(3);
        level.getChunkAt(origin);
        if (level.getBlockEntity(wall) != null) throw new IllegalStateException("Dart fixture overlaps BlockEntity");
        var before = level.getBlockState(wall);
        var bounds = new AABB(origin).inflate(20);
        var existing = level.getEntitiesOfClass(TCGolemDartEntity.class, bounds).stream().map(TCGolemDartEntity::getUUID).collect(java.util.stream.Collectors.toSet());
        var owner = new TCGolemEntity(TCEntityTypes.GOLEM.get(), level);
        var target = net.minecraft.world.entity.EntityType.PIG.create(level);
        var props = new GolemProperties();
        for (var arm : GolemArm.getArms()) if (Arrays.asList(arm.traits).contains(EnumGolemTrait.RANGED)) props.setArms(arm);
        props.setRank(3); owner.setProperties(props);
        owner.setPos(origin.getX() + .5, origin.getY(), origin.getZ() + .5);
        target.setPos(origin.getX() + 8.5, origin.getY(), origin.getZ() + .5);
        var hit = new TCGolemDartEntity(TCEntityTypes.GOLEM_DART.get(), owner, level);
        var embedded = new TCGolemDartEntity(TCEntityTypes.GOLEM_DART.get(), owner, level);
        try {
            owner.getRandom().setSeed(6459);
            var random = net.minecraft.util.RandomSource.create(6459);
            double expected = (float)owner.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) / 3 + .5F + random.nextGaussian() * .25;
            TCGolemDartEntity[] emitted = {null};
            java.util.function.Consumer<net.neoforged.neoforge.event.entity.EntityJoinLevelEvent> observer = event -> {
                if (event.getLevel() == level && event.getEntity() instanceof TCGolemDartEntity dart && dart.getOwner() == owner) emitted[0] = dart;
            };
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(observer);
            try { owner.performRangedAttack(target, .5F); }
            finally { net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(observer); }
            var shot = java.util.Objects.requireNonNull(emitted[0], "Actual ranged attack did not spawn a dart");
            var tag = new net.minecraft.nbt.CompoundTag();
            shot.addAdditionalSaveData(tag);
            var restored = new TCGolemDartEntity(TCEntityTypes.GOLEM_DART.get(), level);
            try {
                restored.readAdditionalSaveData(tag);
                rows.add(new TCGolemBehaviorAudit.Row("golem_dart_dynamic_damage_and_save", shot.getOwner() == owner
                        && shot.pickup == AbstractArrow.Pickup.DISALLOWED && Math.abs(shot.getBaseDamage() - expected) < 1e-10
                        && shot.getBaseDamage() == restored.getBaseDamage() && shot.getPickupItemStackOrigin().is(Items.ARROW),
                        "Real fired arrow uses attackDamage/3+range+Gaussian*.25, NPC pickup policy and persisted base damage"));
            } finally { restored.discard(); shot.discard(); }
            target.setPos(origin.getX() + 1.1, origin.getY(), origin.getZ() + .5);
            target.setNoAi(true); level.addFreshEntity(target);
            float health = target.getHealth();
            hit.setPos(origin.getX() + .1, origin.getY() + .5, origin.getZ() + .5);
            hit.setBaseDamage(4); hit.setDeltaMovement(1, 0, 0);
            hit.tick();
            rows.add(new TCGolemBehaviorAudit.Row("golem_dart_actual_arrow_hit", health - target.getHealth() == 4 && hit.isRemoved(),
                    "Actual arrow collision applies velocity-scaled base damage4, not the old fixed2 throwable damage; dealt=" + (health - target.getHealth())));
            target.discard();
            level.setBlock(wall, net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3);
            embedded.setPos(origin.getX() + .5, origin.getY() + .5, origin.getZ() + .5);
            embedded.setDeltaMovement(1, 0, 0);
            for (int tick = 0; tick < 5; tick++) embedded.tick();
            var embeddedTag = new net.minecraft.nbt.CompoundTag();
            embedded.addAdditionalSaveData(embeddedTag);
            rows.add(new TCGolemBehaviorAudit.Row("golem_dart_embeds_in_block", !embedded.isRemoved() && embeddedTag.getBoolean("inGround")
                    && level.getBlockState(wall).is(net.minecraft.world.level.block.Blocks.STONE),
                    "Actual block collision embeds the dart and preserves vanilla arrow lifetime instead of immediate deletion"));
            level.setBlock(wall, before, 3);
            var shots = new ArrayList<Integer>();
            var ranged = new TCGolemEntity(TCEntityTypes.GOLEM.get(), level) {
                @Override public void performRangedAttack(net.minecraft.world.entity.LivingEntity victim, float range) { shots.add(tickCount); }
            };
            try {
                ranged.setProperties(props); ranged.setPos(owner.getX(), owner.getY(), owner.getZ());
                target = net.minecraft.world.entity.EntityType.PIG.create(level);
                target.setPos(origin.getX() + 8.5, origin.getY(), origin.getZ() + .5);
                ranged.setTarget(target);
                var goal = new TCGolemRangedAttackGoal(ranged);
                boolean allowed = goal.canUse();
                for (int tick = 0; tick < 50; tick++) { ranged.tickCount = tick; goal.tick(); }
                rows.add(new TCGolemBehaviorAudit.Row("golem_dart_legacy_cadence", allowed && shots.equals(List.of(22, 44)),
                        "At8 blocks, original floor(rangeFraction*5+20) cadence fires at ticks22 and44; observed=" + shots));
            } finally { ranged.discard(); }
        } finally {
            owner.discard(); target.discard(); hit.discard(); embedded.discard();
            level.getEntitiesOfClass(TCGolemDartEntity.class, bounds).stream().filter(dart -> !existing.contains(dart.getUUID())).forEach(TCGolemDartEntity::discard);
            level.setBlock(wall, before, 3);
        }
    }
}
