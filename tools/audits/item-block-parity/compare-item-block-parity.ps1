[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$LegacyManifest,
    [Parameter(Mandatory = $true)][string]$PortManifest,
    [Parameter(Mandatory = $true)][string]$RulesRoot,
    [Parameter(Mandatory = $true)][string]$OutputJson,
    [Parameter(Mandatory = $true)][string]$OutputMarkdown,
    [string[]]$Checks = @("registry", "block_item_pairs", "blockstates", "models", "lang"),
    [string[]]$Ids,
    [ValidateSet("off", "safe", "strict")][string]$FailMode = "safe",
    [string]$CuratedSummaryPath
)

$ErrorActionPreference = "Stop"

function Read-RuleDocument([string]$RulesRoot, [string]$FileName) {
    $path = Join-Path $RulesRoot $FileName
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        return [pscustomobject]@{ schemaVersion = 1; entries = @() }
    }
    return Get-Content -Raw -LiteralPath $path | ConvertFrom-Json
}

function New-RuleLookup($Document) {
    $lookup = @{}
    foreach ($entry in @($Document.entries)) {
        if ($null -eq $entry) { continue }
        if ([string]::IsNullOrWhiteSpace($entry.kind) -or [string]::IsNullOrWhiteSpace($entry.id)) { continue }
        $lookup["$($entry.kind):$($entry.id)"] = $entry
    }
    return $lookup
}

function Get-Rule($Lookup, [string]$Kind, [string]$Id) {
    $key = "${Kind}:${Id}"
    if ($Lookup.ContainsKey($key)) { return $Lookup[$key] }
    return $null
}

function Format-RuleEvidence([string]$Prefix, $Rule) {
    if ($null -eq $Rule) { return $Prefix }
    if (![string]::IsNullOrWhiteSpace($Rule.reason)) {
        return "$Prefix; intentional rule: $($Rule.reason)"
    }
    return "$Prefix; intentional rule"
}
function Get-VariantMappingEvidence($VariantMapping) {
    $pairs = @(
        foreach ($variant in @($VariantMapping.variants)) {
            "$($variant.legacyVariant)->$($variant.portId)"
        }
    )
    if ($pairs.Count -eq 0) {
        return "Variant mapping has no variants"
    }
    return "Legacy metadata variants mapped: $($pairs -join ', '); $($VariantMapping.reason)"
}

function Test-VariantMappingResolved($VariantMapping, $PortLookup, [string]$Kind) {
    $missing = [System.Collections.Generic.List[string]]::new()
    foreach ($variant in @($VariantMapping.variants)) {
        if ([string]::IsNullOrWhiteSpace($variant.portId)) {
            $missing.Add("<empty>")
            continue
        }
        $portKey = "${Kind}:$($variant.portId)"
        if (-not $PortLookup.ContainsKey($portKey)) {
            $missing.Add($variant.portId)
        }
    }
    return [pscustomobject][ordered]@{
        resolved = $missing.Count -eq 0
        missingPortIds = @($missing)
    }
}

foreach ($required in @($LegacyManifest, $PortManifest, (Join-Path $RulesRoot "known-renames.json"), (Join-Path $RulesRoot "parity-rules.json"))) {
    if (-not (Test-Path -LiteralPath $required -PathType Leaf)) { throw "Required parity input not found: $required" }
}

$legacy = Get-Content -Raw -LiteralPath $LegacyManifest | ConvertFrom-Json
$port = Get-Content -Raw -LiteralPath $PortManifest | ConvertFrom-Json
$renames = Get-Content -Raw -LiteralPath (Join-Path $RulesRoot "known-renames.json") | ConvertFrom-Json
$parityRules = Get-Content -Raw -LiteralPath (Join-Path $RulesRoot "parity-rules.json") | ConvertFrom-Json
$noItemBlockRules = Read-RuleDocument $RulesRoot "no-item-block-expected.json"
$noLootRules = Read-RuleDocument $RulesRoot "no-loot-expected.json"
$allowedExtrasRules = Read-RuleDocument $RulesRoot "allowed-extras.json"
$variantMappingRules = Read-RuleDocument $RulesRoot "variant-mapping.json"

$noItemBlockLookup = New-RuleLookup $noItemBlockRules
$noLootLookup = New-RuleLookup $noLootRules
$allowedExtrasLookup = New-RuleLookup $allowedExtrasRules
$variantMappingLookup = New-RuleLookup $variantMappingRules

