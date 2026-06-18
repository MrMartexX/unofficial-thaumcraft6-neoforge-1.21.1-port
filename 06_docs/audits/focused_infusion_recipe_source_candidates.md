# Focused Infusion Recipe Source Candidates

Generated: 2026-06-18 16:05:32 +03:00

## Purpose

This document extracts likely legacy `addInfusionCraftingRecipe` blocks for current missing recipe-page references. It is a preparation artifact for adding a modern infusion recipe/page boundary, not an in-world infusion implementation.

## Summary

| Metric | Count |
|---|---:|
| Missing infusion-related references scanned | 41 |
| References with focused infusion candidate | 34 |
| References without focused infusion candidate | 7 |
| Focused infusion candidate blocks | 43 |

## Candidate distribution by current audit class

| Class | Count |
|---|---:|
| AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED | 3 |
| ELDRITCH_PAGE_DEFERRED | 8 |
| GOLEMANCY_PAGE_DEFERRED | 5 |
| INFUSION_PAGE_DEFERRED | 17 |
| INFUSION_RESEARCH_LEGACY_PAGE_KEY | 10 |

## Focused infusion candidate overview

| Reference | Current class | Resource id | Research | Instability | Aspects | File | Line |
|---|---|---|---|---:|---|---|---:|
| thaumcraft:focus_2 | AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED | thaumcraft:focus_2 | FOCUSADVANCED@1 | 3 | magic=25, order=50 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 278 |
| thaumcraft:focus_3 | AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED | thaumcraft:focus_3 | FOCUSGREATER@1 | 5 | magic=25, order=50, void=100 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 279 |
| thaumcraft:VisAmulet | AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED | thaumcraft:VisAmulet | VISAMULET | 1 | aura=50, energy=100, void=50 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 281 |
| thaumcraft:PrimalCrusher | ELDRITCH_PAGE_DEFERRED | thaumcraft:MaskSippingFiend | FORTRESSMASK |  | undead=80, life=80, protect=20 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 347 |
| thaumcraft:PrimalCrusher | ELDRITCH_PAGE_DEFERRED | thaumcraft:PrimalCrusher | PRIMALCRUSHER | 6 | earth=75, tool=75, entropy=50, void=50, aversion=50, eldritch=50, desire=50 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 351 |
| thaumcraft:voidingot | ELDRITCH_PAGE_DEFERRED | thaumcraft:VoidseerPearl | VOIDSEERPEARL | 8 | mind=150, void=150, magic=100 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 370 |
| thaumcraft:VoidRobeChest | ELDRITCH_PAGE_DEFERRED | thaumcraft:VoidRobeChest | VOIDROBEARMOR | 6 | metal=35, protect=35, energy=25, eldritch=25, void=35 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 342 |
| thaumcraft:VoidRobeHelm | ELDRITCH_PAGE_DEFERRED | thaumcraft:VoidRobeHelm | VOIDROBEARMOR | 6 | metal=25, senses=25, protect=25, energy=25, eldritch=25, void=25 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 341 |
| thaumcraft:VoidRobeLegs | ELDRITCH_PAGE_DEFERRED | thaumcraft:VoidRobeLegs | VOIDROBEARMOR | 6 | metal=30, protect=30, energy=25, eldritch=25, void=30 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 343 |
| thaumcraft:VoidseerPearl | ELDRITCH_PAGE_DEFERRED | thaumcraft:VoidseerPearl | VOIDSEERPEARL | 8 | mind=150, void=150, magic=100 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 370 |
| thaumcraft:VoidSiphon | ELDRITCH_PAGE_DEFERRED | thaumcraft:VoidSiphon | VOIDSIPHON | 7 | eldritch=50, entropy=50, void=100, craft=50 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 369 |
| thaumcraft:JarBrain | GOLEMANCY_PAGE_DEFERRED | thaumcraft:JarBrain | JARBRAIN | 4 | mind=25, senses=25, undead=25 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 280 |
| thaumcraft:MindBiothaumic | GOLEMANCY_PAGE_DEFERRED | thaumcraft:MindBiothaumic | MINDBIOTHAUMIC | 1 | mind=50, mechanism=25 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 334 |
| thaumcraft:SealBreak | GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealBreak | SEALBREAK | 1 | tool=10, entropy=10, man=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 270 |
| thaumcraft:SealButcher | GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealButcher | SEALBUTCHER | 0 | beast=10, senses=10, man=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 269 |
| thaumcraft:SealHarvest | GOLEMANCY_PAGE_DEFERRED | thaumcraft:SealHarvest | SEALHARVEST | 0 | plant=10, senses=10, man=10 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 268 |
| thaumcraft:BootsTraveller | INFUSION_PAGE_DEFERRED | thaumcraft:BootsTraveller | BOOTSTRAVELLER | 1 | flight=100, motion=100 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 333 |
| thaumcraft:CHARMUNDYING | INFUSION_PAGE_DEFERRED | thaumcraft:CHARMUNDYING | CHARMUNDYING | 2 | life=25 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 361 |
| thaumcraft:CLOUDRING | INFUSION_PAGE_DEFERRED | thaumcraft:CLOUDRING | CLOUDRING | 1 | air=50 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 359 |
| thaumcraft:ElementalAxe | INFUSION_PAGE_DEFERRED | thaumcraft:MirrorEssentia | MIRRORESSENTIA | 2 | motion=25, water=25, exchange=25 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 293 |
| thaumcraft:ElementalAxe | INFUSION_PAGE_DEFERRED | thaumcraft:ElementalAxe | ELEMENTALTOOLS | 1 | water=60, plant=30 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 297 |
| thaumcraft:ElementalHoe | INFUSION_PAGE_DEFERRED | thaumcraft:ElementalHoe | ELEMENTALTOOLS | 1 | order=30, plant=30, entropy=30 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 308 |
| thaumcraft:ElementalPick | INFUSION_PAGE_DEFERRED | thaumcraft:ElementalAxe | ELEMENTALTOOLS | 1 | water=60, plant=30 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 297 |
| thaumcraft:ElementalPick | INFUSION_PAGE_DEFERRED | thaumcraft:ElementalPick | ELEMENTALTOOLS | 1 | fire=30, metal=30, senses=30 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 301 |
| thaumcraft:ElementalPick | INFUSION_PAGE_DEFERRED | thaumcraft:PrimalCrusher | PRIMALCRUSHER | 6 | earth=75, tool=75, entropy=50, void=50, aversion=50, eldritch=50, desire=50 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 351 |
| thaumcraft:ElementalShovel | INFUSION_PAGE_DEFERRED | thaumcraft:ElementalSword | ELEMENTALTOOLS | 1 | air=30, motion=30, aversion=30 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 304 |
| thaumcraft:ElementalShovel | INFUSION_PAGE_DEFERRED | thaumcraft:ElementalShovel | ELEMENTALTOOLS | 1 | earth=60, craft=30 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 307 |
| thaumcraft:ElementalShovel | INFUSION_PAGE_DEFERRED | thaumcraft:PrimalCrusher | PRIMALCRUSHER | 6 | earth=75, tool=75, entropy=50, void=50, aversion=50, eldritch=50, desire=50 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 351 |
| thaumcraft:ElementalSword | INFUSION_PAGE_DEFERRED | thaumcraft:ElementalPick | ELEMENTALTOOLS | 1 | fire=30, metal=30, senses=30 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 301 |
| thaumcraft:ElementalSword | INFUSION_PAGE_DEFERRED | thaumcraft:ElementalSword | ELEMENTALTOOLS | 1 | air=30, motion=30, aversion=30 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 304 |
| thaumcraft:MaskAngryGhost | INFUSION_PAGE_DEFERRED | thaumcraft:MaskAngryGhost | FORTRESSMASK |  | entropy=80, death=80, protect=20 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 346 |
| thaumcraft:MaskGrinningDevil | INFUSION_PAGE_DEFERRED | thaumcraft:MaskGrinningDevil | FORTRESSMASK |  | mind=80, life=80, protect=20 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 345 |
| thaumcraft:MaskSippingFiend | INFUSION_PAGE_DEFERRED | thaumcraft:MaskSippingFiend | FORTRESSMASK |  | undead=80, life=80, protect=20 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 347 |
| thaumcraft:CuriosityBand | INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:CuriosityBand | CURIOSITYBAND | 5 | mind=150, void=50, trap=100 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 360 |
| thaumcraft:HelmGoggles | INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:HelmGoggles | FORTRESSMASK |  | senses=40, aura=20, protect=20 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 344 |
| thaumcraft:ThaumiumFortressChest | INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:ThaumiumFortressChest | ARMORFORTRESS | 3 | metal=50, protect=30, energy=25 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 339 |
| thaumcraft:ThaumiumFortressHelm | INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:ThaumiumFortressHelm | ARMORFORTRESS | 3 | metal=50, protect=20, energy=25 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 338 |
| thaumcraft:ThaumiumFortressLegs | INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:ThaumiumFortressLegs | ARMORFORTRESS | 3 | metal=50, protect=25, energy=25 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 340 |
| thaumcraft:VerdantHeart | INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:VerdantHeart | VERDANTCHARMS | 5 | life=60, order=30, plant=60 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 352 |
| thaumcraft:VerdantHeart | INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:VerdantHeartLife | VERDANTCHARMS |  | life=80, man=80 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 355 |
| thaumcraft:VerdantHeart | INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:VerdantHeartSustain | VERDANTCHARMS |  | desire=80, air=80 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 358 |
| thaumcraft:VerdantHeartLife | INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:VerdantHeartLife | VERDANTCHARMS |  | life=80, man=80 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 355 |
| thaumcraft:VerdantHeartSustain | INFUSION_RESEARCH_LEGACY_PAGE_KEY | thaumcraft:VerdantHeartSustain | VERDANTCHARMS |  | desire=80, air=80 | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 358 |

