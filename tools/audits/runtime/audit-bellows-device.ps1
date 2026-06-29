[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/runtime/bellows_device_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/runtime/bellows_device_report.md" }

function Read-TextOrEmpty([string]$RelativePath) {
    $path = Join-Path $RepoRoot $RelativePath
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { return "" }
    return Get-Content -Raw -LiteralPath $path
}

function Add-Row($Rows, [string]$Area, [string]$Status, [string]$Severity, [string]$Evidence, $Details) {
    $Rows.Add([pscustomobject][ordered]@{
        area = $Area
        status = $Status
        severity = $Severity
        evidence = $Evidence
        details = $Details
    })
}

function Test-ContainsAll([string]$Text, [string[]]$Tokens) {
    foreach ($token in $Tokens) {
        if ($Text -notlike "*$token*") { return $false }
    }
    return $true
}

$rows = [System.Collections.Generic.List[object]]::new()
$pass = "BELLOWS_DEVICE_PASS"
$review = "BELLOWS_DEVICE_REVIEW_NEEDED"
$errorStatus = "BELLOWS_DEVICE_ERROR"

$blockPath = "05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia/TCBellowsBlock.java"
$bePath = "05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCBellowsBlockEntity.java"
$furnaceAccessorPath = "05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCVanillaFurnaceBellowsAccessor.java"
$registryPath = "05_neoforge_port/src/main/java/thaumcraft/common/registry/TCBlockEntities.java"
$rendererRegistryPath = "05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java"
$rendererPath = "05_neoforge_port/src/main/java/thaumcraft/client/renderer/TCBellowsRenderer.java"
$smelterPath = "05_neoforge_port/src/main/java/thaumcraft/common/tiles/essentia/TCSmelterBlockEntity.java"
$tubePath = "05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport/blockentity/TCLegacyTubeBlockEntity.java"
$designPath = "06_docs/gameplay/bellows_device_design.md"

$blockText = Read-TextOrEmpty $blockPath
$beText = Read-TextOrEmpty $bePath
$furnaceAccessorText = Read-TextOrEmpty $furnaceAccessorPath
$registryText = Read-TextOrEmpty $registryPath
$rendererRegistryText = Read-TextOrEmpty $rendererRegistryPath
$rendererText = Read-TextOrEmpty $rendererPath
$smelterText = Read-TextOrEmpty $smelterPath
$tubeText = Read-TextOrEmpty $tubePath
$designText = Read-TextOrEmpty $designPath

if (Test-ContainsAll $blockText @("implements EntityBlock", "new TCBellowsBlockEntity", "TCBellowsBlockEntity.serverTick", "TCBellowsBlockEntity.clientTick", "RenderShape.ENTITYBLOCK_ANIMATED", "TCBlockEntities.BELLOWS")) {
    Add-Row $rows "block_wiring" $pass "info" "Bellows block owns BlockEntity tickers and renders through the animated tile path." @{ path = $blockPath }
} else {
    Add-Row $rows "block_wiring" $errorStatus "error" "Bellows block is missing EntityBlock/ticker/animated-render wiring." @{ path = $blockPath }
}

if (Test-ContainsAll $registryText @("BlockEntityType<TCBellowsBlockEntity>> BELLOWS", 'BLOCK_ENTITY_TYPES.register("bellows"', "TCBlocks.BELLOWS")) {
    Add-Row $rows "registry_wiring" $pass "info" "Bellows BlockEntityType is registered against the Bellows block." @{ path = $registryPath }
} else {
    Add-Row $rows "registry_wiring" $errorStatus "error" "Bellows BlockEntityType registration is missing." @{ path = $registryPath }
}

if (Test-ContainsAll $rendererRegistryText @("TCBlockEntities.BELLOWS.get()", "TCBellowsRenderer::new")) {
    Add-Row $rows "renderer_registration" $pass "info" "Bellows BlockEntityRenderer is registered on the client event bus." @{ path = $rendererRegistryPath }
} else {
    Add-Row $rows "renderer_registration" $errorStatus "error" "Bellows BlockEntityRenderer registration is missing." @{ path = $rendererRegistryPath }
}

if (Test-ContainsAll $beText @("class TCBellowsBlockEntity extends BlockEntity", "clientTick", "tickClient", "inflation", "firstClientRun", "resolveTargetKind", '"smelter"', '"tube_buffer"', '"vanilla_furnace"', "saveAdditional", "loadAdditional")) {
    Add-Row $rows "device_state" $pass "info" "Bellows device state owns persisted target counters and the legacy client inflation cycle." @{ path = $bePath }
} else {
    Add-Row $rows "device_state" $errorStatus "error" "Bellows device state is incomplete." @{ path = $bePath }
}

if (Test-ContainsAll $furnaceAccessorText @("AbstractFurnaceBlockEntity", '"cookingProgress"', '"cookingTotalTime"', "boostCookProgress", "setAccessible(true)")) {
    Add-Row $rows "vanilla_furnace_accessor" $pass "info" "Vanilla furnace cook-time boost is isolated behind a single reflected accessor." @{ path = $furnaceAccessorPath }
} else {
    Add-Row $rows "vanilla_furnace_accessor" $errorStatus "error" "Vanilla furnace cook-time boost accessor is missing or not isolated." @{ path = $furnaceAccessorPath }
}

if (Test-ContainsAll $beText @("boostVanillaFurnace", "TCVanillaFurnaceBellowsAccessor.boostCookProgress", "serverDelay >= 2", "vanillaFurnaceBoosts++")) {
    Add-Row $rows "vanilla_furnace_consumer" $pass "info" "Bellows applies the legacy every-two-ticks vanilla furnace progress boost." @{ path = $bePath }
} else {
    Add-Row $rows "vanilla_furnace_consumer" $errorStatus "error" "Bellows vanilla furnace boost path is missing." @{ path = $bePath }
}

if (Test-ContainsAll $smelterText @("refreshBellows", "TCBlocks.BELLOWS", "TCBellowsBlock.ENABLED", "TCBellowsBlock.FACING", "smeltTimeForVis")) {
    Add-Row $rows "smelter_consumer" $pass "info" "Smelter consumes enabled attached Bellows for smelt-time calculation." @{ path = $smelterPath }
} else {
    Add-Row $rows "smelter_consumer" $errorStatus "error" "Smelter Bellows consumer boundary is missing." @{ path = $smelterPath }
}

if (Test-ContainsAll $tubeText @("refreshBellowsPressure", "TCBlocks.BELLOWS", "TCBellowsBlock.ENABLED", "bellows * 32", "setBellowsPressure")) {
    Add-Row $rows "tube_buffer_consumer" $pass "info" "Tube buffer derives suction pressure from enabled attached Bellows." @{ path = $tubePath }
} else {
    Add-Row $rows "tube_buffer_consumer" $errorStatus "error" "Tube buffer Bellows pressure boundary is missing." @{ path = $tubePath }
}

if (Test-ContainsAll $rendererText @("class TCBellowsRenderer", "textures/blocks/bellows.png", "textures/models/bore.png", "hasTubeBufferExtension", "translateFromOrientation", "createBellowsModel", "createBoreNozzleModel")) {
    Add-Row $rows "client_renderer" $pass "info" "Bellows renderer owns the legacy model texture, orientation transform, inflation parts and tube-buffer extension." @{ path = $rendererPath }
} else {
    Add-Row $rows "client_renderer" $errorStatus "error" "Bellows client renderer is missing legacy model/extension coverage." @{ path = $rendererPath }
}

if (Test-ContainsAll $designText @("Bellows device boundary", "vanilla_furnace", "tube_buffer", "client animation")) {
    Add-Row $rows "design_doc" $pass "info" "Bellows device design note exists and records scope boundaries." @{ path = $designPath }
} else {
    Add-Row $rows "design_doc" $review "review" "Bellows device design note is missing or incomplete." @{ path = $designPath }
}

$commonClientLeaks = @()
foreach ($relativePath in @($blockPath, $bePath, $furnaceAccessorPath, $registryPath, $smelterPath, $tubePath)) {
    $text = Read-TextOrEmpty $relativePath
    if ($text -match "import\s+net\.minecraft\.client\.") {
        $commonClientLeaks += $relativePath
    }
}
if ($commonClientLeaks.Count -eq 0) {
    Add-Row $rows "client_server_safety" $pass "info" "Bellows server/common boundary has no net.minecraft.client imports." @{ filesChecked = 6 }
} else {
    Add-Row $rows "client_server_safety" $errorStatus "error" "Client imports leaked into Bellows common/server boundary." @{ files = @($commonClientLeaks) }
}

$orderedRows = @($rows)
$summary = [ordered]@{
    rows = $orderedRows.Count
    pass = @($orderedRows | Where-Object status -eq $pass).Count
    reviewNeeded = @($orderedRows | Where-Object status -eq $review).Count
    errors = @($orderedRows | Where-Object status -eq $errorStatus).Count
}
$output = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "Bellows device boundary audit. This validates wiring, legacy behavior ownership and renderer coverage, not final pixel-level screenshot parity."
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$output | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Bellows device report")
$lines.Add("")
$lines.Add("Generated: $($output.generatedAtUtc)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | PASS | REVIEW | ERRORS |")
$lines.Add("|---:|---:|---:|---:|")
$lines.Add("| $($summary.rows) | $($summary.pass) | $($summary.reviewNeeded) | $($summary.errors) |")
$lines.Add("")
$lines.Add("## Non-pass rows")
$lines.Add("")
$lines.Add("| Status | Area | Severity | Evidence |")
$lines.Add("|---|---|---|---|")
$nonPass = @($orderedRows | Where-Object { $_.status -ne $pass })
foreach ($row in $nonPass) {
    $lines.Add("| $($row.status) | $($row.area) | $($row.severity) | $($row.evidence.Replace('|', '\|')) |")
}
if ($nonPass.Count -eq 0) {
    $lines.Add("| BELLOWS_DEVICE_PASS | <all> | info | Every Bellows device row passed. |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Bellows device report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors)"
if ($summary.errors -gt 0) {
    exit 1
}
