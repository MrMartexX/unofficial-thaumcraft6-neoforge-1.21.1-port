[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [string]$PortRoot = "05_neoforge_port",
    [Parameter(Mandatory = $true)][string]$LegacyManifestPath,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [Parameter(Mandatory = $true)][string]$OutputLegacyManifest,
    [Parameter(Mandatory = $true)][string]$OutputPortManifest,
    [string[]]$Ids,
    [string[]]$IdPrefix,
    [string[]]$Families,
    [string[]]$Packages,
    [switch]$ChangedOnly,
    [string]$SinceCommit,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
$portPath = Join-Path $RepoRoot $PortRoot
if (-not (Test-Path -LiteralPath $LegacyManifestPath -PathType Leaf)) { throw "Legacy manifest not found: $LegacyManifestPath" }
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw "Port manifest not found: $PortManifestPath" }
if (-not (Test-Path -LiteralPath $portPath -PathType Container)) { throw "Port root not found: $portPath" }
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_focus_filter_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_focus_filter_report.md" }

function Normalize-Id([object]$Value) {
    if ($null -eq $Value) { return "" }
    $s = ([string]$Value).Trim().ToLowerInvariant()
    if ($s.StartsWith("thaumcraft:")) { $s = $s.Substring("thaumcraft:".Length) }
    return $s
}
function Get-EntryId($Entry) {
    foreach ($name in @("registryId", "id", "name", "registryName", "legacyId")) {
        $prop = $Entry.PSObject.Properties[$name]
        if ($null -ne $prop -and -not [string]::IsNullOrWhiteSpace([string]$prop.Value)) { return Normalize-Id $prop.Value }
    }
    return ""
}
function Get-EntrySearchText($Entry) {
    $values = [System.Collections.Generic.List[string]]::new()
    foreach ($name in @("registryId", "id", "symbol", "declaredClass", "declaredType", "portPackage", "portExtends", "sourceFile", "portClassFile")) {
        $prop = $Entry.PSObject.Properties[$name]
        if ($null -ne $prop -and $null -ne $prop.Value) { $values.Add([string]$prop.Value) }
    }
    foreach ($name in @("portImplements", "behaviorClues", "methodPresence", "blockEntities", "blockEntityClasses", "menus", "menuClasses")) {
        $prop = $Entry.PSObject.Properties[$name]
        if ($null -ne $prop -and $null -ne $prop.Value) { foreach ($v in @($prop.Value)) { if ($v) { $values.Add([string]$v) } } }
    }
    return ($values -join " `n").ToLowerInvariant()
}
function Add-SelectedId([System.Collections.Generic.HashSet[string]]$Set, [object]$Value) {
    $id = Normalize-Id $Value
    if (-not [string]::IsNullOrWhiteSpace($id)) { [void]$Set.Add($id) }
}
function Test-AnyPrefix([string]$Id, [string[]]$Prefixes) {
    foreach ($prefix in @($Prefixes)) {
        $p = Normalize-Id $prefix
        if (-not [string]::IsNullOrWhiteSpace($p) -and $Id.StartsWith($p)) { return $true }
    }
    return $false
}
function Test-AnyContains([string]$Text, [string[]]$Needles) {
    foreach ($needle in @($Needles)) {
        if (-not [string]::IsNullOrWhiteSpace($needle) -and $Text.Contains(([string]$needle).ToLowerInvariant())) { return $true }
    }
    return $false
}
function ConvertTo-RelativeRepoPath([string]$FullPath) {
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\\", "/")
}
function Get-ChangedFiles([string]$BaseCommit) {
    $base = if ([string]::IsNullOrWhiteSpace($BaseCommit)) { "HEAD~1" } else { $BaseCommit }
    $args = @("diff", "--name-only", "$base..HEAD", "--", $PortRoot)
    $output = @(& git @args 2>$null | ForEach-Object { [string]$_ })
    if ($LASTEXITCODE -ne 0 -or $output.Count -eq 0) {
        $output = @(& git diff --name-only HEAD~1..HEAD -- $PortRoot 2>$null | ForEach-Object { [string]$_ })
    }
    return @($output | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | ForEach-Object { $_.Replace("\\", "/") } | Sort-Object -Unique)
}
function Test-ChangedEntry($Entry, [string[]]$ChangedFiles) {
    $id = Get-EntryId $Entry
    if ([string]::IsNullOrWhiteSpace($id)) { return $false }
    $paths = [System.Collections.Generic.List[string]]::new()
    foreach ($name in @("sourceFile", "portClassFile")) {
        $prop = $Entry.PSObject.Properties[$name]
        if ($null -ne $prop -and $prop.Value) { $paths.Add((Join-Path $PortRoot ([string]$prop.Value)).Replace("\\", "/")) }
    }
    $assetNeedles = @(
        "/blockstates/$id.json",
        "/models/item/$id.json",
        "/loot_table/blocks/$id.json",
        "/loot_tables/blocks/$id.json",
        "/recipes/$id.json",
        "/tags/blocks/$id.json",
        "/tags/items/$id.json",
        "/$id.png",
        "$id.json"
    )
    foreach ($changed in @($ChangedFiles)) {
        foreach ($path in @($paths)) { if ($changed -eq $path) { return $true } }
        foreach ($needle in $assetNeedles) { if ($changed.Contains($needle)) { return $true } }
    }
    return $false
}
function Copy-ManifestWithFilteredEntries($Manifest, [object[]]$Entries, [string]$OutputPath, [string[]]$SelectedIds, [bool]$KeepAllIfSchemaUnknown) {
    $copy = $Manifest | ConvertTo-Json -Depth 50 | ConvertFrom-Json
    $entryProps = @("entries", "items", "blocks")
    foreach ($propName in $entryProps) {
        $prop = $copy.PSObject.Properties[$propName]
        if ($null -eq $prop -or $null -eq $prop.Value) { continue }
        $sourceEntries = @($prop.Value)
        $filtered = @($sourceEntries | Where-Object {
            $id = Get-EntryId $_
            if ([string]::IsNullOrWhiteSpace($id)) { return $KeepAllIfSchemaUnknown }
            return $id -in $SelectedIds
        })
        $copy.$propName = @($filtered)
    }
    if (-not $copy.PSObject.Properties["focusFilter"]) {
        $copy | Add-Member -NotePropertyName focusFilter -NotePropertyValue ([pscustomobject]@{}) -Force
    }
    $copy.focusFilter = [pscustomobject][ordered]@{
        schemaVersion = 1
        generatedAtUtc = [DateTime]::UtcNow.ToString("o")
        selectedIds = @($SelectedIds)
        originalPortEntries = @($Entries).Count
        note = "Filtered manifest generated by focused_filter.ps1. Raw full manifests remain in tools/reports/local/item-block-parity."
    }
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputPath) | Out-Null
    $copy | ConvertTo-Json -Depth 50 | Set-Content -LiteralPath $OutputPath -Encoding utf8NoBOM
}