## References without focused infusion candidates

| Reference | Current class | JSON path |
|---|---|---|
| thaumcraft:focus_1 | AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED | $.entries[0].stages[2].recipes[2] |
| thaumcraft:GolemPress | GOLEMANCY_PAGE_DEFERRED | $.entries[8].stages[2].recipes[1] |
| thaumcraft:infusionaltar | INFUSION_PAGE_DEFERRED | $.entries[1].stages[2].recipes[2] |
| thaumcraft:infusionaltarancient | INFUSION_PAGE_DEFERRED | $.entries[4].stages[1].recipes[1] |
| thaumcraft:infusionaltareldritch | INFUSION_PAGE_DEFERRED | $.entries[5].stages[1].recipes[1] |
| thaumcraft:arcane_brick | INFUSION_RESEARCH_LEGACY_PAGE_KEY | $.entries[6].stages[1].recipes[3] |
| thaumcraft:arcane_stone | INFUSION_RESEARCH_LEGACY_PAGE_KEY | $.entries[6].stages[0].recipes[0] |

## Extracted infusion source blocks

### thaumcraft:focus_2

- Current class: AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED
- Resource id: thaumcraft:focus_2
- Research: FOCUSADVANCED@1
- Instability: 3
- Aspects: magic=25, order=50
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:278

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:focus_2"), new InfusionRecipe("FOCUSADVANCED@1", new ItemStack(ItemsTC.focus2), 3, new AspectList().add(Aspect.MAGIC, 25).add(Aspect.ORDER, 50), new ItemStack(ItemsTC.focus1), new ItemStack(ItemsTC.quicksilver), "gemDiamond", new ItemStack(ItemsTC.quicksilver), new ItemStack(Items.ENDER_PEARL)));
```

### thaumcraft:focus_3

- Current class: AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED
- Resource id: thaumcraft:focus_3
- Research: FOCUSGREATER@1
- Instability: 5
- Aspects: magic=25, order=50, void=100
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:279

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:focus_3"), new InfusionRecipe("FOCUSGREATER@1", new ItemStack(ItemsTC.focus3), 5, new AspectList().add(Aspect.MAGIC, 25).add(Aspect.ORDER, 50).add(Aspect.VOID, 100), new ItemStack(ItemsTC.focus2), new ItemStack(ItemsTC.quicksilver), Ingredient.fromItem(ItemsTC.primordialPearl), new ItemStack(ItemsTC.quicksilver), new ItemStack(Items.NETHER_STAR)));
```

