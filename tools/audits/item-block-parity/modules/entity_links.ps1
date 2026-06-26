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
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot 'item_block_entity_links_report.json' }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot 'item_block_entity_links_report.md' }

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
        check = 'entity_links'
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

$rulesPath = Join-Path $RulesRoot 'entity-links-rules.json'
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{
        sourceRoots = @('src/main/java')
        sourceExtensions = @('.java')
        resourceRoots = @('src/main/resources/data', 'src/main/resources/assets')
        resourceExtensions = @('.json')
        entityTerms = @('EntityType','Mob','LivingEntity','SpawnPlacement','AttributeSupplier')
        spawnEggTerms = @('SpawnEggItem','spawn_egg','egg')
        attributeTerms = @('AttributeSupplier','Attributes.','createAttributes')
        rendererTerms = @('EntityRenderers.register','EntityRenderer','MobRenderer')
        dataTerms = @('entity_type','spawn_egg','spawns','entities')
        manifestCandidateTerms = @('spawn_egg','golem','wisp','pech','cultist','projectile','entity')
        resourceEntityTerms = @('entity_type','spawn_egg','spawn','entity')
        status = [pscustomobject]@{ pass = 'ENTITY_LINKS_PASS'; review = 'ENTITY_LINKS_REVIEW_NEEDED'; error = 'ENTITY_LINKS_ERROR' }
        markdownSampleLimit = 120
    }
}
$passStatus = if ($rules.status.pass) { [string]$rules.status.pass } else { 'ENTITY_LINKS_PASS' }
$reviewStatus = if ($rules.status.review) { [string]$rules.status.review } else { 'ENTITY_LINKS_REVIEW_NEEDED' }
$errorStatus = if ($rules.status.error) { [string]$rules.status.error } else { 'ENTITY_LINKS_ERROR' }
$sampleLimit = if ($rules.markdownSampleLimit) { [int]$rules.markdownSampleLimit } else { 120 }

if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw ('Port manifest not found: ' + $PortManifestPath) }
$portManifest = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$portEntries = Get-Entries $portManifest
$sourceFiles = Get-FilesFromRoots -Roots @($rules.sourceRoots) -Extensions @($rules.sourceExtensions)
$resourceFiles = Get-FilesFromRoots -Roots @($rules.resourceRoots) -Extensions @($rules.resourceExtensions)
$rows = @()
$entitySourceFiles = 0
$spawnEggSourceFiles = 0
$attributeSourceFiles = 0
$rendererSourceFiles = 0
$resourceEntityFiles = 0
$manifestCandidateIds = 0

foreach ($file in @($sourceFiles)) {
    $text = Get-Content -Raw -LiteralPath $file.FullName
    $entityHits = Get-MatchedTerms -Text $text -Terms @($rules.entityTerms)
    $spawnEggHits = Get-MatchedTerms -Text $text -Terms @($rules.spawnEggTerms)
    $attributeHits = Get-MatchedTerms -Text $text -Terms @($rules.attributeTerms)
    $rendererHits = Get-MatchedTerms -Text $text -Terms @($rules.rendererTerms)
    if ($entityHits.Count -eq 0 -and $spawnEggHits.Count -eq 0 -and $attributeHits.Count -eq 0 -and $rendererHits.Count -eq 0) { continue }
    $rel = ConvertTo-RelativeRepoPath $file.FullName
    $evidence = @()
    if ($entityHits.Count -gt 0) { $evidence += ('entity terms: ' + ($entityHits -join ', ')); $entitySourceFiles++ }
    if ($spawnEggHits.Count -gt 0) { $evidence += ('spawn egg terms: ' + ($spawnEggHits -join ', ')); $spawnEggSourceFiles++ }
    if ($attributeHits.Count -gt 0) { $evidence += ('attribute terms: ' + ($attributeHits -join ', ')); $attributeSourceFiles++ }
    if ($rendererHits.Count -gt 0) { $evidence += ('renderer terms: ' + ($rendererHits -join ', ')); $rendererSourceFiles++ }
    $classification = 'entity_source_review'
    if ($spawnEggHits.Count -gt 0) { $classification = 'spawn_egg_source_review' }
    elseif ($attributeHits.Count -gt 0) { $classification = 'entity_attribute_source_review' }
    elseif ($rendererHits.Count -gt 0) { $classification = 'entity_renderer_source_review' }
    Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification $classification -Subject $file.Name -Path $rel -Evidence $evidence -Recommendation 'Confirm EntityType, spawn egg, attribute, renderer and spawn policy against legacy behavior or mark explicit intentional differences.'
}

