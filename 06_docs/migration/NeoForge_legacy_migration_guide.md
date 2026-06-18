**Руководство по портированию legacy-модов Minecraft**  
**Forge 1.7.10 / 1.12.2 → NeoForge 1.21.1+**

Статус документа: рабочий technical guide. Перед началом реального порта нужно зафиксировать точную целевую версию NeoForge: 1.21.1, 1.21.4, 1.21.8, 1.21.11 или актуальную latest-ветку.

Языковая пометка: документ намеренно сохранён на русском, потому что сейчас это рабочий migration reference проекта. Переводить его стоит только если это станет блокером для совместной работы; параллельные расходящиеся переводы не вести.

# 1\. Как пользоваться

Эта версия не пытается быть сухой энциклопедией всех изменений Minecraft. Она устроена как рабочий документ для разработчика, который переносит большой мод и должен принимать архитектурные решения. Разделы сгруппированы по подсистемам мода, а не только по истории Minecraft.

| **Маркер**                | **Значение**                                                                    | **Как использовать**                                                        |
| ------------------------- | ------------------------------------------------------------------------------- | --------------------------------------------------------------------------- |
| Проверено для 1.21.1 docs | Паттерн совпадает с документацией NeoForge 1.21.1.                              | Можно использовать как стартовую точку, но всё равно проверить компиляцией. |
| Концептуально             | Идея верная, но конкретные классы или signatures могут отличаться между 1.21.x. | Использовать как направление, затем сверить с target version.               |
| Legacy only               | Информация нужна только для понимания старого Forge 1.7.10/1.12.2.              | Не переносить в новый код напрямую.                                         |
| Rewrite candidate         | Подсистема обычно требует новой архитектуры.                                    | Не искать механическую замену классов. Сначала спроектировать новый слой.   |

Ключевой принцип: переносить нужно не старые классы, а роль подсистемы. Например, вместо поиска "нового IWorldGenerator" нужно определить, что именно генерировалось: руда, дерево, структура, спавн моба или отдельное измерение.

# 2\. Версионная рамка и источники риска

Целевой диапазон "NeoForge 1.21.1+" удобен для планирования, но опасен для копирования кода. Даже внутри 1.21.x менялись signatures, package names и вспомогательные helper API. Поэтому документ фиксирует паттерны и отдельно отмечает, где требуется проверка target version.

- Для Minecraft 1.21.1+ следует планировать Java 21, а не Java 17.
- Для NeoForge использовать official Mojang mappings как основную базу. Parchment допустим как улучшение читаемости, Yarn использовать только как внешний reference.
- Официальная документация NeoForge 1.21-1.21.1 уже помечена как неактивно поддерживаемая. Для latest-порта нужно открыть docs для конкретной ветки.
- Кодовые примеры в этом документе являются минимальными ориентирами. Перед вставкой в проект нужно сверить imports и signatures с MDK выбранной версии.

# 3\. Gate 0: новый проект до переноса кода

Первая ошибка больших портов: начинать с копирования старого кода. Правильнее сначала получить чистый запускающийся NeoForge-проект. Иначе невозможно понять, что ломает сборку: Gradle, Java, mappings, зависимости или сам legacy-код.

## 3.1 Минимальный результат Gate 0

- Чистый NeoForge MDK / ModDevGradle проект создан под выбранную версию.
- Gradle wrapper и IDE используют Java 21.
- runClient запускается.
- runServer запускается.
- Мод отображается в Mods menu.
- META-INF/neoforge.mods.toml валиден.
- Пустой проект собирается командой build без legacy-кода.

## 3.2 Build migration notes

| **Legacy**               | **Modern target**                        | **Риск**                                                                      |
| ------------------------ | ---------------------------------------- | ----------------------------------------------------------------------------- |
| ForgeGradle 1.x / 2.x    | ModDevGradle / актуальный NeoForge setup | Старый build.gradle почти никогда не переносится напрямую.                    |
| Java 6/7/8               | Java 21                                  | Старый код может конфликтовать с современными language level и зависимостями. |
| mcmod.info               | META-INF/neoforge.mods.toml              | Metadata нужно переписать, а не конвертировать механически.                   |
| старые runtime libraries | explicit dependencies в Gradle           | Мёртвые библиотеки лучше заменить или вынести в compatibility layer.          |

# 4\. Структура проекта и neoforge.mods.toml

В legacy-модах часто смешивались common-код, client-код, регистрация, proxy и контент. В новом проекте желательно сразу задать структуру, которая отделяет API-слои от доменной логики.

## 4.1 Рекомендуемая структура packages

com.examplemod

ExampleMod.java

registry/

ModBlocks.java

ModItems.java

ModBlockEntities.java

ModEntities.java

ModMenus.java

ModCreativeTabs.java

common/

block/

blockentity/

item/

entity/

menu/

network/

data/

client/

ClientSetup.java

screen/

renderer/

model/

particle/

data/

datagen providers

integration/

optional compatibility modules

## 4.2 Metadata

Файл NeoForge metadata должен находиться в resources/META-INF/neoforge.mods.toml. Он не заменяет @Mod entry point, а дополняет его: описывает мод, зависимости, версию загрузчика и отображение в Mods menu.

Не использовать старый mcmod.info. Если в исходном моде были dependencies в mcmod.info, их нужно перенести в dependency entries нового mod file и Gradle dependencies.

# 5\. Mappings: MCP/SRG → Mojang

Механический поиск "нового имени" старого класса часто приводит к ошибкам. В больших портах нужно строить внутреннюю таблицу соответствий по роли, а не только по названию.

| **Старый элемент**   | **Неудачный подход**              | **Правильный вопрос**                                                                       |
| -------------------- | --------------------------------- | ------------------------------------------------------------------------------------------- |
| TileEntity           | Как теперь называется TileEntity? | Как сейчас хранить данные блока, тикать машину, синхронизировать GUI и сохранять состояние? |
| IInventory           | Где новый IInventory?             | Как предоставить доступ к inventory другим модам и automation-системам?                     |
| SimpleNetworkWrapper | Какой новый SimpleNetworkWrapper? | Какие payloads нужны, какие данные идут clientbound/serverbound и кто authoritative?        |
| IWorldGenerator      | Какой новый IWorldGenerator?      | Что именно генерируется и как это выразить через features, biome modifiers или structures?  |
| GuiContainer         | Где новый GuiContainer?           | Какие данные должны жить на server menu, а какие только на client screen?                   |

## 5.1 Методика поиска соответствий

- Найти старый класс и все места его использования.
- Определить роль: storage, rendering, interaction, networking, registration, data-driven content или compatibility.
- Найти современную subsystem по документации NeoForge и vanilla source.
- Проверить, является ли изменение простым переименованием или архитектурной заменой.
- Создать small proof of concept в чистом проекте.
- Только потом переносить доменную логику из legacy-кода.

# 6\. EventBus и lifecycle

В legacy Forge были preInit/init/postInit, proxies и частое использование глобального bus. В NeoForge 1.21.1+ нужно чётко различать mod event bus и game bus.