$implemented = @("registry", "duplicate_registry_id", "block_item_pairs", "blockstates", "models", "lang", "loot")
$unknownChecks = @($Checks | Where-Object { $_ -notin $implemented })
if ($unknownChecks.Count -gt 0) { throw "Checks are not implemented in this batch: $($unknownChecks -join ', ')" }

$idFilter = @{}
foreach ($requestedId in @($Ids)) {
    if ([string]::IsNullOrWhiteSpace($requestedId)) { continue }
    $normalized = if ($requestedId.Contains(":")) { $requestedId.Split(":", 2)[1] } else { $requestedId }
    $idFilter[$normalized] = $true
}
function Test-Selected([string]$Id) { return $idFilter.Count -eq 0 -or $idFilter.ContainsKey($Id) }

$portLookup = @{}
foreach ($entry in $port.entries) { $portLookup["$($entry.kind):$($entry.registryId)"] = $entry }
$renameLookup = @{}
foreach ($entry in $renames.entries) { $renameLookup["$($entry.kind):$($entry.legacyId)"] = $entry }
$variantLookup = @{}
foreach ($entry in @($variantMappingRules.entries)) {
    $legacyId = if ($entry.legacyId) { $entry.legacyId } else { $entry.id }
    if (-not [string]::IsNullOrWhiteSpace($entry.kind) -and -not [string]::IsNullOrWhiteSpace($legacyId)) {
        $variantLookup["$($entry.kind):$legacyId"] = $entry
    }
}
$results = [System.Collections.Generic.List[object]]::new()

function Add-Result([string]$Kind, [string]$Id, [string]$Check, [string]$Status, [string]$Evidence, [string]$LegacyId = "") {
    $results.Add([pscustomobject][ordered]@{
        kind = $Kind
        id = "thaumcraft:$Id"
        check = $Check
        status = $Status
        legacyId = if ($LegacyId) { "thaumcraft:$LegacyId" } else { $null }
        evidence = $Evidence
    })
}

if ("duplicate_registry_id" -in $Checks) {
    foreach ($id in @($port.diagnostics.duplicateBlockIds)) {
        if (Test-Selected $id) { Add-Result "block" $id "duplicate_registry_id" "MISSING" "Duplicate DeferredRegister block ID" }
    }
    foreach ($id in @($port.diagnostics.duplicateItemIds)) {
        if (Test-Selected $id) { Add-Result "item" $id "duplicate_registry_id" "MISSING" "Duplicate DeferredRegister item ID" }
    }
}

if ("registry" -in $Checks) {
    foreach ($entry in $legacy.entries | Where-Object comparable) {
        $targetId = $entry.registryId
        $renameKey = "$($entry.kind):$($entry.registryId)"
        $rename = if ($renameLookup.ContainsKey($renameKey)) { $renameLookup[$renameKey] } else { $null }
        if ($null -ne $rename) { $targetId = $rename.portId }
        if (-not (Test-Selected $targetId)) { continue }
        $portKey = "$($entry.kind):$targetId"
        if ($portLookup.ContainsKey($portKey)) {
            $status = if ($null -ne $rename) { "RENAMED_WITH_MAPPING" } else { "PASS" }
            Add-Result $entry.kind $targetId "registry" $status $entry.evidence $entry.registryId
        } else {
            $variantKey = "$($entry.kind):$($entry.registryId)"
            $variantMapping = if ($variantLookup.ContainsKey($variantKey)) { $variantLookup[$variantKey] } else { $null }
            if ($null -ne $variantMapping) {
                $variantResolution = Test-VariantMappingResolved $variantMapping $portLookup $entry.kind
                if ($variantResolution.resolved) {
                    Add-Result $entry.kind $entry.registryId "registry" "VARIANT_MAPPED" (Get-VariantMappingEvidence $variantMapping) $entry.registryId
                } else {
                    Add-Result $entry.kind $entry.registryId "registry" "MISSING" "Variant mapping unresolved; missing port IDs: $($variantResolution.missingPortIds -join ', ')" $entry.registryId
                }
            } else {
                Add-Result $entry.kind $targetId "registry" "MISSING" $entry.evidence $entry.registryId
            }
        }
    }
}

