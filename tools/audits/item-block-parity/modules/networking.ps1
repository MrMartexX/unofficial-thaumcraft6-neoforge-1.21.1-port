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
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot 'item_block_networking_report.json' }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot 'item_block_networking_report.md' }

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
        check = 'networking'
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

$rulesPath = Join-Path $RulesRoot 'networking-rules.json'
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{
        sourceRoots = @('src/main/java')
        sourceExtensions = @('.java')
        payloadTerms = @('CustomPacketPayload','PayloadRegistrar','RegisterPayloadHandlersEvent','StreamCodec','playToServer','playToClient')
        serverboundTerms = @('playToServer','Serverbound','serverbound')
        clientboundTerms = @('playToClient','Clientbound','clientbound')
        validationTerms = @('ServerPlayer','player','containerMenu','distanceToSqr','mayBuild')
        mutationTerms = @('setItem','shrink(','progressResearch','setChanged')
        manifestCandidateTerms = @('network','payload','packet','sync')
        status = [pscustomobject]@{ pass = 'NETWORKING_PASS'; review = 'NETWORKING_REVIEW_NEEDED'; error = 'NETWORKING_ERROR' }
        markdownSampleLimit = 120
    }
}
$passStatus = if ($rules.status.pass) { [string]$rules.status.pass } else { 'NETWORKING_PASS' }
$reviewStatus = if ($rules.status.review) { [string]$rules.status.review } else { 'NETWORKING_REVIEW_NEEDED' }
$errorStatus = if ($rules.status.error) { [string]$rules.status.error } else { 'NETWORKING_ERROR' }
$sampleLimit = if ($rules.markdownSampleLimit) { [int]$rules.markdownSampleLimit } else { 120 }

if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw ('Port manifest not found: ' + $PortManifestPath) }
$portManifest = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$portEntries = Get-Entries $portManifest
$sourceFiles = Get-SourceFiles -Roots @($rules.sourceRoots) -Extensions @($rules.sourceExtensions)
$rows = @()
$networkingFiles = 0
$payloadFiles = 0
$serverboundFiles = 0
$clientboundFiles = 0
$validationEvidenceFiles = 0
$mutationRiskFiles = 0

foreach ($file in @($sourceFiles)) {
    $text = Get-Content -Raw -LiteralPath $file.FullName
    $payloadHits = Get-MatchedTerms -Text $text -Terms @($rules.payloadTerms)
    $serverHits = Get-MatchedTerms -Text $text -Terms @($rules.serverboundTerms)
    $clientHits = Get-MatchedTerms -Text $text -Terms @($rules.clientboundTerms)
    $validationHits = Get-MatchedTerms -Text $text -Terms @($rules.validationTerms)
    $mutationHits = Get-MatchedTerms -Text $text -Terms @($rules.mutationTerms)
    $hasNetworkPackage = (ConvertTo-RelativeRepoPath $file.FullName).ToLowerInvariant().Contains('/network/')
    if ($payloadHits.Count -eq 0 -and -not $hasNetworkPackage) { continue }
    $networkingFiles++
    $rel = ConvertTo-RelativeRepoPath $file.FullName
    $evidence = @()
    if ($payloadHits.Count -gt 0) { $evidence += ('payload terms: ' + ($payloadHits -join ', ')) }
    if ($serverHits.Count -gt 0) { $evidence += ('serverbound terms: ' + ($serverHits -join ', ')) }
    if ($clientHits.Count -gt 0) { $evidence += ('clientbound terms: ' + ($clientHits -join ', ')) }
    if ($validationHits.Count -gt 0) { $evidence += ('validation terms: ' + ($validationHits -join ', ')) }
    if ($mutationHits.Count -gt 0) { $evidence += ('mutation terms: ' + ($mutationHits -join ', ')) }
    if ($payloadHits.Count -gt 0) { $payloadFiles++ }
    if ($serverHits.Count -gt 0) { $serverboundFiles++ }
    if ($clientHits.Count -gt 0) { $clientboundFiles++ }
    if ($validationHits.Count -gt 0) { $validationEvidenceFiles++ }
    if ($mutationHits.Count -gt 0) { $mutationRiskFiles++ }

    if ($serverHits.Count -gt 0 -and $validationHits.Count -lt 2) {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'serverbound_validation_review' -Subject $file.Name -Path $rel -Evidence $evidence -Recommendation 'Confirm serverbound handler validates player authority, menu/state, distance and loaded-world assumptions.'
    } elseif ($serverHits.Count -gt 0 -and $mutationHits.Count -gt 0) {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'serverbound_mutation_risk_review' -Subject $file.Name -Path $rel -Evidence $evidence -Recommendation 'Confirm mutations are server-authoritative and cannot be driven by trusted client state.'
    } elseif ($payloadHits.Count -gt 0 -or $serverHits.Count -gt 0 -or $clientHits.Count -gt 0) {
        Add-Row -Rows ([ref]$rows) -Status $passStatus -Severity 'info' -Classification 'networking_source_evidence' -Subject $file.Name -Path $rel -Evidence $evidence -Recommendation 'Networking source evidence observed.'
    } else {
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'network_package_review' -Subject $file.Name -Path $rel -Evidence @('File lives under a network package but no configured payload term was detected.') -Recommendation 'Confirm whether this is an active networking boundary or stale/helper code.'
    }
}