| **События**                                                                        | **Где регистрировать**                  | **Примеры**                                                        |
| ---------------------------------------------------------------------------------- | --------------------------------------- | ------------------------------------------------------------------ |
| Регистрация объектов, creative tabs, capabilities, payload handlers, datagen hooks | Mod event bus, переданный в constructor | DeferredRegister.register(modBus), modBus.addListener(...)         |
| Runtime gameplay events                                                            | NeoForge.EVENT_BUS                      | server tick, player interaction, entity events                     |
| Client setup и renderer registration                                               | Mod event bus, client-only subscriber   | EntityRenderers.register, MenuScreens.register, particle providers |

@Mod(ExampleMod.MOD_ID)

public final class ExampleMod {

public static final String MOD_ID = "examplemod";

public ExampleMod(IEventBus modBus, ModContainer modContainer) {

ModBlocks.BLOCKS.register(modBus);

ModItems.ITEMS.register(modBus);

ModMenus.MENUS.register(modBus);

modBus.addListener(this::commonSetup);

modBus.addListener(ModCreativeTabs::buildCreativeTabContents);

NeoForge.EVENT_BUS.addListener(this::onServerTick);

modContainer.registerConfig(ModConfig.Type.COMMON, ExampleConfig.CONFIG_SPEC);

}

}

Legacy only: @SidedProxy, preInit/init/postInit и MinecraftForge.EVENT_BUS как универсальное место для всего. В NeoForge-ориентированном тексте это должно быть только историческим контекстом.

# 7\. Registries и object references

Для NeoForge 1.21.1+ базовый путь: DeferredRegister или RegisterEvent. Для guide лучше рекомендовать DeferredRegister, потому что он уменьшает ошибки порядка регистрации.

## 7.1 Главные правила

- Не создавать static final Block напрямую как единственный источник истины.
- Не вызывать holder.get() слишком рано, до завершения registration lifecycle.
- Для blocks и items использовать специализированные DeferredRegister helpers там, где они доступны.
- Если API требует Holder или DeferredHolder, не заменять его обычным Supplier.
- Регистрацию делать через mod event bus.

## 7.2 Минимальные паттерны

public final class ModBlocks {

public static final DeferredRegister.Blocks BLOCKS =

DeferredRegister.createBlocks(ExampleMod.MOD_ID);

public static final Supplier&lt;Block&gt; MACHINE_CASING =

BLOCKS.register("machine_casing",

() -> new Block(BlockBehaviour.Properties.of()

.strength(3.0F, 6.0F)

.requiresCorrectToolForDrops()));

private ModBlocks() {}

}

public final class ModItems {

public static final DeferredRegister.Items ITEMS =

DeferredRegister.createItems(ExampleMod.MOD_ID);

public static final Supplier&lt;Item&gt; MACHINE_CASING =

ITEMS.registerSimpleBlockItem("machine_casing", ModBlocks.MACHINE_CASING);

public static final Supplier&lt;Item&gt; COPPER_GEAR =

ITEMS.register("copper_gear", () -> new Item(new Item.Properties()));

private ModItems() {}

}

## 7.3 Registry migration table

| **Legacy**                       | **Modern target**                                     | **Примечание**                                      |
| -------------------------------- | ----------------------------------------------------- | --------------------------------------------------- |
| GameRegistry.registerBlock       | DeferredRegister.Blocks                               | BlockItem регистрировать отдельно или через helper. |
| GameRegistry.registerItem        | DeferredRegister.Items                                | Item.Properties обновить.                           |
| GameRegistry.registerTileEntity  | DeferredRegister&lt;BlockEntityType<?&gt;>            | TileEntity теперь BlockEntity.                      |
| EntityRegistry.registerModEntity | DeferredRegister&lt;EntityType<?&gt;>                 | Отдельно атрибуты, renderer и spawn rules.          |
| OreDictionary.registerOre        | Tags, чаще c: namespace                               | Не registry, а data-driven membership.              |
| RegistryObject как основной тип  | Supplier / DeferredHolder / DeferredBlock по ситуации | Не смешивать Forge tutorials с NeoForge 1.21.x.     |

# 8\. Blocks, Items, BlockStates и Data Components

Большая часть простых items и blocks переносится относительно спокойно, но старые metadata blocks, IIcon и ItemStack NBT требуют переработки.

## 8.1 Metadata blocks

В 1.7.10/1.12.2 блоки часто хранили варианты через metadata 0-15. После flattening это обычно надо переводить в отдельные block IDs или BlockState properties.

| **Legacy pattern**                 | **Лучший modern target**           | **Когда использовать**                                                         |
| ---------------------------------- | ---------------------------------- | ------------------------------------------------------------------------------ |
| Один блок с 16 metadata-вариантами | Несколько отдельных blocks         | Если варианты являются разными материалами или контентом.                      |
| Один блок с metadata orientation   | BlockState DirectionProperty       | Если состояние реально является состоянием одного блока.                       |
| metadata как machine tier          | Отдельные blocks или enum property | Зависит от рецептов, лута, model complexity и баланса.                         |
| metadata для цвета                 | EnumProperty или отдельные blocks  | Если нужно совместимое поведение с tags/recipes, часто проще отдельные blocks. |

## 8.2 ItemStack data

Старый ItemStack NBT больше не должен быть автоматической первой опцией. В 1.21.x для данных на ItemStack нужно рассматривать Data Components.

- Использовать Data Components для режима предмета, выбранного spell, charge state, module list, tool state.
- Использовать capabilities для поведения и внешнего доступа, например item energy API.
- Не путать data components с block/entity attachments.

## 8.3 Creative tabs

Старый setCreativeTab и Item.Properties().tab не являются modern target. Для существующих вкладок использовать BuildCreativeModeTabContentsEvent, для своей вкладки регистрировать CreativeModeTab через builder.

@SubscribeEvent

public static void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {

if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {

event.accept(ModItems.COPPER_GEAR.get());

}

}

# 9\. TileEntity → BlockEntity и машины

Для tech/magic-модов этот раздел критичен. Почти все машины, алтари, банки маны, infusion pillars, pipes, chargers и storage blocks в старом коде были TileEntity. В новом коде это BlockEntity, но переносить нужно не только класс, а всю модель хранения, ticking, sync, menu и capability access.

## 9.1 Что обычно можно сохранить

- Доменную логику: формулы энергии, рецепты, прогресс, правила крафта, структура multiblock.
- Формат некоторых внутренних данных, если нужен save compatibility.
- Часть алгоритмов поиска соседей или обработки recipes, если они не завязаны на старый World/BlockPos API.

## 9.2 Что обычно переписывается

- Регистрация BlockEntityType.
- save/load methods.
- Ticker registration.
- GUI/menu opening and sync.
- Inventory/fluid/energy access through capabilities.
- Client rendering through BlockEntityRenderer.

## 9.3 Correct ticker placement

Ticker задаётся в block-классе, который реализует EntityBlock. Саму tick-логику удобно держать static method внутри BlockEntity.

public class MyMachineBlock extends Block implements EntityBlock {

@Override

public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

return new MyMachineBlockEntity(pos, state);

}

@Override

public &lt;T extends BlockEntity&gt; BlockEntityTicker&lt;T&gt; getTicker(

Level level, BlockState state, BlockEntityType&lt;T&gt; type) {

return type == ModBlockEntities.MACHINE.get()

? (lvl, pos, st, be) -> MyMachineBlockEntity.tick(

lvl, pos, st, (MyMachineBlockEntity) be)

: null;

}

}