foreach ($entry in $port.entries) {
    if (-not (Test-Selected $entry.registryId)) { continue }
    if ($entry.kind -eq "block") {
        if ("block_item_pairs" -in $Checks) {
            $rule = Get-Rule $noItemBlockLookup "block" $entry.registryId
            if ($entry.blockItem) {
                Add-Result "block" $entry.registryId "block_item_pairs" "PASS" "TCItems blockItem registration"
            } elseif ($null -ne $rule) {
                Add-Result "block" $entry.registryId "block_item_pairs" "INTENTIONAL_MISSING" (Format-RuleEvidence "No BlockItem expected" $rule)
            } else {
                Add-Result "block" $entry.registryId "block_item_pairs" "MISSING" "TCItems blockItem registration"
            }
        }
        if ("blockstates" -in $Checks) {
            Add-Result "block" $entry.registryId "blockstates" $(if ($entry.resources.blockstate) { "PASS" } else { "MISSING" }) "assets/thaumcraft/blockstates/$($entry.registryId).json"
        }
        if ("models" -in $Checks) {
            $modelEvidence = if ($entry.resources.missingBlockModels.Count -gt 0) { "Missing: $($entry.resources.missingBlockModels -join ', ')" } else { "All blockstate model references resolve" }
            Add-Result "block" $entry.registryId "models" $(if ($entry.resources.blockModelsResolved) { "PASS" } else { "MISSING" }) $modelEvidence
        }
        if ("lang" -in $Checks) {
            Add-Result "block" $entry.registryId "lang" $(if ($entry.resources.langKey) { "PASS" } else { "MISSING" }) "block.thaumcraft.$($entry.registryId)"
        }
        if ("loot" -in $Checks) {
            $rule = Get-Rule $noLootLookup "block" $entry.registryId
            if ($entry.resources.lootTable) {
                Add-Result "block" $entry.registryId "loot" "PASS" "data/thaumcraft/loot_table/blocks/$($entry.registryId).json"
            } elseif ($null -ne $rule) {
                Add-Result "block" $entry.registryId "loot" "INTENTIONAL_MISSING" (Format-RuleEvidence "No loot table expected" $rule)
            } else {
                Add-Result "block" $entry.registryId "loot" "MISSING" "data/thaumcraft/loot_table/blocks/$($entry.registryId).json"
            }
        }
    }
    elseif ($entry.kind -eq "item") {
        if ("models" -in $Checks) {
            Add-Result "item" $entry.registryId "models" $(if ($entry.resources.itemModel) { "PASS" } else { "MISSING" }) "assets/thaumcraft/models/item/$($entry.registryId).json"
        }
        if ("lang" -in $Checks) {
            $langKey = if ($entry.blockItem) { "block.thaumcraft.$($entry.registryId)" } else { "item.thaumcraft.$($entry.registryId)" }
            Add-Result "item" $entry.registryId "lang" $(if ($entry.resources.langKey) { "PASS" } else { "MISSING" }) $langKey
        }
    }
}

$orderedResults = @($results | Sort-Object kind, id, check)
$safeFailureChecks = @($parityRules.safeFailureChecks)
$safeFailures = @($orderedResults | Where-Object { $_.status -eq "MISSING" -and $_.check -in $safeFailureChecks })
$strictFailures = @($orderedResults | Where-Object status -eq "MISSING")
$notEvaluated = @(
    "variant_mapping", "texture_graph", "recipes", "tags", "aspects", "research_refs",
    "data_components", "blockentities", "capabilities", "menus", "networking",
    "client_server_safety", "secondary_legacy_probe", "original_jar_probe", "runtime", "visual"
)
$summary = [ordered]@{
    checks = @($Checks).Count
    results = $orderedResults.Count
    pass = @($orderedResults | Where-Object status -eq "PASS").Count
    renamed = @($orderedResults | Where-Object status -eq "RENAMED_WITH_MAPPING").Count
    variantMapped = @($orderedResults | Where-Object status -eq "VARIANT_MAPPED").Count
    intentionalMissing = @($orderedResults | Where-Object status -eq "INTENTIONAL_MISSING").Count
    missing = @($orderedResults | Where-Object status -eq "MISSING").Count
    safeFailures = $safeFailures.Count
    strictFailures = $strictFailures.Count
    legacyInferredReviewRequired = $legacy.summary.inferredReviewRequired
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    failMode = $FailMode
    filters = [ordered]@{ ids = @($Ids); checks = @($Checks) }
    implementedChecks = @($Checks)
    notEvaluatedChecks = $notEvaluated
    ruleInputs = [ordered]@{
        noItemBlockExpected = @($noItemBlockRules.entries).Count
        noLootExpected = @($noLootRules.entries).Count
        allowedExtras = @($allowedExtrasRules.entries).Count
        variantMappings = @($variantMappingRules.entries).Count
    }
    summary = $summary
    results = $orderedResults
}

