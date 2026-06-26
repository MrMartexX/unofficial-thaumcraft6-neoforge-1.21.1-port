[CmdletBinding()]
param(
    [string]$RepoRoot,
    [switch]$AllowDirty,
    [switch]$FullAudit,
    [switch]$SkipAuditRun,
    [ValidateSet("off", "safe", "strict")][string]$FailMode = "off",
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"

if (-not $RepoRoot) {
    $RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "../../..")).Path
}
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path
Set-Location -LiteralPath $RepoRoot

$reportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity"
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot "item_block_framework_verifier_v2_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot "item_block_framework_verifier_v2_report.md" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}
function Escape-MarkdownCell([object]$Value) {
    if ($null -eq $Value) { return "" }
    $s = if ($Value -is [array]) { ($Value -join "; ") } else { [string]$Value }
    return $s.Replace("|", "\|").Replace("`r", " ").Replace("`n", " ")
}
function Add-Row {
    param(
        [System.Collections.Generic.List[object]]$Rows,
        [string]$Area,
        [string]$Status,
        [string]$Severity,
        [string]$Evidence,
        [object]$Details
    )
    $Rows.Add([pscustomobject][ordered]@{
        area = $Area
        status = $Status
        severity = $Severity
        evidence = $Evidence
        details = $Details
    })
}
function Read-JsonFile([string]$Path) { return Get-Content -Raw -LiteralPath $Path | ConvertFrom-Json }
function Get-SummaryInt {
    param([object]$Report, [string]$Name)
    if ($null -eq $Report -or $null -eq $Report.summary) { return 0 }
    $value = $Report.summary.$Name
    if ($null -eq $value) { return 0 }
    return [int]$value
}

$rows = [System.Collections.Generic.List[object]]::new()
$passStatus = "FRAMEWORK_VERIFIER_PASS"
$reviewStatus = "FRAMEWORK_VERIFIER_REVIEW_NEEDED"
$errorStatus = "FRAMEWORK_VERIFIER_ERROR"

$auditRoot = Join-Path $RepoRoot "tools/audits/item-block-parity"
$auditScript = Join-Path $auditRoot "audit-item-block-parity.ps1"
$rulesRoot = Join-Path $auditRoot "rules"
$registryPath = Join-Path $rulesRoot "check-registry.json"
$ownerRulesPath = Join-Path $rulesRoot "check-invocation-rules.json"
$matrixPath = Join-Path $RepoRoot "06_docs/audits/item_block_parity_layer_completion_matrix.md"
$ciWorkflowPath = Join-Path $RepoRoot ".github/workflows/item-block-framework-verifier.yml"
$goldenFocusedFamiliesScriptPath = Join-Path $auditRoot "run-golden-focused-families.ps1"
$goldenFocusedFamiliesRulesPath = Join-Path $rulesRoot "golden-focused-families.json"

if (Test-Path -LiteralPath $auditScript -PathType Leaf) {
    Add-Row $rows "required_paths" $passStatus "info" "Audit orchestrator exists." ([ordered]@{ path = ConvertTo-RelativeRepoPath $auditScript })
} else {
    Add-Row $rows "required_paths" $errorStatus "error" "Audit orchestrator is missing." ([ordered]@{ path = ConvertTo-RelativeRepoPath $auditScript })
}
if (-not (Test-Path -LiteralPath $registryPath -PathType Leaf)) {
    Add-Row $rows "required_paths" $errorStatus "error" "Check registry is missing." ([ordered]@{ path = ConvertTo-RelativeRepoPath $registryPath })
}
if (-not (Test-Path -LiteralPath $ownerRulesPath -PathType Leaf)) {
    Add-Row $rows "required_paths" $errorStatus "error" "Invocation owner rules are missing." ([ordered]@{ path = ConvertTo-RelativeRepoPath $ownerRulesPath })
}

if (Test-Path -LiteralPath $ciWorkflowPath -PathType Leaf) {
    $ciText = Get-Content -LiteralPath $ciWorkflowPath -Raw
    $requiredCiTokens = @(
        'workflow_dispatch',
        'actions/checkout@v4',
        'actions/setup-java@v4',
        'actions/upload-artifact@v4',
        'verify-item-block-parity-framework.ps1',
        'tools/reports/local/item-block-parity/*.json',
        'tools/reports/local/item-block-parity/*.md'
    )
    $missingCiTokens = @($requiredCiTokens | Where-Object { $ciText -notlike "*$_*" })
    if ($missingCiTokens.Count -eq 0) {
        Add-Row $rows "ci_workflow" $passStatus "info" "CI verifier workflow exists and uploads local report artifacts." ([ordered]@{ path = ConvertTo-RelativeRepoPath $ciWorkflowPath })
    } else {
        Add-Row $rows "ci_workflow" $errorStatus "error" "CI verifier workflow is missing required wiring tokens." ([ordered]@{ path = ConvertTo-RelativeRepoPath $ciWorkflowPath; missing = @($missingCiTokens) })
    }
} else {
    Add-Row $rows "ci_workflow" $reviewStatus "review" "CI verifier workflow is not present; local verifier still works, but CI artifact publication is not configured." ([ordered]@{ path = ConvertTo-RelativeRepoPath $ciWorkflowPath })
}

