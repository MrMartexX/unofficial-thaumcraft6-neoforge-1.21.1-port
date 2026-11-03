[CmdletBinding()]
param(
    [string]$RepoRoot,
    [string]$OutputPath,
    [switch]$FailOnIntegrityError
)
$ErrorActionPreference = "Stop"
if (-not $RepoRoot) { $RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "../../..")).Path }
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path
$port = Join-Path $RepoRoot "05_neoforge_port"
if (-not (Test-Path -LiteralPath $port -PathType Container)) { throw "Missing port: $port" }
if (-not $OutputPath) {
    $OutputPath = Join-Path $RepoRoot "tools/reports/local/visual-parity/v0-active-registry.json"
}

function Source([string]$Relative) {
    $path = Join-Path $port $Relative
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { throw "Missing: $Relative" }
    return [System.IO.File]::ReadAllText($path)
}
function SourceLine([string]$Text, [int]$Index) {
    return 1 + [regex]::Matches($Text.Substring(0, $Index), "\r?\n").Count
}
function FileSha([string]$Relative) {
    return (Get-FileHash -LiteralPath (Join-Path $port $Relative) -Algorithm SHA256).Hash.ToLowerInvariant()
}

$specs = @(
    @("items", "src/main/java/thaumcraft/common/registry/TCItems.java", "ITEMS\.register|simpleItem|blockItem|spawnEgg"),
    @("blocks", "src/main/java/thaumcraft/common/registry/TCBlocks.java", "BLOCKS\.register"),
    @("entity_types", "src/main/java/thaumcraft/common/registry/TCEntityTypes.java", "ENTITY_TYPES\.register"),
    @("block_entity_types", "src/main/java/thaumcraft/common/registry/TCBlockEntities.java", "BLOCK_ENTITY_TYPES\.register"),
    @("menus", "src/main/java/thaumcraft/common/registry/TCMenus.java", "MENUS\.register"),
    @("creative_tabs", "src/main/java/thaumcraft/common/registry/TCCreativeTabs.java", "CREATIVE_MODE_TABS\.register")
)
$categories = [ordered]@{}
$bindings = [ordered]@{}
$issues = [System.Collections.Generic.List[string]]::new()
$summary = [ordered]@{}

foreach ($spec in $specs) {
    $name = $spec[0]; $relative = $spec[1]; $source = Source $relative
    $pattern = '\b(?<symbol>[A-Z][A-Z0-9_]*)\s*=\s*(?<method>' + $spec[2] + ')\s*\(\s*"(?<id>[a-z0-9_]+)"'
    $rows = @([regex]::Matches($source, $pattern) | ForEach-Object {
        [pscustomobject][ordered]@{
            id = ("thaumcraft:" + $_.Groups["id"].Value)
            symbol = $_.Groups["symbol"].Value
            declared_with = $_.Groups["method"].Value
            line = SourceLine $source $_.Index
        }
    })
    if ((@($rows | Select-Object -ExpandProperty id -Unique)).Count -ne $rows.Count) {
        $issues.Add("Duplicate $name IDs")
    }
    if ((@($rows | Select-Object -ExpandProperty symbol -Unique)).Count -ne $rows.Count) {
        $issues.Add("Duplicate $name symbols")
    }
    $categories[$name] = [ordered]@{
        source = ("05_neoforge_port/" + $relative)
        source_sha256 = FileSha $relative
        count = $rows.Count
        default_review_status = "NOT_CHECKED"
        rows = $rows
    }
    $summary[$name] = $rows.Count
}

$bindingSpecs = @(
    @("entity_renderers", "entity_types", "src/main/java/thaumcraft/client/renderer/TCEntityRenderers.java", 'registerEntityRenderer\(\s*TCEntityTypes\.(?<symbol>[A-Z][A-Z0-9_]*)\.get\(\)\s*,\s*(?<provider>.*?)\);'),
    @("block_entity_renderers", "block_entity_types", "src/main/java/thaumcraft/client/renderer/TCBlockEntityRenderers.java", 'registerBlockEntityRenderer\(\s*TCBlockEntities\.(?<symbol>[A-Z][A-Z0-9_]*)\.get\(\)\s*,\s*(?<provider>.*?)\);'),
    @("menu_screens", "menus", "src/main/java/thaumcraft/client/gui/TCMenuScreens.java", 'event\.register\(\s*TCMenus\.(?<symbol>[A-Z][A-Z0-9_]*)\.get\(\)\s*,\s*(?<provider>.*?)\);')
)
foreach ($spec in $bindingSpecs) {
    $name = $spec[0]; $registry = $spec[1]; $relative = $spec[2]
    $source = Source $relative
    $idBySymbol = @{}
    foreach ($row in $categories[$registry].rows) { $idBySymbol[$row.symbol] = $row.id }
    $rows = @([regex]::Matches($source, ("(?s)" + $spec[3])) | ForEach-Object {
        $symbol = $_.Groups["symbol"].Value
        $id = $null
        if ($idBySymbol.ContainsKey($symbol)) { $id = $idBySymbol[$symbol] }
        [pscustomobject][ordered]@{
            registry_symbol = $symbol
            id = $id
            provider = ([regex]::Replace($_.Groups["provider"].Value.Trim(), '\s+', ' '))
            line = SourceLine $source $_.Index
        }
    })
    $unresolved = @($rows | Where-Object { $null -eq $_.id })
    if ($unresolved.Count -gt 0) { $issues.Add("$name has $($unresolved.Count) unresolved registry references") }
    $duplicates = @($rows | Group-Object registry_symbol | Where-Object Count -gt 1)
    if ($duplicates.Count -gt 0) { $issues.Add("$name contains repeated bindings") }
    $bindings[$name] = [ordered]@{
        source = ("05_neoforge_port/" + $relative)
        source_sha256 = FileSha $relative
        count = $rows.Count
        unresolved_registry_fields = $unresolved.Count
        rows = $rows
    }
    $summary[$name] = $rows.Count
}