foreach ($file in @($resourceFiles)) {
    $rel = ConvertTo-RelativeRepoPath $file.FullName
    $text = (Get-Content -Raw -LiteralPath $file.FullName) + ' ' + $rel
    $resourceHits = Get-MatchedTerms -Text $text -Terms @($rules.resourceEntityTerms)
    $dataHits = Get-MatchedTerms -Text $text -Terms @($rules.dataTerms)
    if ($resourceHits.Count -eq 0 -and $dataHits.Count -eq 0) { continue }
    $resourceEntityFiles++
    $evidence = @()
    if ($resourceHits.Count -gt 0) { $evidence += ('resource entity terms: ' + ($resourceHits -join ', ')) }
    if ($dataHits.Count -gt 0) { $evidence += ('data terms: ' + ($dataHits -join ', ')) }
    Add-Row -Rows ([ref]$rows) -Status $passStatus -Severity 'info' -Classification 'entity_resource_evidence' -Subject $file.Name -Path $rel -Evidence $evidence -Recommendation 'Resource contains entity/spawn-egg data clues.'
}

foreach ($entry in @($portEntries)) {
    $id = Get-EntryId $entry
    if ([string]::IsNullOrWhiteSpace($id)) { continue }
    $entryText = (Get-EntrySearchText $entry) + ' ' + $id
    $hits = Get-MatchedTerms -Text $entryText -Terms @($rules.manifestCandidateTerms)
    if ($hits.Count -gt 0) {
        $manifestCandidateIds++
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'manifest_entity_candidate' -Subject $id -Path '' -Evidence @('manifest terms: ' + ($hits -join ', ')) -Recommendation 'Confirm whether this ID requires EntityType, spawn egg, attributes, renderer, spawn data or explicit intentional non-entity policy.'
    }
}

if ($rows.Count -eq 0) {
    Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'no_entity_link_evidence' -Subject 'source/resource scan' -Path (ConvertTo-RelativeRepoPath $PortRoot) -Evidence @('No configured entity/spawn-egg evidence was found.') -Recommendation 'Confirm whether entity support is not started or rules need additional terms.'
}

$orderedRows = @($rows | Sort-Object severity, classification, path, subject)
$summary = [ordered]@{
    rows = @($orderedRows).Count
    pass = @($orderedRows | Where-Object { $_.status -eq $passStatus }).Count
    reviewNeeded = @($orderedRows | Where-Object { $_.status -eq $reviewStatus }).Count
    errors = @($orderedRows | Where-Object { $_.status -eq $errorStatus }).Count
    sourceFilesScanned = @($sourceFiles).Count
    resourceFilesScanned = @($resourceFiles).Count
    entitySourceFiles = $entitySourceFiles
    spawnEggSourceFiles = $spawnEggSourceFiles
    attributeSourceFiles = $attributeSourceFiles
    rendererSourceFiles = $rendererSourceFiles
    resourceEntityFiles = $resourceEntityFiles
    manifestCandidateIds = $manifestCandidateIds
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString('o')
    policy = 'Report-only entity link audit. Review rows identify EntityType, spawn egg, attribute, renderer, data-resource and manifest entity candidates; they are not gameplay parity failures.'
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
$lines += '# Item/block entity link audit'
$lines += ''
$lines += ('Generated: ' + $report.generatedAtUtc)
$lines += ''
$lines += 'Policy: report-only entity/spawn-egg review. Review rows are EntityType, spawn egg, attribute, renderer and spawn-data policy work, not gameplay parity failures.'
$lines += ''
$lines += '## Summary'
$lines += ''
$lines += ('- Rows: ' + $summary.rows)
$lines += ('- Pass: ' + $summary.pass)
$lines += ('- Review needed: ' + $summary.reviewNeeded)
$lines += ('- Errors: ' + $summary.errors)
$lines += ('- Source files scanned: ' + $summary.sourceFilesScanned)
$lines += ('- Resource files scanned: ' + $summary.resourceFilesScanned)
$lines += ('- Entity source files: ' + $summary.entitySourceFiles)
$lines += ('- Spawn egg source files: ' + $summary.spawnEggSourceFiles)
$lines += ('- Attribute source files: ' + $summary.attributeSourceFiles)
$lines += ('- Renderer source files: ' + $summary.rendererSourceFiles)
$lines += ('- Resource entity files: ' + $summary.resourceEntityFiles)
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
    $lines += '| ENTITY_LINKS_PASS | <all> | <all> | <all> | Source/resource scan found no review-only rows. | None. |'
}
$lines += ''
$lines += ('Only the first ' + $sampleLimit + ' non-pass rows are shown in Markdown; JSON contains all rows.')
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output ('Entity link audit report: ' + $OutputMarkdown)
Write-Output ('Rows={0}, pass={1}, reviewNeeded={2}, errors={3}, sourceFilesScanned={4}, resourceFilesScanned={5}, entitySourceFiles={6}, spawnEggSourceFiles={7}, attributeSourceFiles={8}, rendererSourceFiles={9}, resourceEntityFiles={10}, manifestCandidateIds={11}' -f $summary.rows, $summary.pass, $summary.reviewNeeded, $summary.errors, $summary.sourceFilesScanned, $summary.resourceFilesScanned, $summary.entitySourceFiles, $summary.spawnEggSourceFiles, $summary.attributeSourceFiles, $summary.rendererSourceFiles, $summary.resourceEntityFiles, $summary.manifestCandidateIds)
