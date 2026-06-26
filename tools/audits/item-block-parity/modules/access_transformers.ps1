[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$LegacyRoot,
    [Parameter(Mandatory = $true)][string]$PortRoot,
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = 'Stop'
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path
function Resolve-WithinRepo([string]$PathValue) {
    if ([string]::IsNullOrWhiteSpace($PathValue)) { return '' }
    if ([System.IO.Path]::IsPathRooted($PathValue)) { return (Resolve-Path -LiteralPath $PathValue).Path }
    $candidate = Join-Path $RepoRoot $PathValue
    if (Test-Path -LiteralPath $candidate) { return (Resolve-Path -LiteralPath $candidate).Path }
    return $candidate
}
$LegacyRoot = Resolve-WithinRepo $LegacyRoot
$PortRoot = Resolve-WithinRepo $PortRoot
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot 'tools/audits/item-block-parity/rules' }
$reportRoot = Join-Path $RepoRoot 'tools/reports/local/item-block-parity'
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot 'item_block_access_transformers_report.json' }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot 'item_block_access_transformers_report.md' }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return '' }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace('\', '/')
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
        check = 'access_transformers'
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
    param([string]$Root, [string[]]$RelativeRoots, [string[]]$Extensions)
    $files = @()
    if ([string]::IsNullOrWhiteSpace($Root) -or -not (Test-Path -LiteralPath $Root -PathType Container)) { return @() }
    foreach ($relative in @($RelativeRoots)) {
        if ([string]::IsNullOrWhiteSpace($relative)) { continue }
        $fullRoot = Join-Path $Root $relative
        if (-not (Test-Path -LiteralPath $fullRoot -PathType Container)) { continue }
        foreach ($file in @(Get-ChildItem -LiteralPath $fullRoot -Recurse -File -ErrorAction SilentlyContinue)) {
            if ($file.Extension -in $Extensions -or $file.Name -match '(?i)(access.*transform|.*_at\.cfg|tc_at\.cfg)$') { $files += $file }
        }
    }
    return @($files | Sort-Object FullName -Unique)
}
function Get-ManifestCandidateRows {
    param([ref]$Rows, [string[]]$Terms)
    $manifestPaths = @(
        Join-Path $RepoRoot 'tools/reports/local/item-block-parity/legacy_primary_manifest.json',
        Join-Path $RepoRoot 'tools/reports/local/item-block-parity/port_manifest.json'
    )
    $candidateCount = 0
    foreach ($manifestPath in $manifestPaths) {
        if (-not (Test-Path -LiteralPath $manifestPath -PathType Leaf)) { continue }
        $manifest = Get-Content -Raw -LiteralPath $manifestPath | ConvertFrom-Json
        $collections = @('entries','items','blocks','blockItems')
        foreach ($collection in $collections) {
            $prop = $manifest.PSObject.Properties[$collection]
            if ($null -eq $prop -or $null -eq $prop.Value) { continue }
            foreach ($entry in @($prop.Value)) {
                $values = @()
                foreach ($ep in @($entry.PSObject.Properties)) {
                    if ($null -ne $ep.Value) { $values += [string]$ep.Value }
                }
                $text = (($values -join ' ') -replace '[^a-zA-Z0-9_:/.-]', ' ').ToLowerInvariant()
                $hits = @(Get-MatchedTerms -Text $text -Terms $Terms)
                if ($hits.Count -gt 0) {
                    $idProp = $entry.PSObject.Properties['registryId']
                    if ($null -eq $idProp) { $idProp = $entry.PSObject.Properties['id'] }
                    $subject = if ($null -ne $idProp) { [string]$idProp.Value } else { 'manifest-entry' }
                    Add-Row -Rows $Rows -Status 'ACCESS_TRANSFORMERS_REVIEW_NEEDED' -Severity 'info' -Classification 'manifest_visibility_candidate' -Subject $subject -Path (ConvertTo-RelativeRepoPath $manifestPath) -Evidence $hits -Recommendation 'Classify whether this manifest entry depends on widened visibility, reflection, rendering internals or other access-sensitive legacy behavior.'
                    $candidateCount++
                }
            }
        }
    }
    return $candidateCount
}

