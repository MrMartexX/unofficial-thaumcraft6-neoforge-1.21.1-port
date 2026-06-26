[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$LegacyRoot,
    [Parameter(Mandatory = $true)][string]$SecondaryLegacyRoot,
    [Parameter(Mandatory = $true)][string]$PortRoot,
    [string]$LegacyManifestPath,
    [string]$PortManifestPath,
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = 'Stop'
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path

function Resolve-WithinRepo([string]$PathValue) {
    if ([string]::IsNullOrWhiteSpace($PathValue)) { return '' }
    if ([System.IO.Path]::IsPathRooted($PathValue)) {
        if (Test-Path -LiteralPath $PathValue) { return (Resolve-Path -LiteralPath $PathValue).Path }
        return $PathValue
    }
    $candidate = Join-Path $RepoRoot $PathValue
    if (Test-Path -LiteralPath $candidate) { return (Resolve-Path -LiteralPath $candidate).Path }
    return $candidate
}

$LegacyRoot = Resolve-WithinRepo $LegacyRoot
$SecondaryLegacyRoot = Resolve-WithinRepo $SecondaryLegacyRoot
$PortRoot = Resolve-WithinRepo $PortRoot
if ($LegacyManifestPath) { $LegacyManifestPath = Resolve-WithinRepo $LegacyManifestPath }
if ($PortManifestPath) { $PortManifestPath = Resolve-WithinRepo $PortManifestPath }
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot 'tools/audits/item-block-parity/rules' }
$reportRoot = Join-Path $RepoRoot 'tools/reports/local/item-block-parity'
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot 'item_block_source_conflict_report.json' }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot 'item_block_source_conflict_report.md' }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return '' }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace('\', '/')
}

function Normalize-TextForHash([string]$Text) {
    if ($null -eq $Text) { return '' }
    $t = $Text -replace "`r`n", "`n"
    $t = $t -replace "`r", "`n"
    return $t.Trim()
}

function Get-Sha256([string]$Text) {
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($Text)
    $sha = [System.Security.Cryptography.SHA256]::Create()
    try {
        return ([System.BitConverter]::ToString($sha.ComputeHash($bytes))).Replace('-', '').ToLowerInvariant()
    } finally {
        $sha.Dispose()
    }
}