## 9.4 Machine architecture checklist

- Есть ли server-only state и client display state? Не смешивать их.
- Что сохраняется в NBT? Что синхронизируется в menu? Что отправляется custom payload?
- Можно ли уменьшить ticking: проверять раз в N ticks, кэшировать соседей, пересобирать network только при изменении блока?
- Нужна ли capability invalidation или cache update при изменениях соседей?
- Есть ли recipe lookup cache, чтобы не сканировать RecipeManager каждый tick?
- Есть ли отдельная модель для multiblock validation?

# 10\. Inventories, fluids, energy, transfers, capabilities

В v3 эта тема расширена, потому что именно она ломает большинство технических и магических модов. Старый IInventory/ISidedInventory больше не должен быть внешним API машины. Modern target: capability providers and lookups.

## 10.1 Разделение storage и access

| **Задача**                  | **Механизм**                                               | **Почему**                            |
| --------------------------- | ---------------------------------------------------------- | ------------------------------------- |
| Хранить 9 слотов машины     | ItemStackHandler внутри BlockEntity + save/load            | Это внутреннее состояние.             |
| Дать трубам доступ к слотам | Capabilities.ItemHandler.BLOCK provider                    | Это внешнее поведение для automation. |
| Хранить fluid tank          | FluidTank или свой storage + NBT                           | Это состояние блока.                  |
| Дать доступ к жидкости      | Capabilities.FluidHandler.BLOCK                            | Совместимость с другими модами.       |
| Хранить энергию             | IEnergyStorage или свой energy storage                     | Состояние и API могут быть разными.   |
| Дать доступ к энергии       | Capabilities.EnergyStorage.BLOCK/ITEM/ENTITY по target API | Совместимость.                        |

## 10.2 Provider registration pattern

@SubscribeEvent

public static void registerCapabilities(RegisterCapabilitiesEvent event) {

event.registerBlockEntity(

Capabilities.ItemHandler.BLOCK,

ModBlockEntities.MACHINE.get(),

(blockEntity, side) -> blockEntity.getItemHandler(side)

);

}

public class MyMachineBlockEntity extends BlockEntity {

private final ItemStackHandler inventory = new ItemStackHandler(9);

@Nullable

public IItemHandler getItemHandler(@Nullable Direction side) {

return inventory;

}

}

## 10.3 Transfer networks

Для крупных модов с трубами, кабелями или магическими каналами нельзя просто каждый tick сканировать мир. Нужно проектировать network layer.

- Network graph должен обновляться при block update, neighbor change или chunk load/unload, а не пересчитываться полностью каждый tick.
- Для частых capability lookups использовать cache-подход target version, например BlockCapabilityCache там, где он подходит.
- Разделить simulation и extraction/insertion: сначала рассчитать доступные объёмы, потом применить изменения.
- Не доверять клиенту в распределении энергии, предметов или жидкости.
- Добавить тесты на chunk boundaries, unload/reload и dedicated server.

# 11\. Data storage: NBT, SavedData, Attachments, Data Components

Главная ошибка: использовать один механизм для всего. В modern port нужно выбирать хранилище по месту жизни данных.

| **Где живут данные**              | **Modern mechanism**                   | **Пример**                                      |
| --------------------------------- | -------------------------------------- | ----------------------------------------------- |
| Собственный BlockEntity           | Fields + saveAdditional/loadAdditional | Прогресс машины, buffer, mode.                  |
| ItemStack                         | Data Components                        | Заряд wand, выбранный spell, installed modules. |
| Entity, chunk, чужой block entity | Data Attachments                       | Mana игрока, aura чанка, метка на entity.       |
| Уровень или мир                   | SavedData                              | Глобальная research map мира, ritual registry.  |
| Внешний API                       | Capabilities                           | Inventory/fluid/energy access.                  |

## 11.1 Save compatibility

Если мод должен открывать старые миры, нужно отдельно спроектировать data migration. Не рассчитывать, что vanilla DataFixer автоматически поймёт старые modded NBT.

- Зафиксировать старый NBT schema.
- Написать importer: old tag → new state.
- Добавить version field в новые custom data.
- Не удалять старые keys сразу, если нужен staged migration.
- Сделать тестовый мир с legacy данными и проверять load/save/reload.

# 12\. Networking payloads

Старый SimpleNetworkWrapper/IMessage/IMessageHandler переписывается полностью. В NeoForge payloads строятся вокруг CustomPacketPayload, StreamCodec, RegisterPayloadHandlersEvent и PayloadRegistrar.

## 12.1 Packet inventory

Перед переписыванием пакетов нужно составить таблицу всех legacy packets.

| **Legacy packet type** | **Direction**                       | **Modern decision**                                                |
| ---------------------- | ----------------------------------- | ------------------------------------------------------------------ |
| Open GUI packet        | server → client или client → server | Часто заменить на openMenu/menu data, без custom packet.           |
| Machine progress sync  | server → client                     | Menu DataSlot/ContainerData или lightweight clientbound payload.   |
| Button click in GUI    | client → server                     | Serverbound payload с проверкой player, distance, menu state.      |
| Spell cast request     | client → server                     | Server validates cooldown, mana, target, permissions.              |
| Particle effect        | server → tracking clients           | Clientbound payload или vanilla particle sync, зависит от эффекта. |
| Research unlock        | server → client                     | Server authoritative, client receives display sync.                |

## 12.2 Security rules

- Client никогда не должен сообщать "я получил предмет" или "я потратил энергию корректно". Он может только запросить действие.
- Server должен проверять player, distance, container/menu state, held item, cooldown, permissions и loaded chunk.
- Не отправлять большие структуры без лимитов. Для serverbound payload особенно важны size limits.
- Не использовать packets для данных, которые already sync through menu slots or vanilla systems.

## 12.3 Minimal payload shape

public record SyncMachineProgressPayload(BlockPos pos, int progress)

implements CustomPacketPayload {

public static final CustomPacketPayload.Type&lt;SyncMachineProgressPayload&gt; TYPE =

new CustomPacketPayload.Type<>(

ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, "sync_machine_progress"));

public static final StreamCodec&lt;ByteBuf, SyncMachineProgressPayload&gt; STREAM_CODEC =

StreamCodec.composite(

BlockPos.STREAM_CODEC, SyncMachineProgressPayload::pos,

ByteBufCodecs.VAR_INT, SyncMachineProgressPayload::progress,

SyncMachineProgressPayload::new

);

@Override

public Type&lt;? extends CustomPacketPayload&gt; type() {

return TYPE;

}

}

@SubscribeEvent

public static void registerPayloads(RegisterPayloadHandlersEvent event) {

final PayloadRegistrar registrar = event.registrar("1");

registrar.playToClient(

SyncMachineProgressPayload.TYPE,

SyncMachineProgressPayload.STREAM_CODEC,

ClientPayloadHandler::handleMachineProgress

);

}

Signatures могут отличаться в newer 1.21.x. Проверить imports и handler thread rules по target docs.

# 13\. GUI, menus, screens

Legacy GUI нельзя переносить копированием. Нужно разделить серверное меню, клиентский экран, синхронизацию данных и действия пользователя.

## 13.1 Старое и новое

