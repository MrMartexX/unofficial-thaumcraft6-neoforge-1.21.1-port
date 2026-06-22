[CmdletBinding()]
param(
    [string]$RepoRoot,
    [string]$LegacyRoot = "02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master",
    [string]$PortRoot = "05_neoforge_port",
    [ValidateSet("quick", "resources", "registry")][string]$Preset = "quick",
    [string[]]$Checks,
    [string[]]$Ids,
    [switch]$RefreshLegacy,
    [switch]$ListChecks,
    [switch]$ExplainPlan,
    [switch]$WriteCuratedSummary,
    [ValidateSet("off", "safe", "strict")][string]$FailMode = "safe"
)

$ErrorActionPreference = "Stop"
if (-not $RepoRoot) {
    $RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "../../..")).Path
}
$RepoRoot = (Resolve-Path $RepoRoot).Path
$implementedChecks = @("registry", "duplicate_registry_id", "block_item_pairs", "blockstates", "models", "lang", "loot")
$presetChecks = @{
    registry = @("registry", "duplicate_registry_id", "block_item_pairs")
    quick = @("registry", "duplicate_registry_id", "block_item_pairs", "blockstates", "models", "lang")
    resources = @("duplicate_registry_id", "block_item_pairs", "blockstates", "models", "lang", "loot")
}

if ($ListChecks) {
    $implementedChecks
    exit 0
}
if (-not $Checks -or $Checks.Count -eq 0) { $Checks = $presetChecks[$Preset] }
$unknown = @($Checks | Where-Object { $_ -notin $implementedChecks })
if ($unknown.Count -gt 0) { throw "Checks are not implemented in Batch 1: $($unknown -join ', ')" }

$reportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity"
$legacyManifest = Join-Path $reportRoot "legacy_primary_manifest.json"
$portManifest = Join-Path $reportRoot "port_manifest.json"
$reportJson = Join-Path $reportRoot "item_block_parity_report.json"
$reportMarkdown = Join-Path $reportRoot "item_block_parity_report.md"
$rulesRoot = Join-Path $PSScriptRoot "rules"

function Test-LegacyCacheFresh {
    if (-not (Test-Path -LiteralPath $legacyManifest -PathType Leaf)) { return $false }
    try {
        $cached = Get-Content -Raw -LiteralPath $legacyManifest | ConvertFrom-Json
        if ($cached.schemaVersion -ne 1 -or -not $cached.sourceFiles) { return $false }
        $legacyPath = Join-Path $RepoRoot $LegacyRoot
        foreach ($sourceFile in $cached.sourceFiles) {
            $fullPath = Join-Path $legacyPath $sourceFile.path
            if (-not (Test-Path -LiteralPath $fullPath -PathType Leaf)) { return $false }
            $currentHash = (Get-FileHash -Algorithm SHA256 -LiteralPath $fullPath).Hash.ToLowerInvariant()
            if ($currentHash -ne $sourceFile.sha256) { return $false }
        }
        return $true
    } catch {
        return $false
    }
}

$legacyCacheFresh = Test-LegacyCacheFresh

if ($ExplainPlan) {
    Write-Output "Item/block parity Batch 1 execution plan"
    Write-Output "RepoRoot: $RepoRoot"
    Write-Output "Primary legacy: $LegacyRoot"
    Write-Output "Port: $PortRoot"
    Write-Output "Checks: $($Checks -join ', ')"
    Write-Output "IDs: $(if ($Ids) { $Ids -join ', ' } else { '<all>' })"
    Write-Output "FailMode: $FailMode"
    Write-Output "Legacy cache: $(if ($RefreshLegacy -or -not $legacyCacheFresh) { 'refresh' } else { 'reuse verified fingerprinted manifest' })"
    Write-Output "Not evaluated: variants, texture graph, data references, behavior, runtime, visual parity"
    exit 0
}

New-Item -ItemType Directory -Force -Path $reportRoot | Out-Null
$legacyExtractor = Join-Path $PSScriptRoot "extract-legacy-primary-manifest.ps1"
$portExtractor = Join-Path $PSScriptRoot "extract-port-manifest.ps1"
$comparer = Join-Path $PSScriptRoot "compare-item-block-parity.ps1"
$validator = Join-Path $PSScriptRoot "validate-parity-report.ps1"

if ($RefreshLegacy -or -not $legacyCacheFresh) {
    & $legacyExtractor -RepoRoot $RepoRoot -LegacyRoot $LegacyRoot -OutputPath $legacyManifest
    if (-not $?) { throw "Legacy manifest extraction failed." }
} else {
    Write-Output "Using cached legacy manifest: $legacyManifest"
}

& $portExtractor -RepoRoot $RepoRoot -PortRoot $PortRoot -OutputPath $portManifest
if (-not $?) { throw "Port manifest extraction failed." }

$compareArguments = @{
    LegacyManifest = $legacyManifest
    PortManifest = $portManifest
    RulesRoot = $rulesRoot
    OutputJson = $reportJson
    OutputMarkdown = $reportMarkdown
    Checks = $Checks
    Ids = $Ids
    FailMode = $FailMode
}
if ($WriteCuratedSummary) {
    $compareArguments.CuratedSummaryPath = Join-Path $RepoRoot "06_docs/audits/item_block_parity_baseline_summary.md"
}
& $comparer @compareArguments
$compareSucceeded = $?
$compareExitCode = $LASTEXITCODE
& $validator -LegacyManifest $legacyManifest -PortManifest $portManifest -ReportPath $reportJson
if (-not $?) { throw "Parity report validation failed." }
if (-not $compareSucceeded) {
    if ($null -ne $compareExitCode) { exit $compareExitCode }
    exit 1
}
exit 0