$rulesPath = Join-Path $RulesRoot 'access-transformers-rules.json'
if (-not (Test-Path -LiteralPath $rulesPath -PathType Leaf)) { throw 'Access transformer rules JSON not found.' }
$rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
$rows = @()
$sourceRoots = @($rules.sourceRoots)
$legacyRoots = @($rules.legacyRoots)
$extensions = @($rules.sourceExtensions)
$portFiles = @(Get-SourceFiles -Root $PortRoot -RelativeRoots $sourceRoots -Extensions $extensions)
$extraPortFiles = @()
foreach ($relativeFile in @('build.gradle','build.gradle.kts','gradle.properties','settings.gradle','settings.gradle.kts','src/main/resources/META-INF/neoforge.mods.toml')) {
    $candidate = Join-Path $PortRoot $relativeFile
    if (Test-Path -LiteralPath $candidate -PathType Leaf) { $extraPortFiles += (Get-Item -LiteralPath $candidate) }
}
$portFiles = @($portFiles + $extraPortFiles)
$portFiles = @($portFiles | Sort-Object FullName -Unique)
$legacyFiles = @(Get-SourceFiles -Root $LegacyRoot -RelativeRoots $legacyRoots -Extensions $extensions)
$allFiles = @($portFiles + $legacyFiles)

$atFileCount = 0
$buildWiringCount = 0
$visibilityEvidenceCount = 0
$legacyAtFiles = 0
$portAtFiles = 0

foreach ($file in $allFiles) {
    $text = Get-Content -Raw -LiteralPath $file.FullName -ErrorAction SilentlyContinue
    if ($null -eq $text) { $text = '' }
    $relative = ConvertTo-RelativeRepoPath $file.FullName
    $isLegacy = $file.FullName.StartsWith($LegacyRoot, [System.StringComparison]::OrdinalIgnoreCase)
    $scope = if ($isLegacy) { 'legacy' } else { 'port' }
    $nameText = ($file.Name + ' ' + $relative)
    $atFileHits = @(Get-MatchedTerms -Text $nameText -Terms @($rules.accessTransformerFileTerms))
    $ruleHits = @(Get-MatchedTerms -Text $text -Terms @($rules.accessRuleTerms))
    $buildHits = @(Get-MatchedTerms -Text $text -Terms @($rules.buildWiringTerms))
    $visibilityHits = @(Get-MatchedTerms -Text $text -Terms @($rules.visibilityTerms))

    if ($atFileHits.Count -gt 0 -or ($file.Extension -eq '.cfg' -and $ruleHits.Count -gt 0)) {
        $status = 'ACCESS_TRANSFORMERS_REVIEW_NEEDED'
        $class = if ($isLegacy) { 'legacy_access_transformer_file' } else { 'port_access_transformer_file' }
        $combinedEvidence = @($atFileHits + $ruleHits)
        $combinedEvidence = @($combinedEvidence | Select-Object -Unique)
        Add-Row -Rows ([ref]$rows) -Status $status -Severity 'review' -Classification $class -Subject $file.Name -Path $relative -Evidence $combinedEvidence -Recommendation 'Review each widened member and classify as still required, superseded by modern API, intentionally avoided or blocked.'
        $atFileCount++
        if ($isLegacy) { $legacyAtFiles++ } else { $portAtFiles++ }
    }
    if ($buildHits.Count -gt 0) {
        Add-Row -Rows ([ref]$rows) -Status 'ACCESS_TRANSFORMERS_REVIEW_NEEDED' -Severity 'review' -Classification ($scope + '_build_wiring') -Subject $file.Name -Path $relative -Evidence $buildHits -Recommendation 'Confirm access transformer build wiring is intentional and documented; avoid hidden dependency on legacy AT behavior.'
        $buildWiringCount++
    }
    if ($visibilityHits.Count -gt 0) {
        Add-Row -Rows ([ref]$rows) -Status 'ACCESS_TRANSFORMERS_REVIEW_NEEDED' -Severity 'review' -Classification ($scope + '_visibility_source_evidence') -Subject $file.Name -Path $relative -Evidence $visibilityHits -Recommendation 'Classify this visibility/reflection evidence and prefer public NeoForge/Minecraft APIs before adding access widening.'
        $visibilityEvidenceCount++
    }
}

if ($atFileCount -eq 0) {
    Add-Row -Rows ([ref]$rows) -Status 'ACCESS_TRANSFORMERS_PASS' -Severity 'info' -Classification 'no_access_transformer_files_found' -Subject 'access-transformer-files' -Path '' -Evidence @('No AT-like files discovered in configured legacy/port roots') -Recommendation 'Keep this as report-only evidence; original jar/source conflict probes may still add visibility context later.'
}
if ($portAtFiles -eq 0) {
    Add-Row -Rows ([ref]$rows) -Status 'ACCESS_TRANSFORMERS_PASS' -Severity 'info' -Classification 'no_port_access_transformer_file' -Subject 'port-access-transformer' -Path (ConvertTo-RelativeRepoPath $PortRoot) -Evidence @('No port AT file found') -Recommendation 'Good default: avoid access transformers unless a modern API gap is proven and documented.'
}
$manifestCandidateIds = Get-ManifestCandidateRows -Rows ([ref]$rows) -Terms @($rules.manifestCandidateTerms)