$entityFields = @($bindings.entity_renderers.rows | Select-Object -ExpandProperty registry_symbol)
$screenFields = @($bindings.menu_screens.rows | Select-Object -ExpandProperty registry_symbol)
$berFields = @($bindings.block_entity_renderers.rows | Select-Object -ExpandProperty registry_symbol)
$entitiesWithoutRenderer = @($categories.entity_types.rows | Where-Object { $_.symbol -notin $entityFields } | Select-Object -ExpandProperty id)
$menusWithoutScreen = @($categories.menus.rows | Where-Object { $_.symbol -notin $screenFields } | Select-Object -ExpandProperty id)
$beWithoutBer = @($categories.block_entity_types.rows | Where-Object { $_.symbol -notin $berFields } | Select-Object -ExpandProperty id)
if ($entitiesWithoutRenderer.Count -gt 0) { $issues.Add("Entity types without renderer: $($entitiesWithoutRenderer.Count)") }
if ($menusWithoutScreen.Count -gt 0) { $issues.Add("Menus without screen: $($menusWithoutScreen.Count)") }

$assets = Join-Path $port "src/main/resources/assets/thaumcraft"
$assetRows = @()
if (Test-Path -LiteralPath $assets -PathType Container) {
    $assetRows = @(Get-ChildItem -LiteralPath $assets -Recurse -File | ForEach-Object {
        $relative = [System.IO.Path]::GetRelativePath($assets, $_.FullName).Replace('\', '/')
        $family = "other"
        foreach ($prefix in @("models/item/", "models/block/", "blockstates/", "textures/", "lang/", "particles/", "shaders/", "sounds/")) {
            if ($relative.StartsWith($prefix, [StringComparison]::Ordinal)) {
                $family = $prefix.TrimEnd('/'); break
            }
        }
        [pscustomobject][ordered]@{
            path = ("05_neoforge_port/src/main/resources/assets/thaumcraft/" + $relative)
            family = $family
            bytes = $_.Length
        }
    } | Sort-Object path)
} else { $issues.Add("Missing assets/thaumcraft directory") }
$assetSummary = [ordered]@{}
foreach ($group in ($assetRows | Group-Object family | Sort-Object Name)) { $assetSummary[$group.Name] = $group.Count }
$summary.resources = $assetRows.Count

$head = $null
try {
    $candidate = (& git -C $RepoRoot rev-parse HEAD 2>$null)
    if ($LASTEXITCODE -eq 0 -and $candidate -match '^[0-9a-fA-F]{40}$') { $head = $candidate.ToLowerInvariant() }
} catch {}

$report = [ordered]@{
    schema_version = "v0-source-snapshot-1"
    generated_at_utc = [DateTime]::UtcNow.ToString("o")
    commit_sha = $head
    source = "local source/static extraction; NOT a runtime registry dump"
    categories = $categories
    bindings = $bindings
    resources = [ordered]@{ scope = "resource presence, NOT proof of active use"; summary = $assetSummary; rows = $assetRows }
    integrity = [ordered]@{
        issues = @($issues)
        entity_types_without_renderer = $entitiesWithoutRenderer
        menus_without_screen = $menusWithoutScreen
        block_entity_types_without_ber = $beWithoutBer
    }
    deferred_scope = @(
        "bidirectional legacy mapping",
        "dynamic ItemStack/Creative variants and display order",
        "model parent chains, tint, overrides and all rendering states",
        "standalone GUIs/HUD, particles, FX and original-JAR parity",
        "paired client screenshots and multiplayer behavior"
    )
    summary = $summary
}
$directory = Split-Path -Parent $OutputPath
New-Item -Path $directory -ItemType Directory -Force | Out-Null
$report | ConvertTo-Json -Depth 20 | Set-Content -LiteralPath $OutputPath -Encoding utf8NoBOM
Write-Output "V0 inventory: $OutputPath"
Write-Output "Items=$($summary.items), blocks=$($summary.blocks), entities=$($summary.entity_types), block entities=$($summary.block_entity_types), menus=$($summary.menus), entity renderers=$($summary.entity_renderers), BERs=$($summary.block_entity_renderers), menu screens=$($summary.menu_screens), assets=$($summary.resources)"
Write-Output "Integrity issues=$($issues.Count); block entities without BER are NOT automatically errors."
if ($FailOnIntegrityError -and $issues.Count -gt 0) { throw "V0 integrity problems: $($issues.Count)" }
