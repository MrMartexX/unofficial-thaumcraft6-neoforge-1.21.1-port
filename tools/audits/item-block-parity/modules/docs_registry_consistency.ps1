[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot "tools/audits/item-block-parity/rules" }
$reportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity"
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot "docs_registry_consistency_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot "docs_registry_consistency_report.md" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace('\', '/')
}

function Resolve-RepoRelativePath([string]$RelativePath, [string]$Fallback) {
    $value = $RelativePath
    if ([string]::IsNullOrWhiteSpace($value)) { $value = $Fallback }
    return [System.IO.Path]::GetFullPath((Join-Path $RepoRoot $value))
}

$rulesPath = Join-Path $RulesRoot "docs-registry-consistency-rules.json"
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{}
}

$passStatus = if ($rules.status -and $rules.status.pass) { [string]$rules.status.pass } else { "DOCS_REGISTRY_PASS" }
$reviewStatus = if ($rules.status -and $rules.status.review) { [string]$rules.status.review } else { "DOCS_REGISTRY_REVIEW_NEEDED" }
$errorStatus = if ($rules.status -and $rules.status.error) { [string]$rules.status.error } else { "DOCS_REGISTRY_ERROR" }

$rows = @()
function Add-Row {
    param(
        [string]$Status,
        [string]$Severity,
        [string]$Subject,
        [string]$Evidence,
        [string]$Expected = "",
        [string]$Actual = ""
    )
    $script:rows += [pscustomobject]@{
        check = "docs_deferred"
        status = $Status
        severity = $Severity
        subject = $Subject
        expected = $Expected
        actual = $Actual
        evidence = $Evidence
    }
}

$registryPath = Resolve-RepoRelativePath ([string]$rules.registryPath) "tools/audits/item-block-parity/rules/check-registry.json"
$invocationRulesPath = Resolve-RepoRelativePath ([string]$rules.invocationRulesPath) "tools/audits/item-block-parity/rules/check-invocation-rules.json"
$frameworkPath = Resolve-RepoRelativePath ([string]$rules.frameworkAlignmentPath) "06_docs/audits/item_block_parity_framework_alignment.md"
$matrixPath = Resolve-RepoRelativePath ([string]$rules.layerCompletionMatrixPath) "06_docs/audits/item_block_parity_layer_completion_matrix.md"

foreach ($requiredPath in @($registryPath, $invocationRulesPath, $frameworkPath, $matrixPath)) {
    if (-not (Test-Path -LiteralPath $requiredPath -PathType Leaf)) {
        Add-Row $errorStatus "error" (ConvertTo-RelativeRepoPath $requiredPath) "Required docs/registry consistency input is missing."
    }
}

if (@($rows | Where-Object { $_.status -eq $errorStatus }).Count -eq 0) {
    $registry = Get-Content -Raw -LiteralPath $registryPath | ConvertFrom-Json
    $invocationRules = Get-Content -Raw -LiteralPath $invocationRulesPath | ConvertFrom-Json
    $frameworkText = Get-Content -Raw -LiteralPath $frameworkPath
    $matrixText = Get-Content -Raw -LiteralPath $matrixPath

    $checks = @($registry.checks | Where-Object { $null -ne $_ })
    $nameCounts = @{}
    foreach ($check in $checks) {
        $name = ([string]$check.name).Trim()
        if ([string]::IsNullOrWhiteSpace($name)) { continue }
        if (-not $nameCounts.ContainsKey($name)) { $nameCounts[$name] = 0 }
        $nameCounts[$name] = [int]$nameCounts[$name] + 1
    }
    $duplicateNames = @($nameCounts.Keys | Where-Object { [int]$nameCounts[$_] -gt 1 } | Sort-Object)
    if ($duplicateNames.Count -eq 0) { Add-Row $passStatus "info" "check-registry unique names" "All registry check names are unique." }
    else { Add-Row $errorStatus "error" "check-registry unique names" "Duplicate check names found." "unique check names" ($duplicateNames -join ", ") }

    $implementedChecks = @($checks | Where-Object { ([string]$_.status) -eq "implemented" })
    $implementedNames = @($implementedChecks | ForEach-Object { ([string]$_.name).Trim() } | Where-Object { -not [string]::IsNullOrWhiteSpace($_) })
    $ownerNames = @($invocationRules.owners | ForEach-Object { ([string]$_.name).Trim() } | Where-Object { -not [string]::IsNullOrWhiteSpace($_) })
    $missingOwners = @($implementedNames | Where-Object { $_ -notin $ownerNames })
    if ($missingOwners.Count -eq 0) { Add-Row $passStatus "info" "implemented checks have owners" "Every implemented registry check has an invocation owner rule." }
    else { Add-Row $reviewStatus "review" "implemented checks have owners" "Some implemented registry checks are missing invocation owner rules." "all implemented owner rules" ($missingOwners -join ", ") }

    $sourceChecks = @($checks | Where-Object { ([string]$_.layer) -eq "source_quality" })
    $sourceImplemented = @($sourceChecks | Where-Object { ([string]$_.status) -eq "implemented" }).Count
    $sourcePlanned = @($sourceChecks | Where-Object { ([string]$_.status) -eq "planned" }).Count
    $sourceLine = @($matrixText -split "`r?`n" | Where-Object { $_.StartsWith("| Source quality and legacy evidence (`$layer=source_quality) |") } | Select-Object -First 1)
    if ($sourceLine.Count -eq 0) {
        Add-Row $reviewStatus "review" "source_quality registry snapshot counts" "Layer completion matrix is missing the source_quality summary row." ("implemented=" + $sourceImplemented + "; planned=" + $sourcePlanned) "missing"
    } else {
        $parts = @($sourceLine[0].Split('|') | ForEach-Object { $_.Trim() })
        $actualImplemented = if ($parts.Count -gt 2) { $parts[2] } else { "" }
        $actualPlanned = if ($parts.Count -gt 3) { $parts[3] } else { "" }
        if ($actualImplemented -eq [string]$sourceImplemented -and $actualPlanned -eq [string]$sourcePlanned) {
            Add-Row $passStatus "info" "source_quality registry snapshot counts" "Layer completion matrix source_quality implemented/planned counts match registry." ("implemented=" + $sourceImplemented + "; planned=" + $sourcePlanned) ("implemented=" + $actualImplemented + "; planned=" + $actualPlanned)
        } else {
            Add-Row $reviewStatus "review" "source_quality registry snapshot counts" "Layer completion matrix source_quality implemented/planned counts do not match registry." ("implemented=" + $sourceImplemented + "; planned=" + $sourcePlanned) ("implemented=" + $actualImplemented + "; planned=" + $actualPlanned)
        }
    }

    $expectedBatch = if ($rules.expectedClosedBatch) { [string]$rules.expectedClosedBatch } else { "31" }
    $batchRowToken = "| " + $expectedBatch + " | Closed:"
    if ($frameworkText.Contains($batchRowToken)) { Add-Row $passStatus "info" "framework alignment closed batch row" "Framework alignment contains the expected closed batch row." $batchRowToken $batchRowToken }
    else { Add-Row $reviewStatus "review" "framework alignment closed batch row" "Framework alignment is missing the expected closed batch row." $batchRowToken "missing" }

    $closureToken = "Batch " + $expectedBatch + " closure:"
    if ($frameworkText.Contains($closureToken)) { Add-Row $passStatus "info" "framework alignment closure note" "Framework alignment contains the expected closure note." $closureToken $closureToken }
    else { Add-Row $reviewStatus "review" "framework alignment closure note" "Framework alignment is missing the expected closure note." $closureToken "missing" }

    $backlogIndex = $matrixText.IndexOf("## Completion backlog derived from the current registry")
    $backlogText = if ($backlogIndex -ge 0) { $matrixText.Substring($backlogIndex) } else { "" }
    $listedImplemented = @()
    foreach ($name in $implementedNames) {
        $token = "| " + $name + " |"
        if ($backlogText.Contains($token)) { $listedImplemented += $name }
    }
    if ($listedImplemented.Count -eq 0) { Add-Row $passStatus "info" "implemented checks not in backlog" "No implemented checks are still listed in the planned backlog." }
    else { Add-Row $reviewStatus "review" "implemented checks not in backlog" "Implemented checks are still listed in the planned backlog." "not in backlog" ($listedImplemented -join ", ") }
}

