[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [string]$ReportRoot,
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
if (-not $ReportRoot) { $ReportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity" }
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot "tools/audits/item-block-parity/rules" }
if (-not $OutputJson) { $OutputJson = Join-Path $ReportRoot "status_taxonomy_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $ReportRoot "status_taxonomy_report.md" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}

function Test-WildcardAny {
    param([string]$Name, [string[]]$Patterns)
    foreach ($pattern in @($Patterns)) {
        if ([string]::IsNullOrWhiteSpace($pattern)) { continue }
        if ($Name -like $pattern) { return $true }
    }
    return $false
}

function Get-StatusValuesRecursive {
    param(
        $Value,
        [int]$Depth = 0
    )
    if ($null -eq $Value) { return }
    if ($Depth -gt 64) { return }
    if ($Value -is [string]) { return }
    if ($Value -is [ValueType]) { return }

    if ($Value -is [System.Collections.IDictionary]) {
        foreach ($key in $Value.Keys) {
            $child = $Value[$key]
            if ([string]$key -eq "status" -and $child -is [string]) {
                [string]$child
                continue
            }
            Get-StatusValuesRecursive -Value $child -Depth ($Depth + 1)
        }
        return
    }

    if ($Value -is [System.Collections.IEnumerable]) {
        foreach ($item in $Value) {
            Get-StatusValuesRecursive -Value $item -Depth ($Depth + 1)
        }
        return
    }

    $properties = @($Value.PSObject.Properties | Where-Object { $_.MemberType -eq "NoteProperty" })
    foreach ($property in $properties) {
        if ($property.Name -eq "status" -and $property.Value -is [string]) {
            [string]$property.Value
            continue
        }
        Get-StatusValuesRecursive -Value $property.Value -Depth ($Depth + 1)
    }
}

function Get-StatusCategory {
    param(
        [string]$ObservedStatus,
        $Rules
    )
    if ([string]::IsNullOrWhiteSpace($ObservedStatus)) { return $null }
    $normalized = $ObservedStatus.Trim().ToUpperInvariant()
    if ($Rules.statusMap -and ($Rules.statusMap.PSObject.Properties.Name -contains $normalized)) {
        return [string]$Rules.statusMap.$normalized
    }
    foreach ($patternRule in @($Rules.statusPatterns)) {
        if (-not $patternRule.pattern -or -not $patternRule.category) { continue }
        if ($normalized -like ([string]$patternRule.pattern).ToUpperInvariant()) {
            return [string]$patternRule.category
        }
    }
    return $null
}

function Add-Row {
    param(
        [System.Collections.Generic.List[object]]$Rows,
        [string]$Status,
        [string]$Severity,
        [string]$Path,
        [string]$ObservedStatus,
        [string]$CanonicalCategory,
        [int]$Occurrences,
        [string]$Evidence
    )
    [void]$Rows.Add([object]([pscustomobject]@{
        check = "status_taxonomy"
        status = $Status
        severity = $Severity
        path = $Path
        observedStatus = $ObservedStatus
        canonicalCategory = $CanonicalCategory
        occurrences = $Occurrences
        evidence = $Evidence
    }))
}

$rulesPath = Join-Path $RulesRoot "status-taxonomy-rules.json"
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{
        reportFilePatterns = @("*_report.json", "item_block_parity_not_evaluated_checks.json", "item_block_auto_fix_candidates.json")
        ignoredFilePatterns = @("legacy_primary_manifest.json", "legacy_primary_manifest.focused.json", "port_manifest.json", "port_manifest.focused.json", "status_taxonomy_report.json")
        statusMap = [pscustomobject]@{ PASS = "pass"; REVIEW_NEEDED = "review"; ERROR = "error" }
        statusPatterns = @(
            [pscustomobject]@{ pattern = "*_PASS"; category = "pass" },
            [pscustomobject]@{ pattern = "*_REVIEW_NEEDED"; category = "review" },
            [pscustomobject]@{ pattern = "*_ERROR"; category = "error" }
        )
        status = [pscustomobject]@{ pass = "STATUS_TAXONOMY_PASS"; review = "STATUS_TAXONOMY_REVIEW_NEEDED"; error = "STATUS_TAXONOMY_ERROR" }
    }
}

$reportPatterns = @($rules.reportFilePatterns)
$ignoredPatterns = @($rules.ignoredFilePatterns)
$passStatus = if ($rules.status.pass) { [string]$rules.status.pass } else { "STATUS_TAXONOMY_PASS" }
$reviewStatus = if ($rules.status.review) { [string]$rules.status.review } else { "STATUS_TAXONOMY_REVIEW_NEEDED" }
$errorStatus = if ($rules.status.error) { [string]$rules.status.error } else { "STATUS_TAXONOMY_ERROR" }

$rows = [System.Collections.Generic.List[object]]::new()

if (-not (Test-Path -LiteralPath $ReportRoot -PathType Container)) {
    Add-Row $rows $reviewStatus "review" (ConvertTo-RelativeRepoPath $ReportRoot) "" "" 0 "Report root does not exist yet; run one or more parity checks before status taxonomy validation."
} else {
    $files = @(Get-ChildItem -LiteralPath $ReportRoot -File -Filter "*.json" -ErrorAction SilentlyContinue | Sort-Object Name)
    $targets = @(
        foreach ($file in $files) {
            if ($file.FullName -eq $OutputJson) { continue }
            if (Test-WildcardAny -Name $file.Name -Patterns $ignoredPatterns) { continue }
            if (Test-WildcardAny -Name $file.Name -Patterns $reportPatterns) { $file }
        }
    )

    if ($targets.Count -eq 0) {
        Add-Row $rows $reviewStatus "review" (ConvertTo-RelativeRepoPath $ReportRoot) "" "" 0 "No taxonomy-target JSON reports found under report root."
    }

    foreach ($file in $targets) {
        $relative = ConvertTo-RelativeRepoPath $file.FullName
        try {
            $json = Get-Content -Raw -LiteralPath $file.FullName | ConvertFrom-Json
        } catch {
            Add-Row $rows $errorStatus "error" $relative "" "" 0 "JSON parse failed: $($_.Exception.Message)"
            continue
        }

        $statuses = @(Get-StatusValuesRecursive -Value $json | Where-Object { -not [string]::IsNullOrWhiteSpace($_) })
        if ($statuses.Count -eq 0) {
            Add-Row $rows $reviewStatus "review" $relative "" "" 0 "No string status fields found in targeted report; report taxonomy cannot be normalized yet."
            continue
        }

        $grouped = @($statuses | Group-Object | Sort-Object Name)
        foreach ($group in $grouped) {
            $observed = [string]$group.Name
            $category = Get-StatusCategory -ObservedStatus $observed -Rules $rules
            if ([string]::IsNullOrWhiteSpace($category)) {
                Add-Row $rows $reviewStatus "review" $relative $observed "unknown" $group.Count "Observed status is not mapped by status taxonomy rules."
            } else {
                Add-Row $rows $passStatus "info" $relative $observed $category $group.Count "Observed status maps to canonical category '$category'."
            }
        }
    }
}

$orderedRows = @($rows | Sort-Object severity, path, observedStatus)
$unknownRows = @($orderedRows | Where-Object { $_.canonicalCategory -eq "unknown" })
$summary = [ordered]@{
    rows = $orderedRows.Count
    pass = @($orderedRows | Where-Object { $_.status -eq $passStatus }).Count
    reviewNeeded = @($orderedRows | Where-Object { $_.status -eq $reviewStatus }).Count
    errors = @($orderedRows | Where-Object { $_.status -eq $errorStatus }).Count
    reportRoot = ConvertTo-RelativeRepoPath $ReportRoot
    observedStatuses = @($orderedRows | Where-Object { -not [string]::IsNullOrWhiteSpace($_.observedStatus) } | Select-Object -ExpandProperty observedStatus -Unique | Sort-Object)
    unknownStatuses = @($unknownRows | Select-Object -ExpandProperty observedStatus -Unique | Sort-Object)
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "Report-only status taxonomy validation. Pass rows mean an observed report status maps to a canonical status category; review rows mean an unmapped or absent status needs taxonomy/rule refinement, not gameplay parity failure."
    inputs = [ordered]@{
        reportRoot = ConvertTo-RelativeRepoPath $ReportRoot
        rules = ConvertTo-RelativeRepoPath $rulesPath
        checks = @($Checks)
    }
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block parity status taxonomy")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: report-only status taxonomy validation. Review rows identify unmapped report statuses or reports without status fields; they are framework hardening work, not gameplay parity failures.")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("- Rows: $($summary.rows)")
$lines.Add("- Pass: $($summary.pass)")
$lines.Add("- Review needed: $($summary.reviewNeeded)")
$lines.Add("- Errors: $($summary.errors)")
$lines.Add("- Unknown statuses: $(@($summary.unknownStatuses) -join ', ')")
$lines.Add("")
$lines.Add("## Non-pass rows")
$lines.Add("")
$lines.Add("| Status | Path | Observed status | Category | Occurrences | Evidence |")
$lines.Add("|---|---|---|---|---:|---|")
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus })
foreach ($row in $nonPass) {
    $evidence = if ($row.evidence) { $row.evidence.Replace("|", "\|") } else { "" }
    $lines.Add("| $($row.status) | $($row.path) | $($row.observedStatus) | $($row.canonicalCategory) | $($row.occurrences) | $evidence |")
}
if ($nonPass.Count -eq 0) {
    $lines.Add("| STATUS_TAXONOMY_PASS | <all> | <mapped> | <known> | 0 | All observed statuses map to canonical categories |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Status taxonomy report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors), unknownStatuses=$(@($summary.unknownStatuses).Count)"
