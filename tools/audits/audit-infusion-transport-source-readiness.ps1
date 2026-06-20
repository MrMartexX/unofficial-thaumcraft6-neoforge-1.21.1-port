param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path,
    [string]$OutputPath = ''
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Get-RelativePath([string]$Root, [string]$Path) {
    $rootFull = (Resolve-Path $Root).Path.TrimEnd('\','/')
    $pathFull = (Resolve-Path $Path).Path
    if ($pathFull.StartsWith($rootFull, [System.StringComparison]::OrdinalIgnoreCase)) {
        return $pathFull.Substring($rootFull.Length).TrimStart('\','/') -replace '\\','/'
    }
    return $pathFull -replace '\\','/'
}

function Escape-Md([string]$Text) {
    if ($null -eq $Text) { return '' }
    return ($Text -replace '\|','\|' -replace "`r",'' -replace "`n",' ')
}

$RepoRoot = (Resolve-Path $RepoRoot).Path
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $RepoRoot '06_docs/audits/infusion_transport_source_readiness_audit.md'
}

$roots = @(
    '05_neoforge_port/src/main/java/thaumcraft/common/essentia/transport',
    '05_neoforge_port/src/main/java/thaumcraft/common/blocks/essentia',
    '05_neoforge_port/src/main/java/thaumcraft/common/crafting/infusion',
    '05_neoforge_port/src/main/java/thaumcraft/common/registry'
)

$files = New-Object System.Collections.Generic.List[System.IO.FileInfo]
foreach ($relRoot in $roots) {
    $fullRoot = Join-Path $RepoRoot $relRoot
    if (Test-Path $fullRoot) {
        Get-ChildItem -Path $fullRoot -Recurse -File -Include *.java | ForEach-Object { $files.Add($_) | Out-Null }
    }
}
$files = @($files | Sort-Object FullName -Unique)

$storagePattern = '(?i)(AspectList|Aspect\b|essentia|amount|capacity|stored|storage|contents|buffer|tank)'
$readPattern = '(?i)(available|get|contains|canExtract|canInsert|canReceive|has|amount|size)'
$drainPattern = '(?i)(drain|extract|remove|take|consume|pull)'
$insertPattern = '(?i)(insert|receive|add|accept|push|fill)'
$transportPattern = '(?i)(TCEssentiaTransport|EssentiaTransport|Tube|SmelterEndpoint|TUBE|tube)'
$blockEntityPattern = '(?i)(extends\s+.*BlockEntity|BlockEntityType|BlockEntity\b)'
$placeholderPattern = '(?i)(TODO|placeholder|stub|bridge|non-gameplay|deferred|legacy-aligned)'

$fileRows = New-Object System.Collections.Generic.List[object]
$evidenceRows = New-Object System.Collections.Generic.List[object]

foreach ($file in $files) {
    $rel = Get-RelativePath $RepoRoot $file.FullName
    $lines = @(Get-Content -LiteralPath $file.FullName)
    $hasTransport = $false
    $hasBlockEntity = $false
    $hasStorage = $false
    $hasRead = $false
    $hasDrain = $false
    $hasInsert = $false
    $hasPlaceholder = $false

    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = [string]$lines[$i]
        $lineHit = $false
        $category = New-Object System.Collections.Generic.List[string]
        if ($line -match $transportPattern -or $rel -match '(?i)(essentia/transport|Tube|SmelterEndpoint)') { $hasTransport = $true; $category.Add('transport') | Out-Null; $lineHit = $true }
        if ($line -match $blockEntityPattern -or $rel -match '(?i)BlockEntity') { $hasBlockEntity = $true; $category.Add('block_entity') | Out-Null; $lineHit = $true }
        if ($line -match $storagePattern) { $hasStorage = $true; $category.Add('storage_term') | Out-Null; $lineHit = $true }
        if ($line -match $readPattern) { $hasRead = $true; $category.Add('read_term') | Out-Null; $lineHit = $true }
        if ($line -match $drainPattern) { $hasDrain = $true; $category.Add('drain_term') | Out-Null; $lineHit = $true }
        if ($line -match $insertPattern) { $hasInsert = $true; $category.Add('insert_term') | Out-Null; $lineHit = $true }
        if ($line -match $placeholderPattern) { $hasPlaceholder = $true; $category.Add('placeholder_term') | Out-Null; $lineHit = $true }
        if ($lineHit) {
            $evidenceRows.Add([pscustomobject]@{
                File = $rel
                Line = $i + 1
                Category = ($category -join ', ')
                Text = $line.Trim()
            }) | Out-Null
        }
    }

    if (-not ($hasTransport -or $hasBlockEntity -or $hasStorage -or $hasDrain -or $hasInsert)) { continue }

    $status = 'reference_only'
    if ($hasPlaceholder) {
        $status = 'needs_manual_review_placeholder_or_bridge'
    } elseif ($hasBlockEntity -and $hasStorage -and $hasRead -and $hasDrain) {
        $status = 'review_candidate_has_read_and_drain_terms'
    } elseif ($hasBlockEntity -and $hasStorage -and $hasRead -and -not $hasDrain) {
        $status = 'not_source_ready_no_drain_term'
    } elseif ($hasBlockEntity -and -not $hasStorage) {
        $status = 'not_source_ready_no_storage_term'
    } elseif ($hasTransport -and -not $hasStorage) {
        $status = 'transport_only_no_storage_term'
    }

    $fileRows.Add([pscustomobject]@{
        File = $rel
        Transport = $hasTransport
        BlockEntity = $hasBlockEntity
        Storage = $hasStorage
        Read = $hasRead
        Drain = $hasDrain
        Insert = $hasInsert
        Placeholder = $hasPlaceholder
        Status = $status
    }) | Out-Null
}

