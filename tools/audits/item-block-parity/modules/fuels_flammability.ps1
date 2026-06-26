[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$LegacyManifestPath,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [Parameter(Mandatory = $true)][string]$PortRoot,
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = 'Stop'
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path
$PortRoot = (Resolve-Path -LiteralPath $PortRoot).Path
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot 'tools/audits/item-block-parity/rules' }
$reportRoot = Join-Path $RepoRoot 'tools/reports/local/item-block-parity'
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot 'item_block_fuels_flammability_report.json' }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot 'item_block_fuels_flammability_report.md' }

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
        check = 'fuels_flammability'
        status = $Status
        severity = $Severity
        classification = $Classification
        subject = $Subject
        path = $Path
        evidence = @($Evidence)
        recommendation = $Recommendation
    }
}
function Get-FilesFromRoots {
    param([string[]]$Roots, [string[]]$Extensions)
    $files = @()
    foreach ($root in @($Roots)) {
        if ([string]::IsNullOrWhiteSpace($root)) { continue }
        $fullRoot = Join-Path $PortRoot $root
        if (-not (Test-Path -LiteralPath $fullRoot -PathType Container)) { continue }
        $all = @(Get-ChildItem -LiteralPath $fullRoot -Recurse -File -ErrorAction SilentlyContinue)
        foreach ($file in $all) {
            if (@($Extensions) -contains $file.Extension) { $files += $file }
        }
    }
    return @($files | Sort-Object FullName -Unique)
}

$rulesPath = Join-Path $RulesRoot 'fuels-flammability-rules.json'
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{
        sourceRoots = @('src/main/java')
        sourceExtensions = @('.java')
        resourceRoots = @('src/main/resources/data')
        resourceExtensions = @('.json')
        fuelTerms = @('FurnaceFuelBurnTimeEvent','fuel','burnTime')
        flammabilityTerms = @('flammable','fireSpread','FireBlock')
        combustibleResourceTerms = @('logs','planks','wood','coal','fuel','flammable')
        manifestCandidateTerms = @('log','wood','plank','coal','fuel','fire','flame')
        sourceReviewTerms = @('shrink(','consume','ignite','burn')
        status = [pscustomobject]@{ pass = 'FUELS_FLAMMABILITY_PASS'; review = 'FUELS_FLAMMABILITY_REVIEW_NEEDED'; error = 'FUELS_FLAMMABILITY_ERROR' }
        markdownSampleLimit = 120
    }
}
$passStatus = if ($rules.status.pass) { [string]$rules.status.pass } else { 'FUELS_FLAMMABILITY_PASS' }
$reviewStatus = if ($rules.status.review) { [string]$rules.status.review } else { 'FUELS_FLAMMABILITY_REVIEW_NEEDED' }
$errorStatus = if ($rules.status.error) { [string]$rules.status.error } else { 'FUELS_FLAMMABILITY_ERROR' }
$sampleLimit = if ($rules.markdownSampleLimit) { [int]$rules.markdownSampleLimit } else { 120 }

if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw ('Port manifest not found: ' + $PortManifestPath) }
$portManifest = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$portEntries = Get-Entries $portManifest
$sourceFiles = Get-FilesFromRoots -Roots @($rules.sourceRoots) -Extensions @($rules.sourceExtensions)
$resourceFiles = Get-FilesFromRoots -Roots @($rules.resourceRoots) -Extensions @($rules.resourceExtensions)
$rows = @()
$fuelSourceFiles = 0
$flammabilitySourceFiles = 0
$combustibleResourceFiles = 0
$manifestCandidateIds = 0

foreach ($file in @($sourceFiles)) {
    $text = Get-Content -Raw -LiteralPath $file.FullName
    $fuelHits = Get-MatchedTerms -Text $text -Terms @($rules.fuelTerms)
    $flammabilityHits = Get-MatchedTerms -Text $text -Terms @($rules.flammabilityTerms)
    $reviewHits = Get-MatchedTerms -Text $text -Terms @($rules.sourceReviewTerms)
    if ($fuelHits.Count -eq 0 -and $flammabilityHits.Count -eq 0) { continue }
    $rel = ConvertTo-RelativeRepoPath $file.FullName
    $evidence = @()
    if ($fuelHits.Count -gt 0) { $evidence += ('fuel terms: ' + ($fuelHits -join ', ')); $fuelSourceFiles++ }
    if ($flammabilityHits.Count -gt 0) { $evidence += ('flammability terms: ' + ($flammabilityHits -join ', ')); $flammabilitySourceFiles++ }
    if ($reviewHits.Count -gt 0) { $evidence += ('review terms: ' + ($reviewHits -join ', ')) }
    if ($fuelHits.Count -gt 0 -and $flammabilityHits.Count -gt 0) {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'fuel_and_flammability_source_review' -Subject $file.Name -Path $rel -Evidence $evidence -Recommendation 'Confirm this file separates burn-time policy from block fire-spread/destruction policy.'
    } elseif ($fuelHits.Count -gt 0) {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'fuel_source_review' -Subject $file.Name -Path $rel -Evidence $evidence -Recommendation 'Confirm burn times match legacy policy or are intentionally changed.'
    } else {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'flammability_source_review' -Subject $file.Name -Path $rel -Evidence $evidence -Recommendation 'Confirm fire spread/flammability behavior matches legacy policy or is intentionally changed.'
    }
}

