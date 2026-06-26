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
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot 'item_block_config_gates_report.json' }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot 'item_block_config_gates_report.md' }

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
        check = 'config_gates'
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

if ($Checks -and 'config_gates' -notin $Checks) { return }
$rulesPath = Join-Path $RulesRoot 'config-gates-rules.json'
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{
        statuses = [pscustomobject]@{ pass = 'CONFIG_GATE_PASS'; review = 'CONFIG_GATE_REVIEW_NEEDED'; error = 'CONFIG_GATE_ERROR' }
        sourceRoots = @('src/main/java')
        resourceRoots = @('src/main/resources', 'src/generated/resources')
        sourceExtensions = @('.java')
        resourceExtensions = @('.json', '.toml', '.properties', '.mcmeta')
        configTerms = @('ModConfig','ForgeConfigSpec','ConfigSpec','BooleanValue','ConfigValue')
        gateTerms = @('enabled','disabled','enable','disable','isEnabled','allow','blacklist','whitelist')
        featureFlagTerms = @('feature','FeatureFlag','Toggle','experimental','debug')
        resourceConfigTerms = @('config','enabled','disabled','feature','gate','condition','conditions')
        manifestCandidateTerms = @('research','arcane','aura','taint','worldgen','entity','recipe','config')
    }
}
$passStatus = [string]$rules.statuses.pass
$reviewStatus = [string]$rules.statuses.review
$errorStatus = [string]$rules.statuses.error
$rows = @()
$sourceFiles = @(Get-FilesByRoots -Roots @($rules.sourceRoots) -Extensions @($rules.sourceExtensions))
$resourceFiles = @(Get-FilesByRoots -Roots @($rules.resourceRoots) -Extensions @($rules.resourceExtensions))

$configSourceFiles = 0
$gateSourceFiles = 0
$featureFlagSourceFiles = 0
foreach ($file in $sourceFiles) {
    $text = Get-Content -Raw -LiteralPath $file.FullName
    $configHits = Get-MatchedTerms -Text $text -Terms @($rules.configTerms)
    $gateHits = Get-MatchedTerms -Text $text -Terms @($rules.gateTerms)
    $featureHits = Get-MatchedTerms -Text $text -Terms @($rules.featureFlagTerms)
    if ($configHits.Count -eq 0 -and $gateHits.Count -eq 0 -and $featureHits.Count -eq 0) { continue }
    $rel = ConvertTo-RelativeRepoPath $file.FullName
    $evidence = @()
    if ($configHits.Count -gt 0) { $evidence += ('config terms: ' + ($configHits -join ', ')); $configSourceFiles++ }
    if ($gateHits.Count -gt 0) { $evidence += ('gate terms: ' + ($gateHits -join ', ')); $gateSourceFiles++ }
    if ($featureHits.Count -gt 0) { $evidence += ('feature flag terms: ' + ($featureHits -join ', ')); $featureFlagSourceFiles++ }
    $classification = 'config_source_review'
    if ($gateHits.Count -gt 0 -and $configHits.Count -gt 0) { $classification = 'config_gate_source_review' }
    elseif ($gateHits.Count -gt 0) { $classification = 'gate_source_review' }
    elseif ($featureHits.Count -gt 0) { $classification = 'feature_flag_source_review' }
    Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification $classification -Subject $file.Name -Path $rel -Evidence $evidence -Recommendation 'Confirm config-backed feature gates, defaults, server/client/common scope and legacy parity policy or mark explicit intentional differences.'
}

