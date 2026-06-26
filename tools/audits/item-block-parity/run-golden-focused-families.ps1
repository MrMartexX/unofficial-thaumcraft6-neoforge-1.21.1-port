[CmdletBinding()]
param(
    [string]$RepoRoot,
    [string[]]$Families,
    [string]$Preset,
    [ValidateSet("off", "safe", "strict")][string]$FailMode = "off",
    [switch]$RefreshLegacy,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"

if (-not $RepoRoot) {
    $RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "../../..")).Path
}
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path
Set-Location -LiteralPath $RepoRoot

$auditRoot = Join-Path $RepoRoot "tools/audits/item-block-parity"
$auditScript = Join-Path $auditRoot "audit-item-block-parity.ps1"
$rulesPath = Join-Path $auditRoot "rules/golden-focused-families.json"
$reportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity"
$logRoot = Join-Path $reportRoot "golden-focused-family-logs"
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot "item_block_golden_focused_families_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot "item_block_golden_focused_families_report.md" }
New-Item -ItemType Directory -Force -Path $reportRoot | Out-Null
New-Item -ItemType Directory -Force -Path $logRoot | Out-Null

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}
function Escape-MarkdownCell([object]$Value) {
    if ($null -eq $Value) { return "" }
    $s = if ($Value -is [array]) { ($Value -join "; ") } else { [string]$Value }
    return $s.Replace("|", "\|").Replace("`r", " ").Replace("`n", " ")
}

if (-not (Test-Path -LiteralPath $auditScript -PathType Leaf)) { throw "Audit script not found: $auditScript" }
if (-not (Test-Path -LiteralPath $rulesPath -PathType Leaf)) { throw "Golden focused family rules not found: $rulesPath" }

$rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
if (-not $Preset) { $Preset = if ($rules.defaultPreset) { [string]$rules.defaultPreset } else { "ci-safe" } }
$definitions = @($rules.families)
if ($Families -and $Families.Count -gt 0) {
    $wanted = @($Families | ForEach-Object { [string]$_ })
    $definitions = @($definitions | Where-Object { [string]$_.name -in $wanted })
    $missing = @($wanted | Where-Object { $_ -notin @($definitions | ForEach-Object { [string]$_.name }) })
    if ($missing.Count -gt 0) { throw "Unknown golden focused families: $($missing -join ', ')" }
}
if ($definitions.Count -eq 0) { throw "No golden focused family definitions selected." }

$rows = [System.Collections.Generic.List[object]]::new()
$pwsh = (Get-Command pwsh -ErrorAction Stop).Source
foreach ($definition in $definitions) {
    $name = [string]$definition.name
    $safeName = ($name -replace '[^A-Za-z0-9_.-]', '_')
    $logPath = Join-Path $logRoot "$safeName.log"
    $args = @(
        "-NoProfile",
        "-File", $auditScript,
        "-RepoRoot", $RepoRoot,
        "-Preset", $Preset,
        "-FailMode", "off"
    )
    if (-not $RefreshLegacy) { $args += "-UseCachedLegacy" }
    $familyValues = @($definition.families | ForEach-Object { [string]$_ } | Where-Object { -not [string]::IsNullOrWhiteSpace($_) })
    $prefixValues = @($definition.idPrefix | ForEach-Object { [string]$_ } | Where-Object { -not [string]::IsNullOrWhiteSpace($_) })
    $idValues = @($definition.ids | ForEach-Object { [string]$_ } | Where-Object { -not [string]::IsNullOrWhiteSpace($_) })
    if ($familyValues.Count -gt 0) { $args += "-Families"; $args += @($familyValues) }
    if ($prefixValues.Count -gt 0) { $args += "-IdPrefix"; $args += @($prefixValues) }
    if ($idValues.Count -gt 0) { $args += "-Ids"; $args += @($idValues) }

    $started = [DateTime]::UtcNow
    $output = @(& $pwsh @args 2>&1 | ForEach-Object { [string]$_ })
    $exitCode = if ($null -ne $LASTEXITCODE) { [int]$LASTEXITCODE } else { 0 }
    $finished = [DateTime]::UtcNow
    $output | Set-Content -LiteralPath $logPath -Encoding utf8NoBOM

    $rows.Add([pscustomobject][ordered]@{
        name = $name
        status = if ($exitCode -eq 0) { "GOLDEN_FOCUSED_FAMILY_PASS" } else { "GOLDEN_FOCUSED_FAMILY_ERROR" }
        severity = if ($exitCode -eq 0) { "info" } else { "error" }
        preset = $Preset
        families = @($familyValues)
        idPrefix = @($prefixValues)
        ids = @($idValues)
        exitCode = $exitCode
        startedAtUtc = $started.ToString("o")
        finishedAtUtc = $finished.ToString("o")
        log = ConvertTo-RelativeRepoPath $logPath
        evidence = if ($exitCode -eq 0) { "Focused audit completed." } else { "Focused audit failed; inspect log." }
    })
}

$orderedRows = @($rows)
$errors = @($orderedRows | Where-Object { $_.status -eq "GOLDEN_FOCUSED_FAMILY_ERROR" }).Count
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "Report-only golden focused family smoke. Each row runs the item/block parity orchestrator with dependency-aware focused filtering for a stable family slice."
    inputs = [ordered]@{
        repoRoot = $RepoRoot
        preset = $Preset
        selectedFamilies = @($definitions | ForEach-Object { [string]$_.name })
        failMode = $FailMode
        refreshLegacy = [bool]$RefreshLegacy
    }
    summary = [ordered]@{
        rows = $orderedRows.Count
        pass = @($orderedRows | Where-Object { $_.status -eq "GOLDEN_FOCUSED_FAMILY_PASS" }).Count
        reviewNeeded = 0
        errors = $errors
    }
    results = @($orderedRows)
}
$report | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block golden focused families report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | PASS | REVIEW | ERRORS |")
$lines.Add("|---:|---:|---:|---:|")
$lines.Add("| $($report.summary.rows) | $($report.summary.pass) | $($report.summary.reviewNeeded) | $($report.summary.errors) |")
$lines.Add("")
$lines.Add("## Families")
$lines.Add("")
$lines.Add("| Family | Status | Preset | Families | ID prefixes | Exit code | Log |")
$lines.Add("|---|---|---|---|---|---:|---|")
foreach ($row in $orderedRows) {
    $lines.Add("| $($row.name) | $($row.status) | $($row.preset) | $(Escape-MarkdownCell $row.families) | $(Escape-MarkdownCell $row.idPrefix) | $($row.exitCode) | ``$($row.log)`` |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Golden focused families report: $OutputMarkdown"
Write-Output "Rows=$($report.summary.rows), pass=$($report.summary.pass), reviewNeeded=$($report.summary.reviewNeeded), errors=$($report.summary.errors)"
if ($errors -gt 0 -and $FailMode -ne "off") { exit 1 }
exit 0
