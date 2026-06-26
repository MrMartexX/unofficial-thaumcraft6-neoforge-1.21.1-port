[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$LegacyRoot,
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
$PortRoot = Resolve-WithinRepo $PortRoot
if ($LegacyManifestPath) { $LegacyManifestPath = Resolve-WithinRepo $LegacyManifestPath }
if ($PortManifestPath) { $PortManifestPath = Resolve-WithinRepo $PortManifestPath }
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot 'tools/audits/item-block-parity/rules' }
$reportRoot = Join-Path $RepoRoot 'tools/reports/local/item-block-parity'
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot 'item_block_public_api_report.json' }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot 'item_block_public_api_report.md' }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return '' }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace('\', '/')
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

function Get-Entries($Manifest) {
    foreach ($name in @('entries','items','blocks')) {
        $prop = $Manifest.PSObject.Properties[$name]
        if ($null -ne $prop -and $null -ne $prop.Value) { return @($prop.Value) }
    }
    return @()
}

function Get-EntrySearchText($Entry) {
    $values = @()
    foreach ($prop in @($Entry.PSObject.Properties)) {
        if ($null -eq $prop.Value) { continue }
        if ($prop.Value -is [System.Array]) {
            foreach ($v in @($prop.Value)) { if ($null -ne $v) { $values += [string]$v } }
        } else {
            $values += [string]$prop.Value
        }
    }
    return (($values -join ' ') -replace '[^a-zA-Z0-9_:/.-]', ' ').ToLowerInvariant()
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

function Get-SourceFiles {
    param([string]$Root, [string[]]$RelativeRoots, [string[]]$Extensions)
    $files = @()
    if ([string]::IsNullOrWhiteSpace($Root) -or -not (Test-Path -LiteralPath $Root -PathType Container)) { return @() }
    foreach ($relative in @($RelativeRoots)) {
        $base = Join-Path $Root $relative
        if (-not (Test-Path -LiteralPath $base -PathType Container)) { continue }
        foreach ($ext in @($Extensions)) {
            $files += @(Get-ChildItem -LiteralPath $base -Recurse -File -ErrorAction SilentlyContinue | Where-Object { $_.Extension -eq $ext })
        }
    }
    return @($files | Sort-Object FullName -Unique)
}

function Get-PublicDeclarations {
    param([string]$Text)
    $patterns = @(
        '^\s*public\s+(?:abstract\s+|final\s+|sealed\s+|non-sealed\s+)?(?:class|interface|enum|record)\s+([A-Za-z_][A-Za-z0-9_]*)',
        '^\s*public\s+(?:static\s+|final\s+|abstract\s+|synchronized\s+|native\s+|default\s+)*[A-Za-z0-9_<>,\[\]\.? extends super]+\s+([A-Za-z_][A-Za-z0-9_]*)\s*\(',
        '^\s*public\s+(?:static\s+|final\s+|volatile\s+|transient\s+)*[A-Za-z0-9_<>,\[\]\.? extends super]+\s+([A-Za-z_][A-Za-z0-9_]*)\s*(?:=|;)'
    )
    $decls = @()
    foreach ($pattern in $patterns) {
        foreach ($match in [regex]::Matches($Text, $pattern, [System.Text.RegularExpressions.RegexOptions]::Multiline)) {
            if ($match.Groups.Count -gt 1) { $decls += $match.Groups[1].Value }
        }
    }
    return @($decls | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Select-Object -Unique)
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
        check = 'public_api'
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
    $s = if ($Value -is [System.Array]) { (@($Value) -join ', ') } else { [string]$Value }
    return ($s -replace '\|', '\|' -replace "`r?`n", ' ')
}

$rulesPath = Join-Path $RulesRoot 'public-api-rules.json'
if (-not (Test-Path -LiteralPath $rulesPath -PathType Leaf)) {
    throw "Public API rules not found: $rulesPath"
}
$rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json

$sourceRoots = @($rules.sourceRoots)
$sourceExtensions = @($rules.sourceExtensions)
$apiTerms = @($rules.apiTerms)
$interopTerms = @($rules.interopTerms)
$stabilityTerms = @($rules.stabilityTerms)
$candidateIdTerms = @($rules.candidateIdTerms)

$portFiles = @(Get-SourceFiles -Root $PortRoot -RelativeRoots $sourceRoots -Extensions $sourceExtensions)
$legacyFiles = @(Get-SourceFiles -Root $LegacyRoot -RelativeRoots $sourceRoots -Extensions $sourceExtensions)

$rows = @()
$portPublicFiles = 0
$legacyPublicFiles = 0
$interopEvidenceFiles = 0
$stabilityEvidenceFiles = 0
$portClassNames = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)

foreach ($file in $portFiles) {
    $text = Get-Content -Raw -LiteralPath $file.FullName
    $decls = @(Get-PublicDeclarations $text)
    $apiHits = @(Get-MatchedTerms -Text $text -Terms $apiTerms)
    $interopHits = @(Get-MatchedTerms -Text $text -Terms $interopTerms)
    $stabilityHits = @(Get-MatchedTerms -Text $text -Terms $stabilityTerms)
    $relative = ConvertTo-RelativeRepoPath $file.FullName
    [void]$portClassNames.Add([System.IO.Path]::GetFileNameWithoutExtension($file.Name))

    if ($decls.Count -gt 0) {
        $portPublicFiles++
        $evidence = @("publicDeclarations=$($decls.Count)") + @($decls | Select-Object -First 8)
        if ($interopHits.Count -gt 0) { $evidence += "interopTerms=$($interopHits -join ',')" }
        if ($stabilityHits.Count -gt 0) { $evidence += "stabilityTerms=$($stabilityHits -join ',')" }
        Add-Row -Rows ([ref]$rows) -Status 'pass' -Severity 'info' -Classification 'port_public_surface_present' -Subject $relative -Path $relative -Evidence $evidence -Recommendation 'Classify whether this surface is stable public API, internal API, or legacy compatibility API.'
    } elseif ($interopHits.Count -gt 0 -or $apiHits.Count -gt 0) {
        if ($interopHits.Count -gt 0) { $interopEvidenceFiles++ }
        if ($stabilityHits.Count -gt 0) { $stabilityEvidenceFiles++ }
        $evidence = @()
        if ($apiHits.Count -gt 0) { $evidence += "apiTerms=$($apiHits -join ',')" }
        if ($interopHits.Count -gt 0) { $evidence += "interopTerms=$($interopHits -join ',')" }
        if ($stabilityHits.Count -gt 0) { $evidence += "stabilityTerms=$($stabilityHits -join ',')" }
        Add-Row -Rows ([ref]$rows) -Status 'review_needed' -Severity 'medium' -Classification 'api_clue_without_public_declaration' -Subject $relative -Path $relative -Evidence $evidence -Recommendation 'Review whether this file should expose stable API contracts or remain internal implementation.'
    }
}

foreach ($file in $legacyFiles) {
    $text = Get-Content -Raw -LiteralPath $file.FullName
    $decls = @(Get-PublicDeclarations $text)
    if ($decls.Count -eq 0) { continue }
    $legacyPublicFiles++
    $name = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)
    if (-not $portClassNames.Contains($name)) {
        $relative = ConvertTo-RelativeRepoPath $file.FullName
        Add-Row -Rows ([ref]$rows) -Status 'review_needed' -Severity 'medium' -Classification 'legacy_public_surface_without_same_named_port_source' -Subject $name -Path $relative -Evidence @("legacyPublicDeclarations=$($decls.Count)", ($decls | Select-Object -First 8)) -Recommendation 'Review whether this legacy public surface is intentionally removed, renamed, internalized or still required for addon compatibility.'
    }
}

