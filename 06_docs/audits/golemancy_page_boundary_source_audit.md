# Golemancy Page Boundary Source Audit

Generated: 2026-06-18 16:33:15 +03:00

## Purpose

This document extracts legacy source evidence for current `GOLEMANCY_PAGE_DEFERRED` recipe-page references. It is an analysis artifact only; do not treat every hit as a recipe implementation target.

## Summary

| Metric | Count |
|---|---:|
| Golemancy deferred references | 1 |
| References with at least one source hit | 1 |
| References without source hit | 0 |
| Legacy Java files scanned | 902 |
| Source hits | 3 |

## Boundary classification

| Classification | Count |
|---|---:|
| GOLEM_MACHINE_BOUNDARY | 1 |

## Source hit kind distribution

| Hit kind | Count |
|---|---:|
| GOLEM_BLOCK_OR_ITEM_SOURCE | 3 |

## Deferred reference overview

| Reference | Classification | Research file | JSON path | Source hits |
|---|---|---|---|---:|
| thaumcraft:GolemPress | GOLEM_MACHINE_BOUNDARY | 05_neoforge_port/src/main/resources/data/thaumcraft/research/golemancy.json | $.entries[8].stages[2].recipes[1] | 3 |

## Source hit overview

| Reference | Hit kind | File | Line | Matched term |
|---|---|---|---:|---|
| thaumcraft:GolemPress | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 122 | GolemPress |
| thaumcraft:GolemPress | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 123 | GolemPress |
| thaumcraft:GolemPress | GOLEM_BLOCK_OR_ITEM_SOURCE | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 124 | thaumcraft:GolemPress |

## Extracted source contexts

### thaumcraft:GolemPress @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:122

- Classification: GOLEM_MACHINE_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 118

```java
        Part GP2 = new Part(Blocks.CAULDRON, new ItemStack(BlocksTC.placeholderCauldron));
        Part GP3 = new Part(Blocks.PISTON.getDefaultState().withProperty((IProperty)BlockPistonBase.FACING, (Comparable)EnumFacing.UP), BlocksTC.golemBuilder);
        Part GP4 = new Part(Blocks.ANVIL, new ItemStack(BlocksTC.placeholderAnvil));
        Part GP5 = new Part(BlocksTC.tableStone, new ItemStack(BlocksTC.placeholderTable));
        Part[][][] golempressBlueprint = { { { null, null }, { GP1, null } }, { { GP2, GP4 }, { GP3, GP5 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("MINDCLOCKWORK", golempressBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation("thaumcraft:GolemPress"), new ThaumcraftApi.BluePrint("MINDCLOCKWORK", new ItemStack(BlocksTC.golemBuilder), golempressBlueprint, new ItemStack(Blocks.IRON_BARS), new ItemStack(Items.CAULDRON), new ItemStack(Blocks.PISTON), new ItemStack(Blocks.ANVIL), new ItemStack(BlocksTC.tableStone)));
    }

```

### thaumcraft:GolemPress @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:123

- Classification: GOLEM_MACHINE_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 119

```java
        Part GP3 = new Part(Blocks.PISTON.getDefaultState().withProperty((IProperty)BlockPistonBase.FACING, (Comparable)EnumFacing.UP), BlocksTC.golemBuilder);
        Part GP4 = new Part(Blocks.ANVIL, new ItemStack(BlocksTC.placeholderAnvil));
        Part GP5 = new Part(BlocksTC.tableStone, new ItemStack(BlocksTC.placeholderTable));
        Part[][][] golempressBlueprint = { { { null, null }, { GP1, null } }, { { GP2, GP4 }, { GP3, GP5 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("MINDCLOCKWORK", golempressBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation("thaumcraft:GolemPress"), new ThaumcraftApi.BluePrint("MINDCLOCKWORK", new ItemStack(BlocksTC.golemBuilder), golempressBlueprint, new ItemStack(Blocks.IRON_BARS), new ItemStack(Items.CAULDRON), new ItemStack(Blocks.PISTON), new ItemStack(Blocks.ANVIL), new ItemStack(BlocksTC.tableStone)));
    }

    public static void initializeAlchemyRecipes() {
```

### thaumcraft:GolemPress @ src/main/java/thaumcraft/common/config/ConfigRecipes.java:124

- Classification: GOLEM_MACHINE_BOUNDARY
- Hit kind: GOLEM_BLOCK_OR_ITEM_SOURCE
- Context start line: 120

```java
        Part GP4 = new Part(Blocks.ANVIL, new ItemStack(BlocksTC.placeholderAnvil));
        Part GP5 = new Part(BlocksTC.tableStone, new ItemStack(BlocksTC.placeholderTable));
        Part[][][] golempressBlueprint = { { { null, null }, { GP1, null } }, { { GP2, GP4 }, { GP3, GP5 } } };
        IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("MINDCLOCKWORK", golempressBlueprint));
        ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation("thaumcraft:GolemPress"), new ThaumcraftApi.BluePrint("MINDCLOCKWORK", new ItemStack(BlocksTC.golemBuilder), golempressBlueprint, new ItemStack(Blocks.IRON_BARS), new ItemStack(Items.CAULDRON), new ItemStack(Blocks.PISTON), new ItemStack(Blocks.ANVIL), new ItemStack(BlocksTC.tableStone)));
    }

    public static void initializeAlchemyRecipes() {
        ResourceLocation visCrystalGroup = new ResourceLocation("thaumcraft:viscrystalgroup");
```

## Next implementation guidance

1. Do not implement all golemancy deferred references as recipes blindly.
2. Separate seal behavior placeholders from actual crafting/arcane recipes.
3. Treat `GolemPress`, `JarBrain`, and `MindBiothaumic` as block/item/machine boundaries until verified against exact source.
4. Choose one narrow family per batch, then re-run research recipe page gap audit.
