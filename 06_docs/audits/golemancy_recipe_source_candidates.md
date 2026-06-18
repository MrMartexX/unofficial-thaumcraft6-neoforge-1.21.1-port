# Focused Golemancy Recipe Source Candidates

Generated: 2026-06-18 15:45:33 +03:00

## Purpose

This document extracts only likely recipe source blocks from legacy `ConfigRecipes.java` for current `GOLEMANCY_PAGE_DEFERRED` references. It intentionally avoids broad seal text hits from the larger boundary audit.

## Summary

| Metric | Count |
|---|---:|
| Golemancy deferred references scanned | 19 |
| References with focused recipe candidate | 18 |
| References without focused recipe candidate | 1 |
| Focused recipe candidate blocks | 29 |

## Candidate kind distribution

| Kind | Count |
|---|---:|
| ARCANE_CRAFTING | 1 |
| CRUCIBLE | 23 |
| INFUSION | 5 |

## Focused recipe candidate overview

| Reference | Kind | Resource id | Research | Aspects | File | Line |
|---|---|---|---|---|---|---:|
| thaumcraft:JarBrain | INFUSION | thaumcraft:JarBrain | JARBRAIN | mind=25, senses=25, undead=25 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 280 |
| thaumcraft:MindBiothaumic | CRUCIBLE | thaumcraft:SealCollectAdv | SEALCOLLECT&&MINDBIOTHAUMIC | senses=10, mind=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 168 |
| thaumcraft:MindBiothaumic | CRUCIBLE | thaumcraft:SealStoreAdv | SEALSTORE&&MINDBIOTHAUMIC | senses=10, mind=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 170 |
| thaumcraft:MindBiothaumic | CRUCIBLE | thaumcraft:SealEmptyAdv | SEALEMPTY&&MINDBIOTHAUMIC | senses=10, mind=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 172 |
| thaumcraft:MindBiothaumic | CRUCIBLE | thaumcraft:SealGuardAdv | SEALGUARD&&MINDBIOTHAUMIC | senses=20, mind=20 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 176 |
| thaumcraft:MindBiothaumic | CRUCIBLE | thaumcraft:SealBreakAdv | SEALBREAK&&MINDBIOTHAUMIC | senses=10, mind=10, tool=20 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 179 |
| thaumcraft:MindBiothaumic | INFUSION | thaumcraft:MindBiothaumic | MINDBIOTHAUMIC | mind=50, mechanism=25 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 334 |
| thaumcraft:SealBreak | CRUCIBLE | thaumcraft:SealBreakAdv | SEALBREAK&&MINDBIOTHAUMIC | senses=10, mind=10, tool=20 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 179 |
| thaumcraft:SealBreak | INFUSION | thaumcraft:SealBreak | SEALBREAK | tool=10, entropy=10, man=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 270 |
| thaumcraft:SealBreakAdv | CRUCIBLE | thaumcraft:SealBreakAdv | SEALBREAK&&MINDBIOTHAUMIC | senses=10, mind=10, tool=20 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 179 |
| thaumcraft:SealButcher | INFUSION | thaumcraft:SealButcher | SEALBUTCHER | beast=10, senses=10, man=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 269 |
| thaumcraft:SealCollect | CRUCIBLE | thaumcraft:SealCollect | SEALCOLLECT | desire=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 167 |
| thaumcraft:SealCollect | CRUCIBLE | thaumcraft:SealCollectAdv | SEALCOLLECT&&MINDBIOTHAUMIC | senses=10, mind=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 168 |
| thaumcraft:SealCollectAdv | CRUCIBLE | thaumcraft:SealCollectAdv | SEALCOLLECT&&MINDBIOTHAUMIC | senses=10, mind=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 168 |
| thaumcraft:SealEmpty | CRUCIBLE | thaumcraft:SealEmpty | SEALEMPTY | void=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 171 |
| thaumcraft:SealEmpty | CRUCIBLE | thaumcraft:SealEmptyAdv | SEALEMPTY&&MINDBIOTHAUMIC | senses=10, mind=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 172 |
| thaumcraft:SealEmptyAdv | CRUCIBLE | thaumcraft:SealEmptyAdv | SEALEMPTY&&MINDBIOTHAUMIC | senses=10, mind=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 172 |
| thaumcraft:SealGuard | CRUCIBLE | thaumcraft:SealGuard | SEALGUARD | aversion=20, protect=20 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 175 |
| thaumcraft:SealGuard | CRUCIBLE | thaumcraft:SealGuardAdv | SEALGUARD&&MINDBIOTHAUMIC | senses=20, mind=20 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 176 |
| thaumcraft:SealGuard | ARCANE_CRAFTING | thaumcraft:modaggression |  | fire=1 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 201 |
| thaumcraft:SealGuardAdv | CRUCIBLE | thaumcraft:SealGuardAdv | SEALGUARD&&MINDBIOTHAUMIC | senses=20, mind=20 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 176 |
| thaumcraft:SealHarvest | INFUSION | thaumcraft:SealHarvest | SEALHARVEST | plant=10, senses=10, man=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 268 |
| thaumcraft:SealLumber | CRUCIBLE | thaumcraft:SealLumber | SEALLUMBER | plant=40, senses=20 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 177 |
| thaumcraft:SealProvide | CRUCIBLE | thaumcraft:SealProvide | SEALPROVIDE | exchange=10, desire=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 173 |
| thaumcraft:SealStock | CRUCIBLE | thaumcraft:SealStock | SEALSTOCK | mind=10, desire=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 174 |
| thaumcraft:SealStore | CRUCIBLE | thaumcraft:SealStore | SEALSTORE | aversion=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 169 |
| thaumcraft:SealStore | CRUCIBLE | thaumcraft:SealStoreAdv | SEALSTORE&&MINDBIOTHAUMIC | senses=10, mind=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 170 |
| thaumcraft:SealStoreAdv | CRUCIBLE | thaumcraft:SealStoreAdv | SEALSTORE&&MINDBIOTHAUMIC | senses=10, mind=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 170 |
| thaumcraft:SealUse | CRUCIBLE | thaumcraft:SealUse | SEALUSE | craft=20, senses=10, mind=20 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 178 |

