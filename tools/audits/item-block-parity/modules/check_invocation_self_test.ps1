[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown,
    [string]$OrchestratorPath,
    [string]$CheckRegistryPath
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot "tools/audits/item-block-parity/rules" }
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/check_invocation_self_test_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/check_invocation_self_test_report.md" }
$auditRoot = (Resolve-Path (Join-Path $RulesRoot "..")).Path

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}
function Add-Row {
    param(
        [System.Collections.Generic.List[object]]$Rows,
        [string]$CheckName,
        [string]$Status,
        [string]$Severity,
        [string]$OwnerType,
        [string]$OwnerPath,
        [string]$Invocation,
        [string]$Evidence
    )
    $Rows.Add([pscustomobject][ordered]@{
        check = "check_invocation"
        implementedCheck = $CheckName
        status = $Status
        severity = $Severity
        ownerType = $OwnerType
        ownerPath = $OwnerPath
        invocation = $Invocation
        evidence = $Evidence
    })
}

$registryPath = Join-Path $RulesRoot "check-registry.json"
$ownersPath = Join-Path $RulesRoot "check-invocation-rules.json"
if (-not (Test-Path -LiteralPath $registryPath -PathType Leaf)) { throw "Check registry not found: $registryPath" }
if (-not (Test-Path -LiteralPath $ownersPath -PathType Leaf)) { throw "Check invocation owner rules not found: $ownersPath" }

$registry = Get-Content -Raw -LiteralPath $registryPath | ConvertFrom-Json
$ownerRules = Get-Content -Raw -LiteralPath $ownersPath | ConvertFrom-Json
$passStatus = if ($ownerRules.status.pass) { [string]$ownerRules.status.pass } else { "CHECK_INVOCATION_PASS" }
$reviewStatus = if ($ownerRules.status.review) { [string]$ownerRules.status.review } else { "CHECK_INVOCATION_REVIEW_NEEDED" }
$errorStatus = if ($ownerRules.status.error) { [string]$ownerRules.status.error } else { "CHECK_INVOCATION_ERROR" }

$ownerLookup = @{}
foreach ($owner in @($ownerRules.owners)) {
    if ($null -eq $owner -or [string]::IsNullOrWhiteSpace($owner.name)) { continue }
    if ($ownerLookup.ContainsKey($owner.name)) {
        $ownerLookup[$owner.name] = @($ownerLookup[$owner.name]) + $owner
    } else {
        $ownerLookup[$owner.name] = @($owner)
    }
}

$implemented = @($registry.checks | Where-Object { $_.status -eq "implemented" } | Sort-Object name)
$rows = [System.Collections.Generic.List[object]]::new()
foreach ($entry in $implemented) {
    $name = [string]$entry.name
    if (-not $ownerLookup.ContainsKey($name)) {
        Add-Row $rows $name $errorStatus "error" "" "" "" "Implemented check has no invocation owner rule."
        continue
    }
    $owners = @($ownerLookup[$name])
    if ($owners.Count -ne 1) {
        Add-Row $rows $name $errorStatus "error" "" "" "" "Implemented check has $($owners.Count) owner rules; expected exactly one."
        continue
    }
    $owner = $owners[0]
    $ownerType = [string]$owner.ownerType
    $ownerPath = [string]$owner.ownerPath
    $invocation = [string]$owner.invocation
    if ([string]::IsNullOrWhiteSpace($ownerType) -or [string]::IsNullOrWhiteSpace($ownerPath) -or [string]::IsNullOrWhiteSpace($invocation)) {
        Add-Row $rows $name $errorStatus "error" $ownerType $ownerPath $invocation "Owner rule is missing ownerType, ownerPath or invocation."
        continue
    }
    $fullOwnerPath = Join-Path $auditRoot $ownerPath
    if (-not (Test-Path -LiteralPath $fullOwnerPath -PathType Leaf)) {
        Add-Row $rows $name $errorStatus "error" $ownerType $ownerPath $invocation "Owner path does not exist: $(ConvertTo-RelativeRepoPath $fullOwnerPath)"
        continue
    }
    Add-Row $rows $name $passStatus "info" $ownerType $ownerPath $invocation "Implemented check has a single existing invocation owner."
}

foreach ($owner in @($ownerRules.owners | Sort-Object name)) {
    if ($owner.name -notin @($implemented.name)) {
        Add-Row $rows ([string]$owner.name) $reviewStatus "review" ([string]$owner.ownerType) ([string]$owner.ownerPath) ([string]$owner.invocation) "Owner rule exists for a check that is not currently implemented."
    }
}

$orderedRows = @($rows | Sort-Object severity, implementedCheck, status)
$summary = [ordered]@{
    rows = $orderedRows.Count
    pass = @($orderedRows | Where-Object status -eq $passStatus).Count
    reviewNeeded = @($orderedRows | Where-Object status -eq $reviewStatus).Count
    errors = @($orderedRows | Where-Object status -eq $errorStatus).Count
    implementedChecks = @($implemented).Count
    ownerRules = @($ownerRules.owners).Count
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "Report-only implemented-check invocation self-test. This verifies every implemented registry check has exactly one existing comparer/module/script owner before verifier v2 treats registry status as authoritative."
    inputs = [ordered]@{
        registry = ConvertTo-RelativeRepoPath $registryPath
        ownerRules = ConvertTo-RelativeRepoPath $ownersPath
        checks = @($Checks)
    }
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block parity check invocation self-test")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: report-only ownership self-test. Errors are framework wiring failures, not gameplay parity failures.")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("- Rows: $($summary.rows)")
$lines.Add("- Pass: $($summary.pass)")
$lines.Add("- Review needed: $($summary.reviewNeeded)")
$lines.Add("- Errors: $($summary.errors)")
$lines.Add("- Implemented checks: $($summary.implementedChecks)")
$lines.Add("- Owner rules: $($summary.ownerRules)")
$lines.Add("")
$lines.Add("## Non-pass rows")
$lines.Add("")
$lines.Add("| Status | Implemented check | Owner type | Owner path | Invocation | Evidence |")
$lines.Add("|---|---|---|---|---|---|")
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus })
foreach ($row in $nonPass) {
    $evidence = if ($row.evidence) { $row.evidence.Replace("|", "\|") } else { "" }
    $lines.Add("| $($row.status) | $($row.implementedCheck) | $($row.ownerType) | $($row.ownerPath) | $($row.invocation) | $evidence |")
}
if ($nonPass.Count -eq 0) {
    $lines.Add("| CHECK_INVOCATION_PASS | <all> | <all> | <all> | <all> | Every implemented check has one existing owner |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Check invocation self-test report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors), implementedChecks=$($summary.implementedChecks), ownerRules=$($summary.ownerRules)"