foreach ($path in @($OutputJson, $OutputMarkdown)) { New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null }
$report | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block parity report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Result | Count |")
$lines.Add("|---|---:|")
$lines.Add("| PASS | $($summary.pass) |")
$lines.Add("| RENAMED_WITH_MAPPING | $($summary.renamed) |")
$lines.Add("| VARIANT_MAPPED | $($summary.variantMapped) |")
$lines.Add("| INTENTIONAL_MISSING | $($summary.intentionalMissing) |")
$lines.Add("| MISSING | $($summary.missing) |")
$lines.Add("| Safe failures | $($summary.safeFailures) |")
$lines.Add("| Legacy inferred IDs requiring review | $($summary.legacyInferredReviewRequired) |")
$lines.Add("")
$lines.Add("Rule inputs: no-item=$(@($noItemBlockRules.entries).Count), no-loot=$(@($noLootRules.entries).Count), allowed-extra=$(@($allowedExtrasRules.entries).Count), variants=$(@($variantMappingRules.entries).Count).")
$lines.Add("")
$lines.Add("This is not a full parity verdict. Not evaluated: $($notEvaluated -join ', ').")
$lines.Add("")
$lines.Add("## Missing and mapped results")
$lines.Add("")
$lines.Add("| Kind | ID | Check | Status | Evidence |")
$lines.Add("|---|---|---|---|---|")
foreach ($result in $orderedResults | Where-Object { $_.status -ne "PASS" }) {
    $safeEvidence = $result.evidence.Replace("|", "\|")
    $lines.Add("| $($result.kind) | ``$($result.id)`` | $($result.check) | $($result.status) | $safeEvidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

if ($CuratedSummaryPath) {
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $CuratedSummaryPath) | Out-Null
    $missingByCheck = @($orderedResults | Where-Object status -eq "MISSING" | Group-Object check | Sort-Object Name | ForEach-Object { "- $($_.Name): $($_.Count)" })
    $intentionalByCheck = @($orderedResults | Where-Object status -eq "INTENTIONAL_MISSING" | Group-Object check | Sort-Object Name | ForEach-Object { "- $($_.Name): $($_.Count)" })
    if ($intentionalByCheck.Count -eq 0) { $intentionalByCheck = @("- none: 0") }
    $curated = @(
        "# Item/block parity baseline summary",
        "",
        "Generated: $($report.generatedAtUtc)",
        "",
        "The executable baseline produced $($summary.results) evaluated results: $($summary.pass) pass, $($summary.renamed) mapped rename, $($summary.variantMapped) variant mapped, $($summary.intentionalMissing) intentional missing and $($summary.missing) missing.",
        "",
        "Of the missing results, $($summary.safeFailures) currently fall into safe resource-boundary checks. The baseline is intentionally generated with FailMode off until the rule overrides are reviewed.",
        "",
        "There are $($summary.legacyInferredReviewRequired) legacy source entries whose IDs were inferred from symbols and therefore did not contribute to a registry pass.",
        "",
        "Intentional missing results by implemented check:",
        "",
        $intentionalByCheck,
        "",
        "Missing results by implemented check:",
        "",
        $missingByCheck,
        "",
        "This milestone covers only: $($Checks -join ', '). It does not claim behavior, runtime or visual parity.",
        "",
        'Raw details: `tools/reports/local/item-block-parity/item_block_parity_report.md` (local, ignored).'
    ) | ForEach-Object { $_ }
    $curated | Set-Content -LiteralPath $CuratedSummaryPath -Encoding utf8NoBOM
}

Write-Output "Parity report: $OutputMarkdown"
Write-Output "Pass=$($summary.pass), mapped=$($summary.renamed), variantMapped=$($summary.variantMapped), intentional=$($summary.intentionalMissing), missing=$($summary.missing), safeFailures=$($summary.safeFailures)"
if ($FailMode -eq "safe" -and $safeFailures.Count -gt 0) { exit 2 }
if ($FailMode -eq "strict" -and $strictFailures.Count -gt 0) { exit 3 }