$manifestCandidateIds = 0
if ($PortManifestPath -and (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) {
    $manifest = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
    foreach ($entry in @(Get-Entries $manifest)) {
        $id = Get-EntryId $entry
        if ([string]::IsNullOrWhiteSpace($id)) { continue }
        $text = Get-EntrySearchText $entry
        $hits = @(Get-MatchedTerms -Text "$id $text" -Terms $candidateIdTerms)
        if ($hits.Count -gt 0) {
            $manifestCandidateIds++
            Add-Row -Rows ([ref]$rows) -Status 'review_needed' -Severity 'low' -Classification 'manifest_public_api_candidate' -Subject $id -Path '' -Evidence @("candidateTerms=$($hits -join ',')") -Recommendation 'Review whether this registry ID participates in a public addon/API contract or is purely internal gameplay content.'
        }
    }
}

if ($rows.Count -eq 0) {
    Add-Row -Rows ([ref]$rows) -Status 'review_needed' -Severity 'medium' -Classification 'no_public_api_evidence_found' -Subject 'public_api' -Path '' -Evidence @('no public declarations or API clues found') -Recommendation 'Confirm whether the port intentionally exposes no stable public API surface.'
}

$passCount = @($rows | Where-Object { $_.status -eq 'pass' }).Count
$reviewCount = @($rows | Where-Object { $_.status -eq 'review_needed' }).Count
$errorCount = @($rows | Where-Object { $_.status -eq 'error' }).Count

$summary = [pscustomobject][ordered]@{
    rows = $rows.Count
    pass = $passCount
    reviewNeeded = $reviewCount
    errors = $errorCount
    sourceFilesScanned = ($portFiles.Count + $legacyFiles.Count)
    portSourceFilesScanned = $portFiles.Count
    legacySourceFilesScanned = $legacyFiles.Count
    portPublicApiFiles = $portPublicFiles
    legacyPublicApiFiles = $legacyPublicFiles
    interopEvidenceFiles = $interopEvidenceFiles
    stabilityEvidenceFiles = $stabilityEvidenceFiles
    manifestCandidateIds = $manifestCandidateIds
}

$report = [pscustomobject][ordered]@{
    schemaVersion = 1
    generatedAtUtc = (Get-Date).ToUniversalTime().ToString('o')
    check = 'public_api'
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
$md += '# Public API surface audit'
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

Write-Output "Public API surface audit report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors), sourceFilesScanned=$($summary.sourceFilesScanned), portPublicApiFiles=$($summary.portPublicApiFiles), legacyPublicApiFiles=$($summary.legacyPublicApiFiles), manifestCandidateIds=$($summary.manifestCandidateIds)"