| **Forge 1.7/1.12** | **NeoForge 1.21.1+**               | **Migration note**                   |
| ------------------ | ---------------------------------- | ------------------------------------ |
| GuiScreen          | Screen                             | Client-only.                         |
| GuiContainer       | AbstractContainerScreen            | Client-only, linked to menu type.    |
| Container          | AbstractContainerMenu              | Server side logical container.       |
| IGuiHandler        | MenuProvider / SimpleMenuProvider  | Старый handler убрать.               |
| player.openGui     | serverPlayer.openMenu              | Открывать на logical server.         |
| updateProgressBar  | DataSlot / ContainerData / payload | Выбирать по объёму и частоте данных. |

## 13.2 Menu data strategy

- Простые integer-поля: DataSlot или ContainerData.
- Slot contents: menu slots and ItemStackHandler.
- Большие структуры: не синхронизировать каждый tick, использовать lazy sync или explicit refresh.
- Client screen не должен менять server state напрямую. Button click отправляет serverbound request.

serverPlayer.openMenu(new SimpleMenuProvider(

(containerId, playerInventory, player) ->

new MyMenu(containerId, playerInventory),

Component.translatable("menu.title.examplemod.my_menu")

));

Если нужно передать BlockPos или другую extra data на client menu constructor, использовать overload с Consumer&lt;RegistryFriendlyByteBuf&gt; и MenuType через IContainerFactory / IMenuTypeExtension#create.

# 14\. Rendering, assets и client-only code

Rendering в старых модах часто был тесно связан с блоками и items. В modern port нужно разделить static JSON assets, dynamic renderers и client-only registration.

## 14.1 Static models

- IIcon, registerIcons и setTextureName удалить.
- Block models, item models и blockstates перенести в assets.
- Для большого мода генерировать models/blockstates через datagen.

## 14.2 Dynamic rendering

- TileEntitySpecialRenderer → BlockEntityRenderer.
- Entity renderer регистрируется client-only.
- GUI rendering через Screen/AbstractContainerScreen and GuiGraphics.
- OpenGL immediate style заменить на PoseStack, MultiBufferSource, RenderType.

## 14.3 Dedicated server checklist

- Нет imports net.minecraft.client.\* в common packages.
- Renderer registration не вызывается из main mod constructor без client separation.
- Particles providers, screens, entity renderers, BER находятся в client package.
- runServer и dedicated server test проходят без ClassNotFoundException.

# 15\. Entities, attributes, AI, spawn

Custom entity портируется через несколько отдельных слоёв: Entity class, EntityType, attributes, renderer, synced data, spawn placement, biome spawn integration.

| **Subsystem** | **Legacy**                       | **NeoForge target**                  |
| ------------- | -------------------------------- | ------------------------------------ |
| Registration  | EntityRegistry.registerModEntity | EntityType registry                  |
| Synced fields | DataWatcher                      | SynchedEntityData                    |
| Tick          | onUpdate                         | tick                                 |
| Attributes    | SharedMonsterAttributes          | Attributes / AttributeSupplier       |
| Renderer      | RenderingRegistry                | EntityRenderers.register client-side |
| Biome spawn   | EntityRegistry.addSpawn/events   | Biome modifiers / spawn placement    |
| Spawn egg     | old registry helpers             | SpawnEggItem through item registry   |

## 15.1 AI migration

Goal/AI logic may look familiar, but names and assumptions changed. Port AI by behavior, not line by line.

- Separate navigation, targeting, attacking, idle behavior, interaction and save data.
- Check whether old pathfinding assumptions still hold in current vanilla.
- Attribute registration must be done before entity is used.
- Client renderer should not reference server-only logic.

# 16\. Data-driven resources and datagen

For large legacy mods, datagen is not just convenience. It is the only sustainable way to keep hundreds of recipes, models, tags, loot tables and language keys consistent.

## 16.1 Correct 1.21.1 data paths

| **Data type**      | **Path pattern**                                                            | **Migration note**                                          |
| ------------------ | --------------------------------------------------------------------------- | ----------------------------------------------------------- |
| Recipes            | data/&lt;namespace&gt;/recipe/&lt;path&gt;.json                             | Not recipes plural for 1.21.1 docs.                         |
| Loot tables        | data/&lt;mod_id&gt;/loot_table/&lt;name&gt;.json                            | Not loot_tables plural for 1.21.1 docs.                     |
| Tags               | data/&lt;tag_namespace&gt;/tags/&lt;registry_path&gt;/&lt;tag_path&gt;.json | Common tags usually use c: namespace.                       |
| Biome modifiers    | data/&lt;modid&gt;/neoforge/biome_modifier/&lt;path&gt;.json                | For features and spawns in biomes.                          |
| Advancements       | data/&lt;modid&gt;/advancement/&lt;path&gt;.json                            | Check target version path.                                  |
| Enchantments 1.21+ | data/&lt;modid&gt;/enchantment/&lt;path&gt;.json                            | Enchantments are data-driven and stored as item components. |

## 16.2 Custom recipes

Machine recipes, infusion recipes, ritual recipes and spell crafting usually need custom RecipeType and RecipeSerializer. Do not force them into shaped/shapeless vanilla recipes if the semantics are different.

- Define recipe input model.
- Define serializer with MapCodec/StreamCodec according to target version.
- Create datagen builder.
- Cache recipe lookups inside machines.
- Expose data format clearly for datapack makers.

## 16.3 Loot and GLM

For your own blocks and entities: generate loot tables. For modifying vanilla or other mods drops: prefer Global Loot Modifiers. This avoids overriding many loot tables and improves mod compatibility.

# 17\. Worldgen, biomes, dimensions, structures

Worldgen is a rewrite candidate in almost every 1.7.10/1.12.2 → 1.21.x port. The old generator API and numeric IDs are not a good target.

## 17.1 Split legacy worldgen before porting

| **Legacy feature**     | **Modern target**                                                 | **Risk**                        |
| ---------------------- | ----------------------------------------------------------------- | ------------------------------- |
| Ore generation         | configured/placed feature + biome modifier                        | Medium, but data-driven.        |
| Tree/plant generation  | feature + placed feature + biome modifier                         | Medium/high depending on shape. |
| Mob spawn in biomes    | biome modifier adding spawn entries                               | Medium.                         |
| Custom biome           | data-driven biome definition + registration strategy              | High.                           |
| Custom dimension       | datapack/registry dimension definitions                           | Critical rewrite.               |
| Old structures         | structure sets, template pools, jigsaw or custom structure system | Critical rewrite.               |
| World aura/magic field | chunk attachment/SavedData + generation hook strategy             | Needs architecture.             |

## 17.2 Practical strategy

- Disable legacy worldgen temporarily.
- Port blocks/items/entities first, so worldgen has valid targets.
- Add ore/simple features through data-driven files or datagen.
- Add biome modifiers.
- Only then port structures and dimensions.
- Test new world creation, existing world reload and server generation.

Do not use BiomeLoadingEvent as the target architecture for NeoForge 1.21.1+ guide. Mention it only as older Forge-era context.

# 18\. Config and feature flags

Config migration is usually easy technically, but dangerous architecturally. The main issue is not syntax, but when a config value is read and whether it changes registered content.

## 18.1 ModConfigSpec

Modern NeoForge configs use ModConfigSpec and are registered from the mod constructor via ModContainer#registerConfig.

