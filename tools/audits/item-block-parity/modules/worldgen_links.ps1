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
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot 'item_block_worldgen_links_report.json' }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot 'item_block_worldgen_links_report.md' }

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
        check = 'worldgen_links'
        status = $Status
        severity = $Severity
        classification = $Classification
        subject = $Subject
        path = $Path
        evidence = @($Evidence)
        recommendation = $Recommendation
    }
}
function Get-FilesByRoots {
    param([string[]]$Roots, [string[]]$Extensions)
    $files = @()
    foreach ($root in @($Roots)) {
        if ([string]::IsNullOrWhiteSpace($root)) { continue }
        $fullRoot = Join-Path $PortRoot $root
        if (-not (Test-Path -LiteralPath $fullRoot -PathType Container)) { continue }
        foreach ($ext in @($Extensions)) {
            $files += @(Get-ChildItem -LiteralPath $fullRoot -Recurse -File -ErrorAction SilentlyContinue | Where-Object { $_.Extension -eq $ext })
        }
    }
    return @($files | Sort-Object FullName -Unique)
}

if ($Checks -and 'worldgen_links' -notin $Checks) { return }
$rulesPath = Join-Path $RulesRoot 'worldgen-links-rules.json'
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{
        statuses = [pscustomobject]@{ pass = 'WORLDGEN_LINK_PASS'; review = 'WORLDGEN_LINK_REVIEW_NEEDED'; error = 'WORLDGEN_LINK_ERROR' }
        sourceRoots = @('src/main/java')
        resourceRoots = @('src/main/resources', 'src/generated/resources')
        sourceExtensions = @('.java')
        resourceExtensions = @('.json', '.mcmeta')
        worldgenTerms = @('WorldGen','ConfiguredFeature','PlacedFeature','BiomeModifier')
        featureTerms = @('ConfiguredFeature','PlacedFeature','Feature','OreConfiguration','TreeConfiguration')
        biomeModifierTerms = @('BiomeModifier','biome_modifier','add_features','features')
        structureTerms = @('Structure','StructureSet','template_pool')
        dimensionTerms = @('DimensionType','LevelStem','dimension_type')
        resourceWorldgenTerms = @('worldgen','configured_feature','placed_feature','biome_modifier','structure','dimension')
        manifestCandidateTerms = @('ore','crystal','greatwood','silverwood','sapling','mound','obelisk','worldgen')
    }
}
$passStatus = [string]$rules.statuses.pass
$reviewStatus = [string]$rules.statuses.review
$errorStatus = [string]$rules.statuses.error
$rows = @()
$sourceFiles = @(Get-FilesByRoots -Roots @($rules.sourceRoots) -Extensions @($rules.sourceExtensions))
$resourceFiles = @(Get-FilesByRoots -Roots @($rules.resourceRoots) -Extensions @($rules.resourceExtensions))

$worldgenSourceFiles = 0
$featureSourceFiles = 0
$biomeModifierSourceFiles = 0
$structureSourceFiles = 0
$dimensionSourceFiles = 0
foreach ($file in $sourceFiles) {
    $text = Get-Content -Raw -LiteralPath $file.FullName
    $worldgenHits = Get-MatchedTerms -Text $text -Terms @($rules.worldgenTerms)
    $featureHits = Get-MatchedTerms -Text $text -Terms @($rules.featureTerms)
    $biomeHits = Get-MatchedTerms -Text $text -Terms @($rules.biomeModifierTerms)
    $structureHits = Get-MatchedTerms -Text $text -Terms @($rules.structureTerms)
    $dimensionHits = Get-MatchedTerms -Text $text -Terms @($rules.dimensionTerms)
    if ($worldgenHits.Count -eq 0 -and $featureHits.Count -eq 0 -and $biomeHits.Count -eq 0 -and $structureHits.Count -eq 0 -and $dimensionHits.Count -eq 0) { continue }
    $rel = ConvertTo-RelativeRepoPath $file.FullName
    $evidence = @()
    if ($worldgenHits.Count -gt 0) { $evidence += ('worldgen terms: ' + ($worldgenHits -join ', ')); $worldgenSourceFiles++ }
    if ($featureHits.Count -gt 0) { $evidence += ('feature terms: ' + ($featureHits -join ', ')); $featureSourceFiles++ }
    if ($biomeHits.Count -gt 0) { $evidence += ('biome modifier terms: ' + ($biomeHits -join ', ')); $biomeModifierSourceFiles++ }
    if ($structureHits.Count -gt 0) { $evidence += ('structure terms: ' + ($structureHits -join ', ')); $structureSourceFiles++ }
    if ($dimensionHits.Count -gt 0) { $evidence += ('dimension terms: ' + ($dimensionHits -join ', ')); $dimensionSourceFiles++ }
    $classification = 'worldgen_source_review'
    if ($biomeHits.Count -gt 0) { $classification = 'biome_modifier_source_review' }
    elseif ($featureHits.Count -gt 0) { $classification = 'feature_source_review' }
    elseif ($structureHits.Count -gt 0) { $classification = 'structure_source_review' }
    elseif ($dimensionHits.Count -gt 0) { $classification = 'dimension_source_review' }
    Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification $classification -Subject $file.Name -Path $rel -Evidence $evidence -Recommendation 'Confirm configured/placed feature, biome modifier, structure/dimension and generation-step policy against legacy behavior or mark explicit intentional differences.'
}