### thaumcraft:VisAmulet

- Current class: AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED
- Resource id: thaumcraft:VisAmulet
- Research: VISAMULET
- Instability: 1
- Aspects: aura=50, energy=100, void=50
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:281

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VisAmulet"), new InfusionRecipe("VISAMULET", new ItemStack(ItemsTC.amuletVis, 1, 1), 6, new AspectList().add(Aspect.AURA, 50).add(Aspect.ENERGY, 100).add(Aspect.VOID, 50), new ItemStack(ItemsTC.baubles, 1, 0), new ItemStack(ItemsTC.visResonator), ThaumcraftApiHelper.makeCrystal(Aspect.AIR), ThaumcraftApiHelper.makeCrystal(Aspect.FIRE), ThaumcraftApiHelper.makeCrystal(Aspect.WATER), ThaumcraftApiHelper.makeCrystal(Aspect.EARTH), ThaumcraftApiHelper.makeCrystal(Aspect.ORDER)));
```

### thaumcraft:PrimalCrusher

- Current class: ELDRITCH_PAGE_DEFERRED
- Resource id: thaumcraft:MaskSippingFiend
- Research: FORTRESSMASK
- Instability: 
- Aspects: undead=80, life=80, protect=20
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:347

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskSippingFiend"), new InfusionRecipe("FORTRESSMASK", new Object[] { "mask", new NBTTagInt(2) }, 8, new AspectList().add(Aspect.UNDEAD, 80).add(Aspect.LIFE, 80).add(Aspect.PROTECT, 20), new ItemStack(ItemsTC.fortressHelm, 1, 32767), new ItemStack(Items.DYE, 1, 1), "plateIron", "leather", new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.MILK_BUCKET), "plateIron"));
```

### thaumcraft:PrimalCrusher

- Current class: ELDRITCH_PAGE_DEFERRED
- Resource id: thaumcraft:PrimalCrusher
- Research: PRIMALCRUSHER
- Instability: 6
- Aspects: earth=75, tool=75, entropy=50, void=50, aversion=50, eldritch=50, desire=50
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:351

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:PrimalCrusher"), new InfusionRecipe("PRIMALCRUSHER", isPC, 6, new AspectList().add(Aspect.EARTH, 75).add(Aspect.TOOL, 75).add(Aspect.ENTROPY, 50).add(Aspect.VOID, 50).add(Aspect.AVERSION, 50).add(Aspect.ELDRITCH, 50).add(Aspect.DESIRE, 50), Ingredient.fromItem(ItemsTC.primordialPearl), Ingredient.fromItem(ItemsTC.voidPick), Ingredient.fromItem(ItemsTC.voidShovel), Ingredient.fromItem(ItemsTC.elementalPick), Ingredient.fromItem(ItemsTC.elementalShovel)));
```

### thaumcraft:voidingot

- Current class: ELDRITCH_PAGE_DEFERRED
- Resource id: thaumcraft:VoidseerPearl
- Research: VOIDSEERPEARL
- Instability: 8
- Aspects: mind=150, void=150, magic=100
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:370

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidseerPearl"), new InfusionRecipe("VOIDSEERPEARL", new ItemStack(ItemsTC.charmVoidseer), 8, new AspectList().add(Aspect.MIND, 150).add(Aspect.VOID, 150).add(Aspect.MAGIC, 100), new ItemStack(ItemsTC.baubles, 1, 4), new ItemStack(ItemsTC.brain), new ItemStack(ItemsTC.voidSeed), new ItemStack(ItemsTC.brain), Ingredient.fromItem(ItemsTC.primordialPearl)));
```