$orderedRows = @($rows | Sort-Object severity, subject, status)
$summary = [ordered]@{
    rows = $orderedRows.Count
    pass = @($orderedRows | Where-Object { $_.status -eq $passStatus }).Count
    reviewNeeded = @($orderedRows | Where-Object { $_.status -eq $reviewStatus }).Count
    errors = @($orderedRows | Where-Object { $_.status -eq $errorStatus }).Count
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "Report-only docs/registry consistency validation. Review rows identify documentation or registry drift; error rows identify unreadable core inputs or duplicate registry names."
    inputs = [ordered]@{
        registry = ConvertTo-RelativeRepoPath $registryPath
        invocationRules = ConvertTo-RelativeRepoPath $invocationRulesPath
        frameworkAlignment = ConvertTo-RelativeRepoPath $frameworkPath
        layerCompletionMatrix = ConvertTo-RelativeRepoPath $matrixPath
        rules = ConvertTo-RelativeRepoPath $rulesPath
        checks = @($Checks)
    }
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = @()
$lines += "# Item/block parity docs/registry consistency"
$lines += ""
$lines += "Generated: " + $report.generatedAtUtc
$lines += ""
$lines += "Policy: report-only docs/registry consistency validation. Review rows are framework hardening work, not gameplay parity failures."
$lines += ""
$lines += "## Summary"
$lines += ""
$lines += "- Rows: " + $summary.rows
$lines += "- Pass: " + $summary.pass
$lines += "- Review needed: " + $summary.reviewNeeded
$lines += "- Errors: " + $summary.errors
$lines += ""
$lines += "## Non-pass rows"
$lines += ""
$lines += "| Status | Subject | Expected | Actual | Evidence |"
$lines += "|---|---|---|---|---|"
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus })
foreach ($row in $nonPass) {
    $subject = if ($row.subject) { ([string]$row.subject).Replace("|", "\|") } else { "" }
    $expected = if ($row.expected) { ([string]$row.expected).Replace("|", "\|") } else { "" }
    $actual = if ($row.actual) { ([string]$row.actual).Replace("|", "\|") } else { "" }
    $evidence = if ($row.evidence) { ([string]$row.evidence).Replace("|", "\|") } else { "" }
    $lines += "| " + $row.status + " | " + $subject + " | " + $expected + " | " + $actual + " | " + $evidence + " |"
}
if ($nonPass.Count -eq 0) { $lines += "| DOCS_REGISTRY_PASS | <all> | <none> | <none> | Core docs and registry are consistent |" }
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Docs/registry consistency report: $OutputMarkdown"
Write-Output ("Rows=" + $summary.rows + ", pass=" + $summary.pass + ", reviewNeeded=" + $summary.reviewNeeded + ", errors=" + $summary.errors)