if ((Test-Path -LiteralPath $goldenFocusedFamiliesScriptPath -PathType Leaf) -and (Test-Path -LiteralPath $goldenFocusedFamiliesRulesPath -PathType Leaf)) {
    $goldenScriptText = Get-Content -LiteralPath $goldenFocusedFamiliesScriptPath -Raw
    $goldenRequiredTokens = @(
        'golden-focused-families.json',
        'audit-item-block-parity.ps1',
        '$auditParams.Families',
        '$auditParams.IdPrefix',
        'item_block_golden_focused_families_report.json'
    )
    $goldenMissingTokens = @($goldenRequiredTokens | Where-Object { $goldenScriptText -notlike "*$_*" })
    try {
        $goldenRules = Read-JsonFile $goldenFocusedFamiliesRulesPath
        $goldenFamilyCount = @($goldenRules.families).Count
    } catch {
        $goldenFamilyCount = 0
        $goldenMissingTokens = @($goldenMissingTokens + "valid-golden-focused-families-json")
    }
    if ($goldenMissingTokens.Count -eq 0 -and $goldenFamilyCount -gt 0) {
        Add-Row $rows "golden_focused_families" $passStatus "info" "Golden focused family runner and rules are present." ([ordered]@{ script = ConvertTo-RelativeRepoPath $goldenFocusedFamiliesScriptPath; rules = ConvertTo-RelativeRepoPath $goldenFocusedFamiliesRulesPath; families = $goldenFamilyCount })
    } else {
        Add-Row $rows "golden_focused_families" $errorStatus "error" "Golden focused family wiring is incomplete." ([ordered]@{ script = ConvertTo-RelativeRepoPath $goldenFocusedFamiliesScriptPath; rules = ConvertTo-RelativeRepoPath $goldenFocusedFamiliesRulesPath; missing = @($goldenMissingTokens); families = $goldenFamilyCount })
    }
} else {
    Add-Row $rows "golden_focused_families" $reviewStatus "review" "Golden focused family runner/rules are not present; broad framework verifier still works, but focused regression slices are not configured." ([ordered]@{ script = ConvertTo-RelativeRepoPath $goldenFocusedFamiliesScriptPath; rules = ConvertTo-RelativeRepoPath $goldenFocusedFamiliesRulesPath })
}
$minimalGameTestFixturePath = Join-Path $RepoRoot "05_neoforge_port/src/main/java/thaumcraft/common/runtime/TCMinimalGameTestFixture.java"
$minimalGameTestFixtureExporterPath = Join-Path $RepoRoot "05_neoforge_port/src/main/java/thaumcraft/common/runtime/TCMinimalGameTestFixtureExporter.java"
$minimalGameTestBuildPath = Join-Path $RepoRoot "05_neoforge_port/build.gradle"
$minimalGameTestBootstrapPath = Join-Path $RepoRoot "05_neoforge_port/src/main/java/thaumcraft/Thaumcraft.java"
$minimalFixtureMissingTokens = [System.Collections.Generic.List[string]]::new()
if (Test-Path -LiteralPath $minimalGameTestFixturePath -PathType Leaf) {
    $minimalFixtureText = Get-Content -LiteralPath $minimalGameTestFixturePath -Raw
    foreach ($token in @('tc.minimalGameTestFixture', 'TCBlocks.ARCANE_WORKBENCH', 'TCBlocks.RESEARCH_TABLE', 'TCBlocks.CRUCIBLE', 'TCItems.THAUMONOMICON', 'TCItems.THAUMOMETER')) {
        if ($minimalFixtureText -notlike "*$token*") { $minimalFixtureMissingTokens.Add($token) }
    }
} else {
    $minimalFixtureMissingTokens.Add('TCMinimalGameTestFixture.java')
}
if (Test-Path -LiteralPath $minimalGameTestFixtureExporterPath -PathType Leaf) {
    $minimalExporterText = Get-Content -LiteralPath $minimalGameTestFixtureExporterPath -Raw
    foreach ($token in @('ServerStartedEvent', 'TCMinimalGameTestFixture.writeMarkdown', 'event.getServer().halt(false)')) {
        if ($minimalExporterText -notlike "*$token*") { $minimalFixtureMissingTokens.Add($token) }
    }
} else {
    $minimalFixtureMissingTokens.Add('TCMinimalGameTestFixtureExporter.java')
}
if (Test-Path -LiteralPath $minimalGameTestBuildPath -PathType Leaf) {
    $minimalBuildText = Get-Content -LiteralPath $minimalGameTestBuildPath -Raw
    foreach ($token in @('tc.minimalGameTestFixture', 'tcMinimalGameTestFixturePath')) {
        if ($minimalBuildText -notlike "*$token*") { $minimalFixtureMissingTokens.Add($token) }
    }
}
if (Test-Path -LiteralPath $minimalGameTestBootstrapPath -PathType Leaf) {
    $minimalBootstrapText = Get-Content -LiteralPath $minimalGameTestBootstrapPath -Raw
    if ($minimalBootstrapText -notlike '*TCMinimalGameTestFixtureExporter::onServerStarted*') { $minimalFixtureMissingTokens.Add('TCMinimalGameTestFixtureExporter::onServerStarted') }
}
if ($minimalFixtureMissingTokens.Count -eq 0) {
    Add-Row $rows "minimal_gametest_fixture" $passStatus "info" "Minimal scripted GameTest fixture source, exporter, Gradle opt-in and bootstrap listener are present." ([ordered]@{ fixture = ConvertTo-RelativeRepoPath $minimalGameTestFixturePath; exporter = ConvertTo-RelativeRepoPath $minimalGameTestFixtureExporterPath })
} else {
    Add-Row $rows "minimal_gametest_fixture" $errorStatus "error" "Minimal scripted GameTest fixture wiring is incomplete." ([ordered]@{ missing = @($minimalFixtureMissingTokens) })
}
$gitDirty = @(& git status --short)
if ($gitDirty.Count -gt 0) {
    $status = if ($AllowDirty) { $reviewStatus } else { $errorStatus }
    $severity = if ($AllowDirty) { "review" } else { "error" }
    Add-Row $rows "git_working_tree" $status $severity "Working tree is not clean." ([ordered]@{ entries = @($gitDirty) })
} else {
    Add-Row $rows "git_working_tree" $passStatus "info" "Working tree is clean." ([ordered]@{})
}