$resourceWorldgenFiles = 0
foreach ($file in $resourceFiles) {
    $text = Get-Content -Raw -LiteralPath $file.FullName -ErrorAction SilentlyContinue
    if ([string]::IsNullOrWhiteSpace($text)) { continue }
    $hits = Get-MatchedTerms -Text $text -Terms @($rules.resourceWorldgenTerms)
    $pathHint = (ConvertTo-RelativeRepoPath $file.FullName).ToLowerInvariant()
    if ($hits.Count -eq 0 -and $pathHint -notmatch 'worldgen|biome_modifier|configured_feature|placed_feature|structure|dimension') { continue }
    $resourceWorldgenFiles++
    $evidence = @()
    if ($hits.Count -gt 0) { $evidence += ('resource worldgen terms: ' + ($hits -join ', ')) }
    if ($pathHint -match 'worldgen|biome_modifier|configured_feature|placed_feature|structure|dimension') { $evidence += 'path suggests worldgen/data boundary' }
    Add-Row -Rows ([ref]$rows) -Status $passStatus -Severity 'info' -Classification 'worldgen_resource_evidence' -Subject $file.Name -Path (ConvertTo-RelativeRepoPath $file.FullName) -Evidence $evidence -Recommendation 'Resource contains worldgen-linked data clues.'
}

$manifestCandidateIds = 0
if (Test-Path -LiteralPath $PortManifestPath -PathType Leaf) {
    $manifest = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
    foreach ($entry in @(Get-Entries $manifest)) {
        $id = Get-EntryId $entry
        if ([string]::IsNullOrWhiteSpace($id)) { continue }
        $searchText = (Get-EntrySearchText $entry) + ' ' + $id
        $hits = Get-MatchedTerms -Text $searchText -Terms @($rules.manifestCandidateTerms)
        if ($hits.Count -eq 0) { continue }
        $manifestCandidateIds++
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'manifest_worldgen_candidate' -Subject $id -Path '' -Evidence @('manifest terms: ' + ($hits -join ', ')) -Recommendation 'Confirm whether this ID participates in ore/tree/plant/structure/worldgen data or explicitly classify as non-worldgen content.'
    }
}
if ($rows.Count -eq 0) {
    Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'no_worldgen_link_evidence' -Subject 'source/resource scan' -Path (ConvertTo-RelativeRepoPath $PortRoot) -Evidence @('No configured worldgen-linked evidence was found.') -Recommendation 'Confirm whether worldgen support is not started or rules need additional terms.'
}