## References without focused recipe candidates

| Reference | JSON path |
|---|---|
| thaumcraft:GolemPress | $.entries[8].stages[2].recipes[1] |

## Extracted recipe source blocks

### thaumcraft:JarBrain

- Kind: INFUSION
- Resource id: thaumcraft:JarBrain
- Research: JARBRAIN
- Aspects: mind=25, senses=25, undead=25
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:280

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:JarBrain"), new InfusionRecipe("JARBRAIN", new ItemStack(BlocksTC.jarBrain), 4, new AspectList().add(Aspect.MIND, 25).add(Aspect.SENSES, 25).add(Aspect.UNDEAD, 25), new ItemStack(BlocksTC.jarNormal), new ItemStack(ItemsTC.brain), new ItemStack(Items.SPIDER_EYE), new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.SPIDER_EYE)));
```

### thaumcraft:MindBiothaumic

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealCollectAdv
- Research: SEALCOLLECT&&MINDBIOTHAUMIC
- Aspects: senses=10, mind=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:168

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealCollectAdv"), new CrucibleRecipe("SEALCOLLECT&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:pickup_advanced"), GolemHelper.getSealStack("thaumcraft:pickup"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
```

### thaumcraft:MindBiothaumic

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealStoreAdv
- Research: SEALSTORE&&MINDBIOTHAUMIC
- Aspects: senses=10, mind=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:170

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStoreAdv"), new CrucibleRecipe("SEALSTORE&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:fill_advanced"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
```

### thaumcraft:MindBiothaumic

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealEmptyAdv
- Research: SEALEMPTY&&MINDBIOTHAUMIC
- Aspects: senses=10, mind=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:172

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmptyAdv"), new CrucibleRecipe("SEALEMPTY&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:empty_advanced"), GolemHelper.getSealStack("thaumcraft:empty"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
```

### thaumcraft:MindBiothaumic

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealGuardAdv
- Research: SEALGUARD&&MINDBIOTHAUMIC
- Aspects: senses=20, mind=20
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:176

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuardAdv"), new CrucibleRecipe("SEALGUARD&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:guard_advanced"), GolemHelper.getSealStack("thaumcraft:guard"), new AspectList().add(Aspect.SENSES, 20).add(Aspect.MIND, 20)));
```

### thaumcraft:MindBiothaumic

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealBreakAdv
- Research: SEALBREAK&&MINDBIOTHAUMIC
- Aspects: senses=10, mind=10, tool=20
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:179

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealBreakAdv"), new CrucibleRecipe("SEALBREAK&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:breaker_advanced"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10).add(Aspect.TOOL, 20)));
```

### thaumcraft:MindBiothaumic

- Kind: INFUSION
- Resource id: thaumcraft:MindBiothaumic
- Research: MINDBIOTHAUMIC
- Aspects: mind=50, mechanism=25
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:334

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MindBiothaumic"), new InfusionRecipe("MINDBIOTHAUMIC", new ItemStack(ItemsTC.mind, 1, 1), 4, new AspectList().add(Aspect.MIND, 50).add(Aspect.MECHANISM, 25), new ItemStack(ItemsTC.mind, 1, 0), new ItemStack(ItemsTC.brain), new ItemStack(ItemsTC.mechanismComplex)));
```

### thaumcraft:SealBreak

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealBreakAdv
- Research: SEALBREAK&&MINDBIOTHAUMIC
- Aspects: senses=10, mind=10, tool=20
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:179

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealBreakAdv"), new CrucibleRecipe("SEALBREAK&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:breaker_advanced"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10).add(Aspect.TOOL, 20)));
```