### thaumcraft:VoidRobeChest

- Current class: ELDRITCH_PAGE_DEFERRED
- Resource id: thaumcraft:VoidRobeChest
- Research: VOIDROBEARMOR
- Instability: 6
- Aspects: metal=35, protect=35, energy=25, eldritch=25, void=35
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:342

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeChest"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeChest), 6, new AspectList().add(Aspect.METAL, 35).add(Aspect.PROTECT, 35).add(Aspect.ENERGY, 25).add(Aspect.ELDRITCH, 25).add(Aspect.VOID, 35), new ItemStack(ItemsTC.voidChest), new ItemStack(ItemsTC.clothChest), "plateVoid", "plateVoid", new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.fabric), "leather"));
```

### thaumcraft:VoidRobeHelm

- Current class: ELDRITCH_PAGE_DEFERRED
- Resource id: thaumcraft:VoidRobeHelm
- Research: VOIDROBEARMOR
- Instability: 6
- Aspects: metal=25, senses=25, protect=25, energy=25, eldritch=25, void=25
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:341

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeHelm"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeHelm), 6, new AspectList().add(Aspect.METAL, 25).add(Aspect.SENSES, 25).add(Aspect.PROTECT, 25).add(Aspect.ENERGY, 25).add(Aspect.ELDRITCH, 25).add(Aspect.VOID, 25), new ItemStack(ItemsTC.voidHelm), new ItemStack(ItemsTC.goggles, 1, 32767), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric)));
```

### thaumcraft:VoidRobeLegs

- Current class: ELDRITCH_PAGE_DEFERRED
- Resource id: thaumcraft:VoidRobeLegs
- Research: VOIDROBEARMOR
- Instability: 6
- Aspects: metal=30, protect=30, energy=25, eldritch=25, void=30
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:343

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeLegs"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeLegs), 6, new AspectList().add(Aspect.METAL, 30).add(Aspect.PROTECT, 30).add(Aspect.ENERGY, 25).add(Aspect.ELDRITCH, 25).add(Aspect.VOID, 30), new ItemStack(ItemsTC.voidLegs), new ItemStack(ItemsTC.clothLegs), "plateVoid", "plateVoid", new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.fabric), "leather"));
```

### thaumcraft:VoidseerPearl

- Current class: ELDRITCH_PAGE_DEFERRED
- Resource id: thaumcraft:VoidseerPearl
- Research: VOIDSEERPEARL
- Instability: 8
- Aspects: mind=150, void=150, magic=100
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:370

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidseerPearl"), new InfusionRecipe("VOIDSEERPEARL", new ItemStack(ItemsTC.charmVoidseer), 8, new AspectList().add(Aspect.MIND, 150).add(Aspect.VOID, 150).add(Aspect.MAGIC, 100), new ItemStack(ItemsTC.baubles, 1, 4), new ItemStack(ItemsTC.brain), new ItemStack(ItemsTC.voidSeed), new ItemStack(ItemsTC.brain), Ingredient.fromItem(ItemsTC.primordialPearl)));
```

### thaumcraft:VoidSiphon

- Current class: ELDRITCH_PAGE_DEFERRED
- Resource id: thaumcraft:VoidSiphon
- Research: VOIDSIPHON
- Instability: 7
- Aspects: eldritch=50, entropy=50, void=100, craft=50
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:369

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidSiphon"), new InfusionRecipe("VOIDSIPHON", new ItemStack(BlocksTC.voidSiphon), 7, new AspectList().add(Aspect.ELDRITCH, 50).add(Aspect.ENTROPY, 50).add(Aspect.VOID, 100).add(Aspect.CRAFT, 50), new ItemStack(BlocksTC.metalBlockVoid), new ItemStack(BlocksTC.stoneArcane), new ItemStack(BlocksTC.stoneArcane), new ItemStack(ItemsTC.mechanismComplex), "plateBrass", "plateBrass", new ItemStack(Items.NETHER_STAR)));
```

### thaumcraft:JarBrain

- Current class: GOLEMANCY_PAGE_DEFERRED
- Resource id: thaumcraft:JarBrain
- Research: JARBRAIN
- Instability: 4
- Aspects: mind=25, senses=25, undead=25
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:280

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:JarBrain"), new InfusionRecipe("JARBRAIN", new ItemStack(BlocksTC.jarBrain), 4, new AspectList().add(Aspect.MIND, 25).add(Aspect.SENSES, 25).add(Aspect.UNDEAD, 25), new ItemStack(BlocksTC.jarNormal), new ItemStack(ItemsTC.brain), new ItemStack(Items.SPIDER_EYE), new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.SPIDER_EYE)));
```

### thaumcraft:MindBiothaumic

