[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$LegacyManifestPath,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
if (-not (Test-Path -LiteralPath $LegacyManifestPath -PathType Leaf)) { throw "Legacy manifest not found: $LegacyManifestPath" }
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw "Port manifest not found: $PortManifestPath" }
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_behavior_boundary_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_behavior_boundary_report.md" }

function Read-RuleDocument([string]$Root, [string]$FileName) {
    if ([string]::IsNullOrWhiteSpace($Root)) { return [pscustomobject]@{ schemaVersion = 1; entries = @() } }
    $path = Join-Path $Root $FileName
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { return [pscustomobject]@{ schemaVersion = 1; entries = @() } }
    return Get-Content -Raw -LiteralPath $path | ConvertFrom-Json
}
function New-RenameLookup($Document) {
    $lookup = @{}
    foreach ($entry in @($Document.entries)) {
        if ($entry.kind -and $entry.legacyId -and $entry.portId) { $lookup["$($entry.kind):$($entry.legacyId)"] = $entry.portId }
    }
    return $lookup
}
function New-VariantLookup($Document) {
    $lookup = @{}
    foreach ($entry in @($Document.entries)) {
        $legacyId = if ($entry.legacyId) { $entry.legacyId } else { $entry.id }
        if ($entry.kind -and $legacyId -and $entry.variants) { $lookup["$($entry.kind):$legacyId"] = @($entry.variants | ForEach-Object { $_.portId } | Where-Object { $_ }) }
    }
    return $lookup
}
function Test-HasClue($Entry, [string]$Clue) {
    return @($Entry.behaviorClues) -contains $Clue
}
function Test-Truthy($Value) {
    if ($null -eq $Value) { return $false }
    if ($Value -is [bool]) { return $Value }
    return [string]$Value -eq "True"
}
function Get-TargetIds($Entry, $RenameLookup, $VariantLookup) {
    $key = "$($Entry.kind):$($Entry.registryId)"
    if ($VariantLookup.ContainsKey($key)) { return @($VariantLookup[$key] | Select-Object -Unique) }
    if ($RenameLookup.ContainsKey($key)) { return @($RenameLookup[$key]) }
    return @($Entry.registryId)
}
function Format-List($Values) {
    $items = @($Values | Where-Object { $_ } | Select-Object -Unique)
    if ($items.Count -eq 0) { return "<none>" }
    return $items -join ", "
}

$legacy = Get-Content -Raw -LiteralPath $LegacyManifestPath | ConvertFrom-Json
$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$renames = Read-RuleDocument $RulesRoot "known-renames.json"
$variants = Read-RuleDocument $RulesRoot "variant-mapping.json"
$renameLookup = New-RenameLookup $renames
$variantLookup = New-VariantLookup $variants

$portLookup = @{}
foreach ($entry in @($port.entries)) { $portLookup["$($entry.kind):$($entry.registryId)"] = $entry }

$selected = @($Checks | Where-Object { $_ -in @("blockentities", "capabilities", "menus") } | Select-Object -Unique)
if ($selected.Count -eq 0) { $selected = @("blockentities", "capabilities", "menus") }