| **Type** | **Use for**                                                         | **Avoid**                                         |
| -------- | ------------------------------------------------------------------- | ------------------------------------------------- |
| STARTUP  | Very early configuration that does not change content registration. | Do not use to disable blocks/items/entities.      |
| CLIENT   | Client visuals, HUD, keybind-related options.                       | Server gameplay rules.                            |
| COMMON   | Options present on client and server, not synced.                   | Assuming it matches server values in multiplayer. |
| SERVER   | World/server gameplay config synced to client.                      | Client-only visuals.                              |

## 18.2 Legacy config migration

- Map old config keys to new keys.
- Keep old names if users expect compatibility, or document breaking rename.
- Validate ranges aggressively.
- Do not make registry presence depend on non-synced configs.
- For datapack-like content, prefer data files and conditions over config toggles.

# 19\. Commands, debug tools and migration helpers

Commands are not only player-facing features. During porting they are useful as debug tools.

- Use Brigadier command registration.
- Add debug commands to inspect block entity state, aura/chunk state, network graph, research state, and recipe matches.
- Do not leave unsafe debug commands enabled in release builds.
- Use permission checks for commands that mutate world or player data.

## 19.1 Useful migration commands

| **Command idea**              | **Purpose**                                    |
| ----------------------------- | ---------------------------------------------- |
| /modid dump_machine x y z     | Print BE NBT, energy, inventory, recipe match. |
| /modid validate_network x y z | Rebuild and diagnose pipe/energy/mana network. |
| /modid aura chunk             | Inspect chunk attachment or SavedData.         |
| /modid research player        | Inspect migrated research/progression state.   |
| /modid spawn_test_entity      | Validate EntityType, attributes and renderer.  |

# 20\. Special cases: coremods, ASM, mixins, dead dependencies

Большие 1.7.10-моды часто использовали hacks: ASM transformers, reflection, AccessTransformers, coremods, direct private field access, old APIs from dead libraries. Эти части нельзя переносить как обычный код.

| **Legacy case**                        | **Migration decision**                                                         | **Risk**         |
| -------------------------------------- | ------------------------------------------------------------------------------ | ---------------- |
| Coremod/ASM transformer                | Переоценить необходимость. Идеально убрать или заменить на supported hook/API. | Очень высокий.   |
| Reflection into vanilla private fields | Проверить Access Transformer, public API или redesign.                         | Высокий.         |
| Mixin                                  | Использовать только если нет supported API и есть контроль совместимости.      | Высокий.         |
| Dead library dependency                | Заменить, vendoring только после лицензии и безопасности.                      | Средний/высокий. |
| Old integration with other mods        | Сделать optional integration layer.                                            | Средний.         |
| Custom save format                     | Написать importer and schema versioning.                                       | Высокий.         |

# 21\. Legacy-code audit workflow

Перед портированием нужно получить карту старого мода. Без неё порт превращается в хаотическое исправление compile errors.

## 21.1 Audit checklist

- Собрать старый проект, если возможно.
- Зафиксировать список всех features с точки зрения игрока.
- Составить package/class map.
- Найти все registries и static initializers.
- Найти TileEntity/BlockEntity candidates.
- Найти networking packets and directions.
- Найти GUI/container pairs.
- Найти worldgen/dimensions/biomes/structures.
- Найти save data, player data, world data.
- Найти integrations and dead dependencies.
- Найти rendering/client-only classes.
- Найти reflection/ASM/coremod/mixin/AT.

## 21.2 Dependency graph

Для каждого feature указать, от каких систем он зависит. Например, "Arcane Infuser" зависит от blocks, BE, inventory, energy/mana capability, recipes, GUI, networking, rendering particles, research gating. Значит, его нельзя портировать до базовых registries, items, recipes and data storage.

# 22\. Porting strategy for large mods

| **Этап**               | **Цель**                                     | **Exit criteria**                  |
| ---------------------- | -------------------------------------------- | ---------------------------------- |
| 0\. Empty project      | Стабильная среда Java 21/NeoForge.           | runClient/runServer работают.      |
| 1\. Registry skeleton  | Blocks/items/entities/menu types без логики. | No missing registry crashes.       |
| 2\. Simple content     | Простые items, blocks, models, lang.         | Контент виден in-game.             |
| 3\. Data generation    | Recipes, tags, loot, models generated.       | Datagen output валиден.            |
| 4\. Machines core      | BlockEntity save/load/tick без GUI.          | Состояние сохраняется.             |
| 5\. Capabilities       | Inventory/fluid/energy external access.      | Automation работает.               |
| 6\. Menus/screens      | GUI and data sync.                           | Slots and progress sync.           |
| 7\. Networking         | Custom payloads only where needed.           | Server authoritative.              |
| 8\. Entities/rendering | Entity gameplay and visuals.                 | Dedicated server safe.             |
| 9\. Worldgen           | Features, biomes, structures, dimensions.    | New world and reload tests pass.   |
| 10\. Integrations      | Optional compatibility.                      | Missing dependency does not crash. |
| 11\. Release hardening | Performance, regression, docs.               | Testing gates green.               |

# 23\. What to port, adapt or rewrite

| **Subsystem**         | **Decision**                          | **Reason**                                       |
| --------------------- | ------------------------------------- | ------------------------------------------------ |
| Simple items          | Port/adapt                            | Mostly Item.Properties, models, creative tab.    |
| Simple blocks         | Adapt                                 | Registration, properties, blockstate/model JSON. |
| Metadata blocks       | Rewrite design                        | Flattening and BlockState model change.          |
| Machines              | Adapt domain logic, rewrite API layer | BE, capabilities, GUI, sync all changed.         |
| Pipes/cables/networks | Rewrite architecture                  | Performance and capability cache model changed.  |
| GUI                   | Rewrite API layer                     | Menu/Screen split.                               |
| Networking            | Rewrite                               | Payload system replaces old wrappers.            |
| Worldgen              | Rewrite                               | Data-driven model.                               |
| Custom dimensions     | Rewrite completely                    | Numeric IDs gone.                                |
| Rendering             | Adapt/rewrite                         | JSON models, BER, EntityRenderers, client-only.  |
| Configs               | Rewrite syntax, preserve semantics    | ModConfigSpec and config types.                  |
| Recipes/tags/loot     | Rewrite to data/datagen               | JSON/data-driven.                                |
| Research/progression  | Adapt or redesign                     | Depends on old save format and GUI.              |
| Coremods/ASM          | Avoid or redesign                     | High compatibility risk.                         |

# 24\. Testing gates

Testing должен быть встроен в процесс. Нельзя переносить 20 систем, а потом впервые запускать игру.

| **Gate** | **Проверка**                                                      |
| -------- | ----------------------------------------------------------------- |
| Gate 0   | Clean project, Java 21, runClient, runServer, metadata.           |
| Gate 1   | Registries: items, blocks, BE types, entity types, menus.         |
| Gate 2   | Assets/datagen: models, blockstates, lang, recipes, tags, loot.   |
| Gate 3   | Blocks/items in-game: place, break, drops, creative tab.          |
| Gate 4   | BlockEntity persistence: save, reload, setChanged.                |
| Gate 5   | Capabilities: automation access, sided behavior, unload/reload.   |
| Gate 6   | Menus/screens: open server-side, screen client-side, sync.        |
| Gate 7   | Networking: payload registration, validation, no client trust.    |
| Gate 8   | Entities: attributes, renderer, spawn, save/load.                 |
| Gate 9   | Worldgen: new world creation, feature placement, structure tests. |
| Gate 10  | Dedicated server: no client imports, multiplayer connect.         |
| Gate 11  | Performance: tick cost, network size, worldgen lag, memory.       |
| Gate 12  | Regression: core gameplay loops, compatibility, packaging.        |