- Current class: GOLEMANCY_PAGE_DEFERRED
- Resource id: thaumcraft:MindBiothaumic
- Research: MINDBIOTHAUMIC
- Instability: 1
- Aspects: mind=50, mechanism=25
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:334

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MindBiothaumic"), new InfusionRecipe("MINDBIOTHAUMIC", new ItemStack(ItemsTC.mind, 1, 1), 4, new AspectList().add(Aspect.MIND, 50).add(Aspect.MECHANISM, 25), new ItemStack(ItemsTC.mind, 1, 0), new ItemStack(ItemsTC.brain), new ItemStack(ItemsTC.mechanismComplex)));
```

### thaumcraft:SealBreak

- Current class: GOLEMANCY_PAGE_DEFERRED
- Resource id: thaumcraft:SealBreak
- Research: SEALBREAK
- Instability: 1
- Aspects: tool=10, entropy=10, man=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:270

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealBreak"), new InfusionRecipe("SEALBREAK", GolemHelper.getSealStack("thaumcraft:breaker"), 1, new AspectList().add(Aspect.TOOL, 10).add(Aspect.ENTROPY, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), Ingredient.fromItem(Items.GOLDEN_AXE), Ingredient.fromItem(Items.GOLDEN_PICKAXE), Ingredient.fromItem(Items.GOLDEN_SHOVEL)));
```

### thaumcraft:SealButcher

- Current class: GOLEMANCY_PAGE_DEFERRED
- Resource id: thaumcraft:SealButcher
- Research: SEALBUTCHER
- Instability: 0
- Aspects: beast=10, senses=10, man=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:269

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealButcher"), new InfusionRecipe("SEALBUTCHER", GolemHelper.getSealStack("thaumcraft:butcher"), 0, new AspectList().add(Aspect.BEAST, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), GolemHelper.getSealStack("thaumcraft:guard"), "leather", new ItemStack(Blocks.WOOL, 1, 32767), new ItemStack(Items.RABBIT_HIDE), new ItemStack(Items.PORKCHOP), new ItemStack(Items.MUTTON), new ItemStack(Items.BEEF)));
```

### thaumcraft:SealHarvest

- Current class: GOLEMANCY_PAGE_DEFERRED
- Resource id: thaumcraft:SealHarvest
- Research: SEALHARVEST
- Instability: 0
- Aspects: plant=10, senses=10, man=10
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:268

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealHarvest"), new InfusionRecipe("SEALHARVEST", GolemHelper.getSealStack("thaumcraft:harvest"), 0, new AspectList().add(Aspect.PLANT, 10).add(Aspect.SENSES, 10).add(Aspect.MAN, 10), new ItemStack(ItemsTC.seals), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.PUMPKIN_SEEDS), new ItemStack(Items.MELON_SEEDS), new ItemStack(Items.BEETROOT_SEEDS), new ItemStack(Items.REEDS), new ItemStack(Blocks.CACTUS)));
```

### thaumcraft:BootsTraveller

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:BootsTraveller
- Research: BOOTSTRAVELLER
- Instability: 1
- Aspects: flight=100, motion=100
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:333

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:BootsTraveller"), new InfusionRecipe("BOOTSTRAVELLER", new ItemStack(ItemsTC.travellerBoots), 1, new AspectList().add(Aspect.FLIGHT, 100).add(Aspect.MOTION, 100), new ItemStack(Items.LEATHER_BOOTS, 1, 32767), ConfigItems.AIR_CRYSTAL, ConfigItems.AIR_CRYSTAL, new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.fabric), new ItemStack(Items.FEATHER), new ItemStack(Items.FISH, 1, 32767)));
```

### thaumcraft:CHARMUNDYING

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:CHARMUNDYING
- Research: CHARMUNDYING
- Instability: 2
- Aspects: life=25
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:361

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CHARMUNDYING"), new InfusionRecipe("CHARMUNDYING", new ItemStack(ItemsTC.charmUndying), 2, new AspectList().add(Aspect.LIFE, 25), new ItemStack(Items.TOTEM_OF_UNDYING), "plateBrass"));
```

### thaumcraft:CLOUDRING

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:CLOUDRING
- Research: CLOUDRING
- Instability: 1
- Aspects: air=50
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:359

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CLOUDRING"), new InfusionRecipe("CLOUDRING", new ItemStack(ItemsTC.ringCloud), 1, new AspectList().add(Aspect.AIR, 50), new ItemStack(ItemsTC.baubles, 1, 1), ConfigItems.AIR_CRYSTAL, new ItemStack(Items.FEATHER)));
```

### thaumcraft:ElementalAxe

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:MirrorEssentia
- Research: MIRRORESSENTIA
- Instability: 2
- Aspects: motion=25, water=25, exchange=25
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:293

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MirrorEssentia"), new InfusionRecipe("MIRRORESSENTIA", new ItemStack(BlocksTC.mirrorEssentia), 2, new AspectList().add(Aspect.MOTION, 25).add(Aspect.WATER, 25).add(Aspect.EXCHANGE, 25), new ItemStack(ItemsTC.mirroredGlass), "ingotIron", "ingotIron", "ingotIron", new ItemStack(Items.ENDER_PEARL)));
```

### thaumcraft:ElementalAxe

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:ElementalAxe
- Research: ELEMENTALTOOLS
- Instability: 1
- Aspects: water=60, plant=30
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:297

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalAxe"), new InfusionRecipe("ELEMENTALTOOLS", isEA, 1, new AspectList().add(Aspect.WATER, 60).add(Aspect.PLANT, 30), new ItemStack(ItemsTC.thaumiumAxe, 1, 32767), ConfigItems.WATER_CRYSTAL, ConfigItems.WATER_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
```

### thaumcraft:ElementalHoe

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:ElementalHoe
- Research: ELEMENTALTOOLS
- Instability: 1
- Aspects: order=30, plant=30, entropy=30
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:308

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalHoe"), new InfusionRecipe("ELEMENTALTOOLS", new ItemStack(ItemsTC.elementalHoe), 1, new AspectList().add(Aspect.ORDER, 30).add(Aspect.PLANT, 30).add(Aspect.ENTROPY, 30), new ItemStack(ItemsTC.thaumiumHoe, 1, 32767), ConfigItems.ORDER_CRYSTAL, ConfigItems.ENTROPY_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
```

