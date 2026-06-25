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

$ErrorActionPreference = 'Stop'
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot 'tools/audits/item-block-parity/rules' }
$reportRoot = Join-Path $RepoRoot 'tools/reports/local/item-block-parity'
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot 'item_block_legacy_mapping_report.json' }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot 'item_block_legacy_mapping_report.md' }

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
    foreach ($name in @('registryId', 'id', 'name', 'registryName', 'legacyId')) {
        $prop = $Entry.PSObject.Properties[$name]
        if ($null -ne $prop -and -not [string]::IsNullOrWhiteSpace([string]$prop.Value)) { return Normalize-Id $prop.Value }
    }
    return ''
}
function Get-EntrySearchText($Entry) {
    $values = @()
    foreach ($name in @('registryId', 'id', 'name', 'symbol', 'declaredClass', 'declaredType', 'sourceFile', 'portClassFile', 'portPackage', 'portExtends')) {
        $prop = $Entry.PSObject.Properties[$name]
        if ($null -ne $prop -and $null -ne $prop.Value) { $values += [string]$prop.Value }
    }
    foreach ($name in @('portImplements', 'behaviorClues', 'methodPresence', 'blockEntities', 'blockEntityClasses', 'menus', 'menuClasses')) {
        $prop = $Entry.PSObject.Properties[$name]
        if ($null -ne $prop -and $null -ne $prop.Value) {
            foreach ($value in @($prop.Value)) { if ($value) { $values += [string]$value } }
        }
    }
    return (($values -join ' ') -replace '[^a-zA-Z0-9_:/.-]', ' ').ToLowerInvariant()
}
function Get-EntryKind($Entry) {
    foreach ($name in @('kind', 'type', 'declaredType')) {
        $prop = $Entry.PSObject.Properties[$name]
        if ($null -ne $prop -and -not [string]::IsNullOrWhiteSpace([string]$prop.Value)) { return [string]$prop.Value }
    }
    return ''
}
function Get-IdMap([object[]]$Entries) {
    $map = @{}
    foreach ($entry in @($Entries)) {
        $id = Get-EntryId $entry
        if ([string]::IsNullOrWhiteSpace($id)) { continue }
        if (-not $map.ContainsKey($id)) { $map[$id] = @() }
        $map[$id] = @($map[$id] + $entry)
    }
    return $map
}
function Get-CandidateIds([string]$LegacyId, [string]$LegacyText, [hashtable]$PortMap, [int]$Limit) {
    $candidates = @()
    foreach ($candidate in @($PortMap.Keys | Sort-Object)) {
        if ($candidate -eq $LegacyId) { continue }
        if ($candidate.Contains($LegacyId) -or $LegacyId.Contains($candidate)) { $candidates += $candidate; continue }
        if (-not [string]::IsNullOrWhiteSpace($LegacyText) -and $LegacyText.Contains($candidate)) { $candidates += $candidate; continue }
        foreach ($entry in @($PortMap[$candidate])) {
            $candidateText = Get-EntrySearchText $entry
            if ($candidateText.Contains($LegacyId)) { $candidates += $candidate; break }
        }
        if ($candidates.Count -ge $Limit) { break }
    }
    return @($candidates | Select-Object -Unique | Select-Object -First $Limit)
}
function Add-Row {
    param(
        [ref]$Rows,
        [string]$Status,
        [string]$Severity,
        [string]$LegacyId,
        [string]$PortId,
        [string]$Classification,
        [string]$Evidence,
        [string[]]$Candidates = @()
    )
    $Rows.Value += [pscustomobject][ordered]@{
        check = 'legacy_mapping'
        status = $Status
        severity = $Severity
        legacyId = $LegacyId
        portId = $PortId
        classification = $Classification
        candidates = @($Candidates)
        evidence = $Evidence
    }
}

$rulesPath = Join-Path $RulesRoot 'legacy-mapping-rules.json'
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{ candidateLimit = 5; status = [pscustomobject]@{ pass = 'LEGACY_MAPPING_PASS'; review = 'LEGACY_MAPPING_REVIEW_NEEDED'; error = 'LEGACY_MAPPING_ERROR' } }
}
$passStatus = if ($rules.status.pass) { [string]$rules.status.pass } else { 'LEGACY_MAPPING_PASS' }
$reviewStatus = if ($rules.status.review) { [string]$rules.status.review } else { 'LEGACY_MAPPING_REVIEW_NEEDED' }
$errorStatus = if ($rules.status.error) { [string]$rules.status.error } else { 'LEGACY_MAPPING_ERROR' }
$candidateLimit = if ($rules.candidateLimit) { [int]$rules.candidateLimit } else { 5 }

if (-not (Test-Path -LiteralPath $LegacyManifestPath -PathType Leaf)) { throw ('Legacy manifest not found: ' + $LegacyManifestPath) }
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw ('Port manifest not found: ' + $PortManifestPath) }