foreach ($file in @($resourceFiles)) {
    $rel = ConvertTo-RelativeRepoPath $file.FullName
    $text = (Get-Content -Raw -LiteralPath $file.FullName) + ' ' + $rel
    $resourceHits = Get-MatchedTerms -Text $text -Terms @($rules.combustibleResourceTerms)
    if ($resourceHits.Count -eq 0) { continue }
    $combustibleResourceFiles++
    Add-Row -Rows ([ref]$rows) -Status $passStatus -Severity 'info' -Classification 'combustible_resource_evidence' -Subject $file.Name -Path $rel -Evidence @('resource terms: ' + ($resourceHits -join ', ')) -Recommendation 'Data/tag resource contains combustible or fuel-related clues.'
}

foreach ($entry in @($portEntries)) {
    $id = Get-EntryId $entry
    if ([string]::IsNullOrWhiteSpace($id)) { continue }
    $entryText = (Get-EntrySearchText $entry) + ' ' + $id
    $hits = Get-MatchedTerms -Text $entryText -Terms @($rules.manifestCandidateTerms)
    if ($hits.Count -gt 0) {
        $manifestCandidateIds++
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'manifest_fuel_flammability_candidate' -Subject $id -Path '' -Evidence @('manifest terms: ' + ($hits -join ', ')) -Recommendation 'Confirm whether this ID needs fuel burn time, flammability/fire-spread tags or an explicit intentional non-fuel/non-flammable policy.'
    }
}

if ($rows.Count -eq 0) {
    Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'no_fuel_flammability_evidence' -Subject 'source/resource scan' -Path (ConvertTo-RelativeRepoPath $PortRoot) -Evidence @('No configured fuel or flammability evidence was found.') -Recommendation 'Confirm whether fuel/flammability support is not started or rules need additional terms.'
}

$orderedRows = @($rows | Sort-Object severity, classification, path, subject)
$summary = [ordered]@{
    rows = @($orderedRows).Count
    pass = @($orderedRows | Where-Object { $_.status -eq $passStatus }).Count
    reviewNeeded = @($orderedRows | Where-Object { $_.status -eq $reviewStatus }).Count
    errors = @($orderedRows | Where-Object { $_.status -eq $errorStatus }).Count
    sourceFilesScanned = @($sourceFiles).Count
    resourceFilesScanned = @($resourceFiles).Count
    fuelSourceFiles = $fuelSourceFiles
    flammabilitySourceFiles = $flammabilitySourceFiles
    combustibleResourceFiles = $combustibleResourceFiles
    manifestCandidateIds = $manifestCandidateIds
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString('o')
    policy = 'Report-only fuel and flammability audit. Review rows identify burn-time, fire-spread, combustible tag and manifest candidates; they are not gameplay parity failures.'
    inputs = [ordered]@{
        portManifest = ConvertTo-RelativeRepoPath $PortManifestPath
        portRoot = ConvertTo-RelativeRepoPath $PortRoot
        rules = ConvertTo-RelativeRepoPath $rulesPath
        checks = @($Checks)
    }
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 18 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = @()
$lines += '# Item/block fuel and flammability audit'
$lines += ''
$lines += ('Generated: ' + $report.generatedAtUtc)
$lines += ''
$lines += 'Policy: report-only fuel/flammability review. Review rows are burn-time/fire-spread policy work, not gameplay parity failures.'
$lines += ''
$lines += '## Summary'
$lines += ''
$lines += ('- Rows: ' + $summary.rows)
$lines += ('- Pass: ' + $summary.pass)
$lines += ('- Review needed: ' + $summary.reviewNeeded)
$lines += ('- Errors: ' + $summary.errors)
$lines += ('- Source files scanned: ' + $summary.sourceFilesScanned)
$lines += ('- Resource files scanned: ' + $summary.resourceFilesScanned)
$lines += ('- Fuel source files: ' + $summary.fuelSourceFiles)
$lines += ('- Flammability source files: ' + $summary.flammabilitySourceFiles)
$lines += ('- Combustible resource files: ' + $summary.combustibleResourceFiles)
$lines += ('- Manifest candidate IDs: ' + $summary.manifestCandidateIds)
$lines += ''
$lines += '## Non-pass sample'
$lines += ''
$lines += '| Status | Classification | Subject | Path | Evidence | Recommendation |'
$lines += '|---|---|---|---|---|---|'
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus } | Select-Object -First $sampleLimit)
foreach ($row in $nonPass) {
    $evidence = if ($row.evidence) { (@($row.evidence) -join '; ').Replace('|', '\|') } else { '' }
    $recommendation = if ($row.recommendation) { $row.recommendation.Replace('|', '\|') } else { '' }
    $lines += ('| ' + $row.status + ' | ' + $row.classification + ' | ' + $row.subject + ' | ' + $row.path + ' | ' + $evidence + ' | ' + $recommendation + ' |')
}
if ($nonPass.Count -eq 0) {
    $lines += '| FUELS_FLAMMABILITY_PASS | <all> | <all> | <all> | Source/resource scan found no review-only rows. | None. |'
}
$lines += ''
$lines += ('Only the first ' + $sampleLimit + ' non-pass rows are shown in Markdown; JSON contains all rows.')
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output ('Fuel and flammability audit report: ' + $OutputMarkdown)
Write-Output ('Rows={0}, pass={1}, reviewNeeded={2}, errors={3}, sourceFilesScanned={4}, resourceFilesScanned={5}, fuelSourceFiles={6}, flammabilitySourceFiles={7}, combustibleResourceFiles={8}, manifestCandidateIds={9}' -f $summary.rows, $summary.pass, $summary.reviewNeeded, $summary.errors, $summary.sourceFilesScanned, $summary.resourceFilesScanned, $summary.fuelSourceFiles, $summary.flammabilitySourceFiles, $summary.combustibleResourceFiles, $summary.manifestCandidateIds)
