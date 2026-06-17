# Hedge Alchemy Legacy Recipe Blocks

Generated: 2026-06-18 02:42:38 +03:00

## Purpose

This document extracts the exact legacy crucible recipe source blocks for the HEDGE_ALCHEMY family before any NeoForge data serializer or Thaumonomicon page implementation is written.

## Summary

| Metric | Count |
|---|---:|
| Unique hedge alchemy page references | 10 |
| Extracted legacy crucible blocks | 10 |
| References without extracted crucible block | 0 |

## Research key distribution

| Research key | Count |
|---|---:|
| HEDGEALCHEMY@2 | 4 |
| HEDGEALCHEMY@3 | 4 |
| HEDGEALCHEMY@1 | 2 |

## Extracted recipe overview

| Reference | Resource id | Research | Aspects | File | Line |
|---|---|---|---|---|---:|
| thaumcraft:hedge_clay | thaumcraft:hedge_clay | HEDGEALCHEMY@3 |  | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 553 |
| thaumcraft:hedge_dye | thaumcraft:hedge_dye | HEDGEALCHEMY@2 |  | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 552 |
| thaumcraft:hedge_glowstone | thaumcraft:hedge_glowstone | HEDGEALCHEMY@2 |  | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 551 |
| thaumcraft:hedge_gunpowder | thaumcraft:hedge_gunpowder | HEDGEALCHEMY@2 |  | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 549 |
| thaumcraft:hedge_lava | thaumcraft:hedge_lava | HEDGEALCHEMY@3 | fire=15, earth=5 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 556 |
| thaumcraft:hedge_leather | thaumcraft:hedge_leather | HEDGEALCHEMY@1 | air=3, beast=3 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 145 |
| thaumcraft:hedge_slime | thaumcraft:hedge_slime | HEDGEALCHEMY@2 |  | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 550 |
| thaumcraft:hedge_string | thaumcraft:hedge_string | HEDGEALCHEMY@3 |  | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 554 |
| thaumcraft:hedge_tallow | thaumcraft:hedge_tallow | HEDGEALCHEMY@1 | fire=1 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 144 |
| thaumcraft:hedge_web | thaumcraft:hedge_web | HEDGEALCHEMY@3 |  | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 555 |

## Extracted legacy source blocks

### thaumcraft:hedge_clay

- File: src/main/java/thaumcraft/common/config/ConfigRecipes.java
- Start line: 553
- Research: HEDGEALCHEMY@3
- Aspects: 

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_clay"), new CrucibleRecipe("HEDGEALCHEMY@3", new ItemStack(Items.CLAY_BALL, 1, 0), new ItemStack(Blocks.DIRT), new AspectList(new ItemStack(Items.CLAY_BALL, 1, 0)).remove(new AspectList(new ItemStack(Blocks.DIRT)))));
```

### thaumcraft:hedge_dye

- File: src/main/java/thaumcraft/common/config/ConfigRecipes.java
- Start line: 552
- Research: HEDGEALCHEMY@2
- Aspects: 

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_dye"), new CrucibleRecipe("HEDGEALCHEMY@2", new ItemStack(Items.DYE, 2, 0), new ItemStack(Items.DYE, 1, 0), new AspectList(new ItemStack(Items.DYE))));
```

### thaumcraft:hedge_glowstone

- File: src/main/java/thaumcraft/common/config/ConfigRecipes.java
- Start line: 551
- Research: HEDGEALCHEMY@2
- Aspects: 

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_glowstone"), new CrucibleRecipe("HEDGEALCHEMY@2", new ItemStack(Items.GLOWSTONE_DUST, 2, 0), "dustGlowstone", new AspectList(new ItemStack(Items.GLOWSTONE_DUST))));
```

### thaumcraft:hedge_gunpowder

- File: src/main/java/thaumcraft/common/config/ConfigRecipes.java
- Start line: 549
- Research: HEDGEALCHEMY@2
- Aspects: 

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_gunpowder"), new CrucibleRecipe("HEDGEALCHEMY@2", new ItemStack(Items.GUNPOWDER, 2, 0), new ItemStack(Items.GUNPOWDER), new AspectList(new ItemStack(Items.GUNPOWDER))));
```

### thaumcraft:hedge_lava

- File: src/main/java/thaumcraft/common/config/ConfigRecipes.java
- Start line: 556
- Research: HEDGEALCHEMY@3
- Aspects: fire=15, earth=5

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_lava"), new CrucibleRecipe("HEDGEALCHEMY@3", new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.BUCKET), new AspectList().add(Aspect.FIRE, 15).add(Aspect.EARTH, 5)));
```

### thaumcraft:hedge_leather

- File: src/main/java/thaumcraft/common/config/ConfigRecipes.java
- Start line: 145
- Research: HEDGEALCHEMY@1
- Aspects: air=3, beast=3

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_leather"), new CrucibleRecipe("HEDGEALCHEMY@1", new ItemStack(Items.LEATHER), new ItemStack(Items.ROTTEN_FLESH), new AspectList().merge(Aspect.AIR, 3).merge(Aspect.BEAST, 3)));
```

### thaumcraft:hedge_slime

- File: src/main/java/thaumcraft/common/config/ConfigRecipes.java
- Start line: 550
- Research: HEDGEALCHEMY@2
- Aspects: 

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_slime"), new CrucibleRecipe("HEDGEALCHEMY@2", new ItemStack(Items.SLIME_BALL, 2, 0), new ItemStack(Items.SLIME_BALL), new AspectList(new ItemStack(Items.SLIME_BALL))));
```

### thaumcraft:hedge_string

- File: src/main/java/thaumcraft/common/config/ConfigRecipes.java
- Start line: 554
- Research: HEDGEALCHEMY@3
- Aspects: 

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_string"), new CrucibleRecipe("HEDGEALCHEMY@3", new ItemStack(Items.STRING), new ItemStack(Items.WHEAT), new AspectList(new ItemStack(Items.STRING)).remove(new AspectList(new ItemStack(Items.WHEAT)))));
```

### thaumcraft:hedge_tallow

- File: src/main/java/thaumcraft/common/config/ConfigRecipes.java
- Start line: 144
- Research: HEDGEALCHEMY@1
- Aspects: fire=1

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_tallow"), new CrucibleRecipe("HEDGEALCHEMY@1", new ItemStack(ItemsTC.tallow), new ItemStack(Items.ROTTEN_FLESH), new AspectList().merge(Aspect.FIRE, 1)));
```

### thaumcraft:hedge_web

- File: src/main/java/thaumcraft/common/config/ConfigRecipes.java
- Start line: 555
- Research: HEDGEALCHEMY@3
- Aspects: 

```java
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_web"), new CrucibleRecipe("HEDGEALCHEMY@3", new ItemStack(Blocks.WEB), new ItemStack(Items.STRING), new AspectList(new ItemStack(Blocks.WEB)).remove(new AspectList(new ItemStack(Items.STRING)))));
```

## Next implementation guidance

1. Use this extraction as the source of truth for the first crucible recipe data model and page snapshot batch.
2. Implement only the recipe serializer, datapack loading, audit export, and Thaumonomicon page snapshot for this family first.
3. Do not implement crucible block behavior, item transformation, essentia systems, particles, or in-world crafting in the same batch.
4. Preserve the legacy ResourceLocation ids from the extracted blocks so existing research page references resolve.
