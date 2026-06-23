[CmdletBinding()]
param(
    [string]$RepoRoot,
    [string]$LegacyRoot = "02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master",
    [string]$SecondaryLegacyRoot = "03_self_decompiled_check/vineflower_thaumcraft6",
    [string]$OriginalJar = "01_original_jar/Thaumcraft-1.12.2-6.1.BETA26.jar",
    [string]$PortRoot = "05_neoforge_port",
    [ValidateSet("quick", "resources", "data", "behavior-boundary", "source-quality", "ci-safe", "full", "registry")]
    [string]$Preset = "quick",
    [string[]]$Checks,
    [string[]]$Ids,
    [string[]]$IdPrefix,
    [string[]]$Families,
    [string[]]$Packages,
    [switch]$ChangedOnly,
    [string]$SinceCommit,
    [switch]$RefreshLegacy,
    [switch]$UseCachedLegacy,
    [switch]$ProbeSecondaryLegacy,
    [switch]$ListChecks,
    [switch]$ExplainPlan,
    [switch]$WriteCuratedSummary,
    [switch]$RunBuild,
    [switch]$RunSmoke,
    [switch]$RunRelatedAudits,
    [ValidateSet("off", "safe", "strict")]
    [string]$FailMode = "safe"
)

$ErrorActionPreference = "Stop"
if (-not $RepoRoot) {
    $RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "../../..")).Path
}
$RepoRoot = (Resolve-Path $RepoRoot).Path

$reportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity"
$legacyManifest = Join-Path $reportRoot "legacy_primary_manifest.json"
$portManifest = Join-Path $reportRoot "port_manifest.json"
$reportJson = Join-Path $reportRoot "item_block_parity_report.json"
$reportMarkdown = Join-Path $reportRoot "item_block_parity_report.md"
$notEvaluatedJson = Join-Path $reportRoot "item_block_parity_not_evaluated_checks.json"
$notEvaluatedMarkdown = Join-Path $reportRoot "item_block_parity_not_evaluated_checks.md"
$rulesRoot = Join-Path $PSScriptRoot "rules"
$checkRegistryPath = Join-Path $rulesRoot "check-registry.json"

if (-not (Test-Path -LiteralPath $checkRegistryPath -PathType Leaf)) {
    throw "Check registry not found: $checkRegistryPath"
}
$checkRegistry = Get-Content -Raw -LiteralPath $checkRegistryPath | ConvertFrom-Json
$implementedChecks = @($checkRegistry.checks | Where-Object { $_.status -eq "implemented" } | ForEach-Object { $_.name })
$allKnownChecks = @($checkRegistry.checks | ForEach-Object { $_.name })

$presetChecks = @{
    registry = @("registry", "duplicate_registry_id", "block_item_pairs")
    quick = @("registry", "duplicate_registry_id", "block_item_pairs", "blockstates", "models", "textures", "lang", "orphan_references")
    resources = @("blockstates", "models", "textures", "lang", "loot", "tags", "recipes", "orphan_references")
    data = @("recipes", "loot", "tags", "fuels_flammability", "aspects", "research_refs", "thaumonomicon_refs")
    "behavior-boundary" = @("item_properties", "block_properties", "blockentities", "capabilities", "menus", "networking", "client_server_safety")
    "source-quality" = @("legacy_primary_manifest", "secondary_legacy_probe", "source_conflict_report", "original_jar_probe")
    "ci-safe" = @("registry", "json_validity", "blockstates", "models", "textures", "lang", "orphan_references", "client_server_safety", "datapack_load")
    full = @($allKnownChecks)
}

if (-not $Checks -or $Checks.Count -eq 0) {
    $Checks = @($presetChecks[$Preset])
}
if ($ProbeSecondaryLegacy) {
    $Checks = @($Checks + "secondary_legacy_probe") | Select-Object -Unique
}
$Checks = @($Checks | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Select-Object -Unique)

$unknown = @($Checks | Where-Object { $_ -notin $allKnownChecks })
if ($unknown.Count -gt 0) {
    throw "Unknown item/block parity checks: $($unknown -join ', ')"
}

$implementedSelected = @($Checks | Where-Object { $_ -in $implementedChecks })
$notEvaluated = @($Checks | Where-Object { $_ -notin $implementedChecks })
$comparerChecks = @("registry", "duplicate_registry_id", "block_item_pairs", "blockstates", "models", "textures", "lang", "loot", "orphan_references")
$comparerSelected = @($implementedSelected | Where-Object { $_ -in $comparerChecks })
$moduleSelected = @($implementedSelected | Where-Object { $_ -notin $comparerChecks })