function Get-SourceKey {
    param([string]$FullPath, [string]$Root, [string]$Text)
    $package = ''
    $className = [System.IO.Path]::GetFileNameWithoutExtension($FullPath)
    $m = [regex]::Match($Text, '(?m)^\s*package\s+([A-Za-z0-9_.]+)\s*;')
    if ($m.Success) { $package = $m.Groups[1].Value }
    if (-not [string]::IsNullOrWhiteSpace($package)) { return "$package.$className" }
    if (-not [string]::IsNullOrWhiteSpace($Root)) {
        $rel = [System.IO.Path]::GetRelativePath($Root, $FullPath).Replace('\', '/')
        $rel = $rel -replace '^src/main/java/', ''
        $rel = $rel -replace '\.java$', ''
        return ($rel.Replace('/', '.')).ToLowerInvariant()
    }
    return $className.ToLowerInvariant()
}

function Get-SourceMap {
    param([string]$Root, [string]$Kind)
    $map = @{}
    $files = @()
    if (-not [string]::IsNullOrWhiteSpace($Root) -and (Test-Path -LiteralPath $Root -PathType Container)) {
        $files = @(Get-ChildItem -LiteralPath $Root -Recurse -File -Filter '*.java' -ErrorAction SilentlyContinue)
    }
    foreach ($file in $files) {
        $text = [System.IO.File]::ReadAllText($file.FullName)
        $key = Get-SourceKey -FullPath $file.FullName -Root $Root -Text $text
        if ([string]::IsNullOrWhiteSpace($key)) { continue }
        $norm = Normalize-TextForHash $text
        $entry = [pscustomobject][ordered]@{
            key = $key
            kind = $Kind
            path = ConvertTo-RelativeRepoPath $file.FullName
            hash = Get-Sha256 $norm
            lineCount = @($norm -split "`n").Count
        }
        if (-not $map.ContainsKey($key)) { $map[$key] = @() }
        $map[$key] = @($map[$key] + $entry)
    }
    return [pscustomobject][ordered]@{
        files = @($files)
        map = $map
    }
}

function Get-MatchedTerms {
    param([string]$Text, [string[]]$Terms)
    $hits = @()
    $lower = $Text.ToLowerInvariant()
    foreach ($term in @($Terms)) {
        if ([string]::IsNullOrWhiteSpace($term)) { continue }
        if ($lower.Contains(([string]$term).ToLowerInvariant())) { $hits += [string]$term }
    }
    return @($hits | Select-Object -Unique)
}

function Get-Entries($Manifest) {
    foreach ($name in @('entries','items','blocks')) {
        $prop = $Manifest.PSObject.Properties[$name]
        if ($null -ne $prop -and $null -ne $prop.Value) { return @($prop.Value) }
    }
    return @()
}

function Normalize-Id([object]$Value) {
    if ($null -eq $Value) { return '' }
    $s = ([string]$Value).Trim().ToLowerInvariant()
    if ($s.StartsWith('thaumcraft:')) { $s = $s.Substring('thaumcraft:'.Length) }
    return $s
}

function Get-EntryId($Entry) {
    foreach ($name in @('registryId','id','name','registryName','legacyId')) {
        $prop = $Entry.PSObject.Properties[$name]
        if ($null -ne $prop -and -not [string]::IsNullOrWhiteSpace([string]$prop.Value)) { return Normalize-Id $prop.Value }
    }
    return ''
}

function Add-Row {
    param(
        [ref]$Rows,
        [string]$Status,
        [string]$Severity,
        [string]$Classification,
        [string]$Subject,
        [string]$Path,
        [string[]]$Evidence = @(),
        [string]$Recommendation = ''
    )
    $Rows.Value += [pscustomobject][ordered]@{
        check = 'source_conflict_report'
        status = $Status
        severity = $Severity
        classification = $Classification
        subject = $Subject
        path = $Path
        evidence = @($Evidence)
        recommendation = $Recommendation
    }
}

function Escape-MarkdownCell([object]$Value) {
    if ($null -eq $Value) { return '' }
    $s = if ($Value -is [System.Array]) { ($Value -join ', ') } else { [string]$Value }
    return (($s -replace '\|', '\|') -replace "`r?`n", '<br>')
}

$rulesPath = Join-Path $RulesRoot 'source-conflict-rules.json'
$rules = $null
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
}
$candidateTerms = @('thaumcraft','wand','focus','golem','aura','aspect','research','essentia','crucible','infusion','arcane','caster','seal','void','thaumium')
if ($null -ne $rules -and $rules.PSObject.Properties['candidateIdTerms']) { $candidateTerms = @($rules.candidateIdTerms) }

$primary = Get-SourceMap -Root $LegacyRoot -Kind 'primary_legacy'
$secondary = Get-SourceMap -Root $SecondaryLegacyRoot -Kind 'secondary_legacy'
$port = Get-SourceMap -Root $PortRoot -Kind 'port'

$allKeys = @($primary.map.Keys + $secondary.map.Keys + $port.map.Keys | Sort-Object -Unique)
$rows = @()
$primarySecondaryConflicts = 0
$legacyOnlyClasses = 0
$portOnlyClasses = 0
$primaryOnlyClasses = 0
$secondaryOnlyClasses = 0
$sharedClasses = 0