$pass = @($rows | Where-Object { $_.status -eq 'ACCESS_TRANSFORMERS_PASS' }).Count
$review = @($rows | Where-Object { $_.status -eq 'ACCESS_TRANSFORMERS_REVIEW_NEEDED' }).Count
$errors = @($rows | Where-Object { $_.status -eq 'ACCESS_TRANSFORMERS_ERROR' }).Count
$summary = [pscustomobject][ordered]@{
    rows = @($rows).Count
    pass = $pass
    reviewNeeded = $review
    errors = $errors
    sourceFilesScanned = @($allFiles).Count
    legacySourceFilesScanned = @($legacyFiles).Count
    portSourceFilesScanned = @($portFiles).Count
    accessTransformerFiles = $atFileCount
    legacyAccessTransformerFiles = $legacyAtFiles
    portAccessTransformerFiles = $portAtFiles
    buildWiringFiles = $buildWiringCount
    visibilityEvidenceFiles = $visibilityEvidenceCount
    manifestCandidateIds = $manifestCandidateIds
}
$report = [pscustomobject][ordered]@{
    schemaVersion = 1
    generatedAtUtc = (Get-Date).ToUniversalTime().ToString('o')
    policy = 'Report-only access transformer visibility audit. Review rows identify AT files, build wiring, reflection/access evidence and candidate IDs; they are not parity failures.'
    inputs = [pscustomobject][ordered]@{
        repoRoot = $RepoRoot
        legacyRoot = ConvertTo-RelativeRepoPath $LegacyRoot
        portRoot = ConvertTo-RelativeRepoPath $PortRoot
        rulesPath = ConvertTo-RelativeRepoPath $rulesPath
        checks = @($Checks)
    }
    summary = $summary
    results = @($rows | Sort-Object status, classification, subject, path)
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 32 | Set-Content -LiteralPath $OutputJson -Encoding utf8
$lines = @()
$lines += '# Item/block access transformer visibility audit'
$lines += ''
$lines += ('Generated: ' + $report.generatedAtUtc)
$lines += ''
$lines += 'Policy: report-only access-transformer/visibility review. Review rows are visibility policy work, not gameplay parity failures.'
$lines += ''
$lines += '## Summary'
$lines += ''
$lines += ('- Rows: ' + $summary.rows)
$lines += ('- Pass: ' + $summary.pass)
$lines += ('- Review needed: ' + $summary.reviewNeeded)
$lines += ('- Errors: ' + $summary.errors)
$lines += ('- Source files scanned: ' + $summary.sourceFilesScanned)
$lines += ('- Legacy source files scanned: ' + $summary.legacySourceFilesScanned)
$lines += ('- Port source files scanned: ' + $summary.portSourceFilesScanned)
$lines += ('- Access transformer files: ' + $summary.accessTransformerFiles)
$lines += ('- Legacy AT files: ' + $summary.legacyAccessTransformerFiles)
$lines += ('- Port AT files: ' + $summary.portAccessTransformerFiles)
$lines += ('- Build wiring files: ' + $summary.buildWiringFiles)
$lines += ('- Visibility evidence files: ' + $summary.visibilityEvidenceFiles)
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
Write-Output ('Access transformer audit report: ' + $OutputMarkdown)
Write-Output ('Rows={0}, pass={1}, reviewNeeded={2}, errors={3}, sourceFilesScanned={4}, legacySourceFilesScanned={5}, portSourceFilesScanned={6}, accessTransformerFiles={7}, legacyAccessTransformerFiles={8}, portAccessTransformerFiles={9}, buildWiringFiles={10}, visibilityEvidenceFiles={11}, manifestCandidateIds={12}' -f $summary.rows, $summary.pass, $summary.reviewNeeded, $summary.errors, $summary.sourceFilesScanned, $summary.legacySourceFilesScanned, $summary.portSourceFilesScanned, $summary.accessTransformerFiles, $summary.legacyAccessTransformerFiles, $summary.portAccessTransformerFiles, $summary.buildWiringFiles, $summary.visibilityEvidenceFiles, $summary.manifestCandidateIds)