$results = [System.Collections.Generic.List[object]]::new()
foreach ($legacyEntry in @($legacy.entries | Where-Object { $_.kind -eq "block" })) {
    $targetIds = @(Get-TargetIds $legacyEntry $renameLookup $variantLookup)
    $targetEntries = @(
        foreach ($targetId in $targetIds) {
            $key = "block:$targetId"
            if ($portLookup.ContainsKey($key)) { $portLookup[$key] }
        }
    )
    $targetMissing = @($targetIds | Where-Object { -not $portLookup.ContainsKey("block:$_") })

    foreach ($check in $selected) {
        $expected = $false
        $present = $false
        $legacyEvidence = ""
        $portEvidence = ""
        if ($check -eq "blockentities") {
            $expected = (Test-Truthy $legacyEntry.hasTileEntity) -or (Test-HasClue $legacyEntry "tile_entity")
            $present = @($targetEntries | Where-Object { (Test-Truthy $_.hasBlockEntity) -or @($_.blockEntities).Count -gt 0 }).Count -gt 0
            $legacyEvidence = "legacy hasTileEntity=$($legacyEntry.hasTileEntity); tileEntities=$(Format-List $legacyEntry.legacyTileEntities); clues=$(Format-List $legacyEntry.behaviorClues)"
            $portBlockEntitiesText = Format-List (@($targetEntries | ForEach-Object { $_.blockEntities }))
            $portBlockEntityClassesText = Format-List (@($targetEntries | ForEach-Object { $_.blockEntityClasses }))
            $portEvidence = "port blockEntities=$portBlockEntitiesText; classes=$portBlockEntityClassesText"
        } elseif ($check -eq "menus") {
            $expected = (Test-Truthy $legacyEntry.hasGui) -or (Test-HasClue $legacyEntry "gui")
            $present = @($targetEntries | Where-Object { (Test-Truthy $_.hasMenu) -or @($_.menus).Count -gt 0 }).Count -gt 0
            $legacyEvidence = "legacy hasGui=$($legacyEntry.hasGui); clues=$(Format-List $legacyEntry.behaviorClues)"
            $portMenusText = Format-List (@($targetEntries | ForEach-Object { $_.menus }))
            $portMenuClassesText = Format-List (@($targetEntries | ForEach-Object { $_.menuClasses }))
            $portEvidence = "port menus=$portMenusText; classes=$portMenuClassesText"
        } elseif ($check -eq "capabilities") {
            $expected = (Test-Truthy $legacyEntry.hasInventory) -or (Test-HasClue $legacyEntry "inventory")
            $present = @($targetEntries | Where-Object { (Test-Truthy $_.hasCapability) -or (Test-Truthy $_.hasInventory) }).Count -gt 0
            $legacyEvidence = "legacy hasInventory=$($legacyEntry.hasInventory); clues=$(Format-List $legacyEntry.behaviorClues)"
            $portCapabilityText = Format-List (@($targetEntries | ForEach-Object { $_.hasCapability }))
            $portInventoryText = Format-List (@($targetEntries | ForEach-Object { $_.hasInventory }))
            $portEvidence = "port hasCapability=$portCapabilityText; hasInventory=$portInventoryText"
        }

        if (-not $expected -and -not $present -and $targetMissing.Count -eq 0) { continue }

        $status = "BOUNDARY_MATCH"
        if ($targetMissing.Count -gt 0) {
            $status = "BOUNDARY_REVIEW_NEEDED"
            $portEvidence = "target port block ID(s) missing: $($targetMissing -join ', ')"
        } elseif ($expected -and -not $present) {
            $status = "BOUNDARY_GAP"
        } elseif (-not $expected -and $present) {
            $status = "BOUNDARY_REVIEW_NEEDED"
        }

        $results.Add([pscustomobject][ordered]@{
            check = $check
            legacyId = "thaumcraft:$($legacyEntry.registryId)"
            targetIds = @($targetIds | ForEach-Object { "thaumcraft:$_" })
            legacyClass = $legacyEntry.legacyClass
            status = $status
            legacyExpected = [bool]$expected
            portPresent = [bool]$present
            evidence = "$legacyEvidence; $portEvidence"
        })
    }
}

$orderedResults = @($results | Sort-Object check, status, legacyId)
$summaryByCheck = @($selected | ForEach-Object {
    $check = $_
    $rows = @($orderedResults | Where-Object check -eq $check)
    [pscustomobject][ordered]@{
        check = $check
        rows = $rows.Count
        matches = @($rows | Where-Object status -eq "BOUNDARY_MATCH").Count
        gaps = @($rows | Where-Object status -eq "BOUNDARY_GAP").Count
        reviewNeeded = @($rows | Where-Object status -eq "BOUNDARY_REVIEW_NEEDED").Count
    }
})
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    selectedChecks = @($selected)
    policy = "Report-only behavior-boundary scan. BOUNDARY_GAP and BOUNDARY_REVIEW_NEEDED are evidence for follow-up batches; they do not assert runtime parity or fail the build."
    summary = [ordered]@{
        rows = $orderedResults.Count
        matches = @($orderedResults | Where-Object status -eq "BOUNDARY_MATCH").Count
        gaps = @($orderedResults | Where-Object status -eq "BOUNDARY_GAP").Count
        reviewNeeded = @($orderedResults | Where-Object status -eq "BOUNDARY_REVIEW_NEEDED").Count
        byCheck = @($summaryByCheck)
    }
    results = $orderedResults
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
$report | ConvertTo-Json -Depth 14 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block behavior boundary report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Check | Rows | MATCH | GAP | REVIEW_NEEDED |")
$lines.Add("|---|---:|---:|---:|---:|")
foreach ($row in $summaryByCheck) { $lines.Add("| $($row.check) | $($row.rows) | $($row.matches) | $($row.gaps) | $($row.reviewNeeded) |") }
$lines.Add("")
$lines.Add("## Gaps and review-needed rows")
$lines.Add("")
$lines.Add("| Check | Legacy ID | Target ID(s) | Status | Evidence |")
$lines.Add("|---|---|---|---|---|")
foreach ($row in $orderedResults | Where-Object { $_.status -ne "BOUNDARY_MATCH" }) {
    $safeEvidence = $row.evidence.Replace("|", "\|")
    $targets = @($row.targetIds) -join ", "
    $lines.Add("| $($row.check) | ``$($row.legacyId)`` | ``$targets`` | $($row.status) | $safeEvidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Behavior boundary report: $OutputMarkdown"
Write-Output "Rows=$($report.summary.rows), matches=$($report.summary.matches), gaps=$($report.summary.gaps), reviewNeeded=$($report.summary.reviewNeeded)"