$portManifest = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$legacyManifest = Get-Content -Raw -LiteralPath $LegacyManifestPath | ConvertFrom-Json
$portEntries = @($portManifest.entries)
$selected = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
$reasons = [System.Collections.Generic.List[object]]::new()

foreach ($id in @($Ids)) {
    $n = Normalize-Id $id
    if (-not [string]::IsNullOrWhiteSpace($n)) {
        Add-SelectedId $selected $n
        $reasons.Add([pscustomobject][ordered]@{ id = $n; reason = "explicit_id" })
    }
}
foreach ($entry in $portEntries) {
    $id = Get-EntryId $entry
    if ([string]::IsNullOrWhiteSpace($id)) { continue }
    $search = Get-EntrySearchText $entry
    if ($IdPrefix -and (Test-AnyPrefix $id $IdPrefix)) { Add-SelectedId $selected $id; $reasons.Add([pscustomobject][ordered]@{ id = $id; reason = "id_prefix" }) }
    if ($Families -and (Test-AnyContains $search $Families)) { Add-SelectedId $selected $id; $reasons.Add([pscustomobject][ordered]@{ id = $id; reason = "family" }) }
    if ($Packages -and (Test-AnyContains $search $Packages)) { Add-SelectedId $selected $id; $reasons.Add([pscustomobject][ordered]@{ id = $id; reason = "package" }) }
}
$changedFiles = @()
if ($ChangedOnly -or -not [string]::IsNullOrWhiteSpace($SinceCommit)) {
    $changedFiles = @(Get-ChangedFiles $SinceCommit)
    foreach ($entry in $portEntries) {
        $id = Get-EntryId $entry
        if ([string]::IsNullOrWhiteSpace($id)) { continue }
        if (Test-ChangedEntry $entry $changedFiles) { Add-SelectedId $selected $id; $reasons.Add([pscustomobject][ordered]@{ id = $id; reason = "changed_file" }) }
    }
}

$selectedIds = @($selected | Sort-Object)
$filterRequested = [bool]($Ids -or $IdPrefix -or $Families -or $Packages -or $ChangedOnly -or -not [string]::IsNullOrWhiteSpace($SinceCommit))
if (-not $filterRequested -or $selectedIds.Count -eq 0) {
    Copy-Item -LiteralPath $PortManifestPath -Destination $OutputPortManifest -Force
    Copy-Item -LiteralPath $LegacyManifestPath -Destination $OutputLegacyManifest -Force
    $mode = if ($filterRequested) { "no_filter_matches_using_full_manifests" } else { "no_filter_requested" }
} else {
    Copy-ManifestWithFilteredEntries $portManifest $portEntries $OutputPortManifest $selectedIds $false
    Copy-ManifestWithFilteredEntries $legacyManifest @($legacyManifest.entries) $OutputLegacyManifest $selectedIds $true
    $mode = "filtered"
}

$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    mode = $mode
    selectedIds = @($selectedIds)
    selectedCount = $selectedIds.Count
    filters = [ordered]@{
        ids = @($Ids)
        idPrefix = @($IdPrefix)
        families = @($Families)
        packages = @($Packages)
        changedOnly = [bool]$ChangedOnly
        sinceCommit = $SinceCommit
        changedFiles = @($changedFiles)
    }
    reasons = @($reasons | Sort-Object id, reason -Unique)
    output = [ordered]@{
        portManifest = ConvertTo-RelativeRepoPath $OutputPortManifest
        legacyManifest = ConvertTo-RelativeRepoPath $OutputLegacyManifest
    }
    note = "If filters match no IDs, full manifests are used to avoid silently producing empty audits. Use explicit -Ids with known registry IDs for precise slicing."
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block focused filter report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Mode: ``$($report.mode)``")
$lines.Add("")
$lines.Add("Selected IDs: **$($report.selectedCount)**")
$lines.Add("")
$lines.Add("## Selected IDs")
$lines.Add("")
if ($selectedIds.Count -eq 0) { $lines.Add("<none>") } else { foreach ($id in $selectedIds) { $lines.Add("- ``thaumcraft:$id``") } }
$lines.Add("")
$lines.Add("## Changed files")
$lines.Add("")
if ($changedFiles.Count -eq 0) { $lines.Add("<none>") } else { foreach ($file in $changedFiles) { $lines.Add("- ``$file``") } }
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Focused filter report: $OutputMarkdown"
Write-Output "Mode=$mode, selectedIds=$($selectedIds.Count), changedFiles=$($changedFiles.Count)"