### thaumcraft:ElementalPick

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:ElementalAxe
- Research: ELEMENTALTOOLS
- Instability: 1
- Aspects: water=60, plant=30
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:297

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalAxe"), new InfusionRecipe("ELEMENTALTOOLS", isEA, 1, new AspectList().add(Aspect.WATER, 60).add(Aspect.PLANT, 30), new ItemStack(ItemsTC.thaumiumAxe, 1, 32767), ConfigItems.WATER_CRYSTAL, ConfigItems.WATER_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
```

### thaumcraft:ElementalPick

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:ElementalPick
- Research: ELEMENTALTOOLS
- Instability: 1
- Aspects: fire=30, metal=30, senses=30
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:301

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalPick"), new InfusionRecipe("ELEMENTALTOOLS", isEP, 1, new AspectList().add(Aspect.FIRE, 30).add(Aspect.METAL, 30).add(Aspect.SENSES, 30), new ItemStack(ItemsTC.thaumiumPick, 1, 32767), ConfigItems.FIRE_CRYSTAL, ConfigItems.FIRE_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
```

### thaumcraft:ElementalPick

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:PrimalCrusher
- Research: PRIMALCRUSHER
- Instability: 6
- Aspects: earth=75, tool=75, entropy=50, void=50, aversion=50, eldritch=50, desire=50
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:351

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:PrimalCrusher"), new InfusionRecipe("PRIMALCRUSHER", isPC, 6, new AspectList().add(Aspect.EARTH, 75).add(Aspect.TOOL, 75).add(Aspect.ENTROPY, 50).add(Aspect.VOID, 50).add(Aspect.AVERSION, 50).add(Aspect.ELDRITCH, 50).add(Aspect.DESIRE, 50), Ingredient.fromItem(ItemsTC.primordialPearl), Ingredient.fromItem(ItemsTC.voidPick), Ingredient.fromItem(ItemsTC.voidShovel), Ingredient.fromItem(ItemsTC.elementalPick), Ingredient.fromItem(ItemsTC.elementalShovel)));
```

### thaumcraft:ElementalShovel

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:ElementalSword
- Research: ELEMENTALTOOLS
- Instability: 1
- Aspects: air=30, motion=30, aversion=30
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:304

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalSword"), new InfusionRecipe("ELEMENTALTOOLS", isESW, 1, new AspectList().add(Aspect.AIR, 30).add(Aspect.MOTION, 30).add(Aspect.AVERSION, 30), new ItemStack(ItemsTC.thaumiumSword, 1, 32767), ConfigItems.AIR_CRYSTAL, ConfigItems.AIR_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
```

### thaumcraft:ElementalShovel

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:ElementalShovel
- Research: ELEMENTALTOOLS
- Instability: 1
- Aspects: earth=60, craft=30
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:307

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalShovel"), new InfusionRecipe("ELEMENTALTOOLS", isES, 1, new AspectList().add(Aspect.EARTH, 60).add(Aspect.CRAFT, 30), new ItemStack(ItemsTC.thaumiumShovel, 1, 32767), ConfigItems.EARTH_CRYSTAL, ConfigItems.EARTH_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
```

### thaumcraft:ElementalShovel

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:PrimalCrusher
- Research: PRIMALCRUSHER
- Instability: 6
- Aspects: earth=75, tool=75, entropy=50, void=50, aversion=50, eldritch=50, desire=50
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:351

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:PrimalCrusher"), new InfusionRecipe("PRIMALCRUSHER", isPC, 6, new AspectList().add(Aspect.EARTH, 75).add(Aspect.TOOL, 75).add(Aspect.ENTROPY, 50).add(Aspect.VOID, 50).add(Aspect.AVERSION, 50).add(Aspect.ELDRITCH, 50).add(Aspect.DESIRE, 50), Ingredient.fromItem(ItemsTC.primordialPearl), Ingredient.fromItem(ItemsTC.voidPick), Ingredient.fromItem(ItemsTC.voidShovel), Ingredient.fromItem(ItemsTC.elementalPick), Ingredient.fromItem(ItemsTC.elementalShovel)));
```

### thaumcraft:ElementalSword

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:ElementalPick
- Research: ELEMENTALTOOLS
- Instability: 1
- Aspects: fire=30, metal=30, senses=30
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:301

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalPick"), new InfusionRecipe("ELEMENTALTOOLS", isEP, 1, new AspectList().add(Aspect.FIRE, 30).add(Aspect.METAL, 30).add(Aspect.SENSES, 30), new ItemStack(ItemsTC.thaumiumPick, 1, 32767), ConfigItems.FIRE_CRYSTAL, ConfigItems.FIRE_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
```

### thaumcraft:ElementalSword

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:ElementalSword
- Research: ELEMENTALTOOLS
- Instability: 1
- Aspects: air=30, motion=30, aversion=30
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:304

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalSword"), new InfusionRecipe("ELEMENTALTOOLS", isESW, 1, new AspectList().add(Aspect.AIR, 30).add(Aspect.MOTION, 30).add(Aspect.AVERSION, 30), new ItemStack(ItemsTC.thaumiumSword, 1, 32767), ConfigItems.AIR_CRYSTAL, ConfigItems.AIR_CRYSTAL, new ItemStack(ItemsTC.nuggets, 1, 10), new ItemStack(BlocksTC.plankGreatwood)));
```