function Add-Table([System.Collections.Generic.List[string]]$md, [string]$Title, $Rows, [string]$EmptyText) {
    $md.Add('') | Out-Null
    $md.Add("## $Title") | Out-Null
    $md.Add('') | Out-Null
    if (-not $Rows -or @($Rows).Count -eq 0) {
        $md.Add($EmptyText) | Out-Null
        return
    }
    $md.Add('| File | Transport | BlockEntity | Storage | Read | Drain | Insert | Placeholder | Status |') | Out-Null
    $md.Add('|---|---:|---:|---:|---:|---:|---:|---:|---|') | Out-Null
    foreach ($row in $Rows) {
        $md.Add('| ' + (Escape-Md $row.File) + ' | ' + $row.Transport + ' | ' + $row.BlockEntity + ' | ' + $row.Storage + ' | ' + $row.Read + ' | ' + $row.Drain + ' | ' + $row.Insert + ' | ' + $row.Placeholder + ' | ' + (Escape-Md $row.Status) + ' |') | Out-Null
    }
}

function Add-EvidenceTable([System.Collections.Generic.List[string]]$md, [string]$Title, $Rows, [string]$EmptyText) {
    $md.Add('') | Out-Null
    $md.Add("## $Title") | Out-Null
    $md.Add('') | Out-Null
    if (-not $Rows -or @($Rows).Count -eq 0) {
        $md.Add($EmptyText) | Out-Null
        return
    }
    $md.Add('| File | Line | Category | Evidence |') | Out-Null
    $md.Add('|---|---:|---|---|') | Out-Null
    foreach ($row in $Rows) {
        $md.Add('| ' + (Escape-Md $row.File) + ' | ' + $row.Line + ' | ' + (Escape-Md $row.Category) + ' | `' + (Escape-Md $row.Text) + '` |') | Out-Null
    }
}

$reviewCandidates = @($fileRows | Where-Object { $_.Status -eq 'review_candidate_has_read_and_drain_terms' } | Sort-Object File)
$notReady = @($fileRows | Where-Object { $_.Status -like 'not_source_ready*' -or $_.Status -like 'transport_only*' } | Sort-Object File)
$placeholders = @($fileRows | Where-Object { $_.Placeholder } | Sort-Object File)
$evidenceFocus = @($evidenceRows | Where-Object { $_.Category -match 'storage_term|drain_term|insert_term|block_entity|transport' } | Sort-Object File, Line | Select-Object -First 160)

$md = New-Object System.Collections.Generic.List[string]
$md.Add('# Infusion Transport Source Readiness Audit') | Out-Null
$md.Add('') | Out-Null
$md.Add('Generated: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')) | Out-Null
$md.Add('') | Out-Null
$md.Add('## Summary') | Out-Null
$md.Add('') | Out-Null
$md.Add('| Metric | Count |') | Out-Null
$md.Add('|---|---:|') | Out-Null
$md.Add('| Scanned source files | ' + $files.Count + ' |') | Out-Null
$md.Add('| Files with source-readiness signals | ' + $fileRows.Count + ' |') | Out-Null
$md.Add('| Review candidates with read and drain terms | ' + $reviewCandidates.Count + ' |') | Out-Null
$md.Add('| Not-source-ready transport/storage files | ' + $notReady.Count + ' |') | Out-Null
$md.Add('| Placeholder/bridge warning files | ' + $placeholders.Count + ' |') | Out-Null
$md.Add('') | Out-Null
$md.Add('## Interpretation') | Out-Null
$md.Add('') | Out-Null
$md.Add('- This audit narrows the broad real-source candidate scan to the current essentia transport and related source API surface.') | Out-Null
$md.Add('- A file is not source-ready just because it is a tube or transport block entity; it must expose stable readable storage and an all-or-nothing drain path.') | Out-Null
$md.Add('- `TCInfusionAspectSourceResolver` now selects storage-bearing aspect containers; tube transport buffers remain excluded.') | Out-Null
$md.Add('- Player-facing infusion completion remains disabled by policy.') | Out-Null

Add-Table $md 'Potential review candidates' $reviewCandidates 'No files currently expose enough read/drain/storage signals to select a real source automatically.'
Add-Table $md 'Not source ready or transport only' $notReady 'No transport-only or not-source-ready files were detected.'
Add-Table $md 'Placeholder or bridge warnings' $placeholders 'No placeholder/bridge warning files were detected.'
Add-EvidenceTable $md 'Focused evidence, first 160 rows' $evidenceFocus 'No focused evidence rows were found.'

$md.Add('') | Out-Null
$md.Add('## Porting conclusion') | Out-Null
$md.Add('') | Out-Null
$md.Add('- Do not connect infusion completion directly to tubes or transient transport buffers: legacy `EssentiaHandler` discovered `IAspectSource` containers.') | Out-Null
$md.Add('- The first selected storage-bearing source is `TCWardedJarBlockEntity` through `TCAspectSourceContainer`; player-facing completion remains audit-only.') | Out-Null
$md.Add('- This audit should be re-run after changes under `thaumcraft.common.essentia.transport`, `thaumcraft.common.blocks.essentia`, or infusion source resolver code.') | Out-Null

New-Item -ItemType Directory -Force -Path (Split-Path $OutputPath) | Out-Null
Set-Content -LiteralPath $OutputPath -Value $md -Encoding UTF8
Write-Host ('[tc-port] Wrote ' + (Get-RelativePath $RepoRoot $OutputPath))
