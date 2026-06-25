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
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot 'item_block_variants_report.json' }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot 'item_block_variants_report.md' }

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
function Get-Entries($Manifest) {
    foreach ($name in @('entries', 'items', 'blocks')) {
        $prop = $Manifest.PSObject.Properties[$name]
        if ($null -ne $prop -and $null -ne $prop.Value) { return @($prop.Value) }
    }
    return @()
}
function Get-VariantInfo {
    param(
        [string]$Id,
        [string[]]$Suffixes
    )
    $normalized = Normalize-Id $Id
    foreach ($suffix in @($Suffixes | Sort-Object Length -Descending)) {
        if ([string]::IsNullOrWhiteSpace($suffix)) { continue }
        $tail = '_' + $suffix.ToLowerInvariant()
        if ($normalized.EndsWith($tail) -and $normalized.Length -gt $tail.Length) {
            return [pscustomobject][ordered]@{
                id = $normalized
                family = $normalized.Substring(0, $normalized.Length - $tail.Length)
                variant = $suffix.ToLowerInvariant()
                source = 'suffix_rule'
            }
        }
    }
    if ($normalized -match '^(?<family>.+)_(?<variant>[0-9]+)$') {
        return [pscustomobject][ordered]@{
            id = $normalized
            family = $Matches.family
            variant = $Matches.variant
            source = 'numeric_suffix'
        }
    }
    return $null
}
function Add-FamilyId {
    param(
        [hashtable]$Groups,
        [string]$Family,
        [string]$Id
    )
    if ([string]::IsNullOrWhiteSpace($Family) -or [string]::IsNullOrWhiteSpace($Id)) { return }
    if (-not $Groups.ContainsKey($Family)) { $Groups[$Family] = @() }
    if ($Id -notin @($Groups[$Family])) { $Groups[$Family] = @($Groups[$Family] + $Id) }
}
function Build-VariantGroups {
    param(
        [object[]]$Entries,
        [string[]]$Suffixes
    )
    $groups = @{}
    foreach ($entry in @($Entries)) {
        $id = Get-EntryId $entry
        if ([string]::IsNullOrWhiteSpace($id)) { continue }
        $info = Get-VariantInfo -Id $id -Suffixes $Suffixes
        if ($null -eq $info) { continue }
        Add-FamilyId -Groups $groups -Family $info.family -Id $info.id
    }
    return $groups
}
function Add-Row {
    param(
        [ref]$Rows,
        [string]$Status,
        [string]$Severity,
        [string]$Family,
        [string]$Classification,
        [string[]]$LegacyIds,
        [string[]]$PortIds,
        [string]$Evidence
    )
    $Rows.Value += [pscustomobject][ordered]@{
        check = 'variants'
        status = $Status
        severity = $Severity
        family = $Family
        classification = $Classification
        legacyIds = @($LegacyIds)
        portIds = @($PortIds)
        evidence = $Evidence
    }
}
function Get-SortedIds([object]$Value) {
    return @($Value | Where-Object { -not [string]::IsNullOrWhiteSpace([string]$_) } | Sort-Object -Unique)
}
function Join-Ids([string[]]$Ids) {
    if (@($Ids).Count -eq 0) { return '' }
    return (@($Ids) -join ', ')
}

$rulesPath = Join-Path $RulesRoot 'variants-rules.json'
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{
        variantSuffixes = @('black', 'blue', 'brown', 'cyan', 'gray', 'green', 'light_blue', 'light_gray', 'lime', 'magenta', 'orange', 'pink', 'purple', 'red', 'white', 'yellow')
        minimumFamilySize = 2
        status = [pscustomobject]@{ pass = 'VARIANTS_PASS'; review = 'VARIANTS_REVIEW_NEEDED'; error = 'VARIANTS_ERROR' }
    }
}
$passStatus = if ($rules.status.pass) { [string]$rules.status.pass } else { 'VARIANTS_PASS' }
$reviewStatus = if ($rules.status.review) { [string]$rules.status.review } else { 'VARIANTS_REVIEW_NEEDED' }
$errorStatus = if ($rules.status.error) { [string]$rules.status.error } else { 'VARIANTS_ERROR' }
$suffixes = @($rules.variantSuffixes | ForEach-Object { ([string]$_).Trim().ToLowerInvariant() } | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Sort-Object -Unique)
$minimumFamilySize = if ($rules.minimumFamilySize) { [int]$rules.minimumFamilySize } else { 2 }

if (-not (Test-Path -LiteralPath $LegacyManifestPath -PathType Leaf)) { throw ('Legacy manifest not found: ' + $LegacyManifestPath) }
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw ('Port manifest not found: ' + $PortManifestPath) }

$legacyManifest = Get-Content -Raw -LiteralPath $LegacyManifestPath | ConvertFrom-Json
$portManifest = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$legacyEntries = Get-Entries $legacyManifest
$portEntries = Get-Entries $portManifest
$legacyGroups = Build-VariantGroups -Entries $legacyEntries -Suffixes $suffixes
$portGroups = Build-VariantGroups -Entries $portEntries -Suffixes $suffixes
$families = @(@($legacyGroups.Keys) + @($portGroups.Keys) | Sort-Object -Unique)
$rows = @()

