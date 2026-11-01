# TC6 1.21.1 Eldritch Bosses Parity Audit

| Check | Status | Details |
|---|---|---|
| `warden_registration_and_attributes` | **PASS** | Warden (1.5x3.5), HP 400, Speed 0.33, Damage 10, Armor 4, KnockbackRes 0.95, FollowRange 40 |
| `warden_title_and_dual_boss_bars` | **PASS** | Warden title: Chaugnar Faugn, Main & Shield boss bars initialized |
| `warden_absorption_shield_and_regeneration` | **PASS** | Max shield 264.0F (0.66 of 400), progress tracked dynamically |
| `warden_field_frenzy_invulnerability` | **PASS** | Field Frenzy triggers on shield break, 150-tick invulnerability active |
| `warden_ranged_attacks_and_warp` | **PASS** | Warden fires Eldritch Orbs and sonic screech with knockback, wither, and weakness |
| `golem_registration_and_attributes` | **PASS** | Golem (1.75x3.5), Fire-Immune, HP 400, Speed 0.3, Damage 10, Armor 6, KnockbackRes 0.95 |
| `golem_phase2_headless_survival` | **PASS** | Golem survives lethal blow, enters headless phase, resets HP to 400 and gains spawnTimer 100 |
| `golem_headless_beam_charging_and_homing_orbs` | **PASS** | Headless Phase charges energy beam and fires homing GolemOrb barrages |
| `golem_heavy_smash_and_fragile_crush` | **PASS** | Melee smash hits with knockup, fragile blocks crushed under heavy stride |
| `boss_damage_cap_enrage_and_drops` | **PASS** | Damage capped at 35, boss enters Enraged state, Primordial Pearl & Rare Bag drop contract |

**Result:** 10/10 PASS - 100% PARITY ACHIEVED