### thaumcraft:MaskAngryGhost

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:MaskAngryGhost
- Research: FORTRESSMASK
- Instability: 
- Aspects: entropy=80, death=80, protect=20
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:346

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskAngryGhost"), new InfusionRecipe("FORTRESSMASK", new Object[] { "mask", new NBTTagInt(1) }, 8, new AspectList().add(Aspect.ENTROPY, 80).add(Aspect.DEATH, 80).add(Aspect.PROTECT, 20), new ItemStack(ItemsTC.fortressHelm, 1, 32767), new ItemStack(Items.DYE, 1, 15), "plateIron", "leather", new ItemStack(Items.POISONOUS_POTATO), new ItemStack(Items.SKULL, 1, 1), "plateIron"));
```

### thaumcraft:MaskGrinningDevil

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:MaskGrinningDevil
- Research: FORTRESSMASK
- Instability: 
- Aspects: mind=80, life=80, protect=20
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:345

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskGrinningDevil"), new InfusionRecipe("FORTRESSMASK", new Object[] { "mask", new NBTTagInt(0) }, 8, new AspectList().add(Aspect.MIND, 80).add(Aspect.LIFE, 80).add(Aspect.PROTECT, 20), new ItemStack(ItemsTC.fortressHelm, 1, 32767), new ItemStack(Items.DYE, 1, 0), "plateIron", "leather", new ItemStack(BlocksTC.shimmerleaf), new ItemStack(ItemsTC.brain), "plateIron"));
```

### thaumcraft:MaskSippingFiend

- Current class: INFUSION_PAGE_DEFERRED
- Resource id: thaumcraft:MaskSippingFiend
- Research: FORTRESSMASK
- Instability: 
- Aspects: undead=80, life=80, protect=20
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:347

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskSippingFiend"), new InfusionRecipe("FORTRESSMASK", new Object[] { "mask", new NBTTagInt(2) }, 8, new AspectList().add(Aspect.UNDEAD, 80).add(Aspect.LIFE, 80).add(Aspect.PROTECT, 20), new ItemStack(ItemsTC.fortressHelm, 1, 32767), new ItemStack(Items.DYE, 1, 1), "plateIron", "leather", new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.MILK_BUCKET), "plateIron"));
```

### thaumcraft:CuriosityBand

- Current class: INFUSION_RESEARCH_LEGACY_PAGE_KEY
- Resource id: thaumcraft:CuriosityBand
- Research: CURIOSITYBAND
- Instability: 5
- Aspects: mind=150, void=50, trap=100
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:360

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CuriosityBand"), new InfusionRecipe("CURIOSITYBAND", new ItemStack(ItemsTC.bandCuriosity), 5, new AspectList().add(Aspect.MIND, 150).add(Aspect.VOID, 50).add(Aspect.TRAP, 100), new ItemStack(ItemsTC.baubles, 1, 6), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK), new ItemStack(Items.EMERALD), new ItemStack(Items.WRITABLE_BOOK)));
```

### thaumcraft:HelmGoggles

- Current class: INFUSION_RESEARCH_LEGACY_PAGE_KEY
- Resource id: thaumcraft:HelmGoggles
- Research: FORTRESSMASK
- Instability: 
- Aspects: senses=40, aura=20, protect=20
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:344

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:HelmGoggles"), new InfusionRecipe("FORTRESSMASK", new Object[] { "goggles", new NBTTagByte((byte)1) }, 5, new AspectList().add(Aspect.SENSES, 40).add(Aspect.AURA, 20).add(Aspect.PROTECT, 20), new ItemStack(ItemsTC.fortressHelm, 1, 32767), new ItemStack(Items.SLIME_BALL), new ItemStack(ItemsTC.goggles, 1, 32767)));
```

### thaumcraft:ThaumiumFortressChest

- Current class: INFUSION_RESEARCH_LEGACY_PAGE_KEY
- Resource id: thaumcraft:ThaumiumFortressChest
- Research: ARMORFORTRESS
- Instability: 3
- Aspects: metal=50, protect=30, energy=25
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:339

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ThaumiumFortressChest"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressChest), 3, new AspectList().add(Aspect.METAL, 50).add(Aspect.PROTECT, 30).add(Aspect.ENERGY, 25), new ItemStack(ItemsTC.thaumiumChest, 1, 32767), "plateThaumium", "plateThaumium", "plateThaumium", "plateThaumium", new ItemStack(Items.GOLD_INGOT), "leather"));
```

### thaumcraft:ThaumiumFortressHelm

