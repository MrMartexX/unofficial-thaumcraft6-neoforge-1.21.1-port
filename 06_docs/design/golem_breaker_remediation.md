# Golem Breaker Remediation

Scope: replace the active Breaker stub, using the primary legacy SealBreaker and
SealBreakerAdvanced as references. Do not expand other mining or worldgen systems.

Preserve the one-area-position-per-tick scan, task-position cache, hardness times
ten work counter, 21 normal / 7 silk work per completion attempt, crack cleanup,
metadata-only filtering, advanced nine slots and SMART requirement. Preserve the
legacy whitelist conjunction instead of silently substituting an OR filter.

Modern boundary: resolve flattened metadata families explicitly. Harvest through
server-side loot tables with the actual BlockEntity and a silk-enchanted loot
tool, while honoring modern break cancellation and protected command/structure
blocks. The legacy skipEvent=true path is not a reason to bypass modern protection.
This is an intentional safety adaptation, not byte-for-byte API equivalence.

Acceptance: actual queued work, duplicate suppression, partial mining, ordinary
and silk drops, rejected unbreakable/filtered blocks, cancelled break with no loot,
and option persistence. Use only the disposable server audit world. State metadata
without an explicit modern representation cannot be declared fully equivalent.
