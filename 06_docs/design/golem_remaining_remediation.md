# Remaining Golem Remediation

Primary references: EntityThaumcraftGolem, FlightNodeProcessor,
PathNavigateGolemAir, AIOwnerHurtByTarget, AIOwnerHurtTarget, AIFollowOwner and
RenderThaumcraftGolem from the primary 1.12.2 source. Recheck original bytecode
where decompiled arithmetic is ambiguous. Follow migration-guide server/client,
entity-state and inventory boundaries.

Implement the six-axis air evaluator, midpoint path following with six-node
shortcuts, legacy .033/.0125 flight acceleration and .5 arrival damping. Climbing
uses vanilla WallClimberNavigation (the successor of legacy PathNavigateClimber),
horizontal-collision state and fall immunity. Change navigation only when the
movement mode changes, not on every rank/attribute refresh.

Owner-defense/assistance require FIGHTER and follow mode. Retaliation requires
FIGHTER; no invented generic nearest-monster target selector. Honor modern PvP
and ownership safeguards. Preserve the legacy follow thresholds and test actual
safe teleport against a deliberately unreachable owner position.

Rendering work must use original assets/parameters, buffered render types and
restore state; a screenshot or green build alone cannot prove exact legacy pixel
equivalence. External addon dictionary semantics and arbitrary cross-mod handler
transactions remain compatibility boundaries, not claims that can be closed by
local fixtures. Keep unrelated concurrent worldgen work outside this batch.

Ranged follow-up: GolemArmDart uses AIArrowAttack(1.0,20,25,16), EntityArrow-based
EntityGolemDart and RenderDart at scale0.033. Replace fixed-damage throwable and
invisible renderer stubs; retain dynamic damage, aim, embedding and non-player
pickup policy. Audit actual hits, cadence, NBT and emitted arrow geometry.
