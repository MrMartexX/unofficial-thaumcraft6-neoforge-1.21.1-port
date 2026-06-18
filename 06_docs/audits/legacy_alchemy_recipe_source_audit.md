# Legacy Alchemy Recipe Source Audit

Generated: 2026-06-18 21:35:54 +03:00

## Summary

| Metric | Count |
|---|---:|
| Alchemy missing recipe-page references from current audit | 0 |
| References with at least one legacy source hit | 0 |
| References without direct source hit | 0 |
| Legacy Java files scanned | 902 |
| Legacy API/pattern source hits | 361 |

## Alchemy reference family distribution

| Family | Count |
|---|---:|

## Legacy recipe API/pattern hit distribution

| Pattern | Count |
|---|---:|
| ShapedArcane | 78 |
| ConfigRecipes | 70 |
| CrucibleRecipe | 62 |
| addInfusionCraftingRecipe | 57 |
| addCrucibleRecipe | 43 |
| InfusionRecipe | 35 |
| ShapelessArcane | 16 |

## Alchemy page references and legacy source hits

No alchemy missing recipe-page references were found in the current research page gap audit.

## Representative legacy source hits

No direct legacy source hits were found for the alchemy page references.

## Legacy recipe API/pattern hit samples

| Pattern | File | Line | Snippet |
|---|---|---:|---|
| addCrucibleRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 219 | public static void addCrucibleRecipe(ResourceLocation registry, CrucibleRecipe recipe) { |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 131 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:vis_crystal_" + aspect.getTag()), new CrucibleRecipe("BASEALCHEMY", ThaumcraftApiHelper.makeCrystal(aspect), "nuggetQuartz", new AspectList().add(aspect, 2 ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 134 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:nitor"), new CrucibleRecipe("UNLOCKALCHEMY@3", new ItemStack(BlocksTC.nitor.get(EnumDyeColor.YELLOW)), "dustGlowstone", new AspectList().merge(Aspect.ENERG ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 140 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:alumentum"), new CrucibleRecipe("ALUMENTUM", new ItemStack(ItemsTC.alumentum), new ItemStack(Items.COAL, 1, 32767), new AspectList().merge(Aspect.ENERGY, 1 ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 141 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:brassingot"), new CrucibleRecipe("METALLURGY@1", new ItemStack(ItemsTC.ingots, 1, 2), "ingotIron", new AspectList().merge(Aspect.TOOL, 5))); |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 142 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:thaumiumingot"), new CrucibleRecipe("METALLURGY@2", new ItemStack(ItemsTC.ingots, 1, 0), "ingotIron", new AspectList().merge(Aspect.MAGIC, 5).merge(Aspect. ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 143 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:voidingot"), new CrucibleRecipe("BASEELDRITCH", new ItemStack(ItemsTC.ingots, 1, 1), new ItemStack(ItemsTC.voidSeed), new AspectList().merge(Aspect.METAL,  ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 144 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_tallow"), new CrucibleRecipe("HEDGEALCHEMY@1", new ItemStack(ItemsTC.tallow), new ItemStack(Items.ROTTEN_FLESH), new AspectList().merge(Aspect.FIRE,  ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 145 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_leather"), new CrucibleRecipe("HEDGEALCHEMY@1", new ItemStack(Items.LEATHER), new ItemStack(Items.ROTTEN_FLESH), new AspectList().merge(Aspect.AIR, 3 ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 146 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:focus_1"), new CrucibleRecipe("UNLOCKAUROMANCY", new ItemStack(ItemsTC.focus1), ConfigItems.ORDER_CRYSTAL, new AspectList().merge(Aspect.CRYSTAL, 20).merge ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 148 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:metal_purification_iron"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters, 1, 0), "oreIron", new AspectList().merge(Aspect.METAL, 5 ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 149 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:metal_purification_gold"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters, 1, 1), "oreGold", new AspectList().merge(Aspect.METAL, 5 ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 150 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:metal_purification_cinnabar"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters, 1, 6), "oreCinnabar", new AspectList().merge(Aspect. ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 152 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:metal_purification_copper"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters, 1, 2), "oreCopper", new AspectList().merge(Aspect.META ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 155 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:metal_purification_tin"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters, 1, 3), "oreTin", new AspectList().merge(Aspect.METAL, 5). ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 158 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:metal_purification_silver"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters, 1, 4), "oreSilver", new AspectList().merge(Aspect.META ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 161 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:metal_purification_lead"), new CrucibleRecipe("METALPURIFICATION", new ItemStack(ItemsTC.clusters, 1, 5), "oreLead", new AspectList().merge(Aspect.METAL, 5 ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 163 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:LiquidDeath"), new CrucibleRecipe("LIQUIDDEATH", FluidUtil.getFilledBucket(new FluidStack(ConfigBlocks.FluidDeath.instance, 1000)), new ItemStack(Items.BUC ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 164 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:BottleTaint"), new CrucibleRecipe("BOTTLETAINT", new ItemStack(ItemsTC.bottleTaint), ItemPhial.makeFilledPhial(Aspect.FLUX), new AspectList().add(Aspect.FL ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 165 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:BathSalts"), new CrucibleRecipe("BATHSALTS", new ItemStack(ItemsTC.bathSalts), new ItemStack(ItemsTC.salisMundus), new AspectList().add(Aspect.MIND, 40).ad ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 166 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SaneSoap"), new CrucibleRecipe("SANESOAP", new ItemStack(ItemsTC.sanitySoap), new ItemStack(BlocksTC.fleshBlock), new AspectList().add(Aspect.MIND, 75).add ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 167 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealCollect"), new CrucibleRecipe("SEALCOLLECT", GolemHelper.getSealStack("thaumcraft:pickup"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.D ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 168 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealCollectAdv"), new CrucibleRecipe("SEALCOLLECT&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:pickup_advanced"), GolemHelper.getSealStack("thaum ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 169 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStore"), new CrucibleRecipe("SEALSTORE", GolemHelper.getSealStack("thaumcraft:fill"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSIO ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 170 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStoreAdv"), new CrucibleRecipe("SEALSTORE&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:fill_advanced"), GolemHelper.getSealStack("thaumcraft: ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 171 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmpty"), new CrucibleRecipe("SEALEMPTY", GolemHelper.getSealStack("thaumcraft:empty"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.VOID,  ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 172 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealEmptyAdv"), new CrucibleRecipe("SEALEMPTY&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:empty_advanced"), GolemHelper.getSealStack("thaumcraft ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 173 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealProvide"), new CrucibleRecipe("SEALPROVIDE", GolemHelper.getSealStack("thaumcraft:provider"), GolemHelper.getSealStack("thaumcraft:empty_advanced"), ne ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 174 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealStock"), new CrucibleRecipe("SEALSTOCK", GolemHelper.getSealStack("thaumcraft:stock"), GolemHelper.getSealStack("thaumcraft:fill"), new AspectList().ad ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 175 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuard"), new CrucibleRecipe("SEALGUARD", GolemHelper.getSealStack("thaumcraft:guard"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.AVERSI ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 176 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealGuardAdv"), new CrucibleRecipe("SEALGUARD&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:guard_advanced"), GolemHelper.getSealStack("thaumcraft ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 177 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealLumber"), new CrucibleRecipe("SEALLUMBER", GolemHelper.getSealStack("thaumcraft:lumber"), GolemHelper.getSealStack("thaumcraft:breaker"), new AspectLis ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 178 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealUse"), new CrucibleRecipe("SEALUSE", GolemHelper.getSealStack("thaumcraft:use"), new ItemStack(ItemsTC.seals), new AspectList().add(Aspect.CRAFT, 20).a ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 179 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:SealBreakAdv"), new CrucibleRecipe("SEALBREAK&&MINDBIOTHAUMIC", GolemHelper.getSealStack("thaumcraft:breaker_advanced"), GolemHelper.getSealStack("thaumcra ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 180 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:EverfullUrn"), new CrucibleRecipe("EVERFULLURN", new ItemStack(BlocksTC.everfullUrn), new ItemStack(Items.FLOWER_POT), new AspectList().add(Aspect.WATER, 3 ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 549 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_gunpowder"), new CrucibleRecipe("HEDGEALCHEMY@2", new ItemStack(Items.GUNPOWDER, 2, 0), new ItemStack(Items.GUNPOWDER), new AspectList(new ItemStack( ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 550 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_slime"), new CrucibleRecipe("HEDGEALCHEMY@2", new ItemStack(Items.SLIME_BALL, 2, 0), new ItemStack(Items.SLIME_BALL), new AspectList(new ItemStack(It ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 551 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_glowstone"), new CrucibleRecipe("HEDGEALCHEMY@2", new ItemStack(Items.GLOWSTONE_DUST, 2, 0), "dustGlowstone", new AspectList(new ItemStack(Items.GLOW ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 552 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_dye"), new CrucibleRecipe("HEDGEALCHEMY@2", new ItemStack(Items.DYE, 2, 0), new ItemStack(Items.DYE, 1, 0), new AspectList(new ItemStack(Items.DYE))) ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 553 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_clay"), new CrucibleRecipe("HEDGEALCHEMY@3", new ItemStack(Items.CLAY_BALL, 1, 0), new ItemStack(Blocks.DIRT), new AspectList(new ItemStack(Items.CLA ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 554 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_string"), new CrucibleRecipe("HEDGEALCHEMY@3", new ItemStack(Items.STRING), new ItemStack(Items.WHEAT), new AspectList(new ItemStack(Items.STRING)).r ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 555 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_web"), new CrucibleRecipe("HEDGEALCHEMY@3", new ItemStack(Blocks.WEB), new ItemStack(Items.STRING), new AspectList(new ItemStack(Blocks.WEB)).remove( ... |
| addCrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 556 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:hedge_lava"), new CrucibleRecipe("HEDGEALCHEMY@3", new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.BUCKET), new AspectList().add(Aspect.FIRE, 15).add ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 190 | public static void addInfusionCraftingRecipe(ResourceLocation registry, InfusionRecipe recipe) |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 268 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealHarvest"), new InfusionRecipe("SEALHARVEST", GolemHelper.getSealStack("thaumcraft:harvest"), 0, new AspectList().add(Aspect.PLANT, 10).add(Aspe ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 269 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealButcher"), new InfusionRecipe("SEALBUTCHER", GolemHelper.getSealStack("thaumcraft:butcher"), 0, new AspectList().add(Aspect.BEAST, 10).add(Aspe ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 270 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:SealBreak"), new InfusionRecipe("SEALBREAK", GolemHelper.getSealStack("thaumcraft:breaker"), 1, new AspectList().add(Aspect.TOOL, 10).add(Aspect.EN ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 271 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterAir"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalAir), 0, new AspectList().add(Aspect.AIR, 10).add(Aspect.CRY ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 272 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterFire"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalFire), 0, new AspectList().add(Aspect.FIRE, 10).add(Aspect. ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 273 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterWater"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalWater), 0, new AspectList().add(Aspect.WATER, 10).add(Aspe ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 274 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterEarth"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalEarth), 0, new AspectList().add(Aspect.EARTH, 10).add(Aspe ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 275 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterOrder"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalOrder), 0, new AspectList().add(Aspect.ORDER, 10).add(Aspe ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 276 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterEntropy"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalEntropy), 0, new AspectList().add(Aspect.ENTROPY, 10).ad ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 277 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CrystalClusterFlux"), new InfusionRecipe("CRYSTALFARMER", new ItemStack(BlocksTC.crystalTaint), 4, new AspectList().add(Aspect.FLUX, 10).add(Aspect ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 278 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:focus_2"), new InfusionRecipe("FOCUSADVANCED@1", new ItemStack(ItemsTC.focus2), 3, new AspectList().add(Aspect.MAGIC, 25).add(Aspect.ORDER, 50), ne ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 279 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:focus_3"), new InfusionRecipe("FOCUSGREATER@1", new ItemStack(ItemsTC.focus3), 5, new AspectList().add(Aspect.MAGIC, 25).add(Aspect.ORDER, 50).add( ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 280 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:JarBrain"), new InfusionRecipe("JARBRAIN", new ItemStack(BlocksTC.jarBrain), 4, new AspectList().add(Aspect.MIND, 25).add(Aspect.SENSES, 25).add(As ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 281 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VisAmulet"), new InfusionRecipe("VISAMULET", new ItemStack(ItemsTC.amuletVis, 1, 1), 6, new AspectList().add(Aspect.AURA, 50).add(Aspect.ENERGY, 10 ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 283 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:RunicArmor"), ra); |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 291 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:Mirror"), new InfusionRecipe("MIRROR", new ItemStack(BlocksTC.mirror), 1, new AspectList().add(Aspect.MOTION, 25).add(Aspect.DARKNESS, 25).add(Aspe ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 292 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MirrorHand"), new InfusionRecipe("MIRRORHAND", new ItemStack(ItemsTC.handMirror), 5, new AspectList().add(Aspect.TOOL, 50).add(Aspect.MOTION, 50),  ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 293 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MirrorEssentia"), new InfusionRecipe("MIRRORESSENTIA", new ItemStack(BlocksTC.mirrorEssentia), 2, new AspectList().add(Aspect.MOTION, 25).add(Aspec ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 297 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalAxe"), new InfusionRecipe("ELEMENTALTOOLS", isEA, 1, new AspectList().add(Aspect.WATER, 60).add(Aspect.PLANT, 30), new ItemStack(ItemsTC.t ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 301 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalPick"), new InfusionRecipe("ELEMENTALTOOLS", isEP, 1, new AspectList().add(Aspect.FIRE, 30).add(Aspect.METAL, 30).add(Aspect.SENSES, 30),  ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 304 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalSword"), new InfusionRecipe("ELEMENTALTOOLS", isESW, 1, new AspectList().add(Aspect.AIR, 30).add(Aspect.MOTION, 30).add(Aspect.AVERSION, 3 ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 307 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalShovel"), new InfusionRecipe("ELEMENTALTOOLS", isES, 1, new AspectList().add(Aspect.EARTH, 60).add(Aspect.CRAFT, 30), new ItemStack(ItemsT ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 308 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalHoe"), new InfusionRecipe("ELEMENTALTOOLS", new ItemStack(ItemsTC.elementalHoe), 1, new AspectList().add(Aspect.ORDER, 30).add(Aspect.PLAN ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 310 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEBURROWING"), IEBURROWING); |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 313 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IECOLLECTOR"), IECOLLECTOR); |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 316 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEDESTRUCTIVE"), IEDESTRUCTIVE); |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 319 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEREFINING"), IEREFINING); |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 322 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IESOUNDING"), IESOUNDING); |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 325 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEARCING"), IEARCING); |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 328 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IEESSENCE"), IEESSENCE); |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 331 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:IELAMPLIGHT"), IELAMPLIGHT); |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 333 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:BootsTraveller"), new InfusionRecipe("BOOTSTRAVELLER", new ItemStack(ItemsTC.travellerBoots), 1, new AspectList().add(Aspect.FLIGHT, 100).add(Aspec ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 334 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MindBiothaumic"), new InfusionRecipe("MINDBIOTHAUMIC", new ItemStack(ItemsTC.mind, 1, 1), 4, new AspectList().add(Aspect.MIND, 50).add(Aspect.MECHA ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 335 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ArcaneBore"), new InfusionRecipe("ARCANEBORE", new ItemStack(ItemsTC.turretPlacer, 1, 2), 4, new AspectList().add(Aspect.ENERGY, 25).add(Aspect.EAR ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 336 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:LampGrowth"), new InfusionRecipe("LAMPGROWTH", new ItemStack(BlocksTC.lampGrowth), 4, new AspectList().add(Aspect.PLANT, 20).add(Aspect.LIGHT, 15). ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 337 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:LampFertility"), new InfusionRecipe("LAMPFERTILITY", new ItemStack(BlocksTC.lampFertility), 4, new AspectList().add(Aspect.BEAST, 20).add(Aspect.LI ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 338 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ThaumiumFortressHelm"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressHelm), 3, new AspectList().add(Aspect.METAL, 50).add(Aspe ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 339 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ThaumiumFortressChest"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressChest), 3, new AspectList().add(Aspect.METAL, 50).add(As ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 340 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ThaumiumFortressLegs"), new InfusionRecipe("ARMORFORTRESS", new ItemStack(ItemsTC.fortressLegs), 3, new AspectList().add(Aspect.METAL, 50).add(Aspe ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 341 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeHelm"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeHelm), 6, new AspectList().add(Aspect.METAL, 25).add(Aspect.SENSE ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 342 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeChest"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeChest), 6, new AspectList().add(Aspect.METAL, 35).add(Aspect.PRO ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 343 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeLegs"), new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeLegs), 6, new AspectList().add(Aspect.METAL, 30).add(Aspect.PROTE ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 344 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:HelmGoggles"), new InfusionRecipe("FORTRESSMASK", new Object[] { "goggles", new NBTTagByte((byte)1) }, 5, new AspectList().add(Aspect.SENSES, 40).a ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 345 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskGrinningDevil"), new InfusionRecipe("FORTRESSMASK", new Object[] { "mask", new NBTTagInt(0) }, 8, new AspectList().add(Aspect.MIND, 80).add(Asp ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 346 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskAngryGhost"), new InfusionRecipe("FORTRESSMASK", new Object[] { "mask", new NBTTagInt(1) }, 8, new AspectList().add(Aspect.ENTROPY, 80).add(Asp ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 347 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskSippingFiend"), new InfusionRecipe("FORTRESSMASK", new Object[] { "mask", new NBTTagInt(2) }, 8, new AspectList().add(Aspect.UNDEAD, 80).add(As ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 351 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:PrimalCrusher"), new InfusionRecipe("PRIMALCRUSHER", isPC, 6, new AspectList().add(Aspect.EARTH, 75).add(Aspect.TOOL, 75).add(Aspect.ENTROPY, 50).a ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 352 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeart"), new InfusionRecipe("VERDANTCHARMS", new ItemStack(ItemsTC.charmVerdant), 5, new AspectList().add(Aspect.LIFE, 60).add(Aspect.ORDER, ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 355 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeartLife"), new InfusionRecipe("VERDANTCHARMS", new Object[] { "type", new NBTTagByte((byte)1) }, 5, new AspectList().add(Aspect.LIFE, 80). ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 358 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeartSustain"), new InfusionRecipe("VERDANTCHARMS", new Object[] { "type", new NBTTagByte((byte)2) }, 5, new AspectList().add(Aspect.DESIRE, ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 359 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CLOUDRING"), new InfusionRecipe("CLOUDRING", new ItemStack(ItemsTC.ringCloud), 1, new AspectList().add(Aspect.AIR, 50), new ItemStack(ItemsTC.baubl ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 360 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CuriosityBand"), new InfusionRecipe("CURIOSITYBAND", new ItemStack(ItemsTC.bandCuriosity), 5, new AspectList().add(Aspect.MIND, 150).add(Aspect.VOI ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 361 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CHARMUNDYING"), new InfusionRecipe("CHARMUNDYING", new ItemStack(ItemsTC.charmUndying), 2, new AspectList().add(Aspect.LIFE, 25), new ItemStack(Ite ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 368 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:CausalityCollapser"), new InfusionRecipe("RIFTCLOSER", new ItemStack(ItemsTC.causalityCollapser), 8, new AspectList().add(Aspect.ELDRITCH, 50).add( ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 369 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidSiphon"), new InfusionRecipe("VOIDSIPHON", new ItemStack(BlocksTC.voidSiphon), 7, new AspectList().add(Aspect.ELDRITCH, 50).add(Aspect.ENTROPY, ... |
| addInfusionCraftingRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 370 | ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidseerPearl"), new InfusionRecipe("VOIDSEERPEARL", new ItemStack(ItemsTC.charmVoidseer), 8, new AspectList().add(Aspect.MIND, 150).add(Aspect.VOI ... |
| ConfigRecipes | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 73 | import thaumcraft.common.config.ConfigRecipes; |
| ConfigRecipes | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 816 | recipe = ConfigRecipes.recipeGroups.get(rk.toString()); |
| ConfigRecipes | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 2017 | recipe = ConfigRecipes.recipeGroups.get(rk.toString()); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 65 | public class ConfigRecipes |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 226 | shapelessOreDictRecipe("ArcaneEarToggle", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.arcaneEarToggle), new Object[] { new ItemStack(BlocksTC.arcaneEar), new ItemStack(Blocks.LEVER) }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 380 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "ironnuggetconvert"), ConfigRecipes.defaultGroup, new ItemStack(Items.IRON_NUGGET), "#", '#', new ItemStack(ItemsTC.nuggets, 1, 0)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 381 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "thaumiumtonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 6), "#", '#', new ItemStack(ItemsTC.ingots, 1, 0)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 382 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "voidtonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 7), "#", '#', new ItemStack(ItemsTC.ingots, 1, 1)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 383 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "brasstonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 8), "#", '#', new ItemStack(ItemsTC.ingots, 1, 2)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 384 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "quartztonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 9), "#", '#', new ItemStack(Items.QUARTZ)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 385 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "quicksilvertonuggets"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.nuggets, 9, 5), "#", '#', new ItemStack(ItemsTC.quicksilver)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 386 | oreDictRecipe("nuggetstothaumium", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.ingots, 1, 0), new Object[] { "###", "###", "###", '#', "nuggetThaumium" }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 387 | oreDictRecipe("nuggetstovoid", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.ingots, 1, 1), new Object[] { "###", "###", "###", '#', "nuggetVoid" }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 388 | oreDictRecipe("nuggetstobrass", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.ingots, 1, 2), new Object[] { "###", "###", "###", '#', "nuggetBrass" }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 389 | oreDictRecipe("nuggetstoquicksilver", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.quicksilver), new Object[] { "###", "###", "###", '#', "nuggetQuicksilver" }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 396 | oreDictRecipe("fleshtoblock", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.fleshBlock), new Object[] { "###", "###", "###", '#', Items.ROTTEN_FLESH }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 397 | oreDictRecipe("blocktoflesh", ConfigRecipes.defaultGroup, new ItemStack(Items.ROTTEN_FLESH, 9, 0), new Object[] { "#", '#', BlocksTC.fleshBlock }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 398 | oreDictRecipe("ambertoblock", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.amberBlock), new Object[] { "##", "##", '#', "gemAmber" }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 399 | oreDictRecipe("amberblocktobrick", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.amberBrick, 4), new Object[] { "##", "##", '#', new ItemStack(BlocksTC.amberBlock) }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 400 | oreDictRecipe("amberbricktoblock", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.amberBlock, 4), new Object[] { "##", "##", '#', new ItemStack(BlocksTC.amberBrick) }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 401 | oreDictRecipe("amberblocktoamber", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.amber, 4), new Object[] { "#", '#', new ItemStack(BlocksTC.amberBlock) }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 402 | oreDictRecipe("ironplate", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.plate, 3, 1), new Object[] { "BBB", 'B', "ingotIron" }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 431 | ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:triplemeattreatfake"), new ShapelessOreRecipe(ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.tripleMeatTreat), "nuggetMeat", "nuggetMeat", "nuggetMe ... |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 433 | ThaumcraftApi.addFakeCraftingRecipe(new ResourceLocation("thaumcraft:salismundusfake"), new ShapelessOreRecipe(ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.salisMundus), Items.FLINT, Items.BOWL, Items.REDSTONE, new  ... |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 434 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "shimmerleaftoquicksilver"), ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.quicksilver), "#", '#', BlocksTC.shimmerleaf); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 435 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "cinderpearltoblazepowder"), ConfigRecipes.defaultGroup, new ItemStack(Items.BLAZE_POWDER), "#", '#', BlocksTC.cinderpearl); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 445 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "PlankGreatwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.plankGreatwood, 4), "W", 'W', new ItemStack(BlocksTC.logGreatwood)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 446 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "PlankSilverwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.plankSilverwood, 4), "W", 'W', new ItemStack(BlocksTC.logSilverwood)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 447 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsGreatwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsGreatwood, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.plankGreatwo ... |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 448 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsSilverwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsSilverwood, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.plankSilve ... |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 449 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsArcane"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsArcane, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.stoneArcane)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 450 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsArcaneBrick"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsArcaneBrick, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.stoneArc ... |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 451 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "StairsAncient"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stairsAncient, 4, 0), "K  ", "KK ", "KKK", 'K', new ItemStack(BlocksTC.stoneAncient)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 452 | oreDictRecipe("StoneArcane", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stoneArcane, 9), new Object[] { "KKK", "KCK", "KKK", 'K', "stone", 'C', new ItemStack(ItemsTC.crystalEssence) }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 453 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "BrickArcane"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.stoneArcaneBrick, 4), "KK", "KK", 'K', new ItemStack(BlocksTC.stoneArcane)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 454 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabGreatwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabGreatwood, 6), "KKK", 'K', new ItemStack(BlocksTC.plankGreatwood)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 455 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabSilverwood"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabSilverwood, 6), "KKK", 'K', new ItemStack(BlocksTC.plankSilverwood)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 456 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabArcaneStone"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabArcaneStone, 6), "KKK", 'K', new ItemStack(BlocksTC.stoneArcane)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 457 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabArcaneBrick"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabArcaneBrick, 6), "KKK", 'K', new ItemStack(BlocksTC.stoneArcaneBrick)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 458 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabAncient"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabAncient, 6), "KKK", 'K', new ItemStack(BlocksTC.stoneAncient)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 459 | GameRegistry.addShapedRecipe(new ResourceLocation("thaumcraft", "SlabEldritch"), ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.slabEldritch, 6), "KKK", 'K', new ItemStack(BlocksTC.stoneEldritchTile)); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 460 | oreDictRecipe("phial", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.phial, 8, 0), new Object[] { " C ", "G G", " G ", 'G', "blockGlass", 'C', Items.CLAY_BALL }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 461 | oreDictRecipe("tablewood", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.tableWood), new Object[] { "SSS", "W W", 'S', "slabWood", 'W', "plankWood" }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 462 | oreDictRecipe("tablestone", ConfigRecipes.defaultGroup, new ItemStack(BlocksTC.tableStone), new Object[] { "SSS", "W W", 'S', new ItemStack(Blocks.STONE_SLAB), 'W', "stone" }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 467 | oreDictRecipe("GolemBell", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.golemBell), new Object[] { " QQ", " QQ", "S  ", 'S', "stickWood", 'Q', "gemQuartz" }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 476 | oreDictRecipe("BrassBrace", ConfigRecipes.defaultGroup, new ItemStack(ItemsTC.jarBrace, 2), new Object[] { "NSN", "S S", "NSN", 'N', "nuggetBrass", 'S', "stickWood" }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 570 | if (!ConfigRecipes.recipeGroups.containsKey(group)) { |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 571 | ConfigRecipes.recipeGroups.put(group, new ArrayList<ResourceLocation>()); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 573 | ArrayList list = ConfigRecipes.recipeGroups.get(group); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 587 | if (!ConfigRecipes.recipeGroups.containsKey(group)) { |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 588 | ConfigRecipes.recipeGroups.put(group, new ArrayList<ResourceLocation>()); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 590 | ArrayList list = ConfigRecipes.recipeGroups.get(group); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 610 | if (!ConfigRecipes.recipeGroups.containsKey(group)) { |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 611 | ConfigRecipes.recipeGroups.put(group, new ArrayList<ResourceLocation>()); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 613 | ArrayList list = ConfigRecipes.recipeGroups.get(group); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 620 | ConfigRecipes.defaultGroup = new ResourceLocation(""); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 621 | ConfigRecipes.recipeGroups = new HashMap<String, ArrayList<ResourceLocation>>(); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ModConfig.java | 154 | ConfigRecipes.oreDictRecipe("coppernuggetstoingot", defaultGroup, is2, new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.nuggets, 1, 1) }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ModConfig.java | 175 | ConfigRecipes.oreDictRecipe("tinnuggetstoingot", defaultGroup, is2, new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.nuggets, 1, 2) }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ModConfig.java | 196 | ConfigRecipes.oreDictRecipe("silvernuggetstoingot", defaultGroup, is2, new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.nuggets, 1, 3) }); |
| ConfigRecipes | src/main/java/thaumcraft/common/config/ModConfig.java | 217 | ConfigRecipes.oreDictRecipe("leadnuggetstoingot", defaultGroup, is2, new Object[] { "###", "###", "###", '#', new ItemStack(ItemsTC.nuggets, 1, 4) }); |
| ConfigRecipes | src/main/java/thaumcraft/proxies/CommonProxy.java | 29 | import thaumcraft.common.config.ConfigRecipes; |
| ConfigRecipes | src/main/java/thaumcraft/proxies/CommonProxy.java | 73 | ConfigRecipes.initializeSmelting(); |
| ConfigRecipes | src/main/java/thaumcraft/proxies/CommonProxy.java | 79 | ConfigRecipes.postAspects(); |
| ConfigRecipes | src/main/java/thaumcraft/proxies/CommonProxy.java | 82 | ConfigRecipes.compileGroups(); |
| ConfigRecipes | src/main/java/thaumcraft/Registrar.java | 23 | import thaumcraft.common.config.ConfigRecipes; |
| ConfigRecipes | src/main/java/thaumcraft/Registrar.java | 77 | ConfigRecipes.initializeNormalRecipes(event.getRegistry()); |
| ConfigRecipes | src/main/java/thaumcraft/Registrar.java | 78 | ConfigRecipes.initializeArcaneRecipes(event.getRegistry()); |
| ConfigRecipes | src/main/java/thaumcraft/Registrar.java | 80 | ConfigRecipes.initializeAlchemyRecipes(); |
| ConfigRecipes | src/main/java/thaumcraft/Registrar.java | 81 | ConfigRecipes.initializeCompoundRecipes(); |
| CrucibleRecipe | src/main/java/thaumcraft/api/crafting/CrucibleRecipe.java | 10 | public class CrucibleRecipe implements IThaumcraftRecipe  { |
| CrucibleRecipe | src/main/java/thaumcraft/api/crafting/CrucibleRecipe.java | 20 | public CrucibleRecipe(String researchKey, ItemStack result, Object catalyst, AspectList tags) { |
| CrucibleRecipe | src/main/java/thaumcraft/api/crafting/CrucibleRecipe.java | 105 | public CrucibleRecipe setGroup(ResourceLocation s) { |
| CrucibleRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 11 | import thaumcraft.api.crafting.CrucibleRecipe; |
| CrucibleRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 227 | public static CrucibleRecipe getCrucibleRecipe(ItemStack stack) { |
| CrucibleRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 229 | if (r instanceof CrucibleRecipe) { |
| CrucibleRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 230 | if (((CrucibleRecipe)r).getRecipeOutput().isItemEqual(stack)) |
| CrucibleRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 231 | return ((CrucibleRecipe)r); |
| CrucibleRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 241 | public static CrucibleRecipe getCrucibleRecipeFromHash(int hash) { |
| CrucibleRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 243 | if (recipe instanceof CrucibleRecipe && ((CrucibleRecipe)recipe).hash==hash) |
| CrucibleRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 244 | return (CrucibleRecipe)recipe; |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java | 41 | import thaumcraft.api.crafting.CrucibleRecipe; |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java | 378 | else if (recipeObject instanceof CrucibleRecipe) { |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java | 379 | ro = ((CrucibleRecipe)recipeObject).getRecipeOutput(); |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 845 | else if (recipe instanceof CrucibleRecipe) { |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 846 | CrucibleRecipe re = (CrucibleRecipe)recipe; |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 935 | else if (recipe instanceof CrucibleRecipe) { |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 936 | drawCruciblePage(x + 128, y + 128, mx, my, (CrucibleRecipe)recipe); |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 1288 | private void drawCruciblePage(int x, int y, int mx, int my, CrucibleRecipe rc) { |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 2032 | if (recipe instanceof CrucibleRecipe && ((CrucibleRecipe)recipe).getRecipeOutput().isItemEqual(stack)) { |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 2033 | if (!ThaumcraftCapabilities.knowsResearchStrict(mc.player, ((CrucibleRecipe)recipe).getResearch())) { |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 20 | import thaumcraft.api.crafting.CrucibleRecipe; |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 40 | static HashMap<Integer, CrucibleRecipe> recipeCache; |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 109 | private static CrucibleRecipe getRecipeCached(int hash) { |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 113 | CrucibleRecipe cr = ThaumcraftApi.getCrucibleRecipeFromHash(hash); |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 130 | CrucibleRecipe cr = getRecipeCached(hash); |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 177 | CrucibleRecipe cr = getRecipeCached(hash); |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 198 | CrucibleRecipe cr = getRecipeCached(hash); |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 221 | private void drawOutputIcon(int x, int y, CrucibleRecipe cr, long time) { |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 261 | CrucibleRecipe cr = getRecipeCached(hash); |
| CrucibleRecipe | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 312 | GuiThaumatorium.recipeCache = new HashMap<Integer, CrucibleRecipe>(); |
| CrucibleRecipe | src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java | 14 | import thaumcraft.api.crafting.CrucibleRecipe; |
| CrucibleRecipe | src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java | 32 | CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(tile.recipeHash.get(stack)); |
| CrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 38 | import thaumcraft.api.crafting.CrucibleRecipe; |
| CrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 129 | CrucibleRecipe[] cre = new CrucibleRecipe[Aspect.aspects.size()]; |
| CrucibleRecipe | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 147 | ArrayList<CrucibleRecipe> rl = new ArrayList<CrucibleRecipe>(); |
| CrucibleRecipe | src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java | 43 | import thaumcraft.api.crafting.CrucibleRecipe; |
| CrucibleRecipe | src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java | 54 | public static CrucibleRecipe findMatchingCrucibleRecipe(EntityPlayer player, AspectList aspects, ItemStack lastDrop) { |
| CrucibleRecipe | src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java | 56 | CrucibleRecipe out = null; |
| CrucibleRecipe | src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java | 58 | if (re != null && re instanceof CrucibleRecipe) { |
| CrucibleRecipe | src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java | 59 | CrucibleRecipe recipe = (CrucibleRecipe)re; |
| CrucibleRecipe | src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java | 435 | private static AspectList generateTagsFromCrucibleRecipes(ItemStack stack, ArrayList<String> history) { |
| CrucibleRecipe | src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java | 436 | CrucibleRecipe cr = ThaumcraftApi.getCrucibleRecipe(stack); |
| CrucibleRecipe | src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java | 596 | ret = generateTagsFromCrucibleRecipes(stack, history); |
| CrucibleRecipe | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 13 | import thaumcraft.api.crafting.CrucibleRecipe; |
| CrucibleRecipe | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 66 | for (CrucibleRecipe cr : thaumatorium.recipes) { |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileCrucible.java | 33 | import thaumcraft.api.crafting.CrucibleRecipe; |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileCrucible.java | 178 | CrucibleRecipe rc = ThaumcraftCraftingManager.findMatchingCrucibleRecipe(player, aspects, item); |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 39 | import thaumcraft.api.crafting.CrucibleRecipe; |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 61 | CrucibleRecipe currentRecipe; |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 63 | public ArrayList<CrucibleRecipe> recipes; |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 78 | recipes = new ArrayList<CrucibleRecipe>(); |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 95 | CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(hash); |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 154 | CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(recipeHash.get(currentCraft)); |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 177 | CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(recipeHash.get(a)); |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 446 | ArrayList<CrucibleRecipe> recipesTemp = new ArrayList<CrucibleRecipe>(); |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 449 | if (r instanceof CrucibleRecipe) { |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 450 | CrucibleRecipe creps = (CrucibleRecipe)r; |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 475 | for (CrucibleRecipe cr : recipes) { |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 482 | for (CrucibleRecipe cr2 : recipes) { |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 488 | private class RecipeOutputComparator implements Comparator<CrucibleRecipe> |
| CrucibleRecipe | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 494 | public int compare(CrucibleRecipe a, CrucibleRecipe b) { |
| InfusionRecipe | src/main/java/thaumcraft/api/crafting/InfusionRecipe.java | 16 | public class InfusionRecipe implements IThaumcraftRecipe |
| InfusionRecipe | src/main/java/thaumcraft/api/crafting/InfusionRecipe.java | 26 | public InfusionRecipe(String research, Object outputResult, int inst, AspectList aspects2, Object centralItem, Object ... recipe) { |
| InfusionRecipe | src/main/java/thaumcraft/api/crafting/InfusionRecipe.java | 106 | public InfusionRecipe setGroup(ResourceLocation s) { |
| InfusionRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 14 | import thaumcraft.api.crafting.InfusionRecipe; |
| InfusionRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 201 | public static InfusionRecipe getInfusionRecipe(ItemStack res) { |
| InfusionRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 203 | if (r instanceof InfusionRecipe) { |
| InfusionRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 204 | if (((InfusionRecipe)r).getRecipeOutput() instanceof ItemStack) { |
| InfusionRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 205 | if (((ItemStack) ((InfusionRecipe)r).getRecipeOutput()).isItemEqual(res)) |
| InfusionRecipe | src/main/java/thaumcraft/api/ThaumcraftApi.java | 206 | return ((InfusionRecipe)r); |
| InfusionRecipe | src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java | 42 | import thaumcraft.api.crafting.InfusionRecipe; |
| InfusionRecipe | src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java | 375 | else if (recipeObject instanceof InfusionRecipe && ((InfusionRecipe)recipeObject).getRecipeOutput() instanceof ItemStack) { |
| InfusionRecipe | src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java | 376 | ro = (ItemStack)((InfusionRecipe)recipeObject).getRecipeOutput(); |
| InfusionRecipe | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 853 | else if (recipe instanceof InfusionRecipe) { |
| InfusionRecipe | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 854 | InfusionRecipe re2 = (InfusionRecipe)recipe; |
| InfusionRecipe | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 938 | else if (recipe instanceof InfusionRecipe) { |
| InfusionRecipe | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 939 | drawInfusionPage(x + 128, y + 128, mx, my, (InfusionRecipe)recipe); |
| InfusionRecipe | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 1345 | private void drawInfusionPage(int x, int y, int mx, int my, InfusionRecipe ri) { |
| InfusionRecipe | src/main/java/thaumcraft/client/gui/GuiResearchPage.java | 2038 | else if (recipe instanceof InfusionRecipe && ((InfusionRecipe)recipe).getRecipeOutput() instanceof ItemStack && ((ItemStack)((InfusionRecipe)recipe).getRecipeOutput()).isItemEqual(stack)) { |

API hit table truncated to first 250 hits out of 361.

## References without direct legacy source hit

All alchemy page references had at least one direct legacy source hit.

## Next implementation guidance

1. The next large safe slice should be chosen from the highest-count alchemy family with direct legacy source hits.
2. Implement only recipe data model, serializer, loader audit, and Thaumonomicon page snapshot for the first selected family.
3. Do not implement crucible block behavior, essentia handling, alchemy machines, particles, or item transformations in the same slice.
4. Preserve legacy recipe IDs and research page references first; behavior can follow only after page/data parity is testable.