$summary = [pscustomobject][ordered]@{
    rows = $rows.Count
    pass = @($rows | Where-Object { $_.status -eq $passStatus }).Count
    reviewNeeded = @($rows | Where-Object { $_.status -eq $reviewStatus }).Count
    errors = @($rows | Where-Object { $_.status -eq $errorStatus }).Count
    sourceFilesScanned = $sourceFiles.Count
    resourceFilesScanned = $resourceFiles.Count
    worldgenSourceFiles = $worldgenSourceFiles
    featureSourceFiles = $featureSourceFiles
    biomeModifierSourceFiles = $biomeModifierSourceFiles
    structureSourceFiles = $structureSourceFiles
    dimensionSourceFiles = $dimensionSourceFiles
    resourceWorldgenFiles = $resourceWorldgenFiles
    manifestCandidateIds = $manifestCandidateIds
}
$report = [pscustomobject][ordered]@{
    schemaVersion = 1
    generatedAtUtc = (Get-Date).ToUniversalTime().ToString('o')
    policy = 'Report-only worldgen link audit. Review rows identify configured/placed feature, biome modifier, structure/dimension, data-resource and manifest worldgen candidates; they are not gameplay parity failures.'
    inputs = [pscustomobject][ordered]@{
        repoRoot = $RepoRoot
        portRoot = ConvertTo-RelativeRepoPath $PortRoot
        portManifestPath = ConvertTo-RelativeRepoPath $PortManifestPath
        rulesPath = ConvertTo-RelativeRepoPath $rulesPath
        checks = @($Checks)
    }
    summary = $summary
    results = @($rows | Sort-Object status, classification, subject, path)
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 32 | Set-Content -LiteralPath $OutputJson -Encoding utf8
$lines = @()
$lines += '# Item/block worldgen link audit'
$lines += ''
$lines += ('Generated: ' + $report.generatedAtUtc)
$lines += ''
$lines += 'Policy: report-only worldgen-linked block/data review. Review rows are configured/placed feature, biome modifier, structure/dimension and worldgen data policy work, not gameplay parity failures.'
$lines += ''
$lines += '## Summary'
$lines += ''
$lines += ('- Rows: ' + $summary.rows)
$lines += ('- Pass: ' + $summary.pass)
$lines += ('- Review needed: ' + $summary.reviewNeeded)
$lines += ('- Errors: ' + $summary.errors)
$lines += ('- Source files scanned: ' + $summary.sourceFilesScanned)
$lines += ('- Resource files scanned: ' + $summary.resourceFilesScanned)
$lines += ('- Worldgen source files: ' + $summary.worldgenSourceFiles)
$lines += ('- Feature source files: ' + $summary.featureSourceFiles)
$lines += ('- Biome modifier source files: ' + $summary.biomeModifierSourceFiles)
$lines += ('- Structure source files: ' + $summary.structureSourceFiles)
$lines += ('- Dimension source files: ' + $summary.dimensionSourceFiles)
$lines += ('- Resource worldgen files: ' + $summary.resourceWorldgenFiles)
$lines += ('- Manifest candidate IDs: ' + $summary.manifestCandidateIds)
$lines += ''
$lines += '## First rows'
$lines += ''
$lines += '| Status | Classification | Subject | Path | Evidence | Recommendation |'
$lines += '|---|---|---|---|---|---|'
foreach ($row in @($report.results | Select-Object -First 80)) {
    $evidence = (($row.evidence -join '; ') -replace '\|','/')
    $recommendation = (($row.recommendation) -replace '\|','/')
    $lines += ('| {0} | {1} | {2} | {3} | {4} | {5} |' -f $row.status, $row.classification, $row.subject, $row.path, $evidence, $recommendation)
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8
Write-Output ('Worldgen link audit report: ' + $OutputMarkdown)
Write-Output ('Rows={0}, pass={1}, reviewNeeded={2}, errors={3}, sourceFilesScanned={4}, resourceFilesScanned={5}, worldgenSourceFiles={6}, featureSourceFiles={7}, biomeModifierSourceFiles={8}, structureSourceFiles={9}, dimensionSourceFiles={10}, resourceWorldgenFiles={11}, manifestCandidateIds={12}' -f $summary.rows, $summary.pass, $summary.reviewNeeded, $summary.errors, $summary.sourceFilesScanned, $summary.resourceFilesScanned, $summary.worldgenSourceFiles, $summary.featureSourceFiles, $summary.biomeModifierSourceFiles, $summary.structureSourceFiles, $summary.dimensionSourceFiles, $summary.resourceWorldgenFiles, $summary.manifestCandidateIds)