foreach ($family in $families) {
    $legacyIds = if ($legacyGroups.ContainsKey($family)) { Get-SortedIds $legacyGroups[$family] } else { @() }
    $portIds = if ($portGroups.ContainsKey($family)) { Get-SortedIds $portGroups[$family] } else { @() }
    $legacyCount = @($legacyIds).Count
    $portCount = @($portIds).Count
    $interesting = ($legacyCount -ge $minimumFamilySize) -or ($portCount -ge $minimumFamilySize) -or (($legacyCount -ne $portCount) -and (($legacyCount + $portCount) -gt 1))
    if (-not $interesting) { continue }

    $missingInPort = @($legacyIds | Where-Object { $_ -notin $portIds })
    $portOnly = @($portIds | Where-Object { $_ -notin $legacyIds })

    if ($legacyCount -gt 0 -and $portCount -gt 0 -and $missingInPort.Count -eq 0 -and $portOnly.Count -eq 0) {
        Add-Row -Rows ([ref]$rows) -Status $passStatus -Severity 'info' -Family $family -Classification 'variant_family_exact_match' -LegacyIds $legacyIds -PortIds $portIds -Evidence 'Legacy and port variant family IDs match exactly.'
    } elseif ($legacyCount -gt 1 -and $portCount -eq 1) {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Family $family -Classification 'legacy_variants_collapsed_in_port_candidate' -LegacyIds $legacyIds -PortIds $portIds -Evidence 'Multiple legacy variant IDs map to one apparent port family ID; review split/collapse policy.'
    } elseif ($legacyCount -eq 1 -and $portCount -gt 1) {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Family $family -Classification 'port_split_variant_candidate' -LegacyIds $legacyIds -PortIds $portIds -Evidence 'Port has multiple apparent variants for a single legacy family ID; review split policy.'
    } elseif ($legacyCount -gt 0 -and $portCount -eq 0) {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Family $family -Classification 'missing_port_variant_family' -LegacyIds $legacyIds -PortIds $portIds -Evidence 'Legacy variant family has no corresponding port variant family.'
    } elseif ($legacyCount -eq 0 -and $portCount -gt 0) {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Family $family -Classification 'port_only_variant_family' -LegacyIds $legacyIds -PortIds $portIds -Evidence 'Port variant family has no corresponding legacy variant family.'
    } else {
        $evidence = 'Legacy and port variant family memberships differ.'
        if ($missingInPort.Count -gt 0) { $evidence += ' Missing in port: ' + (Join-Ids $missingInPort) + '.' }
        if ($portOnly.Count -gt 0) { $evidence += ' Port-only variants: ' + (Join-Ids $portOnly) + '.' }
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Family $family -Classification 'variant_family_mismatch' -LegacyIds $legacyIds -PortIds $portIds -Evidence $evidence
    }
}

$orderedRows = @($rows | Sort-Object severity, classification, family)
$summary = [ordered]@{
    rows = @($orderedRows).Count
    pass = @($orderedRows | Where-Object { $_.status -eq $passStatus }).Count
    reviewNeeded = @($orderedRows | Where-Object { $_.status -eq $reviewStatus }).Count
    errors = @($orderedRows | Where-Object { $_.status -eq $errorStatus }).Count
    exactFamilies = @($orderedRows | Where-Object { $_.classification -eq 'variant_family_exact_match' }).Count
    reviewFamilies = @($orderedRows | Where-Object { $_.status -eq $reviewStatus }).Count
    legacyFamilies = @($legacyGroups.Keys).Count
    portFamilies = @($portGroups.Keys).Count
    legacyEntries = @($legacyEntries).Count
    portEntries = @($portEntries).Count
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString('o')
    policy = 'Report-only metadata/split-ID variant audit. Review rows identify possible split, collapse, missing or port-only variant families; they are not gameplay parity failures.'
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
$report | ConvertTo-Json -Depth 18 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = @()
$lines += '# Item/block variant split audit'
$lines += ''
$lines += ('Generated: ' + $report.generatedAtUtc)
$lines += ''
$lines += 'Policy: report-only metadata/split-ID variant audit. Review rows are registry identity work, not gameplay parity failures.'
$lines += ''
$lines += '## Summary'
$lines += ''
$lines += ('- Rows: ' + $summary.rows)
$lines += ('- Pass: ' + $summary.pass)
$lines += ('- Review needed: ' + $summary.reviewNeeded)
$lines += ('- Errors: ' + $summary.errors)
$lines += ('- Exact variant families: ' + $summary.exactFamilies)
$lines += ('- Review families: ' + $summary.reviewFamilies)
$lines += ('- Legacy variant families: ' + $summary.legacyFamilies)
$lines += ('- Port variant families: ' + $summary.portFamilies)
$lines += ''
$lines += '## Non-pass sample'
$lines += ''
$lines += '| Status | Classification | Family | Legacy IDs | Port IDs | Evidence |'
$lines += '|---|---|---|---|---|---|'
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus } | Select-Object -First 100)
foreach ($row in $nonPass) {
    $legacyText = Join-Ids @($row.legacyIds)
    $portText = Join-Ids @($row.portIds)
    $evidence = if ($row.evidence) { $row.evidence.Replace('|', '\|') } else { '' }
    $lines += ('| ' + $row.status + ' | ' + $row.classification + ' | ' + $row.family + ' | ' + $legacyText + ' | ' + $portText + ' | ' + $evidence + ' |')
}
if ($nonPass.Count -eq 0) {
    $lines += '| VARIANTS_PASS | <all> | <all> | <all> | <all> | All detected variant families match exactly. |'
}
$lines += ''
$lines += 'Only the first 100 non-pass rows are shown in Markdown; JSON contains all rows.'
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output ('Variant split audit report: ' + $OutputMarkdown)
Write-Output ('Rows={0}, pass={1}, reviewNeeded={2}, errors={3}, exactFamilies={4}, reviewFamilies={5}, legacyFamilies={6}, portFamilies={7}' -f $summary.rows, $summary.pass, $summary.reviewNeeded, $summary.errors, $summary.exactFamilies, $summary.reviewFamilies, $summary.legacyFamilies, $summary.portFamilies)