- Current class: INFUSION_RESEARCH_LEGACY_PAGE_KEY
- Resource id: thaumcraft:ThaumiumFortressHelm
- Research: ARMORFORTRESS
- Instability: 3
- Aspects: metal=50, protect=20, energy=25
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:338

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ThaumiumFortressHelm"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressHelm), 3, new AspectList().add(Aspect.METAL, 50).add(Aspect.PROTECT, 20).add(Aspect.ENERGY, 25), new ItemStack(ItemsTC.thaumiumHelm, 1, 32767), "plateThaumium", "plateThaumium", new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.EMERALD)));
```

### thaumcraft:ThaumiumFortressLegs

- Current class: INFUSION_RESEARCH_LEGACY_PAGE_KEY
- Resource id: thaumcraft:ThaumiumFortressLegs
- Research: ARMORFORTRESS
- Instability: 3
- Aspects: metal=50, protect=25, energy=25
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:340

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ThaumiumFortressLegs"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressLegs), 3, new AspectList().add(Aspect.METAL, 50).add(Aspect.PROTECT, 25).add(Aspect.ENERGY, 25), new ItemStack(ItemsTC.thaumiumLegs, 1, 32767), "plateThaumium", "plateThaumium", "plateThaumium", new ItemStack(Items.GOLD_INGOT), "leather"));
```

### thaumcraft:VerdantHeart

- Current class: INFUSION_RESEARCH_LEGACY_PAGE_KEY
- Resource id: thaumcraft:VerdantHeart
- Research: VERDANTCHARMS
- Instability: 5
- Aspects: life=60, order=30, plant=60
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:352

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeart"), new InfusionRecipe("VERDANTCHARMS", new ItemStack(ItemsTC.charmVerdant), 5, new AspectList().add(Aspect.LIFE, 60).add(Aspect.ORDER, 30).add(Aspect.PLANT, 60), new ItemStack(ItemsTC.baubles, 1, 4), new ItemStack(ItemsTC.nuggets, 1, 10), ThaumcraftApiHelper.makeCrystal(Aspect.LIFE), new ItemStack(Items.MILK_BUCKET), ThaumcraftApiHelper.makeCrystal(Aspect.PLANT)));
```

### thaumcraft:VerdantHeart

- Current class: INFUSION_RESEARCH_LEGACY_PAGE_KEY
- Resource id: thaumcraft:VerdantHeartLife
- Research: VERDANTCHARMS
- Instability: 
- Aspects: life=80, man=80
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:355

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeartLife"), new InfusionRecipe("VERDANTCHARMS", new Object[] { "type", new NBTTagByte((byte)1) }, 5, new AspectList().add(Aspect.LIFE, 80).add(Aspect.MAN, 80), new ItemStack(ItemsTC.charmVerdant), new ItemStack(Items.GOLDEN_APPLE), ThaumcraftApiHelper.makeCrystal(Aspect.LIFE), pis1, ThaumcraftApiHelper.makeCrystal(Aspect.MAN)));
```

### thaumcraft:VerdantHeart

- Current class: INFUSION_RESEARCH_LEGACY_PAGE_KEY
- Resource id: thaumcraft:VerdantHeartSustain
- Research: VERDANTCHARMS
- Instability: 
- Aspects: desire=80, air=80
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:358

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeartSustain"), new InfusionRecipe("VERDANTCHARMS", new Object[] { "type", new NBTTagByte((byte)2) }, 5, new AspectList().add(Aspect.DESIRE, 80).add(Aspect.AIR, 80), new ItemStack(ItemsTC.charmVerdant), new ItemStack(ItemsTC.tripleMeatTreat), ThaumcraftApiHelper.makeCrystal(Aspect.DESIRE), pis2, ThaumcraftApiHelper.makeCrystal(Aspect.AIR)));
```

### thaumcraft:VerdantHeartLife

- Current class: INFUSION_RESEARCH_LEGACY_PAGE_KEY
- Resource id: thaumcraft:VerdantHeartLife
- Research: VERDANTCHARMS
- Instability: 
- Aspects: life=80, man=80
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:355

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeartLife"), new InfusionRecipe("VERDANTCHARMS", new Object[] { "type", new NBTTagByte((byte)1) }, 5, new AspectList().add(Aspect.LIFE, 80).add(Aspect.MAN, 80), new ItemStack(ItemsTC.charmVerdant), new ItemStack(Items.GOLDEN_APPLE), ThaumcraftApiHelper.makeCrystal(Aspect.LIFE), pis1, ThaumcraftApiHelper.makeCrystal(Aspect.MAN)));
```

### thaumcraft:VerdantHeartSustain

- Current class: INFUSION_RESEARCH_LEGACY_PAGE_KEY
- Resource id: thaumcraft:VerdantHeartSustain
- Research: VERDANTCHARMS
- Instability: 
- Aspects: desire=80, air=80
- Source: src/main/java/thaumcraft/common/config/ConfigRecipes.java:358

```java
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeartSustain"), new InfusionRecipe("VERDANTCHARMS", new Object[] { "type", new NBTTagByte((byte)2) }, 5, new AspectList().add(Aspect.DESIRE, 80).add(Aspect.AIR, 80), new ItemStack(ItemsTC.charmVerdant), new ItemStack(ItemsTC.tripleMeatTreat), ThaumcraftApiHelper.makeCrystal(Aspect.DESIRE), pis2, ThaumcraftApiHelper.makeCrystal(Aspect.AIR)));
```

## Next implementation guidance

1. Add a modern `thaumcraft:infusion` recipe/page boundary before adding these JSON recipes.
2. Keep in-world infusion crafting behavior deferred; first target recipe loading and Thaumonomicon page display.
3. Add a small first JSON batch after the serializer/catalog/view boundary is in place.
