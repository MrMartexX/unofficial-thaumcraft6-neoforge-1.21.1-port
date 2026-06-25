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
# Batch 32 dependency-aware focus expansion start
function Read-FocusedDependencyRules {
    param([string]$RulesRoot)
    $rulesPath = Join-Path $RulesRoot "focused-dependency-rules.json"
    if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
        return Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
    }
    return [pscustomobject]@{
        scanRoots = @(
            "src/main/resources/data/thaumcraft/recipes",
            "src/main/resources/data/thaumcraft/tags",
            "src/main/resources/data/thaumcraft/loot_tables",
            "src/main/resources/data/thaumcraft/loot_table",
            "src/main/resources/assets/thaumcraft/blockstates",
            "src/main/resources/assets/thaumcraft/models"
        )
        idReferencePattern = "thaumcraft:([a-z0-9_./-]+)"
        maxScanFiles = 4000
    }
}
function Add-FocusDependencyReason {
    param(
        [ref]$Reasons,
        [string]$Id,
        [string]$Reason,
        [string]$Evidence
    )
    if ([string]::IsNullOrWhiteSpace($Id)) { return }
    $Reasons.Value += [pscustomobject][ordered]@{
        id = $Id
        reason = $Reason
        evidence = $Evidence
    }
}
function Add-FocusDependencyId {
    param(
        [System.Collections.Generic.HashSet[string]]$SelectedSet,
        [System.Collections.Generic.HashSet[string]]$SeedSet,
        [string]$Id,
        [string]$Reason,
        [string]$Evidence,
        [ref]$Reasons
    )
    $normalized = Normalize-Id $Id
    if ([string]::IsNullOrWhiteSpace($normalized)) { return }
    if ($SeedSet.Contains($normalized)) { return }
    if ($SelectedSet.Add($normalized)) {
        Add-FocusDependencyReason -Reasons $Reasons -Id $normalized -Reason $Reason -Evidence $Evidence
    }
}
function Test-TextMentionsFocusId {
    param([string]$Text, [string]$Id)
    if ([string]::IsNullOrWhiteSpace($Text) -or [string]::IsNullOrWhiteSpace($Id)) { return $false }
    $normalizedText = $Text.ToLowerInvariant()
    $normalizedId = Normalize-Id $Id
    if ($normalizedText.Contains('thaumcraft:' + $normalizedId)) { return $true }
    if ($normalizedText.Contains('/' + $normalizedId + '.')) { return $true }
    if ($normalizedText.Contains('/' + $normalizedId + '/')) { return $true }
    if ($normalizedText.Contains('"' + $normalizedId + '"')) { return $true }
    return $false
}
function Get-KnownManifestIdMap {
    param([object[]]$Entries)
    $map = @{}
    foreach ($entry in @($Entries)) {
        $id = Get-EntryId $entry
        if (-not [string]::IsNullOrWhiteSpace($id) -and -not $map.ContainsKey($id)) { $map[$id] = $entry }
    }
    return $map
}
function Get-FocusDependencyExpansion {
    param(
        [object[]]$Entries,
        [string[]]$SeedIds,
        [string]$PortPath,
        [string]$RulesRoot
    )
    $rules = Read-FocusedDependencyRules -RulesRoot $RulesRoot
    $knownIds = Get-KnownManifestIdMap -Entries $Entries
    $selectedSet = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
    $seedSet = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
    $dependencyReasons = @()

    foreach ($seed in @($SeedIds)) {
        $normalized = Normalize-Id $seed
        if ([string]::IsNullOrWhiteSpace($normalized)) { continue }
        [void]$selectedSet.Add($normalized)
        [void]$seedSet.Add($normalized)
    }

    foreach ($seed in @($seedSet | Sort-Object)) {
        if (-not $knownIds.ContainsKey($seed)) { continue }
        $seedText = Get-EntrySearchText $knownIds[$seed]
        foreach ($candidate in @($knownIds.Keys | Sort-Object)) {
            if ($candidate -eq $seed) { continue }
            if (Test-TextMentionsFocusId -Text $seedText -Id $candidate) {
                Add-FocusDependencyId -SelectedSet $selectedSet -SeedSet $seedSet -Id $candidate -Reason 'seed_manifest_reference' -Evidence ("seed entry mentions " + $candidate) -Reasons ([ref]$dependencyReasons)
            }
            $candidateText = Get-EntrySearchText $knownIds[$candidate]
            if (Test-TextMentionsFocusId -Text $candidateText -Id $seed) {
                Add-FocusDependencyId -SelectedSet $selectedSet -SeedSet $seedSet -Id $candidate -Reason 'reverse_manifest_reference' -Evidence ("manifest entry mentions " + $seed) -Reasons ([ref]$dependencyReasons)
            }
        }
    }

    $pattern = if ($rules.idReferencePattern) { [string]$rules.idReferencePattern } else { 'thaumcraft:([a-z0-9_./-]+)' }
    $maxScanFiles = if ($rules.maxScanFiles) { [int]$rules.maxScanFiles } else { 4000 }
    $scannedFiles = 0
    $stopScan = $false
    foreach ($scanRoot in @($rules.scanRoots)) {
        if ($stopScan) { break }
        if ([string]::IsNullOrWhiteSpace([string]$scanRoot)) { continue }
        $fullScanRoot = Join-Path $PortPath ([string]$scanRoot)
        if (-not (Test-Path -LiteralPath $fullScanRoot -PathType Container)) { continue }
        foreach ($file in @(Get-ChildItem -LiteralPath $fullScanRoot -Recurse -File -Filter '*.json' -ErrorAction SilentlyContinue)) {
            $scannedFiles++
            if ($scannedFiles -gt $maxScanFiles) { $stopScan = $true; break }
            $text = Get-Content -Raw -LiteralPath $file.FullName
            $refs = @([regex]::Matches($text.ToLowerInvariant(), $pattern) | ForEach-Object { Normalize-Id $_.Groups[1].Value } | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Sort-Object -Unique)
            $fileRel = ConvertTo-RelativeRepoPath $file.FullName
            $fileMentionsSeed = $false
            foreach ($seed in @($seedSet | Sort-Object)) {
                if ($refs -contains $seed -or (Test-TextMentionsFocusId -Text $fileRel -Id $seed)) { $fileMentionsSeed = $true; break }
            }
            if (-not $fileMentionsSeed) { continue }
            foreach ($refId in @($refs)) {
                if ($knownIds.ContainsKey($refId)) {
                    Add-FocusDependencyId -SelectedSet $selectedSet -SeedSet $seedSet -Id $refId -Reason 'resource_or_data_json_reference' -Evidence $fileRel -Reasons ([ref]$dependencyReasons)
                }
            }
        }
    }

    $selectedIdsOut = @($selectedSet | Sort-Object)
    $seedIdsOut = @($seedSet | Sort-Object)
    $addedIdsOut = @($selectedIdsOut | Where-Object { $_ -notin $seedIdsOut } | Sort-Object)
    return [pscustomobject][ordered]@{
        seedSelectedIds = @($seedIdsOut)
        selectedIds = @($selectedIdsOut)
        addedIds = @($addedIdsOut)
        reasons = @($dependencyReasons | Sort-Object id, reason, evidence -Unique)
        scannedFiles = $scannedFiles
    }
}
# Batch 32 dependency-aware focus expansion end

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