## 24.1 GameTest and automation

Для блоков, машин, item interactions и некоторых entity behavior стоит рассмотреть Game Tests. Они полезны, если мод большой и после каждого рефакторинга нужно автоматически проверять базовые сценарии.

# 25\. Documentation templates for the team

Документация миграции нужна не "для красоты", а чтобы не решать одни и те же API-вопросы повторно.

## 25.1 Subsystem migration note template

| **Поле**       | **Что писать**                                           |
| -------------- | -------------------------------------------------------- |
| Subsystem      | Например: machine inventory, spell casting, aura chunks. |
| Legacy classes | Главные старые классы и packages.                        |
| Modern target  | Какие NeoForge/vanilla systems используются.             |
| Decision       | Port, adapt, rewrite, postpone.                          |
| Data model     | Что сохраняется, где хранится, как мигрируется.          |
| Networking     | Какие payloads есть, direction, validation.              |
| Client-only    | Renderers, screens, particles.                           |
| Tests          | Какие gates или GameTests покрывают систему.             |
| Risks          | Performance, compatibility, save migration.              |

# 26\. Mod-type playbooks

## 26.1 Magic mod

- Сначала портировать базовые items, blocks, data components for wands/tools.
- Mana/aura: выбрать SavedData, chunk attachments или player/entity attachments по месту жизни данных.
- Research/progression: отдельно спроектировать save model и sync model.
- Spells: server authoritative. Client sends intent, server validates resources and target.
- Particles/rendering: client-only providers, не хранить gameplay state на client.
- Rituals/multiblocks: валидировать server-side, кэшировать structure state.

## 26.2 Tech mod

- Machines: BE + capabilities + menu sync.
- Energy networks: отдельный graph layer, не сканировать мир каждый tick.
- Recipes: custom RecipeType/Serializer + datagen builder.
- Pipes: capability lookup cache, chunk boundary tests.
- Performance: profiler, tick budget, batch updates.

## 26.3 Worldgen-heavy mod

- Контент blocks/items перенести до worldgen.
- Worldgen выразить через data-driven features and biome modifiers.
- Structures/dimensions портировать после базового контента.
- Добавить new world, reload, server generation and datapack override tests.

## 26.4 Content/library mod

- Focus on stable registry names, tags and datagen.
- Keep API packages separated from internal implementation.
- Document breaking API changes for dependent mods.

# 27\. Expanded migration matrix

| **Area**      | **Forge 1.7/1.12**              | **NeoForge 1.21.1+ target**              | **Difficulty** | **Decision**         |
| ------------- | ------------------------------- | ---------------------------------------- | -------------- | -------------------- |
| Build         | Old ForgeGradle, Java 6/7/8     | Java 21, modern Gradle/MDG               | Medium         | New project          |
| Metadata      | mcmod.info                      | neoforge.mods.toml                       | Low            | Rewrite              |
| Mappings      | MCP/SRG                         | Mojang mappings, optional Parchment      | Medium         | Map by role          |
| Lifecycle     | preInit/init/postInit           | mod bus + game bus                       | Medium         | Rewrite init         |
| Registries    | GameRegistry/EntityRegistry     | DeferredRegister/RegisterEvent           | Medium         | Rewrite API          |
| Blocks/items  | metadata, IIcon                 | BlockState, JSON models, Data Components | Medium/high    | Adapt                |
| Creative tabs | CreativeTabs/setCreativeTab     | CreativeModeTab builder/event            | Medium         | Rewrite              |
| TileEntity    | TileEntity/ITickable            | BlockEntity/EntityBlock#getTicker        | Medium/high    | Adapt                |
| Inventory     | IInventory/ISidedInventory      | IItemHandler capability                  | Medium         | Rewrite access layer |
| Fluids        | Old fluid APIs                  | IFluidHandler capability                 | Medium         | Adapt                |
| Energy        | RF/IC2/custom                   | IEnergyStorage or bridge layer           | Medium/high    | Adapt/design         |
| GUI           | GuiContainer/IGuiHandler        | Menu/Screen/openMenu                     | High           | Rewrite              |
| Networking    | SimpleNetworkWrapper/IMessage   | CustomPacketPayload/StreamCodec          | High           | Rewrite              |
| Rendering     | TESR/direct GL/IIcon            | JSON/BER/EntityRenderers/PoseStack       | High           | Adapt/rewrite        |
| Entities      | EntityRegistry/DataWatcher      | EntityType/SynchedEntityData             | High           | Adapt                |
| Recipes       | GameRegistry.addRecipe          | recipe JSON/custom RecipeType/datagen    | Medium         | Data-driven          |
| Loot          | getDrops/events                 | loot_table JSON/GLM                      | Medium         | Data-driven          |
| Tags          | OreDictionary                   | tags, c: common namespace                | Medium         | Data-driven          |
| Worldgen      | IWorldGenerator/WorldGenMinable | features, biome modifiers, datagen       | High           | Rewrite              |
| Dimensions    | numeric IDs/DimensionManager    | registry/datapack dimensions             | Critical       | Rewrite              |
| Config        | Configuration cfg               | ModConfigSpec                            | Low/medium     | Rewrite syntax       |
| Save data     | WorldSavedData/IEEP/NBT         | SavedData/Attachments/Components         | Medium/high    | Design               |
| Coremods      | ASM transformers                | supported APIs/mixins only if necessary  | Critical       | Avoid/redesign       |

# 28\. Old API → modern target quick reference