$diffCheckOutput = @(& git diff --check 2>&1 | ForEach-Object { [string]$_ })
if ($LASTEXITCODE -eq 0) {
    Add-Row $rows "git_diff_check" $passStatus "info" "git diff --check found no whitespace errors." ([ordered]@{ output = @($diffCheckOutput) })
} else {
    Add-Row $rows "git_diff_check" $errorStatus "error" "git diff --check found whitespace errors." ([ordered]@{ output = @($diffCheckOutput) })
}

$psFiles = @(Get-ChildItem -LiteralPath $auditRoot -Filter "*.ps1" -Recurse -File | Sort-Object FullName)
$parseErrorCount = 0
$parseErrorFiles = [System.Collections.Generic.List[object]]::new()
foreach ($file in $psFiles) {
    $parseErrors = $null
    [System.Management.Automation.PSParser]::Tokenize((Get-Content -LiteralPath $file.FullName -Raw), [ref]$parseErrors) | Out-Null
    if ($parseErrors.Count -gt 0) {
        $parseErrorCount += $parseErrors.Count
        $parseErrorFiles.Add([pscustomobject][ordered]@{
            path = ConvertTo-RelativeRepoPath $file.FullName
            errors = @($parseErrors | ForEach-Object { [string]$_ })
        })
    }
}
if ($parseErrorCount -eq 0) {
    Add-Row $rows "powershell_parser" $passStatus "info" "All audit PowerShell scripts parse successfully." ([ordered]@{ files = $psFiles.Count })
} else {
    Add-Row $rows "powershell_parser" $errorStatus "error" "PowerShell parser errors were found." ([ordered]@{ files = @($parseErrorFiles); errors = $parseErrorCount })
}