$seedSelectedIds = @($selected | Sort-Object)
$rulesRootForFocus = Join-Path (Split-Path -Parent $PSScriptRoot) "rules"
$dependencyExpansion = Get-FocusDependencyExpansion -Entries $portEntries -SeedIds $seedSelectedIds -PortPath $portPath -RulesRoot $rulesRootForFocus
$selectedIds = @($dependencyExpansion.selectedIds)
$dependencyAddedIds = @($dependencyExpansion.addedIds)
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
    seedSelectedIds = @($seedSelectedIds)
    dependencyExpandedIds = @($dependencyAddedIds)
    focusDependencyExpansion = [ordered]@{
        enabled = [bool]$filterRequested
        seedCount = @($seedSelectedIds).Count
        addedCount = @($dependencyAddedIds).Count
        scannedFiles = $dependencyExpansion.scannedFiles
        reasons = @($dependencyExpansion.reasons)
    }
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
$lines.Add("Seed selected IDs: **$(@($seedSelectedIds).Count)**")
$lines.Add("Dependency-added IDs: **$(@($dependencyAddedIds).Count)**")
$lines.Add("")
$lines.Add("## Selected IDs")
$lines.Add("")
if ($selectedIds.Count -eq 0) { $lines.Add("<none>") } else { foreach ($id in $selectedIds) { $lines.Add("- ``thaumcraft:$id``") } }
$lines.Add("")
$lines.Add("## Dependency expansion reasons")
$lines.Add("")
if (@($dependencyExpansion.reasons).Count -eq 0) {
    $lines.Add("<none>")
} else {
    foreach ($reason in @($dependencyExpansion.reasons)) {
        $lines.Add("- ``thaumcraft:$($reason.id)`` — $($reason.reason): ``$($reason.evidence)``")
    }
}
$lines.Add("")
$lines.Add("## Changed files")
$lines.Add("")
if ($changedFiles.Count -eq 0) { $lines.Add("<none>") } else { foreach ($file in $changedFiles) { $lines.Add("- ``$file``") } }
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Focused filter report: $OutputMarkdown"
Write-Output "Mode=$mode, selectedIds=$($selectedIds.Count), changedFiles=$($changedFiles.Count)"