| **Legacy API**                              | **Modern target**                               | **Note**                                                             |
| ------------------------------------------- | ----------------------------------------------- | -------------------------------------------------------------------- |
| GameRegistry.registerBlock                  | DeferredRegister.Blocks                         | Register on mod bus.                                                 |
| GameRegistry.registerItem                   | DeferredRegister.Items                          | Use Item.Properties.                                                 |
| GameRegistry.registerTileEntity             | BlockEntityType registry                        | TileEntity becomes BlockEntity.                                      |
| EntityRegistry.registerModEntity            | EntityType registry                             | Attributes/renderer/spawn separately.                                |
| IIcon/registerIcons                         | JSON models/textures                            | Remove code icon registration.                                       |
| BlockContainer                              | Block implements EntityBlock                    | Return BlockEntity.                                                  |
| TileEntitySpecialRenderer                   | BlockEntityRenderer                             | Client-only.                                                         |
| ITickable.update                            | EntityBlock#getTicker + static tick             | Ticker not in BE itself.                                             |
| IInventory/ISidedInventory                  | IItemHandler capability                         | Legacy internal use possible, but external API should be capability. |
| SimpleNetworkWrapper                        | PayloadRegistrar + CustomPacketPayload          | Rewrite.                                                             |
| IMessage/IMessageHandler                    | record payload + handler                        | Rewrite.                                                             |
| GuiContainer                                | AbstractContainerScreen                         | Client.                                                              |
| Container                                   | AbstractContainerMenu                           | Server logical menu.                                                 |
| IGuiHandler                                 | MenuProvider/SimpleMenuProvider                 | openMenu on server.                                                  |
| WorldSavedData                              | SavedData                                       | World/level data.                                                    |
| IExtendedEntityProperties                   | Data Attachment or capability                   | Choose storage vs behavior.                                          |
| NBTTagCompound                              | CompoundTag                                     | Modern naming.                                                       |
| stack.getTagCompound for custom stack state | Data Components                                 | For ItemStack state.                                                 |
| OreDictionary                               | Tags                                            | Usually c: common namespace.                                         |
| GameRegistry.addRecipe                      | recipe JSON/datagen/custom recipes              | Data-driven.                                                         |
| getDrops/dropBlockAsItem                    | loot_table JSON or GLM                          | GLM for modifying existing loot.                                     |
| IWorldGenerator                             | features/biome modifiers                        | Rewrite.                                                             |
| DimensionManager numeric IDs                | registry/datapack dimensions                    | Rewrite.                                                             |
| ICommand                                    | Brigadier                                       | Rewrite command registration.                                        |
| Potion/PotionEffect                         | MobEffect/MobEffectInstance                     | Modern naming.                                                       |
| SharedMonsterAttributes                     | Attributes/AttributeSupplier                    | Entity attributes.                                                   |
| MinecraftClient                             | Minecraft                                       | Avoid Yarn naming.                                                   |
| PacketByteBuf                               | ByteBuf/FriendlyByteBuf/RegistryFriendlyByteBuf | Avoid Yarn naming.                                                   |

# 29\. Official source anchors

Эти источники использованы как ориентиры. Для реального проекта открыть документацию именно выбранной версии NeoForge.

| **Topic**            | **Source page**                                                                 |
| -------------------- | ------------------------------------------------------------------------------- |
| Java requirements    | NeoForge User Guide: Java requirements for Minecraft 1.20.5-latest.             |
| Registries           | NeoForge docs: Concepts / Registries.                                           |
| Menus                | NeoForge docs 1.21.1: GUI / Menus.                                              |
| Networking           | NeoForge docs: Networking / Payloads.                                           |
| Config               | NeoForge docs 1.21.1: Miscellaneous / Configuration.                            |
| Data Attachments     | NeoForge docs 1.21.1: Data Storage / Data Attachments.                          |
| Data Components      | NeoForge docs 1.21.1: Items / Data Components.                                  |
| Recipes              | NeoForge docs 1.21.1: Resources / Server / Recipes.                             |
| Loot Tables and GLM  | NeoForge docs 1.21.1: Resources / Server / Loot Tables / Global Loot Modifiers. |
| Tags                 | NeoForge docs 1.21.1: Resources / Server / Tags.                                |
| Biome Modifiers      | NeoForge docs 1.21.1: Worldgen / Biome Modifiers.                               |
| Sounds and Particles | NeoForge docs 1.21.1: Resources / Client / Sounds, Particles.                   |
| Attributes           | NeoForge docs: Entities / Attributes.                                           |
| Game Tests           | NeoForge docs: Miscellaneous / Game Tests.                                      |

# 31\. Deep-dive checklist: content migration

Этот раздел добавляет глубину без раздувания основного текста. Его удобно использовать как чек-лист во время фактического переноса контента.

## 31.1 Blocks

| **Вопрос**                      | **Что проверить**                                                 | **Решение**                                            |
| ------------------------------- | ----------------------------------------------------------------- | ------------------------------------------------------ |
| У блока были metadata-варианты? | Какие варианты являются состояниями, а какие отдельным контентом? | BlockState properties или отдельные block IDs.         |
| Был custom render?              | Достаточно ли JSON модели?                                        | Если нет, custom model loader или BlockEntityRenderer. |
| Был tile entity?                | Нужны ли данные, tick, GUI, capabilities?                         | EntityBlock + BlockEntityType.                         |
| Были нестандартные drops?       | Зависели от tool, silk touch, fortune, NBT?                       | Loot table или custom loot function/GLM.               |
| Были collision bounds?          | Менялись ли по состоянию?                                         | VoxelShape methods.                                    |
| Был hardcoded harvest level?    | Какие tools/tags требуются?                                       | Block properties + tags.                               |

## 31.2 Items

| **Вопрос**                    | **Что проверить**                          | **Решение**                                                               |
| ----------------------------- | ------------------------------------------ | ------------------------------------------------------------------------- |
| Item хранит NBT?              | Это состояние stack или external behavior? | Data Components для state, capability для API behavior.                   |
| Item открывает GUI?           | Нужна server menu или client-only screen?  | Server openMenu для container UI, pure client screen только для settings. |
| Item стреляет/кастит spell?   | Кто authoritative?                         | Client request, server validation.                                        |
| Item имеет variants/subtypes? | Старый damage/metadata?                    | Отдельные items или Data Components.                                      |
| Item имеет model overrides?   | Зависит от state?                          | Item properties/model predicates по target version.                       |

## 31.3 Recipes and data

- Каждый legacy recipe должен получить статус: vanilla-shaped, vanilla-shapeless, furnace-like, machine recipe, ritual/infusion recipe, hidden/debug recipe.
- Machine recipes лучше не прятать в коде. Нужен data format, serializer, datagen builder and validation.
- Если рецепт зависит от research/progression, не смешивать recipe existence and unlock state. Рецепт может существовать в data, а доступность проверяется gameplay logic.
- Для recipe viewer compatibility держать data format predictable и documented.

# 32\. Deep-dive checklist: machine and network migration

## 32.1 Single machine

| **Layer**     | **Migration task**                      | **Common failure**                                                   |
| ------------- | --------------------------------------- | -------------------------------------------------------------------- |
| Block         | Interaction, state, shape, model.       | use method runs client-side only or returns wrong InteractionResult. |
| BlockEntity   | Fields, save/load, tick.                | Data not saved because setChanged not called.                        |
| Inventory     | Internal ItemStackHandler.              | Handler saved but not exposed to automation.                         |
| Capability    | Provider registration and sided access. | All sides expose same slots accidentally.                            |
| Menu          | Slots, quick move, DataSlots.           | Client menu has real server inventory instead of dummy.              |
| Screen        | Draw background, labels, progress.      | Client imports leak into common class.                               |
| Networking    | Only actions not covered by menu sync.  | Client directly mutates machine state.                               |
| Recipe lookup | Cache result or use efficient lookup.   | RecipeManager scanned every tick.                                    |
| Performance   | Tick only when needed.                  | Every machine scans neighbors every tick.                            |

## 32.2 Machine network

Pipes, cables, essentia networks, mana links and item transport systems need a graph model. Direct block scanning every tick works in a small test world, but fails in real bases.

- Represent network nodes and edges separately from block entities.
- Rebuild graph on block placed, removed, neighbor change, chunk load/unload or explicit invalidation.
- Cache capability endpoints and invalidate cache when neighbor block/entity changes.
- Use simulation pass before actual transfer: calculate what can move, then apply.
- Have clear ownership of network state: SavedData, chunk attachment, manager service or distributed BE state.
- Stress-test large networks, chunk boundaries, unload/reload and multiplayer.

# 33\. Deep-dive checklist: magic systems