### thaumcraft:SealBreak

- Kind: INFUSION
- Resource id: thaumcraft:SealBreak
- Research: SEALBREAK
- Aspects: tool=10, entropy=10, man=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:270

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealBreak"), new InfusionRecipe("SEALBREAK", GolemHelper.getSealStack("thaumcraft:breaker"), 1, new AspectList().add(Aspect.TOOL, 10).add(Aspect.ENTROPY, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), Ingredient.fromItem(Items.GOLDEN_AXE), Ingredient.fromItem(Items.GOLDEN_PICKAXE), Ingredient.fromItem(Items.GOLDEN_SHOVEL)));
```

### thaumcraft:SealBreakAdv

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealBreakAdv
- Research: SEALBREAK&&MINDBIOTHAUMIC
- Aspects: senses=10, mind=10, tool=20
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:179

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealBreakAdv"), new CrucibleRecipe("SEALBREAK&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:breaker_advanced"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10).add(Aspect.TOOL, 20)));
```

### thaumcraft:SealButcher

- Kind: INFUSION
- Resource id: thaumcraft:SealButcher
- Research: SEALBUTCHER
- Aspects: beast=10, senses=10, man=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:269

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealButcher"), new InfusionRecipe("SEALBUTCHER", GolemHelper.getSealStack("thaumcraft:butcher"), 0, new AspectList().add(Aspect.BEAST, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), GolemHelper.getSealStack("thaumcraft:guard"), "leather", new ItemStack(Blocks.WOOL, 1, 32767), new ItemStack(Items.RABBIT_HIDE), new ItemStack(Items.PORKCHOP), new ItemStack(Items.MUTTON), new ItemStack(Items.BEEF)));
```

### thaumcraft:SealCollect

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealCollect
- Research: SEALCOLLECT
- Aspects: desire=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:167

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealCollect"), new CrucibleRecipe("SEALCOLLECT", GolemHelper.getSealStack("thaumcraft:pickup"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.DESIRE, 10)));
```

### thaumcraft:SealCollect

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealCollectAdv
- Research: SEALCOLLECT&&MINDBIOTHAUMIC
- Aspects: senses=10, mind=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:168

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealCollectAdv"), new CrucibleRecipe("SEALCOLLECT&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:pickup_advanced"), GolemHelper.getSealStack("thaumcraft:pickup"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
```

### thaumcraft:SealCollectAdv

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealCollectAdv
- Research: SEALCOLLECT&&MINDBIOTHAUMIC
- Aspects: senses=10, mind=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:168

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealCollectAdv"), new CrucibleRecipe("SEALCOLLECT&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:pickup_advanced"), GolemHelper.getSealStack("thaumcraft:pickup"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
```

### thaumcraft:SealEmpty

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealEmpty
- Research: SEALEMPTY
- Aspects: void=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:171

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmpty"), new CrucibleRecipe("SEALEMPTY", GolemHelper.getSealStack("thaumcraft:empty"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.VOID, 10)));
```

### thaumcraft:SealEmpty

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealEmptyAdv
- Research: SEALEMPTY&&MINDBIOTHAUMIC
- Aspects: senses=10, mind=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:172

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmptyAdv"), new CrucibleRecipe("SEALEMPTY&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:empty_advanced"), GolemHelper.getSealStack("thaumcraft:empty"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
```

### thaumcraft:SealEmptyAdv

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealEmptyAdv
- Research: SEALEMPTY&&MINDBIOTHAUMIC
- Aspects: senses=10, mind=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:172

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmptyAdv"), new CrucibleRecipe("SEALEMPTY&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:empty_advanced"), GolemHelper.getSealStack("thaumcraft:empty"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
```

### thaumcraft:SealGuard

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealGuard
- Research: SEALGUARD
- Aspects: aversion=20, protect=20
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:175

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuard"), new CrucibleRecipe("SEALGUARD", GolemHelper.getSealStack("thaumcraft:guard"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSION, 20).add(Aspect.PROTECT, 20)));
```