$manifestCandidateCount = 0
foreach ($entry in @($portEntries)) {
    $id = Get-EntryId $entry
    if ([string]::IsNullOrWhiteSpace($id)) { continue }
    $entryText = Get-EntrySearchText $entry
    $hits = Get-MatchedTerms -Text $entryText -Terms @($rules.manifestCandidateTerms)
    if ($hits.Count -gt 0) {
        $manifestCandidateCount++
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'manifest_networking_candidate' -Subject $id -Path '' -Evidence @('manifest terms: ' + ($hits -join ', ')) -Recommendation 'Confirm whether this manifest ID requires payload sync, menu state sync or explicit serverbound action validation.'
    }
}

if ($networkingFiles -eq 0) {
    Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'no_networking_source_observed' -Subject 'source scan' -Path (ConvertTo-RelativeRepoPath $PortRoot) -Evidence @('No configured networking source terms were found.') -Recommendation 'Confirm whether networking has not started or rules need additional search terms.'
}

$orderedRows = @($rows | Sort-Object severity, classification, path, subject)
$summary = [ordered]@{
    rows = @($orderedRows).Count
    pass = @($orderedRows | Where-Object { $_.status -eq $passStatus }).Count
    reviewNeeded = @($orderedRows | Where-Object { $_.status -eq $reviewStatus }).Count
    errors = @($orderedRows | Where-Object { $_.status -eq $errorStatus }).Count
    sourceFilesScanned = @($sourceFiles).Count
    networkingFiles = $networkingFiles
    payloadFiles = $payloadFiles
    serverboundFiles = $serverboundFiles
    clientboundFiles = $clientboundFiles
    validationEvidenceFiles = $validationEvidenceFiles
    mutationRiskFiles = $mutationRiskFiles
    manifestCandidateIds = $manifestCandidateCount
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString('o')
    policy = 'Report-only networking boundary audit. Review rows identify payload validation, mutation-risk and manifest sync candidates; they are not gameplay parity failures.'
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
$lines += '# Item/block networking boundary audit'
$lines += ''
$lines += ('Generated: ' + $report.generatedAtUtc)
$lines += ''
$lines += 'Policy: report-only networking boundary review. Review rows are networking validation work, not gameplay parity failures.'
$lines += ''
$lines += '## Summary'
$lines += ''
$lines += ('- Rows: ' + $summary.rows)
$lines += ('- Pass: ' + $summary.pass)
$lines += ('- Review needed: ' + $summary.reviewNeeded)
$lines += ('- Errors: ' + $summary.errors)
$lines += ('- Source files scanned: ' + $summary.sourceFilesScanned)
$lines += ('- Networking files: ' + $summary.networkingFiles)
$lines += ('- Payload files: ' + $summary.payloadFiles)
$lines += ('- Serverbound files: ' + $summary.serverboundFiles)
$lines += ('- Clientbound files: ' + $summary.clientboundFiles)
$lines += ('- Validation evidence files: ' + $summary.validationEvidenceFiles)
$lines += ('- Mutation-risk files: ' + $summary.mutationRiskFiles)
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
    $lines += '| NETWORKING_PASS | <all> | <all> | <all> | Source scan found no review-only networking rows. | None. |'
}
$lines += ''
$lines += ('Only the first ' + $sampleLimit + ' non-pass rows are shown in Markdown; JSON contains all rows.')
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output ('Networking boundary audit report: ' + $OutputMarkdown)
Write-Output ('Rows={0}, pass={1}, reviewNeeded={2}, errors={3}, sourceFilesScanned={4}, networkingFiles={5}, payloadFiles={6}, serverboundFiles={7}, clientboundFiles={8}, validationEvidenceFiles={9}, mutationRiskFiles={10}, manifestCandidateIds={11}' -f $summary.rows, $summary.pass, $summary.reviewNeeded, $summary.errors, $summary.sourceFilesScanned, $summary.networkingFiles, $summary.payloadFiles, $summary.serverboundFiles, $summary.clientboundFiles, $summary.validationEvidenceFiles, $summary.mutationRiskFiles, $summary.manifestCandidateIds)