Magic mods often look less API-heavy than tech mods, but they usually have deeper state: research, aura, player knowledge, spell data, hidden progression, particles and world effects.

## 33.1 Mana, aura and research

| **System**                 | **Possible storage**                            | **Notes**                                          |
| -------------------------- | ----------------------------------------------- | -------------------------------------------------- |
| Player mana                | Entity attachment or capability-like player API | Needs death behavior and sync.                     |
| Chunk aura                 | Chunk attachment                                | Mark chunk dirty when mutable data changes.        |
| Global research state      | SavedData                                       | World-specific progression.                        |
| Per-player research        | Entity attachment or SavedData keyed by UUID    | Choose depending on offline access and sync needs. |
| Wand focus/spell selection | Item Data Component                             | State belongs to ItemStack.                        |
| Ritual structure cache     | BlockEntity or manager cache                    | Do not rescan every tick if structure is stable.   |

## 33.2 Spell casting

- Client sends intent: spell id, hand, target candidate, maybe selected mode.
- Server validates: player state, cooldown, mana, item, research, dimension rules, target range and line of sight.
- Server applies gameplay effects.
- Server sends particles/sound/display sync to tracking clients.
- Client predictions must be cosmetic and reversible.

# 34\. Deep-dive checklist: worldgen and dimensions

## 34.1 Ore or simple feature migration

- Create or register block/item content first.
- Define configured feature and placed feature through data/datagen.
- Apply to biomes through biome modifier.
- Test with a new world and locate generated features.
- Test datapack override by disabling or changing biome modifier.

## 34.2 Structures

- Decide if old structure is better rebuilt as template/jigsaw system or custom structure code.
- Separate structure pieces/assets from placement rules.
- Avoid hardcoded dimension/biome IDs. Use resource keys and tags.
- Test structure generation in multiple seeds, multiplayer, and server-only generation.

## 34.3 Dimensions

Custom dimensions from 1.7.10/1.12.2 with numeric IDs are critical rewrite candidates. Treat them as a new feature with legacy-inspired behavior.

| **Legacy assumption**                         | **Modern replacement**                                                 |
| --------------------------------------------- | ---------------------------------------------------------------------- |
| Dimension ID = integer                        | Resource key / datapack definition.                                    |
| Provider class directly controls everything   | Separate dimension type, biome source, chunk generator and data files. |
| Hardcoded portal target                       | Registry key based target and safe teleport logic.                     |
| Custom sky/weather/rendering mixed with logic | Separate server dimension rules and client rendering.                  |

# 35\. Deep-dive checklist: rendering and visual systems

## 35.1 Model decision tree

| **Visual need**                      | **Best starting point**                                          |
| ------------------------------------ | ---------------------------------------------------------------- |
| Static cube or simple shape          | JSON block model.                                                |
| Orientation or state-dependent model | Blockstate JSON variants.                                        |
| Complex but static model             | Baked model or exported JSON model depending on target workflow. |
| Animated machine part                | BlockEntityRenderer, but keep gameplay state server-side.        |
| Entity model                         | EntityRenderer + model/layers.                                   |
| One-off spell effect                 | Particles and clientbound event/payload if needed.               |
| HUD or overlay                       | Client-only event/screen overlay, no server imports.             |

## 35.2 Visual migration risks

- Old GL state leaks can break other renderers. Modern render code should be scoped and use PoseStack correctly.
- Do not store Minecraft.getInstance() or client objects in common singletons.
- Particle spawning should not become gameplay authority. Gameplay state belongs to server.
- If rendering depends on BE data, define what is synced and how often.

# 36\. Deep-dive checklist: compatibility and optional integrations

Legacy mods often integrated with JEI/NEI, Waila/The One Probe, Baubles/Curios-like APIs, RF/IC2/BuildCraft, Thaumcraft addons or old libraries. In a modern port these should become optional modules.

| **Integration type**       | **Recommended structure**                                        | **Reason**                                            |
| -------------------------- | ---------------------------------------------------------------- | ----------------------------------------------------- |
| Recipe viewers             | Optional integration package loaded only when dependency exists. | Avoid hard dependency.                                |
| HUD/tooltips               | Small adapter layer.                                             | APIs change often.                                    |
| Energy APIs                | Bridge from internal energy to standard capability.              | Preserve internal rules while exposing compatibility. |
| Curios/trinkets-like slots | Separate optional module.                                        | Do not bake into core item logic.                     |
| Other mod blocks/items     | Use tags where possible.                                         | Less brittle than direct item references.             |
| Old dead library           | Replace or internalize only after license review.                | Security and maintenance risk.                        |

# 37\. Risk register

Для крупного порта полезно вести риск-регистр. Ниже стартовый вариант.

| **Risk**                                            | **Impact**                                 | **Mitigation**                                        |
| --------------------------------------------------- | ------------------------------------------ | ----------------------------------------------------- |
| Wrong Java/Gradle baseline                          | Project cannot build or run.               | Gate 0 before any code migration.                     |
| Old API examples copied from Forge/Fabric tutorials | Non-compiling code or wrong architecture.  | Use official target docs and mark examples by status. |
| Client-only class on dedicated server               | Server crash.                              | runServer gate and package separation.                |
| Capabilities implemented with old pattern           | Automation incompatibility.                | Use target provider/lookup pattern.                   |
| Worldgen ported line by line                        | Broken generation or impossible migration. | Rewrite as data-driven features/modifiers.            |
| Network trusts client                               | Dupes, exploits, desync.                   | Server validates every action.                        |
| Save migration ignored                              | Old worlds lose data.                      | Schema versioning and importer tests.                 |
| Performance not tested                              | Lag in real bases.                         | Profiler, stress worlds, network graph tests.         |
| Optional dependency treated as required             | Crashes without addon.                     | Optional integration modules.                         |

# 38\. Review questions before coding

Эти вопросы стоит пройти перед тем, как переносить очередную подсистему.

- Какую gameplay role выполняет подсистема?
- Какие old classes её реализуют?
- Какие data files или registries нужны в NeoForge?
- Какие данные сохраняются и где?
- Какие данные синхронизируются клиенту?
- Какие действия может запрашивать клиент и что валидирует сервер?
- Нужны ли capabilities для внешнего доступа?
- Есть ли dedicated server risk?
- Есть ли worldgen/datapack override risk?
- Какие тесты докажут, что перенос завершён?

# 39\. Glossary

| **Term**          | **Meaning in this guide**                                                                    |
| ----------------- | -------------------------------------------------------------------------------------------- |
| API layer         | Код, который связывает доменную логику мода с NeoForge/Minecraft API. Обычно переписывается. |
| Domain logic      | Игровые правила мода: рецепты машин, магия, прогресс, расчёты энергии. Часто сохраняется.    |
| Data-driven       | Контент описывается data files/datapacks/datagen, а не hardcoded Java-only registration.     |
| Capability        | Внешний API доступа к поведению, например inventory/fluid/energy.                            |
| Data Component    | Данные на ItemStack.                                                                         |
| Data Attachment   | Дополнительные данные на entity, chunk или block entity.                                     |
| SavedData         | Данные уровня/мира.                                                                          |
| Gate              | Минимальный набор проверок перед переходом к следующему этапу.                               |
| Rewrite candidate | Подсистема, где прямой перенос вреднее, чем новая архитектура.                               |
