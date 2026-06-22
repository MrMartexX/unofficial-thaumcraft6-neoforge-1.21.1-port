param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
)

$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path -LiteralPath $RepoRoot).Path
$script:checks = 0

function Read-Text {
    param([string]$RelativePath)

    $path = Join-Path $repo $RelativePath
    if (!(Test-Path -LiteralPath $path)) {
        throw "[tc-port] Missing required file: $RelativePath"
    }

    return (Get-Content -Raw -LiteralPath $path) -replace "`r`n", "`n"
}

function Assert-Contains {
    param(
        [string]$Text,
        [string]$Needle,
        [string]$Label
    )

    if (!$Text.Contains($Needle)) {
        throw "[tc-port] Missing expected boundary: $Label"
    }

    $script:checks++
}

function Assert-Regex {
    param(
        [string]$Text,
        [string]$Pattern,
        [string]$Label
    )

    if ($Text -notmatch $Pattern) {
        throw "[tc-port] Missing expected boundary: $Label"
    }

    $script:checks++
}

$be = Read-Text '05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java'
$menu = Read-Text '05_neoforge_port/src/main/java/thaumcraft/common/menu/TCSmelterMenu.java'
$caps = Read-Text '05_neoforge_port/src/main/java/thaumcraft/common/capabilities/TCMachineCapabilities.java'
$block = Read-Text '05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCSmelterBlock.java'
$essCaps = Read-Text '05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/TCEssentiaCapabilities.java'

Assert-Contains $be 'implements WorldlyContainer, MenuProvider' 'server-owned WorldlyContainer/MenuProvider boundary'
Assert-Contains $be 'public static final int SLOT_INPUT = 0;' 'legacy input slot index 0'
Assert-Contains $be 'public static final int SLOT_FUEL = 1;' 'legacy fuel slot index 1'
Assert-Contains $be 'public static final int SLOT_COUNT = 2;' 'legacy two-slot inventory shape'
Assert-Contains $be 'private static final int[] SLOTS_BOTTOM = {SLOT_FUEL};' 'bottom sided slot exposes fuel/remainder lane'
Assert-Contains $be 'private static final int[] SLOTS_TOP = {};' 'top side exposes no insertion slots'
Assert-Contains $be 'private static final int[] SLOTS_SIDES = {SLOT_INPUT};' 'horizontal sides expose input slot'
Assert-Contains $be 'private final IItemHandler unsidedItemHandler = new SidedInvWrapper(this, null);' 'unsided item handler adapter'
Assert-Contains $be 'sidedItemHandlers.put(direction, new SidedInvWrapper(this, direction));' 'sided item handler adapters'
Assert-Regex $be 'public IItemHandler itemHandler\(@Nullable Direction side\)\s*\{\s*return side == null \? unsidedItemHandler : sidedItemHandlers\.get\(side\);\s*\}' 'side-aware itemHandler dispatch'

Assert-Regex $be 'if \(slot == SLOT_INPUT\)\s*\{[\s\S]*new AspectList\(stack\)[\s\S]*return list\.size\(\) > 0;' 'input slot accepts aspect-bearing stacks only'
Assert-Contains $be 'return slot == SLOT_FUEL && getBurnTime(stack) > 0;' 'fuel slot accepts burnable stacks only'
Assert-Contains $be 'return side != Direction.UP && canPlaceItem(slot, stack);' 'automation insertion is blocked from top'
Assert-Contains $be 'return side != Direction.UP || slot != SLOT_FUEL' 'top extraction remains blocked except explicit fuel-remainder exception'
Assert-Contains $be '|| stack.is(net.minecraft.world.item.Items.BUCKET);' 'bucket remainder extraction exception'
Assert-Contains $be 'return new TCSmelterMenu(containerId, inventory, this);' 'menu is created from smelter BlockEntity'
Assert-Contains $be 'Containers.dropContents(level, pos, this);' 'inventory drops on block removal'
Assert-Contains $be 'ContainerHelper.saveAllItems(tag, items, registries);' 'inventory save anchor'
Assert-Contains $be 'ContainerHelper.loadAllItems(tag, items, registries);' 'inventory load anchor'

Assert-Contains $menu 'addSlot(new AspectInputSlot(smelter, TCSmelterBlockEntity.SLOT_INPUT, 80, 8));' 'legacy input slot coordinate'
Assert-Contains $menu 'addSlot(new FuelSlot(smelter, TCSmelterBlockEntity.SLOT_FUEL, 80, 48));' 'legacy fuel slot coordinate'
Assert-Contains $menu 'blockEntity.furnaceCookTime()' 'cook progress DataSlot'
Assert-Contains $menu 'blockEntity.furnaceBurnTime()' 'burn time DataSlot'
Assert-Contains $menu 'blockEntity.currentItemBurnTime()' 'current item burn time DataSlot'
Assert-Contains $menu 'blockEntity.storedVis()' 'stored vis DataSlot'
Assert-Contains $menu 'blockEntity.smeltTime()' 'target smelt time DataSlot'
Assert-Regex $menu 'else if \(TCSmelterBlockEntity\.getBurnTime\(stack\) > 0\)[\s\S]*moveItemStackTo\(stack, SLOT_FUEL, SLOT_FUEL \+ 1, false\)' 'quick-move routes burnable stacks into fuel'
Assert-Regex $menu 'new AspectList\(stack\)\.size\(\) > 0[\s\S]*moveItemStackTo\(stack, SLOT_INPUT, SLOT_INPUT \+ 1, false\)' 'quick-move routes aspect-bearing stacks into input'
Assert-Contains $menu 'return !stack.isEmpty() && new AspectList(stack).size() > 0;' 'input menu slot placement guard'
Assert-Contains $menu 'return TCSmelterBlockEntity.getBurnTime(stack) > 0;' 'fuel menu slot placement guard'

Assert-Contains $caps 'Capabilities.ItemHandler.BLOCK' 'NeoForge item handler capability registration'
Assert-Contains $caps 'TCBlockEntities.SMELTER_BASIC.get()' 'basic smelter item capability'
Assert-Contains $caps 'TCBlockEntities.SMELTER_THAUMIUM.get()' 'thaumium smelter item capability'
Assert-Contains $caps 'TCBlockEntities.SMELTER_VOID.get()' 'void smelter item capability'
Assert-Contains $caps '(smelter, side) -> smelter.itemHandler(side)' 'capability delegates to sided smelter handler'

Assert-Contains $block 'protected ItemInteractionResult useItemOn' 'item interaction opens smelter menu'
Assert-Contains $block 'protected InteractionResult useWithoutItem' 'empty-hand interaction opens smelter menu'
Assert-Contains $block 'serverPlayer.openMenu(smelter);' 'menu opens server-side'
Assert-Contains $block 'smelter.dropContents(level, pos);' 'block removal drops smelter contents'

if ($essCaps -match 'SMELTER_(BASIC|THAUMIUM|VOID)') {
    throw '[tc-port] Smelter tiers must not be exposed through TCEssentiaCapabilities.BLOCK.'
}
$script:checks++

Write-Host "[tc-port] Smelter player/inventory boundary audit passed ($script:checks checks)."