foreach ($key in $allKeys) {
    $p = @()
    $s = @()
    $o = @()
    if ($primary.map.ContainsKey($key)) { $p = @($primary.map[$key]) }
    if ($secondary.map.ContainsKey($key)) { $s = @($secondary.map[$key]) }
    if ($port.map.ContainsKey($key)) { $o = @($port.map[$key]) }
    $evidence = @()
    if ($p.Count -gt 0) { $evidence += "primary=$($p.Count)" }
    if ($s.Count -gt 0) { $evidence += "secondary=$($s.Count)" }
    if ($o.Count -gt 0) { $evidence += "port=$($o.Count)" }
    $path = ''
    if ($o.Count -gt 0) { $path = $o[0].path } elseif ($p.Count -gt 0) { $path = $p[0].path } elseif ($s.Count -gt 0) { $path = $s[0].path }

    if ($p.Count -gt 0 -and $s.Count -gt 0 -and $o.Count -gt 0) {
        $sharedClasses++
        $pHashes = @($p | ForEach-Object { $_.hash } | Sort-Object -Unique)
        $sHashes = @($s | ForEach-Object { $_.hash } | Sort-Object -Unique)
        $legacyHashOverlap = @($pHashes | Where-Object { $_ -in $sHashes })
        if ($legacyHashOverlap.Count -eq 0) {
            $primarySecondaryConflicts++
            Add-Row -Rows ([ref]$rows) -Status 'reviewNeeded' -Severity 'medium' -Classification 'primary_secondary_conflict' -Subject $key -Path $path -Evidence $evidence -Recommendation 'Primary and secondary legacy source both contain this class but normalized hashes differ; classify source authority before using this class as parity evidence.'
        } else {
            Add-Row -Rows ([ref]$rows) -Status 'pass' -Severity 'info' -Classification 'shared_source_evidence' -Subject $key -Path $path -Evidence $evidence -Recommendation 'Primary, secondary and port source all expose this class key; keep as source evidence, not gameplay parity certification.'
        }
        continue
    }

    if ($p.Count -gt 0 -and $s.Count -gt 0 -and $o.Count -eq 0) {
        $legacyOnlyClasses++
        Add-Row -Rows ([ref]$rows) -Status 'reviewNeeded' -Severity 'medium' -Classification 'legacy_only_source' -Subject $key -Path $path -Evidence $evidence -Recommendation 'Legacy source exposes this class but port source does not; classify as omitted, renamed, merged, superseded or pending.'
        continue
    }

    if ($p.Count -eq 0 -and $s.Count -eq 0 -and $o.Count -gt 0) {
        $portOnlyClasses++
        Add-Row -Rows ([ref]$rows) -Status 'reviewNeeded' -Severity 'low' -Classification 'port_only_source' -Subject $key -Path $path -Evidence $evidence -Recommendation 'Port source exposes this class without primary or secondary legacy counterpart; classify as NeoForge infrastructure, rename, helper or new implementation.'
        continue
    }

    if ($p.Count -gt 0 -and $s.Count -eq 0 -and $o.Count -eq 0) {
        $primaryOnlyClasses++
        Add-Row -Rows ([ref]$rows) -Status 'reviewNeeded' -Severity 'medium' -Classification 'primary_only_legacy_source' -Subject $key -Path $path -Evidence $evidence -Recommendation 'Only the primary legacy source exposes this class; compare against secondary/jar evidence before treating it as authoritative.'
        continue
    }

    if ($p.Count -eq 0 -and $s.Count -gt 0 -and $o.Count -eq 0) {
        $secondaryOnlyClasses++
        Add-Row -Rows ([ref]$rows) -Status 'reviewNeeded' -Severity 'medium' -Classification 'secondary_only_legacy_source' -Subject $key -Path $path -Evidence $evidence -Recommendation 'Only the secondary legacy source exposes this class; compare against primary/jar evidence before treating it as authoritative.'
        continue
    }

    if ($p.Count -gt 0 -and $s.Count -eq 0 -and $o.Count -gt 0) {
        Add-Row -Rows ([ref]$rows) -Status 'reviewNeeded' -Severity 'low' -Classification 'secondary_missing_source' -Subject $key -Path $path -Evidence $evidence -Recommendation 'Primary legacy and port source expose this class but secondary legacy evidence is absent; classify source confidence.'
        continue
    }

    if ($p.Count -eq 0 -and $s.Count -gt 0 -and $o.Count -gt 0) {
        Add-Row -Rows ([ref]$rows) -Status 'reviewNeeded' -Severity 'low' -Classification 'primary_missing_source' -Subject $key -Path $path -Evidence $evidence -Recommendation 'Secondary legacy and port source expose this class but primary legacy evidence is absent; classify source confidence.'
        continue
    }
}

