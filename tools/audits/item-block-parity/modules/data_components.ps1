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
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot 'item_block_data_components_report.json' }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot 'item_block_data_components_report.md' }

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
function Get-EntryKind($Entry) {
    foreach ($name in @('kind','type','declaredType')) {
        $prop = $Entry.PSObject.Properties[$name]
        if ($null -ne $prop -and -not [string]::IsNullOrWhiteSpace([string]$prop.Value)) { return ([string]$prop.Value).ToLowerInvariant() }
    }
    return ''
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
        check = 'data_components'
        status = $Status
        severity = $Severity
        classification = $Classification
        subject = $Subject
        path = $Path
        evidence = @($Evidence)
        recommendation = $Recommendation
    }
}
function Get-SourceFiles {
    param([string[]]$SourceRoots, [string[]]$Extensions)
    $files = @()
    foreach ($root in @($SourceRoots)) {
        if ([string]::IsNullOrWhiteSpace($root)) { continue }
        $fullRoot = Join-Path $PortRoot $root
        if (-not (Test-Path -LiteralPath $fullRoot -PathType Container)) { continue }
        foreach ($ext in @($Extensions)) {
            if ([string]::IsNullOrWhiteSpace($ext)) { continue }
            $files += @(Get-ChildItem -LiteralPath $fullRoot -Recurse -File -Filter ('*' + $ext))
        }
    }
    return @($files | Sort-Object FullName -Unique)
}

$rulesPath = Join-Path $RulesRoot 'data-components-rules.json'
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{
        sourceRoots = @('src/main/java')
        sourceFileExtensions = @('.java')
        dataComponentTerms = @('DataComponentType','DataComponents')
        legacyNbtTerms = @('CompoundTag','getTag','setTag','CustomData')
        itemStackTerms = @('ItemStack')
        candidateIdPatterns = @('caster','focus','wand')
        maxMarkdownRows = 120
        status = [pscustomobject]@{ pass = 'DATA_COMPONENTS_PASS'; review = 'DATA_COMPONENTS_REVIEW_NEEDED'; error = 'DATA_COMPONENTS_ERROR' }
    }
}
$passStatus = if ($rules.status.pass) { [string]$rules.status.pass } else { 'DATA_COMPONENTS_PASS' }
$reviewStatus = if ($rules.status.review) { [string]$rules.status.review } else { 'DATA_COMPONENTS_REVIEW_NEEDED' }
$errorStatus = if ($rules.status.error) { [string]$rules.status.error } else { 'DATA_COMPONENTS_ERROR' }
$maxMarkdownRows = if ($rules.maxMarkdownRows) { [int]$rules.maxMarkdownRows } else { 120 }

if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw ('Port manifest not found: ' + $PortManifestPath) }
if (-not (Test-Path -LiteralPath $LegacyManifestPath -PathType Leaf)) { throw ('Legacy manifest not found: ' + $LegacyManifestPath) }
$portManifest = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$legacyManifest = Get-Content -Raw -LiteralPath $LegacyManifestPath | ConvertFrom-Json
$portEntries = @(Get-Entries $portManifest)
$legacyEntries = @(Get-Entries $legacyManifest)
$rows = @()

$sourceFiles = @(Get-SourceFiles -SourceRoots @($rules.sourceRoots) -Extensions @($rules.sourceFileExtensions))
$dataComponentFileCount = 0
$legacyNbtFileCount = 0
foreach ($file in $sourceFiles) {
    $text = Get-Content -Raw -LiteralPath $file.FullName
    $componentHits = @(Get-MatchedTerms -Text $text -Terms @($rules.dataComponentTerms))
    $nbtHits = @(Get-MatchedTerms -Text $text -Terms @($rules.legacyNbtTerms))
    $stackHits = @(Get-MatchedTerms -Text $text -Terms @($rules.itemStackTerms))
    $relative = ConvertTo-RelativeRepoPath $file.FullName
    if ($componentHits.Count -gt 0) { $dataComponentFileCount++ }
    if ($nbtHits.Count -gt 0) { $legacyNbtFileCount++ }

    if ($componentHits.Count -gt 0 -and $nbtHits.Count -eq 0) {
        Add-Row -Rows ([ref]$rows) -Status $passStatus -Severity 'info' -Classification 'modern_data_component_evidence' -Subject $file.Name -Path $relative -Evidence $componentHits -Recommendation 'Modern data component API evidence is present without legacy tag evidence in this file.'
    } elseif ($componentHits.Count -gt 0 -and $nbtHits.Count -gt 0) {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'component_and_legacy_tag_bridge' -Subject $file.Name -Path $relative -Evidence @($componentHits + $nbtHits | Select-Object -Unique) -Recommendation 'Review whether legacy tag state is intentionally bridged into typed data components or confined to save/load code.'
    } elseif ($nbtHits.Count -gt 0 -and $stackHits.Count -gt 0) {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'legacy_itemstack_tag_candidate' -Subject $file.Name -Path $relative -Evidence @($nbtHits + $stackHits | Select-Object -Unique) -Recommendation 'Review ItemStack tag-like state and classify it as data component, capability behavior, or save-only state.'
    } elseif ($nbtHits.Count -gt 0) {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'legacy_tag_or_save_load_evidence' -Subject $file.Name -Path $relative -Evidence $nbtHits -Recommendation 'Review tag usage context. BlockEntity/entity persistence can remain tag-backed; ItemStack gameplay state should prefer typed data components.'
    }
}

