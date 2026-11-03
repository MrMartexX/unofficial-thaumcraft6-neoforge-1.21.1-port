# Thaumcraft 6 → NeoForge 1.21.1: единый исследовательский реестр визуальных и связанных дефектов

> **Срез исследования: 2026-11-03. Исследование НЕ завершено. Исправления НЕ выполнялись.**
>
> **Базовый код:** [`main`, commit `72eaac17a1e1c0c51b27cb151ef7f244944c89d5`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/commit/72eaac17a1e1c0c51b27cb151ef7f244944c89d5). Все ссылки на код ниже относятся к этому срезу, если не указано иное. Если основной код обновится, выводы нужно перепроверить.

## Как читать этот документ

Это **единая точка продолжения**, объединяющая первоначальные обнаружения, полный накопленный отчёт 2 ноября, результаты 3 ноября и дополнительные проверки истории коммитов. Первоисточники сохраняются ниже **целиком, без сокращения содержимого**, в разворачиваемых приложениях A и B; новое систематизировано в основном тексте. Первоначальная история обсуждения изучена для восстановления пропущенных деталей, но **личная стенограмма переписки не публикуется в репозитории**: приведены её предметные технические результаты.

- [Статус и границы](#статус-и-границы)
- [Карта проверенных направлений](#карта-проверенных-направлений)
- [Реестр изначального исследования, дополненный по истории](#реестр-изначального-исследования-дополненный-по-истории)
- [Дополнительные находки F01–F09 после N16](#дополнительные-находки-f01f09-после-n16)
- [Чёрные контуры: дерево проверяемых механизмов](#чёрные-контуры-дерево-проверяемых-механизмов)
- [Что осталось до завершения](#что-осталось-до-завершения)
- [Приложение A: полный отчёт 2026-11-02](#приложение-a-полный-отчёт-2026-11-02)
- [Приложение B: полный отчёт-продолжение 2026-11-03](#приложение-b-полный-отчёт-продолжение-2026-11-03)

### Словарь доказательств

| Метка | Что она означает | Что она не означает |
| --- | --- | --- |
| `confirmed_code` | Известно конкретное различие исходного и портового кода/ресурса | Не доказывает, что оно проявляется у всех игроков в клиенте |
| `confirmed_regression` | Различие действительно противоречит проверенной legacy-семантике | Не обязательно известна единая графическая первопричина |
| `historical_find` | Результат первичного прохода, зафиксирован в переданной истории | Не перепроверен заново на всех активных ID |
| `plausible_root_cause` | Механизм правдоподобно объясняет симптом | Не подменяет эксперимент и кадры из клиента |
| `negative_control` | Конкретная гипотеза исключена при данных условиях | Объект не признан полностью визуально корректным |
| `requires_client` | Нужны воспроизводимые сравнения игры/кадров | Не означает отсутствия дефекта |
| `not_covered` | Нет полной проверки данного семейства | Не означает "нет дефектов" |

## Статус и границы

**Исходное требование пользователя:** глубокое и широкое сопоставление порта с Thaumcraft 6 1.12.2, выявление повторяющихся дефектов и мест, где аудиторы их пропустили; **до окончания сбора информации не изменять код порта и существующие аудиты**. Исходный запрос охватывает Creative/inventory, цвета и display-повороты, локализацию, Curios/броню, модели и формы неполных блоков, GUI/HUD/Thaumonomicon, печати, чёрные поверхности Inlay, аспекты и мобов. Дополнительно пользователь сообщил о неправильном виде Hungry Chest, толстых чёрных обводках и рассинхронизации заполнения шкал, Grappler, Thaumaturge/Darkseer иконках.

**Контрольные результаты объёма (не метрики визуального успеха):**

| Объект/срез | Количество | Ограничение |
| --- | ---: | --- |
| Первичный просмотр `item models` | 533 | Не означает сравнения всех углов/состояний |
| Первичный просмотр `block models` | 264, для одной alpha-проверки 253 | Разные способы подсчёта и критерии |
| `blockstates` | 227 | Наличие файла не равно корректной state-модели |
| Java-файлы рабочего модуля по первичному подсчёту | 735 | Не означает покрытия поведения каждого класса |
| Зарегистрированные блоки по предыдущему проходу | 175 | Нужна актуальная точная инвентаризация |
| Предметы/фабричные объявления по предыдущему проходу | ~466 | Не равно количеству Creative-вариантов |
| Alpha-наблюдения в block models | 98 → 77 → 37 | Это последовательные фильтры, не "37 багов" |
| Классификация `render_type` для 37 кандидатов | 18 нарушений legacy layer-контракта, 19 без доказанного нарушения *именно слоя* | Для 19 возможны другие дефекты |
| Уникальные legacy Creative скриншоты | 10 | Все имеются, но полная слотная разметка отсутствует |
| Активные special armor item ID без legacy custom armor model | 16 | Геометрия на теле не проверена покадрово |
| Современные регистрации entity renderers | 43 | Сопоставлены 43 legacy; 9 invisible классифицированы адресно |
| Современные GUI-файлы из последнего скана | 25 | Для 18 найдены прямые blit; отсутствует исчерпывающая runtime-матрица |

**Особенно важно:** текущий отчёт фиксирует *состояние исследования*, а не утверждение о завершённом full parity. Базовая ветка не изменялась в рамках этого документа; новый документ предназначен для ревью через PR. Локальные AIH worktrees и незакоммиченные исправления не входят в подтверждённый GitHub-срез.

## Карта проверенных направлений

| Направление | Базовые ID | Новые проверки | Итог к этому срезу |
| --- | --- | --- | --- |
| Creative, лишние предметы, blueprint | D06, D13; исторический H01 | N06 | 4 standalone blueprint отсутствуют в найденных legacy-регистрациях; полный порядок 10 экранов не закрыт |
| Отображение предметов, предикаты, повороты | D01, D02–D04, D08; H02–H03 | F03, F04, F07 | Потеря состояния/предиката Grappler, ARGB-кандидаты, отсутствующие цветовые обработчики; трансформации для всех ID не проверены |
| Локализации | D13; H04 | — | Выявлен список missing keys из первичного анализа; требуется повторная инвентаризация текущих ключей |
| Броня, Curios, мобы | D09–D10; H05 | N01–N04; F05–F06 | Подтверждены архитектурные замены геометрии, 16 armor ID, 9 invisible случаев распределены по семантике |
| Неполные блоки, occlusion, alpha | D08, D12; H06 | N05, N12–N16; F07, F08 | 18 моделей с потерей legacy-layer; выявлены конкретные отклонения shape/sub-hit; Hungry Chest добавлен в очередь |
| HUD и GUI | D05; H07 | N07–N11; F01–F02 | Нарушение алгоритма заполнения и порядка слоёв; причину толстого контура **пока не удалось доказать** |
| Печати и синхронизация | D07; H08 | — | `SealEntity.syncToClient` заглушка; клиентский путь не восстановлен |
| Аспекты предметов | D11; H09 | — | Проверен частичный parity dataset; полная legacy ItemStack-матрица отсутствует |
| Аудиторы/CI | исходная диагностика | — | Проверка структурной целостности не покрывает визуальную семантику; обновление аудиторов отложено |

## Реестр изначального исследования, дополненный по истории

В приложении A сохранён подробный первичный реестр **D01–D13**. В приложении B сохранены полные записи **N01–N16**, включая доказательства, исключения и адресные ссылки. Ниже **H01–H10** восстанавливают детали первого большого прохода, которые иначе могли потеряться при переходе между чатами.

| ID | Конкретный результат первичного прохода | Статус на момент консолидации |
| --- | --- | --- |
| H01 | В `TCCreativeTabOrder` дважды добавлялись `VIS_GENERATOR` (строки 300/352) и `ARCANE_EAR_TOGGLE` (360/740). `deepslate_amber_ore`, `deepslate_cinnabar_ore`, `deepslate_quartz_ore` являются современными расширениями, а не предметами legacy 1.12.2. Обнаружено расхождение между политикой скрытия заглушек и комментариями к compatibility placeholders. | `historical_find`: повтор в исходнике не гарантирует два отображения; нужна проверка текущей вкладки и фактических ID |
| H02 | `thaumatorium.json` имел только `display.gui`, без собственных first/third-person hand transforms. `condenser_lattice` и `condenser_lattice_dirty` имели `gui/fixed`, без отдельных hand transforms. 142 модели прямо ссылались на блок/сложный loader, 126 не имели собственного `display`. | `historical_find`, массовое число не является количеством ошибочных поворотов; нужны effective parent transforms и сравнение всех контекстов |
| H03 | Сопоставление оригинального `ColorHandler.java` и портового `TCColorHandlers.java` выявило неперенесённые цветовые ветви для focus, caster, golem placer, filtered tube, inlay/stabilizer, баннеров; Nitor и аспектовые цвета являются примерами уже реализованной правильной ARGB-логики. | `confirmed_code` в ряде активных ветвей; уточнения F03/F08; для баннеров ещё определить активный путь |
| H04 | В первичном анализе не найдены имена для `golem`, `vis_battery`, `condenser_lattice`, `condenser_lattice_dirty`, `pattern_crafter`, `potion_sprayer`, `grapple_gun`, `grapple_gun_tip`, `grapple_gun_spool`; дополнительно технические `placeholder_bars/anvil/cauldron/table` и `hole`. Старый `ru_ru.lang` не заменяет `ru_ru.json`. | `historical_find`: ключи нужно перепроверить на текущем SHA и по каждому активному translation key; технические заглушки учитывать отдельно |
| H05 | `CuriosClientCompat` ранее подключал рендеры `CURIOSITY_BAND` и `GOGGLES`; `HeadCurioRenderer` использовал плоскую поверхность. Отдельные кастомные модели одежды/брони отсутствовали, хотя PNG на месте. | `historical_find`; N01 точнее подтверждает 16 special armor ID, обычную Thaumaturge Cloth ошибочно не включать в эти 16 |
| H06 | Кандидаты сложной геометрии/несовпадающих interaction shapes: Arcane Workbench, Charger, Golem Builder, Stabilizer, Pattern Crafter. Отдельно N12–N16 доказали shape/subHit регрессии для ряда объектов. | `historical_find` для первоначальной группы: визуальная геометрия, collision, selection и occlusion должны сравниваться **раздельно**; не приписывать автоматический регресс legacy-блоку с полной коллизией |
| H07 | В GUI-кандидаты включены Smelter, Golem Builder, Focal Manipulator, Research Table, Thaumonomicon Browser/Entry. У focal-иконок поворот -90° присутствовал и в legacy; квадратные текстуры `textures/foci/_medium.png` и `_effect.png` были в оригинале. | N10 подтверждает негативный контроль: нельзя просто удалить -90° или заявить, что квадратный фон появился в порте |
| H08 | Для установленных печатей пустой `SealEntity.syncToClient(Level)`, нет подтверждённого аналога `RenderEventHandler.drawSeals`. Есть риск отсутствия persistence серверной карты. | `confirmed_code` для заглушки, `requires_client` для итогового проявления, сетевой синхронизации и сохранения |
| H09 | `TCAspectTooltipEvents` ограничивает показ аспектов контейнерным экраном, Shift и непустыми данными. `TCAspectAssignments` использует явные назначения + generated cache. Раннее parity: 1798 legacy, 1987 port, 1139 совпавших сопоставленных, 398 без надёжного mapping. | Полный per-variant legacy coverage отсутствует; числа не заявлять как итог |
| H10 | Кандидаты мобов: Guardian/Warden/Crab, Golem, Mind Spider; визуально отдельно проверять Cultist Knight/Cleric, Brainy/Inhabited Zombie, Firebat/Pech/Taintacle/Taint Seed. Сохранённые entity PNG не доказывают корректную UV и анимацию. | D09 и N01–N04 адресно уточнены; другие кандидаты остаются открытыми |

**Не переносить исторические количества и строки как безусловно актуальные:** основной код мог измениться после их первоначального измерения. При новых результатах добавлять новую запись с SHA и явным выводом о том, что проверено.

## Дополнительные находки F01–F09 после N16

Все F-записи получены в ходе последующих чтений того же GitHub-среза и истории коммитов. Они **дополняют**, а не отменяют D/N-реестр.

### F01. Research Table действительно исправляли через явное смешивание альфа-канала

**Уровень:** `confirmed_code` для истории Research Table; связь со шкалами HUD: `plausible_root_cause`, **не доказана**.

- [`a0095771`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/commit/a0095771d3127bdab457cb3e2add5a14961a8f34) исправлял alpha blending Research Table и вводил специальный путь рисования GUI.
- [`d39be45`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/commit/d39be45db1b6d0c577bd85f78fc29c2b269b6a0d) заменил вариант `guiGraphics.blit(RenderType::guiTextured, ...)` на последовательность `RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); guiGraphics.blit(...)` в `TCResearchTableScreen.blitGui`.
- [`ddc8c5a`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/commit/ddc8c5acf1a76e26456d0f4d31a7386cb3036f79) устранил двойные вызовы `drawFullTexture/blit` для knowledge в **`TCThaumonomiconEntryScreen`**, не в Research Table. Эти два эпизода нельзя смешивать.
- В [`TCInstrumentHud.java`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCInstrumentHud.java) нет аналогичной явной пары `enableBlend/defaultBlendFunc`. Однако vanilla GUI рендер может сам устанавливать соответствующий state: отсутствие вызовов **не доказывает** ошибку.
- N09 отдельно фиксирует повторный вызов `TCKnowledgeGainHud.renderOverScreen` в самом [`TCResearchTableScreen.java`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java); число вызовов и blend-исправление — **разные механизмы**.

**Следующий тест:** инструментировать pre/post blend state и порядок вызовов во время рисования Vis/Flux/Sanity, сравнить пиксели рамки при GUI scale 1–4 и без стороннего UI/resource pack. Проверить разницу при `defaultBlendFunc`, не переписывая HUD до воспроизводимого доказательства.

### F02. Статическая карта прямого GUI blit и явного blend

**Уровень:** `confirmed_code_inventory`; визуальное проявление: `requires_client`.

Первый адресный скан 25 Java-файлов в `thaumcraft/client/gui`: в 18 обнаружены прямые `blit` вызовы; среди этих 18 в 13 есть явный `RenderSystem.enableBlend`, в 5 отсутствует: `ClientAspectTooltipComponent`, `TCFocusPouchScreen`, `TCInstrumentHud`, `TCTurretAdvancedScreen`, `TCTurretBasicScreen`. Это **кандидаты для проверки render-state**, но не пять доказанных ошибок: многие вызовы обслуживает `GuiGraphics`.

В `ClientAspectTooltipComponent.renderAmount` четыре смещённые чёрные копии текста формируют намеренную контурную обводку **цифр**. Не считать её объяснением чёрной рамки Vis/Sanity.

### F03. Картирование tintindex и проблемного ARGB

**Уровень:** `confirmed_code` по return values/model contract; игровые артефакты: `requires_client`.

- Просмотр прямых современных JSON `"tintindex"` дал 21 model-файл, включая `block/candle`, `inlay_*`, `stabilizer`, `tube_filter_core`, `jar_normal/void_25/50/75/100`, `taint_growth_*`, `condenser_lattice_side` и `item/caster_gauntlet_focus`. Для `tube_filter_core` задействованы шесть граней с индексом 1; у `caster_gauntlet_focus` отмечено 15 тонируемых граней. Точное присутствие `tintindex` — не гарантия вызова корректного handler.
- 6 активных окрашиваемых item ID: `cloth_boots/legs/chest` и `void_robe_legs/chest/helm`. [`TCColorHandlers`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCColorHandlers.java) возвращает `DyedItemColor.getOrDefault` как 24-битный RGB на слое 0 и `WHITE=0xFFFFFF` для остальных. Правило NeoForge 1.21.1 требует ARGB, то есть обычно непрозрачный RGB = `0xFFRRGGBB`. Legacy default dye 6961280 = `0x6A3880`. **Порядок robe texture layers совпадает с оригиналом:** это важный negative control.
- 16 candle tint values и 7 crystal item tint values заданы RGB, соответствующие активные JSON-слои тонируются; листья также берут RGB из vanilla `FoliageColor`/`BiomeColors`. Для динамических/world блоков и OBJ ещё требуется runtime-проверка.
- Контроль: 16 Nitor item color values и aspect/jar tint ветки используют непрозрачный ARGB. Нельзя говорить, что сломан весь `TCColorHandlers`.

**Проверить:** способ сборки клиента и то, применяет ли именно текущий рендер-путь возвращённую alpha каждого обработчика. Один и тот же симптом "чёрное" не является доказательством ARGB-причины для любого объекта.

### F04. Все прямые item-model overrides в современной ресурсной выборке

**Уровень:** `confirmed_code` для model/property-несоответствия.

Найдено семь item-моделей с JSON `overrides`. Активные `jar_normal`/`jar_void` (предикат `thaumcraft:fill`), `caster_basic` (`thaumcraft:focus`) и `primordial_pearl` (`thaumcraft:type`) имеют регистрации соответствующих свойств. `grapple_gun.json` проверяет **ненеймспейсированный** `type`, активной property-регистрации для него нет; кроме того, [`TCGrappleGunItem`](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/items/tools/TCGrappleGunItem.java) не поддерживает оригинальное `loaded` переключение и сброс. Нужно восстановить **оба** условия для реальной иконки, не путать их с рендером крюка в мире.

`caster_advanced.json` и `verdant_charm.json` содержат старые overrides, но активных одноимённых item-регистраций в `TCItems` не найдено. **Не объявлять действующими дефектами без доказательства reachable item.**

### F05. Полная классификация невидимых entity-renderer связей, без ложных девяти багов

**Уровень:** `confirmed_code` для регистраций; фактические FX: `requires_client`.

Из 43 modern entity type/renderer registrations и 43 legacy counterpart mappings девять современных назначены `TCInvisibleEntityRenderer`:

| Entity | Original | Вывод |
| --- | --- | --- |
| `GRAPPLE` | Видимый крюк/трос | **Потеря** видимой модели/эффекта, частицы `CRIT` не заменяют геометрию |
| `FOCUSMINE` | Визуализируемая Focus Mine | **Потеря** модели/элемента, `WITCH` частицы не эквивалент |
| `GOLEM_DART` | Видимый снаряд | **Потеря** модели/эффекта |
| `MIND_SPIDER` | `RenderMindSpider`, полупрозрачный паук с viewer-condition, alpha около 0.1, scale 0.6 | **Потеря** условной геометрии для видящего игрока; современное `VIEWER` состояние сохранено, но renderer пуст |
| `FOCUS_PROJECTILE` | `RenderNoProjectile` без mesh, вызывает `renderParticle` | Не требовать модель; проверить FX/путь вызова отдельно (`CRIT` не гарантирует parity) |
| `FOCUS_CLOUD` | Legacy без собственной mesh | `negative_control` для геометрии |
| `TAINT_SWARM` | Legacy без собственной mesh | `negative_control` для геометрии |
| `ALUMENTUM` | Legacy `RenderNoProjectile` | `negative_control` для геометрии |
| `CAUSALITY_COLLAPSER` | Legacy `RenderNoProjectile` | `negative_control` для геометрии |

Это перепроверка и детализация D09/приложения A, а не девять независимо доказанных регрессий.

### F06. Модельная геометрия мобов и эффекты специального предмета

**Уровень:** `confirmed_code_path_mismatch`; client rendering не проверен.

- Guardian и Warden: legacy `ModelEldritchGuardian`, UV 128×64, плащ из секций, ремни и броня; modern `HumanoidModel(ModelLayers.PLAYER)` с 64×64 layout.
- Eldritch Crab: legacy `ModelEldritchCrab`, UV 128×64, хвост, клешни и броня; modern `SpiderModel(ModelLayers.SPIDER)`.
- Thaumcraft Golem: legacy `golem_base.obj` и составной `PartModel`; modern `HumanoidModel(ModelLayers.PLAYER)` с выбором текстуры по материалу.
- `TaintCrawler`: vanilla Silverfish в оригинале и порте, не включать в тот же список.
- Modern `TCEldritchGolemModel` и `TCPechRenderer` имеют существенную **собственную** геометрию; не приравнивать их к обобщённым vanilla-заглушкам.
- **Дополнительная находка:** legacy `RenderSpecialItem` добавлял к брошенному `SPECIAL_ITEM` вращающиеся светящиеся магентово-белые additive rays (примерно 5–10 quads по графическому режиму), затем vanilla item; modern `SPECIAL_ITEM` назначен обычный `ItemEntityRenderer`, эффект не восстановлен. **Открыто:** есть ли активный gameplay-путь создания `TCSpecialItemEntity`? Наличие регистрации без reachable spawn не доказывает видимый дефект у игрока.

### F07. Дополнительные неполные блоки и Hungry Chest

**Уровень:** Hungry Chest — `user_observed_requires_code_client_confirmation`; другие блоки — исторические кандидаты.

Пользователь сообщает, что **Hungry Chest выглядит как обычный блок досок**, а не как сложный сундук оригинала. Предыдущий обзор указывал на упрощённую модель и отсутствие полноценной block-entity визуализации, но полного `blockstate -> model -> BER -> animation` доказательного дерева для этого ID пока **нет**. Не считать тикет закрытым. Проверить установленный блок, предмет, открытие, лицевая сторона и поведение при поглощении предметов, включая текстуру/анимацию/форму коллизии.

Блоки, требующие такой же связанной проверки: `Brain Jar` (модель/контейнер), `Hole` (эффект/шейдер), `Dioptra` (индикатор/матрица), `Mirrors` (тоннель/портал), `Focal Manipulator` (вращение кристаллов), `Centrifuge` (ротор), `Void Siphon` (визуал портала), `Pattern Crafter` (subHit/иконки), `Alembic` (подписи), `Essentia Tubes` (соединения/стержни), `Golem Builder` (пресс/лава), `Warded Jars` (содержимое/этикетки), `Flux Rift` (геометрия), `Cultist Portal` (рамка/UV), `Arcane Bore` (луч). Это **список сохранённых подозрений/ранних сравнений, НЕ набор всех доказанных ошибок**. По `Pattern Crafter`, jars и `Void Siphon` есть частичные проверки в N/D; исключения прежних проверок не обнуляют другое поведение.

### F08. Динамические block/item цвета: причины нельзя смешивать

**Уровень:** `confirmed_code_registration_loss`.

В legacy `ColorHandler.java` были прямые обработчики для `Inlay` (по `CHARGE 0..15`), `Stabilizer` (по энергии), filtered tube (`tintindex=1` и выбранный аспект), focus items (`getFocusColor`), caster gauntlet focus (`tintindex=1`), golem placer (`material.itemColor`). В современных регистрациях часть этих active ID не обслуживается эквивалентным `BlockColor`/`ItemColor`. Для Inlay и Stabilizer также независимо установлена потеря legacy cutout/translucent в 18-модельной группе. Это два разных механизма.

Legacy `BlockInlay.colorMultiplier` и `BlockStabilizer.colorMultiplier` формировали **непрозрачный** `0xFF...`. Inlay при `CHARGE=0` даёт около `0xFF4C4C4C`, при `CHARGE=15` — белый `0xFFFFFFFF`. Поэтому **отсутствие этого tint само по себе должно убирать затемнение, а не объяснять чёрную плоскость**: для пользовательского симптома чёрного Inlay нужны проверки `render_type`, alpha, геометрии, layering и material pipeline.

### F09. Что точно НЕ следует считать доказанной ошибкой

**Статус:** `negative_control` / `not_covered` по конкретным гипотезам.

1. Совпадение `hud.png` и других девяти проверенных PNG снимает **гипотезу повреждения конкретных файлов**, но не blend/UV/разный renderer.
2. Исходный `hud.png` имеет согласованные с портовыми исходными координатами размеры; не подтверждена универсальная ошибка размера текстуры.
3. Две фазы Sanity HUD присутствуют в legacy — это не случайный второй вызов одного и того же элемента. Смещения/формула заполнения остаются.
4. Различный порядок слоёв Voidseer и Thaumaturge уже был в original JAR.
5. Поворот и квадратный фон focus-иконок присутствуют в legacy, но новый pipeline ещё может менять итоговый ракурс.
6. Девять `TCInvisibleEntityRenderer` не равны девяти багам; см. F05.
7. 19 из 37 alpha-моделей **не имеют доказанного legacy render-layer mismatch**, но не признаны идеальными вообще.
8. Прямые JSON-ссылки на имена моделей/текстур в указанном исследовании не дали missing targets, но indirect OBJ/MTL, UV, selection, tint и игровой результат этим не проверены.

## Чёрные контуры: дерево проверяемых механизмов

| Симптом | Подтверждённый факт | Рабочие гипотезы, требующие различения |
| --- | --- | --- |
| Толстая чёрная рамка Vis/Flux/Sanity HUD | PNG `hud.png` совпадает; отличаются порядок, координаты и заполнение; Research Table в прошлом исправляли через `defaultBlendFunc` | Blend equation/state и формат альфы GUI, двойной blit, atlas sampling/UV, соседние пиксели, наложение заполнения и фона |
| Чёрная плоскость Stabilizing Inlay | Утеряны legacy render layers и динамическая окраска | Solid вместо cutout/translucent, alpha и draw order; **отсутствие tint не объясняет чёрный цвет само по себе** |
| Тёмные иконки robes/candles/crystals | RGB значения отправляются в ARGB contract в части tint handlers | Нулевой high alpha, прозрачный tint, альтернативный pipeline, слой/модель конкретного предмета |
| Неправильно выглядящие мобы | Для некоторых renderer заменена geometry/UV | UV несовместимость, неверный texture size, armor layer, анимация; не обязательно blend |
| Research Table в прошлом | Есть конкретный коммит исправления blend helper | Проблема HUD может быть аналогична, но **пока доказана лишь аналогия**, не общий root cause |

**Критерий закрытия чёрного контура:** сравнение парных снимков legacy/port при одинаковом масштабе и настройках; изоляция UI quad/текстуры; проверка blend-state во время кадра; минимальный воспроизводимый case; root cause указан по каждому HUD или доказан как общий экспериментом.

## Что осталось до завершения

Доказательное покрытие ещё не является исчерпывающим. Остались **следующие самостоятельные этапы**:

1. [ ] Сгенерировать **полный активный registry manifest**: block/item/blockstate/model/textures/BER/armor/entity/renderers/screens/particles/FX/aliases с legacy counterpart и флагами active vs archive.
2. [ ] Для каждого активного `item` проверить весь `model parent chain`, `overrides/item properties`, `tintindex`, красители/компоненты, 3D transforms во всех GUI/hand/ground/fixed contexts, локализации и ItemStack metadata variants.
3. [ ] Creative: разметить **все 10 legacy PNG** по слотам, сверить реальный runtime таб порта, порядок, дубль/extra/omission и четыре blueprint.
4. [ ] Для всех активных блоков связать `blockstates`, geometry, `render_type`, alpha pixels, tint, baked results, BER и точные shapes: selection, collision, occlusion, picking, sub-hit. Включить Hungry Chest и дополнительные tile visuals/animations.
5. [ ] Для всего набора GUI/HUD воспроизвести alpha/blend/UV/offset при нескольких GUI scale, подтвердить/исключить первопричину толстого контура Vis/Sanity; отдельно проверять Research Table knowledge overlap.
6. [ ] Отдельно закрыть все **entity/armor/Curios/FX** пути: custom geometry/UV, анимации, условные renderer слои, сетевые viewer-условия, special-item rays, частицы, first/third-person.
7. [ ] Проверить server↔client paths **seals/aspects**: display conditions (goggles/`ISealDisplayer`), persistence и packet sync; сделать точный аспектный parity для всех legacy ItemStack-вариантов.
8. [ ] Сохранить реестр **object-by-object** с `legacy_file/line`, `port_file/line`, URL SHA, категорией, механическим diff, тестом, ожидаемым результатом, screenshots и статусом. Указывать `not_checked` и negative control явно.
9. [ ] После покрытия и игровых сравнений сформулировать проект улучшения аудиторов (fail-closed для доказуемой статики), оценить false positives, и **только затем** согласовывать изменения кода/аудиторов.

**Блокирующие для "полного завершения" доказательства:** нет полной пары игровых снимков legacy/modern для каждого проверяемого случая, нет инвентаря всех активных ID со статусом каждого и не определена общая или раздельные причины всех чёрных контуров. Поэтому процент "готовности полного исследования" из имеющихся данных вычислять нельзя.

## Источники, происхождение и сохранность

- База кода: [repo https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port); зафиксированный выше SHA; legacy исходники `01_original_jar/`, `02_existing_decompiled_repo/`, `04_api_reference/`, современный модуль `05_neoforge_port/`.
- Входящие файлы пользователя: `TC6_VISUAL_AUDIT_CONTEXT_RECOVERY_2026-11-02(1).md` и `chat_history.txt`. Полный первый Markdown отчёт сохранён ниже как приложение A; технические факты из переписки включены в H/F реестр. Личный текст переписки не дублируется в публичном GitHub.
- Предыдущее продолжение: `TC6_VISUAL_AUDIT_CONTINUATION_2026-11-03.md`, сохранено целиком как приложение B.
- Источники visual-паритета и артефактов: `06_docs/audits/`, `06_docs/rendering/`, `06_docs/resources/creative_tab_order_reference.md`, `07_Test_Instance_and_Comparisons/`, 10 оригинальных инвентарных PNG.
- Внешний контракт графической системы: [NeoForge 1.21.1: model render types](https://docs.neoforged.net/docs/1.21.1/resources/client/models/) и соответствующие NeoForge client colors docs, упомянутые в оригинальном отчёте. Ссылки на код и commit history имеют приоритет при причинном анализе.
- Открыто: не все первоначальные число/списки имеют сохранённый машиночитаемый raw dump. Их статус честно указан как historical; они не должны превращаться в "подтверждено всё" только из-за сохранения документа.

---

## Приложение A: полный отчёт 2026-11-02

Сохранена **полная исходная версия** пользовательского Markdown отчёта с D01–D13, прямыми ссылками, alpha-классификацией, HUD, Grappler, цветами и entity renderers. Некоторые старые промежуточные статусы уточнены в N/F, поэтому при расхождениях читать хронологически: **A → B → F**.

<details>
<summary>Развернуть исходный отчёт целиком (253 строки)</summary>

# Thaumcraft 6 → NeoForge 1.21.1: восстановленный контекст исследования дефектов

Дата фиксации: 2026-11-02. Статус: **исследование продолжается; не является итоговым аудитом**.

## 1. Цель и неизменные ограничения

Цель: максимально полно выявить отклонения игрового поведения, внешнего вида и интерфейса порта от оригинального Thaumcraft 6 (Minecraft 1.12.2 Forge), включая ещё не замеченные пользователем дефекты и общие первопричины.

- **Пока не изменять код порта, существующие аудиторы, CI, ветки и PR.** В первую очередь собрать исходные данные, доказательства и полный перечень потенциально затронутых объектов.
- Не объявлять полное соответствие на основании сборки, наличия файлов, количества совпавших записей или зелёных отчётов.
- Указывать, что доказано прямым сравнением, что обоснованно предполагается, а что требует проверки в Minecraft-клиенте.
- Не запускать и не дублировать локальные задачи AIH. Локальные незакоммиченные worktree могут отличаться от GitHub `main`.

## 2. Зафиксированный источник истины

- Репозиторий: https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port
- Проверенный `main` SHA: `72eaac17a1e1c0c51b27cb151ef7f244944c89d5` (2026-11-02, GitHub connector и локальная проверка после clone).
- Локально скачан полный read-only исследовательский снимок репозитория (184 МБ, `git status --short` пуст). Этот путь является **временным** и сам по себе не гарантирует сохранения между сессиями.
- Насчитано 9646 файлов командой `rg --files` в скачанном снимке, 307 файлов внутри `06_docs/`. Ранее в переписке упоминался охват 9662 путей, но эти величины нельзя напрямую сравнивать без идентичной методики подсчёта.
- Рабочая реализация: `05_neoforge_port/`. Оригинальные материалы: `01_original_jar/`, `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/`, `04_api_reference/`.
- Сопоставления и скриншоты: `07_Test_Instance_and_Comparisons/`.

Обязательная документация:

1. `06_docs/CURRENT_TASK.md`
2. `06_docs/current_port_status.md`
3. `06_docs/repo_map.md`
4. `06_docs/development_workflow_chatgpt_aih.md`
5. `06_docs/migration/NeoForge_legacy_migration_guide.md`
6. `06_docs/migration/migration_matrix.md`
7. `06_docs/migration/porting_order.md`

Замечание о статусе: `current_port_status.md` фиксирует технические parity-результаты и прямо не заявляет измеренного pixel parity. Его выводы не отменяют дефекты, найденные позже при сравнении с legacy.

### Сохранившиеся исходные скриншоты

Все десять файлов `Thaumcraft_1.12.2_Inventory_1.png` ... `_10.png` находятся в `07_Test_Instance_and_Comparisons/02_Thaumcraft 1.12.2 Inventory Screenshots/`. Наличие каждого файла подтверждено в GitHub и локальном снимке. Полная визуальная разметка каждого слота и порядок всех элементов **ещё не подтверждены**.

## 3. Восстановленные результаты исследовательской переписки

**Обозначения:** `К` — конкретное подтверждение в коде снимка; `П` — вывод из предыдущего исследования, требующий повторной адресной сверки; `И` — игровое/визуальное подтверждение отсутствует. Эти записи являются начальным реестром, не итоговым каталогом всех ID.

| № | Группа | Известная находка и проверяемые источники | Уровень / что осталось |
| --- | --- | --- | --- |
| D01 | Grappler | `models/item/grapple_gun.json` использует предикат `type`, но `thaumcraft/client/TCItemProperties.java` регистрирует `type` только для `PRIMORDIAL_PEARL`. Legacy: `ItemGrappleGun.java`. | **К**; проверить все состояния и клиентскую иконку (**И**). |
| D02 | Цвет одежды | `TCColorHandlers.java` возвращает для robe `DyedItemColor.getOrDefault(...)` (RGB без добавления alpha); в NeoForge 1.21.1 tint ожидается в ARGB. Порядок слоёв Voidseer/Thaumaturge различается **и в оригинале**: это само по себе не дефект. | **К** по коду RGB и оригинальному JAR; проверить фактическую окраску и рецепты окрашивания (**П/И**). |
| D03 | Прочие цвета | В `TCColorHandlers.java` константы AIR/FIRE/etc. и многие dye-коды записаны как RGB, тогда как для Nitor и аспектов alpha явно добавляется; упоминались свечи, кристаллы, листва. | **К** по исходному коду; нужен полный inventory всех регистраций/слоёв, не переносить вывод на каждую грань автоматически. |
| D04 | Динамические цвета | В моделях `inlay_dot.json`, `stabilizer.json` есть `tintindex`, а в `TCColorHandlers.java` отсутствуют регистрации соответствующих BlockColor. Legacy: `thaumcraft/client/ColorHandler.java`. Аналогично проверить filter tube, focus, caster gauntlet, golem placer, banners. | **К** для Inlay/Stabilizer; остальным требуется адресная сверка (**П/И**). |
| D05 | HUD Vis/Flux/рассудка | `TCInstrumentHud.java` использует `graphics.fill` вместо текстурного заполнения legacy. У рассудка есть дополнительный blit рамки поверх заполнения, координаты `x=1` и `x+7`; пользователем отмечены чёрные контуры. Legacy: **`thaumcraft/client/lib/events/HudHandler.java`**. | **К** по коду; природа массовых чёрных контуров **не установлена**; проверить альфу PNG, UV, blend state, sampler/atlas, порядок слоёв, пиксельное смещение и client screenshots (**И**). |
| D06 | Blueprint и Creative | Порт регистрирует 4 отдельных blueprint в `TCItems.java` и добавляет их в `TCCreativeTabOrder.java` (строки ~715–721). В предыдущей проверке legacy `ConfigItems.java`/`ConfigBlocks.java` эти самостоятельные регистрации не обнаружены. | **К** для порта, **П** для полного legacy списка. Не путать отсутствующие предметы с настоящими страницами-чертежами Thaumonomicon; сравнить все десять Creative-скриншотов (**И**). |
| D07 | Печати | `SealEntity.syncToClient(Level)` содержит только комментарий; в предыдущем исследовании не найден эквивалент legacy `RenderEventHandler.drawSeals`. | **К** для заглушки; проверить пути сетевой синхронизации, видимости при `ISealDisplayer` и сохранение на диск (**П/И**). |
| D08 | Thaumatorium и сложные блоки | Для ряда сложных моделей/предметов ранее найдены несоответствия display-трансформаций и `VoxelShape`; `.noOcclusion()` не доказывает корректности коллизии или отрисовки. | **П**; нужна предметная матрица `blockstate/model/display/shape/collision/render layer` и игровая проверка (**И**). |
| D09 | Мобы и снаряды | Ранее отмечены Eldritch Guardian/Warden/Crab, Golem, Mind Spider, Focus Mine и Golem Dart: generic или invisible renderer вместо legacy-геометрии/UV/эффекта. Legacy `ModelEldritchGuardian.java`, `ModelEldritchCrab.java`, `RenderThaumcraftGolem.java`. | **П**; сравнить все активные entity registrations/renderer mappings и gameplay FX по каждому ID (**И**). |
| D10 | Броня и Curios | Часть специальных legacy armor-моделей не восстановлена; в Curios выявлены узкие регистрации для Goggles и Curiosity Band. Не вся бижутерия обязана иметь видимый рендерер. | **П**; сделать полный wearables parity и исключить ложные срабатывания (**И**). |
| D11 | Аспекты и tooltip | `TCAspectTooltipEvents.java` ограничивает показ аспектов (Shift, контейнер, непустой ItemStack и возвращённые аспекты). Предыдущая статистика: legacy 1798, port 1987, 1139 сопоставленных и 1139 совпавших, 398 legacy без надёжного сопоставления. | **П**; проверить data provenance в `07_Test_Instance_and_Comparisons/aspect_parity/`, варианты повреждений, metadata/компоненты, настройку `showTags`. |
| D12 | Прозрачность | В последней переписке сообщалось: 253 block-модели, из них 98 с альфа-пикселями на гранях; для 77 не найден подходящий `render_type`. Возможны false positive (неактивные модели, формат рендера и наследование). | **П**, категорически не 77 подтверждённых визуальных дефектов. Нужна проверка активности и material/render path, отдельных PNG и поведения клиента. |
| D13 | Имена, Creative и модельные aliases | Ранее отмечены пропущенные language-keys, активный ID vs устаревший alias (`pattern_crafter`/`patterncrafter`) и различия порядка вкладки. | **П**; построить точный реестр активных ID и сверить legacy mapping. |

**Ключевая гипотеза по чёрным контурам:** единая причина пока не доказана. Пересечения tint/alpha, оригинального PNG, tintindex, смешивания, неверного UV, наложения фоновых текстур и выборочного `render_type` могут создавать похожий внешний симптом разными способами. Нельзя без сравнения pixel output объявить все чёрные контуры одной ARGB-ошибкой.

## 4. Известные ограничения действующих аудитов

По предыдущему анализу:

- `creative_tabs.ps1` проверяет присутствие ID, но не полное legacy-содержимое, порядок, дубли и лишние записи.
- `visual_model_transforms.ps1` проверяет наличие display-данных, но не фактический legacy-ракурс каждого display context.
- `texture_color_parity.ps1` проверяет ресурсы/цветовые свойства, но не полный runtime tint/ARGB и все отсутствующие обработчики.
- `visual_equivalence_completion.ps1` преимущественно формирует отчёт; warning/review не обязательно ломает сборку.
- `legacy_shape_parity.ps1` не должен считать `noOcclusion` или неполную JSON-модель достаточным доказательством корректного `VoxelShape`.
- Нужны cross-resource проверки: активный ID → модель → parent chain → overrides/predicates → properties → textures → tint layers → renderer/material → screenshot.

**Не менять эти скрипты до конца текущего исследовательского этапа.**

## 5. Покрытие предыдущей работы и пределы доказательства

По истории исследования сообщалось о проверке 533 item models, 264 block models, 227 blockstates, 735 Java classes/files, 175 registered blocks, примерно 466 items, 59 block classes, 33 renderers и 56/56 совпадающих entity-textures. Для части чисел метод подсчёта и raw-output в одном самостоятельном файле не сохранён. Не интерпретировать эти числа как доказательство визуальной корректности всех перечисленных объектов.

В GitHub сохранились дополнительные отчёты и скрипты, которые нужно индексировать как предшествующие источники, а не как закрытие нового аудита:

- `06_docs/audits/generated/` (parity и runtime reports)
- `06_docs/audits/item_block_parity_visual_equivalence_completion.md`
- `06_docs/rendering/rendering_model_pipeline_audit.md`
- `06_docs/resources/creative_tab_order_reference.md`
- `07_Test_Instance_and_Comparisons/aspect_parity/{dumps,reports,input,tools}/`
- `tools/audits/item-block-parity/modules/`

## 6. Что отсутствует и чего не следует предполагать

1. Не обнаружен отдельный сохранённый **полный реестр всех дефектов 1–2 ноября** с объектными строками, evidence ID и client screenshots.
2. Нет доказательств, что доступны незакоммиченные изменения из локальных AIH worktree пользователя; снимок соответствует только публичному `main` SHA.
3. Все десять legacy Creative PNG присутствуют, но не проведена воспроизводимая pixel/slot-разметка всех предметов и современных экранов.
4. Не завершена реальная клиентская матрица для GUI scale, held item first/third person, equipment, block states, entity animations, particles и network-visible seals.
5. Не доказано, что динамические render-state проблемы имеют единую первопричину.

## 7. Следующий порядок работы (только исследование)

1. Зафиксировать полный scope-manifest активных registrations, моделей, textures, screens, GUI overlays, entities, block entities, particles, equipment и их legacy-контрчастей с исключениями.
2. Для каждого ID собрать link chain и diff legacy vs port; для 13 групп выше провести ретест доказательств и классифицировать `confirmed / probable / false positive / requires client`.
3. Creative: извлечь точный оригинальный порядок и варианты из legacy registrations + 10 снимков; сверить современные вкладки, имена, алиасы и четыре blueprint.
4. Rendering: пройти все tint handlers, ARGB, tintindex, модельные parent/override, display contexts, UV, alpha, render_type и совместное поведение GUI/HUD.
5. Entity/armor/block: сверить реальные geometry, UV, states, collision, overlays, save/network visibility и эффектные слои; вести полный registry-based coverage.
6. Получить одинаковые контрольные кадры legacy и NeoForge на заданном GUI scale/шейдерах/ресурс-паках и метаданных рендера. Там, где нет runtime, оставлять явный блокер.
7. Только после полноты перечня — предложить дизайн новых fail-closed аудитов и отдельно согласовать исправления. На текущем этапе **не запускать реализацию**.

## 8. Протокол дальнейшей фиксации

Для каждой записи дефекта сохранять: `ID`, `legacy path + line + SHA/reference`, `port path + line + SHA`, `asset IDs`, `симптом`, `механизм`, `доказательство`, `неоднозначности`, `coverage`, `степень уверенности`, `тест с ожидаемым результатом`, `status`. Отдельно вести `coverage` по всем активным объектам, включая строки `no defect found` и `requires runtime`; отсутствие строки не должно означать «проверено».

Этот документ — контрольная точка восстановления контекста, а **не утверждение о завершении исследования или подтверждение каждого предварительного вывода**.

## 9. Продолжение 2026-11-02: render_type, прозрачность и чёрные поверхности

Исследование выполнено **только чтением** снимка `72eaac17`. Сверена [документация NeoForge 1.21.1](https://docs.neoforged.net/docs/1.21.1/resources/client/models/): если не задан `render_type`, происходит fallback через стандартные назначения `ItemBlockRenderTypes`, затем `minecraft:solid`. В зависимости от нужной семантики используются `cutout`, `cutout_mipped` и `translucent`. Наличие альфа-пикселя само по себе не определяет правильный слой: приоритет у конкретного поведения legacy.

### От первоначальных 77 к более узким 37

1. Из 253 `models/block/*.json` прежний поиск по целому PNG давал 77 моделей с непрозрачным по умолчанию рендером и использованием PNG, имеющих альфу.
2. Новая read-only проверка разрешила parent-модели, ссылки `#texture`, разобрала реальные `elements[].faces`, заданные и автоматически выводимые UV, и проверила пиксели, в которые попадают центры текстурных выборок. Результат: **37** моделей с альфой на используемых гранях и без собственного/унаследованного `render_type`.
3. Из них **17** имеют частичную альфу, **20** — только полностью прозрачные пиксели. Все 37 ссылаются через blockstate на реально зарегистрированные блоки, вместе затрагивают **38 уникальных зарегистрированных ID** (16 оттенков свечей используют общую модель).
4. Это **технические кандидаты**, не 37 доказанных визуальных дефектов. Остаются Minecraft-parent геометрия, нестандартные BER, фактические baked-квады и runtime-проверка. Методика использует предположения о стандартных UV при их отсутствии.

Полный набор 37 model ID из этой проверки:

`inlay_extend`, `lamp_fertility_on`, `inlay_side0`, `taint_geyser`, `arcane_ear_toggle_off`, `inlay_dot_3`, `arcane_ear_toggle_on`, `taint_fibre`, `amber_block`, `taint_growth_2`, `recharge_pedestal`, `smelter_vent`, `taint_growth_1`, `condenser`, `inlay_dot_1`, `arcane_ear_off`, `alembic`, `stabilizer`, `taint_growth_4`, `inlay_side_alt1`, `pillar_ancient`, `void_siphon`, `vis_generator`, `pillar_eldritch`, `lamp_arcane_on`, `candle`, `condenser_off`, `inlay_side1`, `arcane_ear_on`, `lamp_growth_on`, `metal_alchemical_advanced`, `amber_brick`, `infernal_furnace`, `pillar_arcane`, `inlay_dot_0`, `inlay_side_alt0`, `inlay_dot_2`.

### Прямое legacy-сопоставление слоёв (более сильное доказательство)

| Семейство | Legacy | Порт на исследованном SHA | Статус |
| --- | --- | --- | --- |
| Amber Block и Amber Brick | `ConfigBlocks.java` регистрирует `BlockTranslucent`; его `getBlockLayer()` возвращает `TRANSLUCENT` | `TCBlocks.java:224–226` создаёт обычный `Block`, модели `amber_block.json`/`amber_brick.json` не задают `render_type` | **Подтверждённая потеря legacy-слоя** |
| Stabilizer | `BlockStabilizer.java:52–53` возвращает `CUTOUT` | `stabilizer.json` содержит `tintindex`, но не `render_type`; отдельного BlockColor для Stabilizer в `TCColorHandlers.java` нет | **Подтверждены две независимые потери: слой и tint** |
| Stabilizing Inlay | `BlockInlay.java:202–203` разрешает `CUTOUT` и `TRANSLUCENT` | Дочерние `inlay_*` JSON используют альфа-области и не задают эти слои, современный tint отсутствует | **Подтверждено несовпадение пути**, точное разделение baked-слоёв требует анализа |
| Taint Fibre | `BlockTaintFibre.java:203–204` возвращает `CUTOUT` | `taint_fibre.json` и варианты `taint_growth_*` без `render_type` | **Подтверждённое несовпадение** |
| Infernal Furnace | `BlockInfernalFurnace.java:57–58` возвращает `CUTOUT_MIPPED` | `infernal_furnace.json` без `render_type` | **Подтверждённое несовпадение** |
| Jar | `BlockJar.java` возвращает `TRANSLUCENT` | `jar_normal.json`/`jar_void.json` используют `minecraft:translucent` | **Контрольный пример переноса** |
| Leaves | `BlockLeavesTC.java` наследует слой vanilla leaves | `leaves_greatwood.json`/`leaves_silverwood.json` используют `minecraft:cutout_mipped` | **Контрольный пример переноса** |
| Redstone Relay | `BlockRedstoneRelay.java` возвращает `CUTOUT` | Есть legacy-подобные JSON, но отдельной актуальной регистрации `redstone_relay` в `TCBlocks.java` не найдено | **Не засчитывать как активный дефект без регистрации** |

Прямое сравнение с оригинальным `Thaumcraft-1.12.2-6.1.BETA26.jar`: **байт-в-байт совпадают 9 PNG**: `amber_side`, `amber_top`, `amber_brick`, `stabilizer`, `inlay_connect0`, `inlay_under`, `taint_fibres`, `condenser`, `hud.png`.

Pixel facts:

- `amber_side`, `amber_top`, `amber_brick`: все 256 пикселей каждой текстуры имеют alpha **232/255**.
- `inlay_connect0`: **227 из 256** пикселей имеют alpha 0 и RGB `(0,0,0)`.
- `inlay_under`: **512** пикселей имеют alpha 0 и RGB `(0,0,0)`; есть полупрозрачные пиксели.
- `stabilizer`: 1900 пикселей с alpha 0 имеют чёрный RGB; есть и другие прозрачные/полупрозрачные точки.

Отсюда технический вывод: когда используется `solid` вместо нужного альфа-совместимого пути, прозрачные участки с чёрным RGB могут визуально проявляться как чёрные плоскости или контуры. **Это правдоподобная системная причина части сообщений пользователя, но не достаточное доказательство того, что каждый чёрный контур имеет именно эту первопричину.**

### HUD как отдельный механизм

`hud.png` **побайтно идентичен** legacy. Оригинальный `thaumcraft/client/lib/events/HudHandler.java:286–344` рисует заполнение Sanity Checker текстурными фрагментами, затем рамку и особый значок при `tw >= 100`. В порте `TCInstrumentHud.java:94–120` вместо фрагментов рисуются цветные `graphics.fill`, используется отличающееся позиционирование и не воспроизводится отдельный значок максимального warp. Следовательно, для HUD есть прямое расхождение алгоритмов; связывать его чёрный контур с отсутствующим `render_type` блочных моделей **нельзя**.

### Незавершённые проверки этого этапа

1. Пройти оставшиеся 37 по legacy-семантике индивидуально и отделить реальные несовпадения от допустимой прозрачности в атласе/UV.
2. Проверить фактические baked render types для моделей со специальным block-entity renderer и материалами.
3. Сравнить специальные состояния, вращения, GUI/inventory модели и альфа-границы в запущенном клиенте с фиксацией одинаковых условий.
4. Расширить связанный поиск на иконки, entity renderers, GUI overlays, где дефект может происходить из ARGB, наложения UV, blend-mode или другого источника, а не из block JSON.

Код порта, GitHub и действующие аудиты **не изменялись**.

## 10. Перепроверка одежды: отсеян ложный кандидат, подтверждён ARGB

Прямое сравнение `models/item/void_robe_chest.json`, `void_robe_legs.json`, `void_robe_helm.json`, `cloth_chest.json`, `cloth_legs.json` с моделями внутри оригинального JAR показало, что индексы `layer0` и `layer1` **сохранены в исходном порядке** (хотя каталог ресурсов был переименован с `items/` на `item/`). У Voidseer `layer0` уже в оригинале указывал на `*_over`, а у Cloth Robes — на основную текстуру. Предыдущая версия гипотезы «порядок слоёв мог быть сломан при переносе» **не подтверждена**.

Legacy `thaumcraft/client/ColorHandler.java:186–189` применяет цвет при `tintIndex == 0`, в остальных случаях возвращает `-1` (то есть `0xFFFFFFFF`). В порте `TCColorHandlers.java` применяет тот же индекс, но возвращает `DyedItemColor.getOrDefault(...)` без выставления непрозрачного alpha и `WHITE = 0xFFFFFF` вместо `0xFFFFFFFF` для другого слоя. `ItemRobeArmor.LEGACY_DEFAULT_COLOR = 6961280` также 24-битный RGB. Это конкретное нарушение формата ARGB в NeoForge 1.21.1; визуальную степень влияния нужно подтвердить клиентом.

Такая же проверка применима к другим возвращаемым RGB константам в `TCColorHandlers` (кристаллы, свечи, листья), но не следует автоматически объявлять каждую иконку полностью невидимой без runtime-данных. Официальное основание: https://docs.neoforged.net/docs/1.21.1/resources/client/models/#tinting

## 11. Индивидуализация 37 кандидатов: исходные JSON, legacy-классы, исключения

Контроль выполнен по исходному JAR и текущим JSON порта: нормализованы только пути `thaumcraft:blocks/` → `thaumcraft:block/`, `thaumcraft:items/` → `thaumcraft:item/`. Сравнивались **одинаково названные block-model JSON**, а не blockstate-файлы или итоговые baked-модели:

- **14/37** структурно идентичны после нормализации: `inlay_extend`, `inlay_side0`, `inlay_dot_3`, `taint_fibre`, `taint_growth_2`, `taint_growth_1`, `inlay_dot_1`, `arcane_ear_off`, `taint_growth_4`, `inlay_side_alt1`, `inlay_side1`, `inlay_dot_0`, `inlay_side_alt0`, `inlay_dot_2`.
- **9/37** имеют одноимённый legacy JSON с отличиями: **8** добавляют стандартный parent `minecraft:block/block` к прежнему JSON без `parent` (`recharge_pedestal`, `smelter_vent`, `condenser`, `stabilizer`, `void_siphon`, `vis_generator`, `candle`, `arcane_ear_on`), у `infernal_furnace` отличаются пути vanilla-textures (`blocks/lava_still` → `minecraft:block/lava_still`, `blocks/fire_layer_0` → `minecraft:block/fire_0`). Технические изменения parent/path сами по себе не доказательство дефекта; существование vanilla texture и фактический baked-результат ещё подлежат проверке.
- **14/37** не имеют *одноимённого* файла в исходном `models/block/`: `lamp_fertility_on`, `taint_geyser`, `arcane_ear_toggle_off`, `arcane_ear_toggle_on`, `amber_block`, `alembic`, `pillar_ancient`, `pillar_eldritch`, `lamp_arcane_on`, `condenser_off`, `lamp_growth_on`, `metal_alchemical_advanced`, `amber_brick`, `pillar_arcane`. У ряда из них есть legacy blockstates, OBJ или другие родственные файлы, поэтому статус **не «утраченный original asset»**; необходима сверка структуры назначения модели.

**18 из 37** имеют прямую привязку к классам с *явной* legacy-семантикой альфа-слоя:

| Группа | Количество моделей из 37 | Legacy и привязка | Результат |
| --- | ---: | --- | --- |
| `inlay_*` | 9 | `BlockInlay.canRenderInLayer`: `CUTOUT` **или** `TRANSLUCENT` | Потерян multi-layer-путь; нельзя слепо назначить единственный слой без исследования двух проходов |
| `taint_fibre` и `taint_growth_{1,2,4}` | 4 | `BlockTaintFibre.getBlockLayer`: `CUTOUT`; ростовые варианты встречаются в современном `blockstates/taint_fibre.json` | Прямое несоответствие слоя |
| `taint_geyser` | 1 | `ConfigBlocks.java:258` создаёт `new BlockTaint("taint_geyser")`; `BlockTaint.java:70–71` возвращает `CUTOUT` | **Новая подтверждённая потеря legacy-слоя** |
| `amber_block`, `amber_brick` | 2 | `BlockTranslucent`: `TRANSLUCENT` | Подтверждено ранее |
| `stabilizer` | 1 | `BlockStabilizer`: `CUTOUT` | Подтверждено ранее |
| `infernal_furnace` | 1 | `BlockInfernalFurnace`: `CUTOUT_MIPPED` | Подтверждено ранее |
| **Итого** | **18** | Статический parity-анализ | **Не равно количеству проверенных клиентом дефектов** |

При подсчёте важно, что в наборе есть `growth_1`, `growth_2`, `growth_4`, но **нет** `growth_3` (он не попал в альфа-фильтр); вместе с `taint_fibre` это четыре варианта.

**Негативный контроль:** `candle.json` фигурирует в 37, но `BlockCandle.java` в оригинале **не** переопределяет `getBlockLayer()` (наследует обычный путь). Поэтому наличие alpha-пикселя на выбранной UV-области **само по себе не подтверждает миграционную регрессию свечей из-за render_type**. Свечи остаются отдельным подтверждённым кандидатом по ARGB-tint.

Оставшиеся 19 моделей требуют индивидуальной семантической проверки и/или клиентского сравнения; считать их автоматически сломанными было бы ошибкой.

## 12. Новые доказательства по HUD: изменён не только способ рисования

`thaumcraft/client/lib/events/HudHandler.java:207–275` нормирует `base`, `vis`, `flux` по **525**, а при `vis + flux > 1` дополнительно пропорционально уменьшает оба заполнения. Порт `TCInstrumentHud.java:71–86` выбирает `capacity = max(1, aura.base())`, затем использует `vis / capacity` и `flux / capacity`, ограничивая flux оставшейся высотой. Это **конкретная потеря алгоритмической parity**, независимо от текстур, альфы и чёрной обводки.

Воспроизводимый численный сценарий для сравнения: при `base=200`, `vis=200`, `flux=0` оригинал заполнит около `64×200/525 ≈ 24.4` пикселя по вертикали, порт — `64×200/200 = 64` пикселя. Ещё порт вызывает `graphics.blit(HUD, ...)` рамки **до** заполнения Vis/Flux, тогда как оригинал накладывает рамку **после** текстурных уровней. Это отдельный порядок наложения, важный для визуальной проверки границ.

## 13. Новые доказательства по предикатам и динамическим цветам

### Grappler: отсутствует также источник состояния `loaded`

Legacy `thaumcraft/common/items/tools/ItemGrappleGun.java:32–36` регистрирует предикат **`type`** со значением `1` при NBT-флаге `loaded == 1`. При успешном запуске якоря код ставит `loaded=1`, а `onUpdate` сбрасывает его, когда активный Grapple исчезает. В порте `TCGrappleGunItem.java` нет ни записи `loaded`, ни сброса, а `TCItemProperties.java` регистрирует `thaumcraft:type` **только** для `PRIMORDIAL_PEARL`; `models/item/grapple_gun.json` по-прежнему переключает `type=0/1`. Это **две взаимосвязанные потери**: источник состояния и регистрация визуального предиката. Простой registration-only fix не восстановит legacy-поведение.

Read-only перебор `overrides[].predicate` **всех современных `models/item/*.json`** нашёл также `caster_advanced.json` с `focus` и `verdant_charm.json` с `type`, но **одноимённых активных item-регистраций** в `TCItems.java` нет. Современные `verdant_heart`, `verdant_heart_life`, `verdant_heart_sustain` напрямую ссылаются на отдельные `verdant_charm_{0,1,2}`. Поэтому **не засчитывать** отсутствие предикатов у этих двух архивных/неиспользуемых корневых моделей как установленный пользовательский дефект. Активные `caster_basic` (`thaumcraft:focus`), `jar_normal`/`jar_void` (`thaumcraft:fill`), `primordial_pearl` (`thaumcraft:type`) имеют соответствующие регистрации.

### Системный цветовой аудит, сравнение с `ColorHandler.java`

В актуальном `TCColorHandlers.java` **не обнаружены** регистрации, эквивалентные legacy обработчикам для:

- `TCItems.FOCUS_1`, `FOCUS_2`, `FOCUS_3` — legacy применяет `ItemFocus.getFocusColor(stack)`. Современный `ItemFocus.java:40–41` **реализует** `getFocusColor()` из компонента пакета, но этот метод **не подключён к ItemColor**. Модели `focus_1/2/3` используют `item/generated` и окрашиваемый слой `layer0`.
- `TCItems.CASTER_BASIC` — в legacy `itemCasterColourHandler` красит грань `tintIndex>0` цветом установленного focus; современный `caster_gauntlet_focus.json` явно содержит грани с `tintindex:1`, но обработчика ItemColor нет. Вариант `thaumcraft:focus` выбирается модельным предикатом, что **не заменяет tint handler**.
- `TCItems.GOLEM_PLACER` (ID `golem`) — legacy ItemColor использовал `props.getMaterial().itemColor`; современный предмет активен, модель `item/generated` имеет цветовой индекс `0`, но сопоставимый обработчик не зарегистрирован. Реальное извлечение материала из современных компонентов требует отдельной проверки.
- `TCBlocks.TUBE_FILTER` — legacy BlockColor красил `tintIndex==1` по `TileTubeFilter.aspectFilter`; современный `TCColorHandlers` такой регистрации не имеет. Однако надо отдельно проверить, используется ли tint на актуальном block model/BER, прежде чем объявить визуальное проявление доказанным.

Ранее подтверждённые пропуски Inlay и Stabilizer сохраняются; banner/grass-ambient/Nitor нужно **привязывать к реальным портовым регистрациям и пути рендера**: например, Nitor в порте реализован как невидимый блок с клиентским эффектом, поэтому отсутствие block-tint обработчика само по себе не доказывает отсутствия нужной окраски.

Отдельно подтверждена **систематическая смешанная конвенция формата цветов** в одном файле: константы `AIR/FIRE/WATER/EARTH/ORDER/ENTROPY/FLUX`, все 16 `DYE_*`, `WHITE=0xFFFFFF`, возврат `DyedItemColor.getOrDefault`, `FoliageColor`/`BiomeColors` без очевидного заполнения старшего байта — RGB или потенциально RGB; в то же время 16 Nitor item colors, Jar и aspect colors явно используют `0xFF...`. Это затрагивает минимум обработчики **7 типов crystal items, 16 цветов свечей, 6 предметов одежды**; листья проверить в соответствующих baked-tint путях. NeoForge 1.21.1 требует `ARGB`, в том числе для `RegisterColorHandlersEvent.Block`/`.Item` (https://docs.neoforged.net/docs/1.21.1/resources/client/models/#tinting). Конкретный эффект на видимость/оттенок фиксировать по клиентским кадрам, поскольку он может зависеть от способа смешивания и типа модели.

Все результаты этого этапа получены **без изменения кода, моделей или существующих аудиторов**. Отдельный клиентский pixel-parity и полное индивидуальное закрытие 37 кандидатов ещё не проводились.

## 14. Независимый пересмотр сущностей: 9 invisible renderers не означают 9 дефектов

Прямо сопоставлены все девять `TCInvisibleEntityRenderer` из `TCEntityRenderers.java` с регистрациями `thaumcraft/proxies/ProxyEntities.java:112–137` и реальными методами legacy-рендереров:

| Entity type в порте | Оригинальный renderer | Фактическая legacy-семантика | Уточнение статуса |
| --- | --- | --- | --- |
| `GRAPPLE` | `RenderGrapple` | Рисует `ModelGrappler`, свечение и 3D-трос/верёвку (`CoreGLE.glePolyCone`) | **Подтверждена потеря геометрии и верёвки** |
| `FOCUSMINE` | `RenderFocusMine` | Рисует `ModelGrappler` с `textures/entity/mine.png` и меняет оттенок по таймеру | **Подтверждена потеря модели и анимации** |
| `GOLEM_DART` | `RenderDart` | Рисует геометрию снаряда | **Подтверждена потеря видимого снаряда** |
| `MIND_SPIDER` | `RenderMindSpider` | Рисует SpiderModel только адресному наблюдателю (`getViewer`), с прозрачностью до 0.1, с глазным слоем | **Подтверждена потеря управляемой видимости**, нельзя заменять просто обычным постоянно видимым пауком |
| `FOCUS_PROJECTILE` | `RenderNoProjectile` | Геометрию не рисует, но вызывает `EntityFocusProjectile.renderParticle()` | **Несовпадение FX-пути**: современный `TCFocusProjectileEntity.tick()` создаёт обычные `ParticleTypes.CRIT`, требуется сравнение семантики эффекта |
| `FOCUS_CLOUD` | `RenderFocusCloud` | `renderEntityAt` пустой | **Исключение: сама невидимость соответствует legacy**, проверять прочие части эффекта отдельно |
| `TAINT_SWARM` | `RenderTaintSwarm` | `doRender` пустой | **Исключение: сама невидимость соответствует legacy** |
| `ALUMENTUM` | `RenderNoProjectile` | Специальное действие renderer предусмотрено лишь для `EntityFocusProjectile`; для Alumentum визуальных квадов нет | **Исключение по renderer**, эффект взрыва/частиц отдельно не закрыт |
| `CAUSALITY_COLLAPSER` | `RenderNoProjectile` | Аналогично Alumentum | **Исключение по renderer**, общий FX parity не закрыт |

Таким образом: **4** явные потери видимой геометрии/эффекта, **1** несовпадение рендер-зависимых частиц, **4** случая, где пустой renderer по самой геометрии **не является ошибкой**. Отдельно проверять отсутствие client-side owner/visibility синхронизации, особенно для `MIND_SPIDER` и Grapple.

### Сохранённые PNG не означают сохранение геометрии мобов

- Современный `TCEldritchGuardianRenderer` и `TCEldritchWardenRenderer` наследуют `HumanoidMobRenderer` с `new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER))`; оригинальный `RenderEldritchGuardian` применял **`ModelEldritchGuardian`**, причём к Guardian и Warden, с особым alpha-blending и distance fade для Guardian. В порте нет этого специализированного модельного/alpha-пути.
- Современный `TCEldritchCrabRenderer` использует vanilla `SpiderModel(ModelLayers.SPIDER)`; оригинальный `RenderEldritchCrab` использовал собственный `ModelEldritchCrab` с хвостом, клешнями и несколькими сегментами.
- Современный `TCGolemRenderer` использует vanilla `HumanoidModel(ModelLayers.PLAYER)` и меняет лишь texture по материалу; legacy `common/golems/client/RenderThaumcraftGolem.java:53–91` загружает `models/obj/golem_base.obj`, кеширует составные модели и отдельно рисует части/оснастку. Даже сохранённые material PNG не способны воспроизвести OBJ-геометрию и комплектацию через vanilla player model.
- Исследованные `eldritch_guardian.png`, `eldritch_warden.png`, `crab.png` в порте имеют размер **256×128** и предназначались для специфической геометрии/UV (legacy `ModelEldritchCrab` задаёт `textureWidth=128`, `textureHeight=64`). Простая подстановка этих PNG в стандартный player/spider UV-atlas не эквивалентна оригиналу и может давать смешанные/чёрные фрагменты на модели. Причину **конкретных** скриншотных чёрных контуров без client кадра всё равно нельзя считать доказанной.

### Контроль целостности прямых JSON-ссылок

Отдельный перебор всех `models/**/*.json` порта проверил ссылки с namespace `thaumcraft:` в полях `parent`, `textures`, `overrides[].model` на существование соответствующего `models/...json` или `textures/...png`. **Необнаруженных прямых ссылок по этим трём категориям — 0**. Это положительный контроль, **не** тест неверного выбора модели, косвенных OBJ/MTL-ссылок, legacy-геометрии или клиентской функциональности.

</details>

## Приложение B: полный отчёт-продолжение 2026-11-03

Сохранена **полная исходная версия** промежуточного отчёта с N01–N16, всеми приведёнными таблицами, выводами, ссылками и тестами. Записи F01–F09 основного текста уточняют и дополняют эту версию.

<details>
<summary>Развернуть отчёт-продолжение целиком (309 строк)</summary>

# Thaumcraft 6 -> NeoForge 1.21.1: продолжение исследования визуальных дефектов

Дата контрольной точки: 2026-11-03. Статус: **продолжающееся read-only исследование, не итоговая сертификация визуального соответствия**.

## 0. Назначение и преемственность

Этот документ дополняет, но не заменяет переданный `TC6_VISUAL_AUDIT_CONTEXT_RECOVERY_2026-11-02(1).md` (253 строки, 14 разделов) и `chat_history.txt` (1364 строки). Сохраняются все находки, оговорки и доказательства этих источников. Не следует считать перечисленные ниже повторно проверенные участки доказательством завершения остальных областей.

Источник истины: [MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port). Повторная проверка ветки `main` 2026-11-03: SHA `72eaac17a1e1c0c51b27cb151ef7f244944c89d5`, идентичен историческому снимку 2026-11-02. Все приведённые ниже URL на GitHub фиксируют этот SHA, поэтому данные не зависят от будущего движения ветки.

Оригинал: `01_original_jar/`, `02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/`, при необходимости `03_self_decompiled_check/`. Порт: `05_neoforge_port/`. Документы: `06_docs/`. Снимки: `07_Test_Instance_and_Comparisons/`.

**Важный запрет:** сейчас только собираем и проверяем сведения. Не изменять Java, JSON, модели, существующие аудиторы, Git-ветки, CI и PR; не запускать AIH. Не объявлять результат клиента доказанным без воспроизводимой клиентской проверки.

## 1. Полный исходный объём исследования (запрос пользователя)

1. Порядок, состав и лишние предметы в Creative, включая legacy metadata-варианты и четыре blueprint.
2. Предметные цвета, динамические tint, иконки (в том числе Grappler, Thaumaturge, Voidseer/Darkseer), повороты GUI/first-person/third-person.
3. Неотображаемые/неправильные локализованные названия предметов и блоков.
4. Броня и Curios: модель, UV, слои, окраска на теле, видимые аксессуары.
5. Неполные и сложные блоки: геометрия, `VoxelShape`, collision, occlusion, render layer.
6. GUI/HUD: толщина чёрного контура, порядок слоёв и смещение заполнения, Focus-иконки Auromancy и вопрос о квадратном фоне.
7. Невидимые установленные печати с очками/колокольчиком, их синхронизация и сохранение.
8. Stabilizing Inlay и другие чёрные плоскости, альфа и `tintindex`.
9. Видимость аспектов именно legacy-предметов и объяснение несовпадений mapping.
10. Все мобы, снаряды и специальные визуальные эффекты, а не только совпадение PNG.

Условие полноты: для каждого активного ID строится цепочка «legacy registration -> port registration -> model/state/parent -> PNG/UV/tint -> renderer -> runtime». Отдельный статус для каждого элемента: `confirmed`, `probable`, `false_positive_for_this_mechanism`, `requires_client`, `not_checked`. Наличие файла, успешная компиляция, нулевой счётчик missing refs не означают pixel parity.

## 2. Индекс результатов, перенесённых из контрольной точки 2026-11-02

| Старый ID | Направление | Состояние на начало текущего этапа |
| --- | --- | --- |
| D01 | Grappler | `type` модельный predicate без активной регистрации и без источника/сброса `loaded`; кодовая потеря установлена. |
| D02 | Thaumaturge/Voidseer | В ItemColor передаётся 24-bit RGB вместо ARGB; порядок PNG-слоёв в legacy и порте совпадает, гипотеза перестановки слоёв снята. |
| D03 | Прочие RGB/ARGB | Crystal, Candle, dye, Foliage и другие обработчики смешивают 24- и 32-битные цвета, реальный клиентский результат ещё не зафиксирован. |
| D04 | Отсутствующие tint handlers | Inlay, Stabilizer, Focus 1/2/3, Caster Basic, Golem Placer, возможный Tube Filter; часть случаев требует проверки активной модели. |
| D05 | HUD | Алгоритмы Vis/Flux и Warp/Sanity, масштаб 525 против base, порядок рамки, текстурные сегменты и maximum-warp indicator расходятся; причина чёрной обводки открыта. |
| D06 | Creative/Blueprint | Четыре отдельные blueprint регистрации и вставка в вкладку порта; см. усиление доказательства в N06 ниже. |
| D07 | Seals | `SealEntity.syncToClient` заглушка; визуализация установленных печатей не восстановлена, проверять сохранение и сетевую область. |
| D08 | Сложные блоки | Для каждого блока нужна таблица shape/model/display/render и клиентская проверка. |
| D09 | Entity | 4 потери видимой геометрии/эффекта в invisible renderer (Grapple, Focus Mine, Golem Dart, Mind Spider); 1 расхождение FX Focus Projectile; 4 валидных исключения. Специализированные модели Guardian/Warden/Crab/Golem потеряны. |
| D10 | Armor/Curios | Частичная потеря специальных armor-моделей; дополнено точным списком 16 ID (N01). |
| D11 | Aspect tooltip | Прошлые числа 1798 legacy, 1987 port, 1139 сопоставленных/совпавших, 398 несопоставленных; нужны provenance и runtime. |
| D12 | Прозрачность | 77 -> 37 кандидатов после UV-фильтра; 18 с прямой legacy alpha-layer семантикой, 19 оставалось неразобранными; закрыты именно в аспекте legacy-layer в N04. |
| D13 | Localization/aliases | Нужен registry-based полный языковой каталог, исключая архивные неактивные alias. |

### Важные результаты предыдущего этапа, которые нельзя потерять

- В портовом дереве прямые внутренние model/texture ссылки `parent`, `textures`, `overrides[].model` с `thaumcraft:` имели 0 отсутствующих целей. Это контроль целостности, не гарантия корректности.
- Legacy и портовые `hud.png`, `stabilizer`, две `inlay`-текстуры и янтарные PNG сравнивались побайтно: для отмеченного набора совпали. Похожий чёрный контур может порождаться несколькими механизмами (solid с alpha, RGB без alpha, UV, наложение, blending).
- Оригинальный `HudHandler` использует нормирование Vis/Flux по 525 и сначала заполнение, затем рамку; порт нормирует по `base`, рисует цветные прямоугольники и меняет порядок.
- Невидимость `FOCUS_CLOUD`, `TAINT_SWARM`, `ALUMENTUM` и `CAUSALITY_COLLAPSER` не является доказанным дефектом их *геометрии* на основании legacy renderer.
- `caster_advanced` и `verdant_charm` имеют архивные predicates без одноимённой активной регистрации. Не считать их новыми багами в отсутствие доказанного runtime пути.
- Все десять legacy Creative PNG находятся в репозитории, но точная разметка слотов и сравнение с 1.21.1 всё ещё не завершены.
- Прошлые количества просмотренных моделей и классов отражают ширину статического просмотра, не pixel parity; исходный глобальный реестр со скриншотами каждого ID не сохранился.

## 3. Новые проверенные результаты 2026-11-03

### N01. Специальные модели брони: не восстановлена геометрия 16 активных предметов

**Статус:** `confirmed_static_model_path_loss`; фактическая степень UV-искажения для каждой позы: `requires_client`.

Сверены все 11 legacy-классов под `common/items/armor/`, все современные классы этого каталога, `TCArmorMaterials.java`, `TCClientExtensions.java` и регистрации в `TCItems.java`. Legacy пять классов имели `getArmorModel(...)` и выбирали `ModelRobe`, `ModelKnightArmor`, `ModelLeaderArmor` или `ModelFortressArmor` с моделями для двух масштабов. Портовые `ArmorItem` классы этих семейств такой модели не предоставляют; `TCClientExtensions` регистрирует только два fluid extension. Поиск `getHumanoidArmorModel` по репозиторию ничего не нашёл; поиск `ModelRobe` даёт только legacy и контрольную декомпиляцию.

| Legacy armor class / model | Активные ID в порте | Количество |
| --- | --- | ---: |
| `ItemCultistPlateArmor` / `ModelKnightArmor` | `crimson_plate_helm`, `crimson_plate_chest`, `crimson_plate_legs` | 3 |
| `ItemCultistRobeArmor` / `ModelRobe` | `crimson_robe_helm`, `crimson_robe_chest`, `crimson_robe_legs` | 3 |
| `ItemCultistLeaderArmor` / `ModelLeaderArmor` | `crimson_praetor_helm`, `crimson_praetor_chest`, `crimson_praetor_legs` | 3 |
| `ItemFortressArmor` / `ModelFortressArmor` | `thaumium_fortress_helm`, `thaumium_fortress_helm_goggles`, `thaumium_fortress_chest`, `thaumium_fortress_legs` | 4 |
| `ItemVoidRobeArmor` / `ModelRobe` | `void_robe_helm`, `void_robe_chest`, `void_robe_legs` | 3 |
| **Итого** | **16 активных item ID** | **16** |

Имеются 26 modern armor PNG под `textures/models/armor/`, а также legacy PNG под `textures/entity/armor/`. Присутствие PNG и настройка `ArmorMaterial.Layer` не заменяют Java-геометрию. У `ItemRobeArmor` (обычная Thaumaturge Cloth) в legacy **не** было аналогичного `getArmorModel`; неверно автоматически причислять её к этим 16. Проблема иконок Cloth/Voidseer по ARGB остаётся отдельной.

Доказательства: [modern TCItems](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCItems.java), [modern TCArmorMaterials](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/registry/TCArmorMaterials.java), [modern TCClientExtensions](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/TCClientExtensions.java), [legacy armor directory](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/items/armor).

**Будущий тест:** 16 предметов на игроке и armor stand, front/back, crouch/walk, inner/outer layer; сохранить GUI item render отдельно от модели на теле и проверить материал/свечение.

### N02. Inhabited Zombie экипируется неверным комплектом

**Статус:** `confirmed_code_behavior_mismatch`. В legacy `EntityInhabitedZombie.onInitialSpawn` на голову ставится `ItemsTC.crimsonPlateHelm`, на chest/legs с вероятностью от сложности `crimsonPlateChest/Legs`. В порте `TCInhabitedZombieEntity.populateLegacyArmor` ставит `TCItems.VOID_HELM`, `VOID_CHEST`, `VOID_LEGS`. Это меняет силуэт, текстуры и потенциально свойства предметов.

- [legacy EntityInhabitedZombie.java, около строк 48-57](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/entities/monster/EntityInhabitedZombie.java)
- [modern TCInhabitedZombieEntity.java, строки 57-72](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCInhabitedZombieEntity.java)

Это отдельный дефект от специального `zombie_plate_armor.png`: legacy `ItemCultistPlateArmor.getArmorTexture` выбирал для `EntityInhabitedZombie` специальную текстуру, а текущий `ArmorMaterial.Layer` статический. Проверять оба условия раздельно.

### N03. Ритуальный Cultist Cleric: потеря парения и привязки к алтарю

**Статус:** `confirmed_renderer_feature_absence`.

Legacy `RenderCultist.doRender`, если entity имеет тип `EntityCultistCleric` с `getIsRitualist()==true`, добавляет вертикальное синусоидальное покачивание и рисует полупрозрачную анимированную wispy-ленту к home position. В порте `TCCultistRenderer` ограничивается `HumanoidMobRenderer`, бронёй и текстурой; нет соответствующих bob/line. Современный `TCCultistClericEntity` имеет синхронизированный флаг `RITUALIST`, поэтому отсутствующее визуальное поведение не объясняется удалением статуса. Дальше отдельно проверить home position и клиентскую синхронизацию координат.

- [legacy RenderCultist.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/mob/RenderCultist.java)
- [modern TCCultistRenderer.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCCultistRenderer.java)
- [modern TCCultistClericEntity.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/entities/TCCultistClericEntity.java)

**Негативный контроль:** `RenderCultistLeader` содержит метод `drawFloatyLine`, но не вызывает его в `doRender`; нельзя объявлять такой же пропуск у лидера по одному присутствию неиспользуемого метода.

### N04. Giant Brainy Zombie: не перенесено изменение цвета при злости

**Статус:** `confirmed_renderer_behavior_mismatch`.

Legacy `RenderBrainyZombie.preRenderScale` масштабирует Giant Brainy Zombie на `1 + anger` и дополнительно применяет `glColor3f(1, 1-q, 1-q)`, где `q=min(1, anger)/2`. Современный `TCBrainyZombieRenderer.scale` сохранил только `poseStack.scale(1+anger,...)`, tint-эквивалента нет. Нужен клиентский сценарий для трёх уровней злости.

- [legacy RenderBrainyZombie.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/renderers/entity/mob/RenderBrainyZombie.java)
- [modern TCBrainyZombieRenderer.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBrainyZombieRenderer.java)

**Негативный контроль:** legacy `ZOMBIE_VILLAGER_TEXTURES` объявлен, но `getEntityTexture` всегда возвращал `ZOMBIE_TEXTURES`. Отсутствие переключения на `bzombievil.png` в современном renderer само по себе **не** обнаруженная регрессия.

### N05. От 37 кандидатов alpha/solid к завершённой статической классификации именно render-layer семантики

**Статус:** `layer_classification_completed` для 37 **моделей**; `runtime_pixel_parity_open`.

Из переданного документа 18/37 имеют прямое legacy правило слоя: 9 `inlay_*` (CUTOUT и TRANSLUCENT), 4 `taint_fibre/growth`, 1 `taint_geyser`, 2 amber, 1 stabilizer, 1 infernal furnace. Это действительно потерянные либо изменённые layer contracts. Оставшиеся **19/37** проверены против соответствующих legacy-классов и цепочки наследования. У них **нет специального `getBlockLayer` или `canRenderInLayer`**, следовательно, один только отсутствующий `render_type` в 1.21.1 не доказывает отклонение от legacy для этой группы.

| Семейство и модельные ID из оставшихся 19 | Количество | Legacy-класс | Статический результат |
| --- | ---: | --- | --- |
| `lamp_arcane_on`, `lamp_growth_on`, `lamp_fertility_on` | 3 | `devices/BlockLamp` -> `BlockTCDevice` | Нет собственного слоя |
| `arcane_ear_off`, `arcane_ear_on`, `arcane_ear_toggle_off`, `arcane_ear_toggle_on` | 4 | `BlockArcaneEar`, `BlockArcaneEarToggle` | Нет собственного слоя |
| `recharge_pedestal` | 1 | `BlockRechargePedestal` | Нет собственного слоя |
| `condenser`, `condenser_off` | 2 | `BlockCondenser` | Нет собственного слоя |
| `vis_generator` | 1 | `BlockVisGenerator` | Нет собственного слоя |
| `void_siphon` | 1 | `BlockVoidSiphon` | Нет собственного слоя |
| `smelter_vent` | 1 | `essentia/BlockSmelterVent` | Нет собственного слоя |
| `alembic` | 1 | `essentia/BlockAlembic` | Нет собственного слоя |
| `pillar_ancient`, `pillar_eldritch`, `pillar_arcane` | 3 | `basic/BlockPillar` | Нет собственного слоя |
| `metal_alchemical_advanced` | 1 | `basic/BlockMetalTC` | Нет собственного слоя |
| `candle` | 1 | `basic/BlockCandle` | Нет собственного слоя |
| **Итого** | **19** | | **Не доказано layer-несовпадение** |

Проверены также родители `BlockTCDevice -> BlockTCTile -> BlockTC`; они не переопределяют render layer. В `BlockMetalTC` отсутствует отдельная альфа-семантика. Legacy `BlockCandle` ранее служил негативным контролем и теперь подтверждён повторно.

**Смысл исключения:** 19 записей снимаются с очереди «подтверждённое отсутствие render_type». Они **не исключаются** из проверки baked-модели, alpha-пикселей, геометрии, BER, tint, GUI/inventory и фактического вида. В частности, `condenser.json` содержит восемь элементов с неполным объёмом, тогда как `TCCondenserBlock.SHAPE` охватывает полный 16x16x16. Но legacy `BlockCondenser` не переопределял `getBoundingBox`, так что это **кандидат на неудобную коллизию**, не автоматически подтверждённый регрессией против оригинала.

Доказательства: [legacy device classes](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices), [legacy essentia classes](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/essentia), [modern model directory](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/tree/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block).

### N06. Blueprint в legacy Creative: укреплён кодовый контроль

**Статус:** `confirmed_port_extra_item_registration`; независимая визуальная разметка десяти оригинальных скриншотов `requires_client_or_image_review`.

Повторно просмотрены целиком legacy `ConfigItems.java`, `ConfigBlocks.java`, `thaumcraft/api/items/ItemsTC.java` и `CreativeTabThaumcraft.java`. В них отсутствуют самостоятельные предметы или регистрации `golem_press_blueprint`, `infusion_altar_blueprint`, `infusion_altar_ancient_blueprint`, `infusion_altar_eldritch_blueprint`. Legacy вкладка не формирует произвольный список: её класс определяет только иконку `ItemsTC.goggles`. В современном `TCCreativeTabOrder` все четыре явно добавляются в блоке около строк 715-721. По совокупности source регистрации это **дополнения порта, не подтверждённые legacy Creative items**. Страницы/чертежи в Thaumonomicon не путать с `Item` в Creative.

Дальнейшая визуальная работа со скриншотами нужна для полного *порядка* legacy Creative, но не является предпосылкой для вывода об отсутствии этих четырёх самостоятельных item-регистраций.

### N07. HUD: исключена одна из возможных причин UV-смещения

**Статус:** `negative_control_confirmed`. В `TCInstrumentHud` используется шестипараметровая форма `GuiGraphics.blit` с неявным предположением о размере исходной текстуры 256x256. Повторно прочитанный PNG `assets/thaumcraft/textures/gui/hud.png` действительно имеет заголовок IHDR **256x256** (получен из байтов исходного GitHub asset). Поэтому **неправильная базовая размерность PNG в этом overload** не объясняет наблюдаемый чёрный контур. Подозреваемыми остаются UV-регионы, вызовы двух рамок, порядок `fill/blit`, alpha/blending, координаты рамки и различные GUI-scale.

Основание о неявном размере `blit`: [официальная документация NeoForge 1.21.1 по GUI](https://docs.neoforged.net/docs/1.21.1/gui/screens/). Проверка относится к `hud.png`; нельзя автоматически распространить её на остальные GUI PNG.

### N08. Проверка фактических размеров основных GUI PNG: неверная базовая размерность не подтверждена

**Статус:** `negative_control_confirmed` для просмотренных экранов; `runtime_gui_scale_open`.

Для 18 современных GUI-классов проверены задействованные пути `GuiGraphics.blit`, для соответствующих PNG прочитаны реальные заголовки IHDR. Обычные фоновые PNG, передаваемые в `blit(..., 256, 256)`, действительно **256×256**. Два файла Thaumonomicon — `gui_researchbook.png` и `gui_researchbook_overlay.png` — имеют **512×512**, но их Git blob SHA совпадают с исходными legacy ресурсами (`8bfcbcab70120db5fc4ce0f62589d90260463ea0` и `64d201ea2db7c86bbd74c700589177b865942a09` соответственно). Оригинальный `GuiResearchPage` тоже использовал 256-координатную схему `drawTexturedModalRect`, а современный `TCThaumonomiconEntryScreen` оставил `LEGACY_RESEARCH_TEXTURE_SIZE = 256`. Поэтому обнаружение физического 512×512 PNG **само по себе не доказывает новую ошибку порта**.

Источники: [legacy GuiResearchPage](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/gui/GuiResearchPage.java), [modern TCThaumonomiconEntryScreen](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCThaumonomiconEntryScreen.java).

Вывод ограничен координатным пространством и размером исходного PNG. Не проверяет корректность всех UV, состояния blend, фильтрацию, z-order и экранные снимки. Нельзя объяснять общий чёрный контур только несоответствием PNG 256/512.

### N09. Research Table: Knowledge Gain HUD рисуется повторно

**Статус:** `confirmed_repeated_render_call`; визуальная тяжесть эффекта `requires_client`.

В `TCResearchTableScreen.render` строки **165–166** подряд вызывают `TCKnowledgeGainHud.renderOverScreen(guiGraphics, TCClientRenderTime.guiPartialTick())`. Точно такая же пара находится в `renderTooltip`, строки **191–192**. У `TCKnowledgeGainHud.renderInternal` нет защиты от повторного *рисования*: однократность `tickOncePerClientTick` ограничивает лишь изменение состояния, но не вывод книжного значка и частиц. При активном `kgFade > 0` повторные вызовы реально накладывают графику, что может менять её непрозрачность и яркость; точное число отрисовок за кадр зависит от жизненного цикла `Screen` и требует трассировки клиента.

При этом глобальный `TCKnowledgeGainHud.renderLayer` специально пропускает `TCResearchTableScreen`. Дубли **внутри самого экрана** от этого не исчезают. Не установлено, что именно они вызывают толстую чёрную рамку шкал Vis/Warp: это разные участки HUD.

Источники: [TCResearchTableScreen.java, 149–199](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchTableScreen.java), [TCKnowledgeGainHud.java, 80–132](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCKnowledgeGainHud.java).

### N10. Focus-узел в Thaumonomicon: квадратный фон в legacy существовал, ориентация текущего кода совпадает

**Статус:** `negative_control_confirmed` для общей рамки и поворота; `requires_client` для alpha/пиксельного вида.

В оригинальном `GuiResearchBrowser` строки **617–637** до прорисовки каждого research icon выводят базовую рамку/подложку из текстуры: стандартный вариант 32×32 с UV `(80,48)`, `ROUND` использует `(144,48)`, `HEX` — `(112,48)`, hidden сдвигает V ещё на 32, а `SPIKY` добавляет внешний декор. Современный `TCThaumonomiconBrowserScreen` строки **383–397** сохранил ветку выбора базовой рамки. Следовательно, обнаружить **какой-либо квадратный фон** за Focus-иконкой Auromancy не означает автоматически регрессию: стандартная квадратная подложка была и в legacy. Для доказательства лишнего *сплошного* прямоугольника надо сравнить сам PNG и framebuffer при конкретном состоянии узла.

В `TCResearchIconRenderer.renderFocus` есть `Axis.ZP.rotationDegrees(-90)`, что соответствует исходному `GuiFocalManipulator.drawPart` с `GL11.glRotatef(90,0,0,-1)`. Старое предположение о неправильном повороте здесь **не воспроизводится на данном SHA**. Нельзя называть ориентацию текущим подтверждённым дефектом по прошлому наблюдению. Нужен отдельный тест подложки (`_effect`/`_medium`), альфы и цвета glyph.

Источники: [legacy GuiResearchBrowser](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java), [legacy GuiFocalManipulator](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java), [TCThaumonomiconBrowserScreen](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCThaumonomiconBrowserScreen.java), [TCResearchIconRenderer](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCResearchIconRenderer.java).

### N11. Количественная проверка HUD: смещение, условия показа и заполнение

**Статус:** `confirmed_static_coordinate_and_behavior_mismatch`; конечный кадр `requires_client`.

Оригинал: `HudHandler.renderHuds` строки **67–111**, `renderThaumometerHud` **207–282**, `renderSanityHud` **286–334**. Порт: `TCInstrumentHud` **40–119**. Для сравнения берётся только Thaumometer в руке, без HUD палочки и без другой внешней модификации GUI.

| Элемент | Legacy (пиксели GUI) | NeoForge 1.21.1 | Доказуемое отличие |
| --- | --- | --- | --- |
| Начало рамки Thaumometer | `(3,1)` (`translate(2,0)` + quad `(1,1)`) | `(2,2)` | X −1, Y +1 |
| Верхняя граница Vis при равной нормализованной доле `f` | `74 − 64f` | `76 − round(64f)` | Около +2 px по Y, отдельно от другого нормирования |
| Маркер base | `(4,72 − 64b)` при `b=clamp(base/525)` | `(3,74 − round(64b))` | X −1, около +2 px по Y |
| Рамка Sanity, если одна | `(1,1)` | `(1,2)` | Y +1 |
| Рамка Sanity после Thaumometer | `(1,81)` после `start += 80` | `(1,84)` после `y += 82` от 2 | Y +3 |
| Начало ширины заливки Sanity | X=7, ширина 8 | X=8, ширина 8 | X +1 |

Независимо от этих координат **алгоритм тоже отличается**: legacy делит Vis/Flux на 525, использует фрагменты HUD-текстуры и blend/additive glow; порт делит на `aura.base()`, закрашивает прямоугольники ARGB, лишён соответствующей анимации и при максимальном Warp не выводит особый значок. Эти пункты уже были в D05, здесь уточнены проверяемые пиксельные положения.

Ещё два условия показа: legacy Thaumometer HUD включается только при удержании предмета и `inGameHasFocus`, а порт включает aura при **удержании Thaumometer ИЛИ наличии Goggles**, и проверяет `hideGui`, но не повторяет явную legacy-проверку фокуса. Первое — статически подтверждённое изменение поведения (возможно намеренное); второе требует реального теста экранов и событий HUD.

**Вывод для «чёрного контура»:** разная геометрия и порядок draw способны дать заметный край даже при идентичном `hud.png`, но **не доказывают единую причину чёрных обводок на всех объектах**.

Источники: [legacy HudHandler.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/client/lib/events/HudHandler.java), [modern TCInstrumentHud.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/client/gui/TCInstrumentHud.java).

### N12. Arcane Ear и Arcane Ear Toggle: формы выделения и столкновения не соответствуют legacy

**Статус:** `confirmed_voxel_shape_mismatch`; сравнение клиентских контуров `requires_client`.

В исходном `BlockArcaneEar.getBoundingBox` выбранная сторона определяет AABB размером **12×6×12** в 16-пиксельных блоковых координатах, например `UP [2,0,2]..[14,6,14]`, `NORTH [2,2,10]..[14,14,16]`. Современный `TCArcaneEarBlock` возвращает **10×7×10** (`UP [3,0,3]..[13,7,13]`, `NORTH [3,3,9]..[13,13,16]`). С каждой стороны уменьшено поперечное сечение на 2 пикселя и увеличена толщина вдоль facing на 1 пиксель. Для остальных четырёх ориентаций аналогично изменены границы. `TCArcaneEarToggleBlock` наследует форму `TCArcaneEarBlock`, оригинальный Toggle также наследовал базовую форму.

Это подтверждённое расхождение selection/collision geometry. Оно **не является доказанной причиной чёрной кромки в текстуре**: `VoxelShape` и рендер поверхности — разные механизмы.

Источники: [legacy BlockArcaneEar.java, 116–138](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockArcaneEar.java), [modern TCArcaneEarBlock.java, 43–76](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCArcaneEarBlock.java).

### N13. Pattern Crafter: потеря hit-test отдельной части, не просто «куб вместо модели»

**Статус:** `confirmed_interaction_semantics_loss`.

Legacy `crafting/BlockPatternCrafter.java` **82–136** использовал `RayTracer.retraceBlock`, вычислял `subHit` по `IndexedCuboid6`, отдельно подсвечивал точную часть механизма (`getSelectedBoundingBox`, `getCuboidByFacing`) и вызывал `TilePatternCrafter.cycle()` **только** при `subHit == 0`. Современный `crafting/TCPatternCrafterBlock.java` **85–94** вызывает `crafter.cycle()` при любом `useWithoutItem`, не проверяя часть устройства, а альтернативной трассировки subHit в классе нет.

**Негативный контроль:** legacy `getBoundingBox` самого Pattern Crafter возвращал `FULL_BLOCK_AABB`, так что наличие общей полной формы на современном блоке не является проблемой **само по себе**. Утрата касается выделения конкретного subpart и условия действия, а не общей базовой коробки.

Источники: [legacy BlockPatternCrafter.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/crafting/BlockPatternCrafter.java), [modern TCPatternCrafterBlock.java](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCPatternCrafterBlock.java).

### N14. Warded Jar (normal/void): неверная высота формы при корректном translucent render_type

**Статус:** `confirmed_voxel_shape_mismatch`; rendered PNG/layers `negative_control_confirmed`.

Legacy `essentia/BlockJar.getBoundingBox` возвращал `(3,0,3)..(13,12,13)` в пикселях. Современный `TCWardedJarBlock.SHAPE` имеет `(3,0,3)..(13,14,13)`: выделение и по умолчанию коллизия выше оригинала на **2/16 блока**. В `TCBlocks` оба активных `JAR_NORMAL` и `JAR_VOID` создаются через `wardedJarBlock(...)`, то есть отклонение затрагивает минимум оба ID.

Параллельный контроль ресурсов: у `models/block/jar_normal.json` и `jar_void.json` установлен `"render_type": "minecraft:translucent"`, как ожидается от legacy `BlockJar.getBlockLayer = TRANSLUCENT`; модельные `elements` доходят до Y=12. Следовательно, **нельзя** приписывать этим двум банкам отсутствие прозрачного слоя по примеру 18 других кандидатных моделей. Модерновый `TCBrainJarBlock` имеет корректную по высоте форму Y=12, что дополнительно локализует расхождение.

Источники: [legacy BlockJar.java, 54–65](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java), [modern TCWardedJarBlock.java, 41–84](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCWardedJarBlock.java), [modern jar_normal.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/jar_normal.json), [modern jar_void.json](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/jar_void.json).

### N15. Stabilizing Inlay: selection shape перестала учитывать соединения

**Статус:** `confirmed_voxel_shape_mismatch`; гипотеза «shape вызывает чёрную плоскость» **не доказана**.

Legacy `devices/BlockInlay.getBoundingBox` вычислял индекс по четырём связанным сторонам и выбирал одну из **16** коробок `REDSTONE_WIRE_AABB`. Изолированная Inlay имела `(3,0,3)..(13,1,13)`; по мере соединения форма расширялась в соответствующие направления вплоть до `(0,0,0)..(16,1,16)`. В `TCInlayBlock.getShape` всегда возвращается одна форма `(0,0,0)..(16,1,16)` **даже в изоляции**. При этом state `NORTH/EAST/SOUTH/WEST` и логика связи в современном классе сохранились, то есть данные для legacy-формы есть, но функция формы их не использует. Коллизия и там и здесь пуста (non-solid inlay), отличие касается **контуров выделения и raycast в зависимости от соединений**.

Эта находка дополняет старые D04/D12 о tint и слоях Inlay, но является **другим механизмом**, не объясняющим цвет пикселей чёрной плоскости без прямого рендер-теста.

Источники: [legacy BlockInlay.java, 56–70, 368–371](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockInlay.java), [modern TCInlayBlock.java, 24–39, 97–104, 117–132](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCInlayBlock.java).

### Контрольные случаи без ложного обвинения shape

- `TCLampBlock` имеет modern Y=2..14 и X/Z=4..12, совпадает с legacy `BlockLamp` AABB.
- `TCInfusionPillarBlock` повторяет особенность legacy `BlockPillar`: обычное выделение полублока, а коллизия расширяется до двух блоков по высоте.
- `TCVoidSiphonBlock` повторяет составные AABB legacy `BlockVoidSiphon`.
- `TCInfusionCandleBlock` сохраняет форму свечи и отсутствие коллизии.
- `TCCondenserBlock` сейчас использует полную коробку, но исходный `BlockCondenser` не переопределял `getBoundingBox`: это пока **не** shape-регрессия, даже если модель заметно меньше полного куба.

### N16. Системное изменение selection/collision для блоков, у которых legacy использовал полный куб

**Статус:** `confirmed_static_geometry_behavior_delta`; оценка как дефекта или допустимого UX-улучшения требует решения о строгости parity и клиентской проверки.

Проверены исходные `BlockTC`, `BlockTCTile`, `BlockTCDevice`: у них нет общего переопределения `getBoundingBox` или `addCollisionBoxToList`. Ниже проверенные дочерние классы также не задавали специальную общую форму, кроме **Crucible**, у которого отдельно задавалась *коллизия*:

| Активный блок | Legacy: selection / collision | Порт: selection / collision | Что именно изменилось |
| --- | --- | --- | --- |
| `crucible` | Выделение full cube; коллизия из основания Y=0..5 и четырёх стенок толщиной 2 px | `TCCrucibleBlock.SHAPE` из того же основания и стенок используется для `getShape`; коллизия по умолчанию наследует эту форму | Коллизия приблизительно сохраняет исходный контур; **выделение/луч в пустую середину** больше не full cube |
| `bellows` | Full cube / full cube по умолчанию | `TCBellowsBlock`: составной `CORE_SHAPE` плюс выход по шести ориентациям и явный `getCollisionShape=shapeFor(state)` | Выделение и коллизия сужены до видимых частей |
| `centrifuge` | Full cube / full cube | `TCCentrifugeBlock`: составные `(4,0,4)..(12,4,12)`, `(2,4,2)..(14,12,14)`, `(4,12,4)..(12,16,12)` | Выделение и коллизия не заполняют углы |
| `infusion_matrix` | Full cube / full cube | `TCInfusionMatrixBlock`: крестообразная композиция четырёх коробок, наружные края доходят до 0/16 | В пустых углах selection/collision отсутствуют |
| `table` | Full cube / full cube | `TCTableBlock`: столешница и ножки, полые промежутки | Выделение и коллизия только по деталям |
| `research_table` | Full cube / full cube | `TCResearchTableBlock`: столешница и ножки, полые промежутки | Аналогичное изменение |

Это **не** шесть доказанных ошибок текстур или моделей. Для некоторых блоков более точная современная форма может быть осознанным улучшением взаимодействия. Но при строгой цели воспроизводить поведение 1.12.2 это подтверждённое отличие, которое необходимо вынести на решение, а не молча считать соответствием оригиналу. Для `crucible` особенно важно не спутать две независимые формы: оригинал имел **полное выделение** при **полой коллизии**.

Источники: [legacy BlockCrucible](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/crafting/BlockCrucible.java), [modern TCCrucibleBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCCrucibleBlock.java), [legacy BlockBellows](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/devices/BlockBellows.java), [modern TCBellowsBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/devices/TCBellowsBlock.java), [legacy BlockCentrifuge](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/essentia/BlockCentrifuge.java), [modern TCCentrifugeBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCCentrifugeBlock.java), [legacy BlockInfusionMatrix](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/crafting/BlockInfusionMatrix.java), [modern TCInfusionMatrixBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCInfusionMatrixBlock.java), [legacy BlockTable](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/basic/BlockTable.java), [modern TCTableBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/basic/TCTableBlock.java), [legacy BlockResearchTable](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master/src/main/java/thaumcraft/common/blocks/crafting/BlockResearchTable.java), [modern TCResearchTableBlock](https://github.com/MrMartexX/unofficial-thaumcraft6-neoforge-1.21.1-port/blob/72eaac17a1e1c0c51b27cb151ef7f244944c89d5/05_neoforge_port/src/main/java/thaumcraft/common/blocks/crafting/TCResearchTableBlock.java).

## 4. Что удалось проверить и что остаётся открытым

| Подмножество исследования | Текущий результат | Следующий доказательный шаг |
| --- | --- | --- |
| 37 alpha/solid block model candidates | 18 имеют явную потерю legacy layer; 19 не доказывают layer regression | Проверить фактический baked render, RGB/alpha, occlusion и одинаковые кадры |
| 11 legacy armor class families | 5 особых model-классов не подключены в 16 активных ID | Полный wearables/Curios и клиентская матрица всех комплектов |
| Inhabited Zombie | Неверно экипирован `void_*` вместо `crimson_plate_*` | Проверить внешний вид, отдельный `zombie_plate_armor` texture и AI drop |
| Cultist Cleric ritualist | Флаг сохранён, bob и алтарная wispy-линия не отрисованы | Spawn sync home position и клиенты 1.12/1.21 |
| Giant Brainy Zombie | Потерян anger RGB tint | Сравнение 0 / средняя / максимальная злость |
| 4 blueprint | Нет legacy standalone registrations; есть портовые записи | Инвентарные скриншоты и полный каталог других лишних ID |
| HUD/UI | N08 исключает PNG-size как универсальное объяснение; N09 подтверждает дубли Knowledge HUD; N10 снимает ложные гипотезы о Focus; N11 уточняет координаты HUD | GUI scale, UV/alpha, layer order, pixel capture; системная причина контура **не доказана** |
| Non-full shapes | N12 Arcane Ear(+Toggle), N14 jar normal/void, N15 Inlay: подтверждённые shape-отклонения; N13 Pattern Crafter: потеря subHit; N16: 6 системных selection/collision отличий | Полный реестр остальных блоков, клиентская проверка raycast и selection outline |
| Aspect tooltip и legacy variants | База mapping неполна | Реестр активных legacy ID и provenance |
| Остальные mobs, armor, block-entities и FX | Только частичный адресный просмотр | Full registry-based parity без автоматического исключения неподтверждённых случаев |

## 5. Следующая исследовательская очередь

1. **Model/renderer inventory:** все активные entity и armor model bindings; специально исследовать held-item layers, UV и отдельные FX рендереров, сравнивать с legacy. N01-N04 добавить в object ledger.
2. **GUI/HUD:** после N08-N11 собрать полную карту active screens/overlays, проверить двойные вызовы за один кадр, texture UV, порядок blend/z; отдельно воспроизвести thick black border, который пока нельзя свести к одной причине.
3. **Block shape/alpha:** продолжить системное сравнение selection/collision/render/occlusion на всех active ID по аналогии с N12-N15, включая блоки вне исходных 37. Сопоставить экранные выделения с legacy, разделяя форму и цвет/прозрачность.
4. **Creative order и names:** поднять все десять legacy PNG, разметить item/metadata и modern tabs, отделить дополнительные 1.21 ores, заглушки и alias.
5. **Tint/ARGB:** exhaustive matching model `tintindex` -> color registrations, включая динамическое извлечение компонентов, BlockColor/ItemColor/BER; проверить фактическую прозрачность.
6. **Seals/aspects:** проверить пути server -> client -> overlay, сохранение state и условия видимости. Тестировать только когда статические предпосылки полностью собраны.
7. **Методика аудита:** не менять существующие проверки до завершения исследования; после полного покрытия отдельно предложить fail-closed дизайн и запросить согласование исправлений.

Для будущего реестра: `ID | active ID | legacy source/path/line | port source/path/line | diff | root cause | screenshots/test | evidence | status | audit blind spot`. Утверждение `not_checked` обязательно отличать от `no_defect_found`.

## 6. Техническая достоверность и границы

- Все новые выводы N01-N16 получены прямым read-only чтением GitHub-коннектором на зафиксированном SHA; исследование не запускало клиент Minecraft и не создавало/изменяло PR или веток.
- Не запускались Gradle/build/tests: текущая задача исследовательская; игровой клиент и оригинальный JAR в файловой системе этой сессии не материализованы целиком.
- 37, 18, 19 относятся к **моделям**, а не количеству уникальных блоков или подтверждённых визуальных симптомов. 16 относится к **активным item ID** специальных комплектов.
- У портового `TCArmorMaterials` есть текстуры, но это не доказательство корректного UV на собственной legacy геометрии.
- Чёрный контур HUD, чернота Inlay и тёмные модельные фрагменты мобов могут иметь разные физические причины; объединение их в одну ошибку пока было бы недоказанной гипотезой.

**Следующая точка продолжения:** от N16 перейти к остальным non-full-cube блокам и model/renderer inventory, затем к full Creative/tint/localization/Seals/aspects. Утверждения о причинах чёрной обводки проверять **по отдельным объектам**, без универсальной гипотезы. Сохранять фиксированный SHA; не менять код/аудиторы до окончания исследования.

</details>

---

**Правило следующих обновлений:** менять сводный индекс и добавлять новые датированные записи с отдельными IDs и доказательствами, не стирать исходные результаты и не переписывать статусы прошлых срезов задним числом. Перепроверять SHA и делать новую версию через PR; код порта и аудиторов до окончания исследования не менять.
