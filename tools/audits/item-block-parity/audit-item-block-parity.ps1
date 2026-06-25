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
    data = @("recipes", "loot", "drop_behavior", "tags", "fuels_flammability", "aspects", "research_refs", "thaumonomicon_refs")
    "behavior-boundary" = @("item_properties", "block_properties", "blockentities", "capabilities", "menus", "networking", "client_server_safety")
    "source-quality" = @("legacy_primary_manifest", "secondary_legacy_probe", "source_conflict_report", "original_jar_probe", "report_schema", "check_invocation", "report_freshness", "status_taxonomy", "docs_deferred")
    "ci-safe" = @("registry", "json_validity", "blockstates", "models", "textures", "lang", "orphan_references", "client_server_safety", "datapack_load", "report_schema", "check_invocation", "report_freshness", "status_taxonomy", "docs_deferred")
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
        policy = "Report-only not-evaluated check inventory. Planned checks are framework work, not pass/fail parity results."
        inputs = [ordered]@{
            preset = $Preset
            selectedChecks = @($Checks)
            implementedSelected = @($implementedSelected)
        }
        summary = [ordered]@{
            rows = @($rows).Count
            notEvaluated = @($rows).Count
            implementedSelected = @($implementedSelected).Count
        }
        results = @($rows)
        preset = $Preset
        selectedChecks = @($Checks)
        implementedSelected = @($implementedSelected)
        notEvaluated = @($rows)
        note = "Batch 27 keeps this report schema-contract compatible while still treating planned checks as NOT_EVALUATED."
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
    $runtimeModule = Join-Path $PSScriptRoot "modules/runtime_checks.ps1"
    if (-not (Test-Path -LiteralPath $runtimeModule -PathType Leaf)) { throw "Runtime check module not found: $runtimeModule" }
    & $runtimeModule -RepoRoot $RepoRoot -PortRoot $PortRoot -AuditScript $PSCommandPath -RunBuild:$RunBuild -RunSmoke:$RunSmoke -RunRelatedAudits:$RunRelatedAudits -FailMode $FailMode -OutputJson (Join-Path $reportRoot "item_block_runtime_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_runtime_report.md")
    if (-not $?) { throw "Runtime check module failed." }
}
if ($Ids -or $ChangedOnly -or $SinceCommit -or $IdPrefix -or $Families -or $Packages) {
    Write-Output "Focused filter requested; filtered manifests will be generated after primary manifests are refreshed."
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

$legacyManifestForChecks = $legacyManifest
$portManifestForChecks = $portManifest
$filterRequested = [bool]($Ids -or $IdPrefix -or $Families -or $Packages -or $ChangedOnly -or -not [string]::IsNullOrWhiteSpace($SinceCommit))
if ($filterRequested) {
    $focusedFilterModule = Join-Path $PSScriptRoot "modules/focused_filter.ps1"
    if (-not (Test-Path -LiteralPath $focusedFilterModule -PathType Leaf)) { throw "Focused filter module not found: $focusedFilterModule" }
    $filteredLegacyManifest = Join-Path $reportRoot "legacy_primary_manifest.focused.json"
    $filteredPortManifest = Join-Path $reportRoot "port_manifest.focused.json"
    & $focusedFilterModule -RepoRoot $RepoRoot -PortRoot $PortRoot -LegacyManifestPath $legacyManifestForChecks -PortManifestPath $portManifestForChecks -OutputLegacyManifest $filteredLegacyManifest -OutputPortManifest $filteredPortManifest -Ids $Ids -IdPrefix $IdPrefix -Families $Families -Packages $Packages -ChangedOnly:$ChangedOnly -SinceCommit $SinceCommit -OutputJson (Join-Path $reportRoot "item_block_focus_filter_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_focus_filter_report.md")
    if (-not $?) { throw "Focused filter module failed." }
    $legacyManifestForChecks = $filteredLegacyManifest
    $portManifestForChecks = $filteredPortManifest
}
if ("secondary_legacy_probe" -in $implementedSelected) {
    $secondaryProbe = Join-Path $PSScriptRoot "modules/secondary_legacy_probe.ps1"
    if (-not (Test-Path -LiteralPath $secondaryProbe -PathType Leaf)) { throw "Secondary legacy probe module not found: $secondaryProbe" }
    & $secondaryProbe -RepoRoot $RepoRoot -PrimaryManifestPath $legacyManifestForChecks -SecondaryLegacyRoot $SecondaryLegacyRoot -OutputJson (Join-Path $reportRoot "legacy_secondary_probe_report.json") -OutputMarkdown (Join-Path $reportRoot "legacy_secondary_probe_report.md")
    if (-not $?) { throw "Secondary legacy probe failed." }
}


$dataReferenceChecks = @("recipes", "tags", "aspects", "research_refs", "thaumonomicon_refs")
$selectedDataReferenceChecks = @($implementedSelected | Where-Object { $_ -in $dataReferenceChecks })
if ($selectedDataReferenceChecks.Count -gt 0) {
    $dataReferenceModule = Join-Path $PSScriptRoot "modules/data_refs.ps1"
    if (-not (Test-Path -LiteralPath $dataReferenceModule -PathType Leaf)) { throw "Data reference module not found: $dataReferenceModule" }
    & $dataReferenceModule -RepoRoot $RepoRoot -PortManifestPath $portManifestForChecks -PortRoot $PortRoot -Checks $selectedDataReferenceChecks -OutputJson (Join-Path $reportRoot "item_block_data_reference_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_data_reference_report.md")
    if (-not $?) { throw "Data reference module failed." }
}

$datapackLoadChecks = @("datapack_load")
$selectedDatapackLoadChecks = @($implementedSelected | Where-Object { $_ -in $datapackLoadChecks })
if ($selectedDatapackLoadChecks.Count -gt 0) {
    $datapackLoadModule = Join-Path $PSScriptRoot "modules/runtime_datapack_smoke.ps1"
    if (-not (Test-Path -LiteralPath $datapackLoadModule -PathType Leaf)) { throw "Runtime/datapack load smoke module not found: $datapackLoadModule" }
    & $datapackLoadModule -RepoRoot $RepoRoot -PortManifestPath $portManifestForChecks -PortRoot $PortRoot -RulesRoot $rulesRoot -Checks $selectedDatapackLoadChecks -OutputJson (Join-Path $reportRoot "item_block_runtime_datapack_smoke_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_runtime_datapack_smoke_report.md")
    if (-not $?) { throw "Runtime/datapack load smoke module failed." }
}
$gameTestSmokeChecks = @("game_test_smoke")
$selectedGameTestSmokeChecks = @($implementedSelected | Where-Object { $_ -in $gameTestSmokeChecks })
if ($selectedGameTestSmokeChecks.Count -gt 0) {
    $gameTestSmokeModule = Join-Path $PSScriptRoot "modules/game_test_smoke.ps1"
    if (-not (Test-Path -LiteralPath $gameTestSmokeModule -PathType Leaf)) { throw "GameTest/scripted behavior smoke module not found: $gameTestSmokeModule" }
    & $gameTestSmokeModule -RepoRoot $RepoRoot -PortManifestPath $portManifestForChecks -PortRoot $PortRoot -RulesRoot $rulesRoot -Checks $selectedGameTestSmokeChecks -OutputJson (Join-Path $reportRoot "item_block_game_test_smoke_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_game_test_smoke_report.md")
    if (-not $?) { throw "GameTest/scripted behavior smoke module failed." }
}
$itemPropertyChecks = @("item_properties")
$selectedItemPropertyChecks = @($implementedSelected | Where-Object { $_ -in $itemPropertyChecks })
if ($selectedItemPropertyChecks.Count -gt 0) {
    $itemPropertyModule = Join-Path $PSScriptRoot "modules/item_properties.ps1"
    if (-not (Test-Path -LiteralPath $itemPropertyModule -PathType Leaf)) { throw "Item property module not found: $itemPropertyModule" }
    & $itemPropertyModule -RepoRoot $RepoRoot -LegacyManifestPath $legacyManifestForChecks -PortManifestPath $portManifestForChecks -LegacyRoot $LegacyRoot -PortRoot $PortRoot -RulesRoot $rulesRoot -Checks $selectedItemPropertyChecks -OutputJson (Join-Path $reportRoot "item_block_item_property_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_item_property_report.md")
    if (-not $?) { throw "Item property module failed." }
}
$blockPropertyChecks = @("block_properties")
$selectedBlockPropertyChecks = @($implementedSelected | Where-Object { $_ -in $blockPropertyChecks })
if ($selectedBlockPropertyChecks.Count -gt 0) {
    $blockPropertyModule = Join-Path $PSScriptRoot "modules/block_properties.ps1"
    if (-not (Test-Path -LiteralPath $blockPropertyModule -PathType Leaf)) { throw "Block property module not found: $blockPropertyModule" }
    & $blockPropertyModule -RepoRoot $RepoRoot -LegacyManifestPath $legacyManifestForChecks -PortManifestPath $portManifestForChecks -LegacyRoot $LegacyRoot -PortRoot $PortRoot -RulesRoot $rulesRoot -Checks $selectedBlockPropertyChecks -OutputJson (Join-Path $reportRoot "item_block_block_property_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_block_property_report.md")
    if (-not $?) { throw "Block property module failed." }
}
$dropBehaviorChecks = @("drop_behavior")
$selectedDropBehaviorChecks = @($implementedSelected | Where-Object { $_ -in $dropBehaviorChecks })
if ($selectedDropBehaviorChecks.Count -gt 0) {
    $dropBehaviorModule = Join-Path $PSScriptRoot "modules/loot_drop_behavior.ps1"
    if (-not (Test-Path -LiteralPath $dropBehaviorModule -PathType Leaf)) { throw "Loot/drop behavior module not found: $dropBehaviorModule" }
    & $dropBehaviorModule -RepoRoot $RepoRoot -LegacyManifestPath $legacyManifestForChecks -PortManifestPath $portManifestForChecks -LegacyRoot $LegacyRoot -PortRoot $PortRoot -RulesRoot $rulesRoot -Checks $selectedDropBehaviorChecks -OutputJson (Join-Path $reportRoot "item_block_loot_drop_behavior_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_loot_drop_behavior_report.md")
    if (-not $?) { throw "Loot/drop behavior module failed." }
}
$behaviorBoundaryChecks = @("blockentities", "capabilities", "menus")
$selectedBehaviorBoundaryChecks = @($implementedSelected | Where-Object { $_ -in $behaviorBoundaryChecks })
if ($selectedBehaviorBoundaryChecks.Count -gt 0) {
    $behaviorBoundaryModule = Join-Path $PSScriptRoot "modules/behavior_boundary.ps1"
    if (-not (Test-Path -LiteralPath $behaviorBoundaryModule -PathType Leaf)) { throw "Behavior boundary module not found: $behaviorBoundaryModule" }
    & $behaviorBoundaryModule -RepoRoot $RepoRoot -LegacyManifestPath $legacyManifestForChecks -PortManifestPath $portManifestForChecks -RulesRoot $rulesRoot -Checks $selectedBehaviorBoundaryChecks -OutputJson (Join-Path $reportRoot "item_block_behavior_boundary_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_behavior_boundary_report.md")
    if (-not $?) { throw "Behavior boundary module failed." }
}
$visualBoundaryChecks = @("visual_boundary")
$selectedVisualBoundaryChecks = @($implementedSelected | Where-Object { $_ -in $visualBoundaryChecks })
if ($selectedVisualBoundaryChecks.Count -gt 0) {
    $visualBoundaryModule = Join-Path $PSScriptRoot "modules/visual_model_transforms.ps1"
    if (-not (Test-Path -LiteralPath $visualBoundaryModule -PathType Leaf)) { throw "Visual boundary module not found: $visualBoundaryModule" }
    & $visualBoundaryModule -RepoRoot $RepoRoot -PortManifestPath $portManifestForChecks -PortRoot $PortRoot -RulesRoot $rulesRoot -Checks $selectedVisualBoundaryChecks -OutputJson (Join-Path $reportRoot "item_block_visual_model_transform_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_visual_model_transform_report.md")
    if (-not $?) { throw "Visual boundary module failed." }
}
$textureColorChecks = @("texture_color")
$selectedTextureColorChecks = @($implementedSelected | Where-Object { $_ -in $textureColorChecks })
if ($selectedTextureColorChecks.Count -gt 0) {
    $textureColorModule = Join-Path $PSScriptRoot "modules/texture_color_parity.ps1"
    if (-not (Test-Path -LiteralPath $textureColorModule -PathType Leaf)) { throw "Texture/color parity module not found: $textureColorModule" }
    & $textureColorModule -RepoRoot $RepoRoot -PortManifestPath $portManifestForChecks -PortRoot $PortRoot -LegacyRoot $LegacyRoot -OriginalJar $OriginalJar -RulesRoot $rulesRoot -Checks $selectedTextureColorChecks -OutputJson (Join-Path $reportRoot "item_block_texture_color_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_texture_color_report.md")
    if (-not $?) { throw "Texture/color parity module failed." }
}
$soundParticleChecks = @("sounds_particles")
$selectedSoundParticleChecks = @($implementedSelected | Where-Object { $_ -in $soundParticleChecks })
if ($selectedSoundParticleChecks.Count -gt 0) {
    $soundParticleModule = Join-Path $PSScriptRoot "modules/sounds_particles_fx.ps1"
    if (-not (Test-Path -LiteralPath $soundParticleModule -PathType Leaf)) { throw "Sound/particle/FX module not found: $soundParticleModule" }
    & $soundParticleModule -RepoRoot $RepoRoot -LegacyManifestPath $legacyManifestForChecks -PortManifestPath $portManifestForChecks -LegacyRoot $LegacyRoot -PortRoot $PortRoot -RulesRoot $rulesRoot -Checks $selectedSoundParticleChecks -OutputJson (Join-Path $reportRoot "item_block_sound_particle_fx_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_sound_particle_fx_report.md")
    if (-not $?) { throw "Sound/particle/FX module failed." }
}
# Batch 33 JSON validity validator start
$jsonValidityChecks = @("json_validity")
$selectedJsonValidityChecks = @($implementedSelected | Where-Object { $_ -in $jsonValidityChecks })
function Invoke-JsonValidityValidator {
    if ($selectedJsonValidityChecks.Count -eq 0) { return }
    $jsonValidityModule = Join-Path $PSScriptRoot "modules/json_validity.ps1"
    if (-not (Test-Path -LiteralPath $jsonValidityModule -PathType Leaf)) { throw "JSON validity module not found: $jsonValidityModule" }
    & $jsonValidityModule -RepoRoot $RepoRoot -PortRoot $PortRoot -RulesRoot $rulesRoot -Checks $selectedJsonValidityChecks -OutputJson (Join-Path $reportRoot "item_block_json_validity_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_json_validity_report.md")
    if (-not $?) { throw "JSON validity module failed." }
}
# Batch 33 JSON validity validator end
# Batch 31 docs/registry consistency audit start
$docsDeferredChecks = @("docs_deferred")
$selectedDocsDeferredChecks = @($implementedSelected | Where-Object { $_ -in $docsDeferredChecks })
function Invoke-DocsRegistryConsistencyAudit {
    if ($selectedDocsDeferredChecks.Count -eq 0) { return }
    $docsRegistryModule = Join-Path $PSScriptRoot "modules/docs_registry_consistency.ps1"
    if (-not (Test-Path -LiteralPath $docsRegistryModule -PathType Leaf)) { throw "Docs/registry consistency module not found: $docsRegistryModule" }
    & $docsRegistryModule -RepoRoot $RepoRoot -RulesRoot $rulesRoot -Checks $selectedDocsDeferredChecks -OutputJson (Join-Path $reportRoot "docs_registry_consistency_report.json") -OutputMarkdown (Join-Path $reportRoot "docs_registry_consistency_report.md")
    if (-not $?) { throw "Docs/registry consistency module failed." }
}
# Batch 31 docs/registry consistency audit end
# Batch 30 status taxonomy validator start
$statusTaxonomyChecks = @("status_taxonomy")
$selectedStatusTaxonomyChecks = @($implementedSelected | Where-Object { $_ -in $statusTaxonomyChecks })
function Invoke-StatusTaxonomyValidator {
    if ($selectedStatusTaxonomyChecks.Count -eq 0) { return }
    $statusTaxonomyModule = Join-Path $PSScriptRoot "modules/status_taxonomy_validator.ps1"
    if (-not (Test-Path -LiteralPath $statusTaxonomyModule -PathType Leaf)) { throw "Status taxonomy validator module not found: $statusTaxonomyModule" }
    & $statusTaxonomyModule -RepoRoot $RepoRoot -ReportRoot $reportRoot -RulesRoot $rulesRoot -Checks $selectedStatusTaxonomyChecks -OutputJson (Join-Path $reportRoot "status_taxonomy_report.json") -OutputMarkdown (Join-Path $reportRoot "status_taxonomy_report.md")
    if (-not $?) { throw "Status taxonomy validator module failed." }
}
# Batch 30 status taxonomy validator end
# Batch 29 report freshness guard start
$reportFreshnessChecks = @("report_freshness")
$selectedReportFreshnessChecks = @($implementedSelected | Where-Object { $_ -in $reportFreshnessChecks })
function Invoke-ReportFreshnessGuard {
    if ($selectedReportFreshnessChecks.Count -eq 0) { return }
    $reportFreshnessModule = Join-Path $PSScriptRoot "modules/report_freshness_guard.ps1"
    if (-not (Test-Path -LiteralPath $reportFreshnessModule -PathType Leaf)) { throw "Report freshness guard module not found: $reportFreshnessModule" }
    & $reportFreshnessModule -RepoRoot $RepoRoot -ReportRoot $reportRoot -RulesRoot $rulesRoot -Checks $selectedReportFreshnessChecks -OutputJson (Join-Path $reportRoot "report_freshness_guard_report.json") -OutputMarkdown (Join-Path $reportRoot "report_freshness_guard_report.md")
    if (-not $?) { throw "Report freshness guard module failed." }
}
# Batch 29 report freshness guard end
$reportSchemaChecks = @("report_schema")
$selectedReportSchemaChecks = @($implementedSelected | Where-Object { $_ -in $reportSchemaChecks })
function Invoke-ReportSchemaValidator {
    if ($selectedReportSchemaChecks.Count -eq 0) { return }
    $reportSchemaModule = Join-Path $PSScriptRoot "modules/report_schema_validator.ps1"
    if (-not (Test-Path -LiteralPath $reportSchemaModule -PathType Leaf)) { throw "Report schema validator module not found: $reportSchemaModule" }
    & $reportSchemaModule -RepoRoot $RepoRoot -ReportRoot $reportRoot -RulesRoot $rulesRoot -Checks $selectedReportSchemaChecks -OutputJson (Join-Path $reportRoot "report_schema_contract_report.json") -OutputMarkdown (Join-Path $reportRoot "report_schema_contract_report.md")
    if (-not $?) { throw "Report schema validator module failed." }
}

# Batch 28 check invocation self-test start
$checkInvocationChecks = @("check_invocation")
$selectedCheckInvocationChecks = @($implementedSelected | Where-Object { $_ -in $checkInvocationChecks })
function Invoke-CheckInvocationSelfTest {
    if ($selectedCheckInvocationChecks.Count -eq 0) { return }
    $checkInvocationModule = Join-Path $PSScriptRoot "modules/check_invocation_self_test.ps1"
    if (-not (Test-Path -LiteralPath $checkInvocationModule -PathType Leaf)) { throw "Check invocation self-test module not found: $checkInvocationModule" }
    & $checkInvocationModule -RepoRoot $RepoRoot -RulesRoot $rulesRoot -Checks $selectedCheckInvocationChecks -OutputJson (Join-Path $reportRoot "item_block_check_invocation_self_test_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_check_invocation_self_test_report.md")
    if (-not $?) { throw "Check invocation self-test module failed." }
}
# Batch 28 check invocation self-test end
$autoFixCandidateChecks = @("auto_fix_candidates")
$selectedAutoFixCandidateChecks = @($implementedSelected | Where-Object { $_ -in $autoFixCandidateChecks })
function Invoke-AutoFixCandidateReporter {
    param([string]$LegacyManifestForCandidates, [string]$PortManifestForCandidates)
    if ($selectedAutoFixCandidateChecks.Count -eq 0) { return }
    $autoFixCandidateModule = Join-Path $PSScriptRoot "modules/auto_fix_candidates.ps1"
    if (-not (Test-Path -LiteralPath $autoFixCandidateModule -PathType Leaf)) { throw "Auto-fix candidate module not found: $autoFixCandidateModule" }
    & $autoFixCandidateModule -RepoRoot $RepoRoot -LegacyManifestPath $LegacyManifestForCandidates -PortManifestPath $PortManifestForCandidates -PortRoot $PortRoot -RulesRoot $rulesRoot -ReportRoot $reportRoot -Checks $selectedAutoFixCandidateChecks -ParityReportPath $reportJson -OutputJson (Join-Path $reportRoot "item_block_auto_fix_candidates.json") -OutputMarkdown (Join-Path $reportRoot "item_block_auto_fix_candidates.md")
    if (-not $?) { throw "Auto-fix candidate module failed." }
}
$clientServerSafetyChecks = @("client_server_safety")
$selectedClientServerSafetyChecks = @($implementedSelected | Where-Object { $_ -in $clientServerSafetyChecks })
if ($selectedClientServerSafetyChecks.Count -gt 0) {
    $clientServerSafetyModule = Join-Path $PSScriptRoot "modules/client_server_safety.ps1"
    if (-not (Test-Path -LiteralPath $clientServerSafetyModule -PathType Leaf)) { throw "Client/server safety module not found: $clientServerSafetyModule" }
    & $clientServerSafetyModule -RepoRoot $RepoRoot -PortManifestPath $portManifestForChecks -PortRoot $PortRoot -RulesRoot $rulesRoot -Checks $selectedClientServerSafetyChecks -OutputJson (Join-Path $reportRoot "item_block_client_server_safety_report.json") -OutputMarkdown (Join-Path $reportRoot "item_block_client_server_safety_report.md")
    if (-not $?) { throw "Client/server safety module failed." }
}
if ($comparerSelected.Count -eq 0) {
    Invoke-AutoFixCandidateReporter -LegacyManifestForCandidates $legacyManifestForChecks -PortManifestForCandidates $portManifestForChecks
Invoke-JsonValidityValidator
Invoke-CheckInvocationSelfTest
Invoke-DocsRegistryConsistencyAudit
Invoke-StatusTaxonomyValidator
Invoke-ReportFreshnessGuard
Invoke-ReportSchemaValidator
    Write-Output "No comparer checks selected. Module/source-quality checks completed."
    exit 0
}
$compareArguments = @{
    LegacyManifest = $legacyManifestForChecks
    PortManifest = $portManifestForChecks
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
& $validator -LegacyManifest $legacyManifestForChecks -PortManifest $portManifestForChecks -ReportPath $reportJson
if (-not $?) { throw "Parity report validation failed." }
Invoke-AutoFixCandidateReporter -LegacyManifestForCandidates $legacyManifestForChecks -PortManifestForCandidates $portManifestForChecks
Invoke-JsonValidityValidator
Invoke-CheckInvocationSelfTest
Invoke-DocsRegistryConsistencyAudit
Invoke-StatusTaxonomyValidator
Invoke-ReportFreshnessGuard
Invoke-ReportSchemaValidator
if (-not $compareSucceeded) {
    if ($null -ne $compareExitCode) { exit $compareExitCode }
    exit 1
}
exit 0
