package thaumcraft.common.golems;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.parts.*;
import thaumcraft.common.entities.TCGolemEntity;
import thaumcraft.common.registry.TCEntityTypes;

final class TCGolemAttributeAudit {
    static void run(ServerLevel level, List<TCGolemBehaviorAudit.Row> rows) {
        var golem = new TCGolemEntity(TCEntityTypes.GOLEM.get(), level);
        boolean attributes = true, movement = true, healed = true;
        int combinations = 0;
        StringBuilder healingEvidence = new StringBuilder();
        GolemProperties ordinary = null, repairing = null;
        try {
            for (var material : GolemMaterial.getMaterials()) for (var head : GolemHead.getHeads())
            for (var arms : GolemArm.getArms()) for (var legs : GolemLeg.getLegs()) for (var addon : GolemAddon.getAddons()) {
                var props = new GolemProperties();
                props.setMaterial(material); props.setHead(head); props.setArms(arms); props.setLegs(legs); props.setAddon(addon);
                if (props.hasTrait(EnumGolemTrait.REPAIR)) repairing = props;
                else ordinary = props;
                for (int rank : new int[]{0, 3, 10}) {
                    props.setRank(rank);
                    golem.setProperties(props);
                    int health = 10 + material.healthMod;
                    if (props.hasTrait(EnumGolemTrait.FRAGILE)) health = (int)(health * .75D);
                    health += rank;
                    int armor = material.armor;
                    if (props.hasTrait(EnumGolemTrait.ARMORED)) armor = (int)Math.max(armor * 1.5D, armor + 1);
                    if (props.hasTrait(EnumGolemTrait.FRAGILE)) armor = (int)(armor * .75D);
                    double damage = props.hasTrait(EnumGolemTrait.FIGHTER)
                            ? (props.hasTrait(EnumGolemTrait.BRUTAL) ? Math.max(material.damage * 1.5D, material.damage + 1) : material.damage) + rank * .25D : 0;
                    attributes &= golem.getAttributeValue(Attributes.MAX_HEALTH) == health
                            && golem.getAttributeValue(Attributes.ARMOR) == armor && golem.getAttributeValue(Attributes.ATTACK_DAMAGE) == damage;
                    float speed = 1 + rank * .025F + (props.hasTrait(EnumGolemTrait.LIGHT) ? .2F : 0)
                            + (props.hasTrait(EnumGolemTrait.HEAVY) ? -.175F : 0) + (props.hasTrait(EnumGolemTrait.FLYER) ? -.33F : 0)
                            + (props.hasTrait(EnumGolemTrait.WHEELED) ? .25F : 0);
                    movement &= Math.abs(golem.getGolemMoveSpeed() - speed) < .000001F && golem.getAttributeValue(Attributes.MOVEMENT_SPEED) == .3D
                            && golem.getAttributeValue(Attributes.STEP_HEIGHT) == (props.hasTrait(EnumGolemTrait.WHEELED) ? .5D : .6D);
                    combinations++;
                }
            }
            rows.add(new TCGolemBehaviorAudit.Row("golem_original_jar_attributes", attributes && combinations > 0,
                    "Original jar confirms integer truncation after FRAGILE scaling and max(armor*1.5,armor+1); combinations=" + combinations));
            rows.add(new TCGolemBehaviorAudit.Row("golem_original_jar_work_speed", movement && combinations > 0,
                    "Base attribute remains0.3; work/home speed applies all additive trait/rank modifiers; wheel step=0.5 otherwise0.6"));
            golem.setNoAi(true);
            var spawn = level.getSharedSpawnPos();
            golem.setPos(spawn.getX() + .5, 250, spawn.getZ() + .5);
            for (var props : new GolemProperties[]{ordinary, repairing}) {
                if (props == null) { healed = false; healingEvidence.append(" missing trait variant;"); continue; }
                golem.setProperties(props);
                int interval = props.hasTrait(EnumGolemTrait.REPAIR) ? 40 : 100;
                // ServerLevel advances tickCount before tick(); direct fixture calls must do so explicitly.
                golem.setHealth(1); golem.tickCount = interval - 1; golem.tick();
                float before = golem.getHealth();
                int beforeTick = golem.tickCount;
                healed &= before == 1;
                golem.tickCount = interval;
                golem.tick();
                healed &= golem.getHealth() == 2;
                healingEvidence.append(" interval=").append(interval).append(" before=").append(before).append('@').append(beforeTick)
                        .append(" after=").append(golem.getHealth()).append('@').append(golem.tickCount);
            }
            rows.add(new TCGolemBehaviorAudit.Row("golem_legacy_repair_cadence", healed,
                    "Heal exactly one on tick100, or tick40 with REPAIR; preceding tick does not heal;" + healingEvidence));
        } finally { golem.discard(); }
    }
}