### thaumcraft:SealGuard

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealGuardAdv
- Research: SEALGUARD&&MINDBIOTHAUMIC
- Aspects: senses=20, mind=20
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:176

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuardAdv"), new CrucibleRecipe("SEALGUARD&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:guard_advanced"), GolemHelper.getSealStack("thaumcraft:guard"), new AspectList().add(Aspect.SENSES, 20).add(Aspect.MIND, 20)));
```

### thaumcraft:SealGuard

- Kind: ARCANE_CRAFTING
- Resource id: thaumcraft:modaggression
- Research: 
- Aspects: fire=1
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:201

```java
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:modaggression"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "SEALGUARD", 50, new AspectList().add(Aspect.FIRE, 1), new ItemStack(ItemsTC.modules, 1, 1), " R ", "RTR", "PGP", 'R', "paneGlass", 'T', new ItemStack(Items.BLAZE_POWDER), 'P', "plateBrass", 'G', new ItemStack(ItemsTC.mechanismSimple)));
```

### thaumcraft:SealGuardAdv

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealGuardAdv
- Research: SEALGUARD&&MINDBIOTHAUMIC
- Aspects: senses=20, mind=20
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:176

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuardAdv"), new CrucibleRecipe("SEALGUARD&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:guard_advanced"), GolemHelper.getSealStack("thaumcraft:guard"), new AspectList().add(Aspect.SENSES, 20).add(Aspect.MIND, 20)));
```

### thaumcraft:SealHarvest

- Kind: INFUSION
- Resource id: thaumcraft:SealHarvest
- Research: SEALHARVEST
- Aspects: plant=10, senses=10, man=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:268

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealHarvest"), new InfusionRecipe("SEALHARVEST", GolemHelper.getSealStack("thaumcraft:harvest"), 0, new AspectList().add(Aspect.PLANT, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.PUMPKIN_SEEDS), new ItemStack(Items.MELON_SEEDS), new ItemStack(Items.BEETROOT_SEEDS), new ItemStack(Items.REEDS), new ItemStack(Blocks.CACTUS)));
```

### thaumcraft:SealLumber

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealLumber
- Research: SEALLUMBER
- Aspects: plant=40, senses=20
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:177

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealLumber"), new CrucibleRecipe("SEALLUMBER", GolemHelper.getSealStack("thaumcraft:lumber"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectList().add(Aspect.PLANT, 40).add(Aspect.SENSES, 20)));
```

### thaumcraft:SealProvide

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealProvide
- Research: SEALPROVIDE
- Aspects: exchange=10, desire=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:173

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealProvide"), new CrucibleRecipe("SEALPROVIDE", GolemHelper.getSealStack("thaumcraft:provider"), GolemHelper.getSealStack("thaumcraft:empty_advanced"), new AspectList().add(Aspect.EXCHANGE, 10).add(Aspect.DESIRE, 10)));
```

### thaumcraft:SealStock

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealStock
- Research: SEALSTOCK
- Aspects: mind=10, desire=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:174

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStock"), new CrucibleRecipe("SEALSTOCK", GolemHelper.getSealStack("thaumcraft:stock"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.MIND, 10).add(Aspect.DESIRE, 10)));
```

### thaumcraft:SealStore

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealStore
- Research: SEALSTORE
- Aspects: aversion=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:169

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStore"), new CrucibleRecipe("SEALSTORE", GolemHelper.getSealStack("thaumcraft:fill"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSION, 10)));
```

### thaumcraft:SealStore

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealStoreAdv
- Research: SEALSTORE&&MINDBIOTHAUMIC
- Aspects: senses=10, mind=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:170

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStoreAdv"), new CrucibleRecipe("SEALSTORE&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:fill_advanced"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
```

### thaumcraft:SealStoreAdv

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealStoreAdv
- Research: SEALSTORE&&MINDBIOTHAUMIC
- Aspects: senses=10, mind=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:170

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStoreAdv"), new CrucibleRecipe("SEALSTORE&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:fill_advanced"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().add(Aspect.SENSES, 10).add(Aspect.MIND, 10)));
```

### thaumcraft:SealUse

- Kind: CRUCIBLE
- Resource id: thaumcraft:SealUse
- Research: SEALUSE
- Aspects: craft=20, senses=10, mind=20
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:178

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealUse"), new CrucibleRecipe("SEALUSE", GolemHelper.getSealStack("thaumcraft:use"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.CRAFT, 20).add(Aspect.SENSES, 10).add(Aspect.MIND, 20)));
```

## Next implementation guidance

1. Prefer references that have a focused `ARCANE_CRAFTING` or `INFUSION` candidate before touching broad seal behavior.
2. Do not implement broad seal behavior from this file; create a separate seal placeholder/page strategy after recipe candidates are exhausted.
3. After each focused implementation batch, re-run the research recipe page gap audit.