$configResourceFiles = 0
foreach ($file in $resourceFiles) {
    $text = Get-Content -Raw -LiteralPath $file.FullName -ErrorAction SilentlyContinue
    if ([string]::IsNullOrWhiteSpace($text)) { continue }
    $hits = Get-MatchedTerms -Text $text -Terms @($rules.resourceConfigTerms)
    $pathHint = (ConvertTo-RelativeRepoPath $file.FullName).ToLowerInvariant()
    if ($hits.Count -eq 0 -and $pathHint -notmatch 'config|condition|feature|gate') { continue }
    $configResourceFiles++
    $evidence = @()
    if ($hits.Count -gt 0) { $evidence += ('resource config terms: ' + ($hits -join ', ')) }
    if ($pathHint -match 'config|condition|feature|gate') { $evidence += 'path suggests config/conditional data boundary' }
    Add-Row -Rows ([ref]$rows) -Status $passStatus -Severity 'info' -Classification 'config_resource_evidence' -Subject $file.Name -Path (ConvertTo-RelativeRepoPath $file.FullName) -Evidence $evidence -Recommendation 'Resource contains config, conditional-load or feature-gate data clues.'
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
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'manifest_config_candidate' -Subject $id -Path '' -Evidence @('manifest terms: ' + ($hits -join ', ')) -Recommendation 'Confirm whether this ID is controlled by config, feature gates, conditional data loading or explicit always-on policy.'
    }
}
if ($rows.Count -eq 0) {
    Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'no_config_gate_evidence' -Subject 'source/resource scan' -Path (ConvertTo-RelativeRepoPath $PortRoot) -Evidence @('No configured config-gate evidence was found.') -Recommendation 'Confirm whether config gates are not started or rules need additional terms.'
}

$summary = [pscustomobject][ordered]@{
    rows = $rows.Count
    pass = @($rows | Where-Object { $_.status -eq $passStatus }).Count
    reviewNeeded = @($rows | Where-Object { $_.status -eq $reviewStatus }).Count
    errors = @($rows | Where-Object { $_.status -eq $errorStatus }).Count
    sourceFilesScanned = $sourceFiles.Count
    resourceFilesScanned = $resourceFiles.Count
    configSourceFiles = $configSourceFiles
    gateSourceFiles = $gateSourceFiles
    featureFlagSourceFiles = $featureFlagSourceFiles
    configResourceFiles = $configResourceFiles
    manifestCandidateIds = $manifestCandidateIds
}
$report = [pscustomobject][ordered]@{
    schemaVersion = 1
    generatedAtUtc = (Get-Date).ToUniversalTime().ToString('o')
    policy = 'Report-only config gate audit. Review rows identify config specs, enable/disable gates, feature flags, config-backed resources and manifest config candidates; they are not gameplay parity failures.'
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
$lines += '# Item/block config gate audit'
$lines += ''
$lines += ('Generated: ' + $report.generatedAtUtc)
$lines += ''
$lines += 'Policy: report-only config/feature-gate review. Review rows are config scope/default/conditional-loading policy work, not gameplay parity failures.'
$lines += ''
$lines += '## Summary'
$lines += ''
$lines += ('- Rows: ' + $summary.rows)
$lines += ('- Pass: ' + $summary.pass)
$lines += ('- Review needed: ' + $summary.reviewNeeded)
$lines += ('- Errors: ' + $summary.errors)
$lines += ('- Source files scanned: ' + $summary.sourceFilesScanned)
$lines += ('- Resource files scanned: ' + $summary.resourceFilesScanned)
$lines += ('- Config source files: ' + $summary.configSourceFiles)
$lines += ('- Gate source files: ' + $summary.gateSourceFiles)
$lines += ('- Feature flag source files: ' + $summary.featureFlagSourceFiles)
$lines += ('- Config resource files: ' + $summary.configResourceFiles)
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
Write-Output ('Config gate audit report: ' + $OutputMarkdown)
Write-Output ('Rows={0}, pass={1}, reviewNeeded={2}, errors={3}, sourceFilesScanned={4}, resourceFilesScanned={5}, configSourceFiles={6}, gateSourceFiles={7}, featureFlagSourceFiles={8}, configResourceFiles={9}, manifestCandidateIds={10}' -f $summary.rows, $summary.pass, $summary.reviewNeeded, $summary.errors, $summary.sourceFilesScanned, $summary.resourceFilesScanned, $summary.configSourceFiles, $summary.gateSourceFiles, $summary.featureFlagSourceFiles, $summary.configResourceFiles, $summary.manifestCandidateIds)