function Write-NotEvaluatedReport {
    param([string[]]$NotEvaluatedChecks)
    New-Item -ItemType Directory -Force -Path $reportRoot | Out-Null
    $rows = @(
        foreach ($name in $NotEvaluatedChecks) {
            $entry = @($checkRegistry.checks | Where-Object { $_.name -eq $name } | Select-Object -First 1)
            [pscustomobject][ordered]@{
                name = $name
                status = if ($entry) { $entry.status } else { "unknown" }
                layer = if ($entry) { $entry.layer } else { "unknown" }
                reason = if ($entry) { $entry.reason } else { "Not in check registry" }
            }
        }
    )
    $output = [ordered]@{
        schemaVersion = 1
        generatedAtUtc = [DateTime]::UtcNow.ToString("o")
        preset = $Preset
        selectedChecks = @($Checks)
        implementedSelected = @($implementedSelected)
        notEvaluated = @($rows)
        note = "Batch 2 recognizes planned checks without pretending they are implemented."
    }
    $output | ConvertTo-Json -Depth 10 | Set-Content -LiteralPath $notEvaluatedJson -Encoding utf8NoBOM

    $lines = [System.Collections.Generic.List[string]]::new()
    $lines.Add("# Item/block parity not-evaluated checks")
    $lines.Add("")
    $lines.Add("Generated: $($output.generatedAtUtc)")
    $lines.Add("")
    $lines.Add("Preset: ``$Preset``")
    $lines.Add("")
    $lines.Add("| Check | Layer | Status | Reason |")
    $lines.Add("|---|---|---|---|")
    foreach ($row in $rows) {
        $reason = if ($row.reason) { $row.reason.Replace("|", "\\|") } else { "" }
        $lines.Add("| $($row.name) | $($row.layer) | $($row.status) | $reason |")
    }
    $lines | Set-Content -LiteralPath $notEvaluatedMarkdown -Encoding utf8NoBOM
}

if ($ListChecks) {
    $checkRegistry.checks |
        Sort-Object layer, name |
        Select-Object name, layer, status, reason
    exit 0
}

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
$legacyAction = if ($RefreshLegacy -or -not $legacyCacheFresh) { "refresh" } else { "reuse verified fingerprinted manifest" }

if ($ExplainPlan) {
    Write-Output "Item/block parity Batch 2 execution plan"
    Write-Output "RepoRoot: $RepoRoot"
    Write-Output "Primary legacy: $LegacyRoot"
    Write-Output "Secondary legacy: $SecondaryLegacyRoot"
    Write-Output "Original jar: $OriginalJar"
    Write-Output "Port: $PortRoot"
    Write-Output "Preset: $Preset"
    Write-Output "Selected checks: $($Checks -join ', ')"
    Write-Output "Implemented selected checks: $(if ($implementedSelected) { $implementedSelected -join ', ' } else { '<none>' })"
    Write-Output "Comparer selected checks: $(if ($comparerSelected) { $comparerSelected -join ', ' } else { '<none>' })"
    Write-Output "Module selected checks: $(if ($moduleSelected) { $moduleSelected -join ', ' } else { '<none>' })"
    Write-Output "Not evaluated selected checks: $(if ($notEvaluated) { $notEvaluated -join ', ' } else { '<none>' })"
    Write-Output "IDs: $(if ($Ids) { $Ids -join ', ' } else { '<all>' })"
    Write-Output "ID prefixes: $(if ($IdPrefix) { $IdPrefix -join ', ' } else { '<none>' })"
    Write-Output "Families: $(if ($Families) { $Families -join ', ' } else { '<none>' })"
    Write-Output "Packages: $(if ($Packages) { $Packages -join ', ' } else { '<none>' })"
    Write-Output "ChangedOnly: $ChangedOnly"
    Write-Output "SinceCommit: $(if ($SinceCommit) { $SinceCommit } else { '<none>' })"
    Write-Output "FailMode: $FailMode"
    Write-Output "RunBuild: $RunBuild"
    Write-Output "RunSmoke: $RunSmoke"
    Write-Output "RunRelatedAudits: $RunRelatedAudits"
    Write-Output "Legacy cache: $legacyAction"
    Write-Output "Batch 2 note: unimplemented selected checks are reported as NOT_EVALUATED, not as pass/fail."
    exit 0
}

Write-NotEvaluatedReport -NotEvaluatedChecks $notEvaluated

if ($FailMode -eq "strict" -and $notEvaluated.Count -gt 0) {
    throw "Strict mode cannot continue with not-evaluated selected checks: $($notEvaluated -join ', ')"
}

if ($RunBuild -or $RunSmoke -or $RunRelatedAudits) {
    Write-Output "Batch 2 skeleton note: RunBuild/RunSmoke/RunRelatedAudits are recognized parameters but not executed until Batch 10 runtime integration."
}
if ($ChangedOnly -or $SinceCommit -or $IdPrefix -or $Families -or $Packages) {
    Write-Output "Batch 2 skeleton note: ChangedOnly/SinceCommit/IdPrefix/Families/Packages are accepted for contract stability; filtering will be implemented in later extractor/check batches."
}

if ($implementedSelected.Count -eq 0) {
    Write-Output "No implemented checks selected. Wrote NOT_EVALUATED report: $notEvaluatedMarkdown"
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

if ("secondary_legacy_probe" -in $implementedSelected) {
    $secondaryProbe = Join-Path $PSScriptRoot "modules/secondary_legacy_probe.ps1"
    if (-not (Test-Path -LiteralPath $secondaryProbe -PathType Leaf)) { throw "Secondary legacy probe module not found: $secondaryProbe" }
    & $secondaryProbe -RepoRoot $RepoRoot -PrimaryManifestPath $legacyManifest -SecondaryLegacyRoot $SecondaryLegacyRoot -OutputJson (Join-Path $reportRoot "legacy_secondary_probe_report.json") -OutputMarkdown (Join-Path $reportRoot "legacy_secondary_probe_report.md")
    if (-not $?) { throw "Secondary legacy probe failed." }
}

if ($comparerSelected.Count -eq 0) {
    Write-Output "No comparer checks selected. Module/source-quality checks completed."
    exit 0
}
$compareArguments = @{
    LegacyManifest = $legacyManifest
    PortManifest = $portManifest
    RulesRoot = $rulesRoot
    OutputJson = $reportJson
    OutputMarkdown = $reportMarkdown
    Checks = $comparerSelected
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