$manifestCandidateIds = 0
foreach ($manifestPath in @($LegacyManifestPath, $PortManifestPath)) {
    if ([string]::IsNullOrWhiteSpace($manifestPath) -or -not (Test-Path -LiteralPath $manifestPath -PathType Leaf)) { continue }
    $manifest = Get-Content -Raw -LiteralPath $manifestPath | ConvertFrom-Json
    foreach ($entry in Get-Entries $manifest) {
        $id = Get-EntryId $entry
        if ([string]::IsNullOrWhiteSpace($id)) { continue }
        $hits = @(Get-MatchedTerms -Text $id -Terms $candidateTerms)
        if ($hits.Count -gt 0) { $manifestCandidateIds++ }
    }
}

$passCount = @($rows | Where-Object { $_.status -eq 'pass' }).Count
$reviewCount = @($rows | Where-Object { $_.status -eq 'reviewNeeded' }).Count
$summary = [pscustomobject][ordered]@{
    rows = @($rows).Count
    pass = $passCount
    reviewNeeded = $reviewCount
    errors = 0
    sourceFilesScanned = @($primary.files).Count + @($secondary.files).Count + @($port.files).Count
    primarySourceFiles = @($primary.files).Count
    secondarySourceFiles = @($secondary.files).Count
    portSourceFiles = @($port.files).Count
    keysCompared = @($allKeys).Count
    sharedClasses = $sharedClasses
    primarySecondaryConflicts = $primarySecondaryConflicts
    legacyOnlyClasses = $legacyOnlyClasses
    portOnlyClasses = $portOnlyClasses
    primaryOnlyClasses = $primaryOnlyClasses
    secondaryOnlyClasses = $secondaryOnlyClasses
    manifestCandidateIds = $manifestCandidateIds
}

$report = [pscustomobject][ordered]@{
    schemaVersion = 1
    generatedAtUtc = (Get-Date).ToUniversalTime().ToString('o')
    check = 'source_conflict_report'
    checks = @($Checks)
    summary = $summary
    results = @($rows)
}

$reportDir = Split-Path -Parent $OutputJson
if (-not (Test-Path -LiteralPath $reportDir -PathType Container)) {
    New-Item -ItemType Directory -Path $reportDir -Force | Out-Null
}
$report | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $OutputJson -Encoding UTF8

$md = @()
$md += '# Source conflict report'
$md += ''
$md += '| Metric | Value |'
$md += '|---|---:|'
foreach ($prop in $summary.PSObject.Properties) {
    $md += "| $($prop.Name) | $($prop.Value) |"
}
$md += ''
$md += '| Status | Severity | Classification | Subject | Path | Evidence | Recommendation |'
$md += '|---|---|---|---|---|---|---|'
foreach ($row in $rows) {
    $md += "| $(Escape-MarkdownCell $row.status) | $(Escape-MarkdownCell $row.severity) | $(Escape-MarkdownCell $row.classification) | $(Escape-MarkdownCell $row.subject) | $(Escape-MarkdownCell $row.path) | $(Escape-MarkdownCell $row.evidence) | $(Escape-MarkdownCell $row.recommendation) |"
}
$md -join "`n" | Set-Content -LiteralPath $OutputMarkdown -Encoding UTF8

Write-Output "Source conflict report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors), sourceFilesScanned=$($summary.sourceFilesScanned), primarySourceFiles=$($summary.primarySourceFiles), secondarySourceFiles=$($summary.secondarySourceFiles), portSourceFiles=$($summary.portSourceFiles), sharedClasses=$($summary.sharedClasses), primarySecondaryConflicts=$($summary.primarySecondaryConflicts), legacyOnlyClasses=$($summary.legacyOnlyClasses), portOnlyClasses=$($summary.portOnlyClasses), manifestCandidateIds=$($summary.manifestCandidateIds)"