$jsonFiles = @(Get-ChildItem -LiteralPath $rulesRoot -Filter "*.json" -Recurse -File | Sort-Object FullName)
$jsonErrorFiles = [System.Collections.Generic.List[object]]::new()
foreach ($file in $jsonFiles) {
    try { Get-Content -Raw -LiteralPath $file.FullName | ConvertFrom-Json | Out-Null }
    catch {
        $jsonErrorFiles.Add([pscustomobject][ordered]@{
            path = ConvertTo-RelativeRepoPath $file.FullName
            error = $_.Exception.Message
        })
    }
}
if ($jsonErrorFiles.Count -eq 0) {
    Add-Row $rows "rule_json" $passStatus "info" "All audit rule JSON files parse successfully." ([ordered]@{ files = $jsonFiles.Count })
} else {
    Add-Row $rows "rule_json" $errorStatus "error" "Rule JSON parse errors were found." ([ordered]@{ files = @($jsonErrorFiles) })
}

$orchestratorText = if (Test-Path -LiteralPath $auditScript -PathType Leaf) { Get-Content -LiteralPath $auditScript -Raw } else { "" }
$joinedBatchMarkers = @([regex]::Matches($orchestratorText, '# Batch \d+ [^\r\n]+ end# Batch \d+') | ForEach-Object { $_.Value })
if ($joinedBatchMarkers.Count -eq 0) {
    Add-Row $rows "orchestrator_comments" $passStatus "info" "No joined batch end/start comments were found." ([ordered]@{})
} else {
    Add-Row $rows "orchestrator_comments" $reviewStatus "review" "Joined batch comments were found; these are cosmetic if invocation variables remain on separate lines." ([ordered]@{ markers = @($joinedBatchMarkers) })
}

$registry = $null
$ownerRules = $null
if (Test-Path -LiteralPath $registryPath -PathType Leaf) { $registry = Read-JsonFile $registryPath }
if (Test-Path -LiteralPath $ownerRulesPath -PathType Leaf) { $ownerRules = Read-JsonFile $ownerRulesPath }

if ($registry -and $ownerRules) {
    $implemented = @($registry.checks | Where-Object { $_.status -eq "implemented" })
    $nonImplemented = @($registry.checks | Where-Object { $_.status -ne "implemented" })
    if ($nonImplemented.Count -eq 0) {
        Add-Row $rows "registry_status" $passStatus "info" "All currently registered checks are implemented." ([ordered]@{ implemented = $implemented.Count; nonImplemented = 0 })
    } else {
        Add-Row $rows "registry_status" $reviewStatus "review" "Some registered checks are not implemented." ([ordered]@{ implemented = $implemented.Count; nonImplemented = @($nonImplemented | Select-Object name, layer, status, reason) })
    }
    $ownerNames = @($ownerRules.owners | ForEach-Object { [string]$_.name })
    $missingOwners = @($implemented | Where-Object { $_.name -notin $ownerNames } | ForEach-Object { [string]$_.name })
    if ($missingOwners.Count -eq 0) {
        Add-Row $rows "owner_rules" $passStatus "info" "Every implemented check has an owner-rule name entry." ([ordered]@{ implemented = $implemented.Count; ownerRules = @($ownerRules.owners).Count })
    } else {
        Add-Row $rows "owner_rules" $errorStatus "error" "Implemented checks are missing owner-rule entries." ([ordered]@{ missing = @($missingOwners) })
    }
}

if (Test-Path -LiteralPath $matrixPath -PathType Leaf) {
    $matrixText = Get-Content -LiteralPath $matrixPath -Raw
    if ($matrixText -match '\| <none> \| <none> \| All currently registered checks are implemented\. \| No planned framework check remains in the registry\. \|') {
        Add-Row $rows "completion_backlog" $passStatus "info" "Layer completion matrix reports no planned framework backlog." ([ordered]@{ path = ConvertTo-RelativeRepoPath $matrixPath })
    } else {
        Add-Row $rows "completion_backlog" $reviewStatus "review" "Layer completion matrix does not report an empty backlog." ([ordered]@{ path = ConvertTo-RelativeRepoPath $matrixPath })
    }
} else {
    Add-Row $rows "completion_backlog" $errorStatus "error" "Layer completion matrix is missing." ([ordered]@{ path = ConvertTo-RelativeRepoPath $matrixPath })
}

if (-not $SkipAuditRun) {
    if ($FullAudit) {
        Write-Output "Running full report-only item/block parity framework audit."
        & $auditScript -RepoRoot $RepoRoot -Preset full -UseCachedLegacy -FailMode off
    } else {
        Write-Output "Running core report-only item/block parity framework smoke."
        $coreChecks = @("json_validity", "runtime_smoke", "check_invocation", "docs_deferred", "status_taxonomy", "report_freshness", "report_schema")
        & $auditScript -RepoRoot $RepoRoot -Checks $coreChecks -UseCachedLegacy -FailMode off
    }
    if (-not $?) {
        Add-Row $rows "audit_smoke" $errorStatus "error" "Audit smoke command failed." ([ordered]@{ fullAudit = [bool]$FullAudit })
    } else {
        Add-Row $rows "audit_smoke" $passStatus "info" "Audit smoke command completed." ([ordered]@{ fullAudit = [bool]$FullAudit })
    }
}