$legacyManifest = Get-Content -Raw -LiteralPath $LegacyManifestPath | ConvertFrom-Json
$portManifest = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$legacyEntries = @($legacyManifest.entries)
$portEntries = @($portManifest.entries)
$legacyMap = Get-IdMap -Entries $legacyEntries
$portMap = Get-IdMap -Entries $portEntries
$rows = @()

foreach ($legacyId in @($legacyMap.Keys | Sort-Object)) {
    $legacyEntry = @($legacyMap[$legacyId] | Select-Object -First 1)[0]
    if ($portMap.ContainsKey($legacyId)) {
        Add-Row -Rows ([ref]$rows) -Status $passStatus -Severity 'info' -LegacyId $legacyId -PortId $legacyId -Classification 'direct_id_match' -Evidence 'Legacy ID exists in port manifest.'
    } else {
        $legacyText = Get-EntrySearchText $legacyEntry
        $candidates = Get-CandidateIds -LegacyId $legacyId -LegacyText $legacyText -PortMap $portMap -Limit $candidateLimit
        $evidence = if ($candidates.Count -gt 0) { 'Legacy ID is missing in port manifest; possible rename candidates were found.' } else { 'Legacy ID is missing in port manifest and no conservative rename candidate was found.' }
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -LegacyId $legacyId -PortId '' -Classification 'missing_legacy_id' -Evidence $evidence -Candidates $candidates
    }
}

foreach ($portId in @($portMap.Keys | Sort-Object)) {
    if (-not $legacyMap.ContainsKey($portId)) {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -LegacyId '' -PortId $portId -Classification 'port_only_id' -Evidence 'Port ID has no direct legacy manifest ID match.'
    }
}

$orderedRows = @($rows | Sort-Object severity, classification, legacyId, portId)
$summary = [ordered]@{
    rows = @($orderedRows).Count
    pass = @($orderedRows | Where-Object { $_.status -eq $passStatus }).Count
    reviewNeeded = @($orderedRows | Where-Object { $_.status -eq $reviewStatus }).Count
    errors = @($orderedRows | Where-Object { $_.status -eq $errorStatus }).Count
    directMatches = @($orderedRows | Where-Object { $_.classification -eq 'direct_id_match' }).Count
    missingLegacyIds = @($orderedRows | Where-Object { $_.classification -eq 'missing_legacy_id' }).Count
    portOnlyIds = @($orderedRows | Where-Object { $_.classification -eq 'port_only_id' }).Count
    legacyEntries = @($legacyEntries).Count
    portEntries = @($portEntries).Count
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString('o')
    policy = 'Report-only legacy mapping review. Review rows identify missing direct IDs, possible renames and port-only IDs; they are not gameplay parity failures.'
    inputs = [ordered]@{
        legacyManifest = ConvertTo-RelativeRepoPath $LegacyManifestPath
        portManifest = ConvertTo-RelativeRepoPath $PortManifestPath
        rules = ConvertTo-RelativeRepoPath $rulesPath
        checks = @($Checks)
    }
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = @()
$lines += '# Item/block legacy mapping review'
$lines += ''
$lines += ('Generated: ' + $report.generatedAtUtc)
$lines += ''
$lines += 'Policy: report-only legacy mapping review. Review rows are mapping work, not gameplay parity failures.'
$lines += ''
$lines += '## Summary'
$lines += ''
$lines += ('- Rows: ' + $summary.rows)
$lines += ('- Pass: ' + $summary.pass)
$lines += ('- Review needed: ' + $summary.reviewNeeded)
$lines += ('- Errors: ' + $summary.errors)
$lines += ('- Direct matches: ' + $summary.directMatches)
$lines += ('- Missing legacy IDs: ' + $summary.missingLegacyIds)
$lines += ('- Port-only IDs: ' + $summary.portOnlyIds)
$lines += ''
$lines += '## Non-pass sample'
$lines += ''
$lines += '| Status | Classification | Legacy ID | Port ID | Candidates | Evidence |'
$lines += '|---|---|---|---|---|---|'
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus } | Select-Object -First 100)
foreach ($row in $nonPass) {
    $candidateText = if ($row.candidates) { @($row.candidates) -join ', ' } else { '' }
    $evidence = if ($row.evidence) { $row.evidence.Replace('|', '\|') } else { '' }
    $lines += ('| ' + $row.status + ' | ' + $row.classification + ' | ' + $row.legacyId + ' | ' + $row.portId + ' | ' + $candidateText + ' | ' + $evidence + ' |')
}
if ($nonPass.Count -eq 0) {
    $lines += '| LEGACY_MAPPING_PASS | <all> | <all> | <all> | <none> | All legacy IDs have direct port manifest matches. |'
}
$lines += ''
$lines += 'Only the first 100 non-pass rows are shown in Markdown; JSON contains all rows.'
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output ('Legacy mapping review report: ' + $OutputMarkdown)
Write-Output ('Rows={0}, pass={1}, reviewNeeded={2}, errors={3}, directMatches={4}, missingLegacyIds={5}, portOnlyIds={6}' -f $summary.rows, $summary.pass, $summary.reviewNeeded, $summary.errors, $summary.directMatches, $summary.missingLegacyIds, $summary.portOnlyIds)
