# Legacy Alchemy Recipe Source Audit

Generated: 2026-06-18 17:15:23 +03:00

## Summary

| Metric | Count |
|---|---:|
| Alchemy missing recipe-page references from current audit | 3 |
| References with at least one legacy source hit | 3 |
| References without direct source hit | 0 |
| Legacy Java files scanned | 902 |
| Legacy API/pattern source hits | 361 |

## Alchemy reference family distribution

| Family | Count |
|---|---:|
| ALCHEMY_OTHER | 3 |

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

| Family | Reference | Legacy hits | Research path |
|---|---|---:|---|
| ALCHEMY_OTHER | thaumcraft:EverfullUrn | 35 | $.entries[20].stages[1].recipes[0] |
| ALCHEMY_OTHER | thaumcraft:JarLabelEssence | 1 | $.entries[11].stages[0].recipes[4] |
| ALCHEMY_OTHER | thaumcraft:Thaumatorium | 186 | $.entries[18].stages[1].recipes[0] |

## Representative legacy source hits

| Reference | Family | File | Line | Snippet |
|---|---|---|---:|---|
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/java/thaumcraft/api/blocks/BlocksTC.java | 153 | public static Block everfullUrn; |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/java/thaumcraft/common/config/ConfigBlocks.java | 329 | BlocksTC.everfullUrn = registerBlock(new BlockWaterJug()); |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 180 | ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:EverfullUrn"), new CrucibleRecipe("EVERFULLURN", new ItemStack(BlocksTC.everfullUrn), new ItemStack(Items.FLOWER_POT), new AspectList().add(Aspect.WATER, 3 ... |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/de_de.lang | 1330 | research.EVERFULLURN.title=Everfull Urn |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/de_de.lang | 1331 | research.EVERFULLURN.stage.1=Alchemical creation has proven to be much easier than I expected. If I can somehow automate this process to create something simple and practical it will go a long way toward further alchemic ... |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/de_de.lang | 1332 | research.EVERFULLURN.stage.2=A small fountain of pure water always flows from the top of this urn, making it a perfect water source to fill my buckets, bottles or other liquid containers.<BR>The urn will also automatical ... |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/en_us.lang | 1389 | research.EVERFULLURN.title=Everfull Urn |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/en_us.lang | 1390 | research.EVERFULLURN.stage.1=Alchemical creation has proven to be much easier than I expected. If I can somehow automate this process to create something simple and practical it will go a long way toward further alchemic ... |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/en_us.lang | 1391 | research.EVERFULLURN.stage.2=A small fountain of pure water always flows from the top of this urn, making it a perfect water source to fill my buckets, bottles or other liquid containers.<BR>The urn will also automatical ... |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/fr_fr.lang | 1384 | research.EVERFULLURN.title=Everfull urn |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/fr_fr.lang | 1385 | research.EVERFULLURN.stage.1=Alchemical creation has proven to be much easier than i expected. if i can somehow automate this process to create something simple and practical it will go a long way toward further alchemic ... |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/fr_fr.lang | 1386 | research.EVERFULLURN.stage.2=A small fountain of pure water always flows from the top of this urn, making it a perfect water source to fill my buckets, bottles or other liquid containers.<br>the urn will also automatical ... |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ja_jp.lang | 1383 | research.EVERFULLURN.title=底なしの壺 |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ja_jp.lang | 1384 | research.EVERFULLURN.stage.1=錬金術のクラフトは予想よりもとても簡単だった。この単純で実用的なものを作る工程をどうにかして自動化できれば、更なる錬金術のブレイクスルーへの道が大きく開けるだろう。<BR>無限水源のようなものがスタートとしてちょうどよいだろうか？ |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ja_jp.lang | 1385 | research.EVERFULLURN.stage.2=純水の小さな湧き水がこの壺の口から常に流れ続け、バケツやボトルなどの液体容器を満たす完璧な水源となり得る。<BR>またこの壺は2ブロック以内の任意の液体容器に、ブロック上面から水が搬入できるものがあれば、それへと自動的に水を補給する。これはるつぼの給水を自動化するのに完璧だが、他の用途にも使える。<BR>より俗っぽく、液体パイプを壺の上面に取り付けることもできる。<BR>バケツ一 ... |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ko_kr.lang | 1384 | research.EVERFULLURN.title=영원한 항아리 |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ko_kr.lang | 1385 | research.EVERFULLURN.stage.1=연금술적인 창작은 기대했던 것보단 훨씬 쉽다는 것이 입증되었습니다. 어떻게든 이 과정을 자동화해서 간단하고 실용적인 무언가를 만들 수 있다면, 더 많은 연금술적인 돌파구를 향해 갈 것입니다.<BR>무한한 물의 원천 같은 것이 좋은 시작이 되겠죠? |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ko_kr.lang | 1386 | research.EVERFULLURN.stage.2=이 깨끗한 물이 담긴 작은 분수는 항상 이 항아리의 꼭대기에서 흘러 나와 양동이나 병 또는 다른 액체 용기를 채울 수 있습니다.<BR>만약 윗부분으로 물을 받아들일 수 있는 장치가 있다면 2블럭 안에 있는 어떤 액체 용기든간에 물을 채울 것입니다. 이 기능은 도가니를 자동으로 채우는 데 적합하지만 다른 용도도 있습니다.<BR>항아리 위로 액 ... |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/nl_NL.lang | 1411 | research.EVERFULLURN.title=Everfull Urn |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/nl_NL.lang | 1412 | research.EVERFULLURN.stage.1=Alchemical creation has proven to be much easier than I expected. If I can somehow automate this process to create something simple and practical it will go a long way toward further alchemic ... |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/nl_NL.lang | 1413 | research.EVERFULLURN.stage.2=A small fountain of pure water always flows from the top of this urn, making it a perfect water source to fill my buckets, bottles or other liquid containers.<BR>The urn will also automatical ... |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ru_ru.lang | 1379 | research.EVERFULLURN.title=Неиссякающая урна |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ru_ru.lang | 1380 | research.EVERFULLURN.stage.1=Алхимия оказалась намного проще, чем я думал. А если я еще смогу и автоматизировать работу с ней, создав что-то простое и практичное, то в конечном итоге мне это очень поможет в алхимических  ... |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ru_ru.lang | 1381 | research.EVERFULLURN.stage.2=Небольшой фонтан чистой воды постоянно льётся из горлышка этой урны, делая её идеальным источником воды для моих вёдер, бутылок или других контейнеров жидкости.<BR>Урна также автоматически по ... |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_cn.lang | 1383 | research.EVERFULLURN.title=无尽之瓮 |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_cn.lang | 1384 | research.EVERFULLURN.stage.1=炼金术的创造事实上比我预想的容易的多。如果我能以某种方式自动化地创造一些简单而实用的东西，那么这将是炼金术的进一步突破。 <BR>或许无限水源是一个良好的开始？ |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_cn.lang | 1385 | research.EVERFULLURN.stage.2=从瓮口不断涌出的一小股清泉让无尽之瓮能够用来为水桶，药瓶和其他流体容器提供水源。 <BR>它还能够为两格范围内的流体容器自动添水，只要这些容器能够从顶部注水。配合坩埚可以很完美地实现自动化，当然别的方面也有诸多妙用。 <BR>流体管道可以接在瓮的顶部以便作为其它更凡庸的方法使用。 <BR>每制造一桶水都会从灵气中抽取1点vis。 <BR>水的味道有点……奇怪。我确定无需担心。 |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_tw.lang | 1383 | research.EVERFULLURN.title=無盡之甕 |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_tw.lang | 1384 | research.EVERFULLURN.stage.1=煉金製造已被證實遠比我所預期的還要容易。如果能找到方法將其過程自動化以生產一些簡單而實用的物質，相信會是煉金術的一大突破。 <BR>或許類似無限水源之類的東西會是個不錯的開始？ |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_tw.lang | 1385 | research.EVERFULLURN.stage.2=細小的純水噴泉會不斷從甕中流出，成為我裝填水桶、瓶子或其他容器的完美水源。 <BR>若兩格內有任何盛水的容器，這個甕也會自動將之注滿。除了可以完美地自動填滿坩堝外，當然還有其他實用的功能。 <BR>可以在甕的上方接上流體管道以利於進行更多凡世的用途。 <BR>每生成一桶水都會直接從靈氣中汲取1點魔素。 <BR>這水嘗起來有些...異味。我確定這沒什麼好擔心的。 |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/research/alchemy.json | 411 | "key": "EVERFULLURN", |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/research/alchemy.json | 412 | "name": "research.EVERFULLURN.title", |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/research/alchemy.json | 418 | "text": "research.EVERFULLURN.stage.1", |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/research/alchemy.json | 422 | "text": "research.EVERFULLURN.stage.2", |
| thaumcraft:EverfullUrn | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/research/alchemy.json | 423 | "recipes": ["thaumcraft:EverfullUrn"] |
| thaumcraft:JarLabelEssence | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/research/alchemy.json | 258 | "recipes": ["thaumcraft:WardedJar","thaumcraft:JarVoid","thaumcraft:BrassBrace","thaumcraft:JarLabel","thaumcraft:JarLabelEssence"] |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/api/blocks/BlocksTC.java | 146 | public static Block thaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/api/blocks/BlocksTC.java | 147 | public static Block thaumatoriumTop; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 22 | import thaumcraft.common.container.ContainerThaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 26 | import thaumcraft.common.tiles.crafting.TileThaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 30 | public class GuiThaumatorium extends GuiContainer |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 32 | private TileThaumatorium inventory; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 33 | private ContainerThaumatorium container; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 42 | public GuiThaumatorium(InventoryPlayer par1InventoryPlayer, TileThaumatorium par2TileEntityFurnace) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 43 | super(new ContainerThaumatorium(par1InventoryPlayer, par2TileEntityFurnace)); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 48 | tex = new ResourceLocation("thaumcraft", "textures/gui/gui_thaumatorium.png"); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 54 | container = (ContainerThaumatorium) inventorySlots; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 110 | if (GuiThaumatorium.recipeCache.containsKey(hash)) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 111 | return GuiThaumatorium.recipeCache.get(hash); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 115 | GuiThaumatorium.recipeCache.put(hash, cr); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/gui/GuiThaumatorium.java | 312 | GuiThaumatorium.recipeCache = new HashMap<Integer, CrucibleRecipe>(); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java | 16 | import thaumcraft.common.tiles.crafting.TileThaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java | 20 | public class TileThaumatoriumRenderer extends TileEntitySpecialRenderer |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java | 24 | public TileThaumatoriumRenderer() { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java | 28 | public void renderTileEntityAt(TileThaumatorium tile, double par2, double par4, double par6, float par8) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/client/renderers/tile/TileThaumatoriumRenderer.java | 64 | renderTileEntityAt((TileThaumatorium)te, x, y, z, partialTicks); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java | 24 | import thaumcraft.common.tiles.crafting.TileThaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java | 25 | import thaumcraft.common.tiles.crafting.TileThaumatoriumTop; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java | 28 | public class BlockThaumatorium extends BlockTCDevice implements IBlockFacingHorizontal |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java | 32 | public BlockThaumatorium(boolean top) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java | 33 | super(Material.IRON, null, top ? "thaumatorium_top" : "thaumatorium"); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java | 42 | return new TileThaumatorium(); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java | 45 | return new TileThaumatoriumTop(); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java | 93 | if (top && worldIn.getBlockState(pos.down()).getBlock() == BlocksTC.thaumatorium) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java | 96 | if (!top && worldIn.getBlockState(pos.up()).getBlock() == BlocksTC.thaumatoriumTop) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java | 106 | if (worldIn.getBlockState(pos.up()).getBlock() == BlocksTC.thaumatoriumTop) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java | 118 | if (tile != null && tile instanceof TileThaumatorium) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/blocks/devices/BlockBrainBox.java | 58 | if (worldIn.getBlockState(pos.offset(BlockStateUtils.getFacing(state))).getBlock() != BlocksTC.thaumatorium && worldIn.getBlockState(pos.offset(BlockStateUtils.getFacing(state))).getBlock() != BlocksTC.thaumatoriumTop) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/blocks/devices/BlockBrainBox.java | 65 | return (worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock() == BlocksTC.thaumatorium \|\| worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock() == BlocksTC.thaumatoriumTop) && worldIn.getBlockState( ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/config/ConfigBlocks.java | 42 | import thaumcraft.common.blocks.crafting.BlockThaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/config/ConfigBlocks.java | 110 | import thaumcraft.common.tiles.crafting.TileThaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/config/ConfigBlocks.java | 111 | import thaumcraft.common.tiles.crafting.TileThaumatoriumTop; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/config/ConfigBlocks.java | 330 | BlocksTC.thaumatorium = registerBlock(new BlockThaumatorium(false)); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/config/ConfigBlocks.java | 331 | BlocksTC.thaumatoriumTop = registerBlock(new BlockThaumatorium(true)); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/config/ConfigBlocks.java | 399 | GameRegistry.registerTileEntity(TileThaumatorium.class, "thaumcraft:TileThaumatorium"); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/config/ConfigBlocks.java | 400 | GameRegistry.registerTileEntity(TileThaumatoriumTop.class, "thaumcraft:TileThaumatoriumTop"); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 111 | Part TH1 = new Part(BlocksTC.metalAlchemical.getDefaultState(), BlocksTC.thaumatoriumTop).setApplyPlayerFacing(true); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 112 | Part TH2 = new Part(BlocksTC.metalAlchemical.getDefaultState(), BlocksTC.thaumatorium).setApplyPlayerFacing(true); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 115 | IDustTrigger.registerDustTrigger(new DustTriggerMultiblock("THAUMATORIUM", thaumotoriumBlueprint)); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 116 | ThaumcraftApi.addMultiblockRecipeToCatalog(new ResourceLocation("thaumcraft:Thaumatorium"), new ThaumcraftApi.BluePrint("THAUMATORIUM", thaumotoriumBlueprint, new ItemStack(BlocksTC.metalAlchemical, 2), new ItemStack(Blo ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/config/ConfigRecipes.java | 251 | ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:MnemonicMatrix"), new ShapedArcaneRecipe(ConfigRecipes.defaultGroup, "THAUMATORIUM", 50, new AspectList().add(Aspect.EARTH, 1).add(Aspect.ORDER, 1),  ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/container/ContainerThaumatorium.java | 8 | import thaumcraft.common.tiles.crafting.TileThaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/container/ContainerThaumatorium.java | 11 | public class ContainerThaumatorium extends Container |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/container/ContainerThaumatorium.java | 13 | private TileThaumatorium thaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/container/ContainerThaumatorium.java | 16 | public ContainerThaumatorium(InventoryPlayer par1InventoryPlayer, TileThaumatorium tileEntity) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/container/ContainerThaumatorium.java | 19 | thaumatorium = tileEntity; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/container/ContainerThaumatorium.java | 20 | ((ContainerThaumatorium)(thaumatorium.eventHandler = this)).addSlotToContainer(new Slot(tileEntity, 0, 55, 24)); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/container/ContainerThaumatorium.java | 29 | thaumatorium.updateRecipes(player); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/container/ContainerThaumatorium.java | 34 | thaumatorium.updateRecipes(player); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/container/ContainerThaumatorium.java | 39 | if (!thaumatorium.getWorld().isRemote) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/container/ContainerThaumatorium.java | 40 | thaumatorium.eventHandler = null; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/container/ContainerThaumatorium.java | 45 | return thaumatorium.isUsableByPlayer(par1EntityPlayer); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 14 | import thaumcraft.common.tiles.crafting.TileThaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 50 | if (te != null && te instanceof TileThaumatorium) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 51 | TileThaumatorium thaumatorium = (TileThaumatorium)te; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 54 | for (int hash : thaumatorium.recipeHash) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 56 | thaumatorium.recipeEssentia.remove(i); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 57 | thaumatorium.recipePlayer.remove(i); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 58 | thaumatorium.recipeHash.remove(i); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 59 | thaumatorium.currentCraft = -1; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 65 | if (!flag && thaumatorium.recipeHash.size() < thaumatorium.maxRecipes) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 66 | for (CrucibleRecipe cr : thaumatorium.recipes) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 68 | thaumatorium.recipeEssentia.add(cr.getAspects().copy()); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 69 | thaumatorium.recipePlayer.add(player.getName()); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 70 | thaumatorium.recipeHash.add(cr.hash); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 77 | thaumatorium.markDirty(); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/network/misc/PacketSelectThaumotoriumRecipeToServer.java | 78 | thaumatorium.syncTile(false); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/lib/RefGui.java | 8 | public static int THAUMATORIUM = 3; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 49 | public class TileThaumatorium extends TileThaumcraftInventory implements IAspectContainer, IEssentiaTransport, ITickable |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatorium.java | 65 | public TileThaumatorium() { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 16 | public class TileThaumatoriumTop extends TileThaumcraft implements IAspectContainer, IEssentiaTransport, ISidedInventory, ITickable |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 18 | public TileThaumatorium thaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 20 | public TileThaumatoriumTop() { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 21 | thaumatorium = null; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 25 | if (thaumatorium == null) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 27 | if (tile != null && tile instanceof TileThaumatorium) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 28 | thaumatorium = (TileThaumatorium)tile; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 35 | if (thaumatorium == null) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 38 | return thaumatorium.addToContainer(tt, am); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 43 | return thaumatorium != null && thaumatorium.takeFromContainer(tt, am); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 58 | return thaumatorium != null && thaumatorium.doesContainerContainAmount(tt, am); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 63 | if (thaumatorium == null) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 66 | return thaumatorium.containerContains(tt); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 76 | return thaumatorium != null && thaumatorium.isConnectable(face); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 81 | return thaumatorium != null && thaumatorium.canInputFrom(face); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 91 | if (thaumatorium == null) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 94 | thaumatorium.setSuction(aspect, amount); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 99 | if (thaumatorium == null) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 102 | return thaumatorium.getSuctionType(loc); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 107 | if (thaumatorium == null) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 110 | return thaumatorium.getSuctionAmount(loc); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 125 | if (thaumatorium == null) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 128 | return thaumatorium.takeEssentia(aspect, amount, face); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 133 | if (thaumatorium == null) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 136 | return thaumatorium.addEssentia(aspect, amount, face); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 146 | if (thaumatorium == null) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 149 | return thaumatorium.essentia; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 154 | if (thaumatorium == null) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 157 | thaumatorium.setAspects(aspects); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 165 | if (thaumatorium == null) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 168 | return thaumatorium.getStackInSlot(par1); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 172 | if (thaumatorium == null) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 175 | return thaumatorium.decrStackSize(par1, par2); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 179 | if (thaumatorium == null) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 182 | return thaumatorium.removeStackFromSlot(par1); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 186 | if (thaumatorium == null) { |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 189 | thaumatorium.setInventorySlotContents(par1, stack2); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 234 | thaumatorium.clear(); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/common/tiles/crafting/TileThaumatoriumTop.java | 250 | return thaumatorium.isEmpty(); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/proxies/ProxyGUI.java | 23 | import thaumcraft.client.gui.GuiThaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/proxies/ProxyGUI.java | 40 | import thaumcraft.common.container.ContainerThaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/proxies/ProxyGUI.java | 53 | import thaumcraft.common.tiles.crafting.TileThaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/proxies/ProxyGUI.java | 87 | return new GuiThaumatorium(player.inventory, (TileThaumatorium)world.getTileEntity(new BlockPos(x, y, z))); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/proxies/ProxyGUI.java | 156 | return new ContainerThaumatorium(player.inventory, (TileThaumatorium)world.getTileEntity(new BlockPos(x, y, z))); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/proxies/ProxyTESR.java | 21 | import thaumcraft.client.renderers.tile.TileThaumatoriumRenderer; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/proxies/ProxyTESR.java | 33 | import thaumcraft.common.tiles.crafting.TileThaumatorium; |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/java/thaumcraft/proxies/ProxyTESR.java | 68 | registerTESR(TileThaumatorium.class, new TileThaumatoriumRenderer()); |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/blockstates/thaumatorium.json | 4 | "model": "thaumcraft:thaumatorium.obj", |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/blockstates/thaumatorium.json | 5 | "textures": {"#texture": "thaumcraft:blocks/thaumatorium"}, |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/blockstates/thaumatorium.json | 13 | "facing=up":    { "model": "thaumcraft:thaumatorium.obj", "x": 90}, |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/blockstates/thaumatorium.json | 14 | "facing=down":    { "model": "thaumcraft:thaumatorium.obj", "x": 90}, |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/blockstates/thaumatorium.json | 15 | "facing=west":    { "model": "thaumcraft:thaumatorium.obj", "x": 90}, |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/blockstates/thaumatorium.json | 16 | "facing=south":    { "model": "thaumcraft:thaumatorium.obj", "y": 270, "x": 90}, |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/blockstates/thaumatorium.json | 17 | "facing=north":    { "model": "thaumcraft:thaumatorium.obj", "y": 90, "x": 90}, |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/blockstates/thaumatorium.json | 18 | "facing=east":    { "model": "thaumcraft:thaumatorium.obj", "y": 180, "x": 90} |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/de_de.lang | 317 | tile.thaumatorium.name=Thaumatorium |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/de_de.lang | 318 | tile.thaumatorium_top.name=Thaumatorium |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/de_de.lang | 1306 | research.THAUMATORIUM.title=Alchemical Automation |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/de_de.lang | 1307 | research.THAUMATORIUM.stage.1=Sometimes alchemy can be a hit and miss affair - miscounted aspects, degrading essentia and ingredients that require manual labor to mix. I have had enough.<BR>Now that I have raw, liquid es ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/de_de.lang | 1308 | research.THAUMATORIUM.stage.2=I have invented an automated device I have named the Thaumatorium. The Thaumatorium is a marvelous invention that allows a thaumaturge to select which formula to use and what catalysts to ad ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/en_us.lang | 339 | tile.thaumatorium.name=Thaumatorium |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/en_us.lang | 340 | tile.thaumatorium_top.name=Thaumatorium |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/en_us.lang | 1365 | research.THAUMATORIUM.title=Alchemical Automation |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/en_us.lang | 1366 | research.THAUMATORIUM.stage.1=Sometimes alchemy can be a hit and miss affair - miscounted aspects, degrading essentia and ingredients that require manual labor to mix. I have had enough.<BR>Now that I have raw, liquid es ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/en_us.lang | 1367 | research.THAUMATORIUM.stage.2=I have invented an automated device I have named the Thaumatorium. Like many multi part devices this one must be created using Salis Mundus.<BR>The Thaumatorium is a marvelous invention that ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/fr_fr.lang | 338 | tile.thaumatorium.name=Thaumatorium |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/fr_fr.lang | 339 | tile.thaumatorium_top.name=Thaumatorium |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/fr_fr.lang | 1360 | research.THAUMATORIUM.title=Alchemical automation |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/fr_fr.lang | 1361 | research.THAUMATORIUM.stage.1=Sometimes alchemy can be a hit and miss affair - miscounted aspects, degrading essentia and ingredients that require manual labor to mix. i have had enough.<br>now that i have raw, liquid es ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/fr_fr.lang | 1362 | research.THAUMATORIUM.stage.2=I have invented an automated device i have named the thaumatorium. like many multi part devices this one must be created using salis mundus.<br>the thaumatorium is a marvelous invention that ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ja_jp.lang | 338 | tile.thaumatorium.name=ソーマトリウム |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ja_jp.lang | 339 | tile.thaumatorium_top.name=ソーマトリウム |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ja_jp.lang | 1359 | research.THAUMATORIUM.title=錬金術的自動化 |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ja_jp.lang | 1360 | research.THAUMATORIUM.stage.1=時として錬金術は運任せになり得る。相の数え間違い、エッセンシアや素材の分解は手作業による混合では避けて通れない。もうたくさんだ。<BR>原液の液体エッセンシアが扱えるようになった今、錬金術の工程全体を改善すべきであろう。 |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ja_jp.lang | 1361 | research.THAUMATORIUM.stage.2=自動装置を発明し、ソーマトリウムと名付けた。多くの複数ブロック装置と同様に、サリス・ムンドゥスを使って作らねばならない。<br>ソーマトリウムは素晴らしい発明で魔導師が使いたいレシピと加えるべき触媒を選択することが可能になる。そうするとこれは自動的に配管を通して利用可能な供給源からエッセンシアを吸引する。触媒が供給され続ける限り、これは要求された製品を常に完璧に提供し続ける。< ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ko_kr.lang | 338 | tile.thaumatorium.name=사우마토리움 |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ko_kr.lang | 339 | tile.thaumatorium_top.name=사우마토리움 |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ko_kr.lang | 1360 | research.THAUMATORIUM.title=연금의 자동화 |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ko_kr.lang | 1361 | research.THAUMATORIUM.stage.1=연금술은 때때로 불친절하며, 이는 에센시아의 혼합에서 필요한 성분들을 저하시킵니다. 충분히 겪었죠.<BR>이제 나는 가공되지 않은 액체 에센시아를 가지고 있기 때문에 이 전체적인 연금술 과정을 개선해야 합니다. |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ko_kr.lang | 1362 | research.THAUMATORIUM.stage.2=사우마토리움이라고 이름 붙인 자동화 기기를 발명했습니다. 사우마토리움은 사용할 조합 공식과 촉매제를 선택할 수 있는 엄청난 발명품입니다. 선택하고 나면 자동으로 튜브를 통해 사용 가능한 공급원에서 에센시아를 추출합니다. 촉매가 공급되는 한 필요한 물체를 완벽하게 지속적으로 생성합니다.<BR>더이상 물을 필요로 하지 않지만, 열원은 여전히  ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/nl_NL.lang | 339 | tile.thaumatorium.name=Thaumatorium |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/nl_NL.lang | 340 | tile.thaumatorium_top.name=Thaumatorium |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/nl_NL.lang | 1387 | research.THAUMATORIUM.title=Alchemical Automation |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/nl_NL.lang | 1388 | research.THAUMATORIUM.stage.1=Sometimes alchemy can be a hit and miss affair - miscounted aspects, degrading essentia and ingredients that require manual labor to mix. I have had enough.<BR>Now that I have raw, liquid es ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/nl_NL.lang | 1389 | research.THAUMATORIUM.stage.2=I have invented an automated device I have named the Thaumatorium. Like many multi part devices this one must be created using Salis Mundus.<BR>The Thaumatorium is a marvelous invention that ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ru_ru.lang | 338 | tile.thaumatorium.name=Тауматорий |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ru_ru.lang | 339 | tile.thaumatorium_top.name=Тауматорий |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ru_ru.lang | 1355 | research.THAUMATORIUM.title=Алхимическая автоматизация |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ru_ru.lang | 1356 | research.THAUMATORIUM.stage.1=Временами алхимия преподносит неприятные сюрпризы - то аспекты несовместимые, то эссенция уничтожается, то необходимо вручную смешивать ингредиенты. Всё, с меня хватит.<BR>Теперь, имея эссен ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/ru_ru.lang | 1357 | research.THAUMATORIUM.stage.2=Я изобрёл устройство, которое назвал Тауматориум. Как и для большинства многоблочных структур, для его активации, нужен Salis Mundus.<BR>Тауматориум - это прекрасное изобретение, которое поз ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_cn.lang | 338 | tile.thaumatorium.name=神秘炼金塔 |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_cn.lang | 339 | tile.thaumatorium_top.name=神秘炼金塔 |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_cn.lang | 1359 | research.THAUMATORIUM.title=炼金自动化 |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_cn.lang | 1360 | research.THAUMATORIUM.stage.1=迄今炼金术多少有些变成一件撞运道的差事 - 误算数量的要素，品质低劣的源质和炼金配料全都需要手动劳作去混合。我受够了。 <BR>现在我有了可用的液体源质，我需要改进整个炼金工艺了。 |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_cn.lang | 1361 | research.THAUMATORIUM.stage.2=我发明了一个自动的设备，命名为神秘炼金塔。像大部分多方块结构一样，需要使用世界盐来制造。 <BR>神秘炼金塔是一个奇妙的发明，神秘使可以选择使用哪种配方和添加哪种触媒。然后它就会使用管道自动从可用的来源中抽取源质。只要有足够的触媒它就可以随时生产所需物品。 <BR>组成其基座的坩埚不需要加入水，但是依然需要热源。<LINE>当访问设备时可以看到几个槽位。左上角的槽位是放置触媒的 ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_tw.lang | 338 | tile.thaumatorium.name=秘術煉成機 |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_tw.lang | 339 | tile.thaumatorium_top.name=秘術煉成機 |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_tw.lang | 1359 | research.THAUMATORIUM.title=煉金自動化 |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_tw.lang | 1360 | research.THAUMATORIUM.stage.1=煉金過程總會遇到一些挫折與失誤——算錯要素、源質降解，而且材料需要勞心費神手動混合。 我受夠了。 <BR>現在我有了純粹的液態源質，我應該改善整個煉金過程。 |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/lang/zh_tw.lang | 1361 | research.THAUMATORIUM.stage.2=我發明了一項自動裝置，並命名為秘術煉成機。就像其他複合式裝置一樣需要利用世界鹽來製作。 <BR>秘術煉成機是能讓秘術師選擇煉成公式與觸媒的一項神奇發明。可以透過管道中自動抽取所需的源質。只需提供觸媒，它就能完美地持續合成每項物品。 <BR>底座的的坩堝並不需要添水，但其下方依舊需要提供熱源。<LINE>開啟該設備後會看見一些格子。左上方的格子用來放置觸媒。 放置好後便能在右上方 ... |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/research/alchemy.json | 376 | "key": "THAUMATORIUM", |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/research/alchemy.json | 377 | "name": "research.THAUMATORIUM.title", |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/research/alchemy.json | 378 | "icons": [ "thaumcraft:thaumatorium" ], |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/research/alchemy.json | 384 | "text": "research.THAUMATORIUM.stage.1", |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/research/alchemy.json | 388 | "text": "research.THAUMATORIUM.stage.2", |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/research/alchemy.json | 389 | "recipes": ["thaumcraft:Thaumatorium","thaumcraft:MnemonicMatrix"] |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/assets/thaumcraft/research/alchemy.json | 397 | "parents": [ "THAUMATORIUM", "INFUSION"], |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/changelog.txt | 23 | - thaumatorium should now display all recipes (occasionally a recipe would be lost, like the spiritus vis crystal recipe) |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/changelog.txt | 24 | - thaumatorium recipes are now sorted alphabetically |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/changelog.txt | 69 | - improved thaumatorium GUI |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/changelog.txt | 156 | - fixed thaumatorium crafting that involves the same catalyst being able to craft multiple different results (for example vis crystal recipes) |
| thaumcraft:Thaumatorium | ALCHEMY_OTHER | src/main/resources/changelog.txt | 164 | - fix thaumatorium, infusion crafting and golem builder not working |

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