$reportFilesToCheck = @(
    "item_block_check_invocation_self_test_report.json",
    "docs_registry_consistency_report.json",
    "status_taxonomy_report.json",
    "report_freshness_guard_report.json",
    "report_schema_contract_report.json",
    "item_block_runtime_smoke_report.json",
    "item_block_json_validity_report.json",
    "item_block_legacy_mapping_report.json",
    "item_block_variants_report.json",
    "item_block_creative_tabs_report.json",
    "item_block_data_components_report.json",
    "item_block_networking_report.json",
    "item_block_fuels_flammability_report.json",
    "item_block_equipment_report.json",
    "item_block_entity_links_report.json",
    "item_block_worldgen_links_report.json",
    "item_block_config_gates_report.json",
    "item_block_access_transformers_report.json",
    "item_block_public_api_report.json",
    "item_block_source_conflict_report.json",
    "item_block_original_jar_probe_report.json"
) | Select-Object -Unique

$checkedReportCount = 0
foreach ($reportName in $reportFilesToCheck) {
    $reportPath = Join-Path $reportRoot $reportName
    if (-not (Test-Path -LiteralPath $reportPath -PathType Leaf)) {
        Add-Row $rows "report_summary" $errorStatus "error" "Expected report is missing." ([ordered]@{ report = $reportName })
        continue
    }
    try {
        $report = Read-JsonFile $reportPath
        $errors = Get-SummaryInt $report "errors"
        $fail = Get-SummaryInt $report "fail"
        $checkedReportCount++
        if ($errors -gt 0 -or $fail -gt 0) {
            Add-Row $rows "report_summary" $errorStatus "error" "Report summary contains errors or failed steps." ([ordered]@{ report = $reportName; errors = $errors; fail = $fail })
        } else {
            Add-Row $rows "report_summary" $passStatus "info" "Report summary has no errors/failures." ([ordered]@{ report = $reportName; errors = $errors; fail = $fail })
        }
    } catch {
        Add-Row $rows "report_summary" $errorStatus "error" "Report JSON could not be parsed." ([ordered]@{ report = $reportName; error = $_.Exception.Message })
    }
}
Add-Row $rows "report_inventory" $passStatus "info" "Framework verifier inspected generated report summaries." ([ordered]@{ checkedReports = $checkedReportCount })

$orderedRows = @($rows)
$summary = [ordered]@{
    rows = $orderedRows.Count
    pass = @($orderedRows | Where-Object status -eq $passStatus).Count
    reviewNeeded = @($orderedRows | Where-Object status -eq $reviewStatus).Count
    errors = @($orderedRows | Where-Object status -eq $errorStatus).Count
}
$output = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "Verifier v2 framework certification gate. This checks parser validity, rule JSON, registry/owner wiring, docs backlog state, report-only audit smoke and generated report summaries. It does not launch Minecraft unless separate opt-in runtime commands are used outside this verifier."
    inputs = [ordered]@{
        repoRoot = $RepoRoot
        allowDirty = [bool]$AllowDirty
        fullAudit = [bool]$FullAudit
        skipAuditRun = [bool]$SkipAuditRun
        failMode = $FailMode
    }
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$output | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block framework verifier v2 report")
$lines.Add("")
$lines.Add("Generated: $($output.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($output.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | PASS | REVIEW | ERRORS |")
$lines.Add("|---:|---:|---:|---:|")
$lines.Add("| $($summary.rows) | $($summary.pass) | $($summary.reviewNeeded) | $($summary.errors) |")
$lines.Add("")
$lines.Add("## Non-pass rows")
$lines.Add("")
$lines.Add("| Status | Area | Severity | Evidence |")
$lines.Add("|---|---|---|---|")
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus })
foreach ($row in $nonPass) {
    $lines.Add("| $($row.status) | $($row.area) | $($row.severity) | $(Escape-MarkdownCell $row.evidence) |")
}
if ($nonPass.Count -eq 0) {
    $lines.Add("| FRAMEWORK_VERIFIER_PASS | <all> | info | Every verifier row passed. |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Framework verifier v2 report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors)"

if ($summary.errors -gt 0) { exit 1 }
exit 0