$componentCandidateIds = @()
foreach ($entry in $portEntries) {
    $id = Get-EntryId $entry
    if ([string]::IsNullOrWhiteSpace($id)) { continue }
    $kind = Get-EntryKind $entry
    if ($kind -ne '' -and $kind -notmatch 'item|block') { continue }
    foreach ($pattern in @($rules.candidateIdPatterns)) {
        if ([string]::IsNullOrWhiteSpace($pattern)) { continue }
        if ($id -like ('*' + $pattern + '*')) {
            $componentCandidateIds += $id
            Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'registry_id_component_policy_candidate' -Subject $id -Path '' -Evidence @($pattern) -Recommendation 'Candidate ID family may need explicit policy: separate item, typed data component, capability behavior, or intentionally stateless item.'
            break
        }
    }
}

if ($rows.Count -eq 0) {
    Add-Row -Rows ([ref]$rows) -Status $passStatus -Severity 'info' -Classification 'no_data_component_or_legacy_tag_evidence' -Subject 'source-scan' -Path '' -Evidence @() -Recommendation 'No data component or legacy tag evidence was found by configured rules.'
}

$orderedRows = @($rows | Sort-Object severity, classification, path, subject)
$summary = [ordered]@{
    rows = @($orderedRows).Count
    pass = @($orderedRows | Where-Object { $_.status -eq $passStatus }).Count
    reviewNeeded = @($orderedRows | Where-Object { $_.status -eq $reviewStatus }).Count
    errors = @($orderedRows | Where-Object { $_.status -eq $errorStatus }).Count
    sourceFilesScanned = @($sourceFiles).Count
    dataComponentFiles = $dataComponentFileCount
    legacyNbtFiles = $legacyNbtFileCount
    componentCandidateIds = @($componentCandidateIds | Select-Object -Unique).Count
    legacyEntries = @($legacyEntries).Count
    portEntries = @($portEntries).Count
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString('o')
    policy = 'Report-only legacy NBT/data component bridge audit. Review rows classify potential ItemStack state and save/load/tag usage; they are not gameplay parity failures.'
    inputs = [ordered]@{
        legacyManifest = ConvertTo-RelativeRepoPath $LegacyManifestPath
        portManifest = ConvertTo-RelativeRepoPath $PortManifestPath
        portRoot = ConvertTo-RelativeRepoPath $PortRoot
        rules = ConvertTo-RelativeRepoPath $rulesPath
        checks = @($Checks)
    }
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = @()
$lines += '# Item/block data component bridge audit'
$lines += ''
$lines += ('Generated: ' + $report.generatedAtUtc)
$lines += ''
$lines += 'Policy: report-only legacy NBT/data component bridge audit. Review rows are migration policy work, not gameplay parity failures.'
$lines += ''
$lines += '## Summary'
$lines += ''
$lines += ('- Rows: ' + $summary.rows)
$lines += ('- Pass: ' + $summary.pass)
$lines += ('- Review needed: ' + $summary.reviewNeeded)
$lines += ('- Errors: ' + $summary.errors)
$lines += ('- Source files scanned: ' + $summary.sourceFilesScanned)
$lines += ('- Data component files: ' + $summary.dataComponentFiles)
$lines += ('- Legacy NBT/tag files: ' + $summary.legacyNbtFiles)
$lines += ('- Candidate registry IDs: ' + $summary.componentCandidateIds)
$lines += ''
$lines += '## Non-pass sample'
$lines += ''
$lines += '| Status | Classification | Subject | Path | Evidence | Recommendation |'
$lines += '|---|---|---|---|---|---|'
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus } | Select-Object -First $maxMarkdownRows)
foreach ($row in $nonPass) {
    $evidence = if ($row.evidence) { (@($row.evidence) -join ', ').Replace('|', '\|') } else { '' }
    $recommendation = if ($row.recommendation) { $row.recommendation.Replace('|', '\|') } else { '' }
    $lines += ('| ' + $row.status + ' | ' + $row.classification + ' | ' + $row.subject + ' | ' + $row.path + ' | ' + $evidence + ' | ' + $recommendation + ' |')
}
if ($nonPass.Count -eq 0) {
    $lines += '| DATA_COMPONENTS_PASS | <all> | <all> | <none> | <none> | No review rows found. |'
}
$lines += ''
$lines += ('Only the first ' + $maxMarkdownRows + ' non-pass rows are shown in Markdown; JSON contains all rows.')
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output ('Data component bridge audit report: ' + $OutputMarkdown)
Write-Output ('Rows={0}, pass={1}, reviewNeeded={2}, errors={3}, sourceFilesScanned={4}, dataComponentFiles={5}, legacyNbtFiles={6}, componentCandidateIds={7}' -f $summary.rows, $summary.pass, $summary.reviewNeeded, $summary.errors, $summary.sourceFilesScanned, $summary.dataComponentFiles, $summary.legacyNbtFiles, $summary.componentCandidateIds)
